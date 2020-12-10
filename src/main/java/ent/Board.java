/*
 * Copyright (c) Candidate 181379, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is the main source of all game-board interaction, control and management. This outer class primarily
 * aims to store and manage metadata required as the bare minimum for the board. For example, this includes storing
 * the 2D array used for mapping {@link Tile} instances, helper methods for retrieving explicit collections of Tiles
 * - such as {@link #getPlayableTiles()} and {@link #getKingsWall(Player)} - and other important metadata such as the
 * board's width and height.
 * <p>
 * Within this class are three subclasses, each with their own explicit purpose:
 * {@link BoardManager}
 * {@link Movement}
 * {@link Builder}
 * <p>
 * Each of these serves an important purpose, as is discussed in their respective documentation.
 * <p>
 * The constructor for this class is very important in correctly constructing a new board. It contains lots of
 * error-avoidance procedures and safety measures to ensure consistency in mapping parameters to behaviour.
 * <p>
 * Arguably, the most valuable behaviour of this class is abstracted interaction with the {@link #tiles} 2D array.
 * Direct interaction can prove difficult, as the array uses x/y coordinates - atypical to row/column coordinates
 * used in most games. Row/column coordinates are the equivalent of inverse y/x.
 * <p>
 * Whilst abstracting interaction between these two systems is difficult, constructing a new {@link #tiles} array
 * requires direct conversion between these coordinate systems. An example of this complexity is shown in
 * {@link #getWithOffset(boolean)}.
 */
public class Board extends Entity {
    /**
     * This nested {@link ArrayList} structure is used for storing the {@link Tile} instances that represent the
     * Board's contents.
     * <p>
     * Usage of ArrayLists was favoured due to their classes providing helpful functions, such as
     * {@link ArrayList#size()}. Combined with the complexity of the game, this would prove to increase efficiency in
     * executing moves, querying the board, and other complex calculations. Furthermore, specific tiles can be easily
     * accessed with the {@link ArrayList#get(int)} methods.
     *
     * @see Tile
     * @see ArrayList
     */
    private final ArrayList<ArrayList<Tile>> tiles;
    /**
     * The {@link Color} of playable {@link Tile} pieces.
     */
    private final Color playableTilesColour;
    /**
     * The {@link Color} of unplayable {@link Tile} pieces.
     */
    private final Color unplayableTilesColour;
    /**
     * The calculated width of the Board, assigned in the constructor.
     */
    private final int width;
    /**
     * The calculated height of the Board, assigned in the constructor.
     */
    private final int height;
    /**
     * The {@link BoardManager} instance associated with this Board.
     */
    private final BoardManager manager;
    /**
     * Should we display {@link javafx.scene.control.Label Labels} on top of the {@link #tiles}, indicating their x
     * and y coordinates?
     */
    private boolean showLabels = false;

    /**
     * The constructor serves a very important role in constructing a new instance. Typically, the structure of
     * inbound data is not supplied by a direct instantiation call, but instead via the
     * {@link Builder#build(ArrayList)} method of the {@link Builder}. This constructor focuses less on validating
     * parameters (done by the Builder), but instead on performing post-construction Board manipulation. For example,
     * establishing which {@link Tile Tiles} are and aren't playable, assigning them the correct respective
     * {@link Color}, and removing metadata from unplayable {@link Piece Pieces}.
     * <p>
     * The constructor also create a new {@link BoardManager} instance, assigning it to the {@link #manager} variable.
     *
     * @param tiles                 {@link ArrayList} - A two-dimensional nested ArrayList object, containing
     *                              {@link Tile} objects that represent the structure of the Board.
     * @param playableTilesColour   {@link Color} - The Color to be assigned to playable {@link Tile Tiles}. Playable
     *                              tiles are those that the {@link Player} can interact with.
     * @param unplayableTilesColour {@link Color} - The Color to be assigned to unplayable {@link Tile Tiles}.
     * @param showLabels            Boolean - If labels should be shown on top of Pieces.
     *
     * @see Builder
     * @see Tile
     * @see Color
     * @see Piece
     */
    public Board(ArrayList<ArrayList<Tile>> tiles, Color playableTilesColour, Color unplayableTilesColour,
                 Boolean showLabels) {
        this.tiles = tiles;
        this.playableTilesColour = playableTilesColour;
        this.unplayableTilesColour = unplayableTilesColour;
        this.width = tiles.get(0).size();
        this.height = tiles.size();
        this.showLabels = showLabels;

        for (Tile tile : getPlayableTiles()) {
            tile.setPlayable(true);
            tile.setColour(getPlayableTilesColour());
        }
        for (Tile tile : getUnplayableTiles()) {
            tile.setPlayable(false);
            tile.getPiece().setPlayer(null); //Don't use Player.Defaults.NONE.getPlayer() as this tile is unplayable.
            // NONE is for tiles that have no occupying player.

            tile.setColour(getUnplayableTilesColour());
            tile.deleteOccupyingPiece(isShowLabels());
        }

        this.manager = new BoardManager(this);
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }

