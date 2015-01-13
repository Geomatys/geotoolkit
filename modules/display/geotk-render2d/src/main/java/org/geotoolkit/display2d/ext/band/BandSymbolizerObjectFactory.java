/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2014, Geomatys
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
package org.geotoolkit.display2d.ext.band;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRegistry
public class BandSymbolizerObjectFactory {
    
    private static final QName _BandSymbolizer_QNAME = new QName("http://geotoolkit.org", "BandSymbolizer");
    
    public BandSymbolizer createBandSymbolizer() {
        return new BandSymbolizer();
    }
    
    @XmlElementDecl(namespace = "http://geotoolkit.org", name = "BandSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<BandSymbolizer> createCellSymbolizer(final BandSymbolizer value) {
        return new JAXBElement<BandSymbolizer>(_BandSymbolizer_QNAME, BandSymbolizer.class, null, value);
    }
    
}
