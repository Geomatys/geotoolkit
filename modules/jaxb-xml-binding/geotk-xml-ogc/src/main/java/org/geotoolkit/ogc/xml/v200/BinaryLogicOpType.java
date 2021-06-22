/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.Filter;


/**
 * <p>Java class for BinaryLogicOpType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BinaryLogicOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}LogicOpsType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="2">
 *         &lt;group ref="{http://www.opengis.net/fes/2.0}FilterPredicates"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryLogicOpType", propOrder = {
    "comparisonOpsOrSpatialOpsOrTemporalOps"
})
public abstract class BinaryLogicOpType extends LogicOpsType implements org.geotoolkit.ogc.xml.BinaryLogicOperator {

    @XmlElementRefs({
        @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "logicOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "_Id", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "Function", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "extensionOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "temporalOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> comparisonOpsOrSpatialOpsOrTemporalOps;

    public BinaryLogicOpType() {
    }

    /**
     * Build a new Binary logic operator
     */
     public BinaryLogicOpType(final Object... operators) {
         this.comparisonOpsOrSpatialOpsOrTemporalOps = new ArrayList<>();

         for (Object obj: operators) {

             if(obj instanceof JAXBElement){
                 obj = ((JAXBElement)obj).getValue();
             }

             // comparison operator
             if (obj instanceof ComparisonOpsType)  {
                 this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createComparisonOps((ComparisonOpsType)obj));

             // logical operator
             } else if (obj instanceof LogicOpsType) {
                 this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createLogicOps((LogicOpsType)obj));

             // spatial operator
             } else if (obj instanceof SpatialOpsType) {
                 this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createSpatialOps((SpatialOpsType) obj));

             // ID
             } else if (obj instanceof AbstractIdType) {
                 this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createIdOps((AbstractIdType) obj));

             // temporal
             } else if (obj instanceof TemporalOpsType) {
                 this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createTemporalOps((TemporalOpsType) obj));

             // filter
             } else if (obj instanceof FilterType) {
                 treatFilter((FilterType)obj);

             } else {
                 throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
             }
         }
     }

     private void treatFilter(final FilterType filter) {
         if (filter.getComparisonOps() != null) {
            this.comparisonOpsOrSpatialOpsOrTemporalOps.add(filter.getComparisonOps());
         }
         if (filter.getLogicOps() != null) {
            this.comparisonOpsOrSpatialOpsOrTemporalOps.add(filter.getLogicOps());
         }
         if (filter.getSpatialOps() != null) {
            this.comparisonOpsOrSpatialOpsOrTemporalOps.add(filter.getSpatialOps());
         }
         if (filter.getTemporalOps()!= null) {
            this.comparisonOpsOrSpatialOpsOrTemporalOps.add(filter.getTemporalOps());
         }
    }

     /**
      * Build a new Binary logic operator
      */
     public BinaryLogicOpType(final BinaryLogicOpType that) {
         if (that != null && that.comparisonOpsOrSpatialOpsOrTemporalOps != null) {
            this.comparisonOpsOrSpatialOpsOrTemporalOps = new ArrayList<JAXBElement<?>>();
            final ObjectFactory factory = new ObjectFactory();
            for (JAXBElement<?> jb: that.comparisonOpsOrSpatialOpsOrTemporalOps) {

                final Object obj = jb.getValue();

                // comparison operator
                if (obj instanceof ComparisonOpsType)  {
                    final ComparisonOpsType co = ((ComparisonOpsType)obj).getClone();
                    this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createComparisonOps(co));

                // logical operator
                } else if (obj instanceof LogicOpsType) {
                    final LogicOpsType lo = ((LogicOpsType)obj).getClone();
                    this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createLogicOps(lo));

                // spatial operator
                } else if (obj instanceof SpatialOpsType) {
                    final SpatialOpsType so = ((SpatialOpsType)obj).getClone();
                    this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createSpatialOps(so));

                // temporal operator
                } else if (obj instanceof TemporalOpsType) {
                    final TemporalOpsType to = ((TemporalOpsType)obj).getClone();
                    this.comparisonOpsOrSpatialOpsOrTemporalOps.add(FilterType.createTemporalOps(to));

                // function
                } else if (obj instanceof FunctionType) {
                    final FunctionType fu = new FunctionType((FunctionType)obj);
                    this.comparisonOpsOrSpatialOpsOrTemporalOps.add(factory.createFunction(fu));

                // extension
                } else if (obj instanceof ExtensionOpsType) {
                    final ExtensionOpsType ext = ((ExtensionOpsType)obj).getClone();
                    this.comparisonOpsOrSpatialOpsOrTemporalOps.add(factory.createExtensionOps(ext));

                // id
                } else if (obj instanceof ResourceIdType) {
                    final ResourceIdType rid =  new ResourceIdType((ResourceIdType)obj);
                    this.comparisonOpsOrSpatialOpsOrTemporalOps.add(factory.createResourceId(rid));

                } else {
                    throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
                }
            }
         }
     }

    /**
     * Gets the value of the comparisonOpsOrSpatialOpsOrTemporalOps property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the comparisonOpsOrSpatialOpsOrTemporalOps property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComparisonOpsOrSpatialOpsOrTemporalOps().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyIsLikeType }{@code >}
     * {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     * {@link JAXBElement }{@code <}{@link LogicOpsType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractIdType }{@code >}
     * {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyIsNilType }{@code >}
     * {@link JAXBElement }{@code <}{@link TemporalOpsType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link ComparisonOpsType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link ExtensionOpsType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyIsNullType }{@code >}
     * {@link JAXBElement }{@code <}{@link ResourceIdType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyIsBetweenType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BBOXType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryLogicOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     */
    public List<JAXBElement<?>> getComparisonOpsOrSpatialOpsOrTemporalOps() {
        if (comparisonOpsOrSpatialOpsOrTemporalOps == null) {
            comparisonOpsOrSpatialOpsOrTemporalOps = new ArrayList<>();
        }
        return this.comparisonOpsOrSpatialOpsOrTemporalOps;
    }

    @Override
    public List<Filter> getOperands() {
        List<Filter> result = new ArrayList<>();
        for (JAXBElement jb: getComparisonOpsOrSpatialOpsOrTemporalOps()) {
            result.add((Filter)jb.getValue());
        }
        return result;
    }

    @Override
    public List<Object> getFilters() {
        List<Object> result = new ArrayList<>();
        for (JAXBElement jb: getComparisonOpsOrSpatialOpsOrTemporalOps()) {
            result.add(jb.getValue());
        }
        return result;
    }

    public List<JAXBElement<?>> getLogicOps() {
        return getComparisonOpsOrSpatialOpsOrTemporalOps();
    }
}
