/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Locale;
import java.io.IOException;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

import ucar.nc2.NetcdfFileWriteable;
import org.opengis.metadata.Metadata;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.collection.BackingStoreException;
import org.geotoolkit.image.io.FileImageWriter;
import org.geotoolkit.metadata.netcdf.NetcdfMetadataWriter;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.ISO_FORMAT_NAME;
import static org.geotoolkit.image.io.plugin.NetcdfImageReader.Spi.*;


/**
 * Base implementation for NetCDF writers.
 *
 * {@section Support of related formats}
 * This implementation uses the <a href="http://www.unidata.ucar.edu/software/netcdf-java/">UCAR
 * NetCDF library</a> for writing data. Consequently, it can be used for writing other formats
 * supported by that library.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public class NetcdfImageWriter extends FileImageWriter {
    /**
     * The NetCDF file where to write the images. This field is set to a non-null value
     * by {@link #prepareWriteSequence(IIOMetadata)}, then reset to a null value by
     * {@link #endWriteSequence()}. Whatever this field is null or not determines
     * whatever a sequence of write operations started or not.
     */
    private NetcdfFileWriteable ncFile;

    /**
     * The list of NetCDF dimensions inferred from the CRS definition.
     */
    private final List<NetcdfDimension> dimensions;

    /**
     * The list of image to write. Each image can be written in one or many NetCDF variables.
     */
    private final List<NetcdfImage> images;

    /**
     * The object to use for writing metadata, created only if needed.
     */
    private transient NetcdfMetadataWriter metadataWriter;

    /**
     * Constructs a new NetCDF writer.
     *
     * @param spi The service provider.
     */
    public NetcdfImageWriter(final Spi spi) {
        super(spi != null ? spi : new Spi());
        dimensions = new ArrayList<>(4);
        images     = new ArrayList<>(4);
    }

    /**
     * Ensures that the NetCDF file is either open (if {@code open} is {@code true}),
     * or not already opened (if {@code open} is {@code false}).
     *
     * @param  open Whatever the NetCDF file needs to be open or note.
     * @throws IllegalStateException If the NetCDF file is not in the expected state.
     */
    private void ensureState(final boolean open) throws IllegalStateException {
        if ((ncFile != null) != open) {
            throw new IllegalStateException(open ? "prepareWriteSequence(...) must be invoked first."
                    : "A sequence is already in progress."); // TODO: localize
        }
    }

    /**
     * Returns {@code true} since this writer can append many images in the same stream.
     *
     * @return Always {@code true} in this default implementation.
     */
    @Override
    public boolean canWriteSequence() {
        return true;
    }

    /**
     * Writes a complete NetCDF file containing a single image and associated metadata.
     * The default implementation delegates to the following methods, in that order:
     * <p>
     * <ul>
     *   <li>{@link #prepareWriteSequence(IIOMetadata)}</li>
     *   <li>{@link #writeToSequence(IIOImage, ImageWriteParam)}</li>
     *   <li>{@link #endWriteSequence()}</li>
     * </ul>
     * <p>
     * The output must have been set beforehand using the {@link #setOutput(Object)} method. The
     * given {@code IIOImage} may contain either a {@link RenderedImage} or a {@link Raster} source.
     *
     * @param  metadata The stream metadata to be used for writing global attributes, or {@code null}.
     * @param  image    The image to write. While not mandatory, metadata are recommended in order to
     *                  specify the geographic location.
     * @param  param    The parameter controlling the writing process, or {@code null}.
     * @throws IllegalStateException If the {@linkplain #output output} has not been set, or if a
     *         sequence is already in process of being written.
     * @throws IOException If an error occurs during writing.
     */
    @Override
    public void write(final IIOMetadata metadata, final IIOImage image, final ImageWriteParam param) throws IOException {
        prepareWriteSequence(metadata);
        writeToSequence(image, param);
        endWriteSequence();
    }

    /**
     * Prepares the NetCDF file to accept a series of subsequent {@link #writeToSequence(IIOImage,
     * ImageWriteParam)} calls. Any necessary header information is included using the information
     * provided in the metadata, if non-null.
     * <p>
     * The output must have been set beforehand using the {@link #setOutput(Object)} method.
     *
     * @param  metadata The stream metadata to be used for writing global attributes, or {@code null}.
     * @throws IllegalStateException If the {@linkplain #output output} has not been set, or if a
     *         sequence is already in process of being written.
     * @throws IOException If an error occurs during writing.
     */
    @Override
    public void prepareWriteSequence(final IIOMetadata metadata) throws IOException {
        ensureState(false);
        if (output instanceof NetcdfFileWriteable) {
            ncFile = (NetcdfFileWriteable) output;
        } else {
            ncFile = NetcdfFileWriteable.createNew(getOutputFile().getPath(), false);
        }
        writeMetadata(metadata);
    }

    /**
     * Writes the given metadata, if non-null. The NetCDF file must be in "define" mode.
     * This method may be invoked more than once, in which case the metadata will be merged
     * with precedence given to the first occurrences.
     */
    private void writeMetadata(final IIOMetadata metadata) throws IOException {
        if (metadata != null) try {
            if (XArrays.contains(metadata.getExtraMetadataFormatNames(), ISO_FORMAT_NAME)) {
                final Node root = metadata.getAsTree(ISO_FORMAT_NAME);
                if (root instanceof IIOMetadataNode) {
                    final Object userObject = ((IIOMetadataNode) root).getUserObject();
                    if (userObject instanceof Metadata) {
                        if (metadataWriter == null) {
                            metadataWriter = new NetcdfMetadataWriter(ncFile, this);
                        }
                        metadataWriter.write((Metadata) userObject);
                    }
                }
            }
        } catch (BackingStoreException e) {
            throw e.unwrapOrRethrow(IOException.class);
        }
    }

    /**
     * Appends a single image and possibly associated metadata to the NetCDF file.
     * The {@link #prepareWriteSequence(IIOMetadata)} method must have been called
     * beforehand, or an {@link IllegalStateException} is thrown.
     * <p>
     * The given {@code IIOImage} may contain either a {@link RenderedImage} or a {@link Raster}
     * source. An {@link ImageWriteParam} may optionally be supplied to control the writing process.
     *
     * @param  image The image to write. While not mandatory, metadata are recommended in order to
     *               specify the geographic location.
     * @param  param The parameter controlling the writing process, or {@code null}.
     * @throws IllegalStateException If the {@link #prepareWriteSequence(IIOMetadata)}
     *         has not been invoked.
     * @throws IOException If an error occurs during writing.
     */
    @Override
    public void writeToSequence(final IIOImage image, final ImageWriteParam param) throws IOException {
        ensureState(true);
        int i = dimensions.size();
        final NetcdfImage data = new NetcdfImage(this, image, param, dimensions);
        final int upper = dimensions.size();
        while (i < upper) {
            // If any new dimension were added as a side effect of the NetcdfImage construction,
            // add them to the NetCDF file now. The intend is to get an error sooner if something
            // goes wrong with the NetCDF dimension creation.
            dimensions.get(i++).create(ncFile, locale);
        }
        data.createVariables(ncFile, createRectIter(image, param));
        images.add(data);
    }

    /**
     * Completes the writing of a sequence of images begun with {@link #prepareWriteSequence(IIOMetadata)}.
     * In current implementation, most of the actual NetCDF writing process happen here, so this method may
     * be long to execute.
     *
     * @throws IOException If an error occurs during writing.
     */
    @Override
    public void endWriteSequence() throws IOException {
        ensureState(true);
        /*
         * Physically write the NetCDF dimensions, variables and attributes. After this method
         * call, we are no longer in "define" mode, so only the actual value of NetCDF variables
         * can be written.
         */
        ncFile.create();
        for (final NetcdfDimension dimension : dimensions) {
            dimension.write(ncFile);
        }
        for (final NetcdfImage image : images){
            image.write(ncFile);
        }
        close();
    }

    /**
     * Closes the underlying NetCDF file, unless it was provided by the user himself.
     *
     * @throws IOException If an error occurred while closing the file.
     */
    @Override
    protected void close() throws IOException {
        metadataWriter = null;
        try {
            if (ncFile != null && ncFile != output) {
                ncFile.close();
            }
        } finally {
            ncFile = null;
            images.clear();
            dimensions.clear();
            super.close();
        }
    }



    /**
     * The service provider for {@code NetcdfImageWriter}. This SPI provides
     * necessary implementation for creating default {@link NetcdfImageWriter}.
     * <p>
     * The default constructor initializes the fields to the values listed below.
     * Users wanting different values should create a subclass of {@code Spi} and
     * set the desired values in their constructor.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "NetCDF"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "application/netcdf"}, {@code "application/x-netcdf"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.NetcdfImageWriter"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;Value of {@link org.geotoolkit.util.Version#GEOTOOLKIT}&nbsp;</td></tr>
     *   <tr><td colspan="2" align="center">See super-class javadoc for remaining fields</td></tr>
     * </table>
     *
     * @author Johann Sorel (Geomatys)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 3.20
     * @module
     */
    public static final class Spi extends FileImageWriter.Spi {
        /**
         * Constructs a default {@code NetcdfImageWriter.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = NAMES;
            MIMETypes       = MIME_TYPES;
            suffixes        = SUFFIXES;
            pluginClassName = "org.geotoolkit.image.io.plugin.NetcdfImageWriter";
            readerSpiNames  = new String[] {"org.geotoolkit.image.io.plugin.NetcdfImageReader$Spi"};
            final int length = outputTypes.length;
            outputTypes = Arrays.copyOf(outputTypes, length+1);
            outputTypes[length] = NetcdfFileWriteable.class;
            nativeStreamMetadataFormatName = NATIVE_FORMAT_NAME;
            nativeImageMetadataFormatName  = NATIVE_FORMAT_NAME;
            extraStreamMetadataFormatNames = EXTRA_FORMAT_NAMES;
            extraImageMetadataFormatNames  = EXTRA_FORMAT_NAMES;
        }

        /**
         * Returns a description for this provider.
         *
         * @todo Localize
         */
        @Override
        public String getDescription(Locale locale) {
            return "NetCDF image encoder";
        }

        /**
         * Returns an instance of the {@code NetcdfImageWriter} implementation associated
         * with this service provider.
         *
         * @param  extension An optional extension object, which may be null.
         * @return An image writer instance.
         * @throws IOException if the attempt to instantiate the writer fails.
         */
        @Override
        public ImageWriter createWriterInstance(final Object extension) throws IOException {
            return new NetcdfImageWriter(this);
        }
    }
}
