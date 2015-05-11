/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.web;

import javax.measure.unit.Unit;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;


/**
 * An "object type" in a URN.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.07
 *
 * @since 2.4
 * @module
 */
final class URN_Type {
    /**
     * List of object types. An object type is for example {@code "crs"} in
     * <code>"urn:ogc:def:<b>crs</b>:EPSG:6.8"</code>.
     */
    private static final URN_Type[] TYPES = {
        new URN_Type("crs",                 CoordinateReferenceSystem.class, CRSAuthorityFactory.class),
        new URN_Type("datum",               Datum.class,                     DatumAuthorityFactory.class),
        new URN_Type("meridian",            PrimeMeridian.class,             DatumAuthorityFactory.class),
        new URN_Type("ellipsoid",           Ellipsoid.class,                 DatumAuthorityFactory.class),
        new URN_Type("cs",                  CoordinateSystem.class,          CSAuthorityFactory.class),
        new URN_Type("axis",                CoordinateSystemAxis.class,      CSAuthorityFactory.class),
        new URN_Type("coordinateOperation", CoordinateOperation.class,       CoordinateOperationAuthorityFactory.class),
        new URN_Type("method",              OperationMethod.class,           CoordinateOperationAuthorityFactory.class),
        new URN_Type("parameter",           ParameterValue.class,            CoordinateOperationAuthorityFactory.class),
        new URN_Type("group",               ParameterValueGroup.class,       CoordinateOperationAuthorityFactory.class),
//      new URN_Type("derivedCRSType",      ...),
        new URN_Type("verticalDatumType",   VerticalDatumType.class,         VerticalDatumType.class),
        new URN_Type("pixelInCell",         PixelInCell.class,               PixelInCell.class),
        new URN_Type("rangeMeaning",        RangeMeaning.class,              RangeMeaning.class),
        new URN_Type("axisDirection",       AxisDirection.class,             AxisDirection.class),
        new URN_Type("uom",                 Unit.class,                      CSAuthorityFactory.class)
    };

    /**
     * Subset of {@link #TYPES} for the main ones.
     */
    static final URN_Type[] MAIN = {
        TYPES[0], TYPES[1], TYPES[4], TYPES[6]
    };

    /**
     * The object type name.
     */
    public final String name;

    /**
     * The object type, either as an {@link IdentifiedObject} subinterface or a {@link CodeList}.
     */
    public final Class<?> objectType;

    /**
     * The factory for this type, either as an {@link AuthorityFactory} subinterface
     * or a {@link CodeList}.
     */
    public final Class<?> factoryType;

    /**
     * Creates a new instance of {@code URN_Type}.
     */
    private URN_Type(final String name, final Class<?> objectType, final Class<?> factoryType) {
        this.name        = name;
        this.objectType  = objectType;
        this.factoryType = factoryType;
    }

    /**
     * Returns an instance of the specified name (case-insensitive), or {@code null} if none.
     */
    public static URN_Type getInstance(final String name) {
        for (int i=0; i<TYPES.length; i++) {
            final URN_Type candidate = TYPES[i];
            if (name.equalsIgnoreCase(candidate.name)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Returns an instance of the specified type, or {@code null} if none.
     *
     * @since 3.07
     */
    public static URN_Type getInstance(final Class<?> type) {
        for (int i=0; i<TYPES.length; i++) {
            final URN_Type candidate = TYPES[i];
            if (candidate.objectType.isAssignableFrom(type)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the specified factory is an instance of this type.
     */
    public boolean isInstance(final AuthorityFactory factory) {
        return factoryType.isInstance(factory);
    }

    /**
     * Returns the type name, for formatting and debugging purpose.
     */
    @Override
    public String toString() {
        return name;
    }
}
