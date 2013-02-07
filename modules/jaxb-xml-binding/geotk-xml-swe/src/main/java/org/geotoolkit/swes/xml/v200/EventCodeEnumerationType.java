/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swes.xml.v200;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EventCodeEnumerationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EventCodeEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CapabilitiesChanged"/>
 *     &lt;enumeration value="OfferingAdded"/>
 *     &lt;enumeration value="OfferingDeleted"/>
 *     &lt;enumeration value="SensorDescriptionUpdated"/>
 *     &lt;enumeration value="SensorInserted"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EventCodeEnumerationType")
@XmlEnum
public enum EventCodeEnumerationType {


    /**
     * A property inside the service&rsquo;s Capabilities document was added, removed or changed its value. If multiple changes happen at the same time (or in a time interval sufficiently small for a service to recognize them as belonging together) then they constitute one capabilities changed event.
     * 
     */
    @XmlEnumValue("CapabilitiesChanged")
    CAPABILITIES_CHANGED("CapabilitiesChanged"),

    /**
     * A new offering - as defined by the SWE Service Model - was added to the service.
     * 
     */
    @XmlEnumValue("OfferingAdded")
    OFFERING_ADDED("OfferingAdded"),

    /**
     * An offering- as defined by the SWE Service Model - was deleted from the service.
     * 
     */
    @XmlEnumValue("OfferingDeleted")
    OFFERING_DELETED("OfferingDeleted"),

    /**
     * The description of a sensor was updated, meaning that information contained in the current and / or previous versions of the sensor's description was added, removed or modified.
     * 
     */
    @XmlEnumValue("SensorDescriptionUpdated")
    SENSOR_DESCRIPTION_UPDATED("SensorDescriptionUpdated"),

    /**
     * A sensor was inserted at the service, resulting in a new offering - as defined by the SWE Service Model - having been added to the service.
     * 
     */
    @XmlEnumValue("SensorInserted")
    SENSOR_INSERTED("SensorInserted");
    private final String value;

    EventCodeEnumerationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EventCodeEnumerationType fromValue(String v) {
        for (EventCodeEnumerationType c: EventCodeEnumerationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
