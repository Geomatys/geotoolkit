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

/**
 * Utility classes which enable dynamic binding to factory implementations at runtime. Because Geotk
 * core API consists mostly of interfaces (including <A HREF="http://www.geoapi.org">GeoAPI</A>),
 * factories play a role in how developers use the API. Although the interfaces that are declared in GeoAPI
 * are implemented in various Geotk packages, they should not be used directly. Instead they should be
 * obtained through factories.
 * <p>
 * Factories are some interfaces that define <cite>services</cite> (also called <cite>providers</cite>) for
 * instantiating other objects. In the Geotk library they extend the {@link org.geotoolkit.factory.Factory}
 * class, but this is not mandatory. For example the {@link org.opengis.referencing.crs.CRSAuthorityFactory}
 * GeoAPI interface (implemented in Geotk by {@link org.geotoolkit.referencing.factory.ReferencingObjectFactory})
 * is a service allowing instantiation of {@link org.opengis.referencing.crs.CoordinateReferenceSystem}
 * objects from identifiers.
 * <p>
 * Factories are registered for use in a <cite>services registry</cite>, which acts as a container
 * for service implementations. Service registries don't need to be a Geotk implementation. They
 * can be (but are not limited to) instances of:
 * <p>
 * <ul>
 *   <li>{@link java.util.ServiceLoader} provided since Java 6</li>
 *   <li>{@link javax.imageio.spi.ServiceRegistry} provided since Java 4</li>
 *   <li>{@link org.geotoolkit.factory.FactoryRegistry} provided since GeoTools 2.1</li>
 * </ul>
 *
 * {@section Getting started}
 * {@link org.geotoolkit.factory.FactoryFinder} and {@link org.geotoolkit.factory.AuthorityFactoryFinder}
 * provide convenience static methods for getting implementations of service interfaces. They are the
 * only methods for getting started with Geotk factories.
 *
 * {@section Registering a factory}
 * To declare a factory implementation, a {@code services} subdirectory is placed within the
 * {@code META-INF} directory that is present in every JAR file. This directory contains a file
 * for each factory interface that has one or more implementation classes present in the JAR file.
 * For example if a JAR file provides one or more {@link org.opengis.referencing.datum.DatumFactory}
 * implementations, then it must provide the following file:
 *
 * {@preformat text
 *     META-INF/services/org.opengis.referencing.datum.DatumFactory
 * }
 *
 * with a content similar to the one below:
 *
 * {@preformat text
 *     com.mycompany.MyDatumFactory1
 *     com.mycompany.MyDatumFactory2
 *     com.mycompany.MyDatumFactory3
 * }
 *
 * The ordering is initially unspecified. Users can
 * {@linkplain javax.imageio.spi.ServiceRegistry#setOrdering set an ordering} explicitly themselves,
 * or implementations can do that automatically
 * {@linkplain javax.imageio.spi.RegisterableService#onRegistration on registration}.
 * <p>
 * Note that the factory classes should be lightweight and quick to load. Implementations of these
 * interfaces should avoid complex dependencies on other classes and on native code. A usual pattern
 * for more complex services is to register a lightweight proxy for the heavyweight service.
 *
 * {@section Fetching a factory}
 * The example below iterates over all registered {@code DatumFactory} using the standard service
 * loader bundled in Java 6. Those factories may by registered in many different JAR files.
 *
 * {@preformat java
 *     ServiceLoader registry = ServiceLoader.load(DatumFactory.class);
 *     Iterator<DatumFactory> providers = registry.iterator();
 * }
 *
 * Following does the same using the Geotk factory registry, which allows more control.
 *
 * {@preformat java
 *     Class<?>[] categories = new Class[] {
 *         DatumFactory.class
 *         // Put more categories here, if desired.
 *     };
 *     FactoryRegistry registry = new FactoryRegistry(categories);
 *
 *     // Get the providers
 *     Filter filter = null;
 *     Hints  hints  = null;
 *     Iterator<DatumFactory> providers = registry.getServiceProviders(DatumFactory.class, filter, hints);
 * }
 *
 * Users wanting a specific implementation can
 * {@linkplain javax.imageio.spi.ServiceRegistry#getServiceProviders iterates through registered ones}
 * and pickup the desired implementation themself. An alternative is to bundle the criterions in a map
 * of {@linkplain org.geotoolkit.factory.Hints hints} and lets the factory registry (Geotk extension
 * only) selects an implementation accordingly. Note that the hints, if provided, don't need to apply
 * directly to the requested factory. They may apply indirectly through factory dependencies.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
package org.geotoolkit.factory;
