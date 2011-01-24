/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.internal.jaxb;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.geotoolkit.util.Disposable;

import static org.geotoolkit.util.Utilities.ensureNonNull;


/**
 * Maps {@link UUID} to arbitrary objects. The objects are typically instances of ISO 19115
 * (metadata) and ISO 19111 (referencing) types. This {@code UUIDs} class works as a map with
 * the following properties:
 *
 * <ul>
 *   <li><p>Each UUID is associated to a specific instance, i.e. the underlying map uses
 *   {@linkplain System#identityHashCode identity hash code} and the {@code ==} comparison
 *   operator (not the {@link Object#equals(Object)} method) when looking for the {@link UUID}
 *   of a given object.</p></li>
 *
 *   <li><p>The object instances are referenced by {@linkplain WeakReference weak references}.
 *   Consequently, assigning a {@link UUID} to an object will not prevent the object from being
 *   garbage collected.</p></li>
 * </ul>
 *
 * This class convenient at XML marshalling and unmarshalling time for handling the {@code uuid}
 * and {@code uuidref} attributes. The {@code uuidref} attribute is used to refer to an XML element
 * that has a corresponding {@code uuid} attribute.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 * @module
 */
@ThreadSafe
public class UUIDs {
    /**
     * The system-wide default map of UUIDs.
     */
    public static final UUIDs DEFAULT = new UUIDs();

    /**
     * The objects for which a UUID has been created.
     * Every access to this map must be synchronized on {@code this}.
     */
    private final Map<UUID,WeakRef> uuidToObject = new HashMap<UUID,WeakRef>();

    /**
     * The UUID for existing objects.
     * Every access to this map must be synchronized on {@code this}.
     */
    private final Map<WeakRef,UUID> objectToUUID = new HashMap<WeakRef,UUID>();

    /**
     * Creates a new, initially empty, map of UUIDs.
     */
    public UUIDs() {
    }

    /**
     * Returns the object associated to the given UUID, or {@code null} if none.
     *
     * @param  uuid The UUID for which to look for an object.
     * @return The object associated to the given UUID, or {@code null} if none.
     */
    public Object lookup(final UUID uuid) {
        final Reference<?> ref;
        synchronized (this) {
            ref = uuidToObject.get(uuid);
        }
        return (ref != null) ? ref.get() : null;
    }

    /**
     * Returns the UUID associated to the given object, or {@code null} if none.
     *
     * @param  object The object for which to get the UUID.
     * @return The UUID of the given object, or {@code null} if none.
     */
    public UUID getUUID(final Object object) {
        final StrongRef check = new StrongRef(object);
        synchronized (this) {
            return objectToUUID.get(check);
        }
    }

