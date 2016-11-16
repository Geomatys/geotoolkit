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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.SpatialOpsType;
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
 *         &lt;choice>
 *           &lt;element name="FeatureOfInterestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *           &lt;element name="location">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element ref="{http://www.opengis.net/ogc}spatialOps"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
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
@XmlType(name = "GetFeatureOfInterest", propOrder = {
    "featureOfInterestId",
    "location",
    "eventTime"
})
@XmlRootElement(name = "GetFeatureOfInterest")
public class GetFeatureOfInterest extends RequestBaseType implements org.geotoolkit.sos.xml.GetFeatureOfInterest {

    /**
     * Identifier of the feature of interest, for which detailed information is requested.
     * These identifiers are usually listed in the Contents section of the service metadata (Capabilities) document.
     */
    @XmlElement(name = "FeatureOfInterestId")
    @XmlSchemaType(name = "anyURI")
    private List<String> featureOfInterestId;
    
    private GetFeatureOfInterest.Location location;
    
     /**
     * Allows a client to request targets from a specific instant, multiple instances or periods of time in the past, present and future.
     * This is useful for dynamic sensors for which the properties of the target are time-dependent.
     * Multiple time paramters may be indicated so that the client may request details of the observation target at multiple times.
     * The supported range is listed in the contents section of the service metadata.
     *
     */
    private List<EventTime> eventTime;

    /**
     * An empty constructor used by jaxB
     */
     public GetFeatureOfInterest() {
     }


     public GetFeatureOfInterest(final String version, final String service, final String featureId) {
        super(version, service);
        this.featureOfInterestId = new ArrayList<>();
        if (featureId != null) {
            this.featureOfInterestId.add(featureId);
        }
     }

     public GetFeatureOfInterest(final String version, final String service, final List<String> featureId) {
        super(version, service);
        this.featureOfInterestId = featureId;
     }

     public GetFeatureOfInterest(final String version, final String service, final Filter location) {
        super(version, service);
        if (location != null) {
            this.location = new Location(location);
        }
     }
     
     public GetFeatureOfInterest(final String version, final String service, final List<String> featureId, final Filter location) {
        super(version, service);
        this.featureOfInterestId = featureId;
        if (location != null) {
            this.location = new Location(location);
        }
     }

    /**
     * Gets the value of the featureOfInterestId property.
     */
    @Override
    public List<String> getFeatureOfInterestId() {
        if (featureOfInterestId == null) {
            featureOfInterestId = new ArrayList<>();
        }
        return featureOfInterestId;
    }
    
    /**
     * Gets the value of the eventTime property.
     * (unmodifiable)
     */
    public List<EventTime> getEventTime() {
        if (eventTime == null) {
            eventTime = new ArrayList<>();
        }
        return eventTime;
    }
    
    /**
     * Gets the value of the featureOfInterestLocation property.
     *
     */
    public GetFeatureOfInterest.Location getLocation() {
        return location;
    }

    /**
     * Set the value of the featureOfInterestLocation property.
     *
     */
    public void setLocation(final GetFeatureOfInterest.Location location) {
        this.location = location;
    }
    
    @Override
    public List<Filter> getSpatialFilters() {
        final List<Filter> results = new ArrayList<>();
        if (location != null) {
            results.add(location.getSpatialOperator());
        }
        return results;
    }
    
    @Override
    public List<Filter> getTemporalFilters() {
        final List<Filter> results = new ArrayList<>();
        if (eventTime != null) {
            for (EventTime time : eventTime) {
                results.add(time.getFilter());
            }
        }
        return results;
    }
    
    @Override
    public List<String> getProcedure() {
        return new ArrayList<>(); // no procedure filter in v 1.0.0
    }

    @Override
    public List<String> getObservedProperty() {
        return new ArrayList<>(); // no observedProperty filter in v 1.0.0
    }
    
    @Override
    public String getResponseFormat() {
        return "text/xml";
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetFeatureOfInterest && super.equals(object)) {
            final GetFeatureOfInterest that = (GetFeatureOfInterest) object;
            return Objects.equals(this.eventTime,           that.eventTime)           &&
                   Objects.equals(this.featureOfInterestId, that.featureOfInterestId) &&
                   Objects.equals(this.location,            that.location);
        } 
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.featureOfInterestId != null ? this.featureOfInterestId.hashCode() : 0);
        hash = 41 * hash + (this.eventTime != null ? this.eventTime.hashCode() : 0);
        hash = 41 * hash + (this.location != null ? this.location.hashCode() : 0);
        return hash;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/ogc}spatialOps"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "spatialOps"
    })
    public static class Location {

        @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
        private JAXBElement<? extends SpatialOpsType> spatialOps;

        public Location() {

        }

        public Location(final JAXBElement<? extends SpatialOpsType> spatialOps) {
            this.spatialOps = spatialOps;
        }

        public Location(final Filter filter) {
            if (filter instanceof SpatialOpsType) {
                this.spatialOps = FilterType.createSpatialOps((SpatialOpsType)filter);
            } else if (filter != null) {
                throw new IllegalArgumentException("Unexpected spatial filter type:" + filter);
            }
        }

        public SpatialOpsType getSpatialOperator() {
            if (spatialOps != null) {
                return spatialOps.getValue();
            }
            return null;
        }
        
        /**
         * Gets the value of the spatialOps property.
         */
        public JAXBElement<? extends SpatialOpsType> getSpatialOps() {
            return spatialOps;
        }

        /**
         * Gets the value of the spatialOps property.
         */
        public void setSpatialOps(final JAXBElement<? extends SpatialOpsType> spatialOps) {
            this.spatialOps = spatialOps;
        }
        
        /**
          * Verify if this entry is identical to the specified object.
          */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Location) {
                final  Location that = ( Location) object;
                return Objects.equals(this.spatialOps.getValue(), that.spatialOps.getValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + (this.spatialOps != null ? this.spatialOps.hashCode() : 0);
            return hash;
        }
    }
}
