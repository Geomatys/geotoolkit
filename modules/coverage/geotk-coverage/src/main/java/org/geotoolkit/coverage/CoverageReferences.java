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
package org.geotoolkit.coverage;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import net.jcip.annotations.ThreadSafe;

import org.opengis.coverage.Coverage;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Disposable;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.internal.ReferenceQueueConsumer;


/**
 * Holds {@linkplain WeakReference weak references} to {@linkplain Coverage coverage} instances.
 * Every call to the {@link #reference} method with the same {@code Coverage} instance will return
 * the same {@code WeakReference} instance. If all weak references to a coverage have been created
 * by the same instance of {@code CoverageReferences} (typically the {@link #DEFAULT} one), then it
 * is guaranteed that given:
 *
 * {@preformat java
 *     Coverage coverageA = ...;
 *     Coverage coverageB = ...;
 *
 *     WeakReference<Coverage> refA = reference(coverageA);
 *     WeakReference<Coverage> refB = reference(coverageB);
 * }
 *
 * then testing {@code (refA == refB)} is equivalent to testing {@code (coverageA == coverageB)}
 * Comparing the references instead than the coverages can keep comparisons mainfull even after
 * the coverages have been garbage collected. This is sometime useful for checking if two results
 * of a calculation were done using the same coverage inputs, without preventing garbage-collection
 * of those coverages.
 * <p>
 * Because a weak references created by this class may be shared by many, invoking
 * {@link Reference#clear} on them has no effect. Only the garbage-collector can
 * clear the references.
 *
 * {@note This class may be extended in a future version with coverage-specific enhancements.
 *        For example we could prevent the garbage-collection of a coverage as long as the JAI
 *        <code>TileCache</code> has not disposed the image tiles. This class is defined in this
 *        package instead than in the more generic <code>org.geotoolkit.util.collection</code>
 *        package for that reason.}
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.13
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
public class CoverageReferences {
    /**
     * The default, system-wide weak references for {@linkplain Coverage coverages}.
     */
    public static final CoverageReferences DEFAULT = new CoverageReferences();

    /**
     * The map of coverages references. Keys and values are the same instances.
     */
    private final ConcurrentMap<Ref,Ref> pool = new ConcurrentHashMap<Ref,Ref>();

    /**
     * Creates a new coverage cache. This method is given protected access for subclassing
     * only. For typical usage, the {@link #DEFAULT} instance should be used instead.
     */
    protected CoverageReferences() {
    }

    /**
     * Returns a {@linkplain WeakReference weak reference} to the specified coverage. If such
     * reference already exists for the specified coverage, then that reference is returned.
     * Otherwise, a new {@code WeakReference} is created and returned.
     *
     * @param  coverage The coverage to reference.
     * @return A unique weak reference to the specified coverage.
     */
    public Reference<Coverage> reference(final Coverage coverage) {
        if (coverage == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_1, "coverage"));
        }
        Ref ref = pool.get(new Lookup(coverage));
        if (ref == null) {
            ref = new Ref(coverage);
            final Ref old = pool.putIfAbsent(ref, ref);
            if (old != null) {
                ref = old;
            }
        }
        /*
         * We really want identity equality, not Object.equals(...), otherwise we have
         * no guarantee that the coverage is not garbage-collected before the user invokes
         * Reference.get().
         */
        assert ref.get() == coverage;
        return ref;
    }

    /**
     * A temporary key used for lookup only. The main feature of this lookup key is to
     * be comparable with {@link Ref} objects.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static final class Lookup {
        /**
         * The coverage to test for equality.
         */
        private final Coverage coverage;

        /**
         * Creates a new lookup key for the given coverage.
         */
        Lookup(final Coverage coverage) {
            this.coverage = coverage;
        }

        /**
         * Returns the hash code value.
         */
        @Override
        public int hashCode() {
            return System.identityHashCode(coverage);
        }

        /**
         * Compares the given object with this lookup key for equality. {@code Lookup} objects are
         * aimed to be compared to {@code Ref} objects only. Comparisons with other objects should
         * never happen, nevertheless we allow comparisons with other {@code Lookup} instances for
         * symmetry with {@link Ref#equals} implementation, which make us more compliant with the
         * {@link Object#equals} transitivity contract.
         * <p>
         * Note: We need to compare coverages for identity equality, not using
         *       an hypothetical {@link Coverage#equals(Object)} method.
         */
        @Override
        public boolean equals(final Object object) {
            if (object instanceof Ref) {
                return coverage == ((Ref) object).get();
            }
            if (object instanceof Lookup) {
                return coverage == ((Lookup) object).coverage;
            }
            return super.equals(object);
        }
    }

    /**
     * A reference to a coverage, to be stored in {@link CoverageReferences#pool}.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.13
     *
     * @since 2.1
     * @module
     */
    private final class Ref extends WeakReference<Coverage> implements Disposable {
        /**
         * Hash code value, saved at construction time before
         * the coverage reference is nullified.
         */
        private final int hash;

        /**
         * Constructs a reference to the specified coverage.
         */
        Ref(final Coverage coverage) {
            super(coverage, ReferenceQueueConsumer.DEFAULT.queue);
            hash = System.identityHashCode(coverage);
        }

        /**
         * Returns the hash code value for the coverage.
         */
        @Override
        public int hashCode() {
            return hash;
        }

        /**
         * Compares this reference with the specified object for equality. {@code Ref} objects are
         * comparable to {@code Lookup} keys and with other {@code Ref} objects. In the later case,
         * two {@code Ref} objects are equal if their referent are non-null and equal. If one or
         * both referents are null, then the {@code Ref} objects are not considered equal except
         * if their is an identity equality ({@code this == other}).
         * <p>
         * Note: We need to compare coverages for identity equality, not using
         *       an hypothetical {@link Coverage#equals(Object)} method.
         */
        @Override
        public boolean equals(final Object object) {
            if (object instanceof Lookup) {
                return ((Lookup) object).coverage == get();
            }
            if (object instanceof Ref) {
                final Coverage coverage = get();
                if (coverage != null) {
                    return coverage == ((Ref) object).get();
                }
                // Need to fallback on the identity check below.
            }
            /*
             * Do not unconditionally return 'false' here! The check for identity equality
             * is mandatory for proper disposal of references when the referenced Coverage
             * has been garbage-collected.
             */
            return super.equals(object);
        }

        /**
         * Removes this reference from the pool after it has been cleared by the garbage-collector.
         * This method may be invoked either by the user or from the {@link Disposer} thread.
         * However explicit invocations do not clear the referent, because if we did we would
         * have no guarantee that the reference returned by {@link CoverageReferences#reference}
         * has a non-null referent.
         */
        @Override
        public void clear() {
            dispose();
        }

        /**
         * Removes a reference from the map. This method is invoked by
         * {@link ReferenceQueueConsumer#process} when the reference has
         * been garbage-collected.
         */
        @Override
        public void dispose() {
            // Do not invoke super.clear() - see clear() javadoc.
            if (get() == null) {
                final Ref old = pool.remove(this);
                assert old == this || old == null;
            }
        }
    }
}
