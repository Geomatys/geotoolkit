/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
import java.io.Writer;
import java.io.IOException;
import java.awt.RenderingHints;
import javax.imageio.spi.ServiceRegistry;

import org.opengis.util.InternationalString;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.ConformanceResult;

import org.geotoolkit.io.TableWriter;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;


/**
 * Base class for Geotoolkit.org factories. {@link FactoryRegistry} handles {@code Factory}
 * subclasses specially, but extending this class is not mandatory ({@code FactoryRegistry}
 * will work with arbitrary implementations as well). This base class provides some convenience
 * features for subclasses:
 * <p>
 * <ul>
 *   <li>An initially empty map of {@linkplain #hints hints} to be filled by subclasses
 *       constructors.</li>
 *   <li>An opportunity to said whatever this factory {@linkplain #availability is available}.</li>
 *   <li>An opportunity to {@linkplain #setOrdering set the ordering} of this factory relative
 *       to other factories.</li>
 *   <li>An opportunity to {@linkplain #dispose dispose} resources.</li>
 * </ul>
 *
 * {@section How hints are set}
 * Hints are used for two purposes. The distinction is important because the set
 * of hints may not be identical in both cases:
 * <p>
 * <ol>
 *   <li>Hints are used for creating new factories.</li>
 *   <li>Hints are used in order to check if an <em>existing</em> factory is suitable.</li>
 * </ol>
 * <p>
 * This {@code Factory} base class does <strong>not</strong> provide any facility for the first
 * case. Subclasses shall inspect themselves all relevant hints supplied by the user, and pass them
 * to any dependencies. Do <strong>not</strong> use the {@link #hints} field for that; use the hints
 * provided by the user in the constructor. If all dependencies are created at construction time
 * (<cite>constructor injection</cite>), there is no need to keep all user's hints once the
 * construction is finished.
 * <p>
 * The {@link #hints} field is for the second case only. Implementations shall copy in this field
 * only the user's hints that are know to be relevant to this factory. If a hint is relevant but
 * the user didn't specified any value, the hint key should be added to the {@link #hints} map
 * anyway with a {@code null} value. Only direct dependencies shall be put in the {@link #hints}
 * map. Indirect dependencies (i.e. hints used by other factories used by this factory) will be
 * inspected automatically by {@link FactoryRegistry} in a recursive way.
 *
 * {@section Guidline for subclasses}
 * <ul>
 *   <li>Provide a public no-arguments constructor.</li>
 *   <li>In addition, it is recommended that implementations provide a constructor expecting
 *       a single {@link Hints} argument. This optional argument gives to the user some control
 *       over the factory's low-level details. The amount of control is factory specific.</li>
 *   <li>The lack of constructor expecting a {@link Map} argument is intentional. This is in
 *       order to discourage blind-copy of all user-supplied hints to the {@link #hints} map.</li>
 *   <li>Add the fully qualified name of the <u>implementation</u> class in the
 *       {@code META-INF/services/}<var>classname</var> file where <var>classname</var>
 *       is the fully qualified name of the service <u>interface</u>.</li>
 *   <li>The factory implementations will be discovered when
 *       {@link FactoryRegistry#scanForPlugins} will be invoked.</li>
 * </ul>
 *
 * {@section Example}
 * An application supplied a {@linkplain Hints#DATUM_FACTORY datum factory hint}, being passed to a
 * {@linkplain org.opengis.referencing.datum.DatumAuthorityFactory datum authority factory} so that
 * all datum created from an authority code will come from the supplied datum factory.
 *
 * <blockquote>
 * <b>Java code</b>
 * {@preformat java
 *     class DatumAuthorityFactory extends Factory {
 *         final DatumFactory datumFactory;
 *
 *         DatumAuthorityFactory() {
 *             this(EMPTY_HINTS);
 *         }
 *
 *         DatumAuthorityFactory(Hints userHints) {
 *             datumFactory = FactoryFinder.getDatumFactory(userHints);
 *             hints.put(Hints.DATUM_FACTORY, datumFactory);
 *         }
 *     }
 * }
 *
 * <b>Remarks</b><br>
 * <ul>
 *   <li>The map of ({@code userHints}) is never modified, since it is supplied by the user.</li>
 *   <li>All hints relevant to other factories are used in the constructor. Hints relevant to
 *       {@code DatumFactory} are used when {@code FactoryFinder.getDatumFactory(...)} is invoked.</li>
 *   <li>The {@code DatumAuthorityFactory} constructor stores only the hints relevant to it.
 *       Indirect dependencies (e.g. hints relevant to {@code DatumFactory}) will be inspected
 *       recursively by {@link FactoryRegistry}.</li>
 *   <li>In the above example, {@link #hints} will never be used for creating new factories.</li>
 * </ul></blockquote>
 * <p>
 * As seen in those examples this concept of a hint becomes more interesting when
 * the operation being controlled is discovery of other services used by the factory.
 * By supplying appropriate hints one can chain together several factories and retarget
 * them to an application specific task.
 *
 * @author Ian Schneider
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.03
 *
 * @see Hints
 * @see FactoryRegistry
 *
 * @since 2.1
 * @module
 */
