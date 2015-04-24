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
package org.geotoolkit.data.kml;

import java.net.URISyntaxException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Extensions.Names;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.xml.KmlExtensionReader;
import org.geotoolkit.xml.StaxStreamReader;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DataReader extends StaxStreamReader implements KmlExtensionReader {

    private static final String URI_DATA = "http://www.sandres.com";
    public Map<String, List<String>> complexTable;
    public Map<String, List<String>> simpleTable;

    public DataReader(){
        super();
//        initComplexTable();
//        initSimpleTable();
    }

    public List<String> read() {
        List<String> root = null;
        try {
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT:
                        final String eName = reader.getLocalName();
                        final String eUri = reader.getNamespaceURI();

                        if (URI_DATA.equals(eUri)) {
                            if ("racine".equals(eName)) {
                                root = this.readRacine();
                            }
                        }
                        break;
                }
            }
        } catch (URISyntaxException | XMLStreamException | KmlException ex) {
            Logging.getLogger("org.geotoolkit.data.kml").log(Level.SEVERE, null, ex);
        }
        return root;
    }

    private List<String> readRacine() throws XMLStreamException, KmlException, URISyntaxException {
        List<String> elements = new ArrayList<>();
boucle: while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_DATA.equals(eUri)) {
                        if ("element".equals(eName)) {
                            elements.add(this.readElement());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if ("racine".equals(reader.getLocalName())
                            && URI_DATA.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }
        return elements;
    }

    public String readElement() throws XMLStreamException, KmlException, URISyntaxException {
        return reader.getElementText();
    }

    @Override
    public boolean canHandleComplexExtension(String containingUri,
            String containingTag, String contentsUri, String contentsTag)
    {
        return false;
    }

    @Override
    public boolean canHandleSimpleExtension(String containingUri,
            String containingTag, String contentsUri, String contentsTag)
    {
        return (("racine".equals(contentsTag)) || ("element".equals(contentsTag)));
    }

    @Override
    public Entry<Object, Names> readExtensionElement(String containingUri,
            String containingTag, String contentsUri, String contentsTag)
            throws XMLStreamException, KmlException, URISyntaxException
    {
        Object object = null;
        if ("racine".equals(contentsTag)) {
            object = readRacine();
        } else if ("element".equals(contentsTag)){
            object = readElement();
        }
        return new SimpleEntry<>(object, null);
    }
}
