package dev.itzrozzadev.core.command;

import dev.itzrozzadev.core.exception.BaseException;
import dev.itzrozzadev.core.messages.Messenger;
import dev.itzrozzadev.core.player.PlayerUtil;
import dev.itzrozzadev.core.plugin.BasePlugin;
import dev.itzrozzadev.core.task.Task;
import dev.itzrozzadev.core.text.Replacer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Cooldown
 */
public abstract class BaseCommand extends Command {

	private String label;
	private boolean registered = false;
	@Getter
	@Setter
	private int minArguments = 0;

	@Setter
	private boolean autoHandleHelp = true;
	protected CommandSender sender;
	protected String[] args;

	protected List<String> getHelpLabel() {
		return Arrays.asList("help", "?");
	}

	protected BaseCommand(final String label) {
		this(parseLabel(label), parseAliases(label));
	}

	protected BaseCommand(final String label, final List<String> aliases) {

		super(label);
		setLabel(label);
		if (aliases != null)
			setAliases(aliases);

		setPermission(getDefaultPermission());
	}

	private static String parseLabel(final String label) {
		return label.split("([|/])")[0];
	}

	private static List<String> parseAliases(final String label) {
		final String[] aliases = label.split("([|/])");

		return aliases.length > 0 ? Arrays.asList(Arrays.copyOfRange(aliases, 1, aliases.length)) : new ArrayList<>();
	}

	protected String getHelpMessage() {
		return "/{label} {usage} - {description} {permission}";
	}

	protected String getDefaultPermission() {
		return BasePlugin.getNamed().toLowerCase() + ".command.{label}";
	}

	@Override
	public String getPermissionMessage() {
		return Messenger.getErrorPrefix() + "You do not have permission to use this command.";
	}

	public final void register() {
		register(true);
	}

	public final void register(final boolean unregisterOldAliases) {
		this.register(true, unregisterOldAliases);
	}

	public final void register(final boolean unregisterOldCommand, final boolean unregisterOldAliases) {
		if (!canRegister())
			return;

		final PluginCommand oldCommand = Bukkit.getPluginCommand(getLabel());
		final boolean shouldUnregisterOldCommand = oldCommand != null && unregisterOldCommand;

		if (shouldUnregisterOldCommand) {
			final String owningPlugin = oldCommand.getPlugin().getName();
			if (!owningPlugin.equals(BasePlugin.getNamed()))
				Messenger.log("(!) Command /" + getLabel() + " already (" + owningPlugin + "), overriding and unregistering /" + oldCommand.getLabel() + ", /" + String.join(", /", oldCommand.getAliases()));

			CommandRegistry.unregisterCommand(oldCommand.getLabel(), unregisterOldAliases);
		}

		CommandRegistry.registerCommand(this);
		registered = true;
	}

	public final void unregister() {
		if (this instanceof BaseSubCommand) {
			Bukkit.getLogger().warning("(!) You can't unregister a subcommand!");
			return;
		}
		if (!registered) {
			Bukkit.getLogger().warning("(!) Command /" + getLabel() + " is not registered!");
			return;
		}
		CommandRegistry.unregisterCommand(getLabel());
		registered = false;
	}

	protected boolean canRegister() {
		return true;
	}

	@Override
	public final boolean execute(final CommandSender sender, final String label, final String[] args) {

		if (BasePlugin.isReloading() || !BasePlugin.getInstance().isEnabled()) {
			Messenger.send(sender, "(!) Cannot execute command while reloading!");

			return false;
		}

		this.sender = sender;
		this.label = label;
		this.args = args;

		final String sublabel = this instanceof BaseSubCommand ? " " + ((BaseSubCommand) this).getSubLabel() : "";

		try {
			if (getPermission() != null)
				if (!checkPerm(getPermission())) return false;
			if (args.length < getMinArguments() || autoHandleHelp && args.length == 1 && (getHelpLabel().contains(args[0].toLowerCase()))) {
				Task.runLaterAsync(() -> {
					if(getMultilineHelpMessage() == null){
						final String usage =  getUsage().length() > 0  ? getUsage() : null;
						final String description = getDescription().length() > 0 ? getDescription() : null;

						Messenger.send(sender, Replacer.replace(getHelpMessage(),
								"label", getLabel(),
								"usage", usage,
								"description", description,
								"permission", "&f" + (sender.isOp() ? getPermission() != null ? " &8- &f" + getPermission() : " &8- &f" + getDefaultPermission() : "")
						));
					}else {
						Messenger.send(sender, getMultilineHelpMessage());
					}

				});
				return true;
			}
			onCommand();
		} catch (final BaseException ex) {
			if (ex.getMessages() != null)
				Messenger.send(sender, ex.getMessages());


		}
		return true;
	}


	protected abstract void onCommand();

	@Override
	public final List<String> tabComplete(final CommandSender sender, final String alias, final String[] args, final Location location) throws IllegalArgumentException {
		return tabComplete(sender, alias, args);
	}

	@Override
	public final List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
		this.sender = sender;
		label = alias;
		this.args = args;

		if (hasPerm(getPermission())) {
			List<String> suggestions = tabComplete();
			if (suggestions == null)
				suggestions = new ArrayList<>();
			return suggestions;
		}
		return new ArrayList<>();
	}


	protected List<String> tabComplete() {
		return null;
	}

	protected String[] getMultilineHelpMessage() {
		return null;
	}


	protected final boolean isPlayer() {
		return sender instanceof Player;
	}

	protected final void checkConsole() throws CommandException {
		if (!isPlayer())
			throw new CommandException("(!) You must be a player to execute this command!");
	}

	public final boolean checkPerm(@NonNull final String permission) throws CommandException {
		if (isPlayer() && !hasPerm(permission)) {
			if (getPermissionMessage() != null)
				Messenger.send(sender, getPermissionMessage().replace("{permission}", permission));
			return false;
		}

		return true;
	}

	public final boolean checkPerm(@NonNull final CommandSender sender, @NonNull final String permission) throws CommandException {
		if (isPlayer() && !hasPerm(permission)) {
			if (getPermissionMessage() != null)
				Messenger.send(sender, getPermissionMessage().replace("{permission}", permission));
			return false;
		}

		return true;
	}

	protected final boolean hasPerm(final String permission) {
		return hasPerm(sender, permission);
	}

	protected final boolean hasPerm(final CommandSender sender, final String permission) {
		return permission == null || PlayerUtil.hasPermission(sender, permission.replace("{label}", getLabel()));
	}
}
