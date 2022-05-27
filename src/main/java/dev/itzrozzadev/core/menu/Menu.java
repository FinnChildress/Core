package dev.itzrozzadev.core.menu;

import dev.itzrozzadev.core.menu.button.Button;
import dev.itzrozzadev.core.plugin.BasePlugin;
import dev.itzrozzadev.core.task.Task;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements IMenu {


	@Getter
	private Player viewer;
	@Getter
	@Setter
	private String title;

	@Getter
	@Setter
	private int size;

	private final Map<Integer, Button> predefinedButtons;

	private MenuInventory menuInventory;


	protected Menu() {
		this("&0Menu", 27);
	}

	protected Menu(final int size) {
		this("&0Menu", size);

	}

	protected Menu(final String title) {
		this(title, 27);

	}

	protected Menu(final String title, final int size) {
		this(title, size, new HashMap<>());
	}

	protected Menu(final String title, final int size, final Map<Integer, Button> predefinedButtons) {
		this.title = title;
		this.size = size;
		this.predefinedButtons = predefinedButtons;
	}


	public void addButton(final int slot, final Button button) {
		if (button.isNullOrAir()) return;
		final boolean isSlotInMenu = slot >= 0 && slot < size;
		if (!isSlotInMenu) return;
		predefinedButtons.put(slot, button);
	}

	protected Button getButton(final int slot) {
		return predefinedButtons.get(slot);
	}

	private void buildInventory() {
		menuInventory = new MenuInventory(title, size);
		final Map<Integer, ItemStack> items = compileItems();
		addItems(items);
		addPredefinedButtons();
	}

	private void addItems(final Map<Integer, ItemStack> items) {
		for (final Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
			menuInventory.setItem(entry.getKey(), entry.getValue());
		}
	}

	private Map<Integer, ItemStack> compileItems() {
		final Map<Integer, ItemStack> items = new HashMap<>();
		for (int i = 0; i < size; i++) {
			final ItemStack item = getItemAt(i);
			items.put(i, item);
		}
		return items;
	}

	protected ItemStack getItemAt(final int slot) {
		return null;
	}

	private void addPredefinedButtons() {
		for (final Map.Entry<Integer, Button> entry : predefinedButtons.entrySet()) {
			final int slot = entry.getKey();
			final Button button = entry.getValue();
			menuInventory.setItem(slot, button.getItem());
		}
	}


	@Override
	public void open(final Player player) {
		this.viewer = player;
		final Menu previousMenu = Menu.getCurrentMenu(player);
		if (previousMenu != null) {
			player.setMetadata(MenuMetadata.PREVIOUS_MENU.getKey(), new FixedMetadataValue(BasePlugin.getInstance(), previousMenu));
		}
		Task.runLater(1, () -> {
			buildInventory();
			menuInventory.open(player);

			player.setMetadata(MenuMetadata.CURRENT_MENU.getKey(), new FixedMetadataValue(BasePlugin.getInstance(), this));
		});
	}

	@Override
	public void onButtonClick(final Button button, final MenuClick click) {
		button.onMenuClick(click);
	}

	@Override
	public void onMenuClick(final MenuClick click) {

	}

	public void restartMenu() {
		final Inventory inventory = getViewer().getOpenInventory().getTopInventory();

		compileItems().forEach(inventory::setItem);

		predefinedButtons.forEach((slot, button) -> inventory.setItem(slot, button.getItem()));

		this.getViewer().updateInventory();
	}

	protected void handleClose(final Inventory inventory) {
		onMenuClose(inventory);
		this.viewer.removeMetadata(MenuMetadata.CURRENT_MENU.getKey(), BasePlugin.getInstance());
		this.viewer.setMetadata(MenuMetadata.LAST_CLOSED_MENU.getKey(), new FixedMetadataValue(BasePlugin.getInstance(), this));
	}

	protected void onMenuClose(Inventory inventory) {

	}

	protected boolean isActionAllowed(final MenuClickLocation location, final int slot, @Nullable final ItemStack clicked, @Nullable final ItemStack cursor, final InventoryAction action) {
		return false;
	}

	private static Menu getMenu(final Player player, final MenuMetadata tag) {
		if (player.hasMetadata(tag.getKey())) {
			return (Menu) player.getMetadata(tag.getKey()).get(0).value();
		}
		return null;
	}

	public static Menu getCurrentMenu(final Player player) {
		return getMenu(player, MenuMetadata.CURRENT_MENU);
	}

	public static Menu getPreviousMenu(final Player player) {
		return getMenu(player, MenuMetadata.PREVIOUS_MENU);
	}
}
