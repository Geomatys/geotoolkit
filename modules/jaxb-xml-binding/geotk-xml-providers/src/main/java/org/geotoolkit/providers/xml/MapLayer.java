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
 * Represents a layer, with its provider, its name and its style.
 *
 * @author Cédric Briançon
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dataReference",
    "styleReference"
})
@XmlRootElement(name = "MapLayer")
public class MapLayer extends MapItem {
    @XmlElement(name = "dataReference", required = true)
    private DataReference dataReference;

    @XmlElement(name = "styleReference")
    private StyleReference styleReference;

    MapLayer(){
    }

    public MapLayer(final DataReference dataReference, final StyleReference styleReference) {
        this.dataReference = dataReference;
        this.styleReference = styleReference;
    }

    public DataReference getDataReference() {
        return dataReference;
    }

    public void setDataReference(final DataReference dataReference) {
        this.dataReference = dataReference;
    }

    public StyleReference getStyleReference() {
        return styleReference;
    }

    public void setReferenceStyle(final StyleReference styleReference) {
        this.styleReference = styleReference;
    }
}
