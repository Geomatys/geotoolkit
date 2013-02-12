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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v200.OMObservationType;
import org.geotoolkit.sos.xml.ResultTemplate;
import org.geotoolkit.swe.xml.v200.AbstractDataComponentPropertyType;
import org.geotoolkit.swe.xml.v200.AbstractDataComponentType;
import org.geotoolkit.swe.xml.v200.AbstractEncodingType;
import org.geotoolkit.swe.xml.v200.AbstractSimpleComponentType;
import org.geotoolkit.swe.xml.v200.BinaryEncodingType;
import org.geotoolkit.swe.xml.v200.BooleanType;
import org.geotoolkit.swe.xml.v200.CategoryRangeType;
import org.geotoolkit.swe.xml.v200.CategoryType;
import org.geotoolkit.swe.xml.v200.CountRangeType;
import org.geotoolkit.swe.xml.v200.CountType;
import org.geotoolkit.swe.xml.v200.DataArrayType;
import org.geotoolkit.swe.xml.v200.DataChoiceType;
import org.geotoolkit.swe.xml.v200.DataRecordType;
import org.geotoolkit.swe.xml.v200.MatrixType;
import org.geotoolkit.swe.xml.v200.QuantityRangeType;
import org.geotoolkit.swe.xml.v200.QuantityType;
import org.geotoolkit.swe.xml.v200.TextEncodingType;
import org.geotoolkit.swe.xml.v200.TextType;
import org.geotoolkit.swe.xml.v200.TimeRangeType;
import org.geotoolkit.swe.xml.v200.TimeType;
import org.geotoolkit.swe.xml.v200.VectorType;
import org.geotoolkit.swe.xml.v200.XMLEncodingType;
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
    private ResultTemplateType.ResultStructure resultStructure;
    @XmlElement(required = true)
    private ResultTemplateType.ResultEncoding resultEncoding;

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
        if (resultStructure != null && resultStructure.abstractDataComponent != null) {
            return resultStructure.abstractDataComponent.getValue();
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
        if (resultEncoding != null && resultEncoding.abstractEncoding != null) {
            return resultEncoding.abstractEncoding.getValue();
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
     *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
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
        "abstractEncoding"
    })
    public static class ResultEncoding {

        @XmlElementRef(name = "AbstractEncoding", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
        private JAXBElement<? extends AbstractEncodingType> abstractEncoding;

        public ResultEncoding() {
            
        }
        
        public ResultEncoding(final AbstractEncodingType encoding) {
            final org.geotoolkit.swe.xml.v200.ObjectFactory factory = new org.geotoolkit.swe.xml.v200.ObjectFactory();
            if (encoding instanceof TextEncodingType) {
                this.abstractEncoding = factory.createTextEncoding((TextEncodingType)encoding);
            } else if (encoding instanceof XMLEncodingType) {
                this.abstractEncoding = factory.createXMLEncoding((XMLEncodingType)encoding);
            } else if (encoding instanceof BinaryEncodingType) {
                this.abstractEncoding = factory.createBinaryEncoding((BinaryEncodingType)encoding);
            } else {
                this.abstractEncoding = factory.createAbstractEncoding(encoding);
            }
        }
        
        /**
         * Gets the value of the abstractEncoding property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractEncodingType> getAbstractEncoding() {
            return abstractEncoding;
        }

        /**
         * Sets the value of the abstractEncoding property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
         *     
         */
        public void setAbstractEncoding(JAXBElement<? extends AbstractEncodingType> value) {
            this.abstractEncoding = ((JAXBElement<? extends AbstractEncodingType> ) value);
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
     *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractDataComponent"/>
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
        "abstractDataComponent"
    })
    public static class ResultStructure {

        @XmlElementRef(name = "AbstractDataComponent", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
        private JAXBElement<? extends AbstractDataComponentType> abstractDataComponent;

        public ResultStructure() {
            
        }
        
        public ResultStructure(final AbstractDataComponentType value) {
            this.abstractDataComponent = AbstractDataComponentPropertyType.getJAXBElement(value);
        }
        
        /**
         * Gets the value of the abstractDataComponent property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
         *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TimeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}
         *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link QuantityType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TextType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CountType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractDataComponentType> getAbstractDataComponent() {
            return abstractDataComponent;
        }

        /**
         * Sets the value of the abstractDataComponent property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
         *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TimeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}
         *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link QuantityType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TextType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CountType }{@code >}
         *     
         */
        public void setAbstractDataComponent(JAXBElement<? extends AbstractDataComponentType> value) {
            this.abstractDataComponent = ((JAXBElement<? extends AbstractDataComponentType> ) value);
        }

    }

}
