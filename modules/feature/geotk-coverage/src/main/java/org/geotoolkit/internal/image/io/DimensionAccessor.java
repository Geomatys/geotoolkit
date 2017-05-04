/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.util.Locale;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;
import javax.measure.Unit;

import org.opengis.util.InternationalString;
import org.opengis.coverage.SampleDimension;
import org.opengis.metadata.content.TransferFunctionType;

import org.apache.sis.util.ArraysExt;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.image.io.metadata.MetadataNodeAccessor;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * A convenience specialization of {@link MetadataNodeAccessor} for the
 * {@code "ImageDescription/Dimensions"} node. Example:
 *
 * {@preformat java
 *     SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(null));
 *     DimensionAccessor accessor = new DimensionAccessor(metadata);
 *     accessor.selectChild(accessor.appendChild());
 *     accessor.setValueRange(-100, 2000);
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.21
 *
 * @since 3.06
 * @module
 */
public final class DimensionAccessor extends MetadataNodeAccessor {
    /**
     * Small tolerance threshold for rounding errors.
     */
    private static final double EPS = 1E-10;

    /**
     * Creates a new accessor for the given metadata.
     *
     * @param metadata The Image I/O metadata. An instance of the
     *        {@link org.geotoolkit.image.io.metadata.SpatialMetadata}
     *        sub-class is recommended, but not mandatory.
     */
    public DimensionAccessor(final IIOMetadata metadata) {
        super(metadata, GEOTK_FORMAT_NAME, "ImageDescription/Dimensions", "Dimension");
    }

    /**
     * Sets the description, transfer function, minimum, maximum and fill values from the
     * given sample dimension. This convenience method fetches the information from the
     * given band and delegates to the other setter methods defined in this class.
     *
     * @param band   The band from which to get the attribute values.
     * @param locale The locale to use for localizing the description.
     *
     * @since 3.17
     */
    public void setDimension(final SampleDimension band, final Locale locale) {
        if (band instanceof GridSampleDimension) {
            setUserObject(band);
        }
        final InternationalString description = band.getDescription();
        if (description != null) {
            setDescriptor(description.toString(locale));
        }
        final double minimum = band.getMinimumValue();
        final double maximum = band.getMaximumValue();
        setValueRange(minimum, maximum);
        double[] fillValues = band.getNoDataValues();
        if (fillValues == null && band instanceof GridSampleDimension) {
            /*
             * This may happen if the sample dimension is geophysics.  We will accept the fill
             * values from the non-geophysics view if they are outside the range of geophysics
             * sample values, so there is no possible confusion. This is needed for example in
             * NetCDF files, where "fillValues" attribute exists even for geophysics data.
             */
            fillValues = ((GridSampleDimension) band).geophysics(false).getNoDataValues();
            if (fillValues != null) {
                int n = 0;
                for (int i=0; i<fillValues.length; i++) {
                    final double fillValue = fillValues[i];
                    if (fillValue < minimum || fillValue > maximum) {
                        fillValues[n++] = fillValue;
                    }
                }
                fillValues = ArraysExt.resize(fillValues, n);
            }
        }
        setFillSampleValues(fillValues);
        setTransfertFunction(band.getScale(), band.getOffset(), null); // TODO: declare transfer function.
        setUnits(band.getUnits());
    }

    /**
     * Sets the {@code "descriptor"} attribute to the given value.
     *
     * @param descriptor The descriptor, or {@code null} if none.
     */
    public void setDescriptor(final String descriptor) {
        setAttribute("descriptor", descriptor);
    }

    /**
     * Sets the {@code "units"} attribute to the given value.
     *
     * @param units The units, or {@code null} if none.
     */
    public void setUnits(final String units) {
        setAttribute("units", units);
    }

    /**
     * Sets the {@code "units"} attribute to the given value.
     *
     * @param units The units, or {@code null} if none.
     */
    public void setUnits(final Unit<?> units) {
        setAttribute("units", units);
    }

    /**
     * Sets the {@code "minValue"} and {@code "maxValue"} attributes to the given range.
     * They are the geophysical value, already transformed by the transfer function if
     * there is one.
     * <p>
     * This method replaces {@link Float#MAX_VALUE} by infinite values, because the
     * maximum value is often used in many format for meaning "infinity".
     *
     * @param minimum The value to be assigned to the {@code "minValue"} attribute.
     * @param maximum The value to be assigned to the {@code "maxValue"} attribute.
     */
    public void setValueRange(float minimum, float maximum) {
        if (minimum == -Float.MAX_VALUE) minimum = Float.NEGATIVE_INFINITY;
        if (maximum ==  Float.MAX_VALUE) maximum = Float.POSITIVE_INFINITY;
        setAttribute("minValue", minimum);
        setAttribute("maxValue", maximum);
    }

