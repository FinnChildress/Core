package dev.itzrozzadev.core.version;

import dev.itzrozzadev.core.exception.BaseException;
import lombok.Getter;
import org.bukkit.Bukkit;

public final class MinecraftVersion {

	private static String serverVersion;

	@Getter
	private static V current;

	public enum V {
		v1_21(21),
		v1_20(20),
		v1_19(19),
		v1_18(18),
		v1_17(17),
		v1_16(16),
		v1_15(15),
		v1_14(14),
		v1_13(13),
		v1_12(12),
		v1_11(11),
		v1_10(10),
		v1_9(9),
		v1_8(8),
		v1_7(7),
		v1_6(6),
		v1_5(5),
		v1_4(4),
		v1_3_AND_BELOW(3);

		private final int minorVersionNumber;

		@Getter
		private final boolean tested;

		V(final int version) {
			this(version, true);
		}

		V(final int version, final boolean tested) {
			this.minorVersionNumber = version;
			this.tested = tested;
		}

		private static V parse(final int number) {
			for (final V v : values())
				if (v.minorVersionNumber == number)
					return v;

			throw new BaseException("Invalid version number: " + number);
		}

		/**
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return "1." + this.minorVersionNumber;
		}
	}

	public static boolean equals(final V version) {
		return compareWith(version) == 0;
	}

	public static boolean olderThan(final V version) {
		return compareWith(version) < 0;
	}

	public static boolean newerThan(final V version) {
		return compareWith(version) > 0;
	}

	public static boolean atLeast(final V version) {
		return equals(version) || newerThan(version);
	}

	private static int compareWith(final V version) {
		try {
			return getCurrent().minorVersionNumber - version.minorVersionNumber;

		} catch (final Throwable throwable) {
			throwable.printStackTrace();

			return 0;
		}
	}

	/**
	 * Return the class versioning such as v1_14_R1
	 *
	 * @return
	 */
	public static String getServerVersion() {
		return serverVersion.equals("craftbukkit") ? "" : serverVersion;
	}

	// Initialize the version
	static {
		try {

			final String packageName = Bukkit.getServer() == null ? "" : Bukkit.getServer().getClass().getPackage().getName();
			final String curr = packageName.substring(packageName.lastIndexOf('.') + 1);
			final boolean hasGatekeeper = !"craftbukkit".equals(curr) && !"".equals(packageName);

			serverVersion = curr;

			if (hasGatekeeper) {
				int pos = 0;

				for (final char ch : curr.toCharArray()) {
					pos++;

					if (pos > 2 && ch == 'R')
						break;
				}

				final String numericVersion = curr.substring(1, pos - 2).replace("_", ".");

				int found = 0;

				for (final char ch : numericVersion.toCharArray())
					if (ch == '.')
						found++;

				if (found != 1) {
					Bukkit.getLogger().severe("(!) Minecraft Version checker malfunction. Could not detect your server version. Detected: " + numericVersion + " Current: " + curr);
				} else {

					current = V.parse(Integer.parseInt(numericVersion.split("\\.")[1]));
				}

			} else
				current = V.v1_3_AND_BELOW;

		} catch (final Throwable throwable) {
			Bukkit.getLogger().severe("(!) Error detecting your Minecraft version. Check your server compatibility.");
			throwable.printStackTrace();
		}
	}
}