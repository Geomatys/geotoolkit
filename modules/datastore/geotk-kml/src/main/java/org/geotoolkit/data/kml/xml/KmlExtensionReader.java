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
import java.util.Map.Entry;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.KmlException;

/**
 * <p>This interface provides methods a StaxStreamReader needs to implement
 * to be added as an extension reader to Kml main reader with method
 * <span style="font-style: italic;">addExtensionReader()</span></p>
 * <p>Here is an example of use:</p>
 *
 * <pre>
 * <div style="background-color: #cccccc; color: red;">
 * final KmlReader reader = new KmlReader();
 * final GxReader gxReader = new GxReader(reader);
 * reader.setInput(new File(pathToTestFile));
 * reader.addExtensionReader(gxReader);
 * </div>
 * final Kml kmlObjects = reader.read();
 * reader.dispose();
 * </pre>
 *
 * <p>For a given extension reader, reading an extension object could depend on
 * uri/tag of containing element and uri/tag of extension element itself.</p>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface KmlExtensionReader {

    /**
     *
     * @param containingUri
     * @param containingTag
     * @param contentsUri
     * @param contentsTag
     * @return
     */
    boolean canHandleComplexExtension(String containingUri, String containingTag,
            String contentsUri, String contentsTag);

    /**
     *
     * @param containingUri
     * @param containingTag
     * @param contentsUri
     * @param contentsTag
     * @return
     */
    boolean canHandleSimpleExtension(String containingUri, String containingTag,
            String contentsUri, String contentsTag);

    /**
     * 
     * @param containingUri
     * @param containingTag
     * @param contentsUri
     * @param contentsTag
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    Entry<Object, Extensions.Names> readExtensionElement(
            String containingUri, String containingTag, String contentsUri, String contentsTag)
            throws XMLStreamException, KmlException, URISyntaxException;
}
