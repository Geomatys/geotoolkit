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
package org.geotoolkit.ogc.xml.v110;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.capability.Functions;


/**
 * <p>Java class for FunctionNamesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FunctionNamesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="FunctionName" type="{http://www.opengis.net/ogc}FunctionNameType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FunctionNamesType", propOrder = {
    "functionNames"
})
public class FunctionNamesType implements Functions {

    @XmlElement(name = "FunctionName", required = true)
    private List<FunctionNameType> functionNames;


    /**
     * Empty construtor used by JAXB
     */
    public FunctionNamesType() {
        
    }
    
    /**
     *Build a new Function names
     */
    public FunctionNamesType(List<FunctionNameType> functionNames) {
        this.functionNames = functionNames;
    }
    
    public Collection<FunctionName> getFunctionNames() {
        List<FunctionName> result = new ArrayList<FunctionName>();
        if (functionNames == null) {
            functionNames = new ArrayList<FunctionNameType>();
        }
        for (FunctionNameType f: functionNames) {
            result.add(f);
        }
        return result;
    }

    public FunctionName getFunctionName(String name) {
       if (functionNames == null) {
            functionNames = new ArrayList<FunctionNameType>();
       }
       for (FunctionNameType f: functionNames) {
            if (f.getName().equals(name)) return f;
       }
       return null;
    }

}
