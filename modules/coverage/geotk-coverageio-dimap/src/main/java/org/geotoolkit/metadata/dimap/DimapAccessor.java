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

import org.opengis.geometry.coordinate.GeometryFactory;
import static org.geotoolkit.metadata.dimap.DimapConstants.ATTRIBUTE_HREF;
import static org.geotoolkit.metadata.dimap.DimapConstants.ATT_HREF;
import static org.geotoolkit.metadata.dimap.DimapConstants.ATT_VERSION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_AFFINE_X0;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_AFFINE_X1;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_AFFINE_X2;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_AFFINE_Y0;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_AFFINE_Y1;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_AFFINE_Y2;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_BANDS_LAYOUT;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_BAND_DESCRIPTION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_BAND_DISPLAY_ORDER;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_BAND_INDEX;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_BAND_STATISTICS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_BLUE_CHANNEL;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_BYTEORDER;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_CRS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_COPYRIGHT;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_FRAME;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_ID;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_NAME;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_PRODUCER_NAME;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_PRODUCER_URL;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_PRODUCTION_DATE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_QL_PATH;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_SOURCES;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATASET_TN_PATH;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATA_ACCESS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATA_FILE_FORMAT;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATA_PROCESSING;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATA_PROCESSING_ALGORITHM_NAME;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATA_PROCESSING_ALGORITHM_TYPE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DATA_TYPE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_DYNAMIC_STRETCH;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_GEOPOSITION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_GEOPOSITION_AFFINE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_GEOPOSITION_INSERT;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_GEOPOSITION_POINTS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_GREEN_CHANNEL;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_HIGH_THRESHOLD;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_HORIZONTAL_CS_CODE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_IMAGE_DISPLAY;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_IMAGE_INTERPRETATION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_JOB_ID;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_LOW_THRESHOLD;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_NBANDS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_NBITS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_NCOLS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_NROWS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PHYSICAL_BIAS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PHYSICAL_GAIN;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PHYSICAL_UNIT;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PROCESSING_OPTIONS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PRODUCTION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PRODUCTION_FACILITY;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PRODUCTION_FACILITY_PROCESSING_CENTER;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PRODUCTION_FACILITY_SOFTWARE_NAME;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PRODUCTION_FACILITY_SOFTWARE_VERSION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PRODUCT_INFO;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_PRODUCT_TYPE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_RASTER_DIMENSIONS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_RASTER_ENCODING;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_RED_CHANNEL;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_IMAGING_DATE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_INCIDENCE_ANGLE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_INSTRUMENT;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_INSTRUMENT_INDEX;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_MISSION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_MISSION_INDEX;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_PROCESSING_LEVEL;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_SOURCE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_SUN_AZIMUTH;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_SUN_ELEVATION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_THEORETICAL_RESOLUTION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_VIEWING_ANGLE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SCENE_GRID_REFERENCE;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SKIP_BYTES;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SOURCE_DESCRIPTION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SOURCE_INFORMATION;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_SPECTRAL_BAND_INFO;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_STX_LIN_MAX;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_STX_LIN_MIN;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_STX_MAX;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_STX_MEAN;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_STX_MIN;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_STX_STDV;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_TIE_POINT;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_TIE_POINT_CRS_X;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_TIE_POINT_CRS_Y;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_TIE_POINT_DATA_X;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_TIE_POINT_DATA_Y;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_THRESHOLDS;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_ULXMAP;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_ULYMAP;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_XDIM;
import static org.geotoolkit.metadata.dimap.DimapConstants.TAG_YDIM;
import static org.geotoolkit.util.DomUtilities.firstElement;
import static org.geotoolkit.util.DomUtilities.getListElements;
import static org.geotoolkit.util.DomUtilities.textAttributeValueSafe;
import static org.geotoolkit.util.DomUtilities.textValueSafe;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.unit.Unit;
import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.TypeMap;
import org.geotoolkit.geometry.isoonjts.GeometryUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSGeometryFactory;
import org.geotoolkit.internal.jaxb.gmi.MI_Metadata;
import org.geotoolkit.lang.Static;
import org.geotoolkit.metadata.dimap.DimapConstants.DataType;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.acquisition.DefaultAcquisitionInformation;
import org.geotoolkit.metadata.iso.acquisition.DefaultInstrument;
import org.geotoolkit.metadata.iso.acquisition.DefaultOperation;
import org.geotoolkit.metadata.iso.acquisition.DefaultPlatform;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.constraint.DefaultLegalConstraints;
import org.geotoolkit.metadata.iso.content.AbstractContentInformation;
import org.geotoolkit.metadata.iso.content.DefaultBand;
import org.geotoolkit.metadata.iso.content.DefaultImageDescription;
import org.geotoolkit.metadata.iso.distribution.DefaultFormat;
import org.geotoolkit.metadata.iso.extent.DefaultBoundingPolygon;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicDescription;
import org.geotoolkit.metadata.iso.identification.AbstractIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultBrowseGraphic;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.lineage.DefaultAlgorithm;
import org.geotoolkit.metadata.iso.lineage.DefaultLineage;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessStep;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessing;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;
import org.geotoolkit.metadata.iso.spatial.AbstractSpatialRepresentation;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.WarpTransform2D;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.logging.Logging;
import org.opengis.coverage.SampleDimensionType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.lineage.Lineage;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utility class to access usable objects from a dimap file.
 *
 * @author Johann Sorel (Geomatys)
 * @author Christophe Mourette (Geomatys)
 * @module pending
 */
@SuppressWarnings("restriction")
public final class DimapAccessor extends Static {

    private static final Logger LOGGER = Logging.getLogger(DimapAccessor.class);

    private DimapAccessor() {
    }

