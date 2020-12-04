/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import err.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing complex, essentials behaviour relating to the {@link Board}. For
 * example, the movement of pieces - coordinated by the {@link Movement} class - requires a complex series of
 * validity checks on multiple {@link Tile Tiles}, {@link Piece Pieces} and {@link Player Players}.
 * <p>
 * By using this class, complex behaviour can be abstracted away in to much simpler method calls.
 *
 * @see Movement
 * @see Tile
 * @see Piece
 * @see Player
 */
public class BoardManager {
    /**
     * The {@link Board} instance to be managed throughout this class.
     */
    private final Board board;

    BoardManager(Board board) {
        this.board = board;
    }

    /**
     * This method simply returns a Boolean marking if the {@link Tile} at the given x,y coordinates is occupied
     * by an uncaptured, valid {@link Piece} instance.
     *
     * @param x Int - The integer, x coordinate of the {@link Tile} to be evaluated for occupational status.
     * @param y Int - The integer, y coordinate of the {@link Tile} to be evaluated for occupational status.
     *
     * @return Boolean - if the {@link Tile} at the given x,y coordinates is occupied by an uncaptured, valid
     * {@link Piece} instance.
     *
     * @see Tile
     * @see Piece
     * @see Piece#getChecker()
     * @see Piece#getCapturedBy()
     */
    private boolean isOccupied(int x, int y) {
        return board.getTileAtIndex(x, y).getPiece().getChecker() != null && board.getTileAtIndex(x, y).getPiece().getCapturedBy() == null;
    }

    /**
     * This method attempts to execute a move from origin(x,y) to destination(x,y). The process is considerably
     * complex given the large array of requirements for a {@link Piece} to be eligible to move.
     * <p>
     * Initially, this method determines if the move is a capturing move, in that it attempts to move exactly two
     * diagonal spaces away. Next, we determine the direction of movement, either left-up, right-up, left-down or
     * right-down. Of this list, the last two moves are reserved only for {@link Piece.Type#KING}
     * {@link Piece Pieces} - another factor we must check.
     *
     * @param origin {@link Piece} - The origin Piece that we're attempting to move.
     * @param toX    Int - The integer x of the destination coordinates for the given origin Piece.
     * @param toY    Int - The integer y of the destination coordinates for the given origin Piece.
     *
     * @see Piece.Type#KING ent.Piece.Type#KING
     */
    public void makeMove(Piece origin, int toX, int toY) {
        boolean capturing = Math.abs(origin.getX() - toX) == 2 && Math.abs(origin.getY() - toY) == 2;

        boolean left = toX < origin.getX();
        boolean up = toY > origin.getY();

        if (left) {
            if (up) {
                //Left+up
                checkLeftUp(origin, capturing);
            } else {
                //Left+down
                checkLeftDown(origin, capturing);
            }
        } else {
            if (up) {
                //Right+up
                checkRightUp(origin, capturing);
            } else {
                //Right+down
                checkRightDown(origin, capturing);
            }
        }
    }

    /**
     * This method determines if a move in the left-up direction is valid. If the move is not capturing, only the
     * destination {@link Tile} coordinates are calculated. Otherwise, a capturing move sees both the destination
     * and mid-piece coordinates calculated. The 'mid-piece' refers to the {@link Piece} found in between the
     * two-long diagonal maneuver being made. Existence of this piece is mandatory for a capturing move to be
     * possible.
     *
     * @param origin        {@link Piece} - The Piece from which this move originates.
     * @param capturingMove Boolean - If this move attempts to be a capturing move -- does the destination x and
     *                      y coordinates displace the current x and y coordinates of the origin by +/- two units?
     */
    private void checkLeftUp(Piece origin, boolean capturingMove) {
        int destX = origin.getX() - (capturingMove ? 2 : 1);
        int destY = origin.getY() + (capturingMove ? 2 : 1);

        int midX = origin.getX();
        int midY = origin.getY();

        midX -= (capturingMove ? 2 : 1);
        midY += (capturingMove ? 2 : 1);


        finalCheck(origin, destX, destY, midX, midY, capturingMove, false);
    }

