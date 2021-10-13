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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.opengis.referencing.cs.AxisDirection;


/**
 * Definition of a coordinate system axis.
 *
 * <p>Java class for CoordinateSystemAxisType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CoordinateSystemAxisType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}CoordinateSystemAxisBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}axisID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}remarks" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}axisAbbrev"/>
 *         &lt;element ref="{http://www.opengis.net/gml}axisDirection"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml}uom use="required""/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinateSystemAxisType", propOrder = {
    "axisID",
    "axisAbbrev",
    "axisDirection"
})
public class CoordinateSystemAxisType extends CoordinateSystemAxisBaseType {

    private List<IdentifierType> axisID;
    @XmlElement(required = true)
    private CodeType axisAbbrev;
    @XmlElement(required = true)
    private CodeType axisDirection;
    @XmlAttribute(namespace = "http://www.opengis.net/gml", required = true)
    @XmlSchemaType(name = "anyURI")
    private String uom;

    /**
     * Set of alternative identifications of this coordinate system axis.
     * The first axisID, if any, is normally the primary identification code,
     * and any others are aliases.
     * Gets the value of the axisID property.
     *
     * @return An unmodifiable list of the axis identifier.
     */
    public List<IdentifierType> getAxisID() {
        if (axisID == null) {
            axisID = new ArrayList<IdentifierType>();
        }
        return Collections.unmodifiableList(axisID);
    }

    /**
     * Gets the value of the axisAbbrev property.
     */
    public CodeType getAxisAbbrev() {
        return axisAbbrev;
    }

    public void setAxisAbbrev(CodeType axisAbbrev) {
        this.axisAbbrev = axisAbbrev;
    }

    public void setAxisAbbrev(String axisAbbrev) {
        this.axisAbbrev = new CodeType(axisAbbrev);
    }

    /**
     * Gets the value of the axisDirection property.
     *
     */
    public CodeType getAxisDirection() {
        return axisDirection;
    }

    public void setAxisDirection(CodeType axisDirection) {
        this.axisDirection = axisAbbrev;
    }

    public void setAxisDirection(String axisDirection) {
        this.axisDirection = new CodeType(axisDirection);
    }

    public void setAxisDirection(AxisDirection axisDirection) {
        this.axisDirection = new CodeType(axisDirection.identifier());
    }

    /**
     * Gets the value of the uom property.
     *
     */
    public String getUom() {
        return uom;
    }

    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof CoordinateSystemAxisType) {
            final CoordinateSystemAxisType that = (CoordinateSystemAxisType) object;
            return Objects.equals(this.axisAbbrev, that.axisAbbrev) &&
                   Objects.equals(this.axisDirection, that.axisDirection) &&
                   Objects.equals(this.axisID, that.axisID) &&
                   Objects.equals(this.uom, that.uom);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.axisID != null ? this.axisID.hashCode() : 0);
        hash = 97 * hash + (this.axisAbbrev != null ? this.axisAbbrev.hashCode() : 0);
        hash = 97 * hash + (this.axisDirection != null ? this.axisDirection.hashCode() : 0);
        hash = 97 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[CoordinateSystemAxisType]").append("\n");
        if (axisAbbrev != null) {
            sb.append("axisAbbrev: ").append(axisAbbrev).append('\n');
        }
        if (axisDirection != null) {
            sb.append("axisDirection: ").append(axisDirection).append('\n');
        }
        if (axisID != null) {
            sb.append("axisID: ").append(axisID).append('\n');
        }
        if (uom != null) {
            sb.append("uom: ").append(uom).append('\n');
        }
        return sb.toString();
     }
 }
