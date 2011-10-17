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

import java.util.logging.Level;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.imageio.metadata.IIOMetadata;

import javax.media.jai.WarpAffine;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.referencing.operation.builder.LocalizationGrid;
import org.geotoolkit.referencing.operation.transform.WarpTransform2D;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.logging.Logging;

import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.w3c.dom.Node;

import static com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffMetaDataUtils.*;
import static org.geotoolkit.util.DomUtilities.*;

/**
 * Utility class to read geotiff metadata tags.
 * http://www.remotesensing.org/geotiff/faq.html
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GeoTiffMetaDataReader {

    private static final Logger LOGGER = Logging.getLogger(GeoTiffMetaDataReader.class);

    public static class ValueMap extends HashMap<Integer, Object>{

        /**
	 * @param key
	 * @return A string representing the value, or null if the key was not
	 *         found or failed to parse.
	 */
	public String getAsString(final int key) {
            final Object value = get(key);

            if(value instanceof String){
                return (String) value;
            }else if(value instanceof Number){
                return ((Number)value).toString();
            }else{
                return null;
            }
        }

        public double getAsDouble(final int key){
            final Object value = get(key);

            if(value instanceof Number){
                return ((Number)value).doubleValue();
            }else if(value != null){
                try {
                    final String geoKey = value.toString();
                    return Double.parseDouble(geoKey);
                } catch (NumberFormatException ne) {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, ne.getLocalizedMessage(), ne);
                    }
                    return Double.NaN;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
                    return Double.NaN;
                }
            }else{
                return Double.NaN;
            }
        }

    }

    private final Node root;
    private final Node imgFileDir;
    private final Node geoKeyDir;

    public GeoTiffMetaDataReader(final IIOMetadata imageMetadata) throws IOException{
        
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
        final ValueMap entries = new ValueMap();
        for(int i=0,l=4; i<nbKeys; i++,l+=4){
            final Object value;

            final int valueKey      = structure[l+0];
            final int valuelocation = structure[l+1];
            final int valueNb       = structure[l+2];
            final int valueOffset   = structure[l+3];
            if(valuelocation == 0){
                //value is located in the offset field
                value = valueOffset;
            }else{
                //value is in another tag
                value = readValue(valuelocation, valueOffset, valueNb);
            }
            entries.put(valueKey, value);
        }

        //create the spatial metadatas.
        final SpatialMetadata spatialMetadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        fillGridMetaDatas(spatialMetadata, entries);

        final GeoTiffCRSReader crsReader = new GeoTiffCRSReader(null);
        crsReader.fillCRSMetaDatas(spatialMetadata, entries);
        return spatialMetadata;
    }

    /**
     * Fill the spatial metadatas with the values available in the geotiff tags.
     */
    private void fillGridMetaDatas(final SpatialMetadata metadatas, final ValueMap entries) throws IOException{
        final GridDomainAccessor accesor = new GridDomainAccessor(metadatas);

        /*
         * FAQ GEOTIFF :
         * Setting the GTRasterTypeGeoKey value to RasterPixelIsPoint or RasterPixelIsArea
         * alters how the raster coordinate space is to be interpreted.
         * This is defined in section 2.5.2.2 of the GeoTIFF specification.
         * => In the case of PixelIsArea (default) a pixel is treated as an area
         * and the raster coordinate (0,0) is the top left corner of the top left pixel.
         * => PixelIsPoint treats pixels as point samples with empty space between the "pixel" samples.
         * In this case raster (0,0) is the location of the top left raster pixel.
         *
         * Note : GeoTiff mix the concepts of CellGeometry and PixelOrientation.
         */

        //get the raster type
        final Object value = entries.get(GTRasterTypeGeoKey);
        final PixelOrientation orientation;
        if(value != null){
            int type = (Integer)value;
            if(type < 1 || type > 2){
                throw new IOException("Unexpected raster type : "+ type);
            }else{
                orientation = (type==RasterPixelIsArea)?PixelOrientation.UPPER_LEFT:PixelOrientation.CENTER;
            }
        }else{
            orientation = PixelOrientation.UPPER_LEFT;
        }
        final CellGeometry cellGeometry = (orientation == PixelOrientation.UPPER_LEFT)
                                          ? CellGeometry.AREA:CellGeometry.POINT;
        
        //read the image bounds
        final Rectangle bounds = readBounds();

        //check if a transformation is present /////////////////////////////////
        final AffineTransform transform = readTransformation();
        if(transform != null){
            //we have the transformation directly, just copy values in the
            //spatial metadatas
            accesor.setAll(transform, bounds, cellGeometry, orientation);
            return;
        }

        //check for pixel scale and tie points /////////////////////////////////
        final double[] pixelScale = readPixelScale();
        final double[] tiePoint = readTiePoint();
        
        if(pixelScale == null && tiePoint != null){
            
            final LocalizationGrid grid = new LocalizationGrid(2, 2);
            grid.setLocalizationPoint(0, 0, tiePoint[3], tiePoint[4]);
            grid.setLocalizationPoint(1, 0, tiePoint[9], tiePoint[10]);
            grid.setLocalizationPoint(1, 1, tiePoint[15], tiePoint[16]);
            grid.setLocalizationPoint(0, 1, tiePoint[21], tiePoint[22]);            
            final AffineTransform gridToCRS = grid.getAffineTransform();
            gridToCRS.scale(1f/bounds.width, 1f/bounds.height);
            accesor.setAll(gridToCRS, bounds, cellGeometry, orientation);
            return;
            
        }else if(pixelScale != null && tiePoint != null){
            //TODO the is a third value in the tie point
            final double scaleX         = pixelScale[0];
            final double scaleY         = -pixelScale[1];
            final double tiePointColumn = tiePoint[0];
            final double tiePointRow    = tiePoint[1];
            final double translateX     = tiePoint[3] - (scaleX * tiePointColumn);
            final double translateY     = tiePoint[4] - (scaleY * tiePointRow);
            final AffineTransform gridToCRS = new AffineTransform(scaleX, 0, 0, scaleY, translateX, translateY);
            accesor.setAll(gridToCRS, bounds, cellGeometry, orientation);
            return;
        }

        //unknowned definition /////////////////////////////////////////////////
        throw new IOException("Unknowned Grid to CRS transformation definition.");
    }

    /**
     * Read the image size from the tags : ImageWidth and ImageLenght.
     */
    private Rectangle readBounds() throws IOException {
        final Rectangle rect = new Rectangle();
        
        final Node width = getNodeByNumber(imgFileDir, ImageWidth);
        rect.width = readTiffShorts(getNodeByLocalName(width, TAG_GEOTIFF_SHORTS))[0];
        final Node height = getNodeByNumber(imgFileDir, ImageLenght);
        rect.height = readTiffShorts(getNodeByLocalName(height, TAG_GEOTIFF_SHORTS))[0];

        return rect;
    }

    /**
     * Read the transformation from the TAG_MODEL_TRANSFORMATION if it exist.
     */
    private AffineTransform readTransformation() throws IOException {

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
     * Read the tie points from the TAG_MODEL_TIE_POINT if it exist.
     */
    private double[] readTiePoint() {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_TIE_POINT);
        if (node == null) {
            return null;
        }

        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if(valueNode == null){
            return null;
        }

        return readTiffDoubles(valueNode);
    }

    /**
     * Read the pixel scale from the TAG_MODEL_PIXEL_SCALE if it exist.
     */
    private double[] readPixelScale() {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_PIXEL_SCALE);
        if (node == null) {
            return null;
        }

        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if(valueNode == null){
            return null;
        }

        return readTiffDoubles(valueNode);
    }

    /**
     * Read values for the given tag number.
     * @param tagNumber
     * @param offset
     * @param lenght
     * @return
     * @throws IOException
     */
    private Object readValue(final int tagNumber, final int offset, final int lenght) throws IOException{
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

        final Object value;

        if(TAG_GEOTIFF_ASCII.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            value = readTiffAscii(valueNode);

        }else if(TAG_GEOTIFF_ASCIIS.equalsIgnoreCase(typeName)){
            value = readTiffAsciis(valueNode).substring(offset, offset+lenght);

        }else if(TAG_GEOTIFF_SHORT.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            value = readTiffShort(valueNode);

        }else if(TAG_GEOTIFF_SHORTS.equalsIgnoreCase(typeName)){

            final int[] shorts = readTiffShorts(valueNode);

            if(lenght == 1){
                value = shorts[offset];
            }else{
                value = new int[lenght];
                System.arraycopy(shorts, offset, value, 0, lenght);
            }

        }else if(TAG_GEOTIFF_LONG.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            value = readTiffLong(valueNode);

        }else if(TAG_GEOTIFF_LONGS.equalsIgnoreCase(typeName)){
            final long[] longs = readTiffLongs(valueNode);

            if(lenght == 1){
                value = longs[offset];
            }else{
                value = new long[lenght];
                System.arraycopy(longs, offset, value, 0, lenght);
            }

        }else if(TAG_GEOTIFF_DOUBLE.equalsIgnoreCase(typeName)){
            if(lenght != 1){
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");
            }
            value = readTiffDouble(valueNode);

        }else if(TAG_GEOTIFF_DOUBLES.equalsIgnoreCase(typeName)){
            double[] doubles = readTiffDoubles(valueNode);
            if(lenght == 1){
                value = doubles[offset];
            }else{
                value = new double[lenght];
                System.arraycopy(doubles, offset, value, 0, lenght);
            }
        }else{
            throw new IOException("Incorrect metadata description, unknowned value type "
                    +typeName+" for tag number "+ tagNumber);
        }

        return value;
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
    
}
