/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.io;


/**
 * Describes how a stream is to be encoded. Instances of this class are used to supply
 * information to instances of {@link GridCoverageWriter}.
 *
 * {@note This class is conceptually equivalent to the <code>ImageWriteParam</code> class provided
 * in the standard Java library. The main difference is that <code>GridCoverageWriteParam</code>
 * works with geodetic coordinates while <code>ImageWriteParam</code> works with pixel coordinates.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @see javax.imageio.ImageWriteParam
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
     * Creates a new {@code GridCoverageWriteParam} instance. All properties are
     * initialized to {@code null}. Callers must invoke setter methods in order
     * to provide information about the way to encode the stream.
     */
    public GridCoverageWriteParam() {
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
}
