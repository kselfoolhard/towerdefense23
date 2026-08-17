package com.modulo08.towerdefense.entities;

public enum TowerType {

    BASICA(3f, 1.0f, 10, 20),
    FRANCO_ATIRADOR(6f, 2.0f, 80, 40),
    METRALHADORA(2.5f, 0.3f, 3, 30),
    MELEE(1.2f, 0.8f, 15, 25);

    private final float range;
    private final float cooldown;
    private final int dano;
    private final int custo;

    TowerType(float range, float cooldown, int dano, int custo) {
        this.range = range;
        this.cooldown = cooldown;
        this.dano = dano;
        this.custo = custo;
    }

    public float getRange() {
        return range;
    }

    public float getCooldown() {
        return cooldown;
    }

    public int getDano() {
        return dano;
    }

    public int getCusto() {
        return custo;
    }
}
