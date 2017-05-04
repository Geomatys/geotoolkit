/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet;

import java.util.logging.Level;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.imageio.metadata.IIOMetadata;

import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;

import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.matrix.Matrix3;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.matrix.NoninvertibleMatrixException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;

import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.jdk8.JDK8;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.referencing.operation.MathTransforms;

import org.w3c.dom.Node;

import static com.sun.media.imageio.plugins.tiff.GeoTIFFTagSet.*;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultImageCRS;
import org.apache.sis.referencing.operation.matrix.Matrix2;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffMetaDataUtils.*;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import static org.geotoolkit.util.DomUtilities.*;

/**
 * Utility class to read geotiff metadata tags.
 * http://www.remotesensing.org/geotiff/faq.html
 *
 * @author Johann Sorel  (Geomatys)
 * @author Remi Marechal (Geomatys)
 */
public final class GeoTiffMetaDataReader {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.metadata.geotiff");

    public static class ValueMap extends HashMap<Integer, Object> {

        /**
     * @param key
     * @return A string representing the value, or null if the key was not
     *         found or failed to parse.
     */
    public String getAsString(final int key) {

            final Object value = get(key);

            if (value instanceof String) return (String) value;

            if (value instanceof Number) return ((Number)value).toString();

            return null;
        }

        public double getAsDouble(final int key) {
            final Object value = get(key);

            if (value == null)           return Double.NaN;
            if (value instanceof Number) return ((Number)value).doubleValue();

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
        }
    }

    private final IIOMetadata imageMetadata;
    private final Node root;
    private final Node imgFileDir;
    private final Node geoKeyDir;

    public GeoTiffMetaDataReader(final IIOMetadata imageMetadata) throws IOException{
        this.imageMetadata = imageMetadata;
        root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
        if (root == null) throw new IOException("No image metadatas");

        imgFileDir = getNodeByLocalName(root,TAG_GEOTIFF_IFD);
        if (imgFileDir == null) throw new IOException("No GeoTiff metadatatas");

        geoKeyDir = getNodeByNumber(imgFileDir,TAG_GEO_KEY_DIRECTORY);
    }

    /**
     * Read the Spatial Metadatas.
     *
     * @return SpatialMetadata
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     * @throws java.io.IOException
     */
    public SpatialMetadata readSpatialMetaData() throws NoSuchAuthorityCodeException, FactoryException, IOException {

        final SpatialMetadata spatialMetadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(SpatialMetadataFormat.GEOTK_FORMAT_NAME));

        if (geoKeyDir != null) {
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
                if (valuelocation == 0) {
                    //value is located in the offset field
                    value = valueOffset;
                } else {
                    //value is in another tag
                    value = readValue(valuelocation, valueOffset, valueNb);
                }
                entries.put(valueKey, value);
            }

            //create the spatial metadatas.
            fillGridMetaDatas(spatialMetadata, entries);

