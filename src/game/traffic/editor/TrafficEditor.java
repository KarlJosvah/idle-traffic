package game.traffic.editor;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import engine.gamestate.GameState;
import engine.gamestate.InputEvent;
import engine.gamestate.StateElement;
import game.traffic.entity.house.House;
import game.traffic.entity.house.HouseWorld;
import game.traffic.helper.Camera2D;

public final class TrafficEditor implements StateElement {

// ======================================================================================================================================================

	private final Camera2D camera;
	private final HouseWorld world;
	private final EditorUI ui = new EditorUI();

	private boolean collapsed;
	private House.Type draggingType;
	private boolean editingName;
	private String nameBuffer = "";

	private int mouseX = -1;
	private int mouseY = -1;
	private int viewportWidth = 1;
	private int viewportHeight = 1;

// ======================================================================================================================================================

	public TrafficEditor(Camera2D camera, HouseWorld world) {
		this.camera = camera;
		this.world = world;
	}

// ======================================================================================================================================================

	@Override
	public void init(GameState parentState) {
		this.collapsed = false;
		this.draggingType = null;
		this.editingName = false;
		this.nameBuffer = "";
	}

	@Override
	public void close() {
		this.draggingType = null;
		this.editingName = false;
		this.nameBuffer = "";
	}

// ======================================================================================================================================================

	@Override
	public void render(Graphics2D g, int renderWidth, int renderHeight) {
		if (renderWidth > 0) {
			this.viewportWidth = renderWidth;
		}
		if (renderHeight > 0) {
			this.viewportHeight = renderHeight;
		}
		this.ui.render(
			g, this.viewportWidth, this.viewportHeight, this.collapsed,
			this.world, this.world.getSelected(), this.editingName, this.nameBuffer,
			this.draggingType, this.mouseX, this.mouseY
		);
	}

// ======================================================================================================================================================

	@Override
	public void input(InputEvent event) {
		if (event.isKey()) {
			this.handleKey(event);
			return;
		}
		if (!event.isMouse()) {
			return;
		}
		if (event.getX() >= 0 && event.getY() >= 0) {
			this.mouseX = event.getX();
			this.mouseY = event.getY();
		}

		InputEvent.Type type = event.getType();
		if (type == InputEvent.Type.MOUSE_PRESSED && event.getButton() == InputEvent.LEFT) {
			this.handleLeftPress(event);
		} else if (type == InputEvent.Type.MOUSE_DRAGGED) {
			if (this.draggingType != null) {
				event.consume();
			}
		} else if (type == InputEvent.Type.MOUSE_RELEASED && event.getButton() == InputEvent.LEFT) {
			this.handleLeftRelease(event);
		}
	}

