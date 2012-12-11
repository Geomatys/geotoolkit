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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.operation.TransformException;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.util.GenericName;

import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.collection.BackingStoreException;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultResolution;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.util.collection.XCollections.addIfNonNull;
import static org.geotoolkit.util.collection.XCollections.isNullOrEmpty;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.ISO_FORMAT_NAME;


/**
 * Base class of {@link GridCoverage} readers. Reading is a two steps process:
 * <p>
 * <ul>
 *   <li>The input must be set first using the {@link #setInput(Object)} method.</li>
 *   <li>The actual reading is performed by a call to the
 *       {@link #read(int, GridCoverageReadParam)} method.</li>
 * </ul>
 * <p>
 * Example:
 *
 * {@preformat java
 *     GridCoverageReader reader = ...
 *     reader.setInput(new File("MyCoverage.asc"));
 *     GridCoverage coverage = reader.read(0, null);
 * }
 *
 * {@note This class is conceptually equivalent to the <code>ImageReader</code> class provided in
 * the standard Java library. Implementations of this class are often wrappers around a Java
 * <code>ImageReader</code>, converting geodetic coordinates to pixel coordinates before to
 * delegate the reading of pixel values.}
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @see ImageReader
 *
 * @since 3.09 (derived from 2.4)
 * @module
 */
public abstract class GridCoverageReader extends GridCoverageStore {
    /**
     * The input (typically a {@link java.io.File}, {@link java.net.URL} or {@link String}),
     * or {@code null} if input is not set.
     */
    Object input;

    /**
     * Creates a new instance.
     */
    protected GridCoverageReader() {
        ignoreGridTransforms = true;
    }

    /**
     * Sets the input source to the given object. The input is typically a
     * {@link java.io.File} or a {@link String} object. But some other types
     * (e.g. {@link javax.imageio.stream.ImageInputStream}) may be accepted
     * as well depending on the implementation.
     *
     * {@section How streams are closed}
     * <ul>
     *   <li>If the given input is an {@linkplain java.io.InputStream input stream},
     *      {@linkplain javax.imageio.stream.ImageInputStream image input stream} or
     *      a {@linkplain java.io.Reader reader}, then it is caller responsibility to
     *      close the given stream after usage.</li>
     *  <li>If an input stream has been generated automatically by this {@code GridCoverageReader}
     *      from the given input object, then this coverage reader will close the stream when the
     *      {@link #reset()} or {@link #dispose()} method is invoked, or when a new input is set.</li>
     * </ul>
     *
     * @param  input The input (typically {@link java.io.File} or {@link String}) to be read.
     * @throws IllegalArgumentException If the input is not a valid instance for this reader.
     * @throws CoverageStoreException If the operation failed.
     *
     * @see ImageReader#setInput(Object)
     */
    public void setInput(final Object input) throws CoverageStoreException {
        this.input = input;
        abortRequested = false;
    }

    /**
     * Returns the input which was set by the last call to {@link #setInput(Object)},
     * or {@code null} if none.
     *
     * @return The current input, or {@code null} if none.
     * @throws CoverageStoreException If the operation failed.
     *
     * @see ImageReader#getInput()
     */
    public Object getInput() throws CoverageStoreException {
        return input;
    }

    /**
     * Returns the name of the {@linkplain #input}, or "<cite>Untitled</cite>" if
     * the input is not a recognized type. This is used for formatting messages only.
     */
    final String getInputName() {
        final Object input = this.input;
        if (IOUtilities.canProcessAsPath(input)) {
            return IOUtilities.name(input);
        } else {
            return Vocabulary.getResources(locale).getString(Vocabulary.Keys.UNTITLED);
        }
    }

    /**
     * Returns the list of coverage names available from the current input source. The length
     * of the returned list is the number of coverages found in the current input source. The
     * elements in the returned list are the names of each coverage.
     * <p>
     * The returned list may be backed by this {@code GridCoverageReader}: it should be used
     * only as long as this reader and its input source are valid. Iterating over the list
     * may be costly and the operation performed on the list may throw a
     * {@link BackingStoreException}.
     *
     * @return The names of the coverages.
     * @throws IllegalStateException If the input source has not been set.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#getNumImages(boolean)
     */
    public abstract List<? extends GenericName> getCoverageNames()
            throws CoverageStoreException, CancellationException;

