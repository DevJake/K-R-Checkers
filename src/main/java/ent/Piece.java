/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Piece {
    private final int x;
    private final int y;
    private Type type;
    private Player capturedBy = null;
    private Color colour;
    private Player player;
    private Circle checker;

    public Piece(int x, int y, Color colour, Player player, Type type) {
        this.x = x;
        this.y = y;
        this.colour = colour;
        this.player = player;
        this.type = type;
        init();
    }

    @Override
    public String toString() {
        return "Piece{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                ", capturedBy=" + capturedBy +
                ", colour=" + colour +
                ", player=" + player +
                ", checker=" + checker +
                '}';
    }

    public void makeKing() {
        this.type = Type.KING;
    }

    //Makes a piece no longer visible to the board
    public void deleteFromBoard() {
        checker = null;
    }

    //Completely deletes a piece from the board, including all information about the piece, such as the owner, colour
    // and type.
    public void delete() {
        setPlayer(null);
        setColour(null);
        setType(null);
    }

    public Piece init() {
        checker = new Circle(30, colour);
        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Circle getChecker() {
        return checker;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Player getCapturedBy() {
        return capturedBy;
    }

    public void setCapturedBy(Player capturedBy) {
        this.capturedBy = capturedBy;
    }


    public enum Type { //https://en.wikipedia.org/wiki/English_draughts#Pieces
        MAN,
        KING
    }
}
