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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier" maxOccurs="unbounded"/>
 *       &lt;/sequence>
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
    "identifier"
})
@XmlRootElement(name = "DescribeProcess")
public class DescribeProcess extends RequestBaseType implements org.geotoolkit.wps.xml.DescribeProcess {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    protected List<CodeType> identifier;
    
    public DescribeProcess() {
        
    }
    
    public DescribeProcess(String service, String language, List<CodeType> identifiers) {
        super(service, language);
        this.identifier = identifiers;
    }

    /**
     * Unordered list of one or more identifiers of the processes for which the client is requesting detailed descriptions. This element shall be repeated for each process for which a description is requested. These Identifiers are unordered, but the WPS shall return the process descriptions in the order in which they were requested.Gets the value of the identifier property.
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

}
