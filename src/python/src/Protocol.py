#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#

import uuid
from uuid import UUID

from event.Events import PlayerMakeMoveEvent
from protocol.Protocols import PlayerMakeMoveProtocol, ProtocolManager


class Message:
    def __init__(self, id: UUID, message: str, response_code: UUID):
        self.id = id
        self.message = message
        self.response_code = response_code


p = PlayerMakeMoveProtocol('', '')

print(p.footer)
print(p.is_match_for(Message(uuid.uuid4(), "Test", None)))
e = ProtocolManager.decodeFor(Message(uuid.uuid4(), "pmm://a4>b6//:", None))
if type(e) == PlayerMakeMoveEvent:
    e: PlayerMakeMoveEvent = e

print(e.to_pos)

print(e.__class__)
