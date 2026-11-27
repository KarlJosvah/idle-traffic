package game.traffic.entity;

import java.util.ArrayList;
import java.util.List;

import game.traffic.factory.HouseFactory;

public final class HouseWorld {

// ======================================================================================================================================================

	private final List<House> houses = new ArrayList<House>();

	private House selected;

// ======================================================================================================================================================

	public House add(House.Type type, double x, double y) {
		House house = HouseFactory.of(type);
		house.setPosition(x, y);
		this.houses.add(house);
		return house;
	}

	public void remove(House house) {
		if (house == null) {
			return;
		}
		this.houses.remove(house);
		if (this.selected == house) {
			this.selected = null;
		}
	}

	public void clear() {
		this.houses.clear();
		this.selected = null;
	}

// ======================================================================================================================================================

	public List<House> getHouses() {
		return this.houses;
	}

	public House getSelected() {
		return this.selected;
	}

	public void setSelected(House selected) {
		this.selected = selected;
	}

	public int countType(House.Type type) {
		int count = 0;
		for (House house : this.houses) {
			if (house.getType() == type) {
				count++;
			}
		}
		return count;
	}

	/** Topmost first, so overlapping houses pick the one drawn last. */
	public House findAt(double worldX, double worldY) {
		for (int i = this.houses.size() - 1; i >= 0; i--) {
			House house = this.houses.get(i);
			if (house.contains(worldX, worldY)) {
				return house;
			}
		}
		return null;
	}
}
