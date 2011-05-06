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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utils method for dom parsing.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DomUtilities extends Static {

    private DomUtilities(){}

    /**
     * Convinient method to aquiere a DOM document from an input.
     * This is provided as a convinient method, use the default JRE classes so it may
     * not be the faster parsing method.
     */
    public static Document read(final Object input) throws ParserConfigurationException, SAXException, IOException {
        final InputStream stream = toInputStream(input);
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder constructeur = factory.newDocumentBuilder();
        final Document document = constructeur.parse(stream);
        stream.close();
        return document;
    }

    /**
     * Convinient method to write a dom.
     *
     * @param doc : DOM document to write
     * @param output : output file, url, uri, outputstream
     * @throws TransformerException
     */
    public static void write(final Document doc, final Object output) throws TransformerException, FileNotFoundException, IOException{
        final Source source = new DOMSource(doc);

        final Result result;
        if(output instanceof File){
            result = new StreamResult((File)output);
        }else if(output instanceof Writer){
            result = new StreamResult((Writer)output);
        }else{
            final OutputStream stream = toOutputStream(output);
            result = new StreamResult(stream);
        }

        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer trs = factory.newTransformer();
        trs.setOutputProperty(OutputKeys.INDENT, "yes");
        trs.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

        trs.transform(source, result);
    }

    /**
     * Search and return the first node with a given tag name.
     * This will search recursivly in the node
     *
     * @param parent : node to explore
     * @param tagName : child node name
     * @return first element with tagName in parent node or null
     */
    public static Element firstElement(final Element parent, final String tagName){
        return firstElement(parent, tagName, true);
    }

    /**
     * Search and return the first node with a given tag name.
     *
     * @param parent : node to explore
     * @param tagName : child node name
     * @param recursive : search in sub nodes or not
     * @return first element with tagName in parent node or null
     */
    public static Element firstElement(final Element parent, final String tagName, final boolean recursive){

        if(recursive){
            final NodeList lst = parent.getElementsByTagName(tagName);
            if(lst.getLength() > 0){
                return (Element) lst.item(0);
            }
        }else{
            //search only in this node
            final NodeList lst = parent.getChildNodes();
            for(int i=0,n=lst.getLength();i<n;i++){
                final Node child = lst.item(i);
                if(child instanceof Element && tagName.equalsIgnoreCase(child.getLocalName())){
                    return (Element) child;
                }
            }
        }
        return null;
    }



    /**
     * Return the first node in the given node children which localName
     * matchs the given name. Or null if there are no such node.
     */
    public static Node getNodeByLocalName(final Node parent, final String name) {
        final NodeList lst = parent.getChildNodes();

        for(int i=0,n=lst.getLength();i<n;i++){
            final Node child = lst.item(i);
            if(name.equalsIgnoreCase(child.getLocalName())){
                return child;
            }
        }
        return null;
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
    public static <T> T textValue(final Element parent, final String tagName, final Class<T> clazz) throws NonconvertibleObjectException{
        final Element ele = firstElement(parent, tagName, true);
        if(ele == null) return null;
        final String text = ele.getTextContent();
        if(text == null) return null;
        return ConverterRegistry.system().converter(String.class, clazz).convert(text);
    }

    /**
     * Same as {@link DomUtilities#textValue(org.w3c.dom.Element, java.lang.String, java.lang.Class) }
     * but dont throw any exception.
     */
    public static <T> T textValueSafe(final Element parent, final String tagName, final Class<T> clazz) {
        try {
            return textValue(parent, tagName, clazz);
        } catch (NonconvertibleObjectException ex) {
            Logger.getLogger(DomUtilities.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }

    /**
     * Convert an object source to a stream.
     */
    private static OutputStream toOutputStream(final Object input) throws FileNotFoundException, IOException{

        if(input instanceof File){
            return new FileOutputStream((File)input);
        }else{
            throw new IOException("Can not handle input type : " + ((input!=null)?input.getClass() : input));
        }
    }

    /**
     * Convert an object source to a stream.
     */
    private static InputStream toInputStream(final Object input) throws FileNotFoundException, IOException{

        if(input instanceof InputStream){
            return (InputStream) input;
        }else if(input instanceof File){
            return new FileInputStream((File)input);
        }else if(input instanceof URI){
            return ((URI)input).toURL().openStream();
        }else if(input instanceof URL){
            return ((URL)input).openStream();
        }else if(input instanceof String){
            try{
                //try to open it as a path
                return new URL((String)input).openStream();
            }catch(Exception ex){
                //consider it's the document itself
                return new ByteArrayInputStream(input.toString().getBytes());
            }
        }else{
            throw new IOException("Can not handle input type : " + ((input!=null)?input.getClass() : input));
        }
    }

}
