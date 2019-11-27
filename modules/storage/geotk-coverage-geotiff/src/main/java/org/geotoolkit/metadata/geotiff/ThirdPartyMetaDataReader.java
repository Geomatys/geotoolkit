/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2016, Geomatys
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.FactoryException;

import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.iso.Names;
import org.apache.sis.util.logging.Logging;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.temporal.object.TemporalUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.geotoolkit.coverage.SampleDimensionUtils.NODATA_CATEGORY_NAME;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.GDAL_METADATA_KEY;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.GDAL_NODATA_KEY;

/**
 * Tiff format may contain multiple additional tags.
 * Some of them can have valuable metadata or style informations.
 *
 * Various lists of possible tags :
 * http://search.cpan.org/dist/Image-MetaData-JPEG/lib/Image/MetaData/JPEG/TagLists.pod
 * http://www.awaresystems.be/imaging/tiff/tifftags/private.html
 * http://www.awaresystems.be/imaging/tiff/tifftags/privateifd.html
 *
 * @author Johann Sorel  (Geomatys)
 * @author Marechal Remi (Geomatys).
 */
public strictfp class ThirdPartyMetaDataReader {

    /**
     * Logger to diffuse no blocking error message.
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.metadata.geotiff");

    /**
     * Root {@link Node} from analysed {@link SpatialMetadata}.
     *
     * @see ThirdPartyMetaDataReader#ThirdPartyMetaDataReader(javax.imageio.metadata.IIOMetadata)
     */
    private final Node root;

    /**
     *
     * @param imageMetadata
     * @throws IOException
     */
    public ThirdPartyMetaDataReader(final IIOMetadata imageMetadata) throws IOException {
        root = imageMetadata.getAsTree(imageMetadata.getNativeMetadataFormatName());
        if (root == null) throw new IOException("No image metadatas");
    }

    /**
     * Fill the given Spatial Metadatas with additional informations.
     */
    public void fillSpatialMetaData(SpatialMetadata metadata) throws IOException {
        final TreeSet<Double> noDatas = new TreeSet<Double>();

        int samplePerPixels          = -1;
        int bitsPerSamples           = -1;
        int sampleFormat             = -1;

        long[] minSampleValues       = null;
        long[] maxSampleValues       = null;
        double[] gDalMinSampleValue  = null;
        double[] gDalMaxSampleValue  = null;

        double[] modelTransformation = null;
        double[] modelPixelScale     = null;
        Double sampleToGeoScale      = null;
        Double sampleToGeoOffset     = null;
        boolean scaleFound           = false;

        //-- get sample per pixel and scale / offset
        final NodeList children = root.getChildNodes();
        for(int i = 0, n = children.getLength(); i < n; i++) {
            final IIOMetadataNode child = (IIOMetadataNode) children.item(i);
            final int number = Integer.valueOf(child.getAttribute("number"));
            switch (number) {
                case 277 : { //-- samples per pixel
                    final Node sppNode = child.getChildNodes().item(0);
                    samplePerPixels = (int) GeoTiffMetaDataUtils.readTiffLongs(sppNode)[0];
                    break;
                }
                case 34264 : { //-- ModelTransformationTag
                    scaleFound = true;
                    final Node mdTransNode = child.getChildNodes().item(0);
                    modelTransformation = GeoTiffMetaDataUtils.readTiffDoubles(mdTransNode);

                   /*
                    * get pixelScaleZ at coordinate 10 and pixelOffsetZ at coordinate 11.
                    * always array of length 16 like follow.
                    * |Sx, 0, 0, Tx|
                    * |0, Sy, 0, Ty|
                    * |0, 0, Sz, Tz|
                    * |0, 0,  0, 1 |
                    */
                    //-- scaleZ
                    /* HACK + WARNING : As of now (2019-11-26), the reason why sample transfer function is deduced from
                     * projection matrix is unknown. One hypothesis is that for DEMs, whose sample values could be
                     * considered as position altitude, the Z scale and offset relates to it. Well, be very careful with
                     * that code.
                     */
                    if (StrictMath.abs(modelTransformation[10]) > 1E-9 || StrictMath.abs(modelTransformation[11]) > 1E-9) {
                        if (modelPixelScale != null) assert StrictMath.abs(StrictMath.abs(modelTransformation[10]) - StrictMath.abs(modelPixelScale[2])) < 1E-9;
                        sampleToGeoScale = modelTransformation[10];
                        sampleToGeoOffset = modelTransformation[11];
                    }
                    break;
                }
            }
            if (scaleFound && samplePerPixels != -1) break;
        }

        assert samplePerPixels != -1 : "SamplePerPixels is not define.";

        String datetime = null;
        String datetimeDigitized = null;
        String datetimeOriginal = null;

        for(int i = 0, n = children.getLength(); i < n; i++){
            final IIOMetadataNode child = (IIOMetadataNode) children.item(i);
            final int number = Integer.valueOf(child.getAttribute("number"));

            switch (number) {
                //-- apparemment meme categories pour tout les gridSampleDimensions ...??? (à definir pas rencontrer pour le moment).
                //GDAL tags
                case GDAL_METADATA_KEY :  {//metadatas as xml
                    final Node valueNode = child.getChildNodes().item(0);
                    String stats = GeoTiffMetaDataUtils.readTiffAsciis(valueNode);
                    stats = stats.replaceAll(">( |\n|\t)*<", "><").trim();
                    Double min = null;
                    Double max = null;
                    try {
                        final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        final Document doc = db.parse(new ByteArrayInputStream(stats.getBytes()));
                        final NodeList lst = doc.getElementsByTagName("Item");
                        for (int k = 0, kn = lst.getLength(); k < kn; k++) {
                            final Element node = (Element) lst.item(k);
                            final String name  = node.getAttribute("name");
                            if ("STATISTICS_MINIMUM".equals(name)) {
                                min = Double.valueOf(node.getTextContent());
                            } else if ("STATISTICS_MAXIMUM".equals(name)) {
                                max = Double.valueOf(node.getTextContent());
                            }
                        }

                       /*
                        * Pour le moment fill les valeurs. Peut etre GDAL defini un min et max par bands.
                        * Pas encore rencontré.
                        * Tableau en prevision.
                        */
                        if (min != null && max != null) {
                            gDalMinSampleValue = new double[samplePerPixels];
                            Arrays.fill(gDalMinSampleValue, min);
                            gDalMaxSampleValue = new double[samplePerPixels];
                            Arrays.fill(gDalMaxSampleValue, max);
                        }
                    } catch (ParserConfigurationException | SAXException | NumberFormatException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                    break;
                }
                case GDAL_NODATA_KEY: {// no data value as ascii text
                   /*
                    * GDal documentation assume only one value for All image bands.
                    * http://www.gdal.org/frmt_gtiff.html
                    */
                    final Node valueNode = child.getChildNodes().item(0);
                    String str = GeoTiffMetaDataUtils.readTiffAsciis(valueNode);
                    if (str.contains(",")) {
                        final String[] strs = str.split(",");
                        assert strs.length == 2 : "Unformat nodata string value. Found : ("+str+")";
                        str = strs[0] +"."+ strs[1];
                    }
                    try {
                        noDatas.add((str.trim().equalsIgnoreCase("nan")) ? Double.NaN : Double.valueOf(str).doubleValue());
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.INFO, "No data value cannot be read.", e);
                    }
                    break;
                }
                case 33550 : { //-- ModelPixelScaleTag
                    final Node mdptNode = child.getChildNodes().item(0);
                    modelPixelScale = GeoTiffMetaDataUtils.readTiffDoubles(mdptNode);
                    //-- get pixel scale Z. Always array of length 3 (ScaleX, ScaleY, ScaleZ).
                    if (StrictMath.abs(modelPixelScale[2]) > 1E-9) {
                        if (modelTransformation != null) assert StrictMath.abs(StrictMath.abs(modelTransformation[10]) - StrictMath.abs(modelPixelScale[2])) < 1E-9;
                        sampleToGeoScale = modelPixelScale[2];
                    }
                    break;
                }
                case GeoTiffConstants.BitsPerSample : { //-- bits per sample
                    final Node bpsNode = child.getChildNodes().item(0);
                    bitsPerSamples = (int) GeoTiffMetaDataUtils.readTiffLongs(bpsNode)[0];
                    break;
                }
                case GeoTiffConstants.SampleFormat : { //-- sample format
                    final Node sfNode = child.getChildNodes().item(0);
                    sampleFormat = (int) GeoTiffMetaDataUtils.readTiffLongs(sfNode)[0];
                    break;
                }
                case GeoTiffConstants.MinSampleValue : { //-- min sample Value
                    final Node sppNode = child.getChildNodes().item(0);
                    minSampleValues =  GeoTiffMetaDataUtils.readTiffLongs(sppNode);
                    break;
                }
                case GeoTiffConstants.MaxSampleValue : { //-- max sample value
                    final Node sfNode = child.getChildNodes().item(0);
                    maxSampleValues = GeoTiffMetaDataUtils.readTiffLongs(sfNode);
                    break;
                }
                case GeoTiffConstants.DateTime : { //-- image date time
                    final Node sfNode = child.getChildNodes().item(0);
                    datetime = GeoTiffMetaDataUtils.readTiffAsciis(sfNode);
                    break;
                }
                case GeoTiffConstants.DateTimeDigitized : { //-- image date time digitized
                    final Node sfNode = child.getChildNodes().item(0);
                    datetimeDigitized  = GeoTiffMetaDataUtils.readTiffAsciis(sfNode);
                    break;
                }
                case GeoTiffConstants.DateTimeOriginal : { //-- image date time original
                    final Node sfNode = child.getChildNodes().item(0);
                    datetimeDigitized  = GeoTiffMetaDataUtils.readTiffAsciis(sfNode);
                    break;
                }
            }
        }


        //add another dimension when a date information is available.
        String date = null;
        if (datetime != null) date = datetime;
        else if (datetimeOriginal  != null) date = datetimeOriginal;
        else if (datetimeDigitized != null) date = datetimeDigitized;
        if (date != null) {
            try {
                final Calendar dd = TemporalUtilities.parseDateCal(date.trim());
                dd.setTimeZone(TimeZone.getTimeZone("UTC"));
                GeoTiffExtension.setOrCreateSliceDimension(metadata, CommonCRS.Temporal.JAVA.crs(), dd.getTimeInMillis());
            } catch (FactoryException | ParseException ex) {
                // dates are often badly formatted, this is an extra crs information
                // in the worse case the value will be in the metadatas.
                // NOTE : should we raise an IOException or just log ?
                // JSorel : since it doesn't compromise reading or georeferencing
                //          I don't think it should block the reading
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        final DimensionAccessor accessor = new DimensionAccessor(metadata);

        assert bitsPerSamples  != -1;
        assert samplePerPixels != -1;

        double[] minSV = new double[samplePerPixels];
        double[] maxSV = new double[samplePerPixels];

        Class typeClass = null;

        if (minSampleValues != null && maxSampleValues != null) {
            assert minSampleValues.length == samplePerPixels;
            assert maxSampleValues.length == samplePerPixels;
            for (int i = 0; i < samplePerPixels; i++) {
                minSV[i] = minSampleValues[i];
                maxSV[i] = maxSampleValues[i];
            }
        } else if (gDalMinSampleValue != null) {
            minSV = gDalMinSampleValue;
            maxSV = gDalMaxSampleValue;
        } else {
           /*
            * If min and max sample values are not stipulate in metadata, we assume
            * that min and max interval is with exclusives terminal because in lot of case
            * the "No category Data" has often a value at interval terminals.
            */
            double min;
            double max;
            switch (bitsPerSamples) {
                case Byte.SIZE : {
                    min = 0;
                    max = 255;
                    typeClass = Integer.class;
                    break;
                }
                case Short.SIZE : {
                    if (sampleFormat == 2) {
                        min = -32768;
                        max = 32767;
                    } else {
                        min = 0;
                        max = 0xFFFF;
                    }
                    typeClass = Integer.class;
                    break;
                }
                case Integer.SIZE : {
                    if (sampleFormat == 3) {
                        //-- Float
                        min = -Float.MAX_VALUE;
                        max = Float.MAX_VALUE;
                        typeClass = Float.class;
                    } else {
                        //-- integer
                        min = Integer.MIN_VALUE;
                        max = Integer.MAX_VALUE;
                        typeClass = Integer.class;
                    }
                    break;
                }
                case Double.SIZE : {
                    min = -Double.MAX_VALUE;
                    max = Double.MAX_VALUE;
                    typeClass = Double.class;
                    break;
                }
                default : throw new IllegalStateException("Unknow sample type");
            }
            Arrays.fill(minSV, min);
            Arrays.fill(maxSV, max);
        }

        //-- in case where min and max sample values are define in tiff metadata.
        if (typeClass == null) {
            switch (bitsPerSamples) {
                case Double.SIZE : {
                    typeClass = Double.class;
                    break;
                }
                default : {
                    if (sampleFormat == 3) {
                        //-- Float
                        assert bitsPerSamples == Float.SIZE : "ThirdPartyMetadataReader : bitsPerSample == Integer.size expected, found bitsPerSample = "+bitsPerSamples;
                        typeClass = Float.class;
                    } else {
                        //-- integer
                        typeClass = Integer.class;
                    }
                    break;
                }
            }
        }

        for (int b = 0; b < samplePerPixels; b++) {
            if (accessor.childCount() >= samplePerPixels) {
                accessor.selectChild(b);
            } else {
                accessor.selectChild(accessor.appendChild());
            }

            final List<Category> categories = buildCategories(minSV[b], maxSV[b], sampleToGeoScale, sampleToGeoOffset, typeClass, noDatas);

            /*
             * If categories list is empty means image is only photographic and doesn't
             * represent any GEOPHISIC context.
             *
             * We create gridsampleDimension only if image as a Coverage.GEOPHISIC view.
             * GEOPHISIC means exist at least, one of, scale, offset or noData values.
             */
            if (!categories.isEmpty()) {
                final SampleDimension dim = new SampleDimension(Names.createLocalName(null, null, String.valueOf(b)), null, categories);
                accessor.setDimension(dim, Locale.ENGLISH);
            }
        }
    }

    /**
     * Build {@linkplain Category categories} {@link List} from sample and noData values from band.<br><br>
     *
     * Cases : <br>
     * - if one of scale or offset is null then their default value will be used (scale=1, offset=0)
     * - if no data values are given and scale/offset are missing then their default value will be used (scale=1, offset=0)
     *
     * @param minSampleValue minimum sample value for current {@link SampleDimension} (band).
     * @param maxSampleValue maximum sample value for current {@link SampleDimension} (band).
     * @param typeClass data type of sample values
     * @param scale scale use to convert sample values into geophysic values, or {@code null} if none.
     * @param offset offset use to convert sample values into geophysic values, or {@code null} if none.
     * @param nodataValues {@link Set} which contain all nodata, organize in ascending order, for current band.
     * @return {@link Category} list for current band.
     *
     * @throws NullArgumentException if nodataValues {@link Set} or typeClass is {@code null}.
     */
    public static List<Category> buildCategories(final double minSampleValue, final double maxSampleValue,
                                                 Double scale, Double offset, final Class typeClass,
                                                 final TreeSet<Double> nodataValues) {
        ArgumentChecks.ensureNonNull("noDataValues", nodataValues);
        ArgumentChecks.ensureNonNull("typeClass",    typeClass);

        if (scale == null && offset != null) {
            //we use the default scale value
            scale = 1.0;
        }
        if (scale != null && offset == null) {
            //we use the default offset value
            offset = 0.0;
        }

        final List<Category> categories = new ArrayList<>();
        if (nodataValues.isEmpty()) {
            if (scale != null) {
                //main datas category
                final SampleDimension.Builder builder = new SampleDimension.Builder();
                builder.addQuantitative("data", getTypedRangeNumber(typeClass,minSampleValue, true, maxSampleValue, true),
                        (MathTransform1D) MathTransforms.linear(scale, offset), null);
                categories.addAll(builder.categories());
            }
            return categories;
        }

        /*
        If we have no data values but no scale or offset we set them to the default values.
        TODO : Normaly we should not create a category because the image is obviously just photographic,
        but we should define a 'mask' somehow to indicate where the image datas are.
        Since geotk/sis do not have yet this approach we cheat by interpreting the data
        as geophysic with a nodata category
        */
        if(scale==null) scale = 1.0;
        if(offset==null) offset = 0.0;

        double currentMinSV  = minSampleValue;
        final double currentMaxSV  = maxSampleValue;
        boolean isMinInclude = true;
        boolean isMaxInclude = true;

        final Iterator<Double> itNoData = nodataValues.iterator();
        while (itNoData.hasNext()) {
            double currentNoData = itNoData.next();

            final SampleDimension.Builder ndbuilder = new SampleDimension.Builder();
            ndbuilder.addQualitative(NODATA_CATEGORY_NAME, getTypedRangeNumber(typeClass, currentNoData, true, currentNoData, true));
            final Category noDataCat = ndbuilder.categories().get(0);
            categories.add(noDataCat);
            currentNoData = noDataCat.getSampleRange().getMinDouble();

            if (currentNoData == currentMinSV) {
                isMinInclude = false;
            } else if (currentNoData == currentMaxSV) {
                isMaxInclude = false;
            } else if (currentMinSV < currentNoData && currentNoData < currentMaxSV) {//-- intersection
                final SampleDimension.Builder builder = new SampleDimension.Builder();
                builder.addQuantitative("data", getTypedRangeNumber(typeClass, currentMinSV, isMinInclude, currentNoData, false),
                        (MathTransform1D) MathTransforms.linear(scale, offset), null);
                categories.addAll(builder.categories());
                isMinInclude = false;
                currentMinSV = currentNoData;
            }
        }

        assert currentMaxSV == maxSampleValue : "buildCategories : last category : currentMaxSample "
                + "value should be equals to maxSampleValues. Expected: "+maxSampleValue+". Found: "+currentMaxSV;

        if (currentMinSV < currentMaxSV) {
            final SampleDimension.Builder builder = new SampleDimension.Builder();
            builder.addQuantitative("data", getTypedRangeNumber(typeClass, currentMinSV, isMinInclude, currentMaxSV, isMaxInclude),
                    (MathTransform1D) MathTransforms.linear(scale, offset), null);
            categories.addAll(builder.categories());
        }

        return categories;
    }

    /**
     * Returns an appropriate {@link NumberRange} from given parameters.
     *
     * @param <T> type of internal data.
     * @param type type of internal data.
     * @param min minimum range value.
     * @param isMinIncluded {@code true} if minimum value is considered as include into range interval else false (exclusive).
     * @param max maximum range value.
     * @param isMaxIncluded {@code true} if maximum value is considered as include into range interval else false (exclusive).
     * @return appropriate range value casted in expected type.
     */
    private static <T extends Number & Comparable<T>> NumberRange<T> getTypedRangeNumber(final Class<T> type,
            final double min, final boolean isMinIncluded,
            final double max, final boolean isMaxIncluded)
    {
        return new NumberRange(type, Numbers.cast(min, type), isMinIncluded,
                                     Numbers.cast(max, type), isMaxIncluded);
    }

}
