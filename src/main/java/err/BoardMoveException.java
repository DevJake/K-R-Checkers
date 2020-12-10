/*
 * Copyright (c) Candidate 181379, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package err;

import ent.Piece;

/**
 * An exception for generic illegal moves within the {@link ent.Board}.
 */
public class BoardMoveException extends RuntimeException {
    private Piece origin;
    private int destX;
    private int destY;

    public BoardMoveException(String message) {
        super(message);
    }

    public BoardMoveException(Piece origin, int destX, int destY) {
        this.origin = origin;
        this.destX = destX;
        this.destY = destY;

    }

    public Piece getOrigin() {
        return origin;
    }

    public int getDestX() {
        return destX;
    }

    public int getDestY() {
        return destY;
    }
}