    /**
     * This method determines if a move in the left-down direction is valid. If the move is not capturing, only the
     * destination {@link Tile} coordinates are calculated. Otherwise, a capturing move sees both the destination
     * and mid-piece coordinates calculated. The 'mid-piece' refers to the {@link Piece} found in between the
     * two-long diagonal maneuver being made. Existence of this piece is mandatory for a capturing move to be
     * possible.
     *
     * @param origin        {@link Piece} - The Piece from which this move originates.
     * @param capturingMove Boolean - If this move attempts to be a capturing move -- does the destination x and
     *                      y coordinates displace the current x and y coordinates of the origin by +/- two units?
     */
    private void checkLeftDown(Piece origin, boolean capturingMove) {
        int destX = origin.getX() - (capturingMove ? 2 : 1);
        int destY = origin.getY() - (capturingMove ? 2 : 1);

        int midX = origin.getX();
        int midY = origin.getY();

        midX -= (capturingMove ? 2 : 1);
        midY -= (capturingMove ? 2 : 1);


        finalCheck(origin, destX, destY, midX, midY, capturingMove, false);
    }


    /**
     * This method determines if a move in the right-up direction is valid. If the move is not capturing, only the
     * destination {@link Tile} coordinates are calculated. Otherwise, a capturing move sees both the destination
     * and mid-piece coordinates calculated. The 'mid-piece' refers to the {@link Piece} found in between the
     * two-long diagonal maneuver being made. Existence of this piece is mandatory for a capturing move to be
     * possible.
     *
     * @param origin        {@link Piece} - The Piece from which this move originates.
     * @param capturingMove Boolean - If this move attempts to be a capturing move -- does the destination x and
     *                      y coordinates displace the current x and y coordinates of the origin by +/- two units?
     */
    private void checkRightUp(Piece origin, boolean capturingMove) {
        int destX = origin.getX() + (capturingMove ? 2 : 1);
        int destY = origin.getY() + (capturingMove ? 2 : 1);

        int midX = origin.getX();
        int midY = origin.getY();

        midX += (capturingMove ? 2 : 1);
        midY += (capturingMove ? 2 : 1);

        finalCheck(origin, destX, destY, midX, midY, capturingMove, false);
    }

    /**
     * This method determines if a move in the right-down direction is valid. If the move is not capturing, only the
     * destination {@link Tile} coordinates are calculated. Otherwise, a capturing move sees both the destination
     * and mid-piece coordinates calculated. The 'mid-piece' refers to the {@link Piece} found in between the
     * two-long diagonal maneuver being made. Existence of this piece is mandatory for a capturing move to be
     * possible.
     *
     * @param origin        {@link Piece} - The Piece from which this move originates.
     * @param capturingMove Boolean - If this move attempts to be a capturing move -- does the destination x and
     *                      y coordinates displace the current x and y coordinates of the origin by +/- two units?
     */
    private void checkRightDown(Piece origin, boolean capturingMove) {
        int destX = origin.getX() + (capturingMove ? 2 : 1);
        int destY = origin.getY() - (capturingMove ? 2 : 1);

        int midX = origin.getX();
        int midY = origin.getY();

        midX += (capturingMove ? 2 : 1);
        midY -= (capturingMove ? 2 : 1);

        finalCheck(origin, destX, destY, midX, midY, capturingMove, false);
    }

