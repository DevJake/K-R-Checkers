/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.beans.Transient;

import static fx.controllers.Main.mainBoard;
import static fx.controllers.Main.toRGBString;

/**
 * Handles all interactions with a Tile on the Board. This includes wrapping the Piece. There is an important
 * distinction between this class and the Piece class. The main focus was to isolate their functionality, since they
 * ultimately cannot be merged; a Tile can contain a piece, but a Tile doesn't have to contain a Piece to be
 * functional. Furthermore, Tiles are selectively 'playable' - they either can or cannot be occupied by a Piece. Each
 * Tile also has an assigned Color.
 * <p>
 * Beyond this functionality, Tiles play no role in orchestrating a game. Their purpose is to be a manageable vessel
 * for containing Pieces.
 *
 * @see Board
 * @see Piece
 */
public class Tile extends Entity {
    /**
     * Each Tile contains a StackPane. By inserting Node subclasses in to the StackPane, the Tile can be rendered as
     * 'containing' multiple objects. In order to contain a Piece, we simply insert a new Circle in to the pane,
     * assigning the Circle's colour to that of the respective Piece's owner's chosen colour.
     * <p>
     * Importantly, we don't automatically push the new Circle to the top of the StackPane. This is because we may
     * want to overlay a Label, so as to denote the x/y coordinates of the Tile, for example.
     * <p>
     * This #init and #delete methods are the primary methods responsible for interacting with the StackPane. Direct
     * interaction is advised against as it becomes easy to lose track of StackPane contents.
     *
     * @see Piece
     * @see javafx.scene.shape.Circle
     * @see Label
     * @see Player
     * @see #init()
     * @see #deleteOccupyingPiece()
     */
    private final transient StackPane node;
    private final transient Circle kingStatus = new Circle(25, Color.rgb(0, 0, 0, 0));
    /**
     * The Color of this Tile.
     *
     * @see fx.controllers.Main#toRGBString(Color)
     */
    private Color colour;
    /**
     * The {@link Piece} contained within this Tile. This can be null. Management of this Piece is done within the
     * Piece itself, not the Tile, since Pieces can be moved from one Tile instance to another.
     */
    private Piece piece;
    /**
     * If this Tile can be occupied by a Piece. This isn't checked internally, as it should instead be checked by
     * external code attempting to modify Piece contents of this Tile.
     */
    private boolean isPlayable;
    /**
     * The {@link Label} this Tile may have. As shown, it can be null. This Label is overlaid on top of the StackPane
     * . One example of usage includes displaying the x/y coordinates of the Tile, helping players in moving pieces,
     * as well as debugging.
     */
    private Label label = null;

    /**
     * A standard constructor for a new Tile. Typically called when the {@link Board} is generated.
     *
     * @param colour The {@link Color} of this Tile.
     * @param node   The {@link StackPane} that occupies this Tile. This isn't given by default, as the StackPane is
     *               initialised externally in the {@link fx.controllers.Main} class.
     * @param piece  The {@link Piece} that this Tile contains. This can be null.
     */
    public Tile(Color colour, StackPane node, Piece piece) {
        this.colour = colour;
        this.node = node;
        this.piece = piece;

        this.kingStatus.setStrokeWidth(3d);
        this.kingStatus.setStroke(Color.BLACK);

//        setupDrag();
//        piece.getChecker().addEventHandler(new EventType<>(), new MouseDrag());
    }

    /**
     * @return If this Tile can be occupied by a Piece. Importantly, this value being false doesn't mean the Tile
     * will reject any attempts to update its state. It is merely an indicator of if the game should allow pieces here.
     */
    public boolean isPlayable() {
        return isPlayable;
    }

    /**
     * Assigns the playability state of the game.
     *
     * @param playable The playability state. True for playable, false for unplayable.
     */
    public void setPlayable(boolean playable) {
        isPlayable = playable;
        if (!playable)
            node.setOnDragDetected(event -> { //TODO delete
            });
    }

