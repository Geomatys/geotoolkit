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
package org.geotoolkit.metadata.dimap;

import java.util.Collections;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.CitationDate;
import java.util.Date;
import java.util.Arrays;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.opengis.metadata.Identifier;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.opengis.metadata.identification.CharacterSet;
import java.util.Locale;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.measure.unit.Unit;
import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.TypeMap;
import org.geotoolkit.lang.Static;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.WarpTransform2D;
import org.geotoolkit.util.NumberRange;

import org.opengis.coverage.SampleDimensionType;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.geotoolkit.metadata.dimap.DimapConstants.*;
import static org.geotoolkit.util.DomUtilities.*;

/**
 * Utility class to access usable objects from a dimap file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public final class DimapAccessor {

    private DimapAccessor() {
    }

    /**
     * Read the Coordinate Reference System of the grid.
     * Thoses informations are provided by the CoordinateReferenceSystem tag.
     * 
     * @param doc
     * @return CoordinateReferenceSystem
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    public static CoordinateReferenceSystem readCRS(Element doc) throws NoSuchAuthorityCodeException, FactoryException{
        final Element ele = firstElement(doc, TAG_CRS);
        final Element code = firstElement(ele, TAG_HORIZONTAL_CS_CODE);
        return CRS.decode(code.getTextContent());
    }

    /**
     * Read the Grid to CRS transform.
     * Thoses informations are provided by the Geoposition tag.
     *
     * @param doc
     * @return AffineTransform
     * @throws FactoryException
     * @throws TransformException
     */
    public static AffineTransform readGridToCRS(Element doc) throws FactoryException, TransformException{
        final Element ele = firstElement(doc, TAG_GEOPOSITION);
        final Element insert = firstElement(ele, TAG_GEOPOSITION_INSERT);
        final Element points = firstElement(ele, TAG_GEOPOSITION_POINTS);
        final Element affine = firstElement(ele, TAG_GEOPOSITION_AFFINE);

        if(insert != null){
            // X = ULXMAP + XDIM * i
            // Y = ULYMAP - YDIM * j
            final double ulx = textValueSafe(insert, TAG_ULXMAP, Double.class);
            final double uly = textValueSafe(insert, TAG_ULYMAP, Double.class);
            final double xdim = textValueSafe(insert, TAG_XDIM, Double.class);
            final double ydim = textValueSafe(insert, TAG_YDIM, Double.class);
            return new AffineTransform(xdim, 0, 0, -ydim, ulx, uly);
        }else if(affine != null){
            // X (CRS) = X0 + X1 * X(Data) + X2 * Y(Data)
            // Y (CRS) = Y0 + Y1 * X(Data) + Y2 * Y(Data)
            final double x0 = textValueSafe(affine, TAG_AFFINE_X0, Double.class);
            final double x1 = textValueSafe(affine, TAG_AFFINE_X1, Double.class);
            final double x2 = textValueSafe(affine, TAG_AFFINE_X2, Double.class);
            final double y0 = textValueSafe(affine, TAG_AFFINE_Y0, Double.class);
            final double y1 = textValueSafe(affine, TAG_AFFINE_Y1, Double.class);
            final double y2 = textValueSafe(affine, TAG_AFFINE_Y2, Double.class);
            return new AffineTransform(x0, y0, x1, y1, x2, y2);
        }else if(points != null){
            // transformation in not accurate if the method has been defined.
            // read the points and calculate an average transform from them.
            final NodeList tiePoints = ele.getElementsByTagName(TAG_TIE_POINT);
            final List<Point2D> sources = new ArrayList<Point2D>();
            final List<Point2D> dests = new ArrayList<Point2D>();

            for(int i=0,n=tiePoints.getLength();i<n;i++){
                final Element vertex = (Element) tiePoints.item(i);
                final double coordX = textValueSafe(vertex, TAG_TIE_POINT_CRS_X, Double.class);
                final double coordY = textValueSafe(vertex, TAG_TIE_POINT_CRS_Y, Double.class);
                final int dataY = textValueSafe(vertex, TAG_TIE_POINT_DATA_X, Integer.class);
                final int dataX = textValueSafe(vertex, TAG_TIE_POINT_DATA_Y, Integer.class);
                sources.add(new Point2D.Double(dataX,dataY));
                dests.add(new Point2D.Double(coordX,coordY));
            }

            final WarpTransform2D warptrs = new WarpTransform2D(
                    sources.toArray(new Point2D[sources.size()]),
                    dests.toArray(new Point2D[dests.size()]), 1);

            final Warp warp = warptrs.getWarp();
            if(warp instanceof WarpAffine){
                final WarpAffine wa = (WarpAffine) warp;
                return wa.getTransform();
            }else{
                throw new TransformException("Wrap transform is not affine.");
            }
        }else{
            throw new TransformException("Geopositioning type unknowned.");
        }
        
    }

    /**
     * Read the raster dimension from the document. This include number of rows,
     * columns and bands.
     * Thoses informations are provided by the Raster_dimensions tag.
     * 
     * @param doc
     * @return int[] 0:rows, 1:cols, 2:bands
     */
    public static int[] readRasterDimension(Element doc){
        final Element ele = firstElement(doc, TAG_RASTER_DIMENSIONS);
        final int rows = textValueSafe(ele, TAG_NROWS, Integer.class);
        final int cols = textValueSafe(ele, TAG_NCOLS, Integer.class);
        final int bands = textValueSafe(ele, TAG_NBANDS, Integer.class);
        return new int[]{rows,cols,bands};
    }

    /**
     * Read the coverage sample dimensions.
     * Thoses informations are provided by the Image_display tag.
     *
     * @param parent
     * @return GridSampleDimension
     */
    public static int[] readColorBandMapping(Element parent){
        final Element ele = firstElement(parent, TAG_IMAGE_DISPLAY);
        if(ele == null) return null;
        final Element displayOrder = firstElement(ele, TAG_BAND_DISPLAY_ORDER);
        if(displayOrder == null) return null;

        //those parameters are mandatory
        final int red = textValueSafe(displayOrder, TAG_RED_CHANNEL, Integer.class);
        final int green = textValueSafe(displayOrder, TAG_GREEN_CHANNEL, Integer.class);
        final int blue = textValueSafe(displayOrder, TAG_BLUE_CHANNEL, Integer.class);

        return new int[]{red-1,green-1,blue-1};
    }

    /**
     * Read the coverage sample dimensions.
     * Thoses informations are provided by dimap tags :
     * - Image_Interpretation for description and sample to geophysic.
     * - Image_display for special values.
     * - Raster_Encoding for sample model bytes encoding
     * 
     * @param doc
     * @return GridSampleDimension
     */
    public static GridSampleDimension[] readSampleDimensions(Element doc, String coverageName,int nbbands){

        // read raster encoding informations -----------------------------------
        final Element nodeEncoding = firstElement(doc, TAG_RASTER_ENCODING);
        final int nbits         = textValueSafe(nodeEncoding, TAG_NBITS, Integer.class);
        final String byteOrder  = textValueSafe(nodeEncoding, TAG_BYTEORDER, String.class);
        final String dataType   = textValueSafe(nodeEncoding, TAG_DATA_TYPE, String.class);
        final Integer skip      = textValueSafe(nodeEncoding, TAG_SKIP_BYTES, Integer.class);
        final String layout     = textValueSafe(nodeEncoding, TAG_BANDS_LAYOUT, String.class);
        final SampleDimensionType dimensionType = TypeMap.getSampleDimensionType(DataType.valueOf(dataType).getNumberSet(),nbits);


        // read special values -------------------------------------------------
        final Element nodeDisplay   = firstElement(doc, TAG_IMAGE_DISPLAY);
        final Element nodeBandOrder = firstElement(nodeDisplay, TAG_BAND_DISPLAY_ORDER);
        final Integer red   = textValueSafe(nodeBandOrder, TAG_RED_CHANNEL, Integer.class);
        final Integer green = textValueSafe(nodeBandOrder, TAG_GREEN_CHANNEL, Integer.class);
        final Integer blue  = textValueSafe(nodeBandOrder, TAG_BLUE_CHANNEL, Integer.class);


        // read band statistics ------------------------------------------------
        final NodeList nodeStats = nodeDisplay.getElementsByTagName(TAG_BAND_STATISTICS);
        final Map<Integer,NumberRange> valueRanges = new HashMap<Integer, NumberRange>();
        for(int i=0,n=nodeStats.getLength(); i<n ;i++){
            final Element bandStat = (Element) nodeStats.item(i);
            final double stxMin     = textValueSafe(bandStat, TAG_STX_MIN, Double.class);
            final double stxMax     = textValueSafe(bandStat, TAG_STX_MAX, Double.class);
            final double stxMean    = textValueSafe(bandStat, TAG_STX_MEAN, Double.class);
            final double stxStdv    = textValueSafe(bandStat, TAG_STX_STDV, Double.class);
            final double stxLinMin  = textValueSafe(bandStat, TAG_STX_LIN_MIN, Double.class);
            final double stxLinMax  = textValueSafe(bandStat, TAG_STX_LIN_MAX, Double.class);
            final int bandIndex     = textValueSafe(bandStat, TAG_BAND_INDEX, Integer.class);
            valueRanges.put(bandIndex, NumberRange.create(stxMin, stxMax));
        }

        // read dimensions -----------------------------------------------------
        final Element nodeInterpretation = firstElement(doc, TAG_IMAGE_INTERPRETATION);
        final NodeList spectrals = nodeInterpretation.getElementsByTagName(TAG_SPECTRAL_BAND_INFO);
        final Map<Integer,GridSampleDimension> dimensions = new HashMap<Integer, GridSampleDimension>();

        for(int i=0,n=spectrals.getLength(); i<n ;i++){
            final Element spectre = (Element) spectrals.item(i);

            /*
            This record provides the unit of the physical value resulting from data radiometric count
            to physical measure conversion such as Illumination or height :
            L = X/A + B

            - L is the resulting physical value expressed in PHYSICAL_UNIT
            - X is the radiometric value at a given pixel location as stored in the raster file (unitless).
            - A is the gain (PHYSICAL_GAIN)
            - B is the bias (PHYSICAL_BIAS)
             */

            final int bandIndex     = textValueSafe(spectre, TAG_BAND_INDEX, Integer.class);
            final String bandDesc   = textValueSafe(spectre, TAG_BAND_DESCRIPTION, String.class);
            final String physicUnit = textValueSafe(spectre, TAG_PHYSICAL_UNIT, String.class);
            final double physicGain = textValueSafe(spectre, TAG_PHYSICAL_GAIN, Double.class);
            final double physicBias = textValueSafe(spectre, TAG_PHYSICAL_BIAS, Double.class);

            Unit unit = null;
            try{
                Unit.valueOf(physicUnit.trim());
            }catch(Exception ex){
                //catch anything, this doesn't always throw parse exception
                unit = Unit.ONE;
            }

            //range is in geophysic values, can not use it, todo convert it.
            final NumberRange range = valueRanges.get(bandIndex);

            final Category[] cats = new Category[]{
                new Category("vals", null, Integer.MIN_VALUE,Integer.MAX_VALUE,1/physicGain, physicBias)
            };

            final GridSampleDimension dim = new GridSampleDimension(bandDesc, cats, unit);
            dimensions.put(bandIndex, dim);
        }

        final GridSampleDimension[] dims = new GridSampleDimension[nbbands];
        for(int i=0; i<nbbands; i++){
            GridSampleDimension dim = dimensions.get(i+1);
            if(dim == null){
                //no information on this band, create an empty one
                dim = new GridSampleDimension(String.valueOf(i+1));
            }
            dims[i] = dim;
        }

        return dims;
    }

    /**
     * @return DatasetName from Dataset_ID tag. 
     */
    public static String readDatasetName(Element doc){
        final Element datasetID = firstElement(doc, TAG_DATASET_ID);
        final String name = textValueSafe(datasetID, TAG_DATASET_NAME, String.class);
        return name;
    }

    /**
     * Converts the given dimap document in a metadata object.
     * Since there is no one to one relation between ISO 19115 and Dimap,
     * the returned metadta is a best effort relation.
     *
     * @param doc
     * @param metadata : metadata to fill, if null it will create one.
     * @return Metadata, never null
     */
    public static DefaultMetadata fillMetadata(Element doc, DefaultMetadata metadata){

        if(metadata == null){
            metadata = new DefaultMetadata();
        }

        //default values
        metadata.setCharacterSet(CharacterSet.UTF_8);
        metadata.setLanguage(Locale.ENGLISH);

        // identification ----------------------------------------------------------
        //dataset id
        final Element datasetID = firstElement(doc, TAG_DATASET_ID);
        final String name = textValueSafe(datasetID, TAG_DATASET_NAME, String.class);
        final String copyright = textValueSafe(datasetID, TAG_DATASET_COPYRIGHT, String.class);

        final DefaultDataIdentification identificationInfo = new DefaultDataIdentification();
        final DefaultCitation citation = new DefaultCitation();
        citation.setTitle(new SimpleInternationalString(name));
        Identifier id = new DefaultIdentifier(name);
        citation.setIdentifiers(Arrays.asList(id));
        final CitationDate creationDate = new DefaultCitationDate(new Date(), DateType.CREATION);
        citation.setDates(Arrays.asList(creationDate));
        identificationInfo.setCitation(citation);

        metadata.setIdentificationInfo(Collections.singleton(identificationInfo));


        //TODO

        return metadata;
    }

}
