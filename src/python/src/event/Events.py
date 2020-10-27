#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#

class Event:
    pass


class PlayerMakeMoveEvent(Event):
    def __init__(self, from_pos: tuple, to_pos: tuple):
        self.from_pos = from_pos
        self.to_pos = to_pos


class BridgeMessageEvent(Event):
    pass