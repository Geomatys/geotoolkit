/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d.ext.tiledebug;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * JAXB extension for Tile debug symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRegistry
public final class TileDebugSymbolizerObjectFactory {

    private static final QName _TileDebugSymbolizer_QNAME = new QName("http://geotoolkit.org", "TileDebugSymbolizer");

    public TileDebugSymbolizer createTileDebugSymbolizer() {
        return new TileDebugSymbolizer();
    }

    @XmlElementDecl(namespace = "http://geotoolkit.org", name = "TileDebugSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<TileDebugSymbolizer> createTileDebugSymbolizer(final TileDebugSymbolizer value) {
        return new JAXBElement<TileDebugSymbolizer>(_TileDebugSymbolizer_QNAME, TileDebugSymbolizer.class, null, value);
    }

}