    /**
     * Returns the grid geometry for the {@link GridCoverage} to be read at the given index.
     *
     * @param  index The index of the coverage to be queried.
     * @return The grid geometry for the {@link GridCoverage} at the specified index.
     * @throws IllegalStateException If the input source has not been set.
     * @throws IndexOutOfBoundsException If the supplied index is out of bounds.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#getWidth(int)
     * @see ImageReader#getHeight(int)
     */
    public abstract GeneralGridGeometry getGridGeometry(int index)
            throws CoverageStoreException, CancellationException;

    /**
     * Returns the sample dimensions for each band of the {@link GridCoverage} to be read.
     * If sample dimensions are not known, then this method returns {@code null}.
     *
     * @param  index The index of the coverage to be queried.
     * @return The list of sample dimensions for the {@link GridCoverage} at the specified index,
     *         or {@code null} if none. This list length is equals to the number of bands in the
     *         {@link GridCoverage}.
     * @throws IllegalStateException If the input source has not been set.
     * @throws IndexOutOfBoundsException If the supplied index is out of bounds.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     */
    public abstract List<GridSampleDimension> getSampleDimensions(int index)
            throws CoverageStoreException, CancellationException;

    /**
     * Returns the ranges of valid sample values for each band in this format.
     * The ranges are always expressed in <cite>geophysics</cite> units.
     * <p>
     * The default implementation computes the ranges from the information returned
     * by {@link #getSampleDimensions(int)}, if any.
     *
     * @param  index The index of the coverage to be queried.
     * @return The ranges of values for each band, or {@code null} if none.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @since 3.10
     *
     * @deprecated Not used in practice. Could be a convenience static method working on any
     * sample dimensions.
     */
    @Deprecated
    public List<MeasurementRange<?>> getSampleValueRanges(final int index)
            throws CoverageStoreException, CancellationException
    {
        final List<GridSampleDimension> sampleDimensions = getSampleDimensions(index);
        if (sampleDimensions == null) {
            return null;
        }
        @SuppressWarnings({"unchecked","rawtypes"})  // Generic array creation.
        final MeasurementRange<?>[] ranges = new MeasurementRange[sampleDimensions.size()];
        for (int i=0; i<ranges.length; i++) {
            GridSampleDimension sd = sampleDimensions.get(i);
            if (sd != null) {
                sd = sd.geophysics(true);
                ranges[i] = MeasurementRange.createBestFit(
                        sd.getMinimumValue(), true, sd.getMaximumValue(), true, sd.getUnits());
            }
        }
        return Arrays.asList(ranges);
    }

