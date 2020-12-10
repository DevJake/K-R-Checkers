#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#
from Entity import Board, Player
from event.Events import BoardUpdateStateEvent, EventListener


class GameManager():
    root: Board = None

    def set_root(self, board: Board):
        self.root = board

        # Define new protocols and events for asking the java code about board states
        # BoardValidMovesEvent - ask for valid moves (pieces the AI is allowed to move) and what directions we can
        # move them, for a given board state. We receive a list of tuples back xreceive the list back

        # The root value is set when we receive a boardupdatestatusevent. TODO make sure this only fires when the
        #  player successfully executes a move

        # 1. we set the root value
        # 2. we start a forloop, iterating up to the given depth
        # 3. we ask java what pieces we're allowed to move
        # 4. we retrieve the moves list and generate all of the permutations possible, ordering them as nodes below
        # our root, or respective prior board state
        # 5. we use alpha-beta pruning to eliminate branches
        # 6. we gather the leaf nodes of our remaining branches and query them for valid moves, thus repeating from
        # step #3.
        # 7. once we reach the desired depth, we take the branch with the highest heuristic score, take the moves it
        # makes, and submit them to the java code to be executed.

    def predict(self, board: Board) -> list:  # Returns a list of moves to be executed.
        pass


class BoardStatusListener(EventListener):
    def on_board_update_status(self, event: BoardUpdateStateEvent):
        print(event.old_state)
        print(event.new_state)

        event.new_state.delete_piece_at(6, 2)
        event.new_state.set_piece_at(7, 3, Player.HUMAN)

        GameManager.set_root(event.new_state)

        # We don't need to send the state back to the board at any point, we instead only need to:
        # 1. request a list of pieces this board state can move
        # 2. request the directions a piece at pos (x,y) is able to move

        # 4. send a list of instructions on what pieces to move and where to, and in what order
