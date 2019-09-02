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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.lang.Static;
import org.geotoolkit.nio.IOUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utils method for dom parsing.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
     * Search and return the list node with a given tag name.
     *
     * @param parent : node to explore
     * @param tagName : child node name
     * @param recursive : search in sub nodes or not
     * @return first element with tagName in parent node or null
     */
    public static List<Element> getListElements(final Element parent, final String tagName){
        final NodeList lst = parent.getElementsByTagName(tagName);
        List<Element> result = new ArrayList<>();

        for(int i=0,n=lst.getLength();i<n;i++){
            final Node child = lst.item(i);
            if(child instanceof Element && tagName.equalsIgnoreCase(child.getNodeName())){
                result.add((Element) child);
            }
        }
        return result;
    }

    /**
     * Search and return the list node with a given tag name.
     *
     * @param parent : node to explore
     * @param tagName : child node name
     * @param recursive : search in sub nodes or not
     * @return first element with tagName in parent node or null
     */
    public static List<Element> getListElementsNonRecusive(final Element parent, final String tagName){
        final NodeList lst = parent.getChildNodes();
        List<Element> result = new ArrayList<>();

        for(int i=0,n=lst.getLength();i<n;i++){
            final Node child = lst.item(i);
            if(child instanceof Element && tagName.equalsIgnoreCase(child.getLocalName())){
                result.add((Element) child);
            }
        }
        return result;
    }



    /**
     * Return the first node in the given node children which localName
     * matchs the given name. Or null if there are no such node.
     */
    public static Node getNodeByLocalName(final Node parent, final String name) {
        if (name.equalsIgnoreCase(parent.getLocalName())) return parent;
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
    public static <T> T textValue(final Element parent, final String tagName, final Class<T> clazz) throws UnconvertibleObjectException{
        final Element ele = firstElement(parent, tagName, true);
        if(ele == null) return null;
        final String text = ele.getTextContent();
        if(text == null) return null;
        return ObjectConverters.convert(text, clazz);
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
    public static <T> T textValue(final Element parent, final String tagName, final Class<T> clazz, final boolean recursive) throws UnconvertibleObjectException{
        final Element ele = firstElement(parent, tagName, recursive);
        if(ele == null) return null;
        final String text = ele.getTextContent();
        if(text == null) return null;
        return ObjectConverters.convert(text, clazz);
    }

    /**
     * Search a child node with the given tag name and return it's text attribute
     * converted to the given clazz.
     * The convertion in made using the geotoolkit Converters.
     *
     * @param <T> : wished value class
     * @param parent : node to explore
     * @param tagName : child node name
     * @param attributeName : child node attibute name
     * @param clazz : wished value class
     * @return T or null if no node with tagname was found or convertion to given class failed.
     */
    public static <T> T textAttributeValue(final Element parent, final String tagName,final String attributeName, final Class<T> clazz) throws UnconvertibleObjectException{
        final Element ele = firstElement(parent, tagName, true);
        if(ele == null) return null;
        final String text = ele.getAttribute(attributeName);
        if(text == null) return null;
        return ObjectConverters.convert(text, clazz);
    }

    /**
     * Search a child node with the given tag name and return it's text attribute
     * converted to the given clazz.
     * The convertion in made using the geotoolkit Converters.
     *
     * @param <T> : wished value class
     * @param parent : node to explore
     * @param tagName : child node name
     * @param attributeName : child node attibute name
     * @param clazz : wished value class
     * @return T or null if no node with tagname was found or convertion to given class failed.
     */
    public static <T> T textAttributeValue(final Element parent, final String tagName,final String attributeName, final Class<T> clazz, boolean recursive) throws UnconvertibleObjectException{
        final Element ele = firstElement(parent, tagName, recursive);
        if(ele == null) return null;
        final String text = ele.getAttribute(attributeName);
        if(text == null) return null;
        return ObjectConverters.convert(text, clazz);
    }

    /**
     * Same as {@link DomUtilities#textValue(org.w3c.dom.Element, java.lang.String, java.lang.Class) }
     * but dont throw any exception.
     */
    public static <T> T textValueSafe(final Element parent, final String tagName, final Class<T> clazz) {
        try {
            return textValue(parent, tagName, clazz);
        } catch (UnconvertibleObjectException ex) {
            Logging.getLogger("org.geotoolkit.util").log(Level.WARNING, null, ex);
            return null;
        }
    }

    /**
     * Same as {@link DomUtilities#textValue(org.w3c.dom.Element, java.lang.String, java.lang.Class) }
     * but dont throw any exception.
     */
    public static <T> T textValueSafe(final Element parent, final String tagName, final Class<T> clazz, final boolean recusive) {
        try {
            return textValue(parent, tagName, clazz, recusive);
        } catch (UnconvertibleObjectException ex) {
            Logging.getLogger("org.geotoolkit.util").log(Level.WARNING, null, ex);
            return null;
        }
    }

    /**
     * Same as {@link DomUtilities#textAttributeValue(org.w3c.dom.Element, java.lang.String, java.lang.String, java.lang.Class) }
     * but dont throw any exception.
     */
    public static <T> T textAttributeValueSafe(final Element parent, final String tagName, final String attributeName, final Class<T> clazz) {
        try {
            return textAttributeValue(parent, tagName,attributeName,  clazz);
        } catch (UnconvertibleObjectException ex) {
            Logging.getLogger("org.geotoolkit.util").log(Level.WARNING, null, ex);
            return null;
        }
    }

    /**
     * Same as {@link DomUtilities#textAttributeValue(org.w3c.dom.Element, java.lang.String, java.lang.String, java.lang.Class, java.lang.boolean) }
     * but dont throw any exception.
     */
    public static <T> T textAttributeValueSafe(final Element parent, final String tagName, final String attributeName, final Class<T> clazz, final boolean recursive) {
        try {
            return textAttributeValue(parent, tagName,attributeName,  clazz, recursive);
        } catch (UnconvertibleObjectException ex) {
            Logging.getLogger("org.geotoolkit.util").log(Level.WARNING, null, ex);
            return null;
        }
    }

    /**
     * Convert an object source to a stream.
     */
    private static OutputStream toOutputStream(final Object input) throws FileNotFoundException, IOException{
        return IOUtilities.openWrite(input);
    }

    /**
     * Convert an object source to a stream.
     */
    private static InputStream toInputStream(final Object input) throws FileNotFoundException, IOException{

        //special case when input object is document itelf
        if (input instanceof String) {
            try {
                //try to open it as a path
                final URL url = new URL((String) input);
            } catch (MalformedURLException ex) {
                //consider it's the document itself
                return new ByteArrayInputStream(input.toString().getBytes());
            }
        }

        return IOUtilities.open(input);
    }

    /**
     * Reformat and indent an xml file.
     */
    public static void prettyPrint(Path input, Path output) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException, TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult result = new StreamResult(output.toFile());
        DOMSource source = new DOMSource(read(input));
        transformer.transform(source, result);
    }

}
