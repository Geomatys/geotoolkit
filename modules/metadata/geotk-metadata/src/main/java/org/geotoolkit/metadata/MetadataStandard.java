/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.text.ParseException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.NullArgumentException;


/**
 * Enumeration of some metadata standards. A standard is defined by a set of Java interfaces
 * in a specific package or subpackages. For example the {@linkplain #ISO_19115 ISO 19115}
 * standard is defined by <A HREF="http://geoapi.sourceforge.net">GeoAPI</A> interfaces in
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
 *   <li><p>A property is <cite>writable</cite> if a {@code set*(...)} method is defined
 *       in the implementation class for the corresponding {@code get*()} method. The
 *       setter doesn't need to be defined in the interface.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 2.4
 * @module
 */
public final class MetadataStandard {
    /**
     * An instance working on ISO 19111 standard as defined by
     * <A HREF="http://geoapi.sourceforge.net">GeoAPI</A> interfaces
     * in the {@link org.opengis.referencing} package and subpackages.
     *
     * @since 2.5
     */
    public static final MetadataStandard ISO_19111 = new MetadataStandard("org.opengis.referencing.");

    /**
     * An instance working on ISO 19115 standard as defined by
     * <A HREF="http://geoapi.sourceforge.net">GeoAPI</A> interfaces
     * in the {@link org.opengis.metadata} package and subpackages.
     */
    public static final MetadataStandard ISO_19115 = new MetadataStandard("org.opengis.metadata.",
            "org.geotoolkit.metadata.iso.", new String[] {"Default", "Abstract"});

    /**
     * An instance working on ISO 19119 standard as defined by
     * <A HREF="http://geoapi.sourceforge.net">GeoAPI</A> interfaces
     * in the {@link org.opengis.service} package and subpackages.
     *
     * @since 2.5
     */
    public static final MetadataStandard ISO_19119 = new MetadataStandard("org.opengis.service.");

    /**
     * The root packages for metadata interfaces. Must ends with {@code "."}.
     */
    private final String interfacePackage;

    /**
     * The root packages for metadata implementations, or {@code null} if none.
     */
    private final String implementationPackage;

    /**
     * The prefix that implementation classes may have, or {@code null} if none.
     * The most common prefix should be first, since the prefix will be tried in that order.
     */
    private final String[] prefix;

    /**
     * Accessors for the specified implementations.
     */
    private final Map<Class<?>,PropertyAccessor> accessors = new HashMap<Class<?>,PropertyAccessor>();

    /**
     * Implementations for a given interface, or {@code null} if none.
     * If non-null, then this map will be filled as needed.
     */
    private final Map<Class<?>,Class<?>> implementations;

