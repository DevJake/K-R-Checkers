/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package event;

import ent.Board;

/**
 * Called when the {@link Board} has any change in state.
 */
public class BoardUpdateEvent extends Event {
    private final Board oldBoard;
    private final Board newBoard;

    public BoardUpdateEvent(Board oldBoard, Board newBoard) {
        this.oldBoard = oldBoard;
        this.newBoard = newBoard;
    }

    public Board getOldBoard() {
        return oldBoard;
    }

    public Board getNewBoard() {
        return newBoard;
    }
}
