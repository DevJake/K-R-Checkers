/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import event.Event;
import event.GameCompletedEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class GameManager {
    private final Board board;
    private final Queue<Player> playerQueue = new LinkedList<>();
    public Piece lastLockedPiece;
    public Player lastPlayer;
    private List<Piece> moveablePieces = new ArrayList<>();
    private int round = 0;
    private boolean evalDone = false;
    private boolean endMove = false;
    private boolean remainingMove = true;

    public GameManager(Board board, List<Player> playerQueue, Pane canvas) {
        this.board = board;
        this.playerQueue.addAll(playerQueue);

//        canvas.onMousePressedProperty().set(event -> {
//            double tileX = Math.floor(event.getX() / (canvas.getWidth() / 8));
//            double tileY = Math.floor((canvas.getHeight() - event.getY()) / (canvas.getHeight() / 8));
//
//            Piece clickedPiece = board.getTileAtIndex((int) tileX, (int) tileY).getPiece();
//            if (clickedPiece.getPlayer() != lastPlayer) {
//                lastLockedPiece = clickedPiece;
//                lastPlayer = lastLockedPiece.getPlayer();
//            }
//        });
    }

    public boolean isEvalDone() {
        return evalDone;
    }

    public void setEvalDone(boolean evalDone) {
        this.evalDone = evalDone;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(Player lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public Piece getLastLockedPiece() {
        return lastLockedPiece;
    }

    public void setLastLockedPiece(Piece lastLockedPiece) {
        this.lastLockedPiece = lastLockedPiece;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayerQueue() {
        return new ArrayList<>(playerQueue);
    }

    private Player nextInQueue() {
        Player next = playerQueue.poll();
        playerQueue.add(next); //Put to the back of the queue
        return next;
    }

    public List<Piece> getMoveablePieces() {
        return moveablePieces;
    }

    private boolean isGameFinished() {
        //Check if one team has all pieces captured
        for (Player player : getPlayerQueue()) {
            if (board.getPiecesOwnedBy(player).size() == 0)
                return true;
        }

        return false;
    }

    public void beginGame() {
        new Thread(() -> {
            lastPlayer = nextInQueue();
            while (!isGameFinished()) {

                playRound();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (isGameFinished()) {
                Player winner = board.getPiecesOwnedBy(getPlayerQueue().get(0)).isEmpty() ? getPlayerQueue().get(1) :
                        getPlayerQueue().get(0);
                Player loser = getPlayerQueue().get(0) == winner ? getPlayerQueue().get(1) : getPlayerQueue().get(0);
                Event.Manager.fire(new GameCompletedEvent(winner, loser));

                Thread.currentThread().interrupt();
            }
        }).start();

    }

    public boolean isRemainingMove() {
        return remainingMove;
    }

    public void setRemainingMove(boolean remainingMove) {
        this.remainingMove = remainingMove;
    }

    private void playRound() {
        /*
        1. Lock player
            i. force auto-capturing
            ii. if can auto-capture multiple, ask which to choose
            iii. if only one auto-capture available, execute
        2. Lock gameplay to the last-moved piece
        3. when no more auto-captures available, allow them to move their piece
            i. if piece cannot be moved, terminate move
            ii. When their single move is exhausted, terminate move
         */
        if (this.evalDone || lastPlayer == null)
            return;


        if (lastLockedPiece == null) { //They've not yet chosen their Piece to be played
            List<Piece> haveCaptures =
                    board.getPiecesOwnedBy(lastPlayer).stream().filter(piece -> !board.getManager().getDirectionsOfCapture(piece).isEmpty()).collect(Collectors.toList()); //We're evaluating all of their pieces to see which ones have captures to be made

            System.out.println("Captureable pieces: " + haveCaptures.size());
            System.out.println("Capturables: " + haveCaptures);

            System.out.println(board.getPiecesOwnedBy(lastPlayer));

            board.getPiecesOwnedBy(lastPlayer).forEach(p -> p.getChecker());

            if (haveCaptures.size() > 0) {
                //Restrict choice of next Piece to one of these Pieces

                this.moveablePieces.clear();
                this.moveablePieces = new ArrayList<>(haveCaptures);

                for (Piece haveCapture : haveCaptures) {
                    haveCapture.getChecker().getStrokeDashArray().addAll(5d, 5d);
                }

            }
            this.evalDone = true;
            return;
        }

        lastLockedPiece.getChecker().getStrokeDashArray().addAll(5d, 5d);


        //No choice selection, until they've chosen their first Piece

        //TODO once their turn ends, null the lastLockedPiece and update lastPlayer


        if (lastLockedPiece != null) { //The player has now chosen their starting piece
            if (board.getManager().getDirectionsOfCapture(lastLockedPiece).size() == 0) {
                System.out.println("No capturing moves available");
            } else
                System.out.println(board.getManager().getDirectionsOfCapture(lastLockedPiece));
        }



        this.evalDone = true;
        this.endMove = true;
        this.remainingMove = true;
        lastPlayer = nextInQueue();
        round++;
    }

    public boolean isEndMove() {
        return endMove;
    }

    public void setEndMove(boolean endMove) {
        this.endMove = endMove;
    }

    public int getRound() {
        return round;
    }
}

