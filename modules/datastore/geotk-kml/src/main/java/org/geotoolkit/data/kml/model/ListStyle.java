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

import java.awt.Color;
import java.util.List;

/**
 * <p>This interface maps ListStyle.</p>
 *
 * <pre>
 * &lt;element name="ListStyle" type="kml:ListStyleType" substitutionGroup="kml:AbstractSubStyleGroup"/>
 *
 * &lt;complexType name="ListStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractSubStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:listItemType" minOccurs="0"/>
 *              &lt;element ref="kml:bgColor" minOccurs="0"/>
 *              &lt;element ref="kml:ItemIcon" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:maxSnippetLines" minOccurs="0"/>
 *              &lt;element ref="kml:ListStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ListStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ListStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ListStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface ListStyle extends AbstractSubStyle {

    /**
     *
     * @return
     */
    ListItem getListItem();

    /**
     *
     * @return
     */
    Color getBgColor();

    /**
     *
     * @return
     */
    List<ItemIcon> getItemIcons();

    /**
     *
     * @return
     */
    int getMaxSnippetLines();
    
    /**
     *
     * @param listItem
     */
    void setListItem(ListItem listItem);

    /**
     *
     * @param bgColor
     */
    void setBgColor(Color bgColor);

    /**
     *
     * @param itemIcons
     */
    void setItemIcons(List<ItemIcon> itemIcons);

    /**
     *
     * @param maxSnippetLines
     */
    void setMaxSnippetLines(int maxSnippetLines);

}
