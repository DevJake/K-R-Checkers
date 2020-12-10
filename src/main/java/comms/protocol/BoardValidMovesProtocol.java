/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms.protocol;

import comms.MessageContainer;
import ent.Board;
import ent.Player;
import event.BoardValidMovesEvent;
import event.Event;
import fx.controllers.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class BoardValidMovesProtocol extends Protocol {
    public BoardValidMovesProtocol(String header, String footer) {
        super(header, footer, BoardValidMovesEvent.class);
    }

    public BoardValidMovesProtocol() {
        super(BoardValidMovesEvent.class);
    }

    @Override
    public MessageContainer.Message encode(Event event) {
        return null;
    }

//    private ArrayList<ArrayList<Tile>> constructBoard() {
//        ArrayList<ArrayList<Tile>> outer = new ArrayList<>();
//
//        for (int i = 0; i < 8; i++) {
//            ArrayList<Tile> temp = new ArrayList<>();
//
//            for (int j = 0; j < 8; j++) {
//                Tile tile = new Tile(Color.ORANGE, null, new Piece(j, i, Color.BLACK,
//                        Player.Defaults.NONE.getPlayer(), Piece.Type.MAN));
//                temp.add(tile);
//            }
//
//            outer.add(temp);
//        }
//
//        return outer;
//    }

    @Override
    public Event decode(MessageContainer.Message message) {
        System.out.println("Actual message=" + message.getMessage());

        ArrayList tiles = (ArrayList) Main.mainBoard.getTiles().clone();

        Board b = new Board(Main.mainBoard.getTiles(),
                Main.mainBoard.getPlayableTilesColour(),
                Main.mainBoard.getUnplayableTilesColour(), Main.mainBoard.isShowLabels());

        message.setMessage(message.getMessage().replace("board: [[[", ""));

        message.setMessage(message.getMessage().replaceFirst(" ], ].*", ""));

        ArrayList<String> values = new ArrayList<>(Arrays.asList(message.getMessage().split(" , ")));

        Pattern p = Pattern.compile("\\[player_type:([a-zA-Z]{1,8}), pos_x:([0-9]{1,2}), pos_y:([0-9]{1,2})]");

        b.getPlayableTiles().forEach(t -> t.deleteOccupyingPiece(true));
        b.getUnplayableTiles().forEach(t -> t.deleteOccupyingPiece(true));

        for (String value : values) {
            String type = p.matcher(value).group(1);
            int x = Integer.valueOf(p.matcher(value).group(2));
            int y = Integer.valueOf(p.matcher(value).group(3));

            b.getTileAtIndex(x, y).getPiece().setPlayer(type.equals("HUMAN") ? Player.Defaults.HUMAN.getPlayer() :
                    Player.Defaults.COMPUTER.getPlayer());
        }

        return new BoardValidMovesEvent(b, null);
    }
}
