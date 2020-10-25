/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package err;

import comms.protocol.Protocol;
import event.Event;

public class EventProtocolMismatchException extends Exception {
    public EventProtocolMismatchException(String message) {
        super(message);
    }

    public <E extends Event> EventProtocolMismatchException(Protocol protocol, Class<E> receivesClass) {
        super("The provided Event instance, " + receivesClass.getName() + " does not match to the expected Event type" +
                " in protocol" +
                " " + protocol.getEventClass().getName() + "(" + protocol.getClass().getName() + "). ");
    }
}
