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
package org.geotoolkit.gml.xml.v311modified;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * A 1D coordinate reference system used for the recording of time. 
 * 
 * <p>Java class for TemporalCRSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TemporalCRSType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractReferenceSystemType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}usesTemporalCS"/>
 *         &lt;element ref="{http://www.opengis.net/gml}usesTemporalDatum"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalCRSType", propOrder = {
    "usesTemporalCS",
    "usesTemporalDatum"
})
public class TemporalCRSType extends AbstractReferenceSystemType {

    @XmlElement(required = true)
    private TemporalCSRefType usesTemporalCS;
    @XmlElement(required = true)
    private TemporalDatumRefType usesTemporalDatum;

    public TemporalCRSType() {

    }

    public TemporalCRSType(final String id, final String name, final String description, String srsName,
            TemporalCSRefType usesTemporalCS, TemporalDatumRefType usesTemporalDatum) {
        super(id, name, description, srsName);
        this.usesTemporalCS    = usesTemporalCS;
        this.usesTemporalDatum = usesTemporalDatum;
    }

    /**
     * Gets the value of the usesTemporalCS property.
     */
    public TemporalCSRefType getUsesTemporalCS() {
        return usesTemporalCS;
    }

    /**
     * Sets the value of the usesTemporalCS property.
     */
    public void setUsesTemporalCS(TemporalCSRefType value) {
        this.usesTemporalCS = value;
    }

    /**
     * Gets the value of the usesTemporalDatum property.
     */
    public TemporalDatumRefType getUsesTemporalDatum() {
        return usesTemporalDatum;
    }

    /**
     * Sets the value of the usesTemporalDatum property.
     * 
     */
    public void setUsesTemporalDatum(TemporalDatumRefType value) {
        this.usesTemporalDatum = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (usesTemporalCS != null) {
            sb.append("usesTemporalCS: ").append(usesTemporalCS).append('\n');
        }
        if (usesTemporalDatum != null) {
            sb.append("usesTemporalDatum: ").append(usesTemporalDatum).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof TemporalCRSType && super.equals(object)) {
            final TemporalCRSType that = (TemporalCRSType) object;

            return Utilities.equals(this.usesTemporalCS,    that.usesTemporalCS) &&
                   Utilities.equals(this.usesTemporalDatum, that.usesTemporalDatum);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.usesTemporalCS != null ? this.usesTemporalCS.hashCode() : 0);
        hash = 29 * hash + (this.usesTemporalDatum != null ? this.usesTemporalDatum.hashCode() : 0);
        return hash;
    }

}
