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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Can be assimilated as a group of layer. Can contain other {@linkplain MapItem groups}
 * or {@linkplain MapLayer layers} directly.
 *
 * @author Cédric Briançon
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "mapItem",
    "mapLayer"
})
@XmlRootElement(name = "MapItem")
public class MapItem {
    @XmlElement(name = "MapItem")
    private List<MapItem> mapItem = new ArrayList<MapItem>();

    @XmlElement(name = "MapLayer")
    private List<MapLayer> mapLayer = new ArrayList<MapLayer>();

    MapItem() {
    }

    public MapItem(final List<MapItem> mapItem, final List<MapLayer> mapLayer) {
        this.mapItem = mapItem;
        this.mapLayer = mapLayer;
    }

    public List<MapItem> getMapItem() {
        return mapItem;
    }

    public void setMapItem(final List<MapItem> mapItem) {
        this.mapItem = mapItem;
    }

    public List<MapLayer> getMapLayer() {
        return mapLayer;
    }

    public void setMapLayer(final List<MapLayer> mapLayer) {
        this.mapLayer = mapLayer;
    }
}
