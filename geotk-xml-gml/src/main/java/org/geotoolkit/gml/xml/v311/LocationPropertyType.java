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
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.LocationProperty;
import org.geotoolkit.util.Utilities;


/**
 * Deprecated in GML 3.1.0
 *
 * <p>Java class for LocationPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LocationPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}AbstractGeometry"/>
 *           &lt;element ref="{http://www.opengis.net/gml}LocationKeyWord"/>
 *           &lt;element ref="{http://www.opengis.net/gml}LocationString"/>
 *           &lt;element ref="{http://www.opengis.net/gml}Null"/>
 *         &lt;/choice>
 *       &lt;/sequence>
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
@XmlType(name = "LocationPropertyType", propOrder = {
    "abstractGeometry",
    "locationKeyWord",
    "locationString",
    "_null"
})
@XmlSeeAlso({
    PriorityLocationPropertyType.class
})
public class LocationPropertyType implements LocationProperty {

    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    protected JAXBElement<? extends AbstractGeometryType> abstractGeometry;
    @XmlElement(name = "LocationKeyWord")
    protected CodeType locationKeyWord;
    @XmlElement(name = "LocationString")
    protected StringOrRefType locationString;
    @XmlList
    @XmlElement(name = "Null")
    protected List<String> _null;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    protected String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    protected String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    protected String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    protected String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    protected String actuate;

    public AbstractGeometryType getAbstractGeometry() {
        if (abstractGeometry != null) {
            return this.abstractGeometry.getValue();
        }
        return null;
    }

    /**
     * Gets the value of the abstractGeometry property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TinType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TriangulatedSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolyhedralSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiLineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractGeometryType> getjbAbstractGeometry() {
        return abstractGeometry;
    }

    /**
     * Sets the value of the abstractGeometry property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TinType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TriangulatedSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolyhedralSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiLineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}
     *
     */
    public void setAbstractGeometry(final JAXBElement<? extends AbstractGeometryType> value) {
        this.abstractGeometry = ((JAXBElement<? extends AbstractGeometryType> ) value);
    }

    /**
     * Gets the value of the locationKeyWord property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    @Override
    public CodeType getLocationKeyWord() {
        return locationKeyWord;
    }

    /**
     * Sets the value of the locationKeyWord property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setLocationKeyWord(final CodeType value) {
        this.locationKeyWord = value;
    }

    /**
     * Gets the value of the locationString property.
     *
     * @return
     *     possible object is
     *     {@link StringOrRefType }
     *
     */
    public StringOrRefType getLocationString() {
        return locationString;
    }

    /**
     * Sets the value of the locationString property.
     *
     * @param value
     *     allowed object is
     *     {@link StringOrRefType }
     *
     */
    public void setLocationString(final StringOrRefType value) {
        this.locationString = value;
    }

    /**
     * Gets the value of the nilReason property.
     */
    @Override
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return nilReason;
    }

    /**
     * Gets the value of the null property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNull() {
        if (_null == null) {
            _null = new ArrayList<String>();
        }
        return this._null;
    }

    /**
     * Gets the value of the remoteSchema property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LocationPropertyType) {
            final LocationPropertyType that = (LocationPropertyType) object;

            boolean geom = false;
            if (this.abstractGeometry != null && that.abstractGeometry != null) {
                geom = Objects.equals(this.abstractGeometry.getValue(),   that.abstractGeometry.getValue());
            } else if (this.abstractGeometry == null && that.abstractGeometry == null) {
                geom = true;
            }

            return Objects.equals(this.actuate,            that.actuate)          &&
                   Objects.equals(this._null,              that._null)            &&
                   Objects.equals(this.arcrole,            that.arcrole)          &&
                   Objects.equals(this.type,               that.type)             &&
                   Objects.equals(this.href,               that.href)             &&
                   Objects.equals(this.locationKeyWord,    that.locationKeyWord)  &&
                   Objects.equals(this.locationString,     that.locationString)   &&
                   Objects.equals(this.nilReason,          that.nilReason)        &&
                   Objects.equals(this.remoteSchema,       that.remoteSchema)     &&
                   Objects.equals(this.show,               that.show)             &&
                   Objects.equals(this.role,               that.role)             &&
                   Objects.equals(this.title,              that.title)            &&
                   geom;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.abstractGeometry != null ? this.abstractGeometry.hashCode() : 0);
        hash = 79 * hash + (this.locationKeyWord != null ? this.locationKeyWord.hashCode() : 0);
        hash = 79 * hash + (this.locationString != null ? this.locationString.hashCode() : 0);
        hash = 79 * hash + (this._null != null ? this._null.hashCode() : 0);
        hash = 79 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 79 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 79 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 79 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 79 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 79 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 79 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

}