    /**
     * If the given metadata is non-null, supports the ISO-19115 format and contains a
     * {@link Metadata} user object in the root node, returns that object. Otherwise
     * creates a new, initially empty, metadata object.
     */
    private static DefaultMetadata createMetadata(final IIOMetadata streamMetadata) throws CoverageStoreException {
        if (streamMetadata != null) try {
            if (XArrays.contains(streamMetadata.getExtraMetadataFormatNames(), ISO_FORMAT_NAME)) {
                final Node root = streamMetadata.getAsTree(ISO_FORMAT_NAME);
                if (root instanceof IIOMetadataNode) {
                    final Object userObject = ((IIOMetadataNode) root).getUserObject();
                    if (userObject instanceof Metadata) {
                        // Unconditionally copy the metadata, even if the original object was
                        // already an instance of DefaultMetadata, because the original object
                        // may be cached in the ImageReader - so we don't want to modify it.
                        return new DefaultMetadata((Metadata) userObject);
                    }
                }
            }
        } catch (BackingStoreException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw new CoverageStoreException(cause);
            }
            throw e.unwrapOrRethrow(CoverageStoreException.class);
        }
        return new DefaultMetadata();
    }

    /**
     * Returns the ISO 19115 metadata object associated with the input source as a whole
     * and each coverages. The default implementation constructs the metadata from the
     * {@linkplain #getStreamMetadata() stream metadata} and the
     * {@linkplain #getCoverageMetadata(int) coverage metadata},
     * eventually completed by the {@link #getGridGeometry(int)}.
     * <p>
     * Since the relationship between Image I/O metadata and ISO 19115 is not always a
     * "<cite>one-to-one</cite>" relationship, this method works on a best effort basis.
     *
     * @return The ISO 19115 metadata (never {@code null}).
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     *
     * @see <a href="../../image/io/metadata/SpatialMetadataFormat.html#default-formats">Metadata formats</a>
     *
     * @since 3.18
     */
    public Metadata getMetadata() throws CoverageStoreException {
        final SpatialMetadata streamMetadata = getStreamMetadata();
        final DefaultMetadata metadata = createMetadata(streamMetadata);
        /*
         * Extract all information available from the stream metadata, provided that metadata
         * elements were not already provided by the above call to createMetadata(...). Since
         * createMetadata(...) typically get its information from the stream metadata as well,
         * we assume that creating here new objects from stream metadata would be redundant.
         */
        DataIdentification identification = null;
        if (streamMetadata != null) {
            final Collection<DataQuality> quality = metadata.getDataQualityInfo();
            if (quality.isEmpty()) {
                addIfNonNull(quality, streamMetadata.getInstanceForType(DataQuality.class));
            }
            final Collection<AcquisitionInformation> acquisition = metadata.getAcquisitionInformation();
            if (acquisition.isEmpty()) {
                addIfNonNull(acquisition, streamMetadata.getInstanceForType(AcquisitionInformation.class));
            }
            /*
             * Get the existing identification info if any, or create a new one otherwise.
             * If an identification info is found, remove it from the metadata (it will be
             * added back at the end of this method, or a copy of it will be added).
             */
            final Iterator<Identification> it = metadata.getIdentificationInfo().iterator();
            while (it.hasNext()) {
                final Identification candidate = it.next();
                if (candidate instanceof DataIdentification) {
                    identification = (DataIdentification) candidate;
                    it.remove();
                    break;
                }
            }
            if (identification == null) {
                identification = streamMetadata.getInstanceForType(DataIdentification.class);
            }
        }
        /*
         * Check if we should complete the extents and resolutions. We will do so only
         * if the vertical/temporal extent, geographic bounding box and resolution are
         * not already provided in the metadata.  If the geographic extent is declared
         * by an other kind of object than GeographicBoundingBox, we will still add the
         * bounding box because the existing extent could be only a textual description.
         */
        boolean failed              = false;  // For logging warning only once.
        boolean computeExtents      = true;   // 'false' if extents are already present.
        boolean computeResolutions  = true;   // 'false' is resolutions are already present.
        DefaultExtent   extent      = null;   // The extent to compute, if needed.
        List<Extent>    extents     = null;   // The extents already provided in the metadata.
        Set<Resolution> resolutions = null;   // The resolutions to compute, if needed.
        if (identification != null) {
            computeResolutions = isNullOrEmpty(identification.getSpatialResolutions());
            final Collection<? extends Extent> existings = identification.getExtents();
            if (!isNullOrEmpty(existings)) {
                extents = new ArrayList<>(existings);
                extent = UniqueExtents.getIncomplete(extents);
                if (extent == null) {
                    // The plugin-provided Metadata instance seems to contain Extents
                    // that are complete enough, so we will not try to complete them.
                    computeExtents = false;
                    extents = null;
                }
            }
        }
        /*
         * Check if we should complete the content info and the spatial representation info.
         * If the plugin-provided metadata declare explicitly such information, we will not
         * compute them in this method (the plugin information will have precedence).
         */
        final Collection<ContentInformation>    contentInfo = metadata.getContentInfo();
        final Collection<SpatialRepresentation> spatialInfo = metadata.getSpatialRepresentationInfo();
        final boolean computeContent = (contentInfo != null) && contentInfo.isEmpty();
        final boolean computeSpatial = (spatialInfo != null) && spatialInfo.isEmpty();
        if (computeContent || computeSpatial || computeResolutions || computeExtents) {
            final List<? extends GenericName> coverageNames = getCoverageNames();
            final int numCoverages = coverageNames.size();
            for (int i=0; i<numCoverages; i++) {
                if (computeContent || computeSpatial) {
                    final SpatialMetadata coverageMetadata = getCoverageMetadata(i);
                    if (coverageMetadata != null) {
                        if (computeContent) {
                            final ImageDescription description = coverageMetadata.getInstanceForType(ImageDescription.class);
                            if (description != null) {
                                contentInfo.add(description);
                            }
                        }
                        if (computeSpatial) {
                            final Georectified rectified = coverageMetadata.getInstanceForType(Georectified.class);
                            if (rectified != null) {
                                metadata.getSpatialRepresentationInfo().add(rectified);
                            }
                        }
                    }
                }
                if (computeResolutions || computeExtents) {
                    /*
                    * Resolution along the horizontal axes only, ignoring all other axes.
                    */
                    final GeneralGridGeometry gg = getGridGeometry(i);
                    if (computeResolutions) {
                        final Measure m = CRSUtilities.getHorizontalResolution(
                                gg.getCoordinateReferenceSystem(), gg.getResolution());
                        if (m != null) {
                            final DefaultResolution resolution = new DefaultResolution();
                            resolution.setDistance(m.doubleValue()); // TODO: take unit in account.
                            if (resolutions == null) {
                                resolutions = new LinkedHashSet<>();
                            }
                            resolutions.add(resolution);
                        }
                    }
                    /*
                    * Horizontal, vertical and temporal extents. The horizontal extents is
                    * represented as a geographic bounding box, which may require a reprojection.
                    */
                    if (computeExtents && gg.isDefined(GeneralGridGeometry.ENVELOPE)) {
                        if (extent == null) {
                            extent = new UniqueExtents();
                        }
                        try {
                            extent.addElements(gg.getEnvelope());
                        } catch (TransformException e) {
                            // Not a big deal if we fail. We will just let the identification section unchanged.
                            if (!failed) {
                                failed = true; // Log only once.
                                Logging.recoverableException(LOGGER, GridCoverageReader.class, "getMetadata", e);
                            }
                        }
                    }
                }
            }
        }
        /*
         * At this point, we have computed extents and resolutions from every images
         * in the stream. Now store the result. Note that we unconditionally create
         * a copy of the identification info, even if the original object was already
         * an instance of DefaultDataIdentification, because the original object may
         * be cached in the ImageReader.
         */
        if (extent != null || resolutions != null) {
            final DefaultDataIdentification copy = new DefaultDataIdentification(identification);
            if (extent != null) {
                if (extents != null) {
                    copy.setExtents(extents);
                } else {
                    copy.getExtents().add(extent);
                }
            }
            if (resolutions != null) {
                copy.setSpatialResolutions(resolutions);
            }
            identification = copy;
        }
        if (identification != null) {
            metadata.getIdentificationInfo().add(identification);
        }
        return metadata;
    }

    /**
     * Returns the metadata associated with the input source as a whole, or {@code null} if none.
     * The default implementation returns {@code null} in every cases.
     *
     * @return The metadata associated with the input source as a whole, or {@code null}.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     *
     * @see ImageReader#getStreamMetadata()
     *
     * @since 3.14
     */
    public SpatialMetadata getStreamMetadata() throws CoverageStoreException {
        return null;
    }

    /**
     * Returns the metadata associated with the given coverage, or {@code null} if none.
     * The default implementation returns {@code null} in every cases.
     *
     * @param  index The index of the coverage to be queried.
     * @return The metadata associated with the given coverage, or {@code null}.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     *
     * @see ImageReader#getImageMetadata(int)
     *
     * @since 3.14
     */
    public SpatialMetadata getCoverageMetadata(final int index) throws CoverageStoreException {
        return null;
    }

    /**
     * Returns an optional map of properties associated with the coverage at the given index, or
     * {@code null} if none. The properties are implementation-specific; they are available to
     * subclasses for any use. The {@code GridCoverageReader} class will simply gives those
     * properties to the {@link javax.media.jai.PropertySource} object to be created by the
     * {@link #read read} method, without any processing.
     * <p>
     * The default implementation returns {@code null} in every cases.
     *
     * @param  index The index of the coverage to be queried.
     * @return The properties, or {@code null} if none.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     */
    public Map<?,?> getProperties(int index) throws CoverageStoreException, CancellationException {
        return null;
    }

    /**
     * Reads the grid coverage.
     *
     * @param  index The index of the coverage to be queried.
     * @param  param Optional parameters used to control the reading process, or {@code null}.
     * @return The {@link GridCoverage} at the specified index.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#read(int)
     */
    public abstract GridCoverage read(int index, GridCoverageReadParam param)
            throws CoverageStoreException, CancellationException;

    /**
     * Restores the {@code GridCoverageReader} to its initial state.
     *
     * @throws CoverageStoreException If an error occurs while restoring to the initial state.
     *
     * @see ImageReader#reset()
     */
    @Override
    public void reset() throws CoverageStoreException {
        input = null;
        super.reset();
    }

    /**
     * Allows any resources held by this reader to be released. The result of calling
     * any other method subsequent to a call to this method is undefined.
     *
     * @throws CoverageStoreException If an error occurs while disposing resources.
     *
     * @see ImageReader#dispose()
     */
    @Override
    public void dispose() throws CoverageStoreException {
        input = null;
        super.dispose();
    }
}