    @Override
    public String toString() {
        return "Tile{" +
                "colour=" + colour +
                ", node=" + node +
                ", piece=" + piece +
                ", isPlayable=" + isPlayable +
                '}';
    }

    /**
     * Initialises this Tile's {@link #node} with a new {@link javafx.scene.shape.Circle}, representing the
     * {@link Piece}. This is done by destroying all children, then constructing and inserting the Circle. It would
     * be possible to selectively remove the Circle from the children, but this could potentially prove overly
     * complex for our use case.
     * <p>
     * A new Circle is only included if {@link #isPlayable()} returns true.
     */
    public void init() {
        this.node.getChildren().clear();

        if (isPlayable) {
            this.node.getChildren().add(piece.init().getChecker());
            piece.getChecker().radiusProperty().bind(node.widthProperty().divide(2).multiply(0.8));
            piece.getChecker().setStroke(Color.BLACK);
            piece.getChecker().setStrokeWidth(0.5);

            if (piece.getType() == Piece.Type.KING) {
                this.node.getChildren().add(kingStatus);
            }
        }

        if (mainBoard.isShowLabels())
            showLabel();
    }

    /**
     * Shows a {@link Label} at the foremost of the {@link #node}, with the x and y coordinates of the contained
     * {@link Piece}. This is primarily used for debugging purposes, but may also prove useful for gameplay, given
     * this game uses x/y coordinates over row/column coordinates (i.e., y/x coordinates).
     *
     * @see Label
     * @see Piece
     */
    public void showLabel() {
        this.node.getChildren().removeAll(this.label);
        this.label = new javafx.scene.control.Label("x:" + getPiece().getX() + " y:" + getPiece().getY());
        this.node.getChildren().add(this.label);
    }

    /**
     * Removes the {@link Label} applied to this Tile. Furthermore, the Label is completely destroyed (set to null)
     * and must be reconstructed again. This is typically done through {@link #showLabel()} which handles
     * construction of x/y coordinate Labels.
     *
     * @see Label
     * @see #showLabel()
     */
    public void removeLabel() {
        if (label != null) {
            this.node.getChildren().remove(label);
            this.label = null;
        }
    }

    /**
     * @return The {@link Color} of this Tile.
     */
    public Color getColour() {
        return colour;
    }

    /**
     * @param colour {@link Color} - The new Color for this Tile. When passed in, the Tile doesn't remain static, the
     *               CSS style controlling the Tile's colour is instead immediately updated.
     */
    public void setColour(Color colour) {
        this.colour = colour;

        node.setStyle("-fx-background-color: " + toRGBString(colour));
    }

    /**
     * @return The {@link StackPane} for this Tile.
     */
    public StackPane getNode() {
        return node;
    }

    /**
     * @return The {@link Piece} associated with this Tile.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * @param piece The new {@link Piece} for this Tile.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * This method intends to completely delete all information about this Tile and the Piece it contains. This is
     * done when a Piece is either moved (calling delete on the origin Tile) or a Piece is captured.
     * <p>
     * The {@link #node node's} checker is destroyed, then {@link Piece#deleteChecker()} and {@link Piece#deletePiece()}
     * are executed.
     */
    public void deleteOccupyingPiece(boolean keepLabel) {
        if (keepLabel) {
            this.node.getChildren().remove(piece.getChecker());
            this.node.getChildren().remove(kingStatus);
        } else this.node.getChildren().clear();
        piece.deletePiece();
    }

    /**
     * @param direction The {@link Direction} in which to attempt a move.
     *
     * @return Boolean - If the move was successful.
     */
    public boolean makeMove(Direction direction) {
//TODO Call legal-check methods on the Board
        return true;
    }

    /**
     * @param visibility The new visibility state of the {@link #node}.
     */
    public void setVisible(boolean visibility) {
        node.setVisible(visibility);
    }

}

