#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#
from Entity import Board


class Heuristic:
    def evaluateFor(self, board: Board, piece_x: int, piece_y: int) -> int:
        pass

    def __init__(self):
        HeuristicsManager.register_heuristic(self)


class HeuristicsManager:
    __heuristics = set()

    @staticmethod
    def register_heuristic(heuristic: Heuristic):
        HeuristicsManager.__heuristics.add(heuristic)


class CapturablePiecesHeuristic(Heuristic):
