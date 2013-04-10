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
package org.geotoolkit.internal.image.io;

import java.util.List;
import java.util.Locale;
import java.awt.image.DataBuffer;

import org.opengis.referencing.crs.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.IdentifiedObject;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.collection.UnmodifiableArrayList;


/**
 * Utilities methods related to data types.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.07
 * @module
 */
public final class DataTypes extends Static {
    /**
     * Enumeration of valid coordinate reference system types.
     */
    public static final List<String> CRS_TYPES = UnmodifiableArrayList.wrap(
        "geographic", "projected"
    );

    /**
     * The interfaces associated to the {@link #CRS_TYPES} enumeration.
     * Must be in the same order than the above-cited list of type name.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Class<? extends CoordinateReferenceSystem>[] CRS_INTERFACES = new Class[] {
        GeographicCRS.class, ProjectedCRS.class
    };

    /**
     * Enumeration of valid coordinate system types.
     */
    public static final List<String> CS_TYPES = UnmodifiableArrayList.wrap(
        "ellipsoidal", "cartesian"
    );

    /**
     * The interfaces associated to the {@link #CS_TYPES} enumeration.
     * Must be in the same order than the above-cited list of type name.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Class<? extends CoordinateSystem>[] CS_INTERFACES = new Class[] {
        EllipsoidalCS.class, CartesianCS.class
    };

    /**
     * Enumeration of valid datum types.
     */
    public static final List<String> DATUM_TYPES = UnmodifiableArrayList.wrap(
        "geodetic", "vertical", "temporal", "image", "engineering"
    );

    /**
     * The interfaces associated to the {@link #DATUM_TYPES} enumeration.
     * Must be in the same order than the above-cited list of type name.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Class<? extends Datum>[] DATUM_INTERFACES = new Class[] {
        GeodeticDatum.class, VerticalDatum.class, TemporalDatum.class, ImageDatum.class, EngineeringDatum.class
    };

    /**
     * Do not allow instantiation of this class.
     */
    private DataTypes() {
    }

    /**
     * Decodes the given name as a {@link DataBuffer} constant. If the name
     * is not recognized, then {@link DataBuffer#TYPE_UNDEFINED} is returned.
     *
     * @param name The name ({@code "BYTE"}, {@code "SHORT"}, {@code "FLOAT"}, <i>etc.</i>)
     * @return The corresponding {@link DataBuffer} constant.
     *
     * @todo Use switch on String.
     */
    public static int getDataBufferType(String name) {
        name = name.toUpperCase(Locale.US);
        if (name.equals("BYTE" ))  return DataBuffer.TYPE_BYTE;
        if (name.equals("SHORT"))  return DataBuffer.TYPE_SHORT;
        if (name.equals("USHORT")) return DataBuffer.TYPE_USHORT;
        if (name.equals("INT"))    return DataBuffer.TYPE_INT;
        if (name.equals("FLOAT"))  return DataBuffer.TYPE_FLOAT;
        if (name.equals("DOUBLE")) return DataBuffer.TYPE_DOUBLE;
        return DataBuffer.TYPE_UNDEFINED;
    }

    /**
     * Returns the interface for the given name of the type.
     *
     * @param  <T>      The compile-time class of {@code baseType}.
     * @param  baseType {@link CoordinateReferenceSystem}, {@link CoordinateSystem}, {@link Datum}
     *                  or a sub-interface of the above.
     * @param  name     The name of the type for which the interface is wanted.
     * @return The interface for the given name, or {@code null} if unknown.
     * @throws ClassCastException If the type inferred from the given {@code name} is not
     *         assignable to the given {@code baseType}.
     */
    public static <T extends IdentifiedObject> Class<? extends T> getInterface(
            final Class<T> baseType, final String name) throws ClassCastException
    {
        final Class<? extends IdentifiedObject>[] types;
        final List<String> names;
        if (CoordinateReferenceSystem.class.isAssignableFrom(baseType)) {
            types = CRS_INTERFACES;
            names = CRS_TYPES;
        } else if (CoordinateSystem.class.isAssignableFrom(baseType)) {
            types = CS_INTERFACES;
            names = CS_TYPES;
        } else if (Datum.class.isAssignableFrom(baseType)) {
            types = DATUM_INTERFACES;
            names = DATUM_TYPES;
        } else {
            throw new IllegalArgumentException(baseType.getName());
        }
        for (int i=0; i<types.length; i++) {
            if (name.equalsIgnoreCase(names.get(i))) {
                return types[i].asSubclass(baseType);
            }
        }
        return null;
    }

    /**
     * Returns the name of the type for the given identified object.
     *
     * @param object The object for which the name of the type is wanted.
     * @param types  One of the {@code FOO_INTERFACES} constant.
     * @param names  The {@code FOO_TYPES} constant associated with the above types.
     */
    private static <T extends IdentifiedObject> String getType(final T object,
            final Class<? extends T>[] types, final List<String> names)
    {
        for (int i=0; i<types.length; i++) {
            final Class<? extends IdentifiedObject> type = types[i];
            if (type.isInstance(object)) {
                return names.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the name of the type for the given CRS object, or {@code null} if none.
     *
     * @param  object The object for which the name of the type is wanted, or {@code null}.
     * @return The name of the type, or {@code null} if unknown.
     */
    public static String getType(final CoordinateReferenceSystem object) {
        return getType(object, CRS_INTERFACES, CRS_TYPES);
    }

    /**
     * Returns the name of the type for the given CS object, or {@code null} if none.
     *
     * @param  object The object for which the name of the type is wanted, or {@code null}.
     * @return The name of the type, or {@code null} if unknown.
     */
    public static String getType(final CoordinateSystem object) {
        return getType(object, CS_INTERFACES, CS_TYPES);
    }

    /**
     * Returns the name of the type for the given datum object, or {@code null} if none.
     *
     * @param  object The object for which the name of the type is wanted, or {@code null}.
     * @return The name of the type, or {@code null} if unknown.
     */
    public static String getType(final Datum object) {
        return getType(object, DATUM_INTERFACES, DATUM_TYPES);
    }
}
