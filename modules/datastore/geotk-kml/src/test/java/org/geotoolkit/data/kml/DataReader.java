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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Extensions.Names;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.xml.KmlExtensionReader;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.xml.StaxStreamReader;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DataReader extends StaxStreamReader implements KmlExtensionReader{
    
    private static final String URI_DATA = "http://www.sandres.com";
    public Map<String, List<String>> complexTable;
    public Map<String, List<String>> simpleTable;

    public DataReader(){
        super();
//        initComplexTable();
//        initSimpleTable();
    }

    /**
     * <p>Set input (use KML 2.2).</p>
     *
     * @param input
     * @throws IOException
     * @throws XMLStreamException
     */
    @Override
    public void setInput(Object input)
            throws IOException, XMLStreamException {
        super.setInput(input);
    }

    public List<String> read(){
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
        } catch (URISyntaxException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KmlException ex) {
            System.out.println("KML EXCEPTION : " + ex.getMessage());
        }
        return root;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    private List<String> readRacine()
            throws XMLStreamException, KmlException, URISyntaxException {

        List<String> elements = new ArrayList<String>();

        boucle:
        while (reader.hasNext()) {

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

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     * @throws URISyntaxException
     */
    public String readElement()
            throws XMLStreamException, KmlException, URISyntaxException {

        return reader.getElementText();

    }

    @Override
    public boolean canHandleComplexExtension(String containingUri,
            String containingTag, String contentsUri, String contentsTag) {
        return false;
    }

    @Override
    public boolean canHandleSimpleExtension(String containingUri,
            String containingTag, String contentsUri, String contentsTag) {
        return (("racine".equals(contentsTag))
                || ("element".equals(contentsTag)));
    }

    @Override
    public Entry<Object, Names> readExtensionElement(String containingUri, 
            String containingTag, String contentsUri, String contentsTag)
            throws XMLStreamException, KmlException, URISyntaxException {
        Object object = null;
        
        if("racine".equals(contentsTag)){
            object = this.readRacine();
        } else if ("element".equals(contentsTag)){
            object = this.readElement();
        }
        return new SimpleEntry<Object, Names>(object, null);
    }
}
