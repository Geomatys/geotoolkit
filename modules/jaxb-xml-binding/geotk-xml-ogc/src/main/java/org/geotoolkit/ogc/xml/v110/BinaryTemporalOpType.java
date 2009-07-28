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
package org.geotoolkit.ogc.xml.v110;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractTimeComplexType;
import org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType;
import org.geotoolkit.gml.xml.v311.AbstractTimeObjectType;
import org.geotoolkit.gml.xml.v311.AbstractTimePrimitiveType;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;


/**
 * <p>Java class for BinaryTemporalOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryTemporalOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}TemporalOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *           &lt;element ref="{http://www.opengis.net/gml}AbstractTimeObject"/>
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
@XmlType(name = "BinaryTemporalOpType", propOrder = {
    "propertyName",
    "rest"
})
public class BinaryTemporalOpType extends TemporalOpsType {

    @XmlElements({
        //@XmlElement(name = "TimeTopologyComplex", namespace = "http://www.opengis.net/gml", type = TimeTopologyComplexType.class),
        @XmlElement(name = "AbstractTimeGeometricPrimitive", namespace = "http://www.opengis.net/gml", type = AbstractTimeGeometricPrimitiveType.class),
        //@XmlElement(name = "TimeEdge", namespace = "http://www.opengis.net/gml", type = TimeEdgeType.class),
        @XmlElement(name = "AbstractTimeObject", namespace = "http://www.opengis.net/gml", type = AbstractTimeObjectType.class),
        @XmlElement(name = "TimePeriod", namespace = "http://www.opengis.net/gml", type = TimePeriodType.class),
        @XmlElement(name = "TimeInstant", namespace = "http://www.opengis.net/gml", type = TimeInstantType.class),
        //@XmlElement(name = "PropertyName", type = String.class)
        @XmlElement(name = "AbstractTimeComplex", namespace = "http://www.opengis.net/gml", type = AbstractTimeComplexType.class),
        @XmlElement(name = "AbstractTimePrimitive", namespace = "http://www.opengis.net/gml", type = AbstractTimePrimitiveType.class)
        //@XmlElement(name = "AbstractTimeTopologyPrimitive", namespace = "http://www.opengis.net/gml", type = AbstractTimeTopologyPrimitiveType.class)
        //@XmlElement(name = "TimeNode", namespace = "http://www.opengis.net/gml", type = TimeNodeType.class)
    })
    private List<Object> rest;

    @XmlElement(name = "PropertyName", type = String.class)
    private String propertyName;

    /**
     * Empty contructor used by JAXB
     */
    public BinaryTemporalOpType(){
        
    }
    
    /**
     * Build a new temporal operator with the specified objects.
     */
    public BinaryTemporalOpType(Object... elements){
        rest = new ArrayList<Object>();
        for (Object obj: elements){
            rest.add(obj);
        }
    }
    
    /**
     * Gets the value of the abstractTimeObjectOrAbstractTimePrimitiveOrAbstractTimeTopologyPrimitive property.
     */
    public List<Object> getRest() {
        if (rest == null) {
            rest = new ArrayList<Object>();
        }
        return Collections.unmodifiableList(rest);
    }

    /**
     * @return the propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @param propertyName the propertyName to set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

}

