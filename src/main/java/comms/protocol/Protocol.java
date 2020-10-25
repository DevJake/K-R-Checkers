/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms.protocol;

import comms.Message;
import err.EventProtocolMismatchException;
import event.Event;

public abstract class Protocol<E extends Event> {
    private final Class<E> eventClass;
    private String header = "";
    private String footer = "";

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

    public abstract Message encode(E event) throws EventProtocolMismatchException;

    public abstract Message decode(Message message);

    public boolean isMatchFor(Message message) {
        return message.getMessage().startsWith(header) && message.getMessage().endsWith(footer);
    }
}
