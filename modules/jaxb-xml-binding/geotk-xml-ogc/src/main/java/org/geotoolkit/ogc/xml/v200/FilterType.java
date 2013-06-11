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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.geotoolkit.util.Utilities;
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
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}AbstractSelectionClauseType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/fes/2.0}FilterPredicates"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterType", propOrder = {
    "comparisonOps",
    "spatialOps",
    "temporalOps",
    "logicOps",
    "extensionOps",
    "function",
    "id"
})
@XmlRootElement(name="Filter")
public class FilterType extends AbstractSelectionClauseType implements Filter, XMLFilter {

    @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<? extends ComparisonOpsType> comparisonOps;
    @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<? extends SpatialOpsType> spatialOps;
    @XmlElementRef(name = "temporalOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<? extends TemporalOpsType> temporalOps;
    @XmlElementRef(name = "logicOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<? extends LogicOpsType> logicOps;
    private ExtensionOpsType extensionOps;
    @XmlElement(name = "Function")
    private FunctionType function;
    @XmlElementRef(name = "_Id", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractIdType>> id;

    @XmlTransient
    private Map<String, String> prefixMapping;

    /**
     * a transient factory to build JAXBelement
     */
    @XmlTransient
    private static ObjectFactory FACTORY = new ObjectFactory();

    public FilterType() {

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

        // spatial operator
        } else if (obj instanceof TemporalOpsType) {
            this.temporalOps = createTemporalOps((TemporalOpsType) obj);

        // id operator
        } else if (obj instanceof AbstractIdType) {
            this.id = new ArrayList<JAXBElement<? extends AbstractIdType>>();
            this.id.add(createIdOps((AbstractIdType) obj));

        // clone    
        } else if (obj instanceof FilterType) {
            final FilterType that = (FilterType) obj;
            if (that.comparisonOps != null) {
                final ComparisonOpsType comp = that.comparisonOps.getValue().getClone();
                this.comparisonOps = createComparisonOps(comp);
            }
            if (that.extensionOps != null) {
                this.extensionOps = that.extensionOps.getClone();
            }
            if (that.function != null) {
                this.function = new FunctionType(that.function);
            }
            if (that.id != null) {
                this.id = new ArrayList<JAXBElement<? extends AbstractIdType>>();
                for (JAXBElement<? extends AbstractIdType> jb : that.id) {
                    AbstractIdType aid = jb.getValue();
                    if (aid instanceof ResourceIdType) {
                        final ResourceIdType raid = (ResourceIdType) aid;
                        this.id.add(FACTORY.createResourceId(new ResourceIdType(raid)));
                    } else {
                        throw new IllegalArgumentException("exexpected ID type in filter:" + aid.getClass().getName());
                    }
                }
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
            if (that.temporalOps != null) {
                final TemporalOpsType temp = that.temporalOps.getValue().getClone();
                this.temporalOps = createTemporalOps(temp);
            }
        } else { 
            throw new IllegalArgumentException("This kind of object is not allowed:" + obj.getClass().getSimpleName());
        }
    }

    /**
     * Gets the value of the comparisonOps property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PropertyIsBetweenType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComparisonOpsType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyIsLikeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyIsNilType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyIsNullType }{@code >}
     *
     */
    public JAXBElement<? extends ComparisonOpsType> getComparisonOps() {
        return comparisonOps;
    }

    /**
     * Sets the value of the comparisonOps property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PropertyIsBetweenType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComparisonOpsType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyIsLikeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyIsNilType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyIsNullType }{@code >}
     *
     */
    public void setComparisonOps(JAXBElement<? extends ComparisonOpsType> value) {
        this.comparisonOps = ((JAXBElement<? extends ComparisonOpsType> ) value);
    }

    /**
     * Gets the value of the spatialOps property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BBOXType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     *
     */
    public JAXBElement<? extends SpatialOpsType> getSpatialOps() {
        return spatialOps;
    }

    /**
     * Sets the value of the spatialOps property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BBOXType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     *
     */
    public void setSpatialOps(JAXBElement<? extends SpatialOpsType> value) {
        this.spatialOps = ((JAXBElement<? extends SpatialOpsType> ) value);
    }

    /**
     * Gets the value of the temporalOps property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TemporalOpsType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *
     */
    public JAXBElement<? extends TemporalOpsType> getTemporalOps() {
        return temporalOps;
    }

    /**
     * Sets the value of the temporalOps property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TemporalOpsType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
     *
     */
    public void setTemporalOps(JAXBElement<? extends TemporalOpsType> value) {
        this.temporalOps = ((JAXBElement<? extends TemporalOpsType> ) value);
    }

    /**
     * Gets the value of the logicOps property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LogicOpsType }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryLogicOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}
     *
     */
    public JAXBElement<? extends LogicOpsType> getLogicOps() {
        return logicOps;
    }

    /**
     * Sets the value of the logicOps property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LogicOpsType }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryLogicOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}
     *
     */
    public void setLogicOps(JAXBElement<? extends LogicOpsType> value) {
        this.logicOps = ((JAXBElement<? extends LogicOpsType> ) value);
    }

    /**
     * Gets the value of the extensionOps property.
     *
     * @return
     *     possible object is
     *     {@link ExtensionOpsType }
     *
     */
    public ExtensionOpsType getExtensionOps() {
        return extensionOps;
    }

    /**
     * Sets the value of the extensionOps property.
     *
     * @param value
     *     allowed object is
     *     {@link ExtensionOpsType }
     *
     */
    public void setExtensionOps(ExtensionOpsType value) {
        this.extensionOps = value;
    }

    /**
     * Gets the value of the function property.
     *
     * @return
     *     possible object is
     *     {@link FunctionType }
     *
     */
    public FunctionType getFunction() {
        return function;
    }

    /**
     * Sets the value of the function property.
     *
     * @param value
     *     allowed object is
     *     {@link FunctionType }
     *
     */
    public void setFunction(FunctionType value) {
        this.function = value;
    }

    /**
     * Gets the value of the id property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ResourceIdType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractIdType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends AbstractIdType>> getId() {
        if (id == null) {
            id = new ArrayList<JAXBElement<? extends AbstractIdType>>();
        }
        return this.id;
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

    public static JAXBElement<? extends TemporalOpsType> createTemporalOps(final TemporalOpsType operator) {

        if (operator instanceof TimeAfterType) {
            return FACTORY.createAfter((TimeAfterType) operator);
        } else if (operator instanceof TimeAnyInteractsType) {
            return FACTORY.createAnyInteracts((TimeAnyInteractsType) operator);
        } else if (operator instanceof TimeBeforeType) {
            return FACTORY.createBefore((TimeBeforeType) operator);
        } else if (operator instanceof TimeBeginsType) {
            return FACTORY.createBegins((TimeBeginsType) operator);
        } else if (operator instanceof TimeBegunByType) {
            return FACTORY.createBegunBy((TimeBegunByType) operator);
        } else if (operator instanceof TimeDuringType) {
            return FACTORY.createDuring((TimeDuringType) operator);
        } else if (operator instanceof TimeEndedByType) {
            return FACTORY.createEndedBy((TimeEndedByType) operator);
        } else if (operator instanceof TimeEndsType) {
            return FACTORY.createEnds((TimeEndsType) operator);
        } else if (operator instanceof TimeMeetsType) {
            return FACTORY.createMeets((TimeMeetsType) operator);
        } else if (operator instanceof TimeMetByType) {
            return FACTORY.createMetBy((TimeMetByType) operator);
        } else if (operator instanceof TimeOverlappedByType) {
            return FACTORY.createOverlappedBy((TimeOverlappedByType) operator);
        } else if (operator instanceof TimeContainsType) {
            return FACTORY.createTContains((TimeContainsType) operator);
        } else if (operator instanceof TimeEqualsType) {
            return FACTORY.createTEquals((TimeEqualsType) operator);
        } else if (operator instanceof TimeOverlapsType) {
            return FACTORY.createTOverlaps((TimeOverlapsType) operator);
        } else {
            return null;
        }
    }

    public static JAXBElement<? extends AbstractIdType> createIdOps(final AbstractIdType operator) {

        if (operator instanceof ResourceIdType) {
            return FACTORY.createResourceId((ResourceIdType) operator);
        } else {
            return FACTORY.createId(operator);
        }
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
    public Object getFilterObject() {
        if (comparisonOps != null) {
            return comparisonOps.getValue();
        } else if (id != null && !id.isEmpty()) {
            final List<AbstractIdType> featureId = new ArrayList<AbstractIdType>();
            for (JAXBElement<? extends AbstractIdType> jb : id) {
                featureId.add(jb.getValue());
            }
            return featureId;
        } else if (logicOps != null) {
            return logicOps.getValue();
        } else if (spatialOps != null) {
            return spatialOps.getValue();
        } else if (extensionOps != null) {
            return extensionOps;
        } else if (function != null) {
            return function;
        } else if (temporalOps != null) {
            return temporalOps.getValue();
        }
        return null;
    }

    @Override
    public boolean evaluate(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(FilterVisitor fv, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']').append('\n');
        if (spatialOps != null) {
            s.append("SpatialOps: ").append(spatialOps.getValue()).append('\n');
        }
        if (comparisonOps != null) {
            s.append("ComparisonOps: ").append(comparisonOps.getValue()).append('\n');
        }
        if (logicOps != null) {
            s.append("LogicOps: ").append(logicOps.getValue()).append('\n');
        }
        if (temporalOps != null) {
            s.append("temporalOps: ").append(temporalOps.getValue()).append('\n');
        }
        if (extensionOps != null) {
            s.append("extensionOps: ").append(extensionOps).append('\n');
        }
        if (function != null) {
            s.append("function: ").append(function).append('\n');
        }
        if (id != null) {
            s.append("id:").append('\n');
            int i = 0;
            for (JAXBElement<? extends AbstractIdType> jb: id) {
                s.append("id ").append(i).append(": ").append(jb.getValue()).append('\n');
                i++;
            }
        }
        return s.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof FilterType) {
            final FilterType that = (FilterType) object;

            boolean comp = false;
            if (this.comparisonOps != null && that.comparisonOps != null) {
                comp = Objects.equals(this.comparisonOps.getValue(), that.comparisonOps.getValue());
            } else if (this.comparisonOps == null && that.comparisonOps == null) {
                comp = true;
            }

            boolean log = false;
            if (this.logicOps != null && that.logicOps != null) {
                log = Objects.equals(this.logicOps.getValue(), that.logicOps.getValue());
            } else if (this.logicOps == null && that.logicOps == null) {
                log = true;
            }

            boolean spa = false;
            if (this.spatialOps != null && that.spatialOps != null) {
                spa = Objects.equals(this.spatialOps.getValue(), that.spatialOps.getValue());
            } else if (this.spatialOps == null && that.spatialOps == null) {
                spa = true;
            }
            boolean temp = false;
            if (this.temporalOps != null && that.temporalOps != null) {
                temp = Objects.equals(this.temporalOps.getValue(), that.temporalOps.getValue());
            } else if (this.temporalOps == null && that.temporalOps == null) {
                temp = true;
            }
            /**
             * TODO ID
             */
            return  comp && spa && log && temp &&
                    Objects.equals(this.extensionOps, that.extensionOps) &&
                    Objects.equals(this.function,     that.function);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.spatialOps != null ? this.spatialOps.hashCode() : 0);
        hash = 29 * hash + (this.comparisonOps != null ? this.comparisonOps.hashCode() : 0);
        hash = 29 * hash + (this.logicOps != null ? this.logicOps.hashCode() : 0);
        hash = 29 * hash + (this.temporalOps != null ? this.temporalOps.hashCode() : 0);
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
