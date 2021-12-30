/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.factory;

import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.sis.io.TableAppender;
import org.apache.sis.util.logging.Logging;
import static org.apache.sis.internal.util.CollectionsExt.unmodifiableOrCopy;
import org.opengis.util.InternationalString;


/**
 * A set of hints providing control on factories to be used. They provides a way to control
 * low-level details. When hints are used in conjunction with {@linkplain FactoryRegistry
 * factory registry} (the Geotk service discovery mechanism), we have the complete Geotk
 * plugin system. By using hints to allow application code to effect service discovery,
 * we allow client code to retarget the Geotk library for their needs.
 *
 * Hints may be ignored if they do not apply to the object to be instantiated.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 *
 * @see Factory
 * @see FactoryRegistry
 */
public class Hints extends RenderingHints {

    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////              Coordinate Reference Systems              ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.referencing.crs.CRSFactory} instance to use.
     *
     * @see FactoryFinder#getCRSFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey CRS_FACTORY = new ClassKey(
            "org.opengis.referencing.crs.CRSFactory");

    /**
     * The {@link org.opengis.referencing.cs.CSFactory} instance to use.
     *
     * @see FactoryFinder#getCSFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey CS_FACTORY = new ClassKey(
            "org.opengis.referencing.cs.CSFactory");

    /**
     * The {@link org.opengis.referencing.datum.DatumFactory} instance to use.
     *
     * @see FactoryFinder#getDatumFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey DATUM_FACTORY = new ClassKey(
            "org.opengis.referencing.datum.DatumFactory");

    /**
     * The {@link org.opengis.referencing.operation.MathTransformFactory} instance to use.
     *
     * @see FactoryFinder#getMathTransformFactory(Hints)
     * @category Referencing
     */
    public static final ClassKey MATH_TRANSFORM_FACTORY = new ClassKey(
            "org.opengis.referencing.operation.MathTransformFactory");

    /**
     * The default {@link org.opengis.referencing.crs.CoordinateReferenceSystem}
     * to use. This is used by some factories capable to provide a default CRS
     * when no one were explicitly specified by the user.
     *
     * @since 2.2
     * @category Referencing
     */
    public static final Key DEFAULT_COORDINATE_REFERENCE_SYSTEM = new Key(
            "org.opengis.referencing.crs.CoordinateReferenceSystem");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                     Grid Coverages                     ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.geotoolkit.coverage.SampleDimensionType} to use.
     *
     * @category Coverage
     */
    public static final Key SAMPLE_DIMENSION_TYPE = new Key("org.geotoolkit.coverage.SampleDimensionType");



    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////               Feature, Filter and Style                ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * The {@link org.opengis.filter.FilterFactory} instance to use.
     *
     * @see FactoryFinder#getFilterFactory(Hints)
     *
     * @category Feature
     *
     * @since 3.00
     */
    public static final ClassKey FILTER_FACTORY = new ClassKey("org.opengis.filter.FilterFactory");

    /**
     * The {@link org.opengis.style.StyleFactory} instance to use.
     *
     * @see FactoryFinder#getStyleFactory(Hints)
     *
     * @category Feature
     *
     * @since 3.00
     */
    public static final ClassKey STYLE_FACTORY = new ClassKey("org.opengis.style.StyleFactory");

    /**
     * When adding features in a datastore, it is not always necessary to have
     * the returned id of the inserted feature.
     * JDBC featurestore for exemple are much more efficient when inserting datas
     * in batch mode. setting this value to false may bring a huge performance
     * gain.
     *
     * Default value is true.
     */
    public static final Key UPDATE_ID_ON_INSERT = new Key(Boolean.class);

    /**
     * Used to identify a PropertyDescriptor if he is part of the FeatureID.
     */
    public static final Key PROPERTY_IS_IDENTIFIER = new Key(Boolean.class);

    /**
     * This flag indicates that the featurestore can ignore features which are smaller
     * than the given resolution. FeatureStore are supposed to
     * try to conform to this request only if it doesnt requiere to much work.
     * For exemple when exploring a quad tree, tiles can be ignored when there bbox
     * is to small or when the feature bbox can be read before.
     *
     * Default value is null.
     */
    public static final Key KEY_IGNORE_SMALL_FEATURES = new Key(double[].class);

    /**
     * Constructs a map of hints initialized with the system-wide default values.
     *
     * @since 2.5
     */
    public Hints() {
        super(null);
    }

    /**
     * Constructs a new map of hints with the specified key/value pair. First, an initial map
     * is created as with the {@linkplain #Hints() no-argument constructor}. This map may not
     * be empty. Then, the given key-value pair is added. If a default value was present for
     * the given key, then the given value replaces the default one.
     *
     * @param key   The key of the particular hint property.
     * @param value The value of the hint property specified with {@code key}.
     */
    public Hints(final RenderingHints.Key key, final Object value) {
        // Don't use 'super(key,value)' because it doesn't check validity.
        this();
        put(key, value);
    }

    /**
     * Constructs a new map of hints with two key/value pairs. First, an initial map is created
     * as with the {@linkplain #Hints() no-argument constructor}. This map may not be empty.
     * Then, the given key-value pairs are added. If a default value was present for a given
     * key, then the given value replaces the default one.
     *
     * @param key1   The key for the first pair.
     * @param value1 The value for the first pair.
     * @param key2   The key2 for the second pair.
     * @param value2 The value2 for the second pair.
     *
     * @since 2.4
     */
    public Hints(final RenderingHints.Key key1, final Object value1,
                 final RenderingHints.Key key2, final Object value2)
    {
        this(key1, value1);
        put (key2, value2);
    }

    /**
     * Constructs a new object with keys and values from the given map (which may be null).
     * First, an initial map is created as with the {@linkplain #Hints() no-argument constructor}.
     * This map may not be empty. Then, the given key-value pairs are added. If a default value
     * was presents for a given key, then the given value replace the default one.
     *
     * @param hints A map of key/value pairs to initialize the hints, or {@code null} if none.
     */
    public Hints(final Map<? extends RenderingHints.Key, ?> hints) {
        this();
        if (hints != null) {
            putAll(hints);
        }
    }

    /**
     * Constructs a new object with keys and values from the given map (which may be null).
     * First, an initial map is created as with the {@linkplain #Hints() no-argument constructor}.
     * This map may not be empty. Then, the given key-value pairs are added. If a default value
     * was presents for a given key, then the given value replace the default one.
     *
     * @param hints A map of key/value pairs to initialize the hints, or {@code null} if none.
     *
     * @since 2.5
     */
    public Hints(final RenderingHints hints) {
        this();
        if (hints != null) {
            putAll(hints);
        }
    }

    /**
     * Returns a new map of hints with the same content than this map.
     *
     * @since 2.5
     */
    @Override
    public Hints clone() {
        return (Hints) super.clone();
    }

    /**
     * Returns a string representation of the hints. The default implementation formats
     * the set of hints as a tree.
     *
     * @since 2.4
     */
    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * Returns a string representation of the specified hints. This is used by
     * {@link Hints#toString} in order to share the code provided in this class.
     */
    private static String toString(final Map<?,?> hints) {
        return format(hints);
    }

    /**
     * Formats the specified hints. This method is just the starting
     * point for {@link #format(Writer, Map, String, Map)} below.
     */
    private static String format(final Map<?,?> hints) {
        final TableAppender table;
        try {
            table = new TableAppender(" ");
            format(table, hints, "  ");
        } catch (IOException e) {
            // Should never happen, since we are writing in a buffer.
            throw new AssertionError(e);
        }
        return table.toString();
    }

    /**
     * Formats recursively the tree. This method invoke itself.
     */
    private static void format(final TableAppender table, final Map<?,?> hints, final String indent) throws IOException {
        for (final Map.Entry<?,?> entry : hints.entrySet()) {
            final Object k = entry.getKey();
            String key = (k instanceof RenderingHints.Key) ?
                    Hints.nameOf((RenderingHints.Key) k) : String.valueOf(k);
            Object value = entry.getValue();
            table.append(indent);
            table.append(key);
            char separator = ':';
            table.nextColumn();
            table.append(separator);
            table.append(' ');
            table.append(String.valueOf(value));
            table.nextLine();
        }
    }

    /**
     * Returns the enclosing class of the given key, or {@code null} if none. A special case
     * is applied for {@code sun.awt.SunHints}, which maps to {@link RenderingHints}.
     */
    private static Class<?> getEnclosingClass(final RenderingHints.Key key) {
        Class<?> c = key.getClass().getEnclosingClass();
        if (c != null && c.getName().startsWith("sun.")) {
            c = RenderingHints.class;
        }
        return c;
    }

    /**
     * Tries to find the name of the given key, using reflection.
     *
     * @param  key The key for which a name is wanted, or {@code null}.
     * @return The key name as declared in the static constants.
     */
    static String nameOf(final RenderingHints.Key key) {
        if (key == null) {
            return null;
        }
        if (!(key instanceof Key)) {
            final Field field = fieldOf(key);
            if (field != null) {
                return field.getName();
            }
        }
        return key.toString();
    }

    /**
     * Tries to find the field of the given key, using reflection. This method searches
     * for a constant declared in the enclosing class, which is typically one of:
     * <p>
     * <ul>
     *   <li>{@code org.geotoolkit.factory.Hints}  (this class)</li>
     *   <li>{@code java.awt.RenderingHints}       (actually sun.awt.SunHints at least on Sun JDK)</li>
     * </ul>
     *
     * @param  key The key for which a field is wanted, or {@code null}.
     * @return The key field as declared in the static constants.
     */
    static Field fieldOf(final RenderingHints.Key key) {
        Field field = null;
        if (key != null) {
            Class<?> c = getEnclosingClass(key);
            if (c == null || (field = fieldOf(c, key)) == null) {
                if (key instanceof Key && c != (c = ((Key) key).getValueClass())) {
                    field = fieldOf(c, key);
                }
            }
        }
        return field;
    }

    /**
     * If the given key is declared in the given class, returns its name.
     * Otherwise returns {@code null}.
     */
    private static String nameOf(final Class<?> type, final RenderingHints.Key key) {
        final Field f = fieldOf(type, key);
        return (f != null) ? f.getName() : null;
    }

    /**
     * If the given key is declared in the given class, returns its field.
     * Otherwise returns {@code null}.
     */
    private static Field fieldOf(final Class<?> type, final RenderingHints.Key key) {
        final Field[] fields = type.getFields();
        for (int i=0; i<fields.length; i++) {
            final Field f = fields[i];
            if (Modifier.isStatic(f.getModifiers())) {
                final Object v;
                try {
                    v = f.get(null);
                } catch (IllegalAccessException e) {
                    continue;
                }
                if (v == key) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * The type for keys used to control various aspects of the factory
     * creation. Factory creation impacts rendering (which is why extending
     * {@linkplain java.awt.RenderingHints.Key rendering key} is not a complete
     * non-sense), but may impact other aspects of an application as well.
     *
     * {@section Serialization}
     * Keys are serializable if the instance to serialize is declared as a public static
     * final constant in the {@linkplain Class#getEnclosingClass() enclosing class}.
     * Otherwise, an {@link java.io.NotSerializableException} will be thrown.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.05
     *
     * @since 2.1
     * @module
     */
    @SuppressWarnings("serial") // Not relevant because of writeReplace()
    public static class Key extends RenderingHints.Key implements Serializable {
        /**
         * The number of keys created up to date.
         */
        private static int count;

        /**
         * The class name for {@link #valueClass}.
         */
        private final String className;

        /**
         * Base class of all values for this key. Will be created from {@link #className} only when
         * first required, in order to avoid too early class loading.
         */
        private transient Class<?> valueClass;

        /**
         * Constructs a new key for values of the given class.
         *
         * @param classe The base class for all valid values.
         */
        public Key(final Class<?> classe) {
            this(classe.getName());
            valueClass = classe;
        }

        /**
         * Constructs a new key for values of the given class. The class is specified by name
         * instead of a {@link Class} object. This allows to defer class loading until needed.
         *
         * @param className Name of base class for all valid values.
         *
         * @since 3.00
         */
        public Key(final String className) {
            super(count());
            this.className = className;
        }

        /**
         * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
         * call in constructors"): {@code count++} need to be executed in a synchronized
         * block since it is not an atomic operation.
         */
        private static synchronized int count() {
            return count++;
        }

        /**
         * Returns the expected class for values stored under this key.
         *
         * @return The class of values stored under this key.
         */
        public Class<?> getValueClass() {
            if (valueClass == null) {
                try {
                    valueClass = Class.forName(className);
                } catch (ClassNotFoundException exception) {
                    Logging.unexpectedException(null, Key.class, "getValueClass", exception);
                    valueClass = Object.class;
                }
            }
            return valueClass;
        }

        /**
         * Returns {@code true} if the specified object is a valid value for this key. The default
         * implementation checks if the specified value {@linkplain Class#isInstance is an instance}
         * of the {@linkplain #getValueClass value class}.
         * <p>
         * Note that many hint keys defined in the {@link Hints} class relax this rule and accept
         * {@link Class} object assignable to the expected {@linkplain #getValueClass value class}
         * as well.
         *
         * @param value The object to test for validity.
         * @return {@code true} if the value is valid; {@code false} otherwise.
         *
         * @see Hints.ClassKey#isCompatibleValue(Object)
         * @see Hints.FileKey#isCompatibleValue(Object)
         * @see Hints.IntegerKey#isCompatibleValue(Object)
         * @see Hints.OptionKey#isCompatibleValue(Object)
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            return getValueClass().isInstance(value);
        }

        /**
         * Returns a string representation of this key. This is mostly for debugging purpose.
         * The default implementation tries to infer the key name using reflection.
         */
        @Override
        public String toString() {
            Class<?> c = getEnclosingClass(this);
            String name = nameOf(c, this);
            if (name == null) {
                if (c != (c = getValueClass())) {
                    name = nameOf(c, this);
                }
                if (name == null) {
                    name = super.toString();
                }
            }
            return name;
        }

        /**
         * Invoked on serialization for writing a proxy instead than this {@code Key}
         * instance. The proxy will use reflection in order to restore the key as one
         * of the static constants defined in the {@linkplain Class#getEnclosingClass()
         * enclosing class} on deserialization.
         *
         * @return The proxy to be serialized instead than this {@code Key}.
         * @throws ObjectStreamException If this key can not be serialized
         *         because it is not a known constant.
         *
         * @since 3.05
         *
         * @level hidden
         */
        protected final Object writeReplace() throws ObjectStreamException {
            return new SerializedKey(this);
        }
    }

    /**
     * A key for value that may be specified either as instance of {@code T}, or as
     * {@code Class<T>}.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    public static final class ClassKey extends Key {
        /**
         * Constructs a new key for values of the given class.
         *
         * @param classe The base class for all valid values.
         */
        public ClassKey(final Class<?> classe) {
            super(classe);
        }

        /**
         * Constructs a new key for values of the given class. The class is specified by name
         * instead of a {@link Class} object. This allows to defer class loading until needed.
         *
         * @param className Name of base class for all valid values.
         *
         * @since 3.00
         */
        public ClassKey(final String className) {
            super(className);
        }

        /**
         * Returns {@code true} if the specified object is a valid value for this key. This
         * method checks if the specified value is non-null and is one of the following:
         * <p>
         * <ul>
         *   <li>An instance of the {@linkplain #getValueClass() expected value class}.</li>
         *   <li>A {@link Class} assignable to the expected value class.</li>
         *   <li>An array of {@code Class} objects assignable to the expected value class.</li>
         * </ul>
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            if (value == null) {
                return false;
            }
            /*
             * If the value is an array of classes, invokes this method recursively
             * in order to check the validity of each elements in the array.
             */
            if (value instanceof Class<?>[]) {
                final Class<?>[] types = (Class<?>[]) value;
                for (int i=0; i<types.length; i++) {
                    if (!isCompatibleValue(types[i])) {
                        return false;
                    }
                }
                return types.length != 0;
            }
            /*
             * If the value is a class, checks if it is assignable to the expected value class.
             * As a special case, if the value is not assignable but is an abstract class while
             * we expected an interface, we will accept this class anyway because the some sub-
             * classes may implement the interface (we dont't really know). For example the
             * AbstractAuthorityFactory class doesn't implements the CRSAuthorityFactory interface,
             * but sub-classe of it do. We make this relaxation in order to preserve compatibility,
             * but maybe we will make the check stricter in the future.
             */
            if (value instanceof Class<?>) {
                final Class<?> type = (Class<?>) value;
                final Class<?> expected = getValueClass();
                if (expected.isAssignableFrom(type)) {
                    return true;
                }
                if (expected.isInterface() && !type.isInterface()) {
                    final int modifiers = type.getModifiers();
                    if (Modifier.isAbstract(modifiers) && !Modifier.isFinal(modifiers)) {
                        return true;
                    }
                }
                return false;
            }
            return super.isCompatibleValue(value);
        }
    }

    /**
     * Key for hints to be specified as a {@link Path}.
     * The file may also be specified as a {@link File} and {@link String} object.
     *
     * @author Martin Desruisseaux (IRD)
     * @author Jody Garnett (Refractions)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    public static final class FileKey extends Key {
        /**
         * {@code true} if write operations need to be allowed.
         */
        private final boolean writable;

        /**
         * Creates a new key for {@link Path} value.
         *
         * @param writable {@code true} if write operations need to be allowed.
         */
        public FileKey(final boolean writable) {
            super(Path.class);
            this.writable = writable;
        }

        /**
         * Returns {@code true} if the specified object is a valid file or directory.
         * The check performed depends on the value of the {@code writable} argument
         * given to the constructor:
         * <p>
         * <ul>
         *   <li>If {@code false}, then the file must exists and be {@linkplain Files#isReadable(Path)}  readable}.</li>
         *   <li>If {@code true}, then there is a choice:<ul>
         *       <li>If the file exists, it must be {@linkplain Files#isWritable(Path)} writeable}.</li>
         *       <li>Otherwise the file must have a {@linkplain Path#getParent parent} and
         *           that parent must be writable.</li></ul></li>
         * </ul>
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            final Path path;
            if (value instanceof Path) {
                path = (Path) value;
            }else if (value instanceof File) {
                path = ((File) value).toPath();
            } else if (value instanceof String) {
                path = Paths.get((String) value);
            } else {
                return false;
            }
            if (writable) {
                if (Files.exists(path)) {
                    return Files.isWritable(path);
                } else {
                    final Path parent = path.getParent();
                    return parent!=null && Files.isWritable(parent);
                }
            } else {
                return Files.isReadable(path);
            }
        }
    }

    /**
     * A hint used to capture a configuration setting as an integer.
     * A default value is provided and may be checked with {@link #getDefault()}.
     *
     * @author Jody Garnett (Refractions)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    public static final class IntegerKey extends Key {
        /**
         * The default value.
         */
        private final int number;

        /**
         * Creates a new key with the specified default value.
         *
         * @param number The default value.
         */
        public IntegerKey(final int number) {
            super(Integer.class);
            this.number = number;
        }

        /**
         * Returns the default value.
         *
         * @return The default value.
         */
        public int getDefault(){
            return number;
        }

        /**
         * Returns the value from the specified hints as an integer. If no value were found
         * for this key, then this method returns the {@linkplain #getDefault default value}.
         *
         * @param  hints The map where to fetch the hint value, or {@code null}.
         * @return The hint value as an integer, or the default value if not hint
         *         was explicitly set.
         */
        public int toValue(final Hints hints) {
            if (hints != null) {
                final Object value = hints.get(this);
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                } else if (value instanceof CharSequence) {
                    return Integer.parseInt(value.toString());
                }
            }
            return number;
        }

        /**
         * Returns {@code true} if the specified object is a valid integer.
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            if (value instanceof Short || value instanceof Integer) {
                return true;
            }
            if (value instanceof String || value instanceof InternationalString) {
                try {
                    Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    Logging.getLogger("org.geotoolkit.factory").finer(e.toString());
                }
            }
            return false;
        }
    }

    /**
     * Key that allows the choice of several options. The special value {@code "*"} can be used
     * as a wildcard to indicate that undocumented options may be supported.
     *
     * @author Jody Garnett (Refractions)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @SuppressWarnings("serial") // Not relevant because of Key.writeReplace()
    public static final class OptionKey extends Key {
        /**
         * The set of options allowed.
         */
        private final Set<String> options;

        /**
         * {@code true} if the {@code "*"} wildcard was given in the set of options.
         */
        private final boolean wildcard;

        /**
         * Creates a new key for a configuration option.
         *
         * @param alternatives The available options.
         */
        public OptionKey(final String... alternatives) {
            super(String.class);
            final Set<String> options = new TreeSet<>(Arrays.asList(alternatives));
            this.wildcard = options.remove("*");
            this.options  = unmodifiableOrCopy(options);
        }

        /**
         * Returns the set of available options.
         *
         * @return The available options.
         */
        public Set<String> getOptions() {
            return options;
        }

        /**
         * Returns {@code true} if the specified object is one of the valid options. If the
         * options specified at construction time contains the {@code "*"} wildcard, then
         * this method returns {@code true} for every {@link String} object.
         */
        @Override
        public boolean isCompatibleValue(final Object value) {
            return wildcard ? (value instanceof String) : options.contains(value);
        }
    }
}
