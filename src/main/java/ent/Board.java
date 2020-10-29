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

        getPlayableTiles().forEach(t -> t.setPlayable(true));
        getUnplayableTiles().forEach(t -> t.setPlayable(false));

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
        public boolean makeMove(Piece origin, int toX, int toY) {

            if (!isOccupied(origin.getX(), origin.getY()))
                throw new BoardMoveInvalidOriginException(origin, toX, toY);

            if (isOccupied(toX, toY))
                throw new BoardMoveInvalidDestinationException("Invalid destination, already occupied! x:" + toX + "," +
                        " y: " + toY);

            int diffX = Math.abs(origin.getX() - toX);
            int diffY = Math.abs(origin.getY() - toY);

            if (diffX == 0 || diffX > 2 || diffY == 0 || diffY > 2) //Filters for attempted moves that aren't diagonal
                throw new BoardMoveException("Attempted move was too extreme, or not along a diagonal!");


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


            return true;
        }

        //Attempt to execute a move left+up of the origin
        private void checkLeftUp(Piece origin, boolean capturingMove) {
            System.out.println("Check Left Up");
            int destX = origin.getX() - (capturingMove ? 2 : 1);
            int destY = origin.getY() + (capturingMove ? 2 : 1);

            int midX = origin.getX();
            int midY = origin.getY();

            midX -= (capturingMove ? 2 : 1);
            midY += (capturingMove ? 2 : 1);


            finalCheck(origin, destX, destY, midX, midY, capturingMove);
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


            finalCheck(origin, destX, destY, midX, midY, capturingMove);
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

            finalCheck(origin, destX, destY, midX, midY, capturingMove);
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

            finalCheck(origin, destX, destY, midX, midY, capturingMove);
        }

        private void finalCheck(Piece origin, int destX, int destY, int midX, int midY, boolean capturingMove) {
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
            executeMove(origin, destX, destY, capturing);
        }

        private void executeMove(Piece origin, int destX, int destY, Piece captured) {
            if (captured != null) {
                origin.getPlayer().getCapturedPieces().add(captured);
                captured.deleteFromBoard();
            }

            board.getPieceAtIndex(destX, destY).init();
            board.getPieceAtIndex(origin.getX(), origin.getY()).delete();
        }
    }
}
