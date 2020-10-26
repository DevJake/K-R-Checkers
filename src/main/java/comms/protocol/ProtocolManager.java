/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms.protocol;

import comms.MessageContainer;
import err.EventProtocolMismatchException;
import event.Event;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ProtocolManager {
    private static final ArrayList<Protocol> protocols = new ArrayList<>();

    public static <E extends Event> void registerProtocol(Protocol<E> protocol) {
        protocols.add(protocol);
    }

    public static <E extends Event> Protocol getProtocolFor(Class<E> event) {
        Stream<Protocol> protocolStream = protocols.stream().filter(p -> p.getEventClass() == event); //Might need to
        // use instanceof
        if (protocolStream.count() <= 0)
            return null;
        return protocolStream.findFirst().get();
    }

    public static <E extends Event> void decodeFor(MessageContainer.Message message) {
        for (Protocol protocol : protocols) {
            if (protocol.isMatchFor(message)) {
                Event decode = protocol.decode(message);
                Event.Manager.fire(decode);
            }
        }
    }

    public static <E extends Event> MessageContainer.Message encodeFor(E event) throws EventProtocolMismatchException {
        return getProtocolFor(event.getClass()).encode(event);
    }
}
