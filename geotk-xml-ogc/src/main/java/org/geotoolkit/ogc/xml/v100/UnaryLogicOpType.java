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
package org.geotoolkit.ogc.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.UnaryLogicOperator;


/**
 * <p>Java class for UnaryLogicOpType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UnaryLogicOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}LogicOpsType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/ogc}comparisonOps"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}spatialOps"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}logicOps"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnaryLogicOpType", propOrder = {
    "comparisonOps",
    "spatialOps",
    "logicOps"
})
public abstract class UnaryLogicOpType extends LogicOpsType implements UnaryLogicOperator {

    @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends ComparisonOpsType> comparisonOps;
    @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends SpatialOpsType> spatialOps;
    @XmlElementRef(name = "logicOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends LogicOpsType> logicOps;

    /**
     * An empty constructor used by JAXB
     */
     public UnaryLogicOpType() {
     }

     /**
      * Build a new Binary logic operator
      */
     public UnaryLogicOpType(final Object obj) {

         // comparison operator
         if (obj instanceof ComparisonOpsType) {
             this.comparisonOps = FilterType.createComparisonOps((ComparisonOpsType) obj);

         // logical operator
         } else if (obj instanceof LogicOpsType) {
             this.logicOps = FilterType.createLogicOps((LogicOpsType) obj);

         // spatial operator
         } else if (obj instanceof SpatialOpsType) {
             this.spatialOps = FilterType.createSpatialOps((SpatialOpsType) obj);

         // clone
         } else if (obj instanceof UnaryLogicOpType) {
            final UnaryLogicOpType that = (UnaryLogicOpType) obj;
            if (that.comparisonOps != null) {
                final ComparisonOpsType comp = that.comparisonOps.getValue().getClone();
                this.comparisonOps = FilterType.createComparisonOps(comp);
            }
            if (that.logicOps != null) {
                 final LogicOpsType log = that.logicOps.getValue().getClone();
                 this.logicOps = FilterType.createLogicOps(log);
            }
            if (that.spatialOps != null) {
                final SpatialOpsType spa = that.spatialOps.getValue().getClone();
                this.spatialOps = FilterType.createSpatialOps(spa);
            }
         } else {
             throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
         }
     }

    /**
     * Gets the value of the comparisonOps property.
     *
    */
    public JAXBElement<? extends ComparisonOpsType> getComparisonOps() {
        return comparisonOps;
    }

    /**
     * Sets the value of the comparisonOps property.
     *
     */
    public void setComparisonOps(final JAXBElement<? extends ComparisonOpsType> value) {
        this.comparisonOps = ((JAXBElement<? extends ComparisonOpsType> ) value);
    }

    /**
     * Gets the value of the spatialOps property.
     *
     */
    public JAXBElement<? extends SpatialOpsType> getSpatialOps() {
        return spatialOps;
    }

    /**
     * Sets the value of the spatialOps property.
     *
     */
    public void setSpatialOps(final JAXBElement<? extends SpatialOpsType> value) {
        this.spatialOps = ((JAXBElement<? extends SpatialOpsType> ) value);
    }

    /**
     * Gets the value of the logicOps property.
     *
     */
    public JAXBElement<? extends LogicOpsType> getLogicOps() {
        return logicOps;
    }

    /**
     * Sets the value of the logicOps property.
     *
     */
    public void setLogicOps(final JAXBElement<? extends LogicOpsType> value) {
        this.logicOps = ((JAXBElement<? extends LogicOpsType> ) value);
    }

    @Override
    public Object getChild() {
        if (comparisonOps != null) {
            return comparisonOps.getValue();
        } else if (logicOps != null) {
            return logicOps.getValue();
        } else if (spatialOps != null) {
            return spatialOps.getValue();
        } else {
            return null;
        }
    }
}
