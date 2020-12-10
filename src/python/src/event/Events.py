#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

from Entity import Board, Message, Piece


class Event:
    pass


class BridgeMessageReceiveEvent(Event):
    def __init__(self, message: Message):
        self.message = message


class BridgeMessageSendEvent(Event):
    def __init__(self, message: Message):
        self.message = message


class BoardUpdateStateEvent(Event):
    def __init__(self, old: Board, new: Board):
        self.old_state = old
        self.new_state = new


class OpponentMovePieceEvent(Event):
    def __init__(self, before_piece: Piece, after_piece: Piece):
        self.before_piece = before_piece
        self.after_piece = after_piece


class OpponentCapturePieceEvent(Event):
    def __init__(self, captured_piece: Piece, capturer_origin: Piece, capturer_dest: Piece, was_king: bool):
        self.captured_piece = captured_piece
        self.capturer_origin = capturer_origin
        self.capturer_dest = capturer_dest
        self.was_king = was_king


class OpponentCaptureMultiplePiecesEvent(Event):
    def __init__(self, captured_pieces: list[Piece], capturer_steps: list[Piece]):
        self.captured_pieces = captured_pieces
        self.capturer_steps = capturer_steps


class OpponentConvertToKingEvent(Event):
    def __init__(self, converted: Piece, converter_origin: Piece):
        self.converter_origin = converter_origin
        self.converted = converted


class BoardValidMovesEvent(Event):
    def __init__(self, moveable_pieces: list):  # [(x,y, [Directions])] list(tuple(x, y, list()))
        self.moveable_pieces = moveable_pieces


class EventListener:
    def on_bridge_send_message(self, event: BridgeMessageSendEvent):
        pass

    def on_bridge_receive_message(self, event: BridgeMessageReceiveEvent):
        pass

    def on_board_update_status(self, event: BoardUpdateStateEvent):
        pass

    def on_opponent_move_piece(self, event: OpponentMovePieceEvent):
        pass

    def on_opponent_capture_piece(self, event: OpponentCapturePieceEvent):
        pass

    def on_opponent_capture_multiple_pieces(self, event: OpponentCaptureMultiplePiecesEvent):
        pass

    def on_opponent_convert_to_king(self, event: OpponentConvertToKingEvent):
        pass


class EventManager:
    __listeners: set[EventListener] = set()

    @staticmethod
    def register_listener(listener: EventListener):
        EventManager.__listeners.add(listener)

    @staticmethod
    def fire(event: Event):
        print("Firing new event...")
        print(event)
        for listener in EventManager.__listeners:
            if isinstance(event, BridgeMessageSendEvent):
                listener.on_bridge_send_message(event)
            elif isinstance(event, BridgeMessageReceiveEvent):
                listener.on_bridge_receive_message(event)
            elif isinstance(event, BoardUpdateStateEvent):
                listener.on_board_update_status(event)
            elif isinstance(event, OpponentMovePieceEvent):
                listener.on_opponent_move_piece(event)
            elif isinstance(event, OpponentCapturePieceEvent):
                listener.on_opponent_capture_piece(event)
            elif isinstance(event, OpponentCaptureMultiplePiecesEvent):
                listener.on_opponent_capture_multiple_pieces(event)
            elif isinstance(event, OpponentConvertToKingEvent):
                listener.on_opponent_convert_to_king(event)
