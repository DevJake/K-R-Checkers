/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms.protocol;

import comms.Message;
import event.AIPlayMoveEvent;
import event.Event;

public class AIMakeMoveProtocol extends Protocol {
    public AIMakeMoveProtocol(String header, String footer) {
        super(header, footer, AIPlayMoveEvent.class);
    }

    @Override
    public Message encode(Event event) {
        return null;
    }

    @Override
    public Message decode(Message message) {
        return null;
    }
}
