/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import java.awt.*;

public class Piece extends Entity {
    private final int x;
    private final int y;
    private Type type = Type.MAN;
    private final Color colour;
    private final Player player;
    private Player capturedBy = null;

    public Piece(int x, int y, Type type, Player player) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.colour = player.getColour();
        this.player = player;
    }

    /**
     * @param direction The {@link Direction} in which to attempt a move
     * @return Boolean - If the move was successful
     */
    public boolean makeMove(Direction direction) {
//TODO Call legal-check methods on the Board
        return true;
    }

    /**
     * @return The {@link Player} that captured this piece.
     */
    public Player getCapturedBy() {
        return capturedBy;
    }

    public void setCapturedBy(Player capturedBy) {
        this.capturedBy = capturedBy;
    }

    public Player getTeam() {
        return player;
    }

    public Type getType() {
        return type;
    }

    public Color getColour() {
        return colour;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "type=" + type +
                ", colour=" + colour +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public enum Type { //https://en.wikipedia.org/wiki/English_draughts#Pieces
        MAN,
        KING
    }
}

