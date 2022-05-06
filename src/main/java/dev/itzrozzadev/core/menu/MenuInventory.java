package dev.itzrozzadev.core.menu;

import dev.itzrozzadev.core.text.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class MenuInventory {
	private final String title;
	private final int size;

	private final ItemStack[] items;

	private Inventory inventory;

	protected MenuInventory(final String title, final int size) {
		this.title = title;
		this.size = size;
		this.items = new ItemStack[size];
	}

	public void setItem(final int slot, final ItemStack item) {
		this.items[slot] = item;
	}

	protected void build() {
		this.inventory = Bukkit.createInventory(null, this.size, Text.colorize(this.title));
		for (int i = 0; i < this.items.length; i++) {
			this.inventory.setItem(i, this.items[i]);
		}
	}

	public void open(final Player player) {
		build();
		player.openInventory(this.inventory);
	}


}