    /**
     * Read the Coordinate Reference System of the grid.
     * Those informations are provided by the CoordinateReferenceSystem tag.
     *
     * @param doc
     * @return CoordinateReferenceSystem
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    public static CoordinateReferenceSystem readCRS(final Element doc) throws NoSuchAuthorityCodeException, FactoryException {
        final Element ele = firstElement(doc, TAG_CRS);
        final Element code = firstElement(ele, TAG_HORIZONTAL_CS_CODE);
        return CRS.decode(code.getTextContent());
    }

    /**
     * Read the Grid to CRS transform.
     * Those informations are provided by the Geoposition tag.
     *
     * @param doc
     * @return AffineTransform
     * @throws FactoryException
     * @throws TransformException
     */
    public static AffineTransform readGridToCRS(final Element doc) throws FactoryException, TransformException {
        final Element ele = firstElement(doc, TAG_GEOPOSITION);
        final Element insert = firstElement(ele, TAG_GEOPOSITION_INSERT);
        final Element points = firstElement(ele, TAG_GEOPOSITION_POINTS);
        final Element affine = firstElement(ele, TAG_GEOPOSITION_AFFINE);

        if (insert != null) {
            // X = ULXMAP + XDIM * i
            // Y = ULYMAP - YDIM * j
            final double ulx = textValueSafe(insert, TAG_ULXMAP, Double.class);
            final double uly = textValueSafe(insert, TAG_ULYMAP, Double.class);
            final double xdim = textValueSafe(insert, TAG_XDIM, Double.class);
            final double ydim = textValueSafe(insert, TAG_YDIM, Double.class);
            return new AffineTransform(xdim, 0, 0, -ydim, ulx, uly);
        } else if (affine != null) {
            // X (CRS) = X0 + X1 * X(Data) + X2 * Y(Data)
            // Y (CRS) = Y0 + Y1 * X(Data) + Y2 * Y(Data)
            final double x0 = textValueSafe(affine, TAG_AFFINE_X0, Double.class);
            final double x1 = textValueSafe(affine, TAG_AFFINE_X1, Double.class);
            final double x2 = textValueSafe(affine, TAG_AFFINE_X2, Double.class);
            final double y0 = textValueSafe(affine, TAG_AFFINE_Y0, Double.class);
            final double y1 = textValueSafe(affine, TAG_AFFINE_Y1, Double.class);
            final double y2 = textValueSafe(affine, TAG_AFFINE_Y2, Double.class);
            return new AffineTransform(x0, y0, x1, y1, x2, y2);
        } else if (points != null) {
            // transformation in not accurate if the method has been defined.
            // read the points and calculate an average transform from them.
            final NodeList tiePoints = ele.getElementsByTagName(TAG_TIE_POINT);
            final List<Point2D> sources = new ArrayList<Point2D>();
            final List<Point2D> dests = new ArrayList<Point2D>();

            for (int i = 0, n = tiePoints.getLength(); i < n; i++) {
                final Element vertex = (Element) tiePoints.item(i);
                final double coordX = textValueSafe(vertex, TAG_TIE_POINT_CRS_X, Double.class);
                final double coordY = textValueSafe(vertex, TAG_TIE_POINT_CRS_Y, Double.class);
                final int dataY = textValueSafe(vertex, TAG_TIE_POINT_DATA_X, Integer.class);
                final int dataX = textValueSafe(vertex, TAG_TIE_POINT_DATA_Y, Integer.class);
                sources.add(new Point2D.Double(dataX, dataY));
                dests.add(new Point2D.Double(coordX, coordY));
            }

            final WarpTransform2D warptrs = new WarpTransform2D(
                    sources.toArray(new Point2D[sources.size()]),
                    dests.toArray(new Point2D[dests.size()]), 1);

            final Warp warp = warptrs.getWarp();
            if (warp instanceof WarpAffine) {
                final WarpAffine wa = (WarpAffine) warp;
                return wa.getTransform();
            } else {
                throw new TransformException("Wrap transform is not affine.");
            }
        } else {
            throw new TransformException("Geopositioning type unknowned.");
        }

    }

    /**
     * Read the raster dimension from the document. This include number of rows,
     * columns and bands.
     * Those informations are provided by the Raster_dimensions tag.
     *
     * @param doc
     * @return int[] 0:rows, 1:cols, 2:bands
     */
    public static int[] readRasterDimension(final Element doc) {
        final Element ele = firstElement(doc, TAG_RASTER_DIMENSIONS);
        final int rows = textValueSafe(ele, TAG_NROWS, Integer.class);
        final int cols = textValueSafe(ele, TAG_NCOLS, Integer.class);
        final int bands = textValueSafe(ele, TAG_NBANDS, Integer.class);
        return new int[]{rows, cols, bands};
    }

    /**
     * Read the number of bits used for each pixel of each band of the raster image.
     * 
     * @param doc
     * @return int the number of bits
     */
    public static int readNBits(final Element doc) {
        final Element nodeEncoding = firstElement(doc, TAG_RASTER_ENCODING);
        return textValueSafe(nodeEncoding, TAG_NBITS, Integer.class);
    }

    /**
     * Read the coverage sample dimensions.
     * Those informations are provided by the Image_display tag.
     *
     * @param parent
     * @return GridSampleDimension
     */
    public static int[] readColorBandMapping(final Element parent) {
        final Element ele = firstElement(parent, TAG_IMAGE_DISPLAY);
        if (ele == null) {
            return null;
        }
        final Element displayOrder = firstElement(ele, TAG_BAND_DISPLAY_ORDER);
        if (displayOrder == null) {
            return null;
        }

        //those parameters are mandatory
        final int red = textValueSafe(displayOrder, TAG_RED_CHANNEL, Integer.class);
        final int green = textValueSafe(displayOrder, TAG_GREEN_CHANNEL, Integer.class);
        final int blue = textValueSafe(displayOrder, TAG_BLUE_CHANNEL, Integer.class);

        return new int[]{red - 1, green - 1, blue - 1};
    }

    /**
     * Read the coverage sample dimensions.
     * Those informations are provided by dimap tags :
     * - Image_Interpretation for description and sample to geophysic.
     * - Image_display for special values.
     * - Raster_Encoding for sample model bytes encoding
     *
     * @param doc
     * @return GridSampleDimension
     */
    public static GridSampleDimension[] readSampleDimensions(final Element doc, final String coverageName, final int nbbands) {

        // read raster encoding informations -----------------------------------
        final Element nodeEncoding = firstElement(doc, TAG_RASTER_ENCODING);
        final int nbits = textValueSafe(nodeEncoding, TAG_NBITS, Integer.class);
        final String byteOrder = textValueSafe(nodeEncoding, TAG_BYTEORDER, String.class);
        final String dataType = textValueSafe(nodeEncoding, TAG_DATA_TYPE, String.class);
        final Integer skip = textValueSafe(nodeEncoding, TAG_SKIP_BYTES, Integer.class);
        final String layout = textValueSafe(nodeEncoding, TAG_BANDS_LAYOUT, String.class);
        final SampleDimensionType dimensionType = TypeMap.getSampleDimensionType(DataType.valueOf(dataType).getNumberSet(), nbits);


        // read special values -------------------------------------------------
        final Element nodeDisplay = firstElement(doc, TAG_IMAGE_DISPLAY);
        final Element nodeBandOrder = firstElement(nodeDisplay, TAG_BAND_DISPLAY_ORDER);
        final Integer red = textValueSafe(nodeBandOrder, TAG_RED_CHANNEL, Integer.class);
        final Integer green = textValueSafe(nodeBandOrder, TAG_GREEN_CHANNEL, Integer.class);
        final Integer blue = textValueSafe(nodeBandOrder, TAG_BLUE_CHANNEL, Integer.class);


        // read band statistics ------------------------------------------------
        final NodeList nodeStats = nodeDisplay.getElementsByTagName(TAG_BAND_STATISTICS);
        final Map<Integer, NumberRange> valueRanges = new HashMap<Integer, NumberRange>();
        for (int i = 0, n = nodeStats.getLength(); i < n; i++) {
            final Element bandStat = (Element) nodeStats.item(i);
            final double stxMin = textValueSafe(bandStat, TAG_STX_MIN, Double.class);
            final double stxMax = textValueSafe(bandStat, TAG_STX_MAX, Double.class);
            final double stxMean = textValueSafe(bandStat, TAG_STX_MEAN, Double.class);
            final double stxStdv = textValueSafe(bandStat, TAG_STX_STDV, Double.class);
            final double stxLinMin = textValueSafe(bandStat, TAG_STX_LIN_MIN, Double.class);
            final double stxLinMax = textValueSafe(bandStat, TAG_STX_LIN_MAX, Double.class);
            final int bandIndex = textValueSafe(bandStat, TAG_BAND_INDEX, Integer.class);
            valueRanges.put(bandIndex, NumberRange.create(stxMin, stxMax));
        }

        // read dimensions -----------------------------------------------------
        final Element nodeInterpretation = firstElement(doc, TAG_IMAGE_INTERPRETATION);
        final NodeList spectrals = nodeInterpretation.getElementsByTagName(TAG_SPECTRAL_BAND_INFO);
        final Map<Integer, GridSampleDimension> dimensions = new HashMap<Integer, GridSampleDimension>();

        for (int i = 0, n = spectrals.getLength(); i < n; i++) {
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

            final int bandIndex = textValueSafe(spectre, TAG_BAND_INDEX, Integer.class);
            final String bandDesc = textValueSafe(spectre, TAG_BAND_DESCRIPTION, String.class);
            final String physicUnit = textValueSafe(spectre, TAG_PHYSICAL_UNIT, String.class);
            final double physicGain = textValueSafe(spectre, TAG_PHYSICAL_GAIN, Double.class);
            final double physicBias = textValueSafe(spectre, TAG_PHYSICAL_BIAS, Double.class);

            Unit unit = null;
            try {
                Unit.valueOf(physicUnit.trim());
            } catch (Exception ex) {
                //catch anything, this doesn't always throw parse exception
                unit = Unit.ONE;
            }

            //range is in geophysic values, can not use it, todo convert it.
            final NumberRange range = valueRanges.get(bandIndex);

            final Category[] cats = new Category[]{
                new Category("vals", null, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 / physicGain, physicBias)
            };

            final GridSampleDimension dim = new GridSampleDimension(bandDesc, cats, unit);
            dimensions.put(bandIndex, dim);
        }

        final GridSampleDimension[] dims = new GridSampleDimension[nbbands];
        for (int i = 0; i < nbbands; i++) {
            GridSampleDimension dim = dimensions.get(i + 1);
            if (dim == null) {
                //no information on this band, create an empty one
                dim = new GridSampleDimension(String.valueOf(i + 1));
            }
            dims[i] = dim;
        }

        return dims;
    }

