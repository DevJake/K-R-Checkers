/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import comms.protocol.ProtocolManager;
import event.Event;

import java.util.UUID;

/*
Encodes information for outbound transfers.
 */
public class MessageEncoder {
    private final MessageContainer.State state = MessageContainer.State.OUTBOUND;
    private final MessageContainer.Message message;
    private UUID responseCode;

    public <E extends Event> MessageEncoder(E event) {
        this.message = ProtocolManager.getProtocolFor(event).encode(event);
    }

    public MessageContainer.Message encode() {
        return message;
    }

}
