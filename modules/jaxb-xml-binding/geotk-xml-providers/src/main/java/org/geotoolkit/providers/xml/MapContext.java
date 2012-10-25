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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Main element for this binding. A {@linkplain MapContext map context}
 * should contain a mandatory {@linkplain MapItem map item}.
 *
 * @author Cédric Briançon
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "mapItem"
})
@XmlRootElement(name = "MapContext")
public class MapContext {

    @XmlElement
    private String name;

    @XmlElement(name = "MapItem", required = true)
    private MapItem mapItem;

    /**
     * An empty constructor used by JAXB.
     */
    MapContext() {
    }

    public MapContext(final MapItem mapItem) {
        this(mapItem, null);
    }

    public MapContext(final MapItem mapItem, final String name) {
        this.mapItem = mapItem;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public MapItem getMapItem() {
        return mapItem;
    }

    public void setMapItem(final MapItem mapItem) {
        this.mapItem = mapItem;
    }
}
