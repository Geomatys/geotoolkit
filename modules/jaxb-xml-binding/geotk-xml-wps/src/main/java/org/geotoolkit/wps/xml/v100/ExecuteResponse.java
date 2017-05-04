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
package org.geotoolkit.wps.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}ResponseBaseType">
 *       &lt;sequence>
 *         &lt;element name="Process" type="{http://www.opengis.net/wps/1.0.0}ProcessBriefType"/>
 *         &lt;element name="Status" type="{http://www.opengis.net/wps/1.0.0}StatusType"/>
 *         &lt;element name="DataInputs" type="{http://www.opengis.net/wps/1.0.0}DataInputsType" minOccurs="0"/>
 *         &lt;element name="OutputDefinitions" type="{http://www.opengis.net/wps/1.0.0}OutputDefinitionsType" minOccurs="0"/>
 *         &lt;element name="ProcessOutputs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Output" type="{http://www.opengis.net/wps/1.0.0}OutputDataType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="serviceInstance" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="statusLocation" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "process",
    "status",
    "dataInputs",
    "outputDefinitions",
    "processOutputs"
})
@XmlRootElement(name = "ExecuteResponse")
public class ExecuteResponse extends ResponseBaseType implements org.geotoolkit.wps.xml.ExecuteResponse {

    @XmlElement(name = "Process", required = true)
    protected ProcessBriefType process;
    @XmlElement(name = "Status", required = true)
    protected StatusType status;
    @XmlElement(name = "DataInputs")
    protected DataInputsType dataInputs;
    @XmlElement(name = "OutputDefinitions")
    protected OutputDefinitionsType outputDefinitions;
    @XmlElement(name = "ProcessOutputs")
    protected ExecuteResponse.ProcessOutputs processOutputs;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String serviceInstance;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String statusLocation;

    public ExecuteResponse() {}

    public ExecuteResponse(final String version, final String service, final String lang, final String serviceInstance, final ProcessBriefType process,
            DataInputsType dataInputs, OutputDefinitionsType outputDefinitions, List<OutputDataType> output, StatusType status) {
        super(service, version, lang);
        this.serviceInstance = serviceInstance;
        this.process = process;
        this.dataInputs = dataInputs;
        this.outputDefinitions = outputDefinitions;
        if (output != null) {
            this.processOutputs = new ProcessOutputs(output);
        }
        this.status = status;
    }

    public ExecuteResponse(final ExecuteResponse other) {
        this.setService(other.getService());
        this.setLang(other.getLang());
        this.setVersion(other.getVersion());
        this.setProcess(other.getProcess());
        this.setServiceInstance(other.getServiceInstance());
        this.setStatus(other.getStatus());
        this.setStatusLocation(other.getStatusLocation());
        this.setDataInputs(other.getDataInputs());
        this.setOutputDefinitions(other.getOutputDefinitions());
        this.setProcessOutputs(other.getProcessOutputs());
    }

    /**
     * Gets the value of the process property.
     *
     * @return
     *     possible object is
     *     {@link ProcessBriefType }
     *
     */
    public ProcessBriefType getProcess() {
        return process;
    }

    /**
     * Sets the value of the process property.
     *
     * @param value
     *     allowed object is
     *     {@link ProcessBriefType }
     *
     */
    public void setProcess(final ProcessBriefType value) {
        this.process = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link StatusType }
     *
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *
     */
    public void setStatus(final StatusType value) {
        this.status = value;
    }

    /**
     * Gets the value of the dataInputs property.
     *
     * @return
     *     possible object is
     *     {@link DataInputsType }
     *
     */
    public DataInputsType getDataInputs() {
        return dataInputs;
    }

    /**
     * Sets the value of the dataInputs property.
     *
     * @param value
     *     allowed object is
     *     {@link DataInputsType }
     *
     */
    public void setDataInputs(final DataInputsType value) {
        this.dataInputs = value;
    }

    /**
     * Gets the value of the outputDefinitions property.
     *
     * @return
     *     possible object is
     *     {@link OutputDefinitionsType }
     *
     */
    public OutputDefinitionsType getOutputDefinitions() {
        return outputDefinitions;
    }

