package com.modulo08.towerdefense.systems;

import com.modulo08.towerdefense.entities.EnemyType;

public class Wave {

    private final int quantidadeInimigos;
    private final float intervaloEntreSpawns;
    private final EnemyType tipoInimigo;

    public Wave(int quantidadeInimigos, float intervaloEntreSpawns, EnemyType tipoInimigo) {
        this.quantidadeInimigos = quantidadeInimigos;
        this.intervaloEntreSpawns = intervaloEntreSpawns;
        this.tipoInimigo = tipoInimigo;
    }

    public int getQuantidadeInimigos() {
        return quantidadeInimigos;
    }

    public float getIntervaloEntreSpawns() {
        return intervaloEntreSpawns;
    }

    public EnemyType getTipoInimigo() {
        return tipoInimigo;
    }


}
