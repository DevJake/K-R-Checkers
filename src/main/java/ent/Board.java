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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board extends Entity {
    private final ArrayList<ArrayList<Piece>> tiles;
    private final Color playableTilesColour;
    private final Color unplayableTilesColour;
    private final int width;
    private final int height;

    public Board(ArrayList<ArrayList<Piece>> tiles, Color playableTilesColour, Color unplayableTilesColour) {
        this.tiles = tiles;
        this.playableTilesColour = playableTilesColour;
        this.unplayableTilesColour = unplayableTilesColour;
        this.width = tiles.get(0).size();
        this.height = tiles.size();
    }

    public ArrayList<ArrayList<Piece>> getTiles() { //TODO remove
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

    public Piece getPieceAtIndex(int x, int y) {
        return tiles.get(x).get(y);
    }

    public List<Piece> getPlayableTiles() {
        return getWithOffset(false);
    }

    private List<Piece> getWithOffset(boolean offset){
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < tiles.size(); i++) {
            int _offset = offset ? Math.abs((i-1)%2) : i%2;
            for (int j = 0; j < tiles.get(i).size()/2; j++) { //Slight optimisation ;)
                pieces.add(getPieceAtIndex(i, (j*2)+_offset));
            }
        }

        return pieces;
    }


    public List<Piece> getUnplayableTiles() {
        return getWithOffset(true);
    }

    /**
     * Returns how many uncaptured pieces currently remain on the board
     *
     * @return Int - How many uncaptured pieces remain on the board.
     */
    public int getTotalPieces() { //TODO Unit test
        return tiles.stream().mapToInt(row -> (int) row.stream().filter(piece -> piece.getCapturedBy() == null).count()).sum();
    }


    @Override
    public String toString() {
        return "Board{" +
                "board=" + tiles +
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

            for (int i = 0; i < 8; i++) {
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
