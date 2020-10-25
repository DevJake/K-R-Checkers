/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms.protocol;

import event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProtocolManager {
    private static final ArrayList<Protocol> protocols = new ArrayList<>();

    public static void registerProtocol(Protocol protocol) {
        protocols.add(protocol);
    }

    public <E extends Event> List<Protocol> getProtocolsFor(E event) {
        return protocols.stream().filter(p -> p.getEventClass() == event.getClass()).collect(Collectors.toList());
    }
}
