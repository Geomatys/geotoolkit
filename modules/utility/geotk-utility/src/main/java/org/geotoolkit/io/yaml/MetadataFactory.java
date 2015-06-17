/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2014, Geomatys
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
package org.geotoolkit.io.yaml;

import java.util.Set;
import java.util.Map;
import java.text.ParseException;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.TypeValuePolicy;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.resources.Errors;


/**
 * Creates metadata objects of the given {@link Class} using the properties given in a {@link Map}.
 * {@code MetadataFactory} tries to instantiate metadata objects using {@link Class#newInstance()}.
 * The class given to the {@link #create(Class, Map)} method is typically a GeoAPI interface (e.g.
 * {@link org.opengis.metadata.citation.Citation}), in which case {@code MetadataFactory} will try
 * to find its implementation class ({@link org.apache.sis.metadata.iso.citation.DefaultCitation}).
 * The keys in the map shall be the {@linkplain KeyNamePolicy#UML_IDENTIFIER UML identifiers} of metadata properties,
 * e.g. {@code "title"} for the value to be returned by {@link org.opengis.metadata.citation.Citation#getTitle()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 */
final class MetadataFactory extends Factory {
    /**
     * The default instance.
     */
    static final MetadataFactory DEFAULT = new MetadataFactory(MetadataStandard.ISO_19115);

    /**
     * The standard implemented by this factory.
     */
    private final MetadataStandard standard;

    /**
     * Creates a new factory for the given metadata standard.
     *
     * @param standard The metadata standard implemented by this factory.
     */
    MetadataFactory(final MetadataStandard standard) {
        this.standard = standard;
    }

    /**
     * Returns the property names and expected types for the given class.
     */
    final Map<String,Class<?>> getDefinition(final Class<?> type) {
        return standard.asTypeMap(type, KeyNamePolicy.UML_IDENTIFIER, TypeValuePolicy.ELEMENT_TYPE);
    }

    /**
     * Returns {@code true} if the given definition map contains all the given keys.
     */
    private static boolean containsAll(final Map<?,?> definition, final Set<?> keys) {
        for (final Object key : keys) {
            if (!definition.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the given type or a sub-type which contains all the given key, or {@code null} if none.
     *
     * @param  <T>        Compile-time {@code type}.
     * @param  type       Base type of the desired metadata object.
     * @param  definition The value of {@code getDefinition(type)}.
     * @param  keys       The keys of user-supplied properties of the metadata to construct.
     * @return The type of the metadata object to instantiate, or {@code null} if none.
     */
    private <T> Class<? extends T> guessType(final Class<T> type, final Map<?,?> definition, final Set<String> keys) {
        if (containsAll(definition, keys)) {
            return type;
        }
        final XmlSeeAlso see = type.getAnnotation(XmlSeeAlso.class);
        if (see != null) {
            int count = 0;
            Map<?,?>[] subTypeDefinitions = null;
            final Class<?>[] subTypes = see.value(); // We will filter this array in the loop.
            for (final Class<?> subType : subTypes) {
                if (type.isAssignableFrom(subType) && subType != type) {
                    final Map<String,Class<?>> cd = getDefinition(subType);
                    if (containsAll(cd, keys)) {
                        return subType.asSubclass(type);
                    }
                    if (subTypeDefinitions == null) {
                        subTypeDefinitions = new Map<?,?>[subTypes.length];
                    }
                    subTypeDefinitions[count] = cd;
                    subTypes[count++] = subType;
                }
            }
            /*
             * Only after we verified all direct children, iterate recursively over other children.
             */
            for (int i=0; i<count; i++) {
                final Class<?> subType = guessType(subTypes[i], subTypeDefinitions[i], keys);
                if (subType != null) {
                    return subType.asSubclass(type);
                }
            }
        }
        return null;
    }

    /**
     * Creates a new metadata of the given type, initialized with the property values given in the properties map.
     *
     * @param  <T>        The parameterized type of the {@code type} argument.
     * @param  type       The interface or implementation type of the metadata object to be created.
     * @param  definition The value of {@code getDefinition(type)}.
     * @param  properties The property values to be given to the metadata object.
     * @param  position   The position to report in case of error.
     * @return A new metadata object of the given type, filled with the given values.
     * @throws ParseException If the metadata object can not be created.
     */
    final <T> T create(final Class<T> type, final Map<String,Class<?>> definition,
            final Map<String,?> properties, final int position) throws ParseException
    {
        Class<? extends T> impl = standard.getImplementation(type);
        if (impl == null) {
            if (standard.isMetadata(type)) {
                throw new ParseException(Errors.format(Errors.Keys.UnknownType_1, type), position);
            }
            impl = type; // Will try to instantiate the type directly.
        }
        impl = guessType(impl, definition, properties.keySet());
        final Object metadata;
        try {
            metadata = impl.newInstance();
        } catch (Exception e) {
            /*
             * We catch all Exceptions because Class.newInstance() propagates all of them,
             * including the checked ones (it bypasses the compile-time exception checking).
             */
            throw (ParseException) new ParseException(e.getLocalizedMessage(), position).initCause(e);
        }
        final Map<String,Object> asMap = standard.asValueMap(metadata,
                KeyNamePolicy.UML_IDENTIFIER, ValueExistencePolicy.NON_EMPTY);
        try {
            asMap.putAll(properties);
        } catch (RuntimeException e) {
            throw (ParseException) new ParseException(e.getLocalizedMessage(), position).initCause(e);
        }
        return type.cast(metadata);
    }
}
