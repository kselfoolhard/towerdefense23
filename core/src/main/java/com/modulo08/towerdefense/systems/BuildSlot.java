package com.modulo08.towerdefense.systems;

import com.badlogic.gdx.math.Vector2;
import com.modulo08.towerdefense.entities.Tower;

public class BuildSlot {

    private final Vector2 posicao;
    private Tower torre;

    public BuildSlot(Vector2 posicao) {
        this.posicao = new Vector2(posicao);
    }
    public Vector2 getPosicao(){
        return posicao;
    }
    public boolean estaLivre(){
        return torre == null;
    }
    public Tower getTorre(){
        return torre;
    }
    public void construir(Tower torre){
        this.torre = torre;
    }
}
