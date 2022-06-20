package com.first.game;

import com.badlogic.gdx.Game;
import com.first.game.screens.InScreen;

public class Main extends Game {
    @Override
    public void create () { this.setScreen(new InScreen(this)); }

    @Override
    public void render () { super.render(); }

    @Override
    public void dispose () {
        //System.exit(0);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
