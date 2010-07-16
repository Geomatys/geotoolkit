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
 * <p>This interface maps premiseNumberRange element.</p>
 *
 * <p>Specification for defining the premise number range. Some premises have number as Building C1-C7.</p>
 *
 * <pre>
 * &lt;xs:element name="PremiseNumberRange">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element name="PremiseNumberRangeFrom">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PremiseNumberRangeTo">...
 *          &lt;/xs:element>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="RangeType">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Indicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Separator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attribute name="IndicatorOccurence">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="NumberRangeOccurence">...
 *      &lt;/xs:attribute>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumberRange {

    /**
     *
     * @return
     */
    public PremiseNumberRangeFrom getPremiseNumberRangeFrom();
    
    /**
     * 
     * @return
     */
    public PremiseNumberRangeTo getPremiseNumberRangeTo();
    
    /**
     * <p>Eg. Odd or even number range.</p>
     * 
     * @return
     */
    public String getRangeType();
    
    /**
     * <p>Eg. No. in Building No:C1-C5</p>
     * 
     * @return
     */
    public String getIndicator();
    
    /**
     * <p>"-" in 12-14  or "Thru" in 12 Thru 14 etc.</p>
     * 
     * @return
     */
    public String getSeparator();
    
    /**
     * 
     * @return
     */
    public String getType();
    
    /**
     * <p>No.12-14 where "No." is before actual street number.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getIndicatorOccurrence();
    
    /**
     * <p>Building 23-25 where the number occurs after building name.</p>
     * 
     * @return
     */
    public AfterBeforeTypeNameEnum getNumberRangeOccurrence();

}
