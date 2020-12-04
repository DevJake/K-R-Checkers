/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms.protocol;

import comms.MessageContainer;
import event.Event;
import event.PlayerMakeMoveEvent;

/**
 * Represents a {@link ent.Board} movement executed by the Human {@link ent.Player}.
 */
public class PlayerMakeMoveProtocol extends Protocol {
    public PlayerMakeMoveProtocol(String header, String footer) {
        super(header, footer, PlayerMakeMoveEvent.class);
    }

    @Override
    public MessageContainer.Message encode(Event event) {
        return null;
    }

    @Override
    public Event decode(MessageContainer.Message message) {
        return null;
    }
}
