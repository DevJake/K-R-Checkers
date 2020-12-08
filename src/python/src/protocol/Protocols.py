#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

from abc import ABC, abstractmethod
from event.Events import BridgeMessageReceiveEvent, BridgeMessageSendEvent, Event, OpponentMovePieceEvent


class Protocol(ABC):
    @abstractmethod
    def __init__(self, match_event: [Event], header: str, footer: str = '') -> None:
        self.header = header.lower()
        self.footer = footer.lower()
        self.match_event = match_event

    def is_match_for(self, message: Message) -> bool:
        return message.message.startswith(self.header + '://') and message.message.endswith(self.footer + '//:')

    @abstractmethod
    def decode(self, message: Message) -> Event:
        pass

    @abstractmethod
    def encode(self, event: Event) -> Message:
        pass

    @staticmethod
    def is_type(expected: [Event], received: Event):
        return expected is type(received)


class EventProtocolMismatchException(Exception):
    def __init__(self, protocol: [Protocol], event: [Event],
                 message="There has been a mismatch between a given Protocol/Event and the expected type."):
        self.message = message
        self.protocol = protocol
        self.event = event


class OpponentMovePieceProtocol(Protocol):
    def __init__(self, footer: str = "") -> None:
        header = "omove"
        super().__init__(OpponentMovePieceEvent, header, footer)

    def decode(self, message: Message) -> Event:
        prev, _next = message.message.split('>')

        return OpponentMovePieceEvent(Piece(Player.HUMAN, True, prev[0], prev[1]),
                                      Piece(Player.HUMAN, True, _next[0], _next[1]))

    def encode(self, event: Event) -> Message:
        if event is not OpponentMovePieceEvent:
            raise EventProtocolMismatchException(self, event)

        event: OpponentMovePieceEvent

        return Message(
            f"{event.before_piece.x}.{event.before_piece.y}:{event.after_piece.x}.{event.after_piece.y}").set_header(
            self.header).set_footer(self.footer)


class BridgeMessageReceiveProtocol(Protocol):
    def __init__(self, footer: str = "") -> None:
        header = 'inbridge'
        super().__init__(BridgeMessageReceiveEvent, header, footer)

    def decode(self, message: Message) -> Event:
        return BridgeMessageReceiveEvent(message)

    def encode(self, event: Event) -> Message:
        if event is not BridgeMessageReceiveEvent:
            raise EventProtocolMismatchException(self, event)

        event: BridgeMessageReceiveEvent
        return event.message.set_header(self.header).set_footer(self.footer)


class BridgeMessageSendProtocol(Protocol):
    def __init__(self, footer: str = "") -> None:
        header = 'outbridge'
        super().__init__(BridgeMessageSendEvent, header, footer)

    def decode(self, message: Message) -> Event:
        return BridgeMessageSendEvent(message)

    def encode(self, event: Event) -> Message:
        if not Protocol.is_type(BridgeMessageSendEvent, event):
            raise EventProtocolMismatchException(self, event)

        event: BridgeMessageSendEvent
        return event.message.set_header(self.header).set_footer(self.footer)


class ProtocolManager:
    __protocols = set()

    @staticmethod
    def register_protocol(protocol):
        print(f"Registered new protocol! {protocol.header}://[{protocol.match_event.__name__}]")
        ProtocolManager.__protocols.add(protocol)

    @staticmethod
    def decodeFor(message: Message) -> Event:
        for protocol in ProtocolManager.__protocols:
            if protocol.is_match_for(message):
                return protocol.decode(message)

    @staticmethod
    def encodeFor(event: Event) -> Message:
        for protocol in ProtocolManager.__protocols:
            if protocol.match_event is type(event):
                return protocol.encode(event)
