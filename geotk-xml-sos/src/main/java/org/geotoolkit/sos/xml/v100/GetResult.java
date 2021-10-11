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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.Filter;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sos/1.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="ObservationTemplateId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="eventTime" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ogc}temporalOps"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetResult", propOrder = {
    "observationTemplateId",
    "eventTime"
})
@XmlRootElement(name = "GetResult")
public class GetResult extends RequestBaseType implements org.geotoolkit.sos.xml.GetResult {

    @XmlElement(name = "ObservationTemplateId", required = true)
    @XmlSchemaType(name = "anyURI")
    private String observationTemplateId;
    private List<EventTime> eventTime;

    /**
     * An empty constructor used by jaxB
     */
     GetResult(){}

    /**
     * Build a new request GetResult.
     */
    public GetResult(final String observationTemplateId, final List<EventTime> eventTime, final String version){
       super(version);
       this.eventTime             = eventTime;
       this.observationTemplateId = observationTemplateId;
    }

    @Override
    public String getOffering() {
        return null; // not in v1.0.0
    }

    @Override
    public String getObservedProperty() {
        return null;  // not in v1.0.0
    }

    @Override
    public List<String> getFeatureOfInterest() {
        return new ArrayList<>();  // not in v1.0.0
    }

    @Override
    public Filter getSpatialFilter() {
        return null;  // not in v1.0.0
    }

    /**
     * Gets the value of the observationTemplateId property.
     *
    */
    @Override
    public String getObservationTemplateId() {
        return observationTemplateId;
    }

    /**
     * Gets the value of the eventTime property.
     * (unmodifable)
     */
    public List<EventTime> getEventTime() {
        if (eventTime == null){
            eventTime = new ArrayList<>();
        }
        return eventTime;
    }

    @Override
    public List<Filter> getTemporalFilter() {
        if (eventTime != null) {
            final List<Filter> temporalFilter = new ArrayList<>();
            for (EventTime time : eventTime) {
                temporalFilter.add(time.getFilter());
            }
            return temporalFilter;
        }
        return new ArrayList<>();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetResult && super.equals(object)) {
            final GetResult that = (GetResult) object;
            return Objects.equals(this.eventTime, that.eventTime) &&
                   Objects.equals(this.observationTemplateId,   that.observationTemplateId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.observationTemplateId != null ? this.observationTemplateId.hashCode() : 0);
        hash = 37 * hash + (this.eventTime != null ? this.eventTime.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[GetResult]").append('\n');
        s.append("observation template id=").append(observationTemplateId).append('\n');
        if (eventTime != null) {
            for (EventTime et:eventTime) {
                s.append(et.toString()).append('\n');
            }
        }
        return  s.toString();
    }

    @Override
    public String getResponseFormat() {
        return "text/xml";
    }
}
