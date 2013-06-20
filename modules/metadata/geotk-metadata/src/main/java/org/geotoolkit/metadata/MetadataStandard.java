/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.text.ParseException;
import java.util.MissingResourceException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeModel;
import net.jcip.annotations.ThreadSafe;

import org.opengis.annotation.UML;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.LenientComparable;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.gui.swing.tree.TreeTableNode;
import org.geotoolkit.gui.swing.tree.DefaultTreeModel;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Enumeration of some metadata standards. A standard is defined by a set of Java interfaces
 * in a specific package or subpackages. For example the {@linkplain #ISO_19115 ISO 19115}
 * standard is defined by <A HREF="http://www.geoapi.org">GeoAPI</A> interfaces in
 * the {@link org.opengis.metadata} package and subpackages.
 * <p>
 * This class provides some methods operating on metadata instances through
 * {@linkplain java.lang.reflect Java reflection}. The following rules are
 * assumed:
 *
 * <ul>
 *   <li><p>Properties (or metadata attributes) are defined by the collection of {@code get*()}
 *       method with arbitrary return type, or {@code is*()} method with boolean return type,
 *       found in the <strong>interface</strong>. Getters declared only in the implementation
 *       are ignored.</p></li>
 *   <li><p>Every properties are <cite>readable</cite>.</p></li>
 *   <li><p>A property is <cite>writable</cite> if a {@code set*(...)} method is defined
 *       in the implementation class for the corresponding {@code get*()} method. The
 *       setter doesn't need to be defined in the interface.</p></li>
 * </ul>
 *
 * An instance of {@code MetadataStandard} is associated to every {@link AbstractMetadata} objects.
 * The {@code AbstractMetadata} base class usually form the basis of ISO 19115 implementations but
 * can also be used for other standards. An instance of {@code MetadataStandard} is also associated
 * with Image I/O {@link org.geotoolkit.image.io.metadata.SpatialMetadataFormat} in order to define
 * the tree of XML nodes to be associated with raster data.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata} package.
 */
@Deprecated
@ThreadSafe
public final class MetadataStandard {
    /**
     * Metadata instances defined in this class. The current implementation does not yet
     * contains the user-defined instances. However this may be something we will need to
     * do in the future.
     *
     * @since 3.20
     */
    private static final MetadataStandard[] INSTANCES;

    /**
     * An instance working on ISO 19111 standard as defined by
     * <A HREF="http://www.geoapi.org">GeoAPI</A> interfaces
     * in the {@link org.opengis.referencing} package and subpackages.
     *
     * @since 2.5
     */
    public static final MetadataStandard ISO_19111;

    /**
     * An instance working on ISO 19115 standard as defined by
     * <A HREF="http://www.geoapi.org">GeoAPI</A> interfaces
     * in the {@link org.opengis.metadata} package and subpackages.
     */
    public static final MetadataStandard ISO_19115;

    /**
     * An instance working on ISO 19119 standard as defined by
     * <A HREF="http://www.geoapi.org">GeoAPI</A> interfaces
     * in the {@link org.opengis.service} package and subpackages.
     *
     * @since 2.5
     */
    public static final MetadataStandard ISO_19119;

    /**
     * An instance working on ISO 19123 standard as defined by
     * <A HREF="http://www.geoapi.org">GeoAPI</A> interfaces
     * in the {@link org.opengis.coverage} package and subpackages.
     *
     * @since 3.06
     */
    public static final MetadataStandard ISO_19123;
    static {
        final String[] prefix = {"Default", "Abstract"};
        final String[] acronyms = {"CoordinateSystem", "CS", "CoordinateReferenceSystem", "CRS"};
        ISO_19111 = new MetadataStandard("ISO 19111", "org.opengis.referencing.", "org.geotoolkit.referencing.",  prefix, acronyms);
        ISO_19115 = new MetadataStandard("ISO 19115", "org.opengis.metadata.",    "org.geotoolkit.metadata.iso.", prefix, null);
        ISO_19119 = new MetadataStandard("ISO 19119", "org.opengis.service.",  null, null, null);
        ISO_19123 = new MetadataStandard("ISO 19123", "org.opengis.coverage.", null, null, null);
        INSTANCES = new MetadataStandard[] {
            ISO_19111,
            ISO_19115,
            ISO_19119,
            ISO_19123
        };
    }