public abstract class Factory {
    /**
     * An internal hint used for keeping trace of factories that has been processed.
     */
    private static final Hints.Key DONE = new Hints.Key(Set.class);

    /**
     * An internal hint meaning that a factory has been disposed.
     * The value is the disposal time, used for debugging purpose.
     */
    @SuppressWarnings("serial")
    private static final Hints.Key DISPOSED = new Hints.Key(Date.class) {
        @Override public String toString() {
            return "DISPOSED";
        }
    };

    /**
     * An immutable empty set of hints. This is different than a {@code null} hints, which means
     * <em>default</em> hints and may not be empty.
     *
     * @since 3.00
     */
    public static final Hints EMPTY_HINTS = new EmptyHints();

    /**
     * The {@linkplain #getImplementationHints implementation hints}. This map should be filled by
     * subclasses at construction time. If possible, constructors should not copy blindly all
     * user-provided hints. They should select only the relevant hints and resolve them as of
     * {@linkplain #getImplementationHints implementation hints} contract. For example if a
     * {@linkplain org.opengis.referencing.datum.DatumAuthorityFactory datum authority factory}
     * uses an ordinary {@linkplain org.opengis.referencing.datum.DatumFactory datum factory},
     * its method could be implemented as below (note that we should not check if the datum
     * factory is null, since key with null value is the expected behavior in this case).
     *
     * {@preformat java
     *     hints.put(Hints.DATUM_FACTORY, datumFactory);
     * }
     *
     * This field is not an instance of {@code Hints} because:
     * <ul>
     *   <li>The primary use of this map is to check if this factory can be reused.
     *       It is not for creating new factories.</li>
     *   <li>This map needs to allow {@code null} values, as of
     *       {@link #getImplementationHints} contract.</li>
     * </ul>
     */
    protected final Map<RenderingHints.Key, Object> hints;

    /**
     * An unmodifiable view of {@link #hints}. This is the actual map to be returned
     * by {@link #getImplementationHints}. Its content reflects the {@link #hints}
     * map even if the later is modified.
     */
    private final Map<RenderingHints.Key, Object> unmodifiableHints;

    /**
     * Creates a new factory instance.
     */
    protected Factory() {
        hints = new LinkedHashMap<>();
        unmodifiableHints = Collections.unmodifiableMap(hints);
    }

