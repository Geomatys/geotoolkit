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
 * <p>This interface maps DependentLocalityNumber element.</p>
 *
 * <p>Number of the dependent locality. Some areas are numbered.
 * Eg. SECTOR 5 in a Suburb as in India or SOI SUKUMVIT 10 as in Thailand.</p>
 *
 * <pre>
 * &lt;xs:element name="DependentLocalityNumber" minOccurs="0">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NameNumberOccurrence">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface DependentLocalityNumber {

    /**
     * 
     * @return
     */
    public String getContent();

    /**
     * <p>Eg. SECTOR occurs before 5 in SECTOR 5.</p>
     *
     * @return
     */
    public AfterBeforeEnum getNameNumberOccurrence();

    /**
     * 
     * @return
     */
    public GrPostal getGrPostal();
}
