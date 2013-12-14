/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.plugin;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
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
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageOutputStream;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.io.ImageWriterAdapter;
import org.geotoolkit.image.io.SpatialImageWriter;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Remi Marechal (Geomatys).
 */
public class TiffImageWriter extends SpatialImageWriter {

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
     * Define size in {@code Byte} of a Tiff tag.<br/>
     * Usualy for BigTiff a tag has a size of 20 Bytes, and in case of standard Tiff a tag has a size of 12 Bytes.
     */
    private int currentTiffTagLength;

    /**
     * The channel to the TIFF file. Will be created from the {@linkplain #input} when first needed.
     */
    private FileChannel channel;

    /**
     * The buffer for reading blocks of data.
     */
    private final ByteBuffer buffer;

    /**
     * Position in the {@linkplain #channel} of the current IFD in the {@linkplain #buffer}.
     */
    private int currentIFDPosition;

    /**
     * Position in the {@linkplain #channel} of the next IFD in the {@linkplain #buffer}.
     */
    private int nextIFDPosition;

    /**
     * Current position of the file channel. Stored for avoiding multiple calls to
     * {@link FileChannel#position(long)} while reading consecutive block of data.
     */
    private long filePosition;

    /**
     * {@code Boolean} to define if image will write with bigTiff specification or standard tiff specification.
     */
    private boolean isBigTIFF = false;

    /**
     * Define if current image is tiled or not.
     */
    private boolean isTiled;

    /**
     * Destination image tile width.
     */
    private int currentTW;

    /**
     * Destination image tile height.
     */
    private int currentTH;

    /**
     * Destination image tile number in X direction.
     */
    private int currentNumXT;

    /**
     * Destination image tile number in Y direction.
     */
    private int currentNumYT;

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
     * Bit number per sample about current writen image.
     */
    private short bitPerSample;

    /**
     * Sample number for each pixel about current writen image.
     */
    private int samplePerPixel;

    /**
     * {@code Rectangle} which define boundary of the current writen image.<br/>
     * See {@linkplain #computeRegions(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.
     */
    private Rectangle imageBoundary;

    /**
     * {@code Rectangle} which define boundary of the area which will be writen in the image space.<br/>
     * See {@linkplain #computeRegions(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.
     */
    private Rectangle srcRegion;

    /**
     * {@code Rectangle} which define boundary of the destination area after all  transformations from {@link ImageWriteParam} attributs.<br/>
     * See {@linkplain #computeRegions(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.
     */
    private Rectangle destRegion;

    /**
     * {@code Rectangle} which define boundary in the image space, of the source area but padded to be multiply by tile properties.<br/>
     * Only use during write by tile.<br/>
     * See {@linkplain #computeRegions(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.<br/>
     * See also {@linkplain #writeImageByTiles(java.awt.image.RenderedImage)}.
     */
    private Rectangle tileRegion;

    /**
     * Use to define stripByteCount in case of strip writing or tileByteCount in case of tile writing.
     */
    private int currentByteCount;

    /**
     * Map organize in ascending order by {@code Integer} tag value, and contain all tiff tag in attempt to be writen.
     */
    private TreeMap<Integer, Map> headProperties;

    /**
     * Current {@link ImageWriteParam} in ralation with the current image which will be writen.
     */
    private ImageWriteParam currentImgParam;

    /**
     *
     * @param provider
     * @throws IOException
     */
    TiffImageWriter(final TiffImageWriter.Spi provider) throws IOException {
        super(provider);
        buffer                 = ByteBuffer.allocateDirect(8196);
        headProperties         = new TreeMap<Integer, Map>();
    }

   /**
    *
    * @param streamMetadata
    * @param image
    * @param param
    * @throws IOException
    */
    @Override
    public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param image
     * @throws IOException
     */
    @Override
    public void write(IIOImage image) throws IOException {
        // -- clear precedently image properties---//
        headProperties.clear();

        final IIOMetadata iioMeth = image.getMetadata();
        final Node iioNode = iioMeth.getAsTree(iioMeth.getNativeMetadataFormatName());
        final RenderedImage img   = image.getRenderedImage();
        addMetadataProperties(iioNode);
        write(img);

    }

    /**
     *
     * @param imageIndex
     * @param image
     * @param param
     * @throws IOException
     */
    @Override
    public void writeInsert(final int imageIndex, final IIOImage image, final ImageWriteParam param) throws IOException {

        super.writeInsert(imageIndex, image, param); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param image
     * @param param
     * @throws IOException
     */
    public void write(final RenderedImage image, final ImageWriteParam param) throws IOException {
        this.currentImgParam = param;
        // -- clear precedently image properties---//
        headProperties.clear();
        write(image);
    }

    /**
     *
     * @param image
     * @throws IOException
     */
    @Override
    public void write(final RenderedImage image) throws IOException {
        if (channel != null) {
            //  We authorize to write none big tiff image after big tiff already writen but not the inverse
            if (isBigTIFF != isBigTiff(image)) {
                if (!isBigTIFF)
                throw new IllegalArgumentException("You can't write a bigtiff image when you have already writen none bigtiff image.");
            }

            /*
             * If an image has already been writen we stipulate next ifd position.
             */
            final int offset = (int) (channel.position() + buffer.position());
            buffer.flip();
            channel.write(buffer);
            buffer.clear();

            channel.position(nextIFDPosition);

            if (isBigTIFF) buffer.putLong(offset);
            else buffer.putInt(offset);

            buffer.flip();
            channel.write(buffer);
            buffer.clear();
            channel.position(offset);
            currentIFDPosition = offset;
//            // -- clear precedently image properties---//
//            headProperties.clear();
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
        open();
        // -- add image properties in a list in attempt to writing.
        addImageProperties(image);
        // -- write all image properties.
        writeHeads();
        if (isTiled) {
            writeImageByTiles(image);
        } else {
            writeImageByStrips(image, currentImgParam);
        }
    }

    /**
     * Add metadata properties in {@link #headProperties} list to write appropriate tags.
     *
     * @param iioRootNode Tree root node from metadata.
     */
    final void addMetadataProperties(final Node iioRootNode) {
        if (!iioRootNode.getLocalName().equalsIgnoreCase(TAG_GEOTIFF_IFD)) {
            throw new IllegalStateException("Tree root metadata node should have name : \""+TAG_GEOTIFF_IFD+"\"");
        }
        final NodeList iioNodeList = iioRootNode.getChildNodes();
        final int iioLength = iioNodeList.getLength();
        for (int i = 0; i < iioLength; i++) {
            addNodeProperties(iioNodeList.item(i));
        }

    }

    /**
     * Study current metadata node from {@link IIOMetadata} and add properties if its a Tiff field node.
     *
     * @param tagBody current tiff field tag.
     */
    final void addNodeProperties(final Node tagBody) {
        if (!tagBody.getLocalName().equalsIgnoreCase(TAG_GEOTIFF_FIELD)) {
            throw new IllegalStateException("Internal metadata node should have name : \""+TAG_GEOTIFF_FIELD+"\"");
        }
        // -- get attributs
        final NamedNodeMap tagNamesMap = tagBody.getAttributes();
        // -- tag value
        final int tagnumber = Integer.decode(tagNamesMap.getNamedItem(ATT_NUMBER).getNodeValue());
        short type   = -1;
        int count    = -1;
        Object value = null;
        final NodeList iioNodeList = tagBody.getChildNodes();
        // normalement taille de 1
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
                type = TYPE_SHORT;
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
                // unknow tag do nothing
            }
        }
        if (count != -1) { // -- to avoid unknow tag writing --//
            addProperty(tagnumber, type, count, value);
        }
    }

