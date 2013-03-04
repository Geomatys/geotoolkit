/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.coverage.io;

import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import org.opengis.coverage.InterpolationMethod;

import static org.apache.sis.util.ArgumentChecks.ensureBetween;


/**
 * Describes how a stream is to be encoded. Instances of this class are used to supply
 * information to instances of {@link GridCoverageWriter}.
 *
 * {@note This class is conceptually equivalent to the <code>ImageWriteParam</code> class provided
 * in the standard Java library. The main difference is that <code>GridCoverageWriteParam</code>
 * works with geodetic coordinates while <code>ImageWriteParam</code> works with pixel coordinates.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @see ImageWriteParam
 *
 * @since 3.14
 * @module
 */
public class GridCoverageWriteParam extends GridCoverageStoreParam {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 171475620398426965L;

    /**
     * The image format to use for fetching an {@link ImageWriter} instance, or {@code null} for
     * inferring it automatically from the {@linkplain GridCoverageWriter#getOutput() output}
     * suffix.
     */
    private String formatName;

    /**
     * The compression quality as a value between 0 and 1, or {@code null} if not defined.
     * Value 0 is for more compression, at the cost of either quality for lossy formats or
     * speed for lossless formats.
     *
     * @since 3.15
     */
    private Float compressionQuality;

    /**
     * The interpolation, or {@code null} for the default (nearest-neighbor).
     *
     * @since 3.15
     */
    private InterpolationMethod interpolation;

    /**
     * The sample values to use for filling empty areas, or {@code null} for the default values.
     *
     * @since 3.15
     */
    private double[] backgroundValues;

    /**
     * Creates a new {@code GridCoverageWriteParam} instance. All properties are
     * initialized to {@code null}. Callers must invoke setter methods in order
     * to provide information about the way to encode the stream.
     */
    public GridCoverageWriteParam() {
    }

    /**
     * Creates a new {@code GridCoverageWriteParam} instance initialized to the same
     * values than the given parameters.
     *
     * @param param The parameters to copy, or {@code null} if none.
     *
     * @since 3.15
     */
    public GridCoverageWriteParam(final GridCoverageStoreParam param) {
        super(param);
        if (param instanceof GridCoverageWriteParam) {
            final GridCoverageWriteParam wp = (GridCoverageWriteParam) param;
            formatName         = wp.formatName;
            compressionQuality = wp.compressionQuality;
            backgroundValues   = wp.backgroundValues;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        formatName         = null;
        compressionQuality = null;
        backgroundValues   = null;
        super.clear();
    }

    /**
     * Returns the image format to use for fetching an {@link ImageWriter}, or {@code null}
     * if unspecified. If {@code null}, then the format while be inferred automatically from
     * the {@linkplain GridCoverageWriter#getOutput() output} suffix.
     *
     * @return The format to use for fetching an {@link ImageWriter}, or {@code null} if unspecified.
     */
    public String getFormatName() {
        return formatName;
    }

    /**
     * Sets the image format to use for fetching an {@link ImageWriter}, or {@code null} if
     * unspecified. If {@code null}, then the format while be inferred automatically from
     * the {@linkplain GridCoverageWriter#getOutput() output} suffix.
     *
     * @param name The format to use for fetching an {@link ImageWriter}, or {@code null}
     *        if unspecified.
     */
    public void setFormatName(final String name) {
        formatName = name;
    }

    /**
     * Returns the compression quality as a value between 0 and 1, or {@code null} if not defined.
     * Value 0 stands for more compression, at the cost of either quality for lossy formats or
     * speed for lossless formats.
     *
     * @return The compression quality, or {@code null} for the format-dependent default value.
     *
     * @see ImageWriteParam#getCompressionQuality()
     *
     * @since 3.15
     */
    public Float getCompressionQuality() {
        return compressionQuality;
    }

    /**
     * Sets the compression quality as a value between 0 and 1, or {@code null} for the default.
     * Value 0 stands for more compression, at the cost of either quality for lossy formats or
     * speed for lossless formats.
     *
     * @param quality The compression quality, or {@code null} for the format-dependent default value.
     * @throws IllegalArgumentException If the given value is not null but not in the [0&hellip;1] range.
     *
     * @see ImageWriteParam#setCompressionQuality(float)
     *
     * @since 3.15
     */
    public void setCompressionQuality(final Float quality) throws IllegalArgumentException {
        if (quality != null) {
            ensureBetween("quality", 0f, 1f, quality);
        }
        compressionQuality = quality;
    }

    /**
     * Returns the interpolation method. The default value is
     * {@linkplain InterpolationMethod#NEAREST_NEIGHBOUR nearest-neighbor}.
     *
     * @return The interpolation method (never {@code null}).
     *
     * @since 3.15
     */
    public InterpolationMethod getInterpolation() {
        if (interpolation == null) {
            interpolation = InterpolationMethod.NEAREST_NEIGHBOUR;
        }
        return interpolation;
    }

    /**
     * Sets the interpolation method. As a convenience, the {@code null} value is considered
     * as synonymous to nearest-neighbor. We allow that because {@code null} can be interpreted
     * as "no interpolation".
     * <p>
     * The {@link InterpolationMethod#NEAREST_NEIGHBOUR} method shall be supported by all writers.
     * Other interpolation methods may or may not be supported on a case-by-case basis, but the
     * {@link InterpolationMethod#BILINEAR BILINEAR} and {@link InterpolationMethod#BICUBIC BICUBIC}
     * methods can be considered as typically supported for {@linkplain ImageCoverageWriter
     * image writers} backed by <cite>Java Advanced Imaging</cite>.
     *
     * @param method The interpolation method, or {@code null} for nearest-neighbor.
     *
     * @since 3.15
     */
    public void setInterpolation(final InterpolationMethod method) {
        interpolation = method;
    }

    /**
     * Returns the sample values to use for filling empty areas during reprojection,
     * or {@code null} for the default. If non-null, the array length shall be equals
     * to the number of bands.
     *
     * @return The background values for each bands, or {@code null} for the default.
     *
     * @since 3.15
     */
    public double[] getBackgroundValues() {
        double[] values = backgroundValues;
        if (values != null) {
            values = values.clone();
        }
        return values;
    }

    /**
     * Sets the sample values to use for filling empty areas during reprojection, or
     * {@code null} for the default. If non-null, the array length shall be equals to
     * the number of bands.
     * <p>
     * The values provided to this method will be casted to the appropriate type (typically
     * {@code byte}) at writing time.
     *
     * @param values The background values for each bands, or {@code null} for the default.
     *
     * @since 3.15
     */
    public void setBackgroundValues(double... values) {
        if (values != null) {
            values = values.clone();
        }
        backgroundValues = values;
    }
}
