package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

import engine.gamestate.StateID;
import engine.gamestate.GameState;
import engine.gamestate.InputEvent;
import engine.gamestate.GameStateHandler;

import game.traffic.editor.TrafficEditor;
import game.traffic.entity.HouseLayer;
import game.traffic.entity.HouseWorld;
import game.traffic.entity.InfiniteGrid;

public class PlayState extends GameState {

// ======================================================================================================================================================

	public static final StateID ID = StateID.of("PLAY");

	static {
		GameStateHandler.registerState(PlayState.ID, new PlayState());
	}

	private TrafficEditor editor;

// ======================================================================================================================================================

	public PlayState() {
	}

// ======================================================================================================================================================

	@Override
	public void init() {
		InfiniteGrid grid = new InfiniteGrid();
		HouseWorld world = new HouseWorld();
		this.editor = new TrafficEditor(grid.getCamera(), world);
		this.addElement(grid);
		this.addElement(new HouseLayer(world, grid.getCamera()));
		this.addElement(this.editor);
	}

	@Override
	public void tick(double elapsedSecond, long loopID) {
		this.tickElements(elapsedSecond, loopID);
	}

	@Override
	public void render(Graphics2D g, int renderWidth, int renderHeight) {
		this.renderElements(g, renderWidth, renderHeight);
	}

	@Override
	public void closeState() {
		// GameStateHandler calls close() on all elements after this hook.
	}

// ======================================================================================================================================================

	@Override
	public void input(InputEvent event) {
		// The editor gets first refusal so renaming and drag/drop win over
		// navigation. super.input() then dispatches to elements in registration
		// order and stops at the first consumed event.
		if (this.editor != null && !event.isHandled()) {
			this.editor.input(event);
		}
		super.input(event);
		if (event.isMouse()) {
			if (event.getType() == InputEvent.Type.MOUSE_PRESSED && event.getButton() == InputEvent.LEFT) {
			} else if (event.getType() == InputEvent.Type.MOUSE_PRESSED && event.getButton() == InputEvent.RIGHT) {
			}
			event.consume();
		}
		if (event.isKey()) {
			if (event.getType() == InputEvent.Type.KEY_PRESSED && event.getKeyCode() == KeyEvent.VK_P) {
			}
			event.consume();
		}
	}
}