	private void handleKey(InputEvent event) {
		if (!this.editingName) {
			return;
		}
		if (event.getType() == InputEvent.Type.KEY_RELEASED) {
			event.consume();
			return;
		}
		if (event.getType() != InputEvent.Type.KEY_PRESSED) {
			return;
		}

		int code = event.getKeyCode();
		if (code == KeyEvent.VK_ENTER) {
			this.commitName();
		} else if (code == KeyEvent.VK_ESCAPE) {
			this.editingName = false;
		} else if (code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE) {
			if (!this.nameBuffer.isEmpty()) {
				this.nameBuffer = this.nameBuffer.substring(0, this.nameBuffer.length() - 1);
			}
		} else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_HOME || code == KeyEvent.VK_END) {
			// Caret movement is intentionally not implemented; the name is edited at the end.
		} else {
			char character = event.getKeyChar();
			if (character >= 32 && character != 127) {
				this.nameBuffer += character;
			}
		}
		event.consume();
	}

	private void handleLeftPress(InputEvent event) {
		Rectangle panel = this.ui.getPanelBounds(this.viewportWidth, this.viewportHeight, this.collapsed);
		Rectangle collapse = this.ui.getCollapseButtonBounds(this.viewportWidth, this.viewportHeight, this.collapsed);
		if (collapse.contains(this.mouseX, this.mouseY)) {
			this.commitName();
			this.collapsed = !this.collapsed;
			event.consume();
			return;
		}

		if (!this.collapsed) {
			House.Type[] types = House.Type.values();
			for (int i = 0; i < types.length; i++) {
				Rectangle card = this.ui.getCardBounds(i, this.viewportWidth, this.viewportHeight);
				if (card.contains(this.mouseX, this.mouseY)) {
					this.commitName();
					this.draggingType = types[i];
					event.consume();
					return;
				}
			}
		}
		if (panel.contains(this.mouseX, this.mouseY)) {
			this.commitName();
			event.consume();
			return;
		}

		House selected = this.world.getSelected();
		if (selected != null && this.ui.getInspectorBounds().contains(this.mouseX, this.mouseY)) {
			if (this.ui.getNameFieldBounds().contains(this.mouseX, this.mouseY)) {
				this.editingName = true;
				this.nameBuffer = selected.getName();
			} else if (this.ui.getDestroyButtonBounds().contains(this.mouseX, this.mouseY)) {
				this.world.remove(selected);
				this.select(null);
			} else if (this.ui.getInspectorCloseBounds().contains(this.mouseX, this.mouseY)) {
				this.select(null);
			} else {
				this.commitName();
			}
			event.consume();
			return;
		}

		this.commitName();
		this.select(this.houseAtScreen(this.mouseX, this.mouseY));
		event.consume();
	}

	private void handleLeftRelease(InputEvent event) {
		if (this.draggingType == null) {
			return;
		}
		if (event.getX() < 0 || event.getY() < 0) {
			this.draggingType = null;
			event.consume();
			return;
		}
		House.Type type = this.draggingType;
		this.draggingType = null;
		if (!this.isOverUI(this.mouseX, this.mouseY)) {
			double[] position = this.screenToWorld(this.mouseX, this.mouseY);
			this.select(this.world.add(type, position[0], position[1]));
		}
		event.consume();
	}

	private boolean isOverUI(int x, int y) {
		if (this.ui.getPanelBounds(this.viewportWidth, this.viewportHeight, this.collapsed).contains(x, y)) {
			return true;
		}
		return this.world.getSelected() != null && this.ui.getInspectorBounds().contains(x, y);
	}

	private House houseAtScreen(int screenX, int screenY) {
		double[] position = this.screenToWorld(screenX, screenY);
		return this.world.findAt(position[0], position[1]);
	}

	private double[] screenToWorld(int screenX, int screenY) {
		java.awt.geom.Point2D.Double point = this.camera.screenToWorld(
			screenX, screenY, this.viewportWidth / 2.0, this.viewportHeight / 2.0
		);
		return new double[] { point.x, point.y };
	}

	private void commitName() {
		if (this.editingName && this.world.getSelected() != null) {
			this.world.getSelected().setName(this.nameBuffer);
		}
		this.editingName = false;
	}

	private void select(House house) {
		this.commitName();
		this.world.setSelected(house);
	}

// ======================================================================================================================================================

	public boolean isCollapsed() {
		return this.collapsed;
	}

	public boolean isEditingName() {
		return this.editingName;
	}

	public House.Type getDraggingType() {
		return this.draggingType;
	}

	public Rectangle getCardBounds(int index) {
		return this.ui.getCardBounds(index, this.viewportWidth, this.viewportHeight);
	}

	public Rectangle getCollapseButtonBounds() {
		return this.ui.getCollapseButtonBounds(this.viewportWidth, this.viewportHeight, this.collapsed);
	}

	public Rectangle getNameFieldBounds() {
		return this.ui.getNameFieldBounds();
	}

	public Rectangle getDestroyButtonBounds() {
		return this.ui.getDestroyButtonBounds();
	}
}
