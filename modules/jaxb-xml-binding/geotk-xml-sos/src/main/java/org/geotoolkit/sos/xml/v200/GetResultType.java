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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.BBOXType;
import org.geotoolkit.ogc.xml.v200.BinarySpatialOpType;
import org.geotoolkit.ogc.xml.v200.BinaryTemporalOpType;
import org.geotoolkit.ogc.xml.v200.DistanceBufferType;
import org.geotoolkit.ogc.xml.v200.SpatialOpsType;
import org.geotoolkit.ogc.xml.v200.TemporalOpsType;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;


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
public class GetResultType extends ExtensibleRequestType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String offering;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String observedProperty;
    private List<GetResultType.TemporalFilter> temporalFilter;
    @XmlSchemaType(name = "anyURI")
    private List<String> featureOfInterest;
    private GetResultType.SpatialFilter spatialFilter;

    /**
     * Gets the value of the offering property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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

    /**
     * Gets the value of the temporalFilter property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GetResultType.TemporalFilter }
     * 
     */
    public List<GetResultType.TemporalFilter> getTemporalFilter() {
        if (temporalFilter == null) {
            temporalFilter = new ArrayList<GetResultType.TemporalFilter>();
        }
        return this.temporalFilter;
    }

    /**
     * Gets the value of the featureOfInterest property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getFeatureOfInterest() {
        if (featureOfInterest == null) {
            featureOfInterest = new ArrayList<String>();
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
    public GetResultType.SpatialFilter getSpatialFilter() {
        return spatialFilter;
    }

    /**
     * Sets the value of the spatialFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetResultType.SpatialFilter }
     *     
     */
    public void setSpatialFilter(GetResultType.SpatialFilter value) {
        this.spatialFilter = value;
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
