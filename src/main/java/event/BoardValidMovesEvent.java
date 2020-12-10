/*
 * Copyright (c) Candidate 181379, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package event;

import ent.Board;
import ent.Direction;

import java.util.List;

/**
 * Called when the {@link Board} has any change in state.
 */
public class BoardValidMovesEvent extends Event {
    private final Board board;
    private final List<List<Direction>> validMoves;

    public BoardValidMovesEvent(Board newBoard, List<List<Direction>> validMoves) {
        this.board = newBoard;
        this.validMoves = validMoves;
    }
}
