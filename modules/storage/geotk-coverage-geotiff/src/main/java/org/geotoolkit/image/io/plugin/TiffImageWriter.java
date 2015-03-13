/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2014, Open Source Geospatial Foundation (OSGeo)
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

import com.sun.media.imageioimpl.plugins.tiff.TIFFImageMetadata;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;

import org.apache.sis.internal.storage.ChannelImageOutputStream;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.NullArgumentException;
import static org.geotoolkit.image.internal.ImageUtils.*;
import org.geotoolkit.image.io.SpatialImageWriteParam;
import org.geotoolkit.image.io.SpatialImageWriter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.util.DomUtilities.getNodeByLocalName;

import org.geotoolkit.metadata.geotiff.GeoTiffMetaDataWriter;
import org.geotoolkit.util.DomUtilities;
import org.opengis.util.FactoryException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Remi Marechal       (Geomatys)
 * @author Alexis Manin        (Geomatys)
 */
public class TiffImageWriter extends SpatialImageWriter {

    /**
     * Some needed attributes used during LZW compression algorithm.
     */
    private final static short LZW_CLEAR_CODE      = 256;
    private final static short LZW_EOI_CODE        = 257;
    private final static short LZW_DEFAULT_CODE    = 258;
    private final static short LZW_MAX_CODE_LENGTH = 12;

    /**
     * String use to determinate LZW compression type.
     *
     * @see TiffImageWriteParam#compressionTypes
     */
    private final static String lzw      = "LZW";

    /**
     * String use to determinate packbits compression type.
     *
     * @see TiffImageWriteParam#compressionTypes
     */
    private final static String packbits = "PackBits";

    /**
     * Size of data structures in standard TIFF files ({@code SIZE_*}) and in big TIFF files
     * ({@code SIZE_BIG_*}). In standard TIFF, the size of structures for counting the number
     * of records or the file offsets vary, while in big TIFF everything is 64 bits.
     */
    private static final int
            SIZE_ENTRY = 12,                     SIZE_BIG_ENTRY = 20,
            SIZE_SHORT =   Short.SIZE/Byte.SIZE, SIZE_BIG_SHORT = Long.SIZE/Byte.SIZE,
            SIZE_INT   = Integer.SIZE/Byte.SIZE, SIZE_BIG_INT   = Long.SIZE/Byte.SIZE;

    /**
     * Types supported by this writer. The type is the short at offset 2 in the directory entry.
     */
    private static final short // 1, 2, 3, 4, 5 unsigned 0 -> 2^nbit  6, 7, 8, 9, 10 signed -2^(nbit-1) -> 2^(nbit-1) 11, 12 signed.
            TYPE_UBYTE     = 1,
            TYPE_ASCII     = 2,
            TYPE_USHORT    = 3,
            TYPE_UINT      = 4,
            TYPE_URATIONAL = 5, // unsigned Integer / unsigned Integer
            TYPE_BYTE      = 6,
            // type 7 is undefined
            TYPE_SHORT     = 8,
            TYPE_INT       = 9,
            TYPE_RATIONAL  = 10, // signed Integer / signed Integer
            TYPE_FLOAT     = 11,
            TYPE_DOUBLE    = 12,
            TYPE_IFD       = 13, // IFD is like UINT.
            TYPE_ULONG     = 16,
            TYPE_LONG      = 17,
            TYPE_IFD8      = 18; // IFD is like ULONG.

    /**
     * The size of each type in bytes, or 0 if unknown.
     */
    private static final int[] TYPE_SIZE = new int[19];
    static {
        final int[] size = TYPE_SIZE;
        size[TYPE_BYTE]  = size[TYPE_UBYTE] = size[TYPE_ASCII] =    Byte.SIZE        / Byte.SIZE;
        size[TYPE_SHORT] = size[TYPE_USHORT]                   =   Short.SIZE        / Byte.SIZE;
        size[TYPE_INT]   = size[TYPE_UINT]  = size[TYPE_IFD]   = Integer.SIZE        / Byte.SIZE;
        size[TYPE_LONG]  = size[TYPE_ULONG] = size[TYPE_IFD8]  =    Long.SIZE        / Byte.SIZE;
        size[TYPE_FLOAT]                                       =   Float.SIZE        / Byte.SIZE;
        size[TYPE_DOUBLE]                                      =  Double.SIZE        / Byte.SIZE;
        size[TYPE_URATIONAL] = size[TYPE_RATIONAL]             = (Integer.SIZE << 1) / Byte.SIZE; //rational = Integer / Integer. 2 Integer values red.
    }

    /**
     * The channel to the TIFF file. Will be created from the {@linkplain #output} when first needed.
     */
    private ChannelImageOutputStream channel;

    /**
     * Position in tiff file where to write byte count array.
     * @see #writeByteCountAndOffsets(long, short, java.lang.Object, long, short, java.lang.Object)
     */
    private long byteCountTagPosition;

    /**
     * Position in tiff file where to write offset array.
     * @see #writeByteCountAndOffsets(long, short, java.lang.Object, long, short, java.lang.Object)
     */
    private long offsetTagPosition;

    /*
     * Attributs only use during packbits compression writing.
     */
    /**
     * Buffer position where to write number of compressed {@code byte}.
     *
     * @see #writeWithPackBitsCompression(byte)
     */
    private int n32773Position;

    /**
     * Current compressed {@code byte} number.
     *
     * @see #writeWithPackBitsCompression(byte)
     */
    private int n32773Value;

    /**
     * Position from source data array of the last sample of the current row.
     *
     * @see #writeWithPackBitsCompression(byte)
     */
    private long lastByte32773;

    /**
     * Position from source data array of the penultimate sample of the current row.
     *
     * @see #writeWithPackBitsCompression(byte)
     */
    private long precLastByte32773;

    /**
     * Byte array use during pack bit compression (32774) internal mechanic.
     */
    private byte[] packBitArray;

    /**
     * current position into {@linkplain #packBitArray}.
     */
    private int currentPBAPos;

    /**
     * Count the nth compressed byte.
     * Counter in relation with packbit compression to anticipate end of current strip or tile.
     * @see #writeWithCompression(java.lang.Object, int, int, int, int)
     */
    private long rowByte32773Pos;

    /**
     * Bits size of current written LZW code.
     */
    private int currentLZWCodeLength;

    /**
     * Current written LZW code value from {@linkplain LZWMap}.
     */
    private short currentLZWCode;

    /**
     * Position in the current LZW key array.
     */
    private int wkPos;

    /**
     * Key byte array of the current LZW suit.
     */
    private byte[] wk = null;

    /**
     * Map which contain LZW Key and code.
     */
    private LZWMap lzwMap;

    /**
     * {@code Boolean} to define if image will write with bigTiff specification or standard tiff specification.
     */
    private boolean isBigTIFF = false;

    /**
     * Destination image tile width.
     */
    private int currentImgTW;

    /**
     * Destination image tile height.
     */
    private int currentImgTH;

    /**
     * Destination image tile number in X direction.
     */
    private int currentImgNumXT;

    /**
     * Destination image tile number in Y direction.
     */
    private int currentImgNumYT;

    /**
     * Current size in {@code byte} of a tiff tag.<br/>
     * In case of bigtiff writing size of an entry (tiff tag) is {@linkplain #SIZE_BIG_ENTRY}
     * whereas in standard tiff size is {@linkplain #SIZE_ENTRY}.
     */
    private int currentSizeEntry;

    /**
     * Define size in byte of number which define tag number for each ifd.
     */
    private int currentSizeTagNumber;

    /**
     * Define size in byte of offset for next ifd.
     */
    private int currentSizeNextIFD;

    /**
     * Bit number per sample about current written image.
     */
    private short bitPerSample;

    private short samplePerPixel;

    /**
     * Compression value of current image writing.
     */
    private int compression;

    /**
     * {@code Rectangle} which define boundary of the current written image.<br/>
     * See {@linkplain #computeRegions(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.
     */
     private Rectangle imageBoundary;

    /**
     * {@code Rectangle} which define boundary of the area which will be written in the image space.<br/>
     * See {@linkplain #computeRegions(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.
     */
    private Rectangle srcRegion;

    /**
     * {@code Rectangle} which define boundary of the destination area after all  transformations from {@link ImageWriteParam} attributes.<br/>
     * See {@linkplain #computeRegions(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.
     */
    private Rectangle destRegion;

    /**
     * {@code Rectangle} which define boundary in the image space, of the source area but padded to be multiply by tile properties.<br/>
     * Only use during write by tile.<br/>
     * See {@linkplain #computeRegions(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.<br/>
     * See also {@linkplain #writeImageByTiles(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam)}.
     */
    private Rectangle tileRegion;

    private Rectangle dstRepRegion = null;

    /**
     * Map table which contain all tiff properties from all images.
     */
    private Map<Integer, Map>[] metaHeads;

    private int metaIndex = 0;

    /**
     * Map organize in ascending order by {@code Integer} tag value, and contain all tiff tag in attempt to be writen.
     */
    private TreeMap<Integer, Map> headProperties;

    /**
     * table of length 2 where ifdPosition[0] contain chanel position of current
     * image data beginning and ifdPosition[1] contain chanel position where to write the nextIFD offset.
     */
    private long[] ifdPosition;

    /**
     * Current native {@link ByteOrder}.
     * @see #writeWithCompression(java.lang.Object, int, int, int, int)
     */
    ByteOrder currentBO;

    private int rowsPerStrip;
    private Object replaceOffsetArray;
    private boolean endOfFileReached = false;
    private long endOfFile;

    /**
     *
     * @param provider
     */
    public TiffImageWriter(final TiffImageWriter.Spi provider) {
        super(provider);
        ifdPosition     = new long[2];
        headProperties  = null;
        packBitArray    = new byte[8196];
        currentPBAPos   = 0;
        rowByte32773Pos = 0;
        metaHeads = new Map[4];
    }

    /**
     *
     * @param layerIndex
     */
    private void selectLayer(final int layerIndex) {
        if (layerIndex >= metaHeads.length || metaHeads[layerIndex] == null) {
            throw new IllegalStateException("The specified asked layer doesn't exist at index : "+layerIndex);
        }

        this.headProperties = (TreeMap<Integer, Map>) metaHeads[layerIndex];
        final Map<String, Object> iwObj   = headProperties.get(ImageWidth);
        final Map<String, Object> ihObj   = headProperties.get(ImageLength);
        final Map<String, Object> isppObj = headProperties.get(SamplesPerPixel);
        final Map<String, Object> ibpsObj = headProperties.get(BitsPerSample);
        final Map<String, Object> compObj = headProperties.get(Compression);


        assert compObj != null;
        final int comp = ((short[]) compObj.get(ATT_VALUE))[0];
        assert comp == 1 || comp == 5 || comp == 32773 : "compression of current layer is not supported. layer index : "+layerIndex+" compression value : "+compression;

        compression = comp;

        final int imageWidth;
        final short imgWT = (short) iwObj.get(ATT_TYPE);
        imageWidth = (imgWT == TYPE_USHORT) ? ((short[])  iwObj.get(ATT_VALUE))[0] & 0xFFFF : ((int[])  iwObj.get(ATT_VALUE))[0];

        final short imgHT = (short) ihObj.get(ATT_TYPE);
        final int imageHeight = (imgHT == TYPE_USHORT) ? ((short[])  ihObj.get(ATT_VALUE))[0] & 0xFFFF: ((int[])  ihObj.get(ATT_VALUE))[0];

        samplePerPixel = ((short[]) isppObj.get(ATT_VALUE))[0];
        bitPerSample   = ((short[]) ibpsObj.get(ATT_VALUE))[0];

        isBigTIFF = ((long) imageWidth * imageHeight * samplePerPixel * bitPerSample / Byte.SIZE) >= 1E9; //-- 1Go

        destRegion = new Rectangle(0, 0, imageWidth, imageHeight);

        final Map<String, Object> twObj   =  headProperties.get(TileWidth);
        final Map<String, Object> thObj   =  headProperties.get(TileLength);
        final Map<String, Object> toObj   =  headProperties.get(TileOffsets);

        /*
         * Declare the image as valid only if the mandatory information are present.
         */
        if (twObj == null || thObj == null || toObj == null) {

            final Map<String, Object> rpsObj   =  headProperties.get(RowsPerStrip);
            final Map<String, Object> sOffObj  =  headProperties.get(StripOffsets);
            final Map<String, Object> sbcObj   =  headProperties.get(StripByteCounts);

            final short rpsT = (short) rpsObj.get(ATT_TYPE);
            rowsPerStrip = (rpsT == TYPE_USHORT) ? ((short[]) rpsObj.get(ATT_VALUE))[0] & 0xFFFF : ((int[]) rpsObj.get(ATT_VALUE))[0];
            replaceOffsetArray = sOffObj.get(ATT_VALUE);
            tileRegion = destRegion;
        } else {
            final short twT = (short) twObj.get(ATT_TYPE);
            currentImgTW   = (twT == TYPE_USHORT) ? ((short[]) twObj.get(ATT_VALUE))[0] & 0xFFFF : ((int[]) twObj.get(ATT_VALUE))[0];

            final short thT = (short) thObj.get(ATT_TYPE);
            currentImgTH  = (thT == TYPE_USHORT) ? ((short[]) thObj.get(ATT_VALUE))[0] & 0xFFFF : ((int[]) thObj.get(ATT_VALUE))[0];

            replaceOffsetArray = toObj.get(ATT_VALUE);

            final int numXTile = (imageWidth + currentImgTW - 1)  / currentImgTW;
            final int numYTile = (imageHeight + currentImgTH - 1) / currentImgTH;
            tileRegion = new Rectangle(0, 0, numXTile * currentImgTW, numYTile * currentImgTH);
        }
    }

   /**
    * {@inheritDoc }
    *
    * @param streamMetadata metadatas.
    * @param image
    * @param param properties to write image or null.
    * @throws IOException if problem during image writing.
    * @throws NullArgumentException if streamMetadata or image is null.
    */
    @Override
    public void write(final IIOMetadata streamMetadata, final IIOImage image, final ImageWriteParam param) throws IOException {
        ArgumentChecks.ensureNonNull("IIOImage image", image);

        if (param != null && param.getSourceBands() != null) {
            LOGGER.log(Level.WARNING, "Tiff Image writer does not manage source band selection. ImageWriteParam.sourceBands parameter will be ignored.");
        }

        assert headProperties == null;
        //-- a new distinct map for each layer --//
        headProperties = new TreeMap<>();

        /*
         * Global (stream) metadata. We will try to write it, but if we fail at decoding, we just ignore because it's not
         * vital information.
         */
        if (streamMetadata != null) {
            IIOMetadata strMetadata = null;
            try {
                strMetadata = toGTiffFormat(streamMetadata);
            } catch (Exception e) {
                LOGGER.log(Level.INFO, "Global metadata unreadable. Ignored.");
            }
            if (strMetadata != null) {
                addMetadataProperties(strMetadata.getAsTree(strMetadata.getNativeMetadataFormatName()), headProperties);
            }
        }

        //-- get image --//
        final RenderedImage img   = image.getRenderedImage();

        //-- get metadata from IIOImage --//
        adaptMetadata(image, param);
        final IIOMetadata iioMeth = image.getMetadata();
        if (iioMeth != null) {
            final Node iioNode    = iioMeth.getAsTree(iioMeth.getNativeMetadataFormatName());
            addMetadataProperties(iioNode, headProperties);
        }
        write(img, headProperties, param, ifdPosition);
        final List<? extends BufferedImage> thumbnails = image.getThumbnails();
        if (thumbnails != null) {
            for (BufferedImage buff : thumbnails) {
                assert headProperties == null;
                //-- a new distinct map for each layer --//
                headProperties = new TreeMap<>();
                //-- add thumbnails tiff tag --//
                addProperty(NewSubfileType, TYPE_LONG, 1, new long[]{1}, headProperties);
                write(buff, headProperties, null, ifdPosition);
            }
        }
    }

