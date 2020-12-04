/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package err;

import ent.Piece;

/**
 * An exception for {@link ent.Board} moves with an invalid destination {@link ent.Tile}.
 */
public class BoardMoveInvalidDestinationException extends BoardMoveException {
    private Piece origin;
    private int destX;
    private int destY;

    public BoardMoveInvalidDestinationException(String message) {
        super(message);
    }

    public BoardMoveInvalidDestinationException(Piece origin, int destX, int destY) {
        super(origin, destX, destY);
        this.origin = origin;
        this.destX = destX;
        this.destY = destY;

    }
}
