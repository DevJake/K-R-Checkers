#
#  Copyright (c) Candidate 181379, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#
import Entity as ent
import event.Events as ev
import protocol.Protocols as prot
from BoardListeners import BoardStatusListener
from Server import Bridge
import GameManager as gm

prot.ProtocolManager.register_protocol(prot.OpponentMovePieceProtocol())
prot.ProtocolManager.register_protocol(prot.BridgeMessageReceiveProtocol())
prot.ProtocolManager.register_protocol(prot.BridgeMessageSendProtocol())
prot.ProtocolManager.register_protocol(prot.BoardUpdateStateProtocol())
prot.ProtocolManager.register_protocol(prot.BoardValidMovesProtocol())

ev.EventManager.register_listener(BoardStatusListener())

Bridge.boot()

Bridge.send(ev.BridgeMessageSendEvent(ent.Message("Hello World!")))
