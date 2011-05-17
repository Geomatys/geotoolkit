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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * A contextually local coordinate reference system; which can be divided into two broad categories:
 * - earth-fixed systems applied to engineering activities on or near the surface of the earth;
 * - CRSs on moving platforms such as road vehicles, vessels, aircraft, or spacecraft.
 * For further information, see OGC Abstract Specification Topic 2. 
 * 
 * <p>Java class for EngineeringCRSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EngineeringCRSType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractReferenceSystemType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}usesCS"/>
 *         &lt;element ref="{http://www.opengis.net/gml}usesEngineeringDatum"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EngineeringCRSType", propOrder = {
    "usesCS",
    "usesEngineeringDatum"
})
public class EngineeringCRSType extends AbstractReferenceSystemType {

    @XmlElement(required = true)
    private CoordinateSystemRefType usesCS;
    @XmlElement(required = true)
    private EngineeringDatumRefType usesEngineeringDatum;

    public EngineeringCRSType() {

    }

    public EngineeringCRSType(final String id, final String srsName, final CoordinateSystemRefType usesCS, final EngineeringDatumRefType usesEngineeringDatum) {
        super(id, null, null, srsName);
        this.usesCS = usesCS;
        this.usesEngineeringDatum = usesEngineeringDatum;
    }

    /**
     * Gets the value of the usesCS property.
     * 
     */
    public CoordinateSystemRefType getUsesCS() {
        return usesCS;
    }

    /**
     * Sets the value of the usesCS property.
     * 
     */
    public void setUsesCS(final CoordinateSystemRefType value) {
        this.usesCS = value;
    }

    /**
     * Gets the value of the usesEngineeringDatum property.
     * 
    */
    public EngineeringDatumRefType getUsesEngineeringDatum() {
        return usesEngineeringDatum;
    }

    /**
     * Sets the value of the usesEngineeringDatum property.
     * 
     */
    public void setUsesEngineeringDatum(final EngineeringDatumRefType value) {
        this.usesEngineeringDatum = value;
    }

    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof EngineeringCRSType) {
            final EngineeringCRSType that = (EngineeringCRSType) object;
            return Utilities.equals(this.usesCS, that.usesCS) &&
                   Utilities.equals(this.usesEngineeringDatum, that.usesEngineeringDatum);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.usesCS != null ? this.usesCS.hashCode() : 0);
        hash = 43 * hash + (this.usesEngineeringDatum != null ? this.usesEngineeringDatum.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[EngineeringCRSType]").append('\n');
        if (usesCS != null) {
            sb.append("usesCS: ").append(usesCS).append('\n');
        }
        if (usesEngineeringDatum != null) {
            sb.append("usesEngineeringDatum: ").append(usesEngineeringDatum).append('\n');
        }
        return sb.toString();
     }

}
