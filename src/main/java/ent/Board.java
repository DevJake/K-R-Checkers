/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

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
        private int spacing = 2;

        public void setSpacing(int spacing) {
            this.spacing = spacing;
        }

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
                ArrayList<Node> nodes = new ArrayList<>(children.subList(i * 8, (i * 8) + 8));

                for (int j = 0; j < nodes.size(); j++) {
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

                //TODO fix player type
            }

            return new Board(outer, this.evenTilesColour, this.oddTilesColour);


        }
    }

    public class BoardManager {
        private final Board board;

        public BoardManager(Board board) {
            this.board = board;
        }
    }
}