    /**
     * @return DatasetName from Dataset_ID tag.
     */
    public static String readDatasetName(final Element doc) {
        final Element datasetID = firstElement(doc, TAG_DATASET_ID);
        final String name = textValueSafe(datasetID, TAG_DATASET_NAME, String.class);
        return name;
    }

    /**
     * @return DatasetThumbnail from Dataset_ID tag.
     */
    public static Geometry readDatasetVertex(final Element doc) throws NoSuchAuthorityCodeException, FactoryException {
        final Element datasetFrame = firstElement(doc, TAG_DATASET_FRAME);
        final List<Element> vertexs = getListElements(datasetFrame, "Vertex");

        final CoordinateReferenceSystem crs = readCRS(doc);
        final GeometryFactory geometryFact = new JTSGeometryFactory(crs);

        final int len = vertexs.size();
        final DirectPosition[] exteriorRing = new DirectPosition[len];
        for (int i = 0; i < len; i++) {
            final Element vertex = vertexs.get(i);
            final Double lon = textValueSafe(vertex, "FRAME_LON", Double.class);
            final Double lat = textValueSafe(vertex, "FRAME_LAT", Double.class);

            final double[] coords = new double[2];
            coords[0] = lon;
            coords[1] = lat;
            exteriorRing[i] = geometryFact.createDirectPosition(coords);
        }

        return (Geometry) GeometryUtils.createPolygon(exteriorRing);
    }

    /**
     * @return DatasetThumbnail from Dataset_ID tag.
     */
    public static String readDatasetThumbnail(final Element doc) {
        final Element datasetID = firstElement(doc, TAG_DATASET_ID);
        final String name = textAttributeValueSafe(datasetID, TAG_DATASET_TN_PATH, ATTRIBUTE_HREF, String.class);
        return name.toLowerCase();
    }

    /**
     * @return DatasetQuickLook from Dataset_ID tag.
     */
    public static String readDatasetQuickLook(final Element doc) {
        final Element datasetID = firstElement(doc, TAG_DATASET_ID);
        final String name = textAttributeValueSafe(datasetID, TAG_DATASET_QL_PATH, ATTRIBUTE_HREF, String.class);
        return name.toLowerCase();
    }

