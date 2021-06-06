package com.kompetensum.minimalism;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MainScreen implements Screen {

	private Game game;
	private GameInput input;
	
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	
	private Vector2 position = new Vector2();
	private Vector2 velocity = new Vector2();
	private Vector2 currentVelocity = new Vector2();
	private Texture playerTexture;
	private Animation player;
	private float stateTime = 0;
	private float playerWidth;
	private float playerHeight;
	
	private static final float MAX_VELOCITY = 14.0f;
	private static final float MIN_VELOCITY = 0.2f;
	private static final float INC_VELOCITY = 1.0f;
	private static final float DEC_VELOCITY = 0.99f;
	
	private float redOpacity = 0.0f;
	private float greenOpacity = 0.0f;
	private float blueOpacity = 0.0f;
	
	class Dust {
		Vector2 position = new Vector2();
		Vector2 velocity = new Vector2();
		
		Color color = new Color();
		
		Sound sound;
	}
	
	private Array<Dust> dustList = new Array<Dust>();
	private boolean gameRunning;
	private static final int NUM_DUST_RED = 15;
	private static final int NUM_DUST_GREEN = 15;
	private static final int NUM_DUST_BLUE = 15;
	
	public MainScreen(Game game) {
		this.game = game;
		
		playerTexture = new Texture("data/player.png"); 
		TextureRegion[] regions = TextureRegion.split(playerTexture, 32, 32)[0];
		player = new Animation(0.29f, regions[0], regions[1], regions[2]);
		player.setPlayMode(Animation.LOOP_PINGPONG);
		
		playerWidth = 1 / 32f * regions[0].getRegionWidth();
		playerHeight = 1 / 32f * regions[0].getRegionHeight();
		
		map = new TmxMapLoader().load("data/level1.tmx");
		map.getLayers().get("Red").setOpacity(redOpacity);
		map.getLayers().get("Green").setOpacity(greenOpacity);
		map.getLayers().get("Blue").setOpacity(blueOpacity);
				
		renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);
		
		shapeRenderer = new ShapeRenderer();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 20);
		camera.update();
		
		input = new GameInput();
		Gdx.input.setInputProcessor(input);
		
		
		Random rnd = new Random();

		// Startpos
		position.x = rnd.nextInt(60);
		position.y = rnd.nextInt(60);
		
		// 100 red
		for (int i = 0; i < NUM_DUST_RED; i++) {
			Dust d = new Dust();
			d.position.x = rnd.nextFloat() * 60;
			d.position.y = rnd.nextFloat() * 60;
			d.velocity.x = rnd.nextFloat() * 2;
			d.velocity.y = rnd.nextFloat() * 2;
			d.color.set(1.0f, 0, 0, 1.0f);
			d.sound = Sounds.hits.random();
			dustList.add(d);
		}

		// 100 green
		for (int i = 0; i < NUM_DUST_GREEN; i++) {
			Dust d = new Dust();
			d.position.x = rnd.nextFloat() * 60;
			d.position.y = rnd.nextFloat() * 60;
			d.velocity.x = rnd.nextFloat() * 2;
			d.velocity.y = rnd.nextFloat() * 2;
			d.color.set(0f, 1.0f, 0, 1.0f);
			d.sound = Sounds.hits.random();
			dustList.add(d);
		}

		// 100 blue
		for (int i = 0; i < NUM_DUST_BLUE; i++) {
			Dust d = new Dust();
			d.position.x = rnd.nextFloat() * 60;
			d.position.y = rnd.nextFloat() * 60;
			d.velocity.x = rnd.nextFloat() * 2;
			d.velocity.y = rnd.nextFloat() * 2;
			d.color.set(0f, 0, 1.0f, 1.0f);
			d.sound = Sounds.hits.random();
			dustList.add(d);
		}

		gameRunning = true;
	}
	
	@Override
	public void render(float delta) {
		if (gameRunning) {
			renderRunning(delta);
		} else {
			renderEnd(delta);
		}
	}
	
	public void renderEnd(float delta) {
		// clear the screen
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// get the delta time
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		float dx = 30 - position.x;
		float dy = 30 - position.y;
		
		currentVelocity.x = dx / 2;
		currentVelocity.y = dy / 2;
		currentVelocity.scl(deltaTime);
		
		position.add(currentVelocity);
		
		if (Math.abs(dx) < 0.04f && Math.abs(dy) < 0.04f) {
			camera.zoom += 0.03f;
		}

		camera.position.x = position.x;
		camera.position.y = position.y;
		camera.update();

		map.getLayers().get("Red").setOpacity(1.0f);
		map.getLayers().get("Green").setOpacity(1.0f);
		map.getLayers().get("Blue").setOpacity(1.0f);
		
		renderer.setView(camera);
		renderer.render();
		
		TextureRegion frame = null;

		stateTime += deltaTime;
		frame = player.getKeyFrame(stateTime);
		
		SpriteBatch batch = renderer.getSpriteBatch();
		batch.begin();
		batch.draw(frame, position.x, position.y, playerWidth, playerHeight);
		batch.end();

		shapeRenderer.setProjectionMatrix(camera.combined);
//		shapeRenderer.setTransformMatrix(camera.combined);
		
		if (camera.zoom > 30.0f) {
			game.setScreen(new GameOverScreen(game));
		}
		
	}
	
	public void renderRunning(float delta) {
		// clear the screen
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// get the delta time
		float deltaTime = Gdx.graphics.getDeltaTime();
		boolean xVel = false;
		boolean yVel = false;

		if (input.keysPressed[GameInput.DROP]) {
//			gameRunning = false;
		}
	
		if (dustList.size == 0) {
			gameRunning = false;
		}
		
		if (input.keysPressed[GameInput.LEFT]) {
			velocity.x -= INC_VELOCITY;
			xVel = true;
		}
		
		if (input.keysPressed[GameInput.RIGHT]) {
			velocity.x += INC_VELOCITY;
			xVel = true;
		}
		
		if (input.keysPressed[GameInput.UP]) {
			velocity.y += INC_VELOCITY;
			yVel = true;
		}
		
		if (input.keysPressed[GameInput.DOWN]) {
			velocity.y -= INC_VELOCITY;
			yVel = true;
		}
		
		if (!xVel) {
			velocity.x *= DEC_VELOCITY;
		}

		if (!yVel) {
			velocity.y *= DEC_VELOCITY;
		}

		if (velocity.x > MAX_VELOCITY) {
			velocity.x = MAX_VELOCITY;
		}
		
		if (velocity.x < -MAX_VELOCITY) {
			velocity.x = -MAX_VELOCITY;
		}

		if (velocity.y > MAX_VELOCITY) {
			velocity.y = MAX_VELOCITY;
		}
		
		if (velocity.y < -MAX_VELOCITY) {
			velocity.y = -MAX_VELOCITY;
		}
		
		if (Math.abs(velocity.x) < MIN_VELOCITY) {
			velocity.x = 0;
		}

		if (Math.abs(velocity.y) < MIN_VELOCITY) {
			velocity.y = 0;
		}

		currentVelocity.x = velocity.x;
		currentVelocity.y = velocity.y;
		currentVelocity.scl(deltaTime);
		
		position.add(currentVelocity);
		
		if (position.x < 0) {
			position.x = 0;
			velocity.x = -velocity.x;
			Sounds.player_hit_wall.play(0.5f);
		}

		if (position.x > 59) {
			position.x = 59;
			velocity.x = -velocity.x;
			Sounds.player_hit_wall.play(0.5f);
		}

		if (position.y < 0) {
			position.y = 0;
			velocity.y = -velocity.y;
			Sounds.player_hit_wall.play(0.5f);
		}

		if (position.y > 59) {
			position.y = 59;
			velocity.y = -velocity.y;
			Sounds.player_hit_wall.play(0.5f);
		}

		camera.position.x = position.x;
		camera.position.y = position.y;
		camera.update();

		updateDust(deltaTime);

		map.getLayers().get("Red").setOpacity(redOpacity);
		map.getLayers().get("Green").setOpacity(greenOpacity);
		map.getLayers().get("Blue").setOpacity(blueOpacity);
		
		renderer.setView(camera);
		renderer.render();
		
		TextureRegion frame = null;

		stateTime += deltaTime;
		frame = player.getKeyFrame(stateTime);
		
		SpriteBatch batch = renderer.getSpriteBatch();
		batch.begin();
		batch.draw(frame, position.x, position.y, playerWidth, playerHeight);
		batch.end();

		shapeRenderer.setProjectionMatrix(camera.combined);
//		shapeRenderer.setTransformMatrix(camera.combined);

		renderDust();
		
	}

	private void renderDust() {
		shapeRenderer.begin(ShapeType.Filled);
		for (Dust d : dustList) {
			shapeRenderer.setColor(d.color);
			shapeRenderer.circle(d.position.x,d.position.y,0.2f);
		}
		shapeRenderer.end();
	}

	private void updateDust(float deltaTime) {
		for (Dust d : dustList) {
			currentVelocity.x = d.velocity.x;
			currentVelocity.y = d.velocity.y;
			currentVelocity.scl(deltaTime);
			d.position.add(currentVelocity);
		
			if (d.position.x < 0) {
				d.position.x = 0;
				d.velocity.x = -d.velocity.x;
				d.sound.play(0.01f,1,1);
			}

			if (d.position.x > 60) {
				d.position.x = 60;
				d.velocity.x = -d.velocity.x;
				d.sound.play(0.01f,1,-1);
			}

			if (d.position.y < 0) {
				d.position.y = 0;
				d.velocity.y = -d.velocity.y;
				d.sound.play(0.01f,1,1 - d.position.x/60/2);
			}

			if (d.position.y > 60) {
				d.position.y = 60;
				d.velocity.y = -d.velocity.y;
				d.sound.play(0.01f,1,1 - d.position.x/60/2);
			}
			
			float dx = Math.abs(position.x - d.position.x);
			float dy = Math.abs(position.y - d.position.y);
			if (dx < 0.5f && dy < 0.5f) {
				redOpacity += 1.0 / NUM_DUST_RED * d.color.r; 
				greenOpacity += 1.0 / NUM_DUST_RED * d.color.g; 
				blueOpacity += 1.0 / NUM_DUST_RED * d.color.b;
				d.sound.play(0.1f);
				dustList.removeValue(d, true);
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
