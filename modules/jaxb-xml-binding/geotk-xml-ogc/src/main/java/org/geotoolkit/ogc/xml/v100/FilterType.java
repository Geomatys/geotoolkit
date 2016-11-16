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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;


/**
 * <p>Java class for FilterType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FilterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/ogc}spatialOps"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}comparisonOps"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}logicOps"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}FeatureId" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterType", propOrder = {
    "spatialOps",
    "comparisonOps",
    "logicOps",
    "featureId"
})
public class FilterType implements Filter, XMLFilter {

    @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends SpatialOpsType> spatialOps;
    @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends ComparisonOpsType> comparisonOps;
    @XmlElementRef(name = "logicOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends LogicOpsType> logicOps;
    @XmlElement(name = "FeatureId")
    private List<FeatureIdType> featureId;

    @XmlTransient
    private Map<String, String> prefixMapping;
    
    @XmlTransient
    private static ObjectFactory FACTORY = new ObjectFactory();
    
    /**
     * An empty constructor used by JAXB
     */
    public FilterType() {
        
    }
    
    /**
     * build a new FilterType with the specified logical operator
     */
    public FilterType(final JAXBElement<? extends SpatialOpsType> spatialOps, final JAXBElement<? extends ComparisonOpsType> comparisonOps, JAXBElement<? extends LogicOpsType> logicOps, List<FeatureIdType> featureId) {
        this.comparisonOps = comparisonOps;
        this.featureId = featureId;
        this.logicOps = logicOps;
        this.spatialOps = spatialOps;
    }
    
    /**
     * build a new FilterType with the specified logical operator
     */
    public FilterType(final Object obj) {
        
        // comparison operator
        if (obj instanceof ComparisonOpsType) {
            this.comparisonOps = createComparisonOps((ComparisonOpsType) obj);
            
        // logical operator    
        } else if (obj instanceof LogicOpsType) {
            this.logicOps = createLogicOps((LogicOpsType) obj);
            
        // spatial operator    
        } else if (obj instanceof SpatialOpsType) {
            this.spatialOps = createSpatialOps((SpatialOpsType) obj);

        // id operator
        } else if (obj instanceof FeatureIdType) {
            this.featureId = new ArrayList<FeatureIdType>();
            this.featureId.add((FeatureIdType) obj);

        // clone    
        } else if (obj instanceof FilterType) {
            final FilterType that = (FilterType) obj;
            if (that.comparisonOps != null) {
                final ComparisonOpsType comp = that.comparisonOps.getValue().getClone();
                this.comparisonOps = createComparisonOps(comp);
            }
            if (that.logicOps != null) {
                final LogicOpsType log = that.logicOps.getValue().getClone();
                this.logicOps = createLogicOps(log);
            }
            if (that.prefixMapping != null) {
                this.prefixMapping = new HashMap<String, String>(that.prefixMapping);
            }
            if (that.spatialOps != null) {
                final SpatialOpsType spa = that.spatialOps.getValue().getClone();
                this.spatialOps = createSpatialOps(spa);
            }
            if (that.featureId != null) {
                this.featureId = new ArrayList<FeatureIdType>();
                for (FeatureIdType fid : that.featureId) {
                    this.featureId.add(new FeatureIdType(fid.getFid()));
                }
            }
            
        } else {
            throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
        }
    }
    
     public static JAXBElement<? extends ComparisonOpsType> createComparisonOps(final ComparisonOpsType operator) {
        
        if (operator instanceof PropertyIsLessThanOrEqualToType) {
            return FACTORY.createPropertyIsLessThanOrEqualTo((PropertyIsLessThanOrEqualToType) operator);
        } else if (operator instanceof PropertyIsLessThanType) {
            return FACTORY.createPropertyIsLessThan((PropertyIsLessThanType) operator);
        } else if (operator instanceof PropertyIsGreaterThanOrEqualToType) {
            return FACTORY.createPropertyIsGreaterThanOrEqualTo((PropertyIsGreaterThanOrEqualToType) operator);
        } else if (operator instanceof PropertyIsNotEqualToType) {
            return FACTORY.createPropertyIsNotEqualTo((PropertyIsNotEqualToType) operator);
        } else if (operator instanceof PropertyIsGreaterThanType) {
            return FACTORY.createPropertyIsGreaterThan((PropertyIsGreaterThanType) operator);
        } else if (operator instanceof PropertyIsEqualToType) {
            return FACTORY.createPropertyIsEqualTo((PropertyIsEqualToType) operator);
        } else if (operator instanceof PropertyIsNullType) {
            return FACTORY.createPropertyIsNull((PropertyIsNullType) operator);
        } else if (operator instanceof PropertyIsBetweenType) {
            return FACTORY.createPropertyIsBetween((PropertyIsBetweenType) operator);
        } else if (operator instanceof PropertyIsLikeType) {
            return FACTORY.createPropertyIsLike((PropertyIsLikeType) operator);
        } else if (operator instanceof ComparisonOpsType) {
            return FACTORY.createComparisonOps((ComparisonOpsType) operator);
        } else { 
            return null;
        }
    }
    
    public static JAXBElement<? extends LogicOpsType> createLogicOps(final LogicOpsType operator) {
        
        if (operator instanceof OrType) {
            return FACTORY.createOr((OrType) operator);
        } else if (operator instanceof NotType) {
            return FACTORY.createNot((NotType) operator);
        } else if (operator instanceof AndType) {
            return FACTORY.createAnd((AndType) operator);
        } else if (operator instanceof LogicOpsType) {
            return FACTORY.createLogicOps((LogicOpsType) operator);
        } else {
            return null;
        }
    }
    
    public static JAXBElement<? extends SpatialOpsType> createSpatialOps(final SpatialOpsType operator) {
        
        if (operator instanceof BeyondType) {
            return FACTORY.createBeyond((BeyondType) operator);
        } else if (operator instanceof DWithinType) {
            return FACTORY.createDWithin((DWithinType) operator);
        } else if (operator instanceof BBOXType) {
            return FACTORY.createBBOX((BBOXType) operator);
        } else if (operator instanceof ContainsType) {
            return FACTORY.createContains((ContainsType) operator);
        } else if (operator instanceof CrossesType) {
            return FACTORY.createCrosses((CrossesType) operator);
        } else if (operator instanceof DisjointType) {
            return FACTORY.createDisjoint((DisjointType) operator);
        } else if (operator instanceof EqualsType) {
            return FACTORY.createEquals((EqualsType) operator);
        } else if (operator instanceof IntersectsType) {
            return FACTORY.createIntersects((IntersectsType) operator);
        } else if (operator instanceof OverlapsType) {
            return FACTORY.createOverlaps((OverlapsType) operator);
        } else if (operator instanceof TouchesType) {
            return FACTORY.createTouches((TouchesType) operator);
        } else if (operator instanceof WithinType) {
            return FACTORY.createWithin((WithinType) operator);
        } else if (operator instanceof SpatialOpsType) {
            return FACTORY.createSpatialOps((SpatialOpsType) operator);
        } else {
            return null;
        }
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
     * Gets the value of the comparisonOps property.
     * 
     */
    public JAXBElement<? extends ComparisonOpsType> getComparisonOps() {
        return comparisonOps;
    }

    /**
     * Sets the value of the comparisonOps property.
     */
    public void setComparisonOps(final JAXBElement<? extends ComparisonOpsType> value) {
        this.comparisonOps = ((JAXBElement<? extends ComparisonOpsType> ) value);
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

    /**
     * Gets the value of the featureId property.
     * 
     */
    public List<FeatureIdType> getFeatureId() {
        if (featureId == null) {
            featureId = new ArrayList<FeatureIdType>();
        }
        return this.featureId;
    }
    
    @Override
    public Object getFilterObject() {
        if (comparisonOps != null) {
            return comparisonOps.getValue();
        } else if (featureId != null && !featureId.isEmpty()) {
            return featureId;
        } else if (logicOps != null) {
            return logicOps.getValue();
        } else if (spatialOps != null) {
            return spatialOps.getValue();
        }
        return null;
    }
    
     /**
     * @return the prefixMapping
     */
    @Override
    public Map<String, String> getPrefixMapping() {
        return prefixMapping;
    }

    /**
     * @param prefixMapping the prefixMapping to set
     */
    @Override
    public void setPrefixMapping(Map<String, String> prefixMapping) {
        this.prefixMapping = prefixMapping;
    }
    
    @Override
    public boolean evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        return extraData;
    }
}
