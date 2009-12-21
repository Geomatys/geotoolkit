/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.text;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.ImageReaderAdapter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.util.Version;


/**
 * Reader for the <cite>World File</cite> format. This reader wraps an other image reader
 * for an "ordinary" image format, like TIFF, PNG or JPEG. This {@code WorldFileImageReader}
 * delegates the reading of pixel values to the wrapped reader, and additionally looks for
 * two small text files in the same directory than the image file, with the same filename
 * but a different extension:
 *
 * <ul>
 *   <li><p>A text file containing the coefficients of the affine transform mapping pixel
 *       coordinates to geodesic coordinates. The reader expects one coefficient per line,
 *       is the same order than the one expected by the
 *       {@link AffineTransform#AffineTransform(double[]) AffineTransform(double[])}
 *       constructor, which is <var>scaleX</var>, <var>shearY</var>, <var>shearX</var>,
 *       <var>scaleY</var>, <var>translateX</var>, <var>translateY</var>.
 *       This reader looks for a file having the following extensions, in preference order:</p>
 *       <ol>
 *         <li>The first letter of the image file extension, followed by the last letter of
 *             the image file extension, followed by {@code 'w'}. Example: {@code "tfw"} for
 *             {@code "tiff"} images, and {@code "jgw"} for {@code "jpeg"} images.</li>
 *         <li>The extension of the image file with a {@code 'w'} appended.</li>
 *         <li>The {@code "tfw} extension.</li>
 *       </ol>
 *   </li>
 *   <li><p>A text file containing the <cite>Coordinate Reference System</cite> (CRS)
 *       definition in <cite>Well Known Text</cite> (WKT) syntax. This reader looks
 *       for a file having the {@code ".prj"} extension.</p></li>
 * </ul>
 *
 * Every text file are expected to be encoded in ISO-8859-1 (a.k.a. ISO-LATIN-1) and every
 * numbers are expected to be formatted in US locale.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @see <a href="http://en.wikipedia.org/wiki/World_file">World File Format Description</a>
 * @see WorldFileWriter
 *
 * @since 3.07
 * @module
 */
public class WorldFileImageReader extends ImageReaderAdapter {
    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     * @param main The reader to use for reading the image in the classical image format.
     */
    public WorldFileImageReader(final Spi provider, final ImageReader main) {
        super(provider, main);
    }

    /**
     * Returns the input for the file identified by the given keyword. This method returns an
     * input which is typically a {@link File} or {@link java.net.URL} having the same name
     * than the {@linkplain #input input} of this reader, but a different extension. The new
     * extension is determined from the {@code keyword} argument, which can be:
     *
     * <ul>
     *   <li><p>{@code "tfw"} for the <cite>World File</cite>. The extension of the returned
     *       input may be {@code "tfw"} (most common), {@code "jgw"}, {@code "pgw"} or other
     *       depending on the extension of this reader {@linkplain #input input}, and depending
     *       which file has been determined to exist. See the
     *       <a href="#skip-navbar_top">class javadoc</a> for more details.</p></li>
     *
     *   <li><p>{@code "prj"} for the file of the <cite>Map Projection</cite>. The extension
     *       of the returned input is {@code "prj"}.</p></li>
     * </ul>
     *
     * Subclasses can override this method for specifying a different <cite>World File</cite>
     * ({@code "tfw"}) or <cite>Map Projection</cite> ({@code "prj"}) input. They can also
     * invoke this method with other keywords than the two above-cited ones, in which case
     * this method uses the given keyword as the extension of the returned input. However
     * the default {@code WorldFileImageReader} implementation uses only {@code "tfw"} and
     * {@code "prj"}.
     *
     * @param  keyword {@code "tfw"} for getting the <cite>World File</cite> input, or
     *         {@code "prj"} for getting the <cite>Map Projection</cite> input. Other
     *         keywords are allowed but subclass-specific.
     * @return The given kind of input typically as a {@link File} or {@link java.net.URL}
     *         object, or {@code null} if there is no input for the given keyword.
     * @throws IOException If an error occured while trying to determine the input.
     */
    protected Object getInput(final String keyword) throws IOException {
        return SupportFiles.changeExtension(input, keyword);
    }

