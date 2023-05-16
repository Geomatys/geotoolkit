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

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRegistry
public class PatternSymbolizerObjectFactory {

    private static final QName _PatternSymbolizer_QNAME = new QName("http://geotoolkit.org", "PatternSymbolizer");

    public PatternSymbolizer createPatternSymbolizer() {
        return new PatternSymbolizer();
    }

    @XmlElementDecl(namespace = "http://geotoolkit.org", name = "PatternSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<PatternSymbolizer> createPatternSymbolizer(final PatternSymbolizer value) {
        return new JAXBElement<PatternSymbolizer>(_PatternSymbolizer_QNAME, PatternSymbolizer.class, null, value);
    }

}