            final GeoTiffCRSReader crsReader = new GeoTiffCRSReader();
            crsReader.fillCRSMetaDatas(spatialMetadata, entries);
        } else {
            new ReferencingBuilder(spatialMetadata).setCoordinateReferenceSystem(PredefinedCRS.GRID_2D);
            GridDomainAccessor gridDomainAccessor = new GridDomainAccessor(spatialMetadata);
            gridDomainAccessor.setAll(AffineTransforms2D.castOrCopy(new Matrix3()), readBounds(),
                                      CellGeometry.AREA, PixelOrientation.UPPER_LEFT);
            spatialMetadata.clearInstancesCache();

        }

        //-- looks for additional informations
        final ThirdPartyMetaDataReader thirdReader = new ThirdPartyMetaDataReader(imageMetadata);
        thirdReader.fillSpatialMetaData(spatialMetadata);

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
        PixelOrientation orientation;
        if (value != null) {
            int type = (Integer)value;

            //-- faire un log
            if (type < 1 || type > 2) {
                final String strLog = "Undefine raster Type from geotiff metadatas : \n"
                        + "From GeoKeyDirectoryTag (34735) the internaly key GTRasterTypeGeoKey (1025) should be : \n"
                        + "- 1 for RasterPixelIsArea, or \n"
                        + "- 2 for RasterPixelIsPoint.\n"
                        + "Bad founded raster Type value is : "+type;
                LOGGER.log(Level.SEVERE, strLog);
            }

            orientation = (type == RasterPixelIsPoint) ? PixelOrientation.CENTER : PixelOrientation.UPPER_LEFT;
        } else {
            orientation = PixelOrientation.UPPER_LEFT;
        }
        final CellGeometry cellGeometry = (orientation == PixelOrientation.UPPER_LEFT)
                                          ? CellGeometry.AREA : CellGeometry.POINT;

        //-- read the image bounds
        final Rectangle bounds = readBounds();

        //-- check if a transformation is present /////////////////////////////////
        AffineTransform gridToCRS = readTransformation();
        if (gridToCRS == null) {
            //-- check for pixel scale and tie points /////////////////////////////////
            final double[] pixelScale = readPixelScale();
            final double[] tiePoint   = readTiePoint();

            if (pixelScale == null && tiePoint != null) {

                final int l = tiePoint.length;
                assert l % 6 == 0 : "In tiff specification tiePoint array length should be congrue 6.";

                //-- pixelOrientation Offset
                final int pIOffset = (orientation.equals(PixelOrientation.CENTER)) ? 1 : 0;

                final double[] lowerLeftCorner     = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0, 0}; //-- xmin ymin
                final double[] lowerRightCorner    = new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0, 0}; //-- xmax ymin
                final double[] upperLeftCorner     = new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0, 0, 0, 0}; //-- xmax ymax
                final double[] upperRightCorner    = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 0, 0, 0, 0}; //-- xmax ymax

                //-- first loop to find the two minimum grid poins in X axis to define XStep
                //-- and also find the two minimum grid poins in Y axis to define YStep
                //-- in other to know if grid is regulary
                for (int i = 0; i < l; i += 6) {
                    final double currentX = tiePoint[i];
                    final double currentY = tiePoint[i+1];

                    if (currentX <= lowerLeftCorner[0]
                     && currentY <= lowerLeftCorner[1]) {
                        System.arraycopy(tiePoint, i, lowerLeftCorner, 0, 6);
                    }
                    if (currentX >= lowerRightCorner[0]
                     && currentY <= lowerRightCorner[1]) {
                        System.arraycopy(tiePoint, i, lowerRightCorner, 0, 6);
                    }
                    if (currentX >= upperRightCorner[0]
                     && currentY >= upperRightCorner[1]) {
                        System.arraycopy(tiePoint, i, upperRightCorner, 0, 6);
                    }
                    if (currentX <= upperLeftCorner[0]
                     && currentY >= upperLeftCorner[1]) {
                        System.arraycopy(tiePoint, i, upperLeftCorner, 0, 6);
                    }
                }

                //-- the fourth corner points found
                assert JDK8.isFinite(lowerLeftCorner[0])  : "lowerLeftCorner  grid point not found";
                assert JDK8.isFinite(lowerRightCorner[0]) : "lowerRightCorner grid point not found";
                assert JDK8.isFinite(upperLeftCorner[0])  : "upperLeftCorner  grid point not found";
                assert JDK8.isFinite(upperRightCorner[0]) : "upperRightCorner grid point not found";


                ////////////////////////////////////////////////////////////////////////////////
                //// Commentary code to support NON LINEAR GRIDTOCRS into coverage metadata ////
                //// TODO : add non linear MathTransform into setUserObject from            ////
                //// metadatas for Coverage gridtocrs and use it during pyramid build       ////
                ////////////////////////////////////////////////////////////////////////////////


