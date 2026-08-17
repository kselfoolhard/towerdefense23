package com.modulo08.towerdefense.entities;

import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private static final float THRESHOULD_IMPACTO = 0.15f;

    private final Vector2 posicao;
    private final Enemy alvo;
    private final float velocidade;
    private final int dano;
    private final TowerType tipoTorre; // NOVO: Guarda o tipo da torre

    // Construtor atualizado para receber TowerType
    public Projectile(Vector2 posicaoInicial, Enemy alvo, float velocidade, int dano, TowerType tipoTorre) {
        this.posicao = new Vector2(posicaoInicial);
        this.alvo = alvo;
        this.velocidade = velocidade;
        this.dano = dano;
        this.tipoTorre = tipoTorre;
    }

    public void update(float delta) {
        if (alvo.estaMorto()) {
            return;
        }
        Vector2 direcao = new Vector2(alvo.getPosicao()).sub(posicao);
        if (direcao.len() > 0.0001f) {
            direcao.nor().scl(velocidade * delta);
            posicao.add(direcao);
        }
    }

    public boolean atingiuAlvo() {
        return posicao.dst(alvo.getPosicao()) <= THRESHOULD_IMPACTO;
    }

    public boolean alvoMorreu() {
        return alvo.estaMorto();
    }

    public void aplicarDanoNoAlvo() {
        alvo.setVida(alvo.getVida() - dano);
    }

    public Vector2 getPosicao() {
        return posicao;
    }

    public TowerType getTipoTorre() {
        return tipoTorre;
    }
}
