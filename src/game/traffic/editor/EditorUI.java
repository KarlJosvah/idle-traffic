package game.traffic.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import game.traffic.entity.House;
import game.traffic.entity.HouseWorld;

public final class EditorUI {

// ======================================================================================================================================================

	public static final int PANEL_WIDTH = 268;
	public static final int COLLAPSED_WIDTH = 52;
	public static final int HEADER_HEIGHT = 64;
	public static final int CARD_MARGIN = 16;
	public static final int CARD_HEIGHT = 68;
	public static final int CARD_GAP = 10;

	public static final int INSPECTOR_X = 20;
	public static final int INSPECTOR_Y = 60;
	public static final int INSPECTOR_WIDTH = 304;
	public static final int INSPECTOR_HEIGHT = 316;

// ======================================================================================================================================================

	private static final Color PANEL_BG = new Color(28, 30, 38, 240);
	private static final Color PANEL_BORDER = new Color(92, 98, 116);
	private static final Color CARD_BG = new Color(48, 52, 64);
	private static final Color CARD_HOVER = new Color(66, 72, 90);
	private static final Color FIELD_BG = new Color(18, 20, 26);
	private static final Color TEXT = new Color(232, 236, 244);
	private static final Color MUTED = new Color(152, 160, 178);
	private static final Color ACCENT = new Color(255, 205, 80);
	private static final Color DESTROY = new Color(190, 62, 62);

	private static final Color SMALL_COLOR = new Color(120, 180, 230);
	private static final Color MEDIUM_COLOR = new Color(150, 200, 150);
	private static final Color LARGE_COLOR = new Color(220, 170, 120);

	private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
	private static final Font NORMAL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	private static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

	private static final Color OUTLINE_COLOR = new Color(22, 24, 30);

// ======================================================================================================================================================

	public Rectangle getPanelBounds(int renderWidth, int renderHeight, boolean collapsed) {
		int width = collapsed ? EditorUI.COLLAPSED_WIDTH : EditorUI.PANEL_WIDTH;
		return new Rectangle(renderWidth - width, 0, width, renderHeight);
	}

	public Rectangle getCollapseButtonBounds(int renderWidth, int renderHeight, boolean collapsed) {
		Rectangle panel = this.getPanelBounds(renderWidth, renderHeight, collapsed);
		return new Rectangle(panel.x + panel.width - 32, 14, 20, 20);
	}

	public Rectangle getCardBounds(int index, int renderWidth, int renderHeight) {
		return new Rectangle(
			renderWidth - EditorUI.PANEL_WIDTH + EditorUI.CARD_MARGIN,
			EditorUI.HEADER_HEIGHT + index * (EditorUI.CARD_HEIGHT + EditorUI.CARD_GAP),
			EditorUI.PANEL_WIDTH - EditorUI.CARD_MARGIN * 2,
			EditorUI.CARD_HEIGHT
		);
	}

	public Rectangle getInspectorBounds() {
		return new Rectangle(EditorUI.INSPECTOR_X, EditorUI.INSPECTOR_Y, EditorUI.INSPECTOR_WIDTH, EditorUI.INSPECTOR_HEIGHT);
	}

	public Rectangle getNameFieldBounds() {
		Rectangle panel = this.getInspectorBounds();
		return new Rectangle(panel.x + 16, panel.y + 60, panel.width - 32, 28);
	}

	public Rectangle getInspectorCloseBounds() {
		Rectangle panel = this.getInspectorBounds();
		return new Rectangle(panel.x + panel.width - 34, panel.y + 10, 20, 20);
	}

	public Rectangle getDestroyButtonBounds() {
		Rectangle panel = this.getInspectorBounds();
		return new Rectangle(panel.x + 16, panel.y + panel.height - 48, panel.width - 32, 32);
	}

// ======================================================================================================================================================

	public void render(
		Graphics2D g, int renderWidth, int renderHeight, boolean collapsed,
		HouseWorld world, House selected, boolean editingName, String nameBuffer,
		House.Type draggingType, int mouseX, int mouseY
	) {
		Stroke previousStroke = g.getStroke();
		Color previousColor = g.getColor();
		Font previousFont = g.getFont();

		this.drawPanel(g, renderWidth, renderHeight, collapsed, world, draggingType, mouseX, mouseY);
		if (selected != null) {
			this.drawInspector(g, selected, editingName, nameBuffer);
		}
		if (draggingType != null) {
			this.drawGhost(g, draggingType, mouseX, mouseY);
		}

		g.setStroke(previousStroke);
		g.setColor(previousColor);
		g.setFont(previousFont);
	}

