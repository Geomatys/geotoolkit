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

import org.opengis.annotation.UML;


/**
 * The name of the keys included in a {@link java.util.Map} of metadata. Those maps are created
 * by the {@link AbstractMetadata#asMap()} method. The keys in those map are {@link String}s which
 * can be inferred from the {@linkplain UML#identifier() UML identifier}, the name of the Javabeans
 * property, or the {@linkplain java.lang.reflect.Method#getName() method name}.
 * <p>
 * In GeoAPI implementation of ISO 19115, {@code UML_IDENTIFIER} and {@code JAVA_PROPERTY}
 * are usually identical except for {@linkplain java.util.Collection collections}:
 * {@code JAVA_PROPERTY} names are plural when the property is a collection while
 * {@code UML_IDENTIFIER} usually stay singular no matter the property cardinality.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @see MetadataStandard#asMap(Object, NullValuePolicy, KeyNamePolicy)
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.metadata.KeyNamePolicy}.
 */
@Deprecated
public final class KeyNamePolicy {
    private KeyNamePolicy() {
    }

    /**
     * The keys in the map are the {@linkplain UML#identifier() UML identifier} of the metadata
     * properties. If a property has no UML annotation, then the Javabeans property name is used
     * as a fallback.
     */
    public static final org.apache.sis.metadata.KeyNamePolicy UML_IDENTIFIER =
            org.apache.sis.metadata.KeyNamePolicy.UML_IDENTIFIER;

    /**
     * The keys in the map are the Javabeans property names. This is the method name with
     * the {@code get} or {@code is} prefix removed, and the first letter made lower-case.
     * <p>
     * This is the default type of names returned by {@link AbstractMetadata#asMap()}.
     */
    public static final org.apache.sis.metadata.KeyNamePolicy JAVABEANS_PROPERTY =
            org.apache.sis.metadata.KeyNamePolicy.JAVABEANS_PROPERTY;

    /**
     * The keys in the map are the plain {@linkplain java.lang.reflect.Method#getName() method names}.
     */
    public static final org.apache.sis.metadata.KeyNamePolicy METHOD_NAME =
            org.apache.sis.metadata.KeyNamePolicy.METHOD_NAME;

    /**
     * The keys in the map are sentences inferred from the UML identifiers. This policy starts
     * with the same names than {@link #UML_IDENTIFIER}, searches for word boundaries (defined
     * as a lower case letter followed by a upper case letter) and inserts a space between the
     * words found. The first letter in the sentence is made upper-case. The first letters of
     * following words are made lower-case.
     *
     * @since 3.04
     */
    public static final org.apache.sis.metadata.KeyNamePolicy SENTENCE =
            org.apache.sis.metadata.KeyNamePolicy.SENTENCE;
}