    /**
     * Controls the ordering of the enclosing {@linkplain Factory factory} relative to other
     * factories. An instance of this class is passed to the
     * {@link Factory#setOrdering Factory.setOrdering(Organizer)} method
     * after the enclosing factory has been registered in {@link FactoryRegistry}. That
     * {@code setOrdering} method can invoke any method in this class in order to specify whatever
     * the encloding factory should be selected {@linkplain #before(Class, boolean) before}
     * or {@linkplain #after(Class, boolean) after} an other factory.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.03
     *
     * @since 3.00
     * @module
     */
    protected final class Organizer {
        /**
         * The factory registry that created this {@code Organizer}.
         */
        private final ServiceRegistry registry;

        /**
         * The category for which to set ordering.
         */
        private final Class<?> category;

        /**
         * Creates a new organizer.
         */
        Organizer(final ServiceRegistry registry, final Class<?> category) {
            this.registry = registry;
            this.category = category;
        }

        /**
         * Implementation of {@link #before(Class, boolean)} and {@link #after(Class, boolean)}.
         * The {@code before} boolean argument tells which method is invoking this one.
         */
        @SuppressWarnings("unchecked")
        private void setOrdering(final Class<?> type, final boolean subclasses, final boolean before) {
            final Factory factory = Factory.this;
            assert category.isInstance(factory);
            if (!subclasses && !category.isAssignableFrom(type)) {
                throw new ClassCastException(Errors.format(
                        Errors.Keys.IllegalClass_2, type, category));
            }
            /*
             * The 'exclude' below is the base type of factory to not order, or null if none.
             * Usually this is null. However there is one special case: if the user specified
             * a type which is the same class or a parent class of the factory class, then we
             * have a contradiction: the factory can not be before (or after) itself.
             *
             * We could have limited the exclusion to exactly the factory class, which is
             * - strictly speaking - the only contradiction. However we extend the exclusion
             * to factory subclasses as well on the assumption that if the caller gave a parent
             * class, he probably means "put before (or after) every factory that are not of my
             * kind". This is what HTTP and URN AuthorityFactories mean for instance.
             *
             * This behavior is not documented for now, so it may be revised in the future.
             */
            final Class<?> exclude = type.isInstance(factory) ? factory.getClass() : null;
            @SuppressWarnings("rawtypes")
            final Class category = (Class) this.category; // Intentionnaly unchecked.
            final Iterator<?> it;
            if (subclasses) {
                it = registry.getServiceProviders(category, false);
            } else {
                it = Collections.singleton(registry.getServiceProviderByClass(type)).iterator();
            }
            while (it.hasNext()) {
                final Object other = it.next();
                if (type.isInstance(other) && (exclude == null || !exclude.isInstance(other))) {
                    if (before) {
                        registry.setOrdering(category, factory, other);
                    } else {
                        registry.setOrdering(category, other, factory);
                    }
                }
            }
        }

        /**
         * Implementation of {@link #before(String, boolean)} and {@link #after(String, boolean)}.
         * The {@code before} boolean argument tells which method is invoking this one.
         */
        private void setOrdering(final String type, final boolean subclasses, final boolean before) {
            final Class<?> c;
            try {
                c = Class.forName(type);
            } catch (ClassNotFoundException e) {
                Logging.recoverableException(null, Organizer.class, before ? "before" : "after", e);
                return;
            }
            setOrdering(c, subclasses, before);
        }

        /**
         * Specifies that the enclosing factory should be selected before the specified factory.
         * In other words, the enclosing factory should have precedence over the given one.
         * <p>
         * If {@code subclasses} is {@code false}, then this method searches for a factory of
         * exactly the given class. Invoking this method has no effect on the ordering relative
         * to subclasses of the given factory class, unless {@code subclasses} is {@code true}.
         *
         * @param type The class of the factory which should be selected after the enclosing one.
         * @param subclasses {@code false} if the ordering should apply to the given class only,
         *        or {@code true} if it should apply to subclasses of {@code type} as well.
         */
        public void before(final Class<?> type, final boolean subclasses) {
            setOrdering(type, subclasses, true);
        }

