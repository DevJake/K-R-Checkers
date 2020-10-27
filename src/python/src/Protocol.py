#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#

import uuid
from abc import ABC, abstractmethod
from uuid import UUID


class Message:
    def __init__(self, id: UUID, message: str, response_code: UUID):
        self.id = id
        self.message = message
        self.response_code = response_code


class Event:
    pass


class Protocol(ABC):
    @abstractmethod
    def __init__(self, header: str, footer: str) -> None:
        self.header = header
        self.footer = footer
        ProtocolManager.register_protocol(self)

    def is_match_for(self, message: Message) -> bool:
        return message.message.startswith(self.header + '://') and message.message.endswith(self.footer + '//:')

    @abstractmethod
    def decode(self, message: Message) -> Event:
        pass

    @abstractmethod
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


class PlayerMakeMoveProtocol(Protocol):
    def __init__(self, header: str, footer: str) -> None:
        header = "pmm"
        super().__init__(header, footer)

    def decode(self, message: Message) -> Event:
        prev, _next = message.message.split('>')

        return PlayerMakeMoveEvent((prev[0], prev[1]), (_next[0], _next[1]))

    def encode(self, event: Event) -> Message:
        pass


class PlayerMakeMoveEvent(Event):
    def __init__(self, from_pos: tuple, to_pos: tuple):
        self.from_pos = from_pos
        self.to_pos = to_pos


p = PlayerMakeMoveProtocol('', '')

print(p.footer)
print(p.is_match_for(Message(uuid.uuid4(), "Test", None)))
e = ProtocolManager.decodeFor(Message(uuid.uuid4(), "pmm://a4>b6//:", None))
print(type(e) == PlayerMakeMoveEvent)
e: PlayerMakeMoveEvent = e

print(e.to_pos)

print(e.__class__)
