package com.freefallingball.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class FreeFallingBallGame extends ApplicationAdapter {
    private Texture ballImage;
	private Texture obstacleImage;
    private Sound levelup;
    private Sound impact;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle ball;
    private Array<Rectangle> obstacles;
    private long lastObstacleTime;

	
	@Override
	public void create () {
		ballImage = new Texture(Gdx.files.internal("redBall.jpg"));
		obstacleImage = new Texture(Gdx.files.internal("obstacle.png"));

		impact = Gdx.audio.newSound(Gdx.files.internal("ballimpact.wav"));
		levelup = Gdx.audio.newSound(Gdx.files.internal("bell.wav"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);

		batch = new SpriteBatch();

		ball = new Rectangle();
		ball.x = 480 / 2 - 128 / 2;
		ball.y = 800 / 2;
		ball.width = 128;
		ball.height = 128;

		obstacles = new Array<Rectangle>();
		spawnObstacle();

	}

	private void spawnObstacle() {
		Rectangle obstacle = new Rectangle();
		obstacle.x = MathUtils.random(0, 480 - 256);
		obstacle.y = 0;
		obstacle.width = 256;
		obstacle.height = 256;
		obstacles.add(obstacle);
		lastObstacleTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 1, 1, .3f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(ballImage, ball.x, ball.y);
		for (Rectangle obstacle: obstacles) {
			batch.draw(obstacleImage, obstacle.x, obstacle.y);
		}
		batch.end();

		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			ball.x = touchPos.x - 128 / 2;
		}

		if(ball.x < 0) ball.x = 0;
		if(ball.x > 480 - 128) ball.x = 480 - 128;

		if(TimeUtils.nanoTime() - lastObstacleTime > 1000000000) spawnObstacle();

		for (Iterator<Rectangle> off = obstacles.iterator(); off.hasNext();) {
			Rectangle obstacle = off.next();
			obstacle.y += 50 * Gdx.graphics.getDeltaTime();
			if(obstacle.y - 256 > 800) off.remove();
			if(obstacle.overlaps(ball)) {
				impact.play();
				break;
			}
		}

	}
	
	@Override
	public void dispose () {
		ballImage.dispose();
		obstacleImage.dispose();
		levelup.dispose();
		impact.dispose();
		batch.dispose();
	}
}
