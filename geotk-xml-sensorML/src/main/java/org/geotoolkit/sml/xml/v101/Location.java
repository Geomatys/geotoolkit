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
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.v311.AbstractCurveType;
import org.geotoolkit.gml.xml.v311.CompositeCurveType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.OrientableCurveType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.sml.xml.AbstractLocation;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}Point"/>
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractCurve"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "point",
    "abstractCurve"
})
@XmlRootElement(name = "Location")
public class Location extends SensorObject implements AbstractLocation {

    @XmlElement(name = "Point", namespace = "http://www.opengis.net/gml")
    private PointType point;
    @XmlElementRef(name = "AbstractCurve", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractCurveType> abstractCurve;
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

    public Location() {

    }

    public Location(final AbstractLocation loc) {
        if (loc != null) {
            this.actuate = loc.getActuate();
            this.arcrole = loc.getArcrole();
            this.href    = loc.getHref();
            this.remoteSchema = loc.getRemoteSchema();
            this.role    = loc.getRole();
            this.show    = loc.getShow();
            this.title   = loc.getTitle();
            this.type    = loc.getType();
            if (loc.getGeometry() instanceof PointType) {
                this.point   = (PointType) loc.getGeometry();
            } else if (loc.getGeometry() instanceof AbstractCurveType) {
                org.geotoolkit.gml.xml.v311.ObjectFactory f = new org.geotoolkit.gml.xml.v311.ObjectFactory();
                AbstractCurveType curve = (AbstractCurveType) loc.getGeometry();
                if (curve instanceof CompositeCurveType) {
                    this.abstractCurve = f.createCompositeCurve((CompositeCurveType) curve);
                } else if (curve instanceof LineStringType) {
                    this.abstractCurve = f.createLineString((LineStringType) curve);
                } else if (curve instanceof CurveType) {
                    this.abstractCurve = f.createCurve((CurveType) curve);
                } else if (curve instanceof OrientableCurveType) {
                    this.abstractCurve = f.createOrientableCurve((OrientableCurveType) curve);
                } else  {
                    this.abstractCurve = f.createAbstractCurve(curve);
                }
            }
        }
    }

    public Location(final PointType point) {
        this.point = point;
    }

    @Override
    public AbstractGeometry getGeometry() {
        if (point != null) {
            return point;
        } else if (abstractCurve != null) {
            return abstractCurve.getValue();
        }
        return null;
    }

    /**
     * Gets the value of the point property.
     *
     * @return
     *     possible object is
     *     {@link PointType }
     *
     */
    public PointType getPoint() {
        return point;
    }

    /**
     * Sets the value of the point property.
     *
     * @param value
     *     allowed object is
     *     {@link PointType }
     *
     */
    public void setPoint(final PointType value) {
        this.point = value;
    }

    /**
     * Gets the value of the abstractCurve property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CompositeCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractCurveType> getAbstractCurve() {
        return abstractCurve;
    }

    public AbstractCurveType getCurve() {
        if (abstractCurve != null) {
            return abstractCurve.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the abstractCurve property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CompositeCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *
     */
    public void setAbstractCurve(final JAXBElement<? extends AbstractCurveType> value) {
        this.abstractCurve = ((JAXBElement<? extends AbstractCurveType> ) value);
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
        return type;
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

}
