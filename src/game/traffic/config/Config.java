package game.traffic.config;

public final class Config {

// ======================================================================================================================================================

	private static final Config instance = new Config();

	private volatile Config.WindowMode windowMode = Config.WindowMode.FULLSCREEN;

// ======================================================================================================================================================

	public enum WindowMode {
		FULLSCREEN,
		WINDOWED,
		BORDERLESS
	}

// ======================================================================================================================================================

	private Config() {
	}

	public static Config get() {
		return Config.instance;
	}

// ======================================================================================================================================================

	public synchronized Config setWindowMode(Config.WindowMode windowMode) {
		this.windowMode = windowMode;
		return this;
	}

// ======================================================================================================================================================

	public Config.WindowMode getWindowMode() {
		return this.windowMode;
	}

// ======================================================================================================================================================

	public synchronized void defaultConfig() {
		this.setWindowMode(Config.WindowMode.FULLSCREEN)
		;
	}

	public synchronized void testConfig() {
		this.setWindowMode(Config.WindowMode.FULLSCREEN)
		;
	}
}