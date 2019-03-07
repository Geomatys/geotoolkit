/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.coverage.processing;

import java.util.Locale;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

import org.geotoolkit.coverage.grid.Coverage;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.coverage.grid.AbstractCoverage;
import org.geotoolkit.coverage.grid.Interpolator2D;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Localized;
import org.geotoolkit.image.internal.ImageUtilities;


/**
 * Provides operations for different ways of accessing the grid coverage values as well as
 * image processing functionality. The list of available processing operations is implementation
 * dependent. The class has a discovery mechanism to determine the available processing operations.
 * <p>
 * These processing operations will transform values within a single sample dimension, and
 * leave the values in other sample dimensions unaffected. The modified sample dimension may
 * also change its type. The actual underlying grid data remains unchanged.
 * <p>
 * The class has been designed to allow the adaptations to be done in a "pipe-lined" manner.
 * The class operates on {@link Coverage} to create new a {@link Coverage}. The class does not
 * need to make a copy of the source grid data. Instead, it can return a grid coverage object
 * which applies the adaptations on the original grid coverage whenever a block of data is
 * requested. In this way, a pipeline of several grid coverages can be constructed cheaply.
 * <p>
 * This class can perform any of the following:
 * <ul>
 *   <li>Change the number of bands being accessed.</li>
 *   <li>Change the value sequencing in which the grid values are retrieved.</li>
 *   <li>Allow re-sampling of the grid coverage for a different geometry.
 *       Creating a new {@link Coverage} with different grid geometry allows for reprojecting
 *       the grid coverage to another projection and another georeferencing type, resampling to
 *       another cell resolution and subsetting the grid coverage.</li>
 *   <li>Modify the way the grid values are accessed (filtered, classified...).</li>
 *   <li>Change the interpolation method used when evaluating points which fall between grid cells.</li>
 *   <li>Filtering.</li>
 *   <li>Image enhancements.</li>
 *   <li><i>etc.</i></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 */
public abstract class AbstractCoverageProcessor extends Factory implements Localized {
    /**
     * The logger for coverage processing operations.
     */
    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.coverage.processing");

    /**
     * The logging level for reporting coverage operations.
     * This level is equals or slightly lower than {@link Level#INFO}.
     */
    public static final Level OPERATION = new LogLevel("OPERATION", 780);

    /**
     * The grid coverage logging level type.
     */
    private static final class LogLevel extends Level {
        private static final long serialVersionUID = -2944283575307061508L;
        protected LogLevel(final String name, final int level) {
            super(name, level);
        }
    }

    /**
     * Constructs a coverage processor.
     */
    protected AbstractCoverageProcessor() {
        super();
    }

    /**
     * Retrieves grid processing operations information. Each operation information contains
     * the name of the operation as well as a list of its parameters.
     *
     * @return The available processing operations
     */
    public abstract Collection<AbstractOperation> getOperations();

    /**
     * Returns the operation for the specified name.
     *
     * @param  name Name of the operation.
     * @return The operation for the given name.
     * @throws OperationNotFoundException if there is no operation for the specified name.
     */
    public abstract AbstractOperation getOperation(String name) throws OperationNotFoundException;

    /**
     * Applies an operation.
     *
     * @param  parameters Parameters required for the operation.
     * @return The result as a coverage.
     * @throws OperationNotFoundException if there is no operation for the parameter group name.
     * @throws CoverageProcessingException if the operation can not be executed.
     */
    public abstract Coverage doOperation(final ParameterValueGroup parameters)
            throws OperationNotFoundException, CoverageProcessingException;

    /**
     * The locale for logging message or reporting errors. The default implementations
     * returns the {@linkplain Locale#getDefault() default locale}. Subclasses can override
     * this method if a different locale is wanted.
     *
     * @return The locale for logging message.
     */
    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Logs a message for an operation. The message will be logged only if the source grid
     * coverage is different from the result (i.e. if the operation did some work).
     *
     * @param source The source grid coverage.
     * @param result The resulting grid coverage.
     * @param operationName the operation name.
     * @param fromCache {@code true} if the result has been fetch from the cache.
     */
    final void log(final Coverage source,
                   final Coverage result,
                   final String   operationName,
                   final boolean  fromCache)
    {
        if (source != result) {
            String interp = "Nearest";
            if (result instanceof Interpolator2D) {
                interp = ImageUtilities.getInterpolationName(
                            ((Interpolator2D) result).getInterpolation());
            }
            final Locale locale = getLocale();
            final LogRecord record = Loggings.getResources(locale).getLogRecord(
                    OPERATION, Loggings.Keys.AppliedOperation_4,
                     getName((source != null) ? source : result, locale),
                    operationName, interp, Integer.valueOf(fromCache ? 1 : 0));
            record.setSourceClassName(getClass().getCanonicalName());
            record.setSourceMethodName("doOperation");
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
        }
    }

    /**
     * Returns the primary source coverage from the specified parameters, or {@code null} if none.
     */
    static Coverage getPrimarySource(final ParameterValueGroup parameters) {
        try {
            return (Coverage) parameters.parameter("Source").getValue();
        } catch (ParameterNotFoundException exception) {
            /*
             * "Source" parameter may not exists. Conservatively
             * assumes that the operation will do some useful work.
             */
            return null;
        }
    }

    /**
     * Returns the operation name for the specified parameters.
     */
    static String getOperationName(final ParameterValueGroup parameters) {
        return parameters.getDescriptor().getName().getCode().trim();
    }

    /**
     * Returns the coverage name in the specified locale.
     */
    private static String getName(final Coverage coverage, final Locale locale) {
        if (coverage instanceof AbstractCoverage) {
            final InternationalString name = ((AbstractCoverage) coverage).getName();
            if (name != null) {
                return name.toString(locale);
            }
        }
        return Vocabulary.getResources(locale).getString(Vocabulary.Keys.Untitled);
    }
}
