package com.modulo08.towerdefense.entities;

public enum EnemyType {

    BASICO(1.5f, 30, 10),
    RAPIDO(3.0f, 15, 8),
    TANQUE(0.8f, 80, 25),
    EXPLOSIVO(1.2f, 40, 15);

    private final float velocidade;
    private final int vidaInicial;
    private final int recompensa;

    EnemyType(float velocidade, int vidaInicial, int recompensa) {
        this.velocidade = velocidade;
        this.vidaInicial = vidaInicial;
        this.recompensa = recompensa;
    }

    public float getVelocidade() {
        return velocidade;
    }

    public int getVidaInicial() {
        return vidaInicial;
    }

    public int getRecompensa() {
        return recompensa;
    }
}
