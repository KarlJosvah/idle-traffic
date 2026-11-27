package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

import engine.gamestate.StateID;
import engine.gamestate.GameState;
import engine.gamestate.InputEvent;
import engine.gamestate.GameStateHandler;

import game.traffic.Traffic;

public class PlayState extends GameState {

// ======================================================================================================================================================

	public static final StateID ID = StateID.of("PLAY");

	static {
		GameStateHandler.registerState(PlayState.ID, new PlayState());
	}

// ======================================================================================================================================================

	public PlayState() {
	}

// ======================================================================================================================================================

	@Override
	public void init() {
		this.addElement(new Traffic());
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