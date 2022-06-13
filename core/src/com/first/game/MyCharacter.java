package com.first.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MyCharacter {
    private AnimPlayer idle, jump, walkRight;
    private boolean isJump, isWalk, dir;
    private Vector2 pos;
    private Rectangle rect;

    public MyCharacter() {
        idle = new AnimPlayer("hero/idle.png", 1, 1, 16.0f, Animation.PlayMode.LOOP);
        jump = new AnimPlayer("hero/jump.png", 1, 1, 16.0f, Animation.PlayMode.LOOP);
        walkRight = new AnimPlayer("hero/runRight.png", 4, 1, 16.0f, Animation.PlayMode.LOOP);
        pos = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        rect = new Rectangle(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2,walkRight.getFrame().getRegionWidth(),walkRight.getFrame().getRegionHeight());
    }

    public void setWalk(boolean walk) {isWalk = walk;}

    public void setDir(boolean dir) {this.dir = dir;}

    public TextureRegion getFrame(){
        TextureRegion tmpTex = null;
        if (!isJump && !isWalk && !dir) {
            idle.step(Gdx.graphics.getDeltaTime());
            if (idle.getFrame().isFlipX()) idle.getFrame().flip(true, false);
            tmpTex = idle.getFrame();
        } else if (!isJump && !isWalk && dir) {
            idle.step(Gdx.graphics.getDeltaTime());
            if (!idle.getFrame().isFlipX()) idle.getFrame().flip(true, false);
            tmpTex = idle.getFrame();
        } else  if (!isJump && isWalk && !dir) {
            walkRight.step(Gdx.graphics.getDeltaTime());
            if (walkRight.getFrame().isFlipX()) walkRight.getFrame().flip(true, false);
            tmpTex = walkRight.getFrame();
        } else  if (!isJump && isWalk && dir) {
            walkRight.step(Gdx.graphics.getDeltaTime());
            if (!walkRight.getFrame().isFlipX()) walkRight.getFrame().flip(true, false);
            tmpTex = walkRight.getFrame();
        }
        return tmpTex;
    }

    public Vector2 getPos() {return pos;}

    public Rectangle getRect() {return rect;}
}
