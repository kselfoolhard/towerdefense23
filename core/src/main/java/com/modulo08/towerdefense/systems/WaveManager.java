package com.modulo08.towerdefense.systems;

import com.badlogic.gdx.math.Vector2;
import com.modulo08.towerdefense.entities.Enemy;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {

    private final List<Wave> waves = new ArrayList<>();
    private final List<Enemy> inimigosAtivos;
    private final List<Vector2> waypoints;

    private int waveAtual;
    private int spawnsRestantesDaWave;
    private float spawnTimer;

    public WaveManager(List<Enemy> inimigosAtivos, List<Vector2> waypoints) {
        this.inimigosAtivos = inimigosAtivos;
        this.waypoints = waypoints;
    }

    public void addWave(Wave wave) {
        waves.add(wave);
        if (waves.size() == 1) {
            iniciarWave(wave);
        }
    }

    public void update(float delta) {
        if (waveAtual >= waves.size()) {
        return;
        }

        spawnTimer -= delta;
    if (spawnTimer > 0f) {
        return;
    }

    if (spawnsRestantesDaWave <= 0) {
        waveAtual++;
        if (waveAtual < waves.size()) {
            iniciarWave(waves.get(waveAtual));
        }
        return;
    }

    Wave wave = waves.get(waveAtual);
    Vector2 posicaoInicial = waypoints.get(0);
    Enemy inimigo = new Enemy(posicaoInicial, waypoints, wave.getTipoInimigo());
    inimigosAtivos.add(inimigo);

    spawnsRestantesDaWave--;
    spawnTimer = wave.getIntervaloEntreSpawns();
    }

    private void iniciarWave(Wave wave) {
        spawnsRestantesDaWave = wave.getQuantidadeInimigos();
        spawnTimer = wave.getIntervaloEntreSpawns();
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public int getWaveAtual() {
        return waveAtual;
    }
}
