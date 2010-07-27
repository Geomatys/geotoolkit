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
package org.geotoolkit.data.kml.xml;

import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;

/**
 *
 * @author Samuel Andrés
 */
public interface KmlExtensionWriter {

    /**
     * <p>This method writes a complex extension element.</p>
     *
     * @param contentElement
     * @throws XMLStreamException
     * @throws KmlException
     */
    void writeComplexExtensionElement(Object contentElement)
            throws XMLStreamException, KmlException;

    /**
     * <p>This method writes a simple extension element.</p>
     *
     * @param contentElement
     * @throws XMLStreamException
     * @throws KmlException
     */
    void writeSimpleExtensionElement(SimpleTypeContainer contentElement)
            throws XMLStreamException, KmlException;

    /**
     * <p>This method indicats if KmlExtentionWriter can write given element
     * contained in given extension</p>
     *
     * @param ext
     * @param contentObject
     * @return
     */
    boolean canHandleComplex(Extensions.Names ext, Object contentObject);

    /**
     * <p>This method indicats if KmlExtentionWriter can write given element
     * contained in given extension</p>
     * 
     * @param ext
     * @param elementTag
     * @return
     */
    boolean canHandleSimple(Extensions.Names ext, String elementTag);

}