    /**
     * Sets the {@code "minValue"} and {@code "maxValue"} attributes to the given range.
     * They are the geophysical value, already transformed by the transfer function if
     * there is one.
     * <p>
     * This method replaces {@link Double#MAX_VALUE} by infinite values, because the
     * maximum value is often used in many format for meaning "infinity".
     *
     * @param minimum The value to be assigned to the {@code "minValue"} attribute.
     * @param maximum The value to be assigned to the {@code "maxValue"} attribute.
     */
    public void setValueRange(double minimum, double maximum) {
        if (minimum == -Double.MAX_VALUE) minimum = Double.NEGATIVE_INFINITY;
        if (maximum ==  Double.MAX_VALUE) maximum = Double.POSITIVE_INFINITY;
        setAttribute("minValue", minimum);
        setAttribute("maxValue", maximum);
    }

    /**
     * Sets the {@code "validSampleValues"} attribute to the given range. This is the range of
     * values encoded in the file, before the transformation by the transfer function if there
     * is one.
     * <p>
     * This method does nothing if the given range is infinite.
     *
     * @param minimum The minimal sample value, inclusive.
     * @param maximum The maximal sample value, inclusive.
     */
    public void setValidSampleValue(final double minimum, final double maximum) {
        if (minimum <= maximum && !Double.isInfinite(minimum) && !Double.isInfinite(maximum)) {
            setValidSampleValue(NumberRange.createBestFit(minimum, true, maximum, true));
        }
    }

    /**
     * Sets the {@code "validSampleValues"} attribute to the given range. This is the range of
     * values encoded in the file, before the transformation by the transfer function if there
     * is one.
     *
     * @param range The value to be assigned to the {@code "validSampleValues"} attribute.
     */
    public void setValidSampleValue(final NumberRange<?> range) {
        setAttribute("validSampleValues", range);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given value.
     *
     * @param value The value to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final int value) {
        setAttribute("fillSampleValues", value);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given array.
     *
     * @param values The values to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final int... values) {
        setAttribute("fillSampleValues", values);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given value.
     *
     * @param value The value to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final float value) {
        setAttribute("fillSampleValues", value);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given array.
     *
     * @param values The values to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final float... values) {
        setAttribute("fillSampleValues", values);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given value.
     *
     * @param value The value to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final double value) {
        setAttribute("fillSampleValues", value);
    }

    /**
     * Sets the {@code "fillSampleValues"} attribute to the given array.
     *
     * @param values The values to be assigned to the {@code "fillSampleValues"} attribute.
     */
    public void setFillSampleValues(final double... values) {
        setAttribute("fillSampleValues", values);
    }

    /**
     * Sets the {@code "scaleFactor"}, {@code "offset"} and {@code "transferFunctionType"}
     * attributes to the given values.
     *
     * @param scale  The value to be assigned to the {@code "scaleFactor"} attribute.
     * @param offset The value to be assigned to the {@code "offset"} attribute.
     * @param type   The value to be assigned to the {@code "transferFunctionType"} attribute.
     */
    public void setTransfertFunction(final double scale, final double offset, final TransferFunctionType type) {
        setAttribute("scaleFactor", scale);
        setAttribute("offset", offset);
        setAttribute("transferFunctionType", type);
    }

