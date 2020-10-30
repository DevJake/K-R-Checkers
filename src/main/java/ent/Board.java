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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

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

        for (Tile tile : getPlayableTiles()) {
            tile.setPlayable(true);
            tile.setColour(getPlayableTilesColour());
        }
        for (Tile tile : getUnplayableTiles()) {
            tile.setPlayable(false);

            tile.setColour(getUnplayableTilesColour());
            tile.delete();
        }

        this.manager = new BoardManager(this);
    }

    public BoardManager getManager() {
        return manager;
    }

    public void init() {
        tiles.forEach(t0 -> t0.forEach(Tile::init));
    }

    public void init(Pane canvas) {
        init();
        Movement movement = new Movement(canvas);

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

    public Tile getTileAtIndex(int x, int y) {
//        System.out.println("thingy=" + tiles.get(x).get(y).toString());
        return tiles.get(y).get(x);
    }

    public void setTileAtIndex(int x, int y, Piece piece) {
        tiles.get(y).get(x).setPiece(piece);
    }

    public List<Tile> getPlayableTiles() {
        return getWithOffset(false);
    }

    private List<Tile> getWithOffset(boolean offset) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < this.tiles.size(); i++) {
            int _offset = offset ? Math.abs((i - 1) % 2) : i % 2;
            for (int j = 0; j < this.tiles.get(i).size() / 2; j++) { //Slight optimisation ;)
                tiles.add(getTileAtIndex(i, (j * 2) + _offset));
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

    public void setShowCoordinates(boolean show) {
        getPlayableTiles().forEach(t -> {
            if (show)
                t.showLabel();
            else
                t.removeLabel();
        });
    }

    public List<Tile> getKingsWall(Player player) {
        return getRow(getKingsWallRow(player));
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
        private final Color colourMachine = Color.PINK;
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

    private class Movement {
        private final Pane pane;
        private Line line = init();
        private double beginX;
        private double beginY;
        private double tileX;
        private double tileY;

        public Movement(Pane canvas) {
            this.pane = canvas;
            pane.getChildren().add(line);

            pane.onMouseDraggedProperty().set(event -> {
                line.setEndX(event.getSceneX());
                line.setEndY(event.getSceneY());

                Tile tile = getTileAtIndex(((int) tileX), ((int) tileY));
                if (tile.isPlayable() && tile.getPiece().getChecker() != null)
                    line.setVisible(true);


//                Tile tile = getTileAtIndex(gridX, gridY);
//                getTileAtIndex(gridX, gridY).setColour(Color.BLACK);

            });

            pane.onMousePressedProperty().set(event -> {
                this.tileX = Math.floor(event.getX() / (pane.getWidth() / 8));
                this.tileY = Math.floor((pane.getHeight() - event.getY()) / (pane.getHeight() / 8));

                this.beginX = (tileX * (pane.getWidth() / 8)) + 50;
                this.beginY = pane.getHeight()-(tileY*(pane.getHeight()/8))-50;

                line.setStartX(beginX);
                line.setStartY(beginY);

            });


            //TODO shit's broke yo, fix it
            pane.onMouseClickedProperty().set(event -> {
                pane.getChildren().remove(line);
                this.line = init();
                pane.getChildren().add(this.line);
            });
        }

        private Line init() {
            Line line = new Line(0, 0, 0, 0);
            line.setVisible(false);
            line.setStrokeWidth(5);
            return line;
        }
    }

    public class BoardManager {
        private final Board board;

        private BoardManager(Board board) {
            this.board = board;
        }

        private boolean isOccupied(int x, int y) {
            return board.getTileAtIndex(x, y).getPiece().getChecker() != null && board.getTileAtIndex(x, y).getPiece().getCapturedBy() == null;
        }

        //Make a move from x,y to x,y
        public void makeMove(Piece origin, int toX, int toY) {
            boolean capturing = Math.abs(origin.getX() - toX) == 2 && Math.abs(origin.getY() - toY) == 2;

            boolean left = toX < origin.getX();
            boolean up = toY > origin.getY();

            System.out.println(origin.getX());
            System.out.println(toX);
            System.out.println(origin.getY());
            System.out.println(toY);


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
                if (board.getTileAtIndex(midX, midY).getPiece().getPlayer() == origin.getPlayer())
                    throw new BoardMoveSelfCaptureException("Attempting to capture a member of your team! x:" + midX + "," +
                            " y: "
                            + midY);
            }

            boolean kingOnlyMove = origin.getPlayer().getHomeSide() == Player.HomeSide.BOTTOM ?
                    destY < origin.getY() : destY > origin.getY();

            if (origin.getType() != Piece.Type.KING && kingOnlyMove)
                throw new BoardMoveNotKingException("This piece is not a king!");

            Piece capturing = capturingMove ? board.getTileAtIndex(midX, midY).getPiece() : null;

            if (!trial)
                executeMove(origin, destX, destY, capturing);
        }

        private void executeMove(Piece origin, int destX, int destY, Piece captured) {
            if (captured != null) {
                origin.getPlayer().getCapturedPieces().add(captured);
                captured.deleteFromBoard();
            }

            board.getTileAtIndex(destX, destY).init();
            board.getTileAtIndex(origin.getX(), origin.getY()).delete();

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
