/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final String message;
    private final UUID responseCode;
    private final State state;
    /*
    The UUID of the Message that this Message is responding to. Useful for tracking, as well as implementing
    Ping Pong-esque behaviour.*/

    Message(String message, UUID responseCode, State state) {
        this.id = UUID.randomUUID();
        this.message = message;
        this.responseCode = responseCode;
        this.state = state;
    }

    public UUID getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public UUID getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }

    public enum State {
        INBOUND,
        OUTBOUND
    }
}
