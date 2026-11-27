package game.traffic.entity;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import engine.gamestate.GameState;
import engine.gamestate.InputEvent;
import engine.gamestate.StateElement;
import game.traffic.entity.renderer.InfiniteGridRenderer;
import game.traffic.helper.Camera2D;

public final class InfiniteGrid implements StateElement {

// ======================================================================================================================================================

	public static final double ZOOM_STEP = 1.15;

	private static final double MOVE_SPEED = 480.0;
	private static final double FAST_MOVE_MULTIPLIER = 5.0;
	private static final double ROTATE_SPEED = Math.PI * 0.75;
	private static final double ROTATE_DRAG_SPEED = 0.006;

	private final Camera2D camera = new Camera2D();
	private final InfiniteGridRenderer renderer = new InfiniteGridRenderer(this.camera);
	private final Set<Integer> keysDown = new HashSet<Integer>();

	private boolean rightButtonDown;
	private boolean shiftDown;
	private int dragX;
	private int dragY;

// ======================================================================================================================================================

	@Override
	public void init(GameState parentState) {
		this.keysDown.clear();
		this.rightButtonDown = false;
		this.shiftDown = false;
		this.camera.reset();
	}

	@Override
	public void close() {
		this.keysDown.clear();
		this.rightButtonDown = false;
		this.shiftDown = false;
	}

// ======================================================================================================================================================

	@Override
	public void tick(double elapsedSecond, long loopID) {
		double moveX = 0.0;
		double moveY = 0.0;
		if (this.keysDown.contains(KeyEvent.VK_A)) {
			moveX -= 1.0;
		}
		if (this.keysDown.contains(KeyEvent.VK_D)) {
			moveX += 1.0;
		}
		if (this.keysDown.contains(KeyEvent.VK_W)) {
			moveY -= 1.0;
		}
		if (this.keysDown.contains(KeyEvent.VK_S)) {
			moveY += 1.0;
		}
		if (moveX != 0.0 || moveY != 0.0) {
			double length = Math.hypot(moveX, moveY);
			double speed = MOVE_SPEED * (this.shiftDown ? FAST_MOVE_MULTIPLIER : 1.0);
			this.camera.moveScreen(moveX / length * speed * elapsedSecond, moveY / length * speed * elapsedSecond);
		}

		if (this.keysDown.contains(KeyEvent.VK_Q)) {
			this.camera.rotate(-ROTATE_SPEED * elapsedSecond);
		}
		if (this.keysDown.contains(KeyEvent.VK_E)) {
			this.camera.rotate(ROTATE_SPEED * elapsedSecond);
		}
		this.camera.tick(elapsedSecond);
	}

// ======================================================================================================================================================

	@Override
	public void render(Graphics2D g, int renderWidth, int renderHeight) {
		this.renderer.render(g, renderWidth, renderHeight);
	}

// ======================================================================================================================================================

	@Override
	public void input(InputEvent event) {
		if (event.isKey()) {
			int keyCode = event.getKeyCode();
			if (keyCode == KeyEvent.VK_SHIFT) {
				this.shiftDown = event.getType() == InputEvent.Type.KEY_PRESSED;
			}
			if (event.getType() == InputEvent.Type.KEY_PRESSED) {
				this.keysDown.add(keyCode);
				if (keyCode == KeyEvent.VK_R) {
					this.camera.reset(true);
				} else if (keyCode == KeyEvent.VK_MINUS || keyCode == KeyEvent.VK_SUBTRACT) {
					this.camera.zoomBy(1.0 / InfiniteGrid.ZOOM_STEP);
				} else if (keyCode == KeyEvent.VK_PLUS || keyCode == KeyEvent.VK_EQUALS || keyCode == KeyEvent.VK_ADD) {
					this.camera.zoomBy(InfiniteGrid.ZOOM_STEP);
				}
				event.consume();
			} else if (event.getType() == InputEvent.Type.KEY_RELEASED) {
				this.keysDown.remove(keyCode);
				event.consume();
			}
			return;
		}

		if (event.getType() == InputEvent.Type.MOUSE_PRESSED) {
			if (event.getButton() == InputEvent.RIGHT) {
				this.rightButtonDown = true;
				this.dragX = event.getX();
				this.dragY = event.getY();
				event.consume();
			} else if (event.getButton() == InputEvent.MIDDLE) {
				this.camera.reset(true);
				event.consume();
			}
		} else if (event.getType() == InputEvent.Type.MOUSE_DRAGGED) {
			if (this.rightButtonDown) {
				int deltaX = event.getX() - this.dragX;
				int deltaY = event.getY() - this.dragY;
				this.dragX = event.getX();
				this.dragY = event.getY();
				if (this.shiftDown) {
					this.camera.rotate(deltaX * ROTATE_DRAG_SPEED);
				} else {
					double zoom = this.camera.getZoom();
					this.camera.moveScreen(-deltaX / zoom, -deltaY / zoom);
				}
				event.consume();
			}
		} else if (event.getType() == InputEvent.Type.MOUSE_RELEASED) {
			if (event.getButton() == InputEvent.RIGHT) {
				this.rightButtonDown = false;
				event.consume();
			}
		}
	}

// ======================================================================================================================================================

	public Camera2D getCamera() {
		return this.camera;
	}
}
