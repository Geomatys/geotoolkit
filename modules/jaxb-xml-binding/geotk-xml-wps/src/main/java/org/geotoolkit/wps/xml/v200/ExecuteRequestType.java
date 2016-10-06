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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.wps.xml.Execute;


/**
 * 
 * Schema for a WPS Execute operation request, to execute
 * one identified process with the given data and provide the requested
 * output data.
 * 			
 * 
 * <p>Java class for ExecuteRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExecuteRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Identifier"/>
 *         &lt;element name="Input" type="{http://www.opengis.net/wps/2.0}DataInputType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}OutputDefinitionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="mode" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="sync"/>
 *             &lt;enumeration value="async"/>
 *             &lt;enumeration value="auto"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="response" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="raw"/>
 *             &lt;enumeration value="document"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExecuteRequestType", propOrder = {
    "identifier",
    "input",
    "output"
})
public class ExecuteRequestType extends RequestBaseType implements Execute {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/2.0", required = true)
    protected CodeType identifier;
    @XmlElement(name = "Input")
    protected List<DataInputType> input;
    @XmlElement(name = "Output", required = true)
    protected List<OutputDefinitionType> output;
    @XmlAttribute(name = "mode", required = true)
    protected String mode;
    @XmlAttribute(name = "response", required = true)
    protected String response;

    public ExecuteRequestType() {
        
    }
    
    public ExecuteRequestType(CodeType identifier, List<DataInputType> input, List<OutputDefinitionType> output, String response) {
        this.identifier = identifier;
        this.input = input;
        this.output = output;
        this.response = response;
    }
    
    /**
     * 
     * Identifier of the process to be executed. All valid process identifiers are
     * listed in the wps:Contents section of the Capabilities document.
     * 							
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    @Override
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setIdentifier(CodeType value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the input property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link DataInputType }
     * 
     * 
     * @return 
     */
    @Override
    public List<DataInputType> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
    }

    /**
     * Gets the value of the output property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link OutputDefinitionType }
     * 
     * 
     * @return 
     */
    @Override
    public List<OutputDefinitionType> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMode(String value) {
        this.mode = value;
    }

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponse(String value) {
        this.response = value;
    }
    
    @Override
    public String getLanguage() {
        return "en-EN";
    }

    @Override
    public boolean isLineage() {
        return false;
    }

    @Override
    public boolean isRawOutput() {
        return "raw".equalsIgnoreCase(response);
    }

    @Override
    public boolean isDocumentOutput() {
        return "document".equalsIgnoreCase(response);
    }

    @Override
    public boolean isStatus() {
        return true;
    }

    @Override
    public boolean isStoreExecuteResponse() {
        return "document".equalsIgnoreCase(response); // TODO see with async parameter
    }
}
