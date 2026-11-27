package game.traffic.helper;

import java.awt.geom.Point2D;

public final class Camera2D {

// ======================================================================================================================================================

	private static final double TWO_PI = Math.PI * 2.0;

	private double x;
	private double y;
	private double rotation;

// ======================================================================================================================================================

	public Camera2D() {
		this.reset();
	}

// ======================================================================================================================================================

	public void reset() {
		this.x = 0.0;
		this.y = 0.0;
		this.rotation = 0.0;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getRotation() {
		return this.rotation;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setRotation(double rotation) {
		this.rotation = Camera2D.normalize(rotation);
	}

// ======================================================================================================================================================

	/**
	 * Moves the camera by a screen-space offset, so navigation stays intuitive
	 * even when the view is rotated.
	 */
	public void moveScreen(double dx, double dy) {
		double cos = Math.cos(this.rotation);
		double sin = Math.sin(this.rotation);
		this.x += dx * cos + dy * sin;
		this.y += -dx * sin + dy * cos;
	}

	public void rotate(double delta) {
		this.rotation = Camera2D.normalize(this.rotation + delta);
	}

// ======================================================================================================================================================

	public Point2D.Double worldToScreen(double worldX, double worldY, double centerX, double centerY) {
		double dx = worldX - this.x;
		double dy = worldY - this.y;
		double cos = Math.cos(this.rotation);
		double sin = Math.sin(this.rotation);
		return new Point2D.Double(centerX + dx * cos - dy * sin, centerY + dx * sin + dy * cos);
	}

	public Point2D.Double screenToWorld(double screenX, double screenY, double centerX, double centerY) {
		double dx = screenX - centerX;
		double dy = screenY - centerY;
		double cos = Math.cos(this.rotation);
		double sin = Math.sin(this.rotation);
		return new Point2D.Double(this.x + dx * cos + dy * sin, this.y - dx * sin + dy * cos);
	}

// ======================================================================================================================================================

	private static double normalize(double angle) {
		double normalized = angle % Camera2D.TWO_PI;
		if (normalized > Math.PI) {
			normalized -= Camera2D.TWO_PI;
		} else if (normalized < -Math.PI) {
			normalized += Camera2D.TWO_PI;
		}
		return normalized;
	}
}
