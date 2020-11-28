#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#

#
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#
import uuid
from enum import Enum
from uuid import UUID


class Player(Enum):
    COMPUTER = 0
    HUMAN = 1

    def __init__(self, is_player: bool):
        self.is_player = is_player


class Piece:
    # More information could be present, but isn't, because it simply isn't needed for the minimax algorithm to work
    def __init__(self, owner: Player, in_play: bool, x: int, y: int):
        self.owner = owner
        self.in_play = in_play
        self.x = x
        self.y = y


class Board:
    def __init__(self, representation: str) -> None:
        self.__rep = representation

    def get_dimensions(self):
        pass

    def get_piece_at(self, x: int, y: int) -> Piece:
        pass

    def make_move(self, from_x, from_y, to_x, to_y):
        piece_before = self.get_piece_at(from_x, from_y)
        if not self.is_move_valid(piece_before, to_x, to_y):
            return

        piece_after = piece_before
        piece_after.x = to_x
        piece_after.y = to_y

        self.__remove_piece_at(piece_before)
        self.__place_piece_at(piece_after)

    def __remove_piece_at(self, piece: Piece) -> bool:
        pass

    def __place_piece_at(self, piece: Piece) -> bool:
        pass

    def is_move_valid(self, piece: Piece, to_x: int, to_y: int) -> bool:
        pass


class Message:
    def __init__(self, message: str, id: UUID = uuid.uuid4(), response_code: UUID = None, header:str = "", footer:str = ""):
        self.header = header
        self.footer = footer
        self.id = id
        self.message = message
        self.response_code = response_code


    def set_header(self, header:str):
        self.header = header
        return self

    def set_footer(self, footer:str):
        self.footer =footer
        return self

    def set_response_code(self, response_code:UUID):
        self.response_code = response_code
        return self
