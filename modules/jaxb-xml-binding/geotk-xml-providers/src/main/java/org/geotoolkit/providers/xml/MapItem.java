/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.providers.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Can be assimilated as a group of layer. Can contain other {@linkplain MapItem groups}
 * or {@linkplain MapLayer layers} directly.
 *
 * @author Cédric Briançon
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "mapItems"
})
@XmlRootElement(name = "MapItem")
public class MapItem {
    @XmlElements({
        @XmlElement(name = "MapItem", type = MapItem.class),
        @XmlElement(name = "MapLayer", type = MapLayer.class)
    })
    private List<MapItem> mapItems = new ArrayList<MapItem>();

    MapItem() {
    }

    public MapItem(final List<MapItem> mapItems) {
        this.mapItems = mapItems;
    }

    public List<MapItem> getMapItems() {
        if (mapItems == null) {
            mapItems = new ArrayList<MapItem>();
        }
        return mapItems;
    }

    public void setMapItem(final List<MapItem> mapItems) {
        this.mapItems = mapItems;
    }
}
