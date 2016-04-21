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

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.imageio.spi.ImageReaderWriterSpi;
import java.awt.geom.AffineTransform;
import java.awt.Dimension;

import org.opengis.util.InternationalString;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.Classes;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.apache.sis.referencing.IdentifiedObjects;

import static org.geotoolkit.image.io.MultidimensionalImageStore.*;
import static org.geotoolkit.coverage.io.GridCoverageStore.LOGGER;
import static org.geotoolkit.internal.InternalUtilities.adjustForRoundingError;


/**
 * Static utilities methods for use by {@link ImageCoverageReader} and {@link ImageCoverageStore}.
 * Current implementation contains mostly method related to logging. They are isolated in this
 * class in order to reduce the amount of code de load when there is no logging. To make that
 * effective, it is caller responsibility to check if logging are enabled before to invoke any
 * method in this class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.15
 * @module
 */
final class ImageCoverageStore extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private ImageCoverageStore() {
    }

    /**
     * Logs a "Created encoder|decoder of class Foo" message. This method can
     * be invoked from {@code setInput} or {@code setOutput} methods only.
     * <p>
     * The message is logged at the level returned by {@link GridCoverageStore#getFineLevel()}.
     *
     * @param store  The object which is invoking this method.
     * @param caller The caller class (not necessarily {@code object.getClass()}.
     * @param codec  The object for which to write the class name.
     * @param The provider (for image reader/writer), or {@code null}.
     */
    static void logCodecCreation(final GridCoverageStore store, final Class<?> caller,
            final Object codec, final ImageReaderWriterSpi spi)
    {
        assert caller.isInstance(store) : caller;
        final boolean write = (store instanceof GridCoverageWriter);
        final Locale locale = store.locale;
        String message = Loggings.getResources(locale).getString(
                Loggings.Keys.CreatedCodecOfClass_2, write ? 1 : 0, codec.getClass().getName());
        if (spi != null) {
            final StringBuilder buffer = new StringBuilder(message).append('\n');
            Formats.formatDescription(spi, locale, buffer);
            message = buffer.toString();
        }
        final LogRecord record = new LogRecord(store.getFineLevel(), message);
        record.setLoggerName(LOGGER.getName());
        record.setSourceClassName(caller.getName());
        record.setSourceMethodName(write ? "setOutput" : "setInput");
        LOGGER.log(record);
    }

    /**
     * Logs a read or write operation. This method can be invoked from
     * {@code read} or {@code write} methods only.
     *
     * @param level      The logging level, as provided by {@link GridCoverageStore#getLogLevel(long)}.
     * @param locale     The locale to use for formatting messages.
     * @param caller     The caller class.
     * @param write      {@code true} for a write operation, or {@code false} for a read operation.
     * @param stream     The input or output file or stream.
     * @param imageIndex The index of the image being read or written.
     * @param coverage   The coverage read or written.
     * @param actualSize The actual image size (may be different than the coverage grid envelope),
     *                   or {@code null} to compute it from the grid envelope.
     * @param crs        The coordinate reference system (may be different than the coverage CRS),
     *                   or {@code null} for the coverage CRS.
     * @param destToExtractedGrid The transform from the destination grid to the extracted source
     *                   grid, or {@code null}.
     * @param timeNanos  The elapsed execution time, in nanoseconds.
     */
    static void logOperation(
            final Level               level,
            final Locale              locale,
            final Class<?>            caller,
            final boolean             write,
                  Object              stream,
            final int                 imageIndex,
            final GridCoverage        coverage,
            final Dimension           actualSize,
            CoordinateReferenceSystem crs,
            final MathTransform2D     destToExtractedGrid,
            final long                timeNanos)
    {
        /*
         * Get a string representation of the input or output. If the input/output is
         * a character sequence, a file or a URI/URL, then its string representation is
         * returned. Otherwise we will use only the class name.
         *
         * In the particular where the input is an array, we assume that each array element
         * is for an image at the corresponding index. This is what MosaicImageReader does.
         */
        if (stream instanceof Object[]) {
            final Object[] array = (Object[]) stream;
            if (imageIndex < array.length) {
                stream = array[imageIndex];
            }
        }
        final String streamName;
        if (IOUtilities.canProcessAsPath(stream)) {
            streamName = IOUtilities.filename(stream);
        } else {
            streamName = Classes.getShortClassName(stream);
        }
        /*
         * Get the coverage name, or "untitled" if unknown. This is often "unknown"
         * unless the ImageReader implements the NamedImageStore interface, as for
         * the NetCDF format.
         */
        InternationalString name = null;
        if (coverage instanceof AbstractCoverage) {
            name = ((AbstractCoverage) coverage).getName();
        }
        if (name == null) {
            name = Vocabulary.formatInternational(Vocabulary.Keys.Untitled);
        }
        /*
         * Get the view types (PHOTOGRAPHIC, RENDERED, PACKED, NATIVE, GEOPHYSICS).
         * This is specific to GridCoverage2D. For all other cases, we will write "none".
         */
        final String viewTypes;
        if (coverage instanceof GridCoverage2D) {
            viewTypes = ((GridCoverage2D) coverage).getViewTypes().toString();
        } else {
            viewTypes = Vocabulary.getResources(locale).getString(Vocabulary.Keys.None);
        }
        /*
         * Get the coverage dimension. In the special case of ImageCoverageWriter, the
         * dimension of the image actually written may be different than the dimension
         * of the coverage, if a subregion has been supplied in the ImageWriteParam.
         * The 'actualSize' argument allows to specify the dimension actually written.
         */
        final GridEnvelope ge = coverage.getGridGeometry().getExtent();
        final int dimension = ge.getDimension();
        final StringBuilder buffer = new StringBuilder();
        for (int i=0; i<dimension; i++) {
            int span = ge.getSpan(i);
            if (actualSize != null) {
                switch (i) {
                    case X_DIMENSION: span = actualSize.width;  break;
                    case Y_DIMENSION: span = actualSize.height; break;
                }
            }
            if (i != 0) {
                buffer.append(" \u00D7 ");
            }
            buffer.append(span);
        }
        final String size = buffer.toString();
        /*
         * Format the CRS name and its identifier (if any). In the case of ImageCoverageWriter,
         * the formatted CRS may be different than the coverage CRS since a resampling may have
         * been performed on-the-fly.
         */
        String crsName = null;
        if (crs == null) {
            crs = coverage.getCoordinateReferenceSystem();
        }
        if (crs != null) {
            buffer.setLength(0);
            String t = IdentifiedObjects.getName(crs, null);
            if (t != null) {
                buffer.append(t);
            }
            final String id = IdentifiedObjects.getIdentifierOrName(crs);
            if (id != null) {
                buffer.append(" (").append(id).append(')');
            }
            if (buffer.length() != 0) {
                crsName = buffer.toString();
            }
        }
        if (crsName == null) {
            crsName = Vocabulary.getResources(locale).getString(Vocabulary.Keys.Undefined);
        }
        /*
         * Get the "source to destination" transform. We will format affine transform
         * in a special way since it is the most common case and usually contains only
         * translation and scale factors.
         */
        String transform = null;
        if (destToExtractedGrid != null && !destToExtractedGrid.isIdentity()) try {
            if (destToExtractedGrid instanceof AffineTransform) {
                final AffineTransform tr = (AffineTransform) destToExtractedGrid.inverse();
                if (tr.getShearX() == 0 && tr.getShearY() == 0) {
                    buffer.setLength(0);
                    transform = buffer.append("AffineTransform[scale=(")
                            .append(adjustForRoundingError(tr.getScaleX())).append(", ")
                            .append(adjustForRoundingError(tr.getScaleY())).append("), translation=(")
                            .append(adjustForRoundingError(tr.getTranslateX())).append(", ")
                            .append(adjustForRoundingError(tr.getTranslateY())).append(")]").toString();
                } else {
                    // The 'new' is for avoiding AffineTransform2D.toString().
                    transform = new AffineTransform(tr).toString();
                }
            } else {
                transform = Classes.getShortClassName(destToExtractedGrid);
            }
        } catch (NoninvertibleTransformException e) {
            transform = e.toString();
        }
        if (transform == null) {
            transform = Vocabulary.getResources(locale).getString(Vocabulary.Keys.None);
        }
        /*
         * Put everything in a log record.
         */
        final LogRecord record = Loggings.getResources(locale).getLogRecord(
                level, Loggings.Keys.CoverageStore_8, new Object[] {
                        write ? 1 : 0, streamName, name.toString(locale), viewTypes,
                        size, crsName, transform, timeNanos / 1E+6
        });
        record.setLoggerName(LOGGER.getName());
        record.setSourceClassName(caller.getName());
        record.setSourceMethodName(write ? "write" : "read");
        LOGGER.log(record);
    }
}
