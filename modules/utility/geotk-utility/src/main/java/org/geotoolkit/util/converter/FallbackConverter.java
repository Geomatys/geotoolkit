/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.Serializable;
import org.geotoolkit.gui.swing.tree.Trees;
import javax.swing.tree.MutableTreeNode;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;


/**
 * Fallback to be used when the first converter failed. In case of failure, the error
 * of the first (primary) converter is reported.
 * <p>
 * The primary converter is expected more generic than the fallback converter. We try the generic
 * converter first because we expect that if the user wanted the specific subclass, he would have
 * asked explicitly for it. Trying the generic converter first is both closer to what the user
 * asked and less likely to throw many exceptions before we found a successful conversion.
 *
 * @param <S>  The base type of source objects.
 * @param <T>  The base type of converted objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
final class FallbackConverter<S,T> extends ClassPair<S,T> implements ObjectConverter<S,T>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6588190939281568858L;

    /**
     * The primary converter, to be tried first.
     */
    private ObjectConverter<S, ? extends T> primary;

    /**
     * The fallback converter. Its target type should not be assignable from the primary target
     * type, except if both converters have the same target type. We intend {@linkplain #primary}
     * to be the most generic converter, because we assume that if the user wanted a more specific
     * type he would have asked explicitly for it. In addition this layout reduces the amount of
     * exceptions to be thrown and caught before we found a successful conversion.
     */
    private ObjectConverter<S, ? extends T> fallback;

    /**
     * Creates a converter using the given primary and fallback converters. This method may
     * interchange the two converters in order to meet the {@linkplain #fallback} contract.
     * <p>
     * This constructor is not public because it is invoked in context where the target type
     * needs to be determined dynamically. If we wanted to provide a public constructor, we
     * would need to ask for an explicit {@code Class<T>} argument.
     *
     * @param primary  The primary converter.
     * @param fallback The fallback converter.
     */
    private FallbackConverter(final ObjectConverter<S, ? extends T> primary,
                              final ObjectConverter<S, ? extends T> fallback)
    {
        super(primary.getSourceClass(), Classes.commonClass(
              primary.getTargetClass(), fallback.getTargetClass()));
        if (swap(primary, fallback)) {
            this.primary  = fallback;
            this.fallback = primary;
        } else {
            this.primary  = primary;
            this.fallback = fallback;
        }
        assert sourceClass.equals(fallback.getSourceClass());
        assert targetClass.isAssignableFrom(primary .getTargetClass()) &&
               targetClass.isAssignableFrom(fallback.getTargetClass());
    }

    /**
     * Returns {@code true} if the given primary and fallback converters should be interchanged.
     *
     * @param primary  The primary converter to test.
     * @param fallback The fallback converter to test.
     * @return {@code true} if the given primary and fallback converters should be interchanged.
     */
    private static <S> boolean swap(final ObjectConverter<S,?> primary, final ObjectConverter<S,?> fallback) {
        assert !primary.equals(fallback) : primary;
        if (primary instanceof FallbackConverter) {
            final FallbackConverter<S,?> candidate = (FallbackConverter<S,?>) primary;
            return swap(candidate.primary, fallback) && swap(candidate.fallback, fallback);
        } else {
            final Class<?> t1 = primary .getTargetClass();
            final Class<?> t2 = fallback.getTargetClass();
            return !t1.isAssignableFrom(t2) && t2.isAssignableFrom(t1);
        }
    }

    /**
     * Returns the primary or fallback branch.
     *
     * @param asPrimary
     *          {@code true} for the primary branch, or {@code false} for the fallback branch.
     * @return the requested converter.
     */
    final ObjectConverter<S,? extends T> converter(final boolean asPrimary) {
        assert Thread.holdsLock(this);
        return asPrimary ? primary : fallback;
    }

    /**
     * Adds a new converter in the {@linkplain #primary} or {@linkplain #fallback} branch.
     * This method is unsafe because:
     * <p>
     * <ul>
     *   <li>{@code <? extends T>} would have been a more accurate parameterized type.</li>
     *   <li>Even if we declared the above parameterized type, this method would still
     *       unsafe because {@link #targetClass} is also declared as {@code <? extends T>},
     *       so we have no compile-time waranties that {@code converter.targetClass} is
     *       assignable to {@code this.targetClass}.</li>
     * </ul>
     * <p>
     * This method is invoked in a context where the type is unknown at compile time.
     * Callers must have verified that the converter target class is assignable to this
     * {@link #targetClass}. This method will check that again only if assertions are enabled.
     *
     * @param converter The new converter to add.
     * @param asPrimary {@code true} for adding to the primary branch,
     *        or {@code false} for the fallback branch.
     */
    @SuppressWarnings("unchecked")
    private void add(final ObjectConverter<S,?> converter, final boolean asPrimary) {
        assert Thread.holdsLock(this);
        assert targetClass.isAssignableFrom(converter.getTargetClass()) : converter;
        if (asPrimary) {
            primary = new FallbackConverter(primary, converter);
        } else {
            fallback = new FallbackConverter(fallback, converter);
        }
    }

    /**
     * Adds the given converter to the chain of fallback converters. This method is not public
     * because it is unsafe for the same reasons than {@link #add(ObjectConverter,ObjectConverter)}.
     * <p>
     * The validity of the {@code converter} argument shall have been checked by the caller,
     * and will be checked again when {@link #add(ObjectConverter,boolean)} will be invoked.
     *
     * @param insertAt  An existing chain in which to add the given converter.
     * @param converter A new converter to be used as the fallback one.
     */
    private static <S> void add(FallbackConverter<S,?> insertAt, final ObjectConverter<S,?> converter) {
        final Class<?> targetClass = converter.getTargetClass();
        /*
         * First searches on the fallback side of the tree since they are expected
         * to contain the most specialized classes.  We go down the tree until we
         * find the last node capable to accept the converter.   Only then we may
         * switch the search on the primary side of the tree.
         */
        boolean asPrimary = false;
        do synchronized (insertAt) {
            final ObjectConverter<S,?> candidate = insertAt.converter(asPrimary);
            final Class<?> candidateClass = candidate.getTargetClass();
            if (candidateClass.isAssignableFrom(targetClass)) {
                /*
                 * The new converter could be inserted at this point. Checks if we can
                 * continue to scan down the tree, looking for a more specialized node.
                 */
                if (candidate instanceof FallbackConverter) {
                    if (candidateClass.equals(targetClass)) {
                        /*
                         * Using Number for illustration purpose, but could be anything:
                         *
                         * Adding:  String ⇨ Number
                         * to:      String ⇨ Number            : FallbackConverter
                         *          ├───String ⇨ Short
                         *          └───String ⇨ Number        : FallbackConverter
                         *              ├───String ⇨ Integer
                         *              └───String ⇨ Long
                         *
                         * We don't want to insert the generic Number converter between specialized
                         * ones (Integer and Long). So rather than going down the tree in this case,
                         * we will stop the search as if above "isAssignableFrom" check failed.
                         */
                    } else {
                        insertAt = (FallbackConverter<S,?>) candidate;
                        asPrimary = false;
                        continue;
                    }
                } else {
                    /*
                     * Splits at this point the node in two branches. The previous converter
                     * will be the primary branch and the new converter will be the fallback
                     * branch. The "primary vs fallback" contract is respected since we know
                     * at this point that the new converter is more specialized,  because of
                     * the isAssignableFrom(...) check performed above.
                     */
                    insertAt.add(converter, asPrimary);
                    return;
                }
            }
            /*
             * The branch can not hold the converter. If we can't go down anymore in any
             * of the two branches, insert the converter at the point we have reached so
             * far. If the converter is more generic, inserts it as the primary branch in
             * order to respect the "more generic first" contract.
             */
            if (asPrimary) {
                asPrimary = targetClass.isAssignableFrom(insertAt.primary.getTargetClass()) &&
                           !targetClass.isAssignableFrom(insertAt.fallback.getTargetClass());
                insertAt.add(converter, asPrimary);
                return;
            }
            /*
             * We were looking in the fallback branch. Now look in the primary branch
             * of the same node. Note that 'insertAt' may not be equal to 'existing'
             * anymore since we may have succeeded in going down a few nodes before to
             * stop.
             */
            asPrimary = true;
        } while (true);
    }

    /**
     * Returns a new fallback converter, or {@code existing} if the new instance has been
     * appended at the end of an existing chain.
     *
     * @param  <S> The base type of source objects.
     * @param  <T> The base type of converted objects.
     * @param  existing  The existing converter to be used as the primary one.
     * @param  converter A new converter to be used as the fallback one.
     * @return A converter using the existing converter first and fallback next, or
     *         {@code existing} if the fallback has been appended to an existing chain.
     */
    public static <S,T> ObjectConverter<S,? extends T> create(
                  final ObjectConverter<S,? extends T> existing,
                  final ObjectConverter<S,? extends T> converter)
    {
        if (converter == null) return existing;
        if (existing  == null) return converter;
        if (existing instanceof FallbackConverter) {
            @SuppressWarnings("unchecked")
            final FallbackConverter<S,? extends T> fallback = (FallbackConverter<S,? extends T>) existing;
            if (fallback.targetClass.isAssignableFrom(converter.getTargetClass())) {
                add(fallback, converter);
                return fallback;
            }
        }
        return new FallbackConverter<S,T>(existing, converter);
    }

    /**
     * Same as {@link #create} but without parameterized type. The source and target
     * classes are assumed the same, which is verified only if assertions are enabled.
     *
     * @param  existing  The existing converter to be used as the primary one.
     * @param  converter A new converter to be used as the fallback one.
     * @return A converter using the existing converter first and fallback next, or
     *         {@code existing} if the fallback has been appended to an existing chain.
     */
    @SuppressWarnings("unchecked")
    static ObjectConverter<?,?> createUnsafe(final ObjectConverter<?,?> existing,
                                             final ObjectConverter<?,?> converter)
    {
        assert existing.getSourceClass().equals(converter.getSourceClass());
        return create((ObjectConverter) existing, (ObjectConverter) converter);
    }

    /**
     * Returns the base type of source objects.
     */
    @Override
    public final Class<? super S> getSourceClass() {
        return sourceClass;
    }

    /**
     * Returns the base type of target objects.
     */
    @Override
    public final Class<? extends T> getTargetClass() {
        return targetClass;
    }

    /**
     * Returns {@code true} if both the primary and fallback converters have restrictions.
     */
    @Override
    public boolean hasRestrictions() {
        final ObjectConverter<S, ? extends T> primary, fallback;
        synchronized (this) {
            primary  = this.primary;
            fallback = this.fallback;
        }
        return primary.hasRestrictions() && fallback.hasRestrictions();
    }

    /**
     * Returns {@code true} if both the primary and fallback converters preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        final ObjectConverter<S, ? extends T> primary, fallback;
        synchronized (this) {
            primary  = this.primary;
            fallback = this.fallback;
        }
        return primary.isOrderPreserving() && fallback.isOrderPreserving();
    }

    /**
     * Returns {@code true} if both the primary and fallback converters reverse order.
     */
    @Override
    public boolean isOrderReversing() {
        final ObjectConverter<S, ? extends T> primary, fallback;
        synchronized (this) {
            primary  = this.primary;
            fallback = this.fallback;
        }
        return primary.isOrderReversing() && fallback.isOrderReversing();
    }

    /**
     * Converts the given object, using the fallback if needed.
     */
    @Override
    public T convert(final S source) throws NonconvertibleObjectException {
        final ObjectConverter<S, ? extends T> primary, fallback;
        synchronized (this) {
            primary  = this.primary;
            fallback = this.fallback;
        }
        try {
            return primary.convert(source);
        } catch (NonconvertibleObjectException exception) {
            try {
                return fallback.convert(source);
            } catch (NonconvertibleObjectException failure) {
                exception.add(failure);
                throw exception;
            }
        }
    }

    /**
     * Creates a node for the given converter and adds it to the given tree.
     * This method invokes itself recursively for scanning through fallbacks.
     * <p>
     * This method creates a simplified tree, in that the cascading of fallbacks converter
     * of same {@link #targetClass} are hiden: only their leaves are created. The purpose is
     * to help the developper to focus more on the important elements (the leaf converters)
     * and be less distracted by the amount of {@code FallbackConverter}s traversed in order
     * to reach those leaves.
     *
     * @param converter The converter for which to create a tree.
     * @param addTo The node in which to add the converter.
     */
    private void toTree(final ObjectConverter<?,?> converter, final MutableTreeNode addTo) {
        @SuppressWarnings("unchecked")
        final ClassPair<?,?> name = new ClassPair(converter);
        MutableTreeNode node = new DefaultMutableTreeNode(name);
        if (converter instanceof FallbackConverter) {
            final FallbackConverter<?,?> fallback = (FallbackConverter<?,?>) converter;
            final boolean simplify = fallback.targetClass.equals(targetClass);
            if (simplify) {
                node = addTo;
            }
            fallback.toTree(node);
            if (simplify) {
                return;
            }
        }
        addTo.insert(node, addTo.getChildCount());
    }

    /**
     * Adds a simplified tree representation of this {@code FallbackConverter}
     * to the given node.
     *
     * @param addTo The node in which to add the converter.
     */
    final void toTree(final MutableTreeNode addTo) {
        final ObjectConverter<S,? extends T> primary, fallback;
        synchronized (this) {
            primary  = this.primary;
            fallback = this.fallback;
        }
        toTree(primary,  addTo);
        toTree(fallback, addTo);
    }

    /**
     * Returns a tree representation of this converter.
     * The tree leaves represent the backing converters.
     */
    @Override
    public String toString() {
        @SuppressWarnings("unchecked")
        final ClassPair<?,?> name = new ClassPair(this);
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(name);
        toTree(root);
        return Trees.toString(root);
    }
}
