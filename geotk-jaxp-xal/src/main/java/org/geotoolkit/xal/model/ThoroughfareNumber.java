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
 * <p>THis interface maps ThoroughfareNumber element.</p>
 *
 * <p>Eg.: 23 Archer street or 25/15 Zero Avenue, etc.</p>
 *
 * <pre>
 * &lt;xs:element name="ThoroughfareNumber">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="SingleRangeEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attribute name="Indicator">
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="AfterBeforeEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="AfterBeforeTypeNameEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 * @module
 */
public interface ThoroughfareNumber extends GenericTypedGrPostal {

    /**
     *
     * @return
     */
    SingleRangeEnum getNumberType();


    /**
     * <p>No. in Street No.12 or "#" in Street # 12, etc.</p>
     *
     * @return
     */
    String getIndicator();

    /**
     *
     * @return
     */
    AfterBeforeEnum getIndicatorOccurence();

    /**
     *
     * @return
     */
    AfterBeforeTypeNameEnum getNumberOccurence();

    /**
     *
     * @param numberType
     */
    void setNumberType(SingleRangeEnum numberType);

    /**
     *
     * @param indicator
     */
    void setIndicator(String indicator);

    /**
     *
     * @param indicatorOccurrence
     */
    void setIndicatorOccurrence(AfterBeforeEnum indicatorOccurrence);

    /**
     *
     * @param numberOccurrence
     */
    void setNumberOccurrence(AfterBeforeTypeNameEnum numberOccurrence);
}
