/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package event;

import ent.Player;

/**
 * Called when the A.I. {@link ent.Player} plays a given move.
 */
public class GameCompletedEvent extends Event {
    private final Player winner;
    private final Player loser;

    public GameCompletedEvent(Player winner, Player loser) {
        this.winner = winner;
        this.loser = loser;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
        return loser;
    }
}
