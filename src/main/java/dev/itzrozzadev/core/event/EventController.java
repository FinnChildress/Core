package dev.itzrozzadev.core.event;

import dev.itzrozzadev.core.plugin.BasePlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

@UtilityClass
public class EventController {
	
	public void registerEvent(final Listener event) {
		BasePlugin.getInstance().getServer().getPluginManager().registerEvents(event, BasePlugin.getInstance());
	}


	public static boolean callEvent(final Event event) {
		Bukkit.getPluginManager().callEvent(event);
		boolean isCancelled = !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
		return !isCancelled;
	}


}