    /**
     * This method takes in the results of many prior calculations and performs final validity checks before
     * executing the move. These checks - and their respective {@link RuntimeException} classes - are as follows:
     * <p>
     * {@link BoardMoveInvalidOriginException}:
     * The origin {@link Piece} should be valid. The {@link Tile} it resides on should return true for
     * {@link #isOccupied(int, int)}.
     * <p>
     * {@link BoardMoveInvalidDestinationException}:
     * The destination {@link Tile} should not be occupied. See {@link #isOccupied(int, int)}.
     * <p>
     * {@link BoardMoveException}:
     * The attempted move must not exceed more than two tiles in the given diagonal axis. This is checked
     * irrespective of if the move is labelled as a capturing move.
     * <p>
     * {@link BoardMoveMissingPieceException}:
     * If the move is a capturing move, the {@link Tile} located in-between the origin and destination Tiles must
     * be occupied. See {@link #isOccupied(int, int)}.
     * <p>
     * {@link BoardMoveSelfCaptureException}:
     * The origin {@link Piece} cannot attempt a capturing move if the Piece to be captured is owned by the same
     * {@link Player}.
     * <p>
     * {@link BoardMoveNotKingException}:
     * The origin {@link Piece} cannot perform this action because the attempted direction of movement is towards
     * the {@link Player Player's} {@link Player.HomeSide}. Whilst this is possible, the Piece to be moved is
     * not of {@link Piece.Type} {@link Piece.Type#KING}.
     *
     * @param origin        {@link Piece} - The Piece that we're validating the attempted movement of.
     * @param destX         Int - The integer x value of the destination {@link Tile} coordinates.
     * @param destY         Int - The integer y value of the destination {@link Tile} coordinates.
     * @param midX          Int - The integer x value of the {@link Piece Piece's} coordinates that this maneuver
     *                      is attempting to capture. Applicable if capturingMove is true.
     * @param midY          Int - The integer y value of the {@link Piece Piece's} coordinates that this maneuver
     *                      is attempting to capture. Applicable if capturingMove is true.
     * @param capturingMove Boolean - If this move is attempting to capture a {@link Piece}. Defined by x/y
     *                      destination coordinates having a +/- difference of two to the origin Piece's x/y
     *                      coordinates.
     * @param trial         Boolean - If this move should be executed once it passes validation checks. This is a
     *                      means of purely testing if a move is valid, without re-implementing validity checks.
     *                      If this method is called from a try-catch statement, each given
     *                      {@link BoardMoveException} instance can be captured. If one is thrown, we know that
     *                      the move would not be valid, and why so.
     *
     * @see #isOccupied(int, int)
     * @see Piece
     * @see Tile
     * @see Player
     * @see Player.HomeSide
     * @see Piece.Type
     * @see BoardMoveInvalidOriginException
     * @see BoardMoveInvalidDestinationException
     * @see BoardMoveException
     * @see BoardMoveMissingPieceException
     * @see BoardMoveSelfCaptureException
     * @see BoardMoveNotKingException
     */
    private void finalCheck(Piece origin, int destX, int destY, int midX, int midY, boolean capturingMove,
                            boolean trial) {


        if (!isOccupied(origin.getX(), origin.getY()))
            throw new BoardMoveInvalidOriginException(origin, destX, destY);

        if (isOccupied(destX, destY))
            throw new BoardMoveInvalidDestinationException("Invalid destination, already occupied! x:" + destX +
                    "," +
                    " y: " + destY);

        int diffX = Math.abs(origin.getX() - destX);
        int diffY = Math.abs(origin.getY() - destY);

        if (diffX == 0 || diffX > 2 || diffY == 0 || diffY > 2) //Filters for attempted moves that aren't diagonal
            throw new BoardMoveException("Attempted move was too extreme, or not along a diagonal!");


        if (capturingMove) {
            if (isOccupied(midX, midY))
                throw new BoardMoveMissingPieceException("Attempting to perform a capture over non-existent " +
                        "piece! x:" + midX + ", y: " + midY);
            if (board.getTileAtIndex(midX, midY).getPiece().getPlayer() == origin.getPlayer())
                throw new BoardMoveSelfCaptureException("Attempting to capture a member of your team! x:" + midX + "," +
                        " y: "
                        + midY);
        }

        boolean kingOnlyMove = origin.getPlayer().getHomeSide() == Player.Defaults.HUMAN.getPlayer().getHomeSide() ?
                destY < origin.getY() :
                origin.getPlayer().getHomeSide() == Player.Defaults.COMPUTER.getPlayer().getHomeSide() && destY > origin.getY();

        if (origin.getType() != Piece.Type.KING && kingOnlyMove)
            throw new BoardMoveNotKingException("This piece is not a king!");

        Piece capturing = capturingMove ? board.getTileAtIndex(midX, midY).getPiece() : null;

        if (!trial)
            executeMove(origin, destX, destY, capturing);
    }

