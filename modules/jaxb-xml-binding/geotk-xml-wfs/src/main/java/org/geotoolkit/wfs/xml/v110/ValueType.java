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
package org.geotoolkit.wfs.xml.v110;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.MultiCurveType;
import org.geotoolkit.gml.xml.v311.MultiLineStringType;
import org.geotoolkit.gml.xml.v311.MultiPointType;
import org.geotoolkit.gml.xml.v311.MultiPolygonType;
import org.geotoolkit.gml.xml.v311.MultiSolidType;
import org.geotoolkit.gml.xml.v311.MultiSurfaceType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonType;
import org.geotoolkit.gml.xml.v311.PolyhedralSurfaceType;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueType", propOrder = {
    "value"
})
public class ValueType {

    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> value;

    public ValueType(){

    }

    public ValueType(Object obj) {
        org.geotoolkit.gml.xml.v311.ObjectFactory gmlFactory = new org.geotoolkit.gml.xml.v311.ObjectFactory();
        if (obj instanceof PolygonType) {
            obj = gmlFactory.createPolygon((PolygonType) obj);
        } else if (obj instanceof CurveType) {
            obj = gmlFactory.createCurve((CurveType) obj);
        } else if (obj instanceof PointType) {
            obj = gmlFactory.createPoint((PointType) obj);
        } else if (obj instanceof LineStringType) {
            obj = gmlFactory.createLineString((LineStringType) obj);
        } else if (obj instanceof PolyhedralSurfaceType) {
            obj = gmlFactory.createPolyhedralSurface((PolyhedralSurfaceType) obj);
        } else if (obj instanceof MultiCurveType) {
            obj = gmlFactory.createMultiCurve((MultiCurveType) obj);
        } else if (obj instanceof MultiLineStringType) {
            obj = gmlFactory.createMultiLineString((MultiLineStringType) obj);
        } else if (obj instanceof MultiPointType) {
            obj = gmlFactory.createMultiPoint((MultiPointType) obj);
        } else if (obj instanceof MultiPolygonType) {
            obj = gmlFactory.createMultiPolygon((MultiPolygonType) obj);
        } else if (obj instanceof MultiSolidType) {
            obj = gmlFactory.createMultiSolid((MultiSolidType) obj);
        } else if (obj instanceof MultiSurfaceType) {
            obj = gmlFactory.createMultiSurface((MultiSurfaceType) obj);
        } 
        this.value = Arrays.asList(obj);
    }

    /**
     * @return the value
     */
    public Object getValue() {
        cleanValueList();
        if (value.size() == 1) {
            if (value.get(0) instanceof JAXBElement) {
                return ((JAXBElement)value.get(0)).getValue();
            }
            return value.get(0);
        }
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = Arrays.asList(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ValueType]\n");
        if (value != null) {
            if (value instanceof JAXBElement) {
                sb.append("value<JAXBElement>:").append(((JAXBElement)value).getValue());
            } else {
                sb.append("value:").append(value);
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ValueType) {
            final ValueType that = (ValueType) object;
            return  Utilities.equals(this.getValue(), that.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    public void cleanValueList() {
        List<Object> toRemove = new ArrayList<Object>();
        for (Object obj : value) {
            if (obj instanceof String) {
                if (isOnlySpace((String)obj)) {
                    toRemove.add(obj);
                }
            }
        }
        for (Object obj : toRemove) {
            value.remove(obj);
        }
    }
    public boolean isOnlySpace(String s) {
        String left = s.replaceAll(" ", "");
        left        = left.replaceAll("\n", "");
        left        = left.replaceAll("\t", "");
        return left.length() == 0;
    }
}
