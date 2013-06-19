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
import java.util.Locale;
import java.util.Collection;
import java.util.logging.Logger;
import java.text.ParseException;
import java.lang.reflect.Modifier;
import javax.swing.tree.TreeModel;
import net.jcip.annotations.ThreadSafe;

import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.LenientComparable;
import org.apache.sis.metadata.UnmodifiableMetadataException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.gui.swing.tree.TreeTableNode;


/**
 * Base class for metadata implementations. Subclasses must implement the interfaces
 * of some {@linkplain MetadataStandard metadata standard}. This class uses
 * {@linkplain java.lang.reflect Java reflection} in order to provide default
 * implementation of {@linkplain #AbstractMetadata(Object) copy constructor},
 * {@link #equals(Object)} and {@link #hashCode()} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
@ThreadSafe
public abstract class AbstractMetadata implements LenientComparable {
    /**
     * The logger for metadata implementation.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractMetadata.class);

    /**
     * Hash code value, or 0 if not yet computed. This field is reset to 0 by
     * {@link #invalidate()} in order to account for a change in metadata content.
     */
    private transient int hashCode;

    /**
     * A view of this metadata as a map. Will be created only when first needed.
     */
    private transient Map<String,Object> asMap;

    /**
     * Creates an initially empty metadata.
     */
    protected AbstractMetadata() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     * The {@code source} metadata must implements the same metadata interface (defined by
     * the {@linkplain #getStandard standard}) than this class, but don't need to be the same
     * implementation class. The copy is performed using Java reflections.
     *
     * @param  source The metadata to copy values from, or {@code null} if none.
     * @throws ClassCastException if the specified metadata don't implements the expected
     *         metadata interface.
     * @throws UnmodifiableMetadataException if this class don't define {@code set} methods
     *         corresponding to the {@code get} methods found in the implemented interface,
     *         or if this instance is not modifiable for some other reason.
     */
    protected AbstractMetadata(final Object source)
            throws ClassCastException, UnmodifiableMetadataException
    {
        if (source != null) {
            getStandard().shallowCopy(source, this, true);
        }
    }

    /**
     * Returns the metadata standard implemented by subclasses.
     *
     * @return The metadata standard implemented.
     */
    public abstract MetadataStandard getStandard();

    /**
     * Returns the metadata interface implemented by this class. It should be one of the
     * interfaces defined in the {@linkplain #getStandard metadata standard} implemented
     * by this class.
     *
     * @return The standard interface implemented by this implementation class.
     */
    public Class<?> getInterface() {
        // No need to sychronize, since this method does not depend on property values.
        return getStandard().getInterface(getClass());
    }

    /**
     * Returns {@code true} if this metadata is modifiable. The default implementation
     * uses heuristic rules which return {@code false} if and only if:
     * <p>
     * <ul>
     *   <li>This class does not contains any {@code set*(...)} method</li>
     *   <li>All {@code get*()} methods return a presumed immutable object.
     *       The meaning of "<cite>presumed immutable</cite>" may vary in
     *       different Geotk versions.</li>
     * </ul>
     * <p>
     * Otherwise, this method conservatively returns {@code true}. Subclasses
     * should override this method if they can provide a more rigorous analysis.
     */
    boolean isModifiable() {
        return getStandard().isModifiable(getClass());
    }

    /**
     * Invoked when the metadata changed. Some cached informations will need
     * to be recomputed.
     */
    void invalidate() {
        assert Thread.holdsLock(this);
        hashCode = 0; // Will recompute when needed.
    }

    /**
     * Returns {@code true} if this metadata contains only {@code null} or empty properties.
     * A property is considered empty in any of the following cases:
     * <p>
     * <ul>
     *   <li>An empty {@linkplain CharSequence character sequences}, ignoring white spaces.</li>
     *   <li>An {@linkplain Collection#isEmpty() empty collection} or an empty array.</li>
     *   <li>A collection or array containing only {@code null} or empty elements.</li>
     *   <li>An other metadata object containing only {@code null} or empty attributes.</li>
     * </ul>
     * <p>
     * Note that empty properties can be removed by calling the {@link ModifiableMetadata#prune()}
     * method.
     *
     * {@section Note for implementors}
     * The default implementation uses {@linkplain java.lang.reflect Java reflection} indirectly,
     * by iterating over all entries returned by {@link #asMap()}.  Subclasses that override this
     * method should usually not invoke {@code super.isEmpty()}, because the Java reflection will
     * discover and process the properties defined in the sub-classes - which is usually not the
     * intend when overriding a method.
     *
     * @return {@code true} if this metadata is empty.
     *
     * @see org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox#isEmpty()
     *
     * @since 3.20
     */
    public boolean isEmpty() {
        return Pruner.isEmpty(this, false);
    }

    /**
     * To be made public by {@link ModifiableMetadata}.
     */
    void prune() {
        Pruner.isEmpty(this, true);
    }

    /**
     * Returns a view of this metadata object as a {@linkplain Map map}. The map is backed by this
     * metadata object using Java reflection, so changes in the underlying metadata object are
     * immediately reflected in the map. The keys are the property names as determined by the list
     * of {@code getFoo()} methods declared in the {@linkplain #getInterface metadata interface}.
     * <p>
     * The map supports the {@link Map#put put} and {@link Map#remove remove} operations
     * if the underlying metadata object contains {@code setFoo(...)} methods.
     *
     * @return A view of this metadata object as a map.
     *
     * @see MetadataStandard#asMap(Object)
     * @see MetadataStandard#asMap(Object, NullValuePolicy, KeyNamePolicy)
     * @see MetadataStandard#asNameMap(Class, KeyNamePolicy, KeyNamePolicy)
     * @see MetadataStandard#asTypeMap(Class, TypeValuePolicy, KeyNamePolicy)
     * @see MetadataStandard#asRestrictionMap(Object, NullValuePolicy, KeyNamePolicy)
     * @see MetadataStandard#asDescriptionMap(Class, Locale, KeyNamePolicy)
     */
    public synchronized Map<String,Object> asMap() {
        if (asMap == null) {
            asMap = getStandard().asMap(this);
        }
        return asMap;
    }

    /**
     * Returns a view of this metadata as a tree table. Note that while {@link TreeTableNode} is
     * defined in a {@link org.geotoolkit.gui.swing} sub-package, it can be seen as a data structure
     * independent of Swing. It will not force class loading of Swing framework.
     * <p>
     * In current implementation, the tree is not live (i.e. changes in metadata are not
     * reflected in the tree). However it may be improved in a future Geotk implementation.
     *
     * @return A view of this metadata object as a tree table.
     *
     * @see MetadataStandard#asTreeTable(Object)
     *
     * @since 3.19
     */
    public synchronized TreeTableNode asTreeTable() {
        return getStandard().asTreeTable(this);
    }

    /**
     * Returns a view of this metadata as a tree. Note that while {@link TreeModel} is
     * defined in the {@link javax.swing.tree} package, it can be seen as a data structure
     * independent of Swing. It will not force class loading of Swing framework.
     * <p>
     * In current implementation, the tree is not live (i.e. changes in metadata are not
     * reflected in the tree). However it may be improved in a future Geotk implementation.
     *
     * @return A view of this metadata object as a tree.
     *
     * @see MetadataStandard#asTree(Object)
     */
    public synchronized TreeModel asTree() {
        return getStandard().asTree(this);
    }

    /**
     * Fetches values from every nodes of the given tree except the root, and puts them in
     * this metadata object. The value of the root node is ignored (it is typically just the
     * name of this metadata class).
     * <p>
     * If the given metadata object already contains property values, then the parsing will be
     * merged with the existing values: attributes not defined in the tree will be left unchanged,
     * and collections will be augmented with new entries without change in the previously existing
     * entries.
     * <p>
     * This method can parse the tree created by {@link #asTree()}.
     *
     * {@note The current implementation expects the tree root to be an instance of
     *        <code>javax.swing.tree.TreeNode</code>.}
     *
     * @param  tree The tree from which to fetch the values.
     * @throws ParseException If a value can not be stored in this metadata object.
     *
     * @since 3.00
     */
    public synchronized void parse(final TreeModel tree) throws ParseException {
        getStandard().parse(tree, this);
    }

    /**
     * Compares this metadata with the specified object for equality. The default
     * implementation uses Java reflection. Subclasses may override this method
     * for better performances, or for comparing "hidden" attributes not specified
     * by the GeoAPI (or other standard) interface.
     * <p>
     * This method performs a <cite>deep</cite> comparison (i.e. if this metadata contains
     * other metadata, the comparison will walk through the other metadata content as well)
     * providing that every children implement the {@link Object#equals(Object)} method as well.
     * This is the case by default if every children are subclasses of {@code AbstractMetadata}.
     *
     * @param  object The object to compare with this metadata.
     * @param  mode The strictness level of the comparison.
     * @return {@code true} if the given object is equal to this metadata.
     *
     * @since 3.18
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (mode == ComparisonMode.STRICT) {
            if (object == null || getClass(object) != getClass(this)) {
                return false;
            }
        }
        final MetadataStandard standard = getStandard();
        if (mode != ComparisonMode.STRICT) {
            if (!getInterface().isInstance(object)) {
                return false;
            }
        }
        /*
         * Opportunist usage of hash code if they are already computed. If they are not, we will
         * not compute them - they are not sure to be faster than checking directly for equality,
         * and hash code could be invalidated later anyway if the object change. Note that we
         * don't need to synchronize since reading int fields are guaranteed to be atomic in Java.
         */
        if (object instanceof AbstractMetadata) {
            final int c0 = hashCode;
            if (c0 != 0) {
                final int c1 = ((AbstractMetadata) object).hashCode;
                if (c1 != 0 && c0 != c1) {
                    return false;
                }
            }
        }
        /*
         * DEADLOCK WARNING: A deadlock may occur if the same pair of objects is being compared
         * in an other thread (see http://jira.codehaus.org/browse/GEOT-1777). Ideally we would
         * synchronize on 'this' and 'object' atomically (RFE #4210659). Since we can't in Java
         * a workaround is to always get the locks in the same order. Unfortunately we have no
         * guarantee that the caller didn't looked the object himself. For now the safest approach
         * is to not synchronize at all.
         */
        return standard.shallowEquals(this, object, mode, false);
    }

    /**
     * Returns the class of the given metadata, ignoring Geotk private classes like
     * {@link org.geotoolkit.metadata.iso.citation.CitationConstant}.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-48">GEOTK-48</a>
     */
    private static Class<?> getClass(final Object metadata) {
        Class<?> type = metadata.getClass();
        while (!Modifier.isPublic(type.getModifiers()) && type.getName().startsWith("org.geotoolkit.metadata.iso.")) {
            type = type.getSuperclass();
        }
        return type;
    }

    /**
     * Performs a {@linkplain ComparisonMode#STRICT strict} comparison of this metadata with
     * the given object.
     *
     * @param object The object to compare with this metadata for equality.
     */
    @Override
    public final boolean equals(final Object object) {
        return equals(object, ComparisonMode.STRICT);
    }

    /**
     * Computes a hash code value for this metadata using Java reflection. The hash code
     * is defined as the sum of hash code values of all non-null properties. This is the
     * same contract than {@link java.util.Set#hashCode} and ensure that the hash code
     * value is insensitive to the ordering of properties.
     */
    @Override
    public synchronized int hashCode() {
        int code = hashCode;
        if (code == 0) {
            code = getStandard().hashCode(this);
            if (!isModifiable()) {
                // In current implementation, we do not store the hash code if this metadata is
                // modifiable because we can not track change in dependencies (e.g. a change in
                // a metadata contained in this metadata).
                hashCode = code;
            }
        }
        return code;
    }

    /**
     * Returns a string representation of this metadata.
     */
    @Override
    public synchronized String toString() {
        return getStandard().toString(this);
    }
}
