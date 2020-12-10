#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

import socket
import time
from threading import Timer

import Entity as ent
import GameManager as gm
import event.Events as ev
import protocol.Protocols as prot
from BoardListeners import BoardStatusListener


class Bridge:
    host = '127.0.0.1'
    inbound_port = 5000
    outbound_port = 5001
    __outbound_socket: socket.socket
    __inbound_socket: socket.socket
    refresh_timer: float = 0.5
    __t: Timer

    __is_closed: bool = False

    @staticmethod
    def close():
        Bridge.__outbound_socket.close()
        Bridge.__inbound_socket.close()
        __is_closed = True

    @staticmethod
    def boot():
        Bridge.__inbound_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        Bridge.__inbound_socket.bind((Bridge.host, Bridge.inbound_port))

        Bridge.__t = Timer(0.0, Bridge.__begin_listening)
        Bridge.__t.start()

    @staticmethod
    def __begin_listening():
        print("Began listening...")
        Bridge.__inbound_socket.listen()
        if not Bridge.__is_closed:
            conn, addr = Bridge.__inbound_socket.accept()

            print("Received new Message...")

            data = conn.recv(1024)
            while True:
                d = conn.recv(1024)
                if not d:
                    break
                data += d

            data = data.decode()
            # TODO decode to correct protocol, split off @ID
            print(data)

            e = prot.ProtocolManager.decodeFor(ent.Message(data))

            ev.EventManager.fire(e)

        Bridge.__t = Timer(Bridge.refresh_timer, Bridge.__begin_listening)
        Bridge.__t.start()

    @staticmethod
    def send(event: ev.Event):
        print(f"Sending new message @{int(round(time.time() * 1000))}")

        s = socket.socket()

        s.connect((Bridge.host, Bridge.outbound_port))

        print(prot.ProtocolManager.encodeFor(event))
        m: ent.Message = prot.ProtocolManager.encodeFor(event)

        print(f"Attempting to send a new Message...")
        s.send(f"{m.header}@{m.id}://{m.message}//:".encode())
        s.close()


prot.ProtocolManager.register_protocol(prot.OpponentMovePieceProtocol())
prot.ProtocolManager.register_protocol(prot.BridgeMessageReceiveProtocol())
prot.ProtocolManager.register_protocol(prot.BridgeMessageSendProtocol())
prot.ProtocolManager.register_protocol(prot.BoardUpdateStateProtocol())
prot.ProtocolManager.register_protocol(prot.BoardValidMovesProtocol())

ev.EventManager.register_listener(BoardStatusListener())

Bridge.boot()

Bridge.send(ev.BridgeMessageSendEvent(ent.Message("Hello World!")))

gm = gm.Manager()
