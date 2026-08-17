package com.modulo08.towerdefense;

import com.modulo08.towerdefense.screens.GameScreen;
import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
