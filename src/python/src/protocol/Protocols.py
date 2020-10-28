#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#
from abc import ABC, abstractmethod

from Entity import Message, Piece, Player
from event.Events import BridgeMessageReceiveEvent, Event, OpponentMovePieceEvent


class Protocol(ABC):
    @abstractmethod
    def __init__(self, header: str, footer: str) -> None:
        self.header = header.lower()
        self.footer = footer.lower()
        ProtocolManager.register_protocol(self)

    def is_match_for(self, message: Message) -> bool:
        return message.message.startswith(self.header + '://') and message.message.endswith(self.footer + '//:')

    @abstractmethod
    def decode(self, message: Message) -> Event:
        pass

    @abstractmethod
    def encode(self, event: Event) -> Message:
        pass


class EventProtocolMismatchException(Exception):
    def __init__(self, protocol: [Protocol], event: [Event],
                 message="There has been a mismatch between a given Protocol/Event and the expected type. "):
        self.message = message
        self.protocol = protocol
        self.event = event


class OpponentMovePieceProtocol(Protocol):
    def __init__(self, header: str, footer: str) -> None:
        header = "omove"
        super().__init__(header, footer)

    def decode(self, message: Message) -> Event:
        prev, _next = message.message.split('>')

        return OpponentMovePieceEvent(Piece(Player.HUMAN, True, prev[0], prev[1]),
                                      Piece(Player.HUMAN, True, _next[0], _next[1]))

    def encode(self, event: Event) -> Message:
        if event is not OpponentMovePieceEvent:
            raise EventProtocolMismatchException(self, event)

        event: OpponentMovePieceEvent = event

        return Message(f"{event.before_piece.x}.{event.before_piece.y}:{event.after_piece.x}.{event.after_piece.y}")


class BridgeMessageReceiveProtocol(Protocol):

    def __init__(self, footer: str) -> None:
        header = 'bridge'
        super().__init__(header, footer)

    def decode(self, message: Message) -> Event:
        pass

    def encode(self, event: Event) -> Message:
        if event is not BridgeMessageReceiveEvent:
            raise EventProtocolMismatchException(self, event)


class ProtocolManager:
    __protocols = set()

    @staticmethod
    def register_protocol(protocol):
        print(f"Registered new protocol! {protocol.__str__()}")
        ProtocolManager.__protocols.add(protocol)

    @staticmethod
    def decodeFor(message: Message) -> Event:
        for protocol in ProtocolManager.__protocols:
            if protocol.is_match_for(message):
                print("Match")
                return protocol.decode(message)

    @staticmethod
    def encodeFor(event: Event):
        pass  # TODO
