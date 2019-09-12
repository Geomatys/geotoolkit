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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.Unit;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.internal.storage.MetadataBuilder;
import org.apache.sis.measure.Units;
import org.apache.sis.metadata.ModifiableMetadata;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.content.DefaultCoverageDescription;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.identification.DefaultResolution;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.collection.BackingStoreException;
import static org.apache.sis.util.collection.Containers.isNullOrEmpty;
import org.apache.sis.util.iso.Names;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.ISO_FORMAT_NAME;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import static org.geotoolkit.util.collection.XCollections.addIfNonNull;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;
import org.w3c.dom.Node;


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
 *
 * @see ImageReader
 */
public abstract class AbstractGridCoverageReader implements GridCoverageReader {

    /**
     * The input (typically a {@link java.io.File}, {@link java.net.URL} or {@link String}),
     * or {@code null} if input is not set.
     */
    Object input;

    /**
     * The logger to use for logging messages during read and write operations.
     *
     * @since 3.15
     */
    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.coverage.io");

    /**
     * The locale to use for formatting messages, or {@code null} for a default locale.
     */
    Locale locale;

    /**
     * {@code true} if a request to abort the current read or write operation has been made.
     * Subclasses should set this field to {@code false} at the beginning of each read or write
     * operation, and pool the value regularly during the operation.
     *
     * @see #abort()
     */
    protected volatile boolean abortRequested;

