#
#  Copyright (c) Candidate 181379, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

import uuid
from enum import Enum
from uuid import UUID


class Player(Enum):
    COMPUTER = 'COMPUTER'
    HUMAN = 'HUMAN'

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
    board = []

    def __init__(self):
        for i in range(8):
            self.board.append([None] * 8)
        # Creates a None-filled 8x8 array, representing our board

    def as_string(self):
        out = '['
        for row in range(len(self.board[0])):  # rows 0 to 7
            out += '['
            for column in range(len(self.board[0])):  # columns 0 to 7
                out += f"[player_type:{self.get_piece_at(column, 7 - row)}, pos_x:{column}, pos_y:{7 - row}]" \
                       f" {', ' if column < 7 else ''}"

            out += '], '
        out += ']'
        return out

    def get_width(self):
        return len(self.board[0])

    def get_height(self):
        return len(self.board[0][0])

    def get_piece_at(self, x: int, y: int) -> str:  # The string value is the type of player here
        return self.board[7 - x][y]  # Indexes flipped to match the GUI board

    def set_piece_at(self, x: int, y: int, type: Player):
        self.board[7 - x][y] = type.value  # Indexes flipped to match the GUI board

    def delete_piece_at(self, x: int, y: int):
        self.board[7 - x][y] = None  # Indexes flipped to match the GUI board

    # def make_move(self, from_x, from_y, to_x, to_y):
    #     piece_before = self.get_piece_at(from_x, from_y)
    #     if not self.is_move_valid(piece_before, to_x, to_y):
    #         return
    #
    #     piece_after = piece_before
    #     piece_after.x = to_x
    #     piece_after.y = to_y
    #
    #     self.__remove_piece_at(piece_before)
    #     self.__place_piece_at(piece_after)

    # def __remove_piece_at(self, piece: Piece) -> bool:
    #     pass
    #
    # def __place_piece_at(self, piece: Piece) -> bool:
    #     pass

    def is_move_valid(self, piece: Piece, to_x: int, to_y: int) -> bool:
        pass  # TODO


class Message:
    def __init__(self, message: str, id: UUID = uuid.uuid4(), response_code: UUID = None, header: str = "",
                 footer: str = ""):
        self.header = header
        self.footer = footer
        self.id = id
        self.message = message
        self.response_code = response_code

    def set_header(self, header: str):
        self.header = header
        return self

    def set_footer(self, footer: str):
        self.footer = footer
        return self

    def set_response_code(self, response_code: UUID):
        self.response_code = response_code
        return self


class Direction(Enum):
    FORWARD_LEFT = 0
    FORWARD_RIGHT = 0
    BACKWARD_LEFT = 0
    BACKWARD_RIGHT = 0
    FORWARD_LEFT_CAPTURE = 0
    FORWARD_RIGHT_CAPTURE = 0
    BACKWARD_LEFT_CAPTURE = 0
    BACKWARD_RIGHT_CAPTURE = 0
    # These are respective to the bottom of the board, even if moving pieces on the top of the board.
