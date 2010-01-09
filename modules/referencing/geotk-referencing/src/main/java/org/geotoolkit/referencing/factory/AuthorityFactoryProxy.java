/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.factory;

import java.util.Set;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.opengis.referencing.*;
import org.opengis.referencing.crs.*;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.converter.Classes;


/**
 * Delegates object creations to one of the {@code create} methods in a backing
 * {@linkplain AuthorityFactory authority factory}. It is possible to use the generic
 * {@link AuthorityFactory#createObject createObject} method instead of this class,
 * but some factory implementations are more efficient when we use the most specific
 * {@code create} method. For example when using a
 * {@linkplain org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory EPSG factory backed
 * by a SQL database}, invoking {@link CRSAuthorityFactory#createCoordinateReferenceSystem
 * createCoordinateReferenceSystem} instead of {@link AuthorityFactory#createObject createObject}
 * method will reduce the amount of tables to be queried.
 * <p>
 * This class is useful when the same {@code create} method need to be invoked often,
 * but is unknown at compile time. It may also be used as a workaround for authority
 * factories that don't implement the {@code createObject} method.
 * <p>
 * <b>Example:</b> The following code creates a proxy which will delegates its work to the
 * {@link CRSAuthorityFactory#createGeographicCRS createGeographicCRS} method.
 *
 * {@preformat java
 *     String code = ...;
 *     AuthorityFactory factory = ...;
 *     AuthorityFactoryProxy proxy = AuthorityFactoryProxy.getInstance(factory, GeographicCRS.class);
 *     GeographicCRS crs = proxy.create(factory, code); // Invokes factory.createGeographicCRS(code);
 * }
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
@Immutable
abstract class AuthorityFactoryProxy {
    /**
     * Creates a new proxy.
     */
    private AuthorityFactoryProxy() {
    }

    /**
     * Returns a proxy instance which will create objects of the specified type using the
     * specified factory.
     *
     * @param  factory The factory to use for object creations.
     * @param  type    The type of objects to be created by the proxy.
     */
    public static AuthorityFactoryProxy getInstance(final AuthorityFactory factory,
            final Class<? extends IdentifiedObject> type)
    {
        return getInstance(factory, type, true);
    }

    /**
     * Implementation of {@code getInstance} allowing to disable the delegation to
     * {@link AbstractAuthorityFactoryProxy} when the {@code specific} argument is
     * {@code false}. That argument should always be {@code true} - it may be set
     * to false only for testing purpose.
     */
    static AuthorityFactoryProxy getInstance(final AuthorityFactory factory,
            Class<? extends IdentifiedObject> type, final boolean specific)
    {
        AbstractAuthorityFactory.ensureNonNull("type",    type);
        AbstractAuthorityFactory.ensureNonNull("factory", factory);
        final AbstractAuthorityFactoryProxy<?> proxy = AbstractAuthorityFactoryProxy.getInstance(type);
        type = proxy.type.asSubclass(IdentifiedObject.class);
        /*
         * Checks for some special cases for which a fast implementation is available.
         * Note that the cast should be safe because otherwise the above line would
         * have already throw a ClassCastException.
         */
        if (specific && factory instanceof AbstractAuthorityFactory) {
            @SuppressWarnings("unchecked")
            final AbstractAuthorityFactoryProxy<? extends IdentifiedObject> cast =
                    (AbstractAuthorityFactoryProxy<? extends IdentifiedObject>) proxy;
            return new Geotoolkit((AbstractAuthorityFactory) factory, cast);
        }
        if (factory instanceof CRSAuthorityFactory) {
            final CRSAuthorityFactory crsFactory = (CRSAuthorityFactory) factory;
            if (type.equals(             ProjectedCRS.class)) return new  Projected(crsFactory);
            if (type.equals(            GeographicCRS.class)) return new Geographic(crsFactory);
            if (type.equals(CoordinateReferenceSystem.class)) return new        CRS(crsFactory);
        }
        /*
         * Fallback on the generic case using reflection.
         */
        return new Default(factory, type);
    }

    /**
     * Returns the type of the objects to be created by this proxy instance.
     */
    public abstract Class<? extends IdentifiedObject> getObjectType();

    /**
     * Returns the authority factory used by the {@link #create create} method.
     */
    public abstract AuthorityFactory getAuthorityFactory();

    /**
     * Returns the set of authority codes.
     *
     * @throws FactoryException if access to the underlying database failed.
     */
    public final Set<String> getAuthorityCodes() throws FactoryException {
        return getAuthorityFactory().getAuthorityCodes(getObjectType());
    }

    /**
     * Creates an object for the specified code. This method will delegates to the most
     * specific {@code create} method from the authority factory. The returned object
     * will always be of the type returned by {@link #getObjectType()}.
     *
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     * @throws FactoryException if the object creation failed for some other reason.
     */
    public abstract IdentifiedObject create(String code)
            throws NoSuchAuthorityCodeException, FactoryException;

    /**
     * Returns a string representation of this proxy, for debugging purpose only.
     */
    @Override
    public String toString() {
        return toString(AuthorityFactoryProxy.class);
    }

    /**
     * Returns a string representation of the specified object, for debugging purpose only.
     */
    final String toString(final Class<?> owner) {
        final AuthorityFactory factory = getAuthorityFactory();
        return Classes.getShortName(owner) + '[' +
               Classes.getShortName(getObjectType()) + " in " +
               Classes.getShortClassName(factory) + "(\"" +
               factory.getAuthority().getTitle() + "\")]";
    }




    /**
     * An implementation that delegates to {@link AbstractAuthorityFactoryProxy}.
     * This probably is most common case in Geotk implementation.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    @Immutable
    private static final class Geotoolkit extends AuthorityFactoryProxy {
        /**
         * The authority factory on which to delegates.
         */
        private final AbstractAuthorityFactory factory;

        /**
         * The proxy to use for the delegation..
         */
        private final AbstractAuthorityFactoryProxy<? extends IdentifiedObject> proxy;

        /**
         * Creates a new proxy which will delegates the object creation to the specified instance.
         */
        Geotoolkit(final AbstractAuthorityFactory factory,
                 final AbstractAuthorityFactoryProxy<? extends IdentifiedObject> proxy)
        {
            this.factory = factory;
            this.proxy = proxy;
        }

        @Override
        public Class<? extends IdentifiedObject> getObjectType() {
            return proxy.type;
        }

        @Override
        public AuthorityFactory getAuthorityFactory() {
            return factory;
        }

        @Override
        public IdentifiedObject create(final String code) throws FactoryException {
            return proxy.create(factory, code);
        }
    }




    /**
     * A default implementation using reflections. To be used only when we don't provide
     * a specialized, more efficient, implementation.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    private static final class Default extends AuthorityFactoryProxy {
        /**
         * The argument types of {@code createFoo} methods.
         */
        private static final Class<?>[] PARAMETERS = new Class<?>[] {String.class};

        /**
         * The authority factory on which to delegates.
         */
        private final AuthorityFactory factory;

        /**
         * The type of the objects to be created.
         */
        private final Class<? extends IdentifiedObject> type;

        /**
         * The {@code createFoo} method to invoke.
         */
        private final Method method;

        /**
         * Creates a new proxy which will delegates the object creation to the specified instance.
         */
        Default(final AuthorityFactory factory, final Class<? extends IdentifiedObject> type)
                throws IllegalArgumentException
        {
            this.factory = factory;
            this.type = type;
            final Method[] candidates = factory.getClass().getMethods();
            for (int i=0; i<candidates.length; i++) {
                final Method c = candidates[i];
                if (c.getName().startsWith("create") && type.equals(c.getReturnType()) &&
                        Arrays.equals(PARAMETERS, c.getParameterTypes()))
                {
                    method = c;
                    return;
                }
            }
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1, type));
        }

        @Override
        public Class<? extends IdentifiedObject> getObjectType() {
            return type;
        }

        @Override
        public AuthorityFactory getAuthorityFactory() {
            return factory;
        }

        @Override
        public IdentifiedObject create(final String code) throws FactoryException {
            try {
                return (IdentifiedObject) method.invoke(factory, code);
            } catch (InvocationTargetException exception) {
                final Throwable cause = exception.getCause();
                if (cause instanceof FactoryException) {
                    throw (FactoryException) cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error) {
                    throw (Error) cause;
                }
                throw new FactoryException(cause.getLocalizedMessage(), cause);
            } catch (IllegalAccessException exception) {
                throw new FactoryException(exception.getLocalizedMessage(), exception);
            }
        }
    }




    /**
     * An implementation for {@link CoordinateReferenceSystem} objects.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    private static class CRS extends AuthorityFactoryProxy {
        /**
         * The authority factory on which to delegates.
         */
        protected final CRSAuthorityFactory factory;

        protected CRS(final CRSAuthorityFactory factory) {
            this.factory = factory;
        }

        @Override public Class<? extends CoordinateReferenceSystem> getObjectType() {
            return CoordinateReferenceSystem.class;
        }

        @Override public final AuthorityFactory getAuthorityFactory() {
            return factory;
        }

        @Override public IdentifiedObject create(final String code) throws FactoryException {
            return factory.createCoordinateReferenceSystem(code);
        }
    }


    /**
     * An implementation for {@link GeographicCRS} objects.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    private static final class Geographic extends CRS {
        protected Geographic(final CRSAuthorityFactory factory) {
            super(factory);
        }

        @Override public Class<GeographicCRS> getObjectType() {
            return GeographicCRS.class;
        }

        @Override public IdentifiedObject create(final String code) throws FactoryException {
            return factory.createGeographicCRS(code);
        }
    }


    /**
     * An implementation for {@link ProjectedCRS} objects.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    private static final class Projected extends CRS {
        protected Projected(final CRSAuthorityFactory factory) {
            super(factory);
        }

        @Override public Class<ProjectedCRS> getObjectType() {
            return ProjectedCRS.class;
        }

        @Override public IdentifiedObject create(final String code) throws FactoryException {
            return factory.createProjectedCRS(code);
        }
    }
}
