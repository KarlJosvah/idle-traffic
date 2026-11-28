package game.traffic;

import java.awt.Graphics2D;

import engine.Main;
import engine.gamestate.GameState;
import engine.gamestate.InputEvent;
import engine.gamestate.StateElement;
import game.traffic.editor.TrafficEditor;
import game.traffic.entity.InfiniteGrid;
import game.traffic.entity.house.HouseLayer;
import game.traffic.entity.house.HouseWorld;
import game.traffic.helper.MouseWheelInput;

public class Traffic implements StateElement {

// ======================================================================================================================================================

	public static final int WIDTH = Main.WIDTH;
	public static final int HEIGHT = Main.HEIGHT;

	private InfiniteGrid grid;
	private HouseWorld world;
	private HouseLayer houseLayer;
	private TrafficEditor editor;
	private MouseWheelInput wheelInput;

	private int viewportWidth = Traffic.WIDTH;
	private int viewportHeight = Traffic.HEIGHT;

// ======================================================================================================================================================

	public Traffic() {
	}

// ======================================================================================================================================================

	@Override
	public void init(GameState parentState) {
		if (this.grid == null) {
			this.grid = new InfiniteGrid();
			this.world = new HouseWorld();
			this.houseLayer = new HouseLayer(this.world, this.grid.getCamera());
			this.editor = new TrafficEditor(this.grid.getCamera(), this.world);
			this.wheelInput = new MouseWheelInput();
			this.wheelInput.attach();
		}
		this.grid.init(parentState);
		this.houseLayer.init(parentState);
		this.editor.init(parentState);
	}

	@Override
	public void close() {
		if (this.grid == null) {
			return;
		}
		this.grid.close();
		this.houseLayer.close();
		this.editor.close();
		this.wheelInput.close();
	}

// ======================================================================================================================================================

	@Override
	public void tick(double elapsedSecond, long loopID) {
		int notches = this.wheelInput.pollNotches();
		if (notches != 0) {
			double centerX = this.viewportWidth / 2.0;
			double centerY = this.viewportHeight / 2.0;
			int mouseX = this.wheelInput.getX() >= 0 ? this.wheelInput.getX() : (int) centerX;
			int mouseY = this.wheelInput.getY() >= 0 ? this.wheelInput.getY() : (int) centerY;
			this.grid.getCamera().zoomAt(
				Math.pow(InfiniteGrid.ZOOM_STEP, notches), mouseX, mouseY, centerX, centerY
			);
		}
		this.grid.tick(elapsedSecond, loopID);
		this.houseLayer.tick(elapsedSecond, loopID);
		this.editor.tick(elapsedSecond, loopID);
	}

	@Override
	public void render(Graphics2D g, int renderWidth, int renderHeight) {
		if (renderWidth > 0) {
			this.viewportWidth = renderWidth;
		}
		if (renderHeight > 0) {
			this.viewportHeight = renderHeight;
		}
		this.grid.render(g, renderWidth, renderHeight);
		this.houseLayer.render(g, renderWidth, renderHeight);
		this.editor.render(g, renderWidth, renderHeight);
	}

// ======================================================================================================================================================

	@Override
	public void input(InputEvent event) {
		if (event.isHandled()) {
			return;
		}
		// The editor gets first refusal so renaming and drag/drop win over
		// navigation; unconsumed events fall through to the grid.
		this.editor.input(event);
		if (event.isHandled()) {
			return;
		}
		this.grid.input(event);
		if (event.isHandled()) {
			return;
		}
		this.houseLayer.input(event);
	}

// ======================================================================================================================================================

	public InfiniteGrid getGrid() {
		return this.grid;
	}

	public HouseWorld getWorld() {
		return this.world;
	}

	public TrafficEditor getEditor() {
		return this.editor;
	}

	public MouseWheelInput getWheelInput() {
		return this.wheelInput;
	}
}
