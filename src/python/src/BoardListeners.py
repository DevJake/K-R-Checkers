#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#

import event.Events as ev
import Entity as ent


class BoardStatusListener(ev.EventListener):
    def __init__(self):
        pass

    def on_board_update_status(self, event: ev.BoardUpdateStateEvent):
        print(event.old_state)
        print(event.new_state)

        event.new_state.delete_piece_at(6, 2)
        event.new_state.set_piece_at(7, 3, ent.Player.HUMAN)

        # Server.gm.root = event.new_state

        # We don't need to send the state back to the board at any point, we instead only need to:
        # 1. request a list of pieces this board state can move
        # 2. request the directions a piece at pos (x,y) is able to move

        # 4. send a list of instructions on what pieces to move and where to, and in what order
