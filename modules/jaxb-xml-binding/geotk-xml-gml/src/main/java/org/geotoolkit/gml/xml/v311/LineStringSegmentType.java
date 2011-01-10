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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.geometry.DirectPosition;


/**
 * A LineStringSegment is a curve segment that is defined by two or more coordinate tuples, with linear interpolation between them.
 * Note: LineStringSegment implements GM_LineString of ISO 19107.
 * 
 * <p>Java class for LineStringSegmentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LineStringSegmentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractCurveSegmentType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;choice maxOccurs="unbounded" minOccurs="2">
 *             &lt;element ref="{http://www.opengis.net/gml}pos"/>
 *             &lt;element ref="{http://www.opengis.net/gml}pointProperty"/>
 *             &lt;element ref="{http://www.opengis.net/gml}pointRep"/>
 *           &lt;/choice>
 *           &lt;element ref="{http://www.opengis.net/gml}posList"/>
 *           &lt;element ref="{http://www.opengis.net/gml}coordinates"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="interpolation" type="{http://www.opengis.net/gml}CurveInterpolationType" fixed="linear" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineStringSegmentType", propOrder = {
    "pointPropertyOrPointRep",
    "pos",
    "posList",
    "coordinates"
})
public class LineStringSegmentType extends AbstractCurveSegmentType {

    @XmlElementRefs({
        @XmlElementRef(name = "pointProperty", namespace = "http://www.opengis.net/gml", type = JAXBElement.class),
        @XmlElementRef(name = "pointRep", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> pointPropertyOrPointRep;

    @XmlElement(name = "pos", namespace = "http://www.opengis.net/gml")
    private List<DirectPositionType> pos;
    private DirectPositionListType posList;
    private CoordinatesType coordinates;
    @XmlAttribute
    private CurveInterpolationType interpolation;

    public LineStringSegmentType() {

    }

    public LineStringSegmentType(final Integer numDerivativesAtStart, final Integer numDerivativesAtEnd, final Integer numDerivativeInterior,
            final CurveInterpolationType interpolation, final List<DirectPosition> positions) {
        super(numDerivativesAtStart, numDerivativesAtEnd, numDerivativeInterior);
        this.interpolation = interpolation;
        for (DirectPosition currentPos : positions) {
            DirectPositionType position = new DirectPositionType(currentPos);
            this.pos.add(position);
        }
    }

    /**
     * Gets the value of the posOrPointPropertyOrPointRep property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getPointPropertyOrPointRep() {
        if (pointPropertyOrPointRep == null) {
            pointPropertyOrPointRep = new ArrayList<JAXBElement<?>>();
        }
        return this.pointPropertyOrPointRep;
    }

    /**
     * Gets the value of the posOrPointPropertyOrPointRep property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link DirectPositionType }
     *
     */
    public List<DirectPositionType> getPos() {
        if (pos == null) {
            pos = new ArrayList<DirectPositionType>();
        }
        return this.pos;
    }

    public void setPos(final List<DirectPositionType> pos) {
        this.pos = pos;
    }

    public void setPos(final DirectPositionType pos) {
        if (this.pos == null) {
            this.pos = new ArrayList<DirectPositionType>();
        }
        this.pos.add(pos);
    }

    /**
     * Gets the value of the posList property.
     * 
     * @return
     *     possible object is
     *     {@link DirectPositionListType }
     *     
     */
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
    public void setPosList(final DirectPositionListType value) {
        this.posList = value;
    }

    /**
     * Deprecated with GML version 3.1.0. Use "posList" instead.
     * 
     * @return
     *     possible object is
     *     {@link CoordinatesType }
     *     
     */
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * Deprecated with GML version 3.1.0. Use "posList" instead.
     * 
     * @param value
     *     allowed object is
     *     {@link CoordinatesType }
     *     
     */
    public void setCoordinates(final CoordinatesType value) {
        this.coordinates = value;
    }

    /**
     * Gets the value of the interpolation property.
     * 
     * @return
     *     possible object is
     *     {@link CurveInterpolationType }
     *     
     */
    public CurveInterpolationType getInterpolation() {
        /*if (interpolation == null) {
            return CurveInterpolationType.LINEAR;
        } else {*/
        return interpolation;
    }

    /**
     * Sets the value of the interpolation property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurveInterpolationType }
     *     
     */
    public void setInterpolation(final CurveInterpolationType value) {
        this.interpolation = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LineStringSegmentType) {
            final LineStringSegmentType that = (LineStringSegmentType) object;

            boolean jb = false;
            if (this.getPointPropertyOrPointRep().size() == that.getPointPropertyOrPointRep().size()) {
                jb = true;
                for (int i = 0; i < this.getPointPropertyOrPointRep().size(); i++) {
                    if (!JAXBElementEquals(this.getPointPropertyOrPointRep().get(i), this.getPointPropertyOrPointRep().get(i))) {
                        jb = false;
                    }
                }
            }
            return Utilities.equals(this.coordinates,    that.coordinates)   &&
                   Utilities.equals(this.posList,        that.posList)       &&
                   Utilities.equals(this.interpolation,  that.interpolation) &&
                   Utilities.equals(this.pos,            that.pos) &&
                   jb;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.pos != null ? this.pos.hashCode() : 0);
        hash = 79 * hash + (this.pointPropertyOrPointRep != null ? this.pointPropertyOrPointRep.hashCode() : 0);
        hash = 79 * hash + (this.posList != null ? this.posList.hashCode() : 0);
        hash = 79 * hash + (this.coordinates != null ? this.coordinates.hashCode() : 0);
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

     @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (interpolation != null) {
            sb.append("interpolation:").append(interpolation).append('\n');
        }
        if (coordinates != null) {
            sb.append("coordinates:").append(coordinates).append('\n');
        }
        if (posList != null) {
            sb.append("posList:").append(posList).append('\n');
        }
        if (pointPropertyOrPointRep != null) {
            sb.append("pointPropertyOrPointRep:").append('\n');
            for (JAXBElement<?>  inte : pointPropertyOrPointRep) {
                sb.append(inte.getValue()).append('\n');
            }
        }
        if (pos != null) {
            sb.append("pos:").append('\n');
            for (DirectPositionType inte : pos) {
                sb.append(inte).append('\n');
            }
        }
        return sb.toString();
    }
}
