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


/**
 * Whatever {@link MetadataStandard#asTypeMap MetadataStandard.asTypeMap(...)} should return values
 * for the property types, the element types (same as property types except for collections) or the
 * declaring classes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @see MetadataStandard#asTypeMap(Class, TypeValuePolicy, KeyNamePolicy)
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.metadata.TypeValuePolicy}.
 */
@Deprecated
public final class TypeValuePolicy {
    private TypeValuePolicy() {
    }

    /**
     * The type of a property, as inferred from the
     * {@linkplain java.lang.reflect.Method#getReturnType() return type} of the property method.
     * Collections are not handled in any special way; if the return type is a collection, then
     * the value is {@code Collection.class} (or a subclass).
     */
    public static final org.apache.sis.metadata.TypeValuePolicy PROPERTY_TYPE =
            org.apache.sis.metadata.TypeValuePolicy.PROPERTY_TYPE;

    /**
     * The type of a property, or type of elements if the property is a collection. This is the
     * same than {@link #PROPERTY_TYPE} except that collections are handled in a special way: if
     * the property is a collection, then the value is the type of <em>elements</em> in that
     * collection.
     *
     * {@note Current implementation has an additional slight difference: if the getter method
     *        in the implementation class declares a more specific return value than the getter
     *        method in the interface, and if the setter method (if any) expects the same specialized
     *        type, then <code>ELEMENT_TYPE</code> will use that specialized type. This is different
     *        than <code>PROPERTY_TYPE</code> which always use the type declared in the interface.}
     */
    public static final org.apache.sis.metadata.TypeValuePolicy ELEMENT_TYPE =
            org.apache.sis.metadata.TypeValuePolicy.ELEMENT_TYPE;

    /**
     * The type of the class that declares the method. A metadata implementation may have
     * different declaring classes for its properties if some of them are declared in parent
     * classes.
     */
    public static final org.apache.sis.metadata.TypeValuePolicy DECLARING_CLASS =
            org.apache.sis.metadata.TypeValuePolicy.DECLARING_CLASS;

    /**
     * The type of the interface that declares the method. This is the same than
     * {@link #DECLARING_CLASS}, except that the interface from the metadata standard
     * is returned instead than the implementation class.
     */
    public static final org.apache.sis.metadata.TypeValuePolicy DECLARING_INTERFACE =
            org.apache.sis.metadata.TypeValuePolicy.DECLARING_INTERFACE;
}
