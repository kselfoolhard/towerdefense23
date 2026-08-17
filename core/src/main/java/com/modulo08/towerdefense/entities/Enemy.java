package com.modulo08.towerdefense.entities;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Enemy {

    private static final float THRESHOLD_WAYPOINT = 0.1f;
    private static final float RAIO_EXPLOSAO_BOOMER = 1.5f; // Raio em que o boomer afeta as torres
    private static final float TEMPO_STUN_SEGUNDOS = 2.0f; // 2 segundos de stun

    private final Vector2 posicao;
    private final List<Vector2> waypoints;
    private int waypointAtual;
    private final float velocidade;
    private int vida;
    private final int recompensa;
    private final EnemyType tipo; // Salvando o tipo para identificar o boomer
    private boolean jaExplodiu = false;

    // NOVO: Variável para controlar o tempo da animação
    private float stateTime = 0f;

    public Enemy(Vector2 posicaoInicial, List<Vector2> waypoints, EnemyType tipo) {
        this.posicao = new Vector2(posicaoInicial);
        this.waypoints = waypoints;
        this.waypointAtual = 0;
        this.velocidade = tipo.getVelocidade();
        this.vida = tipo.getVidaInicial();
        this.recompensa = tipo.getRecompensa();
        this.tipo = tipo;
    }

    public void moveTowardsWaypoint(float delta) {
        // NOVO: Incrementa o tempo para a animação avançar os frames
        stateTime += delta;

        if (chegouAoFim()) {
            return;
        }
        Vector2 destino = waypoints.get(waypointAtual);
        Vector2 direcao = new Vector2(destino).sub(posicao);

        if (direcao.len() <= THRESHOLD_WAYPOINT) {
            posicao.set(destino);
            waypointAtual++;
            return;
        }

        direcao.nor().scl(velocidade * delta);
        posicao.add(direcao);
    }

    // Método que aciona a explosão nas torres próximas quando morre ou chega perto
    public void verificarExplosaoBoomer(List<Tower> torres) {
        if (jaExplodiu || tipo != EnemyType.EXPLOSIVO) {
            return;
        }

        // Explode se morreu ou se chegou ao fim do mapa
        if (estaMorto() || chegouAoFim()) {
            jaExplodiu = true;
            for (Tower torre : torres) {
                if (torre.getPosicao().dst(posicao) <= RAIO_EXPLOSAO_BOOMER) {
                    torre.aplicarStun(TEMPO_STUN_SEGUNDOS);
                }
            }
        }
    }

    public Vector2 getPosicao() { return posicao; }
    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }
    public int getRecompensa() { return recompensa; }
    public boolean estaMorto() { return vida <= 0; }

    public float getProgresso() {
        if (chegouAoFim()) {
            return 1.0f;
        }
        float progressoBase = waypointAtual / (float) waypoints.size();
        float distanciaParaProximo = posicao.dst(waypoints.get(waypointAtual));
        return progressoBase - (distanciaParaProximo * 0.0001f);
    }

    public boolean chegouAoFim() {
        return waypointAtual >= waypoints.size();
    }

    public EnemyType getTipo() {
        return tipo;
    }

    // NOVO: Getter do stateTime para ser usado na GameScreen
    public float getStateTime() {
        return stateTime;
    }
}
