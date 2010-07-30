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
package org.geotoolkit.data.kml.model;

import java.util.Calendar;

/**
 * <p>This interface maps TimeSpan element.</p>
 *
 * <pre>
 * &lt;element name="TimeSpan" type="kml:TimeSpanType" substitutionGroup="kml:AbstractTimePrimitiveGroup"/>
 *
 * &lt;complexType name="TimeSpanType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractTimePrimitiveType">
 *          &lt;sequence>
 *              &lt;element ref="kml:begin" minOccurs="0"/>
 *              &lt;element ref="kml:end" minOccurs="0"/>
 *              &lt;element ref="kml:TimeSpanSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:TimeSpanObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="TimeSpanSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="TimeSpanObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface TimeSpan extends AbstractTimePrimitive{

    /**
     *
     * @return
     */
    Calendar getBegin();

    /**
     *
     * @return
     */
    Calendar getEnd();

    /**
     *
     * @param begin
     */
    void setBegin(Calendar begin);

    /**
     *
     * @param end
     */
    void setEnd(Calendar end);

}