/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ogc.xml.v100;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.filter.capability.FunctionName;
import org.geotoolkit.filter.capability.Functions;


/**
 * <p>Java class for FunctionsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FunctionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Function_Names" type="{http://www.opengis.net/ogc}Function_NamesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FunctionsType", propOrder = {
    "functionNames"
})
public class FunctionsType extends Functions {

    @XmlElement(name = "Function_Names", required = true)
    private FunctionNamesType functionNames;

    /**
     * Gets the value of the functionNames property.
     */
    public FunctionNamesType getNames() {
        return functionNames;
    }

    /**
     * Sets the value of the functionNames property.
     */
    public void setFunctionNames(FunctionNamesType value) {
        this.functionNames = value;
    }

    @Override
    public Collection<FunctionName> getFunctionNames() {
        final List<FunctionName> result = new ArrayList<>();
        if (functionNames != null) {
            for (FunctionName fn : functionNames.getFunctionName()) {
                result.add(fn);
            }
        }
        return result;
    }

    @Override
    public FunctionName getFunctionName(String name) {
        if (functionNames != null) {
            for (FunctionName fn : functionNames.getFunctionName()) {
                if (fn.getName().equals(name))
                return fn;
            }
        }
        return null;
    }
}
