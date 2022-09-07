/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.swe.xml;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * An object factory allowing to create SWE object from different version.
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class SweXmlFactory {

    /**
     * Build a Coordinate in the factory version.
     *
     * @param version
     * @param name
     * @param quantity
     * @return
     */
    public static Coordinate createCoordinate(final String version, final String name, final Quantity quantity) {
        if ("1.0.0".equals(version)) {
            if (quantity != null && !(quantity instanceof org.geotoolkit.swe.xml.v100.QuantityType)) {
                throw new IllegalArgumentException("Unexpected SWE version for quantity object.");
            }
            return new org.geotoolkit.swe.xml.v100.CoordinateType(name,
                                                                  (org.geotoolkit.swe.xml.v100.QuantityType) quantity);
        } else if ("1.0.1".equals(version)) {
            if (quantity != null && !(quantity instanceof org.geotoolkit.swe.xml.v101.QuantityType)) {
                throw new IllegalArgumentException("Unexpected SWE version for quantity object.");
            }
            return new org.geotoolkit.swe.xml.v101.CoordinateType(name,
                                                                  (org.geotoolkit.swe.xml.v101.QuantityType) quantity);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static AbstractQualityProperty createQualityProperty(final String version, final AbstractDataComponent compo) {
        if ("1.0.0".equals(version)) {
            if (compo != null && !(compo instanceof org.geotoolkit.swe.xml.v100.AbstractDataComponentType)) {
                throw new IllegalArgumentException("Unexpected SWE version for component object.");
            }
            return new org.geotoolkit.swe.xml.v100.QualityPropertyType((org.geotoolkit.swe.xml.v100.AbstractDataComponentType) compo);
        } else if ("1.0.1".equals(version)) {
            if (compo != null && !(compo instanceof org.geotoolkit.swe.xml.v101.AbstractDataComponentType)) {
                throw new IllegalArgumentException("Unexpected SWE version for component object.");
            }
            return new org.geotoolkit.swe.xml.v101.QualityPropertyType((org.geotoolkit.swe.xml.v101.AbstractDataComponentType) compo);
        } else if ("2.0.0".equals(version)) {
            if (compo != null && !(compo instanceof org.geotoolkit.swe.xml.v200.AbstractDataComponentType)) {
                throw new IllegalArgumentException("Unexpected SWE version for component object.");
            }
            return new org.geotoolkit.swe.xml.v200.QualityPropertyType((org.geotoolkit.swe.xml.v200.AbstractDataComponentType) compo);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static Quantity createQuantity(final String version, final String definition, final UomProperty uom, final Double value) {
        return createQuantity(version, null, definition, uom, value, null);
    }

    /**
     * Build a Quantity object in the factory version.
     *
     * @param version
     * @param definition
     * @param uom
     * @param value
     * @return
     */
    public static Quantity createQuantity(final String version, final String id, final String definition, final UomProperty uom, final Double value, final List<AbstractQualityProperty> quality) {

        if ("1.0.0".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v100.UomPropertyType)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            final List<org.geotoolkit.swe.xml.v100.QualityPropertyType> qual100 = new ArrayList<>();
            if (quality != null) {
                for (AbstractQualityProperty q : quality) {
                    if (q != null && !(q instanceof org.geotoolkit.swe.xml.v100.QualityPropertyType)) {
                        throw new IllegalArgumentException("Unexpected SWE version for quality object.");
                    }
                    qual100.add((org.geotoolkit.swe.xml.v100.QualityPropertyType)q);
                }
            }
            return new org.geotoolkit.swe.xml.v100.QuantityType(id,
                                                                null,
                                                                definition,
                                                                (org.geotoolkit.swe.xml.v100.UomPropertyType)uom,
                                                                value,
                                                                qual100);
        } else if ("1.0.1".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v101.UomPropertyType)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            final List<org.geotoolkit.swe.xml.v101.QualityPropertyType> qual101 = new ArrayList<>();
            if (quality != null) {
                for (AbstractQualityProperty q : quality) {
                    if (q != null && !(q instanceof org.geotoolkit.swe.xml.v101.QualityPropertyType)) {
                        throw new IllegalArgumentException("Unexpected SWE version for quality object.");
                    }
                    qual101.add((org.geotoolkit.swe.xml.v101.QualityPropertyType)q);
                }
            }
            return new org.geotoolkit.swe.xml.v101.QuantityType(id,
                                                                null,
                                                                null,
                                                                definition,
                                                                (org.geotoolkit.swe.xml.v101.UomPropertyType)uom,
                                                                value,
                                                                qual101);
        } else if ("2.0.0".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v200.UnitReference)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            final List<org.geotoolkit.swe.xml.v200.QualityPropertyType> qual200 = new ArrayList<>();
            if (quality != null) {
                for (AbstractQualityProperty q : quality) {
                    if (q != null && !(q instanceof org.geotoolkit.swe.xml.v200.QualityPropertyType)) {
                        throw new IllegalArgumentException("Unexpected SWE version for quality object.");
                    }
                    qual200.add((org.geotoolkit.swe.xml.v200.QualityPropertyType)q);
                }
            }
            return new org.geotoolkit.swe.xml.v200.QuantityType(id,
                                                                null,
                                                                definition,
                                                                (org.geotoolkit.swe.xml.v200.UnitReference)uom,
                                                                value,
                                                                qual200);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    /**
     * Build a Quantity object in the factory version.
     *
     * @param version
     * @param axisID
     * @param definition
     * @param uom
     * @param value
     * @return
     */
    public static Quantity createQuantity(final String version, final String axisID, final String definition, final UomProperty uom, final Double value) {

        if ("1.0.0".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v100.UomPropertyType)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            return new org.geotoolkit.swe.xml.v100.QuantityType(axisID,
                                                               definition,
                                                               (org.geotoolkit.swe.xml.v100.UomPropertyType)uom,
                                                                value);

        } else if ("1.0.1".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v101.UomPropertyType)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            return new org.geotoolkit.swe.xml.v101.QuantityType(axisID,
                                                               definition,
                                                               (org.geotoolkit.swe.xml.v101.UomPropertyType)uom,
                                                               value);
        } else if ("2.0.0".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v200.UnitReference)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            return new org.geotoolkit.swe.xml.v200.QuantityType(axisID,
                                                               definition,
                                                               (org.geotoolkit.swe.xml.v200.UnitReference)uom,
                                                               value);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    /**
     * Build a Quantity object in the factory version.
     *
     * @param version
     * @param definition
     * @param value
     * @return
     */
    public static AbstractText createText(final String version, final String definition, final String value) {
        return createText(version, null, definition, value, null);
    }

    public static AbstractText createText(final String version, final String id, final String definition, final String value, final List<AbstractQualityProperty> quality) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v100.Text(id, definition, value);
        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.swe.xml.v101.Text(id, definition, value);
        } else if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.swe.xml.v200.QualityPropertyType> qual200 = new ArrayList<>();
            if (quality != null) {
                for (AbstractQualityProperty q : quality) {
                    if (q != null && !(q instanceof org.geotoolkit.swe.xml.v200.QualityPropertyType)) {
                        throw new IllegalArgumentException("Unexpected SWE version for quality object.");
                    }
                    qual200.add((org.geotoolkit.swe.xml.v200.QualityPropertyType)q);
                }
            }
            return new org.geotoolkit.swe.xml.v200.TextType(id, definition, value, qual200);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    /**
     * Build a Uom object in the factory version.
     *
     * @param version
     * @param code
     * @param href
     * @return
     */
    public static UomProperty createUomProperty(final String version, final String code, final String href) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v200.UnitReference(code, href);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v100.UomPropertyType(code, href);
        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.swe.xml.v101.UomPropertyType(code, href);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static AbstractBoolean createBoolean(final String version, final String definition, final Boolean value) {
        return createBoolean(version, null, definition, value, null);
    }

    public static AbstractBoolean createBoolean(final String version, final String id, final String definition, final Boolean value, final List<AbstractQualityProperty> quality) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v100.BooleanType(id, definition, value);
        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.swe.xml.v101.BooleanType(id, definition, value);
        } else if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.swe.xml.v200.QualityPropertyType> qual200 = new ArrayList<>();
            if (quality != null) {
                for (AbstractQualityProperty q : quality) {
                    if (q != null && !(q instanceof org.geotoolkit.swe.xml.v200.QualityPropertyType)) {
                        throw new IllegalArgumentException("Unexpected SWE version for quality object.");
                    }
                    qual200.add((org.geotoolkit.swe.xml.v200.QualityPropertyType)q);
                }
            }
            return new org.geotoolkit.swe.xml.v200.BooleanType(id, value, definition, qual200);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static AbstractTime createTime(final String version, final String definition, final UomProperty uom) {
        return createTime(version, null, definition, uom, null);
    }

    public static AbstractTime createTime(final String version, final String id, final String definition, final UomProperty uom, final List<AbstractQualityProperty> quality) {
        if ("1.0.0".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v100.UomPropertyType)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            return new org.geotoolkit.swe.xml.v100.TimeType(id, definition, (org.geotoolkit.swe.xml.v100.UomPropertyType)uom);
        } else if ("1.0.1".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v101.UomPropertyType)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            return new org.geotoolkit.swe.xml.v101.TimeType(id, definition, (org.geotoolkit.swe.xml.v101.UomPropertyType)uom);
        } else if ("2.0.0".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v200.UnitReference)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            final List<org.geotoolkit.swe.xml.v200.QualityPropertyType> qual200 = new ArrayList<>();
            if (quality != null) {
                for (AbstractQualityProperty q : quality) {
                    if (q != null && !(q instanceof org.geotoolkit.swe.xml.v200.QualityPropertyType)) {
                        throw new IllegalArgumentException("Unexpected SWE version for quality object.");
                    }
                    qual200.add((org.geotoolkit.swe.xml.v200.QualityPropertyType)q);
                }
            }
            return new org.geotoolkit.swe.xml.v200.TimeType(id, definition, (org.geotoolkit.swe.xml.v200.UnitReference)uom, qual200);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static Position createPosition(final String version, final URI referenceFrame, final URI localFrame, final Vector location, final Vector orientation) {

        if ("1.0.0".equals(version)) {
            if (!(location instanceof org.geotoolkit.swe.xml.v100.VectorType) || !(orientation instanceof org.geotoolkit.swe.xml.v100.VectorType)) {
                throw new IllegalArgumentException("Unexpected SWE version for location or orientation object.");
            }
            return new org.geotoolkit.swe.xml.v100.PositionType(referenceFrame,
                                                               localFrame,
                                                               (org.geotoolkit.swe.xml.v100.VectorType)location,
                                                               (org.geotoolkit.swe.xml.v100.VectorType)orientation);

        } else if ("1.0.1".equals(version)) {
            if (!(location instanceof org.geotoolkit.swe.xml.v101.VectorType) || !(orientation instanceof org.geotoolkit.swe.xml.v101.VectorType)) {
                throw new IllegalArgumentException("Unexpected SWE version for location or orientation object.");
            }
            return new org.geotoolkit.swe.xml.v101.PositionType(referenceFrame,
                                                               localFrame,
                                                               (org.geotoolkit.swe.xml.v101.VectorType)location,
                                                               (org.geotoolkit.swe.xml.v101.VectorType)orientation);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static Position createPosition(final String version, final URI referenceFrame, final URI localFrame, final Vector location) {

        if ("1.0.0".equals(version)) {
            if (!(location instanceof org.geotoolkit.swe.xml.v100.VectorType)) {
                throw new IllegalArgumentException("Unexpected SWE version for location or orientation object.");
            }
            return new org.geotoolkit.swe.xml.v100.PositionType(referenceFrame,
                                                               localFrame,
                                                               (org.geotoolkit.swe.xml.v100.VectorType)location);

        } else if ("1.0.1".equals(version)) {
            if (!(location instanceof org.geotoolkit.swe.xml.v101.VectorType)) {
                throw new IllegalArgumentException("Unexpected SWE version for location or orientation object.");
            }
            return new org.geotoolkit.swe.xml.v101.PositionType(referenceFrame,
                                                               localFrame,
                                                               (org.geotoolkit.swe.xml.v101.VectorType)location);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }


    /**
     * Build a Vector in the factory version.
     *
     * @param version
     * @param definition
     * @param coordinates
     */
    public static Vector createVector(final String version, final String definition, final List<? extends Coordinate> coordinates) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v100.VectorType(definition,
                                                             (List<org.geotoolkit.swe.xml.v100.CoordinateType>)coordinates);
        } else if ("1.0.1".equals(version)) {
           return new org.geotoolkit.swe.xml.v101.VectorType(definition,
                                                             (List<org.geotoolkit.swe.xml.v101.CoordinateType>)coordinates);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static TextBlock createTextBlock(final String version, final String id, final String tokenSeparator, final String blockSeparator, final String decimalSeparator) {

        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v100.TextBlock(id, tokenSeparator, blockSeparator, decimalSeparator);

        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.swe.xml.v101.TextBlockType(id, tokenSeparator, blockSeparator, decimalSeparator);

        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v200.TextEncodingType(id, decimalSeparator, tokenSeparator, blockSeparator);

        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static AnyScalar buildAnyScalar(final String version, final String id, final String name, final AbstractDataComponent compo) {
        if ("1.0.0".equals(version)) {
            if (!(compo instanceof org.geotoolkit.swe.xml.v100.AbstractDataComponentType)) {
                throw new IllegalArgumentException("Unexpected SWE version for component object.");
            }
            return new org.geotoolkit.swe.xml.v100.AnyScalarPropertyType(name,
                                                                        (org.geotoolkit.swe.xml.v100.AbstractDataComponentType)compo);

        } else if ("1.0.1".equals(version)) {
            if (!(compo instanceof org.geotoolkit.swe.xml.v101.AbstractDataComponentType)) {
                throw new IllegalArgumentException("Unexpected SWE version for component object.");
            }
            return new org.geotoolkit.swe.xml.v101.AnyScalarPropertyType(id,
                                                                         name,
                                                                         (org.geotoolkit.swe.xml.v101.AbstractDataComponentType)compo);
        } else if ("2.0.0".equals(version)) {
            if (!(compo instanceof org.geotoolkit.swe.xml.v200.AbstractDataComponentType)) {
                throw new IllegalArgumentException("Unexpected SWE version for component object.");
            }
            return new org.geotoolkit.swe.xml.v200.Field(name,
                                                         (org.geotoolkit.swe.xml.v200.AbstractDataComponentType)compo);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static AbstractDataRecord buildSimpleDataRecord(final String version, final String blockid, final String id, final String definition, final Boolean fixed, final List<AnyScalar> components) {
        if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.swe.xml.v100.AnyScalarPropertyType> compos = new ArrayList<>();
            for (AnyScalar scalar : components) {
                if (scalar != null && !(scalar instanceof org.geotoolkit.swe.xml.v100.AnyScalarPropertyType)) {
                    throw new IllegalArgumentException("Unexpected SWE version for component object.");
                }
                compos.add((org.geotoolkit.swe.xml.v100.AnyScalarPropertyType)scalar);
            }
            return new org.geotoolkit.swe.xml.v100.SimpleDataRecordType(blockid,
                                                                        id,
                                                                        definition,
                                                                        fixed,
                                                                        compos);

        } else if ("1.0.1".equals(version)) {
            final List<org.geotoolkit.swe.xml.v101.AnyScalarPropertyType> compos = new ArrayList<>();
            for (AnyScalar scalar : components) {
                if (scalar != null && !(scalar instanceof org.geotoolkit.swe.xml.v101.AnyScalarPropertyType)) {
                    throw new IllegalArgumentException("Unexpected SWE version for component object.");
                }
                compos.add((org.geotoolkit.swe.xml.v101.AnyScalarPropertyType)scalar);
            }
            return new org.geotoolkit.swe.xml.v101.SimpleDataRecordType(blockid,
                                                                        id,
                                                                        definition,
                                                                        fixed,
                                                                        compos);
        } else if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.swe.xml.v200.Field> compos = new ArrayList<>();
            for (AnyScalar scalar : components) {
                if (scalar != null && !(scalar instanceof org.geotoolkit.swe.xml.v200.Field)) {
                    throw new IllegalArgumentException("Unexpected SWE version for component object.");
                }
                compos.add((org.geotoolkit.swe.xml.v200.Field)scalar);
            }
            return new org.geotoolkit.swe.xml.v200.DataRecordType(id,
                                                                  definition,
                                                                  fixed,
                                                                  compos);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public static DataArray buildDataArray(final String version, final String id, final int count, final String elementName, final AbstractDataRecord elementType,
            final AbstractEncoding encoding, final String values, final List<Object> dataValues) {
        if ("1.0.0".equals(version)) {
            if (!(elementType instanceof org.geotoolkit.swe.xml.v100.AbstractDataRecordType)) {
                throw new IllegalArgumentException("Unexpected SWE version for elementType object.");
            }
            if (!(encoding instanceof org.geotoolkit.swe.xml.v100.AbstractEncodingType)) {
                throw new IllegalArgumentException("Unexpected SWE version for encoding object.");
            }
            return new org.geotoolkit.swe.xml.v100.DataArrayType(id,
                                                                 count,
                                                                 elementName,
                                                                 (org.geotoolkit.swe.xml.v100.AbstractDataRecordType)elementType,
                                                                 (org.geotoolkit.swe.xml.v100.AbstractEncodingType)encoding,
                                                                 values,
                                                                 dataValues);

        } else if ("1.0.1".equals(version)) {
            if (!(elementType instanceof org.geotoolkit.swe.xml.v101.AbstractDataRecordType)) {
                throw new IllegalArgumentException("Unexpected SWE version for elementType object.");
            }
            if (!(encoding instanceof org.geotoolkit.swe.xml.v101.AbstractEncodingType)) {
                throw new IllegalArgumentException("Unexpected SWE version for encoding object.");
            }
            return new org.geotoolkit.swe.xml.v101.DataArrayType(id,
                                                                 count,
                                                                 elementName,
                                                                 (org.geotoolkit.swe.xml.v101.AbstractDataRecordType)elementType,
                                                                 (org.geotoolkit.swe.xml.v101.AbstractEncodingType)encoding,
                                                                 values,
                                                                 dataValues);
        } else if ("2.0.0".equals(version)) {
            if (!(elementType instanceof org.geotoolkit.swe.xml.v200.DataRecordType)) {
                throw new IllegalArgumentException("Unexpected SWE version for elementType object.");
            }
            if (!(encoding instanceof org.geotoolkit.swe.xml.v200.AbstractEncodingType)) {
                throw new IllegalArgumentException("Unexpected SWE version for encoding object.");
            }
            return new org.geotoolkit.swe.xml.v200.DataArrayType(id,
                                                                 count,
                                                                 (org.geotoolkit.swe.xml.v200.AbstractEncodingType)encoding,
                                                                 values,
                                                                 elementName,
                                                                 (org.geotoolkit.swe.xml.v200.DataRecordType)elementType,
                                                                 dataValues);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }
}
