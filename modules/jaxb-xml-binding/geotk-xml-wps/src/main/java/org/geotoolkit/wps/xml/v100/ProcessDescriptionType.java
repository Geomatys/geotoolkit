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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.wps.xml.ProcessDescription;


/**
 * Full description of a process.
 *
 * <p>Java class for ProcessDescriptionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProcessDescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}ProcessBriefType">
 *       &lt;sequence>
 *         &lt;element name="DataInputs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Input" type="{http://www.opengis.net/wps/1.0.0}InputDescriptionType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ProcessOutputs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Output" type="{http://www.opengis.net/wps/1.0.0}OutputDescriptionType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="storeSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="statusSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessDescriptionType", propOrder = {
    "dataInputs",
    "processOutputs"
})
public class ProcessDescriptionType extends ProcessBriefType implements ProcessDescription {

    @XmlElement(name = "DataInputs", namespace = "")
    protected ProcessDescriptionType.DataInputs dataInputs;
    @XmlElement(name = "ProcessOutputs", namespace = "", required = true)
    protected ProcessDescriptionType.ProcessOutputs processOutputs;
    @XmlAttribute
    protected Boolean storeSupported;
    @XmlAttribute
    protected Boolean statusSupported;


    public ProcessDescriptionType() {

    }

    public ProcessDescriptionType(CodeType identifier, LanguageStringType title, LanguageStringType _abstract,
            String processVersion,final boolean supportStorage, final boolean statusSupported, List<InputDescriptionType> inputs,
             List<OutputDescriptionType> outputs) {
        super(identifier, title, _abstract, processVersion);
        this.statusSupported = statusSupported;
        this.storeSupported = supportStorage;
        if (inputs != null && !inputs.isEmpty()) {
            this.dataInputs = new DataInputs(inputs);
        }
        if (outputs != null && !outputs.isEmpty()) {
            this.processOutputs = new ProcessOutputs(outputs);
        }
    }

    /**
     * Gets the value of the dataInputs property.
     *
     * @return
     *     possible object is
     *     {@link ProcessDescriptionType.DataInputs }
     *
     */
    public ProcessDescriptionType.DataInputs getDataInputs() {
        return dataInputs;
    }

    /**
     * Sets the value of the dataInputs property.
     *
     * @param value
     *     allowed object is
     *     {@link ProcessDescriptionType.DataInputs }
     *
     */
    public void setDataInputs(final ProcessDescriptionType.DataInputs value) {
        this.dataInputs = value;
    }

    /**
     * Gets the value of the processOutputs property.
     *
     * @return
     *     possible object is
     *     {@link ProcessDescriptionType.ProcessOutputs }
     *
     */
    public ProcessDescriptionType.ProcessOutputs getProcessOutputs() {
        return processOutputs;
    }

    /**
     * Sets the value of the processOutputs property.
     *
     * @param value
     *     allowed object is
     *     {@link ProcessDescriptionType.ProcessOutputs }
     *
     */
    public void setProcessOutputs(final ProcessDescriptionType.ProcessOutputs value) {
        this.processOutputs = value;
    }

    /**
     * Gets the value of the storeSupported property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isStoreSupported() {
        if (storeSupported == null) {
            return false;
        } else {
            return storeSupported;
        }
    }

    /**
     * Sets the value of the storeSupported property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setStoreSupported(final Boolean value) {
        this.storeSupported = value;
    }

    /**
     * Gets the value of the statusSupported property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isStatusSupported() {
        if (statusSupported == null) {
            return false;
        } else {
            return statusSupported;
        }
    }

    /**
     * Sets the value of the statusSupported property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setStatusSupported(final Boolean value) {
        this.statusSupported = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (statusSupported != null) {
            sb.append("Status supported:").append(statusSupported).append('\n');
        }
        if (storeSupported != null) {
            sb.append("Store supported:").append(storeSupported).append('\n');
        }
        if (dataInputs != null) {
            sb.append("Data inputs:").append(dataInputs).append('\n');
        }
        if (processOutputs != null) {
            sb.append("Process outputs:").append(processOutputs).append('\n');
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
        if (object instanceof ProcessDescriptionType && super.equals(object)) {
            final ProcessDescriptionType that = (ProcessDescriptionType) object;
            return Objects.equals(this.dataInputs, that.dataInputs) &&
                   Objects.equals(this.processOutputs, that.processOutputs) &&
                   Objects.equals(this.storeSupported, that.storeSupported) &&
                   Objects.equals(this.statusSupported, that.statusSupported);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.dataInputs);
        hash = 23 * hash + Objects.hashCode(this.processOutputs);
        hash = 23 * hash + Objects.hashCode(this.storeSupported);
        hash = 23 * hash + Objects.hashCode(this.statusSupported);
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
     *         &lt;element name="Input" type="{http://www.opengis.net/wps/1.0.0}InputDescriptionType" maxOccurs="unbounded"/>
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
        "input"
    })
    public static class DataInputs {

        @XmlElement(name = "Input", namespace = "", required = true)
        protected List<InputDescriptionType> input;

        public DataInputs() {

        }

        public DataInputs(List<InputDescriptionType> input) {
            this.input = input;
        }

        /**
         * Gets the value of the input property.
         *
         * @return Objects of the following type(s) are allowed in the list
         * {@link InputDescriptionType }
         *
         *
         */
        public List<InputDescriptionType> getInput() {
            if (input == null) {
                input = new ArrayList<>();
            }
            return this.input;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[DataInputs]\n");
            if (input != null) {
                sb.append("Inputs:\n");
                for (InputDescriptionType out : input) {
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
            if (object instanceof DataInputs) {
                final DataInputs that = (DataInputs) object;
                return Objects.equals(this.input, that.input);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(this.input);
            return hash;
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
     *         &lt;element name="Output" type="{http://www.opengis.net/wps/1.0.0}OutputDescriptionType" maxOccurs="unbounded"/>
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

        @XmlElement(name = "Output", namespace = "", required = true)
        protected List<OutputDescriptionType> output;

        public ProcessOutputs() {

        }

        public ProcessOutputs(List<OutputDescriptionType> output) {
            this.output = output;
        }

        /**
         * Gets the value of the output property.
         *
         * @return Objects of the following type(s) are allowed in the list
         * {@link OutputDescriptionType }
         *
         *
         */
        public List<OutputDescriptionType> getOutput() {
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
                for (OutputDescriptionType out : output) {
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
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(this.output);
            return hash;
        }

    }

}