//                double[] preLowerCorner = null;
//                double[] preUpperCorner = null;
//
//                //-- round value because expected value at 0 and 1 array index are integer raster coordinates.
//                int stepX = (int) StrictMath.round(upperRightCorner[0] - lowerLeftCorner[0]);
//                int stepY = (int) StrictMath.round(upperRightCorner[1] - lowerLeftCorner[1]);
//
//                //-- if more than 5 points
//                if (l > 30) {
//                    preLowerCorner = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0, 0};
//                    preUpperCorner = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 0, 0, 0, 0};
//
//                    //-- I didn't find algorithm to avoid this second loop
//                    for (int i = 0; i < l; i += 6) {
//                        final double currentX = tiePoint[i];
//                        final double currentY = tiePoint[i+1];
//                        if (currentX >  lowerLeftCorner[0]
//                         && currentX <= preLowerCorner[0]
//                         && currentY >  lowerLeftCorner[1]
//                         && currentY <= preLowerCorner[1]) {
//                            System.arraycopy(tiePoint, i, preLowerCorner, 0, 6);
//                        }
//                        if (currentX <  upperRightCorner[0]
//                         && currentX >= preUpperCorner[0]
//                         && currentY <  upperRightCorner[1]
//                         && currentY >= preUpperCorner[1]) {
//                            System.arraycopy(tiePoint, i, preUpperCorner, 0, 6);
//                        }
//                    }
//                    assert JDK8.isFinite(preLowerCorner[0]) : "preLowerCorner grid point not found";
//                    assert JDK8.isFinite(preUpperCorner[0]) : "preUpperCorner grid point not found";
//
//                    stepX = (int) StrictMath.round(preLowerCorner[0] - lowerLeftCorner[0]);
//                    stepY = (int) StrictMath.round(preLowerCorner[1] - lowerLeftCorner[1]);
//
//                }
//
//                assert stepX >= 1;// >= 1
//                assert stepY >= 1;// >= 1
//
//                double[] gridLowerCorner = lowerLeftCorner;
//                double[] gridUpperCorner = null;
//
//                //-- often last higher point on X or Y direction is not congrue to regular points step.
//                //-- try to generate regular grid without last higher border points.
//                if ((int) StrictMath.round(upperRightCorner[0] - lowerLeftCorner[0]) % stepX == 0
//                 && (int) StrictMath.round(upperRightCorner[1] - lowerLeftCorner[1]) % stepY == 0) {
//                    gridUpperCorner = upperRightCorner;
//                } else {
//                    gridUpperCorner = preUpperCorner;
//                }
//
//                assert gridUpperCorner != null : "gridUpperCorner should not be null.";
//
//                final int gridWidth  = (int) StrictMath.round(gridUpperCorner[0] - gridLowerCorner[0]);
//                final int gridHeight = (int) StrictMath.round(gridUpperCorner[1] - gridLowerCorner[1]);
//
//                BitSet bit                 = null;
//                LocalizationGrid grid      = null;
//                int expectedBitCardinality = -1;
//
//                if (gridWidth  % stepX == 0
//                 && gridHeight % stepY == 0) {
//                    final int nbPointX = gridWidth  / stepX + 1;//-- [p0 -- step --> p1 -- step --> p2]
//                    final int nbPointY = gridHeight / stepY + 1;
//                    expectedBitCardinality = nbPointX * nbPointY;
//
//                    grid = new LocalizationGrid(nbPointX, nbPointY);
//                    bit  = new BitSet(expectedBitCardinality);
//
//                    for (int i = 0; i < l; i += 6) {
//                        final double currentX = tiePoint[i];
//                        final double currentY = tiePoint[i+1];
//
//                        //-- case where we ignore higher border points
//                        //-- avoid higher border points
//                        if (currentX > gridUpperCorner[0]
//                         || currentY > gridUpperCorner[1]) {
//                            continue;
//                        }
//
//                        final int px = (int) StrictMath.round(currentX - gridLowerCorner[0]);
//                        final int py = (int) StrictMath.round(currentY - gridLowerCorner[1]);
//
//                        assert StrictMath.abs(px - tiePoint[i])   < 1E-9;
//                        assert StrictMath.abs(py - tiePoint[i+1]) < 1E-9;
//
//                        //-- current point coordinates are congrue step
//                        if (px % stepX != 0
//                         || py % stepY != 0) {
//                            break;
//                        }
//                        bit.set(py * nbPointX + px, true);
//                        grid.setLocalizationPoint(px / stepX,  //-- src  X
//                                                  py / stepY,  //-- src  Y
//                                                  tiePoint[i + 3],  //-- dest X
//                                                  tiePoint[i + 4]); //-- dest Y
//                    }
//                }
//
//                //-- if all points represent regulary grid
//                if (bit != null && bit.cardinality() == expectedBitCardinality) {
//
//                    //-- gridToCrs create from grid
//                    assert grid != null;
//                    gridToCRS = grid.getAffineTransform();
//                    gridToCRS.transform(new Point2D.Double(10, 4), null);
//                    gridToCRS.scale(1.0 / stepX, 1.0 / stepY);
//                    gridToCRS.translate(lowerLeftCorner[0] - pIOffset, lowerLeftCorner[1] - pIOffset);
//
//                } else {

                    //-- create matrix transformation from first fourth corner points.
                    //-- note : src points always define as PixelInCell.CELL_CORNER
                    //-- for PixelInCell.CELL_CENTER a translation is effectuate later.
                    final Matrix3 srcPoints  = new Matrix3(lowerLeftCorner[0] - pIOffset, lowerRightCorner[0] - pIOffset, upperRightCorner[0] - pIOffset,
                                                           lowerLeftCorner[1] - pIOffset, lowerRightCorner[1] - pIOffset, upperRightCorner[1] - pIOffset,
                                                                        1,                              1,                              1);
                    final Matrix3 destPoints = new Matrix3(lowerLeftCorner[3], lowerRightCorner[3], upperRightCorner[3],
                                                           lowerLeftCorner[4], lowerRightCorner[4], upperRightCorner[4],
                                                                    1,                  1,                  1);

                    MatrixSIS gtcrs;
                    try {
                        gtcrs = destPoints.multiply(srcPoints.inverse());
                    } catch (NoninvertibleMatrixException ex) {
                        throw new IOException("Grid To Crs creation impossible to inverse source points.", ex);
                    }

                    //-- last matrix row verification
                    //-- avoid double approximation error
                    double epsilon = 0;
                    for (int j = 0; j < 3; j++) {
                        final double elt = gtcrs.getElement(2, j);
                        epsilon += elt * elt;
                    }
                    epsilon  = StrictMath.sqrt(epsilon); //-- compute row vector magnitude
                    epsilon *= 1E-15; //-- define tolerance from vector magnitude.


                    for (int j = 0; j < 3; j++) {
                        final double elt = gtcrs.getElement(2, j);
                        if (elt < epsilon) gtcrs.setElement(2, j, 0);
                    }
                    gridToCRS = AffineTransforms2D.castOrCopy(org.apache.sis.referencing.operation.transform.MathTransforms.linear(gtcrs).getMatrix());
