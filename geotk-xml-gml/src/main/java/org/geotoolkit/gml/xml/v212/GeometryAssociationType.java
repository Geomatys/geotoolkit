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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *         An instance of this type (e.g. a geometryMember) can either
 *         enclose or point to a primitive geometry element. When serving
 *         as a simple link that references a remote geometry instance,
 *         the value of the gml:remoteSchema attribute can be used to
 *         locate a schema fragment that constrains the target instance.
 *
 *
 * <p>Java class for GeometryAssociationType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GeometryAssociationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractGeometry"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/>
 *       &lt;attribute ref="{http://www.opengis.net/gml}remoteSchema"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeometryAssociationType", propOrder = {
    "abstractGeometry"
})
@XmlSeeAlso({
    PointPropertyType.class,
    MultiPolygonPropertyType.class,
    LineStringPropertyType.class,
    MultiPointPropertyType.class,
    LineStringMemberType.class,
    PointMemberType.class,
    LinearRingMemberType.class,
    PolygonPropertyType.class,
    MultiLineStringPropertyType.class,
    PolygonMemberType.class,
    MultiGeometryPropertyType.class
})
public class GeometryAssociationType {

    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractGeometryType> abstractGeometry;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

    public GeometryAssociationType() {

    }

    public GeometryAssociationType(final GeometryAssociationType that) {
        if (that != null) {
            this.actuate      = that.actuate;
            this.arcrole      = that.arcrole;
            this.href         = that.href;
            this.remoteSchema = that.remoteSchema;
            this.role         = that.role;
            this.show         = that.show;
            this.title        = that.title;
            this.type         = that.type;

            if (that.abstractGeometry != null) {
                AbstractGeometryType geom = that.abstractGeometry.getValue().getClone();
                this.abstractGeometry = geom.getXmlElement();
            }
        }
    }

    /**
     * Gets the value of the abstractGeometry property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiLineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeometryCollectionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeometryCollectionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractGeometryType> getAbstractGeometry() {
        return abstractGeometry;
    }

    /**
     * Sets the value of the abstractGeometry property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiLineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeometryCollectionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeometryCollectionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *
     */
    public void setAbstractGeometry(final JAXBElement<? extends AbstractGeometryType> value) {
        this.abstractGeometry = ((JAXBElement<? extends AbstractGeometryType> ) value);
    }

    /**
     * Gets the value of the remoteSchema property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRemoteSchema(final String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRole(final String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setArcrole(final String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShow(final String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setActuate(final String value) {
        this.actuate = value;
    }

    public GeometryAssociationType getClone() {
        throw new UnsupportedOperationException("Must be overriden by sub-class");
    }

    public JAXBElement<? extends GeometryAssociationType> getXmlElement() {
        final ObjectFactory factory = new ObjectFactory();
        if (this instanceof PointPropertyType) {
            return  factory.createPointProperty((PointPropertyType) this);
        } else if (this instanceof MultiPolygonPropertyType) {
            return  factory.createMultiPolygonProperty((MultiPolygonPropertyType) this);
        } else if (this instanceof LineStringPropertyType) {
            return  factory.createLineStringProperty((LineStringPropertyType) this);
        } else if (this instanceof MultiPointPropertyType) {
            return  factory.createMultiPointProperty((MultiPointPropertyType) this);
        } else if (this instanceof LineStringMemberType) {
            return  factory.createLineStringMember((LineStringMemberType) this);
        } else if (this instanceof PointMemberType) {
            return  factory.createPointMember((PointMemberType) this);
        } else if (this instanceof PolygonPropertyType) {
            return  factory.createPolygonProperty((PolygonPropertyType) this);
        } else if (this instanceof MultiLineStringPropertyType) {
            return  factory.createMultiLineStringProperty((MultiLineStringPropertyType) this);
        } else if (this instanceof PolygonMemberType) {
            return  factory.createPolygonMember((PolygonMemberType) this);
         } else if (this instanceof MultiGeometryPropertyType) {
            return  factory.createMultiGeometryProperty((MultiGeometryPropertyType) this);
        } else {
            throw new IllegalArgumentException("unexpected geometry type:" + this);
        }
    }
}