    /**
     * The name, for {@link #toString()} purpose only.
     */
    private final String name;

    /**
     * The root packages for metadata interfaces. Must ends with {@code "."}.
     */
    private final String interfacePackage;

    /**
     * The root packages for metadata implementations, or {@code null} if none.
     */
    private final String implementationPackage;

    /**
     * The prefixes that implementation classes may have, or {@code null} if none.
     * The most common prefix should be first, since the prefix will be tried in that order.
     *
     * @see #getImplementation(Class)
     */
    private final String[] prefix;

    /**
     * The acronyms that implementation classes may have, or {@code null} if none. If non-null,
     * then this array shall contain (<var>full text</var>, <var>acronym</var>) pairs. The full
     * text shall appear that the end of the class name, otherwise it is not replaced. This is
     * necessary in order to avoid the replacement of {@code "DefaultCoordinateSystemAxis"} by
     * {@code "DefaultCSAxis"}.
     *
     * @see #getImplementation(Class)
     *
     * @since 3.15
     */
    private final String[] acronyms;

    /**
     * Accessors for the specified implementations.
     */
    private final Map<Class<?>,PropertyAccessor> accessors = new HashMap<>();

    /**
     * Implementations for a given interface, or {@code null} if none.
     * If non-null, then this map will be filled as needed.
     *
     * @see #getImplementation(Class)
     */
    private final Map<Class<?>,Class<?>> implementations;

    /**
     * Shared pool of {@link MetadataTreeFormat} instances, once for each thread
     * (in order to avoid the need for thread synchronization).
     */
    private final ThreadLocal<MetadataTreeFormat> treeBuilders = new ThreadLocal<MetadataTreeFormat>() {
        @Override
        protected MetadataTreeFormat initialValue() {
            return new MetadataTreeFormat(MetadataStandard.this);
        }
    };

    /**
     * Creates a new instance working on implementation of interfaces defined
     * in the specified package. For the ISO 19115 standard reflected by GeoAPI
     * interfaces, it should be the {@link org.opengis.metadata} package.
     *
     * @param interfacePackage The root package for metadata interfaces.
     */
    public MetadataStandard(String interfacePackage) {
        this(interfacePackage.substring(interfacePackage.lastIndexOf('.')+1), interfacePackage, null, null, null);
    }

    /**
     * Creates a new instance working on implementation of interfaces defined
     * in the specified package.
     *
     * @param name The name of the standard.
     * @param interfacePackage The root package for metadata interfaces.
     * @param implementationPackage The root package for metadata implementations.
     * @param prefix The prefix of implementation class. This array is not cloned.
     * @param acronyms An array of (full text, acronyms) pairs. This array is not cloned.
     */
    private MetadataStandard(final String name, String interfacePackage,
            String implementationPackage, final String[] prefix, final String[] acronyms)
    {
        if (!interfacePackage.endsWith(".")) {
            interfacePackage += '.';
        }
        if (implementationPackage != null) {
            if (!implementationPackage.endsWith(".")) {
                implementationPackage += '.';
            }
            implementations = new HashMap<>();
            if (prefix == null) {
                throw new NullArgumentException();
            }
        } else {
            implementations = null;
        }
        this.interfacePackage      = interfacePackage;
        this.implementationPackage = implementationPackage;
        this.acronyms              = acronyms;
        this.prefix                = prefix;
        this.name                  = name;
    }

