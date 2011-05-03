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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * A LinearRing is defined by four or more coordinate tuples, with linear interpolation between them; the first and last coordinates must be coincident.
 * 
 * <p>Java class for LinearRingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LinearRingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractRingType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;choice maxOccurs="unbounded" minOccurs="4">
 *             &lt;element ref="{http://www.opengis.net/gml}pos"/>
 *             &lt;element ref="{http://www.opengis.net/gml}pointProperty"/>
 *             &lt;element ref="{http://www.opengis.net/gml}pointRep"/>
 *           &lt;/choice>
 *           &lt;element ref="{http://www.opengis.net/gml}posList"/>
 *           &lt;element ref="{http://www.opengis.net/gml}coordinates"/>
 *           &lt;element ref="{http://www.opengis.net/gml}coord" maxOccurs="unbounded" minOccurs="4"/>
 *         &lt;/choice>
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
@XmlType(name = "LinearRingType", propOrder = {
    "posOrPointPropertyOrPointRep",
    "posList",
    "coordinates",
    "coord"
})
public class LinearRingType extends AbstractRingType {

    @XmlElementRefs({
        @XmlElementRef(name = "pointProperty", namespace = "http://www.opengis.net/gml", type = JAXBElement.class),
        @XmlElementRef(name = "pos", namespace = "http://www.opengis.net/gml", type = JAXBElement.class),
        @XmlElementRef(name = "pointRep", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> posOrPointPropertyOrPointRep;
    private DirectPositionListType posList;
    private CoordinatesType coordinates;
    private List<CoordType> coord;

    /**
     * Gets the value of the posOrPointPropertyOrPointRep property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link DirectPositionType }{@code >}
     * {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getPosOrPointPropertyOrPointRep() {
        if (posOrPointPropertyOrPointRep == null) {
            posOrPointPropertyOrPointRep = new ArrayList<JAXBElement<?>>();
        }
        return this.posOrPointPropertyOrPointRep;
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
     * Deprecated with GML version 3.0 and included for backwards compatibility with GML 2.
     * Use "pos" elements instead.Gets the value of the coord property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link CoordType }
     * 
     * 
     */
    public List<CoordType> getCoord() {
        if (coord == null) {
            coord = new ArrayList<CoordType>();
        }
        return this.coord;
    }

    @Override
    public Object evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T evaluate(final Object object, final Class<T> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(final ExpressionVisitor visitor, final Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LinearRingType) {
            final LinearRingType that = (LinearRingType) object;

            boolean jb = false;
            if (this.getPosOrPointPropertyOrPointRep().size() == that.getPosOrPointPropertyOrPointRep().size()) {
                jb = true;
                for (int i = 0; i < this.getPosOrPointPropertyOrPointRep().size(); i++) {
                    if (!JAXBElementEquals(this.getPosOrPointPropertyOrPointRep().get(i), this.getPosOrPointPropertyOrPointRep().get(i))) {
                        jb = false;
                    }
                }
            }
            return Utilities.equals(this.coordinates,  that.coordinates) &&
                   Utilities.equals(this.posList,      that.posList)     &&
                   Utilities.equals(this.coord,        that.coord)       &&
                   jb;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.posOrPointPropertyOrPointRep != null ? this.posOrPointPropertyOrPointRep.hashCode() : 0);
        hash = 97 * hash + (this.posList != null ? this.posList.hashCode() : 0);
        hash = 97 * hash + (this.coordinates != null ? this.coordinates.hashCode() : 0);
        hash = 97 * hash + (this.coord != null ? this.coord.hashCode() : 0);
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
        if (posList != null) {
            sb.append("posList:").append(posList).append('\n');
        }
        if (coordinates != null) {
            sb.append("coodinates:").append(coordinates).append('\n');
        }
        if (coord != null) {
            for (CoordType c : coord) {
                sb.append(c).append('\n');
            }
        }
        
        if (posOrPointPropertyOrPointRep != null) {
            for (JAXBElement jb : posOrPointPropertyOrPointRep) {
                sb.append(jb.getValue()).append('\n');
            }
        }
        return sb.toString();
    }
}
