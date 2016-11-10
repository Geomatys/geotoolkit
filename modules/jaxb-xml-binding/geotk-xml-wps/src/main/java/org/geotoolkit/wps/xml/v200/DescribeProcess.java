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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.CodeType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Identifier" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifier"
})
@XmlRootElement(name = "DescribeProcess")
public class DescribeProcess extends RequestBaseType implements org.geotoolkit.wps.xml.DescribeProcess {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/2.0", required = true)
    protected List<CodeType> identifier;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;

    public DescribeProcess() {
        
    }
    
    public DescribeProcess(String service, String language, List<CodeType> identifiers) {
        super(service);
        this.identifier = identifiers;
        this.lang = language;
    }
    
    /**
     * 
     * One or more identifiers for which the process description shall be obtained.
     * "ALL"" is reserved to retrieve the  descriptions for all available process offerings.
     * Gets the value of the identifier property.
     * 
     * @return Objects of the following type(s) are allowed in the list
     * {@link CodeType }
     * 
     * 
     */
    @Override
    public List<CodeType> getIdentifier() {
        if (identifier == null) {
            identifier = new ArrayList<>();
        }
        return this.identifier;
    }

    /**
     * 
     * RFC 4646 language code of the human-readable text (e.g. "en-CA") in the process description.
     * 							
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    @Override
    public String getLanguage() {
        return lang;
    }

}