    /**
     * Sets the minimum and maximum values from the pixel values. This method is costly
     * and should be invoked only for relatively small images, after we checked that the
     * extremum are not already declared in the metadata.
     *
     * @param  reader The image reader to use for reading the pixel values.
     * @param  imageIndex The index of the image to read (usually 0).
     * @throws IOException If an error occurred while reading the image.
     *
     * @since 3.14
     */
    public void scanValidSampleValue(final ImageReader reader, final int imageIndex) throws IOException {
        int bandIndex = 0;
        final RectIter iter = RectIterFactory.create(reader.readAsRenderedImage(imageIndex, null), null);
        iter.startBands();
        if (!iter.finishedBands()) do {
            if (bandIndex >= childCount()) {
                bandIndex = appendChild();
            }
            selectChild(bandIndex);
            setAttribute("minValue", Double.NaN);
            setAttribute("maxValue", Double.NaN);
            final double[] padValues = getAttributeAsDoubles("fillSampleValues", true);
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            iter.startLines();
            if (!iter.finishedLines()) do {
                iter.startPixels();
                if (!iter.finishedPixels()) {
nextPixel:          do {
                        final double sample = iter.getSampleDouble();
                        if (padValues != null) {
                            for (final double v : padValues) {
                                if (sample == v) {
                                    continue nextPixel;
                                }
                            }
                        }
                        if (sample < min) min = sample;
                        if (sample > max) max = sample;
                    } while (!iter.nextPixelDone());
                }
            } while (!iter.nextLineDone());
            setValidSampleValue(min, max);
            // Do not invoke setValueRange(min, max) because the
            // later is about geophysics values, not sample values.
            bandIndex++;
        } while (!iter.nextBandDone());
    }

    /**
     * Returns {@code true} if a call to {@link #scanValidSampleValue(ImageReader, int)} is
     * recommended. This method uses heuristic rules that may be changed in any future version.
     *
     * @param  reader The image reader to use for reading information.
     * @param  imageIndex The index of the image to query (usually 0).
     * @return {@code true} if a call to {@code scanValidSampleValue} is recommended.
     * @throws IOException If an error occurred while querying the image.
     *
     * @since 3.14
     */
    public boolean isScanSuggested(final ImageReader reader, final int imageIndex) throws IOException {
        final int numChilds = childCount();
        for (int i=0; i<numChilds; i++) {
            selectChild(i);
            if (getAttribute("validSampleValues") == null) {
                final Double minValue = getAttributeAsDouble("minValue");
                final Double maxValue = getAttributeAsDouble("maxValue");
                if (minValue == null || maxValue == null || !(minValue <= maxValue)) { // Une '!' for catching NaN.
                    /*
                     * Stop the band scanning whatever happen: if a scan is recommended for at least
                     * one band, do the scan. If we don't have float type, we don't need to continue
                     * since this method will never returns 'true' in such case.
                     */
                    return ImageUtilities.isFloatType(reader.getRawImageType(imageIndex).getSampleModel().getDataType());
                }
            }
        }
        return false;
    }

    /**
     * Fixes the given value for rounding errors. This method should be invoked only for
     * variables related to sample dimensions, in order to avoid mixing potentially different
     * approach for fixing rounding error (the criterion for geographic coordinates could be
     * different).
     *
     * @param  value The computed value.
     * @return The value to store.
     *
     * @since 3.16
     */
    public static double fixRoundingError(double value) {
        final double sv = value * 36000;
        final double sr = Math.rint(sv);
        if (sv != sr && Math.abs(sv - sr) <= EPS) {
            value = sr / 36000;
        }
        if (value == 0) {
            value = 0; // Replace negative zero by positive zero.
        }
        return value;
    }

    /**
     * Invokes {@link #fixRoundingError(double)} for all elements in the given array.
     * Values in the given array will be modified in-place, and the same array is
     * returned for convenience.
     *
     * @param  values The array of values to fix for rounding error.
     * @return The given array, which now contains potentially modified values.
     *
     * @since 3.16
     */
    public static double[] fixRoundingError(final double[] values) {
        for (int i=0; i<values.length; i++) {
            values[i] = fixRoundingError(values[i]);
        }
        return values;
    }

    /**
     * Temporary Method in attempt to generalize comportment with reflexivity.
     * @param i
     * @return
     */
    public GridSampleDimension getGridSampleDimension(int i) {
        selectParent();
        selectChild(i);
        final Object userObject = getUserObject();
        if (userObject != null && userObject instanceof GridSampleDimension) {
            return (GridSampleDimension) userObject;
        }
        return null;
    }

    /**
     * Extract SampleDimension form accessor.
     *
     * @return list of SampleDimension or null
     */
    public List<GridSampleDimension> getGridSampleDimensions() {
        selectParent();
        final Object userObj = getUserObject();
        if (userObj != null && userObj instanceof List) return (List<GridSampleDimension>) userObj;

        final int nbC = childCount();
        if (nbC == 0) return null;
        final List<GridSampleDimension> gsD = new ArrayList<>(nbC);
        for (int i = 0; i < nbC; i++) {
            selectChild(i);
            final Object obj = getUserObject();
            if (obj != null) gsD.add((GridSampleDimension) obj);
        }
        return (gsD.isEmpty()) ? null : gsD;
    }
}
