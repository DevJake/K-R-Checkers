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
 * An exception for {@link ent.Board} moves where a required {@link Piece} was not present.
 */
public class BoardMoveMissingPieceException extends BoardMoveException {
    public BoardMoveMissingPieceException(String message) {
        super(message);
    }
}