    /**
     * Converts the given dimap document in a metadata object.
     * Since there is no one to one relation between ISO 19115 and Dimap,
     * the returned metadata is a best effort relation.
     *
     * @param doc
     * @param metadata : metadata to fill, if null it will create one.
     * @return Metadata, never null
     */
    public static DefaultMetadata fillMetadata(final Element doc, DefaultMetadata metadata) throws IOException {

        if (metadata == null) {
            metadata = new DefaultMetadata();
        } else {
            //To ensure we don't modify the original
            if (metadata instanceof MI_Metadata) {
                metadata = new MI_Metadata(metadata);
            } else {
                metadata = new DefaultMetadata(metadata);
            }
        }

        String thumbnail = null;
        String name = null;
        
        //Dimap_Document STRUCTURE
        //
        //<Metadata_Id/>                    - Mandatory
        //<Dataset_Id/>                     - Mandatory
        //<Dataset_Frame/>                  - Optional
        //<Coordinate_Reference_System/>    - Mandatory
        //<Raster_CS/>                      - Mandatory
        //<Geoposition/>                    - Mandatory
        //<Production/>                     - Mandatory
        //<Quality_Assessment/>             - Optional
        //<Raster_Dimensions/>              - Mandatory
        //<Raster_Encoding/>                - Mandatory
        //<Data_Processing/>                - Mandatory
        //<Data_Access/>                    - Mandatory
        //<Image_Display/>                  - Mandatory
        //<Image_Interpretation/>           - Mandatory
        //<Dataset_Sources/>                - Mandatory
        //<Data_Strip/>                     - Mandatory

        //Default values
        metadata.setCharacterSet(CharacterSet.UTF_8);
        metadata.setLanguage(Locale.ENGLISH);
        metadata.setDateStamp(new Date());

        //<xsd:element minOccurs="1" maxOccurs="1" ref="Dataset_Id"/> ----------
        final Element datasetID = firstElement(doc, TAG_DATASET_ID);
        if (datasetID != null) {
            //MAPPING
            //
            //<DATASET_NAME/>       → used to build : MetaData.fileIdentifier
            //<DATASET_TN_PATH/>    → ?
            //<DATASET_TN_FORMAT/>  → ?
            //<DATASET_QL_PATH/>    → ?
            //<DATASET_QL_FORMAT/>  → ?
            //<COPYRIGHT/>          → MetaData.metadataConstraints > LegalConstraints.otherConstraints
            
            final String copyright = textValueSafe(datasetID, TAG_DATASET_COPYRIGHT, String.class);
            thumbnail = textAttributeValueSafe(datasetID, TAG_DATASET_TN_PATH, ATTRIBUTE_HREF, String.class);
            name = textValueSafe(datasetID, TAG_DATASET_NAME, String.class);

            //MetaData > FileIdentifier
            metadata.setFileIdentifier(name.replaceAll(":", "_").replaceAll(" ", "_").replaceAll("/", "_"));

            //MetaData > MetadataConstraints
            final Restriction restriction = Restriction.COPYRIGHT;
            final DefaultLegalConstraints constraints = new DefaultLegalConstraints();
            constraints.setUseConstraints(Collections.singleton(restriction));
            constraints.setOtherConstraints(Collections.singleton(new SimpleInternationalString(copyright)));
            
            metadata.getMetadataConstraints().add(constraints);
        }

        //<xsd:element minOccurs="0" maxOccurs="1" ref="Dataset_Frame"/> -------
        //Has been set from the geotiff informations
        final Element datasetFrame = firstElement(doc, TAG_DATASET_FRAME);
        if (datasetFrame != null) {
            //MAPPING
            //
            //SCENE_ORIENTATION         → ?
            //<Vertex>                  Occurs : 3 to 8
            //    <FRAME_LON/>          → used to build : MetaData.identificationInfo > DataIdentification.extents > Extents.geographicElements > BoundingPolygon
            //    <FRAME_LAT/>          → used to build : MetaData.identificationInfo > DataIdentification.extents > Extents.geographicElements > BoundingPolygon
            //    <FRAME_ROW/>          → ?
            //    <FRAME_COL/>          → ?
            //    <FRAME_X/>            → ?
            //    <FRAME_Y/>            → ?
            //</Vertex>
            //...
            //<Scene_Center>            Occurs : 1 to 1
            //    <FRAME_LON/>          → ?
            //    <FRAME_LAT/>          → ?
            //    <FRAME_ROW/>          → ?
            //    <FRAME_COL/>          → ?
            //    <SCENE_ORIENTATION/>  → ?
            //</Scene_Center>
            
            if (metadata instanceof MI_Metadata) {
                Geometry geometry = null;
                try {
                    geometry = readDatasetVertex(doc);
                } catch (NoSuchAuthorityCodeException ex) {
                    throw new IOException("Exception when creating the bounding geometry : ", ex);
                } catch (FactoryException ex) {
                    throw new IOException("Exception when creating the bounding geometry : ", ex);
                }

                /**
                 * Fills IdentificationInfo
                 */
                
                //MetaData > DataIdentification > Extent > BoundingPolygon
                if (geometry != null) {
                    final DefaultBoundingPolygon boundingPolygon = new DefaultBoundingPolygon();
                    boundingPolygon.setPolygons(Collections.singleton(geometry));

                    final DefaultGeographicDescription geographicDesc = new DefaultGeographicDescription();
                    //not safe
                    final Element gridReference = firstElement(doc, TAG_SCENE_GRID_REFERENCE);
                    final String geographicId;
                    if (gridReference != null) {
                        final String rawGeoId = gridReference.getTextContent();
                        geographicId = rawGeoId.substring(0, 3) + "-" + rawGeoId.substring(3);
                    } else {
                        final String[] fileIdSplited = metadata.getFileIdentifier().split("-");
                        geographicId = fileIdSplited[0].substring(fileIdSplited[0].length() - 3)
                                + "-" + fileIdSplited[1].substring(0, 3);
                    }
                    geographicDesc.setGeographicIdentifier(new DefaultIdentifier(geographicId));

                    final DefaultExtent extent = (DefaultExtent) getExtent(metadata);
                    extent.getGeographicElements().add(boundingPolygon);
                    extent.getGeographicElements().add(geographicDesc);
                }
            }
        }

        //<xsd:element minOccurs="1" maxOccurs="1"  ref="Production"/> ---------
        //Can be changed in a Responsible party information
        final Element production = firstElement(doc, TAG_PRODUCTION);
        if (production != null) {
            //MAPPING
            //
            //<DATASET_PRODUCER_NAME/>      → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep.processors > ResponsibleParty.organisationName
            //<DATASET_PRODUCER_URL/>       → ?
            //<DATASET_PRODUCTION_DATE/>    → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep.date
            //<PRODUCT_TYPE/>               → ?
            //<PRODUCT_INFO/>               → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep.description
            //<JOB_ID/>                     → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep > Processing.identifier > Identifier.code
            //<Production_Facility>         Occurs : 1 to 1
            //    <SOFTWARE_NAME/>          → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep.processingInfo > Processing.softwareReference > Citation.title
            //    <SOFTWARE_VERSION/>       → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep.processingInfo > Processing.softwareReference > Citation.edition 
            //    <PROCESSING_CENTER/>      → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep.processingInfo > Processing.softwareReference > Citation.citedResponsibleParties > ResponsibleParty.organisationName
            //</Production_Facility>
            
            final String jobId = textValueSafe(production, TAG_JOB_ID, String.class);
            final String productType = textValueSafe(production, TAG_PRODUCT_TYPE, String.class);
            final String productInfo = textValueSafe(production, TAG_PRODUCT_INFO, String.class);
            final String producerName = textValueSafe(production, TAG_DATASET_PRODUCER_NAME, String.class);
            final Date productionDate = textValueSafe(production, TAG_DATASET_PRODUCTION_DATE, Date.class);
            final Element producerEle = firstElement(production, TAG_DATASET_PRODUCER_URL);
            URI producerURL = null;
            try {
                producerURL = new URI(producerEle.getAttribute(ATT_HREF));
            } catch (URISyntaxException ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
            final Element facility = firstElement(production, TAG_PRODUCTION_FACILITY);

            /**
             * Fills DataQualityInfo
             */
            
            //MetaData > DataQuality > Lineage > ProcessStep
            final DefaultResponsibleParty responsibleParty = new DefaultResponsibleParty(Role.ORIGINATOR);
            responsibleParty.setOrganisationName(new SimpleInternationalString(producerName));
            
            final DefaultProcessStep processStep = getProcessStep(metadata);
            processStep.setDescription(new SimpleInternationalString(productInfo));
            processStep.setDate(productionDate);
            processStep.getProcessors().add(responsibleParty);

            //MetaData > DataQuality > Lineage > ProcessStep > Processing > Identifier
            if (jobId != null) {
                final DefaultProcessing processing = getProcessingInfo(metadata);
                processing.setIdentifier(new DefaultIdentifier(jobId));
            }
            
            //MetaData > DataQuality > Lineage > ProcessStep > Processing > SoftwareReferences
            if (facility != null) {
                final String softwareName = textValueSafe(facility, TAG_PRODUCTION_FACILITY_SOFTWARE_NAME, String.class);
                final String softwareVersion = textValueSafe(facility, TAG_PRODUCTION_FACILITY_SOFTWARE_VERSION, String.class);
                final String productionCenter = textValueSafe(facility, TAG_PRODUCTION_FACILITY_PROCESSING_CENTER, String.class);

                final DefaultCitation softCitation = new DefaultCitation();
                softCitation.setTitle(new SimpleInternationalString(softwareName));
                softCitation.setEdition(new SimpleInternationalString(softwareVersion));
                if (productionCenter != null) {
                    final DefaultResponsibleParty softResponsibleParty = new DefaultResponsibleParty();
                    softResponsibleParty.setOrganisationName(new SimpleInternationalString(productionCenter));
                    softCitation.getCitedResponsibleParties().add(softResponsibleParty);
                }

                final DefaultProcessing processing = getProcessingInfo(metadata);
                processing.getSoftwareReferences().add(softCitation);
            }
        }

        //<xsd:element minOccurs="1" maxOccurs="1"  ref="Raster_Dimensions"/> --
        //Has been set from the geotiff informations
        final Element rasterDim = firstElement(doc, TAG_RASTER_DIMENSIONS);
        if (rasterDim != null) {
            //MAPPING
            //
            //<NCOLS/>  → MetaData.spatialRepresentationInfo > GridSpatialRepresentation.axisDimensionProperties > Dimension.dimensionSize
            //<NROWS/>  → MetaData.spatialRepresentationInfo > GridSpatialRepresentation.axisDimensionProperties > Dimension.dimensionSize
            //<NBANDS/> → ?
            final Integer ncols = textValueSafe(rasterDim, TAG_NCOLS, Integer.class);
            final Integer nrows = textValueSafe(rasterDim, TAG_NROWS, Integer.class);

            /**
             * Fills SpatialRepresentationInfo
             */
            
            //MetaData > GridSpatialRepresentation > Dimension
            final DefaultDimension rowDim = new DefaultDimension();
            rowDim.setDimensionSize(nrows);
            rowDim.setDimensionName(DimensionNameType.ROW);

            final DefaultDimension columnDim = new DefaultDimension();
            columnDim.setDimensionSize(ncols);
            columnDim.setDimensionName(DimensionNameType.COLUMN);

            final DefaultGridSpatialRepresentation gridSpacialRepr = (DefaultGridSpatialRepresentation) getSpatialRepresentationInfo(metadata);

            final List<Dimension> axisDimensions = gridSpacialRepr.getAxisDimensionProperties();
            axisDimensions.add(rowDim);
            axisDimensions.add(columnDim);
        }

        //<xsd:element minOccurs="1" maxOccurs="1"  ref="Data_Processing"/> ----
        final Element dataProcessing = firstElement(doc, TAG_DATA_PROCESSING);
        if (dataProcessing != null) {
            //MAPPING
            //
            //<PROCESSING_LEVEL/>                   → ?
            //<GEOMETRIC_PROCESSING/>               → ?
            //<RADIOMETRIC_PROCESSING/>             → ?
            //<SPECTRAL_PROCESSING/>                → ?
            //<Processing_Options>                  Occurs : 1 to 1
            //    <MEAN_RECTIFICATION_ELEVATION/>   → ?
            //    <LINE_SHIFT/>                     → ?
            //    <DECOMPRESSION_TYPE/>             → ?
            //    <SWIR_BAND_REGISTRATION_FLAG/>    → ?
            //    <X_BANDS_REGISTRATION_FLAG/>      → ?
            //    <RESAMPLING_METHOD/>              → ?
            //    <Dynamic_Stretch>                 Occurs : 0 to 1 
            //        <Thresholds>                  Occurs : 1 to n
            //            <BAND_INDEX/>             → MetaData.contentInfo > ImageDescription.dimensions > Band.descriptor
            //            <LOW_THRESHOLD/>          → MetaData.contentInfo > ImageDescription.dimensions > Band.minValue
            //            <HIGH_THRESHOLD/>         → MetaData.contentInfo > ImageDescription.dimensions > Band.maxValue
            //        </Thresholds>
            //    <Dynamic_Stretch>
            //    <Deconvolution>                   Occurs : 0 to 1
            //        <LINE_SHIFT/>                 → ?
            //        <DECOMPRESSION_TYPE/>         → ?   
            //    <Deconvolution>
            //    <Sampling_Step>                   Occurs : 0 to 1
            //        <SAMPLING_STEP_X/>            → ?
            //        <SAMPLING_STEP_Y/>            → ?
            //    <Sampling_Step>
            //    <SuperMode_Processing>            Occurs : 0 to 1
            //        <SM_CORRELATION_NEEDED/>      → ? 
            //        <SM_RAW_GRID_FILTERING/>      → ?
            //        <SM_PROCESSING_TYPE/>         → ?
            //    <SuperMode_Processing>
            //    <Correction_Algorithm>            Occurs : 0 to n
            //        <ALGORITHM_TYPE/>             → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep.processingInfo > Processing.algorithms > Algorithm.description
            //        <ALGORITHM_NAME/>             → MetaData.dataQualityInfo > DataQuality.lineage > Lineage.processSteps > ProcessStep.processingInfo > Processing.algorithms > Algorithm.citation > Citation.title
            //        <ALGORITHM_ACTIVATION/>       → ?
            //    <Correction_Algorithm>
            //    ...
            //</Processing_Options>
            //<Regions_Of_Interest>                 Occurs : 0 to 1
            //    <Region_Of_Interest>              Occurs : 1 to n
            //        <COL_MIN/>                    → ?
            //        <ROW_MIN/>                    → ?
            //        <COL_MAX/>                    → ?
            //        <ROW_MAX/>                    → ?
            //    </Region_Of_Interest> 
            //    ...
            //</Regions_Of_Interest>
            
            final String algoType = textValueSafe(dataProcessing, TAG_DATA_PROCESSING_ALGORITHM_TYPE, String.class);
            final String algoName = textValueSafe(dataProcessing, TAG_DATA_PROCESSING_ALGORITHM_NAME, String.class);

            /**
             * Fills DataQualityInfo
             */
            
            //MetaData > DataQuality > Lineage > ProcessStep > Processing > Algorithm
            if (algoName != null && algoType != null) {
                final DefaultCitation citation = new DefaultCitation();
                citation.setTitle(new SimpleInternationalString(algoName));

                final DefaultAlgorithm algorithm = new DefaultAlgorithm();
                algorithm.setDescription(new SimpleInternationalString(algoType));
                algorithm.setCitation(citation);

                final DefaultProcessing processing = getProcessingInfo(metadata);
                processing.getAlgorithms().add(algorithm);
            }
            
            /**
             * Fills ContentInfo
             */

            //MetaData > ImageDescription > Dimension
            final Element processingOpts = firstElement(dataProcessing, TAG_PROCESSING_OPTIONS);
            if (processingOpts != null) {
                final Element dynamicStretch = firstElement(dataProcessing, TAG_DYNAMIC_STRETCH);
                
                if (dynamicStretch != null) {
                    final List<Element> thresholds = getListElements(dynamicStretch, TAG_THRESHOLDS);
                    
                    for (int i = 0, len = thresholds.size(); i < len; i++) {
                        final Element threshold = (Element) thresholds.get(i);
                        
                        final String bandIndex = textValueSafe(threshold, TAG_BAND_INDEX, String.class);
                        final Double lowThreshold = textValueSafe(threshold, TAG_LOW_THRESHOLD, Double.class);
                        final Double highThreshold = textValueSafe(threshold, TAG_HIGH_THRESHOLD, Double.class);

                        final DefaultBand dimension = getBandDimension(metadata, bandIndex);
                        dimension.setMinValue(lowThreshold);
                        dimension.setMaxValue(highThreshold);
                        dimension.setDescriptor(new SimpleInternationalString(bandIndex));
                    }
                }
            }
        }

        //<xsd:element minOccurs="1" maxOccurs="1"  ref="Data_Access"/> --------
        final Element dataAccess = firstElement(doc, TAG_DATA_ACCESS);
        if (dataAccess != null) {
            //MAPPING
            //
            //<DATA_FILE_FORMAT/>       → Metadata.identificationInfo > DataIdentification.resourceFormats > Format.name and Format.version
            //<DATA_FILE_FORMAT_DESC/>  → ?
            //<DATA_FILE_ORGANISATION/> → ?
            //<Data_File>               Occurs : 1 to 1
            //    <DATA_FILE_PATH/>      → ?
            //</Data_File>
            
            final Element formatTag = firstElement(dataAccess, TAG_DATA_FILE_FORMAT);

            /**
             * Fills IdentificationInfo
             */
            
            //MetaData > DataIdentification > Format
            if (formatTag != null) {
                final String version = formatTag.getAttribute(ATT_VERSION);
                final String formatName = formatTag.getTextContent();
                
                final DefaultFormat format = new DefaultFormat();
                format.setName(new SimpleInternationalString(formatName));
                format.setVersion(new SimpleInternationalString(version));

                final AbstractIdentification idf = getIdentificationInfo(metadata);
                idf.getResourceFormats().add(format);
            }
        }

        //<xsd:element minOccurs="1" maxOccurs="1" ref="Image_Interpretation"/>
        final Element imageInter = firstElement(doc, TAG_IMAGE_INTERPRETATION);
        if (imageInter != null) {
            //MAPPING
            //
            //<Spectral_Band_Info>               Occurs : 1 to n
            //    <BAND_INDEX/>                  → MetaData.contentInfo > ImageDescription.dimensions > Band.descriptor
            //    <BAND_DESCRIPTION/>            → MetaData.contentInfo > ImageDescription.dimensions > Band.descriptor
            //    <PHYSICAL_UNIT/>               → MetaData.contentInfo > ImageDescription.dimensions > Band.Units
            //    <PHYSICAL_GAIN/>               → MetaData.contentInfo > ImageDescription.dimensions > Band.scaleFactor
            //    <PHYSICAL_BIAS/>               → MetaData.contentInfo > ImageDescription.dimensions > Band.offset
            //    <PHYSICAL_CALIBRATION_DATE/>   → ?
            //</Spectral_Band_Info>
            //...
            
            /**
             * Fills ContentInfo
             */
            
            final List<Element> spectrals = getListElements(imageInter, TAG_SPECTRAL_BAND_INFO);
            if (spectrals != null) {
                
                final int nbits = readNBits(doc);
            
                //MetaData > ImageDescription > RecordType
                //if (physicalUnit != null) {
                //    // TODO: how to build an attribute description
                //    //final RecordType recordType = new DefaultRecordType();
                //
                //    final DefaultImageDescription contentInfo = (DefaultImageDescription) getContentInfo(metadata);
                //    contentInfo.setAttributeDescription(null);
                //}
                
                //MetaData > ImageDescription > Dimensions
                for (int i = 0, len = spectrals.size(); i < len; i++) {
                    final Element spectre = (Element) spectrals.get(i);
                    
                    final String bandIndex = textValueSafe(spectre, TAG_BAND_INDEX, String.class);
                    final String bandDesc = textValueSafe(spectre, TAG_BAND_DESCRIPTION, String.class);
                    final Double physicalGain = textValueSafe(spectre, TAG_PHYSICAL_GAIN, Double.class);
                    final Double physicalBias = textValueSafe(spectre, TAG_PHYSICAL_BIAS, Double.class);
                    String physicalUnit = textValueSafe(spectre, TAG_PHYSICAL_UNIT, String.class);
                    
                    physicalUnit = physicalUnit.substring(physicalUnit.indexOf("(") + 1, physicalUnit.indexOf(")"));

                    //final DefaultNameFactory factory = new DefaultNameFactory();
                    //final TypeName tname = factory.createTypeName(null, bandIndex);
                    //final MemberName memberName = factory.createMemberName(null, "BAND_INDEX", tname);
                    
                    //final Unit unit = Unit.valueOf(physicalUnit);

                    final DefaultBand dimension = getBandDimension(metadata, bandIndex);
                    dimension.setBitsPerValue(nbits);
                    dimension.setDescriptor(new SimpleInternationalString(bandIndex));
                    dimension.setScaleFactor(1 / physicalGain);
                    dimension.setOffset(physicalBias);
                    //dimension.setSequenceIdentifier(memberName);
                    //dimension.setDescriptor(new SimpleInternationalString(bandDesc));
                    //dimension.setUnits(unit);
                }
            } 
        }
        
        //<xsd:element minOccurs="1" maxOccurs="1" ref="Dataset_Sources"/> -----
        //Could be mapped to Aquisition informations
        final Element datasetSources = firstElement(doc, TAG_DATASET_SOURCES);
        if (datasetSources != null) {
            //MAPPING
            //
            //<Source_Information>                      Occurs : 1 to 3
            //    <SOURCE_ID/>                           → ?
            //    <SOURCE_TYPE/>                         → ?
            //    <SOURCE_DESCRIPTION/>                  → Metadata.identificationInfo > DataIdentification.abstract
            //    <Source_Frame>                         Occurs : 0 to 1
            //        <Vertex>                           Occurs : 4 to 4
            //            <FRAME_LON/>                   → ?
            //            <FRAME_LAT/>                   → ?
            //            <FRAME_ROW/>                   → ?
            //            <FRAME_COL/>                   → ?
            //            <FRAME_X/>                     → ?
            //            <FRAME_Y/>                     → ?
            //        </Vertex>
            //        ...
            //    </Source_Frame>
            //    <Scene_Source>                         Occurs : 0 to 1
            //        <MISSION/>                         → MetaData.acquisitionInformation > AcquisitionInformation.operations > Operations.description 
            //                                           AND MetaData.acquisitionInformation > AcquisitionInformation.plateforms > Platform.identifier > Identifier.code
            //                                           AND MetaData.acquisitionInformation > AcquisitionInformation.plateforms > Platform.Citation > Citation.title
            //                                           AND MetaData.acquisitionInformation > AcquisitionInformation.plateforms > Platform.description
            //        <MISSION_INDEX/>                   → MetaData.acquisitionInformation > AcquisitionInformation.operations > Operations.identifier > Identifier.code
            //                                           AND MetaData.acquisitionInformation > AcquisitionInformation.plateforms > Platform.identifier > Identifier.code
            //                                           AND MetaData.acquisitionInformation > AcquisitionInformation.plateforms > Platform.description
            //        <INSTRUMENT/>                      → MetaData.acquisitionInformation > AcquisitionInformation.instruments > Instrument.description
            //        <INSTRUMENT_INDEX/>                → MetaData.acquisitionInformation > AcquisitionInformation.instruments > Instrument.identifier > Identifier.code
            //        <SENSOR_CODE/>                     → ?
            //        <IMAGING_DATE/>                    → MetaData.acquisitionInformation > AcquisitionInformation.operations > Operations.citation > Citation.dates > CitationDate
            //        <IMAGING_TIME/>                    → ?
            //        <GRID_REFERENCE/>                  → ?
            //        <SHIFT_VALUE/>                     → ?
            //        <INCIDENCE_ANGLE/>                 → ?
            //        <THEORETICAL_RESOLUTION/>          → ?
            //        <SUN_AZIMUTH/>                     → MetaData.contentInfo > ImageDescription.processingLevelCode > Identifier.code
            //        <SUN_ELEVATION/>                   → MetaData.contentInfo > ImageDescription.illuminationAzimuthAngle
            //        <SCENE_PROCESSING_LEVEL/>          → MetaData.contentInfo > ImageDescription.illuminationElevationAngle
            //        <VIEWING_ANGLE/>                   → ?
            //        <Imaging_Parameters>               Occurs : 1 to 1
            //            <REVOLUTION_NUMBER/>           → ?
            //            <COMPRESSION_MODE/>            → ?
            //            <DIRECT_PLAYBACK_INDICATOR/>   → ?
            //            <REFOCUSING_STEP_NUM/>         → ?
            //            <COUPLED_MODE_FLAG/>           → ?
            //            <SWATH_MODE/>                  → ?
            //        </Imaging_Parameters>
            //    </Scene_Source>
            //    <Quality_Assessment>                   Occurs : 0 to 1
            //        <QUALITY_TABLES/>                  → ?
            //        <Quality_Parameter>                Occurs : 1 to n
            //            <QUALITY_PARAMETER_CODE/>      → ?
            //            <QUALITY_PARAMETER_DESC/>      → ?
            //            <QUALITY_PARAMETER_VALUE/>     → ?
            //        </Quality_Parameter>
            //    </Quality_Assessment>
            //</Source_Information>
            //...

            final Element sourceInfo = firstElement(datasetSources, TAG_SOURCE_INFORMATION);
            if (sourceInfo != null) {
                final String sourceDesc = textValueSafe(sourceInfo, TAG_SOURCE_DESCRIPTION, String.class);
                
                /**
                 * Fills IdentificationInfo
                 */
                
                //MetaData > IdentificationInfo (DataIdentification) > GraphicOverviews
                final DefaultDataIdentification dataIdentification = (DefaultDataIdentification) getIdentificationInfo(metadata);
                if (thumbnail != null && thumbnail.contains(".")) {
                    dataIdentification.getGraphicOverviews().add(new DefaultBrowseGraphic(generateFileName(name, thumbnail.substring(thumbnail.lastIndexOf(".")))));
                }
                //MetaData > IdentificationInfo (DataIdentification) > Abstract
                dataIdentification.setAbstract(new SimpleInternationalString(sourceDesc));
                
                /**
                 * Fills AcquisitionInfo and ContentInfo
                 */

                final Element sceneSource = firstElement(sourceInfo, TAG_SCENE_SOURCE);
                if (sceneSource != null) {
                    final Date imagingDate = textValueSafe(sceneSource, TAG_SCENE_IMAGING_DATE, Date.class);
                    final String missionName = textValueSafe(sceneSource, TAG_SCENE_MISSION, String.class);
                    final String missionIndex = textValueSafe(sceneSource, TAG_SCENE_MISSION_INDEX, String.class);
                    final String instrumentName = textValueSafe(sceneSource, TAG_SCENE_INSTRUMENT, String.class);
                    final String instrumentIndex = textValueSafe(sceneSource, TAG_SCENE_INSTRUMENT_INDEX, String.class);
                    final String processingLevel = textValueSafe(sceneSource, TAG_SCENE_PROCESSING_LEVEL, String.class);
                    final Double incidenceAngle = textValueSafe(sceneSource, TAG_SCENE_INCIDENCE_ANGLE, Double.class);
                    final Double theoreticalResolution = textValueSafe(sceneSource, TAG_SCENE_THEORETICAL_RESOLUTION, Double.class);
                    final String viewingAngle = textValueSafe(sceneSource, TAG_SCENE_VIEWING_ANGLE, String.class);
                    final Double sunAzimuth = textValueSafe(sceneSource, TAG_SCENE_SUN_AZIMUTH, Double.class);
                    final Double sunElevation = textValueSafe(sceneSource, TAG_SCENE_SUN_ELEVATION, Double.class);

                    /**
                     * Fills AcquisitionInfo
                     */
                    
                    final DefaultAcquisitionInformation acquisitionInfo = getAcquisitionInfo(metadata);

                    //MetaData > AcquisitionInfor > Operations
                    final DefaultCitation citation = new DefaultCitation();
                    citation.getDates().add(new DefaultCitationDate(imagingDate, DateType.CREATION));

                    final DefaultOperation operation = new DefaultOperation();
                    operation.setIdentifier(new DefaultIdentifier(missionIndex));
                    operation.setCitation(citation);
                    operation.setDescription(new SimpleInternationalString(missionName));
                    
                    acquisitionInfo.getOperations().add(operation);

                    //MetaData > AcquisitionInfo > Instruments
                    final DefaultInstrument instrument = new DefaultInstrument();
                    instrument.setIdentifier(new DefaultIdentifier(instrumentName + instrumentIndex));
                    instrument.setDescription(new SimpleInternationalString(instrumentName));
                    
                    acquisitionInfo.getInstruments().add(instrument);

                    //MetaData > AcquisitionInfo > Platforms
                    final DefaultCitation platformCitation = new DefaultCitation();
                    platformCitation.setTitle(new SimpleInternationalString(missionName));

                    final DefaultPlatform platform = new DefaultPlatform();
                    platform.setIdentifier(new DefaultIdentifier(missionName + missionIndex));
                    platform.setCitation(platformCitation);
                    platform.setDescription(new SimpleInternationalString(missionName + missionIndex));
                    ;
                    acquisitionInfo.getPlatforms().add(platform);

                    /**
                     * Fills ContentInfo
                     */

                    //MetaData > ContentInfo (ImageDescription) > ProcessingLevelCode
                    if (processingLevel != null) {
                        final DefaultImageDescription contentInfo = (DefaultImageDescription) getContentInfo(metadata);
                        contentInfo.setProcessingLevelCode(new DefaultIdentifier(processingLevel));
                    }

                    //MetaData > ContentInfo (ImageDescription) > IlluminationAzimuthAngle AND IlluminationElevationAngle
                    final DefaultImageDescription contentInfo = (DefaultImageDescription) getContentInfo(metadata);
                    contentInfo.setIlluminationAzimuthAngle(sunAzimuth);
                    contentInfo.setIlluminationElevationAngle(sunElevation);
                }
            }
        }

        return metadata;
    }

    private static URI generateFileName(String name, String extention) {
        try {
            return new URI(name.replaceAll(":", "_").replaceAll(" ", "_").replaceAll("/", "_").concat(extention));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static DefaultProcessing getProcessingInfo(final DefaultMetadata metadata) {
        final DefaultProcessStep step = getProcessStep(metadata);

        DefaultProcessing processing = (DefaultProcessing) step.getProcessingInformation();

        if (processing == null) {
            processing = new DefaultProcessing();
            step.setProcessingInformation(processing);
        }

        return processing;
    }

    private static DefaultProcessStep getProcessStep(final DefaultMetadata metadata) {
        final DefaultLineage lineage = getLineage(metadata);

        final Collection<ProcessStep> steps = lineage.getProcessSteps();

        if (steps.isEmpty()) {
            final DefaultProcessStep step = new DefaultProcessStep();
            steps.add(step);
            return step;
        } else {
            final List<ProcessStep> copies = new ArrayList<ProcessStep>(steps);
            final ProcessStep step = copies.get(0);
            if (step instanceof DefaultProcessStep) {
                return (DefaultProcessStep) step;
            } else {
                final DefaultProcessStep copy = DefaultProcessStep.castOrCopy(step);
                copies.set(0, copy);
                //copy and replace collection
                lineage.setProcessSteps(copies);
                return copy;
            }
        }
    }

    private static DefaultLineage getLineage(final DefaultMetadata metadata) {
        final DefaultDataQuality quality = getDataQualityInfo(metadata);

        Lineage lineage = quality.getLineage();

        if (lineage == null) {
            lineage = new DefaultLineage();
            quality.setLineage(lineage);
        }

        if (lineage instanceof DefaultLineage) {
            return (DefaultLineage) lineage;
        } else {
            final DefaultLineage copy = DefaultLineage.castOrCopy(lineage);
            quality.setLineage(lineage);
            return copy;
        }

    }

    private static DefaultDataQuality getDataQualityInfo(final DefaultMetadata metadata) {
        final Collection<DataQuality> qualities = metadata.getDataQualityInfo();

        if (qualities.isEmpty()) {
            final DefaultDataQuality quality = new DefaultDataQuality();
            metadata.getDataQualityInfo().add(quality);
            return quality;
        } else {
            final List<DataQuality> copies = new ArrayList<DataQuality>(qualities);
            final DataQuality quality = copies.get(0);
            if (quality instanceof DefaultDataQuality) {
                return (DefaultDataQuality) quality;
            } else {
                final DefaultDataQuality copy = DefaultDataQuality.castOrCopy(quality);
                copies.set(0, copy);
                //copy and replace collection
                metadata.setDataQualityInfo(copies);
                return copy;
            }
        }

    }

    private static DefaultExtent getExtent(final DefaultMetadata metadata) {
        final DefaultDataIdentification identification = (DefaultDataIdentification) getIdentificationInfo(metadata);

        final Collection<Extent> extents = identification.getExtents();

        if (extents.isEmpty()) {
            final DefaultExtent extent = new DefaultExtent();
            extents.add(extent);
            return extent;
        } else {
            final List<Extent> copies = new ArrayList<Extent>(extents);
            final Extent extent = copies.get(0);
            if (extent instanceof DefaultExtent) {
                return (DefaultExtent) extent;
            } else {
                final DefaultExtent copy = DefaultExtent.castOrCopy(extent);
                copies.set(0, copy);
                //copy and replace collection
                identification.setExtents(copies);
                return copy;
            }
        }

    }

    private static AbstractIdentification getIdentificationInfo(final DefaultMetadata metadata) {
        final Collection<Identification> ids = metadata.getIdentificationInfo();

        if (ids.isEmpty()) {
            final DefaultDataIdentification id = new DefaultDataIdentification();
            ids.add(id);
            return id;
        } else {
            final List<Identification> copies = new ArrayList<Identification>(ids);
            final Identification id = copies.get(0);
            if (id instanceof DefaultDataIdentification) {
                return (DefaultDataIdentification) id;
            } else {
                final AbstractIdentification copy = DefaultDataIdentification.castOrCopy(id);
                copies.set(0, copy);
                //copy and replace collection
                metadata.setIdentificationInfo(copies);
                return copy;
            }
        }

    }

    private static DefaultBand getBandDimension(final DefaultMetadata metadata, final String bandId) {
        final DefaultImageDescription imageDescr = (DefaultImageDescription) getContentInfo(metadata);

        final Collection<RangeDimension> dimensions = imageDescr.getDimensions();

        //Search the dimension with band identifier equals to the given identifier
        for (final RangeDimension dimension : dimensions) {

            if (dimension instanceof DefaultBand) {
                final String bandDescr = dimension.getDescriptor().toString();

                if (bandId.equals(bandDescr)) {
                    return (DefaultBand) dimension;
                }
            }
        }

        //If the dimension doesn't exists, creates and returns a new dimension
        final DefaultBand dimension = new DefaultBand();
        dimensions.add(dimension);
        
        return dimension;
    }

    private static AbstractContentInformation getContentInfo(final DefaultMetadata metadata) {
        final Collection<ContentInformation> ids = metadata.getContentInfo();

        if (ids.isEmpty()) {
            final DefaultImageDescription id = new DefaultImageDescription();
            ids.add(id);
            return id;
        } else {
            final List<ContentInformation> copies = new ArrayList<ContentInformation>(ids);
            final ContentInformation id = copies.get(0);
            if (id instanceof DefaultImageDescription) {
                return (DefaultImageDescription) id;
            } else {
                final AbstractContentInformation copy = DefaultImageDescription.castOrCopy(id);
                copies.set(0, copy);
                //copy and replace collection
                metadata.setContentInfo(copies);
                return copy;
            }
        }

    }

    private static DefaultAcquisitionInformation getAcquisitionInfo(final DefaultMetadata metadata) {
        final Collection<AcquisitionInformation> acq = metadata.getAcquisitionInformation();

        if (acq.isEmpty()) {
            final DefaultAcquisitionInformation id = new DefaultAcquisitionInformation();
            acq.add(id);
            return id;
        } else {
            final List<AcquisitionInformation> copies = new ArrayList<AcquisitionInformation>(acq);
            final AcquisitionInformation id = copies.get(0);
            if (id instanceof DefaultAcquisitionInformation) {
                return (DefaultAcquisitionInformation) id;
            } else {
                final DefaultAcquisitionInformation copy = DefaultAcquisitionInformation.castOrCopy(id);
                copies.set(0, copy);
                //copy and replace collection
                metadata.setAcquisitionInformation(copies);
                return copy;
            }
        }

    }

    private static AbstractSpatialRepresentation getSpatialRepresentationInfo(final DefaultMetadata metadata) {
        final Collection<SpatialRepresentation> spa = metadata.getSpatialRepresentationInfo();

        if (spa.isEmpty()) {
            final DefaultGridSpatialRepresentation sp = new DefaultGridSpatialRepresentation();
            spa.add(sp);
            return sp;
        } else {
            final List<SpatialRepresentation> copies = new ArrayList<SpatialRepresentation>(spa);
            final SpatialRepresentation id = copies.get(0);
            if (id instanceof DefaultGridSpatialRepresentation) {
                return (DefaultGridSpatialRepresentation) id;
            } else {
                final AbstractSpatialRepresentation copy = DefaultGridSpatialRepresentation.castOrCopy(id);
                copies.set(0, copy);
                //copy and replace collection
                metadata.setSpatialRepresentationInfo(copies);
                return copy;
            }
        }

    }
}
