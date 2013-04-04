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
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v200.FilterType;
import org.geotoolkit.ogc.xml.v200.SpatialOpsType;
import org.geotoolkit.ogc.xml.v200.TemporalOpsType;
import org.geotoolkit.sos.xml.GetObservation;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;
import org.opengis.filter.Filter;


/**
 * <p>Java class for GetObservationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetObservationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="procedure" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="offering" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="observedProperty" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
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
 *         &lt;element name="responseFormat" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetObservationType", propOrder = {
    "procedure",
    "offering",
    "observedProperty",
    "temporalFilter",
    "featureOfInterest",
    "spatialFilter",
    "responseFormat"
})
public class GetObservationType extends ExtensibleRequestType implements GetObservation {

    @XmlSchemaType(name = "anyURI")
    private List<String> procedure;
    @XmlSchemaType(name = "anyURI")
    private List<String> offering;
    @XmlSchemaType(name = "anyURI")
    private List<String> observedProperty;
    private List<GetObservationType.TemporalFilter> temporalFilter;
    @XmlSchemaType(name = "anyURI")
    private List<String> featureOfInterest;
    private GetObservationType.SpatialFilter spatialFilter;
    @XmlSchemaType(name = "anyURI")
    private String responseFormat;

    public GetObservationType() {
        
    }
    
    public GetObservationType(final String version, final String offering, final List<TemporalOpsType> eventTime, 
            final List<String> procedure, final List<String> observedProperty, final SpatialOpsType spatialFilter, final String responseFormat) {
        super(version, "SOS");
        if (offering != null) {
            this.offering = Arrays.asList(offering);
        }
        if (eventTime != null) {
            temporalFilter = new ArrayList<TemporalFilter>();
            for (TemporalOpsType temporal : eventTime) {
                this.temporalFilter.add(new TemporalFilter(temporal));
            }
        }
        this.procedure = procedure;
        this.observedProperty = observedProperty;
        if (spatialFilter != null) {
            this.spatialFilter = new SpatialFilter(spatialFilter);
        }
        this.responseFormat = responseFormat;
    }
    
    public GetObservationType(final String version, final String offering, final List<TemporalOpsType> eventTime, 
            final List<String> procedure, final List<String> observedProperty, final List<String> foi, final String responseFormat) {
        super(version, "SOS");
        if (offering != null) {
            this.offering = Arrays.asList(offering);
        }
        if (eventTime != null) {
            temporalFilter = new ArrayList<TemporalFilter>();
            for (TemporalOpsType temporal : eventTime) {
                this.temporalFilter.add(new TemporalFilter(temporal));
            }
        }
        this.procedure = procedure;
        this.observedProperty = observedProperty;
        this.featureOfInterest = foi;
        this.responseFormat = responseFormat;
    }
    
    public GetObservationType(final String version, final String service, final List<String> offering, final List<TemporalOpsType> eventTime, 
            final List<String> procedure, final List<String> observedProperty, final SpatialOpsType spatialFilter,
            final List<String> foi, final String responseFormat) {
        super(version, service);
        this.offering = offering;
        if (eventTime != null) {
            temporalFilter = new ArrayList<TemporalFilter>();
            for (TemporalOpsType temporal : eventTime) {
                this.temporalFilter.add(new TemporalFilter(temporal));
            }
        }
        this.procedure = procedure;
        this.observedProperty = observedProperty;
        if (spatialFilter != null) {
            this.spatialFilter = new SpatialFilter(spatialFilter);
        }
        this.featureOfInterest = foi;
        this.responseFormat = responseFormat;
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
     * Gets the value of the offering property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    @Override
    public List<String> getOfferings() {
        if (offering == null) {
            offering = new ArrayList<String>();
        }
        return this.offering;
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
     * Gets the value of the temporalFilter property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GetObservationType.TemporalFilter }
     * 
     */
    @Override
    public List<Filter> getTemporalFilter() {
        if (temporalFilter == null) {
            temporalFilter = new ArrayList<GetObservationType.TemporalFilter>();
        }
        final List<Filter> result = new ArrayList<Filter>();
        for (GetObservationType.TemporalFilter tf : temporalFilter) {
            if (tf.temporalOps != null) {
                result.add(tf.temporalOps.getValue());
            }
        }
        return result;
    }

    /**
     * Gets the value of the featureOfInterest property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    @Override
    public List<String> getFeatureIds() {
        if (featureOfInterest == null) {
            featureOfInterest = new ArrayList<String>();
        }
        return this.featureOfInterest;
    }

    /**
     * Sets the value of the spatialFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetObservationType.SpatialFilter }
     *     
     */
    public void setSpatialFilter(GetObservationType.SpatialFilter value) {
        this.spatialFilter = value;
    }

    /**
     * Gets the value of the responseFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * Sets the value of the responseFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseFormat(String value) {
        this.responseFormat = value;
    }

    /**
     * retro-compatibility with SOS 1.0.0
     * @return always {@code null}
     */
    @Override
    public QName getResultModel() {
        return null;
    }

    @Override
    public String getResponseMode() {
        return "inline";
    }

    @Override
    public String getSrsName() {
        return null;
    }

    @Override
    public Filter getSpatialFilter() {
        if (spatialFilter != null && spatialFilter.spatialOps != null) {
            return spatialFilter.spatialOps.getValue();
        }
        return null;
    }

    @Override
    public Filter getComparisonFilter() {
        return null;
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
     *         &lt;element ref="{http://www.opengis.net/fes/2.0}spatialOps"/>
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
    public static class SpatialFilter {

        @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
        private JAXBElement<? extends SpatialOpsType> spatialOps;

        public SpatialFilter() {
            
        }
        
        public SpatialFilter(final SpatialOpsType spa) {
            if (spa != null) {
                spatialOps = FilterType.createSpatialOps(spa);
            }
        }
        
        /**
         * Gets the value of the spatialOps property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BBOXType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
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
         *     {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BBOXType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
         *     
         */
        public void setSpatialOps(JAXBElement<? extends SpatialOpsType> value) {
            this.spatialOps = ((JAXBElement<? extends SpatialOpsType> ) value);
        }

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
     *         &lt;element ref="{http://www.opengis.net/fes/2.0}temporalOps"/>
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
        "temporalOps"
    })
    public static class TemporalFilter {

        @XmlElementRef(name = "temporalOps", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
        private JAXBElement<? extends TemporalOpsType> temporalOps;

        public TemporalFilter() {
            
        }
        
        public TemporalFilter(final TemporalOpsType temporalOp) {
            if (temporalOp != null) {
                this.temporalOps = FilterType.createTemporalOps(temporalOp);
            }
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
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TemporalOpsType }{@code >}
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
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TemporalOpsType }{@code >}
         *     
         */
        public void setTemporalOps(JAXBElement<? extends TemporalOpsType> value) {
            this.temporalOps = ((JAXBElement<? extends TemporalOpsType> ) value);
        }

    }

}
