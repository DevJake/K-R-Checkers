/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

/**
 * The directions a {@link Piece} can move in. These movements are relative to the Piece; 'forwards' is defined as
 * the direction towards the opponent's baseline/King's Row, backwards being the native {@link Player Player's} own
 * baseline/King's Row.
 *
 * @see ent.Player.HomeSide
 */
public enum Direction {
    FORWARD_LEFT(-1, 1),
    FORWARD_RIGHT(1, 1),
    BACKWARD_LEFT(-1, -1),
    BACKWARD_RIGHT(1, -1),
    FORWARD_LEFT_CAPTURE(-2, 2),
    FORWARD_RIGHT_CAPTURE(2, 2),
    BACKWARD_LEFT_CAPTURE(-2, -2),
    BACKWARD_RIGHT_CAPTURE(2, -2);

    private final int xChange;
    private final int yChange;

    Direction(int xChange, int yChange) {
        this.xChange = xChange;
        this.yChange = yChange;
    }

    public int getxChange() {
        return xChange;
    }

    public int getyChange() {
        return yChange;
    }
}
