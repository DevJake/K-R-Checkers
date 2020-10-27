#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#

from Entity import Message


class Event:
    pass


class PlayerMakeMoveEvent(Event):
    def __init__(self, from_pos: tuple, to_pos: tuple):
        self.from_pos = from_pos
        self.to_pos = to_pos


class BridgeMessageReceiveEvent(Event):
    def __init__(self, message: Message):
        self.message = Message


class BridgeMessageSendEvent(Event):
    def __init__(self, message: Message):
        self.message = Message


class BoardUpdateStateEvent(Event):
    def __init__(self, message: Message):
        self.message = Message


class EventListener:
    def on_bridge_send_message(self, event: BridgeMessageSendEvent):
        pass

    def on_bridge_receive_message(self, event: BridgeMessageReceiveEvent):
        pass

    def on_board_update_status(self, event: BoardUpdateStateEvent):
        pass


class EventManager:
    __listeners: set[EventListener] = set()

    @staticmethod
    def register_listener(listener: EventListener):
        EventManager.__listeners.add(listener)

    @staticmethod
    def fire(event: Event):
        for listener in EventManager.__listeners:
            if event is BridgeMessageReceiveEvent:
                listener.on_bridge_send_message(event)
            elif event is BridgeMessageReceiveEvent:
                listener.on_bridge_receive_message(event)
            elif event is BoardUpdateStateEvent:
                listener.on_board_update_status(event)
