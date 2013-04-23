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
package org.geotoolkit.referencing.factory;

import java.io.Serializable;
import java.io.ObjectStreamException;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractSet;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import net.jcip.annotations.NotThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Loggings;
import org.apache.sis.util.collection.BackingStoreException;

import static org.geotoolkit.util.collection.XCollections.isNullOrEmpty;


/**
 * A lazy set of {@linkplain IdentifiedObject identified objects}. This set creates
 * {@link IdentifiedObject}s from authority codes only when first needed. This class
 * is typically used as the set returned by implementations of the
 * {@link CoordinateOperationAuthorityFactory#createFromCoordinateReferenceSystemCodes(String, String)}
 * method. Deferred creation in this case may have great performance impact since a set can contains
 * about 40 entries (e.g. transformations from "<cite>ED50</cite>" (EPSG:4230) to "<cite>WGS 84</cite>"
 * (EPSG:4326)) while some users only want to look for the first entry (e.g. the default
 * {@link org.geotoolkit.referencing.operation.AuthorityBackedFactory} implementation).
 *
 * {@note This is mostly a helper class for implementors, especially the
 *        <code>org.geotoolkit.referencing.factory.epsg</code> package.
 *        This class is not expected to be useful to users.}
 *
 * {@section Exception handling}
 * If the underlying factory failed to creates an object because of an unsupported
 * operation method ({@link NoSuchIdentifierException}), the exception is logged with
 * the {@link Level#FINE FINE} level (because this is a recoverable failure) and
 * the iteration continue. If the operation creation failed for any other kind of
 * reason ({@link FactoryException}), then the exception is rethrown as an unchecked
 * {@link BackingStoreException}. This default behavior can be changed if a subclass
 * overrides the {@link #isRecoverableFailure isRecoverableFailure} method.
 *
 * {@section Serialization}
 * Serialization of this class forces the immediate creation of all
 * {@linkplain IdentifiedObject identified objects} not yet created.
 * The serialized set is disconnected from the {@linkplain #getAuthorityFactory underlying factory}.
 *
 * @param <T> The type of objects to be included in this set.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.2
 * @module
 */
@NotThreadSafe
public class IdentifiedObjectSet<T extends IdentifiedObject> extends AbstractSet<T> implements Serializable {
    /**
     * For cross-version compatibility during serialisation.
     */
    private static final long serialVersionUID = -4221260663706882719L;

    /**
     * The map of object codes (keys), and the actual identified objects (values) when it has
     * been created. Each entry has a null value until the corresponding object is created.
     */
    private final Map<String,T> objects = new LinkedHashMap<String,T>();

    /**
     * The authority factory given at construction time.
     */
    private final AuthorityFactory factory;

    /**
     * The factory to use for creating {@linkplain IdentifiedObject identified objects}
     * when first needed.
     */
    private final AuthorityFactoryProxy<? super T> proxy;

    /**
     * The type of objects included in this set.
     *
     * @since 3.00
     */
    protected final Class<T> type;

    /**
     * Creates an initially empty set. The {@linkplain IdentifiedObject}s
     * will be created when first needed using the specified factory.
     *
     * @param factory The factory to use for deferred {@link IdentifiedObject}s creations.
     * @param type The type of objects included in this set.
     */
    public IdentifiedObjectSet(final AuthorityFactory factory, final Class<T> type) {
        proxy = AuthorityFactoryProxy.getInstance(type);
        this.factory = factory;
        this.type = type;
    }

    /**
     * Removes all of the elements from this collection.
     */
    @Override
    public void clear() {
        objects.clear();
    }

    /**
     * Returns the number of objects available in this set. Note that this
     * number may decrease during the iteration process if the creation of
     * some {@linkplain IdentifiedObject identified objects} failed.
     */
    @Override
    public int size() {
        return objects.size();
    }

