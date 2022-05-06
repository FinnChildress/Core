package dev.itzrozzadev.core.menu;

import dev.itzrozzadev.core.menu.button.Button;
import org.bukkit.entity.Player;


public interface IMenu {

	void open(Player player);

	void onButtonClick(Button button, MenuClick click);

	void onMenuClick(MenuClick click);

}
