/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import comms.Bridge;
import comms.protocol.ProtocolManager;
import err.EventProtocolMismatchException;
import event.BoardUpdateEvent;
import event.Event;
import event.GameCompletedEvent;
import fx.controllers.Main;
import fx.controllers.Menu;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private final Board board;
    private final Queue<Player> playerQueue = new LinkedList<>();
    private final List<Piece> moveablePieces = new ArrayList<>();
    public Piece lastLockedPiece;
    public Player lastPlayer;
    private int destX = 0;
    private int destY = 0;
    private int round = 0;
    private boolean allCapturesEvalDone = false;
    private boolean endMove = false;
    private boolean exhaustedSingleMove = false;
    private boolean aiHandled = false;

    public GameManager(Board board, List<Player> playerQueue, Pane canvas) {
        this.board = board;
        this.playerQueue.addAll(playerQueue);
    }

    public boolean isAiHandled() {
        return aiHandled;
    }

    public void setAiHandled(boolean aiHandled) {
        this.aiHandled = aiHandled;
    }

    public boolean isAllCapturesEvalDone() {
        return allCapturesEvalDone;
    }

    public void setAllCapturesEvalDone(boolean allCapturesEvalDone) {
        this.allCapturesEvalDone = allCapturesEvalDone;
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
        setMoveableNotice(this.moveablePieces, false);
        this.moveablePieces.clear();
        this.lastLockedPiece = lastLockedPiece;
        this.moveablePieces.add(lastLockedPiece);
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
                if (lastPlayer == Player.Defaults.COMPUTER.getPlayer()) {
//                    if (!isAiHandled())
                    handleMachineMove();
                }

                playRound();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (isGameFinished()) {
                Player winner = board.getPiecesOwnedBy(getPlayerQueue().get(0)).isEmpty() ? getPlayerQueue().get(1) :
                        getPlayerQueue().get(0);
                Player loser = getPlayerQueue().get(0) == winner ? getPlayerQueue().get(1) : getPlayerQueue().get(0);
                Event.Manager.fire(new GameCompletedEvent(winner, loser));

                Menu.setErrorLog(winner.getName() + " has won this game! Check the Player Scores tab for information " +
                        "on your scores...");

                Thread.currentThread().interrupt();
            }
        }).start();

    }

    public boolean hasExhaustedSingleMove() {
        return exhaustedSingleMove;
    }

    public void setExhaustedSingleMove(boolean exhaustedSingleMove) {
        this.exhaustedSingleMove = exhaustedSingleMove;
    }

    public List<Piece> getCapturesFor() {
        return board.getPiecesOwnedBy(lastPlayer).stream().filter(piece -> !board.getManager().getDirectionsOfCapture(piece).isEmpty()).collect(Collectors.toList());
        //We're evaluating all of their pieces to see which ones have captures to be made
    }

    public List<Piece> getHopsFor() {
        return board.getPiecesOwnedBy(lastPlayer).stream().filter(piece -> !getRemainingMoveDirections(piece).isEmpty()).collect(Collectors.toList());
        //We're evaluating all of their pieces to see which ones have non-captures to be made
    }

    public List<Piece> getWithAnyMove() {
        return board.getPiecesOwnedBy(lastPlayer).stream().filter(piece -> !board.getManager().getDirectionsOfCapture(piece).isEmpty() || !getRemainingMoveDirections(piece).isEmpty()).distinct().collect(Collectors.toList());
        //We're gathering all pieces that can make any form of move
    }

    public boolean onOpponentKingsRow(Piece piece) {
        return piece.getX() == (piece.getPlayer().getHomeSide() == Player.HomeSide.BOTTOM ?
                board.getKingsWallRow(Player.Defaults.COMPUTER.getPlayer()) :
                board.getKingsWallRow(Player.Defaults.HUMAN.getPlayer()));
        //We're determining if the given piece is on the opposition's King's Row
    }

    List<Direction> getRemainingMoveDirections(Piece piece) {
        return Arrays.stream(Direction.values()).filter(dir -> board.getManager().moveIsValid(piece, dir)).collect(Collectors.toList());
    }

    private void setMoveableNotice(List<Piece> pieces, boolean enabled) {
        pieces.forEach(p -> {
            if (p == null || p.getChecker() == null)
                return;
            if (enabled) {
                p.getChecker().getStrokeDashArray().clear();
                p.getChecker().getStrokeDashArray().addAll(5d, 5d);
                p.getChecker().setStrokeWidth(2d);
            } else {
                p.getChecker().getStrokeDashArray().clear();
                p.getChecker().setStrokeWidth(1d);
            }
        });
    }

    private void handleMachineMove() {
        doMachineRound();
        try {
            Bridge.send(ProtocolManager.encodeFor(new BoardUpdateEvent(null, board)));
        } catch (IOException | EventProtocolMismatchException e) {
            e.printStackTrace();
        }

        /*
        Set the last piece to that that the AI chooses, then call playRound. It should be a valid move already...

        Next, call setAiHandled and set it true. Also, call endMove() to reset and prep for next player, the human
         */
//        setAiHandled(true);
        setEndMove(true);
    }

    public int getDestX() {
        return destX;
    }

    public void setDestX(int destX) {
        this.destX = destX;
    }

    public int getDestY() {
        return destY;
    }

    public void setDestY(int destY) {
        this.destY = destY;
    }

    private void doMachineRound() {
        Random rand = new Random();
//        Piece piece = Main.gameManager.getWithAnyMove().get(rand.nextInt(Main.gameManager.getWithAnyMove().size()));

        List<Piece> directions = Main.gameManager.getHopsFor();

        if (getCapturesFor().size() > 0)
            directions = getCapturesFor();
        Piece piece = directions.get(rand.nextInt(directions.size()));
        Direction direction =
                getRemainingMoveDirections(piece).get(rand.nextInt(getRemainingMoveDirections(piece).size()));

        setDestX(piece.getX() + direction.getxChange());
        setDestY(piece.getY() + direction.getyChange());

//        Main.mainBoard.getManager().makeMove(piece, piece.getX() + direction.getxChange(),
//                piece.getY() + direction.getyChange());

//        System.out.println("Direction=" + direction);
//        System.out.println("CoordsX=" + piece.getX());
//        System.out.println("CoordsY=" + piece.getY());

        Platform.runLater(() -> Main.mainBoard.getManager().makeMove(piece, Main.gameManager.getDestX(),
                Main.gameManager.getDestY()));

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

        if (exhaustedSingleMove)
            setEndMove(true);

        if (getCapturesFor().isEmpty() && getRemainingMoveDirections(lastLockedPiece)
                .isEmpty() && lastLockedPiece != null)
            setEndMove(true);

        if (lastLockedPiece != null && onOpponentKingsRow(lastLockedPiece) && lastLockedPiece.getType() != Piece.Type.KING && exhaustedSingleMove)
            setEndMove(true);

        if (isEndMove()) { //If their move has ended, reset the game's state and prepare for the next player to play
            endMove();
            return;
        }

        if (lastPlayer == null)
            return;

        if (!this.allCapturesEvalDone) {
            this.moveablePieces.clear();
            //They've not yet chosen their Piece to be played, so we perform the initial check against all Pieces
            setMoveableNotice(board.getPiecesOwnedBy(lastPlayer), false);

            List<Piece> capturesFor = getCapturesFor();

            if (capturesFor.size() > 0) {
                //Restrict choice of next Piece to one of these Pieces
                this.moveablePieces.addAll(capturesFor);
            } else {
                this.moveablePieces.addAll(getWithAnyMove());
            }

            this.allCapturesEvalDone = true;
        } else {
            this.moveablePieces.add(lastLockedPiece);
        }

        setMoveableNotice(this.moveablePieces, true);


        //No choice selection, until they've chosen their first Piece

        //TODO once their turn ends, null the lastLockedPiece and update lastPlayer


//        if (lastLockedPiece != null) { //The player has now chosen their starting piece
//            if (board.getManager().getDirectionsOfCapture(lastLockedPiece).size() == 0) {
//                System.out.println("No capturing moves available");
//            } else
//                System.out.println(board.getManager().getDirectionsOfCapture(lastLockedPiece));
//        }

        //TODO end their move if they cannot make a move
    }

    private void endMove() {
        this.allCapturesEvalDone = false;
        this.endMove = false;
        this.exhaustedSingleMove = false;
        this.lastLockedPiece = null;
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

