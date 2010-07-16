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
package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps premiseNumberSuffix element.</p>
 *
 * <p>A in 12A</p>
 *
 * <pre>
 * &lt;xs:element name="PremiseNumberSuffix">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NumberSuffixSeparator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumberSuffix extends GenericTypedGrPostal {

    /**
     * <p>12-A where 12 is number and A is suffix and "-" is the separator.</p>
     * 
     * @return
     */
    public String getNumberSuffixSeparator();
}
