/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import event.BridgeMessageReceiveEvent;
import event.BridgeMessageSendEvent;
import event.EventListener;

public class BridgeListener extends EventListener {
    @Override
    public void onBridgeMessageReceived(BridgeMessageReceiveEvent event) {
        System.out.println("Received new message via The Bridge!! :: " + event.getMessage().getMessage());
    }

    @Override
    public void onBridgeMessageSend(BridgeMessageSendEvent event) {

    }
}