    /**
     * Returns the metadata standard for the given class. The argument given to this method can be
     * either an interface defined by the standard, or a class implementing such interface. If the
     * class implements more than one interface, then the first interface recognized by this method,
     * in declaration order, will be retained.
     * <p>
     * The current implementation recognizes only the standards defined by the public static
     * constants defined in this class. A future Geotk version may recognize user-defined
     * constants.
     *
     * @param  type The metadata standard interface, or an implementation class.
     * @return The metadata standard for the given type, or {@code null} if not found.
     *
     * @since 3.20
     */
    public static MetadataStandard forClass(final Class<?> type) {
        String name = type.getName();
        for (final MetadataStandard candidate : INSTANCES) {
            if (name.startsWith(candidate.interfacePackage)) {
                return candidate;
            }
        }
        for (final Class<?> interf : Classes.getAllInterfaces(type)) {
            name = interf.getName();
            for (final MetadataStandard candidate : INSTANCES) {
                if (name.startsWith(candidate.interfacePackage)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Returns the accessor for the specified implementation type.
     *
     * @param  type The implementation type.
     * @throws ClassCastException if the specified implementation class do
     *         not implements a metadata interface of the expected package.
     */
    private PropertyAccessor getAccessor(final Class<?> type) throws ClassCastException {
        final PropertyAccessor accessor = getAccessorOptional(type);
        if (accessor == null) {
            throw new ClassCastException(Errors.format(
                    Errors.Keys.UNKNOWN_TYPE_1, type.getCanonicalName()));
        }
        return accessor;
    }

    /**
     * Returns the accessor for the specified implementation type, or {@code null} if none.
     *
     * @param  type The implementation type.
     */
    final PropertyAccessor getAccessorOptional(final Class<?> type) {
        synchronized (accessors) {
            PropertyAccessor accessor = accessors.get(type);
            if (accessor == null) {
                final Class<?> standard = getStandardType(type);
                if (standard != null) {
                    accessor = new PropertyAccessor(type, standard);
                    accessors.put(type, accessor);
                }
            }
            return accessor;
        }
    }

    /**
     * Returns the metadata interface implemented by the specified implementation.
     * Only one metadata interface can be implemented. If the given type is already
     * an interface from the standard, it is returned directly.
     *
     * @param  type The type of the implementation (could also be the interface type).
     * @return The single interface, or {@code null} if none where found.
     */
    private Class<?> getStandardType(final Class<?> type) {
        return PropertyAccessor.getStandardType(type, interfacePackage);
    }

    /**
     * Returns {@code true} if the given type is assignable to a type from this standard.
     * If this method returns {@code true}, then invoking {@link #getInterface(Class)} is
     * guaranteed to succeed without throwing an exception.
     *
     * @param  type The implementation class (can be {@code null}).
     * @return {@code true} if the given class is an interface of this standard,
     *         or implements an interface of this standard.
     *
     * @since 3.03
     */
    public boolean isMetadata(final Class<?> type) {
        if (type == null) {
            return false;
        }
        // Checks if the class is an interface from the standard.
        if (type.getName().startsWith(interfacePackage)) {
            return true;
        }
        // Checks if the class is an implementation of the standard.
        return getAccessorOptional(type) != null;
    }

    /**
     * Returns the metadata interface implemented by the specified implementation class.
     * If the given type is already an interface from this standard, then it is returned
     * unchanged.
     *
     * {@note The word "interface" may be taken in a looser sense than the usual Java sense
     *        because if the given type is defined in this standard package, then it is returned
     *        unchanged. The standard package is usually made of interfaces and code lists only,
     *        but this is not verified by this method.}
     *
     * @param  type The implementation class.
     * @return The interface implemented by the given implementation class.
     * @throws ClassCastException if the specified implementation class does
     *         not implement an interface of this standard.
     *
     * @see AbstractMetadata#getInterface
     */
    public Class<?> getInterface(final Class<?> type) throws ClassCastException {
        ensureNonNull("type", type);
        if (type.getName().startsWith(interfacePackage)) {
            return type;
        }
        return getAccessor(type).type;
    }

    /**
     * Returns the implementation class for the given interface. If no implementation is found,
     * then the given type is returned unchanged. This method is not public because returning
     * the type unchanged is not consistent with the usual public API.
     *
     * @param  type The interface, typically from the {@code org.opengis.metadata} package.
     * @return The implementation class.
     */
    final Class<?> getImplementation(final Class<?> type) {
        // We require the type to be an interface in order to exclude
        // CodeLists, Enums and Exceptions.
        if (type != null && type.isInterface() && implementations != null) {
            String name = type.getName();
            if (name.startsWith(interfacePackage)) {
                synchronized (implementations) {
                    Class<?> candidate = implementations.get(type);
                    if (candidate != null) {
                        return (candidate != Void.TYPE) ? candidate : type;
                    }
                    /*
                     * Prepares a buffer with a copy of the class name in which the interface
                     * package has been replaced by the implementation package, and some text
                     * have been replaced by their acronym (if any).
                     */
                    final StringBuilder buffer = new StringBuilder(implementationPackage)
                            .append(name, interfacePackage.length(), name.length());
                    if (acronyms != null) {
                        for (int i=0; i<acronyms.length; i+=2) {
                            final String acronym = acronyms[i];
                            if (CharSequences.endsWith(buffer, acronym, false)) {
                                buffer.setLength(buffer.length() - acronym.length());
                                buffer.append(acronyms[i+1]);
                                break;
                            }
                        }
                    }
                    /*
                     * Try to insert a prefix in front of the class name, until a match is found.
                     */
                    final int prefixPosition = buffer.lastIndexOf(".") + 1;
                    int length = 0;
                    for (final String p : prefix) {
                        name = buffer.replace(prefixPosition, prefixPosition+length, p).toString();
                        try {
                            candidate = Class.forName(name);
                        } catch (ClassNotFoundException e) {
                            Logging.recoverableException(MetadataStandard.class, "getImplementation", e);
                            length = p.length();
                            continue;
                        }
                        if (candidate.isAnnotationPresent(Deprecated.class)) {
                            // Skip deprecated implementations.
                            length = p.length();
                            continue;
                        }
                        implementations.put(type, candidate);
                        return candidate;
                    }
                    implementations.put(type, Void.TYPE); // Marker for "class not found".
                }
            }
        }
        return type;
    }

    /**
     * Returns a view as a {@linkplain Map map} of the property types for the specified metadata type.
     * The keys are the property names as determined by the list of {@code getFoo()} methods declared
     * in the {@linkplain #getInterface metadata interface}, or the {@linkplain UML} identifier
     * associated to those methods. The values are determined by the {@link TypeValuePolicy}
     * argument.
     * <p>
     * <b>Example:</b> The following code returns {@code InternationalString.class}.
     *
     * {@preformat java
     *   ISO_19115.asTypeMap(Citation.class, ELEMENT_TYPE, UML_IDENTIFIER).get("alternateTitle");
     * }
     *
     * @param  type The interface or implementation class.
     * @param  typeValues Whatever the values should be property types, the element types
     *         (same as property types except for collections) or the declaring class.
     * @param  keyNames Determines the string representation of map keys.
     * @return The types for the the properties of the given class.
     * @throws ClassCastException if the specified interface or implementation class does
     *         not extend or implement a metadata interface of the expected package.
     *
     * @since 3.03
     */
    public Map<String,Class<?>> asTypeMap(Class<?> type, final org.apache.sis.metadata.TypeValuePolicy typeValues,
            final org.apache.sis.metadata.KeyNamePolicy keyNames) throws ClassCastException
    {
        ensureNonNull("type",       type);
        ensureNonNull("typeValues", typeValues);
        ensureNonNull("keyNames",   keyNames);
        type = getImplementation(type);
        return new TypeMap(getAccessor(type), typeValues, keyNames);
    }

    /**
     * Returns a view as a {@linkplain Map map} of the property names for the specified metadata type.
     * The keys are the property names as determined by the list of {@code getFoo()} methods declared
     * in the {@linkplain #getInterface metadata interface}, or the {@linkplain UML} identifier
     * associated to those methods. The values are determined by the {@link KeyNamePolicy} argument.
     * <p>
     * <b>Example:</b> The following code returns {@code "alternateTitles"} (note the plural).
     *
     * {@preformat java
     *   ISO_19115.asNameMap(Citation.class, JAVABEANS_PROPERTY, UML_IDENTIFIER).get("alternateTitle");
     * }
     *
     * {@note The <code>KeyNamePolicy</code> type may seem a bit strange for the
     *        <code>valueNames</code> parameter, but this method is used for mapping a
     *        namespace to an other namespace. In each namespace, the names are unique.}
     *
     * @param  type The interface or implementation class.
     * @param  valueNames Determines the string representation of map values.
     * @param  keyNames Determines the string representation of map keys.
     * @return The names for the properties of the given class.
     * @throws ClassCastException if the specified interface or implementation class does
     *         not extend or implement a metadata interface of the expected package.
     *
     * @since 3.04
     */
    public Map<String,String> asNameMap(Class<?> type, final org.apache.sis.metadata.KeyNamePolicy valueNames,
            final org.apache.sis.metadata.KeyNamePolicy keyNames) throws ClassCastException
    {
        ensureNonNull("type",       type);
        ensureNonNull("typeValues", valueNames);
        ensureNonNull("keyNames",   keyNames);
        type = getImplementation(type);
        return new NameMap(getAccessor(type), valueNames, keyNames);
    }

    /**
     * Returns a view as a {@linkplain Map map} of the property descriptions for the specified
     * metadata type. The keys are the same than {@link #asNameMap asNameMap}, except that only
     * the keys for which a description is available are declared in the map. The values are
     * descriptions localized in the given locale if possible, or in the default locale otherwise.
     * <p>
     * <b>Example:</b> The following code returns "<cite>Short name or other language name by
     * which the cited information is known.</cite>"
     *
     * {@preformat java
     *   ISO_19115.asDescriptionMap(Citation.class, Locale.ENGLISH, UML_IDENTIFIER).get("alternateTitle");
     * }
     *
     * As a special case, the {@code "class"} value can be used for fetching the description
     * of the class rather than a specific method of that class.
     *
     * {@note We could have provided a method without <code>Locale</code> argument and returning
     *        a map with <code>InternationalString</code> values. However the method defined below
     *        is slightly more efficient if the descriptions are going to be asked for more than
     *        one property, because it fetches the <code>ResourceBundle</code> only once.}
     *
     * @param  type The interface or implementation class.
     * @param  locale Determines the locale of map values.
     * @param  keyNames Determines the string representation of map keys.
     * @return The descriptions for the properties of the given class, or an empty map
     *         if there is no description for the given class in this metadata standard.
     * @throws ClassCastException if the specified interface or implementation class does
     *         not extend or implement a metadata interface of the expected package.
     *
     * @since 3.05
     */
    public Map<String,String> asDescriptionMap(Class<?> type, final Locale locale,
            final org.apache.sis.metadata.KeyNamePolicy keyNames) throws ClassCastException
    {
        ensureNonNull("type",     type);
        ensureNonNull("locale",   locale);
        ensureNonNull("keyNames", keyNames);
        type = getImplementation(type);
        try {
            return new DescriptionMap(getAccessor(type), interfacePackage, locale, keyNames);
        } catch (MissingResourceException e) {
            Logging.recoverableException(MetadataStandard.class, "asDescriptionMap", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Returns a view as a {@linkplain Map map} of the restrictions for the specified metadata.
     * The restrictions are inferred from the {@link org.opengis.annotation.Obligation} and
     * {@link org.geotoolkit.lang.ValueRange} annotated on the getter methods.
     * <p>
     * The {@code metadata} argument can be either a {@link Class}, or an instance of a
     * metadata object:
     * <p>
     * <ul>
     *   <li>If the {@code metadata} argument is a {@link Class}, then this method returns all
     *       restrictions that applied on metadata of the given type.</li>
     *   <li>Otherwise if {@code metadata} is an instance of a metadata object, then this method
     *       returns non-null values only for restrictions that are violated by the given instance.
     *       In this case, the returned map is <cite>live</cite>: changes to any metadata value
     *       will be immediately reflected in the restriction map.</li>
     * </ul>
     *
     * @param metadata The metadata instance for which to get the map of violated restrictions,
     *                 or the {@link Class} of a metadata object for listing all restrictions.
     * @param  content Whatever the entries having no restriction or no violation (null value)
     *         should be included in the map.
     * @param  keyNames Determines the string representation of map keys.
     * @return The restrictions that are violated by the given metadata instance,
     *         or all restrictions if {@code metadata} is a {@link Class}.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     *
     * @since 3.04
     */
    public Map<String,ValueRestriction> asRestrictionMap(Object metadata,
            final org.apache.sis.metadata.ValueExistencePolicy content,
            final org.apache.sis.metadata.KeyNamePolicy keyNames) throws ClassCastException
    {
        ensureNonNull("metadata", metadata);
        ensureNonNull("content",  content);
        ensureNonNull("keyNames", keyNames);
        final Class<?> type;
        if (metadata instanceof Class<?>) {
            type = getImplementation((Class<?>) metadata);
            metadata = null;
        } else {
            type = metadata.getClass();
        }
        return new RestrictionMap(getAccessor(type), metadata, content, keyNames);
    }

    /**
     * Returns a view of the specified metadata object as a {@linkplain Map map}.
     * The map is backed by the metadata object using Java reflection, so changes
     * in the underlying metadata object are immediately reflected in the map.
     * <p>
     * The content of the {@linkplain Map#keySet() key set} is determined by the arguments:
     * {@code metadata} determines the set of keys, {@code content} determines whatever the
     * keys for entries having a null value or an empty collection should be included, and
     * {@code keyNames} determines their {@code String} representations.
     * <p>
     * The map supports the {@link Map#put put} operations if the underlying metadata object
     * contains {@code setFoo(...)} methods. The keys are case-insensitive and can be either
     * the javabeans property name, or the UML identifier.
     *
     * @param  metadata The metadata object to view as a map.
     * @param  content Whatever the entries having null value or empty collection should be
     *         included in the map. The default is {@link ValueExistencePolicy#NON_EMPTY NON_EMPTY}.
     * @param  keyNames Determines the string representation of map keys. The default is
     *         {@link KeyNamePolicy#JAVABEANS_PROPERTY JAVABEANS_PROPERTY}.
     * @return A map view over the metadata object.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     *
     * @since 3.03
     */
    public Map<String,Object> asMap(final Object metadata,
            final org.apache.sis.metadata.ValueExistencePolicy content,
            final org.apache.sis.metadata.KeyNamePolicy keyNames) throws ClassCastException
    {
        ensureNonNull("metadata", metadata);
        ensureNonNull("content",  content);
        ensureNonNull("keyNames", keyNames);
        return new PropertyMap(metadata, getAccessor(metadata.getClass()), content, keyNames);
    }

    /**
     * Returns a view as a {@linkplain Map map} of the specified metadata object.
     * The map is backed by the metadata object using Java reflection, so changes
     * in the underlying metadata object are immediately reflected in the map.
     * The keys are the property names as determined by the list of {@code getFoo()}
     * methods declared in the {@linkplain #getInterface metadata interface}, and
     * only the entries with a non-null or non-{@linkplain Collection#isEmpty empty}
     * value are listed.
     * <p>
     * The map supports the {@link Map#put put} operations if the underlying metadata
     * object contains {@code setFoo(...)} methods. The keys are case-insensitive and
     * can be either the javabeans property name, or the UML identifier.
     *
     * @param  metadata The metadata object to view as a map.
     * @return A map view over the metadata object.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     *
     * @see AbstractMetadata#asMap()
     */
    public Map<String,Object> asMap(final Object metadata) throws ClassCastException {
        return asMap(metadata, NullValuePolicy.NON_EMPTY, KeyNamePolicy.JAVABEANS_PROPERTY);
    }

    /**
     * Returns a view of the specified metadata as a tree table. Note that while {@link TreeTableNode}
     * is defined in a {@link org.geotoolkit.gui.swing} sub-package, it can be seen as a data structure
     * independent of Swing. It will not force class loading of Swing framework.
     * <p>
     * In current implementation, the tree is not live (i.e. changes in metadata are not
     * reflected in the tree). However it may be improved in a future Geotk implementation.
     *
     * @param  metadata The metadata object to formats as a tree table.
     * @return A tree table representation of the specified metadata.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     *
     * @see AbstractMetadata#asTreeTable()
     *
     * @since 3.19
     */
    public TreeTableNode asTreeTable(final Object metadata) throws ClassCastException {
        return treeBuilders.get().asTreeTable(metadata);
    }

    /**
     * Returns a view of the specified metadata as a tree. Note that while {@link TreeModel}
     * is defined in the {@link javax.swing.tree} package, it can be seen as a data structure
     * independent of Swing. It will not force class loading of Swing framework.
     * <p>
     * In current implementation, the tree is not live (i.e. changes in metadata are not
     * reflected in the tree). However it may be improved in a future Geotk implementation.
     *
     * @param  metadata The metadata object to formats as a tree.
     * @return A tree representation of the specified metadata.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     *
     * @see AbstractMetadata#asTree()
     */
    public TreeModel asTree(final Object metadata) throws ClassCastException {
        final MetadataTreeFormat builder = treeBuilders.get();
        return new DefaultTreeModel(builder.asTree(metadata), true);
    }

    /**
     * Fetches values from every nodes of the given tree except the root, and puts them in
     * the given metadata object. The value of the root node is ignored (it is typically
     * just the name of the metadata class).
     * <p>
     * If the given metadata object already contains property values, then the parsing will be
     * merged with the existing values: attributes not defined in the tree will be left unchanged,
     * and collections will be augmented with new entries without change in the previously existing
     * entries.
     * <p>
     * This method can parse the tree created by {@link #asTree(Object)}. The current implementation
     * expects the {@linkplain TreeModel#getRoot tree root} to be an instance of {@link TreeNode}.
     *
     * @param  root     The tree from which to fetch the values.
     * @param  metadata The metadata where to store the values.
     * @throws ParseException If a value can not be stored in the given metadata object.
     */
    final void parse(final TreeModel tree, final Object metadata) throws ParseException {
        treeBuilders.get().parse((TreeNode) tree.getRoot(), metadata);
    }

    /**
     * Returns {@code true} if this metadata is modifiable. This method is not public because it
     * uses heuristic rules. In case of doubt, this method conservatively returns {@code true}.
     *
     * @throws ClassCastException if the specified implementation class do
     *         not implements a metadata interface of the expected package.
     *
     * @see ModifiableMetadata#isModifiable()
     */
    final boolean isModifiable(final Class<?> implementation) throws ClassCastException {
        return getAccessor(implementation).isModifiable();
    }

    /**
     * Replaces every properties in the specified metadata by their
     * {@linkplain ModifiableMetadata#unmodifiable unmodifiable variant.
     *
     * @throws ClassCastException if the specified implementation class do
     *         not implements a metadata interface of the expected package.
     *
     * @see ModifiableMetadata#freeze()
     */
    final void freeze(final Object metadata) throws ClassCastException {
        getAccessor(metadata.getClass()).freeze(metadata);
    }

    /**
     * Copies all metadata from source to target. The source must implements the same
     * metadata interface than the target.
     *
     * @param  source The metadata to copy.
     * @param  target The target metadata.
     * @param  skipNulls If {@code true}, only non-null values will be copied.
     * @throws ClassCastException if the source or target object don't
     *         implements a metadata interface of the expected package.
     * @throws UnmodifiableMetadataException if the target metadata is unmodifiable,
     *         or if at least one setter method was required but not found.
     *
     * @see ModifiableMetadata#clone()
     */
    public void shallowCopy(final Object source, final Object target, final boolean skipNulls)
            throws ClassCastException, UnmodifiableMetadataException
    {
        ensureNonNull("target", target);
        final PropertyAccessor accessor = getAccessor(target.getClass());
        if (!accessor.type.isInstance(source)) {
            ensureNonNull("source", source);
            throw new ClassCastException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_CLASS_3,
                    "source", source.getClass(), accessor.type));
        }
        if (!accessor.shallowCopy(source, target, skipNulls)) {
            throw new UnmodifiableMetadataException(Errors.format(Errors.Keys.UNMODIFIABLE_METADATA));
        }
    }

    /**
     * Compares the two specified metadata objects. The comparison is <cite>shallow</cite>,
     * i.e. all metadata attributes are compared using the
     * {@link LenientComparable#equals(Object, ComparisonMode)} method if possible, or the
     * {@link Object#equals(Object)} method otherwise, without explicit recursive call to
     * this {@code shallowEquals(...)} method for child metadata.
     * <p>
     * This method can optionally excludes null values from the comparison. In metadata,
     * null value often means "don't know", so in some occasion we want to consider two
     * metadata as different only if a property value is know for sure to be different.
     * <p>
     * The first arguments must be an implementation of a metadata interface, otherwise an
     * exception will be thrown. The two arguments do not need to be the same implementation
     * however.
     *
     * @param metadata1 The first metadata object to compare.
     * @param metadata2 The second metadata object to compare.
     * @param mode The strictness level of the comparison.
     * @param skipNulls If {@code true}, only non-null values will be compared.
     * @return {@code true} if the given metadata objects are equals.
     * @throws ClassCastException if at least one metadata object don't
     *         implements a metadata interface of the expected package.
     *
     * @see AbstractMetadata#equals(Object, ComparisonMode)
     */
    public boolean shallowEquals(final Object metadata1, final Object metadata2,
            final ComparisonMode mode, final boolean skipNulls) throws ClassCastException
    {
        if (metadata1 == metadata2) {
            return true;
        }
        if (metadata1 == null || metadata2 == null) {
            return false;
        }
        final PropertyAccessor accessor = getAccessor(metadata1.getClass());
        if (accessor.type != getStandardType(metadata2.getClass())) {
            return false;
        }
        return accessor.shallowEquals(metadata1, metadata2, mode, skipNulls);
    }

    /**
     * Computes a hash code for the specified metadata. The hash code is defined as the
     * sum of hash code values of all non-null properties. This is the same contract than
     * {@link java.util.Set#hashCode} and ensure that the hash code value is insensitive
     * to the ordering of properties.
     *
     * @param  metadata The metadata object to compute hash code.
     * @return A hash code value for the specified metadata.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     *
     * @see AbstractMetadata#hashCode()
     */
    public int hashCode(final Object metadata) throws ClassCastException {
        return getAccessor(metadata.getClass()).hashCode(metadata);
    }

    /**
     * Returns a string representation of the specified metadata.
     *
     * @param  metadata The metadata object to formats as a string.
     * @return A string representation of the specified metadata.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     *
     * @see AbstractMetadata#toString()
     */
    public String toString(final Object metadata) throws ClassCastException {
        return Trees.toString(asTreeTable(metadata));
    }

    /**
     * Returns a string representation of this metadata standard. This is
     * for debugging purpose only and may change in any future version.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + name + ']';
    }
}
