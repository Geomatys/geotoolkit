/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.SampleDimensionUtils;
import static org.geotoolkit.image.io.MultidimensionalImageStore.*;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.resources.Errors;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;


/**
 * An {@link ImageReader} implementation which use a {@link GridCoverageReader} for reading
 * sample values. This class is the converse of {@link ImageCoverageReader}: it takes a high
 * level construct ({@code GridCoverageReader}) and wraps it as a lower level construct
 * ({@code ImageReader}). This is an unusual thing to do - consequently, the only purpose
 * of this class is to allow usage of an existing {@code GridCoverageReader} instance with
 * API working only with {@code ImageReader} instances.
 *
 * {@section Example}
 * {@code ImageReaderAdapter} can be used in order to show the content of a {@code GridCoverageReader}
 * in an {@link org.geotoolkit.gui.swing.image.ImageFileProperties} widget.
 *
 * {@note An other approach would be to unwrap the <code>ImageReader</code> which is wrapped
 * by <code>ImageCoverageReader</code>. However the result would not be always the same,
 * because some modules (for example <cite>geotk-coverage-sql</cite>) define subclasses
 * of <code>ImageCoverageReader</code> which alter the way the image is read. The purpose
 * of <code>ImageReaderAdapter</code> is to get exactly the same image than the one produced
 * by the wrapped <code>GridCoverageReader</code>.}
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public class ImageReaderAdapter extends SpatialImageReader {
    /**
     * The sample dimension to make visible. Declared as a constant in order to spot
     * the places where the value {@value} is assumed.
     *
     * @see org.geotoolkit.coverage.AbstractCoverage#VISIBLE_BAND
     */
    private static final int VISIBLE_BAND = 0;

    /**
     * The coverage reader on which to delegate all {@code ImageReader} method invocations.
     */
    protected final GridCoverageReader reader;

    /**
     * The number of images, or 0 if not yet computed.
     */
    private int numImages;

    /**
     * The image sizes at various image indices. Values are computed from the grid geometries
     * when first needed, and cached because each value is typically fetched twice (once for
     * image width, and once for image height).
     */
    private final Map<Integer,Dimension> imageSizes = new HashMap<>();

    /**
     * The image types at various image indices. Values are computed from the sample
     * dimensions when first needed.
     */
    private final Map<Integer,ImageTypeSpecifier> imageTypes = new HashMap<>();

    /**
     * Creates a new adapter for the given coverage reader.
     *
     * @param reader The coverage reader on which to delegate all
     *        {@code ImageReader} method invocations.
     */
    public ImageReaderAdapter(final GridCoverageReader reader) {
        super(null);
        if (reader == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NullArgument_1, "reader"));
        }
        this.reader = reader;
    }

    /**
     * Returns the {@link ImageReader} provider, or {@code null} if none.
     */
    @Override
    public ImageReaderSpi getOriginatingProvider() {
        if (reader instanceof ImageCoverageReader) {
            final ImageReader ir = ((ImageCoverageReader) reader).imageReader;
            if (ir != null) {
                return ir.getOriginatingProvider();
            }
        }
        return super.getOriginatingProvider();
    }

    /**
     * Converts the given {@link CoverageStoreException} to an {@link IOException}. This method
     * unwraps the {@code IOException} if the {@code CoverageStoreException} was just a wrapper
     * for the former. Otherwise the {@code CoverageStoreException} is wrapped in a new
     * {@link IIOException}.
     */
    private static IOException convert(final DataStoreException exception) {
        final Throwable cause = exception.getCause();
        if (cause instanceof IOException) {
            return (IOException) cause;
        }
        return new IIOException(exception.getLocalizedMessage(), exception);
    }

    /**
     * Sets the input to be read. In the input can be successfully assigned to the wrapped
     * {@link GridCoverageReader}, then it is also saved in the inherited {@link #input} field
     * for retrieval by {@link #getInput()}.
     */
    @Override
    public void setInput(final Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
        this.input = null;
        numImages = 0;
        imageSizes.clear();
        imageTypes.clear();
        super.dispose(); // Must be the super-class method, not this.dispose().
        try {
            reader.setInput(input);
        } catch (DataStoreException e) {
            throw new IllegalArgumentException(e);
        }
        this.input           = input;           // Do not invoke super.setInput(...).
        this.seekForwardOnly = seekForwardOnly; // Saved as a matter of principle, but not used.
        this.ignoreMetadata  = ignoreMetadata;
    }

    /**
     * Returns the number of images in the current input. The default implementation returns
     * the length of the list returned by {@link GridCoverageReader#getCoverageNames()}.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IOException {
        if (numImages == 0) numImages = 1;
        return numImages;
    }

    /**
     * Returns the number of bands available for the specified image. The default implementation
     * returns the length of the list returned by {@link GridCoverageReader#getSampleDimensions(int)}.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        final List<SampleDimension> sampleDimensions;
        try {
            sampleDimensions = reader.getSampleDimensions();
        } catch (DataStoreException e) {
            throw convert(e);
        }
        return (sampleDimensions != null) ? sampleDimensions.size() : 1;
    }

    /**
     * Returns the number of dimension of the image at the given index. The default
     * implementation returns the dimension of the geometry returned by
     * {@link GridCoverageReader#getGridGeometry(int)}.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getDimension(final int imageIndex) throws IOException {
        try {
            return reader.getGridGeometry().getDimension();
        } catch (DataStoreException e) {
            throw convert(e);
        }
    }

    /**
     * Returns the grid envelope of the image at the given index. The default
     * implementation returns the grid range of the geometry returned by
     * {@link GridCoverageReader#getGridGeometry(int)}.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     *
     * @since 3.19
     */
    @Override
    public GridExtent getGridEnvelope(final int imageIndex) throws IOException {
        try {
            return reader.getGridGeometry().getExtent();
        } catch (DataStoreException e) {
            throw convert(e);
        }
    }

    /**
     * Returns the image width and height at the given index. The default implementation computes
     * the size from the geometry returned by {@link GridCoverageReader#getGridGeometry(int)}.
     * Subclasses can override this method if they want to compute the size in a different way.
     *
     * @param  imageIndex The image index.
     * @return The width and height of the image at the given index.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    protected Dimension getSize(final int imageIndex) throws IOException {
        final Integer key = imageIndex;
        Dimension size = imageSizes.get(key);
        if (size == null) {
            final GridGeometry geometry;
            try {
                geometry = reader.getGridGeometry();
            } catch (DataStoreException e) {
                throw convert(e);
            }
            final GridExtent range = geometry.getExtent();
            size = new Dimension((int) range.getSize(X_DIMENSION), (int) range.getSize(Y_DIMENSION));
            imageSizes.put(key, size);
        }
        return size;
    }

    /**
     * Returns the width of the image at the given index. This method delegates
     * to {@link #getSize(int)}, which computes the size from the grid geometry.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public final int getWidth(final int imageIndex) throws IOException {
        return getSize(imageIndex).width;
    }

    /**
     * Returns the height of the image at the given index. This method delegates
     * to {@link #getSize(int)}, which computes the size from the grid geometry.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public final int getHeight(final int imageIndex) throws IOException {
        return getSize(imageIndex).height;
    }

    /**
     * Returns the data type which most closely represents the "raw" internal data of the image.
     * The default implementation returns the data type of the sample model of the type returned
     * by {@link #getRawImageType(int)}.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    protected int getRawDataType(final int imageIndex) throws IOException {
        final ImageTypeSpecifier type = getRawImageType(imageIndex);
        return (type != null) ? type.getSampleModel().getDataType() : super.getRawDataType(imageIndex);
    }

    /**
     * Returns the raw type of the image at the given index. The default implementation computes
     * the type from the value returned by {@link GridCoverageReader#getSampleDimensions(int)}.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {
        final Integer key = imageIndex;
        ImageTypeSpecifier type = imageTypes.get(key);
        if (type == null) {
            final List<SampleDimension> bands;
            try {
                bands = reader.getSampleDimensions();
            } catch (DataStoreException e) {
                throw convert(e);
            }
            if (bands != null) {
                final int numBands = bands.size();
                if (numBands > VISIBLE_BAND) {
                    final Dimension size = getSize(imageIndex);
                    final ColorModel cm = SampleDimensionUtils.getColorModel(bands.get(VISIBLE_BAND), VISIBLE_BAND, bands.size());
                    final SampleModel sm = cm.createCompatibleSampleModel(size.width, size.height);
                    type = new ImageTypeSpecifier(cm, sm);
                }
            }
            imageTypes.put(key, type);
        }
        return type;
    }

    /**
     * Returns the possible image types to which the given image can be decoded. The default
     * implementation puts the value returned by {@link #getRawDataType(int)} in a singleton set.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        final ImageTypeSpecifier type = getRawImageType(imageIndex);
        final Set<ImageTypeSpecifier> types;
        if (type != null) {
            types = Collections.singleton(type);
        } else {
            types = Collections.emptySet();
        }
        return types.iterator();
    }

    /**
     * Fetches the stream metadata or image metadata. This method is invoked automatically when
     * the metadata are requested for the first time. The default implementation delegates directly
     * to the coverage reader.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if (reader instanceof ImageCoverageReader) {
            ImageCoverageReader icr = (ImageCoverageReader) reader;
            try {
                return (imageIndex < 0) ? icr.getStreamMetadata() : icr.getCoverageMetadata();
            } catch (DataStoreException e) {
                throw convert(e);
            }
        } else {
            return null;
        }
    }

    /**
     * Reads the image at the given index. The default implementation reads the coverage using
     * the wrapped {@link GridCoverageReader}, then extracts the {@link RenderedImage} from
     * the coverage. Note that the image returned by this method will typically be an instance
     * of {@link PlanarImage} rather than {@link BufferedImage}.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public RenderedImage readAsRenderedImage(final int imageIndex, final ImageReadParam param) throws IOException {
        GridCoverageReadParam gp = null;
        if (param != null) {
            gp = new GridCoverageReadParam();
            gp.setSourceBands(param.getSourceBands());
            gp.setDestinationBands(param.getDestinationBands());
            /*
             * Computes the geodetic envelope.
             */
            final GridGeometry geometry;
            try {
                geometry = reader.getGridGeometry();
            } catch (DataStoreException e) {
                throw convert(e);
            }
            final GridExtent range = geometry.getExtent();
            final Rectangle srcRect = new Rectangle();
            final Rectangle dstRect = new Rectangle(); // Required but ignored.
            computeRegions(param, (int) range.getSize(X_DIMENSION), (int) range.getSize(Y_DIMENSION), null, srcRect, dstRect);
            GeneralEnvelope region = new GeneralEnvelope(range.getDimension());
            for (int i=region.getDimension(); --i >= 0;) {
                final double min, max;
                switch (i) {
                    case X_DIMENSION: min=srcRect.getMinX(); max=srcRect.getMaxX(); break;
                    case Y_DIMENSION: min=srcRect.getMinY(); max=srcRect.getMaxY(); break;
                    default: min=0; max=1; break;
                }
                region.setRange(i, min, max);
            }
            try {
                region = Envelopes.transform(geometry.getGridToCRS(PixelInCell.CELL_CORNER), region);
            } catch (TransformException e) {
                throw new IIOException(e.getLocalizedMessage(), e);
            }
            if (geometry.isDefined(GridGeometry.CRS)) {
                region.setCoordinateReferenceSystem(geometry.getCoordinateReferenceSystem());
            }
            gp.setEnvelope(region);
            /*
             * Computes the resolution.
             */
            final int xSubsampling = param.getSourceXSubsampling();
            final int ySubsampling = param.getSourceYSubsampling();
            if (xSubsampling != 1 || ySubsampling != 1) {
                final double[] resolution = new double[region.getDimension()];
                resolution[X_DIMENSION] = xSubsampling * region.getSpan(X_DIMENSION) / srcRect.getWidth();
                resolution[Y_DIMENSION] = ySubsampling * region.getSpan(Y_DIMENSION) / srcRect.getHeight();
                gp.setResolution(resolution);
            }
        }
        final GridCoverage coverage = read(gp);
        return (coverage == null) ? null :
                coverage.render(null);
    }

    /**
     * Reads the image at the given index. The default implementation delegates to
     * {@link #readAsRenderedImage(int, ImageReadParam)}, then converts the image
     * to an instance of {@link BufferedImage}.
     * <p>
     * The {@code readAsRenderedImage} method should be preferred when the image is
     * not required to be an instance of {@code BufferedImage}.
     *
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        RenderedImage image = readAsRenderedImage(imageIndex, param);
        while (image instanceof RenderedImageAdapter) {
            image = ((RenderedImageAdapter) image).getWrappedImage();
        }
        if (image instanceof PlanarImage) {
            return ((PlanarImage) image).getAsBufferedImage();
        }
        return (BufferedImage) image;
    }

    /**
     * Reads the coverage at the given index. This method is invoked by {@link #readAsRenderedImage(int,
     * ImageReadParam) readAsRenderedImage} after the <cite>image parameters</cite> have been converted
     * to <cite>coverage parameters</cite>.
     * <p>
     * The default implementation delegates to
     * {@link GridCoverageReader#read(int, GridCoverageReadParam)}. Subclasses can override
     * this method if they want to perform additional processing before or after the coverage
     * is read. For example a subclass may invoke {@code GridCoverage2D.view(ViewType.RENDERED)}
     * for a better image rendering.
     *
     * @param  index The index of the coverage to be queried.
     * @param  param Optional parameters used to control the reading process, or {@code null}.
     * @return The {@link GridCoverage} at the specified index, or {@code null} if {@link #abort()}
     *         has been invoked in an other thread during the execution of this method.
     * @throws IOException If the coverage can not be read.
     */
    protected GridCoverage read(final GridCoverageReadParam param) throws IOException {
        try {
            return reader.read(param);
        } catch (DataStoreException e) {
            throw convert(e);
        } catch (CancellationException e) {
            return null;
        }
    }

    /**
     * Aborts the current reading process. This method forward the call to the wrapped
     * {@link GridCoverageReader}, but does not set the {@link #abortRequested} flag in
     * this class (because it is not used).
     */
    @Override
    public void abort() {
        reader.abort();
    }

    /**
     * Disposes this image reader and the wrapped {@link GridCoverageReader}.
     */
    @Override
    public void dispose() {
        try {
            reader.dispose();
        } catch (DataStoreException e) {
            Logging.unexpectedException(null, getClass(), "dispose", e);
        }
        super.dispose();
    }
}
