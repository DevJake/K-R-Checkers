/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import err.*;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fx.controllers.Main.toRGBString;

public class Board extends Entity {
    private final ArrayList<ArrayList<Tile>> tiles;
    private final Color playableTilesColour;
    private final Color unplayableTilesColour;
    private final int width;
    private final int height;
    private final BoardManager manager;

    public Board(ArrayList<ArrayList<Tile>> tiles, Color playableTilesColour, Color unplayableTilesColour) {
        this.tiles = tiles;
        this.playableTilesColour = playableTilesColour;
        this.unplayableTilesColour = unplayableTilesColour;
        this.width = tiles.get(0).size();
        this.height = tiles.size();

        for (Tile tile : getPlayableTiles()) {
            tile.setPlayable(true);
            tile.getNode().setStyle("-fx-background-color: " + toRGBString(getPlayableTilesColour()));
        }
        for (Tile tile : getUnplayableTiles()) {
            tile.setPlayable(false);
            tile.getNode().setStyle("-fx-background-color: " + toRGBString(getUnplayableTilesColour()));
        }

        this.manager = new BoardManager(this);
    }

    public BoardManager getManager() {
        return manager;
    }

    public void init() {
        tiles.forEach(t0 -> t0.forEach(Tile::init));
    }

    public ArrayList<ArrayList<Tile>> getTiles() { //TODO remove
        return tiles;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Color getPlayableTilesColour() {
        return playableTilesColour;
    }

    public Color getUnplayableTilesColour() {
        return unplayableTilesColour;
    }

    public Tile getPieceAtIndex(int x, int y) {
        return tiles.get(x).get(y);
    }

    public void setPieceAtIndex(int x, int y, Piece piece) {
        tiles.get(x).get(y).setPiece(piece);
    }

    public List<Tile> getPlayableTiles() {
        return getWithOffset(false);
    }

    private List<Tile> getWithOffset(boolean offset) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < this.tiles.size(); i++) {
            int _offset = offset ? Math.abs((i - 1) % 2) : i % 2;
            for (int j = 0; j < this.tiles.get(i).size() / 2; j++) { //Slight optimisation ;)
                tiles.add(getPieceAtIndex(i, (j * 2) + _offset));
            }
        }

