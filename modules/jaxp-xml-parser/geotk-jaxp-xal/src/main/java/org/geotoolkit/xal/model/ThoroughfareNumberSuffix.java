/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.xal.model;

/**
 * <p>This interface maps ThoroughfareNumberSuffix element.</p>
 *
 * <p>Suffix after the number. A in 12A Archer Street.</p>
 *
 * <pre>
 * &ltxs:element name="ThoroughfareNumberSuffix">
 *  &ltxs:complexType mixed="true">
 *      &ltxs:attribute name="NumberSuffixSeparator">
 *      &lt/xs:attribute>
 *      &ltxs:attribute name="Type"/>
 *      &ltxs:attributeGroup ref="grPostal"/>
 *      &ltxs:anyAttribute namespace="##other"/>
 *  &lt/xs:complexType>
 * &lt/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ThoroughfareNumberSuffix extends GenericTypedGrPostal{

    /**
     * <p>NEAR, ADJACENT TO, etc</p>
     * <p>12-A where 12 is number and A is suffix and "-" is the separator</p>
     * 
     * @return
     */
    String getNumberSuffixSeparator();

    /**
     * 
     * @param numberSuffixSeparator
     */
    void setNumberSuffixSeparator(String numberSuffixSeparator);
}
