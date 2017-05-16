/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 20014, Geomatys
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
package org.geotoolkit.display2d.ext.pattern;

import org.geotoolkit.display2d.ext.cellular.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRegistry
public class PatternSymbolizerObjectFactory {

    private static final QName _PatternSymbolizer_QNAME = new QName("http://geotoolkit.org", "PatternSymbolizer");

    public PatternSymbolizerType createPatternSymbolizer() {
        return new PatternSymbolizerType();
    }

    @XmlElementDecl(namespace = "http://geotoolkit.org", name = "CellSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<PatternSymbolizerType> createCellSymbolizer(final PatternSymbolizerType value) {
        return new JAXBElement<PatternSymbolizerType>(_PatternSymbolizer_QNAME, PatternSymbolizerType.class, null, value);
    }

}
