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


import java.awt.geom.AffineTransform;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.metadata.IIOMetadata;

import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.util.converter.Classes;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;

/**
 * Utility class to read geotiff metadata tags.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GeoTiffMetaDataReader {

    private final IIOMetadata imageMetadata;
    private final Node root;
    private final Node imgFileDir;
    private final Node geoKeyDir;

    public GeoTiffMetaDataReader(IIOMetadata imageMetadata) throws IOException{
        this.imageMetadata = imageMetadata;
        
        root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
        if(root == null) throw new IOException("No image metadatas");

        imgFileDir = getNodeByLocalName(root,TAG_GEOTIFF_IFD);
        if(imgFileDir == null) throw new IOException("No GeoTiff metadatatas");

        geoKeyDir = getNodeByNumber(imgFileDir,TAG_GEO_KEY_DIRECTORY);
        if(geoKeyDir == null) throw new IOException("No GeoTiff metadatatas informations");
    }

    /**
     * Read the Spatial Metadatas.
     * 
     * @param imageMetadata
     * @return SpatialMetadata
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    public SpatialMetadata readSpatialMetaData() throws NoSuchAuthorityCodeException, FactoryException,IOException{

        final int[] structure = readTiffShorts(getNodeByLocalName(geoKeyDir, TAG_GEOTIFF_SHORTS));

        //first line (4 int) contain the version and number of keys
        //Header={KeyDirectoryVersion, KeyRevision, MinorRevision, NumberOfKeys}
        final int directoryVersion  = structure[0];
        final int keyVersion        = structure[1];
        final int minorVersion      = structure[2];
        final int nbKeys            = structure[3];

        //read all entries
        final Map<Integer,Object[]> entries = new HashMap<Integer,Object[]>();
        for(int i=0,l=4; i<nbKeys; i++,l+=4){
            final Object[] value;

            final int valueKey      = structure[l+0];
            final int valuelocation = structure[l+1];
            final int valueNb       = structure[l+2];
            final int valueOffset   = structure[l+3];
            if(valuelocation == 0){
                //value is located in the offset field
                value = new Object[]{valueOffset};
            }else{
                //value is in another tag
                value = readValue(valuelocation, valueOffset, valueNb);
            }
            entries.put(valueKey, value);
        }

        //create the spatial metadatas.
        final SpatialMetadata spatialMetadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        fillGridMetaDatas(spatialMetadata, entries);
        fillCRSMetaDatas(spatialMetadata, entries);
        return spatialMetadata;
    }

    /**
     * Fill the spatial metadatas with the values available in the geotiff tags.
     */
    private void fillGridMetaDatas(SpatialMetadata metadatas, Map<Integer,Object[]> entries) throws IOException{
        final GridDomainAccessor accesor = new GridDomainAccessor(metadatas);
        throw new IOException("Not done yet.");
    }

    /**
     * Fill the CRS metadatas with the values available in the geotiff tags.
     */
    private void fillCRSMetaDatas(SpatialMetadata metadatas, Map<Integer,Object[]> entries) throws IOException{

        final Object[] values = entries.get(GTModelTypeGeoKey);

        if(values == null){
            throw new IOException("GTModelTypeGeoKey is not defined in tags.");
        }

        switch( (Integer)values[0] ){
            case ModelTypeProjected:  fillProjectedCRSMetaDatas(metadatas,entries);break;
            case ModelTypeGeographic: fillProjectedCRSMetaDatas(metadatas,entries);break;
            case ModelTypeGeocentric: fillProjectedCRSMetaDatas(metadatas,entries);break;
            default: throw new IOException("Unexpected crs model type : "+(Integer)values[0]);
        }

    }

    /**
     * Fill a projected CRS metadatas with the values available in the geotiff tags.
     */
    private void fillProjectedCRSMetaDatas(SpatialMetadata metadatas, Map<Integer,Object[]> entries) throws IOException{
        throw new IOException("Not done yet.");
    }

    /**
     * Fill a geographic CRS metadatas with the values available in the geotiff tags.
     */
    private void fillGeographicCRSMetaDatas(SpatialMetadata metadatas, Map<Integer,Object[]> entries) throws IOException{
        throw new IOException("Not done yet.");
    }

    /**
     * Fill a geocentric CRS metadatas with the values available in the geotiff tags.
     */
    private void fillGeocentricCRSMetaDatas(SpatialMetadata metadatas, Map<Integer,Object[]> entries) throws IOException{
        throw new IOException("Not done yet.");
    }




    /**
     * Read the transformation from the TAG_MODEL_TRANSFORMATION if it exist.
     */
    private AffineTransform readGridToCRS() throws IOException {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_TRANSFORMATION);
        if (node == null) {
            return null;
        }

        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if(valueNode == null){
            return null;
        }

        final double[] matrix = readTiffDoubles(valueNode);
        if (matrix.length == 9) {
            //2D matrix
            return new AffineTransform(matrix[0],matrix[4],matrix[1],
                                       matrix[5],matrix[6],matrix[7]);
        } else if (matrix.length == 16) {
            //3D matrix, only keep the 2d part
            return new AffineTransform(matrix[0],matrix[4],matrix[1],
                                       matrix[5],matrix[3],matrix[7]);
        } else{
            throw new IOException("Unvalid transformation definition. expected 9 or 16 parameters but was "+matrix.length);
        }

    }

    /**
     * Read values for the given tag number.
     * @param tagNumber
     * @param offset
     * @param lenght
     * @return
     * @throws IOException
     */
    private Object[] readValue(int tagNumber, int offset, int lenght) throws IOException{
        final Node node = getNodeByNumber(imgFileDir, tagNumber);
        if(node == null){
            throw new IOException("Incorrect metadata description, no tag with number "+tagNumber);
        }

        //node should have a single subNode containing the value
        final Node valueNode = node.getChildNodes().item(0);
        if(valueNode == null){
            throw new IOException("Incorrect metadata description, no value in tag number "+tagNumber);
        }

        final String typeName = valueNode.getLocalName();

        final Object[] values;

        if(TAG_GEOTIFF_ASCII.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            values = new Object[]{readTiffAscii(valueNode)};

        }else if(TAG_GEOTIFF_ASCIIS.equalsIgnoreCase(typeName)){
            values = new Object[]{readTiffAsciis(valueNode).substring(offset, offset+lenght)};

        }else if(TAG_GEOTIFF_SHORT.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            values = new Object[]{readTiffShort(valueNode)};

        }else if(TAG_GEOTIFF_SHORTS.equalsIgnoreCase(typeName)){
            values = new Object[lenght];
            final int[] shorts = readTiffShorts(valueNode);
            for(int i=0;i<lenght;i++){
                values[i] = shorts[offset+i];
            }
        }else if(TAG_GEOTIFF_LONG.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            values = new Object[]{readTiffLong(valueNode)};

        }else if(TAG_GEOTIFF_LONGS.equalsIgnoreCase(typeName)){
            values = new Object[lenght];
            final long[] longs = readTiffLongs(valueNode);
            for(int i=0;i<lenght;i++){
                values[i] = longs[offset+i];
            }

        }else if(TAG_GEOTIFF_DOUBLE.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            values = new Object[]{readTiffDouble(valueNode)};

        }else if(TAG_GEOTIFF_DOUBLES.equalsIgnoreCase(typeName)){
            values = new Object[lenght];
            final double[] doubles = readTiffDoubles(valueNode);
            for(int i=0;i<lenght;i++){
                values[i] = doubles[offset+i];
            }
        }else{
            throw new IOException("Incorrect metadata description, unknowned value type "
                    +typeName+" for tag number "+ tagNumber);
        }

        return values;
    }

    /**
     * @return String of tagsets on the image file directory node.
     */
    public String readTagSets(){
        return getAttributeValue(imgFileDir, ATT_TAGSETS);
    }

    @Override
    public String toString() {
        return Classes.getShortName(this.getClass()) +"\n"+Trees.toString(Trees.xmlToSwing(root));
    }
    
    /**
     * Return the first node in the given node children which number attribut value
     * match the given number.
     */
    private static Node getNodeByNumber(final Node parent, final int number) {
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
     * Return the first node in the given node children which localName
     * matchs the given name.
     */
    private static Node getNodeByLocalName(final Node parent, final String name) {
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
     * Return the first node in the given node children which name attribute
     * matchs the given name.
     */
    private static Node getNodeByAttributeName(final Node parent, final String name) {
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
    private static String getAttributeValue(Node candidate, String attributName){
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
    private static int readTiffShort(final Node candidate) {
        return Integer.parseInt(getAttributeValue(candidate, ATT_VALUE));
    }

    /**
     * Read a TIFFShorts node values.
     */
    private static int[] readTiffShorts(final Node candidate) {
        final NodeList lst = candidate.getChildNodes();
        final int[] shorts = new int[lst.getLength()];
        for(int i=0;i<shorts.length;i++){
            shorts[i] = readTiffShort(lst.item(i));
        }
        return shorts;
    }

    /**
     * Read a TIFFLong node value.
     */
    private static long readTiffLong(final Node candidate) {
        return Long.parseLong(getAttributeValue(candidate, ATT_VALUE));
    }

    /**
     * Read a TIFFLongs node values.
     */
    private static long[] readTiffLongs(final Node candidate) {
        final NodeList lst = candidate.getChildNodes();
        final long[] longs = new long[lst.getLength()];
        for(int i=0;i<longs.length;i++){
            longs[i] = readTiffLong(lst.item(i));
        }
        return longs;
    }

    /**
     * Read a TIFFDouble node value.
     */
    private static double readTiffDouble(final Node candidate) {
        return Double.parseDouble(getAttributeValue(candidate, ATT_VALUE));
    }

    /**
     * Read a TIFFdoubles node values.
     */
    private static double[] readTiffDoubles(final Node candidate) {
        final NodeList lst = candidate.getChildNodes();
        final double[] doubles = new double[lst.getLength()];
        for(int i=0;i<doubles.length;i++){
            doubles[i] = readTiffDouble(lst.item(i));
        }
        return doubles;
    }

    /**
     * Read a TIFFAscii node value.
     */
    private static String readTiffAscii(final Node candidate) {
        final String valueAttribute = getAttributeValue(candidate, ATT_VALUE);
        return valueAttribute;
    }

    /**
     * Read a TIFFAsciis node values.
     * There are not several String here, they are all concatenate in a single
     * String in one ASCII sub node.
     */
    private static String readTiffAsciis(final Node candidate) {
        final Node subNode = getNodeByLocalName(candidate, TAG_GEOTIFF_ASCII);
        return readTiffAscii(subNode);
    }

}
