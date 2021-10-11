/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.GeometryProperty;


/**
 * A geometric property may either be any geometry element encapsulated in an element of this type or an XLink reference to a remote geometry element (where remote includes geometry elements located elsewhere in the same or another document). Note that either the reference or the contained element shall be given, but not both or none.
 * If a feature has a property that takes a geometry element as its value, this is called a geometry property. A generic type for such a geometry property is GeometryPropertyType.
 *
 * <p>Java class for GeometryPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GeometryPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}AbstractGeometry"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}AssociationAttributeGroup"/>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}OwnershipAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeometryPropertyType", propOrder = {
    "abstractGeometry"
})
public class GeometryPropertyType implements GeometryProperty {

    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<? extends AbstractGeometryType> abstractGeometry;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml/3.2")
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
    @XmlAttribute
    private java.lang.Boolean owns;

    public GeometryPropertyType() {

    }

    public GeometryPropertyType(final AbstractGeometryType geom) {
        final ObjectFactory factory = new ObjectFactory();
        this.abstractGeometry = (JAXBElement<? extends AbstractGeometryType>) factory.buildAnyGeometry(geom);
    }

    /**
     * Gets the value of the abstractGeometry property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TinType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GridType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeometricComplexType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RectifiedGridType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractGeometryType> getJbAbstractGeometry() {
        return abstractGeometry;
    }

    /**
     * Sets the value of the abstractGeometry property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TinType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GridType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeometricComplexType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RectifiedGridType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *
     */
    public void setJbAbstractGeometry(JAXBElement<? extends AbstractGeometryType> value) {
        this.abstractGeometry = ((JAXBElement<? extends AbstractGeometryType> ) value);
    }

    /**
     * Gets the value of the abstractGeometry property.
     *
     * @return
     *     possible object is
     *     {@code <}{@link OrientableSurfaceType }{@code >}
     *     {@code <}{@link AbstractSolidType }{@code >}
     *     {@code <}{@link MultiSurfaceType }{@code >}
     *     {@code <}{@link AbstractGeometricAggregateType }{@code >}
     *     {@code <}{@link SolidType }{@code >}
     *     {@code <}{@link TinType }{@code >}
     *     {@code <}{@link MultiGeometryType }{@code >}
     *     {@code <}{@link SurfaceType }{@code >}
     *     {@code <}{@link MultiPointType }{@code >}
     *     {@code <}{@link LineStringType }{@code >}
     *     {@code <}{@link AbstractCurveType }{@code >}
     *     {@code <}{@link AbstractSurfaceType }{@code >}
     *     {@code <}{@link LinearRingType }{@code >}
     *     {@code <}{@link AbstractRingType }{@code >}
     *     {@code <}{@link TriangulatedSurfaceType }{@code >}
     *     {@code <}{@link PolyhedralSurfaceType }{@code >}
     *     {@code <}{@link CurveType }{@code >}
     *     {@code <}{@link MultiLineStringType }{@code >}
     *     {@code <}{@link AbstractGeometryType }{@code >}
     *     {@code <}{@link OrientableCurveType }{@code >}
     *     {@code <}{@link MultiPolygonType }{@code >}
     *     {@code <}{@link PolygonType }{@code >}
     *     {@code <}{@link RingType }{@code >}
     *     {@code <}{@link MultiCurveType }{@code >}
     *     {@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     *     {@code <}{@link PointType }{@code >}
     *     {@code <}{@link MultiSolidType }{@code >}
     *
     */
    @Override
    public AbstractGeometryType getAbstractGeometry() {
        if (abstractGeometry != null) {
            return abstractGeometry.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the abstractGeometry property.
     *
     * @param value
     *     allowed object is
     *     {@code <}{@link OrientableSurfaceType }{@code >}
     *     {@code <}{@link AbstractSolidType }{@code >}
     *     {@code <}{@link MultiSurfaceType }{@code >}
     *     {@code <}{@link AbstractGeometricAggregateType }{@code >}
     *     {@code <}{@link SolidType }{@code >}
     *     {@code <}{@link TinType }{@code >}
     *     {@code <}{@link MultiGeometryType }{@code >}
     *     {@code <}{@link SurfaceType }{@code >}
     *     {@code <}{@link MultiPointType }{@code >}
     *     {@code <}{@link LineStringType }{@code >}
     *     {@code <}{@link AbstractCurveType }{@code >}
     *     {@code <}{@link AbstractSurfaceType }{@code >}
     *     {@code <}{@link LinearRingType }{@code >}
     *     {@code <}{@link AbstractRingType }{@code >}
     *     {@code <}{@link TriangulatedSurfaceType }{@code >}
     *     {@code <}{@link PolyhedralSurfaceType }{@code >}
     *     {@code <}{@link CurveType }{@code >}
     *     {@code <}{@link MultiLineStringType }{@code >}
     *     {@code <}{@link AbstractGeometryType }{@code >}
     *     {@code <}{@link OrientableCurveType }{@code >}
     *     {@code <}{@link MultiPolygonType }{@code >}
     *     {@code <}{@link PolygonType }{@code >}
     *     {@code <}{@link RingType }{@code >}
     *     {@code <}{@link MultiCurveType }{@code >}
     *     {@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     *     {@code <}{@link PointType }{@code >}
     *     {@code <}{@link MultiSolidType }{@code >}
     *
     */
    public void setAbstractGeometry(final AbstractGeometryType value) {
        final ObjectFactory factory = new ObjectFactory();
        if (value instanceof PolygonType) {
            abstractGeometry = factory.createPolygon((PolygonType) value);
        } else if (value instanceof CurveType) {
            abstractGeometry = factory.createCurve((CurveType) value);
        } else if (value instanceof OrientableSurfaceType) {
            abstractGeometry = factory.createOrientableSurface((OrientableSurfaceType) value);
        } else if (value instanceof LinearRingType) {
            abstractGeometry = factory.createLinearRing((LinearRingType) value);
        } else if (value instanceof RingType) {
            abstractGeometry = factory.createRing((RingType) value);
        } else if (value instanceof PointType) {
            abstractGeometry = factory.createPoint((PointType) value);
        } else if (value instanceof LineStringType) {
            abstractGeometry = factory.createLineString((LineStringType) value);
        } else if (value instanceof MultiCurveType) {
            abstractGeometry = factory.createMultiCurve((MultiCurveType) value);
        } else if (value instanceof MultiPointType) {
            abstractGeometry = factory.createMultiPoint((MultiPointType) value);
        } else if (value instanceof MultiSolidType) {
            abstractGeometry = factory.createMultiSolid((MultiSolidType) value);
        } else if (value instanceof MultiSurfaceType) {
            abstractGeometry = factory.createMultiSurface((MultiSurfaceType) value);
        } else {
            throw new IllegalArgumentException("unexpected geometry type:" + value);
        }
    }

    /**
     * Gets the value of the nilReason property.
     *
     * {@link String }
     *
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
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
    public void setRemoteSchema(String value) {
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
    public void setType(String value) {
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
    public void setHref(String value) {
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
    public void setRole(String value) {
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
    public void setArcrole(String value) {
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
    public void setTitle(String value) {
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
    public void setShow(String value) {
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
    public void setActuate(String value) {
        this.actuate = value;
    }

    /**
     * Gets the value of the owns property.
     *
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *
     */
    public boolean isOwns() {
        if (owns == null) {
            return false;
        } else {
            return owns;
        }
    }

    /**
     * Sets the value of the owns property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *
     */
    public void setOwns(java.lang.Boolean value) {
        this.owns = value;
    }

}
