/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utils method for dom parsing.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DomUtilities {

    private DomUtilities(){}

    /**
     * Convinient method to aquiere a DOM document from an input.
     * This is provided as a convinient method, use the default JRE classes so it may
     * not be the faster parsing method.
     */
    public static Document read(Object input) throws ParserConfigurationException, SAXException, IOException {
        final InputStream stream = toStream(input);
        // création d'une fabrique de documents
        final DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
        final DocumentBuilder constructeur = fabrique.newDocumentBuilder();
        final Document document = constructeur.parse(stream);
        stream.close();
        return document;
    }

    /**
     * Search and return the first node with a given tag name.
     *
     * @param parent : node to explore
     * @param tagName : child node name
     * @return first element with tagName in parent node or null
     */
    public static Element firstElement(Element parent, String tagName){
        final NodeList lst = parent.getElementsByTagName(tagName);
        if(lst.getLength() > 0){
            return (Element) lst.item(0);
        }else{
            return null;
        }
    }

    /**
     * Search a child node with the given tag name and return it's text value
     * converted to the given clazz.
     * The convertion in made using the geotoolkit Converters.
     *
     * @param <T> : wished value class
     * @param parent : node to explore
     * @param tagName : child node name
     * @param clazz : wished value class
     * @return T or null if no node with tagname was found or convertion to given class failed.
     */
    public static <T> T textValue(Element parent, String tagName, Class<T> clazz){
        final Element ele = firstElement(parent, tagName);
        if(ele == null) return null;
        final String text = ele.getTextContent();
        if(text == null) return null;
        return Converters.convert(text, clazz);
    }

    /**
     * Convert an object source to a stream.
     */
    private static InputStream toStream(Object input) throws FileNotFoundException, IOException{

        if(input instanceof InputStream){
            return (InputStream) input;
        }else if(input instanceof File){
            return new FileInputStream((File)input);
        }else if(input instanceof URI){
            return ((URI)input).toURL().openStream();
        }else if(input instanceof URL){
            return ((URL)input).openStream();
        }else if(input instanceof String){
            return new URL((String)input).openStream();
        }else{
            throw new IOException("Can not handle inout type : " + input.getClass());
        }
    }

}
