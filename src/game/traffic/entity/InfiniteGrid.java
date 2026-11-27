package game.traffic.entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import engine.gamestate.GameState;
import engine.gamestate.InputEvent;
import engine.gamestate.StateElement;
import game.traffic.helper.Camera2D;

public final class InfiniteGrid implements StateElement {

// ======================================================================================================================================================

	public static final int CELL_SIZE = 64;

	private static final double MOVE_SPEED = 480.0;
	private static final double ROTATE_SPEED = Math.PI * 0.75;
	private static final double ROTATE_DRAG_SPEED = 0.006;

	private static final Color GRID_COLOR = new Color(55, 58, 70);
	private static final Color AXIS_COLOR = new Color(110, 160, 220);
	private static final Color ORIGIN_COLOR = new Color(240, 130, 90);

	private final Camera2D camera = new Camera2D();
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
			this.camera.moveScreen(moveX / length * MOVE_SPEED * elapsedSecond, moveY / length * MOVE_SPEED * elapsedSecond);
		}

		if (this.keysDown.contains(KeyEvent.VK_Q)) {
			this.camera.rotate(-ROTATE_SPEED * elapsedSecond);
		}
		if (this.keysDown.contains(KeyEvent.VK_E)) {
			this.camera.rotate(ROTATE_SPEED * elapsedSecond);
		}
	}

// ======================================================================================================================================================

	@Override
	public void render(Graphics2D g, int renderWidth, int renderHeight) {
		Stroke previousStroke = g.getStroke();
		Color previousColor = g.getColor();

		double centerX = renderWidth / 2.0;
		double centerY = renderHeight / 2.0;

		Point2D.Double topLeft = this.camera.screenToWorld(0, 0, centerX, centerY);
		Point2D.Double topRight = this.camera.screenToWorld(renderWidth, 0, centerX, centerY);
		Point2D.Double bottomLeft = this.camera.screenToWorld(0, renderHeight, centerX, centerY);
		Point2D.Double bottomRight = this.camera.screenToWorld(renderWidth, renderHeight, centerX, centerY);

		double minX = Math.min(Math.min(topLeft.x, topRight.x), Math.min(bottomLeft.x, bottomRight.x));
		double maxX = Math.max(Math.max(topLeft.x, topRight.x), Math.max(bottomLeft.x, bottomRight.x));
		double minY = Math.min(Math.min(topLeft.y, topRight.y), Math.min(bottomLeft.y, bottomRight.y));
		double maxY = Math.max(Math.max(topLeft.y, topRight.y), Math.max(bottomLeft.y, bottomRight.y));

		g.setColor(GRID_COLOR);
		g.setStroke(new BasicStroke(1f));
		for (int column = (int) Math.floor(minX / CELL_SIZE); column <= (int) Math.ceil(maxX / CELL_SIZE); column++) {
			if (column == 0) {
				continue;
			}
			this.drawWorldLine(g, column * CELL_SIZE, minY, column * CELL_SIZE, maxY, centerX, centerY);
		}
		for (int row = (int) Math.floor(minY / CELL_SIZE); row <= (int) Math.ceil(maxY / CELL_SIZE); row++) {
			if (row == 0) {
				continue;
			}
			this.drawWorldLine(g, minX, row * CELL_SIZE, maxX, row * CELL_SIZE, centerX, centerY);
		}

		g.setColor(AXIS_COLOR);
		g.setStroke(new BasicStroke(2f));
		this.drawWorldLine(g, minX, 0.0, maxX, 0.0, centerX, centerY);
		this.drawWorldLine(g, 0.0, minY, 0.0, maxY, centerX, centerY);

		Point2D.Double origin = this.camera.worldToScreen(0.0, 0.0, centerX, centerY);
		g.setColor(ORIGIN_COLOR);
		g.fillOval((int) Math.round(origin.x - 4), (int) Math.round(origin.y - 4), 9, 9);

		g.setStroke(previousStroke);
		g.setColor(previousColor);
	}

	private void drawWorldLine(Graphics2D g, double x1, double y1, double x2, double y2, double centerX, double centerY) {
		Point2D.Double start = this.camera.worldToScreen(x1, y1, centerX, centerY);
		Point2D.Double end = this.camera.worldToScreen(x2, y2, centerX, centerY);
		g.drawLine(
			(int) Math.round(start.x), (int) Math.round(start.y),
			(int) Math.round(end.x), (int) Math.round(end.y)
		);
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
					this.camera.reset();
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
				this.camera.reset();
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
					this.camera.moveScreen(-deltaX, -deltaY);
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
