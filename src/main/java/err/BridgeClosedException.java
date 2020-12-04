/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package err;

/**
 * An exception for the {@link comms.Bridge} in which an attempt to interact with or send
 * {@link comms.MessageContainer.Message} instances via the {@link comms.Bridge} is not possible, due to no inbound
 * and outbound connections being made.
 */
public class BridgeClosedException extends RuntimeException {
    public BridgeClosedException(String message) {
    }
}
