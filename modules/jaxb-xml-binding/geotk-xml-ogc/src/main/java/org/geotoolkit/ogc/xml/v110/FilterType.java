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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.opengis.filter.Filter;
import org.opengis.util.CodeList;


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
 *         &lt;element ref="{http://www.opengis.net/ogc}_Id" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterType", propOrder = {
    "spatialOps",
    "comparisonOps",
    "logicOps",
    "temporalOps",
    "id"
})
@XmlRootElement(name = "Filter")
public class FilterType implements Filter, XMLFilter {

    @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends SpatialOpsType> spatialOps;
    @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends ComparisonOpsType> comparisonOps;
    @XmlElementRef(name = "logicOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends LogicOpsType> logicOps;
    @XmlElementRef(name = "temporalOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<? extends TemporalOpsType> temporalOps;
    @XmlElementRef(name = "_Id", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractIdType>> id;

    @XmlTransient
    private Map<String, String> prefixMapping;

    /**
     * a transient factory to build JAXBelement
     */
    @XmlTransient
    private static ObjectFactory FACTORY = new ObjectFactory();

    /**
     * An empty constructor used by JAXB
     */
    public FilterType() {
    }

    /**
     * build a new FilterType
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

        // temporal operator
        } else if (obj instanceof TemporalOpsType) {
            this.temporalOps = createTemporalOps((TemporalOpsType) obj);

        // id operator
        } else if (obj instanceof AbstractIdType) {
            this.id = new ArrayList<>();
            this.id.add(createIdOps((AbstractIdType) obj));

        // clone
        } else if (obj instanceof FilterType) {
            final FilterType that = (FilterType) obj;
            if (that.comparisonOps != null) {
                final ComparisonOpsType comp = that.comparisonOps.getValue().getClone();
                this.comparisonOps = createComparisonOps(comp);
            }
            if (that.id != null) {
                this.id = new ArrayList<>();
                for (JAXBElement<? extends AbstractIdType> jb : that.id) {
                    AbstractIdType aid = jb.getValue();
                    if (aid instanceof FeatureIdType) {
                        final FeatureIdType raid = (FeatureIdType) aid;
                        this.id.add(FACTORY.createFeatureId(new FeatureIdType(raid)));
                    } else if (aid instanceof GmlObjectIdType) {
                        final GmlObjectIdType raid = (GmlObjectIdType) aid;
                        this.id.add(FACTORY.createGmlObjectId(new GmlObjectIdType(raid)));
                    } else if (aid != null) {
                        throw new IllegalArgumentException("Unexpected ID type in filter: " + aid.getClass().getName());
                    } else {
                        throw new NullArgumentException("ID Filter object must be specified");
                    }
                }
            }
            if (that.logicOps != null) {
                final LogicOpsType log = that.logicOps.getValue().getClone();
                this.logicOps = createLogicOps(log);
            }
            if (that.prefixMapping != null) {
                this.prefixMapping = new HashMap<>(that.prefixMapping);
            }
            if (that.spatialOps != null) {
                final SpatialOpsType spa = that.spatialOps.getValue().getClone();
                this.spatialOps = createSpatialOps(spa);
            }
        } else if (obj != null) {
            throw new IllegalArgumentException("This kind of object is not allowed: " + obj.getClass().getName());
        } else {
            throw new NullArgumentException("Filter object must be specified");
        }
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
    public void setSpatialOps(final JAXBElement<? extends SpatialOpsType> spatialOps) {
        this.spatialOps = spatialOps;
    }

    /**
     * Sets the value of the spatialOps property.
     */
    public void setSpatialOps(final SpatialOpsType spatialOps) {
        this.spatialOps = createSpatialOps(spatialOps);
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
    public void setComparisonOps(final JAXBElement<? extends ComparisonOpsType> comparisonOps) {
        this.comparisonOps = comparisonOps;
    }

    /**
     * Sets the value of the comparisonOps property.
     */
    public void setComparisonOps(final ComparisonOpsType comparisonOps) {
        this.comparisonOps = createComparisonOps(comparisonOps);
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
    public void setLogicOps(final JAXBElement<? extends LogicOpsType> logicOps) {
        this.logicOps = logicOps;
    }

    /**
     * Sets the value of the logicOps property.
     */
    public void setLogicOps(final LogicOpsType logicOps) {
        this.logicOps = createLogicOps(logicOps);
    }

    /**
     * Gets the value of the logicOps property.
     */
    public JAXBElement<? extends TemporalOpsType> getTemporalOps() {
        return temporalOps;
    }

    /**
     * Sets the value of the TemporalOps property.
     */
    public void setTemporalOps(final JAXBElement<? extends TemporalOpsType> temporalOps) {
        this.temporalOps = temporalOps;
    }

    /**
     * Sets the value of the logicOps property.
     */
    public void setTemporalOps(final TemporalOpsType tempOps) {
        this.temporalOps = createTemporalOps(tempOps);
    }

    private void verifyIdFilter() {
        boolean fid = false;
        boolean gid = false;
        for (JAXBElement<? extends AbstractIdType> jb : id) {
            AbstractIdType idfilter = jb.getValue();
            if (idfilter instanceof FeatureIdType) {
                fid = true;
            } else if (idfilter instanceof GmlObjectIdType) {
                gid = true;
            }
        }
        if (fid && gid) {
            throw new IllegalArgumentException("A filter expression may include only one type of identifier element.");
        }
    }
    /**
     * Gets the value of the id property.
     */
    public List<JAXBElement<? extends AbstractIdType>> getId() {
        if (id == null) {
            id = new ArrayList<>();
        }
        verifyIdFilter();
        return id;
    }

    @Override
    public Object getFilterObject() {
        if (comparisonOps != null) {
            return comparisonOps.getValue();
        } else if (id != null && !id.isEmpty()) {
            final List<AbstractIdType> featureId = new ArrayList<>();
            for (JAXBElement<? extends AbstractIdType> jb : id) {
                featureId.add(jb.getValue());
            }
            return featureId;
        } else if (logicOps != null) {
            return logicOps.getValue();
        } else if (spatialOps != null) {
            return spatialOps.getValue();
        } else if (temporalOps != null) {
            return temporalOps.getValue();
        }
        return null;
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']').append('\n');
        if (spatialOps != null) {
            s.append("SpatialOps: ").append(spatialOps.getValue().toString()).append('\n');
        }
        if (comparisonOps != null) {
            s.append("ComparisonOps: ").append(comparisonOps.getValue().toString()).append('\n');
        }
        if (temporalOps != null) {
            s.append("TemporalOps: ").append(temporalOps.getValue().toString()).append('\n');
        }
        if (logicOps != null) {
            s.append("LogicOps: ").append(logicOps.getValue().toString()).append('\n');
        }
        if (id != null) {
            s.append("id:").append('\n');
            int i = 0;
            for (JAXBElement<? extends AbstractIdType> jb: id) {
                s.append("id ").append(i).append(": ").append(jb.getValue().toString()).append('\n');
                i++;
            }
        }
        return s.toString();
    }

    @Override
    public CodeList<?> getOperatorType() {
        if (spatialOps    != null) return spatialOps   .getValue().getOperatorType();
        if (comparisonOps != null) return comparisonOps.getValue().getOperatorType();
        if (temporalOps   != null) return temporalOps  .getValue().getOperatorType();
        if (logicOps      != null) return logicOps     .getValue().getOperatorType();
        return null;
    }

    @Override
    public List getExpressions() {
        if (spatialOps    != null) return spatialOps   .getValue().getExpressions();
        if (comparisonOps != null) return comparisonOps.getValue().getExpressions();
        if (temporalOps   != null) return temporalOps  .getValue().getExpressions();
        if (logicOps      != null) return logicOps     .getValue().getExpressions();
        return null;
    }

    @Override
    public boolean test(final Object object) {
        if (spatialOps    != null) return spatialOps   .getValue().test(object);
        if (comparisonOps != null) return comparisonOps.getValue().test(object);
        if (temporalOps   != null) return temporalOps  .getValue().test(object);
        if (logicOps      != null) return logicOps     .getValue().test(object);
        return false;
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
            return FACTORY.createTAfter((TimeAfterType) operator);
        } else if (operator instanceof TimeBeforeType) {
            return FACTORY.createTBefore((TimeBeforeType) operator);
        } else if (operator instanceof TimeBeginsType) {
            return FACTORY.createTBegins((TimeBeginsType) operator);
        } else if (operator instanceof TimeBegunByType) {
            return FACTORY.createTBegunBy((TimeBegunByType) operator);
        } else if (operator instanceof TimeContainsType) {
            return FACTORY.createTContains((TimeContainsType) operator);
        } else if (operator instanceof TimeDuringType) {
            return FACTORY.createTDuring((TimeDuringType) operator);
        } else if (operator instanceof TimeEndedByType) {
            return FACTORY.createTEndedBy((TimeEndedByType) operator);
        } else if (operator instanceof TimeEndsType) {
            return FACTORY.createTEnds((TimeEndsType) operator);
        } else if (operator instanceof TimeEqualsType) {
            return FACTORY.createTEquals((TimeEqualsType) operator);
        } else if (operator instanceof TimeMeetsType) {
            return FACTORY.createTMeets((TimeMeetsType) operator);
        } else if (operator instanceof TimeMetByType) {
            return FACTORY.createTMetBy((TimeMetByType) operator);
        } else if (operator instanceof TimeOverlappedByType) {
            return FACTORY.createTOverlappedBy((TimeOverlappedByType) operator);
        } else if (operator instanceof TimeOverlapsType) {
            return FACTORY.createTOveralps((TimeOverlapsType) operator);
        } else if (operator instanceof TemporalOpsType) {
            return FACTORY.createTemporalOps((TemporalOpsType) operator);
        } else {
            return null;
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

    public static JAXBElement<? extends AbstractIdType> createIdOps(final AbstractIdType operator) {
        if (operator instanceof FeatureIdType) {
            return FACTORY.createFeatureId((FeatureIdType) operator);
        } else if (operator instanceof GmlObjectIdType) {
            return FACTORY.createGmlObjectId((GmlObjectIdType) operator);
        } else {
            return null;
        }
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
            /*
             * TODO ID
             */
            return  comp && spa && log && temp;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.spatialOps != null ? this.spatialOps.hashCode() : 0);
        hash = 29 * hash + (this.temporalOps != null ? this.temporalOps.hashCode() : 0);
        hash = 29 * hash + (this.comparisonOps != null ? this.comparisonOps.hashCode() : 0);
        hash = 29 * hash + (this.logicOps != null ? this.logicOps.hashCode() : 0);
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
