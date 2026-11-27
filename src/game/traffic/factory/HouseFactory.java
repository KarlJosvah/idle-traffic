package game.traffic.factory;

import java.util.EnumMap;
import java.util.Map;

import game.traffic.entity.House;

public final class HouseFactory {

// ======================================================================================================================================================

	private static final Map<House.Type, Integer> INDICES = new EnumMap<House.Type, Integer>(House.Type.class);

// ======================================================================================================================================================

	private HouseFactory() {
	}

// ======================================================================================================================================================

	public static House of(House.Type type) {
		switch (type) {
			case SMALL:
				return HouseFactory.ofSmall();
			case MEDIUM:
				return HouseFactory.ofMedium();
			case LARGE:
				return HouseFactory.ofLarge();
			default:
				return HouseFactory.ofSmall();
		}
	}

	private static House ofSmall() {
		return new House(
			House.Type.SMALL,
			"Small House " + HouseFactory.nextIndex(House.Type.SMALL),
			0.0, 0.0, 0.0, 48.0, House.randomColor()
		);
	}

	private static House ofMedium() {
		return new House(
			House.Type.MEDIUM,
			"Medium House " + HouseFactory.nextIndex(House.Type.MEDIUM),
			0.0, 0.0, 0.0, 80.0, House.randomColor()
		);
	}

	private static House ofLarge() {
		return new House(
			House.Type.LARGE,
			"Large House " + HouseFactory.nextIndex(House.Type.LARGE),
			0.0, 0.0, 0.0, 120.0, House.randomColor()
		);
	}

	private static int nextIndex(House.Type type) {
		int index = HouseFactory.INDICES.getOrDefault(type, 0) + 1;
		HouseFactory.INDICES.put(type, index);
		return index;
	}
}
