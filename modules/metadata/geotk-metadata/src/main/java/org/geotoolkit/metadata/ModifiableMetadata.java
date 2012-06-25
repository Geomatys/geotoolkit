/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.CodeList;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.util.collection.CheckedArrayList;
import org.geotoolkit.util.collection.CheckedHashSet;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.internal.jaxb.MarshalContext;


/**
 * Base class for metadata that may (or may not) be modifiable. Implementations will typically
 * provide {@code set*(...)} methods for each corresponding {@code get*()} method. An initially
 * modifiable metadata may become unmodifiable at a later stage (typically after its construction
 * is completed) by the call to the {@link #freeze()} method.
 *
 * {@section Guidline for implementors}
 * Subclasses should follow the pattern below for every {@code get} and {@code set} methods,
 * with a special processing for {@linkplain Collection collections}:
 *
 * {@preformat java
 *     private Foo property;
 *
 *     public Foo getProperty() {
 *         return property;
 *     }
 *
 *     public synchronized void setProperty(Foo newValue) {
 *         checkWritePermission();
 *         property = newValue;
 *     }
 * }
 *
 * For collections (note that the call to {@link #checkWritePermission()} is implicit):
 *
 * {@preformat java
 *     private Collection<Foo> properties;
 *
 *     public synchronized Collection<Foo> getProperties() {
 *         return properties = nonNullCollection(properties, Foo.class);
 *     }
 *
 *     public synchronized void setProperties(Collection<Foo> newValues) {
 *         properties = copyCollection(newValues, properties, Foo.class);
 *     }
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
@ThreadSafe
public abstract class ModifiableMetadata extends AbstractMetadata implements Cloneable {
    /**
     * A null implementation for the {@link #FREEZING} constant.
     */
    private static final class Null extends ModifiableMetadata {
        @Override
        public MetadataStandard getStandard() {
            return null;
        }
    }

    /**
     * A flag used for {@link #unmodifiable} in order to specify that
     * {@link #freeze()} is under way.
     */
    private static final ModifiableMetadata FREEZING = new Null();

    /**
     * An unmodifiable copy of this metadata. Will be created only when first needed.
     * If {@code null}, then no unmodifiable entity is available.
     * If {@code this}, then this entity is itself unmodifiable.
     */
    private transient ModifiableMetadata unmodifiable;

    /**
     * Constructs an initially empty metadata.
     */
    protected ModifiableMetadata() {
        super();
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     * This constructor behavior is as in {@linkplain AbstractMetadata#AbstractMetadata(Object)
     * superclass constructor}.
     *
     * @param  source The metadata to copy values from, or {@code null} if none.
     * @throws ClassCastException if the specified metadata don't implements the expected
     *         metadata interface.
     * @throws UnmodifiableMetadataException if this class don't define {@code set} methods
     *         corresponding to the {@code get} methods found in the implemented interface,
     *         or if this instance is not modifiable for some other reason.
     */
    protected ModifiableMetadata(final Object source)
            throws ClassCastException, UnmodifiableMetadataException
    {
        super(source);
    }

    /**
     * Returns a shallow copy of this metadata.
     *
     * {@section Usage}
     * While {@linkplain Cloneable cloneable}, this class do not provides the {@code clone()}
     * operation as part of the public API. The clone operation is required for the internal
     * working of the {@link #unmodifiable()} method, which needs <strong>shallow</strong>
     * copies of metadata entities. The default {@link Object#clone()} implementation is
     * sufficient in most cases.
     *
     * @return A <strong>shallow</strong> copy of this metadata.
     * @throws CloneNotSupportedException if the clone is not supported.
     */
    @Override
    protected ModifiableMetadata clone() throws CloneNotSupportedException {
        return (ModifiableMetadata) super.clone();
    }

    /**
     * Removes all references to {@linkplain #isEmpty() empty} properties. The default
     * implementation iterates over all {@linkplain NullValuePolicy#NON_NULL non null}
     * properties using {@linkplain java.lang.reflect Java reflection}, and sets to
     * {@code null} the properties for which {@link #isEmpty() isEmpty()} returned
     * {@code true}.
     *
     * @throws UnmodifiableMetadataException If this metadata is not modifiable.
     *
     * @since 3.20
     */
    @Override
    public void prune() throws UnmodifiableMetadataException {
        super.prune();
    }

    /**
     * Returns {@code true} if this metadata is modifiable. This method returns
     * {@code false} if {@link #freeze()} has been invoked on this object.
     *
     * @return {@code true} if this metadata is modifiable.
     *
     * @see #freeze()
     * @see #checkWritePermission()
     */
    @Override
    public final boolean isModifiable() {
        return unmodifiable != this;
    }

    /**
     * Returns an unmodifiable copy of this metadata. Any attempt to modify a property of the
     * returned object will throw an {@link UnmodifiableMetadataException}. The state of this
     * object is not modified.
     * <p>
     * This method is useful for reusing the same metadata object as a template. For example:
     *
     * {@preformat java
     *     DefaultCitation myCitation = new DefaultCitation();
     *     myCitation.setTitle(new SimpleInternationalString("The title of my book"));
     *     myCitation.setEdition(new SimpleInternationalString("First edition"));
     *     final Citation firstEdition = (Citation) myCitation.unmodifiable();
     *
     *     myCitation.setEdition(new SimpleInternationalString("Second edition"));
     *     final Citation secondEdition = (Citation) myCitation.unmodifiable();
     *     // The title of the second edition is unchanged compared to the first edition.
     * }
     *
     * The default implementation makes the following choice:
     * <p>
     * <ul>
     *   <li>If this metadata is itself unmodifiable, then this method returns {@code this}
     *       unchanged.</li>
     *   <li>Otherwise this method {@linkplain #clone() clone} this metadata and
     *       {@linkplain #freeze() freeze} the clone before to return it.</li>
     * </ul>
     *
     * @return An unmodifiable copy of this metadata.
     */
    public synchronized AbstractMetadata unmodifiable() {
        // Reminder: 'unmodifiable' is reset to null by checkWritePermission().
        if (unmodifiable == null) {
            final ModifiableMetadata candidate;
            try {
                /*
                 * Need a SHALLOW copy of this metadata, because some attributes
                 * may already be unmodifiable and we don't want to clone them.
                 */
                candidate = clone();
            } catch (CloneNotSupportedException exception) {
                /*
                 * The metadata is not cloneable for some reason left to the user
                 * (for example it may be backed by some external database).
                 * Assumes that the metadata is unmodifiable.
                 */
                Logging.unexpectedException(LOGGER, getClass(), "unmodifiable", exception);
                return this;
            }
            candidate.freeze();
            // Set the field only after success. The 'unmodifiable' field must
            // stay null if an exception occurred during clone() or freeze().
            unmodifiable = candidate;
        }
        assert !unmodifiable.isModifiable();
        return unmodifiable;
    }

    /**
     * Returns an unmodifiable copy of the specified object. This method performs the
     * following heuristic tests:
     * <p>
     * <ul>
     *   <li>If the specified object is an instance of {@code ModifiableMetadata},
     *       then {@link #unmodifiable()} is invoked on this object.</li>
     *   <li>Otherwise, if the object is a {@linkplain Collection collection}, then the
     *       content is copied into a new collection of similar type, with values replaced
     *       by their unmodifiable variant.</li>
     *   <li>Otherwise, if the object implements the {@link org.opengis.util.Cloneable}
     *       interface, then a clone is returned.</li>
     *   <li>Otherwise, the object is assumed immutable and returned unchanged.</li>
     * </ul>
     *
     * @param  object The object to convert in an immutable one.
     * @return A presumed immutable view of the specified object.
     */
    @SuppressWarnings({"unchecked","rawtypes"}) // We really don't know the collection types.
    static Object unmodifiable(final Object object) {
        /*
         * CASE 1 - The object is an implementation of ModifiableMetadata. It may have
         *          its own algorithm for creating an unmodifiable view of metadata.
         */
        if (object instanceof ModifiableMetadata) {
            return ((ModifiableMetadata) object).unmodifiable();
        }
        /*
         * CASE 2 - The object is a collection. All elements are replaced by their
         *          unmodifiable variant and stored in a new collection of similar
         *          type.
         */
        if (object instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) object;
            final boolean isSet = (collection instanceof Set<?>);
            if (collection.isEmpty()) {
                if (isSet) {
                    collection = Collections.EMPTY_SET;
                } else {
                    collection = Collections.EMPTY_LIST;
                }
            } else {
                final Object[] array = collection.toArray();
                for (int i=0; i<array.length; i++) {
                    array[i] = unmodifiable(array[i]);
                }
                // Do not use the Geotk Checked* classes
                // since we don't need anymore synchronization or type checking.
                collection = UnmodifiableArrayList.wrap(array);
                if (isSet) {
                    collection = XCollections.unmodifiableSet(new LinkedHashSet<>(collection));
                } else {
                    // Conservatively assumes a List if we are not sure to have a Set,
                    // since the list is less destructive (no removal of duplicated).
                }
            }
            return collection;
        }
        /*
         * CASE 3 - The object is a map. Copies all entries in a new map and replaces all values
         *          by their unmodifiable variant. The keys are assumed already immutable.
         */
        if (object instanceof Map<?,?>) {
            Map map = (Map<?,?>) object;
            if (map.isEmpty()) {
                return Collections.EMPTY_MAP;
            }
            map = new LinkedHashMap(map);
            for (final Iterator<Map.Entry> it=map.entrySet().iterator(); it.hasNext();) {
                final Map.Entry entry = it.next();
                entry.setValue(unmodifiable(entry.getValue()));
            }
            return XCollections.unmodifiableMap(map);
        }
        /*
         * CASE 4 - The object is cloneable.
         */
        if (object instanceof org.geotoolkit.util.Cloneable) {
            return ((org.geotoolkit.util.Cloneable) object).clone();
        }
        /*
         * CASE 5 - Any other case. The object is assumed immutable and returned unchanged.
         */
        return object;
    }

    /**
     * Declares this metadata and all its attributes as unmodifiable. Any attempt to modify a
     * property after this method call will throw an {@link UnmodifiableMetadataException}. If
     * this metadata is already unmodifiable, then this method does nothing.
     * <p>
     * Subclasses usually don't need to override this method since the default implementation
     * performs its work using Java reflection.
     *
     * @see #isModifiable()
     * @see #checkWritePermission()
     */
    public synchronized void freeze() {
        if (isModifiable()) {
            ModifiableMetadata success = null;
            try {
                unmodifiable = FREEZING;
                getStandard().freeze(this);
                success = this;
            } finally {
                unmodifiable = success;
            }
        }
    }

    /**
     * Checks if changes in the metadata are allowed. All {@code setFoo(...)} methods in
     * subclasses should invoke this method (directly or indirectly) before to apply any
     * change.
     *
     * @throws UnmodifiableMetadataException if this metadata is unmodifiable.
     *
     * @see #isModifiable()
     * @see #freeze()
     */
    protected void checkWritePermission() throws UnmodifiableMetadataException {
        assert Thread.holdsLock(this);
        if (!isModifiable()) {
            throw new UnmodifiableMetadataException(Errors.format(Errors.Keys.UNMODIFIABLE_METADATA));
        }
        invalidate();
    }

    /**
     * Invoked every time a metadata property is about to change. If there is any cached value
     * in this metadata object, then this method shall clear that cache.
     * <p>
     * Subclasses that override this method must invoke {@code super.invalidate()} in their
     * method body.
     */
    @Override
    final void invalidate() {
        super.invalidate();
        unmodifiable = null;
    }

    /**
     * Tests if the specified collection is modifiable. This method should
     * be used for assertions only since it destroy the collection content
     * in case of assertion failure.
     */
    private static boolean isModifiable(final Collection<?> collection) {
        if (!collection.isEmpty()) try {
            collection.clear();
            return true;
        } catch (UnsupportedOperationException e) {
            // This is the expected exception.
        }
        return false;
    }

    /**
     * Copies the content of one list ({@code source}) into an other ({@code target}).
     * This method performs the following steps:
     * <p>
     * <ul>
     *   <li>Invokes {@link #checkWritePermission()} in order to ensure that this metadata is
     *       modifiable.</li>
     *   <li>If {@code source} is {@linkplain XCollections#isNullOrEmpty(Collection) null or
     *       empty}, returns {@code null} (meaning that the metadata is not provided).</li>
     *   <li>If {@code target} is null, creates a new {@link List}.</li>
     *   <li>Copies the content of the given {@code source} into the target.</li>
     * </ul>
     *
     * @param  <E>         The type of elements in the list.
     * @param  source      The source list, or {@code null}.
     * @param  target      The target list, or {@code null} if not yet created.
     * @param  elementType The base type of elements to put in the list.
     * @return A list (possibly the {@code target} instance) containing the {@code source}
     *         elements, or {@code null} if the source was null.
     * @throws UnmodifiableMetadataException if this metadata is unmodifiable.
     *
     * @see #nonNullList(List, Class)
     *
     * @since 2.5
     */
    @SuppressWarnings("unchecked")
    protected final <E> List<E> copyList(final Collection<? extends E> source,
            List<E> target, final Class<E> elementType)
            throws UnmodifiableMetadataException
    {
        // See the comments in copyCollection(...) for implementation notes.
        if (source != target) {
            if (unmodifiable == FREEZING) {
                assert !isModifiable(source);
                return (List<E>) source;
            }
            checkWritePermission();
            if (XCollections.isNullOrEmpty(source)) {
                target = null;
            } else {
                if (target != null) {
                    target.clear();
                } else {
                    target = new MutableList<>(elementType, source.size());
                }
                target.addAll(source);
            }
        }
        return target;
    }

    /**
     * Copies the content of one Set ({@code source}) into an other ({@code target}).
     * This method performs the following steps:
     * <p>
     * <ul>
     *   <li>Invokes {@link #checkWritePermission()} in order to ensure that this metadata is
     *       modifiable.</li>
     *   <li>If {@code source} is {@linkplain XCollections#isNullOrEmpty(Collection) null or
     *       empty}, returns {@code null} (meaning that the metadata is not provided).</li>
     *   <li>If {@code target} is null, creates a new {@link Set}.</li>
     *   <li>Copies the content of the given {@code source} into the target.</li>
     * </ul>
     *
     * @param  <E>         The type of elements in the set.
     * @param  source      The source set, or {@code null}.
     * @param  target      The target set, or {@code null} if not yet created.
     * @param  elementType The base type of elements to put in the set.
     * @return A set (possibly the {@code target} instance) containing the {@code source}
     *         elements, or {@code null} if the source was null.
     * @throws UnmodifiableMetadataException if this metadata is unmodifiable.
     *
     * @see #nonNullSet(Set, Class)
     *
     * @since 3.02
     */
    @SuppressWarnings("unchecked")
    protected final <E> Set<E> copySet(final Collection<? extends E> source,
            Set<E> target, final Class<E> elementType)
            throws UnmodifiableMetadataException
    {
        // See the comments in copyCollection(...) for implementation notes.
        if (source != target) {
            if (unmodifiable == FREEZING) {
                assert !isModifiable(source);
                return (Set<E>) source;
            }
            checkWritePermission();
            if (XCollections.isNullOrEmpty(source)) {
                target = null;
            } else {
                if (target != null) {
                    target.clear();
                } else {
                    target = new MutableSet<>(elementType, source.size());
                }
                target.addAll(source);
            }
        }
        return target;
    }

    /**
     * Copies the content of one collection ({@code source}) into an other ({@code target}).
     * This method performs the following steps:
     * <p>
     * <ul>
     *   <li>Invokes {@link #checkWritePermission()} in order to ensure that this metadata is
     *       modifiable.</li>
     *   <li>If {@code source} is {@linkplain XCollections#isNullOrEmpty(Collection) null or
     *       empty}, returns {@code null} (meaning that the metadata is not provided).</li>
     *   <li>If {@code target} is null, creates a new {@link Set} or a new {@link List}
     *       depending on the value returned by {@link #collectionType(Class)}.</li>
     *   <li>Copies the content of the given {@code source} into the target.</li>
     * </ul>
     *
     * {@section Choosing a collection type}
     * Implementations shall invoke {@link #copyList copyList} or {@link #copySet copySet}
     * instead than this method when the collection type is enforced by ISO specification.
     * When the type is not enforced by the specification, some freedom are allowed at
     * implementor choice. The default implementation invokes {@link #collectionType(Class)}
     * in order to get a hint about whatever a {@link List} or a {@link Set} should be used.
     *
     * @param  <E>         The type of elements in the collection.
     * @param  source      The source collection, or {@code null}.
     * @param  target      The target collection, or {@code null} if not yet created.
     * @param  elementType The base type of elements to put in the collection.
     * @return A collection (possibly the {@code target} instance) containing the {@code source}
     *         elements, or {@code null} if the source was null.
     * @throws UnmodifiableMetadataException if this metadata is unmodifiable.
     */
    @SuppressWarnings("unchecked")
    protected final <E> Collection<E> copyCollection(final Collection<? extends E> source,
            Collection<E> target, final Class<E> elementType)
            throws UnmodifiableMetadataException
    {
        /*
         * It is not worth to copy the content if the current and the new instance are the
         * same. This is safe only using the != operator, not the !equals(Object) method.
         * This optimization is required for efficient working of PropertyAccessor.set(...).
         */
        if (source != target) {
            if (unmodifiable == FREEZING) {
                /*
                 * freeze() method is under progress. The source collection is already
                 * an unmodifiable instance created by unmodifiable(Object).
                 */
                assert collectionType(elementType).isAssignableFrom(source.getClass());
                assert !isModifiable(source);
                return (Collection<E>) source;
            }
            checkWritePermission();
            if (XCollections.isNullOrEmpty(source)) {
                target = null;
            } else {
                if (target != null) {
                    target.clear();
                } else {
                    final int capacity = source.size();
                    if (useSet(elementType)) {
                        target = new MutableSet<>(elementType, capacity);
                    } else {
                        target = new MutableList<>(elementType, capacity);
                    }
                }
                target.addAll(source);
            }
        }
        return target;
    }

    /**
     * Returns {@code true} if the caller {@code nonNullCollection} method (or list, or set)
     * is allowed to returns {@code null} instead than an empty list. This happen mostly at
     * XML marshalling time.
     */
    private static boolean canReturnNull() {
        return MarshalContext.isMarshaling();
    }

    /**
     * Returns the specified list, or a new one if {@code c} is null.
     * This is a convenience method for implementation of {@code getFoo()}
     * methods.
     *
     * {@section Special cases}
     * The general contract of this method is to never return {@code null}. However this method
     * may exceptionally returns {@code null} during XML marshalling or during copy operations.
     * The intend is to reduce the amount of empty object creations and should be invisible
     * from the public API.
     *
     * @param  <E> The type of elements in the list.
     * @param  c The list to checks.
     * @param  elementType The element type (used only if {@code c} is null).
     * @return {@code c}, or a new list if {@code c} is null.
     */
    // See the comments in nonNullCollection(...) for implementation notes.
    protected final <E> List<E> nonNullList(final List<E> c, final Class<E> elementType) {
        assert Thread.holdsLock(this);
        if (c != null) {
            return c.isEmpty() && canReturnNull() ? null : c;
        }
        if (canReturnNull()) {
            return null;
        }
        if (isModifiable()) {
            return new MutableList<>(elementType);
        }
        return Collections.emptyList();
    }

    /**
     * Returns the specified set, or a new one if {@code c} is null.
     * This is a convenience method for implementation of {@code getFoo()}
     * methods.
     *
     * {@section Special cases}
     * The general contract of this method is to never return {@code null}. However this method
     * may exceptionally returns {@code null} during XML marshalling or during copy operations.
     * The intend is to reduce the amount of empty object creations and should be invisible
     * from the public API.
     *
     * @param  <E> The type of elements in the set.
     * @param  c The set to checks.
     * @param  elementType The element type (used only if {@code c} is null).
     * @return {@code c}, or a new set if {@code c} is null.
     *
     * @since 2.5
     */
    // See the comments in nonNullCollection(...) for implementation notes.
    protected final <E> Set<E> nonNullSet(final Set<E> c, final Class<E> elementType) {
        assert Thread.holdsLock(this);
        if (c != null) {
            return c.isEmpty() && canReturnNull() ? null : c;
        }
        if (canReturnNull()) {
            return null;
        }
        if (isModifiable()) {
            return new MutableSet<>(elementType);
        }
        return Collections.emptySet();
    }

    /**
     * Returns the specified collection, or a new one if {@code c} is null.
     * This is a convenience method for implementation of {@code getFoo()}
     * methods.
     *
     * {@section Choosing a collection type}
     * Implementations shall invoke {@link #nonNullList nonNullList} or {@link #nonNullSet
     * nonNullSet} instead than this method when the collection type is enforced by ISO
     * specification. When the type is not enforced by the specification, some freedom are
     * allowed at implementor choice. The default implementation invokes
     * {@link #collectionType(Class)} in order to get a hint about whatever a {@link List}
     * or a {@link Set} should be used.
     *
     * {@section Special cases}
     * The general contract of this method is to never return {@code null}. However this method
     * may exceptionally returns {@code null} during XML marshalling or during copy operations.
     * The intend is to reduce the amount of empty object creations and should be invisible
     * from the public API.
     *
     * @param  <E> The type of elements in the collection.
     * @param  c The collection to checks.
     * @param  elementType The element type (used only if {@code c} is null).
     * @return {@code c}, or a new collection if {@code c} is null.
     */
    // Despite the javadoc claims, we do not yet return null during copy operations.
    // However a future version may do so if it appears worth on a performance point of view.
    protected final <E> Collection<E> nonNullCollection(final Collection<E> c, final Class<E> elementType) {
        assert Thread.holdsLock(this);
        if (XCollections.isNullOrEmpty(c) && MarshalContext.isMarshaling()) {
            return null;
        }
        if (c != null) {
            assert collectionType(elementType).isAssignableFrom(c.getClass());
            return c.isEmpty() && canReturnNull() ? null : c;
        }
        if (canReturnNull()) {
            return null;
        }
        final boolean isModifiable = isModifiable();
        if (useSet(elementType)) {
            if (isModifiable) {
                return new MutableSet<>(elementType);
            } else {
                return Collections.emptySet();
            }
        } else {
            if (isModifiable) {
                return new MutableList<>(elementType);
            } else {
                return Collections.emptyList();
            }
        }
    }

    /**
     * A checked set synchronized on the enclosing {@link ModifiableMetadata}.
     * Used for mutable sets only. Note that the lock must be modified after
     * {@link #clone}. This is currently done in {@link #unmodifiable(Object)}.
     */
    private final class MutableSet<E> extends CheckedHashSet<E> {
        private static final long serialVersionUID = 2337350768744454264L;

        public MutableSet(Class<E> type) {
            super(type, 4); // Use a small capacity because we typically have few elements.
        }

        public MutableSet(Class<E> type, int capacity) {
            super(type, XCollections.hashMapCapacity(capacity));
        }

        @Override
        protected Object getLock() {
            return ModifiableMetadata.this;
        }

        @Override
        protected void checkWritePermission() throws UnsupportedOperationException {
            ModifiableMetadata.this.checkWritePermission();
        }
    }

    /**
     * A checked list synchronized on the enclosing {@link ModifiableMetadata}.
     * Used for mutable lists only. Note that the lock must be modified after
     * {@link #clone}. This is currently done in {@link #unmodifiable(Object)}.
     */
    private final class MutableList<E> extends CheckedArrayList<E> {
        private static final long serialVersionUID = -5016778173550153002L;

        public MutableList(Class<E> type) {
            super(type, 4); // Use a small capacity because we typically have few elements.
        }

        public MutableList(Class<E> type, int capacity) {
            super(type, capacity);
        }

        @Override
        protected Object getLock() {
            return ModifiableMetadata.this;
        }

        @Override
        protected void checkWritePermission() throws UnsupportedOperationException {
            ModifiableMetadata.this.checkWritePermission();
        }
    }

    /**
     * Returns {@code true} if we should use a {@link Set} instead than a {@link List}
     * for elements of the given type.
     */
    private <E> boolean useSet(final Class<E> elementType) {
        final Class<? extends Collection<E>> type = collectionType(elementType);
        if (Set.class.isAssignableFrom(type)) {
            return true;
        }
        if (List.class.isAssignableFrom(type)) {
            return false;
        }
        throw new NoSuchElementException(Errors.format(Errors.Keys.UNSUPPORTED_DATA_TYPE_$1, type));
    }

    /**
     * Returns the type of collection to use for the given type. The current implementation can
     * return only two values: <code>{@linkplain Set}.class</code> if the attribute should not
     * accept duplicated values, or <code>{@linkplain List}.class</code> otherwise. Future Geotk
     * versions may accept other types.
     * <p>
     * The default implementation returns <code>{@linkplain Set}.class</code> if the element type
     * is assignable to {@link Enum} or {@link CodeList}, and <code>{@linkplain List}.class</code>
     * otherwise. Subclasses can override this method for choosing different kind of collections.
     * <em>Note however that {@link Set} should be used only with immutable element types</em>,
     * for {@linkplain Object#hashCode() hash code} stability.
     *
     * @param  <E> The type of elements in the collection to be created.
     * @param  elementType The type of elements in the collection to be created.
     * @return {@code List.class} or {@code Set.class} depending on whatever the
     *         attribute shall accept duplicated values or not.
     *
     * @since 3.18
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    protected <E> Class<? extends Collection<E>> collectionType(final Class<E> elementType) {
        return (Class) (CodeList.class.isAssignableFrom(elementType) ||
                            Enum.class.isAssignableFrom(elementType) ? Set.class : List.class);
    }
}
