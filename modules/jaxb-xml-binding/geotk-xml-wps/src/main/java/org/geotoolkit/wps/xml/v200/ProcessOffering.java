/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCodeType;
import org.geotoolkit.ows.xml.LanguageString;
import org.w3c.dom.Element;


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
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Process"/>
 *           &lt;any processContents='lax' namespace='##other'/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wps/2.0}processPropertiesAttributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "process",
    "any"
})
@XmlRootElement(name = "ProcessOffering")
public class ProcessOffering implements org.geotoolkit.wps.xml.ProcessOffering {

    @XmlElement(name = "Process")
    protected ProcessDescriptionType process;
    @XmlAnyElement(lax = true)
    protected Object any;
    @XmlAttribute(name = "jobControlOptions", required = true)
    protected List<String> jobControlOptions;
    @XmlAttribute(name = "outputTransmission")
    protected List<DataTransmissionModeType> outputTransmission;
    @XmlAttribute(name = "processVersion")
    protected String processVersion;
    @XmlAttribute(name = "processModel")
    protected String processModel;

    public ProcessOffering() {
        
    }
    
    public ProcessOffering(ProcessDescriptionType process) {
        this.process = process;
    }

    @Override
    public AbstractCodeType getIdentifier() {
        return process.getIdentifier();
    }

    @Override
    public LanguageString getSingleAbstract() {
        return process.getAbstract().get(0);
    }
    
    /**
     * Gets the value of the process property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessDescriptionType }
     *     
     */
    public ProcessDescriptionType getProcess() {
        return process;
    }

    /**
     * Sets the value of the process property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessDescriptionType }
     *     
     */
    public void setProcess(ProcessDescriptionType value) {
        this.process = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     {@link Element }
     *     
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     {@link Element }
     *     
     */
    public void setAny(Object value) {
        this.any = value;
    }

    /**
     * Gets the value of the jobControlOptions property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getJobControlOptions() {
        if (jobControlOptions == null) {
            jobControlOptions = new ArrayList<String>();
        }
        return this.jobControlOptions;
    }

    /**
     * Gets the value of the outputTransmission property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outputTransmission property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutputTransmission().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataTransmissionModeType }
     * 
     * 
     */
    public List<DataTransmissionModeType> getOutputTransmission() {
        if (outputTransmission == null) {
            outputTransmission = new ArrayList<DataTransmissionModeType>();
        }
        return this.outputTransmission;
    }

    /**
     * Gets the value of the processVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessVersion() {
        return processVersion;
    }

    /**
     * Sets the value of the processVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessVersion(String value) {
        this.processVersion = value;
    }

    /**
     * Gets the value of the processModel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessModel() {
        if (processModel == null) {
            return "native";
        } else {
            return processModel;
        }
    }

    /**
     * Sets the value of the processModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessModel(String value) {
        this.processModel = value;
    }

}