    /**
     * Return {@code true} if image as a weight over than 4 Go else {@code false}.<br/>
     * In tiff specification an image is considered as a BigTiff when its weigth exceed 4Go.
     * @param image
     * @return {@code true} if image is a {@code BigTiff} else {@code false}.
     */
    private boolean isBigTiff(final RenderedImage image) {
        // for testing
        return true;
//        final int imgWidth     = image.getWidth();
//        final int imgHeight    = image.getHeight();
//        final SampleModel sm   = image.getSampleModel();
//        final int[] sampleSize = sm.getSampleSize();
//        int pixelSize = 0;
//        for (int i = 0; i < sampleSize.length; i++) {
//            pixelSize += sampleSize[i];
//        }
//        final int imageWeight = imgWidth * imgHeight * pixelSize / Byte.SIZE;
//        return imageWeight >= 4E9;// >= 4Go
    }

    /**
     * Analyze image and get all properties at minimum, necessary to write an image in tiff format.
     *
     * @param image
     */
    private void addImageProperties(final RenderedImage image) {

        /*
         * Define some attributs in function of ImageWriterParam and image properties.
         */
        computeRegions(image, currentImgParam);

        //imagewidth
        final int width          = destRegion.width;
        if (width > 0xFFFF) { // check if width is a short or int
            addProperty(ImageWidth, TYPE_INT, 1, new int[]{ width});
        } else {
            addProperty(ImageWidth, TYPE_USHORT, 1, new short[]{(short) width});
        }

        //imageheight (length)
        final int height          = destRegion.height;
        if (height > 0xFFFF) {
            addProperty(ImageLength, TYPE_INT, 1, new int[]{height});
        } else {
            addProperty(ImageLength, TYPE_USHORT, 1, new short[]{(short) height});
        }

        final SampleModel sm     = image.getSampleModel();
        //sample per pixel
        samplePerPixel = sm.getNumDataElements();
        assert samplePerPixel <= 0xFFFF : "SamplePerPixel exceed short max value"; // -- should never append
        addProperty(SamplesPerPixel, TYPE_USHORT, 1, new short[]{(short) samplePerPixel});

        // bitpersamples
        final int[] sampleSize = sm.getSampleSize();// sample size in bits
        assert this.samplePerPixel == sampleSize.length : "";
        for (int i = 1; i < samplePerPixel; i++) {
            if (sampleSize[i-1] != sampleSize[i]) {
                throw new IllegalStateException("different sample size is not supported");
            }
        }
        assert sampleSize[0] <= 0xFFFF : "BitsPerSample exceed short max value";
        this.bitPerSample = (short) sampleSize[0];
        short[] bitspersample = new short[sampleSize.length];
        Arrays.fill(bitspersample, this.bitPerSample);
        addProperty(BitsPerSample, TYPE_USHORT, sampleSize.length, bitspersample);

        final ColorModel colorMod = image.getColorModel();

        //photometric interpretation
        final short photoInter = getPhotometricInterpretation(colorMod);
        addProperty(PhotometricInterpretation, TYPE_USHORT, 1, new short[]{ photoInter});

        // color map
        if (photoInter == 3) {
            // on construit un color map adequate
            IndexColorModel indexColorMod = ((IndexColorModel)colorMod);
            int mapSize = indexColorMod.getMapSize();
            int[] rgbs  = new int[mapSize];
            indexColorMod.getRGBs(rgbs);// ok c la bonne method pour recup le tableau
            final short[] tiffMap = buildTiffMapArray(rgbs);
            addProperty(ColorMap, TYPE_USHORT, tiffMap.length, tiffMap);
        }

        // compression
        final short compression = 1; // voir plus tard pour packbits a recup dans le imageWriteParam
        assert compression <= 0xFFFF : "compression exceed short max value";
        addProperty(Compression, TYPE_USHORT, 1, new short[]{compression});

        // planar configuration
        final short planarConfig = 1;
        assert planarConfig <= 0xFFFF : "PlanarConfiguration exceed short max value";
        addProperty(PlanarConfiguration, TYPE_USHORT, 1, new short[]{planarConfig});

        /*
         * Some globals class attribut have been already initialized to define writing made.
         * See method computeRegion.
         */
        if (currentTW != 0 && currentTH != 0) {
            addTileOffsetsProperties(currentTW, currentTH, currentNumXT, currentNumYT);
        } else {
            addStripOffsetProperties(destRegion.width, destRegion.height);
        }
    }

    /**
     * Add appropriate tag in {@link #headProperties}
     *
     * @param width image width.
     * @param height image height.
     */
    private void addStripOffsetProperties(final int width, final int height) {
        isTiled = false;

        // row per strip
        final short rowperstrip = 1;
        addProperty(RowsPerStrip, TYPE_USHORT, 1, new short[]{rowperstrip});

        // stripbytecount
        currentByteCount = width * samplePerPixel * bitPerSample / Byte.SIZE;
        final short oSBCType;
        final int oSBCCount = height; // one strip by row
        final Object oSBCAttribut;

        if (currentByteCount > 0xFFFF) {
            // en long
            oSBCType = TYPE_INT; // in tiff spec long means write on 4 byte like Integer in java
            final int[] stripsbytecount = new int[height];
            Arrays.fill(stripsbytecount, currentByteCount);
            oSBCAttribut = stripsbytecount;
        } else {
            // en short
            oSBCType = TYPE_USHORT; // in tiff spec long means write on 4 byte like Integer in java
            final short[] stripsbytecount = new short[height];
            Arrays.fill(stripsbytecount, (short) currentByteCount);
            oSBCAttribut = stripsbytecount;
        }
        addProperty(StripByteCounts, oSBCType, oSBCCount, oSBCAttribut);
        addProperty(StripOffsets, TYPE_INT, height, null);
    }