    /**
     * Ensures that this collection contains an object for the specified authority code.
     * The {@linkplain IdentifiedObject identified object} will be created from the specified
     * code only when first needed.
     *
     * @param  code The code authority code that shall be included in this set.
     * @return {@code true} if this set changed as a result of this call.
     */
    public boolean addAuthorityCode(final String code) {
        final boolean already = objects.containsKey(code);
        final T old = objects.put(code, null);
        if (old != null) {
            // A fully created object was already there. Keep it.
            objects.put(code, old);
            return false;
        }
        return !already;
    }

    /**
     * Ensures that this collection contains the specified object. This set does not allow multiple
     * objects for the same {@linkplain #getAuthorityCode authority code}. If this set already
     * contains an object using the same {@linkplain #getAuthorityCode authority code} than the
     * specified one, then the old object is replaced by the new one even if the objects are not
     * otherwise identical.
     *
     * @param object The object to add to the set.
     * @return {@code true} if this set changed as a result of this call.
     */
    @Override
    public boolean add(final T object) {
        final String code = getAuthorityCode(object);
        return !Utilities.equals(objects.put(code, object), object);
    }

    /**
     * Returns the identified object for the specified value, {@linkplain #createObject creating}
     * it if needed.
     *
     * @throws BackingStoreException if the object creation failed.
     */
    private T get(final String code) throws BackingStoreException {
        T object = objects.get(code);
        if (object == null && objects.containsKey(code)) {
            try {
                object = createObject(code);
                objects.put(code, object);
            } catch (FactoryException exception) {
                if (!isRecoverableFailure(exception)) {
                    throw new org.geotoolkit.util.collection.BackingStoreException(exception);
                }
                log(exception, code);
                objects.remove(code);
            }
        }
        return object;
    }

    /**
     * Returns {@code true} if this collection contains the specified object.
     *
     * @param  object The object to test for presence in this set.
     * @return {@code true} if the given object is presents in this set.
     */
    @Override
    public boolean contains(final Object object) {
        final String code = getAuthorityCode(type.cast(object));
        final T current = get(code);
        return object.equals(current);
    }

    /**
     * Removes a single instance of the specified element from this collection,
     * if it is present.
     *
     * @param  object The object to remove from this set.
     * @return {@code true} if this set changed as a result of this call.
     */
    @Override
    public boolean remove(final Object object) {
        final String code = getAuthorityCode(type.cast(object));
        final T current = get(code);
        if (object.equals(current)) {
            objects.remove(code);
            return true;
        }
        return false;
    }

