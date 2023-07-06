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
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractTimeComplexType;
import org.geotoolkit.gml.xml.v311.AbstractTimeObjectType;
import org.geotoolkit.gml.xml.v311.AbstractTimePrimitiveType;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.opengis.filter.Expression;
import org.opengis.filter.TemporalOperator;
import org.opengis.filter.TemporalOperatorName;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryTemporalOpType", propOrder = {
    "propertyName",
    "rest"
})
public class BinaryTemporalOpType extends TemporalOpsType implements TemporalOperator {

    @XmlElements({
        //@XmlElement(name = "TimeTopologyComplex", namespace = "http://www.opengis.net/gml", type = TimeTopologyComplexType.class),
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
    public BinaryTemporalOpType(final String propertyName, final Object... elements){
        this.propertyName = propertyName;
        rest = new ArrayList<Object>();
        for (Object obj: elements){
            rest.add(obj);
        }
    }

    public BinaryTemporalOpType(final BinaryTemporalOpType that) {
        if (that != null) {
            this.propertyName = that.propertyName;
            for (Object o : that.getRest()) {
                if (o instanceof TimePeriodType) {
                    final TimePeriodType tm = (TimePeriodType)o;
                    this.rest.add(new TimePeriodType(tm.getId(), tm.getBeginPosition(), tm.getEndPosition()));
                } else if (o instanceof TimeInstantType) {
                    final TimeInstantType tm = (TimeInstantType)o;
                    this.rest.add(new TimeInstantType(tm.getId(), tm.getTimePosition()));
                } else {
                    throw new IllegalArgumentException("unexpected litteral type:" + o.getClass().getName());
                }
            }
        }
    }

    /**
     * Gets the value of the rest property.
     */
    public List<Object> getRest() {
        if (rest == null) {
            rest = new ArrayList<Object>();
        }
        return rest;
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
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (propertyName != null)  {
            sb.append("propertyName:").append(propertyName).append('\n');
        }
        if (rest != null) {
            sb.append("rest:\n");
            for (Object obj : rest) {
                sb.append(obj).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public List getExpressions() {
        return Arrays.asList(getExpression1(), getExpression2());
    }

    public Expression getExpression1() {
        return new PropertyNameType(propertyName);
    }

    public Expression getExpression2() {
        if (rest != null && !rest.isEmpty()) {
            return (Expression) rest.get(0);
        }
        return null;
    }

    @Override
    public TemporalOpsType getClone() {
        throw new UnsupportedOperationException("Must be overriden by sub-class");
    }

    @Override
    public TemporalOperatorName getOperatorType() {
        throw new UnsupportedOperationException("Must be overriden by sub-class");
    }
}

