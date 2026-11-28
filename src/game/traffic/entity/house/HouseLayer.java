package game.traffic.entity.house;

import java.awt.Graphics2D;

import engine.gamestate.StateElement;
import game.traffic.entity.renderer.HouseRenderer;
import game.traffic.helper.Camera2D;

public final class HouseLayer implements StateElement {

// ======================================================================================================================================================

	private final HouseWorld world;
	private final Camera2D camera;

// ======================================================================================================================================================

	public HouseLayer(HouseWorld world, Camera2D camera) {
		this.world = world;
		this.camera = camera;
	}

// ======================================================================================================================================================

	@Override
	public void render(Graphics2D g, int renderWidth, int renderHeight) {
		for (House house : this.world.getHouses()) {
			HouseRenderer.render(g, house, this.camera, renderWidth, renderHeight, house == this.world.getSelected());
		}
	}
}
