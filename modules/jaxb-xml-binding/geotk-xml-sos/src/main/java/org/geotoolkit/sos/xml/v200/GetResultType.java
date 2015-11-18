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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.SpatialOpsType;
import org.geotoolkit.ogc.xml.v200.TemporalOpsType;
import org.geotoolkit.sos.xml.GetResult;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;
import org.opengis.filter.Filter;


/**
 * <p>Java class for GetResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetResultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="offering" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="observedProperty" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="temporalFilter" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/fes/2.0}temporalOps"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="featureOfInterest" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spatialFilter" minOccurs="0">
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
@XmlType(name = "GetResultType", propOrder = {
    "offering",
    "observedProperty",
    "temporalFilter",
    "featureOfInterest",
    "spatialFilter"
})
@XmlRootElement(name="GetResult")
public class GetResultType extends ExtensibleRequestType implements GetResult {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String offering;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String observedProperty;
    private List<TemporalFilterType> temporalFilter;
    @XmlSchemaType(name = "anyURI")
    private List<String> featureOfInterest;
    private SpatialFilterType spatialFilter;

    /*
     * An empty constructor used by jaxB
     */
     GetResultType(){}
     
    /**
     * Build a new request GetResult.
     */
    public GetResultType(final String version, final String service, final String offering, final String observedProperty, 
            final List<TemporalOpsType> timeFilter, final SpatialOpsType spatialFilter, final List<String> featureOfInterest){
       super(version, "SOS");
       if (timeFilter != null) {
            this.temporalFilter = new ArrayList<>();
            for (TemporalOpsType tfilter : timeFilter) {
                this.temporalFilter.add(new TemporalFilterType(tfilter));
            }
       }
       this.offering = offering;
       this.observedProperty = observedProperty;
       if (spatialFilter != null) {
           this.spatialFilter = new SpatialFilterType(spatialFilter);
       }
       this.featureOfInterest = featureOfInterest;
    }
    
    /**
     * Gets the value of the offering property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getOffering() {
        return offering;
    }

    /**
     * Sets the value of the offering property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOffering(String value) {
        this.offering = value;
    }

    /**
     * Gets the value of the observedProperty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getObservedProperty() {
        return observedProperty;
    }

    /**
     * Sets the value of the observedProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservedProperty(String value) {
        this.observedProperty = value;
    }

    @Override
    public List<Filter> getTemporalFilter() {
        if (temporalFilter == null) {
            temporalFilter = new ArrayList<>();
        }
        final List<Filter> result = new ArrayList<>();
        for (TemporalFilterType tf : temporalFilter) {
            if (tf.getTemporalOps() != null) {
                result.add(tf.getTemporalOps());
            }
        }
        return result;
    }
    
    public void addTemporalFilter(final TemporalOpsType temporal) {
        if (temporalFilter == null) {
            temporalFilter = new ArrayList<>();
        }
        temporalFilter.add(new TemporalFilterType(temporal));
    }
    /**
     * Gets the value of the featureOfInterest property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    @Override
    public List<String> getFeatureOfInterest() {
        if (featureOfInterest == null) {
            featureOfInterest = new ArrayList<>();
        }
        return this.featureOfInterest;
    }

    /**
     * Gets the value of the spatialFilter property.
     * 
     * @return
     *     possible object is
     *     {@link GetResultType.SpatialFilter }
     *     
     */
    @Override
    public Filter getSpatialFilter() {
        if (spatialFilter != null) {
            return spatialFilter.getSpatialOps();
        }
        return null;
    }

    /**
     * Sets the value of the spatialFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetResultType.SpatialFilter }
     *     
     */
    public void setSpatialFilter(SpatialFilterType value) {
        this.spatialFilter = value;
    }

    /**
     * 1.0.0 compatibility
     * 
     * @return 
     */
    @Override
    public String getObservationTemplateId() {
        return null;
    }
}
