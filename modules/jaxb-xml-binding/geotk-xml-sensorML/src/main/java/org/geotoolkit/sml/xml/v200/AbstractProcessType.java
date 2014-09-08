/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sml.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.ReferenceType;


/**
 * <p>Java class for AbstractProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractProcessType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorml/2.0}DescribedObjectType">
 *       &lt;sequence>
 *         &lt;element name="typeOf" type="{http://www.opengis.net/gml/3.2}ReferenceType" minOccurs="0"/>
 *         &lt;element name="configuration" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}AbstractSettings"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="featuresOfInterest" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}FeatureList"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="inputs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}InputList"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="outputs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}OutputList"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="parameters" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}ParameterList"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="modes" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}AbstractModes"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="definition" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractProcessType", propOrder = {
    "typeOf",
    "configuration",
    "featuresOfInterest",
    "inputs",
    "outputs",
    "parameters",
    "modes"
})
@XmlSeeAlso({
    AggregateProcessType.class,
    SimpleProcessType.class,
    AbstractPhysicalProcessType.class
})
public abstract class AbstractProcessType
    extends DescribedObjectType
{

    protected ReferenceType typeOf;
    protected AbstractProcessType.Configuration configuration;
    protected AbstractProcessType.FeaturesOfInterest featuresOfInterest;
    protected AbstractProcessType.Inputs inputs;
    protected AbstractProcessType.Outputs outputs;
    protected AbstractProcessType.Parameters parameters;
    protected List<AbstractProcessType.Modes> modes;
    @XmlAttribute(name = "definition")
    @XmlSchemaType(name = "anyURI")
    protected String definition;

    /**
     * Gets the value of the typeOf property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getTypeOf() {
        return typeOf;
    }

    /**
     * Sets the value of the typeOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setTypeOf(ReferenceType value) {
        this.typeOf = value;
    }

    /**
     * Gets the value of the configuration property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractProcessType.Configuration }
     *     
     */
    public AbstractProcessType.Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractProcessType.Configuration }
     *     
     */
    public void setConfiguration(AbstractProcessType.Configuration value) {
        this.configuration = value;
    }

    /**
     * Gets the value of the featuresOfInterest property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractProcessType.FeaturesOfInterest }
     *     
     */
    public AbstractProcessType.FeaturesOfInterest getFeaturesOfInterest() {
        return featuresOfInterest;
    }

    /**
     * Sets the value of the featuresOfInterest property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractProcessType.FeaturesOfInterest }
     *     
     */
    public void setFeaturesOfInterest(AbstractProcessType.FeaturesOfInterest value) {
        this.featuresOfInterest = value;
    }

    /**
     * Gets the value of the inputs property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractProcessType.Inputs }
     *     
     */
    public AbstractProcessType.Inputs getInputs() {
        return inputs;
    }

    /**
     * Sets the value of the inputs property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractProcessType.Inputs }
     *     
     */
    public void setInputs(AbstractProcessType.Inputs value) {
        this.inputs = value;
    }

    /**
     * Gets the value of the outputs property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractProcessType.Outputs }
     *     
     */
    public AbstractProcessType.Outputs getOutputs() {
        return outputs;
    }

    /**
     * Sets the value of the outputs property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractProcessType.Outputs }
     *     
     */
    public void setOutputs(AbstractProcessType.Outputs value) {
        this.outputs = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractProcessType.Parameters }
     *     
     */
    public AbstractProcessType.Parameters getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractProcessType.Parameters }
     *     
     */
    public void setParameters(AbstractProcessType.Parameters value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the modes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractProcessType.Modes }
     * 
     * 
     */
    public List<AbstractProcessType.Modes> getModes() {
        if (modes == null) {
            modes = new ArrayList<AbstractProcessType.Modes>();
        }
        return this.modes;
    }

    /**
     * Gets the value of the definition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefinition(String value) {
        this.definition = value;
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
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}AbstractSettings"/>
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
        "abstractSettings"
    })
    public static class Configuration {

        @XmlElementRef(name = "AbstractSettings", namespace = "http://www.opengis.net/sensorml/2.0", type = JAXBElement.class)
        protected JAXBElement<? extends AbstractSettingsType> abstractSettings;

        /**
         * Gets the value of the abstractSettings property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link SettingsType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractSettingsType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractSettingsType> getAbstractSettings() {
            return abstractSettings;
        }

        /**
         * Sets the value of the abstractSettings property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link SettingsType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractSettingsType }{@code >}
         *     
         */
        public void setAbstractSettings(JAXBElement<? extends AbstractSettingsType> value) {
            this.abstractSettings = value;
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
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}FeatureList"/>
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
        "featureList"
    })
    public static class FeaturesOfInterest {

        @XmlElement(name = "FeatureList", required = true)
        protected FeatureListType featureList;

        /**
         * Gets the value of the featureList property.
         * 
         * @return
         *     possible object is
         *     {@link FeatureListType }
         *     
         */
        public FeatureListType getFeatureList() {
            return featureList;
        }

        /**
         * Sets the value of the featureList property.
         * 
         * @param value
         *     allowed object is
         *     {@link FeatureListType }
         *     
         */
        public void setFeatureList(FeatureListType value) {
            this.featureList = value;
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
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}InputList"/>
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
        "inputList"
    })
    public static class Inputs {

        @XmlElement(name = "InputList", required = true)
        protected InputListType inputList;

        /**
         * Gets the value of the inputList property.
         * 
         * @return
         *     possible object is
         *     {@link InputListType }
         *     
         */
        public InputListType getInputList() {
            return inputList;
        }

        /**
         * Sets the value of the inputList property.
         * 
         * @param value
         *     allowed object is
         *     {@link InputListType }
         *     
         */
        public void setInputList(InputListType value) {
            this.inputList = value;
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
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}AbstractModes"/>
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
        "abstractModes"
    })
    public static class Modes {

        @XmlElementRef(name = "AbstractModes", namespace = "http://www.opengis.net/sensorml/2.0", type = JAXBElement.class)
        protected JAXBElement<? extends AbstractModesType> abstractModes;

        /**
         * Gets the value of the abstractModes property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link AbstractModesType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ModeChoiceType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractModesType> getAbstractModes() {
            return abstractModes;
        }

        /**
         * Sets the value of the abstractModes property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link AbstractModesType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ModeChoiceType }{@code >}
         *     
         */
        public void setAbstractModes(JAXBElement<? extends AbstractModesType> value) {
            this.abstractModes = value;
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
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}OutputList"/>
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
        "outputList"
    })
    public static class Outputs {

        @XmlElement(name = "OutputList", required = true)
        protected OutputListType outputList;

        /**
         * Gets the value of the outputList property.
         * 
         * @return
         *     possible object is
         *     {@link OutputListType }
         *     
         */
        public OutputListType getOutputList() {
            return outputList;
        }

        /**
         * Sets the value of the outputList property.
         * 
         * @param value
         *     allowed object is
         *     {@link OutputListType }
         *     
         */
        public void setOutputList(OutputListType value) {
            this.outputList = value;
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
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}ParameterList"/>
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
        "parameterList"
    })
    public static class Parameters {

        @XmlElement(name = "ParameterList", required = true)
        protected ParameterListType parameterList;

        /**
         * Gets the value of the parameterList property.
         * 
         * @return
         *     possible object is
         *     {@link ParameterListType }
         *     
         */
        public ParameterListType getParameterList() {
            return parameterList;
        }

        /**
         * Sets the value of the parameterList property.
         * 
         * @param value
         *     allowed object is
         *     {@link ParameterListType }
         *     
         */
        public void setParameterList(ParameterListType value) {
            this.parameterList = value;
        }

    }

}