	private void drawPanel(
		Graphics2D g, int renderWidth, int renderHeight, boolean collapsed,
		HouseWorld world, House.Type draggingType, int mouseX, int mouseY
	) {
		Rectangle panel = this.getPanelBounds(renderWidth, renderHeight, collapsed);
		g.setColor(PANEL_BG);
		g.fillRect(panel.x, panel.y, panel.width, panel.height);
		g.setColor(PANEL_BORDER);
		g.drawLine(panel.x, 0, panel.x, renderHeight);

		Rectangle button = this.getCollapseButtonBounds(renderWidth, renderHeight, collapsed);
		g.setColor(CARD_BG);
		g.fillRoundRect(button.x, button.y, button.width, button.height, 5, 5);
		g.setColor(button.contains(mouseX, mouseY) ? ACCENT : TEXT);
		g.setFont(TITLE_FONT);
		g.drawString(collapsed ? ">" : "<", button.x + 6, button.y + 16);

		if (collapsed) {
			Graphics2D rotated = (Graphics2D) g.create();
			rotated.translate(panel.x + panel.width / 2 - 7, panel.y + 130);
			rotated.rotate(Math.PI / 2.0);
			rotated.setFont(NORMAL_FONT);
			rotated.setColor(MUTED);
			rotated.drawString("Entities", 0, 0);
			rotated.dispose();
			return;
		}

		g.setFont(TITLE_FONT);
		g.setColor(TEXT);
		g.drawString("Entities", panel.x + 16, 34);
		g.setFont(SMALL_FONT);
		g.setColor(MUTED);
		g.drawString("Drag a house into the world", panel.x + 16, 53);

		House.Type[] types = House.Type.values();
		for (int i = 0; i < types.length; i++) {
			this.drawCard(g, types[i], this.getCardBounds(i, renderWidth, renderHeight), world, draggingType, mouseX, mouseY);
		}
	}

