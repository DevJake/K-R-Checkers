/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package event;

import err.UnregisteredEventException;

import java.util.ArrayList;
import java.util.UUID;

public class Event {
    private final UUID ID = UUID.randomUUID();

    public UUID getID() {
        return ID;
    }

    public static class Manager {
        private static final ArrayList<EventListener> listeners = new ArrayList<>();

        public static void fire(Event e) {
            listeners.forEach(l -> {
                //I would've used a switch statement, but Java doesn't like performing switches with classes
                if (e instanceof BridgeMessageReceiveEvent) {
                    l.onBridgeMessageReceived((BridgeMessageReceiveEvent) e);
                } else if (e instanceof BridgeMessageSendEvent) {
                    l.onBridgeMessageSend((BridgeMessageSendEvent) e);
                } else {
                    throw new UnregisteredEventException("An Event type has been fired, but is not handled in the " +
                            "Manager! Event: " + e.getClass());
                }
            });

        public static void registerListener(EventListener listener, Event... events) {
            for (Event event : events) {
                if (!listeners.containsKey(event))
                    listeners.get(event).add(listener);
                else
                    listeners.put(event, new ArrayList<>(1) {{
                        add(listener);
                    }});
            }
        }
    }
}
