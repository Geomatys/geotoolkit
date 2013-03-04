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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.LineString;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;
import org.opengis.geometry.DirectPosition;


/**
 * <p>Java class for LineStringType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LineStringType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractCurveType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;choice maxOccurs="unbounded" minOccurs="2">
 *             &lt;element ref="{http://www.opengis.net/gml/3.2}pos"/>
 *             &lt;element ref="{http://www.opengis.net/gml/3.2}pointProperty"/>
 *             &lt;element ref="{http://www.opengis.net/gml/3.2}pointRep"/>
 *           &lt;/choice>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}posList"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}coordinates"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineStringType", propOrder = {
    "pointPropertyOrPointRep",
    "pos",
    "posList",
    "coordinates"
})
public class LineStringType extends AbstractCurveType implements LineString {

    @XmlElementRefs({
        @XmlElementRef(name = "pointProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class),
        @XmlElementRef(name = "pointRep", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> pointPropertyOrPointRep;

    @XmlElement(name = "pos", namespace = "http://www.opengis.net/gml/3.2")
    private List<DirectPositionType> pos;
    private DirectPositionListType posList;
    private CoordinatesType coordinates;

    /**
     * An empty constructor used by JAXB.
     */
    LineStringType() {}

    /**
     * Build a new LineString with the specified coordinates
     */
    public LineStringType(final CoordinatesType coordinates) {
        this.coordinates = coordinates;
    }
    
    public LineStringType(final String id, final List<DirectPositionType> pos) {
        super(id, null);
        this.pos = pos;
    }

    /**
     * Build a new LineString with the specified coordinates
     */
    public LineStringType(final List<DirectPosition> positions) {
        this.pos = new ArrayList<DirectPositionType>();
        for (DirectPosition currentPos : positions) {
            final DirectPositionType position;
            if (currentPos instanceof DirectPositionType) {
                position = (DirectPositionType) currentPos;
            } else {
                position = new DirectPositionType(currentPos, true);
            }
            pos.add(position);
        }
    }

    /**
     * Gets the value of the pointPropertyOrPointRep property.
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}
     *
     */
    public List<JAXBElement<?>> getPointPropertyOrPointRep() {
        if (pointPropertyOrPointRep == null) {
            pointPropertyOrPointRep = new ArrayList<JAXBElement<?>>();
        }
        return this.pointPropertyOrPointRep;
    }

    @Override
    public List<DirectPositionType> getPos() {
        if (pos == null) {
            pos = new ArrayList<DirectPositionType>();
        }
        return pos;
    }

    /**
     * Gets the value of the posList property.
     *
     * @return
     *     possible object is
     *     {@link DirectPositionListType }
     *
     */
    @Override
    public DirectPositionListType getPosList() {
        return posList;
    }

    /**
     * Sets the value of the posList property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectPositionListType }
     *
     */
    public void setPosList(DirectPositionListType value) {
        this.posList = value;
    }

    /**
     * Gets the value of the coordinates property.
     *
     * @return
     *     possible object is
     *     {@link CoordinatesType }
     *
     */
    @Override
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of the coordinates property.
     *
     * @param value
     *     allowed object is
     *     {@link CoordinatesType }
     *
     */
    public void setCoordinates(CoordinatesType value) {
        this.coordinates = value;
    }

    @Override
    public Envelope getBounds() {
        double minx =  Double.MAX_VALUE;
        double miny =  Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;
        
        if (pos != null && !pos.isEmpty()) {
            for (DirectPositionType p : pos) {
                final double x = p.getOrdinate(0);
                final double y = p.getOrdinate(1);
                if (x < minx) { minx = x; }
                if (x > maxx) { maxx = x; }
                if (y < miny) { miny = y; }
                if (y > maxy) { maxy = y; }
            }
            final DirectPositionType lowerCorner = new DirectPositionType(minx, miny);
            final DirectPositionType upperCorner = new DirectPositionType(maxx, maxy);
            return new EnvelopeType(lowerCorner, upperCorner, getSrsName());
        }
        return null;
        
        // TODO try with posList and coordinates if pos is null or empty
    }
    
    @Override
    public void emptySrsNameOnChild() {
         if (pos != null && !pos.isEmpty()) {
            for (DirectPositionType p : pos) {
                p.srsName      = null;
                p.srsDimension = null;
            }
        } else if (posList != null) {
            posList.setSrsName(null);
            posList.setSrsDimension(null);
        } 
    }
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof LineStringType) {
            final LineStringType that = (LineStringType) object;

            boolean jb = false;
            if (this.getPointPropertyOrPointRep().size() == that.getPointPropertyOrPointRep().size()) {
                jb = true;
                for (int i = 0; i < this.getPointPropertyOrPointRep().size(); i++) {
                    if (!JAXBElementEquals(this.getPointPropertyOrPointRep().get(i), that.getPointPropertyOrPointRep().get(i))) {
                        jb = false;
                    }
                }
            }
            return Utilities.equals(this.coordinates, that.coordinates) &&
                   Utilities.equals(this.posList,     that.posList)     &&
                   Utilities.equals(this.pos,         that.pos)         &&
                   jb;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.pointPropertyOrPointRep != null ? this.pointPropertyOrPointRep.hashCode() : 0);
        hash = 79 * hash + (this.posList != null ? this.posList.hashCode() : 0);
        hash = 79 * hash + (this.coordinates != null ? this.coordinates.hashCode() : 0);
        hash = 79 * hash + (this.pos != null ? this.pos.hashCode() : 0);
        return hash;
    }

    private boolean JAXBElementEquals(final JAXBElement a, final JAXBElement b) {
        if (a  != null && b != null) {
            return Utilities.equals(a.getValue(), b.getValue());
        } else if (a == null && b == null) {
            return true;
        }
        return false;
    }

    /**
     * Retourne une representation de l'objet.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (posList != null) {
            s.append("posList").append(posList).append('\n');
        }
        if(coordinates != null) {
            s.append("coordinates=").append(coordinates).append('\n');
        }
        if(pointPropertyOrPointRep != null) {
            s.append("point :").append('\n');
            for (JAXBElement jb : pointPropertyOrPointRep) {
                s.append(jb.getValue()).append('\n');
            }
        }
        if(pos != null) {
            s.append("pos :").append('\n');
            for (DirectPositionType jb : pos) {
                s.append(jb).append('\n');
            }
        }
        return s.toString();
    }
}
