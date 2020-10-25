/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import ent.Entity;

import java.util.UUID;

/*
Encodes information for outbound transfers.
 */
public class MessageEncoder {
    private Message.State state = Message.State.OUTBOUND;
    private String message;
    private UUID responseCode;

    public MessageEncoder(String message) {
        this.message = message;
    }

    public MessageEncoder(Entity entity) {
        setMessage(entity);
    }

    public Message.State getState() {
        return state;
    }

    public MessageEncoder setState(Message.State state) {
        this.state = state;
        return this;
    }

    public MessageEncoder setResponseCode(UUID responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public MessageEncoder setMessage(String input) {
        //TODO
        return this;
    }

    public MessageEncoder setMessage(Entity input) {
        //TODO
        return this;
    }

    public Message encode() {
        return new Message(message, responseCode, state);
    }

}
