#
#  Copyright (c) Jake Dean, 2020.
#
#  This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
#  To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
#  Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
#
from Entity import Board, Player
from protocol.Protocols import decode_board

b = Board()

# print(b.board)

b.set_piece_at(0, 0, Player.COMPUTER)
b.set_piece_at(7, 7, Player.HUMAN)

print(b.board)

s = "Tile:[{player_name:HUMAN},{x_pos:0},{y_pos:0}] Tile:[{player_name:A.I.},{x_pos:0},{y_pos:6}] Tile:[{" \
    "player_name:HUMAN},{x_pos:1},{y_pos:1}] Tile:[{player_name:HUMAN},{x_pos:1},{y_pos:3}] Tile:[{player_name:A.I.},{x_pos:1},{y_pos:5}] Tile:[{player_name:A.I.},{x_pos:1},{y_pos:7}] Tile:[{player_name:HUMAN},{x_pos:2},{y_pos:0}] Tile:[{player_name:HUMAN},{x_pos:2},{y_pos:2}] Tile:[{player_name:A.I.},{x_pos:2},{y_pos:6}] Tile:[{player_name:HUMAN},{x_pos:3},{y_pos:1}] Tile:[{player_name:A.I.},{x_pos:3},{y_pos:5}] Tile:[{player_name:A.I.},{x_pos:3},{y_pos:7}] Tile:[{player_name:HUMAN},{x_pos:4},{y_pos:0}] Tile:[{player_name:HUMAN},{x_pos:4},{y_pos:2}] Tile:[{player_name:A.I.},{x_pos:4},{y_pos:6}] Tile:[{player_name:HUMAN},{x_pos:5},{y_pos:1}] Tile:[{player_name:A.I.},{x_pos:5},{y_pos:5}] Tile:[{player_name:A.I.},{x_pos:5},{y_pos:7}] Tile:[{player_name:HUMAN},{x_pos:6},{y_pos:0}] Tile:[{player_name:HUMAN},{x_pos:6},{y_pos:2}] Tile:[{player_name:A.I.},{x_pos:6},{y_pos:6}] Tile:[{player_name:HUMAN},{x_pos:7},{y_pos:1}] Tile:[{player_name:A.I.},{x_pos:7},{y_pos:5}] Tile:[{player_name:A.I.},{x_pos:7},{y_pos:7}"

print(decode_board(s).as_string())
