/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import net.jcip.annotations.ThreadSafe;
import org.opengis.util.FactoryException;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * Create metadata objects of the given {@link Class} using the properties given in a {@link Map}.
 * For any given type, this class tries to instantiate the metadata object in two ways:
 *
 * <ul>
 *   <li><p>First, if a collection of factories has been given to the constructor,
 *       then this method will search for a {@code create(Map, ...)} method returning
 *       an object assignable to the requested one. For example if the user requests a
 *       {@link org.opengis.referencing.crs.VerticalCRS} and this {@code MetadataFactory} has been
 *       given a {@link org.opengis.referencing.crs.CRSFactory} at construction time, then the
 *       {@code CRSFactory.createVerticalCRS(Map, ...)} method will be used.</p></li>
 *
 *   <li><p>If no suitable factory is found, then {@code MetadataFactory} will try to instantiate
 *       a metadata implementation directly using {@link Class#newInstance()}. The class can be an
 *       interface like {@link org.opengis.metadata.citation.Citation} or its implementation class
 *       like {@link org.apache.sis.metadata.iso.citation.DefaultCitation}.
 *       The keys in the map shall be the {@linkplain KeyNamePolicy#UML_IDENTIFIER UML identifiers}
 *       or the {@linkplain KeyNamePolicy#JAVABEANS_PROPERTY Java Beans name} of metadata properties,
 *       for example {@code "title"} for the value to be returned by
 *       {@link org.opengis.metadata.citation.Citation#getTitle()}.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
public class MetadataFactory extends Factory {
    /**
     * An optional set of factories to try before to create the metadata objects directly.
     * This is {@code null} if there is no such factory.
     */
    private final Object[] factories;

    /**
     * The standards implemented by this factory.
     */
    private final MetadataStandard[] standards;

    /**
     * The methods to use for instantiating object of the classes in the keys.
     * This is {@code null} if {@code factories} is null.
     */
    private final Map<Class<?>, FactoryMethod> factoryMethods;

    /**
     * Creates a new factory implementing the {@linkplain MetadataStandard#ISO_19115 ISO 19115}
     * standard and using the referencing factories known to {@link FactoryFinder}.
     */
    public MetadataFactory() {
        this(factories(), MetadataStandard.ISO_19115);
    }

    /**
     * Creates a new factory for the given metadata standards and the referencing factories
     * known to {@link FactoryFinder}.
     *
     * @param standards The metadata standards implemented by this factory.
     */
    public MetadataFactory(final MetadataStandard... standards) {
        this(factories(), standards);
    }

    /**
     * Creates a new factory for the given metadata standards. The given collection of
     * factories will be used if possible before to instantiate a metadata object directly.
     *
     * @param factories The factories to try before to instantiate the metadata directly,
     *        or {@code null} if none.
     * @param standards The metadata standards implemented by this factory.
     */
    public MetadataFactory(final Set<?> factories, final MetadataStandard... standards) {
        if (!isNullOrEmpty(factories)) {
            this.factories = factories.toArray();
            factoryMethods = new HashMap<>();
        } else {
            this.factories = null;
            factoryMethods = null;
        }
        this.standards = standards.clone();
    }

    /**
     * Returns the factories declared in {@link FactoryFinder}.
     */
    private static Set<?> factories() {
        final Set<Object> factories = new LinkedHashSet<>();
        for (int i=0; ; i++) {
            final Set<?> factory;
            switch (i) {
                /*
                 * We declare only the factories having create(Map<String,?>, ...) methods.
                 * We pickup the DatumFactory first because in the particular case of Geotk
                 * implementation, CRS and CS factories are also Datum factory and we don't
                 * want them to hide the default DatumFactory.
                 */
                case 0: factory = FactoryFinder.getDatumFactories(null); break;
                case 1: factory = FactoryFinder.getCSFactories   (null); break;
                case 2: factory = FactoryFinder.getCRSFactories  (null); break;
                default: return factories;
            }
            /*
             * Retains only the first factory. It is not worth to retain the next one because
             * they should have the same set of create methods, and FactoryMethod pickup only
             * the create method found in the first factory. Actually, it would be worth only
             * if the next factories declare more create methods than the first one, but we
             * don't check that.
             */
            final Iterator<?> it = factory.iterator();
            if (it.hasNext()) {
                factories.add(it.next());
            }
        }
    }

    /**
     * Creates a new metadata of the given type, initialized with the property values given in the
     * properties map.
     *
     * @param  <T> The parameterized type of the {@code type} argument.
     * @param  type The interface or implementation type of the metadata object to be created.
     * @param  properties The property values to be given to the metadata object.
     * @return A new metadata object of the given class or implementation, filled with the given values.
     * @throws FactoryException If the metadata object can not be created.
     */
    public <T> T create(final Class<T> type, final Map<String,?> properties) throws FactoryException {
        /*
         * First, check if there is a factory method for the given type.
         */
        if (factoryMethods != null) {
            FactoryMethod method;
            synchronized (factoryMethods) {
                method = factoryMethods.get(type);
                if (method == null) {
                    method = FactoryMethod.find(type, factories);
                    if (method == null) {
                        method = FactoryMethod.NULL;
                    } else {
                        // Found a method. Check if we already created an equal instance previously.
                        // If yes, we can safely share the instance since FactoryMethod are immutable.
                        for (final FactoryMethod existing : factoryMethods.values()) {
                            if (method.equals(existing)) {
                                method = existing;
                                break;
                            }
                        }
                    }
                    factoryMethods.put(type, method);
                }
            }
            final Object metadata = method.create(properties);
            if (metadata != null) {
                return type.cast(metadata);
            }
        }
        /*
         * At this point, we have not found any suitable factory method.
         * Try to instantiate the implementation class directly.
         */
        Exception failure = null;
        for (final MetadataStandard standard : standards) {
            if (standard.isMetadata(type)) {
                final Class<?> impl = standard.getImplementation(type);
                final Object metadata;
                try {
                    metadata = impl.newInstance();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    /*
                     * We catch all Exceptions because Class.newInstance() propagates all of them,
                     * including the checked ones (it bypass the compile-time exception checking).
                     */
                    if (failure == null || failure instanceof InstantiationException) {
                        failure = e;
                    }
                    continue;
                }
                final Map<String,Object> asMap = standard.asValueMap(metadata,
                        KeyNamePolicy.JAVABEANS_PROPERTY, ValueExistencePolicy.NON_EMPTY);
                try {
                    asMap.putAll(properties);
                } catch (RuntimeException e) {
                    throw new FactoryException(e.getLocalizedMessage(), e);
                }
                return type.cast(metadata);
            }
        }
        throw new FactoryException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, type), failure);
    }
}
