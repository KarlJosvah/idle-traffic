package game.traffic.entity.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import game.traffic.entity.House;
import game.traffic.helper.Camera2D;

public final class HouseRenderer {

// ======================================================================================================================================================

	private static final Color OUTLINE_COLOR = new Color(22, 24, 30);
	private static final Color SELECTED_COLOR = new Color(255, 205, 80);
	private static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

// ======================================================================================================================================================

	public static void render(Graphics2D g, House house, Camera2D camera, int renderWidth, int renderHeight, boolean selected) {
		Stroke previousStroke = g.getStroke();
		Color previousColor = g.getColor();
		Font previousFont = g.getFont();

		double centerX = renderWidth / 2.0;
		double centerY = renderHeight / 2.0;
		double half = house.getSize() * 0.5;

		Point2D.Double[] corners = new Point2D.Double[] {
			HouseRenderer.worldCornerToScreen(house, camera, -half, -half, centerX, centerY),
			HouseRenderer.worldCornerToScreen(house, camera, half, -half, centerX, centerY),
			HouseRenderer.worldCornerToScreen(house, camera, half, half, centerX, centerY),
			HouseRenderer.worldCornerToScreen(house, camera, -half, half, centerX, centerY)
		};

		double minX = Math.min(corners[0].x, corners[2].x);
		double maxX = Math.max(corners[0].x, corners[2].x);
		double minY = Math.min(corners[0].y, corners[2].y);
		double maxY = Math.max(corners[0].y, corners[2].y);
		if (maxX < -128 || minX > renderWidth + 128 || maxY < -128 || minY > renderHeight + 128) {
			g.setStroke(previousStroke);
			g.setColor(previousColor);
			g.setFont(previousFont);
			return;
		}

		int[] xs = new int[4];
		int[] ys = new int[4];
		for (int i = 0; i < 4; i++) {
			xs[i] = (int) Math.round(corners[i].x);
			ys[i] = (int) Math.round(corners[i].y);
		}

		g.setColor(house.getColor());
		g.fillPolygon(xs, ys, 4);
		g.setColor(selected ? SELECTED_COLOR : OUTLINE_COLOR);
		g.setStroke(new BasicStroke(selected ? 3f : 1.5f));
		g.drawPolygon(xs, ys, 4);

		g.setFont(LABEL_FONT);
		FontMetrics metrics = g.getFontMetrics();
		double topX = (corners[0].x + corners[1].x) * 0.5;
		double topY = (corners[0].y + corners[1].y) * 0.5;
		g.setColor(selected ? SELECTED_COLOR : new Color(210, 216, 228));
		g.drawString(house.getName(), (int) Math.round(topX - metrics.stringWidth(house.getName()) * 0.5), (int) Math.round(topY - 8));

		g.setStroke(previousStroke);
		g.setColor(previousColor);
		g.setFont(previousFont);
	}

	private static Point2D.Double worldCornerToScreen(House house, Camera2D camera, double dx, double dy, double centerX, double centerY) {
		double cos = Math.cos(house.getRotation());
		double sin = Math.sin(house.getRotation());
		double worldX = house.getX() + dx * cos - dy * sin;
		double worldY = house.getY() + dx * sin + dy * cos;
		return camera.worldToScreen(worldX, worldY, centerX, centerY);
	}
}