    /**
     * Invokes {@link #getInput(String)} and verifies if the returned file exists.
     * If it does not exist, then returns {@code null}.
     *
     * @todo Current implementation checks only {@link File} object.
     *       We should check URL as well.
     */
    private Object getVerifiedInput(final String part) throws IOException {
        Object in = getInput(part);
        if (in instanceof File) {
            if (!((File) in).isFile()) {
                in = null;
            }
        }
        return in;
    }

    /**
     * Creates a new stream or image metadata. This method first delegates to the main reader as
     * documented in the {@linkplain ImageReaderAdapter#createMetadata(int) super-class method},
     * then completes the metadata with information read from the <cite>World File</cite> and
     * <cite>Map Projection</cite> files.
     * <p>
     * The <cite>World File</cite> and <cite>Map Projection</cite> files are determined by calls
     * to the {@link #getInput(String)} method. Subclasses can override the laters if they want
     * to specify different files to be read.
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        SpatialMetadata metadata = super.createMetadata(imageIndex);
        if (imageIndex >= 0) {
            AffineTransform gridToCRS = null;
            CoordinateReferenceSystem crs = null;
            Object in = getVerifiedInput("tfw");
            if (in != null) {
                gridToCRS = SupportFiles.parseTFW(IOUtilities.open(in), in);
            }
            in = getVerifiedInput("prj");
            if (in != null) {
                crs = PrjFiles.read(IOUtilities.open(in), true);
            }
            /*
             * If we have found information in TFW or PRJ files, complete metadata.
             */
            if (gridToCRS != null || crs != null) {
                if (metadata == null) {
                    metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE, this, null);
                }
                if (gridToCRS != null) {
                    final int width  = getWidth (imageIndex);
                    final int height = getHeight(imageIndex);
                    new GridDomainAccessor(metadata).setAll(gridToCRS, new Rectangle(width, height), null, null);
                }
                // TODO: Store the projection.
            }
        }
        return metadata;
    }



    /**
     * Service provider interface (SPI) for {@link WorldFileImageReader}s. This provider wraps
     * an other provider (typically for the TIFF, JPEG or PNG formats), which shall be specified
     * at construction time. The legal {@linkplain #inputTypes input types} are set to
     * {@link String}, {@link File}, {@link URI} and {@link URL} in order to allow the image
     * reader to infer the <cite>World File</cite> ({@code "tfw"}) and <cite>Map Projection</cite>
     * ({@code "prj"}) files from the image input file.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @see WorldFileImageWriter.Spi
     *
     * @since 3.07
     * @module
     */
    public static class Spi extends ImageReaderAdapter.Spi {
        /**
         * Creates a provider which will use the given format for reading pixel values.
         *
         * @param main The provider of the readers to use for reading the pixel values.
         */
        public Spi(final ImageReaderSpi main) {
            super(main);
            pluginClassName = "org.geotoolkit.image.io.text.WorldFileImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
        }

        /**
         * Creates a provider which will use the given format for reading pixel values.
         * This is a convenience constructor for {@link #Spi(ImageReaderSpi)} with a
         * provider fetched from the given format name.
         *
         * @param  format The name of the provider to use for reading the pixel values.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        public Spi(final String format) throws IllegalArgumentException {
            this(Formats.getReaderByFormatName(format));
        }

        /**
         * Returns a brief, human-readable description of this service provider.
         *
         * @param  locale The locale for which the return value should be localized.
         * @return A description of this service provider.
         */
        @Override
        public String getDescription(final Locale locale) {
            return Vocabulary.getResources(locale).getString(
                    Vocabulary.Keys.IMAGE_CODEC_WITH_WORLD_FILE_$2, 0, Formats.getFormatName(main));
        }

        /**
         * Creates a new <cite>World File</cite> reader. The extension is given to the
         * {@linkplain #main main} provider.
         *
         * @param  extension A plug-in specific extension object, or {@code null}.
         * @return A new reader.
         * @throws IOException If the reader can not be created.
         */
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new WorldFileImageReader(this, main.createReaderInstance(extension));
        }
    }
}
