/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
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
package org.geotoolkit.gml.xml.v311;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.util.Utilities;


/**
 * A datum specifies the relationship of a coordinate system to the earth, thus creating a coordinate reference system. A datum uses a parameter or set of parameters that determine the location of the origin of the coordinate reference system. Each datum subtype can be associated with only specific types of coordinate systems. This abstract complexType shall not be used, extended, or restricted, in an Application Schema, to define a concrete subtype with a meaning equivalent to a concrete subtype specified in this document. 
 * 
 * <p>Java class for AbstractDatumType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDatumType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractDatumBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}datumID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}remarks" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}anchorPoint" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}realizationEpoch" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}validArea" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}scope" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDatumType", propOrder = {
    "datumID",
    "anchorPoint",
    "realizationEpoch",
    "validArea",
    "scope"
})
/*@XmlSeeAlso({
    VerticalDatumType.class,
    EngineeringDatumType.class,
    ImageDatumType.class,
    GeodeticDatumType.class,
    TemporalDatumBaseType.class
})*/
public abstract class AbstractDatumType extends AbstractDatumBaseType {

    private List<IdentifierType> datumID;
    private CodeType anchorPoint;
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar realizationEpoch;
    private ExtentType validArea;
    private String scope;

    public AbstractDatumType() {

    }

    public AbstractDatumType(String id, String datumName, CodeType anchorPoint) {
        super(id, datumName);
        this.anchorPoint = anchorPoint;
    }

    /**
     * Set of alternative identifications of this datum.
     * The first datumID, if any, is normally the primary identification code, 
     * and any others are aliases. Gets the value of the datumID property.
     */
    public List<IdentifierType> getDatumID() {
        if (datumID == null) {
            datumID = new ArrayList<IdentifierType>();
        }
        return this.datumID;
    }

    /**
     * Gets the value of the anchorPoint property.
     */
    public CodeType getAnchorPoint() {
        return anchorPoint;
    }

    /**
     * Gets the value of the realizationEpoch property.
     */
    public XMLGregorianCalendar getRealizationEpoch() {
        return realizationEpoch;
    }

    /**
     * Gets the value of the validArea property.
     */
    public ExtentType getValidArea() {
        return validArea;
    }

    /**
     * Gets the value of the scope property.
     */
    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (datumID != null) {
            sb.append("srsID: ").append('\n');
            for (IdentifierType s: datumID) {
                sb.append(s).append('\n');
            }
        }
        if (scope != null) {
            sb.append("scope: ").append(scope).append('\n');
        }
        if (validArea != null) {
            sb.append("valid area: ").append(validArea).append('\n');
        }
        if (anchorPoint != null) {
            sb.append("anchorPoint: ").append(anchorPoint).append('\n');
        }
        if (realizationEpoch != null) {
            sb.append("realizationEpoch: ").append(realizationEpoch).append('\n');
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

        if (object instanceof AbstractDatumType && super.equals(object)) {
            final AbstractDatumType that = (AbstractDatumType) object;

            return Utilities.equals(this.scope,     that.scope)     &&
                   Utilities.equals(this.datumID,   that.datumID)   &&
                   Utilities.equals(this.validArea, that.validArea) &&
                   Utilities.equals(this.realizationEpoch, that.realizationEpoch) &&
                   Utilities.equals(this.anchorPoint, that.anchorPoint);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.datumID != null ? this.datumID.hashCode() : 0);
        hash = 79 * hash + (this.anchorPoint != null ? this.anchorPoint.hashCode() : 0);
        hash = 79 * hash + (this.realizationEpoch != null ? this.realizationEpoch.hashCode() : 0);
        hash = 79 * hash + (this.validArea != null ? this.validArea.hashCode() : 0);
        hash = 79 * hash + (this.scope != null ? this.scope.hashCode() : 0);
        return hash;
    }
}