    /**
     * Sets the value of the outputDefinitions property.
     *
     * @param value
     *     allowed object is
     *     {@link OutputDefinitionsType }
     *
     */
    public void setOutputDefinitions(final OutputDefinitionsType value) {
        this.outputDefinitions = value;
    }

    /**
     * Gets the value of the processOutputs property.
     *
     * @return
     *     possible object is
     *     {@link ExecuteResponse.ProcessOutputs }
     *
     */
    public ExecuteResponse.ProcessOutputs getProcessOutputs() {
        return processOutputs;
    }

    /**
     * Sets the value of the processOutputs property.
     *
     * @param value
     *     allowed object is
     *     {@link ExecuteResponse.ProcessOutputs }
     *
     */
    public void setProcessOutputs(final ExecuteResponse.ProcessOutputs value) {
        this.processOutputs = value;
    }

    /**
     * Gets the value of the serviceInstance property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getServiceInstance() {
        return serviceInstance;
    }

    /**
     * Sets the value of the serviceInstance property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setServiceInstance(final String value) {
        this.serviceInstance = value;
    }

    /**
     * Gets the value of the statusLocation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatusLocation() {
        return statusLocation;
    }

    /**
     * Sets the value of the statusLocation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setStatusLocation(final String value) {
        this.statusLocation = value;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (serviceInstance != null) {
            sb.append("Service instance:").append(serviceInstance).append('\n');
        }
        if (statusLocation != null) {
            sb.append("Status location:").append(statusLocation).append('\n');
        }
        if (dataInputs != null) {
            sb.append("Data inputs:\n").append(dataInputs).append('\n');
        }
        if (outputDefinitions != null) {
            sb.append("Output definitions:\n").append(outputDefinitions).append('\n');
        }
        if (process != null) {
            sb.append("Process:\n").append(process).append('\n');
        }
        if (processOutputs != null) {
            sb.append("Process outputs:\n").append(processOutputs).append('\n');
        }
        if (status != null) {
            sb.append("Status:\n").append(status).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ExecuteResponse && super.equals(object)) {
            final ExecuteResponse that = (ExecuteResponse) object;
            return Objects.equals(this.dataInputs, that.dataInputs) &&
                   Objects.equals(this.outputDefinitions, that.outputDefinitions) &&
                   Objects.equals(this.process, that.process) &&
                   Objects.equals(this.processOutputs, that.processOutputs) &&
                   Objects.equals(this.serviceInstance, that.serviceInstance) &&
                   Objects.equals(this.statusLocation, that.statusLocation) &&
                   Objects.equals(this.status, that.status);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.process);
        hash = 89 * hash + Objects.hashCode(this.status);
        hash = 89 * hash + Objects.hashCode(this.dataInputs);
        hash = 89 * hash + Objects.hashCode(this.outputDefinitions);
        hash = 89 * hash + Objects.hashCode(this.processOutputs);
        hash = 89 * hash + Objects.hashCode(this.serviceInstance);
        hash = 89 * hash + Objects.hashCode(this.statusLocation);
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
     *         &lt;element name="Output" type="{http://www.opengis.net/wps/1.0.0}OutputDataType" maxOccurs="unbounded"/>
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
        "output"
    })
    public static class ProcessOutputs {

        @XmlElement(name = "Output", required = true)
        protected List<OutputDataType> output;

        public ProcessOutputs() {

        }
        public ProcessOutputs(List<OutputDataType> output) {
            this.output = output;
        }

        /**
         * Gets the value of the output property.
         *
         * Objects of the following type(s) are allowed in the list
         * {@link OutputDataType }
         *
         *
         * @return
         */
        public List<OutputDataType> getOutput() {
            if (output == null) {
                output = new ArrayList<>();
            }
            return this.output;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[ProcessOutputs]\n");
            if (output != null) {
                sb.append("Outputs:\n");
                for (OutputDataType out : output) {
                    sb.append(out).append('\n');
                }
            }
            return sb.toString();
        }

        /**
         * Verify that this entry is identical to the specified object.
         *
         * @param object Object to compare
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof ProcessOutputs) {
                final ProcessOutputs that = (ProcessOutputs) object;
                return Objects.equals(this.output, that.output);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + Objects.hashCode(this.output);
            return hash;
        }
    }
}
