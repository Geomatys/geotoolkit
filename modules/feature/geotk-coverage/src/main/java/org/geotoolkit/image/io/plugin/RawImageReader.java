/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.plugin;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;

import com.sun.media.imageio.stream.RawImageInputStream;

import org.geotoolkit.image.SampleModels;
import org.geotoolkit.image.io.SampleConverter;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.lang.SystemOverride;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Descriptions;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.io.UnsupportedImageFormatException;


/**
 * Image reader for raw binary files. The default implementation can process only inputs of type
 * {@link RawImageInputStream}. However subclasses can process arbitrary {@link ImageInputStream}
 * if they override the following methods:
 * <p>
 * <ul>
 *   <li>{@link #getWidth(int)} (mandatory)</li>
 *   <li>{@link #getHeight(int)} (mandatory)</li>
 *   <li>{@link #getRawDataType(int)} (mandatory unless the default value,
 *       which is {@link DataBuffer#TYPE_FLOAT}, is suitable)</li>
 * </ul>
 * <p>
 * This class provides similar functionalities than the RAW image reader provided by the
 * <cite>Image I/O extensions for JAI</cite> library. The main difference is that this class
 * can be extended, and provides support for color palette and "<cite>no data value</cite>"
 * conversion as documented in the super-class. Experience also suggests that this class is
 * faster at least for floating point values. In addition, version 1.1 of the <cite>Image I/O
 * extension for JAI</cite> seems to have a bug in their reading of subsampled floating point
 * values.
 *
 * {@section Restrictions on the sample model}
 * The current implementations requires that the image in the stream has a
 * {@linkplain SampleModels#getPixelStride(SampleModel) pixel stride} equals to 1.
 * If the pixel stride may be different, consider using the reader provided by the
 * <cite>Image I/O extensions for JAI</cite> library instead.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @see RawImageInputStream
 *
 * @since 3.07 (derived from 2.0)
 * @module
 */
public class RawImageReader extends SpatialImageReader {
    /**
     * Constructs a new image reader.
     *
     * @param provider the {@link ImageReaderSpi} that is invoking this constructor, or null.
     */
    public RawImageReader(final Spi provider) {
        super(provider);
    }

