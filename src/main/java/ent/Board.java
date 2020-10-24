/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import err.BoardSpacingException;

import java.awt.*;
import java.util.ArrayList;

public class Board {
    private final ArrayList<ArrayList<Piece>> board;
    private final Color evenTiles;
    private final Color oddTiles;
    private final int width;
    private final int height;

    public Board(ArrayList<ArrayList<Piece>> board, Color evenTiles, Color oddTiles) {
        this.board = board;
        this.evenTiles = evenTiles;
        this.oddTiles = oddTiles;
        this.width = board.get(0).size();
        this.height = board.size();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Color getEvenTiles() {
        return evenTiles;
    }

    public Color getOddTiles() {
        return oddTiles;
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
        private Color evenTiles = Color.WHITE;
        private Color oddTiles = Color.BLACK;

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

        public void setEvenTiles(Color evenTiles) {
            this.evenTiles = evenTiles;
        }

        public void setOddTiles(Color oddTiles) {
            this.oddTiles = oddTiles;
        }

        public Board build() throws BoardSpacingException {


            if (spacing <= 0 || spacing > height - 2)
                throw new BoardSpacingException("Spacing between pieces is either too small or too large!");

            ArrayList<ArrayList<Piece>> outer = new ArrayList<>();

            /*
            A 'Board' is represented as an ArrayList of ArrayLists. The first ArrayList contains the data for each row.
            To do this, the height metric is used to initialise the board row-by-row. This allows for a given piece
            on the board to be indexed by an (x,y) pair.
             */

            //This code initialises the new Board instance to the specified dimensions, along with Teams information
            // and individual Pieces in the correct conditions
            for (int i = 0; i < height; i++) {
                ArrayList<Piece> inner = new ArrayList<>();
                boolean flip = false;
                for (int j = 0; j < width; j++) {
                    if (!flip)
                        inner.add(
                                new Piece(j + i % 2, i, Piece.Type.MAN,
                                        new Team("Team 1", Color.BLACK, new ArrayList<>())));
                    //TODO Unit test this
                    //TODO add team-adding logic and team-assignment logic
                    //TODO add in 'spacing' parameter support
                    flip = !flip;
                }

                outer.add(inner);
            }
            return new Board(outer, evenTiles, oddTiles);
        }
    }
}
