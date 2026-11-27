package game.traffic.entity.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import game.traffic.helper.Camera2D;

public final class InfiniteGridRenderer {

// ======================================================================================================================================================

	public static final int CELL_SIZE = 64;

	private static final Color GRID_COLOR = new Color(55, 58, 70);
	private static final Color AXIS_COLOR = new Color(110, 160, 220);
	private static final Color ORIGIN_COLOR = new Color(240, 130, 90);

	private final Camera2D camera;

// ======================================================================================================================================================

	public InfiniteGridRenderer(Camera2D camera) {
		this.camera = camera;
	}

// ======================================================================================================================================================

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
}