    /**
     * This method is responsible for the actual execution of a move. All validity is assumed, as this is called
     * directly from {@link #finalCheck(Piece, int, int, int, int, boolean, boolean)}. Quite simply, the origin
     * {@link Piece} is removed from its origin. The destination {@link Tile} is updated with the old Piece.
     * <p>
     * Once this method has completed execution of movement, the {@link #doAutoCapture(Piece)} method is called.
     *
     * @param origin   {@link Piece} - The Piece to be moved.
     * @param destX    Int - The integer x coordinate of the destination {@link Tile}.
     * @param destY    Int - The integer y coordinate of the destination {@link Tile}.
     * @param captured {@link Piece} - The Piece that has been captured during this process. This is required so
     *                 that the capturing {@link Player} can be accredited with this Piece and the
     *                 Piece removed from play.
     *
     * @see Piece#getCapturedBy()
     * @see #finalCheck(Piece, int, int, int, int, boolean, boolean)
     */
    private void executeMove(Piece origin, int destX, int destY, Piece captured) {
        if (captured != null) {
            origin.getPlayer().getCapturedPieces().add(captured);
            captured.deleteFromBoard(); //TODO Add to capturer's captured pieces list
        }

        board.getTileAtIndex(destX, destY).init();
        board.getTileAtIndex(origin.getX(), origin.getY()).delete();

        //TODO force capturing of neighbours
        //TODO force auto-crowning of piece if it's on the back board

        doAutoCapture(origin); //TODO might not be working, check

        //TODO check for a winning state
    }

    /**
     * Per the rules of Checkers, a {@link Piece} is mandated to make a capturing move if a capturing move is
     * possible. This method is responsible for enforcing this policy.
     * <p>
     * First, all diagonal axis around the given Piece are checked for eligibility as a capturing move. The
     * validity performed for this is executed via {@link #finalCheck(Piece, int, int, int, int, boolean, boolean)}.
     * <p>
     * Once we've aggregated a {@link List} of valid moves, we then filter our moves to a single move using a
     * {@link java.util.stream.Stream}, isolate the move and execute it.
     *
     * @param piece {@link Piece} - The Piece we should enforce auto-capturing policies against.
     *
     * @see #finalCheck(Piece, int, int, int, int, boolean, boolean)
     */
    private void doAutoCapture(Piece piece) {
        Player opponent = piece.getPlayer().getName().equals(Player.Defaults.COMPUTER.getPlayer().getName()) ?
                Player.Defaults.HUMAN.getPlayer() : Player.Defaults.COMPUTER.getPlayer();
        if (board.getKingsWallRow(opponent) == piece.getY() && piece.getType() == Piece.Type.MAN) {
            piece.makeKing();
            return; //Terminate all moves from here, since they've just been Crowned.
        }

        ArrayList<Boolean> validMoves = new ArrayList<>();
        /*
        Left-Up
        Left-Down
        Right-Up
        Right-Down
         */

        try {
            finalCheck(piece, piece.getX() - 2, piece.getY() + 2, piece.getX() - 1, piece.getY() + 1, true, true);
            validMoves.add(true);
        } catch (BoardMoveException e) {
            validMoves.add(false);
            //Do nothing
        }

        /*
        Check which directions are valid to move in
         */
        try {
            finalCheck(piece, piece.getX() - 2, piece.getY() - 2, piece.getX() - 1, piece.getY() - 1, true, true);
            validMoves.add(true);
        } catch (BoardMoveException e) {
            validMoves.add(false);
            //Do nothing
        }

        try {
            finalCheck(piece, piece.getX() + 2, piece.getY() + 2, piece.getX() + 1, piece.getY() + 1, true, true);
            validMoves.add(true);
        } catch (BoardMoveException e) {
            validMoves.add(false);
            //Do nothing
        }

        try {
            finalCheck(piece, piece.getX() + 2, piece.getY() - 2, piece.getX() + 1, piece.getY() - 1, true, true);
            validMoves.add(true);
        } catch (BoardMoveException e) {
            validMoves.add(false);
            //Do nothing
        }

        //The player has multiple options available... they must now decide which move to follow
        if (validMoves.stream().filter(t -> t.equals(Boolean.TRUE)).count() > 1) {
            //TODO it is now on the player to decide which move to take. Maybe fire event for this, including
            // which moves are valid. Also write a method to generate a list of Direction enums detailing which
            // moves are valid for a given piece

            return;
        }

        //There is now only one move possible. Find the move and execute it.
        if (Boolean.TRUE.equals(validMoves.get(0)))
            makeMove(piece, piece.getX() - 2, piece.getY() + 2);
        if (Boolean.TRUE.equals(validMoves.get(1)))
            makeMove(piece, piece.getX() - 2, piece.getY() - 2);
        if (Boolean.TRUE.equals(validMoves.get(2)))
            makeMove(piece, piece.getX() + 2, piece.getY() + 2);
        if (Boolean.TRUE.equals(validMoves.get(3)))
            makeMove(piece, piece.getX() + 2, piece.getY() - 2);
    }
}
