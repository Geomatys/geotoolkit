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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.capability.ArithmeticOperators;


/**
 * <p>Java class for Arithmetic_OperatorsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Arithmetic_OperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/ogc}Simple_Arithmetic"/>
 *         &lt;element name="Functions" type="{http://www.opengis.net/ogc}FunctionsType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Arithmetic_OperatorsType", propOrder = {
    "simpleArithmeticOrFunctions"
})
public class ArithmeticOperatorsType implements ArithmeticOperators {

    @XmlElements({
        @XmlElement(name = "Simple_Arithmetic", type = SimpleArithmetic.class),
        @XmlElement(name = "Functions", type = FunctionsType.class)
    })
    private List<Object> simpleArithmeticOrFunctions;

    /**
     * Gets the value of the simpleArithmeticOrFunctions property.
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleArithmetic }
     * {@link FunctionsType }
     *
     *
     */
    public List<Object> getSimpleArithmeticOrFunctions() {
        if (simpleArithmeticOrFunctions == null) {
            simpleArithmeticOrFunctions = new ArrayList<>();
        }
        return this.simpleArithmeticOrFunctions;
    }

    @Override
    public boolean hasSimpleArithmetic() {
        for (Object o : simpleArithmeticOrFunctions) {
            if (o instanceof SimpleArithmetic) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FunctionsType getFunctions() {
        for (Object o : simpleArithmeticOrFunctions) {
            if (o instanceof FunctionsType) {
                return (FunctionsType) o;
            }
        }
        return null;
    }

}
