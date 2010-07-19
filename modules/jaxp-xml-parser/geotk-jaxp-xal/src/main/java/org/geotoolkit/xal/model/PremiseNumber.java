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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.xal.model;

/**
 * <p>This interface maps PremiseNumber element.</p>
 *
 * <p>Specification of the identifier of the premise (house, building, etc).
 * Premises in a street are often uniquely identified by means of consecutive identifiers.
 * The identifier can be a number, a letter or any combination of the two.</p>
 *
 * <pre>
 * &lt;xs:element name="PremiseNumber">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NumberType">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attribute name="Indicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="IndicatorOccurrence">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="NumberTypeOccurrence">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumber extends GenericTypedGrPostal{

    /**
     * <p>Building 12-14 is "Range" and Building 12 is "Single".</p>
     *
     * @return
     */
    public SingleRangeEnum getNumberType();

    /**
     * <p>No. in House No.12, # in #12, etc.</p>
     *
     * @return
     */
    public String getIndicator();

    /**
     * <p>No. occurs before 12 No.12</p>
     *
     * @return
     */
    public AfterBeforeEnum getIndicatorOccurrence();

    /**
     * <p>12 in BUILDING 12 occurs "after" premise type BUILDING.</p>
     * @return
     */
    public AfterBeforeEnum getNumberTypeOccurrence();
}