    /**
     * Gets manager.
     *
     * @return the manager
     */
    public BoardManager getManager() {
        return manager;
    }

    /**
     * Each {@link Tile} has an {@link Tile#init()} method, that performs essential setup behaviour. This method acts
     * as a helper function to iteratively call this function on all Tiles within the {@link #tiles} variable.
     *
     * @see Tile
     * @see Tile#init() Tile#init()
     */
    public void init() {
        tiles.forEach(t0 -> t0.forEach(Tile::init));
    }

    /**
     * This method performs important initialisation steps required for Board behaviour. Namely, initialisation and
     * attachment of event listeners within the {@link Movement} nested class to this Board's {@link Pane} node, and
     * the functionality outlined within {@link #init()}.
     *
     * @param canvas {@link Pane} - The Pane node in which the Board renders and should therefore be used for
     *               identifying mouse interaction events.
     *
     * @see Movement
     * @see Pane
     */
    public void init(Pane canvas) {
        init();
        Movement.init(canvas, this);

    }

    /**
     * Gets tiles.
     *
     * @return the tiles
     */
    public ArrayList<ArrayList<Tile>> getTiles() {
        return tiles;
    }

    /**
     * Gets height.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets width.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets playable tiles colour.
     *
     * @return the playable tiles colour
     */
    public Color getPlayableTilesColour() {
        return playableTilesColour;
    }

    /**
     * Gets unplayable tiles colour.
     *
     * @return the unplayable tiles colour
     */
    public Color getUnplayableTilesColour() {
        return unplayableTilesColour;
    }

    /**
     * Returns the given {@link Tile} entity at the specified x and y coordinates. Notably, the structure of the
     * nested {@link ArrayList} structure used within the {@link #tiles} variable means that, despite representing an
     * x/y coordinate system, must be referenced using y/x notation. This method is used to abstract away the
     * potential confusion this may cause.
     *
     * @param x Int - The x coordinate of the {@link Tile} to be retrieved.
     * @param y Int - The y coordinate of the {@link Tile} to be retrieved.
     *
     * @return {@link Tile} - The given {@link Tile} entity at the specified x and y coordinates.
     *
     * @see Tile
     * @see #tiles #tiles
     */
    public Tile getTileAtIndex(int x, int y) {
        return tiles.get(y).get(x);
    }

    /**
     * This method performs the same abstraction functionality as {@link #getTileAtIndex(int, int)}, but instead
     * updates the internal {@link Piece} instance for the {@link Tile} at the given coordinates.
     *
     * @param x     Int - The x coordinate of the {@link Tile} to be updated.
     * @param y     Int - The y coordinate of the {@link Tile} to be updated.
     * @param piece {@link Piece} - The new Piece instance to be used for replacement.
     *
     * @see Tile
     * @see #getTileAtIndex(int, int) #getTileAtIndex(int, int)
     * @see Piece
     */
    public void setTileAtIndex(int x, int y, Piece piece) {
        tiles.get(y).get(x).setPiece(piece);
    }

