/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import javafx.scene.layout.StackPane;

import java.awt.*;

public class Tile extends Entity {
    private final Color colour;
    private final StackPane node;
    private final Piece piece;
    private boolean isPlayable;

    public Tile(Color colour, StackPane node, Piece piece) {
        this.colour = colour;
        this.node = node;
        this.piece = piece;
    }

    public void setPlayable(boolean playable) {
        isPlayable = playable;
    }

    public void init() {
        this.node.getChildren().remove(piece.init().getChecker());
        if (isPlayable)
            this.node.getChildren().add(piece.init().getChecker());
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

    public void delete(){
        this.node.getChildren().remove(piece.getChecker());
        piece.delete();
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

