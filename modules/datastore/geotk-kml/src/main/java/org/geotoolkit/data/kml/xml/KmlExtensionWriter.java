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
 * <p>This interface provides methods an StaxStreamWriter needs to implement
 * to be added as an extension writer to Kml main writer with method
 * <span style="font-style: italic;">addExtensionWriter()</span></p>
 * <p>Here is an example of use:</p>
 *
 * <pre>
 * final Kml kml = kmlFactory.createKml(null, folder, null, null);
 *
 * <div style="background-color: #cccccc; color: red;">
 * kml.addExtensionUri(GxConstants.URI_GX, "gx");
 * </div>
 *
 * final File temp = File.createTempFile("testMultiTrack", ".kml");
 * temp.deleteOnExit();
 * <div style="background-color: #cccccc; color: red;">
 * final KmlWriter writer = new KmlWriter();
 * final GxWriter gxWriter = new GxWriter(writer);
 * writer.setOutput(temp);
 * writer.addExtensionWriter(GxConstants.URI_GX, gxWriter);
 * </div>
 * writer.write(kml);
 * writer.dispose();
 * </pre>
 *
 * <p>For a given extension writer, writting an extension tag could depend on
 * Kml version, extension level and nature of extension object itself.</p>
 *
 * <p>For example, extension level is the difference between BasicLink extensions
 * or Link extensions that are both concluded in an Link. For extension writer,
 * tag choice for one element of these extensions coud depends of its level.</p>
 *
 * <p>Nature of an extension is more complex than its class. Class has
 * no significance for simple extensions. Because of this lack of semantics,
 * simple data are included in SimpleType container. Nature of such simple extensions
 * is indicated by uri/tag availables in simple type container.</p>
 *
 * <p>Even in the case of complex extensions, objects class is not sufficient
 * because of numerous feature types. Only the object himself contains all
 * information about his nature.</p>
 *
 * @author Samuel Andrés
 */
public interface KmlExtensionWriter {

    /**
     * <p>This method indicats if KmlExtentionWriter can write given element
     * contained in given extension</p>
     *
     * @param kmlVersionUri
     * @param ext
     * @param contentObject
     * @return
     */
    boolean canHandleComplex(String kmlVersionUri, Extensions.Names ext, Object contentObject);

    /**
     * <p>This method indicats if KmlExtentionWriter can write given element
     * contained in given extension</p>
     *
     * @param kmlVersionUri
     * @param ext
     * @param elementTag
     * @return
     */
    boolean canHandleSimple(String kmlVersionUri, Extensions.Names ext, String elementTag);

    /**
     * <p>This method writes a complex extension element.</p>
     *
     * @param kmlVersionUri
     * @param ext
     * @param contentElement
     * @throws XMLStreamException
     * @throws KmlException
     */
    void writeComplexExtensionElement(String kmlVersionUri, Extensions.Names ext, Object contentElement)
            throws XMLStreamException, KmlException;

    /**
     * <p>This method writes a simple extension element.</p>
     *
     * @param kmlVersionUri
     * @param ext
     * @param contentElement
     * @throws XMLStreamException
     * @throws KmlException
     */
    void writeSimpleExtensionElement(String kmlVersionUri, Extensions.Names ext, SimpleTypeContainer contentElement)
            throws XMLStreamException, KmlException;

}
