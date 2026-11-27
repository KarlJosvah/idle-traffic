package game.traffic.entity;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class House {

// ======================================================================================================================================================

	public enum Type {
		SMALL,
		MEDIUM,
		LARGE;
	}

	public static final int COLOR_MIN = 80;
	public static final int COLOR_MAX = 220;
	public static final int COLOR_SPREAD = 50;

	private final Type type;
	private String name;
	private double x;
	private double y;
	private double rotation;
	private double size;
	private Color color;

// ======================================================================================================================================================

	public House(Type type, String name, double x, double y, double rotation, double size, Color color) {
		this.type = type;
		this.name = name;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.size = size;
		this.color = color;
	}

// ======================================================================================================================================================

	public Type getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public Point2D.Double getPosition() {
		return new Point2D.Double(this.x, this.y);
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getRotation() {
		return this.rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getSize() {
		return this.size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

// ======================================================================================================================================================

	public boolean contains(double worldX, double worldY) {
		double dx = worldX - this.x;
		double dy = worldY - this.y;
		double cos = Math.cos(-this.rotation);
		double sin = Math.sin(-this.rotation);
		double localX = dx * cos - dy * sin;
		double localY = dx * sin + dy * cos;
		double half = this.size * 0.5;
		return Math.abs(localX) <= half && Math.abs(localY) <= half;
	}

// ======================================================================================================================================================

	public static Color randomColor() {
		return House.randomColor(ThreadLocalRandom.current());
	}

	/**
	 * Picks a low and high bound inside [80, 220] with at most 50 between them,
	 * then picks each RGB channel inside that band. The result is always a light,
	 * low-contrast ("faded") color.
	 */
	public static Color randomColor(Random random) {
		int low = House.COLOR_MIN + random.nextInt(House.COLOR_MAX - House.COLOR_SPREAD - House.COLOR_MIN + 1);
		int high = low + random.nextInt(House.COLOR_SPREAD + 1);
		int range = high - low + 1;
		return new Color(
			low + random.nextInt(range),
			low + random.nextInt(range),
			low + random.nextInt(range)
		);
	}
}
