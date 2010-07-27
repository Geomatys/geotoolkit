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

import java.net.URISyntaxException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.KmlException;

/**
 *
 * @author Samuel Andrés
 */
public interface KmlExtensionReader {

    /**
     *
     * @param containingTag
     * @param contentsTag
     * @return The complex extension level of given element. Null if there is no mapping level.
     */
    Extensions.Names getComplexExtensionLevel(String containingTag, String contentsTag);

    /**
     * 
     * @param containingTag
     * @param contentsTag
     * @return The complex extension level of given element. Null if there is no mapping level.
     */
    Extensions.Names getSimpleExtensionLevel(String containingTag, String contentsTag);

    /**
     * <p>This method read an element whose tag is contained in a specified parent tag.</p>
     * 
     * @param containingTag
     * @param contentsTag
     * @return An object mapping read element. Null if element has not been read.
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    Object readExtensionElement(String containingTag, String contentsTag)
            throws XMLStreamException, KmlException, URISyntaxException;
}
