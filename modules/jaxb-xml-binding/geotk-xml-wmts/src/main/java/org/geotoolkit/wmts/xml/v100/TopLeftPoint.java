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
package org.geotoolkit.wmts.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "topLeftPoint"
})
@XmlRootElement(name="TopLeftPoint")
public class TopLeftPoint {

    @XmlElementRef(name = "Point", namespace="http://www.opengis.net/gml")
    private JAXBElement<PointType> topLeftPoint;

    @XmlTransient
    private ObjectFactory factory = new ObjectFactory();

    public TopLeftPoint() {
    }

    public TopLeftPoint(PointType pt) {
        this.topLeftPoint = factory.createPoint(pt);
    }

    /**
     * @return the topLeftPoint
     */
    public PointType getTopLeftPoint() {
        if (topLeftPoint != null)
            return topLeftPoint.getValue();
        return null;
    }

    /**
     * @param topLeftPoint the topLeftPoint to set
     */
    public void setTopLeftPoint(PointType topLeftPoint) {
        this.topLeftPoint = factory.createPoint(topLeftPoint);
    }

    /**
     * Vérifie que cette station est identique à l'objet spécifié
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TopLeftPoint) {
            final TopLeftPoint that = (TopLeftPoint) object;
            if (this.topLeftPoint == null && that.topLeftPoint == null) {
                return true;
            } else if (this.topLeftPoint != null && that.topLeftPoint != null) {
                return Utilities.equals(this.topLeftPoint.getValue(), that.topLeftPoint.getValue());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.topLeftPoint != null ? this.topLeftPoint.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TopLeftPoint[ ").append('\n');
        if (topLeftPoint != null) {
            s.append(topLeftPoint.getValue()).append('\n');
        }
        return s.append(']').toString();
    }

}