//                }

            } else if (pixelScale != null && tiePoint != null) {
                //TODO the is a third value in the tie point
                final double scaleX         = pixelScale[0];
                final double scaleY         = -pixelScale[1];
                final double tiePointColumn = tiePoint[0];
                final double tiePointRow    = tiePoint[1];
                final double translateX     = tiePoint[3] - (scaleX * tiePointColumn);
                final double translateY     = tiePoint[4] - (scaleY * tiePointRow);
                gridToCRS = new AffineTransform(scaleX, 0, 0, scaleY, translateX, translateY);
            }
        }

        if (gridToCRS != null) {

            // force orientation to PixelOrientation.UPPER_LEFT to keep consistent transformation
            // when we add additional dimensions.
            if (PixelOrientation.CENTER.equals(orientation)) {
                LinearTransform linear = MathTransforms.linear(gridToCRS);
                LinearTransform translate = (LinearTransform)PixelTranslation.translate(linear, PixelInCell.CELL_CENTER, PixelInCell.CELL_CORNER);
                gridToCRS = AffineTransforms2D.castOrCopy(translate.getMatrix());
                orientation = PixelOrientation.UPPER_LEFT;
            }
            accesor.setAll(gridToCRS, bounds, cellGeometry, orientation);
            return;
        }

        //unknowned definition /////////////////////////////////////////////////
        LOGGER.log(Level.FINE, "Unknowned Grid to CRS transformation definition from image internaly metadatas");
    }

    /**
     * Returns the image size from the tags : {@link GeoTiffConstants#ImageWidth} and {@link GeoTiffConstants#ImageLength}.<br>
     * May return {@code null}.
     *
     * @return a {@link Rectangle} if expected geotiff tags are filled, else return {@code null}.
     */
    private Rectangle readBounds() {

        //--  get metadata node about image boundary
        final Node width  = getNodeByNumber(imgFileDir, ImageWidth);
        final Node height = getNodeByNumber(imgFileDir, ImageLength);
        if (width == null || height == null) return null;

        //-- study width
        int rectWidth = -1;

        //-- value can be stored in a short field
        Node widthNode = getNodeByLocalName(width, TAG_GEOTIFF_SHORTS);
        if(widthNode != null) rectWidth = readTiffShorts(widthNode)[0];

        //-- can be in a long field
        if (rectWidth == -1) {
            if(widthNode == null) widthNode = getNodeByLocalName(width, TAG_GEOTIFF_LONGS);
            if(widthNode != null) rectWidth = (int) readTiffLongs(widthNode)[0];
        }

        if (rectWidth == -1) {
            LOGGER.info("Unable to find geographic image boundary. Image width.");
            return null;
        }

        //-- study height
        int rectHeight = -1;
        //-- value can be stored in a short field
        Node heightNode = getNodeByLocalName(height, TAG_GEOTIFF_SHORTS);
        if(heightNode != null) rectHeight = readTiffShorts(heightNode)[0];

        //-- can be in a long field
        if (rectHeight == -1) {
            if(heightNode == null) heightNode = getNodeByLocalName(height, TAG_GEOTIFF_LONGS);
            if(heightNode != null) rectHeight = (int) readTiffLongs(heightNode)[0];
        }

        if (rectHeight == -1) {
            LOGGER.info("Unable to find geographic image boundary. Image height.");
            return null;
        }

        assert rectWidth  >= 0 : "SpatialMetadata : Geographic image boudary.width should be positive.";
        assert rectHeight >= 0 : "SpatialMetadata : Geographic image boudary.height should be positive.";

        return new Rectangle(rectWidth, rectHeight);
    }

    /**
     * Returns the transformation from the {@link GeoTIFFTagSet#TAG_MODEL_TRANSFORMATION} if it exist.<br>
     * May return {@code null}.
     *
     * @return an {@link AffineTransform} if expected geotiff tags are filled, else return {@code null}.
     * @throws IOException if defined transformation is unknow.
     */
    private AffineTransform readTransformation() throws IOException {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_TRANSFORMATION);
        if (node == null) return null;

        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if(valueNode == null) return null;

        //-- TODO find a multidimensional way
        final double[] matrix = readTiffDoubles(valueNode);
        if (matrix.length == 9) {
            //2D matrix
            return new AffineTransform(matrix[0],matrix[4],matrix[1],
                                       matrix[5],matrix[6],matrix[7]);
        } else if (matrix.length == 16) {
            //3D matrix, only keep the 2d part
            return new AffineTransform(matrix[0],matrix[4],matrix[1],
                                       matrix[5],matrix[3],matrix[7]);
        } else {
            throw new IOException("Unvalid transformation definition. expected 9 or 16 parameters but was "+matrix.length);
        }
    }

    /**
     * Read the tie points from the TAG_MODEL_TIE_POINT if it exist.
     */
    private double[] readTiePoint() {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_TIE_POINT);
        if (node == null) return null;

        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if (valueNode == null) return null;

        return readTiffDoubles(valueNode);
    }

    /**
     * Read the pixel scale from the TAG_MODEL_PIXEL_SCALE if it exist.
     */
    private double[] readPixelScale() {

        final Node node = getNodeByNumber(imgFileDir, TAG_MODEL_PIXEL_SCALE);
        if (node == null) return null;

        final Node valueNode = getNodeByLocalName(node, TAG_GEOTIFF_DOUBLES);
        if(valueNode == null) return null;

        return readTiffDoubles(valueNode);
    }

    /**
     * Read values for the given tag number.
     *
     * @param tagNumber
     * @param offset
     * @param lenght
     * @return
     * @throws IOException
     */
    private Object readValue(final int tagNumber, final int offset, final int lenght) throws IOException{
        final Node node = getNodeByNumber(imgFileDir, tagNumber);

        if (node == null)
            throw new IOException("Incorrect metadata description, no tag with number "+tagNumber);

        //node should have a single subNode containing the value
        final Node valueNode = node.getChildNodes().item(0);

        if (valueNode == null)
            throw new IOException("Incorrect metadata description, no value in tag number "+tagNumber);

        final String typeName = valueNode.getLocalName();
        final Object value;

        if (TAG_GEOTIFF_ASCII.equalsIgnoreCase(typeName)) {

            if (lenght != 1)
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");

            value = readTiffAscii(valueNode);

        } else if (TAG_GEOTIFF_ASCIIS.equalsIgnoreCase(typeName)) {

            value = readTiffAsciis(valueNode).substring(offset, offset+lenght);

        } else if (TAG_GEOTIFF_SHORT.equalsIgnoreCase(typeName)) {

            if (lenght != 1)
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");

            value = readTiffShort(valueNode);

        } else if (TAG_GEOTIFF_SHORTS.equalsIgnoreCase(typeName)) {

            final int[] shorts = readTiffShorts(valueNode);

            if (lenght == 1) {
                value = shorts[offset];
            } else {
                value = new int[lenght];
                System.arraycopy(shorts, offset, value, 0, lenght);
            }

        } else if (TAG_GEOTIFF_LONG.equalsIgnoreCase(typeName)) {
            if (lenght != 1)
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");

            value = readTiffLong(valueNode);

        } else if (TAG_GEOTIFF_LONGS.equalsIgnoreCase(typeName)) {

            final long[] longs = readTiffLongs(valueNode);

            if (lenght == 1) {
                value = longs[offset];
            } else {
                value = new long[lenght];
                System.arraycopy(longs, offset, value, 0, lenght);
            }

        } else if (TAG_GEOTIFF_DOUBLE.equalsIgnoreCase(typeName)) {
            if(lenght != 1)
                throw new IOException("Incorrect metadata description, single value type "
                        +typeName+" used to retrieve more than one value");

            value = readTiffDouble(valueNode);

        } else if (TAG_GEOTIFF_DOUBLES.equalsIgnoreCase(typeName)) {

            final double[] doubles = readTiffDoubles(valueNode);
            if (lenght == 1) {
                value = doubles[offset];
            } else {
                value = new double[lenght];
                System.arraycopy(doubles, offset, value, 0, lenght);
            }
        } else {
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
