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
    private String name;
    private Color colour;
    private ArrayList<Piece> capturedPieces = new ArrayList<>();
    private ArrayList<Piece> ownedPieces;

    public Player(String name, Color colour, ArrayList<Piece> ownedPieces) {
        this.name = name;
        this.colour = colour;
        this.id = UUID.randomUUID();
        this.ownedPieces = ownedPieces;
    }

    public ArrayList<Piece> getOwnedPieces() {
        return ownedPieces;
    }

    public ArrayList<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public void capturePiece(Piece piece){
        this.capturedPieces.add(piece);
        piece.setCapturedBy(this);
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
}
