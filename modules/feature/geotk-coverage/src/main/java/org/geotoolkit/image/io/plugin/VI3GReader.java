package org.geotoolkit.image.io.plugin;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.logging.Level;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.storage.ChannelImageInputStream;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.image.io.SpatialImageReader;
import static org.geotoolkit.image.io.WarningProducer.LOGGER;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.opengis.metadata.content.TransferFunctionType;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.PixelInCell;

/**
 * GIMMS AVHRR Global NDVI 1/12-degree Geographic Lat/Lon.
 *
 * 1. DESCRIPTION
 *
 * VI3G dataset is an inverse cartographic transformation and mosaicing of the
 *   GIMMS AVHRR 8-km Albers Conical Equal Area continentals AF, AZ, EA, NA, and
 *   SA to a global 1/12-degree Lat/Lon grid.
 *
 *   Continent demarcation and pixel selection is predetermined with an ancillary
 *   NDVI-3G based land-water mask.
 *
 * 2. FILE NAMING CONVENTION
 *
 *  geo[year][month][period].n[sat][-[VI][version]g
 *
 *  where
 *        year     2-int   2 digit year
 *        month    3-char  abbr. lower case month name
 *        period   3-char  alphanum period: bimonthly 15[ab]
 *        sat      2-int   satellite number
 *        version  n-int   version number (3)
 *
 *  For example,
 *
 *  geo09jan15a.n17-VI3g
 *
 * 3. GRID PARAMETERS
 *
 *    grid-name: Geographic Lat/Lon
 *    pixel-size: 1/12=0.833 degrees
 *
 *    size-x: 4320
 *    size-y: 2160
 *
 *    upper-left-lat: 90.0-1/24
 *    upper-left-lon: -180.0+1/24
 *    lower-right-lat: -90.0+1/24
 *    lower-right-lon: 180.0-1/24
 *
 *    *coordinates located UL corner of pixel
 *
 * 4. DATA FORMAT - VI3g
 *
 *    datatype: 16-bit signed integer
 *    byte-order: big endian
 *
 *    scale-factor: 10000
 *    min-valid: -10000
 *    max-valid: 10000
 *    mask-water: -10000
 *    mask-nodata: -5000
 *
 *    *values include embedded flags (see full NDVI-3G documentation - in preparation)
 *
 * 5. FLAG VALUES
 *    Each NDVI data set (ndvi3g) is an INT16 file saved with ieee-big_endian
 *    it ranges from -10000->(10000->10004)
 *    with the flagW file added to the ndvi values as follows:
 *	ndvi3g = round(ndvi*10000) + flagW - 1;
 *	flagW ranges from 1->7
 *    to retrieve the original ndvi  and flagW values
 * 	flagW = ndvi3g-floor(ndvi3g/10)*10 + 1;
 * 	ndvi = floor(ndvi3g/10)/1000
 *    The meaning of the FLAG:
 *	FLAG = 7 (missing data)
 *	FLAG = 6 (NDVI retrieved from average seasonal profile, possibly snow)
 *	FLAG = 5 (NDVI retrieved from average seasonal profile)
 *	FLAG = 4 (NDVI retrieved from spline interpolation, possibly snow)
 *	FLAG = 3 (NDVI retrieved from spline interpolation)
 *	FLAG = 2 (Good value)
 *	FLAG = 1 (Good value)
 * END
 *
 * @author Alexis Manin (Geomatys)
 */
public class VI3GReader extends SpatialImageReader {

    public static final int SAMPLE_SIZE = Short.SIZE / Byte.SIZE;

    public static final int WIDTH = 2160;
    public static final int HEIGHT = 4320;

    private static final ImageTypeSpecifier IMAGE_TYPE = ImageTypeSpecifier.createGrayscale(16, DataBuffer.TYPE_SHORT, true);

    private Path fileInput;

    /**
     * Constructs a new image reader.
     *
     * @param provider the {@link javax.imageio.spi.ImageReaderSpi} that is invoking this constructor, or null.
     */
    public VI3GReader(Spi provider) {
        super(provider);
    }

    @Override
    public int getWidth(int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (fileInput == null) {
            throw new IOException("No valid input set.");
        }

        return WIDTH;
    }