    /**
     * Removes from this collection all of its elements that are contained in
     * the specified collection.
     *
     * @param  collection The objects to remove from this set.
     * @return {@code true} if this set changed as a result of this call.
     */
    @Override
    public boolean removeAll(final Collection<?> collection) {
        boolean modified = false;
        for (final Object object : collection) {
            if (remove(object)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Returns an iterator over the objects in this set. If the iteration encounter any
     * kind of {@link FactoryException} other than {@link NoSuchIdentifierException}, then
     * the exception will be rethrown as an unchecked {@link BackingStoreException}.
     *
     * @throws BackingStoreException if the underlying factory failed to creates the
     *         first coordinate operation in the set.
     */
    @Override
    public Iterator<T> iterator() throws BackingStoreException {
        return new Iter(objects.entrySet().iterator());
    }

    /**
     * Ensures that the <var>n</var> first objects in this set are created. This method is
     * typically invoked after some calls to {@link #addAuthorityCode} in order to make sure
     * that the {@linkplain #getAuthorityFactory underlying factory} is really capable to
     * create at least one object. {@link FactoryException} (except the ones accepted as
     * {@linkplain #isRecoverableFailure recoverable failures}) are thrown as if they were
     * never wrapped into {@link BackingStoreException}.
     *
     * @param n The number of object to resolve. If this number is equals or greater than the
     *          {@linkplain #size set's size}, then the creation of all objects is guaranteed
     *          successful.
     * @throws FactoryException if an {@linkplain #createObject object creation} failed.
     */
    public void resolve(int n) throws FactoryException {
        if (n > 0) try {
            // Note: iterator creation resolves the first element.
            for (final Iterator<T> it=iterator(); it.hasNext();) {
                if (--n == 0) {
                    break;
                }
                it.next();
            }
        } catch (BackingStoreException exception) {
            throw exception.unwrapOrRethrow(FactoryException.class);
        }
    }

    /**
     * Returns the {@linkplain #getAuthorityCode authority code} of all objects in this set.
     * The returned array contains the codes in iteration order. This method does not trig the
     * {@linkplain #createObject creation} of any new object.
     * <p>
     * This method is typically used together with {@link #setAuthorityCodes} for altering the
     * iteration order on the basis of authority codes.
     *
     * @return The authority codes in iteration order.
     */
    public String[] getAuthorityCodes() {
        final Set<String> codes = objects.keySet();
        return codes.toArray(new String[codes.size()]);
    }

    /**
     * Sets the content of this set as an array of authority codes. For any code in the given list,
     * this method will preserve the corresponding {@linkplain IdentifiedObject identified object}
     * if it was already created. Other objects will be {@linkplain #createObject created} only
     * when first needed, as usual in this {@code IdentifiedObjectSet} implementation.
     * <p>
     * This method is typically used together with {@link #getAuthorityCodes} for altering the
     * iteration order on the basis of authority codes. If the specified {@code codes} array
     * contains the same elements than {@link #getAuthorityCodes} in a different order, then
     * this method just set the new ordering.
     *
     * @param codes The authority codes of identified objects to store in this set.
     *
     * @see #addAuthorityCode
     */
    public void setAuthorityCodes(final String[] codes) {
        final Map<String,T> copy = new HashMap<String,T>(objects);
        objects.clear();
        for (final String code : codes) {
            objects.put(code, copy.get(code));
        }
    }

    /**
     * Returns the code to uses as a key for the specified object. The default implementation
     * returns the code of the first {@linkplain IdentifiedObject#getIdentifiers identifier},
     * if any, or the code of the {@linkplain IdentifiedObject#getName primary name} otherwise.
     * Subclasses may override this method if they want to use a different key for this set.
     *
     * @param  object The object for which to get the authority code.
     * @return The authority code of the given identified object.
     */
    protected String getAuthorityCode(final T object) {
        final Identifier id;
        final Set<ReferenceIdentifier> identifiers = object.getIdentifiers();
        if (!isNullOrEmpty(identifiers)) {
            id = identifiers.iterator().next();
        } else {
            id = object.getName();
        }
        return id.getCode();
    }

    /**
     * Returns the authority factory used by the {@link #createObject createObject} method.
     * This is the factory given at construction time.
     *
     * @return The authority factory.
     *
     * @since 3.00
     */
    protected AuthorityFactory getAuthorityFactory() {
        return factory;
    }

    /**
     * Creates an object for the specified authority code. This method is invoked during the
     * iteration process if an object was not already created.
     *
     * @param  code The code for which to create the identified object.
     * @return The identified object created from the given code.
     * @throws FactoryException If the object creation failed.
     */
    protected T createObject(final String code) throws FactoryException {
        return type.cast(proxy.createFromAPI(factory, code));
    }

    /**
     * Returns {@code true} if the specified exception should be handled as a recoverable failure.
     * This method is invoked during the iteration process if the factory failed to create some
     * objects. If this method returns {@code true} for the given exception, then the exception
     * will be logged in the {@linkplain AbstractAuthorityFactory#LOGGER Geotk factory logger}
     * with the {@link Level#FINE FINE} level. If this method returns {@code false}, then the
     * exception will be retrown as a {@link BackingStoreException}.
     * <p>
     * The default implementation returns applies the following rules:
     * <p>
     * <ul>
     *   <li>If {@link NoSuchAuthorityCodeException}, returns {@code false} since failure to find
     *       a code declared in the set would be an inconsistency. Note that this exception is a
     *       subtype of {@code NoSuchIdentifierException}, so it must be tested before the last
     *       case below.</li>
     *   <li>If {@link NoSuchIdentifiedResource}, returns {@code true} since operations that
     *       depend on external resources are considered optional.</li>
     *   <li>If {@link NoSuchIdentifierException}, returns {@code true} since this exception is caused by an attempt to
     *       {@linkplain org.opengis.referencing.operation.MathTransformFactory#createParameterizedTransform
     *       create a parameterized transform} for an unimplemented operation.</li>
     * </ul>
     *
     * @param  exception The exception that occurred while creating an object.
     * @return {@code true} if the given exception should be considered recoverable, or
     *         {@code false} if it should be considered fatal.
     */
    protected boolean isRecoverableFailure(final FactoryException exception) {
        return (exception instanceof NoSuchIdentifierException) && !(exception instanceof NoSuchAuthorityCodeException);
    }

    /**
     * Logs a message for the specified exception.
     */
    static void log(final FactoryException exception, final String code) {
        final LogRecord record = Loggings.format(Level.FINE, Loggings.Keys.CANT_CREATE_OBJECT_FROM_CODE_1, code);
        record.setSourceClassName(IdentifiedObjectSet.class.getName());
        record.setSourceMethodName("createObject");
        record.setThrown(exception);
        final Logger logger = AbstractAuthorityFactory.LOGGER;
        record.setLoggerName(logger.getName());
        logger.log(record);
    }

    /**
     * Returns a serializable copy of this set. This method is invoked automatically during
     * serialization. The serialized set of identified objects is disconnected from the
     * {@linkplain #getAuthorityFactory underlying factory}.
     *
     * @return The object to write in replacement of this set.
     * @throws ObjectStreamException If this set can not be serialized.
     */
    protected Object writeReplace() throws ObjectStreamException {
        return new LinkedHashSet<T>(this);
    }

    /**
     * The iterator over the entries in the enclosing set. This iterator will creates the
     * {@linkplain IdentifiedObject identified objects} when first needed.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.2
     * @module
     */
    private final class Iter implements Iterator<T> {
        /**
         * The iterator over the entries from the underlying map.
         */
        private final Iterator<Map.Entry<String,T>> iterator;

        /**
         * The next object to returns, or {@code null} if the iteration is over.
         */
        private T element;

        /**
         * Creates a new instance of this iterator.
         *
         * @throws BackingStoreException if the underlying factory failed to creates the
         *         first coordinate operation in the set.
         */
        public Iter(final Iterator<Map.Entry<String,T>> iterator) throws BackingStoreException {
            this.iterator = iterator;
            toNext();
        }

        /**
         * Moves to the next element.
         *
         * @throws BackingStoreException if the underlying factory failed to creates the
         *         coordinate operation.
         */
        private void toNext() throws BackingStoreException {
            while (iterator.hasNext()) {
                final Map.Entry<String,T> entry = iterator.next();
                element = entry.getValue();
                if (element == null) {
                    final String code = entry.getKey();
                    try {
                        element = createObject(code);
                    } catch (FactoryException exception) {
                        if (!isRecoverableFailure(exception)) {
                            throw new org.geotoolkit.util.collection.BackingStoreException(exception);
                        }
                        log(exception, code);
                        iterator.remove();
                        continue;
                    }
                    entry.setValue(element);
                }
                return; // Element found.
            }
            element = null; // No more element found.
        }

        /**
         * Returns {@code true} if there is more elements.
         */
        @Override
        public boolean hasNext() {
            return element != null;
        }

        /**
         * Returns the next element.
         *
         * @throws NoSuchElementException if there is no more operations in the set.
         */
        @Override
        public T next() throws NoSuchElementException {
            final T next = element;
            if (next == null) {
                throw new NoSuchElementException();
            }
            toNext();
            return next;
        }

        /**
         * Removes the last element from the underlying set.
         */
        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
