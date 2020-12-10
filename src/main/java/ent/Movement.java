/*
 * Copyright (c) Candidate 181379, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import fx.controllers.Main;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

/**
 * This class is responsible for handling all interaction events relating to the {@link Board} instance. This
 * includes {@link javafx.scene.input.MouseEvent} instances which are used for determining drag-and-drop gameplay
 * mechanics.
 * <p>
 * The means for implementing drag-and-drop is by drawing a line from the origin {@link Piece} to the destination
 * {@link Tile}, then executing the move upon mouse click release.
 */
class Movement {
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

    private static boolean doRender = false;

    /**
     * Instantiates a new Movement.
     *
     * @param canvas the canvas
     */
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
            if (!Movement.doRender)
                return;

            line.setEndX(event.getSceneX());
            line.setEndY(event.getSceneY());

            Tile tile = board.getTileAtIndex(((int) tileX), ((int) tileY));
            if (tile.isPlayable() && tile.getPiece().getChecker() != null && tile.getPiece().getPlayer() == Main.gameManager.getLastPlayer())
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


            Piece clickedPiece = board.getTileAtIndex((int) tileX, (int) tileY).getPiece();
            Movement.doRender =
                    Main.gameManager.getMoveablePieces().contains(clickedPiece) && Main.gameManager.lastPlayer == clickedPiece.getPlayer() && clickedPiece.getPlayer() == Player.Defaults.HUMAN.getPlayer();

            line.setStartX(beginX);
            line.setStartY(beginY);
        });

        /*
        This method is fired when the user ends a mouse click. Here, we end the drag-and-drop operation. We
        calculate the origin and final x/y coordinates of the mouse drag operation, identifying the relevant
        Piece object for each. Then, we validate the movement, ensuring it is feasible. Finally, we execute the
        move by calling #getManager()#makeMove(origin, destination).

        After executing the move, we destroy the line that's being rendered on the Pane, replacing it with a new
        line.
         */
        canvas.onMouseClickedProperty().set(event -> {
            if (!Movement.doRender)
                return;

            double destTileX = Math.floor(event.getX() / (canvas.getWidth() / 8));
            double destTileY = Math.floor((canvas.getHeight() - event.getY()) / (canvas.getHeight() / 8));
            //System.out.println("actual- " + event.getX() + ":" + event.getY());
            //System.out.println("centered- " + destTileX + ":" + destTileY);

            Tile tileOrigin = board.getTileAtIndex(((int) tileX), ((int) tileY));
            Tile tileDest = board.getTileAtIndex(((int) destTileX), ((int) destTileY));


//            if (tileOrigin.getPiece().getPlayer() != Main.gameManager.lastPlayer)
//                return;

            //System.out.println("--------------");
            //System.out.println("origin playable: " + tileOrigin.isPlayable());
            //System.out.println("destination playable: " + tileDest.isPlayable());
            //System.out.println("origin checker state: " + tileOrigin.getPiece().getChecker());
            //System.out.println("destination player state: " + tileDest.getPiece().getPlayer().getName());

            /*
            Origin should be a playable tile.
            Origin should have a valid checker.

            Destination should be an empty tile.
            Destination should be a playable tile.
             */
//            if (
//                    tileOrigin.isPlayable() && tileDest.isPlayable() && tileOrigin.getPiece().getChecker() != null &&
//                            tileDest.getPiece().getPlayer() == Player.Defaults.NONE.getPlayer()) {
////                    System.out.println(tileDest.getPiece().getPlayer().getName());
//                board.getManager().makeMove(tileOrigin.getPiece(), tileDest.getPiece().getX(),
//                        tileDest.getPiece().getY());
//            }


            if (Movement.doRender) {
                //System.out.println("Doddodoodo render");


                canvas.getChildren().remove(line);
                line = getNewLine();
                canvas.getChildren().add(line);

                boolean capturing =
                        Math.abs(tileOrigin.getPiece().getX() - tileDest.getPiece().getX()) == 2 &&
                                Math.abs(tileOrigin.getPiece().getY() - tileDest.getPiece().getY()) == 2;

                //System.out.println("Capturing=" + capturing);

                if (!Main.gameManager.getCapturesFor().isEmpty() && !capturing) {
                    return; //They are required to make a capturing move, and yet they are attempting not to...
                }

                try {
                    board.getManager().makeMove(tileOrigin.getPiece(), tileDest.getPiece().getX(),
                            tileDest.getPiece().getY());


                    Main.gameManager.setLastLockedPiece(tileDest.getPiece());

                    //System.out.println(tileOrigin.getPiece().getX());
                    //System.out.println(tileDest.getPiece().getX());
                    //System.out.println(tileOrigin.getPiece().getY());
                    //System.out.println(tileDest.getPiece().getY());

                    Main.gameManager.setExhaustedSingleMove(!capturing);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This method simply creates and returns a new {@link Line} instance using hard-coded parameters.
     *
     * @return {@link Line} - A new Line instance with 'null' coordinates (effectively invisible), no
     * visibility,
     * and a stroke width of 5.
     */
    private static Line getNewLine() {
        Line line = new Line(0, 0, 0, 0);
        line.setVisible(false);
        line.setStrokeWidth(5);
        return line;
    }
}
