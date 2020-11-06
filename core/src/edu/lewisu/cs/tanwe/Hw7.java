package edu.lewisu.cs.tanwe;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class Hw7 extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera cam;
	Texture img;
	Texture background;
	float imgX, imgY; // world coordinates
	float imgWidth, imgHeight; // player dimensions
	float WIDTH, HEIGHT; // viewport dimensions
	float WORLDWIDTH, WORLDHEIGHT; // world dimensions
	float screenX, screenY; // player coordinates within the viewport
	LabelStyle labelStyle;
	Label label;
	Label jailLabel;
	boolean jailed = false;
	float jailX, jailY;
	float jailWidth, jailHeight;

	public void setupLabelStyle() {
		labelStyle = new LabelStyle();
		labelStyle.font = new BitmapFont(Gdx.files.internal("fonts/myfont.fnt"));
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		background = new Texture("zoomedinmap.png");
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		WORLDWIDTH = background.getWidth();
		WORLDHEIGHT = background.getHeight();
		imgX = 0;
		imgY = 0;
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH/2,HEIGHT/2);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		setupLabelStyle();
		label = new Label("Coordinates", labelStyle);
		label.setPosition(20, 400);
		jailLabel = new Label("Jailed", labelStyle);
		jailLabel.setPosition(20,20);
	}

	public void handleInput() {
		if (Gdx.input.isKeyPressed(Keys.W)) {
			imgY += 10;
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			imgX -= 10;
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			imgY -= 10;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			imgX += 10;
		}
		if (Gdx.input.isKeyJustPressed(Keys.J)) {
			jailed = true;
			jailWidth = (float)(1.5*imgWidth);
			jailHeight = (float)(1.5*imgHeight);
			jailX = (imgX+(imgWidth/2)-(jailWidth/2));
			jailY = (imgY+(imgHeight/2)-(jailHeight/2));
			System.out.println("JAILED");
		}
		if (Gdx.input.isKeyJustPressed(Keys.U)) {
			jailed = false;
			System.out.println("UNLOCKED");
		}
	}

	public Vector2 getViewPortOrigin() {
		return new Vector2(cam.position.x-WIDTH/2, cam.position.y - HEIGHT/2);
	}

	public Vector2 getScreenCoordinates() {
		Vector2 viewportOrigin = getViewPortOrigin();
		return new Vector2(imgX-viewportOrigin.x, imgY-viewportOrigin.y);
	}

	public void panCoordinates(float border) {
		Vector2 screenPos = getScreenCoordinates();
		if (jailed == false) {
			if (screenPos.x > WIDTH - imgWidth - border) {
				if (imgX + imgWidth > WORLDWIDTH - border) { // about to go off viewport
					wrapCoordinates(WORLDWIDTH,WORLDHEIGHT);
				} else {
					cam.position.x = cam.position.x + screenPos.x - WIDTH + imgWidth + border;
					cam.update();
					batch.setProjectionMatrix(cam.combined);
				}
			}
			if (screenPos.x < border) {
				if (imgX < border) {
					wrapCoordinates(WORLDWIDTH,WORLDHEIGHT);
				} else {
					cam.position.x = cam.position.x - (border - screenPos.x);
					cam.update();
					batch.setProjectionMatrix(cam.combined);
				}
			}
			if (screenPos.y > HEIGHT - imgHeight - border) {
				if (imgY + imgHeight > WORLDHEIGHT - border) {
					lockCoordinates(0,0,WORLDWIDTH,WORLDHEIGHT);
				} else {
					cam.position.y = cam.position.y + screenPos.y - HEIGHT + imgHeight + border;
					cam.update();
					batch.setProjectionMatrix(cam.combined);
				}
			}
			if (screenPos.y < border) {
				if (imgY < border) {
					lockCoordinates(0,0,WORLDWIDTH,WORLDHEIGHT);
				} else {
					cam.position.y = cam.position.y - (border - screenPos.y);
					cam.update();
					batch.setProjectionMatrix(cam.combined);
				}
			}
		}

		if (jailed == true) {
			if (imgX < jailX) {
				imgX = jailX;
			}
			if (imgX > ((jailX+jailWidth)-imgWidth)) {
				imgX = (jailX+jailWidth)-imgWidth;
			}
			if (imgY < jailY) {
				imgY = jailY;
			}
			if (imgY > ((jailY+jailHeight)-imgHeight)) {
				imgY = (jailY+jailHeight)-imgHeight;
			}
		}
	}

	public void wrapCoordinates(float targetWidth, float targetHeight) {
		if (imgX > targetWidth) {
			imgX = -imgWidth;
		} else if (imgX < -imgWidth) {
			imgX = targetWidth;
		}
		if (imgY > targetHeight) {
			imgY = -imgHeight;
		} else if (imgY < -imgHeight) {
			imgY = targetHeight;
		}
	}

	public void wrapCoordinates() {
		wrapCoordinates(WIDTH,HEIGHT);
	}

	public void lockCoordinates(float xLock, float yLock, float targetWidth, 
	float targetHeight) {
        if (imgX > targetWidth - imgWidth) {
            imgX = targetWidth - imgWidth;
        } else if (imgX < xLock) {
            imgX = xLock;
        }
        if (imgY > targetHeight - imgHeight) {
            imgY = targetHeight - imgHeight;
        } else if (imgY < yLock) {
            imgY = yLock;
		}   
	
	}

    public void lockCoordinates() {
		lockCoordinates(0,0,WIDTH, HEIGHT);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleInput();
		panCoordinates(20);
		label.setText("X = " + imgX + ", Y = " + imgY);
		label.setPosition(20+(cam.position.x-WIDTH/2),400+cam.position.y-HEIGHT/2);
		jailLabel.setText("JAILED");
		jailLabel.setPosition(20+(cam.position.x-WIDTH/2),20+cam.position.y-HEIGHT/2);
		batch.begin();
		batch.draw(background,0,0);
		batch.draw(img, imgX, imgY);
		label.draw(batch,1);
		if (jailed == true) {
			jailLabel.draw(batch,1);
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
