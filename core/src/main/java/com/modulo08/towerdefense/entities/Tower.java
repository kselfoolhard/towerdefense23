package com.modulo08.towerdefense.entities;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Tower {

    public enum State { IDLE, ATTACKING, STUNNED }

    private final Vector2 posicao;
    private final TowerType tipo;
    private final float velocidadeProjetil;
    private float tempoDesdeUltimoTiro;
    private float tempoStunRestante = 0f;

    // Variáveis para gerenciar Animações
    private State estadoAtual = State.IDLE;
    private float stateTime = 0f;
    private float tempoAtaqueAnimacao = 0.3f; // Duração do frame/animação de ataque

    public Tower(Vector2 posicao, TowerType tipo, float velocidadeProjetil) {
        this.posicao = new Vector2(posicao);
        this.tipo = tipo;
        this.velocidadeProjetil = velocidadeProjetil;
        this.tempoDesdeUltimoTiro = tipo.getCooldown();
    }

    public void update(float delta, List<Enemy> inimigosAtivos, List<Projectile> projeteisAtivos) {
        stateTime += delta;

        if (tempoStunRestante > 0f) {
            tempoStunRestante -= delta;
            estadoAtual = State.STUNNED;
            return;
        }

        if (estadoAtual == State.ATTACKING && stateTime >= tempoAtaqueAnimacao) {
            estadoAtual = State.IDLE;
        }

        tempoDesdeUltimoTiro += delta;
        if (tempoDesdeUltimoTiro < tipo.getCooldown()) {
            return;
        }

        Enemy alvo = null;
        for (Enemy inimigo : inimigosAtivos) {
            if (inimigo.estaMorto()) continue;
            if (posicao.dst(inimigo.getPosicao()) > tipo.getRange()) continue;

            if (alvo == null || inimigo.getProgresso() > alvo.getProgresso()) {
                alvo = inimigo;
            }
        }

        if (alvo == null) return;

        projeteisAtivos.add(new Projectile(posicao, alvo, velocidadeProjetil, tipo.getDano(), tipo));
        tempoDesdeUltimoTiro = 0f;

        // Dispara estado de ataque
        estadoAtual = State.ATTACKING;
        stateTime = 0f;
    }

    public void aplicarStun(float duracao) {
        this.tempoStunRestante = duracao;
        this.estadoAtual = State.STUNNED;
    }

    public boolean estaStunada() { return tempoStunRestante > 0f; }
    public Vector2 getPosicao() { return posicao; }
    public TowerType getTipo() { return tipo; }
    public float getRange() { return tipo.getRange(); }
    public State getEstadoAtual() { return estadoAtual; }
    public float getStateTime() { return stateTime; }
}
