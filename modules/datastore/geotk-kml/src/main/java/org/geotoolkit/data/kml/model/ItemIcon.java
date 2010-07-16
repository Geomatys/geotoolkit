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

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps ItemIcon element.</p>
 *
 * <pre>
 * &lt;element name="ItemIcon" type="kml:ItemIconType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ItemIconType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:state" minOccurs="0"/>
 *              &lt;element ref="kml:href" minOccurs="0"/>
 *              &lt;element ref="kml:ItemIconSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ItemIconObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ItemIconSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ItemIconObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ItemIcon extends AbstractObject {

    /**
     *
     * @return the list of icon states.
     */
    public List<ItemIconState> getStates();

    /**
     *
     * @return the href.
     */
    public String getHref();

    /**
     *
     * @param states
     */
    public void setStates(List<ItemIconState> states);

    /**
     *
     * @param href
     */
    public void setHref(String href);

}
