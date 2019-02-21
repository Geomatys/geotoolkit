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
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.BinaryLogicOperator;
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
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryLogicOpType", propOrder = {
    "comparisonOpsOrSpatialOpsOrLogicOps"
})
public abstract class BinaryLogicOpType extends LogicOpsType implements org.opengis.filter.BinaryLogicOperator, BinaryLogicOperator {

    @XmlElementRefs({
        @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "spatialOps",    namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "logicOps",      namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> comparisonOpsOrSpatialOpsOrLogicOps;

    public BinaryLogicOpType() {

    }

    /**
      * Build a new Binary logic operator
      */
    public BinaryLogicOpType(final BinaryLogicOpType that) {
         if (that != null && that.comparisonOpsOrSpatialOpsOrLogicOps != null) {
            this.comparisonOpsOrSpatialOpsOrLogicOps = new ArrayList<>();
            final ObjectFactory factory = new ObjectFactory();
            for (JAXBElement<?> jb: that.comparisonOpsOrSpatialOpsOrLogicOps) {

                final Object obj = jb.getValue();

                // comparison operator
                if (obj instanceof ComparisonOpsType) {
                    final ComparisonOpsType co = ((ComparisonOpsType) obj).getClone();
                    this.comparisonOpsOrSpatialOpsOrLogicOps.add(FilterType.createComparisonOps(co));

                    // logical operator
                } else if (obj instanceof LogicOpsType) {
                    final LogicOpsType lo = ((LogicOpsType) obj).getClone();
                    this.comparisonOpsOrSpatialOpsOrLogicOps.add(FilterType.createLogicOps(lo));

                    // spatial operator
                } else if (obj instanceof SpatialOpsType) {
                    final SpatialOpsType so = ((SpatialOpsType) obj).getClone();
                    this.comparisonOpsOrSpatialOpsOrLogicOps.add(FilterType.createSpatialOps(so));

                    // function
                } else if (obj instanceof FunctionType) {
                    final FunctionType fu = new FunctionType((FunctionType) obj);
                    this.comparisonOpsOrSpatialOpsOrLogicOps.add(factory.createFunction(fu));

                } else {
                    throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * Build a new Binary logic operator
     */
     public BinaryLogicOpType(final Object... operators) {
         this.comparisonOpsOrSpatialOpsOrLogicOps = new ArrayList<>();
         final ObjectFactory factory = new ObjectFactory();
         for (Object obj: operators) {

             if(obj instanceof JAXBElement){
                 obj = ((JAXBElement)obj).getValue();
             }

             // comparison operator
            if (obj instanceof ComparisonOpsType)  {
                final ComparisonOpsType co = ((ComparisonOpsType)obj).getClone();
                this.comparisonOpsOrSpatialOpsOrLogicOps.add(FilterType.createComparisonOps(co));

            // logical operator
            } else if (obj instanceof LogicOpsType) {
                final LogicOpsType lo = ((LogicOpsType)obj).getClone();
                this.comparisonOpsOrSpatialOpsOrLogicOps.add(FilterType.createLogicOps(lo));

            // spatial operator
            } else if (obj instanceof SpatialOpsType) {
                final SpatialOpsType so = ((SpatialOpsType)obj).getClone();
                this.comparisonOpsOrSpatialOpsOrLogicOps.add(FilterType.createSpatialOps(so));

            // function
            } else if (obj instanceof FunctionType) {
                final FunctionType fu = new FunctionType((FunctionType)obj);
                this.comparisonOpsOrSpatialOpsOrLogicOps.add(factory.createFunction(fu));

            } else {
                throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
            }
         }
     }

    /**
     * Gets the value of the comparisonOpsOrSpatialOpsOrLogicOps property.
     *
     */
    public List<JAXBElement<?>> getComparisonOpsOrSpatialOpsOrLogicOps() {
        if (comparisonOpsOrSpatialOpsOrLogicOps == null) {
            comparisonOpsOrSpatialOpsOrLogicOps = new ArrayList<>();
        }
        return this.comparisonOpsOrSpatialOpsOrLogicOps;
    }

    @Override
    public LogicOpsType getClone() {
        throw new UnsupportedOperationException("Must be overriden by sub-cless");
    }

    @Override
    public List<Object> getFilters() {
        List<Object> result = new ArrayList<>();
        for (JAXBElement jb: getComparisonOpsOrSpatialOpsOrLogicOps()) {
            result.add(jb.getValue());
        }
        return result;
    }

    @Override
    public List<Filter> getChildren() {
        List<Filter> result = new ArrayList<>();
        for (JAXBElement jb: getComparisonOpsOrSpatialOpsOrLogicOps()) {
            result.add((Filter)jb.getValue());
        }
        return result;
    }

    @Override
    public boolean evaluate(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object accept(FilterVisitor fv, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
