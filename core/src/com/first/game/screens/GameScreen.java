package com.first.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.first.game.Coin;
import com.first.game.Label;
import com.first.game.MyCharacter;
import com.first.game.PhysX;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private Label label;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private List<Coin> coinList;
    private Texture fon;
    private MyCharacter chip;
    private PhysX physX;
    private Music music;
    private int[] foreGround, backGround;
    private int score;
    private boolean start = true;
    final Game game;
    ShaderProgram grayProgram, normalProgram, invertProgram;
    private float timeOfDay;

    public GameScreen(Game game){
        this.game = game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        physX = new PhysX();

        chip = new MyCharacter();
        fon = new Texture("fon.png");
        map = new TmxMapLoader().load("maps/map1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        RectangleMapObject o = (RectangleMapObject) map.getLayers().get("Слой объектов 2").getObjects().get("camera");

        if (map.getLayers().get("land") != null) {
            MapObjects mo = map.getLayers().get("land").getObjects();
            physX.addObjects(mo);
        }
        MapObject mo1 = map.getLayers().get("Слой объектов 2").getObjects().get("hero");
        physX.addObject(mo1, chip.getRect(camera));
        System.out.printf(""+physX.barrelInit());

        foreGround = new int[1];
        foreGround[0] = map.getLayers().getIndex("Слой тайлов 2");
        backGround = new int[1];
        backGround[0] = map.getLayers().getIndex("Слой тайлов 1");

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        label = new Label(50);

        coinList = new ArrayList<>();
        MapLayer ml = map.getLayers().get("coins");
        if (ml != null){
            MapObjects mo = ml.getObjects();
            if (mo.getCount()>0){
                for (int i=0;i<mo.getCount();i++){
                    RectangleMapObject tmpMo = (RectangleMapObject) ml.getObjects().get(i);
                    Rectangle rect = tmpMo.getRectangle();
                    coinList.add(new Coin(new Vector2(rect.x,rect.y)));
                }
            }
        }

        music = Gdx.audio.newMusic(Gdx.files.internal("Soundtracks — Чип и Дейл (Disney, 1989) (www.lightaudio.ru).mp3"));
        music.setLooping(true);
        music.setVolume(0.25f);
        //music.play();

        camera.zoom = 0.25f;

        String VERTEX = Gdx.files.internal("shaders/grayscale/VERTEX").readString();
        String FRAGMENT = Gdx.files.internal("shaders/grayscale/FRAGMENT").readString();
        grayProgram = new ShaderProgram(VERTEX, FRAGMENT);
        VERTEX = Gdx.files.internal("shaders/normal/VERTEX").readString();
        FRAGMENT = Gdx.files.internal("shaders/normal/FRAGMENT").readString();
        normalProgram = new ShaderProgram(VERTEX, FRAGMENT);
        VERTEX = Gdx.files.internal("shaders/invert/VERTEX").readString();
        FRAGMENT = Gdx.files.internal("shaders/invert/FRAGMENT").readString();
        invertProgram = new ShaderProgram(VERTEX, FRAGMENT);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.app.log("zoom", String.valueOf(camera.zoom));

        ScreenUtils.clear(0, 0, 0, 1);

        boolean left=false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.getX()<Gdx.graphics.getWidth()/2)) left = true;

        chip.setWalk(false);
        if (left) {
            physX.setHeroForce(new Vector2(-1500, 0));
            chip.setDir(true);
            chip.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            physX.setHeroForce(new Vector2(1500, 0));
            chip.setDir(false);
            chip.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && physX.cl.isOnGround()) {
            physX.setHeroForce(new Vector2(0, 2500));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y--;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {start=true;}

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {camera.zoom += 0.05f;}
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {camera.zoom -= 0.05f;}

        camera.position.x = physX.getHero().getPosition().x;
        camera.position.y = physX.getHero().getPosition().y;
        camera.update();

        timeOfDay += Gdx.graphics.getDeltaTime();
        batch.begin();
        batch.setShader(grayProgram);
        grayProgram.setUniformf("weight", MathUtils.sin(timeOfDay)+1.0f);
        batch.draw(fon, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setShader(normalProgram);
        batch.end();


        mapRenderer.setView(camera);
        mapRenderer.render(backGround);

        batch.begin();
        batch.setShader(invertProgram);
        batch.draw(chip.getFrame(), chip.getRect(camera).x, chip.getRect(camera).y, chip.getRect(camera).getWidth(), chip.getRect(camera).getHeight());
        batch.setShader(normalProgram);
        label.draw(batch, "Монеток собрано: "+String.valueOf(score), 0, 0);

        for (int i=0;i<coinList.size();i++){
            int state;
            state = coinList.get(i).draw(batch, camera);
            if (coinList.get(i).isOverlaps(chip.getRect(camera), camera)) {
                if (state==0)coinList.get(i).setState();
                if (state==2){
                    coinList.remove(i);
                    score++;
//                    if (score > 14) {
//                        dispose();
//                        game.setScreen(new InScreen(game));
//                    }
                }
            }
        }
        batch.end();

        if (start) physX.step();
        physX.debugDraw(camera);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        for (Coin coin: coinList) {
            coin.shapeDraw(renderer, camera);
        }
        chip.shapeDraw(renderer, camera);
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.CORAL);
        for (Fixture fixture: physX.barrelBodys) {
            float cx = (fixture.getBody().getPosition().x - camera.position.x)/camera.zoom + Gdx.graphics.getWidth()/2;
            float cy = (fixture.getBody().getPosition().y - camera.position.y)/camera.zoom + Gdx.graphics.getHeight()/2;
            float cR = fixture.getShape().getRadius() / camera.zoom;
            renderer.circle(cx, cy, cR);
        }
        renderer.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        coinList.get(0).dispose();
        physX.dispose();
        music.stop();
        music.dispose();
    }
}
