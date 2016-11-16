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
package org.geotoolkit.gml.xml.v212;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 *         All geometry elements are derived from this abstract supertype; 
 *         a geometry element may have an identifying attribute (gid). 
 *         It may be associated with a spatial reference system.
 *       
 * 
 * <p>Java class for AbstractGeometryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractGeometryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="gid" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGeometryType")
@XmlSeeAlso({
    LinearRingType.class,
    PointType.class,
    LineStringType.class,
    BoxType.class,
    PolygonType.class,
    AbstractGeometryCollectionBaseType.class
})
public abstract class AbstractGeometryType {

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String gid;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;

    public AbstractGeometryType() {
        
    }
    
    public AbstractGeometryType(final String srsName) {
        this.srsName = srsName;
    }
    
    public AbstractGeometryType(final AbstractGeometryType that) {
        if (that != null) {
            this.gid     = that.gid;
            this.srsName = that.srsName;
        }
    }
    
    /**
     * Gets the value of the gid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGid() {
        return gid;
    }

    /**
     * Sets the value of the gid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGid(final String value) {
        this.gid = value;
    }

    /**
     * Gets the value of the srsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * Sets the value of the srsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrsName(final String value) {
        this.srsName = value;
    }

    public JAXBElement<? extends AbstractGeometryType> getXmlElement() {
        final ObjectFactory factory = new ObjectFactory();
        if (this instanceof PolygonType) {
            return  factory.createPolygon((PolygonType) this);
        } else if (this instanceof LinearRingType) {
            return  factory.createLinearRing((LinearRingType) this);
        } else if (this instanceof PointType) {
            return  factory.createPoint((PointType) this);
        } else if (this instanceof LineStringType) {
            return  factory.createLineString((LineStringType) this);
        } else if (this instanceof MultiLineStringType) {
            return  factory.createMultiLineString((MultiLineStringType) this);
        } else if (this instanceof MultiPointType) {
            return  factory.createMultiPoint((MultiPointType) this);
        } else if (this instanceof MultiPolygonType) {
            return  factory.createMultiPolygon((MultiPolygonType) this);
        } else {
            throw new IllegalArgumentException("unexpected geometry type:" + this);
        }
    }
    
    public abstract AbstractGeometryType getClone();
}
