package dev.itzrozzadev.core.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class CustomEvent extends Event {

	protected CustomEvent() {
		super(!Bukkit.isPrimaryThread());
	}

	protected CustomEvent(boolean async) {
		super(async);
	}
}