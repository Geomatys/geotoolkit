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
package org.geotoolkit.ogc.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryLogicOpType", propOrder = {
    "comparisonOps",
    "logicOps",
    "spatialOps"
})
public class BinaryLogicOpType extends LogicOpsType {

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
     public BinaryLogicOpType(Object... operators) {
         this.comparisonOps = new ArrayList<JAXBElement<? extends ComparisonOpsType>>();
         this.logicOps      = new ArrayList<JAXBElement<? extends LogicOpsType>>();
         this.spatialOps    = new ArrayList<JAXBElement<? extends SpatialOpsType>>();
         
         for (Object obj: operators) {
             
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

    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(FilterVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<JAXBElement<? extends ComparisonOpsType>> getComparisonOps() {
        if (this.comparisonOps == null) {
            this.comparisonOps = new ArrayList<JAXBElement<? extends ComparisonOpsType>>();
        }
        return comparisonOps;
    }

    public void setComparisonOps(List<JAXBElement<? extends ComparisonOpsType>> comparisonOps) {
        this.comparisonOps = comparisonOps;
    }
    
    public void setComparisonOps(ComparisonOpsType comparisonOp) {
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

    public void setSpatialOps(List<JAXBElement<? extends SpatialOpsType>> spatialOps) {
        this.spatialOps = spatialOps;
    }
    
    public void setSPatialOps(SpatialOpsType spatialOp) {
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

    public void setLogicOps(List<JAXBElement<? extends LogicOpsType>> logicOps) {
        this.logicOps = logicOps;
    }
    
    public void setLogicOps(LogicOpsType logicOp) {
        if (this.logicOps == null) {
            this.logicOps = new ArrayList<JAXBElement<? extends LogicOpsType>>();
        }
        this.logicOps.add(FilterType.createLogicOps(logicOp));
    }
}