package dev.itzrozzadev.core.event;

import dev.itzrozzadev.core.plugin.BasePlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.event.Listener;

@UtilityClass
public class EventRegistry {
	
	public void registerEvent(final Listener event) {
		BasePlugin.getInstance().getServer().getPluginManager().registerEvents(event, BasePlugin.getInstance());
	}
}
