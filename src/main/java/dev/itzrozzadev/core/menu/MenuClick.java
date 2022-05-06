package dev.itzrozzadev.core.menu;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@Getter
@RequiredArgsConstructor
public class MenuClick {

	private final Player player;
	private final Menu menu;
	private final int slot;
	private final ClickType clickType;

}
