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
 */
public enum Direction {
    FORWARD_LEFT,
    FORWARD_RIGHT,
    BACKWARD_LEFT,
    BACKWARD_RIGHT,
    FORWARD_LEFT_CAPTURE,
    FORWARD_RIGHT_CAPTURE,
    BACKWARD_LEFT_CAPTURE,
    BACKWARD_RIGHT_CAPTURE
}
