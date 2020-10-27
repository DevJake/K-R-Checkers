#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#

import socket
import time

from Entity import Message


class Bridge:
    host = '127.0.0.1'
    inbound_port = 5000
    outbound_port = 5001
    __outbound_socket: socket.socket
    __inbound_socket: socket.socket
    refresh_timer: int

    @staticmethod
    def boot():
        Bridge.__inbound_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        Bridge.__inbound_socket.bind((Bridge.host, Bridge.inbound_port))

        # Bridge.__outbound_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        # Bridge.__outbound_socket.bind((Bridge.host, Bridge.outbound_port))

        # Bridge.__begin_listening()

    @staticmethod
    def __begin_listening():
        Bridge.__inbound_socket.listen()
        Bridge.__inbound_socket.accept()

    @staticmethod
    def send(message: Message):
        print(f"Sending new message @{int(round(time.time() * 1000))}")
        # Bridge.__outbound_socket.send(message.message.encode())

        s = socket.socket()

        s.connect((Bridge.host, Bridge.outbound_port))

        print("Attempting to send a new Message...")
        s.send(message.message.encode())

        s.close()

# Bridge.boot()
#
# Bridge.send(Message(None, "Hello World!!", None))
# Bridge.send(Message(None, "Hello World!!", None))
# Bridge.send(Message(None, "Hello World!!", None))
# Bridge.send(Message(None, "Hello World!!", None))
# Bridge.send(Message(None, "Hello World!!", None))