    /**
     * Write {@link RenderedImage} by tile with all tile properties precedently setted by {@link ImageWriteParam}.
     *
     * @param image image which will be writen.
     * @throws IIOException if type from image raster buffer are not supported.
     * @throws IOException if problem during writing.
     */
    private void writeImageByTiles(final RenderedImage image) throws IIOException, IOException {

        final int subsampleX;
        final int subsampleY;

        if (currentImgParam != null) {
            subsampleX = currentImgParam.getSourceXSubsampling();
            subsampleY = currentImgParam.getSourceYSubsampling();
        } else {
            subsampleX = subsampleY = 1;
        }

        final SampleModel sm       = image.getSampleModel();
        final int imagePixelStride = sm.getNumDataElements();

        // ----------- Area define by tiles ----------//
        final int minx = tileRegion.x;
        final int miny = tileRegion.y;
        final int maxx = minx + tileRegion.width;
        final int maxy = miny + tileRegion.height;

        final int subsampletileWidth  = currentTW * subsampleX;
        final int subsampletileHeight = currentTH * subsampleY;


        // ------------ Image properties ------------//
        final int imageMinX = image.getMinX();
        final int imageMinY = image.getMinY();

        final int imageTileWidth = image.getTileWidth();
        final int imageTileHeight = image.getTileHeight();

        final int imageTileGridXOffset    = image.getTileGridXOffset();
        final int imageMaxTileGridXOffset = imageTileGridXOffset + image.getNumXTiles();
        final int imageTileGridYOffset    = image.getTileGridYOffset();
        final int imageMaxTileGridYOffset = imageTileGridYOffset + image.getNumYTiles();

        //---------------- raster properties -----------------//
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
        final int paddingXLength = ((maxx > srcRegionMaxX)  ?  (maxx - srcRegionMaxX) / subsampleX : 0) * imagePixelStride;
        final int paddingYLength = ((maxy > srcRegionMaxY)  ? ((maxy - srcRegionMaxY) / subsampleY) * currentTW : 0) * imagePixelStride;

        Buffer destBuffer;
        final int position = buffer.position();
        final int limit    = buffer.limit();
        buffer.clear();
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:   {
                destBuffer = buffer;
                paddingXArray = new byte[paddingXLength];
                paddingYArray = new byte[paddingYLength];
                break;
            }
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT: {
                destBuffer = buffer.asShortBuffer();
                paddingXArray = new short[paddingXLength];
                paddingYArray = new short[paddingYLength];
                break;
            }
            case DataBuffer.TYPE_INT : {
                destBuffer = buffer.asIntBuffer();
                paddingXArray = new int[paddingXLength];
                paddingYArray = new int[paddingYLength];
                break;
            }
            case DataBuffer.TYPE_FLOAT:  {
                destBuffer = buffer.asFloatBuffer();
                paddingXArray = new float[paddingXLength];
                paddingYArray = new float[paddingYLength];
                break;
            }
            case DataBuffer.TYPE_DOUBLE: {
                destBuffer = buffer.asDoubleBuffer();
                paddingXArray = new double[paddingXLength];
                paddingYArray = new double[paddingYLength];
                break;
            }
            default: throw new IIOException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE, initRastBuff.getClass()));
        }
        buffer.limit(limit).position(position);

        if (image.getNumXTiles()  == currentNumXT
         && image.getNumYTiles()  == currentNumYT
         && image.getTileWidth()  == currentTW
         && image.getTileHeight() == currentTH
         && tileRegion.equals(imageBoundary)) {
            // prendre les raster 1 a 1 et copier les buffers directements
            final int writeTileLength = currentTH * currentTW * imagePixelStride;
            for (int bank = 0; bank < numbanks; bank++) {
                for (int ty = imageTileGridYOffset; ty < imageMaxTileGridYOffset; ty++) {
                    for (int tx = imageTileGridXOffset; tx < imageMaxTileGridXOffset; tx++) {
                        // -- get the following image raster
                        final Raster imageTile        = image.getTile(tx, ty);
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
                        write(destBuffer, sourceArray, dataType, 0, writeTileLength);
                    }
                }
            }
            return;
        }

        for (int bank = 0; bank < numbanks; bank++) {
            for (int cty = 0; cty < currentNumYT; cty++) {
                for (int ctx = 0; ctx < currentNumXT; ctx++) {
                    // calcul des coord de la tuile courante
                    //-- compute current destination tile coordinates
                    final int ctRminy = miny + cty * subsampletileHeight;
                    final int ctRmaxy = ctRminy + subsampletileHeight;
                    final int ctRminx = minx + ctx * subsampletileWidth;
                    final int ctRmaxx = ctRminx + subsampletileWidth;

                    // we looking for which tiles from image will be used to fill destination tile.
//                    // on cherche maintenant a savoir quel tuile de l'image on utilisera pour remplir cette tuile destination
                    final int imageminTy = imageTileGridYOffset + (ctRminy - imageMinY) / imageTileHeight;
                    int imagemaxTy = imageTileGridYOffset + (ctRmaxy - imageMinY + imageTileHeight - 1) / imageTileHeight;
                    // -- in cause of padding imagemaxTy should exceed max tile grid offset from image.
                    imagemaxTy = Math.min(imagemaxTy, imageMaxTileGridYOffset);

                    final int imageminTx = imageTileGridXOffset + (ctRminx - imageMinX) / imageTileWidth;
                    int imagemaxTx = imageTileGridXOffset + (ctRmaxx - imageMinX + imageTileWidth - 1) / imageTileWidth;
                    // -- in cause of padding imagemaxTx should exceed max tile grid offset from image.
                    imagemaxTx = Math.min(imagemaxTx, imageMaxTileGridXOffset);

                    int cuImgTileMinY = imageMinY + (imageminTy - imageTileGridYOffset) * imageTileHeight;
                    int cuImgTileMaxY = cuImgTileMinY + imageTileHeight;
                    for (int imgTy = imageminTy; imgTy < imagemaxTy; imgTy++) {

                        // row intersection on y axis
                        final int deby  = Math.max(cuImgTileMinY, ctRminy);
                        final int tendy = Math.min(cuImgTileMaxY, ctRmaxy);
                        final int endy  = Math.min(srcRegionMaxY, tendy);

                        // offset in pixels number in source image currently tile
                        final int stepOffsetBeforeY = (deby - cuImgTileMinY) * imageTileWidth;

                        for (int y = deby; y < endy; y += subsampleY) {

                            // -- offset in y direction
                            final int stepY = (y - deby) * imageTileWidth;

                            // -- current image tile coordinates in X direction
                            int cuImgTileMinX = imageMinX + (imageminTx - imageTileGridXOffset) * imageTileWidth;
                            int cuImgTileMaxX = cuImgTileMinX + imageTileWidth;

                            // parcour les tuiles sur X
                            for (int imgTx = imageminTx; imgTx < imagemaxTx; imgTx++) {

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

                                // intersection
                                // gerer les decalages offsets x et y dans le databuffer
                                final int debx  = Math.max(cuImgTileMinX, ctRminx);
                                final int tendx = Math.min(cuImgTileMaxX, ctRmaxx);
                                final int endx  = Math.min(srcRegionMaxX, tendx);
                                final int currentXSubSample;
                                final int writeSize;
                                if (subsampleX == 1) {
                                    currentXSubSample = endx - debx;
                                    writeSize         = currentXSubSample * imagePixelStride; // eh !! oui on ecrit en coord tableau et pas en nbre de byte
                                } else {
                                    currentXSubSample = subsampleX;
                                    writeSize         = imagePixelStride;
                                }

                                for (int x = debx; x < endx; x += currentXSubSample) {
                                    final int writeArrayOffset = (stepOffsetBeforeY + stepY + (x-cuImgTileMinX)) * imagePixelStride;
                                    write(destBuffer, sourceArray, dataType, writeArrayOffset, writeSize);
                                }

                                // -- padding in x direction
                                if (ctRmaxx > srcRegionMaxX) {
                                    assert ((endx - debx + subsampleX - 1) / subsampleX + paddingXLength / imagePixelStride) == currentTW : "write width = "+((endx - debx + subsampleX - 1) / subsampleX + paddingXLength / imagePixelStride);
                                    write(destBuffer, paddingXArray, dataType, 0, paddingXLength);
                                }

                                // -- next tile X coordinates
                                cuImgTileMinX += imageTileWidth;
                                cuImgTileMaxX += imageTileWidth;
                            }
                        }

                        // -- padding in y direction.
                        if (ctRmaxy > srcRegionMaxY) {
                            write(destBuffer, paddingYArray, dataType, 0, paddingYLength);
                        }

                        // -- next tile Y coordinates.
                        cuImgTileMinY += imageTileHeight;
                        cuImgTileMaxY += imageTileHeight;
                    }
                }
            }
        }
    }

    /**
     * Write directly datas from sourceArray, which is an array of datatype type,
     * in writebuffer at arrayOffset position with a length of arrayLength.
     *
     * @param writeBuffer destination buffer where data from sourceArray, will be writen.
     * @param sourceArray sample source array data.
     * @param datatype type of data within sourceArray.
     * @param arrayOffset offset in the source array of the first sample which will be writen.
     * @param arrayLength number of sample which will be writen.
     * @throws IIOException
     * @throws IOException
     * @see ByteBuffer#put(byte[], int, int)
     * @see ShortBuffer#put(short[], int, int)
     * @see IntBuffer#put(int[], int, int)
     * @see FloatBuffer#put(float[], int, int)
     * @see DoubleBuffer#put(double[], int, int)
     */
    private void write(final Buffer writeBuffer, final Object sourceArray, final int datatype, final int arrayOffset, final int arrayLength) throws IIOException, IOException {
        int writePos           = arrayOffset;
        final int endWritePos  = arrayOffset + arrayLength;
        final int buffCapacity = writeBuffer.capacity();

        // -- sample number which will contained in buffer.
        final int buffSampleCapacity = buffCapacity / (bitPerSample / Byte.SIZE);

        while (writePos < endWritePos) {
            final int currentWriteLength = Math.min(writePos + buffSampleCapacity, endWritePos) - writePos;
            adjustBuffer(currentWriteLength * (bitPerSample / Byte.SIZE));
            writeBuffer.limit(buffer.limit()).position(buffer.position());
            switch(datatype) {
                case DataBuffer.TYPE_BYTE: {
                    // cast du buffer et du tableau
                    ((ByteBuffer)writeBuffer).put((byte[])sourceArray, writePos, currentWriteLength);// problem du buffer a regler
                    break;
                }
                case DataBuffer.TYPE_USHORT:
                case DataBuffer.TYPE_SHORT: {
                    ((ShortBuffer)writeBuffer).put((short[])sourceArray, writePos, currentWriteLength);
                    break;
                }
                case DataBuffer.TYPE_INT:  {
                    ((IntBuffer)writeBuffer).put((int[])sourceArray, writePos, currentWriteLength);
                    break;
                }
                case DataBuffer.TYPE_FLOAT:  {
                    ((FloatBuffer)writeBuffer).put((float[])sourceArray, writePos, currentWriteLength);
                    break;
                }
                case DataBuffer.TYPE_DOUBLE: {
                    ((DoubleBuffer)writeBuffer).put((double[])sourceArray, writePos, currentWriteLength);
                    break;
                }
                default: throw new IIOException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE, writeBuffer.getClass()));
            }
            writePos += currentWriteLength;
        }


    }

    /**
     * Study tile properties to be in accordance with tiff specification.
     *
     * @param tileWidth
     * @param tileHeight
     * @param numXTile
     * @param numYTile
     */
    private void addTileOffsetsProperties(final int tileWidth, final int tileHeight, final int numXTile, final int numYTile) {
        isTiled = true;

        // tilewidth
        final short tilWType;
        final Object tilWAttribut;
        if (tileWidth > 0xFFFF) {
            // en long
            tilWType = TYPE_INT; // in tiff spec long means write on 4 bytes like Integer in java
            tilWAttribut = new int[]{tileWidth};
        } else {
            // en short
            tilWType = TYPE_USHORT; // in tiff spec long means write on 4 bytes like Integer in java
            tilWAttribut = new short[]{(short)tileWidth};
        }
        addProperty(TileWidth, tilWType, 1, tilWAttribut);
        // -------------------------------------------//

        // tileheight
        final short tilHType;
        final Object tilHAttribut;
        if (tileHeight > 0xFFFF) {
            // en long
            tilHType = TYPE_INT; // in tiff spec long means write on 4 byte like Integer in java
            tilHAttribut = new int[]{tileHeight};
        } else {
            // en short
            tilHType = TYPE_USHORT; // in tiff spec long means write on 4 byte like Integer in java
            tilHAttribut = new short[]{(short)tileHeight};
        }
        addProperty(TileLength, tilHType, 1, tilHAttribut);
        // -------------------------------------------//

        final int numTiles = numXTile * numYTile;

        // tileoffsets
        currentByteCount = tileWidth * tileHeight * samplePerPixel * bitPerSample / Byte.SIZE;
        Object oTBCAttribut;
        final short oTBCType;
        if (currentByteCount > 0xFFFF) {
            // en long
            oTBCType = TYPE_INT; // in tiff spec long means write on 4 byte like Integer in java
            final int[] stripsbytecount = new int[numTiles];
            Arrays.fill(stripsbytecount, currentByteCount);
            oTBCAttribut = stripsbytecount;
        } else {
            // en short
            oTBCType = TYPE_USHORT; // in tiff spec long means write on 4 byte like Integer in java
            final short[] stripsbytecount = new short[numTiles];
            Arrays.fill(stripsbytecount, (short) currentByteCount);
            oTBCAttribut = stripsbytecount;
        }
        addProperty(TileByteCounts, oTBCType, numTiles, oTBCAttribut);
        addProperty(TileOffsets, TYPE_INT, numTiles, null);
    }

    /**
     * Compute {@link Rectangle} which represent writing area iteration on image.
     *
     * @param image image which will be writen.
     * @param param contain writing informations or none if param is null.
     * @return {@link Rectangle} which represent writing area iteration on image.
     */
    private void computeRegions(final RenderedImage image, final ImageWriteParam param) {
        final int imgWidth  = image.getWidth();
        final int imgHeight = image.getHeight();
        final int minx      = image.getMinX();
        final int miny      = image.getMinY();

        imageBoundary = new Rectangle(minx, miny, imgWidth, imgHeight);
        if (param == null) {
            srcRegion  = imageBoundary;
            destRegion = new Rectangle(srcRegion);
            if ((image.getNumXTiles() > 1 || image.getNumYTiles() > 1)
                    && image.getWidth()      % image.getTileWidth()  == 0 // to avoid padding
                    && image.getHeight()     % image.getTileHeight() == 0
                    && image.getTileWidth()  % 16 == 0                    // to verify compatibility with compression (tiff spec).
                    && image.getTileHeight() % 16 == 0) {
                tileRegion   = srcRegion;
                currentTW    = image.getTileWidth();
                currentTH    = image.getTileHeight();
                currentNumXT = srcRegion.width  / currentTW;
                currentNumYT = srcRegion.height / currentTH;
            } else {
                currentTW = currentTH = currentNumXT = currentNumYT = 0;
            }
        } else {

            // param not null
            final Rectangle paramRect = (param.getSourceRegion() == null) ? new Rectangle(imageBoundary) : param.getSourceRegion();

            // in case of subsampling different of 1 with an offset.
            paramRect.translate(param.getSubsamplingXOffset(), param.getSubsamplingYOffset());

            if (!imageBoundary.intersects(paramRect)) {
                throw new IllegalStateException("src region from ImageWriterParam must intersect image boundary.");
            }
            srcRegion = imageBoundary.intersection(paramRect);// on est en coordonnées images

            final int srcXsubsampling = param.getSourceXSubsampling();
            final int srcYsubsampling = param.getSourceYSubsampling();
            assert srcYsubsampling >= 1;
            assert srcXsubsampling >= 1;

            /*
             * Try catch is a wrong way, but ImageWriteParam interface doen't have any method
             * to know if tile dimension has already been setted.
             */
            try {
                currentTW  = param.getTileWidth();
                currentTH  = param.getTileHeight();
            } catch (Exception ex) {
                currentTW  = currentTH = 0;
            }

            if (currentTW > 0 && currentTH > 0) {
                final int cuImgTW = currentTW * srcXsubsampling;
                final int cuImgTH = currentTH * srcYsubsampling;
                currentNumXT = (srcRegion.width  + cuImgTW - 1) / cuImgTW;
                currentNumYT = (srcRegion.height + cuImgTH - 1) / cuImgTH;
                if (currentTW % 16 != 0)
                        throw new IllegalStateException("To be in accordance with tiff specification tile width must be multiple of 16. Current tile width = "+currentImgParam.getTileWidth());
                if (currentTH % 16 != 0)
                        throw new IllegalStateException("To be in accordance with tiff specification tile height must be multiple of 16. Current tile height = "+currentImgParam.getTileHeight());
                tileRegion = new Rectangle(srcRegion.x, srcRegion.y, currentNumXT * cuImgTW, currentNumYT * cuImgTH);

            } else {
                // param not null with tile not specify
                tileRegion = new Rectangle(srcRegion);


                // on va essayer de voir si les tuiles de l'image peuvent correspondre avec la srcregion
                if ((image.getNumXTiles() > 1 || image.getNumYTiles() > 1)
                    && tileRegion.width  % image.getTileWidth()  == 0 // to avoid padding
                    && tileRegion.height % image.getTileHeight() == 0
                    && image.getTileWidth()  % 16 == 0                    // to verify compatibility with compression (tiff spec).
                    && image.getTileHeight() % 16 == 0
                    && srcXsubsampling == 1
                    && srcYsubsampling == 1) {
                    currentTW    = image.getTileWidth();
                    currentTH    = image.getTileHeight();
                    currentNumXT = srcRegion.width  / currentTW;
                    currentNumYT = srcRegion.height / currentTH;
                } else {
                    currentTW = currentTH = currentNumXT = currentNumYT = 0;
                }
            }
            destRegion = new Rectangle((tileRegion.width + srcXsubsampling - 1) / srcXsubsampling, (tileRegion.height + srcYsubsampling - 1) / srcYsubsampling);
        }
    }

    /**
     * Add property tag in {@linkplain #headProperties} before writing.
     *
     * @param tag      tiff tag value.
     * @param type     tiff tag type data.
     * @param count    number of data for this tag.
     * @param attribut tag data.
     */
    private void addProperty(final int tag, final short type, final int count, final Object attribut) {
        final Map<String, Object> tagAttributs = new HashMap<String, Object>();
        tagAttributs.put(ATT_NAME, getName(tag));
        tagAttributs.put(ATT_TYPE, type);
        tagAttributs.put(ATT_COUNT, count);
        tagAttributs.put(ATT_VALUE, attribut);
        headProperties.put(tag, tagAttributs);
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

//    /**
//     * Calcule la som des tailles des element precedent jusqu'au tag stipulé en parametre.
//     * Si tag exist pas c la som des tailles de tou les elements aui est retournée.
//     *
//     * @param defferedHeadProperties
//     * @param tag
//     * @return
//     */
//    private int computedefferedPosition(Map<Integer, Map> defferedHeadProperties, int tag) {
//        int defferedPos = 0;
//        for (int entryTag : defferedHeadProperties.keySet()) {
//            if (entryTag == tag) break;
//            defferedPos += getAttributLenght(defferedHeadProperties.get(entryTag));
//        }
//        return defferedPos;
//    }


    /**
     * Return length in byte of attribut on the file.
     *
     * @param tagAttribut
     * @return length in byte of attribut on the file.
     */
    private int getAttributLenght(final Map tagAttribut) {
        final int type  = (int) ((short) tagAttribut.get(ATT_TYPE));
        final int count = (int) tagAttribut.get(ATT_COUNT);
        return TYPE_SIZE[type] * count;
    }

    /**
     *
     * @throws IOException if problem during buffer writing.
     */
    private void writeHeads() throws IOException {
        // ecriture du nombre de tags

        if (isBigTIFF) {
            adjustBuffer(8);// Long.size / Byte.Size
            buffer.putLong(headProperties.size());
        } else {
            adjustBuffer(4);// Integer.size / Byte.size
            buffer.putShort((short) (headProperties.size()));
        }
        writeTags();
    }

    /**
     * Write all tags within {@link #headProperties} which are all tags without
     * offset but real value contained in 4 bytes.
     *
     * @throws IOException if problem during buffer writing.
     */
    private void writeTags() throws IOException {

//        //compute IFD end position in file to write deffered datas.
//        final long tagNumberSize = ((isBigTIFF) ? Long.SIZE : Short.SIZE)   / Byte.SIZE;
//        final long nextIfdSize   = ((isBigTIFF) ? Long.SIZE : Integer.SIZE) / Byte.SIZE;
//        // current position + tag number size + tag number * tag length + next ifd index size. All exprimed in Byte.
//        final long endTagPos     = currentIFDPosition + tagNumberSize + headProperties.size() * currentSizeEntry + nextIfdSize;
//        long defferedTagPos      = endTagPos;
//
//        final int offsetSize = ((isBigTIFF) ? Long.SIZE : Integer.SIZE) / Byte.SIZE;

        //compute IFD end position in file to write deffered datas.
        // current position + tag number size + tag number * tag length + next ifd index size. All exprimed in Byte.
        final long endTagPos = currentIFDPosition + currentSizeTagNumber + headProperties.size() * currentSizeEntry + currentSizeNextIFD;
        long defferedTagPos  = endTagPos;

        final int offsetSize = currentSizeNextIFD;

        final TreeMap<Integer, Map> defferedMap = new TreeMap<Integer, Map>();

        //-- write tags without offsets --//
        for (int tag : headProperties.keySet()) {
            Map tagattribut = headProperties.get(tag);
            short type = (short) tagattribut.get(ATT_TYPE);
            int count  = (int) tagattribut.get(ATT_COUNT);
            Object offOrVal = tagattribut.get(ATT_VALUE);
            final int dataSize = count * TYPE_SIZE[type];
            if (dataSize <= offsetSize) {
                writeTag((short)tag, type, count, offOrVal);
            } else {
                defferedMap.put(tag, tagattribut);
                writeDefferedTag((short)tag, type, count, defferedTagPos);
                defferedTagPos += getAttributLenght(tagattribut);
            }
        }

        //-- write next IFD file position --//
        //-- if 0 means no next IFD. --//

        nextIFDPosition = (int) (channel.position() + buffer.position());
        adjustBuffer(currentSizeNextIFD);
        if (isBigTIFF) {
            buffer.putLong(0);
        } else {
            buffer.putInt(0);
        }

        assert (int) (channel.position() + buffer.position()) == endTagPos : "chanel and buffer position = "+((channel.position() + buffer.position()))+" end tag position = "+endTagPos;


        for (final int tag : defferedMap.keySet()) {
            final Map tagattribut = defferedMap.get(tag);
            short type = (short) tagattribut.get(ATT_TYPE);
            long count  = (int) tagattribut.get(ATT_COUNT);
            Object offOrVal = tagattribut.get(ATT_VALUE);
            if (tag == StripOffsets || tag == TileOffsets) {
                assert offOrVal == null;
                long off = defferedTagPos;
                for (int i = 0; i < count; i++) {
                    write(type, off);
                    off += currentByteCount; // add length of a strip or a tile.
                }
            } else {
                writeArray(type, offOrVal);
            }
        }
    }

    /**
     *
     * @param img
     * @throws IOException
     */
    private void writeImageByStrips(final RenderedImage img, final ImageWriteParam param) throws IOException {

        // -------- Image boundary -----------------
        final int imageMinX = img.getMinX();
        final int imageMaxX = imageMinX + img.getWidth();
        final int imageMinY = img.getMinY();
        final int imageMaxY = imageMinY + img.getHeight();

        // --------------- Image Tiles -------
        final int imgMinTXOffset = img.getTileGridXOffset();
        final int imgMinTYOffset = img.getTileGridYOffset();

        final int imgNumTileX = img.getNumXTiles();
        final int imgNumTileY = img.getNumYTiles();

        final int imgTileWidth = img.getTileWidth();
        final int imgTileHeight = img.getTileHeight();

        //---------------- raster properties -----------------//
        final Raster initRast         = img.getTile(imgMinTXOffset, imgMinTYOffset);
        final DataBuffer initRastBuff = initRast.getDataBuffer();
        final int numbanks = initRastBuff.getNumBanks();
        final int dataType = initRastBuff.getDataType();

        final int srcRegionMaxX = srcRegion.x + srcRegion.width;
        final int srcRegionMaxY = srcRegion.y + srcRegion.height;

        Buffer destBuffer;
        final int position = buffer.position();
        final int limit    = buffer.limit();
        final int assertByteNum;
        buffer.clear();
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:   {
                destBuffer = buffer;
                assertByteNum = 1;
                break;
            }
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT: {
                destBuffer = buffer.asShortBuffer();
                assertByteNum = Short.SIZE / Byte.SIZE;
                break;
            }
            case DataBuffer.TYPE_INT : {
                destBuffer = buffer.asIntBuffer();
                assertByteNum = Integer.SIZE / Byte.SIZE;
                break;
            }
            case DataBuffer.TYPE_FLOAT:  {
                destBuffer = buffer.asFloatBuffer();
                assertByteNum = Float.SIZE/Byte.SIZE;
                break;
            }
            case DataBuffer.TYPE_DOUBLE: {
                destBuffer = buffer.asDoubleBuffer();
                assertByteNum = Double.SIZE/Byte.SIZE;
                break;
            }
            default: throw new IIOException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE, initRastBuff.getClass()));
        }
        buffer.limit(limit).position(position);

        // ---- subsamples -----//
        final int subsampleX;
        final int subsampleY;

        if (currentImgParam != null) {
            subsampleX = currentImgParam.getSourceXSubsampling();
            subsampleY = currentImgParam.getSourceYSubsampling();
        } else {
            subsampleX = subsampleY = 1;
        }
        assert subsampleX > 0 && subsampleY > 0;
        final SampleModel       sm = img.getSampleModel();
        final int imagePixelStride = sm.getNumDataElements();
        System.out.println("before "+(channel.position() + buffer.position()));
        if (subsampleX == 1
         && subsampleY == 1
         && srcRegion.equals(imageBoundary)
         && imgNumTileX == 1
         && imgNumTileY == 1) {
            // copier les tuiles du raster une
            int writelength = img.getWidth() * imagePixelStride;
            assert writelength * assertByteNum == currentByteCount : "writeLength = "+(writelength * assertByteNum)+" currentByteCount = "+currentByteCount;
            writelength *= img.getHeight();
            // -- get the following image raster
            final Raster imageTile        = img.getTile(imgMinTXOffset, imgMinTYOffset);
            final DataBuffer rasterBuffer = imageTile.getDataBuffer();
            for (int bank = 0; bank < numbanks; bank++) {
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
                write(destBuffer, sourceArray, dataType, 0, writelength);
            }
            System.out.println("after "+(channel.position() + buffer.position()));
            return;
        }

        // on defini intersection indice de tuiles
        final int minTX = imgMinTXOffset + (srcRegion.x - imageMinX) / imgTileWidth;
        final int maxTX = imgMinTXOffset + (srcRegionMaxX - imageMinX + imgTileWidth - 1) / imgTileWidth;
        final int minTY = imgMinTYOffset + (srcRegion.y - imageMinY) / imgTileHeight;
        final int maxTY = imgMinTYOffset + (srcRegionMaxY - imageMinY + imgTileHeight - 1) / imgTileHeight;

        for (int bank = 0; bank < numbanks; bank++) {
           for (int ty = minTY; ty < maxTY; ty++) {
               // definir intersection sur y de la tuile image courant avec srcregion
               final int currentImgTileMinY = imageMinY + ty * imgTileHeight;
               final int currentImgTileMaxY = currentImgTileMinY + imgTileHeight;
               final int minRowY = Math.max(srcRegion.y, currentImgTileMinY);
               final int maxRowY = Math.min(srcRegionMaxY, currentImgTileMaxY);

               final int rowArrayOffset = (minRowY - currentImgTileMinY) * imgTileWidth * imagePixelStride;

               for (int ry = minRowY; ry < maxRowY; ry += subsampleY) {
                   // assertion pour chaque ligne on verifie le nombre de byte ecrit
                   int assertByteCount = 0;

                   // decalage sur chaque ligne
                   final int arrayStepY = (ry - minRowY) * imgTileWidth * imagePixelStride;

                   for (int tx = minTX; tx < maxTX; tx++) {
                       // -- get the following image raster
                        final Raster imageTile        = img.getTile(tx, ty);
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

                       // definir intersection sur x
                       final int currentImgTileMinX = imageMinX + tx * imgTileWidth;
                       final int currentImgTileMaxX = currentImgTileMinX + imgTileWidth;

                       final int cuMinX = Math.max(srcRegion.x, currentImgTileMinX);
                       final int cuMaxX = Math.min(srcRegionMaxX, currentImgTileMaxX);

                       final int arrayXOffset = (cuMinX - currentImgTileMinX) * imagePixelStride;

                       final int writeLenght;
                       final int stepX;
                       if (subsampleX == 1) {
                           stepX = cuMaxX - cuMinX;
                           writeLenght = stepX * imagePixelStride;
                       } else {
                           stepX = subsampleX;
                           writeLenght = imagePixelStride;
                       }
                       for (int x = cuMinX; x < cuMaxX; x += stepX) {
                           final int arrayStepX = (x - cuMinX) * imagePixelStride;
                           final int finalOffset = rowArrayOffset + arrayStepY + arrayXOffset + arrayStepX;
                           write(destBuffer, sourceArray, dataType, finalOffset, writeLenght);

                           // assertion
                           assertByteCount += (writeLenght * assertByteNum);
                       }


                   }
                   assert assertByteCount == currentByteCount : "writen byte number doesn't "
                           + "match with expected comportement : writenByte = "+assertByteCount
                           +" expected writen byte number : "+currentByteCount;
               }
            }
        }
        System.out.println("after "+(channel.position() + buffer.position()));
    }

    /**
     *
     * @param type
     * @param value
     * @throws IOException
     */
    private void write(final short type, final long value) throws IOException {
        switch (type) {
                case TYPE_ASCII :
                case TYPE_BYTE  :
                case TYPE_UBYTE : {
                        adjustBuffer(1);
                        final byte b = (byte) value;
                        buffer.put(b);
                    break;
                }
                case TYPE_SHORT  :
                case TYPE_USHORT : {
                        adjustBuffer(2);
                        final short s = (short) value;
                        buffer.putShort(s);
                    break;
                }
                case TYPE_INT  :
                case TYPE_UINT : {
                        adjustBuffer(4);
                        final int integer = (int) value;
                        buffer.putInt(integer);
                    break;
                }
                case TYPE_LONG :
                case TYPE_ULONG : {
                        adjustBuffer(8);
                        buffer.putLong((long)value);
                    break;
                }
                case TYPE_FLOAT :
                case TYPE_DOUBLE : {
                        adjustBuffer(8);
                        final double d = (double) value;
                        buffer.putDouble(d);
                    break;
                }
                case TYPE_RATIONAL  : // 2 long successif
                case TYPE_URATIONAL :
                default : throw new IllegalStateException("unknow type. type : "+type);
            }
    }

    /**
     * Write array in file in function of data type.
     *
     * @param type type of array data.
     * @param array data array.
     * @throws IOException if problem during buffer writing.
     */
    private void writeArray(final short type, final Object array) throws IOException {
        final int count = Array.getLength(array);
        switch (type) {
                case TYPE_ASCII :
                case TYPE_BYTE  :
                case TYPE_UBYTE : {
                    for (int i = 0; i < count; i++) {
                        adjustBuffer(1);
                        final byte b = Array.getByte(array, i);
                        buffer.put(b);
                    }
                    break;
                }
                case TYPE_SHORT  :
                case TYPE_USHORT : {
                    for (int i = 0; i < count; i++) {
                        adjustBuffer(2);
                        final short s = (short) (Array.getShort(array, i));
                        buffer.putShort(s);
                    }
                    break;
                }
                case TYPE_INT  :
                case TYPE_UINT : {
                    for (int i = 0; i < count; i++) {
                        adjustBuffer(4);
                        final int integer = Array.getInt(array, i);
                        buffer.putInt(integer);
                    }
                    break;
                }
                case TYPE_LONG :
                case TYPE_ULONG : {
                    for (int i = 0; i < count; i++) {
                        adjustBuffer(8);
                        final long l = Array.getLong(array, 0) & 0xFFFFFFFFL;
                        buffer.putLong(l);
                    }
                    break;
                }
                case TYPE_FLOAT :
                case TYPE_DOUBLE : {
                    for (int i = 0; i < count; i++) {
                        adjustBuffer(8);
                        final double d = Array.getDouble(array, 0);
                        buffer.putDouble(d);
                    }
                    break;
                }
                case TYPE_RATIONAL  : // 2 long successif
                case TYPE_URATIONAL :
                default : throw new IllegalStateException("unknow type. type : "+type);
            }
    }

    /**
     * Define photometric interpretation tiff tag in function of {@link ColorModel} properties.
     *
     * @param cm image color model.
     * @return 1 for gray, 2 for RGB, 3 for indexed colormodel.
     */
    private short getPhotometricInterpretation(final ColorModel cm) {
        final ColorSpace cs = cm.getColorSpace();

        if (cs.equals(ColorSpace.getInstance(ColorSpace.CS_GRAY))) {
            // return 0 ou 1
            // return 0 pour min is white
            // return 1 pour min is black
            return 1;
        } else if (cm instanceof IndexColorModel) {
            return 3;
        } else if (cs.isCS_sRGB()) {
            return 2;
        } else {
            throw new IllegalStateException("unknow photometricinterpretation : unknow color model type.");
        }
    }

    /**
     * Adjust buffer position relative to filechanel which contain data,
     * and prepare bytebuffer position and limit for writing action.<br/>
     * If added data lenght exceed buffer limit, buffer is write on file channel.
     *
     * @param writingDataLenght lenght of data which will be added in buffer.
     * @throws IOException if problem during buffer writing.
     */
    private void adjustBuffer(final long writingDataLenght) throws IOException {
        if (writingDataLenght > buffer.capacity() - buffer.position()) {
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
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
        adjustBuffer(currentTiffTagLength);
        buffer.putShort(tag);
        buffer.putShort(type);
        if (isBigTIFF) {
            buffer.putLong(count); // si big  tiff put long
            buffer.putLong(offset);// si bigtiff put long
        } else {
            buffer.putInt((int) count); // si big  tiff put long
            buffer.putInt((int) offset);// si bigtiff put long
        }
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

        adjustBuffer(currentTiffTagLength);
        buffer.putShort(tag);
        buffer.putShort(type);
        final int dataSize;
        if (isBigTIFF) {
            dataSize = Long.SIZE;
            buffer.putLong(count);
        } else {
            dataSize = Integer.SIZE;
            buffer.putInt((int) count);// si big tiff put long
        }

        switch (type) {
                case TYPE_ASCII :
                case TYPE_BYTE  :
                case TYPE_UBYTE : {
                    final int dataCount = dataSize / Byte.SIZE;
                    assert count <= dataCount;
                    for (int i = 0; i < dataCount; i++) {
                        if (i < count) {
                            final byte b = Array.getByte(value, i);
                            buffer.put(b);
                        } else {
                            buffer.put((byte) 0);
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
                            final short s = (short) (Array.getShort(value, i) & 0xFFFF);
                            buffer.putShort(s);
                        } else {
                            buffer.putShort((short) 0);// peut etre un pblem de mask avoir !!!!!
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
                            buffer.putInt(in);
                        } else {
                            buffer.putInt(0);
                        }
                    }
                    break;
                }
                case TYPE_LONG  :
                case TYPE_ULONG : {
                    assert isBigTIFF;
                    assert count == 1;
                    final long l = Array.getLong(value, 0);
                    buffer.putLong(l);
                    break;
                }
//                case TYPE_RATIONAL :
//                case TYPE_URATIONAL :
//                case TYPE_FLOAT :
//                case TYPE_DOUBLE : {
//                    break;
//                }
                default : throw new IllegalStateException("unknow type. type : "+type);
            }
    }

    /**
     * Ensures that the channel is open. If the channel is already open, then this method
     * does nothing.
     *
     * @throws IllegalStateException if the input is not set.
     * @throws IOException If an error occurred while opening the channel.
     */
    private void open() throws IllegalStateException, IOException {

        if (channel == null) {
            if (output == null) {
                throw new IllegalStateException(error(Errors.Keys.NO_IMAGE_INPUT));
            }
            final FileOutputStream out;
            if (output instanceof String) {
                out = new FileOutputStream((String) output);
            } else {
                out = new FileOutputStream((File) output);
            }
            channel = out.getChannel();
            // Closing the channel will close the input stream.
            buffer.clear();

            final ByteOrder bo = ByteOrder.nativeOrder();
            buffer.order(bo);
            if (bo.equals(ByteOrder.BIG_ENDIAN)) {//MM
                buffer.put((byte)'M');
                buffer.put((byte)'M');
            } else {//II
                buffer.put((byte)'I');
                buffer.put((byte)'I');
            }

            if (isBigTIFF) {
                buffer.putShort((short)43);
                buffer.putShort((short) 8);
                buffer.putShort((short) 0);
                currentIFDPosition = buffer.position() + TYPE_SIZE[TYPE_IFD8];// different si biggtiff !!!!!!!
                buffer.putLong( currentIFDPosition);
            } else {
                buffer.putShort((short)42);
                currentIFDPosition = buffer.position() + TYPE_SIZE[TYPE_IFD];// different si biggtiff !!!!!!!
                buffer.putInt( currentIFDPosition);
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
        super.dispose(); //To change body of generated methods, choose Tools | Templates.
        try {
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
            if (channel != null) {
                channel.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(TiffImageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



   /**
     * Service provider interface (SPI) for {@code GeoTiffImageWriter}s. This provider wraps
     * an other provider (TIFF), which shall be specified at construction time.
     *
     * {@section Plugins registration}
     * At the difference of other {@code ImageWriter} plugins, the {@code GeoTiffImageWriter}
     * plugin is not automatically registered in the JVM. This is because there is many plugins
     * to register (one instance of this {@code Spi} class for each format to wrap), and because
     * attempts to get an {@code ImageWriter} to wrap while {@link IIORegistry} is scanning the
     * classpath for services cause an infinite loop. To enable the <cite>Geotiff</cite> plugins,
     * users must invoke {@link #registerDefaults(ServiceRegistry)} explicitly.
     *
     * @author Johann Sorel (Geomatys)
     * @version 3.16
     * @see TiffImageReader.Spi
     * @module
     */
    public static class Spi extends ImageWriterAdapter.Spi {
        /**
         * Creates a provider which will use the given format for writing pixel values.
         *
         * @param main The provider of the writers to use for writing the pixel values.
         */
        public Spi(final ImageWriterSpi main) {
            super(main);
            names           = new String[] {"geotiff", "tiff"};
            MIMETypes       = new String[] {"image/x-geotiff", "image/tiff;subtype=geotiff"};
            pluginClassName = "org.geotoolkit.image.io.plugin.GeoTiffImageWriter";
            vendorName      = "Geotoolkit.org";
            version         = Utilities.VERSION.toString();
            outputTypes     = ArraysExt.append(outputTypes, ImageOutputStream.class);
        }

        /**
         * Creates a provider which will use the given format for writing pixel values.
         * This is a convenience constructor for the above constructor with a provider
         * fetched from the given format name.
         *
         * @param  format The name of the provider to use for writing the pixel values.
         * @throws IllegalArgumentException If no provider is found for the given format.
         */
        public Spi(final String format) throws IllegalArgumentException {
            this(Formats.getWriterByFormatName(format, GeoTiffImageWriter.Spi.class));
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

        /**
         * Creates a new <cite>World File</cite> writer. The {@code extension} argument
         * is forwarded to the {@linkplain #main main} provider with no change.
         *
         * @param  extension A plug-in specific extension object, or {@code null}.
         * @return A new writer.
         * @throws IOException If the writer can not be created.
         */
        @Override
        public ImageWriter createWriterInstance(final Object extension) throws IOException {
            return new TiffImageWriter(this);
        }

        /**
         * Registers a default set of <cite>GeoTiff</cite> formats. This method shall be invoked
         * at least once by client application before to use Image I/O library if they wish to encode
         * <cite>GeoTiff</cite> images. This method can also be invoked more time if the TIFF
         * writer changed, and this change needs to be taken in account by the
         * <cite>GeoTiff</cite> writers. See the <cite>System initialization</cite> section in
         * the <a href="../package-summary.html#package_description">package description</a>
         * for more information.
         * <p>
         * The current implementation registers plugins for the TIFF.
         *
         * @param registry The registry where to register the formats, or {@code null} for
         *        the {@linkplain IIORegistry#getDefaultInstance() default registry}.
         *
         * @see org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()
         * @see org.geotoolkit.lang.Setup
         */
        @Configuration
        public static void registerDefaults(ServiceRegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }
            for (int index=0; ;index++) {
                final TiffImageWriter.Spi provider;
                try {
                    switch (index) {
                        case 0: provider = new TiffImageWriter.TIFF(); break;
                        default: return;
                    }
                } catch (RuntimeException e) {
                    /*
                     * If we failed to register a plugin, this is not really a big deal.
                     * This format will not be available, but it will not prevent the
                     * rest of the application to work.
                     */
                    Logging.recoverableException(Logging.getLogger("org.geotoolkit.image.io"),
                            GeoTiffImageWriter.Spi.class, "registerDefaults", e);
                    continue;
                }
                registry.registerServiceProvider(provider, ImageWriterSpi.class);
                registry.setOrdering(ImageWriterSpi.class, provider.main, provider);
            }
        }

        /**
         * Unregisters the providers registered by {@link #registerDefaults(ServiceRegistry)}.
         *
         * @param registry The registry from which to unregister the formats, or {@code null}
         *        for the {@linkplain IIORegistry#getDefaultInstance() default registry}.
         *
         * @see org.geotoolkit.lang.Setup
         */
        @Configuration
        public static void unregisterDefaults(ServiceRegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }
            for (int index=0; ;index++) {
                final Class<? extends GeoTiffImageWriter.Spi> type;
                switch (index) {
                    case 0: type = GeoTiffImageWriter.TIFF.class; break;
                    default: return;
                }
                final GeoTiffImageWriter.Spi provider = registry.getServiceProviderByClass(type);
                if (provider != null) {
                    registry.deregisterServiceProvider(provider, ImageWriterSpi.class);
                }
            }
        }
    }

    /**
     * Providers for common formats. Each provider needs to be a different class because
     * {@link ServiceRegistry} allows the registration of only one instance of each class.
     */
    static final class TIFF extends TiffImageWriter.Spi {TIFF() {super("TIFF");}}
}
