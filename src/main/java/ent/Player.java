/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package ent;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a Player within the game.
 */
public class Player extends Entity {

    /**
     * A unique ID assigned to this Player. Given that our reference to a Player is our only means to determine many
     * aspects of game logic (who plays next, who just captured a {@link Piece}, etc.), it's important we can
     * accurately identify them.
     */
    private final UUID id;

    /**
     * An ArrayList of opponent {@link Piece Pieces} this Player has captured.
     */
    private final ArrayList<Piece> capturedTiles = new ArrayList<>();

    /**
     * The {@link HomeSide} of this Player. This denotes which side of the {@link Board} is the origin for this
     * Player. This is used to determine where their King's Row is and the acceptable behaviour of their
     * {@link Piece Pieces}, dependent on their {@link ent.Piece.Type}.
     *
     * @see HomeSide
     * @see Board
     * @see Piece
     * @see ent.Piece.Type
     */
    private final HomeSide homeSide;

    /**
     * Simply, the name for this Player.
     */
    private String name;

    /**
     * The {@link Color} used for this Player. This colour doesn't determine the colour of {@link Tile Tiles} or
     * {@link Piece Pieces}, but instead the colour used for representing this individual on, say, a leaderboard.
     * Ideally, this colour would be the same to that of their Pieces, so as to create consistency.
     */
    private Color colour;

    /**
     * Constructs a new Player instance. Importantly, the {@link UUID} for their ID is not passed in, but is instead
     * generated using {@link UUID#randomUUID()}.
     *
     * @param homeSide {@link HomeSide} - The origin HomeSide for this Player.
     * @param name     {@link String} - The name for this Player.
     * @param colour   {@link Color} - The Color class used for representing this Player.
     */
    public Player(HomeSide homeSide, String name, Color colour) {
        this.homeSide = homeSide;
        this.name = name;
        this.colour = colour;
        this.id = UUID.randomUUID();
    }

    /**
     * @return {@link HomeSide} - The origin HomeSide for this Player.
     */
    public HomeSide getHomeSide() {
        return homeSide;
    }

    /**
     * @return An ArrayList of opponent {@link Piece Pieces} this Player has captured.
     */
    public ArrayList<Piece> getCapturedPieces() {
        return capturedTiles;
    }

    /**
     * @return {@link UUID} - The unique ID randomly assigned to this Player.
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return {@link String} - The name for this Player.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name {@link String} - The new name for this Player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return {@link Color} - The Color class used for representing this Player.
     */
    public Color getColour() {
        return colour;
    }

    /**
     * @param colour {@link Color} - The new Color class used for representing this Player.
     */
    public void setColour(Color colour) {
        this.colour = colour;
    }

    /**
     * This enum is used to represent which side of the {@link Board} a Player originates their Pieces from. This is
     * used to determine where their King's Row is and the acceptable behaviour of their
     * * {@link Piece Pieces}, dependent on their {@link ent.Piece.Type}.
     *
     * @see ent.Piece.Type
     * @see Board
     * @see Piece
     */
    public enum HomeSide {
        /**
         * The top half of the {@link Board}.
         */
        TOP,
        /**
         * The bottom half of the {@link Board}.
         */
        BOTTOM,
        /**
         * No side of the {@link Board}. This is equivalent to a 'null' value.
         */
        NONE
    }

    /**
     * This enum is used to help in defining some default Player types. This helps to avoid issues in comparing
     * Player properties during a standard game. In this, we define three default Players;
     * HUMAN:
     * The human player, with a default {@link Piece} {@link Color} of PINK and {@link HomeSide} of BOTTOM. Named the
     * "HUMAN".
     * <p>
     * COMPUTER:
     * The computer player, with a default {@link Piece} {@link Color} of BLACK and {@link HomeSide} of TOP.
     * Appropriately named the "A.I.".
     * <p>
     * NONE:
     * A null-equivalent player that shouldn't be used for any forms of gameplay. When a {@link Tile} contains a
     * {@link Piece} owned by this Player type, it should be perceived as an 'empty' Tile. This type has no
     * {@link HomeSide} or meaningful name value.
     *
     * @see Piece
     * @see HomeSide
     * @see Tile
     * @see Color
     */
    public enum Defaults {
        HUMAN(new Player(HomeSide.BOTTOM, "HUMAN", Color.PINK)),
        COMPUTER(new Player(HomeSide.TOP, "A.I.", Color.BLACK)),
        NONE(new Player(HomeSide.NONE, "NULL", Color.BLACK));

        private final Player player;

        Defaults(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }
    }
}
