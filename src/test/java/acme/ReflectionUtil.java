/*
 * Copyright 2016-2017 Daniel Siviter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package acme;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import javax.annotation.Nonnull;

/**
 * A set of utility methods for performing reflection activities.
 * 
 * @author Daniel Siviter
 * @since v1.0 [12 Jul 2016]
 */
public enum ReflectionUtil { ;
	/**
	 * @param <T> the type to return.
	 * @param source the source object.
	 * @param name the name of the field.
	 * @return the value of the field.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	public static <T> T get(@Nonnull Object source, @Nonnull String name) {
		return get(source, name, null);
	}

	/**
	 * @param <T> the type to return.
	 * @param source the source object.
	 * @param name the name of the field.
	 * @param type the field type. This can {@code null}.
	 * @return the value of the field.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(@Nonnull Object source, @Nonnull String name, Class<T> type) {
		try {
			final Field field = findField(source.getClass(), name, type);
			if (field == null) {
				throw new IllegalArgumentException(
						String.format("Unable to find '%s' on '%s'!", name, source.getClass()));
			}
			setAccessible(field, source);
			return (T) field.get(source);
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException(String.format(
					"Unexpected reflection exception - %s", ex.getClass().getName()), ex);
		}
	}

	/**
	 * @param <T> the type to return.
	 * @param sourceCls the source object class.
	 * @param name the name of the field.
	 * @param type the field type. This can {@code null}.
	 * @return the value of the field.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(@Nonnull Class<?> sourceCls, @Nonnull String name, Class<T> type) {
		try {
			final Field field = findField(sourceCls, name, type);
			if (field == null) {
				throw new IllegalArgumentException(
						String.format("Unable to find '%s' on '%s'!", name, sourceCls));
			}
			setAccessible(field, null);
			return (T) field.get(null);
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException(String.format(
					"Unexpected reflection exception - %s", ex.getClass().getName()), ex);
		}
	}

	/**
	 * @param source the source object.
	 * @param name the name of the field.
	 * @param value the value to set on the field.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	public static void set(@Nonnull Object source, @Nonnull String name, Object value) {
		set(source, name, null, value);
	}

	/**
	 * @param target the target object.
	 * @param name the name of the field.
	 * @param type the field type. This can {@code null}.
	 * @param value the value to set on the field.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	public static void set(@Nonnull Object target, @Nonnull String name, Class<?> type, Object value) {
		try {
			final Field field = findField(target.getClass(), name, type);
			if (field == null) {
				throw new IllegalArgumentException("Unable to find '" + name + "' on '" + target.getClass() + "'!");
			}
			setAccessible(field, target);
			field.set(target, value);
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException(String.format(
					"Unexpected reflection exception - %s", ex.getClass().getName()), ex);
		}
	}

	/**
	 * @param <T> the type to return.
	 * @param source the source object.
	 * @param name the name of the method.
	 * @param args the argument values.
	 * @return the return value of the method. If this is a {@code void} method the value will be {@code null}.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	public static <T> T invoke(@Nonnull Object source, @Nonnull String name, Object... args) {
		Class<?>[] argTypes = new Class<?>[args.length];
		for (int i = 0; i< args.length; i++) {
			argTypes[i] = args[i] == null ? Object.class : args[i].getClass();
		}

		final Method method = findMethod(source.getClass(), name, argTypes);
		if (method == null) {
			throw new IllegalArgumentException(
					String.format("Unable to find '%s' with '%s' on '%s'!",
							name, Arrays.toString(argTypes), source.getClass()));
		}
		return invoke(source, method, args);
	}

	/**
	 * @param <T> the type to return.
	 * @param source the source object.
	 * @param method the method.
	 * @param args the argument values.
	 * @return the return value of the method. If this is a {@code void} method the value will be {@code null}.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(@Nonnull Object source, @Nonnull Method method, Object... args) {
		try {
			setAccessible(method, source);
			return (T) method.invoke(source, args);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			throw new IllegalStateException(String.format(
					"Unexpected reflection exception - %s", ex.getClass().getName()), ex);
	}
	}

	/**
	 * @param clazz the class type to inspect.
	 * @param name the name of the field.
	 * @param type the field type. This can {@code null}.
	 * @return the found field or {@code null} if not.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	public static Field findField(@Nonnull Class<?> clazz, @Nonnull String name, Class<?> type) {
		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			for (Field field : searchType.getDeclaredFields()) {
				if (name.equals(field.getName()) && (type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * @param clazz the class type to inspect.
	 * @param name the name of the method.
	 * @param params the parameter types. This can {@code null}.
	 * @return the found method or {@code null} if not.
	 * @throws IllegalStateException if there is an issue in accessing.
	 */
	public static Method findMethod(@Nonnull Class<?> clazz, @Nonnull String name, Class<?>... params) {
		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			for (Method method : searchType.getDeclaredMethods()) {
				if (name.equals(method.getName()) && Arrays.equals(params, method.getParameterTypes())) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * @param field the field to set as accessible.
	 * @param obj the object instance or {@code null} if {@code static} field.
	 */
	public static void setAccessible(@Nonnull Field field, Object obj) {
		if ((!Modifier.isPublic(field.getModifiers()) ||
				!Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
				Modifier.isFinal(field.getModifiers())) &&
				!field.canAccess(obj))
		{
			field.setAccessible(true);
		}
	}

	/**
	 * @param method the method to set as accessible.
	 * @param obj the object instance or {@code null} if {@code static} method.
	 */
	public static void setAccessible(@Nonnull Method method, Object obj) {
		if ((!Modifier.isPublic(method.getModifiers()) ||
				!Modifier.isPublic(method.getDeclaringClass().getModifiers()) ||
				Modifier.isFinal(method.getModifiers())) &&
				!method.canAccess(obj))
		{
			method.setAccessible(true);
		}
	}

	/**
	 * @param <A> annotation type.
	 * @param clazz the class to inspect.
	 * @param annotation the annotation type wanted.
	 * @return the annotation instance.
	 */
	public static <A extends Annotation> A getAnnotation(@Nonnull Class<?> clazz, @Nonnull Class<A> annotation) {
		return annotation.cast(clazz.getAnnotation(annotation));
	}

	/**
	 * @param <A> annotation type.
	 * @param source the object to inspect.
	 * @param annotation the annotation type wanted.
	 * @return the annotation instance.
	 */
	public static <A extends Annotation> A getAnnotation(@Nonnull Object source, @Nonnull Class<A> annotation) {
		return getAnnotation(source.getClass(), annotation);
	}

	/**
	 * @param <A> annotation type.
	 * @param <V> the return value type.
	 * @param clazz the class to inspect.
	 * @param annotation the annotation to look for.
	 * @param name the name of the annotation value wanted.
	 * @return the value of the named annotation value or {@code null} if not found.
	 */
	public static <V, A extends Annotation> V getAnnotationValue(@Nonnull Class<?> clazz, @Nonnull Class<A> annotation, @Nonnull String name) {
		final A a = getAnnotation(clazz, annotation);
		return a != null ? get(a, name) : null;
	}

	/**
	 * @param <A> annotation type.
	 * @param <V> the return value type.
	 * @param clazz the class to inspect.
	 * @param annotation the annotation to look for.
	 * @return the value of the annotation {@code value} or {@code null} if not found.
	 */
	public static <V, A extends Annotation> V getAnnotationValue(@Nonnull Class<?> clazz, @Nonnull Class<A> annotation) {
		return getAnnotationValue(clazz, annotation, "value");
	}

	/**
	 * @param <A> annotation type.
	 * @param <V> the return value type.
	 * @param obj the instance to inspect.
	 * @param annotation the annotation to look for.
	 * @return the annotation value or {@code null} if not found.
	 */
	public static <V, A extends Annotation> V getAnnotationValue(Object obj, Class<A> annotation) {
		return getAnnotationValue(obj.getClass(), annotation);
	}
}
