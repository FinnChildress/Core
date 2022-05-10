package dev.itzrozzadev.core.menu;

import dev.itzrozzadev.core.item.Item;
import dev.itzrozzadev.core.menu.button.Button;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class MenuPagged<T> extends Menu {

	private int currentPage = 1;
	private final Map<Integer, List<T>> pages = new HashMap<>();
	@Setter
	private int manualSize;

	private final int autoSize;

	@Setter
	private  int nextButtonSlot;

	@Setter
	private int previousButtonSlot;

	@Setter
	private ItemStack nextButtonItem = new Item(Material.ARROW).name("&aNext Page").create();
	@Setter
	private ItemStack previousButtonItem = new Item(Material.ARROW).name("&aPrevious Page").create();


	@SafeVarargs
	public MenuPagged(final T... items) {
		this(Arrays.asList(items));
	}

	public MenuPagged(final List<T> items) {
		final int length = getItemAmount(items);
		this.autoSize = manualSize != 0 ? manualSize : length <= 9 ? 9 : length <= 18 ? 18 : length <= 27 ? 27 : length <= 36 ? 36 : 45;
		setSize(this.autoSize + 9);
		this.nextButtonSlot = getSize() - 1;
		this.previousButtonSlot = getSize() - 9;


		this.pages.clear();
		this.pages.putAll(fillPages(items));


		setButtons();
	}

	private int getItemAmount(final Iterable<T> items) {
		int i = 0;
		for (final T item : items) {
			i++;
		}
		return i;
	}

	private Map<Integer, List<T>> fillPages(final List<T> items) {
		final Map<Integer, List<T>> pages = new HashMap<>();
		final int pageCount = items.size() == this.autoSize ? 0 : items.size() / this.autoSize;

		for (int i = 0; i <= pageCount; i++) {
			final List<T> pageItems = new ArrayList<>();

			final int lower = this.autoSize * i;
			final int upper = lower + this.autoSize;

			for (int index = lower; index < upper; index++) {
				if (index == items.size()) break;
				final T item = items.get(index);
				pageItems.add(item);
			}
			pages.put(i, pageItems);
		}
		return pages;
	}


	private void setButtons() {

		final boolean hasPages = this.pages.size() > 1;


		addButton(this.nextButtonSlot, new Button() {
			final boolean canGo = currentPage < pages.size();

			@Override
			public ItemStack getItem() {
				return hasPages ? MenuPagged.this.nextButtonItem : new Item(Material.AIR).create();
			}

			@Override
			public void onMenuClick(final MenuClick click) {
				if (canGo) {
					currentPage++;
					updatePage();
				}
			}
		});

		addButton(this.previousButtonSlot, new Button() {
			final boolean canGo = currentPage > 1;

			@Override
			public ItemStack getItem() {
				return hasPages ? MenuPagged.this.previousButtonItem : new Item(Material.AIR).create();
			}

			@Override
			public void onMenuClick(final MenuClick click) {
				if (canGo) {
					currentPage--;
					updatePage();
				}
			}
		});
	}

	private void updatePage() {
		setButtons();
		restartMenu();
	}


	abstract protected ItemStack toItem(T item);

	abstract protected void onPageClick(MenuClick click, T item);

	@Override
	public void onMenuClick(final MenuClick click) {
		final List<T> items = getCurrentPageItems();
		final int slot = click.getSlot();
		if (slot < items.size()) {
			final T item = items.get(slot);
			onPageClick(click, item);
		}
	}

	@Override
	protected ItemStack getItemAt(final int slot) {
		final List<T> currentPageItems = getCurrentPageItems();
		if (slot < currentPageItems.size()) {
			final T item = currentPageItems.get(slot);
			if (item != null) {
				return toItem(item);
			}
		}
		if (slot == this.nextButtonSlot) {
			return this.nextButtonItem;
		}
		if (slot == this.previousButtonSlot) {
			return this.previousButtonItem;
		}
		return super.getItemAt(slot);
	}

	private List<T> getCurrentPageItems() {
		return this.pages.get(this.currentPage - 1);
	}
}
