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

/**
 * This class acts as a superclass, with the intention of individual implementations being created and registered
 * with the {@link ProtocolManager}.
 * <p>
 * The main principle behind a Protocol is to provide a consistent means of encoding and decoding a given
 * {@link comms.MessageContainer.Message} to and from bytes, respectively. These bytes are sent across and received
 * from a network connection. By using a Protocol instance, we can ensure that our
 * {@link comms.MessageContainer.Message} should be fully interpretable on the receiving end of a network connection,
 * assuming the receiving party follows the same principles of Protocol syntax. Each Protocol instance is required to
 * define a unique syntax.
 * <p>
 * A typical syntax involves the surrounding a structured message with a pre-defined prefix and suffix. The suffix is
 * somewhat optional and can be used as an extended variable by protocols, working well as a delimiter. The prefix,
 * however, is mandatory, as it informs the {@link ProtocolManager} which Protocol should be used for handling of
 * inbound network traffic. The following String demonstrates an acceptable encoding for a basic network
 * {@link comms.MessageContainer.Message}:
 * <p>
 * "TCP://MSG://Hello World!(***)://"
 * <p>
 * Where:
 * - "TCP://" is the protocol for transmission. In this, we use TCP to ensure any traffic sent is definitely received.
 * - "MSG://" is the prefix defined by the Protocol instance, and "://" is a prefix delimiter.
 * - "Hello World!" is the data (the {@link comms.MessageContainer.Message}) being transferred.
 * - "(***)" is the optional Message suffix applied by the Protocol.
 * - "://" is the suffix delimeter, applied by the {@link ProtocolManager} to every
 * {@link comms.MessageContainer.Message}.
 * <p>
 * In order to ensure the most appropriate Protocol is chosen for encoding a {@link comms.MessageContainer.Message},
 * we don't allow for the supplying of raw String data. Instead, we submit an {@link Event} instance to the
 * {@link ProtocolManager}, which then determines which Protocol has *subscribed* to that Event's class. If a match
 * is found, the appropriate Protocol is selected and the relevant encode or decode method is called.
 * <p>
 * This system works well to ensure data is correctly structured, as each {@link Event} is able to define its
 * expected variables via the constructor(s) it makes available. This ensures that, when developing a Protocol, the
 * information available *should* be very consistent and reliable - a much neater system than trying to dissect a
 * String in to each constituent part, and the data each part represents.
 *
 * @param <E> {@link Event} - The Event instance that this Protocol implementation accepts for both encoding and
 *            decoding.
 *
 * @see ProtocolManager
 * @see comms.MessageContainer.Message
 * @see Event
 * @see #encode(Event)
 * @see #decode(MessageContainer.Message)
 */
public abstract class Protocol<E extends Event> {
    private final Class<E> eventClass;
    /**
     * The 'prefix' component of our {@link comms.MessageContainer.Message}. This is mandatory.
     */
    private String header = "";
    /**
     * The 'suffix' component of our {@link comms.MessageContainer.Message}. This is not mandatory.
     */
    private String footer = "";

    /**
     * @param header     String - The header/prefix to be used by this Protocol implementation. This is mandatory and
     *                   if left blank, is replaced with the name of the implementing class.
     * @param footer     String - The footer/suffix to be used by this Protocol implementation. This is not mandatory
     *                   and if left blank, is replaced with the name of the implementing class.
     * @param eventClass {@link Class<E>} - The generic class of the {@link Event} subclass that this Protocol will
     *                   be expected to handle encoding and decoding for.
     */
    public Protocol(String header, String footer, Class<E> eventClass) {
        this.header = header.isEmpty() ? eventClass.getName() : header;
        this.footer = footer.isEmpty() ? eventClass.getName() : footer;
        this.eventClass = eventClass;

        ProtocolManager.registerProtocol(this);
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public Class<E> getEventClass() {
        return eventClass;
    }

    /**
     * This abstract method is responsible for encoding a given {@link Event} instance in to a
     * {@link comms.MessageContainer.Message}. Implementations of this method are responsible for checking the
     * instance type of the Event, ensuring it matches to the expected Event subclass.
     *
     * @param event {@link Event} - The Event to be encoded.
     *
     * @return {@link comms.MessageContainer.Message} - The encoded {@link Event}.
     *
     * @throws EventProtocolMismatchException Thrown if the provided {@link Event} is not an instance of the expected
     *                                        type.
     */
    public abstract MessageContainer.Message encode(E event) throws EventProtocolMismatchException;

    /**
     * This abstract method is responsible for decoding a given {@link comms.MessageContainer.Message} instance in to
     * an {@link Event}. Implementations of this method are responsible for checking the Message is correctly
     * formatted. Syntax - specifically the prefix/header and suffix/footer - are checked by the
     * {@link ProtocolManager}.
     *
     * @param message {@link comms.MessageContainer.Message} - The Message to be decoded.
     *
     * @return {@link E} - A class implementing {@link Event}.
     */
    public abstract E decode(MessageContainer.Message message);

    /**
     * This method returns true if the provided {@link comms.MessageContainer.Message}'s contents explicitly matches the
     * prefix/header and suffix/footer expected by this Protocol's implementation. Otherwise, return false.
     *
     * @param message {@link comms.MessageContainer.Message} - the Message to be analysed.
     *
     * @return Boolean - True if the provided {@link comms.MessageContainer.Message}'s contents explicitly matches
     * the prefix/header and suffix/footer expected by this Protocol's implementation. Otherwise, return false.
     */
    public boolean isMatchFor(MessageContainer.Message message) {
        return message.getMessage().startsWith(header) && message.getMessage().endsWith(footer);
    }
}
