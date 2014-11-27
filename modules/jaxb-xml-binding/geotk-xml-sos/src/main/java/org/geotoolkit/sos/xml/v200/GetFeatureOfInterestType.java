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

package org.geotoolkit.sos.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.FilterType;
import org.geotoolkit.ogc.xml.v200.SpatialOpsType;
import org.geotoolkit.sos.xml.GetFeatureOfInterest;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;
import org.opengis.filter.Filter;


/**
 * <p>Java class for GetFeatureOfInterestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetFeatureOfInterestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="procedure" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="observedProperty" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="featureOfInterest" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spatialFilter" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/fes/2.0}spatialOps"/>
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFeatureOfInterestType", propOrder = {
    "procedure",
    "observedProperty",
    "featureOfInterest",
    "spatialFilter"
})
@XmlRootElement(name = "GetFeatureOfInterest")
public class GetFeatureOfInterestType extends ExtensibleRequestType implements GetFeatureOfInterest {

    @XmlSchemaType(name = "anyURI")
    private List<String> procedure;
    @XmlSchemaType(name = "anyURI")
    private List<String> observedProperty;
    @XmlSchemaType(name = "anyURI")
    private List<String> featureOfInterest;
    private List<SpatialFilterType> spatialFilter;

    public GetFeatureOfInterestType() {
        
    }
    
    public GetFeatureOfInterestType(final String version, final String service, final String featureId) {
        super(version, service);
        if (featureId != null) {
            this.featureOfInterest = new ArrayList<String>();
            this.featureOfInterest.add(featureId);
        }
    }
    
    public GetFeatureOfInterestType(final String version, final String service, final List<String> observedProperties,
            final List<String> procedure, final List<String> featureId, final Filter location) {
        super(version, service);
        this.observedProperty  = observedProperties;
        this.procedure         = procedure;
        this.featureOfInterest = featureId;
        if (location != null) {
            this.spatialFilter = new ArrayList<SpatialFilterType>();
            this.spatialFilter.add(new SpatialFilterType(location));
        }
    }
    
    public GetFeatureOfInterestType(final String version, final String service, final List<String> featureId) {
        super(version, service);
        this.featureOfInterest = featureId;
    }
    
    public GetFeatureOfInterestType(final String version, final String service, final Filter location) {
        super(version, service);
        if (location != null) {
            this.spatialFilter = new ArrayList<SpatialFilterType>();
            this.spatialFilter.add(new SpatialFilterType(location));
        }
     }
    
    /**
     * Gets the value of the procedure property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    @Override
    public List<String> getProcedure() {
        if (procedure == null) {
            procedure = new ArrayList<String>();
        }
        return this.procedure;
    }

    /**
     * Gets the value of the observedProperty property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    @Override
    public List<String> getObservedProperty() {
        if (observedProperty == null) {
            observedProperty = new ArrayList<String>();
        }
        return this.observedProperty;
    }

    /**
     * Gets the value of the featureOfInterest property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    @Override
    public List<String> getFeatureOfInterestId() {
        if (featureOfInterest == null) {
            featureOfInterest = new ArrayList<String>();
        }
        return this.featureOfInterest;
    }

    /**
     * Gets the value of the spatialFilter property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GetFeatureOfInterestType.SpatialFilter }
     * 
     */
    public List<SpatialFilterType> getRealSpatialFilter() {
        if (spatialFilter == null) {
            spatialFilter = new ArrayList<SpatialFilterType>();
        }
        return this.spatialFilter;
    }

    @Override
    public List<Filter> getSpatialFilters() {
        final List<Filter> results = new ArrayList<Filter>();
        if (spatialFilter != null) {
            for (SpatialFilterType sp : spatialFilter) {
                results.add(sp.getSpatialOps());
            }
        }
        return results;
    }

    /**
     * SOS 1.0.0 compatibility
     * @return 
     */
    @Override
    public List<Filter> getTemporalFilters() {
        return new ArrayList<Filter>();
    }
}
