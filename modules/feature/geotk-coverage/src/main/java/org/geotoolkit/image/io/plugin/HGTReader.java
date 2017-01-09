/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sis.measure.Units;
import org.apache.sis.internal.storage.io.ChannelImageInputStream;
import org.geotoolkit.image.io.SpatialImageReader;
import org.opengis.metadata.content.TransferFunctionType;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;


/**
 * An image reader to read SRTM data in .hgt format.
 *
 * /!\ WARNING : Ugly hacks are used for input management. Extended RawImageReader needs a stream, but we build it only
 * at reading time because we encountered unclosed streams problems.
 *
 * @author Alexis Manin (Geomatys)
 */
public class HGTReader extends SpatialImageReader {

    /**
     * HGT file name pattern. Give lower-left geographic position (CRS:84) of the current tile.
     */
    private static final Pattern FILENAME_PATTERN = Pattern.compile("(?i)(N|S)(\\d+)(E|W)(\\d+)");

    private static final ImageTypeSpecifier IMAGE_TYPE = ImageTypeSpecifier.createGrayscale(16, DataBuffer.TYPE_SHORT, true);

    static final int SAMPLE_SIZE = Short.SIZE / Byte.SIZE;

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
    public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        if (input instanceof Path) {
            fileInput = ((Path) input).toFile();
        } else if (input instanceof File) {
            fileInput = (File) input;
        } else {
            fileInput = null;
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
        if (fileInput == null) {
            throw new IOException("No valid input set.");
        }
        return (int) Math.round(Math.sqrt(fileInput.length() / (Short.SIZE / Byte.SIZE)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (fileInput == null) {
            throw new IOException("No valid input set.");
        }
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

    @Override
    protected SpatialMetadata createMetadata(int imageIndex) throws IOException {
        if (imageIndex < 0)
            return null;
        SpatialMetadata md = new SpatialMetadata(false, this, null);

        final DimensionAccessor dac = new DimensionAccessor(md);
        dac.selectChild(dac.appendChild());
        dac.setFillSampleValues(Short.MIN_VALUE);
        dac.setValidSampleValue(Short.MIN_VALUE + 1, Short.MAX_VALUE);
        dac.setTransfertFunction(1, 0, TransferFunctionType.LINEAR);
        dac.setUnits(Units.METRE);

        if (fileInput == null) {
            throw new IOException("No valid input set.");
        }

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

                final GridEnvelope2D gridEnv = new GridEnvelope2D(0, 0, getWidth(imageIndex), getHeight(imageIndex));
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
        final int width = (int) StrictMath.round(StrictMath.sqrt(fileInput.length() / (Short.SIZE / Byte.SIZE)));
        final Rectangle srcRegion = new Rectangle();
        final Rectangle dstRegion = new Rectangle();

        final BufferedImage image = getDestination(param, getImageTypes(imageIndex), width, width);
        computeRegions(param, width, width, image, srcRegion, dstRegion);
        readLayer(image.getRaster(), param, srcRegion, dstRegion, width);
        return image;
    }

    /**
     * Processes to the image reading, and stores the pixels in the given raster.<br/>
     * Process fill raster from informations stored in stripOffset made.
     *
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void readLayer(final WritableRaster raster, final ImageReadParam param,
                           final Rectangle srcRegion, final Rectangle dstRegion, int srcImgWidth) throws IOException {

        try (final ImageInputStream imageStream = getImageInputStream()) {
            final DataBufferShort dataBuffer = (DataBufferShort) raster.getDataBuffer();
            final short[] data = dataBuffer.getData(0);

            final double stepX = (param == null)? 1 : param.getSourceXSubsampling();
            final double stepY = (param == null)? 1 : param.getSourceYSubsampling();

            // Current position (in byte) from source file.
            long srcBuffPos = (srcRegion.y * srcImgWidth + srcRegion.x) * SAMPLE_SIZE;
            long srcScanLineStride = srcImgWidth * SAMPLE_SIZE;

            // Current position in destination array (short)
            int destPosition = dstRegion.y * raster.getWidth() + dstRegion.x;
            int destScanLineStride = raster.getWidth();

            final int srcMaxY = srcRegion.y + srcRegion.height;
            if (stepX == 1) {
                for (int y = srcRegion.y; y < srcMaxY; y += stepY) {

                    imageStream.seek(srcBuffPos);

                    imageStream.readFully(data, destPosition, srcRegion.width);

                    // Prepare to go on the next line of source image
                    srcBuffPos += srcScanLineStride * stepY;

                    // Prepare to go on the next line of destination image
                    destPosition += destScanLineStride;
                }

            } else {
                for (int y = srcRegion.y; y < srcMaxY; y += stepY) {

                    int tmpDestPosition = destPosition;
                    for (int i = 0; i < srcRegion.width; i += stepX, tmpDestPosition++) {
//                        data[tmpDestPosition] = tmpLine[i];
                        imageStream.seek(srcBuffPos+(i*SAMPLE_SIZE));
                        imageStream.readFully(data, tmpDestPosition, 1);
                    }

                    // Prepare to go on the next line of source image
                    srcBuffPos += srcScanLineStride * stepY;

                    // Prepare to go on the next line of destination image
                    destPosition += destScanLineStride;
                }
            }
        }
    }

    private ImageInputStream getImageInputStream() throws IOException {
        return new ChannelImageInputStream(null, openChannel(fileInput), ByteBuffer.allocateDirect(8192), false);
    }

    private static SeekableByteChannel openChannel(Object input) throws IOException {
        final Path inputPath;
        if (input instanceof File) {
            inputPath = ((File) input).toPath();
        } else if (input instanceof Path) {
            inputPath = (Path) input;
        } else {
            throw new IOException("Input object is not a valid file or path.");
        }
        return Files.newByteChannel(inputPath);
    }

    public static class Spi extends SpatialImageReader.Spi {

        /**
         * The list of valid input types.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {File.class, Path.class};

        /**
         * Default list of file extensions.
         */
        private static final String[] SUFFIXES = new String[] {"hgt"};

        private static final String[] MIME_TYPES = new String[] {"application/x-ogc-srtmhgt"};

        /**
         * Constructs a default {@code RawImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names = SUFFIXES;
            suffixes = SUFFIXES;
            MIMETypes = MIME_TYPES;
            inputTypes      = INPUT_TYPES;
            pluginClassName = HGTReader.class.getName();
            // This reader does not support any metadata.
            nativeStreamMetadataFormatName = null;
            nativeImageMetadataFormatName  = null;
        }

        @Override
        public String getDescription(Locale locale) {
            return "NASA HGT format for SRTM distribution.";
        }

        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            final Path temp;
            if (source instanceof File) {
                temp = ((File) source).toPath();
            } else if (source instanceof  Path) {
                temp = (Path) source;
            } else {
                return false;
            }

            return (FILENAME_PATTERN.matcher(temp.getFileName().toString()).find() && Files.isReadable(temp));
        }

        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new HGTReader(this);
        }
    }
}