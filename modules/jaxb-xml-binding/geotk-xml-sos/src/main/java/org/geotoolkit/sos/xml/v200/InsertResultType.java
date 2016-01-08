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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sos.xml.InsertResult;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;


/**
 * <p>Java class for InsertResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsertResultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="template" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="resultValues" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertResultType", propOrder = {
    "template",
    "resultValues"
})
@XmlRootElement(name="InsertResult")
public class InsertResultType extends ExtensibleRequestType implements InsertResult {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String template;
    @XmlElement(required = true)
    private String resultValues;

    public InsertResultType() {
        
    }
    
    public InsertResultType(final String version, final String template, final String resultValues) {
        super(version, "SOS");
        this.template = template;
        this.resultValues = resultValues;
    }
    
    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemplate(String value) {
        this.template = value;
    }

    /**
     * Gets the value of the resultValues property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    @Override
    public String getResultValues() {
        return resultValues;
    }

    /**
     * Sets the value of the resultValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setResultValues(String value) {
        this.resultValues = value;
    }
    
    @Override
    public String getResponseFormat() {
        for (Object ext : getExtension()) {
            if (ext instanceof String) {
                String outputFormat = (String) ext;
                if (outputFormat.startsWith("responseFormat=")) {
                    return outputFormat.substring(15);
                }
            }
        }
        return "text/xml";
    }

}
