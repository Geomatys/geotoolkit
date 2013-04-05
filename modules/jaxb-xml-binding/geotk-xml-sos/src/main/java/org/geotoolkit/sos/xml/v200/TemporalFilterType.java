/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.sos.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.TemporalOpsType;
import org.geotoolkit.ogc.xml.v200.TimeAfterType;
import org.geotoolkit.ogc.xml.v200.TimeAnyInteractsType;
import org.geotoolkit.ogc.xml.v200.TimeBeforeType;
import org.geotoolkit.ogc.xml.v200.TimeBeginsType;
import org.geotoolkit.ogc.xml.v200.TimeBegunByType;
import org.geotoolkit.ogc.xml.v200.TimeContainsType;
import org.geotoolkit.ogc.xml.v200.TimeDuringType;
import org.geotoolkit.ogc.xml.v200.TimeEndedByType;
import org.geotoolkit.ogc.xml.v200.TimeEndsType;
import org.geotoolkit.ogc.xml.v200.TimeEqualsType;
import org.geotoolkit.ogc.xml.v200.TimeMeetsType;
import org.geotoolkit.ogc.xml.v200.TimeMetByType;
import org.geotoolkit.ogc.xml.v200.TimeOverlappedByType;
import org.geotoolkit.ogc.xml.v200.TimeOverlapsType;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}temporalOps"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalFilterType", propOrder = {
    "temporalOps"
})
public class TemporalFilterType {

    // enumerate the classes instead of using @XmlElementRefs because of an issue with JAX-WS
    @XmlElements({
        @XmlElement(name = "TContains", namespace = "http://www.opengis.net/fes/2.0", type = TimeContainsType.class),
        @XmlElement(name = "Meets", namespace = "http://www.opengis.net/fes/2.0", type = TimeMeetsType.class),
        @XmlElement(name = "Begins", namespace = "http://www.opengis.net/fes/2.0", type = TimeBeginsType.class),
        @XmlElement(name = "During", namespace = "http://www.opengis.net/fes/2.0", type = TimeDuringType.class),
        @XmlElement(name = "EndedBy", namespace = "http://www.opengis.net/fes/2.0", type = TimeEndedByType.class),
        @XmlElement(name = "Ends", namespace = "http://www.opengis.net/fes/2.0", type = TimeEndsType.class),
        @XmlElement(name = "TOverlaps", namespace = "http://www.opengis.net/fes/2.0", type = TimeOverlapsType.class),
        @XmlElement(name = "MetBy", namespace = "http://www.opengis.net/fes/2.0", type = TimeMetByType.class),
        @XmlElement(name = "OverlappedBy", namespace = "http://www.opengis.net/fes/2.0", type = TimeOverlappedByType.class),
        @XmlElement(name = "BegunBy", namespace = "http://www.opengis.net/fes/2.0", type = TimeBegunByType.class),
        @XmlElement(name = "Before", namespace = "http://www.opengis.net/fes/2.0", type = TimeBeforeType.class),
        @XmlElement(name = "After", namespace = "http://www.opengis.net/fes/2.0", type = TimeAfterType.class),
        @XmlElement(name = "AnyInteracts", namespace = "http://www.opengis.net/fes/2.0", type = TimeAnyInteractsType.class),
        @XmlElement(name = "TEquals", namespace = "http://www.opengis.net/fes/2.0", type = TimeEqualsType.class),})
    private TemporalOpsType temporalOps;

    public TemporalFilterType() {
    }

    public TemporalFilterType(final TemporalOpsType temporalOp) {
        if (temporalOp != null) {
            this.temporalOps = temporalOp;
        }
    }

    /**
     * Gets the value of the temporalOps property.
     *
     */
    public TemporalOpsType getTemporalOps() {
        return temporalOps;
    }

    /**
     * Sets the value of the temporalOps property.
     *
     */
    public void setTemporalOps(TemporalOpsType value) {
        this.temporalOps = value;
    }
}
