package com.modulo08.towerdefense.systems;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;

public class InputUtils {

    private InputUtils() {
    }

    public static Vector3 screenToWorld(Camera camera, int screenX, int screenY) {
        Vector3 posicao = new Vector3(screenX, screenY, 0);
        camera.unproject(posicao);
        return posicao;
    }
}
