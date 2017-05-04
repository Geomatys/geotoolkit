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
package org.geotoolkit.display2d.ext.cellular;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRegistry
public class CellSymbolizerObjectFactory {

    private static final QName _CellSymbolizer_QNAME = new QName("http://geotoolkit.org", "CellSymbolizer");

    public CellSymbolizer createCellSymbolizer() {
        return new CellSymbolizer();
    }

    @XmlElementDecl(namespace = "http://geotoolkit.org", name = "CellSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<CellSymbolizer> createCellSymbolizer(final CellSymbolizer value) {
        return new JAXBElement<CellSymbolizer>(_CellSymbolizer_QNAME, CellSymbolizer.class, null, value);
    }

}
