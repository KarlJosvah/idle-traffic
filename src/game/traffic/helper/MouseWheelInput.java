package game.traffic.helper;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The engine does not forward mouse-wheel events through InputEvent, so this
 * bridge listens on the game canvas directly and queues notch counts for the
 * game loop to consume on its next tick.
 */
public final class MouseWheelInput implements MouseWheelListener {

// ======================================================================================================================================================

	private final AtomicInteger notches = new AtomicInteger();

	private volatile int x = -1;
	private volatile int y = -1;
	private Component attached;

// ======================================================================================================================================================

	public void attach() {
		if (this.attached != null) {
			return;
		}
		for (Frame frame : Frame.getFrames()) {
			Component canvas = MouseWheelInput.findCanvas(frame);
			if (canvas != null) {
				canvas.addMouseWheelListener(this);
				this.attached = canvas;
				return;
			}
		}
	}

	public void close() {
		if (this.attached == null) {
			return;
		}
		this.attached.removeMouseWheelListener(this);
		this.attached = null;
	}

	public int pollNotches() {
		return this.notches.getAndSet(0);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

// ======================================================================================================================================================

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		this.x = event.getX();
		this.y = event.getY();
		this.notches.addAndGet(-event.getWheelRotation());
	}

	private static Component findCanvas(Component component) {
		if (component instanceof Canvas) {
			return component;
		}
		if (component instanceof Container) {
			Container container = (Container) component;
			for (int i = 0; i < container.getComponentCount(); i++) {
				Component found = MouseWheelInput.findCanvas(container.getComponent(i));
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}
}
