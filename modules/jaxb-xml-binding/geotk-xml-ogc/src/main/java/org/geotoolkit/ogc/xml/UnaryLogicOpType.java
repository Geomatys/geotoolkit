/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
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
package org.geotoolkit.ogc.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;


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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnaryLogicOpType", propOrder = {
    "comparisonOps",
    "spatialOps",
    "logicOps"
})
public class UnaryLogicOpType extends LogicOpsType {

    @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends ComparisonOpsType> comparisonOps;
    @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends SpatialOpsType> spatialOps;
    @XmlElementRef(name = "logicOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends LogicOpsType> logicOps;

    /**
     * an transient ogc factory to build JAXBelement
     */
    @XmlTransient
    private ObjectFactory factory = new ObjectFactory();
    
    /**
     * An empty constructor used by JAXB
     */
     public UnaryLogicOpType() {
         
     }
     
     /**
      * Build a new Binary logic operator 
      */
     public UnaryLogicOpType(Object obj) {
         
         // comparison operator
         if (obj instanceof ComparisonOpsType) {
             this.comparisonOps = FilterType.createComparisonOps((ComparisonOpsType) obj);

         // logical operator    
         } else if (obj instanceof LogicOpsType) {
             this.logicOps = FilterType.createLogicOps((LogicOpsType) obj);

         // spatial operator    
         } else if (obj instanceof SpatialOpsType) {
             this.spatialOps = FilterType.createSpatialOps((SpatialOpsType) obj);

         } else {
             throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
         }
         
     }
    /**
     * Gets the value of the comparisonOps property.
     */
    public JAXBElement<? extends ComparisonOpsType> getComparisonOps() {
        return comparisonOps;
    }

    /**
     * Sets the value of the comparisonOps property.
     */
    public void setComparisonOps(JAXBElement<? extends ComparisonOpsType> comparisonOps) {
        this.comparisonOps = comparisonOps;
    }
    
    /**
     * Sets the value of the comparisonOps property.
     */
    public void setComparisonOps(ComparisonOpsType comparisonOps) {
        this.comparisonOps = FilterType.createComparisonOps(comparisonOps);
    }
    
    /**
     * Gets the value of the spatialOps property.
     */
    public JAXBElement<? extends SpatialOpsType> getSpatialOps() {
        return spatialOps;
    }
    
    /**
     * Sets the value of the spatialOps property.
     */
    public void setSpatialOps(JAXBElement<? extends SpatialOpsType> spatialOps) {
        this.spatialOps = spatialOps;
    }
    
    /**
     * Sets the value of the spatialOps property.
     */
    public void setSpatialOps(SpatialOpsType spatialOps) {
        this.spatialOps = FilterType.createSpatialOps(spatialOps);
    }

    
    /**
     * Gets the value of the logicOps property.
     */
    public JAXBElement<? extends LogicOpsType> getLogicOps() {
        return logicOps;
    }
    
        /**
     * Sets the value of the logicOps property.
     */
    public void setLogicOps(JAXBElement<? extends LogicOpsType> logicOps) {
        this.logicOps = logicOps;
    }
    
    /**
     * Sets the value of the logicOps property.
     */
    public void setLogicOps(LogicOpsType logicOps) {
        this.logicOps = FilterType.createLogicOps(logicOps);
    }
    
    /**
     * implements geoAPI interface
     * 
     * @return 
     */
    public Filter getFilter() {
        if (comparisonOps != null)
            return comparisonOps.getValue();
        else if (logicOps != null)
            return logicOps.getValue();
        else if (spatialOps != null)
            return spatialOps.getValue();
        else return null;
        
    }

     /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof UnaryLogicOpType) {
            final UnaryLogicOpType that = (UnaryLogicOpType) object;

            boolean comp = false;
            if (this.comparisonOps != null && that.comparisonOps != null) {
                comp = Utilities.equals(this.comparisonOps.getValue(), that.comparisonOps.getValue());
            } else if (this.comparisonOps == null && that.comparisonOps == null)
                comp = true;

            boolean log = false;
            if (this.logicOps != null && that.logicOps != null) {
                log = Utilities.equals(this.logicOps.getValue(), that.logicOps.getValue());
            } else if (this.logicOps == null && that.logicOps == null)
                log = true;

            boolean spa = false;
            if (this.spatialOps != null && that.spatialOps != null) {
                spa = Utilities.equals(this.spatialOps.getValue(), that.spatialOps.getValue());
            } else if (this.spatialOps == null && that.spatialOps == null) {
                spa = true;
            }
            /**
             * TODO ID
             */
            return  comp && spa && log;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.comparisonOps != null ? this.comparisonOps.hashCode() : 0);
        hash = 97 * hash + (this.spatialOps != null ? this.spatialOps.hashCode() : 0);
        hash = 97 * hash + (this.logicOps != null ? this.logicOps.hashCode() : 0);
        return hash;
    }
    
     @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n');
        if (spatialOps != null) {
            s.append("SpatialOps: ").append(spatialOps.getValue().toString()).append('\n');
        }
        if (comparisonOps != null) {
            s.append("ComparisonOps: ").append(comparisonOps.getValue().toString()).append('\n');
        }
        if (logicOps != null) {
            s.append("LogicOps: ").append(logicOps.getValue().toString()).append('\n');
        }
        return s.toString();
    }

    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(FilterVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
