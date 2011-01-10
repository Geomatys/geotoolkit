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
package org.geotoolkit.sos.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110.BinaryTemporalOpType;
import org.geotoolkit.ogc.xml.v110.TemporalOpsType;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventTime", propOrder = {
    "temporalOps",
    "tOveralps",
    "tEquals",
    "tMeets",
    "tOverlappedBy",
    "tEndedBy",
    "tEnds",
    "tAfter",
    "tMetBy",
    "tBegins",
    "tBefore",
    "tBegunBy",
    "tContains",
    "tDuring"
})
public class EventTime {

    @XmlElement(namespace = "http://www.opengis.net/ogc")
    private TemporalOpsType temporalOps;
    @XmlElement(name = "TM_Overalps", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tOveralps;
    @XmlElement(name = "TM_Equals", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tEquals;
    @XmlElement(name = "TM_Meets", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tMeets;
    @XmlElement(name = "TM_OverlappedBy", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tOverlappedBy;
    @XmlElement(name = "TM_EndedBy", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tEndedBy;
    @XmlElement(name = "TM_Ends", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tEnds;
    @XmlElement(name = "TM_After", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tAfter;
    @XmlElement(name = "TM_MetBy", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tMetBy;
    @XmlElement(name = "TM_Begins", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tBegins;
    @XmlElement(name = "TM_Before", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tBefore;
    @XmlElement(name = "TM_BegunBy", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tBegunBy;
    @XmlElement(name = "TM_Contains", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tContains;
    @XmlElement(name = "TM_During", namespace = "http://www.opengis.net/ogc")
    private BinaryTemporalOpType tDuring;

    /**
     * An empty constructor used by jaxB
     */
     EventTime(){}
     
    /**
     * Build a new Event time with T_After parameter or T_Before or T_During
     */
     public EventTime(final BinaryTemporalOpType tAfter, final BinaryTemporalOpType tBefore,
             final BinaryTemporalOpType tDuring){
         this.tAfter  = tAfter;
         this.tBefore = tBefore;
         this.tDuring = tDuring;
     }
     
     /**
     * Build a new Event time with T_Equals.
     */
     public EventTime(final BinaryTemporalOpType tEquals){
         this.tEquals = tEquals;
     }
     
    /**
     * Gets the value of the temporalOps property.
     */
    public TemporalOpsType getTemporalOps() {
        return temporalOps;
    }

    /**
     * Gets the value of the tOveralps property.
     */
    public BinaryTemporalOpType getTOveralps() {
        return tOveralps;
    }

    /**
     * Gets the value of the tEquals property.
     */
    public BinaryTemporalOpType getTEquals() {
        return tEquals;
    }

    /**
     * Gets the value of the tMeets property.
     */
    public BinaryTemporalOpType getTMeets() {
        return tMeets;
    }

    /**
     * Gets the value of the tOverlappedBy property.
     */
    public BinaryTemporalOpType getTOverlappedBy() {
        return tOverlappedBy;
    }

    /**
     * Gets the value of the tEndedBy property.
     */
    public BinaryTemporalOpType getTEndedBy() {
        return tEndedBy;
    }

    /**
     * Gets the value of the tEnds property.
     */
    public BinaryTemporalOpType getTEnds() {
        return tEnds;
    }

    /**
     * Gets the value of the tAfter property.
     */
    public BinaryTemporalOpType getTAfter() {
        return tAfter;
    }

    /**
     * Gets the value of the tMetBy property.
     */
    public BinaryTemporalOpType getTMetBy() {
        return tMetBy;
    }

    /**
     * Gets the value of the tBegins property.
     */
    public BinaryTemporalOpType getTBegins() {
        return tBegins;
    }

    /**
     * Gets the value of the tBefore property.
     */
    public BinaryTemporalOpType getTBefore() {
        return tBefore;
    }

    /**
     * Gets the value of the tBegunBy property.
     */
    public BinaryTemporalOpType getTBegunBy() {
        return tBegunBy;
    }

    /**
     * Gets the value of the tContains property.
     */
    public BinaryTemporalOpType getTContains() {
        return tContains;
    }

    /**
     * Gets the value of the tDuring property.
     */
    public BinaryTemporalOpType getTDuring() {
        return tDuring;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof EventTime) {
            final EventTime that = (EventTime) object;
            return Utilities.equals(this.tAfter, that.tAfter) &&
                    Utilities.equals(this.tBefore, that.tBefore) &&
                    Utilities.equals(this.tBegins, that.tBegins) &&
                    Utilities.equals(this.tBegunBy, that.tBegunBy) &&
                    Utilities.equals(this.tContains, that.tContains) &&
                    Utilities.equals(this.tDuring, that.tDuring) &&
                    Utilities.equals(this.tEndedBy, that.tEndedBy) &&
                    Utilities.equals(this.tEnds, that.tEnds) &&
                    Utilities.equals(this.tEquals, that.tEquals) &&
                    Utilities.equals(this.tMeets, that.tMeets) &&
                    Utilities.equals(this.tMetBy, that.tMetBy) &&
                    Utilities.equals(this.tOveralps, that.tOveralps) &&
                    Utilities.equals(this.tOverlappedBy, that.tOverlappedBy) &&
                    Utilities.equals(this.temporalOps, that.temporalOps);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.temporalOps != null ? this.temporalOps.hashCode() : 0);
        hash = 53 * hash + (this.tOveralps != null ? this.tOveralps.hashCode() : 0);
        hash = 53 * hash + (this.tEquals != null ? this.tEquals.hashCode() : 0);
        hash = 53 * hash + (this.tMeets != null ? this.tMeets.hashCode() : 0);
        hash = 53 * hash + (this.tOverlappedBy != null ? this.tOverlappedBy.hashCode() : 0);
        hash = 53 * hash + (this.tEndedBy != null ? this.tEndedBy.hashCode() : 0);
        hash = 53 * hash + (this.tEnds != null ? this.tEnds.hashCode() : 0);
        hash = 53 * hash + (this.tAfter != null ? this.tAfter.hashCode() : 0);
        hash = 53 * hash + (this.tMetBy != null ? this.tMetBy.hashCode() : 0);
        hash = 53 * hash + (this.tBegins != null ? this.tBegins.hashCode() : 0);
        hash = 53 * hash + (this.tBefore != null ? this.tBefore.hashCode() : 0);
        hash = 53 * hash + (this.tBegunBy != null ? this.tBegunBy.hashCode() : 0);
        hash = 53 * hash + (this.tContains != null ? this.tContains.hashCode() : 0);
        hash = 53 * hash + (this.tDuring != null ? this.tDuring.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (temporalOps != null) {
            s.append("TemporalOps: ").append(temporalOps.toString());
        }
        if (tOveralps != null) {
            s.append("tOveralps: ").append(tOveralps.toString());
        }
        if (tEquals != null) {
            s.append("tEquals: ").append(tEquals);
        }
        if (tMeets != null) {
            s.append("tMeets: ").append(tMeets);
        }
        if (tOverlappedBy != null) {
            s.append("tOverlappedBy: ").append(tOverlappedBy);
        }
        if (tEndedBy != null) {
            s.append("tEndedBy: ").append(tEndedBy);
        }
        if (tEnds != null) {
            s.append("tEnds: ").append(tEnds);
        }
        if (tAfter != null) {
            s.append("tAfter: ").append(tAfter);
        }
        if (tMetBy != null) {
            s.append("tMetBy: ").append(tMetBy);
        }
        if (tBegins != null) {
            s.append("tBegins: ").append(tBegins);
        }
        if (tBefore != null) {
            s.append("tBefore: ").append(tBefore);
        }
        if (tBegunBy != null) {
            s.append("tBegunBy: ").append(tBegunBy);
        }
        if (tContains != null) {
            s.append("tContains: ").append(tContains);
        }
        if (tDuring != null) {
            s.append("tDuring: ").append(tDuring);
        }
        return s.toString();
    }
}

