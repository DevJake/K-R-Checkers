#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#
from abc import ABC, abstractmethod

from Entity import Message
from event.Events import Event, PlayerMakeMoveEvent


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


class PlayerMakeMoveProtocol(Protocol):
    def __init__(self, header: str, footer: str) -> None:
        header = "pmm"
        super().__init__(header, footer)

    def decode(self, message: Message) -> Event:
        prev, _next = message.message.split('>')

        return PlayerMakeMoveEvent((prev[0], prev[1]), (_next[0], _next[1]))

    def encode(self, event: Event) -> Message:
        pass


class BridgeMessageProtocol(Protocol):

    def __init__(self, header: str, footer: str) -> None:
        header = 'bridge'
        super().__init__(header, footer)

    def decode(self, message: Message) -> Event:
        pass

    def encode(self, event: Event) -> Message:
        pass


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
