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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v200.OMObservationType;
import org.geotoolkit.sos.xml.ResultTemplate;
import org.geotoolkit.swe.xml.v200.AbstractDataComponentType;
import org.geotoolkit.swe.xml.v200.AbstractEncodingType;
import org.geotoolkit.swes.xml.v200.AbstractSWESType;


/**
 * <p>Java class for ResultTemplateType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ResultTemplateType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}AbstractSWESType">
 *       &lt;sequence>
 *         &lt;element name="offering" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="observationTemplate">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/om/2.0}OM_Observation"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="resultStructure">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractDataComponent"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="resultEncoding">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
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
@XmlType(name = "ResultTemplateType", propOrder = {
    "offering",
    "observationTemplate",
    "resultStructure",
    "resultEncoding"
})
public class ResultTemplateType extends AbstractSWESType implements ResultTemplate {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String offering;
    @XmlElement(required = true)
    private ResultTemplateType.ObservationTemplate observationTemplate;
    @XmlElement(required = true)
    private ResultStructure resultStructure;
    @XmlElement(required = true)
    private ResultEncoding resultEncoding;

    public ResultTemplateType() {

    }

    public ResultTemplateType(final String offering, final OMObservationType template,  final AbstractDataComponentType resultStructure,
            final AbstractEncodingType encoding) {
        this.offering = offering;
        if (template != null) {
            this.observationTemplate = new ObservationTemplate(template);
        }
        if (resultStructure != null) {
            this.resultStructure = new ResultStructure(resultStructure);
        }
        if (encoding != null) {
            this.resultEncoding = new ResultEncoding(encoding);
        }
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
     * Gets the value of the observationTemplate property.
     *
     * @return
     *     possible object is
     *     {@link ResultTemplateType.ObservationTemplate }
     *
     */
    @Override
    public OMObservationType getObservationTemplate() {
        if (observationTemplate != null) {
            return observationTemplate.omObservation;
        }
        return null;
    }

    /**
     * Sets the value of the observationTemplate property.
     *
     * @param value
     *     allowed object is
     *     {@link ResultTemplateType.ObservationTemplate }
     *
     */
    public void setObservationTemplate(final OMObservationType value) {
        this.observationTemplate = new ObservationTemplate(value);
    }

    /**
     * Gets the value of the resultStructure property.
     *
     * @return
     *     possible object is
     *     {@link ResultTemplateType.ResultStructure }
     *
     */
    @Override
    public AbstractDataComponentType getResultStructure() {
        if (resultStructure != null) {
            return resultStructure.getAbstractDataComponent();
        }
        return null;
    }

    /**
     * Sets the value of the resultStructure property.
     *
     * @param value
     *     allowed object is
     *     {@link ResultTemplateType.ResultStructure }
     *
     */
    public void setResultStructure(final AbstractDataComponentType value) {
        this.resultStructure = new ResultStructure(value);
    }

    /**
     * Gets the value of the resultEncoding property.
     *
     * @return
     *     possible object is
     *     {@link ResultTemplateType.ResultEncoding }
     *
     */
    @Override
    public AbstractEncodingType getResultEncoding() {
        if (resultEncoding != null) {
            return resultEncoding.getAbstractEncoding();
        }
        return null;
    }

    /**
     * Sets the value of the resultEncoding property.
     *
     * @param value
     *     allowed object is
     *     {@link ResultTemplateType.ResultEncoding }
     *
     */
    public void setResultEncoding(final AbstractEncodingType value) {
        this.resultEncoding = new ResultEncoding(value);
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
     *         &lt;element ref="{http://www.opengis.net/om/2.0}OM_Observation"/>
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
        "omObservation"
    })
    public static class ObservationTemplate {

        @XmlElement(name = "OM_Observation", namespace = "http://www.opengis.net/om/2.0", required = true)
        private OMObservationType omObservation;

        public ObservationTemplate() {
        }

        public ObservationTemplate(final OMObservationType omObservation) {
            this.omObservation = omObservation;
        }

        /**
         * Gets the value of the omObservation property.
         *
         * @return
         *     possible object is
         *     {@link OMObservationType }
         *
         */
        public OMObservationType getOMObservation() {
            return omObservation;
        }

        /**
         * Sets the value of the omObservation property.
         *
         * @param value
         *     allowed object is
         *     {@link OMObservationType }
         *
         */
        public void setOMObservation(OMObservationType value) {
            this.omObservation = value;
        }

    }
}
