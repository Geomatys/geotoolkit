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

package org.geotoolkit.metadata.geotiff;

import java.util.Collection;
import com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import static com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.util.DomUtilities.*;

/**
 * Set of convinient methods to manipulate Dom nodes specific to geotiff.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GeoTiffMetaDataUtils {


    /**
     * Return the first node in the given node children which number attribut value
     * match the given number.
     */
    static Node getNodeByNumber(final Node parent, final int number) {
        final NodeList lst = parent.getChildNodes();

        for(int i=0,n=lst.getLength();i<n;i++){
            final Node child = lst.item(i);
            //check it's the node we are looking for
            final String attVal = getAttributeValue(child,ATT_NUMBER);
            if (attVal != null && Integer.parseInt(attVal) == number) {
                return child;
            }
        }
        return null;
    }

    /**
     * Return the first node in the given node children which name attribute
     * matchs the given name.
     */
    static Node getNodeByAttributeName(final Node parent, final String name) {
        final NodeList lst = parent.getChildNodes();

        for(int i=0,n=lst.getLength();i<n;i++){
            final Node child = lst.item(i);
            if(name.equalsIgnoreCase(getAttributeValue(child, ATT_NAME))){
                return child;
            }
        }
        return null;
    }

    /**
     * Returns the attribut value or null if attribut does not exist.
     */
    static String getAttributeValue(final Node candidate, final String attributName){
        final NamedNodeMap attributs = candidate.getAttributes();
        if(attributs != null){
            final Node attribut = attributs.getNamedItem(attributName);
            if(attribut != null){
                return attribut.getNodeValue();
            }
        }
        return null;
    }

    /**
     * Read a TIFFShort node value.
     */
    static int readTiffShort(final Node candidate) {
        return Integer.parseInt(getAttributeValue(candidate, ATT_VALUE));
    }

    /**
     * Read a TIFFShorts node values.
     */
    static int[] readTiffShorts(final Node candidate) {
        final NodeList lst = candidate.getChildNodes();
        final int size = lst.getLength();
        if(size == 0){
            return null;
        }
        final int[] shorts = new int[size];
        for(int i=0;i<shorts.length;i++){
            shorts[i] = readTiffShort(lst.item(i));
        }
        return shorts;
    }

    /**
     * Read a TIFFLong node value.
     */
    static long readTiffLong(final Node candidate) {
        return Long.parseLong(getAttributeValue(candidate, ATT_VALUE));
    }

    /**
     * Read a TIFFLongs node values.
     */
    static long[] readTiffLongs(final Node candidate) {
        final NodeList lst = candidate.getChildNodes();
        final int size = lst.getLength();
        if(size == 0){
            return null;
        }

        final long[] longs = new long[size];
        for(int i=0;i<longs.length;i++){
            longs[i] = readTiffLong(lst.item(i));
        }
        return longs;
    }

    /**
     * Read a TIFFDouble node value.
     */
    static double readTiffDouble(final Node candidate) {
        return Double.parseDouble(getAttributeValue(candidate, ATT_VALUE));
    }

    /**
     * Read a TIFFdoubles node values.
     */
    static double[] readTiffDoubles(final Node candidate) {
        final NodeList lst = candidate.getChildNodes();
        final int size = lst.getLength();
        if(size == 0){
            return null;
        }

        final double[] doubles = new double[size];
        for(int i=0;i<doubles.length;i++){
            doubles[i] = readTiffDouble(lst.item(i));
        }
        return doubles;
    }

    /**
     * Read a TIFFAscii node value.
     */
    static String readTiffAscii(final Node candidate) {
        final String valueAttribute = getAttributeValue(candidate, ATT_VALUE);
        return valueAttribute;
    }

    /**
     * Read a TIFFAsciis node values.
     * There are not several String here, they are all concatenate in a single
     * String in one ASCII sub node.
     */
    static String readTiffAsciis(final Node candidate) {
        final Node subNode = getNodeByLocalName(candidate, TAG_GEOTIFF_ASCII);
        if(subNode != null){
            return readTiffAscii(subNode);
        }
        return "";
    }

    /**
     * Create a dom Node, a special IIOMetadataNode.
     */
    static Element createNode(final String name){
        return new IIOMetadataNode(name);
    }

    /**
     * Create a TiffField node with id and name from the TIFFTag description.
     */
    static Node createTiffField(final TIFFTag tag){
        return createTiffField(tag.getNumber(),tag.getName());
    }
    
    /**
     * Create a Tiffield node.
     */
    static Node createTiffField(final int number, final String name){
        final Element ele = createNode(TAG_GEOTIFF_FIELD);
        ele.setAttribute(ATT_NUMBER, Integer.toString(number));
        ele.setAttribute(ATT_NAME, name);
        return ele;
    }

    /**
     * Create a TIFFShort node.
     */
    static Node createTiffShort(final int value) {
        final Element ele = createNode(TAG_GEOTIFF_SHORT);
        ele.setAttribute(ATT_VALUE, Long.toString(value));
        return ele;
    }

    /**
     * Create a TIFFShorts node.
     */
    static Node createTiffShorts(final int ... ints) {
        final Element ele = createNode(TAG_GEOTIFF_SHORTS);
        for(final int i : ints){
            ele.appendChild(createTiffShort(i));
        }
        return ele;
    }

    /**
     * Create a TIFFLong node.
     */
    static Node createTiffLong(final long value) {
        final Element ele = createNode(TAG_GEOTIFF_LONG);
        ele.setAttribute(ATT_VALUE, Long.toString(value));
        return ele;
    }

    /**
     * Create a TIFFLongs node.
     */
    static Node createTiffLongs(final long ... longs) {
        final Element ele = createNode(TAG_GEOTIFF_LONGS);
        for(final long l : longs){
            ele.appendChild(createTiffLong(l));
        }
        return ele;
    }

    /**
     * Create a TIFFDouble node.
     */
    static Node createTiffDouble(final double value) {
        final Element ele = createNode(TAG_GEOTIFF_DOUBLE);
        ele.setAttribute(ATT_VALUE, Double.toString(value));
        return ele;
    }

    /**
     * Create a TIFFdoubles node.
     */
    static Node createTiffDoubles(final double ... doubles) {
        final Element ele = createNode(TAG_GEOTIFF_DOUBLES);
        for(final double d : doubles){
            ele.appendChild(createTiffDouble(d));
        }
        return ele;
    }

    /**
     * Create a TIFFdoubles node.
     */
    static Node createTiffDoubles(final Collection<Double> doubles) {
        final Element ele = createNode(TAG_GEOTIFF_DOUBLES);
        for(final double d : doubles){
            ele.appendChild(createTiffDouble(d));
        }
        return ele;
    }


    /**
     * Create a TIFFAscii node.
     */
    static Node createTiffAscii(final String ascii) {
        final Element ele = createNode(TAG_GEOTIFF_ASCII);
        ele.setAttribute(ATT_VALUE, ascii);
        return ele;
    }

    /**
     * Create a TIFFAsciis node.
     */
    static Node createTiffAsciis(final String ... asciis) {
        final Element ele = createNode(TAG_GEOTIFF_ASCIIS);
        for(final String ascii : asciis){
            ele.appendChild(createTiffAscii(ascii));
        }
        return ele;
    }

    /**
     * Check if this metadata node contain a geoKeyDirectory node.
     * If it's the case it is likely a geotiff metadata.
     * @return true if metadata tree is a geotiff.
     */
    public static boolean isGeoTiffTree(final Node candidate){
        final Node imgFileDir = getNodeByLocalName(candidate,TAG_GEOTIFF_IFD);
        if(imgFileDir == null) return false;

        final Node geoKeyDir = getNodeByNumber(imgFileDir,TAG_GEO_KEY_DIRECTORY);
        return geoKeyDir != null;
    }


    public static TIFFTag getGeoKeyDirectoryTag() {
        return GeoTIFFTagSet.getInstance().getTag(TAG_GEO_KEY_DIRECTORY);
    }

    public static TIFFTag getGeoDoubleParamsTag() {
        return GeoTIFFTagSet.getInstance().getTag(TAG_GEO_DOUBLE_PARAMS);
    }

    public static TIFFTag getGeoAsciiParamsTag() {
        return GeoTIFFTagSet.getInstance().getTag(TAG_GEO_ASCII_PARAMS);
    }

    public static TIFFTag getModelPixelScaleTag() {
        return GeoTIFFTagSet.getInstance().getTag(TAG_MODEL_PIXEL_SCALE);
    }

    public static TIFFTag getModelTiePointTag() {
        return GeoTIFFTagSet.getInstance().getTag(TAG_MODEL_TIE_POINT);
    }

    public static TIFFTag getModelTransformationTag() {
        return GeoTIFFTagSet.getInstance().getTag(TAG_MODEL_TRANSFORMATION);
    }

}
