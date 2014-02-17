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

import java.util.List;

/**
 * <p>This interface maps ThoroughfareNumberRange element.</p>
 *
 * <p>A container to represent a range of numbers (from x thru y)for a thoroughfare. eg. 1-2 Albert Av.</p>
 *
 * <pre>
 * &lt;xs:element name="ThoroughfareNumberRange">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="ThoroughfareNumberFrom">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfareNumberTo">...
 *          &lt;/xs:element>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="OddEvenEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Indicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Separator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="AfterBeforeEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="AfterBeforeTypeNameEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface ThoroughfareNumberRange {

    /**
     * 
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    ThoroughfareNumberFrom getThoroughfareNumberFrom();

    /**
     *
     * @return
     */
    ThoroughfareNumberTo getThoroughfareNumberTo();

    /**
     * <p>Thoroughfare number ranges are odd or even.</p>
     * 
     * @return
     */
    OddEvenEnum getRangeType();

    /**
     * <p>"No." No.12-13</p>
     *
     * @return
     */
    String getIndicator();

    /**
     * <p>"-" in 12-14  or "Thru" in 12 Thru 14 etc.</p>
     *
     * @return
     */
    String getSeparator();

    /**
     *
     * @return
     */
    AfterBeforeEnum getIndicatorOccurence();

    /**
     * 
     * @return
     */
    AfterBeforeTypeNameEnum getNumberRangeOccurence();

    /**
     *
     * @return
     */
    String getType();

    /**
     *
     * @return
     */
    GrPostal getGrPostal();

    /**
     *
     * @param addressLines
     */
    void setAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     *
     * @param thoroughfareNumberFrom
     */
    void setThoroughfareNumberFrom(ThoroughfareNumberFrom thoroughfareNumberFrom);

    /**
     *
     * @param thoroughfareNumberTo
     */
    void setThoroughfareNumberTo(ThoroughfareNumberTo thoroughfareNumberTo);

    /**
     *
     * @param rangeType
     */
    void setRangeType(OddEvenEnum rangeType);

    /**
     *
     * @param indicator
     */
    void setIndicator(String indicator);

    /**
     *
     * @param separator
     */
    void setSeparator(String separator);

    /**
     *
     * @param indicatorOccurrence
     */
    void setIndicatorOccurrence(AfterBeforeEnum indicatorOccurrence);

    /**
     *
     * @param numberRangeOccurrence
     */
    void setNumberRangeOccurrence(AfterBeforeTypeNameEnum numberRangeOccurrence);

    /**
     *
     * @param type
     */
    void setType(String type);

    /**
     * 
     * @param grPostal
     */
    void setGrPostal(GrPostal grPostal);
}
