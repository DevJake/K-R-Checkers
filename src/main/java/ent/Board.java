/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import javafx.scene.Node;

import java.awt.*;
import java.util.ArrayList;

public class Board extends Entity {
    private final ArrayList<ArrayList<Piece>> board;
    private final Color evenTilesColour;
    private final Color oddTilesColour;
    private final int width;
    private final int height;

//    public static ArrayList<ArrayList<Piece>> fromGridPane(GridPane pane){
//
//    }

    public Board(ArrayList<ArrayList<Piece>> board, Color evenTilesColour, Color oddTilesColour) {
        this.board = board;
        this.evenTilesColour = evenTilesColour;
        this.oddTilesColour = oddTilesColour;
        this.width = board.get(0).size();
        this.height = board.size();
    }

    public ArrayList<ArrayList<Piece>> getBoard() { //TODO remove
        return board;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Color getEvenTilesColour() {
        return evenTilesColour;
    }

    public Color getOddTilesColour() {
        return oddTilesColour;
    }

    public Piece getPieceAtIndex(int x, int y) {
        return board.get(x).get(y);
    }

    /**
     * Returns how many uncaptured pieces currently remain on the board
     *
     * @return Int - How many uncaptured pieces remain on the board.
     */
    public int getTotalPieces() { //TODO Unit test
        return board.stream().mapToInt(row -> (int) row.stream().filter(piece -> piece.getCapturedBy() == null).count()).sum();
    }


    @Override
    public String toString() {
        return "Board{" +
                "board=" + board +
                '}';
    }

    public static class Builder {
        private int width = 10;
        private int height = 10;
        private Color evenTilesColour = Color.WHITE;
        private Color oddTilesColour = Color.BLACK;

        private int spacing = 2;

        public void setSpacing(int spacing) {
            this.spacing = spacing;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setEvenTilesColour(Color evenTilesColour) {
            this.evenTilesColour = evenTilesColour;
        }

        public void setOddTilesColour(Color oddTilesColour) {
            this.oddTilesColour = oddTilesColour;
        }

        /*
        A 'Board' is represented as an ArrayList of ArrayLists. The first ArrayList contains the data for each row.
        To do this, the height metric is used to initialise the board row-by-row. This allows for a given piece
        on the board to be indexed by an (x,y) pair.
        */
        public Board build(ArrayList<Node> children) {
            ArrayList<ArrayList<Piece>> outer = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                ArrayList<Piece> temp = new ArrayList<>();
                ArrayList<Node> nodes = new ArrayList<>(children.subList(i * 8, (i * 8) + 8));

                for (int j = 0; j < nodes.size(); j++) {
                    temp.add(new Piece(j, i, Piece.Type.MAN, Player.Defaults.HUMAN.getPlayer(), nodes.get(j)));
                }

                outer.add(temp);

                //TODO fix player type
            }

            return new Board(outer, this.evenTilesColour, this.oddTilesColour);


        }
    }
}