    /**
     * Gets playable tiles.
     *
     * @return the playable tiles
     */
    public List<Tile> getPlayableTiles() {
        return getWithOffset(false);
    }

    private List<Tile> getWithOffset(boolean offset) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < this.tiles.size(); i++) {
            int compute_offset = offset ? Math.abs((i - 1) % 2) : i % 2;
            for (int j = 0; j < this.tiles.get(i).size() / 2; j++) { //Slight optimisation ;)
                tiles.add(getTileAtIndex(i, (j * 2) + compute_offset));
            }
        }

        return tiles;
    }

    /**
     * Gets row.
     *
     * @param n Int - The integer index for the row to be retrieved.
     *
     * @return {@link List} - A List object containing the {@link Tile Tiles} contained in row *n*.
     */
    public List<Tile> getRow(int n) {
        return tiles.get(n);
    }

    /**
     * Unlike the simplicity found in retrieving a specific row, retrieving a given column requires collecting all
     * {@link Tile} entities at a given index of each row. This method simplifies the column-retrieval process by
     * using Java's {@link java.util.stream.Stream} library.
     *
     * @param n Int - The integer index for the column to be retrieved.
     *
     * @return {@link List} - A List object containing the {@link Tile Tiles} contained in column *n*.
     */
    public List<Tile> getColumn(int n) {
        return tiles.stream().map(t -> t.get(n)).collect(Collectors.toList());
    }

    /**
     * Gets unplayable tiles.
     *
     * @return the unplayable tiles
     */
    public List<Tile> getUnplayableTiles() {
        return getWithOffset(true);
    }

    /**
     * Returns how many uncaptured {@link Piece Pieces} currently remain on the Board
     *
     * @return Int - How many uncaptured {@link Piece Pieces} remain on the Board.
     *
     * @see Tile
     * @see Piece#getCapturedBy() Piece#getCapturedBy()
     */
    public int getTotalPieces() {
        return tiles.stream().mapToInt(row -> (int) row.stream().filter(tile -> tile.getPiece().getCapturedBy() == null).count()).sum();
    }

    //Gets all Pieces that a given Player owns.
    public List<Piece> getPiecesOwnedBy(Player p) {
        return this.getPlayableTiles().stream().filter(t -> t.getPiece().getPlayer() == p).map(t -> t.getPiece()).collect(Collectors.toList());
    }

    /**
     * This method toggles the visibility of the x/y coordinates for each {@link Tile}. If enabled, a
     * {@link javafx.scene.control.Label} is displayed on top of each Tile's {@link StackPane}, showing the x/y
     * coordinates for that Tile.
     */
    public void renderAllLabels() {
        getTiles().forEach(outer -> outer.forEach(Tile::showLabel));
    }

    /**
     * This method returns the back row - otherwise titled the King's Row - of a {@link Player}. This is the back-most
     * row of the board, relative to that Player's origin side. Moving a {@link Piece} on top the King's Row results
     * in it being made a {@link ent.Piece.Type#KING KING}.
     *
     * @param player {@link Player} - The Player to use for determining the orientation of the back row.
     *
     * @return {@link List} - A List of {@link Tile} instances that compose the back row/King's Row for the
     * given {@link Player Player's} team.
     *
     * @see ent.Piece.Type#KING ent.Piece.Type#KING
     * @see Piece
     * @see Player
     * @see Tile
     */
    public List<Tile> getKingsWall(Player player) {
        return getRow(getKingsWallRow(player));
    }

    /**
     * This method performs the same functionality as {@link #getKingsWall(Player)}, but instead returns the integer
     * index of the row, in respect to the {@link ent.Player.HomeSide} of the specified {@link Player}.
     *
     * @param player {@link Player} - The Player to determine the {@link ent.Player.HomeSide} of.
     *
     * @return Int - The integer index of the King's Wall for the given {@link Player}.
     *
     * @see Player
     * @see ent.Player.HomeSide
     * @see #getKingsWall(Player) #getKingsWall(Player)
     */
    public int getKingsWallRow(Player player) {
        if (player.getHomeSide() == Player.HomeSide.TOP) {
            return 0;
        } else if (player.getHomeSide() == Player.HomeSide.BOTTOM) {
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

    /**
     * This class serves as a preliminary means of constructing new {@link Board} instances with minimal information
     * via a chainable method call sequence - a 'builder' class pattern.
     * <p>
     * Furthermore, the {@link #build(ArrayList)} method is responsible for constructing the nested {@link ArrayList}
     * structure required for the {@link Board#tiles} variable. As mentioned in {@link Board Board's} constructor,
     * it's important to convert the row/column coordinate system to the x/y coordinate system used by the game.
     * Given that row/column coordinates behave inversely to x/y, the build method is responsible for applying this
     * complex conversion.
     */
    public static class Builder {
        //        private final Color colourHuman = Color.RED;
//        private final Color colourMachine = Color.PINK;
        private Color evenTilesColour = Color.WHITE;
        private Color oddTilesColour = Color.BLACK;
        private boolean showLabels = false;

        public Builder setShowLabels(boolean showLabels) {
            this.showLabels = showLabels;
            return this;
        }

        /**
         * Sets even tiles colour.
         *
         * @param evenTilesColour the even tiles colour
         *
         * @return the even tiles colour
         */
        public Builder setEvenTilesColour(Color evenTilesColour) {
            this.evenTilesColour = evenTilesColour;
            return this;
        }

        /**
         * Sets odd tiles colour.
         *
         * @param oddTilesColour the odd tiles colour
         *
         * @return the odd tiles colour
         */
        public Builder setOddTilesColour(Color oddTilesColour) {
            this.oddTilesColour = oddTilesColour;
            return this;
        }

        /**
         * A 'Board' is represented as a series of nested {@link ArrayList} objects. The first ArrayList contains the
         * {@link Node} instances for each row. First, we use the height metric to initialise a temporary 2D array
         * row-by-row. To convert this row/column system to the preferable x/y system, we then need to invert it.
         * This method performs the same operation in an efficient method, iterating through every value of the
         * provided parameter, inverting coordinates in-situ.
         * <p>
         * This allows for a given piece on the board to be indexed by an (x,y) pair.
         *
         * @param children {@link ArrayList} - The row/column base version of {@link Node} entities to be used
         *                 for the board. This will be converted to an x/y coordinate system.
         *
         * @return {@link Board} - A new {@link Board} instance, now using the x/y coordinate system.
         *
         * @see Node
         * @see Board
         * @see ArrayList
         */
        public Board build(ArrayList<Node> children) {
            ArrayList<ArrayList<Tile>> outer = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                ArrayList<Tile> temp = new ArrayList<>();
                ArrayList<Node> nodes = new ArrayList<>(children.subList(64 - ((i * 8) + 8), 64 - (i * 8)));

                for (int j = 0; j < nodes.size(); j++) {
                    Tile tile = new Tile(Color.ORANGE, (StackPane) nodes.get(j), new Piece(j, i, Color.BLACK,
                            Player.Defaults.HUMAN.getPlayer(), Piece.Type.MAN));
                    temp.add(tile);

                    if (i - 2 < 3) {
                        tile.getPiece().setPlayer(Player.Defaults.HUMAN.getPlayer());
                        tile.getPiece().setColour(Player.Defaults.HUMAN.getPlayer().getColour());
                    } else {
                        tile.getPiece().setPlayer(Player.Defaults.COMPUTER.getPlayer());
                        tile.getPiece().setColour(Player.Defaults.COMPUTER.getPlayer().getColour());
                    }
                }

                outer.add(temp);
            }

            return new Board(outer, this.evenTilesColour, this.oddTilesColour, showLabels);


        }
    }

}
