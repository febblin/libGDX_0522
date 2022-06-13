package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class MainClass extends ApplicationAdapter {
	private SpriteBatch batch;
	//private ShapeRenderer renderer;
	private Label label;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private OrthographicCamera camera;
	private List<Coin> coinList;
	private Texture fon;
	private MyCharacter chip;
	private PhysX physX;

	private int[] foreGround, backGround;

	private int score;
	private boolean start;
	
	@Override
	public void create () {
		chip = new MyCharacter();
		fon = new Texture("fon.png");
		map = new TmxMapLoader().load("maps/map1.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		physX = new PhysX();
		if (map.getLayers().get("land") != null) {
			MapObjects mo = map.getLayers().get("land").getObjects();
			physX.addObjects(mo);
			MapObject mo1 = map.getLayers().get("Слой объектов 2").getObjects().get("hero");
			physX.addObject(mo1);
		}

		foreGround = new int[1];
		foreGround[0] = map.getLayers().getIndex("Слой тайлов 2");
		backGround = new int[1];
		backGround[0] = map.getLayers().getIndex("Слой тайлов 1");

		batch = new SpriteBatch();
		//renderer = new ShapeRenderer();

		label = new Label(50);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		RectangleMapObject o = (RectangleMapObject) map.getLayers().get("Слой объектов 2").getObjects().get("camera");

		camera.position.x = physX.getHero().getPosition().x;
		camera.position.y = physX.getHero().getPosition().y;
		camera.zoom = 0.5f;
		camera.update();

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

	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);

		chip.setWalk(false);
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			physX.setHeroForce(new Vector2(-3000, 0));
			chip.setDir(true);
			chip.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			physX.setHeroForce(new Vector2(3000, 0));
			chip.setDir(false);
			chip.setWalk(true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			physX.setHeroForce(new Vector2(0, 1300));
		}
//		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y--;
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {start=true;}

		camera.position.x = physX.getHero().getPosition().x;
		camera.position.y = physX.getHero().getPosition().y;
		camera.update();

//		batch.begin();
//		batch.draw(fon, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		batch.end();

		mapRenderer.setView(camera);
		mapRenderer.render(backGround);

		batch.begin();
		batch.draw(chip.getFrame(), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		label.draw(batch, "Монеток собрано: "+String.valueOf(score), 0, 0);

		for (int i=0;i<coinList.size();i++){
			coinList.get(i).draw(batch, camera);
			if (coinList.get(i).isOverlaps(chip.getRect(), camera)) {
				coinList.remove(i);
				score++;
			}
		}
		batch.end();

		if (start) physX.step();
		physX.debugDraw(camera);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		coinList.get(0).dispose();
		physX.dispose();
	}
}
