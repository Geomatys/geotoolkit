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
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;


/**
 * <p>Java class for BinaryLogicOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryLogicOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}LogicOpsType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="2">
 *         &lt;element ref="{http://www.opengis.net/ogc}comparisonOps"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}spatialOps"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}logicOps"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryLogicOpType", propOrder = {
    "comparisonOps",
    "logicOps",
    "spatialOps"
})
public class BinaryLogicOpType extends LogicOpsType implements BinaryLogicOperator {

    @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private List<JAXBElement<? extends ComparisonOpsType>> comparisonOps;
    @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private List<JAXBElement<? extends SpatialOpsType>> spatialOps;
    @XmlElementRef(name = "logicOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private List<JAXBElement<? extends LogicOpsType>> logicOps;

    /**
     * An empty constructor used by JAXB
     */
     public BinaryLogicOpType() {
         
     }
     
     /**
      * Build a new Binary logic operator 
      */
     public BinaryLogicOpType(final Object... operators) {
         this.comparisonOps = new ArrayList<JAXBElement<? extends ComparisonOpsType>>();
         this.logicOps      = new ArrayList<JAXBElement<? extends LogicOpsType>>();
         this.spatialOps    = new ArrayList<JAXBElement<? extends SpatialOpsType>>();
         
         for (Object obj: operators) {

             if(obj instanceof JAXBElement){
                 obj = ((JAXBElement)obj).getValue();
             }

             // comparison operator
             if (obj instanceof ComparisonOpsType)  {
                 this.comparisonOps.add(FilterType.createComparisonOps((ComparisonOpsType)obj));
                 
             // logical operator    
             } else if (obj instanceof LogicOpsType) {
                 this.logicOps.add(FilterType.createLogicOps((LogicOpsType)obj));
             
             // spatial operator    
             } else if (obj instanceof SpatialOpsType) {
                 this.spatialOps.add(FilterType.createSpatialOps((SpatialOpsType) obj));
             
             } else {
                 throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
             }
         }
         
     }
     
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        int i = 0;
        if (getComparisonOps() != null) {
            s.append("comparisonOps: ").append('\n');
            for (JAXBElement<?> jb: getComparisonOps()) {
                s.append(i).append(": ").append(jb.getValue().toString()).append('\n');
                i++;        
            }
        }
        if (getLogicOps() != null) {
            s.append("logicOps: ").append('\n');
            for (JAXBElement<?> jb: getLogicOps()) {
                s.append(i).append(": ").append(jb.getValue().toString()).append('\n');
                i++;        
            }
        }
        if (getSpatialOps() != null) {
            s.append("spatialOps: ").append('\n');
            for (JAXBElement<?> jb: getSpatialOps()) {
                s.append(i).append(": ").append(jb.getValue().toString()).append('\n');
                i++;        
            }
        }
        return s.toString();
    }

    public List<Filter> getChildren() {
        List<Filter> result = new ArrayList<Filter>();
        for (JAXBElement jb: getComparisonOps()) {
            result.add((Filter)jb.getValue());
        }
        for (JAXBElement jb: getLogicOps()) {
            result.add((Filter)jb.getValue());
        }
        for (JAXBElement jb: getSpatialOps()) {
            result.add((Filter)jb.getValue());
        }
        return result;
    }

    public boolean evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(final FilterVisitor visitor, final Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<JAXBElement<? extends ComparisonOpsType>> getComparisonOps() {
        if (this.comparisonOps == null) {
            this.comparisonOps = new ArrayList<JAXBElement<? extends ComparisonOpsType>>();
        }
        return comparisonOps;
    }

    public void setComparisonOps(final List<JAXBElement<? extends ComparisonOpsType>> comparisonOps) {
        this.comparisonOps = comparisonOps;
    }
    
    public void setComparisonOps(final ComparisonOpsType comparisonOp) {
        if (this.comparisonOps == null) {
            this.comparisonOps = new ArrayList<JAXBElement<? extends ComparisonOpsType>>();
        }
        this.comparisonOps.add(FilterType.createComparisonOps(comparisonOp));
    }

    public List<JAXBElement<? extends SpatialOpsType>> getSpatialOps() {
        if (this.spatialOps == null) {
            this.spatialOps = new ArrayList<JAXBElement<? extends SpatialOpsType>>();
        }
        return spatialOps;
    }

    public void setSpatialOps(final List<JAXBElement<? extends SpatialOpsType>> spatialOps) {
        this.spatialOps = spatialOps;
    }
    
    public void setSPatialOps(final SpatialOpsType spatialOp) {
        if (this.spatialOps == null) {
            this.spatialOps = new ArrayList<JAXBElement<? extends SpatialOpsType>>();
        }
        this.spatialOps.add(FilterType.createSpatialOps(spatialOp));
    }
    
    public List<JAXBElement<? extends LogicOpsType>> getLogicOps() {
        if (this.logicOps == null) {
            this.logicOps = new ArrayList<JAXBElement<? extends LogicOpsType>>();
        }
        return logicOps;
    }

    public void setLogicOps(final List<JAXBElement<? extends LogicOpsType>> logicOps) {
        this.logicOps = logicOps;
    }
    
    public void setLogicOps(final LogicOpsType logicOp) {
        if (this.logicOps == null) {
            this.logicOps = new ArrayList<JAXBElement<? extends LogicOpsType>>();
        }
        this.logicOps.add(FilterType.createLogicOps(logicOp));
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BinaryLogicOpType) {
            BinaryLogicOpType that = (BinaryLogicOpType) obj;
            boolean logic = false;
            if (this.logicOps == null && that.logicOps == null) {
                logic = true;
            } else if (this.logicOps != null && that.logicOps != null) {
                if (this.logicOps.size() == that.logicOps.size()) {
                    for (int i = 0; i < this.logicOps.size(); i++) {
                        Object thisExp = ((JAXBElement)this.logicOps.get(i)).getValue();
                        Object thatExp = ((JAXBElement)that.logicOps.get(i)).getValue();
                        if (!Utilities.equals(thisExp, thatExp)) {
                            return false;
                        }
                    }
                    logic = true;
                }
            }
            boolean compa = false;
            if (this.comparisonOps == null && that.comparisonOps == null) {
                compa = true;
            } else if (this.comparisonOps != null && that.comparisonOps != null) {
                if (this.comparisonOps.size() == that.comparisonOps.size()) {
                    for (int i = 0; i < this.comparisonOps.size(); i++) {
                        Object thisExp = ((JAXBElement)this.comparisonOps.get(i)).getValue();
                        Object thatExp = ((JAXBElement)that.comparisonOps.get(i)).getValue();
                        if (!Utilities.equals(thisExp, thatExp)) {
                            return false;
                        }
                    }
                    compa = true;
                }
            }
            boolean spa = false;
            if (this.spatialOps == null && that.spatialOps == null) {
                spa = true;
            } else if (this.spatialOps != null && that.spatialOps != null) {
                if (this.spatialOps.size() == that.spatialOps.size()) {
                    for (int i = 0; i < this.spatialOps.size(); i++) {
                        Object thisExp = ((JAXBElement)this.spatialOps.get(i)).getValue();
                        Object thatExp = ((JAXBElement)that.spatialOps.get(i)).getValue();
                        if (!Utilities.equals(thisExp, thatExp)) {
                            return false;
                        }
                    }
                    spa = true;
                }
            }
            return logic && compa && spa;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.comparisonOps != null ? this.comparisonOps.hashCode() : 0);
        hash = 37 * hash + (this.spatialOps != null ? this.spatialOps.hashCode() : 0);
        hash = 37 * hash + (this.logicOps != null ? this.logicOps.hashCode() : 0);
        return hash;
    }
}
