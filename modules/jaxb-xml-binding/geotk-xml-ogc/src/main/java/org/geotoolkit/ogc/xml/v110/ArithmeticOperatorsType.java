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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.Functions;


/**
 * <p>Java class for ArithmeticOperatorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArithmeticOperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/ogc}SimpleArithmetic"/>
 *         &lt;element name="Functions" type="{http://www.opengis.net/ogc}FunctionsType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArithmeticOperatorsType", propOrder = {
    "simpleArithmetic",
    "functions"
})
public class ArithmeticOperatorsType implements ArithmeticOperators {

    @XmlElement(name = "Functions")
    private FunctionType functions;     
    @XmlElement(name = "SimpleArithmetic")
    private SimpleArithmetic simpleArithmetic;
    
    /**
     * An empty constructor used by JAXB
     */
    public ArithmeticOperatorsType() {
        
    }
    
    /**
     * Build a new Arithmetic Operators
     */
    public ArithmeticOperatorsType(final boolean simple, final Functions functions) {
        if (simple) {
            this.simpleArithmetic = new SimpleArithmetic();
        }
        if (functions != null) {
            this.functions = new FunctionType(functions);
        }
    }

    /**
     * Gets the value of the simpleArithmetic.
     */
    public SimpleArithmetic getSimpleArithmetic() {
        return simpleArithmetic;
    }
    
    /**
     * Gets the value of the simpleArithmetic.
     */
    public Functions getFunctions() {
        throw new UnsupportedOperationException("Operation Not supported yet");
    }

    public boolean hasSimpleArithmetic() {
        return simpleArithmetic != null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ArithmeticOperatorsType]").append("\n");
        if (simpleArithmetic != null) {
            sb.append("simpleArithmetic: ").append(simpleArithmetic).append('\n');
        }
        if (functions != null) {
            sb.append("functions: ").append(functions).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

       if (object instanceof ArithmeticOperatorsType) {
           final ArithmeticOperatorsType that = (ArithmeticOperatorsType) object;
       
            return Utilities.equals(this.functions, that.functions) &&
                   Utilities.equals(this.simpleArithmetic, that.simpleArithmetic);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.functions != null ? this.functions.hashCode() : 0);
        hash = 23 * hash + (this.simpleArithmetic != null ? this.simpleArithmetic.hashCode() : 0);
        return hash;
    }
}
