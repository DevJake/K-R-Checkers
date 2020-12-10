/*
 * Copyright (c) Candidate 181379, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package event;

import comms.MessageContainer;

/**
 * Called when the {@link comms.Bridge} receives any form of {@link comms.MessageContainer.Message}.
 */
public class BridgeMessageReceiveEvent extends Event {
    private final MessageContainer.Message message;

    public BridgeMessageReceiveEvent(MessageContainer.Message m) {
        this.message = m;
    }

    public MessageContainer.Message getMessage() {
        return message;
    }
}
