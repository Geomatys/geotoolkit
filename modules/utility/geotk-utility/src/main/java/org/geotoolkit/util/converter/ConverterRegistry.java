/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import net.jcip.annotations.ThreadSafe;
import org.apache.sis.measure.Angle;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.Classes;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;


/**
 * A collection of {@linkplain ObjectConverter Object Converters}. A converter from the given
 * <var>source type</var> to the given <var>target type</var> can be obtained by a call to
 * {@link #converter(Class, Class)}. If no converter exists for the given source and target
 * types, then this registry searches for a suitable converter accepting a parent class of the
 * given source type, or returning a sub-class of the given target type.
 * <p>
 * New instances of {@code ConverterRegistry} are initially empty. Custom converters must be
 * explicitly {@linkplain #register registered}. However a system-wide registry initialized
 * with default converters is provided by the {@link #system()} method.
 *
 * {@section Note about conversions from interfaces}
 * {@code ConverterRegistry} is primarily designed for handling converters from classes to
 * other classes. Handling of interfaces are not prohibited (and actually sometime supported),
 * but their behavior may be more ambiguous than in the case of classes because of
 * multi-inheritance in interface hierarchy.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
public class ConverterRegistry {
    /**
     * Returns the default system-wide instance. This register is initialized automatically
     * with conversions between some basic Java and Geotk objects, like conversions between
     * {@link java.util.Date} and {@link java.lang.Long}. Those conversions are defined for
     * the lifetime of the JVM.
     * <p>
     * If a temporary set of converters is desired, a new instance of {@code ConverterRegistry}
     * should be created explicitly instead.
     *
     * {@section Adding system-wide converters}
     * Applications can add system-wide custom providers either by explicit call to the
     * {@link #register(ObjectConverter)} method on the system converter, or by listing
     * the fully qualified classnames of their {@link ObjectConverter} instances in the
     * following file (see {@link ServiceLoader} for more info about services loading):
     *
     * {@preformat text
     *     META-INF/services/org.geotoolkit.util.converter.ObjectConverter
     * }
     *
     * @return The system-wide registry instance.
     */
    public static ConverterRegistry system() {
        return System.INSTANCE;
    }

    /**
     * The default system-wide instance.
     */
    private static final class System extends HeuristicRegistry {
        /** The singleton instance. */
        static final System INSTANCE = new System();

        /** Creates a new system-wide converter registry. */
        private System() {
            register(StringConverter.Number             .INSTANCE); // Preferred choice for StringConverter.
            register(StringConverter.Double             .INSTANCE);
            register(StringConverter.Float              .INSTANCE);
            register(StringConverter.Long               .INSTANCE);
            register(StringConverter.Integer            .INSTANCE);
            register(StringConverter.Short              .INSTANCE);
            register(StringConverter.Byte               .INSTANCE);
            register(StringConverter.Boolean            .INSTANCE);
            register(StringConverter.BigDecimal         .INSTANCE);
            register(StringConverter.BigInteger         .INSTANCE);
            register(StringConverter.Color              .INSTANCE);
            register(StringConverter.Locale             .INSTANCE);
            register(StringConverter.Charset            .INSTANCE);
            register(StringConverter.InternationalString.INSTANCE);
            register(StringConverter.File               .INSTANCE); // Most specific first (File, URL, URI).
            register(StringConverter.URL                .INSTANCE);
            register(StringConverter.URI                .INSTANCE);
            register(NumberConverter.Comparable         .INSTANCE);
            register(NumberConverter.Double             .INSTANCE);
            register(NumberConverter.Float              .INSTANCE);
            register(NumberConverter.Long               .INSTANCE);
            register(NumberConverter.Integer            .INSTANCE);
            register(NumberConverter.Short              .INSTANCE);
            register(NumberConverter.Byte               .INSTANCE);
            register(NumberConverter.Boolean            .INSTANCE);
            register(NumberConverter.BigDecimal         .INSTANCE);
            register(NumberConverter.BigInteger         .INSTANCE);
            register(NumberConverter.Color              .INSTANCE);
            register(NumberConverter.String             .INSTANCE); // Last choice for NumberConverter.
            register(DateConverter  .Timestamp          .INSTANCE);
            register(DateConverter  .SQL                .INSTANCE);
            register(DateConverter  .Long               .INSTANCE);
            register(LongConverter  .Date               .INSTANCE);
            register(FileConverter  .URI                .INSTANCE); // The preferred target for File.
            register(FileConverter  .URL                .INSTANCE);
            register(FileConverter  .String             .INSTANCE);
            register(URLConverter   .URI                .INSTANCE); // The preferred target for URL.
            register(URLConverter   .File               .INSTANCE);
            register(URLConverter   .String             .INSTANCE);
            register(URIConverter   .URL                .INSTANCE); // The preferred target for URI.
            register(URIConverter   .File               .INSTANCE);
            register(URIConverter   .String             .INSTANCE);
            /*
             * Following converters were declared in the Angle static initializer.
             * They temporarily moved here as converters are not yet ported to SIS.
             */
            register(new SimpleConverter<Angle,Double>() {
                @Override public Class<Angle>  getSourceClass()      {return Angle .class;}
                @Override public Class<Double> getTargetClass()      {return Double.class;}
                @Override public Double        convert(Angle o)      {return o.degrees();}
            });
            register(new SimpleConverter<Double,Angle>() {
                @Override public Class<Double> getSourceClass()      {return Double.class;}
                @Override public Class<Angle>  getTargetClass()      {return Angle .class;}
                @Override public Angle         convert(Double value) {return new Angle(value);}
            });
            /*
             * Registration of converter working on interfaces
             * (more tricky than class, see javadoc).
             */
            register(CollectionConverter.List.INSTANCE);
            register(CollectionConverter.Set .INSTANCE);
            /*
             * Now add the custom converters, if any.
             */
            @SuppressWarnings("rawtypes")
            final Iterator<ObjectConverter> it = ServiceLoader.load(ObjectConverter.class).iterator();
            while (it.hasNext()) {
                register(it.next());
            }
        }
    }

    /**
     * The map of converters of any kind. All access of this map must be synchronized,
     * including read operations. Note that read operations are sometime followed by a
     * write, so a read/write lock may not be the best match here.
     */
    private final Map<ClassPair<?,?>, ObjectConverter<?,?>> converters;

    /**
     * The map to be returned by {@link #getConvertibleTypes()}, created when first needed.
     */
    private transient Map<Class<?>, Set<Class<?>>> convertibleTypes;

    /**
     * Creates an initially empty set of object converters.
     */
    public ConverterRegistry() {
        converters = new LinkedHashMap<>();
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
     * are related in a hierarchy as below:
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
     *   <li>{@code S} &rarr; {@code T}</li>
     *   <li>{@code S} &rarr; {@code C4}</li>
     * </ul>
     * <p>
     * No {@code S} &rarr; {@code C2} or {@code S} &rarr; {@code C1} converter will be registered,
     * because an identity converter would be sufficient for those cases.
     *
     * {@section Which sub-classes of the source class are registered}
     * Sub-classes of the source class will be registered on a case-by-case basis when the
     * {@link #converter(Class, Class)} is invoked, because we can not know the set of all
     * sub-classes in advance (and would not necessarily want to register all of them anyway).
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
        assert converter.getSourceClass().isAssignableFrom(key.sourceClass) : converter;
        assert key.targetClass.isAssignableFrom(converter.getTargetClass()) : converter;
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
            convertibleTypes = null;
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
        final ClassPair<S,T> key = new ClassPair<>(source, target);
        synchronized (converters) {
            ObjectConverter<S,T> converter = key.cast(converters.get(key));
            if (converter != null) {
                return converter;
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
                converter = key.cast(converters.get(candidate));
                if (converter != null) {
                    register(key, converter);
                    return converter;
                }
            }
            /*
             * No converter found. Gives a chance to subclasses to provide dynamically-generated
             * converter. The default implementation does not provide any.
             */
            converter = createConverter(source, target);
            if (converter != null) {
                register(key, converter);
                return converter;
            }
        }
        /*
         * No explicit converter were found. Checks for the trivial case where an identity
         * converter would fit. We perform this operation last in order to give a chance to
         * register an explicit converter if we need to.
         */
        if (target.isAssignableFrom(source)) {
            return key.cast(IdentityConverter.create(source));
        }
        throw new NonconvertibleObjectException(Errors.format(Errors.Keys.CANT_CONVERT_FROM_TYPE_$2, source, target));
    }

    /**
     * Creates a new converter for the given source and target types, or {@code null} if none.
     * This method is invoked by <code>{@linkplain #converter converter}(source, target)</code>
     * when no registered converter were found for the given types. The default implementation
     * returns {@code null} if all cases. Subclasses can override this method in order to
     * generate some converters dynamically.
     * <p>
     * Note that the source and target classes of the returned converter must match exactly
     * the provided arguments. This method is not allowed to return a more generic converter.
     *
     * @param  <S> The source class.
     * @param  <T> The target class.
     * @param  source The source class.
     * @param  target The target class, or {@code Object.class} for any.
     * @return A newly generated converter from the specified source class to the target class,
     *         or {@code null} if none.
     *
     * @since 3.02
     */
    <S,T> ObjectConverter<S,T> createConverter(final Class<S> source, final Class<T> target) {
        return null;
    }

    /**
     * Returns a target class which is both assignable to the given base and convertible from all
     * the given sources. This method is used mostly for converting two objects of different type
     * to some class implementing the {@link Comparable} interface, in order to compare objects
     * that are normally not comparable each other.
     *
     * {@section Example 1: comparing <code>File</code> with <code>URL</code>}
     * {@link java.io.File} implements the {@code Comparable} interface, while {@link java.net.URL}
     * does not. Consequently the code below will return unconditionally {@code File} no matter the
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
            final Set<Class<?>> types = new LinkedHashSet<>();
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
                        best = Numbers.widestClass(n1, n2);
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
     * Returns the types of all objects than can be converted, together with their possible
     * target types. The map returned by this method is not live - it is a "snapshot" of this
     * registry at invocation time.
     *
     * @return The mapping from source to target types. Keys are source types, and values are
     *         all possible target types for the source type.
     *
     * @since 3.20
     */
    public Map<Class<?>, Set<Class<?>>> getConvertibleTypes() {
        synchronized (converters) {
            if (convertibleTypes == null) {
                final Map<Class<?>, Set<Class<?>>> mapping = new LinkedHashMap<>();
                for (final ClassPair<?,?> pair : converters.keySet()) {
                    Set<Class<?>> targets = mapping.get(pair.sourceClass);
                    if (targets == null) {
                        targets = new LinkedHashSet<>();
                        mapping.put(pair.sourceClass, targets);
                    }
                    targets.add(pair.targetClass);
                }
                // Make the map unmodifiable.
                for (final Map.Entry<Class<?>, Set<Class<?>>> entry : mapping.entrySet()) {
                    entry.setValue(XCollections.unmodifiableSet(entry.getValue()));
                }
                convertibleTypes = XCollections.unmodifiableMap(mapping);
            }
        }
        return convertibleTypes;
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
