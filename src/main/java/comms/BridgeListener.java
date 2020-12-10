/*
 * Copyright (c) Candidate 181379, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import event.BridgeMessageReceiveEvent;
import event.BridgeMessageSendEvent;
import event.EventListener;

/**
 * This class is an implementation of the {@link EventListener} event distribution system. This implementation serves
 * to respond explicitly to events originating from actions taken by the {@link Bridge} class.
 *
 * @see EventListener
 * @see Bridge
 * @see BridgeMessageReceiveEvent
 * @see BridgeMessageSendEvent
 */
public class BridgeListener extends EventListener {
    /**
     * @param event {@link BridgeMessageReceiveEvent} - The BridgeMessageReceiveEvent instance.
     */
    @Override
    public void onBridgeMessageReceived(BridgeMessageReceiveEvent event) {
//        System.out.println("Received new message via The Bridge!! :: " + event.getMessage().getMessage());
    }

    /**
     * @param event {@link BridgeMessageSendEvent} - The BridgeMessageSendEvent instance.
     */
    @Override
    public void onBridgeMessageSend(BridgeMessageSendEvent event) {
    }
}
