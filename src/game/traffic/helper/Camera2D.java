package game.traffic.helper;

import java.awt.geom.Point2D;

public final class Camera2D {

// ======================================================================================================================================================

	private static final double TWO_PI = Math.PI * 2.0;

	private static final double SMOOTH_RESET_RATE = 6.0;
	private static final double POSITION_EPSILON = 0.01;
	private static final double ROTATION_EPSILON = 0.001;
	private static final double ZOOM_EPSILON = 0.001;

	public static final double MIN_ZOOM = 0.35;
	public static final double MAX_ZOOM = 3.0;

	private double x;
	private double y;
	private double rotation;
	private double zoom;
	private boolean smoothReset;

// ======================================================================================================================================================

	public Camera2D() {
		this.reset();
	}

// ======================================================================================================================================================

	public void reset() {
		this.reset(false);
	}

	/**
	 * Resets to (0, 0), rotation 0 and zoom 1. When smooth is true, the values
	 * ease toward those targets over subsequent tick(...) calls instead of
	 * snapping there.
	 */
	public void reset(boolean smooth) {
		if (!smooth) {
			this.smoothReset = false;
			this.x = 0.0;
			this.y = 0.0;
			this.rotation = 0.0;
			this.zoom = 1.0;
			return;
		}
		this.smoothReset = this.x != 0.0 || this.y != 0.0 || this.rotation != 0.0 || this.zoom != 1.0;
	}

	public void tick(double elapsedSecond) {
		if (!this.smoothReset) {
			return;
		}

		double factor = 1.0 - Math.exp(-Camera2D.SMOOTH_RESET_RATE * Math.max(0.0, elapsedSecond));
		this.x -= this.x * factor;
		this.y -= this.y * factor;

		double rotationDelta = Camera2D.normalize(-this.rotation);
		this.rotation = Camera2D.normalize(this.rotation + rotationDelta * factor);
		this.zoom += (1.0 - this.zoom) * factor;

		if (Math.abs(this.x) < Camera2D.POSITION_EPSILON
			&& Math.abs(this.y) < Camera2D.POSITION_EPSILON
			&& Math.abs(rotationDelta) < Camera2D.ROTATION_EPSILON
			&& Math.abs(this.zoom - 1.0) < Camera2D.ZOOM_EPSILON
		) {
			this.reset(false);
		}
	}

	public boolean isSmoothResetting() {
		return this.smoothReset;
	}

// ======================================================================================================================================================

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getRotation() {
		return this.rotation;
	}

	public double getZoom() {
		return this.zoom;
	}

	public void setPosition(double x, double y) {
		this.smoothReset = false;
		this.x = x;
		this.y = y;
	}

	public void setRotation(double rotation) {
		this.smoothReset = false;
		this.rotation = Camera2D.normalize(rotation);
	}

	public void setZoom(double zoom) {
		this.smoothReset = false;
		this.zoom = Camera2D.clampZoom(zoom);
	}

// ======================================================================================================================================================

	/**
	 * Moves the camera by a screen-space offset, so navigation stays intuitive
	 * even when the view is rotated.
	 */
	public void moveScreen(double dx, double dy) {
		this.smoothReset = false;
		double cos = Math.cos(this.rotation);
		double sin = Math.sin(this.rotation);
		this.x += dx * cos + dy * sin;
		this.y += -dx * sin + dy * cos;
	}

	public void rotate(double delta) {
		this.smoothReset = false;
		this.rotation = Camera2D.normalize(this.rotation + delta);
	}

// ======================================================================================================================================================

	public void zoomBy(double factor) {
		this.setZoom(this.zoom * factor);
	}

	/** Zooms while keeping the world point under the given screen pixel fixed. */
	public void zoomAt(double factor, double screenX, double screenY, double centerX, double centerY) {
		this.smoothReset = false;
		Point2D.Double before = this.screenToWorld(screenX, screenY, centerX, centerY);
		double previousZoom = this.zoom;
		this.zoom = Camera2D.clampZoom(this.zoom * factor);
		if (this.zoom == previousZoom) {
			return;
		}
		Point2D.Double after = this.screenToWorld(screenX, screenY, centerX, centerY);
		this.x += before.x - after.x;
		this.y += before.y - after.y;
	}

// ======================================================================================================================================================

	public Point2D.Double worldToScreen(double worldX, double worldY, double centerX, double centerY) {
		double dx = worldX - this.x;
		double dy = worldY - this.y;
		double cos = Math.cos(this.rotation);
		double sin = Math.sin(this.rotation);
		return new Point2D.Double(
			centerX + (dx * cos - dy * sin) * this.zoom,
			centerY + (dx * sin + dy * cos) * this.zoom
		);
	}

	public Point2D.Double screenToWorld(double screenX, double screenY, double centerX, double centerY) {
		double dx = (screenX - centerX) / this.zoom;
		double dy = (screenY - centerY) / this.zoom;
		double cos = Math.cos(this.rotation);
		double sin = Math.sin(this.rotation);
		return new Point2D.Double(this.x + dx * cos + dy * sin, this.y - dx * sin + dy * cos);
	}

// ======================================================================================================================================================

	private static double clampZoom(double zoom) {
		return Math.max(Camera2D.MIN_ZOOM, Math.min(Camera2D.MAX_ZOOM, zoom));
	}

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
