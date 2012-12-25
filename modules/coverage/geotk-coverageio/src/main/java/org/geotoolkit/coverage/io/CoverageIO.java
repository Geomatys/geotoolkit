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
package org.geotoolkit.coverage.io;

import java.net.URL;
import java.io.File;
import java.util.Collections;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.lang.Static;

import static org.apache.sis.util.ArgumentChecks.*;


/**
 * Convenience methods for reading or writing a coverage. The method in this class creates
 * instances of {@link GridCoverageReader} or {@link GridCoverageWriter} for performing the
 * actual work. This is similar to the standard {@link javax.imageio.ImageIO} class and the
 * {@link org.geotoolkit.image.io.XImageIO} class, but applied to coverages.
 *
 * {@section Readers}
 * In the simplest case, this class just creates an {@link ImageCoverageReader} instance with
 * the input set to the given object (typically a {@link File} or {@link URL}). However if the
 * image is very large and is not encoded in a format that support natively tiling, it may be
 * more efficient to create a mosaic of tiles first. The {@link #writeOrReuseMosaic(File)}
 * method is provided for this purpose.
 *
 * {@section Writers}
 * This class delegates the actual work to the {@link ImageCoverageWriter} class.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.18
 * @module
 */
public final class CoverageIO extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private CoverageIO() {
    }

    /**
     * Convenience method reading a coverage from the given input. The input is typically
     * a {@link File}, {@link URL} or {@link String} object, but other types (especially
     * {@link javax.imageio.stream.ImageInputStream}) may be accepted as well depending
     * on the image format. The given input can also be an {@link javax.imageio.ImageReader}
     * instance with its input initialized.
     *
     * @param  input The input to read (typically a {@link File}).
     * @return A coverage read from the given input.
     * @throws CoverageStoreException If the coverage can not be read.
     */
    public static GridCoverage read(final Object input) throws CoverageStoreException {
        final GridCoverageReader reader = createSimpleReader(input);
        try {
            return reader.read(0, null);
        } finally {
            reader.dispose();
        }
    }

    /**
     * Convenience method writing a coverage to the given output. The output is typically
     * a {@link File} or {@link String} object, but other types (especially
     * {@link javax.imageio.stream.ImageOutputStream}) may be accepted as well depending
     * on the image format. The given input can also be an {@link javax.imageio.ImageWriter}
     * instance with its output initialized.
     *
     * @param coverage   The coverage to write.
     * @param formatName The image format as one of the Image I/O plugin name (e.g. {@code "png"}),
     *                   or {@code null} for auto-detection from the output file suffix.
     * @param output     The output where to write the image (typically a {@link File}).
     * @throws CoverageStoreException If the coverage can not be written.
     */
    public static void write(final GridCoverage coverage, final String formatName, final Object output)
            throws CoverageStoreException
    {
        ensureNonNull("coverage", coverage);
        write(Collections.singleton(coverage), formatName, output);
    }

    /**
     * Convenience method writing one of many coverages to the given output. The output
     * is typically a {@link File} or {@link String} object, but other types (especially
     * {@link javax.imageio.stream.ImageOutputStream}) may be accepted as well depending
     * on the image format. The given input can also be an {@link javax.imageio.ImageWriter}
     * instance with its output initialized.
     *
     * @param coverages  The coverages to write.
     * @param formatName The image format as one of the Image I/O plugin name (e.g. {@code "png"}),
     *                   or {@code null} for auto-detection from the output file suffix.
     * @param output     The output where to write the image (typically a {@link File}).
     * @throws CoverageStoreException If the coverages can not be written.
     *
     * @since 3.20
     */
    public static void write(final Iterable<? extends GridCoverage> coverages,
            final String formatName, final Object output) throws CoverageStoreException
    {
        ensureNonNull("coverages", coverages);
        ensureNonNull("output", output);
        GridCoverageWriteParam param = null;
        if (formatName != null) {
            param = new GridCoverageWriteParam();
            param.setFormatName(formatName);
        }
        final GridCoverageWriter writer = new ImageCoverageWriter();
        try {
            writer.setOutput(output);
            writer.write(coverages, param);
        } finally {
            writer.dispose();
        }
    }

    /**
     * Creates a simple reader which does not use any pyramid or mosaic tiling.
     * This reader is appropriate if the image is known to be small.
     * <p>
     * The input is typically a {@link File}, {@link URL} or {@link String} object, but other types
     * (especially {@link javax.imageio.stream.ImageInputStream}) may be accepted as well depending
     * on the image format. The given input can also be an {@link javax.imageio.ImageReader} instance
     * with its input initialized.
     *
     * @param  input The input to read (typically a {@link File}).
     * @return A coverage reader for the given input.
     * @throws CoverageStoreException If the reader can not be created for the given file.
     */
    public static GridCoverageReader createSimpleReader(final Object input) throws CoverageStoreException {
        ensureNonNull("input", input);
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(input);
        return reader;
    }

    /**
     * Creates a simple writer which does not perform any pyramid or mosaic tiling.
     * This writer is appropriate if the image is known to be small.
     * <p>
     * The output is typically a {@link File}, {@link URL} or {@link String} object, but other types
     * (especially {@link javax.imageio.stream.ImageOutputStream}) may be accepted as well depending
     * on the image format. The given output can also be an {@link javax.imageio.ImageWriter} instance
     * with its output initialized.
     *
     * @param  output The output where to write (typically a {@link File}).
     * @return A coverage writer for the given output.
     * @throws CoverageStoreException If the writer can not be created for the given file.
     *
     * @since 3.20
     */
    public static GridCoverageWriter createSimpleWriter(final Object output) throws CoverageStoreException {
        ensureNonNull("output", output);
        final ImageCoverageWriter writer = new ImageCoverageWriter();
        writer.setOutput(output);
        return writer;
    }

    /**
     * Creates a mosaic reader using the given tiles, which must exist. The input argument can
     * be an instance of {@link org.geotoolkit.image.io.mosaic.TileManager}, a {@link File} to
     * a serialized instance of {@code TileManager}, a directory, or an array or collection of
     * {@link org.geotoolkit.image.io.mosaic.Tile} instances.
     * <p>
     * The {@code crs} argument is optional if the input is a {@link File}, in which case this
     * method will attempt to parse a file of the same name with the {@code ".prj"} extension
     * using {@link org.geotoolkit.io.wkt.PrjFiles}. For all other cases, the CRS argument is
     * mandatory.
     *
     * @param  input The file, tiles or tile manager to use a input.
     * @param  crs The coordinate reference system of the mosaic image.
     * @return A mosaic reader for the given tiles and CRS.
     * @throws CoverageStoreException If the reader can not be created for the given tiles.
     */
    public static GridCoverageReader createMosaicReader(final Object input,
            final CoordinateReferenceSystem crs) throws CoverageStoreException
    {
        ensureNonNull("input", input);
        if (crs == null && input instanceof File) {
            return new MosaicCoverageReader((File) input, false);
        }
        ensureNonNull("crs", crs);
        return new MosaicCoverageReader(input, crs);
    }

    /**
     * Creates a mosaic reader using a cache of tiles at different resolutions. Tiles will be
     * created the first time this method is invoked for a given input. The tiles creation time
     * depends on the available memory, the image size and its format. The creation time can
     * range from a few seconds to several minutes or even hours if the given image is very large.
     * <p>
     * The tiles will be created in a sub-directory having the same name than the given input,
     * with an additional {@code ".tiles"} extension.
     *
     * @param  input The input to read.
     * @return A coverage reader for the given file.
     * @throws CoverageStoreException If the reader can not be created for the given file.
     */
    public static GridCoverageReader writeOrReuseMosaic(final File input) throws CoverageStoreException {
        ensureNonNull("input", input);
        return new MosaicCoverageReader(input, true);
    }
}
