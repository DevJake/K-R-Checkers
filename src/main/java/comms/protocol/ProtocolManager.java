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

/**
 * This class and its importance to the {@link Protocol} class are described in the Protocol class documentation. In
 * short, the process of sending network messages is dependent on the ability to ensure both parties are aware of the
 * data being sent. This is enacted through the use of {@link Protocol Protocols}: a strict definition of syntax
 * required for encoding and decoding different types of information. For this, we use Protocols to encode
 * {@link Event} instances to {@link comms.MessageContainer.Message Messages}, and vice-versa for decoding.
 * <p>
 * One major issue is knowing which {@link Event Events} are associated with which {@link Protocol Protocols}. Worse
 * yet, we need to know which {@link comms.MessageContainer.Message Messages} correspond to which {@link Event Events
 * }. Given that each {@link Protocol} knows this information already, we opt to have each Protocol submit itself -
 * and this information - to the ProtocolManager. When an {@link Event} or {@link comms.MessageContainer.Message} is
 * sent or received, respectively, this class is able to automatically determine the most eligible {@link Protocol}
 * to handle this request.
 */
public class ProtocolManager {
    /**
     * We store our {@link Protocol} instances in a {@link HashSet} so that we don't get duplicate versions. This is
     * because we should only ever have one matching {@link Protocol} for each {@link Event}.
     */
    private static final HashSet<Protocol> protocols = new HashSet<>();

    /**
     * @param protocol {@link Protocol} - The new Protocol instance to be registered.
     * @param <E>      {@link E} - An instance of a class that extends {@link Event}. This states the explicit
     *                 {@link Event} we expect this {@link Protocol} to be designed to handle.
     */
    public static <E extends Event> void registerProtocol(Protocol<E> protocol) {
        protocols.add(protocol);
        System.out.println("Registering protocol: " + protocol.getClass() + protocol.getEventClass());
    }

    /**
     * This method takes in an {@link Event} class and determines the correct {@link Protocol} to interact with it.
     *
     * @param event {@link Event} - The Event class to be used in determining the correct {@link Protocol}.
     * @param <E>   {@link E} - A class that extends the {@link Event} superclass.
     *
     * @return {@link Protocol} - The registered Protocol that defined itself as responding to the given
     * {@link Event} type. If none is found, returns null.
     */
    public static <E extends Event> Protocol getProtocolFor(Class<E> event) {
        List<Protocol> select =
                protocols.stream().filter(p -> p.getEventClass() == event).collect(Collectors.toList());//Might need to
// use instanceof
        if (select.size() <= 0)
            return null;
        return select.get(0);
    }

    /**
     * This method receives a {@link comms.MessageContainer.Message} instance, determines the most appropriate
     * {@link Protocol} for decoding, then fires the {@link Event} it decodes to.
     *
     * @param message {@link comms.MessageContainer.Message} - The Message to be decoded.
     * @param <E>     {@link E} - A class that extends the {@link Event} superclass.
     *
     * @see Event
     * @see Event.Manager#fire(Event)
     * @see Protocol
     */
    public static <E extends Event> void decodeFor(MessageContainer.Message message) {
        for (Protocol protocol : protocols) {
            if (protocol.isMatchFor(message)) {
                Event decode = protocol.decode(message);
                Event.Manager.fire(decode);
            }
        }
    }

    /**
     * This method takes an {@link Event} instance and calls the {@link Protocol#encode(Event)} method on the result
     * of {@link #getProtocolFor(Class)}. The resultant {@link comms.MessageContainer.Message} can then be used in
     * the {@link comms.Bridge} class, sent over the network.
     *
     * @param event {@link Event} - The Event class to be encoded.
     * @param <E>   {@link E} - A class that extends the {@link Event} superclass.
     *
     * @return {@link comms.MessageContainer.Message} - The encoded Message instance.
     *
     * @throws EventProtocolMismatchException Thrown if the provided {@link Event} is not an instance of the expected
     *                                        type, per the result of {@link #getProtocolFor(Class)}.
     * @see Event
     * @see Protocol
     * @see #getProtocolFor(Class)
     * @see comms.MessageContainer.Message
     * @see comms.Bridge
     * @see comms.Bridge#send(MessageContainer.Message)
     */
    public static <E extends Event> MessageContainer.Message encodeFor(E event) throws EventProtocolMismatchException {
        return getProtocolFor(event.getClass()).encode(event);
    }
}