    /**
     * {@inheritDoc }.
     *
     * Write IIOImage, its metadata and thumbnails if it contain them.
     *
     * @param image image will be write.
     * @throws IOException if problem during writing.
     * @throws NullArgumentException if image is null.
     */
    @Override
    public void write(final IIOImage image) throws IOException {
        ArgumentChecks.ensureNonNull("IIOImage image", image);

        assert headProperties == null;
        //-- a new distinct map for each layer --//
        headProperties = new TreeMap<>();

        //-- get image --//
        final RenderedImage img   = image.getRenderedImage();

        //-- get metadata from IIOImage --//
        adaptMetadata(image, null);
        final IIOMetadata iioMeth = image.getMetadata();
        if (iioMeth != null) {
            final Node iioNode    = iioMeth.getAsTree(iioMeth.getNativeMetadataFormatName());
            addMetadataProperties(iioNode, headProperties);
        }
        write(img, headProperties, null, ifdPosition);
        for (BufferedImage buff : image.getThumbnails()) {
            assert headProperties == null;
            //-- a new distinct map for each layer --//
            headProperties = new TreeMap<>();
            //-- add thumbnails tiff tag --//
            addProperty(NewSubfileType, TYPE_LONG, 1, new long[]{1}, headProperties);
            write(buff, headProperties, null, ifdPosition);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void writeToSequence(IIOImage image, ImageWriteParam param) throws IOException {
        ArgumentChecks.ensureNonNull("IIOImage image", image);
        if (param == null) {
            write(image);
            return;
        }
        assert headProperties == null;
        //-- a new distinct map for each layer --//
        headProperties = new TreeMap<>();

        //-- get image --//
        final RenderedImage img   = image.getRenderedImage();

        //-- get metadata from IIOImage --//
        adaptMetadata(image, param);
        final IIOMetadata iioMeth = image.getMetadata();
        if (iioMeth != null) {
            final Node iioNode    = iioMeth.getAsTree(iioMeth.getNativeMetadataFormatName());
            addMetadataProperties(iioNode, headProperties);
        }
        write(img, headProperties, param, ifdPosition);
        for (BufferedImage buff : image.getThumbnails()) {
            assert headProperties == null;
            //-- a new distinct map for each layer --//
            headProperties = new TreeMap<>();
            //-- add thumbnails tiff tag --//
            addProperty(NewSubfileType, TYPE_LONG, 1, new long[]{1}, headProperties);
            write(buff, headProperties, param, ifdPosition);
        }
    }

    /**
     * {@inheritDoc }
     *
     * This method is not supported in current implementation.
     *
     * @param imageIndex
     * @param image
     * @param param
     * @throws IOException
     * @throws IllegalStateException
     */
    @Override
    public void writeInsert(final int imageIndex, final IIOImage image, final ImageWriteParam param) throws IOException {
        throw new IllegalStateException("not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canWriteRasters() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canWriteSequence() {
        return true;
    }

    /**
     * Write {@link RenderedImage} from {@link ImageWriteParam} properties.
     *
     * @param image image which will be write.
     * @param param properties to write image or null.
     * @throws IOException if problem during writing.
     * @throws NullArgumentException if image is null.
     */
    public void write(final RenderedImage image, final ImageWriteParam param) throws IOException {
        ArgumentChecks.ensureNonNull("RenderedImage image", image);
        assert headProperties == null;
        //-- a new distinct map for each layer --//
        headProperties = new TreeMap<>();
        write(image, headProperties, param, ifdPosition);
    }

    /**
     * {@inheritDoc }
     *
     * @param image image which will be written.
     * @throws IOException if problem during writing.
     * @throws NullArgumentException if image is null.
     */
    @Override
    public void write(final RenderedImage image) throws IOException {
        ArgumentChecks.ensureNonNull("RenderedImage image", image);
        assert headProperties == null;
        //-- a new distinct map for each layer --//
        headProperties = new TreeMap<>();
        write(image, headProperties, null, ifdPosition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialImageWriteParam getDefaultWriteParam() {
        return new TiffImageWriteParam(this);
    }

    @Override
    public boolean canReplacePixels(int imageIndex) throws IOException {
        // TODO : Check if image at given index is compliant with the method.
        return true;
    }

    /**
     * {@inheritDoc }
     *
     * If param is {@code null} a default tiling mode 256 x 256 is choosen.
     */
    @Override
    public void prepareWriteEmpty(IIOMetadata streamMetadata, ImageTypeSpecifier imageType, int width, int height, IIOMetadata imageMetadata, List<? extends BufferedImage> thumbnails, ImageWriteParam param) throws IOException {
        ArgumentChecks.ensureNonNull("imageType", imageType);
        if (param == null) {
            param = getDefaultWriteParam();
        }
        // TODO : Remove test when tiles won't be necessary.
        if (param.getTilingMode() != ImageWriteParam.MODE_EXPLICIT || param.getTileWidth() <= 0 || param.getTileHeight() <= 0) {
            //-- with prepare write empty force writing by tile 256 x 256--//
            param.setTilingMode(ImageWriteParam.MODE_EXPLICIT);
            param.setTiling(256, 256, 0, 0);
        }
        // avant d'appeler open definir sil sagit d'une bigtiff

        // 1 : isBigTiff
        final SampleModel sm = imageType.getSampleModel();
        final int[] sampleSize = sm.getSampleSize();
        int pixelSize = 0;
        for (int i = 0; i < sampleSize.length; i++) {
            pixelSize += sampleSize[i];
        }
        isBigTIFF = ((long) width * height * pixelSize / Byte.SIZE) >= 1E9; //-- >= 1Go

        if (channel != null) {
            //-- We authorize to write none big tiff image after big tiff already written but not the inverse --//
//            if (isBigTIFF != isBigTiff(image)) {
//                if (!isBigTIFF)
//                throw new IllegalArgumentException("You can't write a bigtiff image when you have already written none bigtiff image.");
//            }
//
            //-- define if nextPositionIFD has already been set --//
            if (ifdPosition[1] > 0) {
                /*
                 * If an image has already been written we stipulate next ifd position.
                 */
                final long offset = (channel.getStreamPosition());
                channel.seek(ifdPosition[1]);

                if (isBigTIFF) channel.writeLong(offset);
                else channel.writeInt((int)offset);

                channel.seek(offset);
                ifdPosition[0] = offset;
            }
        } else {
            if (isBigTIFF) {
                currentSizeEntry     = SIZE_BIG_ENTRY;
                currentSizeTagNumber = Long.SIZE / Byte.SIZE; // long
                currentSizeNextIFD   = Long.SIZE / Byte.SIZE; // long
            } else {
                currentSizeEntry     = SIZE_ENTRY;
                currentSizeTagNumber = Short.SIZE / Byte.SIZE; // short
                currentSizeNextIFD   = Integer.SIZE / Byte.SIZE; // int
            }
        }

        //-- open
        open(ifdPosition);

        assert headProperties == null;
        //-- a new distinct map for each layer --//
        headProperties = new TreeMap<>();

        //-- metadatas study --//
        //-- get metadata from stream --//
        if (streamMetadata != null) {
            final IIOMetadata strMetadata = toGTiffFormat(streamMetadata);
            final Node iioNode    = strMetadata.getAsTree(strMetadata.getNativeMetadataFormatName());
            addMetadataProperties(iioNode, headProperties);
        }

        //-- get metadata from image --//
        if (imageMetadata != null) {
            final IIOMetadata imgMetadata = toGTiffFormat(streamMetadata);
            final Node iioNode    = imgMetadata.getAsTree(imgMetadata.getNativeMetadataFormatName());
            addMetadataProperties(iioNode, headProperties);
        }
        //-- end metadatas --//

        //-- image study --//
        addImageProperties(imageType, width, height, headProperties, param);//-- hidden computeregion()

        if (compression != 1) //uncompressed
            throw new IllegalArgumentException("impossible to write empty with compression. Expected compression value 1, found : "+compression);

        //-- write all image properties. --//
        //-- write tiff tags --//
        writeTags(headProperties, ifdPosition);

        //-- ecriture des arrays byte count
        //-- dans un premier temps sous forme de tuiles

        {
            assert currentImgTW != 0;
            assert currentImgTH != 0;

            final int dstNumXT = tileRegion.width / currentImgTW;
            assert tileRegion.width % currentImgTW == 0;

            final int dstNumYT = tileRegion.height / currentImgTH;
            assert tileRegion.height % currentImgTH == 0;

            assert bitPerSample != 0;

            final int numTiles = dstNumXT * dstNumYT;//-- voir pour strip
            final Object byteCountArray;
            final long byteCountArraySize;
            final Object offsetArray;
            final long offsetArraySize;
            final short arrayType = isBigTIFF? TYPE_ULONG : TYPE_UINT;
            if (isBigTIFF) {
                byteCountArray = new long[numTiles];
                offsetArray = new long[numTiles];
            } else {
                byteCountArray = new int[numTiles];
                offsetArray = new int[numTiles];
            }
            byteCountArraySize = offsetArraySize = numTiles * TYPE_SIZE[arrayType];
            final long buffPos = channel.getStreamPosition();

            //-- to know if bytecountArray will be written deffered
            final int datasize = (isBigTIFF) ? Long.SIZE / Byte.SIZE : Integer.SIZE / Byte.SIZE;
            final long destByteCountArraySize = (byteCountArraySize > datasize) ? byteCountArraySize : 0;
            final long destOffsetArraySize    = (offsetArraySize    > datasize) ? offsetArraySize    : 0;

            //-- write current tile byte position --//
            final long currentByteCount = (long) currentImgTW * currentImgTH * pixelSize / Byte.SIZE;
            final long tileOffsetBeg   = buffPos + destByteCountArraySize + destOffsetArraySize;//-- position in bytes
            long currentoffset         = tileOffsetBeg;

            // fill byte count array
            if (isBigTIFF) {
                Arrays.fill((long[]) byteCountArray, currentByteCount);
                for (int i = 0; i < numTiles; i++) {
                    Array.setLong(offsetArray, i, currentoffset);
                    currentoffset += currentByteCount;
                }
            } else {
                Arrays.fill((int[]) byteCountArray, (int) currentByteCount);
                for (int i = 0; i < numTiles; i++) {
                    Array.setInt(offsetArray, i, (int) currentoffset);
                    currentoffset += currentByteCount;
                }
            }
            endOfFile = currentoffset;
            assert endOfFile == tileOffsetBeg + numTiles * currentByteCount;
            writeByteCountAndOffsets(byteCountTagPosition, arrayType, byteCountArray, offsetTagPosition, arrayType, offsetArray);
            //-- add current offset array in current headProperties --//
            addProperty(TileOffsets, (isBigTIFF) ? TYPE_ULONG : TYPE_UINT, Array.getLength(offsetArray), offsetArray, headProperties);
            assert tileOffsetBeg == channel.getStreamPosition() : "expected : "+tileOffsetBeg+". found : "+channel.getStreamPosition();
            if (metaIndex == metaHeads.length) {
                metaHeads = Arrays.copyOf(metaHeads, metaHeads.length << 1);
            }
            metaHeads[metaIndex++] = headProperties;
            this.headProperties = null;
        }

        // 1 : etude du image type specifier
        // 2 : garder width and height
        // 3 : etude du compute region en fonction de width heigth et param
        // 4 : injecter les metadatas
        // 5 : garder les thumbnails pour les ecrires au endwriteempty
        // 6 : lever exception si != uncompressed
//        super.prepareWriteEmpty(streamMetadata, imageType, width, height, imageMetadata, thumbnails, param); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void endWriteEmpty() throws IOException {
        //-- ecrire les bytecounts
        //-- ecrire les thumbnails
        if (!endOfFileReached) {
            channel.seek(endOfFile - 1);
            channel.writeByte(0);
        }
        endOfFileReached = false;
        endOfFile = -1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void endReplacePixels() throws IOException {
        // Nothing to do
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void prepareReplacePixels(int imageIndex, Rectangle region) throws IOException {
        selectLayer(imageIndex);
        if (headProperties.isEmpty()) {
            throw new IllegalStateException("Image at image index : "+imageIndex+" does not exist.");
            //-- si le fichier vers lequel on pointe est deja remplis signifie qu'il ya deja deja une image ecrite --//
            //-- 1 : ouvrir un reader
            //-- 2 : choisir le "layer" (dans le reader approprié)
            //-- 3 : recuperer le headProperties et initializer les variables utiles
        }

         //-- rectangle servira au intersection des coord image replace pixels
        if (region == null) {
            dstRepRegion = destRegion;
        } else {
            if (!destRegion.intersects(region))
                throw new IllegalArgumentException("The specified region : "+region.toString()+" doesn't intersect region from image at index : "+imageIndex);
            dstRepRegion = destRegion.intersection(region);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
     public void replacePixels(RenderedImage image, ImageWriteParam param) throws IOException {
        if (dstRepRegion == null)
            throw new IllegalStateException("before replace pixel you must call prepareReplacePixels(int imageIndex, Rectangle region) method.");

        //-- write image raster(s) datas --//
        replacePixelsByTiles(image, param);
    }

    /**
     * Try to translate metadata from image into Tiff metadata if its possible.
     * Temporary method in attempt to refactor metadata using.
     *
     * @param image
     * @param param
     * @throws IOException
     */
    private void adaptMetadata(final IIOImage image, ImageWriteParam param) throws IOException {

        IIOMetadata iioImgMetadata = image.getMetadata();

        if (!(iioImgMetadata instanceof SpatialMetadata)) return;

        final String tiffFormatName = getOriginatingProvider().getNativeImageMetadataFormatName();

        final String[] formatNames = iioImgMetadata.getMetadataFormatNames();
        if (!ArraysExt.contains(formatNames, tiffFormatName) &&
                !ArraysExt.contains(formatNames, SpatialMetadataFormat.GEOTK_FORMAT_NAME)) {
            //no tiff metadata, create one
            final ImageTypeSpecifier spec = ImageTypeSpecifier.createFromRenderedImage(image.getRenderedImage());
            iioImgMetadata = getDefaultImageMetadata(spec, param);
            iioImgMetadata = convertImageMetadata(iioImgMetadata, spec, param);
            image.setMetadata(iioImgMetadata);
        }

        image.setMetadata(toGTiffFormat(iioImgMetadata));
    }

    /**
     * Try to translate metadata into Tiff metadata format if its possible.
     * Temporary method in attempt to refactor metadata using.
     *
     * @param metadata
     * @return converted metadata (native GeoTiff metadata format).
     * @throws IOException If we cannot retrieve and convert Geo-spatial information from source metadata.
     */
    private IIOMetadata toGTiffFormat(IIOMetadata metadata) throws IOException {
        // Not a Geotk MD. It must be in tiff format already.
        if (!(metadata instanceof SpatialMetadata)) return metadata;

        final String tiffFormatName = getOriginatingProvider().getNativeImageMetadataFormatName();
        final String[] formatNames = metadata.getMetadataFormatNames();
        Node tiffTree = null;

        if (ArraysExt.contains(formatNames, SpatialMetadataFormat.GEOTK_FORMAT_NAME)) {
            tiffTree = metadata.getAsTree(SpatialMetadataFormat.GEOTK_FORMAT_NAME);
        }

        if (tiffTree != null) {
            final GeoTiffMetaDataWriter writer = new GeoTiffMetaDataWriter();
            try {
                writer.fillMetadata(tiffTree, (SpatialMetadata) metadata);
            } catch (FactoryException ex) {
                throw new IOException(ex);
            }

            //the tree changes are not stored in the model, imageio bug ?
            //I didn't found a way to update the tree so I recreate the iiometadata.
            metadata = new TIFFImageMetadata(
                    TIFFImageMetadata.parseIFD(DomUtilities.getNodeByLocalName(tiffTree, TAG_GEOTIFF_IFD)));
        }

        return metadata;
    }

    /**
     * Write {@link RenderedImage} and its properties (metadata, thumbnails, tags ...).
     *
     * @param image {@link RenderedImage} which will be write.
     * @param headProperties image properties. (All needed tag information are set in this Map).
     * @param param properties to write image or null.
     * @param ifdPosition table of length 2 where ifdPosition[0] contain chanel position of current image datas beginning
     *                    and ifdPosition[1] contain chanel position where to write the nextIFD offset.
     * @throws IOException if problem during writing.
     * @throws IllegalArgumentException if you try to write a bigTiff image when you have already written a none bigTiff image.
     * @see #writeTags(java.util.Map, long[]) ifdPosition table use.
     */
    private void write(final RenderedImage image, final Map<Integer, Map> headProperties,
                       final ImageWriteParam param, final long[] ifdPosition) throws IOException {
        if (channel != null) {
            //-- We authorize to write none big tiff image after big tiff already writen but not the inverse --//
            if (isBigTIFF != isBigTiff(image)) {
                if (!isBigTIFF)
                throw new IllegalArgumentException("You can't write a bigtiff image when you have already writen none bigtiff image.");
            }

            //-- define if nextPositionIFD has already been setted --//
            if (ifdPosition[1] > 0) {
                /*
                 * If an image has already been writen we stipulate next ifd position.
                 */
                final int offset = (int) (channel.getStreamPosition());
                channel.seek(ifdPosition[1]);

                if (isBigTIFF) channel.writeLong(offset);
                else channel.writeInt(offset);

                channel.seek(offset);
                ifdPosition[0] = offset;
            }
        } else {
            isBigTIFF = isBigTiff(image);
            if (isBigTIFF) {
                currentSizeEntry     = SIZE_BIG_ENTRY;
                currentSizeTagNumber = Long.SIZE / Byte.SIZE; // long
                currentSizeNextIFD   = Long.SIZE / Byte.SIZE; // long
            } else {
                currentSizeEntry     = SIZE_ENTRY;
                currentSizeTagNumber = Short.SIZE / Byte.SIZE; // short
                currentSizeNextIFD   = Integer.SIZE / Byte.SIZE; // int
            }
        }
        /*
         * Open channel if its necessary else do nothing.
         */
        open(ifdPosition);

        //-- add image properties in a Map in attempt to writing. --//
        addImageProperties(image, headProperties, param);

        //-- write all image properties. --//
        //-- write tiff tags --//
        writeTags(headProperties, ifdPosition);
        //-- write image raster(s) data --//
        writeImage(image, headProperties, param);
        if (metaIndex == metaHeads.length) {
            metaHeads = Arrays.copyOf(metaHeads, metaHeads.length << 1);
        }
        metaHeads[metaIndex++] = headProperties;
        this.headProperties = null;
    }

    /**
     * Check tagsProperties {@link Map} and define if image will be written by stripOffsets or by tiles.
     *
     * @param image {@link RenderedImage} which will be write.
     * @param tagsProperties image properties. (All needed tag information are set in this Map).
     * @param param properties to write image or null.
     * @throws IOException if problem during writing.
     */
    private void writeImage(final RenderedImage image, final Map<Integer, Map> tagsProperties,  final ImageWriteParam param) throws IOException {

        //-- search strip or tile properties --//
        for (int tag : tagsProperties.keySet()) {
            if (tag == TileByteCounts || tag == TileLength || tag == TileWidth || tag == TileOffsets) {
                writeImageByTiles(image, param);
                return;
            }
            if (tag == StripByteCounts || tag == StripOffsets) {
                writeImageByStrips(image, param);
                return;
            }
        }
        //-- should never append --//
        throw new IllegalStateException("impossible to write image. Tile properties or strip properties don't exists.");
    }

    /**
     * Travel down metadata tree and store metadata properties in {@link Map} properties.
     *
     * @param iioRootNode metadata root node.
     * @param properties Map where metadata properties will be stored.
     * @throws NullPointerException if iioRootNode is {@code null}.
     * @throws IllegalStateException if {@link Node} is not a Node adapted to tiff implementation.
     */
    final void addMetadataProperties(final Node iioRootNode, final Map properties) {
        ArgumentChecks.ensureNonNull("root metadata Node", iioRootNode);
        Element tmpIfd = (Element) getNodeByLocalName(iioRootNode, TAG_GEOTIFF_IFD);
        if (tmpIfd == null) {
            throw new IllegalStateException("Given node does not contains the Tiff root node : \""+TAG_GEOTIFF_IFD+"\"");
        }

        final NodeList iioNodeList = tmpIfd.getChildNodes();
        final int iioLength        = iioNodeList.getLength();
        for (int i = 0; i < iioLength; i++) {
            addNodeProperties(iioNodeList.item(i), properties);
        }
    }

    /**
     * Study current metadata node from {@link IIOMetadata} and add properties in the given {@link Map} if its a Tiff field node.
     *
     * @param tagBody metadata node.
     * @param properties {@link Map} to store all source image properties.
     * @throws IllegalStateException if {@link Node} is not a Node adapted to tiff implementation.
     */
    final void addNodeProperties(final Node tagBody, final Map properties) {
        if (!tagBody.getLocalName().equalsIgnoreCase(TAG_GEOTIFF_FIELD)) {
            throw new IllegalStateException("Internal metadata node should have name : \""+TAG_GEOTIFF_FIELD+"\"");
        }
        //-- get attributes --//
        final NamedNodeMap tagNamesMap = tagBody.getAttributes();
        //-- tag value --//
        final int tagnumber = Integer.decode(tagNamesMap.getNamedItem(ATT_NUMBER).getNodeValue());
        short type   = -1;
        int count    = -1;
        Object value = null;
        final NodeList iioNodeList = tagBody.getChildNodes();
        //-- one node per value --//
        final int iioLength = iioNodeList.getLength();
        assert iioLength == 1;

        for (int i = 0; i < iioLength; i++) {

            final Node iioChild = iioNodeList.item(i);
            final String iioNodeName = iioChild.getLocalName();
            final NodeList iioChildList = iioChild.getChildNodes();
            final int length = iioChildList.getLength();

            if (iioNodeName.equalsIgnoreCase(TAG_GEOTIFF_ASCIIS)) {
                final byte[] asciiValue;
                if (length == 1) {
                    type = TYPE_ASCII;
                    final Node iioNodeAscii = iioChildList.item(0);
                    assert iioNodeAscii.getLocalName().equalsIgnoreCase(TAG_GEOTIFF_ASCII) : "Internal child Node from Asciis parent must have Ascii local name.";
                    final NamedNodeMap asciiMap = iioNodeAscii.getAttributes();
                    final String asciis = asciiMap.getNamedItem(ATT_VALUE).getNodeValue();
                    asciiValue = asciis.getBytes();
                } else {
                    type = TYPE_BYTE;
                    asciiValue = new byte[length];
                    int asciiPos = 0;
                    for (int idc = 0; idc < length; idc++) {
                        final Node iioNodeAscii = iioChildList.item(idc);
                        assert iioNodeAscii.getLocalName().equalsIgnoreCase(TAG_GEOTIFF_ASCII) : "Internal child Node from Asciis parent must have Ascii local name.";
                        final NamedNodeMap asciiMap = iioNodeAscii.getAttributes();
                        final String asciis = asciiMap.getNamedItem(ATT_VALUE).getNodeValue();
                        final byte[] asciiByte = asciis.getBytes();
                        assert asciiByte.length == 1;
                        asciiValue[asciiPos++] = asciiByte[0];
                    }
                }
                count = asciiValue.length;
                value = asciiValue;

            } else if (iioNodeName.equalsIgnoreCase(TAG_GEOTIFF_SHORTS)) {
                type = TYPE_USHORT;
                count = length;
                final short[] shortValue = new short[length];
                int shortPos = 0;
                for (int idc = 0; idc < length; idc++) {
                    final Node iioNodeShort = iioChildList.item(idc);
                    assert iioNodeShort.getLocalName().equalsIgnoreCase(TAG_GEOTIFF_SHORT) : "Internal child Node from Short parent must have Short local name.";
                    final NamedNodeMap shortMap = iioNodeShort.getAttributes();
                    final String s = shortMap.getNamedItem(ATT_VALUE).getNodeValue();
                    shortValue[shortPos++] = Integer.valueOf(s).shortValue();
                }
                value = shortValue;
            } else if (iioNodeName.equalsIgnoreCase(TAG_GEOTIFF_LONGS)) {
                type = TYPE_LONG;
                count = length;
                final long[] longValue = new long[length];
                int longPos = 0;
                for (int idc = 0; idc < length; idc++) {
                    final Node iioNodeLong = iioChildList.item(idc);
                    assert iioNodeLong.getLocalName().equalsIgnoreCase(TAG_GEOTIFF_LONG) : "Internal child Node from Long parent must have Long local name.";
                    final NamedNodeMap longMap = iioNodeLong.getAttributes();
                    final String l = longMap.getNamedItem(ATT_VALUE).getNodeValue();
                    longValue[longPos++] = Long.decode(l);
                }
                value = longValue;
            } else if (iioNodeName.equalsIgnoreCase(TAG_GEOTIFF_DOUBLES)) {
                type = TYPE_DOUBLE;
                count = length;
                final double[] doubleValue = new double[length];
                int doublePos = 0;
                for (int idc = 0; idc < length; idc++) {
                    final Node iioNodedouble = iioChildList.item(idc);
                    assert iioNodedouble.getLocalName().equalsIgnoreCase(TAG_GEOTIFF_DOUBLE) : "Internal child Node from Double parent must have Double local name.";
                    final NamedNodeMap doubleMap = iioNodedouble.getAttributes();
                    final String d = doubleMap.getNamedItem(ATT_VALUE).getNodeValue();
                    doubleValue[doublePos++] = Double.valueOf(d);
                }
                value = doubleValue;
            } else {
                //-- unknown tag do nothing
            }
        }
        if (count > 0) { // -- to avoid unknown tag writing --//
            addProperty(tagnumber, type, count, value, properties);
        }
    }

    /**
     * Return {@code true} if image as a weight over than 4 Go else {@code false}.<br/>
     * In tiff specification an image is considered as a BigTiff when its weight exceed 4Go.
     *
     * @param image source image which will be written.
     * @return {@code true} if image is a {@code BigTiff} else {@code false}.
     */
    private boolean isBigTiff(final RenderedImage image) {
        final int imgWidth     = image.getWidth();
        final int imgHeight    = image.getHeight();
        final SampleModel sm   = image.getSampleModel();
        final int[] sampleSize = sm.getSampleSize();
        int pixelSize = 0;
        for (int i = 0; i < sampleSize.length; i++) {
            pixelSize += sampleSize[i];
        }
        final long imageWeight = (long) imgWidth * imgHeight * pixelSize / Byte.SIZE;
        return imageWeight >= 1E9;// >= 1Go
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canWriteEmpty() throws IOException {
        return true;
    }

    /**
     *
     * @param imageType
     * @param width
     * @param height
     * @param properties
     * @param param
     */
    private void addImageProperties(final ImageTypeSpecifier imageType, final int width, final int height, final Map properties, final ImageWriteParam param) {
        /*
         * Define some attributes in relation with ImageWriterParam and image properties.
         */
        computeRegions(width, height, 0, 0, 1, 1, width, height, param);// pour le moment 0,0,1,1 a voir pour plus tard en fonction des besoins
        //imagewidth
        if (width > 0xFFFF) { // check if width is a short or int
            addProperty(ImageWidth, TYPE_INT, 1, new int[]{ width}, properties);
        } else {
            addProperty(ImageWidth, TYPE_USHORT, 1, new short[]{(short) width}, properties);
        }

        //imageheight (length)
        if (height > 0xFFFF) {
            addProperty(ImageLength, TYPE_INT, 1, new int[]{height}, properties);
        } else {
            addProperty(ImageLength, TYPE_USHORT, 1, new short[]{(short) height}, properties);
        }

        final SampleModel sm     = imageType.getSampleModel();
        //sample per pixel
        samplePerPixel = (short) sm.getNumDataElements();
        assert samplePerPixel <= 0xFFFF : "SamplePerPixel exceed short max value"; // -- should never append
        addProperty(SamplesPerPixel, TYPE_USHORT, 1, new short[]{samplePerPixel}, properties);

        // bitpersamples
        final int[] sampleSize = sm.getSampleSize();// sample size in bits
        assert samplePerPixel == sampleSize.length : "";
        for (int i = 1; i < samplePerPixel; i++) {
            if (sampleSize[i-1] != sampleSize[i]) {
                throw new IllegalStateException("different sample size is not supported in tiff format.");
            }
        }
        assert sampleSize[0] <= 0xFFFF : "BitsPerSample exceed short max value";
        bitPerSample = (short) sampleSize[0];
        short[] bitspersample = new short[sampleSize.length];
        Arrays.fill(bitspersample, bitPerSample);
        addProperty(BitsPerSample, TYPE_USHORT, sampleSize.length, bitspersample, properties);

        //-- sample format --//
        if (bitPerSample == Float.SIZE || bitPerSample == Double.SIZE) {
            final int dataType = sm.getDataType();
            short sampleFormat = 0;
            switch (dataType) {
                case DataBuffer.TYPE_FLOAT  :
                case DataBuffer.TYPE_DOUBLE : {
                    sampleFormat = 3; //-- type floating point --//
                    break;
                }
                case DataBuffer.TYPE_INT : {
                    sampleFormat = 2; //-- type 32 bits Int --//
                    break;
                }
                default : {
                    assert bitPerSample == Long.SIZE : "Define sample format : expected bitpersample equals to Long.SIZE = 64. Found : "+Arrays.toString(bitspersample);
                    sampleFormat = 1; //-- type UInt --//
                    break;
                }
            }
            //-- add sample format tag --//
            if (sampleFormat != 0) {
                final short[] samplForm = new short[sampleSize.length];
                Arrays.fill(samplForm, sampleFormat);
                addProperty(SampleFormat, TYPE_USHORT, sampleSize.length, samplForm, properties);
            }
        }

        final ColorModel colorMod = imageType.getColorModel();

        //photometric interpretation
        final short photoInter = getPhotometricInterpretation(colorMod);
        addProperty(PhotometricInterpretation, TYPE_USHORT, 1, new short[]{ photoInter}, properties);

        // color map
        if (photoInter == 3) {
            // on construit un color map adequate
            IndexColorModel indexColorMod = ((IndexColorModel)colorMod);
            int mapSize = indexColorMod.getMapSize();
            int[] rgbs  = new int[mapSize];
            indexColorMod.getRGBs(rgbs);
            final short[] tiffMap = buildTiffMapArray(rgbs);
            addProperty(ColorMap, TYPE_USHORT, tiffMap.length, tiffMap, properties);
        }

        //-- compression --//
        compression = 1;
        if (param.canWriteCompressed() && param.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT) {
            final String comp = param.getCompressionType();
            if (comp != null) {
                if (lzw.equalsIgnoreCase(comp)) {
                    compression = 5;
                } else if (packbits.equalsIgnoreCase(comp)) {
                    compression = 32773;
                } else {
                    throw new IllegalStateException("the compression type : "+comp+". Is not known. Impossible to write image.");
                }
            }
        }
        assert compression <= 0xFFFF : "compression exceed short max value";
        addProperty(Compression, TYPE_USHORT, 1, new short[]{(short) compression}, properties);

        //-- planar configuration
        final short planarConfig = getPlanarConfiguration(sm);
        addProperty(PlanarConfiguration, TYPE_USHORT, 1, new short[]{planarConfig}, properties);

        /*
         * Some globals class attribut have been already initialized to define writing made.
         * See method computeRegion.
         */
        if (currentImgTW != 0 && currentImgTH != 0) {
            addTileOffsetsProperties(currentImgTW, currentImgTH, currentImgNumXT, currentImgNumYT, properties);
        } else {
            addStripOffsetProperties(destRegion.height, properties);
        }
    }

    /**
     * Analyze image and get all properties at minimum, necessary to write an image in tiff format.
     *
     * @param image source image which will be written.
     * @param properties {@link Map} to store all source image properties.
     * @param param Image parameter to define written area and subsampling if exists else {@code null}.
     */
    private void addImageProperties(final RenderedImage image, final Map properties, final ImageWriteParam param) {

        /*
         * Define some attributs in relation with ImageWriterParam and image properties.
         */
        computeRegions(image, param);

        //imagewidth
        final int width          = destRegion.width;
        if (width > 0xFFFF) { // check if width is a short or int
            addProperty(ImageWidth, TYPE_INT, 1, new int[]{width}, properties);
        } else {
            addProperty(ImageWidth, TYPE_USHORT, 1, new short[]{(short) width}, properties);
        }

        //imageheight (length)
        final int height          = destRegion.height;
        if (height > 0xFFFF) {
            addProperty(ImageLength, TYPE_INT, 1, new int[]{height}, properties);
        } else {
            addProperty(ImageLength, TYPE_USHORT, 1, new short[]{(short) height}, properties);
        }

        final SampleModel sm     = image.getSampleModel();
        //sample per pixel
        samplePerPixel = (short) sm.getNumDataElements();
        assert samplePerPixel <= 0xFFFF : "SamplePerPixel exceed short max value"; //-- should never append
        addProperty(SamplesPerPixel, TYPE_USHORT, 1, new short[]{(short) samplePerPixel}, properties);

        //-- planar configuration
        final short planarConfig = getPlanarConfiguration(sm);
        addProperty(PlanarConfiguration, TYPE_USHORT, 1, new short[]{planarConfig}, properties);

        // bitpersamples
        final int[] sampleSize = sm.getSampleSize();// sample size in bits
        assert samplePerPixel == sampleSize.length : "";
        for (int i = 1; i < samplePerPixel; i++) {
            if (sampleSize[i-1] != sampleSize[i]) {
                throw new IllegalStateException("different sample size is not supported in tiff format.");
            }
        }
        assert sampleSize[0] <= 0xFFFF : "BitsPerSample exceed short max value";
        bitPerSample = (short) sampleSize[0];
        short[] bitspersample = new short[sampleSize.length];
        Arrays.fill(bitspersample, bitPerSample);
        addProperty(BitsPerSample, TYPE_USHORT, sampleSize.length, bitspersample, properties);

        //-- sample format --//
        if (bitPerSample >= Short.SIZE) {
            final int dataType = sm.getDataType();
            short sampleFormat = 0;
            switch (dataType) {
                case DataBuffer.TYPE_FLOAT  :
                case DataBuffer.TYPE_DOUBLE : {
                    sampleFormat = 3; //-- type floating point --//
                    break;
                }
                case DataBuffer.TYPE_SHORT :
                case DataBuffer.TYPE_INT   : {
                    sampleFormat = 2; //-- type 32 bits Int --//
                    break;
                }
                default : {
                    assert bitPerSample == Long.SIZE || bitPerSample == Short.SIZE : "Define sample format : expected bitpersample equals to Long.SIZE = 64 or UShort.SIZE = 16. Found : "+Arrays.toString(bitspersample);
                    sampleFormat = 1; //-- type UInt or UShort--//
                    break;
                }
            }
            //-- add sample format tag --//
            if (sampleFormat != 0) {
                final short[] samplForm = new short[sampleSize.length];
                Arrays.fill(samplForm, sampleFormat);
                addProperty(SampleFormat, TYPE_USHORT, sampleSize.length, samplForm, properties);
            }
        }

        final ColorModel colorMod = image.getColorModel();

        //photometric interpretation
        final short photoInter = getPhotometricInterpretation(colorMod);
        addProperty(PhotometricInterpretation, TYPE_USHORT, 1, new short[]{ photoInter}, properties);

        // color map
        if (photoInter == 3) {
            // on construit un color map adequate
            IndexColorModel indexColorMod = ((IndexColorModel)colorMod);
            int mapSize = indexColorMod.getMapSize();
            int[] rgbs  = new int[mapSize];
            indexColorMod.getRGBs(rgbs);
            final short[] tiffMap = buildTiffMapArray(rgbs);
            addProperty(ColorMap, TYPE_USHORT, tiffMap.length, tiffMap, properties);
        }

        //-- compression --//
        compression = 1;
        if (param != null && param.canWriteCompressed() && param.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT) {
            final String comp = param.getCompressionType();
            if (comp != null) {
                if (lzw.equalsIgnoreCase(comp)) {
                    compression = 5;
                } else if (packbits.equalsIgnoreCase(comp)) {
                    compression = 32773;
                } else {
                    throw new IllegalStateException("the compression type : "+comp+". Is not known. Impossible to write image.");
                }
            }
        }
        assert compression <= 0xFFFF : "compression exceed short max value";
        addProperty(Compression, TYPE_USHORT, 1, new short[]{(short) compression}, properties);

        /*
         * Some globals class attribut have been already initialized to define writing made.
         * See method computeRegion.
         */
        if (currentImgTW != 0 && currentImgTH != 0) {
            addTileOffsetsProperties(currentImgTW, currentImgTH, currentImgNumXT, currentImgNumYT, properties);
        } else {
            addStripOffsetProperties((planarConfig == 2 ) ? destRegion.height * sm.getNumBands() : destRegion.height, properties);
        }
    }

    /**
     * Add appropriate tag in {@link #headProperties} adapted to strip offset writing of current image.<br/>
     * Note : in this current implementation we admit one row for one strip.
     *
     * @param height height of current image.
     * @param properties {@link Map} which contain all properties of current image.
     */
    private void addStripOffsetProperties(final int height, final Map properties) {

        // row per strip
        final short rowperstrip = 1;
        addProperty(RowsPerStrip, TYPE_USHORT, 1, new short[]{rowperstrip}, properties);

        final short arrayType = (isBigTIFF) ? TYPE_ULONG : TYPE_UINT;
        addProperty(StripByteCounts, arrayType, height, null, properties);
        addProperty(StripOffsets, arrayType, height, null, properties);
    }

    /**
     * Write {@link RenderedImage} by tile with all tile properties previously set by {@link ImageWriteParam}.
     *
     * @param image source image which will be written.
     * @param param Image parameter to define written area and subsampling if exists else {@code null}.
     * @throws IOException if problem during buffer writing action or unsupported ata type.
     */
     private void replacePixelsByTiles(final RenderedImage image, final ImageWriteParam param) throws IOException {
        ArgumentChecks.ensureNonNull("image", image);
        ArgumentChecks.ensureNonNull("param", param);
        final int subsampleX = param.getSourceXSubsampling();
        final int subsampleY = param.getSourceYSubsampling();
        if (subsampleX > 1 || subsampleY > 1)
            throw new IllegalStateException("replace pixel with subsampling is not supported yet.");

        // ------------ Image properties ------------//
        final int imageMinX = image.getMinX();
        final int imageMinY = image.getMinY();

        final int imageTileWidth = image.getTileWidth();
        final int imageTileHeight = image.getTileHeight();

        final int imageTileGridXOffset    = image.getTileGridXOffset();
        final int imageMaxTileGridXOffset = imageTileGridXOffset + image.getNumXTiles();
        final int imageTileGridYOffset    = image.getTileGridYOffset();
        final int imageMaxTileGridYOffset = imageTileGridYOffset + image.getNumYTiles();

        final SampleModel sm       = image.getSampleModel();
        final int[] sampleSizes = sm.getSampleSize();
        assert sampleSizes.length == sm.getNumDataElements();

        final int imagePixelStride = sampleSizes.length;
        if (imagePixelStride != samplePerPixel)
            throw new IllegalArgumentException("impossible to replace pixel with different sample number by pixel. Expected : "+samplePerPixel+" found : "+imagePixelStride);

        //-- planar configuration study
        final short imgPC = getPlanarConfiguration(sm);

        final Map<String, Object> pcObj = headProperties.get(PlanarConfiguration);
        short srcPC = 1;
        if (pcObj != null) srcPC = ((short[]) pcObj.get(ATT_VALUE))[0];



        if (srcPC != imgPC) {
            final String src = (srcPC == 1) ? "interleaved" : "banded";
            final String img = (imgPC == 1) ? "interleaved" : "banded";
            throw new IllegalArgumentException("Already written image and current image use to replace pixel should have Sample model of same type."
                    + " SampleModel of Image which already written is type : "+src+". Current image sample model is type : "+img);
        }

        final int pixelLength, planarDenum;
        if (srcPC == 1) {
            //-- interleaved
            pixelLength = imagePixelStride;
            planarDenum = 1;
        } else {
            //-- banded
            pixelLength = 1;
            planarDenum = imagePixelStride;
        }

        for (int s : sampleSizes) {
            if (s != bitPerSample)
                throw new IllegalArgumentException("impossible to replace pixel with different sample size. Expected : "+bitPerSample+" bits by sample, found : "+s+" bits.");
        }
        final int sampleSize = bitPerSample / Byte.SIZE;

        final int cellWidth, cellHeight;
        if (currentImgTW > 0 && currentImgTH > 0) {
            cellWidth  = currentImgTW;
            cellHeight = currentImgTH;
        } else {
            cellWidth = tileRegion.width;
            assert rowsPerStrip > 0;
            cellHeight = rowsPerStrip;
        }

        assert tileRegion.width % cellWidth == 0;
        final int dstNumXT = tileRegion.width / cellWidth;

        //-- definir intersection entre le rectangle de prepareReplacePixel et la zone representée par l'image
        //-- image coordinates
        final int imgMinX = param.getDestinationOffset().x;
        final int imgMinY = param.getDestinationOffset().y;
        final int imgMaxX = imgMinX + image.getWidth();// voir ici avec le param pour voir setSource region
        final int imgMaxY = imgMinY + image.getHeight();

        //-- tile region coordinate  --//
        final int trMinX = tileRegion.x;
        final int trMinY = tileRegion.y;
        final int trMaxX = trMinX + tileRegion.width;
        final int trMaxY = trMinY + tileRegion.height;

        //-- intersection between image replace pixel area --//
        final int imgInterRepMinX = Math.max(dstRepRegion.x, imgMinX);
        final int imgInterRepMaxX = Math.min(dstRepRegion.x + dstRepRegion.width, imgMaxX);
        final int imgInterRepMinY = Math.max(dstRepRegion.y, imgMinY);
        final int imgInterRepMaxY = Math.min(dstRepRegion.y + dstRepRegion.height, imgMaxY);

        //-- if image don't intersect replace pixel region --//
        if (imgInterRepMinX >= imgInterRepMaxX || imgInterRepMinY >= imgInterRepMaxY) return;

        //-- if last byte of image is not reach --//
        if (!endOfFileReached)
            endOfFileReached = (imgInterRepMaxX == trMaxX && imgInterRepMaxY == trMaxY);

        //-- definir index destination des tuiles a parcourir
        final int ctMinX = (imgInterRepMinX - trMinX) / cellWidth;
        final int ctMinY = (imgInterRepMinY - trMinY) / cellHeight;
        final int ctMaxX = (imgInterRepMaxX - trMinX + cellWidth - 1) / cellWidth;
        final int ctMaxY = (imgInterRepMaxY - trMinY + cellHeight - 1) / cellHeight;
        //----------------------------------------------------------------//

        //------------------- raster properties ------------------//
        final Raster initRast         = image.getTile(imageTileGridXOffset, imageTileGridYOffset);
        final DataBuffer initRastBuff = initRast.getDataBuffer();
        final int numbanks = initRastBuff.getNumBanks();
        final int dataType = initRastBuff.getDataType();

        for (int bank = 0; bank < numbanks; bank++) {
            assert srcPC == imgPC;
            for (int cty = ctMinY; cty < ctMaxY; cty++) {//-- pour chaque tuile destination dans l'espace de l'image on determine
                for (int ctx = ctMinX; ctx < ctMaxX; ctx++) {

                    assert channel.getBitOffset() == 0;

                    final long dstTileOffset = (isBigTIFF) ? Array.getLong(replaceOffsetArray, (cty * dstNumXT + ctx) + bank * Array.getLength(replaceOffsetArray) / planarDenum) : (Array.getInt(replaceOffsetArray, (cty * dstNumXT + ctx) + bank * Array.getLength(replaceOffsetArray) / planarDenum) & 0xFFFFFFFFL);

                    //-- compute current destination tile coordinates in image space
                    final int ctRminy = trMinY  + cty * cellHeight;
                    final int ctRmaxy = ctRminy + cellHeight;
                    final int ctRminx = trMinX  + ctx * cellWidth;
                    final int ctRmaxx = ctRminx + cellWidth;

                    //-- define intersection between destination image tiles and intersection replace pixel area
                    final int ctInterMinY = Math.max(imgInterRepMinY, ctRminy);
                    final int ctInterMaxY = Math.min(imgInterRepMaxY, ctRmaxy);
                    final int ctInterMinX = Math.max(imgInterRepMinX, ctRminx);
                    final int ctInterMaxX = Math.min(imgInterRepMaxX, ctRmaxx);

                    //-- we looking for which tiles from image will be used to fill destination tile.
                    //-- define image tile index which will be follow
                    final int imageminTy = imageTileGridYOffset + (ctInterMinY - imgMinY - imageMinY) / imageTileHeight;
                    int imagemaxTy = imageTileGridYOffset + (ctInterMaxY - imgMinY - imageMinY + imageTileHeight - 1) / imageTileHeight;

                    // -- in cause of padding imagemaxTy should exceed max tile grid offset from image.
                    imagemaxTy = Math.min(imagemaxTy, imageMaxTileGridYOffset);

                    final int imageminTx = imageTileGridXOffset + (ctInterMinX - imgMinX - imageMinX) / imageTileWidth;
                    int imagemaxTx = imageTileGridXOffset + (ctInterMaxX - imgMinX - imageMinX + imageTileWidth - 1) / imageTileWidth;

                    // -- in cause of padding imagemaxTx should exceed max tile grid offset from image.
                    imagemaxTx = Math.min(imagemaxTx, imageMaxTileGridXOffset);

                    for (int imgTy = imageminTy; imgTy < imagemaxTy; imgTy++) {//-- pour chaque tuile de l'image source

                        //-- current image tile coordinates in Y direction
                        int cuImgTileMinY = imgMinY + imageMinY + (imageminTy - imageTileGridYOffset) * imageTileHeight;
                        int cuImgTileMaxY = cuImgTileMinY + imageTileHeight;

                        //-- row intersection on y axis in image space
                        final int deby = Math.max(cuImgTileMinY, ctInterMinY);
                        final int endy = Math.min(cuImgTileMaxY, ctInterMaxY);

                        //-- offset in pixels number in Y direction in source image currently tile
                        final int imgStepOffsetY = (deby - cuImgTileMinY) * imageTileWidth;

                        //-- offset in pixels number in Y direction to destination image currently tile
                        final int dstStepOffsetY = (deby - ctRminy) * cellWidth;//-- voir plus tard avec les subsampling

                        for (int y = deby; y < endy; y += subsampleY) {

                            // -- offset in y direction from source image
                            final int imgStepY = (y - deby) * imageTileWidth;

                            //-- offset in y direction to destination image
                            final int dstStepY = (y - deby) * cellWidth;

                            //-- travel tile on X direction --//
                            for (int imgTx = imageminTx; imgTx < imagemaxTx; imgTx++) {
                                // -- current image tile coordinates in X direction
                                int cuImgTileMinX = imgMinX + imageMinX + (imageminTx - imageTileGridXOffset) * imageTileWidth;
                                int cuImgTileMaxX = cuImgTileMinX + imageTileWidth;

                                //-- column intersection on X axis
                                final int debx  = Math.max(cuImgTileMinX, ctInterMinX);
                                final int endx  = Math.min(cuImgTileMaxX, ctInterMaxX);

                                //-- offset in pixels number in Y direction in source image currently tile
                                final int imgStepOffsetX = debx - cuImgTileMinX;

                                //-- offset in pixels number in Y direction to destination image currently tile
                                final int dstStepOffsetX = debx - ctRminx;//-- voir plus tard avec les subsampling

                                //-- channel position at the beginning of writing action in BYTE
                                final long channelPos = /*replacePixelPos + */dstTileOffset + (dstStepOffsetY + dstStepY + dstStepOffsetX) * pixelLength * sampleSize;

                                //-- source image array position in SAMPLE POSITION
                                final int imgArrayPos = (imgStepOffsetY + imgStepY + imgStepOffsetX) * pixelLength;

                                // -- get the following image raster
                                final Raster imageTile        = image.getTile(imgTx, imgTy);
                                final DataBuffer rasterBuffer = imageTile.getDataBuffer();

                                final Object sourceArray;
                                switch (dataType) {
                                    case DataBuffer.TYPE_BYTE:   sourceArray = ((DataBufferByte)   rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_USHORT: sourceArray = ((DataBufferUShort) rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_SHORT:  sourceArray = ((DataBufferShort)  rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_INT:    sourceArray = ((DataBufferInt)    rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_FLOAT:  sourceArray = ((DataBufferFloat)  rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_DOUBLE: sourceArray = ((DataBufferDouble) rasterBuffer).getData(bank); break;
                                    default: throw new AssertionError(dataType);
                                }

                                final int currentXSubSample;
                                final int writeSize;
                                if (subsampleX == 1) {
                                    currentXSubSample = endx - debx;
                                    writeSize         = currentXSubSample * pixelLength; // eh !! oui on ecrit en coord tableau et pas en nbre de byte
                                } else {
                                    currentXSubSample = subsampleX;
                                    writeSize         = pixelLength;
                                }

                                channel.seek(channelPos);
                                for (int x = debx; x < endx; x += currentXSubSample) {
                                    final int writeArrayOffset = imgArrayPos + (x - debx) * pixelLength;
                                    write(sourceArray, dataType, writeArrayOffset, writeSize);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

     /**
     * Write {@link RenderedImage} by tile with all tile properties previously set by {@link ImageWriteParam}.
     *
     * @param image source image which will be written.
     * @param param Image parameter to define written area and subsampling if exists else {@code null}.
     * @throws IOException if problem during buffer writing action or unsupported ata type.
     */
    private void writeImageByTiles(final RenderedImage image, final ImageWriteParam param) throws IOException {

        final int subsampleX;
        final int subsampleY;

        if (param != null) {
            subsampleX = param.getSourceXSubsampling();
            subsampleY = param.getSourceYSubsampling();
        } else {
            subsampleX = subsampleY = 1;
        }

        // ------------ Image properties ------------//
        final int imageMinX = image.getMinX() + imageBoundary.x;
        final int imageMinY = image.getMinY() + imageBoundary.y;

        final int imageTileWidth = image.getTileWidth();
        final int imageTileHeight = image.getTileHeight();

        final int imageTileGridXOffset    = image.getTileGridXOffset();
        final int imageMaxTileGridXOffset = imageTileGridXOffset + image.getNumXTiles();
        final int imageTileGridYOffset    = image.getTileGridYOffset();
        final int imageMaxTileGridYOffset = imageTileGridYOffset + image.getNumYTiles();

        // ----------- Area define by tiles ----------//
        final int minx = tileRegion.x;
        final int miny = tileRegion.y;
        final int maxx = minx + tileRegion.width;
        final int maxy = miny + tileRegion.height;

        final SampleModel sm      = image.getSampleModel();
        final int imgSamplePerPix = sm.getNumDataElements();

        assert currentImgTW != 0;
        assert currentImgTH != 0;

        final int subsampletileWidth  = currentImgTW * subsampleX;
        final int subsampletileHeight = currentImgTH * subsampleY;

        final int currentNumXT = tileRegion.width / subsampletileWidth;
        assert tileRegion.width % subsampletileWidth == 0;

        final int currentNumYT = tileRegion.height / subsampletileHeight;
        assert tileRegion.height % subsampletileHeight == 0;

        final short planarConf = getPlanarConfiguration(sm);
        final int pixelLength = (planarConf == 2) ? 1 : imgSamplePerPix;

        int destTileStride = currentImgTW * currentImgTH * pixelLength;

        assert currentNumXT != 0;
        assert currentNumYT != 0;
        assert bitPerSample != 0;

        int numTiles = currentNumXT * currentNumYT;
        if (planarConf == 2) numTiles *= imgSamplePerPix; //-- band  -> tiles *= numband

        final Object byteCountArray;
        final long byteCountArraySize;
        final Object offsetArray;
        final long offsetArraySize;
        final short arrayType = isBigTIFF ? TYPE_ULONG : TYPE_UINT;
        if (isBigTIFF) {
            byteCountArray = new long[numTiles];
            offsetArray    = new long[numTiles];
        } else {
            byteCountArray = new int[numTiles];
            offsetArray    = new int[numTiles];
        }
        byteCountArraySize = offsetArraySize = numTiles * TYPE_SIZE[arrayType];//-- all byte array size on channel.

        final long buffPos = channel.getStreamPosition();

        //------------------- raster properties ------------------//
        final Raster initRast         = image.getTile(imageTileGridXOffset, imageTileGridYOffset);
        final DataBuffer initRastBuff = initRast.getDataBuffer();
        final int numbanks = initRastBuff.getNumBanks();
        final int dataType = initRastBuff.getDataType();

        final int srcRegionMaxX = srcRegion.x + srcRegion.width;
        final int srcRegionMaxY = srcRegion.y + srcRegion.height;

        // ----------------- padding array --------------------//
        assert tileRegion.width  % subsampleX == 0;
        assert tileRegion.height % subsampleY == 0;
        final Object paddingXArray;
        final Object paddingYArray;
        final Object dstOffYArray;
        int paddingXLength = ((maxx > srcRegionMaxX)  ?  (maxx - srcRegionMaxX) / subsampleX : 0) * pixelLength;
        int paddingYLength = ((maxy > srcRegionMaxY)  ? ((maxy - srcRegionMaxY) / subsampleY) * currentImgTW : 0) * pixelLength;

        final int sampleSize = bitPerSample / Byte.SIZE;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:   {
                paddingXArray = new byte[paddingXLength];
                paddingYArray = new byte[paddingYLength];
                dstOffYArray  = new byte[destTileStride];
                break;
            }
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT: {
                paddingXArray = new short[paddingXLength];
                paddingYArray = new short[paddingYLength];
                dstOffYArray  = new short[destTileStride];
                break;
            }
            case DataBuffer.TYPE_INT : {
                paddingXArray = new int[paddingXLength];
                paddingYArray = new int[paddingYLength];
                dstOffYArray  = new int[destTileStride];
                break;
            }
            case DataBuffer.TYPE_FLOAT:  {
                paddingXArray = new float[paddingXLength];
                paddingYArray = new float[paddingYLength];
                dstOffYArray  = new float[destTileStride];
                break;
            }
            case DataBuffer.TYPE_DOUBLE: {
                paddingXArray = new double[paddingXLength];
                paddingYArray = new double[paddingYLength];
                dstOffYArray  = new double[destTileStride];
                break;
            }
            default: throw new IIOException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE, initRastBuff.getClass()));
        }

        if (image.getNumXTiles()  == currentNumXT
         && image.getNumYTiles()  == currentNumYT
         && image.getTileWidth()  == currentImgTW
         && image.getTileHeight() == currentImgTH
         && compression           == 1 // no compression
         && tileRegion.equals(imageBoundary)) {

            final long currentByteCount = (long) currentImgTW * currentImgTH * bitPerSample *  pixelLength / Byte.SIZE;
            long currentoffset = buffPos + byteCountArraySize + offsetArraySize;

            // fill byte count array
            if (isBigTIFF) {
                Arrays.fill((long[]) byteCountArray, currentByteCount);
                for (int i = 0; i < numTiles; i++) {
                    Array.setLong(offsetArray, i, currentoffset);
                    currentoffset += currentByteCount;
                }
            } else {
                Arrays.fill((int[]) byteCountArray, (int) currentByteCount);
                for (int i = 0; i < numTiles; i++) {
                    Array.setInt(offsetArray, i, (int) currentoffset);
                    currentoffset += currentByteCount;
                }
            }

            writeByteCountAndOffsets(byteCountTagPosition, arrayType, byteCountArray, offsetTagPosition, arrayType, offsetArray);
            //-- add current offset array in current headProperties --//
            addProperty(TileOffsets, arrayType, Array.getLength(offsetArray), offsetArray, headProperties);

            //-- copy raster one by one
            int writeTileLength = currentImgTH * currentImgTW * pixelLength;
            for (int bank = 0; bank < numbanks; bank++) {
                for (int ty = imageTileGridYOffset; ty < imageMaxTileGridYOffset; ty++) {
                    for (int tx = imageTileGridXOffset; tx < imageMaxTileGridXOffset; tx++) {
                        // -- get the following image raster
                        final Raster imageTile        = image.getTile(tx, ty);
                        final DataBuffer rasterBuffer = imageTile.getDataBuffer();

                        final Object sourceArray;
                        switch (dataType) {
                            case DataBuffer.TYPE_BYTE   : {
                                sourceArray = ((DataBufferByte)   rasterBuffer).getData(bank);
                                assert writeTileLength == Array.getLength(sourceArray);
                                channel.write((byte[]) sourceArray);
                            } break;
                            case DataBuffer.TYPE_USHORT : {
                                sourceArray = ((DataBufferUShort) rasterBuffer).getData(bank);
                                assert writeTileLength == Array.getLength(sourceArray);
                                channel.writeShorts((short[]) sourceArray);
                            } break;
                            case DataBuffer.TYPE_SHORT  : {
                                sourceArray = ((DataBufferShort)  rasterBuffer).getData(bank);
                                assert writeTileLength == Array.getLength(sourceArray);
                                channel.writeShorts((short[]) sourceArray);
                            } break;
                            case DataBuffer.TYPE_INT    : {
                                sourceArray = ((DataBufferInt)    rasterBuffer).getData(bank);
                                assert writeTileLength == Array.getLength(sourceArray);
                                channel.writeInts((int[]) sourceArray);
                            } break;
                            case DataBuffer.TYPE_FLOAT  : {
                                sourceArray = ((DataBufferFloat)  rasterBuffer).getData(bank);
                                assert writeTileLength == Array.getLength(sourceArray);
                                channel.writeFloats((float[]) sourceArray);
                            } break;
                            case DataBuffer.TYPE_DOUBLE : {
                                sourceArray = ((DataBufferDouble) rasterBuffer).getData(bank);
                                assert writeTileLength == Array.getLength(sourceArray);
                                channel.writeDoubles((double[]) sourceArray);
                            } break;
                            default: throw new AssertionError(dataType);
                        }
                    }
                }
            }
            return;
        }


        final long destTileByteCount = ((long) currentImgTH) * currentImgTW * sampleSize * pixelLength;

        //-- initialization for packBit compression --//
        rowByte32773Pos   = 0;
        n32773Position    = 0;
        currentPBAPos     = 1;
        lastByte32773     = destTileByteCount - 1;
        precLastByte32773 = lastByte32773     - 1;

        // initialize tile offset
        long tileOffsetBeg = channel.getStreamPosition();//-- position in bytes
        int tileOffsetID = 0;

        for (int bank = 0; bank < numbanks; bank++) {
            for (int cty = 0; cty < currentNumYT; cty++) {
                for (int ctx = 0; ctx < currentNumXT; ctx++) {
                    assert channel.getBitOffset() == 0;

                    //-- compute current destination tile coordinates
                    final int ctRminy = miny + cty * subsampletileHeight;
                    final int ctRmaxy = ctRminy + subsampletileHeight;
                    final int ctRminx = minx + ctx * subsampletileWidth;
                    final int ctRmaxx = ctRminx + subsampletileWidth;

                    //-- intersection on Y axis --//
                    final int interMinY = Math.max(ctRminy, srcRegion.y);
                    final int interMaxY = Math.min(ctRmaxy, srcRegionMaxY);

                    //-- intersection on X axis --//
                    final int interMinX = Math.max(ctRminx, srcRegion.x);
                    final int interMaxX = Math.min(ctRmaxx, srcRegionMaxX);

                    /*
                     * No intersection.
                     * Means destination offset greater than tile width or height.
                     */
                    if (interMaxX <= interMinX || interMaxY <= interMinY) {

                        //-- ecrire tuile vierge --//
                        write(dstOffYArray, dataType, 0, destTileStride, bitPerSample, compression);

                        //-- Use during packBit compression --//
                        lastByte32773     += destTileByteCount;
                        precLastByte32773 += destTileByteCount;

                        /*
                         * To stipulate end of current destination tile.
                         * Moreover in this current algorithm channel is automaticaly
                         * flushed when we write LZW end of file value.
                         */
                        if (compression == 5) writeWithLZWCompression(LZW_EOI_CODE);

                        final long currentOffset = channel.getStreamPosition();
                        final long currentTileByteCount = currentOffset - tileOffsetBeg;

                        if (compression == 1)
                            assert currentTileByteCount == currentImgTW * currentImgTH * pixelLength * sampleSize :"expected currentByteCount = "+(currentImgTW * currentImgTH * pixelLength * sampleSize)+" found = "+currentTileByteCount+" at tile ("+ctx+", "+cty+").";

                        if (isBigTIFF) {
                            Array.setLong(offsetArray, tileOffsetID, tileOffsetBeg);
                            Array.setLong(byteCountArray, tileOffsetID++, currentTileByteCount);
                        } else {
                            Array.setInt(offsetArray, tileOffsetID, (int) tileOffsetBeg);
                            Array.setInt(byteCountArray, tileOffsetID++, (int) currentTileByteCount);
                        }
                        tileOffsetBeg = currentOffset;
                        continue;
                    }

                    //-- destination offset point in Y direction --//
                    for (int r = ctRminy; r < interMinY; r += subsampleY) {
                        write(dstOffYArray, dataType, 0, currentImgTW * pixelLength, bitPerSample, compression);
                    }

                    //-- we looking for which tiles from image will be used to fill destination tile.
                    final int imageminTy = imageTileGridYOffset + (interMinY - imageMinY) / imageTileHeight;
                    int imagemaxTy = imageTileGridYOffset + (interMaxY - imageMinY + imageTileHeight - 1) / imageTileHeight;
                    // -- in cause of padding imagemaxTy should exceed max tile grid offset from image.
                    imagemaxTy = Math.min(imagemaxTy, imageMaxTileGridYOffset);

                    final int imageminTx = imageTileGridXOffset + (interMinX - imageMinX) / imageTileWidth;
                    int imagemaxTx = imageTileGridXOffset + (interMaxX - imageMinX + imageTileWidth - 1) / imageTileWidth;
                    // -- in cause of padding imagemaxTx should exceed max tile grid offset from image.
                    imagemaxTx = Math.min(imagemaxTx, imageMaxTileGridXOffset);

                    int cuImgTileMinY = imageMinY + (imageminTy - imageTileGridYOffset) * imageTileHeight;
                    int cuImgTileMaxY = cuImgTileMinY + imageTileHeight;
                    for (int imgTy = imageminTy; imgTy < imagemaxTy; imgTy++) {

                        final int deby  = Math.max(cuImgTileMinY, interMinY);
                        final int tendy = Math.min(cuImgTileMaxY, interMaxY);
                        final int endy  = Math.min(srcRegionMaxY, tendy);

                        // offset in pixels number in source image currently tile
                        final int stepOffsetBeforeY = (deby - cuImgTileMinY) * imageTileWidth;

                        for (int y = deby; y < endy; y += subsampleY) {

                            //-- destination offset in X direction --//
                            int dstOffWriteLength = Math.min(interMinX - ctRminx, subsampletileWidth);

                            if (dstOffWriteLength > 0) {
                                assert dstOffWriteLength % subsampleX == 0 : "dstOffWriteLength = "+dstOffWriteLength+" , subsampleX = "+subsampleX;
                                dstOffWriteLength /= subsampleX;
                                dstOffWriteLength *= pixelLength;
                                write(dstOffYArray, dataType, 0, dstOffWriteLength, bitPerSample, compression);
                            }

                            // -- offset in y direction
                            final int stepY = (y - deby) * imageTileWidth;

                            // -- current image tile coordinates in X direction
                            int cuImgTileMinX = imageMinX + (imageminTx - imageTileGridXOffset) * imageTileWidth;
                            int cuImgTileMaxX = cuImgTileMinX + imageTileWidth;

                            //-- travel tile on X direction --//
                            for (int imgTx = imageminTx; imgTx < imagemaxTx; imgTx++) {

                                // -- get the following image raster
                                final Raster imageTile        = image.getTile(imgTx, imgTy);
                                final DataBuffer rasterBuffer = imageTile.getDataBuffer();

                                final Object sourceArray;
                                switch (dataType) {
                                    case DataBuffer.TYPE_BYTE   : sourceArray = ((DataBufferByte)   rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_USHORT : sourceArray = ((DataBufferUShort) rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_SHORT  : sourceArray = ((DataBufferShort)  rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_INT    : sourceArray = ((DataBufferInt)    rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_FLOAT  : sourceArray = ((DataBufferFloat)  rasterBuffer).getData(bank); break;
                                    case DataBuffer.TYPE_DOUBLE : sourceArray = ((DataBufferDouble) rasterBuffer).getData(bank); break;
                                    default: throw new AssertionError(dataType);
                                }

                                //-- intersection --//
                                final int debx  = Math.max(cuImgTileMinX, interMinX);
                                final int tendx = Math.min(cuImgTileMaxX, interMaxX);
                                final int endx  = Math.min(srcRegionMaxX, tendx);

                                final int currentXSubSample;
                                final int writeSize;
                                if (subsampleX == 1) {
                                    currentXSubSample = endx - debx;
                                    writeSize         = currentXSubSample * pixelLength;
                                } else {
                                    currentXSubSample = subsampleX;
                                    writeSize         = pixelLength;
                                }

                                for (int x = debx; x < endx; x += currentXSubSample) {
                                    final int writeArrayOffset = (stepOffsetBeforeY + stepY + (x-cuImgTileMinX)) * pixelLength;
                                    write(sourceArray, dataType, writeArrayOffset, writeSize, bitPerSample, compression);
                                }

                                // -- padding in x direction
                                if (ctRmaxx > srcRegionMaxX) {
                                    assert ((endx - debx + subsampleX - 1) / subsampleX + paddingXLength / pixelLength + dstOffWriteLength / pixelLength) == currentImgTW : "write width = "+((endx - debx + subsampleX - 1) / subsampleX + paddingXLength / pixelLength + dstOffWriteLength / pixelLength);
                                    write(paddingXArray, dataType, 0, paddingXLength, bitPerSample, compression);
                                }

                                // -- next tile X coordinates
                                cuImgTileMinX += imageTileWidth;
                                cuImgTileMaxX += imageTileWidth;
                            }
                        }

                        //-- padding in y direction.
                        if (ctRmaxy > srcRegionMaxY) {
                            write(paddingYArray, dataType, 0, paddingYLength, bitPerSample, compression);
                        }

                        //-- next tile Y coordinates.
                        cuImgTileMinY += imageTileHeight;
                        cuImgTileMaxY += imageTileHeight;
                    }

                    //-- Use during packBit compression --//
                    lastByte32773     += destTileByteCount;
                    precLastByte32773 += destTileByteCount;

                    /*
                     * To stipulate end of current destination tile.
                     * Moreover in this current algorithm channel is automaticaly
                     * flushed when we write LZW end of file value.
                     */
                    if (compression == 5) writeWithLZWCompression(LZW_EOI_CODE);

                    final long currentOffset = channel.getStreamPosition();
                    final long currentTileByteCount = currentOffset - tileOffsetBeg;

                    if (compression == 1)
                        assert currentTileByteCount == currentImgTW * currentImgTH * pixelLength * sampleSize :"expected currentByteCount = "+(currentImgTW * currentImgTH * pixelLength * sampleSize)+" found = "+currentTileByteCount+" at tile ("+ctx+", "+cty+").";

                    if (isBigTIFF) {
                        Array.setLong(offsetArray, tileOffsetID, tileOffsetBeg);
                        Array.setLong(byteCountArray, tileOffsetID++, currentTileByteCount);
                    } else {
                        Array.setInt(offsetArray, tileOffsetID, (int) tileOffsetBeg);
                        Array.setInt(byteCountArray, tileOffsetID++, (int) currentTileByteCount);
                    }
                    tileOffsetBeg = currentOffset;
                }
            }
        }
        writeByteCountAndOffsets(byteCountTagPosition, arrayType, byteCountArray, offsetTagPosition, arrayType, offsetArray);
        //-- add current offset array in current headProperties --//
        addProperty(TileOffsets, arrayType, Array.getLength(offsetArray), offsetArray, headProperties);
    }

    /**
     * Write directly data from sourceArray, which is an array of datatype type,
     * in writebuffer at arrayOffset position with a length of arrayLength.<br/>
     *
     * Choose appropriate sub writing method in function of compression value.
     *
     * @param sourceArray sample source array data.
     * @param datatype type of data within sourceArray.
     * @param arrayOffset offset in the source array of the first sample which will be written.
     * @param arrayLength number of sample which will be written.
     * @param bitPerSample number of bits for each pixel samples.
     * @param compression compression value.
     * @throws IOException if problem during buffer writing action or unsupported ata type.
     * @see #write(Object, int, int, int, int, int)
     * @see #writeWithCompression(Object, int, int, int, int)
     */
    private void write(final Object sourceArray, final int datatype, final int arrayOffset,
            final int arrayLength, final int bitPerSample, final int compression) throws IOException {
        final int compress = compression & 0xFFFF;
        if (compress == 1) {
            //-- no compression --//
             write(sourceArray, datatype, arrayOffset, arrayLength);
        } else if (compress == 5 || compress == 32773) {
            //-- with compression --//
            writeWithCompression(sourceArray, datatype, arrayOffset, arrayLength, bitPerSample);
        } else {
            throw new IllegalStateException("Impossible to write image, unknown compression format. Compression = "+compress);
        }
    }

    /**
     * Write directly datas from sourceArray into stream {@linkplain #channel} with no compression.
     *
     * @param sourceArray sample source array data.
     * @param datatype type of data within sourceArray.
     * @param arrayOffset offset in the source array of the first sample which will be written.
     * @param arrayLength number of sample which will be written.
     * @throws IOException if problem during buffer writing action or unsupported data type.
     */
    private void write(final Object sourceArray, final int datatype,
            final int arrayOffset, final int arrayLength) throws IOException {
        switch (datatype) {
            case DataBuffer.TYPE_BYTE   : channel.write(        (byte[])   sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_USHORT : channel.writeShorts(  (short[])  sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_SHORT  : channel.writeShorts(  (short[])  sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_INT    : channel.writeInts(    (int[])    sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_FLOAT  : channel.writeFloats(  (float[])  sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_DOUBLE : channel.writeDoubles( (double[]) sourceArray, arrayOffset, arrayLength); break;
            default : throw new IllegalStateException("Unknown type : "+datatype);
        }
    }

    /**
     * Write directly datas from sourceArray into stream {@linkplain #channel} with no compression.
     *
     * @param sourceArray sample source array data.
     * @param datatype type of data within sourceArray.
     * @param arrayOffset offset in the source array of the first sample which will be written.
     * @param arrayLength number of sample which will be written.
     * @throws IOException if problem during buffer writing action or unsupported data type.
     */
    private void write(final Object sourceArray, final int datatype,
            final int arrayOffset, final int arrayLength, final long channelOffset) throws IOException {
        channel.seek(channelOffset);
        switch (datatype) {
            case DataBuffer.TYPE_BYTE   : channel.write(        (byte[])   sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_USHORT : channel.writeShorts(  (short[])  sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_SHORT  : channel.writeShorts(  (short[])  sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_INT    : channel.writeInts(    (int[])    sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_FLOAT  : channel.writeFloats(  (float[])  sourceArray, arrayOffset, arrayLength); break;
            case DataBuffer.TYPE_DOUBLE : channel.writeDoubles( (double[]) sourceArray, arrayOffset, arrayLength); break;
            default : throw new IllegalStateException("Unknown type : "+datatype);
        }
    }

    /**
     * Study tile properties to be in accordance with tiff specification and add them to write.
     *
     * @param tileWidth destination image tile width.
     * @param tileHeight destination image tile height.
     * @param numXTile tile number in X direction in destination image.
     * @param numYTile tile number in Y direction in destination image.
     * @param properties all destination image properties.
     */
    private void addTileOffsetsProperties(final int tileWidth, final int tileHeight,
            final int numXTile, final int numYTile, final Map properties) {

        // tilewidth
        final short tilWType;
        final Object tilWAttribut;
        if (tileWidth > 0xFFFF) {
            // en long
            tilWType = TYPE_INT;
            tilWAttribut = new int[]{tileWidth};
        } else {
            // en short
            tilWType = TYPE_USHORT;
            tilWAttribut = new short[]{(short)tileWidth};
        }
        addProperty(TileWidth, tilWType, 1, tilWAttribut, properties);
        // -------------------------------------------//

        // tileheight
        final short tilHType;
        final Object tilHAttribut;
        if (tileHeight > 0xFFFF) {
            // en long
            tilHType = TYPE_INT;
            tilHAttribut = new int[]{tileHeight};
        } else {
            // en short
            tilHType = TYPE_USHORT;
            tilHAttribut = new short[]{(short)tileHeight};
        }
        addProperty(TileLength, tilHType, 1, tilHAttribut, properties);
        // -------------------------------------------//

        final Map<String, Object> pcObj   =  (Map<String, Object>) properties.get(PlanarConfiguration);
        final short pc = ((pcObj == null) ? 1 : ((short[]) pcObj.get(ATT_VALUE))[0]);

        int numTiles = numXTile * numYTile;
        if (pc == 2) {
            final Map<String, Object> sppObj   =  (Map<String, Object>) properties.get(SamplesPerPixel);
            assert sppObj != null;
            final short spp = ((short[]) sppObj.get(ATT_VALUE))[0];
            numTiles *= spp;
        }
        final short arrayType = (isBigTIFF) ? TYPE_ULONG : TYPE_UINT;
        addProperty(TileByteCounts, arrayType, numTiles, null, properties);
        addProperty(TileOffsets, arrayType, numTiles, null, properties);
    }

    /**
     * Compute some needed {@link Rectangle} which represent writing area iteration on image.
     *
     * @param srcImgWidth source image width.
     * @param srcImgHeight source image height.
     * @param srcImgMinX source image minimum X coordinate.
     * @param srcImgMinY source image minimum Y coordinate.
     * @param srcImgNumXTile source image tile number in X direction.
     * @param srcImgNumYTile source image tile number in Y direction.
     * @param srcImgTileWidth source image tile width.
     * @param srcImgTileHeight source image tile height.
     * @param param contain writing information or none if param is null.
     * @return {@link Rectangle} which represent writing area iteration on image.
     */
    private void computeRegions(final int srcImgWidth, final int srcImgHeight, final int srcImgMinX, final int srcImgMinY,
                                final int srcImgNumXTile, final int srcImgNumYTile, final int srcImgTileWidth, final int srcImgTileHeight,
                                final ImageWriteParam param) {

        imageBoundary = new Rectangle(srcImgMinX, srcImgMinY, srcImgWidth, srcImgHeight);

        if (param == null) {
            srcRegion  = imageBoundary;
            destRegion = new Rectangle(srcRegion);
            if ((srcImgNumXTile > 1 || srcImgNumYTile > 1)
                    && srcImgWidth      % srcImgTileWidth  == 0 // to avoid padding
                    && srcImgHeight     % srcImgTileHeight == 0
                    && srcImgTileWidth  % 16 == 0                    // to verify compatibility with compression (tiff spec).
                    && srcImgTileHeight % 16 == 0) {
                tileRegion      = srcRegion;
                currentImgTW    = srcImgTileWidth;
                currentImgTH    = srcImgTileHeight;
                currentImgNumXT = srcRegion.width  / currentImgTW;
                currentImgNumYT = srcRegion.height / currentImgTH;
            } else {
                currentImgTW = currentImgTH = currentImgNumXT = currentImgNumYT = 0;
            }
        } else {

            // param not null
            final Rectangle paramRect = (param.getSourceRegion() == null) ? new Rectangle(imageBoundary) : param.getSourceRegion();

            //-- in case of subsampling different of 1 with an offset.
            final int xOffset = param.getSubsamplingXOffset();
            final int yOffset = param.getSubsamplingYOffset();
            paramRect.translate(xOffset, yOffset);
            paramRect.width  -= xOffset;
            paramRect.height -= yOffset;

            if (!imageBoundary.intersects(paramRect)) {
                throw new IllegalStateException("src region from ImageWriterParam must intersect image boundary.");
            }
            srcRegion = imageBoundary.intersection(paramRect);// on est en coordonnées images

            final int srcXsubsampling = param.getSourceXSubsampling();
            final int srcYsubsampling = param.getSourceYSubsampling();
            assert srcYsubsampling >= 1;
            assert srcXsubsampling >= 1;

            //-- destination offsets point --//
            final Point destOffset = param.getDestinationOffset();
            int dstOffX = 0;
            int dstOffY = 0;
            int srcOffX = 0;
            int srcOffY = 0;
            if (destOffset != null) {
                dstOffX = destOffset.x;
                dstOffY = destOffset.y;
                srcOffX = dstOffX * srcXsubsampling;
                srcOffY = dstOffY * srcYsubsampling;
            }

            srcRegion.translate(srcOffX, srcOffY);
            imageBoundary.translate(srcOffX, srcOffY);

            /*
             * Try catch is a wrong way, but ImageWriteParam interface doesn't have any method
             * to know if tiles dimensions have already been set.
             */
            try {
                currentImgTW  = param.getTileWidth();
                currentImgTH  = param.getTileHeight();
            } catch (Exception ex) {
                currentImgTW  = currentImgTH = 0;
            }

            if (currentImgTW > 0 && currentImgTH > 0) {
                final int cuImgTW = currentImgTW * srcXsubsampling;
                final int cuImgTH = currentImgTH * srcYsubsampling;
                currentImgNumXT = (srcOffX + srcRegion.width  + cuImgTW - 1) / cuImgTW;
                currentImgNumYT = (srcOffY + srcRegion.height + cuImgTH - 1) / cuImgTH;
                if (currentImgTW % 16 != 0)
                        throw new IllegalStateException("To be in accordance with tiff specification tile width must be multiple of 16. Current tile width = "+param.getTileWidth());
                if (currentImgTH % 16 != 0)
                        throw new IllegalStateException("To be in accordance with tiff specification tile height must be multiple of 16. Current tile height = "+param.getTileHeight());
                tileRegion = new Rectangle(srcRegion.x - srcOffX, srcRegion.y - srcOffY, currentImgNumXT * cuImgTW, currentImgNumYT * cuImgTH);

            } else {
                // param not null with tile not specify
                tileRegion = new Rectangle(srcRegion);
                tileRegion.width += srcOffX;
                tileRegion.height += srcOffY;

                // on va essayer de voir si les tuiles de l'image peuvent correspondre avec la srcregion
                if ((srcImgNumXTile > 1 || srcImgNumYTile > 1)
                    && tileRegion.width  % srcImgTileWidth  == 0 // to avoid padding
                    && tileRegion.height % srcImgTileHeight == 0
                    && srcImgTileWidth  % 16 == 0                // to verify compatibility with compression (tiff spec).
                    && srcImgTileHeight % 16 == 0
                    && srcXsubsampling == 1
                    && srcYsubsampling == 1) {
                    currentImgTW    = srcImgTileWidth;
                    currentImgTH    = srcImgTileHeight;
                    currentImgNumXT = srcRegion.width  / currentImgTW;
                    currentImgNumYT = srcRegion.height / currentImgTH;
                } else {
                    currentImgTW = currentImgTH = currentImgNumXT = currentImgNumYT = 0;
                }
            }
            destRegion = new Rectangle((srcOffX + srcRegion.width + srcXsubsampling - 1) / srcXsubsampling, (srcOffY + srcRegion.height + srcYsubsampling - 1) / srcYsubsampling);

        }
        assert srcRegion != null;
    }

    /**
     * Compute {@link Rectangle} which represent writing area iteration on image.
     *
     * @param image image which will be written.
     * @param param contain writing information or none if param is null.
     * @return {@link Rectangle} which represent writing area iteration on image.
     */
    private void computeRegions(final RenderedImage image, final ImageWriteParam param) {
        computeRegions(image.getWidth(), image.getHeight(), image.getMinX(), image.getMinY(),
                image.getNumXTiles(), image.getNumYTiles(), image.getTileWidth(), image.getTileHeight(), param);
    }

    /**
     * Add property tag in {@linkplain #headProperties} before writing.
     *
     * @param tag      tiff tag value.
     * @param type     tiff tag type data.
     * @param count    number of data for this tag.
     * @param attribut tag data.
     */
    private void addProperty(final int tag, final short type, final int count, final Object attribut, final Map properties) {
        final Map<String, Object> tagAttributs = new HashMap<String, Object>();
        tagAttributs.put(ATT_NAME, getName(tag));
        tagAttributs.put(ATT_TYPE, type);
        tagAttributs.put(ATT_COUNT, count);
        tagAttributs.put(ATT_VALUE, attribut);
        properties.put(tag, tagAttributs);
    }

    /**
     * Build an appropriate color map table in relation with tiff specification
     * from {@link IndexColorModel#getRGBs(int[]) } table values.
     *
     * @param colorMap IndexColorModel indexed color map values.
     * @return appropriate tiff color map.
     */
    private short[] buildTiffMapArray(final int[] colorMap) {
        final int cmSize = colorMap.length;
        final short[] tiffMap = new short[cmSize * 3];
        int idR = 0;
        int idG = cmSize;
        int idB = cmSize << 1;// = 2 * length_3
        for (int i = 0; i < cmSize; i++) {
            final int argb = colorMap[i];
            final short r = (short) ((argb & 0x00FF0000) >> 8);
            final short g = (short)  (argb & 0x0000FF00);
            final short b = (short) ((argb & 0x000000FF) << 8);
            tiffMap[idR++] = r;
            tiffMap[idG++] = g;
            tiffMap[idB++] = b;
        }
        return tiffMap;
    }

    /**
     * Return length in byte of attribute on the file.
     *
     * @param tagAttribute
     * @return length in byte of attribute on the file.
     */
    private int getAttributeLength(final Map tagAttribute) {
        final int type  = (int) ((short) tagAttribute.get(ATT_TYPE));
        final int count = (int) tagAttribute.get(ATT_COUNT);
        return TYPE_SIZE[type] * count;
    }

    /**
     * Write all tags within {@link #headProperties} which are all tags without
     * offset but real value contained in 4 bytes.
     *
     * @throws IOException if problem during buffer writing.
     */
    private void writeTags(final Map<Integer, Map> properties, long[] ifdPosition) throws IOException {

        //-- compute IFD end position in file to write deferred datas. --//
        final long endTagPos = ifdPosition[0] + currentSizeTagNumber + properties.size() * currentSizeEntry + currentSizeNextIFD;

        final int offsetSize = currentSizeNextIFD;

        final TreeMap<Integer, Map> defferedMap = new TreeMap<Integer, Map>();

        //-- write tag number --//
        if (isBigTIFF) channel.writeLong(properties.size());
        else channel.writeShort(properties.size());

        long deferredTagPos  = endTagPos;
        //-- write tags without offsets --//
        for (int tag : properties.keySet()) {
            Map tagAttribute    = properties.get(tag);
            short type         = (short) tagAttribute.get(ATT_TYPE);
            int count          = (int) tagAttribute.get(ATT_COUNT);
            Object offOrVal    = tagAttribute.get(ATT_VALUE);
            final int dataSize = count * TYPE_SIZE[type];

            /*
             * Write tags in destination file if its possible.
             * Some tags needs to write image before, to have enough information
             * and will be written later.
             */
            if (tag == TileByteCounts || tag == StripByteCounts || tag == TileOffsets || tag == StripOffsets) {
                writeDefferedTag((short)tag, type, count, 0);
            } else {
                if (dataSize <= offsetSize) {
                    writeTag((short)tag, type, count, offOrVal);
                } else {
                    defferedMap.put(tag, tagAttribute);
                    writeDefferedTag((short)tag, type, count, deferredTagPos);
                    deferredTagPos += getAttributeLength(tagAttribute);
                }
            }
        }

        //-- write next IFD file position --//
        //-- if 0 means no next IFD. --//
        ifdPosition[1] = channel.getStreamPosition();

        if (isBigTIFF) channel.writeLong(0);
        else           channel.writeInt(0);

        assert (channel.getStreamPosition()) == endTagPos : "chanel and buffer position = "+((channel.getStreamPosition()))+" end tag position = "+endTagPos;

        for (final int tag : defferedMap.keySet()) {
            final Map tagAttribute = defferedMap.get(tag);
            short type      = (short) tagAttribute.get(ATT_TYPE);
            Object offOrVal = tagAttribute.get(ATT_VALUE);
            writeArray(offOrVal, type);
        }
    }

    /**
     * Write the two tables which define image data position in tiff file.<br/>
     * byteCountArray can be stripByteCount in case of strip writing or tileByteCount table in case of tile writing.<br/>
     * offsetArray can be stripOffset in case of strip writing or tileOffset table in case of tile writing.<br/><br/>
     *
     * Write byteCountArray at byteCountPosition in destination tiff file with byteCountType data type.<br/>
     * Write offsetArray at offsetPosition in destination tiff file with offsetType data type.
     *
     * @param byteCountPosition position in destination file, in {@code byte}, where to write byteCountArray.
     * @param byteCountType type of stored data in byteCountArray.
     * @param byteCountArray array which contain {@code byte} numbers for each strip or tile.
     * @param offsetPosition position in destination file, in {@code byte}, where to write offsetArray.
     * @param offsetType type of stored data in offsetArray.
     * @param offsetArray array which contain position of first byte in destination tiff file of each strip or tile.
     * @throws IOException if problem during array writing in destination buffer.
     */
    private void writeByteCountAndOffsets(final long byteCountPosition, final short byteCountType, final Object byteCountArray,
                                          final long offsetPosition,    final short offsetType,    final Object offsetArray) throws IOException {
        //-- if all offsets or bytecounts datasize should be contained into Long or Integer datasize. --//
        final int datasize = (isBigTIFF) ? Long.SIZE / Byte.SIZE : Integer.SIZE / Byte.SIZE;
        final int bcaLen   = Array.getLength(byteCountArray);
        final int offLen   = Array.getLength(offsetArray);
        assert bcaLen == offLen : "byteCount and offset array should have same length : byte count len = "+bcaLen+" offset array len = "+offLen;
        assert offsetType == byteCountType : "expected same byte count and offset type.";

        //---------- byteCount ---------------//
        if (bcaLen * TYPE_SIZE[byteCountType] <= datasize) {
            channel.seek(byteCountPosition);
            writeArray(byteCountArray, byteCountType);
        } else {

            final long byteOffset = channel.getStreamPosition();
            channel.seek(byteCountPosition);

            if (isBigTIFF) channel.writeLong(byteOffset);
            else channel.writeInt((int) byteOffset);

            channel.seek(byteOffset);
            writeArray(byteCountArray, byteCountType);
        }

        //--------- offsets------------//
        if (offLen * TYPE_SIZE[offsetType] <= datasize) {
            channel.seek(offsetPosition);
            writeArray(offsetArray, offsetType);
        } else {
            final long offsetOffset = channel.getStreamPosition();
            channel.seek(offsetPosition);

            if (isBigTIFF) channel.writeLong(offsetOffset);
            else channel.writeInt((int) offsetOffset);

            channel.seek(offsetOffset);
            writeArray(offsetArray, offsetType);
        }
    }

    /**
     * Write current source image by strips.<br/>
     * Which mean image is not tiled and data are stored by stripOffsets and stripByteCount tables in tiff file.
     *
     * @param img source image which will be written.
     * @param param Image parameter to define written area and subsampling if exists else {@code null}.
     * @throws IOException if problem during image writing or source image raster type is not known.
     */
    private void writeImageByStrips(final RenderedImage img, final ImageWriteParam param) throws IOException {
        // ----------------- Image boundary -------------------//
        final int imageMinX      = img.getMinX() + imageBoundary.x;
        final int imageMinY      = img.getMinY() + imageBoundary.y;

        // ------------------- Image Tiles --------------------//
        final int imgMinTXOffset = img.getTileGridXOffset();
        final int imgMinTYOffset = img.getTileGridYOffset();

        final int imgNumTileX    = img.getNumXTiles();
        final int imgNumTileY    = img.getNumYTiles();

        final int imgTileWidth   = img.getTileWidth();
        final int imgTileHeight  = img.getTileHeight();

        //------------------ raster properties -----------------//
        final Raster initRast         = img.getTile(imgMinTXOffset, imgMinTYOffset);
        final DataBuffer initRastBuff = initRast.getDataBuffer();
        final int dataType            = initRastBuff.getDataType();
        final int numbanks = initRastBuff.getNumBanks();

        assert bitPerSample != 0;

        final int srcRegionMaxX = srcRegion.x + srcRegion.width;
        final int srcRegionMaxY = srcRegion.y + srcRegion.height;

        final int sampleSize = bitPerSample / Byte.SIZE;

        // ---- subsamples -----//
        final int subsampleX;
        final int subsampleY;

        if (param != null) {
            subsampleX = param.getSourceXSubsampling();
            subsampleY = param.getSourceYSubsampling();
        } else {
            subsampleX = subsampleY = 1;
        }
        assert subsampleX > 0 && subsampleY > 0;

        final SampleModel sm   = img.getSampleModel();
        final short planarConf = getPlanarConfiguration(sm);
        final int numband      = sm.getNumBands();

        final int pixelLength = (planarConf == 2) ? 1 : numband; //-- equivalent of pixel stride

        final long currentByteCount = destRegion.width * pixelLength * sampleSize;

        //-- destination offset --//
        final Point dstOffsets = param == null ? null : param.getDestinationOffset();
        int dstOffX, dstOffY;
        if (dstOffsets != null) {
            dstOffX = dstOffsets.x;
            dstOffY = dstOffsets.y;
        } else {
            dstOffX = dstOffY = 0;
        }

        //---------- initialization strip offsets and stripbycount -----------//
        final Object byteCountArray;
        final int byteCountArraySize;
        final Object offsetArray;
        final int offsetArraySize;
        final short arrayType = (isBigTIFF) ? TYPE_ULONG : TYPE_UINT;

        final int tableLength = (planarConf == 2) ? destRegion.height * numband : destRegion.height;
        if (isBigTIFF) {
            byteCountArray = new long[tableLength];
            offsetArray    = new long[tableLength];
            byteCountArraySize = offsetArraySize = tableLength * TYPE_SIZE[TYPE_ULONG];
        } else {
            byteCountArray = new int[tableLength];
            offsetArray    = new int[tableLength];
            byteCountArraySize = offsetArraySize = tableLength * TYPE_SIZE[TYPE_UINT];
        }

        final long buffPos = channel.getStreamPosition();

        if (subsampleX == 1
         && subsampleY == 1
         && srcRegion.equals(imageBoundary)
         && imgNumTileX == 1
         && imgNumTileY == 1
         && compression == 1
         && dstOffX == 0
         && dstOffY == 0) {

             long currentoffset = buffPos + byteCountArraySize + offsetArraySize;
            //-- fill byte count array. --//
            if (isBigTIFF) {
                Arrays.fill((long[]) byteCountArray, currentByteCount);
                for (int i = 0; i < tableLength; i++) {
                    Array.setLong(offsetArray, i, currentoffset);
                    currentoffset += currentByteCount;
                }
            } else {
                Arrays.fill((int[]) byteCountArray, (int) currentByteCount);
                for (int i = 0; i < tableLength; i++) {
                    Array.setInt(offsetArray, i, (int) currentoffset);
                    currentoffset += currentByteCount;
                }
            }

            //-- add current offset array in current headProperties --//
            addProperty(StripOffsets, (isBigTIFF) ? TYPE_ULONG : TYPE_UINT, Array.getLength(offsetArray), offsetArray, headProperties);

            writeByteCountAndOffsets(byteCountTagPosition, arrayType, byteCountArray, offsetTagPosition, arrayType, offsetArray);

            //---------------------------------------------------------------------//

            int writelength = img.getWidth() * pixelLength;
            assert writelength * sampleSize == currentByteCount : "writeLength = "+(writelength * sampleSize)+" currentByteCount = "+currentByteCount;
            writelength *= img.getHeight();
            // -- get the following raster from source image. --//
            final Raster imageTile        = img.getTile(imgMinTXOffset, imgMinTYOffset);
            final DataBuffer rasterBuffer = imageTile.getDataBuffer();
            for (int bank = 0; bank < numbanks; bank++) {
                final Object sourceArray;
                switch (dataType) {
                    case DataBuffer.TYPE_BYTE   :  {
                        sourceArray = ((DataBufferByte)   rasterBuffer).getData(bank);
                        channel.write((byte[]) sourceArray, 0, writelength);
                    } break;
                    case DataBuffer.TYPE_USHORT : {
                        sourceArray = ((DataBufferUShort) rasterBuffer).getData(bank);
                        channel.writeShorts((short[]) sourceArray, 0, writelength);
                    } break;
                    case DataBuffer.TYPE_SHORT  :  {
                        sourceArray = ((DataBufferShort)  rasterBuffer).getData(bank);
                        channel.writeShorts((short[]) sourceArray, 0, writelength);
                    } break;
                    case DataBuffer.TYPE_INT    :    {
                        sourceArray = ((DataBufferInt)    rasterBuffer).getData(bank);
                        channel.writeInts((int[]) sourceArray, 0, writelength);
                    } break;
                    case DataBuffer.TYPE_FLOAT  :  {
                        sourceArray = ((DataBufferFloat)  rasterBuffer).getData(bank);
                        channel.writeFloats((float[]) sourceArray, 0, writelength);
                    } break;
                    case DataBuffer.TYPE_DOUBLE : {
                        sourceArray = ((DataBufferDouble) rasterBuffer).getData(bank);
                        channel.writeDoubles((double[]) sourceArray, 0, writelength);
                    } break;
                    default: throw new AssertionError(dataType);
                }
            }
            return;
        }

        // initialize stripbytecount stripOffset
        long stripOffsetBeg = buffPos;

        //-- initialization for packBit compression --//
        rowByte32773Pos   = 0;
        n32773Position    = 0;
        currentPBAPos     = 1;
        lastByte32773     = currentByteCount - 1;
        precLastByte32773 = lastByte32773 - 1;

        int stripArrayID = 0;
        // on defini intersection indice de tuiles
        final int minTX = imgMinTXOffset + (srcRegion.x - imageMinX) / imgTileWidth;
        final int maxTX = imgMinTXOffset + (srcRegionMaxX - imageMinX + imgTileWidth - 1) / imgTileWidth;
        final int minTY = imgMinTYOffset + (srcRegion.y - imageMinY) / imgTileHeight;
        final int maxTY = imgMinTYOffset + (srcRegionMaxY - imageMinY + imgTileHeight - 1) / imgTileHeight;

        final int dstOffXStride = dstOffX * pixelLength;

        //-- write rows given by destination offset Y--//
        Object destOffsetRowArray, destOffsetXArray;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE   :  {
                destOffsetRowArray = new byte[destRegion.width * pixelLength];
                destOffsetXArray = new byte[dstOffXStride];
            } break;
            case DataBuffer.TYPE_USHORT :
            case DataBuffer.TYPE_SHORT  :  {
                destOffsetRowArray = new short[destRegion.width * pixelLength];
                destOffsetXArray = new short[dstOffXStride];
            } break;
            case DataBuffer.TYPE_INT    :    {
                destOffsetRowArray = new int[destRegion.width * pixelLength];
                destOffsetXArray = new int[dstOffXStride];
            } break;
            case DataBuffer.TYPE_FLOAT  :  {
                destOffsetRowArray = new float[destRegion.width * pixelLength];
                destOffsetXArray = new float[dstOffXStride];
            } break;
            case DataBuffer.TYPE_DOUBLE : {
                destOffsetRowArray = new double[destRegion.width * pixelLength];
                destOffsetXArray = new double[dstOffXStride];
            } break;
            default: throw new AssertionError(dataType);
        }
        for (int bank = 0; bank < numbanks; bank++) {
            if (dstOffY > 0) {
                for (int r = 0; r < dstOffY; r++) {

                    write(destOffsetRowArray, dataType, 0, destRegion.width * pixelLength, bitPerSample, compression);

                    if (compression == 5) writeWithLZWCompression(LZW_EOI_CODE);
                    lastByte32773     += currentByteCount;
                    precLastByte32773 += currentByteCount;
                    final long currentStripOffset = channel.getStreamPosition();

                    //-- offset in byte
                    if (isBigTIFF) {
                        Array.setLong(offsetArray, stripArrayID, stripOffsetBeg);
                        Array.setLong(byteCountArray, stripArrayID++, currentStripOffset - stripOffsetBeg);
                    }
                    else {
                        Array.setInt(offsetArray, stripArrayID, (int) stripOffsetBeg);
                        Array.setInt(byteCountArray, stripArrayID++, (int) (currentStripOffset - stripOffsetBeg));
                    }

                    stripOffsetBeg = currentStripOffset;
                }
            }

           for (int ty = minTY; ty < maxTY; ty++) {
               //-- define intersection on Y axis between srcRegion and current tile from source image --//
               final int currentImgTileMinY = imageMinY + ty * imgTileHeight;
               final int currentImgTileMaxY = currentImgTileMinY + imgTileHeight;
               final int minRowY            = Math.max(srcRegion.y, currentImgTileMinY);
               final int maxRowY            = Math.min(srcRegionMaxY, currentImgTileMaxY);

               final int rowArrayOffset = (minRowY - currentImgTileMinY) * imgTileWidth * pixelLength;

               for (int ry = minRowY; ry < maxRowY; ry += subsampleY) {
                   //-- count use to verify expected wrote byte number. --//
                   int assertByteCount = 0;

                   //-- pour chaque row on ecrit le tableau manquant--//
                   if (dstOffX > 0) {
                       write(destOffsetXArray, dataType, 0, dstOffX * pixelLength, bitPerSample, compression);
                       assertByteCount += dstOffX * pixelLength * sampleSize;
                   }

                   //-- shift on each line. --//
                   final int arrayStepY = (ry - minRowY) * imgTileWidth * pixelLength;

                   for (int tx = minTX; tx < maxTX; tx++) {
                       // -- get the following image raster
                        final Raster imageTile        = img.getTile(tx, ty);
                        final DataBuffer rasterBuffer = imageTile.getDataBuffer();

                        final Object sourceArray;
                        switch (dataType) {
                            case DataBuffer.TYPE_BYTE   : sourceArray = ((DataBufferByte)   rasterBuffer).getData(bank); break;
                            case DataBuffer.TYPE_USHORT : sourceArray = ((DataBufferUShort) rasterBuffer).getData(bank); break;
                            case DataBuffer.TYPE_SHORT  : sourceArray = ((DataBufferShort)  rasterBuffer).getData(bank); break;
                            case DataBuffer.TYPE_INT    : sourceArray = ((DataBufferInt)    rasterBuffer).getData(bank); break;
                            case DataBuffer.TYPE_FLOAT  : sourceArray = ((DataBufferFloat)  rasterBuffer).getData(bank); break;
                            case DataBuffer.TYPE_DOUBLE : sourceArray = ((DataBufferDouble) rasterBuffer).getData(bank); break;
                            default: throw new AssertionError(dataType);
                        }

                       //-- definir intersection sur x --//
                       final int currentImgTileMinX = imageMinX + tx * imgTileWidth;
                       final int currentImgTileMaxX = currentImgTileMinX + imgTileWidth;

                       final int cuMinX = Math.max(srcRegion.x, currentImgTileMinX);
                       final int cuMaxX = Math.min(srcRegionMaxX, currentImgTileMaxX);

                       final int arrayXOffset = (cuMinX - currentImgTileMinX) * pixelLength;

                       final int writeLenght;
                       final int stepX;
                       if (subsampleX == 1) {
                           stepX       = cuMaxX - cuMinX;
                           writeLenght = stepX * pixelLength;
                       } else {
                           stepX       = subsampleX;
                           writeLenght = pixelLength;
                       }

                       for (int x = cuMinX; x < cuMaxX; x += stepX) {
                           final int arrayStepX  = (x - cuMinX) * pixelLength;
                           final int finalOffset = rowArrayOffset + arrayStepY + arrayXOffset + arrayStepX;
                           write(sourceArray, dataType, finalOffset, writeLenght, bitPerSample, compression);

                           //-- assertion --//
                           assertByteCount += (writeLenght * sampleSize);
                       }
                   }

                   if (compression == 5) writeWithLZWCompression(LZW_EOI_CODE);
                   lastByte32773     += currentByteCount;
                   precLastByte32773 += currentByteCount;
                   final long currentStripOffset = channel.getStreamPosition();

                   //-- offset in byte
                   if (isBigTIFF) {
                       Array.setLong(offsetArray, stripArrayID, stripOffsetBeg);
                       Array.setLong(byteCountArray, stripArrayID++, currentStripOffset - stripOffsetBeg);
                   }
                   else {
                       Array.setInt(offsetArray, stripArrayID, (int) stripOffsetBeg);
                       Array.setInt(byteCountArray, stripArrayID++, (int) (currentStripOffset - stripOffsetBeg));
                   }

                   stripOffsetBeg = currentStripOffset;

                   if (compression == 1) //-- means no compression --//
                   assert assertByteCount == currentByteCount : "writen byte number doesn't "
                           + "match with expected comportement : writenByte = "+assertByteCount
                           +" expected writen byte number : "+currentByteCount;
               }
            }
        }
        //-- after destination image writing, write stripOffset and stripByteCount tables --//
        writeByteCountAndOffsets(byteCountTagPosition, arrayType, byteCountArray, offsetTagPosition, arrayType, offsetArray);
        //-- add current offset array in current headProperties --//
        addProperty(StripOffsets, (isBigTIFF) ? TYPE_ULONG : TYPE_UINT, Array.getLength(offsetArray), offsetArray, headProperties);
    }

    /**
     * Write data in stream in function of the wanted compression.
     *
     * @param sourceArray sample source array data.
     * @param dataBuffertype type of data within sourceArray.
     * @param arrayOffset offset in the source array of the first sample which will be writen.
     * @param arrayLength number of sample which will be writen.
     * @param bitPerSample number of bits for each pixel samples.
     * @throws IOException if problem during image writing or source image raster type is not known.
     * @see #writeWithLZWCompression(long)
     */
    private void writeWithCompression(final Object sourceArray, final int dataBuffertype,
            final int arrayOffset, final int arrayLength, final int bitPerSample) throws IOException {
        // on decompress chaque valeur en fonction de son datatype en byte qu'on donne ensuite o compresseur
        for (int i = arrayOffset; i < arrayOffset + arrayLength; i++) {

            final int nbIter = bitPerSample / Byte.SIZE;
            long srcVal;
            switch (dataBuffertype) {
                case DataBuffer.TYPE_BYTE : {
                    assert nbIter == 1 : "In DataBuffer.TYPE_BYTE, expected nbIter = 1, found : "+nbIter;
                    srcVal = Array.getByte(sourceArray, i);
                    break;
                }
                case DataBuffer.TYPE_USHORT :
                case DataBuffer.TYPE_SHORT  : {
                    assert nbIter == 2 : "In DataBuffer.TYPE_SHORT, expected nbIter = 2, found : "+nbIter;
                    srcVal = Array.getShort(sourceArray, i);
                    break;
                }
                case DataBuffer.TYPE_INT : {
                    assert nbIter == 4 : "In DataBuffer.TYPE_INT, expected nbIter = 4, found : "+nbIter;
                    srcVal = Array.getInt(sourceArray, i);
                    break;
                }
                case DataBuffer.TYPE_FLOAT : {
                    assert nbIter == 4 : "In DataBuffer.TYPE_FLOAT, expected nbIter = 4, found : "+nbIter;
                    srcVal = Float.floatToRawIntBits(Array.getFloat(sourceArray, i));
                    break;
                }
                case DataBuffer.TYPE_DOUBLE : {
                    assert nbIter == 8 : "In DataBuffer.TYPE_DOUBLE, expected nbIter = 8, found : "+nbIter;
                    srcVal = Double.doubleToRawLongBits(Array.getDouble(sourceArray, i));
                    break;
                }
                default: throw new IOException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE, dataBuffertype));
            }

            int bitOffset, bitOffsetStep;
            if (currentBO.equals(ByteOrder.LITTLE_ENDIAN)) {
                bitOffset = 0;
                bitOffsetStep = Byte.SIZE;
            } else {
                bitOffset = bitPerSample - Byte.SIZE;
                bitOffsetStep = - Byte.SIZE;
            }

            for (int it = 0; it < nbIter; it++) {
                long mask = 0xFFL << bitOffset;
                long val = srcVal & mask;
                val = val >>> bitOffset;
                 //-- write current byte by expected compression --//
                if (compression == 32773) {
                    writeWithPackBitsCompression((byte) val);
                } else if (compression == 5) {
                    writeWithLZWCompression((byte) val);
                } else {
                    throw new IllegalStateException("no compression value should never append.");
                }
                bitOffset += bitOffsetStep;
            }
        }
    }

    /**
     * Write the given value into stream {@linkplain #channel}
     * in accordance with LZW algorithm compression.
     *
     * @param b value which will be compressed.
     * @throws IOException
     */
    private void writeWithLZWCompression(final long b) throws IOException {
        if (wk == null) {
            wkPos = 0;
            wk    = new byte[20];
            currentLZWCodeLength = 9;
            // -- write clearcode --//
            channel.writeBits(LZW_CLEAR_CODE, currentLZWCodeLength);
            currentLZWCode = LZW_DEFAULT_CODE;
            if (lzwMap == null) {
                lzwMap = new LZWMap();
            } else {
                lzwMap.clear();
            }
        } else if (wk.length == wkPos) {
            wk = Arrays.copyOf(wk, wk.length << 1);
        }

        //-- particularity case for code 257 --//
        if (b == LZW_EOI_CODE) {
            //-- get the last key --//
            final byte[] key = Arrays.copyOf(wk, wkPos);
            final int code   = getCodeFromLZWMap(key);
            channel.writeBits(code, currentLZWCodeLength);
            channel.writeBits(LZW_EOI_CODE, currentLZWCodeLength);
            channel.flush();
            wk = null;
            return;
        }

        wk[wkPos] = (byte) b;
        wkPos++;
        if (wkPos != 1) {
            //-- need wk at the last position length --//
            final byte[] wkStrict = Arrays.copyOf(wk, wkPos);
            if (!lzwMap.containsKey(wkStrict)) {
                //-- create appropriate key
                final byte[] key = Arrays.copyOf(wk, wkPos-1);
                final int code   = getCodeFromLZWMap(key);

                channel.writeBits(code, currentLZWCodeLength);
                lzwMap.put(wkStrict, currentLZWCode);
                currentLZWCode++;
                wk[0] = wk[wkPos - 1];
                wkPos = 1;
                if ((currentLZWCode & 0xFFFF) >= (1 << currentLZWCodeLength)) currentLZWCodeLength++;
                //check si on clear ou pas
                if (currentLZWCodeLength > LZW_MAX_CODE_LENGTH) {

                    //-- from tiff spec write clear code on 12 bits --//
                    channel.writeBits(LZW_CLEAR_CODE, LZW_MAX_CODE_LENGTH);
                    //-- clear all --//
                    lzwMap.clear();
                    currentLZWCodeLength = 9;
                    currentLZWCode       = LZW_DEFAULT_CODE;
                }
            }
        }
    }

    /**
     * Return the expected LZW code from the given key.
     *
     * @param key byte array key.
     * @return the expected LZW code from the given key.
     * @see LZWMap
     */
    private int getCodeFromLZWMap(byte[] key) {
        assert key.length > 0;
        final int code =  (((key.length == 1) ? 0xFF & key[0] : 0xFFFF & lzwMap.get(key)));
        assert code != LZW_CLEAR_CODE : "code should never be equals to LZW clearCode value.";
        assert code <= 4095;
        if (key.length > 1)
        assert code >= 258: "Expected code >= 258 : found = "+(code & 0xFFFF);
        return code;
    }

    /**
     * Write current given byte into stream, in accordance with pack bit compression algorithm.
     *
     * @param value
     * @throws IOException
     */
    private void writeWithPackBitsCompression(final byte value) throws IOException {

        /*
         * Define if the twoth previously stored values are equals to define
         * if previously step was an identical numbers suite.
         * A suite in packBit compression is define if and only if there are more than 2 identical values.
         */
        final boolean isEquals = (currentPBAPos >= n32773Position + 3)
                && packBitArray[currentPBAPos - 1] == packBitArray[currentPBAPos - 2]
                && packBitArray[currentPBAPos - 1] == value;

        if (isEquals) {
            if (currentPBAPos - n32773Position > 3) {
                //-- detect an identical suite number at the end of distinct numbers suite. --//
                //-- write precedently distinct numbers suite. --//
                assert n32773Value == currentPBAPos - n32773Position - 1 : "n32773Value = "+n32773Value+" bufferPos-n32773Position-1 = "+(currentPBAPos-n32773Position-1);
                packBitArray[n32773Position] = (byte) (n32773Value - 3); // on ecrit nvalue -2 et -1 car +1 en lecture
                n32773Position = currentPBAPos - 2;

                assert packBitArray[n32773Position]     == value;
                assert packBitArray[n32773Position + 1] == value;
                //-- if it is end of current row, write little suite of 3 identical numbers. --//
                if (rowByte32773Pos == lastByte32773) {
                    packBitArray[n32773Position++] = (byte) -2;
                    packBitArray[n32773Position++] = value;
                    n32773Value = 0;
                    //-- check array cursor --//
                    currentPBAPos = n32773Position + 1;
                } else {
                    //-- else we initialize like a new suite number. --//
                    packBitArray[n32773Position + 1] = packBitArray[n32773Position + 2] = value;
                    //-- check array cursor --//
                    currentPBAPos = n32773Position + 3;
                    n32773Value = 3;
                }

            } else { //-- It is already into identical number suite --//
                assert currentPBAPos - n32773Position == 3 : "expected 3 found : "+(currentPBAPos - n32773Position);
                //-- add a value in current identical suite number.--//
                n32773Value++;
                //-- if it is end of current row or max compression value is reach. --//
                if (n32773Value == 128 || rowByte32773Pos == lastByte32773) {
                    /*
                     * if max value is reach and it is penultimate byte of the current row,
                     * if current suite is written, it remain an alone byte during next step which is a prohibited comportement.
                     * To conclude the last number of the current suite is keep,
                     * to constitute a little suite of 2 identical numbers which will be written during next step.
                     */
                    if (rowByte32773Pos == precLastByte32773) {
                        assert packBitArray[n32773Position] == 0 : "at n32773Position expected value = 0 found "+packBitArray[n32773Position];
                        packBitArray[n32773Position++] = (byte) (-n32773Value + 2);
                        assert packBitArray[n32773Position] == value : "at n32773Position + 1 expected value = "+value+" found "+packBitArray[n32773Position];
                        //-- shift last byte of precedently sequence --//
                        n32773Position++;
                        packBitArray[n32773Position + 1] = value;
                        currentPBAPos = n32773Position + 2;
                        n32773Value = 1;
                    } else {

                        packBitArray[n32773Position++] = (byte) (-n32773Value + 1);
                        assert packBitArray[n32773Position] == value : "expected val = "+value+" found = "+packBitArray[n32773Position];
                        //-- shift value --//
                        n32773Position++;
                        currentPBAPos = n32773Position + 1;
                        n32773Value = 0;
                    }
                }
            }
        } else {
            //-- check if precedently suite was an identical number suite. --//
            if (n32773Value > currentPBAPos - n32773Position - 1) {
                assert currentPBAPos - n32773Position == 3 : "expected bufferPos - n32773Position = 3 found : "+(currentPBAPos - n32773Position)+" with n32773value = "+n32773Value;
                assert n32773Value <= 128 : "expected value <= 128 found : "+n32773Value;
               /*
                * If it is last number of current row, 2 suite are written to avoid an alone byte at last step of row.
                * The precedently identical number suite is written minus one element
                * to permit to constitute an small distinct number suite of two elements to finish current row.
                */
               if (rowByte32773Pos == lastByte32773) {
                   /*
                    * write current suite with expected number minus one
                    * to avoid an alone byte in the next step
                    */
                   packBitArray[n32773Position++] = (byte) (-n32773Value + 2);
                   assert packBitArray[n32773Position] == packBitArray[n32773Position+1];
                   final byte sam = packBitArray[n32773Position++];
                   //-- write small suite of two element to finish row. --//
                   packBitArray[n32773Position++] = 1;
                   packBitArray[n32773Position++] = sam;
                   packBitArray[n32773Position++] = value;
                   n32773Value = 0;
                   currentPBAPos = n32773Position + 1;
               } else {
                   packBitArray[n32773Position++] = (byte) (-n32773Value + 1);
                   assert packBitArray[n32773Position] == packBitArray[n32773Position + 1];
                   n32773Position++;
                   packBitArray[n32773Position+1] = value;
                   n32773Value = 1;
                   currentPBAPos = n32773Position + 2;
               }

            } else {
                //-- continuity of distinct suite number. --//
                n32773Value++;
                packBitArray[currentPBAPos++] = value;
                assert n32773Value == currentPBAPos - n32773Position - 1 : "n32773Value = "+n32773Value+" pos-n32773Position-1 = "+(currentPBAPos-n32773Position-1);
                if (n32773Value == 128 || rowByte32773Pos == lastByte32773) {
                    /*
                     * if it is penultimate number of the current row,
                     * write current distinct number suite to avoid an alone number to the next step.
                     */
                    if (rowByte32773Pos == precLastByte32773) {
                        packBitArray[n32773Position] = (byte) (n32773Value - 2);
                        n32773Position = currentPBAPos-1;
                        packBitArray[currentPBAPos++] = value;
                        n32773Value = 1;
                    } else {
                        packBitArray[n32773Position] = (byte) (n32773Value - 1);
                        n32773Position = currentPBAPos++;
                        n32773Value = 0;
                    }
                }
            }
        }

        if (currentPBAPos + 3 >= packBitArray.length || rowByte32773Pos == lastByte32773) {
            assert currentPBAPos != n32773Position;
            channel.write(packBitArray, 0, n32773Position);
            currentPBAPos -= n32773Position;
            System.arraycopy(packBitArray, n32773Position, packBitArray, 0, currentPBAPos);//-- ici currentpba joue role de longueur et a deja ca valeur apres ecriture
            n32773Position = 0;
        }
        assert currentPBAPos > 0;
        rowByte32773Pos++;
    }

    /**
     * Write array in file in function of data type.
     *
     * @param tiffType type of array data.
     * @param array data array.
     * @throws IOException if problem during buffer writing.
     */
    private void writeArray(final Object array, final short tiffType) throws IOException {
        switch (tiffType) {
            case TYPE_ASCII     :
            case TYPE_BYTE      :
            case TYPE_UBYTE     : channel.write((byte[]) array); break;

            case TYPE_SHORT     :
            case TYPE_USHORT    : channel.writeShorts((short[]) array); break;

            case TYPE_INT       :
            case TYPE_UINT      : channel.writeInts((int[]) array); break;

            case TYPE_LONG      :
            case TYPE_ULONG     : channel.writeLongs((long[]) array); break;

            case TYPE_FLOAT     :
            case TYPE_DOUBLE    : channel.writeDoubles((double[]) array); break;

            case TYPE_RATIONAL  : // 2 long successif
            case TYPE_URATIONAL :
            default : throw new IllegalStateException("unknow type. type : "+tiffType);
        }
    }

    /**
     * Write tag at current {@link Buffer#position}.
     *
     * @param tag tag value.
     * @param type type of data which will be stored.
     * @param count data number.
     * @param offset {@link FileChannel#position() } where data will be stored.
     * @throws IOException if problem during buffer writing.
     */
    private void writeDefferedTag(short tag, short type, long count, long offset) throws IOException {
        channel.writeShort(tag);
        channel.writeShort(type);

        if (isBigTIFF) channel.writeLong(count);
        else           channel.writeInt((int) count);

        // gestion des tilecount et stripoff avec compression
        if (tag == TileByteCounts || tag == StripByteCounts) {
            byteCountTagPosition = channel.getStreamPosition();
        } else if (tag == TileOffsets || tag == StripOffsets) {
            offsetTagPosition = channel.getStreamPosition();
        }

        if (isBigTIFF) channel.writeLong(offset);
        else           channel.writeInt((int) offset);
    }

    /**
     * Write tag at current {@link Buffer#position}.<br/>
     * {@code offsetOrValue} parameter must be real datas value.<br/><br/>
     * Moreover {@code count * value length} dont exceed 4 bytes.
     *
     * @param tag tag value.
     * @param type type of data which will be stored.
     * @param count data number.
     * @param value data.
     * @throws IOException if problem during buffer writing.
     */
    private void writeTag(final short tag, final short type, final long count, final Object value) throws IOException {
        channel.writeShort(tag);
        channel.writeShort(type);
        final int dataSize;
        if (isBigTIFF) {
            dataSize = Long.SIZE;
            channel.writeLong(count);
        } else {
            dataSize = Integer.SIZE;
            channel.writeInt((int) count);
        }

        switch (type) {
                case TYPE_ASCII :
                case TYPE_BYTE  :
                case TYPE_UBYTE : {
                    final int dataCount = dataSize / Byte.SIZE;
                    assert count <= dataCount;
                    /*
                     * Fill array with the expected count number and complete
                     * the resting datasize with byte value 0.
                     */
                    for (int i = 0; i < dataCount; i++) {
                        if (i < count) {
                            final byte b = Array.getByte(value, i);
                            channel.writeByte(b);
                        } else {
                            channel.writeByte(0);
                        }
                    }
                    break;
                }
                case TYPE_SHORT  :
                case TYPE_USHORT : {
                    final int dataCount = dataSize / Short.SIZE;
                    assert count <= dataCount;
                    for (int i = 0; i < dataCount; i++) {
                        if (i < count) {
                            final short s = Array.getShort(value, i);
                            channel.writeShort(s);
                        } else {
                            channel.writeShort((short) 0);
                        }
                    }
                    break;
                }
                case TYPE_INT  :
                case TYPE_UINT : {
                    final int dataCount = dataSize / Integer.SIZE;
                    assert count == dataCount;
                    for (int i = 0; i < dataCount; i++) {
                        if (i < count) {
                            final int in = Array.getInt(value, i);
                            channel.writeInt(in);
                        } else {
                            channel.writeInt(0);
                        }
                    }
                    break;
                }
                case TYPE_LONG  :
                case TYPE_ULONG : {
                    assert isBigTIFF;
                    assert count == 1;
                    final long l = Array.getLong(value, 0);
                    channel.writeLong(l);
                    break;
                }
//                case TYPE_RATIONAL :
//                case TYPE_URATIONAL :
//                case TYPE_FLOAT :
//                case TYPE_DOUBLE : {
//                    break;
//                }
                default : throw new IllegalStateException("Unknown type : "+type);
            }
    }

    /**
     * Ensures that the channel is open. If the channel is already open, then this method
     * does nothing.
     *
     * @throws IllegalStateException if the input is not set.
     * @throws IOException If an error occurred while opening the channel.
     */
    private void open(long[] ifdPosition) throws IllegalStateException, IOException {

        if (channel == null) {
            if (output == null) {
                throw new IllegalStateException(error(Errors.Keys.NO_IMAGE_INPUT));
            }

            final WritableByteChannel wBC;
            if (output instanceof String) {
                wBC = new FileOutputStream((String) output).getChannel();
            } else if (output instanceof File) {
                wBC = new FileOutputStream((File)output).getChannel();
            } else if (output instanceof FileOutputStream) {
                ((FileOutputStream) output).flush();
                wBC = ((FileOutputStream) output).getChannel();
            } else {
                throw new IOException("Output object is not a valid file or input stream.");
            }

            currentBO = ByteOrder.nativeOrder();
            final ByteBuffer buff = ByteBuffer.allocateDirect(8196);
            buff.order(currentBO);
            channel = new ChannelImageOutputStream("TiffWriter", wBC, buff);

            if (currentBO.equals(ByteOrder.BIG_ENDIAN)) {//MM
                channel.writeByte((byte) 'M');
                channel.writeByte((byte) 'M');
            } else {//II
                channel.writeByte((byte) 'I');
                channel.writeByte((byte) 'I');
            }

            if (isBigTIFF) {
                channel.writeShort((short) 43);
                channel.writeShort((short) 8);
                channel.writeShort((short) 0);
                ifdPosition[0] = channel.getStreamPosition() + TYPE_SIZE[TYPE_IFD8];
                channel.writeLong(ifdPosition[0]);
            } else {
                channel.writeShort((short) 42);
                ifdPosition[0] = channel.getStreamPosition() + TYPE_SIZE[TYPE_IFD];
                channel.writeInt((int) ifdPosition[0]);
            }
        }
    }

    /**
     * Formats an error message for an invalid TIFF file.
     *
     * @todo Localize.
     */
    private IIOException invalidFile(final String cause) {
        return new IIOException("Invalid value for record " + cause);
    }

    /**
     * Formats an error message with no argument.
     */
    private String error(final short key) {
        return Errors.getResources(getLocale()).getString(key);
    }

    /**
     * Formats an error message with one argument.
     */
    private String error(final short key, final Object arg0) {
        return Errors.getResources(getLocale()).getString(key, arg0);
    }

    /**
     * Formats an error message with two argument.
     */
    private String error(final short key, final Object arg0, final Object arg1) {
        return Errors.getResources(getLocale()).getString(key, arg0, arg1);
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            if (channel != null) {
                channel.flush();
                if (output instanceof File) channel.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(TiffImageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOutput(Object output) {
        final Object out = (output instanceof String) ? new File((String) output) : output;
        super.setOutput(out);
        ifdPosition     = new long[2];
        headProperties  = null;
        packBitArray    = new byte[8196];
        currentPBAPos   = 0;
        rowByte32773Pos = 0;
        metaHeads = new Map[4];
        metaIndex = 0;
        try {
            if (channel != null) {
                channel.flush();
                if (out instanceof File) channel.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(TiffImageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        channel = null;
    }

   /**
     * Service provider interface (SPI) for {@code TiffImageWriter}.
     *
     * @author Rémi Maréchal (Geomatys)
     * @author Alexis Manin  (Geomatys)
     * @version 3.16
     * @see TiffImageReader.Spi
     * @module
     */
    public static class Spi extends SpatialImageWriter.Spi {

       static final Class<?>[] TYPES = new Class<?>[] {
               File.class,
               //URI.class,
               //URL.class,
               String.class, // To be interpreted as file path.
// TODO          OutputStream.class,
//               ImageOutputStream.class
               FileOutputStream.class
       };
        /**
         * Creates a provider which will use the given format for writing pixel values.
         */
        public Spi() {
            super();
            names           = new String[] {"geotiff", "geotif", "tiff", "tif"};
            MIMETypes       = new String[] {"image/x-geotiff", "image/tiff;subtype=geotiff"};
            pluginClassName = "org.geotoolkit.image.io.plugin.TiffImageWriter";
            vendorName      = "Geotoolkit.org";
            version         = Utilities.VERSION.toString();
            outputTypes     = TYPES;
            nativeImageMetadataFormatName = "geotiff";
            extraImageMetadataFormatNames = new String[]{SpatialMetadataFormat.GEOTK_FORMAT_NAME};
            nativeStreamMetadataFormatName = "geotiff";
            extraStreamMetadataFormatNames = new String[]{SpatialMetadataFormat.GEOTK_FORMAT_NAME};
        }

        /**
         * Returns a brief, human-writable description of this service provider.
         *
         * @param  locale The locale for which the return value should be localized.
         * @return A description of this service provider.
         */
        @Override
        public String getDescription(final Locale locale) {
            return "Tiff / Geotiff format.";
        }

       @Override
       public boolean canEncodeImage(ImageTypeSpecifier type) {
           return true;
       }

       /**
         * Creates a new instance of the Tiff writer.
         *
         * @param  extension A plug-in specific extension object, or {@code null}.
         * @return A new writer.
         * @throws IOException If the writer can not be created.
         */
        @Override
        public ImageWriter createWriterInstance(final Object extension) throws IOException {
            return new TiffImageWriter(this);
        }
    }
}