        return tiles;
    }

    public List<Tile> getRow(int n) {
        return tiles.get(n);
    }

    public List<Tile> getColumn(int n) {
        return tiles.stream().map(t -> t.get(n)).collect(Collectors.toList());
    }


    public List<Tile> getUnplayableTiles() {
        return getWithOffset(true);
    }

    /**
     * Returns how many uncaptured pieces currently remain on the board
     *
     * @return Int - How many uncaptured pieces remain on the board.
     */
    public int getTotalPieces() { //TODO Unit test
        return tiles.stream().mapToInt(row -> (int) row.stream().filter(tile -> tile.getPiece().getCapturedBy() == null).count()).sum();
    }

    public List<Tile> getKingsWall(Player player) {
        switch (player.getHomeSide()) {
            case TOP:
                return tiles.get(0);
            case BOTTOM:
                return tiles.get(tiles.size() - 1);
        }
        return null;
    }

    public int getKingsWallRow(Player player) {
        switch (player.getHomeSide()) {
            case TOP:
                return 0;
            case BOTTOM:
                return height - 1;
        }
        return -1;
    }


    @Override
    public String toString() {
        return "Board{" +
                "board=" + tiles +
                '}';
    }

    public static class Builder {
        private final int width = 8;
        private final int height = 8;
        private final Color colourHuman = Color.RED;
        private final Color colourMachine = Color.BLACK;
        private Color evenTilesColour = Color.WHITE;
        private Color oddTilesColour = Color.BLACK;

        public Builder setEvenTilesColour(Color evenTilesColour) {
            this.evenTilesColour = evenTilesColour;
            return this;
        }

        public Builder setOddTilesColour(Color oddTilesColour) {
            this.oddTilesColour = oddTilesColour;
            return this;
        }

        /*
        A 'Board' is represented as an ArrayList of ArrayLists. The first ArrayList contains the data for each row.
        To do this, the height metric is used to initialise the board row-by-row. This allows for a given piece
        on the board to be indexed by an (x,y) pair.
        */
        public Board build(ArrayList<Node> children) {
            ArrayList<ArrayList<Tile>> outer = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                ArrayList<Tile> temp = new ArrayList<>();
                ArrayList<Node> nodes = new ArrayList<>(children.subList(64 - ((i * 8) + 8), 64 - (i * 8)));

                for (int j = 0; j < nodes.size(); j++) {
//                    System.out.println("i: " + i + " j:" + j);
                    Tile tile = new Tile(Color.ORANGE, (StackPane) nodes.get(j), new Piece(j, i, Color.BLACK,
                            Player.Defaults.HUMAN.getPlayer(), Piece.Type.MAN));
                    temp.add(tile);

                    if (i - 2 < 3) {
                        tile.getPiece().setPlayer(Player.Defaults.HUMAN.getPlayer());
                        tile.getPiece().setColour(colourHuman);
                    } else {
                        tile.getPiece().setPlayer(Player.Defaults.COMPUTER.getPlayer());
                        tile.getPiece().setColour(colourMachine);
                    }
                }

                outer.add(temp);

//                System.out.println("row: " + i + " added: " + temp);

                //TODO fix player type
            }

            return new Board(outer, this.evenTilesColour, this.oddTilesColour);


        }
    }

    public class BoardManager {
        private final Board board;

        private BoardManager(Board board) {
            this.board = board;
        }

        private boolean isOccupied(int x, int y) {
            return board.getPieceAtIndex(x, y).getPiece().getChecker() != null && board.getPieceAtIndex(x, y).getPiece().getCapturedBy() == null;
        }

        //Make a move from x,y to x,y
        public void makeMove(Piece origin, int toX, int toY) {
            boolean capturing = Math.abs(origin.getX() - toX) == 2 && Math.abs(origin.getY() - toY) == 2;

            boolean left = origin.getX() > toX;
            boolean up = origin.getY() < toY;


            if (left) {
                if (up) {
                    //Left+up
                    checkLeftUp(origin, capturing);
                } else {
                    //Left+down
                    checkLeftDown(origin, capturing);
                }
            } else {
                if (up) {
                    //Right+up
                    checkRightUp(origin, capturing);
                } else {
                    //Right+down
                    checkRightDown(origin, capturing);
                }
            }
        }

        //Attempt to execute a move left+up of the origin
        private void checkLeftUp(Piece origin, boolean capturingMove) {
            int destX = origin.getX() - (capturingMove ? 2 : 1);
            int destY = origin.getY() + (capturingMove ? 2 : 1);

            int midX = origin.getX();
            int midY = origin.getY();

            midX -= (capturingMove ? 2 : 1);
            midY += (capturingMove ? 2 : 1);


            finalCheck(origin, destX, destY, midX, midY, capturingMove, false);
        }

        //Attempt to execute a move left+down of the origin
        private void checkLeftDown(Piece origin, boolean capturingMove) {
            System.out.println("Check Left Down");
            int destX = origin.getX() - (capturingMove ? 2 : 1);
            int destY = origin.getY() - (capturingMove ? 2 : 1);

            int midX = origin.getX();
            int midY = origin.getY();

            midX -= (capturingMove ? 2 : 1);
            midY -= (capturingMove ? 2 : 1);


            finalCheck(origin, destX, destY, midX, midY, capturingMove, false);
        }


        //Attempt to execute a move right+up of the origin
        private void checkRightUp(Piece origin, boolean capturingMove) {
            System.out.println("Check Right Up");
            int destX = origin.getX() + (capturingMove ? 2 : 1);
            int destY = origin.getY() + (capturingMove ? 2 : 1);

            int midX = origin.getX();
            int midY = origin.getY();

            midX += (capturingMove ? 2 : 1);
            midY += (capturingMove ? 2 : 1);

            finalCheck(origin, destX, destY, midX, midY, capturingMove, false);
        }

        //Attempt to execute a move right+down of the origin
        private void checkRightDown(Piece origin, boolean capturingMove) {
            System.out.println("Check Right Down");
            int destX = origin.getX() + (capturingMove ? 2 : 1);
            int destY = origin.getY() - (capturingMove ? 2 : 1);

            int midX = origin.getX();
            int midY = origin.getY();

            midX += (capturingMove ? 2 : 1);
            midY -= (capturingMove ? 2 : 1);

            finalCheck(origin, destX, destY, midX, midY, capturingMove, false);
        }

        private void finalCheck(Piece origin, int destX, int destY, int midX, int midY, boolean capturingMove,
                                boolean trial) {


            if (!isOccupied(origin.getX(), origin.getY()))
                throw new BoardMoveInvalidOriginException(origin, destX, destY);

            if (isOccupied(destX, destY))
                throw new BoardMoveInvalidDestinationException("Invalid destination, already occupied! x:" + destX +
                        "," +
                        " y: " + destY);

            int diffX = Math.abs(origin.getX() - destX);
            int diffY = Math.abs(origin.getY() - destY);

            if (diffX == 0 || diffX > 2 || diffY == 0 || diffY > 2) //Filters for attempted moves that aren't diagonal
                throw new BoardMoveException("Attempted move was too extreme, or not along a diagonal!");


            if (capturingMove) {
                if (isOccupied(midX, midY))
                    throw new BoardMoveMissingPieceException("Attempting to perform a capture over non-existant " +
                            "piece! x:" + midX + ", y: " + midY);
                if (board.getPieceAtIndex(midX, midY).getPiece().getPlayer() == origin.getPlayer())
                    throw new BoardMoveSelfCaptureException("Attempting to capture a member of your team! x:" + midX + "," +
                            " y: "
                            + midY);
            }

            boolean kingOnlyMove = origin.getPlayer().getHomeSide() == Player.HomeSide.BOTTOM ?
                    destY < origin.getY() : destY > origin.getY();

            if (origin.getType() != Piece.Type.KING && kingOnlyMove)
                throw new BoardMoveNotKingException("This piece is not a king!");

            Piece capturing = capturingMove ? board.getPieceAtIndex(midX, midY).getPiece() : null;

            if (!trial)
                executeMove(origin, destX, destY, capturing);
        }

        private void executeMove(Piece origin, int destX, int destY, Piece captured) {
            if (captured != null) {
                origin.getPlayer().getCapturedPieces().add(captured);
                captured.deleteFromBoard();
            }

            board.getPieceAtIndex(destX, destY).init();
            board.getPieceAtIndex(origin.getX(), origin.getY()).delete();

            //TODO force capturing of neighbours
            //TODO force auto-crowning of piece if it's on the back board

            doAutoCapture(origin);

            //TODO check for a winning state
        }

        private void doAutoCapture(Piece piece) {
            Player opponent = piece.getPlayer().getName().equals(Player.Defaults.COMPUTER.getPlayer().getName()) ?
                    Player.Defaults.HUMAN.getPlayer() : Player.Defaults.COMPUTER.getPlayer();
            if (board.getKingsWallRow(opponent) == piece.getY() && piece.getType() == Piece.Type.MAN) {
                piece.makeKing();
                return; //Terminate all moves from here, since they've just been Crowned.
            }

            ArrayList<Boolean> validMoves = new ArrayList<>();
            /*
            Left-Up
            Left-Down
            Right-Up
            Right-Down
             */

            try {
                finalCheck(piece, piece.getX() - 2, piece.getY() + 2, piece.getX() - 1, piece.getY() + 1, true, true);
                validMoves.add(true);
            } catch (BoardMoveException e) {
                validMoves.add(false);
                //Do nothing
            }

            /*
            Check which directions are valid to move in
             */
            try {
                finalCheck(piece, piece.getX() - 2, piece.getY() - 2, piece.getX() - 1, piece.getY() - 1, true, true);
                validMoves.add(true);
            } catch (BoardMoveException e) {
                validMoves.add(false);
                //Do nothing
            }

            try {
                finalCheck(piece, piece.getX() + 2, piece.getY() + 2, piece.getX() + 1, piece.getY() + 1, true, true);
                validMoves.add(true);
            } catch (BoardMoveException e) {
                validMoves.add(false);
                //Do nothing
            }

            try {
                finalCheck(piece, piece.getX() + 2, piece.getY() - 2, piece.getX() + 1, piece.getY() - 1, true, true);
                validMoves.add(true);
            } catch (BoardMoveException e) {
                validMoves.add(false);
                //Do nothing
            }

            //The player has multiple options available... they must now decide which move to follow
            if (validMoves.stream().filter(t -> t == Boolean.TRUE).count() > 1) {
                //TODO it is now on the player to decide which move to take. Maybe fire event for this, including
                // which moves are valid. Also write a method to generate a list of Direction enums detailing which
                // moves are valid for a given piece

                return;
            }

            //There is now only one move possible. Find the move and execute it.
            if (validMoves.get(0))
                makeMove(piece, piece.getX() - 2, piece.getY() + 2);
            if (validMoves.get(1))
                makeMove(piece, piece.getX() - 2, piece.getY() - 2);
            if (validMoves.get(2))
                makeMove(piece, piece.getX() + 2, piece.getY() + 2);
            if (validMoves.get(3))
                makeMove(piece, piece.getX() + 2, piece.getY() - 2);
        }
    }
}