        /**
         * Specifies that the enclosing factory should be selected before the specified factory.
         * This method delegates to {@link #before(Class, boolean)}, or does nothing if no class
         * are found for the given fully-qualified name. This variant is useful when a class may
         * or may not be on the classpath.
         *
         * @param type The fully-qualified name of the factory which should be selected after
         *        the enclosing one.
         * @param subclasses {@code false} if the ordering should apply to the given class only,
         *        or {@code true} if it should apply to subclasses of {@code type} as well.
         */
        public void before(final String type, final boolean subclasses) {
            setOrdering(type, subclasses, true);
        }

        /**
         * Specifies that the enclosing factory should be selected after the specified factory.
         * In other words, the given factory should have precedence over the enclosing one.
         * <p>
         * If {@code subclasses} is {@code false}, then this method searches for a factory of
         * exactly the given class. Invoking this method has no effect on the ordering relative
         * to subclasses of the given factory class, unless {@code subclasses} is {@code true}.
         *
         * @param type The class of the factory which should be selected before the enclosing one.
         * @param subclasses {@code false} if the ordering should apply to the given class only,
         *        or {@code true} if it should apply to subclasses of {@code type} as well.
         */
        public void after(final Class<?> type, final boolean subclasses) {
            setOrdering(type, subclasses, false);
        }

        /**
         * Specifies that the enclosing factory should be selected after the specified factory.
         * This method delegates to {@link #after(Class, boolean)}, or does nothing if no class
         * are found for the given fully-qualified name. This variant is useful when a class may
         * or may not be on the classpath.
         *
         * @param type The fully-qualified name of the factory which should be selected before
         *        the enclosing one.
         * @param subclasses {@code false} if the ordering should apply to the given class only,
         *        or {@code true} if it should apply to subclasses of {@code type} as well.
         */
        public void after(final String type, final boolean subclasses) {
            setOrdering(type, subclasses, false);
        }
    }

    /**
     * Invoked by {@link FactoryRegistry} after registration in order to set the relative ordering.
     * The default implementation does nothing. Subclasses should override this method and invoke
     * {@link Organizer#before(Class, boolean) Organizer.before(...)} or
     * {@link Organizer#after(Class, boolean) Organizer.after(...)} if they know which factory
     * should have precedence over an other factory.
     *
     * @param organizer A handler given by {@link FactoryRegistry} for controlling the ordering
     *        of this factory relative to other factories.
     *
     * @since 3.00
     */
    protected void setOrdering(final Organizer organizer) {
    }

    /**
     * Returns an {@linkplain Collections#unmodifiableMap unmodifiable} view of {@linkplain #hints}
     * used by this factory to customize its use. This map is <strong>not</strong> guaranteed
     * to contains all the hints supplied by the user; it may be only a subset. Consequently,
     * hints provided here are usually not suitable for creating new factories.
     * <p>
     * The primary purpose of this method is to determine if an <strong>existing</strong>
     * factory instance can be reused for a set of user-supplied hints. This method is invoked by
     * {@link FactoryRegistry} in order to compare this factory's hints against user's hints.
     * This is dependency introspection only; {@code FactoryRegistry} <strong>never</strong>
     * invokes this method for creating new factories.
     *
     * {@section Content}
     * Keys are usually static constants from the {@link Hints} class, while values are
     * instances of some key-dependent class. The {@linkplain Map#keySet key set} must contains
     * at least all hints impacting functionality. While the key set may contains all hints
     * supplied by the user, it is recommended to limit the set to only the hints used by this
     * particular factory instance. A minimal set will helps {@link FactoryRegistry} to compare
     * only hints that matter and avoid the creation of unnecessary instances of this factory.
     * <p>
     * The hint values may be different than the one supplied by the user. If a user supplied a
     * hint as a {@link Class} object, this method shall replace it by the actual instance used,
     * if possible.
     *
     * {@section Lifetime}
     * If possible, do not keep a reference to the returned map or map entries for a long time.
     * The map may reference resources to make elligible for garbage collection at a later time.
     * This is especially true for factories created by {@link DynamicFactoryRegistry}.
     *
     * @return The map of hints, or an empty map if none.
     */
    public Map<RenderingHints.Key, ?> getImplementationHints() {
        return unmodifiableHints;
    }

