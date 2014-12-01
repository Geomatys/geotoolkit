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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110.BinaryTemporalOpType;
import org.geotoolkit.ogc.xml.v110.TemporalOpsType;
import org.geotoolkit.ogc.xml.v110.TimeAfterType;
import org.geotoolkit.ogc.xml.v110.TimeBeforeType;
import org.geotoolkit.ogc.xml.v110.TimeBeginsType;
import org.geotoolkit.ogc.xml.v110.TimeBegunByType;
import org.geotoolkit.ogc.xml.v110.TimeContainsType;
import org.geotoolkit.ogc.xml.v110.TimeDuringType;
import org.geotoolkit.ogc.xml.v110.TimeEndedByType;
import org.geotoolkit.ogc.xml.v110.TimeEndsType;
import org.geotoolkit.ogc.xml.v110.TimeEqualsType;
import org.geotoolkit.ogc.xml.v110.TimeMeetsType;
import org.geotoolkit.ogc.xml.v110.TimeMetByType;
import org.geotoolkit.ogc.xml.v110.TimeOverlappedByType;
import org.geotoolkit.ogc.xml.v110.TimeOverlapsType;
import org.opengis.filter.Filter;

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
    private TimeOverlapsType tOveralps;
    @XmlElement(name = "TM_Equals", namespace = "http://www.opengis.net/ogc")
    private TimeEqualsType tEquals;
    @XmlElement(name = "TM_Meets", namespace = "http://www.opengis.net/ogc")
    private TimeMeetsType tMeets;
    @XmlElement(name = "TM_OverlappedBy", namespace = "http://www.opengis.net/ogc")
    private TimeOverlappedByType tOverlappedBy;
    @XmlElement(name = "TM_EndedBy", namespace = "http://www.opengis.net/ogc")
    private TimeEndedByType tEndedBy;
    @XmlElement(name = "TM_Ends", namespace = "http://www.opengis.net/ogc")
    private TimeEndsType tEnds;
    @XmlElement(name = "TM_After", namespace = "http://www.opengis.net/ogc")
    private TimeAfterType tAfter;
    @XmlElement(name = "TM_MetBy", namespace = "http://www.opengis.net/ogc")
    private TimeMetByType tMetBy;
    @XmlElement(name = "TM_Begins", namespace = "http://www.opengis.net/ogc")
    private TimeBeginsType tBegins;
    @XmlElement(name = "TM_Before", namespace = "http://www.opengis.net/ogc")
    private TimeBeforeType tBefore;
    @XmlElement(name = "TM_BegunBy", namespace = "http://www.opengis.net/ogc")
    private TimeBegunByType tBegunBy;
    @XmlElement(name = "TM_Contains", namespace = "http://www.opengis.net/ogc")
    private TimeContainsType tContains;
    @XmlElement(name = "TM_During", namespace = "http://www.opengis.net/ogc")
    private TimeDuringType tDuring;

    /**
     * An empty constructor used by jaxB
     */
     public EventTime() {

     }
     
     public EventTime(final BinaryTemporalOpType tempOp) {

        if (tempOp instanceof TimeOverlapsType) {
            this.tOveralps = (TimeOverlapsType) tempOp;
        } else if (tempOp instanceof TimeEqualsType) {
            this.tEquals = (TimeEqualsType) tempOp;
        } else if (tempOp instanceof TimeMeetsType) {
            this.tMeets = (TimeMeetsType) tempOp;
        } else if (tempOp instanceof TimeOverlappedByType) {
            this.tOverlappedBy = (TimeOverlappedByType) tempOp;
        } else if (tempOp instanceof TimeEndedByType) {
            this.tEndedBy = (TimeEndedByType) tempOp;
        } else if (tempOp instanceof TimeEndsType) {
            this.tEnds = (TimeEndsType) tempOp;
        } else if (tempOp instanceof TimeAfterType) {
            this.tAfter = (TimeAfterType) tempOp;
        } else if (tempOp instanceof TimeMetByType) {
            this.tMetBy = (TimeMetByType) tempOp;
        } else if (tempOp instanceof TimeBeginsType) {
            this.tBegins = (TimeBeginsType) tempOp;
        } else if (tempOp instanceof TimeBeforeType) {
            this.tBefore = (TimeBeforeType) tempOp;
        } else if (tempOp instanceof TimeBegunByType) {
            this.tBegunBy = (TimeBegunByType) tempOp;
        } else if (tempOp instanceof TimeContainsType) {
            this.tContains = (TimeContainsType) tempOp;
        } else if (tempOp instanceof TimeDuringType) {
            this.tDuring = (TimeDuringType) tempOp;
        } else if (tempOp instanceof TemporalOpsType) {
            this.temporalOps = (TemporalOpsType) tempOp;
        }

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
    public TimeOverlapsType getTOveralps() {
        return tOveralps;
    }

    /**
     * Gets the value of the tEquals property.
     */
    public TimeEqualsType getTEquals() {
        return tEquals;
    }

    /**
     * Gets the value of the tMeets property.
     */
    public TimeMeetsType getTMeets() {
        return tMeets;
    }

    /**
     * Gets the value of the tOverlappedBy property.
     */
    public TimeOverlappedByType getTOverlappedBy() {
        return tOverlappedBy;
    }

    /**
     * Gets the value of the tEndedBy property.
     */
    public TimeEndedByType getTEndedBy() {
        return tEndedBy;
    }

    /**
     * Gets the value of the tEnds property.
     */
    public TimeEndsType getTEnds() {
        return tEnds;
    }

    /**
     * Gets the value of the tAfter property.
     */
    public TimeAfterType getTAfter() {
        return tAfter;
    }

    /**
     * Gets the value of the tMetBy property.
     */
    public TimeMetByType getTMetBy() {
        return tMetBy;
    }

    /**
     * Gets the value of the tBegins property.
     */
    public TimeBeginsType getTBegins() {
        return tBegins;
    }

    /**
     * Gets the value of the tBefore property.
     */
    public TimeBeforeType getTBefore() {
        return tBefore;
    }

    /**
     * Gets the value of the tBegunBy property.
     */
    public TimeBegunByType getTBegunBy() {
        return tBegunBy;
    }

    /**
     * Gets the value of the tContains property.
     */
    public TimeContainsType getTContains() {
        return tContains;
    }

    /**
     * Gets the value of the tDuring property.
     */
    public TimeDuringType getTDuring() {
        return tDuring;
    }

    public Filter getFilter() {
        if (tOveralps != null) {
            return tOveralps;
        }
        if (tEquals != null) {
            return tEquals;
        }
        if (tMeets != null) {
            return tMeets;
        }
        if (tOverlappedBy != null) {
            return tOverlappedBy;
        }
        if (tEndedBy != null) {
            return tEndedBy;
        }
        if (tEnds != null) {
            return tEnds;
        }
        if (tAfter != null) {
            return tAfter;
        }
        if (tMetBy != null) {
            return tMetBy;
        }
        if (tBegins != null) {
            return tBegins;
        }
        if (tBefore != null) {
            return tBefore;
        }
        if (tBegunBy != null) {
            return tBegunBy;
        }
        if (tContains != null) {
            return tContains;
        }
        if (tDuring != null) {
            return tDuring;
        }
        if (temporalOps != null) {
            return temporalOps;
        }
        return null;
    }

    public void setFilter(Filter filter) {
        if (filter instanceof TimeOverlapsType) {
            tOveralps = (TimeOverlapsType) filter;
        } else if (filter instanceof TimeEqualsType) {
            tEquals = (TimeEqualsType) filter;
        } else if (filter instanceof TimeMeetsType) {
            tMeets = (TimeMeetsType) filter;
        } else if (filter instanceof TimeOverlappedByType) {
            tOverlappedBy = (TimeOverlappedByType) filter;
        } else if (filter instanceof TimeEndedByType) {
            tEndedBy = (TimeEndedByType) filter;
        } else if (filter instanceof TimeEndsType) {
            tEnds = (TimeEndsType) filter;
        } else if (filter instanceof TimeAfterType) {
            tAfter = (TimeAfterType) filter;
        } else if (filter instanceof TimeMetByType) {
            tMetBy = (TimeMetByType) filter;
        } else if (filter instanceof TimeBeginsType) {
            tBegins = (TimeBeginsType) filter;
        } else if (filter instanceof TimeBeforeType) {
            tBefore = (TimeBeforeType) filter;
        } else if (filter instanceof TimeBegunByType) {
            tBegunBy = (TimeBegunByType) filter;
        } else if (filter instanceof TimeContainsType) {
            tContains = (TimeContainsType) filter;
        } else if (filter instanceof TimeDuringType) {
            tDuring = (TimeDuringType) filter;
        } else if (filter instanceof TemporalOpsType) {
            temporalOps = (TemporalOpsType) filter;
        }
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
            return Objects.equals(this.tAfter, that.tAfter) &&
                    Objects.equals(this.tBefore, that.tBefore) &&
                    Objects.equals(this.tBegins, that.tBegins) &&
                    Objects.equals(this.tBegunBy, that.tBegunBy) &&
                    Objects.equals(this.tContains, that.tContains) &&
                    Objects.equals(this.tDuring, that.tDuring) &&
                    Objects.equals(this.tEndedBy, that.tEndedBy) &&
                    Objects.equals(this.tEnds, that.tEnds) &&
                    Objects.equals(this.tEquals, that.tEquals) &&
                    Objects.equals(this.tMeets, that.tMeets) &&
                    Objects.equals(this.tMetBy, that.tMetBy) &&
                    Objects.equals(this.tOveralps, that.tOveralps) &&
                    Objects.equals(this.tOverlappedBy, that.tOverlappedBy) &&
                    Objects.equals(this.temporalOps, that.temporalOps);
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

