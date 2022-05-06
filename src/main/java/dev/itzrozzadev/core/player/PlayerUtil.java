package dev.itzrozzadev.core.player;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;

@UtilityClass
public class PlayerUtil {

	public boolean hasPermission(final CommandSender sender, final String permission) {
		return sender.hasPermission(permission);
	}
}
