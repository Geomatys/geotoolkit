/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util.converter;

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;


/**
 * A central place where to register converters. The {@linkplain #system system} register is
 * initialized automatically with conversions between some basic Java and Geotoolkit object, like
 * conversions between {@link java.util.Date} and {@link java.lang.Long}. Those conversions are
 * defined for the lifetime of the JVM.
 * <p>
 * If a temporary set of converters is desired, a new instance of {@code ConverterRegistry}
 * should be created.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
public class ConverterRegistry {
    /**
     * The default system-wide instance.
     */
    private static ConverterRegistry system;

    /**
     * Returns the default system-wide instance.
     *
     * @return The system-wide registry instance.
     */
    public synchronized static ConverterRegistry system() {
        ConverterRegistry s = system;
        if (s == null) {
            s = new ConverterRegistry();
            s.register(StringConverter.Number    .INSTANCE); // Preferred choice for StringConverter.
            s.register(StringConverter.Double    .INSTANCE);
            s.register(StringConverter.Float     .INSTANCE);
            s.register(StringConverter.Long      .INSTANCE);
            s.register(StringConverter.Integer   .INSTANCE);
            s.register(StringConverter.Short     .INSTANCE);
            s.register(StringConverter.Byte      .INSTANCE);
            s.register(StringConverter.Boolean   .INSTANCE);
            s.register(StringConverter.Color     .INSTANCE);
            s.register(StringConverter.Locale    .INSTANCE);
            s.register(StringConverter.Charset   .INSTANCE);
            s.register(StringConverter.File      .INSTANCE); // Most specific first (File, URL, URI).
            s.register(StringConverter.URL       .INSTANCE);
            s.register(StringConverter.URI       .INSTANCE);
            s.register(NumberConverter.Comparable.INSTANCE);
            s.register(NumberConverter.Double    .INSTANCE);
            s.register(NumberConverter.Float     .INSTANCE);
            s.register(NumberConverter.Long      .INSTANCE);
            s.register(NumberConverter.Integer   .INSTANCE);
            s.register(NumberConverter.Short     .INSTANCE);
            s.register(NumberConverter.Byte      .INSTANCE);
            s.register(NumberConverter.Boolean   .INSTANCE);
            s.register(NumberConverter.BigDecimal.INSTANCE);
            s.register(NumberConverter.BigInteger.INSTANCE);
            s.register(NumberConverter.Color     .INSTANCE);
            s.register(NumberConverter.String    .INSTANCE); // Last choice for NumberConverter.
            s.register(DateConverter  .Timestamp .INSTANCE);
            s.register(DateConverter  .Long      .INSTANCE);
            s.register(LongConverter  .Date      .INSTANCE);
            s.register(FileConverter  .URI       .INSTANCE); // The preferred target for File.
            s.register(FileConverter  .URL       .INSTANCE);
            s.register(FileConverter  .String    .INSTANCE);
            s.register(URLConverter   .URI       .INSTANCE); // The preferred target for URL.
            s.register(URLConverter   .File      .INSTANCE);
            s.register(URLConverter   .String    .INSTANCE);
            s.register(URIConverter   .URL       .INSTANCE); // The preferred target for URI.
            s.register(URIConverter   .File      .INSTANCE);
            s.register(URIConverter   .String    .INSTANCE);
            system = s; // Only on success.
        }
        return s;
    }

    /**
     * The map of converters of any kind. All access of this map must be synchronized,
     * including read operations. Note that read operations are sometime followed by a
     * write, so a read/write lock may not be the best match here.
     */
    private final Map<ClassPair<?,?>, ObjectConverter<?,?>> converters;

    /**
     * Creates an initially empty set of object converters.
     */
    public ConverterRegistry() {
        converters = new LinkedHashMap<ClassPair<?,?>, ObjectConverter<?,?>>();
    }

    /**
     * Registers a new converter. This method should be invoked only once for a given converter,
     * typically in class static initializer. For example if a {@code Angle} class is defined,
     * the static initializer of that class could register a converter from {@code Angle} to
     * {@code Double}.
     * <p>
     * This method registers the converter for its {@linkplain ObjectConverter#getTargetClass
     * target class}, some parents of the target class (see below) and every interfaces except
     * {@link Cloneable} which are implemented by the target class and not by the source class.
     * For example a converter producing {@link Double} can be used for clients that just ask
     * for a {@link Number}.
     *
     * {@section Which super-classes of the target class are registered}
     * Consider a converter from class {@code S} to class {@code T} where the two classes
     * area related in a hierarchy as below:
     *
     * {@preformat text
     *   C1
     *   └───C2
     *       ├───C3
     *       │   └───S
     *       └───C4
     *           └───T
     * }
     *
     * Invoking this method will register the given converter for all the following cases:
     * <p>
     * <ul>
     *   <li>{@code S} to {@code T}</li>
     *   <li>{@code S} to {@code C4}</li>
     * </ul>
     * <p>
     * No converter to {@code C2} or {@code C1} will be registered, because an identity converter
     * would be suffisient for those cases.
     *
     * {@section Which sub-classes of the source class are registered}
     * Sub-classes of the source class will be registered on a case-by-case basis when the
     * {@link #converter(Class, Class)} is invoked, because we can not know the set of all
     * sub-classes in advance (and would not necessarly want to register all of them anyway).
     *
     * @param converter The converter to register.
     */
    public void register(final ObjectConverter<?,?> converter) {
        /*
         * If the given converter is a FallbackConverter (maybe obtained from an other
         * ConverterRegistry), unwraps it and registers its component individually.
         */
        if (converter instanceof FallbackConverter<?,?>) {
            final FallbackConverter<?,?> fc = (FallbackConverter<?,?>) converter;
            final ObjectConverter<?,?> primary, fallback;
            synchronized (fc) {
                primary  = fc.converter(true);
                fallback = fc.converter(false);
            }
            register(primary);
            register(fallback);
            return;
        }
        /*
         * Registers an individual converter.
         */
        final Class<?> source = converter.getSourceClass();
        final Class<?> target = converter.getTargetClass();
        final Class<?> stopAt = Classes.findCommonClass(source, target);
        synchronized (converters) {
            for (Class<?> i=target; i!=null && !i.equals(stopAt); i=i.getSuperclass()) {
                @SuppressWarnings({"unchecked","rawtypes"})
                final ClassPair<?,?> key = new ClassPair(source, i);
                register(key, converter);
            }
            /*
             * At this point, the given class and parent classes
             * have been registered. Now registers interfaces.
             */
            for (final Class<?> i : target.getInterfaces()) {
                if (i.isAssignableFrom(source)) {
                    /*
                     * Target interface is already implemented by the source, so
                     * there is no reason to convert the source to that interface.
                     */
                    continue;
                }
                if (Cloneable.class.isAssignableFrom(i)) {
                    /*
                     * Exclude this special case. If we were accepting it, we would basically
                     * provide converters from immutable to mutable objects (e.g. from String
                     * to Locale), which is not something we would like to encourage. Even if
                     * the user really wanted a mutable object, in order to modify it he needs
                     * to known the exact type, so asking for a conversion to Cloneable is too
                     * vague.
                     */
                    continue;
                }
                if (Comparable.class.isAssignableFrom(i) && source.equals(Number.class)) {
                    /*
                     * Exclude this special case. java.lang.Number does not implement Comparable,
                     * but its subclasses do. Accepting this case would lead FactoryRegistry to
                     * offers converter from Number to String, which is not the best move if the
                     * user want to compare numbers.
                     */
                    continue;
                }
                if (!i.isAssignableFrom(source)) {
                    @SuppressWarnings({"unchecked","rawtypes"})
                    final ClassPair<?,?> key = new ClassPair(source, i);
                    register(key, converter);
                }
            }
        }
    }

    /**
     * Registers the given converter under the given key. If a previous converter is already
     * registered for the given key, then there is a choice:
     * <p>
     * <ul>
     *   <li>If one converter {@linkplain ClassPair#isDefining is defining} while the
     *       other is not, then the defining converter replaces the non-defining one.</li>
     *   <li>Otherwise the new converter is registered in addition of the old one in a
     *       chain of fallbacks.</li>
     * </ul>
     *
     * @param key The key under which to register the converter.
     * @param converter The converter to register.
     */
    private void register(final ClassPair<?,?> key, ObjectConverter<?,?> converter) {
        assert Thread.holdsLock(converters);
        final ObjectConverter<?,?> existing = converters.get(key);
        if (existing != null) {
            assert !existing.equals(converter) : key;
            final boolean isDefining = key.isDefining(converter);
            if (key.isDefining(existing) == isDefining) {
                // Both the new converter and the old one are specific or are not specific.
                // Creates a chain of fallbacks.
                converter = FallbackConverter.createUnsafe(existing, converter);
            } else if (!isDefining) {
                // Existing converter is specific while the new one is not.
                // Keep the old converter untouched, discard the new one.
                return;
            } else {
                // New converter is specific while the old one was not.
                // Replace the old converter.
            }
        }
        if (converter != existing) {
            converters.put(key, converter);
        }
    }

    /**
     * Returns a converter for the specified source and target classes.
     *
     * @param  <S> The source class.
     * @param  <T> The target class.
     * @param  source The source class.
     * @param  target The target class, or {@code Object.class} for any.
     * @return The converter from the specified source class to the target class.
     * @throws NonconvertibleObjectException if no converter is found.
     */
    public <S,T> ObjectConverter<S,T> converter(final Class<S> source, final Class<T> target)
            throws NonconvertibleObjectException
    {
        final ClassPair<S,T> key = new ClassPair<S,T>(source, target);
        synchronized (converters) {
            ObjectConverter<?,?> converter = converters.get(key);
            if (converter != null) {
                return key.cast(converter);
            }
            /*
             * At this point, no converter were found explicitly for the given key. Searches a
             * converter accepting some super-class of S, and if we find any cache the result.
             * This is the complement of the search performed in the register(ObjectConverter)
             * method, which looked for the parents of the target class. Here we look for the
             * childs of the source class.
             */
            ClassPair<? super S,T> candidate = key;
            while ((candidate = candidate.parentSource()) != null) {
                converter = converters.get(candidate);
                if (converter != null) {
                    register(key, converter);
                    return key.cast(converter);
                }
            }
        }
        /*
         * No explicit converter were found. Checks for the trivial case where an identity
         * converter would fit. We perform this operation last in order to give a chance to
         * register an explicit converter if we need to.
         */
        if (target.isAssignableFrom(source)) {
            return key.cast(new IdentityConverter<S>(source));
        }
        throw new NonconvertibleObjectException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1, key));
    }

    /**
     * Returns a target class which is both assignable to the given base and convertible from all
     * the given sources. This method is used mostly for converting two objects of different type
     * to some class implementing the {@link Comparable} interface, in order to compare objects
     * that are normally not comparable each other.
     *
     * {@section Example 1: comparing <code>File</code> with <code>URL</code>}
     * {@link java.io.File} implements the {@code Comparable} interface, while {@link java.net.URL}
     * does not. Consequently the code below will return inconditionnaly {@code File} no matter the
     * order of {@code sources} arguments:
     *
     * {@preformat java
     *     Class<? extends Comparable> target = registry.findCommonTarget(Comparable.class, File.class, URL.class);
     * }
     *
     * {@section Example 2: comparing <code>Date</code> with <code>Long</code>}
     * Both {@link Long} and {@link java.util.Date} implement {@code Comparable}, and both types
     * are convertible to the other type. There is no obvious rule for selecting a type instead
     * than the other. In order to keep this method determinist, the code below will prefer the
     * first {@code sources} argument assignable to the common target: {@code Long}.
     *
     * {@preformat java
     *     Class<? extends Comparable> target = registry.findCommonTarget(Comparable.class, Long.class, Date.class);
     * }
     *
     * @param <T>     The type represented by the {@code base} argument.
     * @param base    The base type of the desired target.
     * @param sources The source for which a common target is desired.
     * @return        A target assignable to the given {@code base} and convertible from all sources,
     *                or {@code null} if no suitable target has been found.
     *
     * @since 3.01
     */
    public <T> Class<? extends T> findCommonTarget(final Class<T> base, final Class<?>... sources) {
        if (sources.length == 0) {
            return base;
        }
        /*
         * For each source, get the parent classes of that source that are assignable to the given
         * base. The goal is to take in account (later) an eventual parent which is common to every
         * sources, if there is any. We ignore interfaces on intend.
         */
        @SuppressWarnings({"unchecked","rawtypes"}) // Generic array creation.
        final Set<Class<?>>[] targets = new Set[sources.length];
        for (int i=0; i<sources.length; i++) {
            Class<?> source = sources[i];
            final Set<Class<?>> types = new LinkedHashSet<Class<?>>();
            while (source != null && base.isAssignableFrom(source)) {
                types.add(source);
                source = source.getSuperclass();
            }
            targets[i] = types;
        }
        /*
         * Adds every targets which can be produces by at least one registered converter.
         */
        synchronized (converters) {
            for (final ObjectConverter<?,?> converter : converters.values()) {
                final Class<?> target = converter.getTargetClass();
                if (base.isAssignableFrom(target)) {
                    final Class<?> source = converter.getSourceClass();
                    for (int i=0; i<sources.length; i++) {
                        final Class<?> candidate = sources[i];
                        if (source.isAssignableFrom(candidate)) {
                            targets[i].add(target);
                        }
                    }
                }
            }
        }
        /*
         * Now computes the intersection of every target sets. If more than one class can
         * fit, returns the one which would involve the smallest number of conversions.
         * If many class have the same score, select the one which appear first in the
         * argument list.
         */
        final Set<Class<?>> common = targets[0];
        for (int i=1; i<targets.length; i++) {
            common.retainAll(targets[i]);
        }
        Class<?> best        = null;  // The best target type we have found so far.
        int countAssignables = -1;    // Number of source assignable from the best target type.
        int positionFirst    = -1;    // Position of the first source assignable from the target.
        for (final Class<?> candidate : common) {
            int p=0, c=0;
            for (int i=sources.length; --i>=0;) {
                final Class<?> source = sources[i];
                if (source.isAssignableFrom(candidate)) {
                    p = i; // Position of the first assignable source.
                    c++;   // Number of source assignable from the target.
                }
            }
            /*
             * If the current candidate is less successful than the best target,
             * looks for the next candidate.
             */
            if (c < countAssignables) {
                continue;
            }
            /*
             * If the current candidate is as successful than the best target, then there
             * is a choice:
             *
             * 1) If both the candidate and the best target are instance of Number, "merges"
             *    them by retaining the widest type. For example if the sources array contains
             *    Integer.class and Long.class, selects Long.class.
             *
             * 2) Otherwise retains the candidate if and only it is assignable to a source
             *    which appear before (in argument order) the previously best target.
             */
            if (c == countAssignables) {
                if (Number.class.isAssignableFrom(candidate) && Number.class.isAssignableFrom(best)) {
                    @SuppressWarnings({"unchecked","rawtypes"}) final Class<? extends Number> n1 = (Class) candidate;
                    @SuppressWarnings({"unchecked","rawtypes"}) final Class<? extends Number> n2 = (Class) best;
                    try {
                        best = Classes.widestClass(n1, n2);
                        positionFirst = p;
                        continue;
                    } catch (IllegalArgumentException e) {
                        // At least one subclass of Number is not a known type.
                        // Performs the same processing as if it was not a Number.
                        Logging.recoverableException(ConverterRegistry.class, "getCommonTarget", e);
                    }
                }
                if (p >= positionFirst) {
                    continue;
                }
            }
            positionFirst = p;
            countAssignables = c;
            best = candidate;
        }
        return (best != null) ? best.asSubclass(base) : null;
    }

    /**
     * Returns a string representation of registered converters.
     * Used mostly for debugging purpose.
     *
     * @return A string representation of registered converters.
     */
    @Override
    public String toString() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(Classes.getShortClassName(this));
        synchronized (converters) {
            for (final Map.Entry<ClassPair<?,?>, ObjectConverter<?,?>> entry : converters.entrySet()) {
                final DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey());
                final ObjectConverter<?,?> value = entry.getValue();
                if (value instanceof FallbackConverter<?,?>) {
                    ((FallbackConverter<?,?>) value).toTree(node);
                }
                root.add(node);
            }
        }
        return Trees.toString(root);
    }
}
