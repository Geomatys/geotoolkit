/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2014, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2014, Geomatys
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

import java.io.*;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;

import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferDouble;
import java.awt.image.IndexColorModel;
import java.lang.reflect.Array;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

import org.apache.sis.internal.storage.ChannelImageInputStream;

import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.SampleModels;
import org.geotoolkit.image.io.InputStreamAdapter;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.ScaledColorSpace;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.lang.SystemOverride;
import org.geotoolkit.metadata.geotiff.GeoTiffMetaDataReader;
import org.geotoolkit.resources.Errors;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.image.replace.ReplaceProcess;


/**
 * An image reader for uncompressed TIFF files or RGB images. This image reader duplicates the works
 * performed by the reader provided in <cite>Image I/O extension for Java Advanced Imaging</cite>,
 * but is specialized to the specific case of uncompressed files. For such case, this
 * {@code RawTiffImageReader} is faster.
 * <p>
 * {@code RawTiffImageReader} has the following restrictions:
 * <p>
 * <ul>
 *   <li>Can read only from files as a {@link File} or {@link String} input objects
 *       (the standard {@link javax.imageio.stream.ImageInputStream} is not supported).</li>
 *   <li>Can read only uncompressed (RAW) images
 *       (the JPEG and LZW compressions are not supported).</li>
 *   <li>Can read only tiled images (this restriction may be removed in a future version).</li>
 *   <li>Color model must be RGB (this restriction may be removed in a future version).</li>
 *   <li>Components are stored in "chunky" format, not planar (this restriction may be removed
 *       in a future version).</li>
 *   <li>The source and target bands can not be modified (this restriction may be removed
 *       in a future version).</li>
 *   <li>Metadata are ignored (this restriction may be removed in a future version).</li>
 * </ul>
 * <p>
 * Because of the above-cited restrictions, this reader registers itself only after the JAI
 * readers (unless otherwise specified). Users wanting this reader should request for it
 * explicitly, for example as below:
 *
 * {@preformat java
 *     ImageReaderSpi spi = IIORegistry.getDefaultInstance().getServiceProviderByClass(RawTiffImageReader.class);
 *     ImageReader reader = spi.createReaderInstance();
 * }
 *
 * This image reader can also process <cite>Big TIFF</cite> images.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Remi Marechal       (Geomatys)
 * @author Alexis Manin        (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
public class TiffImageReader extends SpatialImageReader {

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
     * Types supported by this reader. The type is the short at offset 2 in the directory entry.
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
        int[] size = TYPE_SIZE;
        size[TYPE_BYTE]  = size[TYPE_UBYTE] = size[TYPE_ASCII] =    Byte.SIZE        / Byte.SIZE;
        size[TYPE_SHORT] = size[TYPE_USHORT]                   =   Short.SIZE        / Byte.SIZE;
        size[TYPE_INT]   = size[TYPE_UINT]  = size[TYPE_IFD]   = Integer.SIZE        / Byte.SIZE;
        size[TYPE_LONG]  = size[TYPE_ULONG] = size[TYPE_IFD8]  =    Long.SIZE        / Byte.SIZE;
        size[TYPE_FLOAT]                                       =   Float.SIZE        / Byte.SIZE;
        size[TYPE_DOUBLE]                                      =  Double.SIZE        / Byte.SIZE;
        size[TYPE_URATIONAL] = size[TYPE_RATIONAL]             = (Integer.SIZE << 1) / Byte.SIZE; //rational = Integer / Integer. 2 Integer values red.
    }

    /**
     * Particularity code LZW.
     * Only use in readFromStripLZW() method.
     */
    private final static short LZW_CLEAR_CODE = 256;
    private final static short LZW_EOI_CODE   = 257;

    /**
     * Reading channel initialized from input, used for imageStream and reverse reading channels creation.
     */
    private ReadableByteChannel channel;

    /**
     * The imageStream to the TIFF file. Will be created from the {@linkplain #input} when first needed.
     */
    private ImageInputStream imageStream;

    /**
     * The buffer for reading blocks of data.
     */
    private ByteBuffer buffer;

    /**
     * Positions of each <cite>Image File Directory</cite> (IFD) in this file. The positions are
     * fetched when first needed. The number of valid positions is either {@link #countIFD}, or
     * the length of the {@code positionIFD} array if {@code countIFD} is negative.
     */
    private long[] positionIFD;

    /**
     * Number of valid elements in {@link #positionIFD}, or -1 if the {@code positionIFD}
     * array is now completed. In the later case, the length of {@code positionIFD} is the
     * number of images.
     */
    private int countIFD;

    /**
     * {@code true} if the file uses the BigTIFF format, or (@code false} for standard TIFF.
     */
    private boolean isBigTIFF;

    /**
     * Index of the current image, or -1 if none.
     */
    private int currentImage;

    /**
     * Information from the <cite>Directory Entries</cite> for the
     * {@linkplain #currentImage current image}.
     */
    private int imageWidth, imageHeight, tileWidth, tileHeight, samplesPerPixel;

    /**
     * Information from the <cite>Directory Entries</cite> for the
     * {@linkplain #currentImage current image}.
     */
    private long[] stripOffsets, stripByteCounts;

    /**
     * How image rows are cover by strip.
     */
    private int rowsPerStrip;

    /**
     * Number of bits per samples. This is (8,8,8) for RGB images.
     */
    private long[] bitsPerSample;

    /**
     * The offsets of each tiles in the current image.
     */
    private long[] tileOffsets;

    /**
     * The cached return value of {@link #getRawDataType(int)} for the current image.
     */
    private ImageTypeSpecifier rawImageType;

    /**
     * Metadata root Node for each image.
     */
    private IIOMetadataNode[] roots;

    /**
     * Compression type of current selected image.
     */
    private int compression;

    /**
     * Map table which contain all tiff properties from all images.
     */
    private Map<Integer, Map>[] metaHeads;

    /**
     * Map which contain all tiff properties of the current selected image.
     */
    private Map<Integer, Map> headProperties;
    
    /**
     * Map which do relation between image and thumbnail indexes and layer index.
     */
    private Map<Integer, List<Integer>> imgAndThumbs;

    /**
     * Define size in {@code Byte} of offset in each Tiff tag.<br/>
     * In tiff specification : {@link Integer#SIZE} / {@link Byte#SIZE}.<br/>
     * In big tiff specification : {@link Long#SIZE} / {@link Byte#SIZE}.
     */
    private int offsetSize;
    
    /**
     * File object of current input.
     */
    private Object currentInput;
    
    private long portosfileChannelPositionBegin;
        
    /**
     * Creates a new reader.
     *
     * @param provider The provider, or {@code null} if none.
     */
    public TiffImageReader(final TiffImageReader.Spi provider) {
        super((provider != null) ? provider : new TiffImageReader.Spi());
        buffer       = ByteBuffer.allocate(8196);
        positionIFD  = new long[4];
        metaHeads    = new Map[4];
        roots        = new IIOMetadataNode[4];
        currentImage = -1;
    }

    /**
     * Returns {@code true} since this image format places no inherent impediment on random access
     * to pixels. Actually, having easy random access is the whole point of uncompressed TIFF files
     * in many GIS infrastructures.
     *
     * @param  imageIndex The image index (ignored by this implementation).
     * @return Always {@code true} in this implementation.
     * @throws IOException If an I/O access was necessary and failed.
     */
    @Override
    public boolean isRandomAccessEasy(final int imageIndex) throws IOException {
        return true;
    }
   
    /**
     * Return true if the current layer is a thumbnail else false.
     * @return true if the current layer is a thumbnail else false.
     */
    private boolean isThumbnail() {
        final Map newSubFil = headProperties.get(NewSubfileType);
        if (newSubFil == null) return false;
        return ((long[]) newSubFil.get(ATT_VALUE))[0] != 0;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean readerSupportsThumbnails() {
        return true;
    }

    /**
     * {@inheritDoc }.
     * May return {@code null} if there are no metadatas.
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if(imageIndex < 0){
            //stream metadata
            return super.createMetadata(imageIndex);
        }
        
        checkLayers();
        final int layerIndex = getLayerIndex(imageIndex);
        if (metaHeads[layerIndex] == null) {
            selectLayer(layerIndex);
        }
        headProperties = metaHeads[layerIndex];
        //-- if there are not geotiff tag return null
        boolean isGeotiff = false;
        for(int key : headProperties.keySet()) {
            if (key == 34735) { //-- if geotiff tag exist
                isGeotiff = true;
                break;
            }
        }
                
        if (!isGeotiff) return null;
        
        fillRootMetadataNode(layerIndex);
        final IIOMetadata metadata = new IIOTiffMetadata(roots[layerIndex]);
        final GeoTiffMetaDataReader metareader = new GeoTiffMetaDataReader(metadata);
        try {
            return metareader.readSpatialMetaData();
        } catch (NoSuchAuthorityCodeException ex) {
            throw new IOException(ex);
        } catch (FactoryException ex) {
            throw new IOException(ex);
        }
    }
    
    /**
     * Returns the number of images available from the current input file. This method
     * will scan the file the first time it is invoked with a {@code true} argument value.
     */
    @Override
    public int getNumImages(boolean allowSearch) throws IOException {
        if (imgAndThumbs == null) {
            if (!allowSearch) return -1;
            checkLayers();
        }
        return imgAndThumbs.size();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public int getNumThumbnails(int imageIndex) throws IOException {
        checkLayers();
        return imgAndThumbs.get(getLayerIndex(imageIndex)).size();
    }
    
    /**
     * Returns the number of bands available for the specified image.
     *
     * @param  imageIndex The image index.
     * @return The number of bands available for the specified image.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex));
        return samplesPerPixel;
    }

    /**
     * Returns the width of the image at the given index.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The width of the given image.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex));
        return imageWidth;
    }
    
    /**
     * Returns the height of the image at the given index.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The height of the given image.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public int getHeight(int imageIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex));
        return imageHeight;
    }

    /**
     * Return width of thumbnail at thumbnailIndex in relation with image at imageIndex.
     * 
     * @param imageIndex 
     * @param thumbnailIndex the index of the thumbnail to be queried.
     * @return width of thumbnail at thumbnailIndex in relation with image at imageIndex.
     * @throws IOException 
     */
    @Override
    public int getThumbnailWidth(int imageIndex, int thumbnailIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex, thumbnailIndex));
        return imageWidth;
    }

    /**
     * 
     * Return height of thumbnail at thumbnailIndex in relation with image at imageIndex.
     * 
     * @param imageIndex 
     * @param thumbnailIndex the index of the thumbnail to be queried.
     * @return height of thumbnail at thumbnailIndex in relation with image at imageIndex.
     * @throws IOException 
     */
    @Override
    public int getThumbnailHeight(int imageIndex, int thumbnailIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex, thumbnailIndex));
        return imageHeight;
    }
    
    /**
     * Returns the width of the tiles in the given image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The width of the tile in the given image.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public int getTileWidth(final int imageIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex));
        return (tileWidth >= 0) ? tileWidth : imageWidth;
    }

    /**
     * Returns the height of the tiles in the given image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The height of the tile in the given image.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public int getTileHeight(int imageIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex));
        return (tileHeight >= 0) ? tileHeight : imageHeight;
    }

    /**
     * Returns {@code true} if the image is organized into tiles.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return {@code true} if the image is organized into tiles.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public boolean isImageTiled(final int imageIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex));
        return (tileWidth >= 0) && (tileHeight >= 0);
    }
    
    /**
     * Returns {@code true} since TIFF images have color palette.
     */
    @Override
    public boolean hasColors(final int imageIndex) throws IOException {
        checkLayers();
        selectLayer(getLayerIndex(imageIndex));
        final int photoInter = ((short[]) headProperties.get(PhotometricInterpretation).get(ATT_VALUE))[0];
        return photoInter == 3;//-- see tiff specification in relation with photometric interpretation. --//
    }

    /**
     * Returns the data type which most closely represents the "raw" internal data of the image.
     * The default implementation is as below:
     *
     * {@preformat java
     *     return getRawImageType(imageIndex).getSampleModel().getDataType();
     * }
     *
     * @param  layerIndex The index of the image to be queried.
     * @return The data type (typically {@link DataBuffer#TYPE_BYTE}).
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    protected int getRawDataType(final int layerIndex) throws IOException {
        return getRawImageType(layerIndex).getSampleModel().getDataType();
    }

    /**
     * Returns the {@link SampleModel} and {@link ColorModel} which most closely represents the
     * internal format of the image.
     *
     * @param  layerIndex The index of the image to be queried.
     * @return The internal format of the image.
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    public ImageTypeSpecifier getRawImageType(final int layerIndex) throws IOException {
        selectLayer(layerIndex);
        if (rawImageType == null) {
            // switch photo metrique interpretation
            final ColorSpace cs;
            final int photoInter = ((short[]) headProperties.get(PhotometricInterpretation).get(ATT_VALUE))[0];
            final Map<String, Object> bitsPerSamples = (headProperties.get(BitsPerSample));
            final Map<String, Object> sampleFormat   = (headProperties.get(SampleFormat));
            
            //-- find appropriate databuffer image type from size of sample or sample format or both of them. --//
            int databufferType;
            
            //-- bits per sample study --//
            int[] bits = null;
            int sampleBitSize = 0;
            long[] bitsPerSample = null;
            if (bitsPerSamples != null) {
                bitsPerSample = ((long[]) bitsPerSamples.get(ATT_VALUE));
                assert bitsPerSample != null : "bitsPerSample array should not be null.";
                bits = new int[bitsPerSample.length];
                for (int i=0; i < bits.length; i++) {
                    final long b = bitsPerSample[i];
                    if ((bits[i] = (int) b) != b) {
                        //-- Verify that 'bitPerSample' values are inside 'int' range (paranoiac check).
                        throw new UnsupportedImageFormatException(error(
                                Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "bitsPerSample", b));
                    }
                    if (i != 0 && b != sampleBitSize) {
                        //-- Current implementation requires all sample values to be of the same size.
                        throw new UnsupportedImageFormatException(error(Errors.Keys.INCONSISTENT_VALUE));
                    }
                    sampleBitSize = (int) b;
                }
            }
            
            //-- sample format study --//
            if (sampleFormat == null) {
                if (bitsPerSamples == null) {
                    if (samplesPerPixel == 0) {
                        switch (photoInter) {
                            case 0  : 
                            case 1  : samplesPerPixel = 1; break; //-- gray color model --//
                            case 2  : samplesPerPixel = 3; break; //-- RGB color model --//
                            default : throw new UnsupportedImageFormatException(error(Errors.Keys.FORBIDDEN_ATTRIBUTE_2,
                                "bitsPerSamples", "sampleFormats"));
                        }
                    } 
                    /*
                    * If bitsPerSample and sample format fields were not specified, assume bytes.
                    */
                   databufferType = DataBuffer.TYPE_BYTE;
                   bits           = new int[samplesPerPixel];
                   Arrays.fill(bits, 8);
                } else {
                    /*
                     * We require exact value, because the reading process read all sample values
                     * in one contiguous read operation.
                     */
                    switch (sampleBitSize) {
                        case Byte   .SIZE : databufferType = DataBuffer.TYPE_BYTE;   break;
                        case Short  .SIZE : databufferType = DataBuffer.TYPE_USHORT; break;
                        case Integer.SIZE : databufferType = DataBuffer.TYPE_INT;    break;
                        case Double.SIZE  : databufferType = DataBuffer.TYPE_DOUBLE; break;
                        default: {
                            throw new UnsupportedImageFormatException(error(
                                    Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "bitsPerSample", sampleBitSize));
                        }
                    }
                }                    
            } else {
                final long[] samplesFormat = ((long[]) sampleFormat.get(ATT_VALUE));
                short samplFormat = (short) samplesFormat[0];
                for (int i = 1; i < samplesFormat.length; i++) {
                    if (samplesFormat[i] != samplFormat) {
                        //-- Current implementation requires all sample values to be of the same size.
                        throw new UnsupportedImageFormatException(error(Errors.Keys.INCONSISTENT_VALUE));
                    } 
                }
                 
                if (samplFormat == 3) {
                    /*
                     * Case to defferency 32 bits Float to 32 bits Integer. 
                     */
                    switch (sampleBitSize) {
                        case Float.SIZE  : databufferType = DataBuffer.TYPE_FLOAT; break;
                        case Double.SIZE : databufferType = DataBuffer.TYPE_DOUBLE; break;
                        default : {
                            throw new UnsupportedImageFormatException(error(
                                    Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "bitsPerSample", sampleBitSize));
                        }
                    }
                } else {
                    //-- undefined sample format --//
                    if (bitsPerSample == null) 
                    throw new UnsupportedImageFormatException(error(
                            Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "sampleformat value", samplFormat));

                   /*
                    * We require exact value, because the reading process read all sample values
                    * in one contiguous read operation.
                    */
                   switch (sampleBitSize) {
                       case Byte   .SIZE : databufferType = DataBuffer.TYPE_BYTE;   break;
                       case Short  .SIZE : databufferType = DataBuffer.TYPE_USHORT; break;
                       case Integer.SIZE : databufferType = DataBuffer.TYPE_INT;    break;
                       case Double.SIZE  : databufferType = DataBuffer.TYPE_DOUBLE; break;
                       default : {
                           throw new UnsupportedImageFormatException(error(
                                   Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "bitsPerSample", sampleBitSize));
                       }
                   }
                }
            }
            
            switch (photoInter) {
                case 0 :   //-- minIsWhite
                case 1 : { //-- minIsBlack
                    if (bits.length > 1) {
                        cs = new ScaledColorSpace(bits.length, 0, Double.MIN_VALUE, Double.MAX_VALUE);//-- attention au choix de la bande !!!!
                    } else {
                        cs = ColorSpace.getInstance(ColorSpace.CS_GRAY); 
                    }
                    break;
                }
                case 2 : { //-- RGB
                    cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                    break;
                }
                case 3 : {//-- palette
                    Map<String, Object> colorMod = headProperties.get(ColorMap);
                    if (colorMod == null)
                        throw new UnsupportedImageFormatException("image should own colorMap informations.");
                    final long[] index  = (long[]) colorMod.get(ATT_VALUE);
                    final int[] indexes = buildColorMapArray(index);
                    final ColorModel cm = new IndexColorModel(sampleBitSize, indexes.length, indexes, 0, true, -1, databufferType);
                    /*
                     * Create a SampleModel with size of 1x1 volontary just to know image properties.
                     * Image with correctively size will be create later with getDestination() in #read(int index, param) method.
                     */
                    rawImageType        = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
                    return rawImageType;
                }
                default : {
                    throw new UnsupportedImageFormatException(error(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2,
                            "photometricInterpretation", photoInter));
                }
            }
            final boolean hasAlpha = bits.length > cs.getNumComponents();
            final ColorModel cm = new ComponentColorModel(cs, bits, hasAlpha, false,
                    hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE, databufferType);
           /*
            * Create a SampleModel with size of 1x1 volontary just to know image properties.
            * Image with correctively size will be create later with getDestination() in #read(int index, param) method.
            */
            rawImageType = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
        }
        return rawImageType;
    }
    
    /**
     * Returns a collection of {@link ImageTypeSpecifier} containing possible image types to which
     * the given image may be decoded. The default implementation returns a singleton containing
     * only the {@linkplain #getRawImageType(int) raw image type}.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return A set of suggested image types for decoding the current given image.
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        return Collections.singleton(getRawImageType(imageIndex)).iterator();
    }

    /**
     * Reads the image at the given index.
     *
     * @param  imageIndex The index of the image to read.
     * @param  param Parameters used to control the reading process, or {@code null}.
     * @return The image.
     * @throws IOException If an error occurred while reading the image.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        checkLayers();
        final BufferedImage image = readLayer(getLayerIndex(imageIndex), param);
                
        //if the image contains floats or double, datas are already in geophysic type
        //we must replace noData values by NaN.
        final int dataType = image.getSampleModel().getDataType();
        if(DataBuffer.TYPE_FLOAT==dataType || DataBuffer.TYPE_DOUBLE==dataType){
            final SpatialMetadata metadata = getImageMetadata(imageIndex);
            if(metadata!=null){
                final DimensionAccessor accessor = new DimensionAccessor(metadata);
                if(accessor.childCount()==1){
                    accessor.selectChild(0);
                    Double noDatas = accessor.getAttributeAsDouble("realFillValue");
                    if(noDatas!=null && noDatas!= null){
                        final double[][][] nodatas = new double[1][2][1];
                        nodatas[0][0][0] = noDatas;
                        Arrays.fill(nodatas[0][1], Double.NaN);
                        final ReplaceProcess process = new ReplaceProcess(image, nodatas);
                        try {
                            process.call();
                        } catch (ProcessException ex) {
                            throw new IOException(ex.getMessage(),ex);
                        }
                    }
                }
            }
        }
        
        return image;
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage readThumbnail(int imageIndex, int thumbnailIndex) throws IOException {
        checkLayers();
        return readLayer(getLayerIndex(getLayerIndex(imageIndex), thumbnailIndex), null);
    }
    
    /**
     * Closes the file imageStream. If the imageStream is already closed, then this method does nothing.
     *
     * @throws IOException If an error occurred while closing the imageStream.
     */
    @Override
    protected void close() throws IOException {
        super.close();
        countIFD       = 0;
        currentImage   = -1;
        bitsPerSample  = null;
        tileOffsets    = null;
        rawImageType   = null;
        if (imageStream != null) {
            // If given input was a stream or an ImageInputStream, it's the owner of the stream who should close it.
            if (currentInput instanceof File) {
                imageStream.close();
            }
            imageStream = null;
            // Keep the buffer, since we may reuse it for the next image.
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setInput(Object input) {
        super.setInput(input); 
        countIFD       = 0;
        bitsPerSample  = null;
        tileOffsets    = null;
        rawImageType   = null;
        buffer       = ByteBuffer.allocate(8196);
        positionIFD  = new long[4];
        metaHeads    = new Map[4];
        roots        = new IIOMetadataNode[4];
        currentImage = -1;
        
        //-- to force open
        channel      = null;
        imageStream  = null;
    }
        
    /**
     * Selects the image at the given index.
     *
     * @param  layerIndex  The index of the image to make the current one.
     * @throws IOException If an error occurred while reading the file.
     * @throws IndexOutOfBoundsException If the given image index is out of bounds.
     */
    private void selectLayer(final int layerIndex) throws IOException, IndexOutOfBoundsException {
        if (layerIndex != currentImage) {
            open(); //-- Does nothing if already open.
            if (layerIndex >= minIndex) {
                final int entrySize, shortSize, intSize;
                if (isBigTIFF) {
                    entrySize = SIZE_BIG_ENTRY;
                    shortSize = SIZE_BIG_SHORT;
                    intSize   = SIZE_BIG_INT;
                } else {
                    entrySize = SIZE_ENTRY;
                    shortSize = SIZE_SHORT;
                    intSize   = SIZE_INT;
                }
                /*
                 * If the requested image if after the last image read,
                 * scan the file until we find the IFD location.
                 */
                if (countIFD >= 0) { //-- Should never be 0 actually.
                    int imageAhead = layerIndex - countIFD;
                    while (imageAhead >= 0) {
                        long position = positionIFD[countIFD - 1];
                        imageStream.seek(position);
                        final long n = readShort();
                        position += shortSize;
                        imageStream.seek(position + n * entrySize);
                        if (!nextImageFileDirectory()) {
                            throw new IndexOutOfBoundsException(error(
                                    Errors.Keys.INDEX_OUT_OF_BOUNDS_1, layerIndex));
                        }
                        imageAhead--;
                    }
                }
                /*
                 * Read the Image File Directory (IFD) content.
                 */
                if (layerIndex < positionIFD.length) {
                    imageWidth      = -1;
                    imageHeight     = -1;
                    tileWidth       = -1;
                    tileHeight      = -1;
                    samplesPerPixel =  0;
                    bitsPerSample   = null;
                    tileOffsets     = null;
                    rawImageType    = null;
                    
                    headProperties  = metaHeads[layerIndex];
                    if (headProperties == null) {
                        headProperties = new HashMap<Integer, Map>();
                        final Collection<long[]> deferred = new ArrayList<>(4);
                        long position = positionIFD[layerIndex];
                        imageStream.seek(position);
                        final long n = readShort();//-- n : tag number which define tiff image properties.
                        position += shortSize;
                        for (int i = 0; i < n; i++) {
                            imageStream.seek(position);
                            parseDirectoryEntries(deferred);
                            position += entrySize;
                        }
                        /*
                         * Complete the arrays that needs further processing.
                         * Get the values that we lack.
                         */
                        readDeferredArrays(deferred.toArray(new long[deferred.size()][]));
                        metaHeads[layerIndex] = headProperties;
                    }

                    final Map<String, Object> iwObj   = headProperties.get(ImageWidth);
                    final Map<String, Object> ihObj   = headProperties.get(ImageLength);
                    final Map<String, Object> isppObj = headProperties.get(SamplesPerPixel);
                    final Map<String, Object> ibpsObj = headProperties.get(BitsPerSample);

                    imageWidth      = (int) ((iwObj   == null) ? -1 : ((long[]) iwObj.get(ATT_VALUE))[0]);
                    imageHeight     = (int) ((ihObj   == null) ? -1 : ((long[]) ihObj.get(ATT_VALUE))[0]);
                    samplesPerPixel = (int) ((isppObj == null) ? -1 : ((long[]) isppObj.get(ATT_VALUE))[0]);
                    bitsPerSample   = ((long[]) ibpsObj.get(ATT_VALUE));

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

                        rowsPerStrip = (int) ((rpsObj == null) ? -1 : ((long[]) rpsObj.get(ATT_VALUE))[0]);
                        if (sOffObj != null) stripOffsets    = (long[]) sOffObj.get(ATT_VALUE);
                        if (sbcObj  != null) stripByteCounts = (long[]) sbcObj.get(ATT_VALUE);
                        ensureDefined(rowsPerStrip,    "rowsPerStrip");
                        ensureDefined(stripOffsets,    "stripOffsets");
                        ensureDefined(stripByteCounts, "stripByteCounts");
                    } else {
                        tileWidth   = (int) ((twObj == null) ? -1 : ((long[]) twObj.get(ATT_VALUE))[0]);
                        tileHeight  = (int) ((thObj == null) ? -1 : ((long[]) thObj.get(ATT_VALUE))[0]);
                        if (toObj  != null)  tileOffsets = (long[]) toObj.get(ATT_VALUE);

                        ensureDefined(tileWidth,   "tileWidth");
                        ensureDefined(tileHeight,  "tileHeight");
                        ensureDefined(tileOffsets, "tileOffsets");
                    }
                    currentImage = layerIndex;
                    return;
                }
            }
            throw new IndexOutOfBoundsException(error(Errors.Keys.INDEX_OUT_OF_BOUNDS_1, layerIndex));
        }
    }

    /**
     * Fill {@link IIOMetadataNode} root in native metadata format to create {@link SpatialMetadata}.
     */
    private void fillRootMetadataNode(final int layerIndex) throws UnsupportedEncodingException {
        if (roots[layerIndex] != null) return;
        roots[layerIndex] = new IIOMetadataNode(TAG_GEOTIFF_IFD);
        for (int tag : headProperties.keySet()) {
            final Map<String, Object> currenTagAttributs = headProperties.get(tag);

            final String name  = (String) currenTagAttributs.get(ATT_NAME);
            final short type   = (short) currenTagAttributs.get(ATT_TYPE);
            final int count    = (int)(long) currenTagAttributs.get(ATT_COUNT);
            final Object value = currenTagAttributs.get(ATT_VALUE);

            final IIOMetadataNode tagBody = new IIOMetadataNode(TAG_GEOTIFF_FIELD);
            tagBody.setAttribute(ATT_NAME, name);
            tagBody.setAttribute(ATT_NUMBER, Integer.toString(tag));

            switch (type) {
                case TYPE_ASCII : {
                    final IIOMetadataNode valuesNode = new IIOMetadataNode(TAG_GEOTIFF_ASCIIS);
                    final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_ASCII);
                    final byte[] allAscii = new byte[count];
                    for (int i = 0; i < count; i++) {
                        allAscii[i] = (byte) Array.getLong(value, i);
                    }
                    String str = new String(allAscii, "ISO-8859-1");
                    valN.setAttribute(ATT_VALUE, str);
                    valuesNode.appendChild(valN);
                    tagBody.appendChild(valuesNode);
                    break;
                }
                case TYPE_BYTE : {
                    final IIOMetadataNode valuesNode = new IIOMetadataNode(TAG_GEOTIFF_ASCIIS);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_ASCII);
                        valN.setAttribute(ATT_VALUE, Byte.toString((byte)Array.getLong(value, i)));
                        valuesNode.appendChild(valN);
                    }
                    tagBody.appendChild(valuesNode);
                    break;
                }
                case TYPE_UBYTE :
                case TYPE_SHORT : {
                    final IIOMetadataNode valueNode = new IIOMetadataNode(TAG_GEOTIFF_SHORTS);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_SHORT);
                        valN.setAttribute(ATT_VALUE, Short.toString((short)Array.getLong(value, i)));
                        valueNode.appendChild(valN);
                    }
                    tagBody.appendChild(valueNode);
                    break;
                }
                case TYPE_USHORT : {
                    final IIOMetadataNode valueNode = new IIOMetadataNode(TAG_GEOTIFF_SHORTS);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_SHORT);
                        valN.setAttribute(ATT_VALUE, Integer.toString((int)Array.getLong(value, i)));
                        valueNode.appendChild(valN);
                    }
                    tagBody.appendChild(valueNode);
                    break;
                }
                case TYPE_INT   :
                case TYPE_UINT  :
                case TYPE_IFD   :
                case TYPE_LONG  :
                case TYPE_ULONG :
                case TYPE_IFD8  : {
                    final IIOMetadataNode valueNode = new IIOMetadataNode(TAG_GEOTIFF_LONGS);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_LONG);
                        valN.setAttribute(ATT_VALUE, Long.toString(Array.getLong(value, i)));
                        valueNode.appendChild(valN);
                    }
                    tagBody.appendChild(valueNode);
                    break;
                }
                case TYPE_RATIONAL :
                case TYPE_URATIONAL :
                case TYPE_FLOAT :
                case TYPE_DOUBLE : {
                    final IIOMetadataNode valueNode = new IIOMetadataNode(TAG_GEOTIFF_DOUBLES);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_DOUBLE);
                        valN.setAttribute(ATT_VALUE, Double.toString(Array.getDouble(value, i)));
                        valueNode.appendChild(valN);
                    }
                    tagBody.appendChild(valueNode);
                    break;
                }
                default : throw new IllegalStateException("unknow type. type : "+type);
            }
            roots[layerIndex].appendChild(tagBody);
        }
    }

    /**
     * Ensures that the given value is positive.
     *
     * @param  value  The value which must be positive.
     * @param  name   The name for the parameter value, to be used in case of error.
     * @throws IIOException If the given value is considered undefined.
     */
    private void ensureDefined(final int value, final String name) throws IIOException {
        if (value < 0) {
            throw new IIOException(error(Errors.Keys.NO_SUCH_ELEMENT_NAME_1, name));
        }
    }

    /**
     * Ensures that the given array is non-null.
     *
     * @param  value  The value which must be non-null.
     * @param  name   The name for the parameter value, to be used in case of error.
     * @throws IIOException If the given value is considered undefined.
     */
    private void ensureDefined(final long[] value, final String name) throws IIOException {
        if (value == null) {
            throw new IIOException(error(Errors.Keys.NO_SUCH_ELEMENT_NAME_1, name));
        }
    }
    
    /**
     * Parses the content of the directory entry at the current {@linkplain #buffer} position.
     * The {@linkplain #buffer} is assumed to have all the required bytes for one entry. The
     * buffer position is undetermined after this method call.
     *
     * @throws IIOException If an error occurred while parsing an entry.
     */
    private void parseDirectoryEntries(final Collection<long[]> deferred) throws IOException {
        final int tag       = imageStream.readShort() & 0xFFFF;
        final short type    = imageStream.readShort();
        final long count    = readInt();
        final long datasize = count * TYPE_SIZE[type];
        if (datasize <= offsetSize) {
            //-- offset is the real value(s).
            entryValue(tag, type, count);
        } else {                
            //-- offset from file begin to data set.
            deferred.add(new long[]{tag, type, count, readInt()});
        }
    }

    /**
     * Reads one value of the given type from the given buffer.
     * This method assumes that the type is valid.
     *
     * @param  type The data type.
     * @return The value.
     */
    private long read(final short type) throws IOException {
        switch (type) {
            case TYPE_BYTE  :  
            case TYPE_ASCII : return imageStream.readByte();
            case TYPE_UBYTE : return imageStream.readByte() & 0xFFL;
            case TYPE_SHORT : {
                return imageStream.readShort();
            }
            case TYPE_USHORT : {
                return imageStream.readShort() & 0xFFFFL;
            }
            case TYPE_INT   : return imageStream.readInt();
            case TYPE_IFD   :
            case TYPE_UINT  : return imageStream.readInt() & 0xFFFFFFFFL;
            case TYPE_LONG  : return imageStream.readLong();
            case TYPE_IFD8  :
            case TYPE_ULONG : {
                final long value = imageStream.readLong();
                if (value < 0) {
                    throw new UnsupportedImageFormatException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE));
                }
                return value;
            }
            default : throw new AssertionError(type);
        }
    }

    /**
     * Reads one value of the given type from the given buffer.
     * This method assumes that the type is valid.
     *
     * @param  type The data type.
     * @return The value.
     */
    private double readAsDouble(final short type) throws IOException {
        switch (type) {
            case TYPE_URATIONAL : {
                final long num = imageStream.readInt() & 0xFFFFFFFFL;
                final long den = imageStream.readInt() & 0xFFFFFFFFL;
                return num / (double) den;
            }
            case TYPE_RATIONAL : {
                final int num = imageStream.readInt();
                final int den = imageStream.readInt();
                return num / (double) den;
            }
            case TYPE_DOUBLE : return imageStream.readDouble();
            case TYPE_FLOAT  : return imageStream.readFloat();
            default: throw new AssertionError(type);
        }
    }

    /**
     * Reads a value from the directory entry and add them in {@linkplain #headProperties}. This method can be invoked right after the entry ID.
     * The {@linkplain #buffer} is assumed to have all the required bytes for one entry. The buffer
     * position is undetermined after this method call.
     *
     * @param tag Tiff tag integer.
     * @param type type of tag
     * @param count data number will be read
     * @throws IIOException If the entry can not be read as an integer.
     */
    private void entryValue(final int tag, final short type, final long count) throws IOException {
        assert count != 0;
        if (count > 0xFFFFFFFFL) throw new IllegalStateException("count value too expensive. not supported yet.");
        final int offsetSize = (isBigTIFF) ? Long.SIZE : Integer.SIZE;
        final Map<String, Object> tagAttributs = new HashMap<>();
        tagAttributs.put(ATT_NAME, getName(tag));
        tagAttributs.put(ATT_TYPE, type);
        tagAttributs.put(ATT_COUNT, count);
        
        switch(tag) {
            case PlanarConfiguration: { //-- PlanarConfiguration.
                assert count == 1 : "with tiff PlanarConfiguration tag, count should be equal 1.";
                final short planarConfiguration = (short) imageStream.readShort();
                tagAttributs.put(ATT_VALUE, new short[]{planarConfiguration});
                headProperties.put(tag, tagAttributs);
                break;
            }
            case PhotometricInterpretation: { //-- PhotometricInterpretation.
                assert count == 1 : "with tiff PhotometricInterpretation tag, count should be equal 1.";
                final short photometricInterpretation = (short) imageStream.readShort();
                tagAttributs.put(ATT_VALUE, new short[]{photometricInterpretation});
                headProperties.put(tag, tagAttributs);
                break;
            }
            case Compression: { //-- Compression.
                assert count == 1 : "with tiff compression tag, count should be equal 1.";
                compression = (int) (imageStream.readShort() & 0xFFFF);
                if (compression != 1 && compression != 32773 && compression != 5) { // '1' stands for "uncompressed". // '32 773' stands for packbits compression
                    final Object nameCompress;
                    switch (compression) {
                        case 6:  nameCompress = "JPEG";      break;
                        case 7:  nameCompress = "JPEG";      break;
                        case 8:  nameCompress = "Deflate";   break;
                        default: nameCompress = compression; break;
                    }
                    throw new UnsupportedImageFormatException(error(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2,
                            "compression", nameCompress));
                }
                tagAttributs.put(ATT_VALUE, new int[]{compression});
                headProperties.put(tag, tagAttributs);
                break;
            }
            default : {
                /*
                 * Here the four byte at 8-11th positions are not offset but real value(s) 
                 * because data(s) size is lesser or equal with offset byte size.
                 * 4 bytes to normal tiff (Integer.size) and 8 bytes to big tiff (Long.size).
                 */
               switch (type) {
                    case TYPE_ASCII :
                    case TYPE_BYTE  : {
                        if (count > offsetSize / Byte.SIZE)
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int) count]; 
                        for (int i = 0; i < count; i++) {
                            result[i] = imageStream.readByte();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_UBYTE : {
                        if (count > offsetSize / Byte.SIZE)
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count]; 
                        for (int i = 0; i < count; i++) {
                            result[i] = imageStream.readByte() & 0xFF;
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_SHORT : {
                        if (count > (offsetSize / Short.SIZE))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = imageStream.readShort();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_USHORT: {
                        if (count > (offsetSize / Short.SIZE))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = (int) (imageStream.readShort() & 0xFFFF);
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_INT   : {
                        if (count > (offsetSize / Integer.SIZE))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = imageStream.readInt();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_UINT :
                    case TYPE_IFD  : {
                        if (count > (offsetSize / Integer.SIZE))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = imageStream.readInt() & 0xFFFFFFFFL;
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_FLOAT : {
                        if ((isBigTIFF && count > 2) || ((!isBigTIFF) && count > 1))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final float[] result = new float[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = imageStream.readFloat();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_RATIONAL  : 
                    case TYPE_URATIONAL : 
                    case TYPE_DOUBLE    : {
                        if (count > 1)
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        if (!isBigTIFF) {
                            throw new IllegalStateException("in standard tiff offset or value in tiff tag, should not be instance of double or rational.");
                        }
                        final double[] result = new double[1];
                        result[0] = readAsDouble(type);
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    /*
                     * Only use by bigTiff format.
                     */
                    case TYPE_LONG  :
                    case TYPE_ULONG :
                    case TYPE_IFD8  : {
                        if (count > 1)
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        if (!isBigTIFF) {
                            throw new IllegalStateException("in standard tiff offset or value in tiff tag, should not be instance of long.");
                        }
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = imageStream.readLong();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case 7 : {
                        break;
                    }
                    default : throw new IIOException(error(Errors.Keys.ILLEGAL_PARAMETER_TYPE_2, getName(tag), type));
                }
            }
        }
    }

    /**
     * To be invoked during {@linkplain #selectLayer(int)}  in order to process all
     * the deferred arrays. This method tries to read the arrays in sequential order as much
     * as possible.
     *
     * @param  deferred The deferred arrays.
     * @throws IOException If an I/O operation was required and failed.
     */
    private void readDeferredArrays(final long[][] deferred) throws IOException {
        /*
         * Organize offsets in ascending order to avoid too much buffer deplacements.
         */
        Arrays.sort(deferred, OFFSET_COMPARATOR);

        for (long[] defTab : deferred) {
            final int tag      = (int) (defTab[0] & 0xFFFF);
            final short type   = (short) defTab[1];
            final int count    = (int) defTab[2];
            //-- in tiff spec offset or data are always unsigned
            final long offset  = defTab[3] & 0xFFFFFFFFL;
            final Map<String, Object> tagAttributs = new HashMap<>();
            tagAttributs.put(ATT_NAME, getName(tag));
            tagAttributs.put(ATT_TYPE, type);
            tagAttributs.put(ATT_COUNT, (long) count);
            
            imageStream.seek(offset);
            final Object result;
            if (type == TYPE_DOUBLE || type == TYPE_FLOAT || type == TYPE_RATIONAL || type == TYPE_URATIONAL) {
                result = new double[count];
                for (int i = 0; i < count; i++) {
                    Array.setDouble(result, i, readAsDouble(type));
                }
            } else {
                result = new long[count];
                for (int i = 0; i < count; i++) {
                    Array.setLong(result, i, read(type));
                }
            }
            tagAttributs.put(ATT_VALUE, result);
            headProperties.put(tag, tagAttributs);
        }
    }

    /**
     * Comparator used for sorting the array to be processed by {@link #readDeferredArrays(long[][])},
     * in order to read the data sequentially from the disk.
     */
    private static final Comparator<long[]> OFFSET_COMPARATOR = new Comparator<long[]>() {
        @Override public int compare(final long[] o1, final long[] o2) {
            return Long.signum(o1[0] - o2[0]);
        }
    };
    
    /**
     * Convert and return color map array from tiff file to an Integer array adapted to build {@link IndexColorModel} in java.
     *
     * @param colorMap array given by tiff reading.
     * @return an Integer array adapted to build {@link IndexColorModel} in java.
     */
    private int[] buildColorMapArray(final long[] colorMap) {
        final int indexLength = colorMap.length;
        assert (indexLength % 3 == 0) : "color map array length should be modulo 3";
        final int length_3 = indexLength / 3;
        final int[] result = new int[length_3];

        //-- color map in a tiff file : N Red values -> N Green values -> N Blue values
        int idR = 0;
        int idG = length_3;
        int idB = length_3 << 1;// = 2 * length_3

        /*
         * mask applied to avoid the low-order bits from the red color overlaps the bits of green color.
         * Moreover to avoid the low-order bits from the green color overlaps the bits of blue color.
         */
        final int mask = 0x0000FF00;

        /*
         * In indexed color model in java, values to defind palette for each color are between 0 -> 255.
         * To build integer value in palette, we need to shift red value by 16 bits, green value by 8 bits and no shift to blue.
         *
         * In our case we build a color model from color map (tiff palette) values define between 0 -> 65535.
         * Then build integer value in palette we will shift each color value by normaly shift minus 8, to bring back all values between 0 -> 256.
         */

        final int alpha = 0xFF000000;

        //-- pixel : 1111 1111 | R | G | B
        for (int i = 0; i < length_3; i++) {
            final int r = ((int) (colorMap[idR++] & mask) << 8);
            final int g = ((int) colorMap[idG++] & mask);
            final int b = ((int) colorMap[idB++] >> 8) ;
            result[i] = alpha | r | g | b;
        }
        return result;
    }
    
    /**
     * Ensures that the imageStream is open. If the imageStream is already open, then this method
     * does nothing.
     *
     * @throws IllegalStateException if the input is not set.
     * @throws IOException If an error occurred while opening the imageStream.
     */
    private void open() throws IllegalStateException, IOException {

        if (imageStream == null) {
            if (input == null) {
                throw new IllegalStateException(error(Errors.Keys.NO_IMAGE_INPUT));
            }
            final FileInputStream in;
            if (input instanceof String) {
                currentInput = new File((String) input);
                if (!((File)currentInput).isFile()) {
                    throw new IOException("Given input is not a valid file : "+input);
                }
            } else {
                currentInput = input;
            }
            if (currentInput instanceof FileInputStream) {
                portosfileChannelPositionBegin = ((FileInputStream)currentInput).getChannel().position();
            } else if (currentInput instanceof InputStream) {
                InputStream stream = (InputStream) currentInput;
                if (stream.markSupported()) {
                    try {
                        stream.reset();
                    } catch (IOException e) {
                        stream.mark(Integer.MAX_VALUE);
                    }
                } else {
                    throw new IllegalStateException("Given input stream does not support rewinding.");
                }
            } else if (currentInput instanceof ImageInputStream) {
                ImageInputStream stream = (ImageInputStream) currentInput;
                stream.reset();
                stream.mark();
            }


            //-- Closing the imageStream will close the input stream.
            //buffer.clear();
            imageStream = getImageInputStream(false);
            
            final byte c = imageStream.readByte();
            if (c != imageStream.readByte()) {
                throw invalidFile("ByteOrder");
            }
            final ByteOrder order;
            if (c == 'M') {
                order = ByteOrder.BIG_ENDIAN;
            } else if (c == 'I') {
                order = ByteOrder.LITTLE_ENDIAN;
            } else {
                throw invalidFile("ByteOrder");
            }
            imageStream.setByteOrder(order);
            final short version = imageStream.readShort();
            if (isBigTIFF = (version == 0x002B)) {
                if (imageStream.readShort() != 8 || imageStream.readShort() != 0) {
                    throw invalidFile("OffsetSize");
                }
            } else if (version != 0x002A) {
                throw invalidFile("MagicNumber");
            }
            offsetSize = ((isBigTIFF) ? Long.SIZE : Integer.SIZE) / Byte.SIZE;
            countIFD = 0;
            nextImageFileDirectory();
            currentImage = -1;
        }
    }
    
    /**
     * Reads the next bytes in the {@linkplain #buffer}, which must be the 32 or 64 bits
     * offset to the next <cite>Image File Directory</cite> (IFD). The offset is then stored
     * in the next free slot of {@link #positionIFD}.
     *
     * @return {@code true} if we found a new IFD, or {@code false} if there is no more IFD.
     */
    private boolean nextImageFileDirectory() throws IOException {
        assert countIFD >= 0;
        final long position = readInt();//-- long if it's big tiff
        if (position != 0) {
            if (countIFD == positionIFD.length) {
                final int countIFD2 = countIFD << 1;
                positionIFD = Arrays.copyOf(positionIFD, Math.max(4, countIFD2));//-- head table
                metaHeads   = Arrays.copyOf(metaHeads,   Math.max(4, countIFD2));
                roots       = Arrays.copyOf(roots,       Math.max(4, countIFD2));
            }
            positionIFD[countIFD++] = position;
            return true;
        } else {
            positionIFD = ArraysExt.resize(positionIFD, countIFD);
            countIFD = -1;
            return false;
        }
    }
    
    /**
     * Reads the {@code int} or {@code long} value (depending if the file is
     * standard of big TIFF) at the current {@linkplain #buffer} position.
     *
     * @return The next integer.
     */
    private long readInt() throws IOException {
        return isBigTIFF ? imageStream.readLong() : imageStream.readInt() & 0xFFFFFFFFL;
    }
    
    /**
     * Reads the {@code short} or {@code long} value (depending if the file is
     * standard of big TIFF) at the current {@linkplain #buffer} position.
     *
     * @return The next short.
     */
    private long readShort() throws IOException {
        return isBigTIFF ? imageStream.readLong() : imageStream.readShort() & 0xFFFFL;
    }
    
    /**
     * Define layer index from image index.
     * 
     * @param imageIndex image index ask by user.
     * @return layer index.
     */
    private int getLayerIndex(final int imageIndex) {
        final Object[] keys = imgAndThumbs.keySet().toArray();
        if (imageIndex >= keys.length)
            throw new IndexOutOfBoundsException(error(
                                    Errors.Keys.INDEX_OUT_OF_BOUNDS_1, imageIndex));
        return (int) keys[imageIndex];
    }
    
    /**
     * Define layer index from image index and thumbnail index.
     * 
     * @param imageIndex image index ask by user.
     * @param thumbnailsIndex thumbnail index ask by user.
     * @return layer index.
     */
    private int getLayerIndex(final int imageIndex, final int thumbnailsIndex) {
        final int imgLayIndex = getLayerIndex(imageIndex);
        final List<Integer> thumbs = imgAndThumbs.get(imgLayIndex);
        if (thumbnailsIndex >= thumbs.size())
            throw new IndexOutOfBoundsException(error(
                                    Errors.Keys.INDEX_OUT_OF_BOUNDS_1, thumbnailsIndex));
        return thumbs.get(thumbnailsIndex);
    }
    
    /**
     * If {@linkplain #checkLayers() } never been asked before, follow all layer 
     * head to define which layers are images and which layers are thumbnails.
     */
    private void checkLayers() throws IOException {
        if (imgAndThumbs == null) {
            imgAndThumbs = new HashMap<>();
            open();
            int idCuLayer = 0;
            int idCuImg = -1;
            
            final int entrySize, shortSize;
            if (isBigTIFF) {
                entrySize = SIZE_BIG_ENTRY;
                shortSize = SIZE_BIG_SHORT;
            } else {
                entrySize = SIZE_ENTRY;
                shortSize = SIZE_SHORT;
            }
            
            do {
                assert idCuLayer == countIFD - 1;
                long position = positionIFD[idCuLayer];
                imageStream.seek(position);
                final long n = readShort();
                position += shortSize;
                selectLayer(idCuLayer);
                if (isThumbnail()) {
                    //-- add in thumbnail list at the correct image key --//
                    final List<Integer> currentThumb = imgAndThumbs.get(idCuImg);
                    // If we found thumbnail before the actual image, we must create image indice.
                    if (idCuImg < 0 || currentThumb == null) {
                        imgAndThumbs.put(++idCuImg, new ArrayList<Integer>());
                    }
                    assert idCuImg >= 0;
                    imgAndThumbs.get(idCuImg).add(idCuLayer++);
                } else {
                    //-- add an other key in imgAndThumbs Map --//
                    idCuImg = idCuLayer;
                    imgAndThumbs.put(idCuLayer++, new ArrayList<Integer>());
                }
                imageStream.seek(position + n * entrySize);
            } while (nextImageFileDirectory());
        }
    }
    
    /**
     * Reads the layer at the given index.
     * 
     * @param layerIndex The index of the image to read.
     * @param  param Parameters used to control the reading process, or {@code null}.
     * @return The image.
     * @throws IOException If an error occurred while reading the image.
     */
    private BufferedImage readLayer(final int layerIndex, final ImageReadParam param) throws IOException {
        selectLayer(layerIndex);
        final Rectangle srcRegion = new Rectangle();
        final Rectangle dstRegion = new Rectangle();
        final BufferedImage image = getDestination(param, getImageTypes(layerIndex), imageWidth, imageHeight);
        /*
         * compute region : ajust les 2 rectangles src region et dest region en fonction des coeff subsampling present dans Imagereadparam.
         */
        computeRegions(param, imageWidth, imageHeight, image, srcRegion, dstRegion);// calculer une region de l'image sur le fichier que l'on doit lire
        if (compression == 32773) {
            assert stripOffsets != null : "with compression 32773 (packbits) : image should be writen in strip offset use case.";
            readFromStrip32773(image.getRaster(), param, srcRegion, dstRegion);
        } else if (compression == 5) {
            if (stripOffsets != null) {
                readFromStripLZW(image.getRaster(), param, srcRegion, dstRegion);
            } else {
                assert tileOffsets != null;
                readFromTilesLZW(image.getRaster(), param, srcRegion, dstRegion);
            }
        } else {
            //-- by strips
            if (stripOffsets != null) {
                readFromStrip(image.getRaster(), param, srcRegion, dstRegion);
            } else {
                //-- by tiles
                readFromTiles(image.getRaster(), param, srcRegion, dstRegion);
            }
        }
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
    private void readFromStrip(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer();
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int sampleSize           = DataBuffer.getDataTypeSize(dataType) / Byte.SIZE;
        final int targetPixelStride    = /*sampleSize */ numBands;
        final int sourceScanlineStride = samplesPerPixel * imageWidth;
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());
        
        //-- fillOrder --//
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        final ImageInputStream rasterReader = getImageInputStream(fO == 2);
        
        //-- planar configuration --//
        final Map<String, Object> planarConfig = headProperties.get(PlanarConfiguration);
        short pC = 1;
        /*
         * If samples per pixel = 1, planar configuration has no impact.
         */
        if (planarConfig != null && samplesPerPixel > 1) {
            pC = ((short[]) planarConfig.get(ATT_VALUE)) [0];
        }
        final int planarStep = (pC != 2) ? samplesPerPixel : 1;
        
        final int srcMaxx = srcRegion.x + srcRegion.width;
        final int srcMaxy = srcRegion.y + srcRegion.height;
        
        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final Object targetArray;
            switch (dataType) {
                case DataBuffer.TYPE_BYTE   : targetArray = ((DataBufferByte)   dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_USHORT : targetArray = ((DataBufferUShort) dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_SHORT  : targetArray = ((DataBufferShort)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_FLOAT  : targetArray = ((DataBufferFloat)  dataBuffer).getData(bank); break; 
                case DataBuffer.TYPE_INT    : targetArray = ((DataBufferInt)    dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_DOUBLE : targetArray = ((DataBufferDouble) dataBuffer).getData(bank); break;
                default: throw new AssertionError(dataType);
            }

            int readLength, srcStepX, srcStepY, planarDenum, planarBankStep;

            if (pC == 1) {
                if (sourceXSubsampling == 1) {
                    /*
                     * if we want to read all image which mean : srcRegion = dstRegion
                     * and all strip are organize in ascending order in tiff file and
                     * moreover if datatype is Byte, we can fill a byteBuffer in one time, by all red samples values.
                     */
                    if (sourceYSubsampling == 1 && dstRegion.equals(srcRegion)
                                                && checkStrips(stripOffsets, stripByteCounts)
                                                && sourceScanlineStride == dstRegion.width * targetPixelStride
                                                && dataType == DataBuffer.TYPE_BYTE) {
                        rasterReader.seek(stripOffsets[0]);
                        rasterReader.read((byte[]) targetArray);
                        continue;
                    }
                    //-- if sourceXSubsampling = 1 we read row by row.
                    readLength = srcRegion.width * samplesPerPixel;
                    srcStepX   = srcRegion.width;
                } else {
                    //-- read pixel by pixel
                    readLength = samplesPerPixel;
                    srcStepX   = sourceXSubsampling;
                }
                planarDenum = 1;
                planarBankStep = 0;
            } else {
                /*
                 * With planar configuration planar ( 2 ) read sample by sample
                 */
                readLength = 1;
                srcStepX   = sourceXSubsampling;
                planarDenum = samplesPerPixel;
                planarBankStep = samplesPerPixel - 1;
            }
            
            for (int s = 0; s < samplesPerPixel; s += planarStep) {
                
                srcStepY              = sourceYSubsampling;

                //-- shift in X direction exprimate in Byte unit.
                final int buffStepX   = (sourceXSubsampling - 1) * samplesPerPixel * sampleSize / planarDenum;
                /*
                 * Iterate over the strip to read, in sequential file access order (which is not
                 * necessarily the same than row indices order).
                 */
                int currentMaxRowPerStrip = -1;
                int currentStripOffset = -1;
                long srcBuffPos = -1;
                long nextSrcBuffPos = -1;

                //-- target start exprimate in sample
                int bankID = bankOffsets[bank] + targetScanlineStride * dstRegion.y;

                //-- step on x axis in target window (dstRegion) exprimate in sample unit.
                final int dstStepBeforeReadX = dstRegion.x * numBands + s;
                final int dstStepAfterReadX  = (targetScanlineStride - (dstRegion.x + dstRegion.width) * targetPixelStride) - s; // sampleSize;

                //-- byte number read for each buffer read action.
                final int srcBuffReadLength = readLength * sampleSize;

                for (int y = srcRegion.y; y < srcMaxy; y += srcStepY) {
                    if (y >= currentMaxRowPerStrip) {
                        currentStripOffset    = y / rowsPerStrip;
                        currentMaxRowPerStrip = currentStripOffset + rowsPerStrip;
                        assert stripOffsets.length % planarDenum == 0;
                        //-- row begining exprimate in byte.
                        srcBuffPos = stripOffsets[currentStripOffset + s * stripOffsets.length / planarDenum] + (y - currentStripOffset * rowsPerStrip) * sourceScanlineStride * sampleSize / planarDenum + srcRegion.x * samplesPerPixel * sampleSize / planarDenum ;//+ s * sampleSize;
                        nextSrcBuffPos = srcBuffPos + sourceYSubsampling * sourceScanlineStride * sampleSize / planarDenum;
                    }

                    //-- move at correct table position from dstRegion.x begin position.
                    bankID += dstStepBeforeReadX;

                    //-- move at correct buffer position from srcRegion.x begin position.
                    assert srcBuffPos != -1 : "bad source buffer position initialize value.";
                    int sampleNumberRead = 0;
                    for (int x = srcRegion.x; x < srcMaxx; x += srcStepX) {
                        //-- adjust read length in function of buffer capacity.
                        long currentPos = srcBuffPos;
                        rasterReader.seek(currentPos);
                        switch (dataType) {
                            case DataBuffer.TYPE_BYTE   : rasterReader.readFully((byte[])   targetArray, bankID, readLength); break;
                            case DataBuffer.TYPE_USHORT :
                            case DataBuffer.TYPE_SHORT  : rasterReader.readFully((short[])  targetArray, bankID, readLength); break;
                            case DataBuffer.TYPE_FLOAT  : rasterReader.readFully((float[])  targetArray, bankID, readLength); break;
                            case DataBuffer.TYPE_INT    : rasterReader.readFully((int[])    targetArray, bankID, readLength); break;
                            case DataBuffer.TYPE_DOUBLE : rasterReader.readFully((double[]) targetArray, bankID, readLength); break;
                            default: throw new AssertionError(dataType);
                        }
                        bankID += readLength;
                        /*
                         * In case where planar configuration = 2, 
                         * read each sample separately.
                         * For example in RGB image all Red sample are read then all Green and to finish all Blue.
                         * This step jump other samples of current read pixel.
                         * 
                         * In case where planar configuration is 1 this attribut is set to 0.
                         */
                        bankID += planarBankStep;
                        
                        //-- move buffer position by read length (exprimate in Byte).
                        srcBuffPos += srcBuffReadLength;
                        /*
                         * jump pixel which are not read caused by subsampling X.
                         * For example with subsamplingX = 3 we read one pixelstride and we jump two of them for each read step.
                         */
                        srcBuffPos += buffStepX;
                        sampleNumberRead += readLength;
                    }
                    assert sampleNumberRead == dstRegion.width * numBands / planarDenum : "expected sample number read = "+(dstRegion.width * numBands / planarDenum)+" found : "+sampleNumberRead;
                    //-- advance bank index from end destination window (dstRegion.x + dstRegion.width) to end of row.
                    bankID     += dstStepAfterReadX;
                    assert bankID % targetPixelStride == 0 : "found : "+(bankID % targetPixelStride);
                    srcBuffPos = nextSrcBuffPos;
                    nextSrcBuffPos += sourceYSubsampling * sourceScanlineStride * sampleSize / planarDenum;
                }
            }
        }
    }
    
    /**
     * Process to the image reading, and stores the pixels in the given raster.<br/>
     * Process fill raster from informations stored in stripOffset made.<br/>
     * Method adapted to read data from LZW(tag value 5) compression.
     * 
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void readFromStripLZW(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer();
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());
        
        //-- predictor study ---//
        final Map<String, Object> predictor = (headProperties.get(Predictor));
        final short predic    = (predictor != null) ? (short) ((long[]) predictor.get(ATT_VALUE)) [0] : 1;
        //-- array which represent a pixel to permit horizontal differencing if exist --//
        
        //-- fillOrder --//
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        
        //-- planar configuration --//
        final Map<String, Object> planarConfig = headProperties.get(PlanarConfiguration);
        short pC = 1;
        /*
         * If samples per pixel = 1, planar configuration has no impact.
         */
        if (planarConfig != null && samplesPerPixel > 1) {
            pC = ((short[]) planarConfig.get(ATT_VALUE)) [0];
        }
        final int pixelLength = (pC != 2) ? samplesPerPixel : 1;
        final int planarDenum = (pC != 2) ? 1 : samplesPerPixel;
        
        final int sourceScanlineStride = imageWidth * pixelLength;
         final long[] prediPix = new long[pixelLength];
        
        //-- adapt imageStream in function of fill order value --//
        final ImageInputStream inputLZW = getImageInputStream(fO == 2);
        
        final long bitpersampl = bitsPerSample[0];
        
       /*
        * Iterate over the strip to read, in sequential file access order (which is not
        * necessarily the same than row indices order).
        */
       final int srcMaxy            = srcRegion.y + srcRegion.height;

       /*
        * Step on x axis in target window (dstRegion) when iterate pass to next row.
        * This step contain sum of shift at begin row and ending destination row shift.
        */
       final int dstStep            = targetScanlineStride - dstRegion.width * samplesPerPixel;

       //-- source sample step when iteration pass to next row --//
       final int nextRowStep        = sourceScanlineStride * sourceYSubsampling;

        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final Object targetArray;
            switch (dataType) {
                case DataBuffer.TYPE_BYTE   : targetArray = ((DataBufferByte)   dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_USHORT : targetArray = ((DataBufferUShort) dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_SHORT  : targetArray = ((DataBufferShort)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_INT    : targetArray = ((DataBufferInt)    dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_FLOAT  : targetArray = ((DataBufferFloat)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_DOUBLE : targetArray = ((DataBufferDouble) dataBuffer).getData(bank); break;
                default: throw new AssertionError(dataType);
            }
            
            for (int s = 0; s < samplesPerPixel; s += pixelLength) {
                
            //-- stripoffset array index start --//
            final int currentStripOffset = srcRegion.y / rowsPerStrip + s * stripOffsets.length / planarDenum;
            final int maxStripOffset     = (srcMaxy + rowsPerStrip - 1) / rowsPerStrip  + s * stripOffsets.length / planarDenum;
            
            /*
             * Long container use to build a sample,
             * because each sample is read byte per byte regardless their bit size.
             */
            long dataContainer = 0;
            int maskCount      = 0;

            //-- target start --//
            int bankID = bankOffsets[bank] + targetScanlineStride * dstRegion.y + dstRegion.x * samplesPerPixel + s;//-- + s
            
            //-- current source y position --//
            int ypos = srcRegion.y;
            
            //-- read throught only necessarry stripOffsets to fill target image --//
            nextStrip : for (int cSO = currentStripOffset; cSO < maxStripOffset; cSO++) {
                
                //-- buffer start position from stripOffsets --//
                long currentBuffPos        = stripOffsets[cSO];
                inputLZW.seek(currentBuffPos);
                
                //-- initialize LZW attributs --//
                //-- length in bit of lzw data --//
                int currentLZWCodeLength = 9;
                //-- byte array map use to decompresse LZW datas --//
                byte[][] lzwTab          = new byte[LZW_CLEAR_CODE][];
                
                //-- current LZW array index --// 
                int idLZWTab         = 0;
                int maxIDLZWTab      = 511; //--> (1 << currentLZWCodeLength) - 1
                //-- precedently iteration LZW code --//
                byte[] oldCodeLZW    = null;
                int hdb              = 0;
                Arrays.fill(prediPix, 0);
                /*
                 * With LZW compression we must read all byte to build appropriate LZW map container.
                 * We define two positions "posRef" and "maxRowRefPos" where "posRef" represent
                 * index of current sample which will be written in target array and "maxRowRefPos" the last exclusive written sample.
                 * 
                 * In other word posRef represent the first available sample.
                 */
                int posRef       = (ypos - (cSO - s * stripOffsets.length / planarDenum) * rowsPerStrip) * sourceScanlineStride + srcRegion.x * pixelLength;
                
                //-- in case where sourceYsubsampling greater than row per strip --//
                if (posRef >= rowsPerStrip * sourceScanlineStride) continue nextStrip;
                
                int nextPosRef         = posRef + nextRowStep;
                int maxRowRefPos       = posRef + dstRegion.width * pixelLength * sourceXSubsampling;
                
                final int maxSamplePos = (Math.min((cSO + 1) * rowsPerStrip, srcMaxy) - cSO * rowsPerStrip) * sourceScanlineStride;
                int samplePos          = 0;
                
                //-- bytePos must read throught all file byte per byte --//
                int bytePos = 0;
                int b       = 0;
                short codeLZW;
                
                //-- work sample by sample --//
                while (LZW_EOI_CODE != (codeLZW = readLZWCode(inputLZW, currentLZWCodeLength))) {
                    if ((idLZWTab + 258) == 4095) 
                        assert codeLZW == 256 : "when LZW map array reach its maximum index value the next value in file should be clear code 256.";
                    
                    if (codeLZW == LZW_CLEAR_CODE) {
                        currentLZWCodeLength = 9;
                        lzwTab               = new byte[LZW_CLEAR_CODE][];
                        idLZWTab             = 0;
                        maxIDLZWTab          = 511;
                        oldCodeLZW           = null;
                        continue;
                    }
                    
                    assert (oldCodeLZW != null || (oldCodeLZW == null && codeLZW < LZW_CLEAR_CODE)) : "After a clear code, next code should be smaller than 256";
                    
                    byte[] entree;
                    if (codeLZW >= 258) {
                        if (lzwTab[codeLZW - 258] != null) {
                            entree = lzwTab[codeLZW - 258];
                        } else {
                            // w + w[0]
                            final int oldCLen = oldCodeLZW.length;
                            entree            = Arrays.copyOf(oldCodeLZW, oldCLen + 1);
                            entree[oldCLen]   = oldCodeLZW[0];
                        }
                    } else {
                        entree = new byte[] { (byte) codeLZW };
                    }

                    assert entree != null;

                    //-- write entree --//
                    for (int i = 0; i < entree.length; i++) {
                        //-- build sample in relation with bits per samples --//
                        final long val = entree[i] & 0x000000FFL;
                        dataContainer  = dataContainer | (val << maskCount);
                        maskCount     += Byte.SIZE;
                        
                        //-- if a sample is built --//
                        if (maskCount == bitpersampl) {
                            //-- add in precedently array before insertion --//
                            //-- if horizontal differencing add with precedently value --//
                            prediPix[hdb] = (predic == 2) ? (prediPix[hdb] + dataContainer) : dataContainer;
                            if (++hdb == pixelLength) hdb = 0;

                            //-- re-initialize datacontainer --//
                            dataContainer = 0;
                            maskCount     = 0;

                            //-- write sample in target array if its necessary --//
                            if (samplePos == posRef) { 
                                switch (dataType) {
                                    case DataBuffer.TYPE_BYTE   : Array.setByte(targetArray, bankID, (byte) (prediPix[b])); break;
                                    case DataBuffer.TYPE_SHORT  : 
                                    case DataBuffer.TYPE_USHORT : Array.setShort(targetArray, bankID, (short) (prediPix[b])); break;
                                    case DataBuffer.TYPE_INT    : Array.setInt(targetArray, bankID, (int) (prediPix[b])); break;
                                    case DataBuffer.TYPE_FLOAT  : Array.setFloat(targetArray, bankID, Float.intBitsToFloat((int) (prediPix[b]))); break;
                                    case DataBuffer.TYPE_DOUBLE : Array.setDouble(targetArray, bankID, Double.longBitsToDouble(prediPix[b])); break;
                                    default: throw new AssertionError(dataType);
                                }
                                bankID += planarDenum;
                                if (++b == pixelLength) {
                                    posRef += (sourceXSubsampling - 1) * pixelLength;
                                    b = 0;
                                }
                                posRef++;

                                //-- this if means : pass to the next destination image row --//
                                if (posRef == maxRowRefPos) {
                                    assert hdb == 0 : "hdb should be zero. hdb = "+hdb;
                                    ypos += sourceYSubsampling;

                                    //-- begin source position writing --//
                                    posRef      = nextPosRef;
                                    nextPosRef += nextRowStep;
                                    
                                    //-- destination shifts --//
                                    bankID += dstStep;
                                    
                                    //-- if it is unnecessary to finish to read current strip --//
                                    if (posRef >= maxSamplePos) continue nextStrip; //-- a affiner
                                    
                                    //-- ending source position writing --//
                                    maxRowRefPos += nextRowStep;
                                }
                            }
                            //-- shift by one when a sample was built --//
                            samplePos++;
                        }
                        if (++bytePos == sourceScanlineStride) {
                            //-- initialize predictor array --//
                            Arrays.fill(prediPix, 0);
                            bytePos = 0;
                        }
                    }

                    if (oldCodeLZW == null) {
                        assert idLZWTab == 0 : "With old code null : lzw tab must be equals to zero.";
                        assert entree.length == 1;
                        oldCodeLZW = entree;
                        continue;
                    }

                    //-- add in LZW map array --//
                    final int oldLen      = oldCodeLZW.length;
                    final byte[] addedTab = Arrays.copyOf(oldCodeLZW, oldLen + 1);
                    addedTab[oldLen]      = entree[0];
                    lzwTab[idLZWTab++]    = addedTab;
                    
                    //-- if current map index reach the maximum value permit by bit number --//
                    if (((idLZWTab + 258) & 0xFFFF) == maxIDLZWTab) {                        
                        /*
                         * When LZW algorithm reach its maximum index value 4095, to don't exceed 12 bits capacity
                         * a clear code 256 is normaly written in the CURRENT bit length.
                         * Continue to force next read in current bit length.
                         * Moreover after this continue an assertion verify this expected comportement.
                         */
                        if (maxIDLZWTab == 4095) continue;
                        currentLZWCodeLength++;
                        final int nextLZWMapLength = 1 << currentLZWCodeLength;
                        maxIDLZWTab                = nextLZWMapLength - 1;
                        lzwTab                     = Arrays.copyOf(lzwTab, nextLZWMapLength);
                    }
                    oldCodeLZW = entree;
                    //---------------------------------------------------------//
                }
                assert samplePos == maxSamplePos : "pos = "+samplePos+" Expected pos = "+maxSamplePos;
            }
        }
       }
    }
    
    /**
     * Read next data of {@code codeLZWLength} bits length from current {@code ImageInputStream input}.
     * 
     * @param input ImageInputStream where to read datas.
     * @param codeLZWLength current bits length of read data.
     * @return data value.
     * @throws IOException if problem during reading from input.
     * @see #readFromStripLZW(java.awt.image.WritableRaster, javax.imageio.ImageReadParam, java.awt.Rectangle, java.awt.Rectangle) 
     */
    private static short readLZWCode(final ImageInputStream input, final int codeLZWLength) throws IOException {
        return ((short) (input.readBits(codeLZWLength))); 
    }
    
    /**
     * Process to the image reading, and stores the pixels in the given raster.<br/>
     * Process fill raster from informations stored in stripOffset made.<br/>
     * Method adapted to read data from packbits(tag 32773) compression.
     *
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void readFromStrip32773(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer();
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());

        //-- fillOrder --//
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        final ImageInputStream rasterReader = getImageInputStream(fO == 2);
        
        //-- planar configuration --//
        final Map<String, Object> planarConfig = headProperties.get(PlanarConfiguration);
        short pC = 1;
        /*
         * If samples per pixel = 1, planar configuration has no impact.
         */
        if (planarConfig != null && samplesPerPixel > 1) {
            pC = ((short[]) planarConfig.get(ATT_VALUE)) [0];
        }
        final int pixelLength = (pC != 2) ? samplesPerPixel : 1;
        final int planarDenum = (pC != 2) ? 1 : samplesPerPixel;
        
        final int sourceScanlineStride = pixelLength * imageWidth;
        
        final long bitpersampl = bitsPerSample[0];

        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final Object targetArray;
            switch (dataType) {
                case DataBuffer.TYPE_BYTE   : targetArray = ((DataBufferByte)   dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_USHORT : targetArray = ((DataBufferUShort) dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_SHORT  : targetArray = ((DataBufferShort)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_INT    : targetArray = ((DataBufferInt)    dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_FLOAT  : targetArray = ((DataBufferFloat)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_DOUBLE : targetArray = ((DataBufferDouble) dataBuffer).getData(bank); break;
                default: throw new AssertionError(dataType);
            }

            for (int s = 0; s < samplesPerPixel; s += pixelLength) {
                
            /*
             * Iterate over the strip to read, in sequential file access order (which is not
             * necessarily the same than row indices order).
             */
            final int srcMaxx = (srcRegion.x + srcRegion.width) * pixelLength;
            final int srcMaxy = srcRegion.y  + srcRegion.height;

            //-- step on x axis in target window (dstRegion)
            final int dstStepBeforeReadX = dstRegion.x * numBands;
            final int dstStepAfterReadX  = (targetScanlineStride - (dstRegion.x + dstRegion.width) * numBands);// / sampleSize;

            //-- target start
            int bankID = bankOffsets[bank] + targetScanlineStride * dstRegion.y+s;

            //-- stripoffset array index start
            int currentStripOffset    = srcRegion.y / rowsPerStrip;

            //-- row index of next strip
            int currentMaxRowperStrip = (currentStripOffset + 1) * rowsPerStrip;

            //-- row index from stripoffset
            int ypos                  = currentStripOffset * rowsPerStrip;

            //-- band to band
            currentStripOffset += s * stripOffsets.length / planarDenum;
            
            //-- buffer start position
            int currentBuffPos        = (int) stripOffsets[currentStripOffset];
            
            //-- row index to begin to write
            int yref                  = srcRegion.y;
            int xref, bankStepBefore, bankStepAfter;
            
            //-- attributs to build appropriate sample from dataType --//
            long dataContainer = 0;
            int maskCount = 0;
            
            while (ypos < srcMaxy) {
                if (ypos >= currentMaxRowperStrip) {
                    currentStripOffset++;
                    currentBuffPos = (int) stripOffsets[currentStripOffset];
                    currentMaxRowperStrip += rowsPerStrip;
                }

                if (ypos == yref) {
                    //-- we write current row
                    xref     = srcRegion.x * pixelLength;
                    yref    += sourceYSubsampling;
                    bankStepBefore = dstStepBeforeReadX;
                    bankStepAfter  = dstStepAfterReadX;
                } else {
                    //-- we travel row without writing action
                    xref = srcMaxx;
                    bankStepBefore = bankStepAfter = 0;
                }

                int xpos = 0;
                bankID += bankStepBefore;
                int b = 0;
                while (xpos < sourceScanlineStride) {
                    rasterReader.seek(currentBuffPos);
                    int n = rasterReader.readByte();
                    if (n >= -127 && n <= - 1) {
                        n = - n + 1; //-- we write n times the following value.
                        final long writeValue = rasterReader.readByte() & 0xFFL;
                        for (int i = 0; i < n; i++) {
                            //-- build sample in relation with bits per samples --//
                            dataContainer = dataContainer | (writeValue << maskCount);
                            maskCount += Byte.SIZE;
                            if (maskCount == bitpersampl) {
                                if (xpos == xref && xref < srcMaxx) {
                                    switch (dataType) {
                                        case DataBuffer.TYPE_BYTE   : Array.setByte(targetArray, bankID, (byte) dataContainer); break;
                                        case DataBuffer.TYPE_SHORT  : 
                                        case DataBuffer.TYPE_USHORT : Array.setShort(targetArray, bankID, (short) dataContainer); break;
                                        case DataBuffer.TYPE_INT    : Array.setInt(targetArray, bankID, (int) dataContainer); break;
                                        case DataBuffer.TYPE_FLOAT  : Array.setFloat(targetArray, bankID, Float.intBitsToFloat((int) dataContainer)); break;
                                        case DataBuffer.TYPE_DOUBLE : Array.setDouble(targetArray, bankID, Double.longBitsToDouble(dataContainer)); break;
                                        default: throw new AssertionError(dataType);
                                    }
                                    bankID += planarDenum;
                                    if (++b == pixelLength) {
                                        xref += (sourceXSubsampling - 1) * pixelLength;
                                        b = 0;
                                    }
                                    xref++;
                                }
                                xpos++;
                                
                                //-- initialize sample build --//
                                dataContainer = 0;
                                maskCount = 0;
                            }
                        }
                        currentBuffPos += 2;//-- read n + value
                    } else if (n >= 0 && n < 128) {
                        if (sourceXSubsampling == 1 && dataType == DataBuffer.TYPE_BYTE) {
                            //-- copy data directly in target table
                            int debx = Math.max(xpos, xref);
                            final int endx = Math.min(xpos + n + 1, srcMaxx);
                            if (debx < endx & xref < srcMaxx) {
                                /*
                                 * In case where [xpos----[xref--------]xpos+n+1----]srcMaxx
                                 */
                                assert debx >= xpos; //////////////////////////// ici a debboguer
                                currentBuffPos++;//-- n value red
                                if (debx != xpos) {
                                    currentBuffPos += (debx - xpos);
                                    rasterReader.seek(currentBuffPos);
                                }

                                final int length = (endx - debx);
                                
                                if (pC == 2) {
                                    //-- read sample by sample 
                                    for (int l = 0; l < length; l++) {
                                        rasterReader.readFully((byte[]) targetArray, bankID, pixelLength);
                                        bankID += planarDenum;
                                    }
                                } else {
                                    //-- read all length in the same destination band
                                    rasterReader.readFully((byte[]) targetArray, bankID, length);
                                    bankID         += length;
                                }
                                xref           += length;
                                currentBuffPos += 1 + xpos + n - debx;
                            } else {
                                currentBuffPos += n + 2; // n + 1 + 1. we write n+1 byte and we shift buffer cursor by 1.
                            }
                            xpos += n + 1;
                        } else {
                            for (int i = 0; i < n + 1; i++) {// copy the next n + 1 bytes
                                final long val = rasterReader.readByte() & 0xFFL;
                                //-- build sample in relation with bits per samples --//
                                dataContainer = dataContainer | (val << maskCount);
                                maskCount += Byte.SIZE;
                                
                                if (maskCount == bitpersampl) {
                                    if (xpos == xref && xref < srcMaxx) {
                                        switch (dataType) {
                                            case DataBuffer.TYPE_BYTE   : Array.setByte(targetArray, bankID, (byte) dataContainer); break;
                                            case DataBuffer.TYPE_SHORT  : 
                                            case DataBuffer.TYPE_USHORT : Array.setShort(targetArray, bankID, (short) dataContainer); break;
                                            case DataBuffer.TYPE_INT    : Array.setInt(targetArray, bankID, (int) dataContainer); break;
                                            case DataBuffer.TYPE_FLOAT  : Array.setFloat(targetArray, bankID, Float.intBitsToFloat((int) dataContainer)); break;
                                            case DataBuffer.TYPE_DOUBLE : Array.setDouble(targetArray, bankID, Double.longBitsToDouble(dataContainer)); break;
                                            default: throw new AssertionError(dataType);
                                        }
                                        bankID += planarDenum;
                                        if (++b == pixelLength) {
                                            xref += (sourceXSubsampling - 1) * pixelLength;
                                            b = 0;
                                        } 
                                        xref++;
                                    }
                                    xpos++;
                                    
                                    //-- initialize sample build --//
                                    dataContainer = 0;
                                    maskCount = 0;
                                }
                            }
                            assert n >= 0;
                            currentBuffPos += (n + 2); // n + 1 + 1. we write n + 1 bytes and we shift buffer cursor by 1.
                        }
                    } else {
                        currentBuffPos++;
                        continue;
                    }
                }
                ypos++;
                bankID += bankStepAfter;
            }
        }
        }
    }
    
    /**
     * Verifies if all the strips are sequenced as a result in the file.
     *
     * @param stripOffsets strip offset table.
     * @param stripByteCounts strip length in Byte.
     * @return true if all the strips are sequenced as a result in the file else false.
     */
    private boolean checkStrips(long[] stripOffsets, long[] stripByteCounts) {
        for (int i = 0; i < stripOffsets.length-1; i++) {
            if (stripOffsets[i] + stripByteCounts[i] != stripOffsets[i + 1]) return false;
        }
        return true;
    }

    /**
     * Processes to the image reading, and stores the pixels in the given raster.
     *
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void readFromTiles(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException
    {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer(); 
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());
        
        //-- planar configuration --//
        final Map<String, Object> planarConfig = headProperties.get(PlanarConfiguration);
        short pC = 1;
        /*
         * If samples per pixel = 1, planar configuration has no impact.
         */
        if (planarConfig != null && samplesPerPixel > 1) {
            pC = ((short[]) planarConfig.get(ATT_VALUE)) [0];
        }
        final int planarStep = (pC == 1) ? samplesPerPixel : 1;
        
        //-- fillOrder --//
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        //-- adapt imageStream in function of fill order value --//
        final ImageInputStream rasterReader = getImageInputStream(fO == 2);
        
        //-- tile index from source area --//
        final int minTileX = srcRegion.x / tileWidth;
        final int minTileY = srcRegion.y / tileHeight;
        final int maxTileX = (srcRegion.x + srcRegion.width  + tileWidth  - 1) / tileWidth;
        final int maxTileY = (srcRegion.y + srcRegion.height + tileHeight - 1) / tileHeight;
        
        //-- tile number from source image dimension --//
        final int numXTile = (imageWidth + tileWidth - 1) / tileWidth;
        final int numYTile = (imageHeight + tileHeight - 1) / tileHeight;
        
        //-- srcRegion max coordinates --//
        final int srcRegionMaxX = srcRegion.x + srcRegion.width;
        final int srcRegionMaxY = srcRegion.y + srcRegion.height;
        
        final long bitpersampl = bitsPerSample[0];
        
        final long sourceScanTileStride = tileWidth * planarStep * bitpersampl / Byte.SIZE;
        
        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final Object targetArray;
            switch (dataType) {
                case DataBuffer.TYPE_BYTE   : targetArray = ((DataBufferByte)   dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_USHORT : targetArray = ((DataBufferUShort) dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_SHORT  : targetArray = ((DataBufferShort)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_INT    : targetArray = ((DataBufferInt)    dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_FLOAT  : targetArray = ((DataBufferFloat)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_DOUBLE : targetArray = ((DataBufferDouble) dataBuffer).getData(bank); break;
                default: throw new AssertionError(dataType);
            }
            
            final int targetRegionOffset = bankOffsets[bank] + dstRegion.y * targetScanlineStride + dstRegion.x * samplesPerPixel;
            
            for (int s = 0; s < samplesPerPixel; s += planarStep) { 
                final int tileIndexOffset = s * numXTile * numYTile;
                for (int ty = minTileY; ty < maxTileY; ty++) {
                    
                    final int rowTileIndexOffset = ty * numXTile;
                    final int interMinY          = Math.max(srcRegion.y, ty * tileHeight);
                    final int interMaxY          = Math.min(srcRegionMaxY, (ty + 1) * tileHeight);
                    
                    final int sourceYOffset = (((interMinY - srcRegion.y) % sourceYSubsampling) == 0) ? 0 : (sourceYSubsampling - ((interMinY - srcRegion.y)) % sourceYSubsampling);
                    if (sourceYOffset >= tileHeight || (interMinY + sourceYOffset >= interMaxY)) continue;
                    
                    final int targetRowOffset = ((interMinY - srcRegion.y + sourceYSubsampling - 1) / sourceYSubsampling) * targetScanlineStride;
                    
         nextTile : for (int tx = minTileX; tx < maxTileX; tx++) {
             
                        //-- define first byte file position to read --//
                        final int tileIndex = tileIndexOffset + rowTileIndexOffset + tx;
                        assert rasterReader.getBitOffset() == 0;
                        
                        //-- define intersection between srcRegion and current tile --//
                        final int interMinX       = Math.max(srcRegion.x, tx * tileWidth);
                        final int interMaxX       = Math.min(srcRegionMaxX, (tx + 1) * tileWidth);
                        
                        //-- source offset in x direction --//
                        final int sourceColOffset = (interMinX - srcRegion.x) % sourceXSubsampling == 0 ? 0 : (sourceXSubsampling - ((interMinX - srcRegion.x) % sourceXSubsampling));
                        //-- in case where subsampling is more longer than tilewidth --//
                        if (sourceColOffset >= tileWidth || (interMinX + sourceColOffset) >= interMaxX) continue nextTile;
                        
                        //-- target begin position --//
                        int targetOffset = targetRegionOffset + targetRowOffset + ((interMinX - srcRegion.x + sourceXSubsampling - 1) / sourceXSubsampling) * samplesPerPixel;
                        int targetPos    = targetOffset + s;
                        int nextTargetPos = targetPos + targetScanlineStride;
                        
                        int srcXStep, targetReadLength, targetStep;
                        if (pC == 1 && sourceXSubsampling == 1) {
                            //-- no planar configuration specification
                            srcXStep = interMaxX - interMinX - sourceColOffset;
                            targetReadLength = targetStep = srcXStep * samplesPerPixel;
                        } else {
                            srcXStep         = sourceXSubsampling;
                            targetReadLength = planarStep;
                            targetStep       = samplesPerPixel;
                        }
                        
                        //-- de ligne en ligne 
                        for (int srcY = interMinY + sourceYOffset; srcY < interMaxY; srcY += sourceYSubsampling) {
                            for (int srcX = interMinX + sourceColOffset; srcX < interMaxX; srcX += srcXStep) {
                                
                                rasterReader.seek(tileOffsets[tileIndex] + (srcY - ty * tileHeight) * sourceScanTileStride +(srcX - tx * tileWidth) * planarStep * (bitpersampl / Byte.SIZE));
                                switch (dataType) {
                                    case DataBuffer.TYPE_BYTE   : rasterReader.readFully((byte[])   targetArray, targetPos, targetReadLength); break;
                                    case DataBuffer.TYPE_USHORT :
                                    case DataBuffer.TYPE_SHORT  : rasterReader.readFully((short[])  targetArray, targetPos, targetReadLength); break;
                                    case DataBuffer.TYPE_FLOAT  : rasterReader.readFully((float[])  targetArray, targetPos, targetReadLength); break;
                                    case DataBuffer.TYPE_INT    : rasterReader.readFully((int[])    targetArray, targetPos, targetReadLength); break;
                                    case DataBuffer.TYPE_DOUBLE : rasterReader.readFully((double[]) targetArray, targetPos, targetReadLength); break;
                                    default: throw new AssertionError(dataType);
                                }
                              
                                //-- target --//
                                targetPos += targetStep;
                            }
                            //-- dest --//
                            targetPos = nextTargetPos;
                            nextTargetPos += targetScanlineStride;
                        }
                    }
                } 
            }
        }
    }
    
    /**
     * Processes to the image reading, and stores the pixels in the given raster.
     *
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void readFromTilesLZW(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException
    {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer();
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());
        
        //-- planar configuration --//
        final Map<String, Object> planarConfig = headProperties.get(PlanarConfiguration);
        short pC = 1;
        /*
         * If samples per pixel = 1, planar configuration has no impact.
         */
        if (planarConfig != null && samplesPerPixel > 1) {
            pC = ((short[]) planarConfig.get(ATT_VALUE)) [0];
        }
        final int pixelLength = (pC == 1) ? samplesPerPixel : 1;
        final int planarDenum = (pC == 2) ? samplesPerPixel : 1;
        
        //-- predictor study ---//
        final Map<String, Object> predictor = (headProperties.get(Predictor));
        final short predic    = (predictor != null) ? (short) ((long[]) predictor.get(ATT_VALUE)) [0] : 1;
        //-- array which represent a pixel to permit horizontal differencing if exist --//
        final long[] prediPix = new long[pixelLength];
        
        //-- fillOrder --//
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        //-- adapt imageStream in function of fill order value --//
        final ImageInputStream rasterLZWReader = getImageInputStream(fO == 2);
        
        //-- tile index from source area --//
        final int minTileX = srcRegion.x / tileWidth;
        final int minTileY = srcRegion.y / tileHeight;
        final int maxTileX = (srcRegion.x + srcRegion.width  + tileWidth  - 1) / tileWidth;
        final int maxTileY = (srcRegion.y + srcRegion.height + tileHeight - 1) / tileHeight;
        
        //-- tile number from source image dimension --//
        final int numXTile = (imageWidth + tileWidth - 1) / tileWidth;
        final int numYTile = (imageHeight + tileHeight - 1) / tileHeight;
        
        //-- srcRegion max coordinates --//
        final int srcRegionMaxX = srcRegion.x + srcRegion.width;
        final int srcRegionMaxY = srcRegion.y + srcRegion.height;
        
        final int sourceScanTileStride = tileWidth * pixelLength;
        
        final long bitpersampl = bitsPerSample[0];
        
        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final Object targetArray;
            switch (dataType) {
                case DataBuffer.TYPE_BYTE   : targetArray = ((DataBufferByte)   dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_USHORT : targetArray = ((DataBufferUShort) dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_SHORT  : targetArray = ((DataBufferShort)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_INT    : targetArray = ((DataBufferInt)    dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_FLOAT  : targetArray = ((DataBufferFloat)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_DOUBLE : targetArray = ((DataBufferDouble) dataBuffer).getData(bank); break;
                default: throw new AssertionError(dataType);
            }
            
            final int targetRegionOffset = bankOffsets[bank] + dstRegion.y * targetScanlineStride + dstRegion.x * samplesPerPixel;
            
            for (int s = 0; s < samplesPerPixel; s += pixelLength) {
                final int tileIndexOffset = s * numXTile * numYTile;
                for (int ty = minTileY; ty < maxTileY; ty++) {
                    
                    final int rowTileIndexOffset = ty * numXTile;
                    final int interMinY          = Math.max(srcRegion.y, ty * tileHeight);
                    final int interMaxY          = Math.min(srcRegionMaxY, (ty + 1) * tileHeight);
                    
                    final int yOffset = (((interMinY - srcRegion.y) % sourceYSubsampling) == 0) ? 0 : (sourceYSubsampling - ((interMinY - srcRegion.y)) % sourceYSubsampling);
                    if (yOffset >= tileHeight || (interMinY + yOffset) >= interMaxY) continue;
                    final int rowSampleOffset = (interMinY + yOffset - ty * tileHeight) * sourceScanTileStride;
                    final int targetRowOffset = ((interMinY - srcRegion.y + sourceYSubsampling - 1) / sourceYSubsampling) * targetScanlineStride;
                    
         nextTile : for (int tx = minTileX; tx < maxTileX; tx++) {
             
                        //-- define first byte file position to read --//
                        final int tileIndex = tileIndexOffset + rowTileIndexOffset + tx;
                        long currentBuffPos = tileOffsets[tileIndex];
                        rasterLZWReader.seek(currentBuffPos);
                        assert rasterLZWReader.getBitOffset() == 0;
                        
                        //-- define intersection between srcRegion and current tile --//
                        final int interMinX       = Math.max(srcRegion.x, tx * tileWidth);
                        final int interMaxX       = Math.min(srcRegionMaxX, (tx + 1) * tileWidth);
                        
                        //-- source offset in x direction --//
                        final int sourceColOffset = (interMinX - srcRegion.x) % sourceXSubsampling == 0 ? 0 : (sourceXSubsampling - ((interMinX - srcRegion.x) % sourceXSubsampling));
                        //-- in case where subsampling is more longer than tilewidth --//
                        if (sourceColOffset >= tileWidth || (interMinX + sourceColOffset) >= interMaxX) continue nextTile;
                        final int maxSampleXPos = (interMaxX - tx * tileWidth) * pixelLength;
                        //-- target begin position --//
                        int targetOffset = targetRegionOffset + targetRowOffset + ((interMinX - srcRegion.x + sourceXSubsampling - 1) / sourceXSubsampling) * samplesPerPixel + s;
                        int targetPos    = targetOffset;
                        
                       /*
                        * With LZW compression we must read all byte to build appropriate LZW map container.
                        * We define to positions "posRef" and "maxRowRefPos" where "posRef" represent
                        * index of current sample which will be written in source array and "maxRowRefPos" the last exclusive written sample.
                        */
                        int posRef = rowSampleOffset + (interMinX + sourceColOffset - tx * tileWidth) * pixelLength;
                        
                        int nextPosRef         = posRef + sourceYSubsampling * sourceScanTileStride;
                        int maxRowRefPos       = rowSampleOffset + maxSampleXPos;
                        final int maxSamplePos = (interMaxY - ty * tileHeight - 1) * sourceScanTileStride + maxSampleXPos;
                        int samplePos          = 0;
                       
                        //-- initialize LZW attributs --//
                        //-- length in bit of lzw data --//
                        int currentLZWCodeLength = 9;
                        //-- byte array map use to decompresse LZW datas --//
                        byte[][] lzwTab          = new byte[LZW_CLEAR_CODE][];

                        //-- current LZW array index --// 
                        int idLZWTab         = 0;
                        int maxIDLZWTab      = 511; //--> (1 << currentLZWCodeLength) - 1
                        //-- precedently iteration LZW code --//
                        byte[] oldCodeLZW    = null;
                        int hdb              = 0;
                        Arrays.fill(prediPix, 0);
                
                        //-- bytePos must read throught all file byte per byte --//
                        int bytePos = 0;
                        int b       = 0;
                        short codeLZW;
                        
                       /*
                        * Long container use to build a sample,
                        * because each sample is read byte per byte regardless their bit size.
                        */
                       long dataContainer = 0;
                       int maskCount      = 0;
                
                        //-- work sample by sample --//
                        while (LZW_EOI_CODE != (codeLZW = readLZWCode(rasterLZWReader, currentLZWCodeLength))) {
                            if ((idLZWTab + 258) == 4095) 
                                assert codeLZW == 256 : "when LZW map array reach its maximum index value the next value in file should be clear code 256.";
                          
                            if (codeLZW == LZW_CLEAR_CODE) {
                                currentLZWCodeLength = 9;
                                lzwTab               = new byte[LZW_CLEAR_CODE][];
                                idLZWTab             = 0;
                                maxIDLZWTab          = 511;
                                oldCodeLZW           = null;
                                continue;
                            }
                    
                            assert (oldCodeLZW != null || (oldCodeLZW == null && codeLZW < LZW_CLEAR_CODE)) :"After a clear code, next code should be smaller than 256";
                    
                            byte[] entree;
                            if (codeLZW >= 258) {
                                if (lzwTab[codeLZW - 258] != null) {
                                    entree = lzwTab[codeLZW - 258];
                                } else {
                                    // w + w[0]
                                    final int oldCLen = oldCodeLZW.length;
                                    entree            = Arrays.copyOf(oldCodeLZW, oldCLen + 1);
                                    entree[oldCLen]   = oldCodeLZW[0];
                                }
                            } else {
                                entree = new byte[] { (byte) codeLZW };
                            }

                            assert entree != null;

                            //-- write entree --//
                            for (int i = 0; i < entree.length; i++) {
                                //-- build sample in relation with bits per samples --//
                                final long val = entree[i] & 0x000000FFL;
                                dataContainer  = dataContainer | (val << maskCount);
                                maskCount     += Byte.SIZE;

                                //-- if a sample is built --//
                                if (maskCount == bitpersampl) {
                                    //-- add in precedently array before insertion --//
                                    //-- if horizontal differencing add with precedently value --//
                                    prediPix[hdb] = (predic == 2) ? (prediPix[hdb] + dataContainer) : dataContainer;
                                    if (++hdb == pixelLength) hdb = 0;

                                    //-- re-initialize datacontainer --//
                                    dataContainer = 0;
                                    maskCount     = 0;
                                    
                                    //-- write sample in target array if its necessary --//
                                    if (samplePos == posRef) { 
                                        switch (dataType) {
                                             case DataBuffer.TYPE_BYTE   : Array.setByte(targetArray, targetPos, (byte) (prediPix[b])); break;
                                            case DataBuffer.TYPE_SHORT  : 
                                            case DataBuffer.TYPE_USHORT : Array.setShort(targetArray, targetPos, (short) (prediPix[b])); break;
                                            case DataBuffer.TYPE_INT    : Array.setInt(targetArray, targetPos, (int) (prediPix[b])); break;
                                            case DataBuffer.TYPE_FLOAT  : Array.setFloat(targetArray, targetPos, Float.intBitsToFloat((int) (prediPix[b]))); break;
                                            case DataBuffer.TYPE_DOUBLE : Array.setDouble(targetArray, targetPos, Double.longBitsToDouble(prediPix[b])); break;
                                            default: throw new AssertionError(dataType);
                                        }
                                        targetPos += planarDenum; 
                                        if (++b == pixelLength) {
                                            posRef += (sourceXSubsampling - 1) * pixelLength;
                                            b = 0;
                                        }
                                        posRef++;
                                        //-- this if means : pass to the next destination image row --//
                                        if (posRef >= maxRowRefPos) {
                                            assert hdb == 0 : "hdb should be zero. hdb = "+hdb;
                                            
                                            //-- begin source position writing --//
                                            posRef      = nextPosRef;
                                            nextPosRef += sourceYSubsampling * sourceScanTileStride;
                                            
                                            //-- ending source position writing --//
                                            maxRowRefPos += sourceYSubsampling * sourceScanTileStride;
                                            
                                            //-- if it is unnecessary to finish to read current tile --//
                                            if (posRef >= maxSamplePos) {
                                                assert maxRowRefPos >= maxSamplePos : "maxRowrefpos = "+maxRowRefPos+" maxSamplepos = "+maxSamplePos;
                                                continue nextTile;
                                            }

                                            //-- destination shifts --//
                                            targetOffset += targetScanlineStride;
                                            targetPos = targetOffset;

                                        }
                                    }
                                    //-- shift by one when a sample was built --//
                                    samplePos++;
                                }
                                if (++bytePos == sourceScanTileStride) {
                                    //-- initialize predictor array --//
                                    Arrays.fill(prediPix, 0);
                                    bytePos = 0;
                                }
                            }

                            if (oldCodeLZW == null) {
                                assert idLZWTab == 0 : "With old code null : lzw tab must be equals to zero.";
                                assert entree.length == 1;
                                oldCodeLZW = entree;
                                continue;
                            }

                            //-- add in LZW map array --//
                            final int oldLen      = oldCodeLZW.length;
                            final byte[] addedTab = Arrays.copyOf(oldCodeLZW, oldLen + 1);
                            addedTab[oldLen]      = entree[0];
                            lzwTab[idLZWTab++]    = addedTab;

                            //-- if current map index reach the maximum value permit by bit number --//
                            if (((idLZWTab + 258) & 0xFFFF) == (maxIDLZWTab)) {
                               /*
                                * When LZW algorithm reach its maximum index value 4095, to don't exceed 12 bits capacity
                                * a clear code 256 is normaly written in the CURRENT (12) bit length.
                                * Continue to force next read in current bit length.
                                * Moreover after this continue an assertion verify this expected comportement.
                                */
                               if (maxIDLZWTab == 4095) continue;
                               currentLZWCodeLength++;
                               final int nextLZWMapLength = 1 << currentLZWCodeLength;
                               maxIDLZWTab                = nextLZWMapLength - 1;
                               lzwTab                     = Arrays.copyOf(lzwTab, nextLZWMapLength);
                            }
                            oldCodeLZW = entree;
                            //---------------------------------------------------------//
                        }
//                        assert samplePos == maxSamplePos : "pos = "+samplePos+" Expected pos+tx+", "+ty+")";
                    }
                } 
            }
        }
    }

    /**
     * Return an image input stream for data usage.
     * @param reversedReading True if we want the returned stream to inverse byte values at reading. False otherwise.
     * @param forceReset True if we want to force returned stream to point at the beginning of the image.
     * @return an {@link javax.imageio.stream.ImageInputStream} for data reading.
     * @throws IOException If we've got a problem while reseting stream position, or initializing it.
     */
    private ImageInputStream getImageInputStream(boolean reversedReading) throws IOException {
        // If we've got an uncompressed image, we are not forced to rewind our source input stream.
        if (!reversedReading && compression != 1) {
            if (imageStream != null) return imageStream;
            else if (currentInput instanceof ImageInputStream) return (ImageInputStream) currentInput;
        }

        channel = openChannel(currentInput);
        if (currentInput instanceof FileInputStream) ((SeekableByteChannel)channel).position(portosfileChannelPositionBegin);
        buffer = null;

        final boolean containData;
        if (buffer == null) {
            containData = false;
            buffer = ByteBuffer.allocateDirect(8192);
        } else {
            containData = true;
        }

        final ImageInputStream result = new ChannelImageInputStream(null, reversedReading? new ReversedBitsChannel(channel) : channel, buffer, containData);
        if (imageStream != null) result.setByteOrder(imageStream.getByteOrder());
        return result;
    }

    /**
     * Create the channel used as source data for reading {@link javax.imageio.stream.ImageInputStream}.
     * @param input The input data to open a channel from.
     * @return A {@link java.nio.channels.ReadableByteChannel} to get data from input object.
     * @throws IOException If given object is of unsupported type.
     */
    private static ReadableByteChannel openChannel(final Object input) throws IOException {
        if (input instanceof File) {
            return new FileInputStream((File)input).getChannel();
        } else if (input instanceof InputStream) {
            if (!(input instanceof FileInputStream)) ((InputStream) input).reset();
            return Channels.newChannel((InputStream) input);
        } else if (input instanceof ImageInputStream) {
            final ImageInputStream IIS = (ImageInputStream) input;
            IIS.reset(); IIS.mark();
            return Channels.newChannel(new InputStreamAdapter((ImageInputStream) input));
        } else {
            throw new IOException("Input object is not a valid file or input stream.");
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

    /**
     * Service provider interface (SPI) for {@code RawTiffImageReader}s. This SPI provides
     * necessary implementation for creating default {@link RawTiffImageReader} instances.
     * <p>
     * The default constructor initializes the fields to the values listed below.
     * Users wanting different values should create a subclass of {@code Spi} and
     * set the desired values in their constructor.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "tiff"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "image/tiff"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.RawTiffImageReader"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;Value of {@link org.geotoolkit.util.Version#GEOTOOLKIT}&nbsp;</td></tr>
     * </table>
     * <p>
     * By default, this provider register itself <em>after</em> the provider supplied by the
     * <cite>Image I/O extension for JAI</cite>, because the later supports a wider range of
     * formats. See {@link #onRegistration onRegistration} for more information.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.16
     *
     * @since 3.16
     * @module
     */
    public static class Spi extends SpatialImageReader.Spi implements SystemOverride {
        /**
         * Default list of file extensions.
         */
        private static final String[] SUFFIXES = new String[] {"tiff", "tif", "geotiff", "geotif"};

        /**
         * The mime types for the {@link RawTiffImageReader}.
         */
        private static final String[] MIME_TYPES = {"image/tiff", "image/x-geotiff", "image/tiff;subtype=geotiff"};

        /**
         * The list of valid input types.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {
            File.class, String.class, InputStream.class, ImageInputStream.class
        };

        /**
         * Constructs a default {@code TiffImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            super();
            names           = SUFFIXES;
            suffixes        = SUFFIXES;
            inputTypes      = INPUT_TYPES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.plugin.TiffImageReader";
            // Current implementation does not support metadata.
            nativeStreamMetadataFormatName = null;
            nativeImageMetadataFormatName  = null;
        }

        /**
         * Returns a brief, human-readable description of this service provider
         * and its associated implementation. The resulting string should be
         * localized for the supplied locale, if possible.
         *
         * @param  locale A Locale for which the return value should be localized.
         * @return A String containing a description of this service provider.
         */
        @Override
        public String getDescription(final Locale locale) {
            return "TIFF image reader";
        }
        
        /**
         * Current implementation returns {@code false} in every case. Future implementation
         * may perform a better check if the {@link RawTiffImageReader} become less restrictive.
         *
         * @param  source The input source to be decoded.
         * @return {@code true} if the given source can be used by {@link RawTiffImageReader}.
         * @throws IOException if an I/O error occurs while reading the stream.
         */
        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            if (source instanceof InputStream && (!(source instanceof FileInputStream))) {
                final InputStream stream = (InputStream) source;
                if (stream.markSupported()) {
                    try {
                        stream.reset();
                    } catch (IOException e) {
                        stream.mark(Integer.MAX_VALUE);
                    }
                }else {
                    return false;
                }
            } else if (source instanceof ImageInputStream) {
                ((ImageInputStream) source).reset();
                ((ImageInputStream) source).mark();
            }
            final ReadableByteChannel channel = openChannel(source);
            long position = 0;
            if (channel instanceof SeekableByteChannel) position = ((SeekableByteChannel) channel).position();
            //-- Closing the imageStream will close the input stream.
            ByteBuffer buffer = ByteBuffer.allocateDirect(16);
            buffer.clear();
            channel.read(buffer);
            buffer.position(0);
            try {
                final byte c = buffer.get();
                if (c != buffer.get()) {
                    return false;
                }
                final ByteOrder order;
                if (c == 'M') {
                    order = ByteOrder.BIG_ENDIAN;
                } else if (c == 'I') {
                    order = ByteOrder.LITTLE_ENDIAN;
                } else {
                    return false;
                }
                final short version = buffer.order(order).getShort(); 
                if ((version == 0x002B)) {
                    if (buffer.getShort() != 8 || buffer.getShort() != 0) {
                        return false; //-- invalide offset size
                    }
                } else if (version != 0x002A) {
                    return false;//-- invalid magic number
                }

                if (source instanceof InputStream) {
                    ((InputStream) source).reset();
                } else if (source instanceof ImageInputStream) {
                    ((ImageInputStream) source).reset();
                }
                return true;
            } finally {
                if (channel instanceof SeekableByteChannel) ((SeekableByteChannel) channel).position(position);
            }
        }

        /**
         * Returns an instance of the {@code ImageReader} implementation associated
         * with this service provider.
         *
         * @param  extension An optional extension object, which may be null.
         * @return An image reader instance.
         * @throws IOException if the attempt to instantiate the reader fails.
         */
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new TiffImageReader(this);
        }

        /**
         * Invoked when this Service Provider is registered. By default, this method
         * {@linkplain ServiceRegistry#setOrdering(Class, Object, Object) sets the ordering}
         * of this {@code RawTiffImageReader.Spi} after the one provided in <cite>Image I/O
         * extension for JAI</cite>. This behavior can be changed by setting the
         * <code>{@value org.geotoolkit.lang.SystemOverride#KEY_ALLOW_OVERRIDE}</code>
         * system property explicitly to {@code true}.
         * <p>
         * Note that the Geotk TIFF image reader will be selected only if the source given to the
         * {@link #canDecodeInput(Object)} method is compliant with the restrictions documented
         * in {@link RawTiffImageReader} javadoc, otherwise the standard TIFF image reader will
         * be selected instead.
         *
         * @param registry The registry where is service is registered.
         * @param category The category for which this service is registered.
         */
        @Override
        public void onRegistration(final ServiceRegistry registry, final Class<?> category) {
            super.onRegistration(registry, category);
            if (category.equals(ImageReaderSpi.class)) {
                for (Iterator<ImageReaderSpi> it = registry.getServiceProviders(ImageReaderSpi.class, false); it.hasNext();) {
                    ImageReaderSpi other = it.next();
                    if (other != this && ArraysExt.contains(other.getFormatNames(), "tiff")) {
                        ImageReaderSpi last = this;
                        try {
                            if (Boolean.getBoolean(KEY_ALLOW_OVERRIDE)) {
                                last  = other;
                                other = this;
                            }
                        } catch (SecurityException e) {
                            Logging.recoverableException(TiffImageReader.Spi.class, "onRegistration", e);
                        }
                        registry.setOrdering(ImageReaderSpi.class, other, last);
                    }
                }
            }
        }
    }
}