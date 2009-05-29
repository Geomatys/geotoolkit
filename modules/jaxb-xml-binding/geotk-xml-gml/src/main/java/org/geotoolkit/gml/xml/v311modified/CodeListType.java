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
package org.geotoolkit.gml.xml.v311modified;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * List of values on a uniform nominal scale.  List of text tokens.   
 * In a list context a token should not include any spaces, so xsd:Name is used instead of xsd:string.   
 * If a codeSpace attribute is present, then its value is a reference to 
 * a Reference System for the value, a dictionary or code list.
 * 
 * <p>Java class for CodeListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CodeListType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/gml>NameList">
 *       &lt;attribute name="codeSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodeListType", propOrder = {
    "value"
})
public class CodeListType {

    @XmlValue
    private List<String> value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String codeSpace;

    /**
     * An empty constructor used by JAXB.
     */
    CodeListType(){
        
    }
    
    /**
     * Build a new List of code (without namespace).
     */
    public CodeListType(List<String> value) {
        this.value = value; 
    }
    
    /**
     * Build a new List of code with the specified namespace.
     */
    public CodeListType(List<String> value, String codeSpace) {
        this.value     = value; 
        this.codeSpace = codeSpace;
    }
    
    /**
     * Build a new List of code (without namespace),
     * with all the element of the list in the parameters.
     */
    public CodeListType(String... values) {
        this.value = new ArrayList<String>();
        for (String element:values) {
            this.value.add(element);
        }
    }
    
    /**
     * XML List based on XML Schema Name type.  
     * An element of this type contains a space-separated list of Name values.
     * (unmodifiable)
     */
    public List<String> getValue() {
        return Collections.unmodifiableList(value);
    }

    /**
     * Gets the value of the codeSpace property.
     */
    public String getCodeSpace() {
        return codeSpace;
    }
}