    /**
     * Returns the number of images available from the current input source. The default
     * implementation fetches this information from the input stream if it is of kind
     * {@link RawImageInputStream}, or delegates to the
     * {@linkplain SpatialImageReader#getNumImages(boolean) super-class} otherwise.
     *
     * @throws IllegalStateException if the input source has not been set.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IllegalStateException, IOException {
        if (input instanceof RawImageInputStream) {
            return ((RawImageInputStream) input).getNumImages();
        }
        return super.getNumImages(allowSearch);
    }

    /**
     * Returns the image's width. The default implementation fetches this information from the
     * input stream if it is of kind {@link RawImageInputStream}, or thrown an exception otherwise.
     * Subclasses can override this method if they can get the image width in an other way.
     *
     * @throws IOException If the width can not be obtained of an I/O error occurred.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (input instanceof RawImageInputStream) {
            final Dimension size = ((RawImageInputStream) input).getImageDimension(imageIndex);
            if (size != null) {
                return size.width;
            }
        }
        throw new IIOException(Errors.format(Errors.Keys.UnspecifiedImageSize));
    }

    /**
     * Returns the image's height. The default implementation fetches this information from the
     * input stream if it is of kind {@link RawImageInputStream}, or thrown an exception otherwise.
     * Subclasses can override this method if they can get the height width in an other way.
     *
     * @throws IOException If the height can not be obtained of an I/O error occurred.
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (input instanceof RawImageInputStream) {
            final Dimension size = ((RawImageInputStream) input).getImageDimension(imageIndex);
            if (size != null) {
                return size.height;
            }
        }
        throw new IIOException(Errors.format(Errors.Keys.UnspecifiedImageSize));
    }

    /**
     * Returns the number of bands available for the specified image. The default
     * implementation fetches this information from the input stream if it is of kind
     * {@link RawImageInputStream}, or delegates to the
     * {@linkplain SpatialImageReader#getNumBands(int) super-class} otherwise.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        if (input instanceof RawImageInputStream) {
            final ImageTypeSpecifier type = ((RawImageInputStream) input).getImageType();
            if (type != null) {
                return type.getSampleModel().getNumBands();
            }
        }
        return super.getNumBands(imageIndex);
    }

    /**
     * Returns the data type which most closely represents the "raw" internal data of the image.
     * It should be one of {@link DataBuffer} constants. The default implementation fetches this
     * information from the input stream if it is of kind {@link RawImageInputStream}, or returns
     * {@link DataBuffer#TYPE_FLOAT} otherwise.
     * <p>
     * Subclasses can override this method if they can get the data type in an other way.
     * See the {@linkplain SpatialImageReader#getRawDataType(int) super-class javadoc}
     * for an explanation about how the returned type is used.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The data type.
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    protected int getRawDataType(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (input instanceof RawImageInputStream) {
            final ImageTypeSpecifier type = ((RawImageInputStream) input).getImageType();
            if (type != null) {
                return type.getSampleModel().getDataType();
            }
        }
        return super.getRawDataType(imageIndex);
    }

    /**
     * Returns the data type which most closely represents the "raw" internal data of the image.
     * The default implementation fetches this information from the input stream if it is of kind
     * {@link RawImageInputStream}, or delegates to the
     * {@linkplain SpatialImageReader#getRawImageType(int) super-class} otherwise.
     */
    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (input instanceof RawImageInputStream) {
            final ImageTypeSpecifier type = ((RawImageInputStream) input).getImageType();
            if (type != null) {
                return type;
            }
        }
        return super.getRawImageType(imageIndex);
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
     * Reads the image indexed by {@code imageIndex}.
     *
     * @param  imageIndex  The index of the image to be retrieved.
     * @param  param       Parameters used to control the reading process, or null.
     * @return The desired portion of the image.
     * @throws IOException if an input operation failed.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        checkImageIndex(imageIndex);
        clearAbortRequest();
        final ImageInputStream   input       = (ImageInputStream) getInput();
        final ImageTypeSpecifier imageType   = getRawImageType(imageIndex);
        final SampleModel        streamModel = imageType.getSampleModel();
        if (SampleModels.getPixelStride(streamModel) != 1) {
            throw new UnsupportedImageFormatException(Errors.format(Errors.Keys.UnsupportedFileType_1,
                    Classes.getShortClassName(streamModel) + "[pixelStride = " +
                    SampleModels.getPixelStride(streamModel) + ']'));
        }
        /*
         * Get informations about the layout of the image in the stream.
         * If the user requested some image other than the one at index 0,
         * advance to that image.
         */
        final int width          = getWidth   (imageIndex);
        final int height         = getHeight  (imageIndex);
        final int numSrcBands    = getNumBands(imageIndex);
        final int dataType       = streamModel.getDataType();
        final int scanlineStride = Math.max(SampleModels.getScanlineStride(streamModel), width);
        final int bytesPerSample = SampleModels.getDataTypeSize(streamModel) / Byte.SIZE;
        final long bytesPerRow   = (long)scanlineStride * bytesPerSample;
        /*
         * Extract user's parameters.
         */
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
        /*
         * Get the stream model and the destination image.
         */
        final int numDstBands = (destinationBands != null) ? destinationBands.length : numSrcBands;
        final SampleConverter[] converters  = new SampleConverter[numDstBands];
        final BufferedImage     image       = getDestination(imageIndex, param, width, height, converters);
        final SampleModel       sampleModel = image.getSampleModel();
        checkReadParamBandSettings(param, numSrcBands, sampleModel.getNumBands());
        processImageStarted(imageIndex);
        /*
         * The data can be stored directly in the underlying buffer if there is no subsampling.
         * In order to keep this reader simpler, we also require consecutive pixels in both the
         * source and destination image, otherwise we will fallback on the more generic and slower
         * path.
         */
        final Rectangle srcRegion = new Rectangle();
        final Rectangle dstRegion = new Rectangle();
        computeRegions(param, width, height, image, srcRegion, dstRegion);
        final int dstScanline = SampleModels.getScanlineStride(sampleModel);
        final int interspace = SampleModels.getDataTypeSize(sampleModel) * (sourceXSubsampling - 1) / Byte.SIZE;
        final boolean isRowDirect =  sourceXSubsampling  == 1 &&
                SampleModels.getPixelStride(streamModel) == 1 &&
                SampleModels.getPixelStride(sampleModel) == 1 &&
                sampleModel.getDataType() == dataType;
        final boolean isDirect = isRowDirect && sourceYSubsampling == 1 && dstScanline == scanlineStride;
        /*
         * Now process to the read operation. We read each band sequentially. The values will
         * be either stored directly in the DataBuffer or stored using an iterator, depending
         * on whatever we are at least in the 'isRowDirect' case.
         */
        final WritableRaster raster = image.getRaster();
        final WritableRectIter iter;
        final DataBuffer buffer;
        final int[] offsets;
        if (isRowDirect) {
            iter    = null;
            buffer  = raster.getDataBuffer();
            offsets = buffer.getOffsets();
        } else {
            iter    = RectIterFactory.createWritable(raster, dstRegion);
            buffer  = null;
            offsets = null;
        }
        for (int i=0; i<numDstBands; i++) {
            final int srcBand = (sourceBands      != null) ? sourceBands     [i] : i;
            final int dstBand = (destinationBands != null) ? destinationBands[i] : i;
            final SampleConverter converter = converters[i];
            /*
             * Computes the position for skipping all unwanted pixels in previous images, previous
             * bands and previous rows. The actual seek operation will be applied later.
             */
            long position;
            if (input instanceof RawImageInputStream) {
                position = ((RawImageInputStream) input).getImageOffset(imageIndex);
                position += bytesPerRow * height * srcBand;
            } else {
                position = bytesPerRow * height * (numSrcBands * imageIndex + srcBand);
            }
            position += (bytesPerRow * srcRegion.y) + (bytesPerSample * srcRegion.x);
            /*
             * Optimized case: try to read all data in a single 'readFully' operation, directly
             * in the underlying DataBuffer. Note: there is no API for determining the bank for
             * a given band.  But the common usage in the JDK is either one bank for all bands,
             * or one bank for each band. The calculation of 'bank' below assumes that this
             * usage apply.
             */
            if (isRowDirect) {
                final int bank = (offsets.length == 1) ? 0 : dstBand;
                final int numReads, length;
                int offset = offsets[bank];
                if (isDirect) {
                    numReads = 1;
                    length   = scanlineStride * dstRegion.height;
                } else {
                    numReads = dstRegion.height;
                    length   = dstRegion.width;
                }
                final float progressFactor = 100f / (numReads * numDstBands);
                final int progressInterval = Math.max(1, Math.round(1 / progressFactor));
                for (int j=0; j<numReads; j++) {
                    if (abortRequested()) {
                        processReadAborted();
                        return image;
                    }
                    if ((j % progressInterval) == 0) {
                        processImageProgress((j + i*numReads) * progressFactor);
                    }
                    input.seek(position);
                    switch (dataType) {
                        case DataBuffer.TYPE_BYTE:   {byte  [] array = ((DataBufferByte)   buffer).getData(bank); input.readFully(array, offset, length); converter.convertUnsigned(array, offset, length); break;}
                        case DataBuffer.TYPE_USHORT: {short [] array = ((DataBufferUShort) buffer).getData(bank); input.readFully(array, offset, length); converter.convertUnsigned(array, offset, length); break;}
                        case DataBuffer.TYPE_SHORT:  {short [] array = ((DataBufferShort)  buffer).getData(bank); input.readFully(array, offset, length); converter.convert        (array, offset, length); break;}
                        case DataBuffer.TYPE_INT:    {int   [] array = ((DataBufferInt)    buffer).getData(bank); input.readFully(array, offset, length); converter.convert        (array, offset, length); break;}
                        case DataBuffer.TYPE_FLOAT:  {float [] array = ((DataBufferFloat)  buffer).getData(bank); input.readFully(array, offset, length); converter.convert        (array, offset, length); break;}
                        case DataBuffer.TYPE_DOUBLE: {double[] array = ((DataBufferDouble) buffer).getData(bank); input.readFully(array, offset, length); converter.convert        (array, offset, length); break;}
                        default: throw new UnsupportedImageFormatException(Errors.format(Errors.Keys.UnsupportedDataType));
                    }
                    position += bytesPerRow * sourceYSubsampling;
                    offset   += dstScanline;
                }
                continue;
            }
            /*
             * General case: use the RectIter.
             */
            final float progressFactor = 100f / (dstRegion.height * numDstBands);
            final int progressInterval = Math.max(1, Math.round(1 / progressFactor));
            int currentRow = i*dstRegion.height;
            iter.startBands();
            for (int j=dstBand; --j>=0;) {
                if (iter.nextBandDone()) {
                    throw new IIOException(Errors.format(Errors.Keys.IllegalBandNumber_1, dstBand));
                }
            }
            iter.startLines();
            if (!iter.finishedLines()) do {
                if (abortRequested()) {
                    processReadAborted();
                    return image;
                }
                if ((++currentRow % progressInterval) == 0) {
                    processImageProgress(currentRow * progressFactor);
                }
                input.seek(position);
                iter.startPixels();
                if (!iter.finishedPixels()) do {
                    switch (dataType) {
                        case DataBuffer.TYPE_BYTE:   iter.setSample(converter.convert(input.readUnsignedByte()));  break;
                        case DataBuffer.TYPE_USHORT: iter.setSample(converter.convert(input.readUnsignedShort())); break;
                        case DataBuffer.TYPE_SHORT:  iter.setSample(converter.convert(input.readShort()));         break;
                        case DataBuffer.TYPE_INT:    iter.setSample(converter.convert(input.readInt()));           break;
                        case DataBuffer.TYPE_FLOAT:  iter.setSample(converter.convert(input.readFloat()));         break;
                        case DataBuffer.TYPE_DOUBLE: iter.setSample(converter.convert(input.readDouble()));        break;
                        default: throw new UnsupportedImageFormatException(Errors.format(Errors.Keys.UnsupportedDataType));
                    }
                    if (iter.nextPixelDone()) {
                        break;
                    }
                    if (input.skipBytes(interspace) != interspace) {
                        throw new EOFException();
                    }
                } while (true);
                position += bytesPerRow * sourceYSubsampling;
            } while (!iter.nextLineDone());
        }
        processImageComplete();
        return image;
    }




    /**
     * Service provider interface (SPI) for {@code RawImageReader}s. This SPI provides
     * necessary implementation for creating default {@link RawImageReader} instances.
     * <p>
     * The default constructor initializes the fields to the values listed below.
     * Users wanting different values should create a subclass of {@code Spi} and
     * set the desired values in their constructor.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "raw"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "image/x-raw"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.RawImageReader"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;Value of {@link org.geotoolkit.util.Version#GEOTOOLKIT}&nbsp;</td></tr>
     * </table>
     * <p>
     * By default, this provider register itself <em>after</em> the provider supplied by the
     * <cite>Image I/O extension for JAI</cite>, because the later supports a wider range of
     * sample models. See {@link #onRegistration onRegistration} for more information.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @since 3.07 (derived from 2.0)
     * @module
     */
    public static class Spi extends SpatialImageReader.Spi implements SystemOverride {
        /**
         * Default list of file extensions.
         */
        private static final String[] SUFFIXES = new String[] {"raw"};

        /**
         * The mime types for the {@link RawImageReader}.
         */
        private static final String[] MIME_TYPES = {"image/x-raw"};

        /**
         * The list of valid input types.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {
            RawImageInputStream.class
        };

        /**
         * Constructs a default {@code RawImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = SUFFIXES;
            suffixes        = SUFFIXES;
            inputTypes      = INPUT_TYPES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.plugin.RawImageReader";
            // This reader does not support any metadata.
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
            return Descriptions.getResources(locale).getString(Descriptions.Keys.CodecRaw);
        }

        /**
         * Returns {@code true} if the given source is an instance of {@link RawImageInputStream}
         * and is compatible with the restriction documented in {@link RawImageReader} javadoc.
         *
         * @param  source The input source to be decoded.
         * @return {@code true} if the given source can be used by {@link RawImageReader}.
         * @throws IOException if an I/O error occurs while reading the stream.
         */
        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            if (source instanceof RawImageInputStream) {
                final RawImageInputStream stream = (RawImageInputStream) source;
                final SampleModel model = stream.getImageType().getSampleModel();
                if (SampleModels.getPixelStride(model) == 1) {
                    return true;
                }
            }
            return false;
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
            return new RawImageReader(this);
        }

        /**
         * Invoked when this Service Provider is registered. By default, this method
         * {@linkplain ServiceRegistry#setOrdering(Class, Object, Object) sets the ordering}
         * of this {@code RawImageReader.Spi} after the one provided in
         * <cite>Image I/O extension for JAI</cite>. This behavior can be changed by setting the
         * <code>{@value org.geotoolkit.lang.SystemOverride#KEY_ALLOW_OVERRIDE}</code>
         * system property explicitly to {@code true}.
         * <p>
         * Note that the Geotk RAW image reader will be selected only if the source given to the
         * {@link #canDecodeInput(Object)} method is compliant with the restrictions documented
         * in the javadoc, otherwise the standard RAW image reader will be selected instead.
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
                    if (other != this && ArraysExt.contains(other.getFormatNames(), "raw")) {
                        // Found an other RAW format. For now we process only
                        // the Sun one and leave the others (if any) untouched.
                        if (other.getClass().getName().startsWith("com.sun.media.")) {
                            ImageReaderSpi last = this;
                            try {
                                if (Boolean.getBoolean(KEY_ALLOW_OVERRIDE)) {
                                    last  = other;
                                    other = this;
                                }
                            } catch (SecurityException e) {
                                Logging.recoverableException(Spi.class, "onRegistration", e);
                            }
                            registry.setOrdering(ImageReaderSpi.class, other, last);
                        }
                    }
                }
            }
        }
    }
}
