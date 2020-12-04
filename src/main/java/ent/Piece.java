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

/**
 * Contains all information representing a Checkers piece. Pieces must be contained within a {@link Tile} object in
 * order to have a visible representation on the {@link Board}.
 *
 * @see Tile
 * @see Board
 */
public class Piece {
    /**
     * The x coordinate of this Piece on the {@link Board}.
     */
    private final int x;
    /**
     * The y coordinate of this Piece on the {@link Board}.
     */
    private final int y;
    /**
     * The {@link Type} of this Piece.
     */
    private Type type;
    /**
     * The {@link Player} who captured this Piece. This can be null, denoting that the Piece is not yet captured.
     */
    private Player capturedBy = null;
    /**
     * The {@link Color} assigned to this Piece. This allows for a distinction between HUMAN and A.I. Pieces across
     * the board.
     */
    private Color colour;
    /**
     * The {@link Player} who owns this Checkers piece. Typically, the owner of a Piece will not change. This is an
     * important factor as it's used in determining if a movement is or isn't valid, given the owner of the current
     * move in the game.
     */
    private Player player;
    /**
     * The JavaFX {@link Circle} instance that represents this Piece on the {@link Tile} it occupies.
     */
    private Circle checker;

    /**
     * A standard constructor for a new Piece. Once the new Piece is constructed, the {@link #init()} method is call.
     *
     * @param x      Int - The *x* coordinate of this Piece on the {@link Board}.
     * @param y      Int - The *y* coordinate of this Piece on the {@link Board}.
     * @param colour {@link Color} - The Color class describing the colour of this Piece.
     * @param player {@link Player} - The player who owns this Piece.
     * @param type   {@link Type} - The Type of this Piece - this controls the logic for which directions this piece
     *               is allowed to move in.
     *
     * @see #init()
     * @see Board
     */
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

    /**
     * Converts this Piece's internal {@link #type} variable to a {@link Type#KING}.
     */
    public void makeKing() {
        this.type = Type.KING;
    }

    /**
     * Makes a piece no longer visible to the {@link Board}. This is done by setting the internal {@link #checker} to
     * null.
     */
    public void deleteFromBoard() {
        checker = null;
    }

    /**
     * Completely deletes a piece from the board, including all information about the piece, such as the owner,
     * colour and type. Importantly, the owning {@link Player} is not set to null as with the other values, but is
     * instead set to {@link Player.Defaults#NONE}.
     *
     * @see Player
     * @see Color
     * @see Type
     */
    public void delete() {
        setPlayer(Player.Defaults.NONE.getPlayer());
        setColour(null);
        setType(null);
    }

    /**
     * Initialises the {@link Circle} node for this Piece.
     *
     * @return This Piece. Ideal for chaining actions with a builder-esque syntax.
     */
    public Piece init() {
        checker = new Circle(30, colour);
        return this;
    }

    /**
     * @return Int - The integer x coordinate of this Piece.
     */
    public int getX() {
        return x;
    }

    /**
     * @return Int - The integer y coordinate of this Piece.
     */
    public int getY() {
        return y;
    }

    /**
     * @return {@link Color} - The Color class denoting the colour of this Piece.
     */
    public Color getColour() {
        return colour;
    }

    /**
     * @param colour {@link Color} - The new colour for this Piece.
     */
    public void setColour(Color colour) {
        this.colour = colour;
    }

    /**
     * @return {@link Player} - The Player object who 'owns' this Piece.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @param player {@link Player} - The new Player instance to claim ownership of this Piece. This should not be
     *               the Player who captures this Piece, as that should instead be denoted using
     *               {@link #setCapturedBy(Player)}.
     *
     * @see #setCapturedBy(Player)
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return {@link Circle} - The Circle representing this Piece.
     */
    public Circle getChecker() {
        return checker;
    }

    /**
     * @return {@link Type} - The Type of this Piece.
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type {@link Type} - The new Type for this Piece.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return {@link Player} - The Player responsible for capturing this Piece.
     */
    public Player getCapturedBy() {
        return capturedBy;
    }

    /**
     * @param capturedBy {@link Piece} - The Player responsible for capturing this Piece.
     */
    public void setCapturedBy(Player capturedBy) {
        this.capturedBy = capturedBy;
    }

    /**
     * The Type of this Piece.
     * <p>
     * MAN:
     * MAN types are only capable of moving forwards, respective of their side of origin. Moves can be forwards-left
     * or forwards-right.
     * <p>
     * KING:
     * KINGs are created when a MAN-Type Piece reaches the *King's Row* - the back-most row of the opponent's side of
     * the board. KING types are allowed to move in any diagonal direction, given they abide to the same rules as MAN
     * types when making movements (destination tile is empty, not exceeding maximum movement distance, etc.).
     * <p>
     * See https://en.wikipedia.org/wiki/English_draughts#Pieces for more information.
     */
    public enum Type {
        MAN,
        KING
    }
}