    @Override
    public int getHeight(int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (fileInput == null) {
            throw new IOException("No valid input set.");
        }

        return HEIGHT;
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

    @Override
    protected SpatialMetadata createMetadata(int imageIndex) throws IOException {
        if (imageIndex < 0)
            return null;
        SpatialMetadata md = new SpatialMetadata(false, this, null);

        final DimensionAccessor dac = new DimensionAccessor(md);
        dac.selectChild(dac.appendChild());
        dac.setFillSampleValues(-10000, -5000);
        dac.setValidSampleValue(0, 10006);
        dac.setTransfertFunction(1, 0, TransferFunctionType.LINEAR);
        dac.setUnits(Units.UNITY.multiply(10000));

        if (fileInput == null) {
            throw new IOException("No valid input set.");
        }

        try {
            // Set Geo-spatial information.
            GridDomainAccessor accessor = new GridDomainAccessor(md);

            final GeographicCRS geographicCRS = CommonCRS.WGS84.geographic();
            final ReferencingBuilder builder = new ReferencingBuilder(md);
            builder.setCoordinateReferenceSystem(geographicCRS);

            // HACK : a problem has been detected with Geotk rendering of latitude first data.
            // As a warkaround, we define the image as longitude first, and then roll it
            // using sheer.
            final GridEnvelope2D gridEnv = new GridEnvelope2D(0, 0, getWidth(imageIndex), getHeight(imageIndex));
            AffineTransform2D tr = new AffineTransform2D(-180.0 / WIDTH, 0, 0, 360.0 / HEIGHT, 90.0, -180.0);

            accessor.setGridGeometry(
                    new GeneralGridGeometry(gridEnv, PixelInCell.CELL_CORNER, tr, geographicCRS),
                    PixelInCell.CELL_CORNER, CellGeometry.AREA
            );

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Geo-spatial information cannot be retrieved from input " + input, e);
        }
        return md;
    }

        @Override
    public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        if (input instanceof Path) {
            fileInput = (Path) input;
        } else if (input instanceof File) {
            fileInput = ((File) input).toPath();
        } else {
            fileInput = null;
        }
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
        final Rectangle srcRegion = new Rectangle();
        final Rectangle dstRegion = new Rectangle();

        final BufferedImage image = getDestination(param, getImageTypes(imageIndex), WIDTH, HEIGHT);
        computeRegions(param, WIDTH, HEIGHT, image, srcRegion, dstRegion);
        readLayer(image.getRaster(), param, srcRegion, dstRegion);
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
                           final Rectangle srcRegion, final Rectangle dstRegion) throws IOException {

        try (final ImageInputStream imageStream = getImageInputStream()) {
            final DataBufferShort dataBuffer = (DataBufferShort) raster.getDataBuffer();
            final short[] data = dataBuffer.getData(0);

            final double stepX = (param == null)? 1 : param.getSourceXSubsampling();
            final double stepY = (param == null)? 1 : param.getSourceYSubsampling();

            // Current position (in byte) from source file.
            long srcBuffPos = (srcRegion.y * WIDTH + srcRegion.x) * SAMPLE_SIZE;
            long srcScanLineStride = WIDTH * SAMPLE_SIZE;

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
        if (fileInput == null)
            throw new IOException("Input object is not a valid file or path.");
        final ChannelImageInputStream stream = new ChannelImageInputStream(null, Files.newByteChannel(fileInput, StandardOpenOption.READ), ByteBuffer.allocateDirect(8192), false);
        stream.setByteOrder(ByteOrder.BIG_ENDIAN);
        return stream;
    }

    public static class Spi extends SpatialImageReader.Spi {

        /**
         * The list of valid input types.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[]{File.class, Path.class};

        /**
         * Default list of file extensions.
         */
        private static final String[] SUFFIXES = new String[]{"vi3g", "VI3G", "n07-VI3g"};

        private static final String[] MIME_TYPES = new String[]{"application/x-ogc-vi3g"};

        /**
         * Constructs a default {@code RawImageReader.Spi}. The fields are
         * initialized as documented in the <a href="#skip-navbar_top">class
         * javadoc</a>. Subclasses can modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default
         * array content.
         */
        public Spi() {
            names = SUFFIXES;
            suffixes = SUFFIXES;
            MIMETypes = MIME_TYPES;
            inputTypes = INPUT_TYPES;
            pluginClassName = VI3GReader.class.getName();
            // This reader does not support any metadata.
            nativeStreamMetadataFormatName = null;
            nativeImageMetadataFormatName = null;
        }

        @Override
        public String getDescription(Locale locale) {
            return "AVHRR VI3G format";
        }

        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            final Path temp;
            if (source instanceof File) {
                temp = ((File) source).toPath();
            } else if (source instanceof Path) {
                temp = (Path) source;
            } else {
                return false;
            }

            return temp.getFileName().toString().toLowerCase().endsWith("vi3g");
        }

        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new VI3GReader(this);
        }
    }
}
