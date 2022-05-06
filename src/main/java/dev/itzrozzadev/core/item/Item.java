package dev.itzrozzadev.core.item;

import dev.itzrozzadev.core.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;


public class Item {
	private Material material = Material.STONE;
	private int amount;
	private short damage;
	private String name;
	private List<String> lore;
	private Map<Enchantment, Integer> enchants = new HashMap<>();
	private boolean unbreakable;
	private boolean glow = false;
	private boolean flagsHidden = true;
	private OfflinePlayer skullOwner;

	public Item(final ItemStack item) {
		this.material = item.getType();
		this.amount = item.getAmount();
		this.damage = item.getDurability();
		if (item.hasItemMeta()) {
			final ItemMeta meta = item.getItemMeta();
			assert meta != null;
			this.name = meta.hasDisplayName() ? meta.getDisplayName() : null;
			this.lore = meta.hasLore() ? meta.getLore() : null;
			this.enchants = meta.hasEnchants() ? meta.getEnchants() : null;
		}
	}


	public Item(final Material material) {
		this(material, 1);
	}

	public Item(final Material material, final int amount) {
		this(material, amount, 0);
	}

	public Item(final Material material, final int amount, final int damage) {
		this(material, amount, (short) damage);
	}

	public Item(final Material material, final int amount, final short damage) {
		this.material = material;
		this.amount = amount;
		this.damage = damage;
		this.lore = new ArrayList<>();
		this.enchants = new HashMap<>();
	}

	public Item material(final Material mat) {
		this.material = mat;
		return this;
	}

	public Item amount(final int amount) {
		this.amount = amount;
		return this;
	}

	public Item name(final String name) {
		this.name = name;
		return this;
	}

	public Item lore(final String... lore) {
		return lore(Arrays.asList(lore));
	}

	public Item lore(final List<String> lore) {
		this.lore = lore;
		return this;
	}

	public Item addEnchant(final Enchantment enchant) {
		return addEnchant(enchant, 1);
	}

	public Item addEnchant(final Enchantment enchant, final int level) {
		if (enchant != null)
			this.enchants.put(enchant, level);
		return this;
	}

	public Item enchants(final Map<Enchantment, Integer> ench) {
		this.enchants = ench;
		return this;
	}


	public Item unbreakable(final boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	public Item glow(final boolean glow) {
		this.glow = glow;
		return this;
	}

	public Item glow() {
		this.glow = true;
		return this;
	}

	public Item hideFlags() {
		this.flagsHidden = true;
		return this;
	}

	public Item showFlags() {
		this.flagsHidden = false;
		return this;
	}

	public Item skull(final UUID uuid) {
		return skull(Bukkit.getOfflinePlayer(uuid));
	}

	public Item skull(final OfflinePlayer offlinePlayer) {
		this.skullOwner = offlinePlayer;
		return this;
	}


	public ItemStack create() {
		final ItemStack item = new ItemStack(this.material, this.amount);
		if (!this.enchants.isEmpty())
			item.addUnsafeEnchantments(this.enchants);
		final ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			if (this.name != null)
				meta.setDisplayName(Text.colorize(this.name));
			if (this.lore != null)
				meta.setLore(Text.colorize(this.lore));
			meta.setUnbreakable(this.unbreakable);

			if (this.damage != 0) {
				item.setDurability(this.damage);
			}
			if (this.glow && this.enchants.isEmpty()) {
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			if (this.flagsHidden)
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);

			item.setItemMeta(meta);
			if (item.getType().equals(Material.PLAYER_HEAD)) {
				final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
				if (this.skullOwner != null)
					skullMeta.setOwningPlayer(this.skullOwner);

				item.setItemMeta((ItemMeta) skullMeta);
			}
		}
		return item;
	}
}
	