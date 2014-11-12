/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2014, Geomatys
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.image.io.plugin;

import com.sun.media.imageio.stream.RawImageInputStream;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.PixelInCell;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import javax.measure.unit.SI;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An image reader to read SRTM data in .hgt format.
 *
 * /!\ WARNING : Ugly hacks are used for input management. Extended RawImageReader needs a stream, but we build it only
 * at reading time because we encountered unclosed streams problems.
 *
 * @author Alexis Manin (Geomatys)
 */
public class HGTReader extends RawImageReader {

    /**
     * HGT file name pattern. Give lower-left geographic position (CRS:84) of the current tile.
     */
    private static final Pattern FILENAME_PATTERN = Pattern.compile("(?i)(N|S)(\\d+)(E|W)(\\d+)");

    private static final short NO_DATA = -32768;

    private static final ImageTypeSpecifier IMAGE_TYPE = ImageTypeSpecifier.createGrayscale(16, DataBuffer.TYPE_SHORT, true);

    private File fileInput;

    /**
     * Constructs a new image reader.
     *
     * @param provider the {@link javax.imageio.spi.ImageReaderSpi} that is invoking this constructor, or null.
     */
    public HGTReader(Spi provider) {
        super(provider);
    }

    @Override
    public Object getInput() {
        // CRAPPY HACK : Get input must return file when queried outside read method, but stream whe Extended Raw reader
        // needs to read data.
        return (input == null)? fileInput : input;
    }

    @Override
    public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
        if (input instanceof Path) {
            fileInput = ((Path) input).toFile();
        } else if (input instanceof File) {
            fileInput = (File) input;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IllegalStateException, IOException {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return (int) Math.round(Math.sqrt(fileInput.length() / (Short.SIZE / Byte.SIZE)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return (int) Math.round(Math.sqrt(fileInput.length() / (Short.SIZE / Byte.SIZE)));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getRawDataType(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return IMAGE_TYPE.getSampleModel().getDataType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return IMAGE_TYPE;
    }

    /**
     * Returns {@code true} since random access is easy in uncompressed images.
     */
    @Override
    public boolean isRandomAccessEasy(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public static RawImageInputStream buildIIStream(final File input) throws IOException {
        final int width = (int) Math.round(Math.sqrt(input.length() / (Short.SIZE / Byte.SIZE)));
        final ImageInputStream wrapped = ImageIO.createImageInputStream(input);
        if (wrapped == null) {
            throw new IOException("Input file cannot be read : "+input);
        }
        return new RawImageInputStream(wrapped, IMAGE_TYPE, new long[]{0}, new Dimension[]{new Dimension(width, width)});
    }

    @Override
    protected SpatialMetadata createMetadata(int imageIndex) throws IOException {
        SpatialMetadata md = new SpatialMetadata(false, this, null);

        final DimensionAccessor dac = new DimensionAccessor(md);
        dac.setFillSampleValues(NO_DATA);
        dac.setUnits(SI.METRE);

        try {
            // Set Geo-spatial information.
            GridDomainAccessor accessor = new GridDomainAccessor(md);

            final String filename = fileInput.getName();
            final Matcher matcher = FILENAME_PATTERN.matcher(filename);
            if (!matcher.find()) {
                LOGGER.log(Level.WARNING, "input name does not match : " + filename);
            } else {
                final GeographicCRS geographicCRS = CommonCRS.WGS84.normalizedGeographic();
                final ReferencingBuilder builder = new ReferencingBuilder(md);
                builder.setCoordinateReferenceSystem(geographicCRS);

                final GridEnvelope2D gridEnv = new GridEnvelope2D(0, 0, getWidth(0), getHeight(0));
                final GeneralEnvelope envelope = new GeneralEnvelope(geographicCRS);
                final int latitude = matcher.group(1).toLowerCase().startsWith("n")?
                        Integer.parseInt(matcher.group(2)) : -Integer.parseInt(matcher.group(2));
                final int longitude = matcher.group(3).toLowerCase().startsWith("e")?
                        Integer.parseInt(matcher.group(4)) : -Integer.parseInt(matcher.group(4));
                envelope.setRange(0, longitude, longitude+1);
                envelope.setRange(1, latitude, latitude+1);

                accessor.setGridGeometry(new GridGeometry2D(gridEnv, envelope), PixelInCell.CELL_CORNER, CellGeometry.POINT);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Geo-spatial information cannot be retrieved from input " + input, e);
        }
        return md;
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
        input = buildIIStream(fileInput);
        try {
            return super.read(imageIndex, param);
        } finally {
            ((ImageInputStream)input).close();
            input = null;
        }
    }

    public static class Spi extends RawImageReader.Spi {

        /**
         * The list of valid input types.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {File.class, Path.class};

        /**
         * Default list of file extensions.
         */
        private static final String[] SUFFIXES = new String[] {"hgt"};

        /**
         * Constructs a default {@code RawImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            suffixes = SUFFIXES;
            inputTypes      = INPUT_TYPES;
            pluginClassName = HGTReader.class.getName();
            // This reader does not support any metadata.
            nativeStreamMetadataFormatName = null;
            nativeImageMetadataFormatName  = null;
        }

        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            if (source instanceof File) {
                try (final RawImageInputStream stream = buildIIStream((File)source)) {
                    return super.canDecodeInput(stream);
                }
            } else {
                return false;
            }
        }

        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new HGTReader(this);
        }
    }
}