/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml.v110modified;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.opengis.filter.capability.FunctionName;


/**
 * <p>Java class for FunctionNameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FunctionNameType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="nArgs" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FunctionNameType", propOrder = {
    "value"
})
public class FunctionNameType implements FunctionName {

    @XmlValue
    private String value;
    @XmlAttribute(required = true)
    private String nArgs;
    
    @XmlTransient
    private List<String> argumentNames;
    @XmlTransient
    private Logger logger = Logger.getAnonymousLogger();

    /**
     * An empty constructor used by JAXB
     */
    public FunctionNameType() {
        
    }
    
    /**
     * An empty constructor used by JAXB
     */
    public FunctionNameType(String name, int nArgs) {
        this.value = name;
        this.nArgs = nArgs + "";
    }
    
    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the value of the nArgs property.
     */
    public String getNArgs() {
        return nArgs;
    }

    public int getArgumentCount() {
        int result = 0;
        try {
            result = Integer.parseInt(nArgs);
        } catch (NumberFormatException e) {
            logger.severe("unable To parse number of argument in function " + value + " the value " + nArgs + " is not a number" );
        }
        return result;
    }

    public List<String> getArgumentNames() {
        if( argumentNames == null ){
            argumentNames = generateArgumentNames(getArgumentCount());
        }
        return argumentNames;
    }

    private static List<String> generateArgumentNames( int count ){
        List<String> names = Arrays.asList( new String[count]);
        for( int i=0; i < count; i++){
            names.set(i, "arg"+i );
        }
        return names;
    }

    public String getName() {
        return value;
    }
}
