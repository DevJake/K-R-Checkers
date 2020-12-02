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
     *
     * @see Builder
     * @see Tile
     * @see Color
     * @see Piece
     */
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
            tile.getPiece().setPlayer(null); //Don't use Player.Defaults.NONE.getPlayer() as this tile is unplayable.
            // NONE is for tiles that have no occupying player.

            tile.setColour(getUnplayableTilesColour());
            tile.delete();
        }

        this.manager = new BoardManager(this);
    }

    public BoardManager getManager() {
        return manager;
    }

    /**
     * Each {@link Tile} has an {@link Tile#init()} method, that performs essential setup behaviour. This method acts
     * as a helper function to iteratively call this function on all Tiles within the {@link #tiles} variable.
     *
     * @see Tile
     * @see Tile#init()
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
     * @see #tiles
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
     * @see #getTileAtIndex(int, int)
     * @see Piece
     */
    public void setTileAtIndex(int x, int y, Piece piece) {
        tiles.get(y).get(x).setPiece(piece);
    }

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
     * @param n Int - The integer index for the row to be retrieved.
     *
     * @return {@link List<Tile>} - A List object containing the {@link Tile Tiles} contained in row *n*.
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
     * @return {@link List<Tile>} - A List object containing the {@link Tile Tiles} contained in column *n*.
     */
    public List<Tile> getColumn(int n) {
        return tiles.stream().map(t -> t.get(n)).collect(Collectors.toList());
    }

    public List<Tile> getUnplayableTiles() {
        return getWithOffset(true);
    }

    /**
     * Returns how many uncaptured {@link Piece Pieces} currently remain on the Board
     *
     * @return Int - How many uncaptured {@link Piece Pieces} remain on the Board.
     *
     * @see Tile
     * @see Piece#getCapturedBy()
     */
    public int getTotalPieces() { //TODO Unit test
        return tiles.stream().mapToInt(row -> (int) row.stream().filter(tile -> tile.getPiece().getCapturedBy() == null).count()).sum();
    }

    /**
     * This method toggles the visibility of the x/y coordinates for each {@link Tile}. If enabled, a
     * {@link javafx.scene.control.Label} is displayed on top of each Tile's {@link StackPane}, showing the x/y
     * coordinates for that Tile.
     *
     * @param show Boolean - If the coordinate Labels should be displayed.
     */
    public void setShowCoordinates(boolean show) {
        getPlayableTiles().forEach(t -> {
            if (show)
                t.showLabel();
            else
                t.removeLabel();
        });
    }

    /**
     * This method returns the back row - otherwise titled the King's Row - of a {@link Player}. This is the back-most
     * row of the board, relative to that Player's origin side. Moving a {@link Piece} on top the King's Row results
     * in it being made a {@link ent.Piece.Type#KING KING}.
     *
     * @param player {@link Player} - The Player to use for determining the orientation of the back row.
     *
     * @return {@link List<Tile>} - A List of {@link Tile} instances that compose the back row/King's Row for the
     * given {@link Player Player's} team.
     *
     * @see ent.Piece.Type#KING
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
     * @see #getKingsWall(Player)
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

        /**
         * A 'Board' is represented as a series of nested {@link ArrayList} objects. The first ArrayList contains the
         * {@link Node} instances for each row. First, we use the height metric to initialise a temporary 2D array
         * row-by-row. To convert this row/column system to the preferable x/y system, we then need to invert it.
         * This method performs the same operation in an efficient method, iterating through every value of the
         * provided parameter, inverting coordinates in-situ.
         * <p>
         * This allows for a given piece on the board to be indexed by an (x,y) pair.
         *
         * @param children {@link ArrayList<Node>} - The row/column base version of {@link Node} entities to be used
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

    /**
     * This class is responsible for handling all interaction events relating to the {@link Board} instance. This
     * includes {@link javafx.scene.input.MouseEvent} instances which are used for determining drag-and-drop gameplay
     * mechanics.
     * <p>
     * The means for implementing drag-and-drop is by drawing a line from the origin {@link Piece} to the destination
     * {@link Tile}, then executing the move upon mouse click release.
     */
    private static class Movement {
        /**
         * This variable is re-instantiated each time the mouse is de-pressed. It simply stores the line used to be
         * drawing between origin and destination {@link Tile Tiles}.
         */
        private static Line line = getNewLine();
        /**
         * The centered click origin x coordinate.
         */
        private static double beginX;
        /**
         * The centered click origin y coordinate.
         */
        private static double beginY;
        /**
         * The true click origin x coordinate.
         */
        private static double tileX;
        /**
         * The true click origin y coordinate.
         */
        private static double tileY;

        public Movement(Pane canvas) {

        }

        /**
         * This method initialises the movement listeners, associating each listener with the provided {@link Pane},
         * directing output to the {@link Board}.
         *
         * @param canvas {@link Pane} - The Pane to be monitored for mouse interactions.
         * @param board  {@link Board} - The Board instance to be controlled via mouse interactions.
         *
         * @see Pane
         * @see Board
         */
        public static void init(Pane canvas, Board board) {
            canvas.getChildren().add(line);

            /*
            This method is fired when the mouse is dragged. This doesn't record the initial location of the mouse
            before dragging, so this method only updates the end location of the line.
             */
            canvas.onMouseDraggedProperty().set(event -> {
                line.setEndX(event.getSceneX());
                line.setEndY(event.getSceneY());

                Tile tile = board.getTileAtIndex(((int) tileX), ((int) tileY));
                if (tile.isPlayable() && tile.getPiece().getChecker() != null)
                    line.setVisible(true);
            });

            /*
            This method is fired when the user initially presses the mouse. Here, we calculate what Tile was clicked,
             determine the centre coordinates of that tile, and assign the start of our line to that central x/y
             position.
             */
            canvas.onMousePressedProperty().set(event -> {
                tileX = Math.floor(event.getX() / (canvas.getWidth() / 8));
                tileY = Math.floor((canvas.getHeight() - event.getY()) / (canvas.getHeight() / 8));

                beginX = (tileX * (canvas.getWidth() / 8)) + 50;
                beginY = canvas.getHeight() - (tileY * (canvas.getHeight() / 8)) - 50;
                //Our board is only ever 8x8 tiles in size, so we can divide our x and y mouse coordinates by 8ths to
                // create new uniform coordinates.

                line.setStartX(beginX);
                line.setStartY(beginY);
            });


            //TODO shit's broke yo, fix it; Might be, no longer sure lol
            /*
            This method is fired when the user ends a mouse click. Here, we end the drag-and-drop operation. We
            calculate the origin and final x/y coordinates of the mouse drag operation, identifying the relevant
            Piece object for each. Then, we validate the movement, ensuring it is feasible. Finally, we execute the
            move by calling #getManager()#makeMove(origin, destination).

            After executing the move, we destroy the line that's being rendered on the Pane, replacing it with a new
            line.
             */
            canvas.onMouseClickedProperty().set(event -> {
                double destTileX = Math.floor(event.getX() / (canvas.getWidth() / 8));
                double destTileY = Math.floor((canvas.getHeight() - event.getY()) / (canvas.getHeight() / 8));
                System.out.println("actual- " + event.getX() + ":" + event.getY());
                System.out.println("centered- " + destTileX + ":" + destTileY);

                Tile tileOrigin = board.getTileAtIndex(((int) tileX), ((int) tileY));
                Tile tileDest = board.getTileAtIndex(((int) destTileX), ((int) destTileY));

                System.out.println("--------------");
                System.out.println("origin playable: " + tileOrigin.isPlayable());
                System.out.println("destination playable: " + tileDest.isPlayable());
                System.out.println("origin checker state: " + tileOrigin.getPiece().getChecker());
                System.out.println("destination player state: " + tileDest.getPiece().getPlayer().getName());

                /*
                Origin should be a playable tile.
                Origin should have a valid checker.

                Destination should be an empty tile.
                Destination should be a playable tile.
                 */
                if (tileOrigin.isPlayable() &&
                        tileDest.isPlayable() &&
                        tileOrigin.getPiece().getChecker() != null &&
                        tileDest.getPiece().getPlayer() == Player.Defaults.NONE.getPlayer()) {
//                    System.out.println(tileDest.getPiece().getPlayer().getName());
                    board.getManager().makeMove(tileOrigin.getPiece(), tileDest.getPiece().getX(),
                            tileDest.getPiece().getY());

                    //TODO if destX/Y is further than 1 tile away, void, unless it involves a capture
                }

                canvas.getChildren().remove(line);
                line = getNewLine();
                canvas.getChildren().add(line);
            });
        }

        /**
         * This method simply creates and returns a new {@link Line} instance using hard-coded parameters.
         *
         * @return {@link Line} - A new Line instance with 'null' coordinates (effectively invisible), no visibility,
         * and a stroke width of 5.
         */
        private static Line getNewLine() {
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
                    throw new BoardMoveMissingPieceException("Attempting to perform a capture over non-existent " +
                            "piece! x:" + midX + ", y: " + midY);
                if (board.getTileAtIndex(midX, midY).getPiece().getPlayer() == origin.getPlayer())
                    throw new BoardMoveSelfCaptureException("Attempting to capture a member of your team! x:" + midX + "," +
                            " y: "
                            + midY);
            }

            boolean kingOnlyMove = origin.getPlayer().getHomeSide() == Player.Defaults.HUMAN.getPlayer().getHomeSide() ?
                    destY < origin.getY() :
                    origin.getPlayer().getHomeSide() == Player.Defaults.COMPUTER.getPlayer().getHomeSide() && destY > origin.getY();

            if (origin.getType() != Piece.Type.KING && kingOnlyMove)
                throw new BoardMoveNotKingException("This piece is not a king!");

            Piece capturing = capturingMove ? board.getTileAtIndex(midX, midY).getPiece() : null;

            if (!trial)
                executeMove(origin, destX, destY, capturing);
        }

        private void executeMove(Piece origin, int destX, int destY, Piece captured) {
            if (captured != null) {
                origin.getPlayer().getCapturedPieces().add(captured);
                captured.deleteFromBoard(); //TODO Add to capturer's captured pieces list
            }

            board.getTileAtIndex(destX, destY).init();
            board.getTileAtIndex(origin.getX(), origin.getY()).delete();

            //TODO force capturing of neighbours
            //TODO force auto-crowning of piece if it's on the back board

            doAutoCapture(origin); //TODO might not be working, check

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
            if (validMoves.stream().filter(t -> t.equals(Boolean.TRUE)).count() > 1) {
                //TODO it is now on the player to decide which move to take. Maybe fire event for this, including
                // which moves are valid. Also write a method to generate a list of Direction enums detailing which
                // moves are valid for a given piece

                return;
            }

            //There is now only one move possible. Find the move and execute it.
            if (Boolean.TRUE.equals(validMoves.get(0)))
                makeMove(piece, piece.getX() - 2, piece.getY() + 2);
            if (Boolean.TRUE.equals(validMoves.get(1)))
                makeMove(piece, piece.getX() - 2, piece.getY() - 2);
            if (Boolean.TRUE.equals(validMoves.get(2)))
                makeMove(piece, piece.getX() + 2, piece.getY() + 2);
            if (Boolean.TRUE.equals(validMoves.get(3)))
                makeMove(piece, piece.getX() + 2, piece.getY() - 2);
        }
    }
}
