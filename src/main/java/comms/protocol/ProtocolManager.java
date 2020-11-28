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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ProtocolManager {
    private static final HashSet<Protocol> protocols = new HashSet<>();

    public static <E extends Event> void registerProtocol(Protocol<E> protocol) {
        protocols.add(protocol);
        System.out.println("Registering protocol: " + protocol.getClass() + protocol.getEventClass());
    }

    public static <E extends Event> Protocol getProtocolFor(Class<E> event) {
        List<Protocol> select =
                protocols.stream().filter(p -> p.getEventClass() == event).collect(Collectors.toList());//Might need to
// use instanceof
        if (select.size() <= 0)
            return null;
        return select.get(0);
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