    /**
     * Returns {@code true} if this factory meets the requirements specified by a map of hints. The
     * default implementation checks if, for any key in the given map which is also presents in this
     * factory's {@linkplain #getImplementationHints implementation hints}, the corresponding values
     * are compatible. Values are considered compatible if they meet one of the following conditions:
     * <p>
     * <ul>
     *   <li>The values are {@linkplain Object#equals equal}</li>
     *   <li>The value in the {@code hint} map is a {@link Class} or an array of classes, and the
     *       value in this factory's implementation hints is an instance of at least one of those
     *       classes.</li>
     * </ul>
     * <p>
     * If this factory has dependencies toward other factories (or to be more specific, if the
     * values of some {@linkplain #getImplementationHints implementation hints} are instances
     * of {@code Factory}), then this method will invokes itself recursively for those factory
     * instances, with a guard against infinite loops if there is cyclic dependencies.
     *
     * @param hints The user requirements (never {@code null}).
     * @return {@code true} if this factory meets the hints requirements.
     *
     * @since 3.00
     */
    protected boolean hasCompatibleHints(final Hints hints) {
        /*
         * Do not synchronize this method. Experience show that it is deadlock-prone.
         * Most subclasses have already synchronized getImplementationHints() if they
         * needed to do so. Keeping this method non-synchronized is safe if subclasses
         * do not modify their hints after initialization (which is the expected behavior).
         */
        final Map<RenderingHints.Key, ?> implementationHints = getImplementationHints();
        if (implementationHints == null) {
            // Factory was bad and did not meet contract - assume it used no Hints.
            return true;
        }
        if (implementationHints.containsKey(DISPOSED)) {
            // Factory has been disposed, so it is not available anymore for any use.
            return false;
        }
        Hints remaining = null;
        @SuppressWarnings("unchecked")
        Set<Factory> alreadyDone = (Set<Factory>) hints.get(DONE);
        for (final Map.Entry<?,?> entry : implementationHints.entrySet()) {
            final Object key      = entry.getKey();
            final Object value    = entry.getValue();
            final Object expected = hints.get(key);
            if (expected != null) {
                /*
                 * We have found a hint that matter. Checks its value as described in
                 * the javadoc. Any mismatch cause immediate termination of this method.
                 */
                if (expected instanceof Class<?>) {
                    if (!((Class<?>) expected).isInstance(value)) {
                        return false;
                    }
                } else if (expected instanceof Class<?>[]) {
                    final Class<?>[] types = (Class<?>[]) expected;
                    int i = 0;
                    do if (i >= types.length) return false;
                    while (!types[i++].isInstance(value));
                } else if (!expected.equals(value)) {
                    return false;
                }
            }
            /*
             * Checks recursively in factory dependencies, if any. Note that the dependencies
             * will be checked against a subset of user's hints. More specifically, all hints
             * processed by the current pass will NOT be passed to the factories dependencies.
             * This is because the same hint may appears in the "parent" factory and a "child"
             * dependency with different value. For example the FORCE_LONGITUDE_FIRST_AXIS_ORDER
             * hint has the value TRUE in OrderedAxisAuthorityFactory, but the later is basically
             * a wrapper around the EPSG Factory (typically), which has the value FALSE for the
             * same hint.
             *
             * Additional note: The 'alreadyDone' set is a safety against cyclic dependencies,
             * in order to protect ourself against never-ending loops. This is not the same
             * kind of dependencies than 'FactoryRegistry.testingHints'. It is a "factory A
             * depends on factory B which depends on factory A" loop, which is legal.
             */
            if (value instanceof Factory) {
                final Factory dependency = (Factory) value;
                if (alreadyDone != null && alreadyDone.contains(dependency)) {
                    // The dependency has already been processed. Executing the
                    // remainder of this code would cause an infinite loop.
                    continue;
                }
                if (remaining == null) {
                    remaining = hints.clone();
                    remaining.keySet().removeAll(implementationHints.keySet());
                    if (remaining.isEmpty()) {
                        // No need to perform any additional checks, so do not execute the
                        // remaining lines in order to avoid the creation of useless objects.
                        continue;
                    }
                    if (alreadyDone == null) {
                        alreadyDone = new HashSet<>();
                        if (remaining.put(DONE, alreadyDone) != null) {
                            throw new AssertionError(); // Paranoiac check (should never happen).
                        }
                    }
                    if (!alreadyDone.add(this)) {
                        throw new AssertionError(); // Paranoiac check (should never happen).
                    }
                }
                /*
                 * Recursive call to this method for scanning dependencies. Useful only if
                 * there is at least 1 remaining user hint, not counting the internal DONE
                 * hint which has been added to the map.
                 */
                assert remaining.isEmpty() || remaining.containsKey(DONE);
                if (remaining.size() > 1) {
                    assert alreadyDone.contains(this);
                    if (!dependency.hasCompatibleHints(remaining)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns whatever this factory is ready for use. The {@link ConformanceResult#pass} method
     * shall returns {@code false} if this factory is not available in the current configuration,
     * typically because some external resources were not found. Implementors are encouraged to
     * provide an {@linkplain ConformanceResult#getExplanation() explanation} when the factory
     * is not available.
     * <p>
     * This method can <strong>not</strong> be used as a manager for automatic download of external
     * resources. It is just a way to tell to {@link FactoryRegistry} that this factory exists, but
     * can't do its job for whatever reasons, so {@code FactoryRegistry} has to choose an other
     * factory. In other words, this method is used only as a filter.
     * <p>
     * Note also that {@code FactoryRegistry} is not designed for factories with intermittent state
     * (i.e. value of {@code availability().pass()} varying with time). The behavior is undetermined
     * in such case.
     * <p>
     * In the default implementation, the conformance result passes as long as this factory
     * has not been {@linkplain #dispose disposed}.
     *
     * @return A conformance result with {@code pass() == true} if this factory is ready for use.
     *
     * @since 3.03
     */
    public ConformanceResult availability() {
        return new Availability();
    }

    /**
     * The default conformance result returned by {@link Factory#availability()}. This class
     * is <cite>live</cite>, i.e. the {@link #pass()} method will return {@code false} if the
     * enclosing factory has been {@linkplain Factory#dispose disposed} without the need to
     * create a new instance of {@code Availability}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.14
     *
     * @since 3.03
     * @module
     */
    protected class Availability implements ConformanceResult {
        /**
         * The value returned by {@link #getExplanation()}.
         * Will be created only when first needed.
         */
        private InternationalString explanation;

        /**
         * If the factory is not available because of some exception,
         * the exception that caused the failure. Otherwise {@code null}.
         */
        private final Throwable failureCause;

        /**
         * Creates a default {@code Availability} object. The new conformance result
         * will {@linkplain #pass() pass} as long as the enclosing factory has not
         * been disposed.
         */
        public Availability() {
            failureCause = null;
        }

        /**
         * Creates a conformance result which declares that the factory is not
         * available because of the given exception.
         *
         * @param failureCause The raison why the factory is not available.
         */
        public Availability(final Throwable failureCause) {
            this.failureCause = failureCause;
        }

        /**
         * Returns the requirement against which the factory is being evaluated.
         * The default implementation returns {@code null}, which is a departure
         * from ISO 19115 since this information is supposed to be mandatory.
         * Subclasses are encouraged to provide a value.
         */
        @Override
        public Citation getSpecification() {
            return null;
        }

        /**
         * Returns an explanation of the meaning of conformance for this result. If this
         * {@code Availability} object has been constructed with a {@link Throwable}, then this
         * method returns {@code "Error"} completed with the message of the <em>last</em> cause
         * having a {@linkplain Throwable#getLocalizedMessage() localized message} (we pickup
         * the last cause on the assumption that it is the root of the problem). Otherwise this
         * method returns a text like "<cite>This result indicates if the factory is available
         * for use</cite>".
         */
        @Override
        public synchronized InternationalString getExplanation() {
            if (explanation == null) {
                String message = null;
                for (Throwable cause=failureCause; cause!=null; cause=cause.getCause()) {
                    final String candidate = cause.getLocalizedMessage();
                    if (candidate != null) {
                        message = candidate;
                    }
                }
                if (message != null) {
                    explanation = Vocabulary.formatInternational(Vocabulary.Keys.Error_1, message);
                } else {
                    explanation = Descriptions.formatInternational(Descriptions.Keys.
                            ConformanceMeansFactoryAvailable_1, Factory.this.getClass());
                }
            }
            return explanation;
        }

        /**
         * If the factory is not available because of some exception, the exception that caused
         * the failure. Otherwise {@code null}. Note that a {@code null} value doesn't mean that
         * the factory is available - the {@link #pass()} method still need to be invoked.
         *
         * @return The exception which make the factory unavailable, or {@code null} if none.
         */
        public Throwable getFailureCause() {
            return failureCause;
        }

        /**
         * Returns {@code true} if the enclosing factory is ready for use. The default
         * implementation returns {@code true} if no throwable was given at construction
         * time and the enclosing factory has not been {@linkplain Factory#dispose disposed}.
         *
         * @return {@code true} if the enclosing factory is ready for use.
         */
        @Override
        public Boolean pass() {
            if (failureCause != null) {
                return Boolean.FALSE;
            }
            synchronized (Factory.this) {
                return !hints.containsKey(DISPOSED);
            }
        }

        /**
         * Returns a string representation of this conformance result.
         * This is mostly for debugging purpose.
         */
        @Override
        public String toString() {
            Class<?> c = getClass();
            while (c.isAnonymousClass()) {
                c = c.getSuperclass();
            }
            return Classes.getShortClassName(Factory.this) + '.' + c.getSimpleName() +
                    '[' + pass() + ": " + getExplanation() + ']';
        }
    }

    /**
     * Disposes resources used by this factory. This method is invoked mainly by
     * {@link DynamicFactoryRegistry}; users are not expected to dispose resources
     * themself since factories may be shared by many classes in the system.
     * <p>
     * The default implementation clears the {@linkplain #hints} map and remember that this factory
     * has been disposed. Subclasses should override this method as below if they can perform more
     * resource disposal.
     *
     * {@preformat java
     *     protected synchronized void dispose(boolean shutdown) {
     *         // Disposes more resources here.
     *         super.dispose(shutdown);
     *     }
     * }
     *
     * @param shutdown {@code false} for normal disposal, or {@code true} if this method is invoked
     *        during the process of a JVM shutdown. In the later case this method may dispose
     *        resources more aggressively. For example
     *        {@link org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory}
     *        may shutdown the JavaDB embedded database.
     *
     * @since 3.00
     */
    protected synchronized void dispose(boolean shutdown) {
        hints.clear();
        hints.put(DISPOSED, new Date());
    }

    /**
     * Returns a hash value for this factory. This method computes the hash value using
     * only immutable properties. This computation does <strong>not</strong> rely on
     * {@linkplain #getImplementationHints implementation hints}, since there is no
     * guarantee that they will not change.
     *
     * @since 2.3
     */
    @Override
    public final int hashCode() {
        return getClass().hashCode() ^ 105470090;
    }

    /**
     * Compares this factory with the specified object for equality.
     * This method returns {@code true} if and only if:
     * <p>
     * <ul>
     *   <li>Both objects are of the exact same class
     *       (a <cite>is instance of</cite> relationship is not enough).</li>
     *   <li>{@linkplain #getImplementationHints implementation hints} are
     *       {@linkplain Map#equals equal}.</li>
     * </ul>
     * <p>
     * The requirement for the <cite>exact same class</cite> is needed for consistency with the
     * {@linkplain FactoryRegistry factory registry} working, since at most one instance of a given
     * class {@linkplain FactoryRegistry#getServiceProviderByClass is allowed} in a registry.
     *
     * @param object The object to compare.
     * @return {@code true} if the given object is equal to this factory.
     *
     * @since 2.3
     */
    @Override
    public final boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final Factory that = (Factory) object;
            final Set<FactoryComparator> comparators = new HashSet<>();
            return new FactoryComparator(this, that).compare(comparators);
        }
        return false;
    }

    /**
     * Returns a string representation of this factory. This method is mostly for debugging purpose,
     * so the string format may vary across different implementations or versions. The default
     * implementation formats all {@linkplain #getImplementationHints implementation hints} as a
     * tree. If the implementation hints include some {@linkplain Factory factory} dependencies,
     * then the implementation hints for those dependencies will appears under a tree branch.
     *
     * @return A string representation of this factory.
     *
     * @since 2.3
     */
    @Override
    public String toString() {
        final String name = format(this);
        final Map<Factory,String> done = new IdentityHashMap<>();
        // We used IdentityHashMap above because we don't want to rely on Factory.equals(...)
        done.put(this, name);
        final String tree = format(getImplementationHints(), done);
        return name + System.lineSeparator() + tree;
    }

    /**
     * Returns a string representation of the specified hints. This is used by
     * {@link Hints#toString} in order to share the code provided in this class.
     */
    static String toString(final Map<?,?> hints) {
        return format(hints, new IdentityHashMap<Factory, String>());
    }

    /**
     * Formats a name for the specified factory.
     */
    private static String format(final Factory factory) {
        String name = Classes.getShortClassName(factory);
        if (factory instanceof AuthorityFactory) {
            final Citation authority = ((AuthorityFactory) factory).getAuthority();
            if (authority != null) {
                name = name + "[\"" + authority.getTitle() + "\"]";
            }
        }
        return name;
    }

    /**
     * Formats the specified hints. This method is just the starting
     * point for {@link #format(Writer, Map, String, Map)} below.
     */
    private static String format(final Map<?,?> hints, final Map<Factory,String> done) {
        final Writer table;
        try {
            table = new TableWriter(null, " ");
            format(table, hints, "  ", done);
        } catch (IOException e) {
            // Should never happen, since we are writing in a buffer.
            throw new AssertionError(e);
        }
        return table.toString();
    }

    /**
     * Formats recursively the tree. This method invoke itself.
     */
    private static void format(final Writer table, final Map<?,?> hints, final String indent,
            final Map<Factory,String> done) throws IOException
    {
        for (final Map.Entry<?,?> entry : hints.entrySet()) {
            final Object k = entry.getKey();
            String key = (k instanceof RenderingHints.Key) ?
                    Hints.nameOf((RenderingHints.Key) k) : String.valueOf(k);
            Object value = entry.getValue();
            table.write(indent);
            table.write(key);
            char separator = ':';
            Factory recursive = null;
            if (value instanceof Factory) {
                recursive = (Factory) value;
                value = format(recursive);
                final String previous = done.put(recursive, key);
                if (previous != null) {
                    done.put(recursive, previous);
                    separator = '=';
                    value = previous;
                    recursive = null;
                }
            }
            table.write('\t');
            table.write(separator);
            table.write(' ');
            table.write(String.valueOf(value));
            table.write('\n');
            if (recursive != null) {
                final String nextIndent = CharSequences.spaces(indent.length() + 2).toString();
                format(table, recursive.getImplementationHints(), nextIndent, done);
            }
        }
    }
}
