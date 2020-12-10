/*
 * Copyright (c) Candidate 181379, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms.protocol;

import comms.MessageContainer;
import ent.Board;
import err.EventProtocolMismatchException;
import event.BoardUpdateEvent;
import event.Event;

/**
 * Represents any form of state change to the {@link ent.Board}.
 */
public class BoardUpdateProtocol extends Protocol {
    public BoardUpdateProtocol(String header, String footer) {
        super(header, footer, BoardUpdateEvent.class);
    }

    public BoardUpdateProtocol() {
        super(BoardUpdateEvent.class);
    }

    @Override
    public MessageContainer.Message encode(Event event) throws EventProtocolMismatchException {
//        System.out.println("Got called");
        if (!(event instanceof BoardUpdateEvent))
            throw new EventProtocolMismatchException(this, event.getClass());


        String oldBoard = ((BoardUpdateEvent) event).getOldBoard() == null ? "NULL" :
                boardToString(((BoardUpdateEvent) event).getOldBoard());
        String newBoard = ((BoardUpdateEvent) event).getNewBoard() == null ? "NULL" :
                boardToString(((BoardUpdateEvent) event).getNewBoard());


        return new MessageContainer.Message("[" + oldBoard + "]-[" + newBoard + "]");
    }

    @Override
    public Event decode(MessageContainer.Message message) {
        return new BoardUpdateEvent(null, null);
    }

    private String boardToString(Board board) {
        StringBuilder sb = new StringBuilder();

        board.getPlayableTiles().forEach(t -> {
            if (t.getPiece() == null || t.getPiece().getPlayer() == null)
                return;

            sb.append("Tile:[");
            sb.append("{").append("player_name:").append(t.getPiece().getPlayer().getName()).append("},");
            sb.append("{").append("x_pos:").append(t.getPiece().getX()).append("},");
            sb.append("{").append("y_pos:").append(t.getPiece().getY()).append("}");
            sb.append("] ");
        });

        return sb.toString();
    }
}