	private void drawCard(
		Graphics2D g, House.Type type, Rectangle bounds, HouseWorld world,
		House.Type draggingType, int mouseX, int mouseY
	) {
		boolean hover = bounds.contains(mouseX, mouseY);
		g.setColor(hover ? CARD_HOVER : CARD_BG);
		g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8);
		g.setColor(draggingType == type ? ACCENT : PANEL_BORDER);
		g.setStroke(new BasicStroke(draggingType == type ? 2f : 1f));
		g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8);
		g.setStroke(new BasicStroke(1f));

		Color typeColor = EditorUI.colorFor(type);
		g.setColor(typeColor);
		g.fillRect(bounds.x + 12, bounds.y + 18, 28, 28);
		g.setColor(EditorUI.OUTLINE_COLOR);
		g.drawRect(bounds.x + 12, bounds.y + 18, 28, 28);

		g.setFont(NORMAL_FONT);
		g.setColor(TEXT);
		g.drawString(EditorUI.labelFor(type), bounds.x + 50, bounds.y + 24);
		g.setFont(SMALL_FONT);
		g.setColor(MUTED);
		g.drawString("Drag to place", bounds.x + 50, bounds.y + 42);

		String count = "x" + world.countType(type);
		FontMetrics metrics = g.getFontMetrics();
		g.drawString(count, bounds.x + bounds.width - 12 - metrics.stringWidth(count), bounds.y + 24);
	}

	private void drawInspector(Graphics2D g, House house, boolean editingName, String nameBuffer) {
		Rectangle panel = this.getInspectorBounds();
		g.setColor(PANEL_BG);
		g.fillRoundRect(panel.x, panel.y, panel.width, panel.height, 10, 10);
		g.setColor(PANEL_BORDER);
		g.drawRoundRect(panel.x, panel.y, panel.width, panel.height, 10, 10);

		g.setFont(SMALL_FONT);
		g.setColor(MUTED);
		g.drawString("INSPECTOR", panel.x + 16, panel.y + 22);

		Rectangle close = this.getInspectorCloseBounds();
		g.setColor(TEXT);
		g.drawString("x", close.x + 5, close.y + 15);

		g.setFont(NORMAL_FONT);
		g.setColor(TEXT);
		g.drawString("Name", panel.x + 16, panel.y + 52);

		Rectangle field = this.getNameFieldBounds();
		g.setColor(FIELD_BG);
		g.fillRoundRect(field.x, field.y, field.width, field.height, 5, 5);
		g.setColor(editingName ? ACCENT : PANEL_BORDER);
		g.drawRoundRect(field.x, field.y, field.width, field.height, 5, 5);
		String text = editingName ? nameBuffer : house.getName();
		g.setColor(TEXT);
		g.drawString(text, field.x + 8, field.y + 19);
		if (editingName) {
			FontMetrics metrics = g.getFontMetrics();
			int caretX = field.x + 8 + metrics.stringWidth(text);
			g.setColor(ACCENT);
			g.drawLine(caretX, field.y + 6, caretX, field.y + field.height - 6);
		}

		int rowY = panel.y + 112;
		this.drawRow(g, "Type", EditorUI.labelFor(house.getType()), panel.x + 16, rowY);
		rowY += 24;
		this.drawRow(g, "Position", String.format("%.0f, %.0f", house.getX(), house.getY()), panel.x + 16, rowY);
		rowY += 24;
		this.drawRow(g, "Rotation", String.format("%.0f\u00b0", Math.toDegrees(house.getRotation())), panel.x + 16, rowY);
		rowY += 24;
		this.drawRow(g, "Size", String.format("%.0f x %.0f", house.getSize(), house.getSize()), panel.x + 16, rowY);
		rowY += 24;
		g.setFont(NORMAL_FONT);
		g.setColor(MUTED);
		g.drawString("Color", panel.x + 16, rowY);
		g.setColor(house.getColor());
		g.fillRect(panel.x + 104, rowY - 11, 22, 14);
		g.setColor(EditorUI.OUTLINE_COLOR);
		g.drawRect(panel.x + 104, rowY - 11, 22, 14);

		Rectangle destroy = this.getDestroyButtonBounds();
		g.setColor(DESTROY);
		g.fillRoundRect(destroy.x, destroy.y, destroy.width, destroy.height, 6, 6);
		g.setColor(Color.WHITE);
		g.setFont(NORMAL_FONT);
		FontMetrics metrics = g.getFontMetrics();
		g.drawString("Destroy", destroy.x + (destroy.width - metrics.stringWidth("Destroy")) / 2, destroy.y + 21);
	}

	private void drawRow(Graphics2D g, String label, String value, int x, int y) {
		g.setFont(NORMAL_FONT);
		g.setColor(MUTED);
		g.drawString(label, x, y);
		g.setColor(TEXT);
		g.drawString(value, x + 88, y);
	}

	private void drawGhost(Graphics2D g, House.Type type, int mouseX, int mouseY) {
		if (mouseX < 0 || mouseY < 0) {
			return;
		}
		Color base = EditorUI.colorFor(type);
		Graphics2D ghost = (Graphics2D) g.create();
		ghost.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 170));
		ghost.fillRoundRect(mouseX - 18, mouseY - 18, 36, 36, 6, 6);
		ghost.setColor(ACCENT);
		ghost.drawRoundRect(mouseX - 18, mouseY - 18, 36, 36, 6, 6);
		ghost.setFont(SMALL_FONT);
		ghost.setColor(TEXT);
		ghost.drawString("Drop to place", mouseX - 36, mouseY + 34);
		ghost.dispose();
	}

	private static String labelFor(House.Type type) {
		switch (type) {
			case SMALL:
				return "Small House";
			case MEDIUM:
				return "Medium House";
			case LARGE:
				return "Large House";
			default:
				return "House";
		}
	}

	private static Color colorFor(House.Type type) {
		switch (type) {
			case SMALL:
				return EditorUI.SMALL_COLOR;
			case MEDIUM:
				return EditorUI.MEDIUM_COLOR;
			case LARGE:
				return EditorUI.LARGE_COLOR;
			default:
				return EditorUI.SMALL_COLOR;
		}
	}
}
