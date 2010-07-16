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
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultListStyle extends DefaultAbstractSubStyle implements ListStyle {

    private ListItem listItem;
    private Color bgColor;
    private List<ItemIcon> itemIcons;
    private int maxSnippetLines;

    /**
     * 
     */
    public DefaultListStyle() {
        this.bgColor = DEF_BG_COLOR;
        this.itemIcons = EMPTY_LIST;
        this.maxSnippetLines = DEF_MAX_SNIPPET_LINES;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractSubStyleSimpleExtensions
     * @param abstractSubStyleObjectExtensions
     * @param listItem
     * @param bgColor
     * @param itemIcons
     * @param maxSnippetLines
     * @param listStyleSimpleExtensions
     * @param listStyleObjectExtensions
     */
    public DefaultListStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleType> listStyleSimpleExtensions,
            List<AbstractObject> listStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractSubStyleSimpleExtensions,
                abstractSubStyleObjectExtensions);
        this.listItem = listItem;
        this.bgColor = bgColor;
        this.itemIcons = itemIcons;
        this.maxSnippetLines = maxSnippetLines;
        if (listStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LIST_STYLE).addAll(listStyleSimpleExtensions);
        }
        if (listStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LIST_STYLE).addAll(listStyleObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ListItem getListItem() {
        return this.listItem;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Color getBgColor() {
        return this.bgColor;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<ItemIcon> getItemIcons() {
        return this.itemIcons;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getMaxSnippetLines() {
        return this.maxSnippetLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setListItem(ListItem listItem) {
        this.listItem = listItem;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setItemIcons(List<ItemIcon> itemIcons) {
        this.itemIcons = itemIcons;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxSnippetLines(int maxSnippetLines) {
        this.maxSnippetLines = maxSnippetLines;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tListStyleDefault : "
                + "\n\tlistItem : " + this.listItem
                + "\n\tbgColor : " + this.bgColor
                + "\n\titemIcons : " + this.itemIcons
                + "\n\tmaxSnippetLines : " + this.maxSnippetLines;
        return resultat;
    }
}