    /**
     * Shared pool of {@link PropertyTree} instances, once for each thread
     * (in order to avoid the need for thread synchronization).
     */
    private final ThreadLocal<PropertyTree> treeBuilders = new ThreadLocal<PropertyTree>() {
        @Override
        protected PropertyTree initialValue() {
            return new PropertyTree(MetadataStandard.this);
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
        this(interfacePackage, null, null);
    }

    /**
     * Creates a new instance working on implementation of interfaces defined
     * in the specified package.
     *
     * @param interfacePackage The root package for metadata interfaces.
     * @param implementationPackage The root package for metadata implementations.
     */
    private MetadataStandard(String interfacePackage, String implementationPackage, String[] prefix) {
        if (!interfacePackage.endsWith(".")) {
            interfacePackage += '.';
        }
        if (implementationPackage != null) {
            if (!implementationPackage.endsWith(".")) {
                implementationPackage += '.';
            }
            implementations = new HashMap<Class<?>,Class<?>>();
        } else {
            implementations = null;
        }
        this.interfacePackage      = interfacePackage;
        this.implementationPackage = implementationPackage;
        this.prefix                = prefix;
    }

    /**
     * Returns the accessor for the specified implementation.
     *
     * @throws ClassCastException if the specified implementation class do
     *         not implements a metadata interface of the expected package.
     */
    private PropertyAccessor getAccessor(final Class<?> implementation)
            throws ClassCastException
    {
        final PropertyAccessor accessor = getAccessorOptional(implementation);
        if (accessor == null) {
            throw new ClassCastException(Errors.format(
                    Errors.Keys.UNKNOW_TYPE_$1, implementation.getName()));
        }
        return accessor;
    }

    /**
     * Returns the accessor for the specified implementation, or {@code null} if none.
     */
    final PropertyAccessor getAccessorOptional(final Class<?> implementation) {
        synchronized (accessors) {
            PropertyAccessor accessor = accessors.get(implementation);
            if (accessor == null) {
                Class<?> type = getType(implementation);
                if (type != null) {
                    accessor = new PropertyAccessor(implementation, type);
                    accessors.put(implementation, accessor);
                }
            }
            return accessor;
        }
    }

    /**
     * Returns the metadata interface implemented by the specified implementation.
     * Only one metadata interface can be implemented.
     *
     * @param  metadata The metadata implementation to wraps.
     * @return The single interface, or {@code null} if none where found.
     */
    private Class<?> getType(final Class<?> implementation) {
        return PropertyAccessor.getType(implementation, interfacePackage);
    }

    /**
     * Returns {@code true} if the given class implements an interface from this standard.
     * If this method returns {@code true}, then invoking {@link #getInterface(Class)} is
     * garanteed to succeed without exception.
     *
     * @param  implementation The implementation class.
     * @return {@code true} if the given class implements an interface of this standard.
     *
     * @since 3.03
     */
    public boolean isMetadata(final Class<?> implementation) {
        return getAccessorOptional(implementation) != null;
    }

    /**
     * Returns the metadata interface implemented by the specified implementation class.
     *
     * @param  implementation The implementation class.
     * @return The interface implemented by the given implementation class.
     * @throws ClassCastException if the specified implementation class do
     *         not implements a metadata interface of the expected package.
     *
     * @see AbstractMetadata#getInterface
     */
    public Class<?> getInterface(final Class<?> implementation) throws ClassCastException {
        return getAccessor(implementation).type;
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
        if (type != null && implementations != null) {
            synchronized (implementations) {
                Class<?> candidate = implementations.get(type);
                if (candidate != null) {
                    return candidate;
                }
                String name = type.getName();
                if (name.startsWith(interfacePackage)) {
                    final StringBuilder buffer = new StringBuilder(implementationPackage)
                            .append(name.substring(interfacePackage.length()));
                    final int prefixPosition = buffer.lastIndexOf(".") + 1;
                    int length = 0;
                    if (prefix != null) {
                        for (final String p : prefix) {
                            name = buffer.replace(prefixPosition, prefixPosition+length, p).toString();
                            try {
                                candidate = Class.forName(name);
                            } catch (ClassNotFoundException e) {
                                Logging.recoverableException(MetadataStandard.class, "getImplementation", e);
                                length = p.length();
                                continue;
                            }
                            implementations.put(type, candidate);
                            return candidate;
                        }
                    }
                }
            }
        }
        return type;
    }

    /**
     * Returns a view of the specified metadata object as a {@linkplain Map map}.
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
     * @throws ClassCastException if at the metadata object don't
     *         implements a metadata interface of the expected package.
     *
     * @see AbstractMetadata#asMap
     */
    public Map<String,Object> asMap(final Object metadata) throws ClassCastException {
        return asMap(metadata, MapContent.NON_EMPTY, MetadataKeyName.JAVABEANS_PROPERTY);
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
     * @param  content Whatever the entries having null values or empty collections should
     *         be included in the map. The default is {@link MapContent#NON_EMPTY NON_EMPTY}.
     * @param  keyNames The string representation of map keys. The default is
     *         {@link MetadataKeyName#JAVABEANS_PROPERTY JAVABEANS_PROPERTY}.
     * @return A map view over the metadata object.
     * @throws ClassCastException if at the metadata object don't
     *         implements a metadata interface of the expected package.
     *
     * @since 3.03
     */
    public Map<String,Object> asMap(final Object metadata, final MapContent content,
            final MetadataKeyName keyNames) throws ClassCastException
    {
        ensureNonNull("metadata", metadata);
        ensureNonNull("content",  content);
        ensureNonNull("keyNames", keyNames);
        return new PropertyMap(metadata, getAccessor(metadata.getClass()), content, keyNames);
    }

    /**
     * Returns a view of the specified metadata as a tree. Note that while {@link TreeModel}
     * is defined in the {@link javax.swing.tree} package, it can be seen as a data structure
     * independent of Swing. It will not force class loading of Swing framework.
     * <p>
     * In current implementation, the tree is not live (i.e. changes in metadata are not
     * reflected in the tree). However it may be improved in a future Geotoolkit implementation.
     *
     * @param  metadata The metadata object to formats as a string.
     * @return A tree representation of the specified metadata.
     * @throws ClassCastException if at the metadata object don't
     *         implements a metadata interface of the expected package.
     *
     * @see AbstractMetadata#asTree
     */
    public TreeModel asTree(final Object metadata) throws ClassCastException {
        final PropertyTree builder = treeBuilders.get();
        return new DefaultTreeModel(builder.asTree(metadata), true);
    }

    /**
     * Fetches values from every nodes of the given tree except the root, and puts them in
     * the given metadata object. The value of the root node is ignored (it is typically
     * just the name of the metadata class).
     * <p>
     * If the given metadata object already contains attribute values, then the parsing will be
     * merged with the existing values: attributes not defined in the tree will be left unchanged,
     * and collections will be augmented with new entries without change in the previously existing
     * entries.
     * <p>
     * This method can parse the tree created by {@link #asTree(Object)}. The current implementation
     * expects the {@linkplain TreeModel#getRoot tree root} to be an instance of {@link TreeNode}.
     *
     * @param  root     The tre from which to fetch the values.
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
     * @see ModifiableMetadata#isModifiable
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
     * @see ModifiableMetadata#clone
     */
    public void shallowCopy(final Object source, final Object target, final boolean skipNulls)
            throws ClassCastException, UnmodifiableMetadataException
    {
        ensureNonNull("target", target);
        final PropertyAccessor accessor = getAccessor(target.getClass());
        if (!accessor.type.isInstance(source)) {
            ensureNonNull("source", source);
            throw new ClassCastException(Errors.format(Errors.Keys.ILLEGAL_CLASS_$2,
                    source.getClass(), accessor.type));
        }
        if (!accessor.shallowCopy(source, target, skipNulls)) {
            throw new UnmodifiableMetadataException(Errors.format(Errors.Keys.UNMODIFIABLE_METADATA));
        }
    }

    /**
     * Compares the two specified metadata objects. The comparison is <cite>shallow</cite>,
     * i.e. all metadata attributes are compared using the {@link Object#equals} method without
     * recursive call to this {@code shallowEquals(...)} method for child metadata.
     * <p>
     * This method can optionaly excludes null values from the comparison. In metadata,
     * null value often means "don't know", so in some occasion we want to consider two
     * metadata as different only if an attribute value is know for sure to be different.
     * <p>
     * The first arguments must be an implementation of a metadata interface, otherwise an
     * exception will be thrown. The two argument do not need to be the same implementation
     * however.
     *
     * @param metadata1 The first metadata object to compare.
     * @param metadata2 The second metadata object to compare.
     * @param skipNulls If {@code true}, only non-null values will be compared.
     * @return {@code true} if the given metadata objects are equals.
     * @throws ClassCastException if at least one metadata object don't
     *         implements a metadata interface of the expected package.
     *
     * @see AbstractMetadata#equals
     */
    public boolean shallowEquals(final Object metadata1, final Object metadata2, final boolean skipNulls)
            throws ClassCastException
    {
        if (metadata1 == metadata2) {
            return true;
        }
        if (metadata1 == null || metadata2 == null) {
            return false;
        }
        final PropertyAccessor accessor = getAccessor(metadata1.getClass());
        if (!accessor.type.equals(getType(metadata2.getClass()))) {
            return false;
        }
        return accessor.shallowEquals(metadata1, metadata2, skipNulls);
    }

    /**
     * Computes a hash code for the specified metadata. The hash code is defined as the
     * sum of hash code values of all non-null properties. This is the same contract than
     * {@link java.util.Set#hashCode} and ensure that the hash code value is insensitive
     * to the ordering of properties.
     *
     * @param  metadata The metadata object to compute hash code.
     * @return A hash code value for the specified metadata.
     * @throws ClassCastException if at the metadata object don't
     *         implements a metadata interface of the expected package.
     *
     * @see AbstractMetadata#hashCode
     */
    public int hashCode(final Object metadata) throws ClassCastException {
        return getAccessor(metadata.getClass()).hashCode(metadata);
    }

    /**
     * Returns a string representation of the specified metadata.
     *
     * @param  metadata The metadata object to formats as a string.
     * @return A string representation of the specified metadata.
     * @throws ClassCastException if at the metadata object don't
     *         implements a metadata interface of the expected package.
     *
     * @see AbstractMetadata#toString
     */
    public String toString(final Object metadata) throws ClassCastException {
        final PropertyTree builder = treeBuilders.get();
        return PropertyTree.toString(builder.asTree(metadata));
    }

    /**
     * Ensures that the specified argument is non-null.
     */
    private static void ensureNonNull(String name, Object value) throws NullArgumentException {
        if (value == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }
}
