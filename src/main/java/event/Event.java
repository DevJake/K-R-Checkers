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

/**
 * This class is a simple superclass for all implementations of the Event system. In order for a class to be suitable
 * as an Event, it must subclass this class.
 * <p>
 * This class generates a unique, random {@link UUID} for each Event instance, so that two instances of the same
 * subclass can be easily tracked and distinguished.
 */
public abstract class Event {
    private final UUID ID = UUID.randomUUID();

    public UUID getID() {
        return ID;
    }

    /**
     * This class is responsible for the management of {@link Event}-related actions. This primarily includes the
     * distribution of Events across {@link EventListener} instances. These Listeners don't explicitly 'subscribe' to
     * an Event, but instead are informed of the Event and choose to act by overriding the relevant method. The
     * relevant method is called for every Listener that is registered, using {@link #registerListener(EventListener)}.
     *
     * @see EventListener
     */
    public static class Manager {
        private static final ArrayList<EventListener> listeners = new ArrayList<>();

        /**
         * This method receives an {@link Event} instance, determines the explicit type of event, then calls the
         * relevant method for each registered {@link EventListener}. The Event instance is cast to the correct type
         * before the method call is made.
         * <p>
         * This method aims to simplify the process of firing Events. Firing an Event should be achievable through
         * one line of succinct code, and the logistics of distributing it to all relevant {@link EventListener
         * EventListeners} should be obscured to the call to fire.
         *
         * @param e {@link Event} - The Event instance to be distributed to all registered {@link EventListener}
         *          objects.
         *
         * @throws UnregisteredEventException Thrown if the given {@link Event} does not have an implementation
         *                                    within this method.
         * @see EventListener
         * @see Event
         * @see #registerListener(EventListener)
         */
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

        }

        /**
         * This method updates the internal index of {@link EventListener} instances to include the new, provided
         * listener. Usage of this method is mandatory for an {@link EventListener} implementation to be able to
         * receive {@link Event} updates.
         *
         * @param listener {@link EventListener} - The EventListener instance to be added to the internal Listeners
         *                 registry.
         */
        public static void registerListener(EventListener listener) {
            listeners.add(listener);
        }
    }
}
