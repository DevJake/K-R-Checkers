/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import java.util.UUID;

public class MessageContainer {
    private final State state;

    public MessageContainer(State state) {
        this.state = state;
    }

    public enum State {
        INBOUND,
        OUTBOUND
    }

    public static class Message {
        private final UUID id;
        private final String message;
        private final UUID responseCode;
        /*
        The UUID of the Message that this Message is responding to. Useful for tracking, as well as implementing
        Ping Pong-esque behaviour.*/

        public Message(String message, UUID responseCode) {
            this.id = UUID.randomUUID();
            this.message = message;
            this.responseCode = responseCode;
        }


        public Message(String message) {
            this.id = UUID.randomUUID();
            this.message = message;
            this.responseCode = null;
        }

        public UUID getId() {
            return id;
        }

        public UUID getResponseCode() {
            return responseCode;
        }

        public String getMessage() {
            return message;
        }


    }
}