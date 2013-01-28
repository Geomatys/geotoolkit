/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.samplingspatial.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.ObjectFactory;
import org.geotoolkit.gml.xml.v321.AbstractCurveType;
import org.geotoolkit.gml.xml.v321.AbstractGeometricAggregateType;
import org.geotoolkit.gml.xml.v321.AbstractGeometricPrimitiveType;
import org.geotoolkit.gml.xml.v321.AbstractGeometryType;
import org.geotoolkit.gml.xml.v321.AbstractSolidType;
import org.geotoolkit.gml.xml.v321.AbstractSurfaceType;
import org.geotoolkit.gml.xml.v321.CompositeCurveType;
import org.geotoolkit.gml.xml.v321.CompositeSolidType;
import org.geotoolkit.gml.xml.v321.CompositeSurfaceType;
import org.geotoolkit.gml.xml.v321.CurveType;
import org.geotoolkit.gml.xml.v321.GeometricComplexType;
import org.geotoolkit.gml.xml.v321.GridType;
import org.geotoolkit.gml.xml.v321.LineStringType;
import org.geotoolkit.gml.xml.v321.LinearRingType;
import org.geotoolkit.gml.xml.v321.MultiCurveType;
import org.geotoolkit.gml.xml.v321.MultiGeometryType;
import org.geotoolkit.gml.xml.v321.MultiPointType;
import org.geotoolkit.gml.xml.v321.MultiSolidType;
import org.geotoolkit.gml.xml.v321.MultiSurfaceType;
import org.geotoolkit.gml.xml.v321.OrientableCurveType;
import org.geotoolkit.gml.xml.v321.OrientableSurfaceType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.gml.xml.v321.PolygonType;
import org.geotoolkit.gml.xml.v321.RectifiedGridType;
import org.geotoolkit.gml.xml.v321.RingType;
import org.geotoolkit.gml.xml.v321.SolidType;
import org.geotoolkit.gml.xml.v321.SurfaceType;
import org.geotoolkit.gml.xml.v321.TinType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.xlink.xml.v100.ActuateType;
import org.geotoolkit.xlink.xml.v100.ShowType;


/**
 * <p>Java class for shapeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shapeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}AbstractGeometry"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shapeType", propOrder = {
    "abstractGeometry"
})
public class ShapeType {

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
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String titleTemp;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private ShowType show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private ActuateType actuate;

    public ShapeType() {
        
    }
    
    public ShapeType(final AbstractGeometryType value) {
        final ObjectFactory factory = new ObjectFactory();
        if (value instanceof PolygonType) {
            abstractGeometry = factory.createPolygon((PolygonType) value);
        } else if (value instanceof OrientableSurfaceType) {
            abstractGeometry = factory.createOrientableSurface((OrientableSurfaceType) value);
        } else if (value instanceof LinearRingType) {
            abstractGeometry = factory.createLinearRing((LinearRingType) value);
        } else if (value instanceof RingType) {
            abstractGeometry = factory.createRing((RingType) value);
        } else if (value instanceof SurfaceType) {
            abstractGeometry = factory.createPolyhedralSurface((SurfaceType) value);
        } else if (value instanceof CurveType) {
            abstractGeometry = factory.createCurve((CurveType) value);
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
     * Gets the value of the abstractGeometry property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeometricComplexType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TinType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GridType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RectifiedGridType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
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
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeometricComplexType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TinType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeSurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GridType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RectifiedGridType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CompositeCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     *     
     */
    public void setAbstractGeometry(JAXBElement<? extends AbstractGeometryType> value) {
        this.abstractGeometry = ((JAXBElement<? extends AbstractGeometryType> ) value);
    }

    /**
     * Gets the value of the nilReason property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
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
     * Gets the value of the titleTemp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitleTemp() {
        return titleTemp;
    }

    /**
     * Sets the value of the titleTemp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitleTemp(String value) {
        this.titleTemp = value;
    }

    /**
     * Gets the value of the show property.
     * 
     * @return
     *     possible object is
     *     {@link ShowType }
     *     
     */
    public ShowType getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShowType }
     *     
     */
    public void setShow(ShowType value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     * 
     * @return
     *     possible object is
     *     {@link ActuateType }
     *     
     */
    public ActuateType getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActuateType }
     *     
     */
    public void setActuate(ActuateType value) {
        this.actuate = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ShapeType]").append("\n");
        if (abstractGeometry != null) {
            sb.append("abstractGeometry: ").append(abstractGeometry.getValue()).append('\n');
        }
        if (remoteSchema != null) {
            sb.append("remoteSchema: ").append(remoteSchema).append('\n');
        }
        if (actuate != null) {
            sb.append("actuate: ").append(actuate).append('\n');
        }
        if (arcrole != null) {
            sb.append("actuate: ").append(arcrole).append('\n');
        }
        if (href != null) {
            sb.append("href: ").append(href).append('\n');
        }
        if (role != null) {
            sb.append("role: ").append(role).append('\n');
        }
        if (show != null) {
            sb.append("show: ").append(show).append('\n');
        }
        if (nilReason != null) {
            sb.append("nilReason: ").append(nilReason).append('\n');
        }
        if (type != null) {
            sb.append("type: ").append(type).append('\n');
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

        if (object instanceof ShapeType) {
            final ShapeType that = (ShapeType) object;

            AbstractGeometryType thatgeom = null;
            if (that.abstractGeometry != null) {
                thatgeom = that.abstractGeometry.getValue();
            }
            
            AbstractGeometryType thisgeom = null;
            if (this.abstractGeometry != null) {
                thisgeom = this.abstractGeometry.getValue();
            }
            
            return Utilities.equals(thisgeom,          thatgeom)          &&
                   Utilities.equals(this.actuate,      that.actuate)      &&
                   Utilities.equals(this.href,         that.href)         &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema) &&
                   Utilities.equals(this.role,         that.role)         &&
                   Utilities.equals(this.show,         that.show)         &&
                   Utilities.equals(this.nilReason,    that.nilReason)    &&
                   Utilities.equals(this.type,         that.type)         &&
                   Utilities.equals(this.arcrole,      that.arcrole);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.abstractGeometry != null ? this.abstractGeometry.hashCode() : 0);
        hash = 83 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 83 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 83 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 83 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 83 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 83 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 83 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        return hash;
    }
}
