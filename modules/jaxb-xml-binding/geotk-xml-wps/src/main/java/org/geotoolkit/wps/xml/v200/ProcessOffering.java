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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
@XmlType(name = "", propOrder = {
    "process",
    "any"
})
@XmlRootElement(name = "ProcessOffering")
public class ProcessOffering implements ProcessProperties {

    private ProcessDescription process;
    @XmlAnyElement(lax = true)
    protected Object any;

    protected List<JobControlOptions> jobControlOptions;
    protected List<DataTransmissionMode> outputTransmission;
    protected String processVersion;
    protected String processModel;

    public ProcessOffering() {}

    public ProcessOffering(ProcessDescription process) {
        this.process = process;
        /* WPS 1 retro-compatibility flag. If store is true, we can use references.
         * If it's false, only values can be used. But if the value is null, we
         * don't know which modes are accepted, and we cannot set any flag.
         */
        if (Boolean.TRUE.equals(process.isStoreSupported())) {
            getOutputTransmission().add(DataTransmissionMode.REFERENCE);
        } else if (Boolean.FALSE.equals(process.isStoreSupported())) {
            getOutputTransmission().add(DataTransmissionMode.VALUE);
        }

        if (process.processVersion != null) {
            processVersion = process.processVersion;
        }
    }

    public ProcessOffering(org.geotoolkit.wps.json.ProcessOffering pr) {
        if (pr != null) {
            if (pr.getProcess() != null) {
                this.process = new ProcessDescription(pr.getProcess());
            }
            this.processVersion = pr.getProcessVersion();
            this.jobControlOptions = pr.getJobControlOptions();
            this.outputTransmission = pr.getOutputTransmission();
        }
    }

    /**
     * Gets the value of the process property.
     *
     * @return
     *     possible object is
     *     {@link ProcessDescription }
     *
     */
    @XmlElement(name = "Process")
    @XmlJavaTypeAdapter(FilterV2.ProcessDescription.class)
    public ProcessDescription getProcess() {
        return process;
    }

    /**
     * Sets the value of the process property.
     *
     * @param value
     *     allowed object is
     *     {@link ProcessDescription }
     *
     */
    public void setProcess(ProcessDescription value) {
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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the jobControlOptions property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJobControlOptions().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    @XmlAttribute(name = "jobControlOptions", required = true)
    //@XmlJavaTypeAdapter(FilterV2.JobControlOptions.class)
    public List<JobControlOptions> getJobControlOptions() {
        if (jobControlOptions == null) {
            jobControlOptions = new ArrayList<>();
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
     * {@link DataTransmissionMode }
     *
     *
     */
    @XmlAttribute(name = "outputTransmission")
    public List<DataTransmissionMode> getOutputTransmission() {
        if (FilterByVersion.isV2()) {
            if (outputTransmission == null) {
                outputTransmission = new ArrayList<>();
            }
            return this.outputTransmission;
        }
        return null;
    }

    /**
     * Gets the value of the processVersion property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @XmlAttribute(name = "processVersion")
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
        if (process != null) {
            process.processVersion = value;
        }
    }

    /**
     * Gets the value of the processModel property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @XmlAttribute(name = "processModel")
    public String getProcessModel() {
        if (FilterByVersion.isV2()) {
            if (processModel == null) {
                return "native";
            } else {
                return processModel;
            }
        }
        return null;
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