    /**
     * Sets the UUID for the given object. A UUID can be associated only once for any new
     * object, and the same UUID can not be associated to 2 different objects.
     *
     * @param  object The object for which to set the UUID.
     * @param  uuid The UUID to associate to the object.
     * @throws IllegalArgumentException If the object is already associated to a UUID,
     *         or if the UUID is already associated to an other object.
     */
    public void setUUID(final Object object, final UUID uuid) throws IllegalArgumentException {
        ensureNonNull("object", object);
        final StrongRef check = new StrongRef(object);
        synchronized (this) {
            if (objectToUUID.containsKey(check)) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.VALUE_ALREADY_DEFINED_$1, object));
            }
            final WeakRef ref = new WeakRef(object);
            final WeakRef oldRef = uuidToObject.put(uuid, ref);
            if (oldRef != null) {
                uuidToObject.put(uuid, oldRef);
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.VALUE_ALREADY_DEFINED_$1, uuid));
            }
            if (objectToUUID.put(ref, uuid) != null) {
                throw new AssertionError(ref); // Should never happen.
            }
        }
    }

    /**
     * Returns the UUID associated to the given object, or {@linkplain UUID#randomUUID()
     * create a random UUID} if the given object does not have one.
     *
     * @param  object The object for which to get the UUID.
     * @return The UUID of the given object.
     */
    public UUID getOrCreateUUID(final Object object) {
        ensureNonNull("object", object);
        final StrongRef check = new StrongRef(object);
        synchronized (this) {
            UUID uuid = objectToUUID.get(check);
            if (uuid == null) {
                final WeakRef ref = new WeakRef(object);
                while (true) {
                    uuid = UUID.randomUUID();
                    final WeakRef old = uuidToObject.put(uuid, ref);
                    if (old == null) break;
                    // We are very unlikely to get a UUID clash. But if we do,
                    // restore the previous mapping and get an other UUID.
                    uuidToObject.put(uuid, old);
                }
                if (objectToUUID.put(ref, uuid) != null) {
                    throw new AssertionError(ref); // Should never happen.
                }
            }
            return uuid;
        }
    }

    /**
     * A weak reference to an object, which remove itself from the maps when the
     * object is garbage collected.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.13
     *
     * @since 3.13
     * @module
     */
    private final class WeakRef extends WeakReference<Object> implements Disposable {
        /**
         * The object identity hash code. Must be computed at construction time, because
         * it must stay valud even after the referent has been garbage collected.
         */
        private final int hashCode;

        /**
         * Creates a new reference for the given object.
         *
         * @param referent The object to hold by weak reference.
         * @param uuid The UUID of the referenced object.
         */
        WeakRef(final Object referent) {
            super(referent, ReferenceQueueConsumer.DEFAULT.queue);
            hashCode = System.identityHashCode(referent);
        }

        /**
         * Invoked when the referent has been garbage collected.
         */
        @Override
        public void dispose() {
            final UUID uuid;
            final WeakRef ref;
            synchronized (UUIDs.this) {
                uuid = objectToUUID.remove(this);
                ref  = uuidToObject.remove(uuid);
            }
            assert ref == this : uuid;
        }

        /**
         * Returns the identity hash code of the referenced object.
         */
        @Override
        public int hashCode() {
            return hashCode;
        }

        /**
         * Returns {@code true} if the given object is a wrapper for the same referenced object.
         */
        @Override
        public boolean equals(final Object other) {
            return (other == this) || (other instanceof StrongRef && ((StrongRef) other).same(this)); // NOSONAR
        }
    }

    /**
     * A strong reference to an object. This is used only as a temporary key for fetching
     * the UUID of an existing object.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.13
     *
     * @since 3.13
     * @module
     */
    private static final class StrongRef {
        /**
         * The object which is identified by an UUID. This is used only for
         * lookup in the {@link #objectToUUID} map.
         */
        private final Object referent;

        /**
         * Creates a temporary object for lookup in the {@link #objectToUUID} map.
         */
        private StrongRef(final Object referent) {
            this.referent = referent;
        }

        /**
         * Returns {@code true} if the given reference is referencing the same object
         * than this temporary {@code StrongRef} instance. The method is invoked by
         * the {@code equals} method of both {@link WeakRef} and {@link StrongRef}.
         */
        final boolean same(final Reference<?> reference) {
            return reference.get() == referent;
        }

        /**
         * Returns {@code true} if the given object is a reference to the same object than
         * this temporary {@code StrongRef} instance. This method is public as an implementation
         * side-effect and should not be invoked directly.
         *
         * @param other The {@link Reference} to compare with this object.
         */
        @Override
        public boolean equals(final Object other) {
            if (other instanceof Reference<?>) {
                return same((Reference<?>) other);
            }
            // Actually we are not interested in the check below,
            // but we perform it anyway as a matter of principle.
            return (other instanceof StrongRef) && ((StrongRef) other).referent == referent;
        }

        /**
         * Returns the identity hash code of the referenced object. This method is
         * public as an implementation side-effect and should not be invoked directly.
         */
        @Override
        public int hashCode() {
            return System.identityHashCode(referent);
        }
    }
}
