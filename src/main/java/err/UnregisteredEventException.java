/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package err;

import event.Event;

/**
 * An exception for {@link Event Events} in which an Event is fired, but its existence is unknown to the
 * {@link Event.Manager}. This results in any {@link event.EventListener} instances being unable to receive the Event.
 */
public class UnregisteredEventException extends RuntimeException {
    public UnregisteredEventException(String message) {
    }
}
