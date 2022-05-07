package dev.itzrozzadev.core.reflection;

import dev.itzrozzadev.core.exception.BaseException;
import dev.itzrozzadev.core.exception.ReflectionException;
import dev.itzrozzadev.core.text.Text;
import dev.itzrozzadev.core.version.MinecraftVersion;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ReflectionUtil {

	/**
	 * The full package name for NMS
	 */
	public static final String NMS = "net.minecraft.server";

	/**
	 * The package name for Craftbukkit
	 */
	public static final String OBC = "org.bukkit.craftbukkit";

	/**
	 * Simple cache for classes
	 */
	private static final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();
	private static final Collection<String> classNameGuard = ConcurrentHashMap.newKeySet();
	private static final Map<Class<?>, ReflectionData<?>> reflectionDataCache = new ConcurrentHashMap<>();


	public static Class<?> getOBCClass(final String name) {
		String version = MinecraftVersion.getServerVersion();

		if (!version.isEmpty())
			version += ".";

		return ReflectionUtil.lookupClass(OBC + "." + version + name);
	}

	public static <T> Class<T> lookupClass(final String path) {
		if (classCache.containsKey(path))
			return (Class<T>) classCache.get(path);

		if (classNameGuard.contains(path)) {

			return lookupClass(path); // Re run method to see if the cached value now exists.
		}

		try {
			classNameGuard.add(path);

			final Class<?> clazz = Class.forName(path);

			classCache.put(path, clazz);
			reflectionDataCache.computeIfAbsent(clazz, ReflectionData::new);

			return (Class<T>) clazz;

		} catch (final ClassNotFoundException ex) {
			throw new ReflectionException("Could not find class: " + path);

		} finally {
			classNameGuard.remove(path);
		}
	}

	public static <T> T getFieldContent(final Object instance, final String field) {
		return getFieldContent(instance.getClass(), field, instance);
	}

	/**
	 * Get the field content
	 *
	 * @param <T>
	 * @param clazz
	 * @param field
	 * @param instance
	 * @return
	 */
	public static <T> T getFieldContent(Class<?> clazz, final String field, final Object instance) {
		final String originalClassName = clazz.getSimpleName();

		do
			// note: getDeclaredFields() fails if any of the fields are classes that cannot be loaded
			for (final Field f : clazz.getDeclaredFields())
				if (f.getName().equals(field))
					return (T) getFieldContent(f, instance);

		while (!(clazz = clazz.getSuperclass()).isAssignableFrom(Object.class));

		throw new ReflectionException("No such field " + field + " in " + originalClassName + " or its superclasses");
	}

	public static Object getFieldContent(final Field field, final Object instance) {
		try {
			field.setAccessible(true);

			return field.get(instance);

		} catch (final ReflectiveOperationException e) {
			throw new ReflectionException("Could not get field " + field.getName() + " in instance " + (instance != null ? instance : field).getClass().getSimpleName());
		}
	}

	public static Constructor<?> getConstructor(@NonNull final String classPath, final Class<?>... params) {
		final Class<?> clazz = lookupClass(classPath);

		return getConstructor(clazz, params);
	}

	public static Constructor<?> getConstructor(@NonNull final Class<?> clazz, final Class<?>... params) {
		try {
			if (reflectionDataCache.containsKey(clazz))
				return reflectionDataCache.get(clazz).getConstructor(params);

			final Constructor<?> constructor = clazz.getConstructor(params);
			constructor.setAccessible(true);

			return constructor;

		} catch (final ReflectiveOperationException ex) {
			ex.printStackTrace();
			throw new BaseException("Could not get constructor of " + clazz + " with parameters " + Arrays.toString(params));
		}
	}


	private static final class ReflectionData<T> {
		private final Class<T> clazz;

		ReflectionData(final Class<T> clazz) {
			this.clazz = clazz;
		}

		//private final Map<String, Collection<Method>> methodCache = new ConcurrentHashMap<>();
		private final Map<Integer, Constructor<?>> constructorCache = new ConcurrentHashMap<>();
		private final Map<String, Field> fieldCache = new ConcurrentHashMap<>();
		private final Collection<String> fieldGuard = ConcurrentHashMap.newKeySet();
		private final Collection<Integer> constructorGuard = ConcurrentHashMap.newKeySet();

		public void cacheConstructor(final Constructor<T> constructor) {
			final List<Class<?>> classes = new ArrayList<>();

			for (final Class<?> param : constructor.getParameterTypes()) {
				if (param == null) {
					Bukkit.getLogger().severe("(!) Argument cannot be null when instatiating " + clazz);
					return;
				}
				classes.add(param);
			}

			constructorCache.put(Arrays.hashCode(classes.toArray(new Class<?>[0])), constructor);
		}

		public Constructor<T> getDeclaredConstructor(final Class<?>... paramTypes) throws NoSuchMethodException {
			final Integer hashCode = Arrays.hashCode(paramTypes);

			if (constructorCache.containsKey(hashCode))
				return (Constructor<T>) constructorCache.get(hashCode);

			if (constructorGuard.contains(hashCode)) {
				return getDeclaredConstructor(paramTypes);
			}

			constructorGuard.add(hashCode);

			try {
				final Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);

				cacheConstructor(constructor);

				return constructor;

			} finally {
				constructorGuard.remove(hashCode);
			}
		}

		public Constructor<T> getConstructor(final Class<?>... paramTypes) throws NoSuchMethodException {
			final Integer hashCode = Arrays.hashCode(paramTypes);

			if (constructorCache.containsKey(hashCode))
				return (Constructor<T>) constructorCache.get(hashCode);

			if (constructorGuard.contains(hashCode)) {


				return getConstructor(paramTypes);
			}

			constructorGuard.add(hashCode);

			try {
				final Constructor<T> constructor = clazz.getConstructor(paramTypes);

				cacheConstructor(constructor);

				return constructor;

			} finally {
				constructorGuard.remove(hashCode);
			}
		}

		public void cacheField(final Field field) {
			fieldCache.put(field.getName(), field);
		}

		public Field getDeclaredField(final String name) throws NoSuchFieldException {

			if (fieldCache.containsKey(name))
				return fieldCache.get(name);

			if (fieldGuard.contains(name)) {

				return getDeclaredField(name);
			}

			fieldGuard.add(name);

			try {
				final Field field = clazz.getDeclaredField(name);

				cacheField(field);

				return field;

			} finally {
				fieldGuard.remove(name);
			}
		}
	}

	public static Method getMethod(final Class<?> clazz, final String methodName) {
		for (final Method method : clazz.getMethods())
			if (method.getName().equals(methodName)) {
				method.setAccessible(true);

				return method;
			}

		return null;
	}

	public static <T> T invoke(final String methodName, final Object instance, final Object... params) {
		return invoke(getMethod(instance.getClass(), methodName), instance, params);
	}

	public static <T> T invoke(final Method method, final Object instance, final Object... params) {
		if (method == null) {

			throw new BaseException("(!) Method cannot be null for " + instance);
		}

		try {
			return (T) method.invoke(instance, params);

		} catch (final ReflectiveOperationException ex) {
			throw new ReflectionException(ex, "Could not invoke method " + method + " on instance " + instance + " with params " + Arrays.toString(params));
		}
	}

	public static boolean isClassAvailable(final String path) {
		try {
			if (classCache.containsKey(path))
				return true;

			Class.forName(path);

			return true;

		} catch (final Throwable t) {
			return false;
		}
	}
	public static <T> T instantiate(final String classPath) {
		final Class<T> clazz = lookupClass(classPath);

		return instantiate(clazz);
	}
	public static <T> T instantiate(final Class<T> clazz) {
		try {
			final Constructor<T> constructor;

			if (reflectionDataCache.containsKey(clazz))
				constructor = ((ReflectionData<T>) reflectionDataCache.get(clazz)).getDeclaredConstructor();

			else
				constructor = clazz.getDeclaredConstructor();

			constructor.setAccessible(true);

			return constructor.newInstance();

		} catch (final ReflectiveOperationException ex) {
			throw new ReflectionException(ex, "Could not make instance of: " + clazz);
		}
	}
	public static <T> T instantiate(final Constructor<T> constructor, final Object... params) {
		try {
			return constructor.newInstance(params);

		} catch (final ReflectiveOperationException ex) {
			throw new BaseException("(!) Could not make new instance of " + constructor + " with params: " + params, ex.toString());
		}
	}

}