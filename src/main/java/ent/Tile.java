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


public class Tile extends Entity {
    private final Color colour;
    private final StackPane node;
    private Piece piece;
    private boolean isPlayable;
    private Label label = null;

    public Tile(Color colour, StackPane node, Piece piece) {
        this.colour = colour;
        this.node = node;
        this.piece = piece;
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

    public void setPlayable(boolean playable) {
        isPlayable = playable;
    }

    public void init() {
        this.node.getChildren().removeAll();

        if (isPlayable) {
            this.node.getChildren().add(piece.init().getChecker());
            piece.getChecker().radiusProperty().bind(node.widthProperty().divide(2).multiply(0.8));
            piece.getChecker().setStroke(Color.BLACK);
            piece.getChecker().setStrokeWidth(0.5);
        }
    }

    public void showLabel() {
        this.node.getChildren().removeAll(this.label);
        this.label = new javafx.scene.control.Label("x:" + getPiece().getX() + " y:" + getPiece().getY());
        this.node.getChildren().add(this.label);
    }

    public void removeLabel() {
        this.node.getChildren().remove(label);
        this.label = null;
    }

    public Color getColour() {
        return colour;
    }

    public StackPane getNode() {
        return node;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void delete() {
        this.node.getChildren().remove(piece.getChecker());
        piece.deleteFromBoard();
    }

    /**
     * @param direction The {@link Direction} in which to attempt a move
     * @return Boolean - If the move was successful
     */
    public boolean makeMove(Direction direction) {
//TODO Call legal-check methods on the Board
        return true;
    }

    public void setVisible(boolean visibility) {
        node.setVisible(visibility);
    }

}

