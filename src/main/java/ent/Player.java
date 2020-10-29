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

public class Player extends Entity {
    private final UUID id;
    private final ArrayList<Piece> capturedTiles = new ArrayList<>();
    private final HomeSide homeSide;
    private String name;
    private Color colour;

    public HomeSide getHomeSide() {
        return homeSide;
    }

    public Player(HomeSide homeSide, String name, Color colour) {
        this.homeSide = homeSide;
        this.name = name;
        this.colour = colour;
        this.id = UUID.randomUUID();
    }

    public ArrayList<Piece> getCapturedPieces() {
        return capturedTiles;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public enum HomeSide {
        TOP,
        BOTTOM
    }

    public enum Defaults {
        HUMAN(new Player(HomeSide.BOTTOM, "HUMAN", Color.PINK)),
        COMPUTER(new Player(HomeSide.TOP, "A.I.", Color.BLACK));

        private final Player player;

        Defaults(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }
    }
}