    /**
     * Creates a new instance.
     */
    protected AbstractGridCoverageReader() {
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
    @Override
    public void setInput(final Object input) throws DataStoreException {
        this.input = input;
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
    @Override
    public Object getInput() throws DataStoreException {
        return input;
    }

    /**
     * Returns the name of the {@linkplain #input}, or "<cite>Untitled</cite>" if
     * the input is not a recognized type. This is used for formatting messages only.
     */
    final String getInputName() {
        final Object input = this.input;
        if (IOUtilities.canProcessAsPath(input)) {
            return IOUtilities.filename(input);
        } else {
            return Vocabulary.getResources(locale).getString(Vocabulary.Keys.Untitled);
        }
    }

    /**
     * If the given metadata is non-null, supports the ISO-19115 format and contains a
     * {@link Metadata} user object in the root node, returns that object. Otherwise
     * creates a new, initially empty, metadata object.
     */
    private static DefaultMetadata createMetadata(final IIOMetadata streamMetadata) throws DataStoreException {
        if (streamMetadata != null) try {
            if (ArraysExt.contains(streamMetadata.getExtraMetadataFormatNames(), ISO_FORMAT_NAME)) {
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
    public Metadata getMetadata() throws DataStoreException {
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
            final GenericName coverageName = getCoverageName();
            if (computeContent || computeSpatial) {

                CoverageDescription ci = null;
                final SpatialMetadata coverageMetadata = getCoverageMetadata();
                if (coverageMetadata != null) {
                    if (computeContent) {
                        ci = coverageMetadata.getInstanceForType(ImageDescription.class);
                        if (ci != null) {
                            contentInfo.add(ci);
                        }
                    }
                    if (computeSpatial) {
                        final Georectified rectified = coverageMetadata.getInstanceForType(Georectified.class);
                        if (rectified != null) {
                            metadata.getSpatialRepresentationInfo().add(rectified);
                        }
                    }
                }
                /*
                 * Get or create the content info to store sample dimensions
                 */
                if (ci==null) {
                    //get or create it
                    if (contentInfo.size()>0) {
                        CoverageDescription cd = contentInfo.stream().limit(1)
                                .filter(CoverageDescription.class::isInstance)
                                .map(CoverageDescription.class::cast)
                                .findFirst().orElse(null);
                        if (cd instanceof ModifiableMetadata && ((ModifiableMetadata)cd).state() != ModifiableMetadata.State.FINAL) {
                            ci = cd;
                        }
                    } else {
                        ci = new DefaultCoverageDescription();
                        contentInfo.add(ci);
                    }
                }

                if (ci!=null && ci.getAttributeGroups()!=null && ci.getAttributeGroups().isEmpty() && ci.getDimensions().isEmpty()) {
                    final List<SampleDimension> sampleDimensions = getSampleDimensions();
                    if (sampleDimensions!=null) {
                        final MetadataBuilder mb = new MetadataBuilder();
                        for (int idx=0,n=sampleDimensions.size();idx<n;idx++) {
                            SampleDimension gsd = sampleDimensions.get(idx).forConvertedValues(true);
                            final Unit<? extends Quantity<?>> units = gsd.getUnits().orElse(null);
                            mb.newSampleDimension();
                            mb.setBandIdentifier(Names.createMemberName(null, null, ""+idx, Integer.class));
                            mb.addBandDescription(gsd.getName().toString());
                            if(units!=null) mb.setSampleUnits(units);
                            mb.addMinimumSampleValue(SampleDimensionUtils.getMinimumValue(gsd));
                            mb.addMaximumSampleValue(SampleDimensionUtils.getMaximumValue(gsd));
                            gsd = gsd.forConvertedValues(false);
                            gsd.getTransferFunctionFormula().ifPresent((f) -> {
                                mb.setTransferFunction(f.getScale(), f.getOffset());
                            });
                        }
                        final DefaultMetadata meta = mb.build(false);
                        final CoverageDescription imgDesc = (CoverageDescription) meta.getContentInfo().iterator().next();
                        ci.getAttributeGroups().addAll((Collection)imgDesc.getAttributeGroups());
                    }
                }

            }
            if (computeResolutions || computeExtents) {
                /*
                 * Resolution along the horizontal axes only, ignoring all other axes. For linear units (feet,
                 * kilometres, etc.), we convert the units to metres for compliance with a current limitation
                 * of Apache SIS, which can handle only metres. For angular resolution (typically in degrees),
                 * we perform an APPROXIMATE conversion to metres using the nautical mile definition. This
                 * conversion is only valid along the latitudes axis (the number is wrong along the longitude
                 * axis), and more accurate for mid-latitude (the numbers are differents close to equator or
                 * to the poles).
                 */
                final GridGeometry gg = getGridGeometry();
                if (computeResolutions && gg.isDefined(GridGeometry.CRS)) {

                    double[] res = null;
                    try {
                        res = gg.getResolution(false);
                    } catch (IncompleteGridGeometryException ex) {
                    }

                    final Quantity<?> m = CRSUtilities.getHorizontalResolution(
                            gg.getCoordinateReferenceSystem(), res);
                    if (m != null) {
                        double  measureValue = m.getValue().doubleValue();
                        final Unit<?>   unit = m.getUnit();
                        Unit<?> standardUnit = null;
                        double  scaleFactor = 1;
                        if (Units.isAngular(unit)) {
                            standardUnit = Units.DEGREE;
                            scaleFactor  = (1852*60); // From definition of nautical miles.
                        } else if (Units.isLinear(unit)) {
                            standardUnit = Units.METRE;
                        }
                        if (standardUnit != null) try {
                            measureValue = unit.getConverterToAny(standardUnit).convert(measureValue) * scaleFactor;
                            final DefaultResolution resolution = new DefaultResolution();
                            resolution.setDistance(measureValue);
                            if (resolutions == null) {
                                resolutions = new LinkedHashSet<>();
                            }
                            resolutions.add(resolution);
                        } catch (IncommensurableException e) {
                            // In case of failure, do not create a Resolution object.
                            Logging.recoverableException(LOGGER, AbstractGridCoverageReader.class, "getMetadata", e);
                        }
                    }
                }
                /*
                * Horizontal, vertical and temporal extents. The horizontal extents is
                * represented as a geographic bounding box, which may require a reprojection.
                */
                if (computeExtents && gg.isDefined(GridGeometry.ENVELOPE)) {
                    if (extent == null) {
                        extent = new UniqueExtents();
                    }
                    try {
                        extent.addElements(gg.getEnvelope());
                    } catch (TransformException e) {
                        // Not a big deal if we fail. We will just let the identification section unchanged.
                        if (!failed) {
                            failed = true; // Log only once.
                            Logging.recoverableException(LOGGER, AbstractGridCoverageReader.class, "getMetadata", e);
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
    public SpatialMetadata getStreamMetadata() throws DataStoreException {
        return null;
    }

    /**
     * Returns the metadata associated with the given coverage, or {@code null} if none.
     * The default implementation returns {@code null} in every cases.
     *
     * @return The metadata associated with the given coverage, or {@code null}.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     *
     * @see ImageReader#getImageMetadata(int)
     *
     * @since 3.14
     */
    public SpatialMetadata getCoverageMetadata() throws DataStoreException {
        return null;
    }

    /**
     * Reads the grid coverage.
     *
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
    @Override
    public abstract GridCoverage read(GridCoverageReadParam param)
            throws DataStoreException, CancellationException;

    /**
     * Cancels the read or write operation which is currently under progress in an other thread.
     * Invoking this method will cause a {@link CancellationException} to be thrown in the reading
     * or writing thread (not this thread), unless the operation had the time to complete.
     *
     * {@section Note for implementors}
     * Subclasses should set the {@link #abortRequested} field to {@code false} at the beginning
     * of each read or write operation, and poll the value regularly during the operation.
     *
     * @see #abortRequested
     * @see javax.imageio.ImageReader#abort()
     * @see javax.imageio.ImageWriter#abort()
     */
    @Override
    public void abort() {
        abortRequested = true;
    }

    /**
     * Throws {@link CancellationException} if a request to abort the current read or write
     * operation has been made since this object was instantiated or {@link #abortRequested}
     * has been cleared.
     *
     * @throws CancellationException If the {@link #abort()} method has been invoked.
     */
    final void checkAbortState() throws CancellationException {
        if (abortRequested) {
            throw new CancellationException(formatErrorMessage(Errors.Keys.CanceledOperation));
        }
    }

    /**
     * Returns a localized string for the specified error key.
     *
     * @param key One of the constants declared in the {@link Errors.Keys} inner class.
     */
    final String formatErrorMessage(final short key) {
        return Errors.getResources(locale).getString(key);
    }

    /**
     * Returns an error message for the given exception, using the current input or output.
     * This method is overridden by {@link GridCoverageReader} and {@link GridCoverageWriter},
     * which will format a better message including the input or output path.
     */
    String formatErrorMessage(final Throwable e) {
        return e.getLocalizedMessage();
    }

    /**
     * Returns an error message for the given exception. If the input or output is known, then
     * this method returns "<cite>Can't read/write 'the name'</cite>" followed by the cause
     * message. Otherwise it returns the localized message of the given exception.
     *
     * @param path The input or output.
     * @param e The exception which occurred.
     * @param isWriting {@code false} If reading, or {@code true} if writing.
     */
    final String formatErrorMessage(final Object path, final Throwable e, final boolean isWriting) {
        String message = e.getLocalizedMessage();
        if (IOUtilities.canProcessAsPath(path)) {
            final String cause = message;
            message = Errors.getResources(locale).getString(isWriting ?
                    Errors.Keys.CantWriteFile_1 : Errors.Keys.CantReadFile_1,
                    IOUtilities.filename(path));
            if (cause != null && cause.indexOf(' ') > 0) { // Append only if we have a sentence.
                message = message + '\n' + cause;
            }
        }
        return message;
    }

    /**
     * Restores the {@code GridCoverageReader} to its initial state.
     *
     * @throws CoverageStoreException If an error occurs while restoring to the initial state.
     *
     * @see ImageReader#reset()
     */
    @Override
    public void reset() throws DataStoreException {
        input = null;
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
    public void dispose() throws DataStoreException {
        input = null;
    }
}
