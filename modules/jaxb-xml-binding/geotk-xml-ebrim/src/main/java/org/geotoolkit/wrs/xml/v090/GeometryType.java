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
package org.geotoolkit.wrs.xml.v090;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Extends WRSExtrinsicObjectType to include the basic properties of a geometry 
 * instance as defined in the OGC simple features model (99-049).
 * The repositoryItem property references an encoding of the geometry instance.
 * 
 *  dimension    - inherent dimension of the geometry instance
 *  geometryType - any concrete GML 3 geometry type
 *  srid         - id of the spatial reference system for this instance
 *       
 * 
 * <p>Java class for GeometryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeometryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/wrs}WRSExtrinsicObjectType">
 *       &lt;sequence>
 *         &lt;element name="dimension" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="geometryType" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="srid" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
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
@XmlType(name = "GeometryType", propOrder = {
    "dimension",
    "geometryType",
    "srid"
})
@XmlRootElement(name = "Geometry")
public class GeometryType extends WRSExtrinsicObjectType {

    @XmlSchemaType(name = "positiveInteger")
    private Integer dimension;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String geometryType;
    @XmlSchemaType(name = "anyURI")
    private String srid;

    /**
     * Gets the value of the dimension property.
     */
    public Integer getDimension() {
        return dimension;
    }

    /**
     * Sets the value of the dimension property.
     */
    public void setDimension(final Integer value) {
        this.dimension = value;
    }

    /**
     * Gets the value of the geometryType property.
     */
    public String getGeometryType() {
        return geometryType;
    }

    /**
     * Sets the value of the geometryType property.
     */
    public void setGeometryType(final String value) {
        this.geometryType = value;
    }

    /**
     * Gets the value of the srid property.
     */
    public String getSrid() {
        return srid;
    }

    /**
     * Sets the value of the srid property.
     */
    public void setSrid(final String value) {
        this.srid = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (dimension != null) {
            sb.append("dimension:").append(dimension).append('\n');
        }
        if (geometryType != null) {
            sb.append("geometryType:").append(geometryType).append('\n');
        }
        if (srid != null) {
            sb.append("srid:").append(srid).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof GeometryType && super.equals(obj)) {
            final GeometryType that = (GeometryType) obj;
            return Utilities.equals(this.dimension,    that.dimension) &&
                   Utilities.equals(this.geometryType, that.geometryType) &&
                   Utilities.equals(this.srid,         that.srid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + super.hashCode();
        hash = 37 * hash + (this.dimension != null ? this.dimension.hashCode() : 0);
        hash = 37 * hash + (this.geometryType != null ? this.geometryType.hashCode() : 0);
        hash = 37 * hash + (this.srid != null ? this.srid.hashCode() : 0);
        return hash;
    }
}
