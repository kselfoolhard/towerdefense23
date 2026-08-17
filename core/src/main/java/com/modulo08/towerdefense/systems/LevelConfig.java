package com.modulo08.towerdefense.systems;

import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.List;

public enum LevelConfig {

    MAPA_1(
        Arrays.asList(
            new Vector2(0f, 6.5f),
            new Vector2(5.5f, 6.5f),
            new Vector2(5.5f, 10.5f),
            new Vector2(12.5f, 10.5f),
            new Vector2(12.5f, 2.5f),
            new Vector2(20f, 2.5f)
        ),
        Arrays.asList(
            new Vector2(2.5f, 7.5f),
            new Vector2(2.5f, 5.5f),
            new Vector2(7.5f, 8f),
            new Vector2(8.5f, 11.5f),
            new Vector2(14.5f, 8f),
            new Vector2(14.5f, 4f),
            new Vector2(16f, 0.5f),
            new Vector2(9f, 2f)
        )
    ),

    MAPA_2(
        Arrays.asList(
            new Vector2(0f, 2.5f),
            new Vector2(4.5f, 2.5f),
            new Vector2(4.5f, 9.5f),
            new Vector2(9.5f, 9.5f),
            new Vector2(9.5f, 4.5f),
            new Vector2(15.5f, 4.5f),
            new Vector2(15.5f, 9.5f),
            new Vector2(20f, 9.5f)
        ),
        Arrays.asList(
            new Vector2(2f, 4f),
            new Vector2(2f, 0.5f),
            new Vector2(6f, 6f),
            new Vector2(7f, 11f),
            new Vector2(8f, 6.5f),
            new Vector2(12.5f, 2f),
            new Vector2(17.5f, 7f),
            new Vector2(17.5f, 11f)
        )
    ),

    MAPA_3(
        Arrays.asList(
            new Vector2(0f, 6.5f),
            new Vector2(8.5f, 6.5f),
            new Vector2(8.5f, 3.5f),
            new Vector2(14.5f, 3.5f),
            new Vector2(14.5f, 8.5f),
            new Vector2(20f, 8.5f)
        ),
        Arrays.asList(
            new Vector2(4.5f, 7.2f),  // Distância 0.7 acima do eixo Y=6.5
            new Vector2(4.5f, 5.8f),  // Distância 0.7 abaixo do eixo Y=6.5
            new Vector2(7.8f, 5.0f),  // Distância 0.7 à esquerda do eixo X=8.5
            new Vector2(9.2f, 5.0f),  // Distância 0.7 à direita do eixo X=8.5
            new Vector2(13.8f, 6.0f), // Distância 0.7 à esquerda do eixo X=14.5
            new Vector2(15.2f, 6.0f)  // Distância 0.7 à direita do eixo X=14.5
        )
    );

    private final List<Vector2> waypoints;
    private final List<Vector2> posicoesDosSlots;

    LevelConfig(List<Vector2> waypoints, List<Vector2> posicoesDosSlots) {
        this.waypoints = waypoints;
        this.posicoesDosSlots = posicoesDosSlots;
    }

    public List<Vector2> getWaypoints() {
        return waypoints;
    }

    public List<Vector2> getPosicoesDosSlots() {
        return posicoesDosSlots;
    }
}
