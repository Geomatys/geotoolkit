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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractCurveType;
import org.geotoolkit.gml.xml.v311.CompositeCurveType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.OrientableCurveType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.sml.xml.AbstractLocation;
import org.geotoolkit.util.Utilities;


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
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "point",
    "abstractCurve"
})
@XmlRootElement(name = "location")
public class Location implements AbstractLocation {

    @XmlElement(name = "Point", namespace = "http://www.opengis.net/gml")
    private PointType point;
    @XmlElementRef(name = "AbstractCurve", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractCurveType> abstractCurve;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;

    public Location() {

    }

    public Location(PointType point) {
        this.point = point;
    }

    public Location(AbstractLocation loc) {
        if (loc != null) {
            this.actuate = loc.getActuate();
            this.arcrole = loc.getArcrole();
            this.href    = loc.getHref();
            this.remoteSchema = loc.getRemoteSchema();
            this.role    = loc.getRole();
            this.show    = loc.getShow();
            this.title   = loc.getTitle();
            this.type    = loc.getType();
            this.point   = loc.getPoint();
            if (loc.getCurve() != null) {
                org.geotoolkit.gml.xml.v311.ObjectFactory f = new org.geotoolkit.gml.xml.v311.ObjectFactory();
                AbstractCurveType curve = loc.getCurve();
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
    
    /**
     * Gets the value of the point property.
     */
    public PointType getPoint() {
        return point;
    }

    /**
     * Sets the value of the point property.
     */
    public void setPoint(PointType value) {
        this.point = value;
    }

    /**
     * Gets the value of the abstractCurve property.
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
     */
    public void setAbstractCurve(JAXBElement<? extends AbstractCurveType> value) {
        this.abstractCurve = value;
    }

    /**
     * Gets the value of the nilReason property.
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
    }

    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     */
    public void setRemoteSchema(String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     */
    public void setActuate(String value) {
        this.actuate = value;
    }

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     */
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the href property.
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     */
    public void setShow(String value) {
        this.show = value;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the type property.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     */
    public void setType(String value) {
        this.type = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Location]").append("\n");
        if (abstractCurve != null) {
            sb.append("abstract curve: ").append(abstractCurve.getValue()).append('\n');
        }
        if (remoteSchema != null) {
            sb.append("remoteSchema: ").append(remoteSchema).append('\n');
        }
        if (point != null) {
            sb.append("point: ").append(point).append('\n');
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
        if (title != null) {
            sb.append("title: ").append(title).append('\n');
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

        boolean record = false;
        if (object instanceof Location) {
            final Location that = (Location) object;
            if (this.abstractCurve != null && that.abstractCurve != null) {
                record = Utilities.equals(this.abstractCurve.getValue(), that.abstractCurve.getValue());
            } else if (this.abstractCurve == null && that.abstractCurve == null) {
                record = true;
            }

            return Utilities.equals(this.actuate,      that.actuate)       &&
                   Utilities.equals(this.arcrole,      that.arcrole)       &&
                   Utilities.equals(this.point,        that.point)         &&
                   Utilities.equals(this.href,         that.href)          &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema)  &&
                   Utilities.equals(this.role,         that.role)          &&
                   Utilities.equals(this.show,         that.show)          &&
                   Utilities.equals(this.title,        that.title)         &&
                   record                                                  &&
                   Utilities.equals(this.type,         that.type)          &&
                   Utilities.equals(this.nilReason,         that.nilReason);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.point != null ? this.point.hashCode() : 0);
        hash = 53 * hash + (this.abstractCurve != null ? this.abstractCurve.hashCode() : 0);
        hash = 53 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 53 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 53 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 53 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 53 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 53 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 53 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 53 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

}
