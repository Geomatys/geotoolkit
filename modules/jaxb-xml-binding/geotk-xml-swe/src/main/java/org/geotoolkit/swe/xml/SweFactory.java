/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.swe.xml;

import java.util.List;


/**
 * An object factory allowing to create SWE object from different version.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SweFactory {

    /**
     * The current SWE version of the factory.
     */
    private String version;

    /**
     * build a new factory to build SWE object from the specified version.
     *
     * @param version The SWE version.
     */
    public SweFactory(String version) {
        this.version = version;
    }

    /**
     * Build a Coordinate in the factory version.
     *
     * @param name
     * @param quantity
     * @return
     */
    public Coordinate createCoordinate(String name, Quantity quantity) {
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

    /**
     * Build a Quantity object in the factory version.
     *
     * @param definition
     * @param uom
     * @param value
     * @return
     */
    public Quantity createQuantity(String definition, UomProperty uom, Double value) {

        if ("1.0.0".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v100.UomPropertyType)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            return new org.geotoolkit.swe.xml.v100.QuantityType(definition,
                                                                (org.geotoolkit.swe.xml.v100.UomPropertyType)uom,
                                                                value);

        } else if ("1.0.1".equals(version)) {
            if (uom != null && !(uom instanceof org.geotoolkit.swe.xml.v101.UomPropertyType)) {
                throw new IllegalArgumentException("Unexpected SWE version for uomProperty object.");
            }
            return new org.geotoolkit.swe.xml.v101.QuantityType(definition,
                                                                (org.geotoolkit.swe.xml.v101.UomPropertyType)uom,
                                                                value);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    /**
     * Build a Quantity object in the factory version.
     *
     * @param definition
     * @param uom
     * @param value
     * @return
     */
    public Quantity createQuantity(String axisID, String definition, UomProperty uom, Double value) {

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
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    /**
     * Build a Uom object in the factory version.
     * 
     * @param code
     * @param href
     * @return
     */
    public UomProperty createUomProperty(String code, String href) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v100.UomPropertyType(code, href);
        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.swe.xml.v101.UomPropertyType(code, href);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public Position createPosition(String referenceFrame, String localFrame, Vector location, Vector orientation) {

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

    public Position createPosition(String referenceFrame, String localFrame, Vector location) {

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
     */
    public Vector createVector(String definition, List<? extends Coordinate> coordinates) {
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

    public AnyScalar createAnyScalar(String name, Quantity quantity) {

        if ("1.0.0".equals(version)) {
            if (!(quantity instanceof org.geotoolkit.swe.xml.v100.QuantityType)) {
                throw new IllegalArgumentException("Unexpected SWE version for location or orientation object.");
            }
            return new org.geotoolkit.swe.xml.v100.AnyScalarPropertyType(name,
                                                                        (org.geotoolkit.swe.xml.v100.QuantityType)quantity);

        } else if ("1.0.1".equals(version)) {
            if (!(quantity instanceof org.geotoolkit.swe.xml.v101.QuantityType)) {
                throw new IllegalArgumentException("Unexpected SWE version for location or orientation object.");
            }
            return new org.geotoolkit.swe.xml.v101.AnyScalarPropertyType(name,
                                                                       (org.geotoolkit.swe.xml.v101.QuantityType)quantity);
        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }

    public SimpleDataRecord createSimpleDataRecord(List<? extends AnyScalar> fields) {

        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v100.SimpleDataRecordType((List<org.geotoolkit.swe.xml.v100.AnyScalarPropertyType>)fields);

        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.swe.xml.v101.SimpleDataRecordEntry((List<org.geotoolkit.swe.xml.v101.AnyScalarPropertyType>)fields);

        } else {
            throw new IllegalArgumentException("Unexpected SWE version:" + version);
        }
    }
}
