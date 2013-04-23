/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.processing;

import java.util.Map;
import java.util.Locale;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.awt.RenderingHints;

import javax.media.jai.JAI;
import javax.media.jai.TileCache;
import javax.media.jai.Interpolation;
import net.jcip.annotations.ThreadSafe;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.processing.Operation;
import org.opengis.coverage.processing.OperationNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.GeneralParameterValue;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryRegistry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.Interpolator2D;
import org.geotoolkit.internal.FactoryUtilities;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Default implementation of a {@linkplain Coverage} processor.
 * This default implementation makes the following assumptions:
 * <p>
 * <ul>
 *   <li>Operations are declared in the
 *       {@code META-INF/services/org.opengis.coverage.processing.Operation} file.</li>
 *   <li>Operations are actually instances of {@link AbstractOperation} (note: this
 *       constraint may be relaxed in a future version after GeoAPI interfaces for grid
 *       coverage will be redesigned).</li>
 *   <li>Operation parameter names are case-insensitive.</li>
 *   <li>Most operations are backed by <cite>Java Advanced Imaging</cite>.</li>
 * </ul>
 * <p>
 * <strong>Note:</strong> This implementation does not cache produced coverages.
 * Since coverages may be big, consider wrapping {@code DefaultCoverageProcessor}
 * instances in {@link CachingCoverageProcessor}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 1.2
 * @module
 */
@ThreadSafe
public class DefaultCoverageProcessor extends AbstractCoverageProcessor {
    /**
     * Augments the amout of memory allocated for the JAI tile cache.
     */
    static {
        final long targetCapacity = 0x4000000; // 64 Mo.
        final long maxMemory = Runtime.getRuntime().maxMemory();
        final TileCache cache = JAI.getDefaultInstance().getTileCache();
        if (maxMemory >= 2*targetCapacity) {
            if (cache.getMemoryCapacity() < targetCapacity) {
                cache.setMemoryCapacity(targetCapacity);
            }
        }
        final float mb = cache.getMemoryCapacity() / (1024f * 1024f);
        LOGGER.log(Level.CONFIG, "Java Advanced Imaging: {0}, TileCache capacity={1} Mb",
                new Object[] {JAI.getBuildVersion(), mb});
        /*
         * Verifies that the tile cache has some reasonable value. A lot of users seem to
         * misunderstand the memory setting in Java and set wrong values. If the user set
         * a tile cache greater than the maximum heap size, tell him that he is looking
         * for serious trouble.
         */
        if (cache.getMemoryCapacity() + (4*1024*1024) >= maxMemory) {
            final LogRecord record = Loggings.format(Level.SEVERE,
                    Loggings.Keys.EXCESSIVE_TILE_CACHE_1, maxMemory / (1024 * 1024.0));
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
        }
    }

    /**
     * The comparator for ordering operation names.
     */
    private static final Comparator<String> COMPARATOR = new Comparator<String>() {
        @Override public int compare(final String name1, final String name2) {
            return name1.toLowerCase(Locale.US).compareTo(name2.toLowerCase(Locale.US));
        }
    };

    /**
     * The set of operations for this coverage processor. Keys are operation's name.
     * Values are operations and should not contains duplicated values. Note that while
     * keys are {@link String} objects, the operation name are actually case-insensitive
     * because of the comparator used in the sorted map.
     */
    private final Map<String,Operation> operations = new TreeMap<String,Operation>(COMPARATOR);

    /**
     * The service registry for finding {@link Operation} implementations.
     */
    private final FactoryRegistry registry;

    /**
     * The processor to declare in the {@link Hints#GRID_COVERAGE_PROCESSOR}.
     */
    private final AbstractCoverageProcessor declaredProcessor;

    /**
     * Constructs a coverage processor from the given hints. The {@link #scanForPlugins}
     * method will be automatically invoked the first time an operation is required.
     * Additional operations can be added by subclasses with the {@link #addOperation} method.
     * <p>
     * Rendering hints will be initialized with the following hints:
     * <p>
     * <ul>
     *   <li>{@link JAI#KEY_REPLACE_INDEX_COLOR_MODEL} set to {@link Boolean#FALSE}.</li>
     *   <li>{@link JAI#KEY_TRANSFORM_ON_COLORMAP} set to {@link Boolean#FALSE}.</li>
     * </ul>
     *
     * @param userHints A set of additional rendering hints, or {@code null} if none.
     */
    public DefaultCoverageProcessor(final Hints userHints) {
        this(userHints, null);
    }

    /**
     * For {@link CachingCoverageProcessor} usage.
     */
    DefaultCoverageProcessor(final Hints userHints, AbstractCoverageProcessor declaredProcessor) {
        registry = new FactoryRegistry(Arrays.asList(new Class<?>[] {
            Operation.class
        }));
        final Map<RenderingHints.Key, Object> hints = this.hints;
        hints.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
        hints.put(JAI.KEY_TRANSFORM_ON_COLORMAP,     Boolean.FALSE);
        /*
         * The following hints are relevant to some operations. We declare them explicitly,
         * with null value (meaning "undefined"), in order to inform FactoryRegistry that if
         * those hints are supplied by the user, then they should be taken in account. Next
         * we override this default setting by the user-supplied one, if any.
         */
        hints.put(Hints.COORDINATE_OPERATION_FACTORY, null);
        hints.put(Hints.LENIENT_DATUM_SHIFT, null);
        hints.put(Hints.DATUM_SHIFT_METHOD, null);
        FactoryUtilities.addImplementationHints(userHints, hints);
        hints.remove(Hints.GRID_COVERAGE_PROCESSOR); // Must erase user setting.
        if (declaredProcessor == null) {
            declaredProcessor = this;
        }
        this.declaredProcessor = declaredProcessor;
    }

    /**
     * Adds the specified operation to this processor. This method is usually invoked
     * at construction time before this processor is made accessible.
     *
     * @param  operation The operation to add.
     * @throws IllegalStateException if an operation already exists with the same name.
     */
    protected synchronized void addOperation(final Operation operation) throws IllegalStateException {
        ensureNonNull("operation", operation);
        if (operations.isEmpty()) {
            scanForPlugins();
        }
        addOperationImpl(operation);
    }

    /**
     * Implementation of {@link #addOperation} method. Also used by {@link #scanForPlugins}
     * instead of the public method in order to avoid never-ending loop.
     */
    private void addOperationImpl(final Operation operation) throws IllegalStateException {
        final String name = operation.getName().trim();
        final Operation old = operations.put(name, operation);
        if (old!=null && !old.equals(operation)) {
            operations.put(old.getName().trim(), old);
            throw new IllegalStateException(Errors.getResources(getLocale()).getString(
                    Errors.Keys.OPERATION_ALREADY_BOUNDS_1, operation.getName()));
        }
    }

    /**
     * Retrieves grid processing operations information. Each operation information contains
     * the name of the operation as well as a list of its parameters.
     */
    @Override
    public synchronized Collection<Operation> getOperations() {
        if (operations.isEmpty()) {
            scanForPlugins();
        }
        return operations.values();
    }

    /**
     * Returns the operation for the specified name.
     *
     * @param  name Name of the operation (case insensitive).
     * @return The operation for the given name.
     * @throws OperationNotFoundException if there is no operation for the specified name.
     */
    @Override
    public synchronized Operation getOperation(String name) throws OperationNotFoundException {
        ensureNonNull("name", name);
        name = name.trim();
        if (operations.isEmpty()) {
            scanForPlugins();
        }
        final Operation operation = operations.get(name);
        if (operation != null) {
            return operation;
        }
        throw new OperationNotFoundException(Errors.getResources(getLocale()).getString(
                Errors.Keys.NO_SUCH_OPERATION_1, name));
    }

    /**
     * Returns a rendering hint.
     *
     * @param  key The hint key (e.g. {@link Hints#JAI_INSTANCE}).
     * @return The hint value for the specified key, or {@code null} if there is no hint for the
     *         specified key.
     */
    public final Object getRenderingHint(final RenderingHints.Key key) {
        return hints.get(key);
    }

    /**
     * Applies a process operation to a coverage. The default implementation checks if source
     * coverages use an interpolation, and then invokes {@link AbstractOperation#doOperation}.
     * If all source coverages used the same interpolation, then this interpolation is applied
     * to the resulting coverage (except if the resulting coverage has already an interpolation).
     *
     * @param  parameters Parameters required for the operation. The easiest way to construct them
     *         is to invoke <code>operation.{@link Operation#getParameters getParameters}()</code>
     *         and to modify the returned group.
     * @return The result as a coverage.
     * @throws OperationNotFoundException if there is no operation for the parameter group name.
     * @throws CoverageProcessingException if the operation can not be executed.
     */
    @Override
    public synchronized Coverage doOperation(final ParameterValueGroup parameters)
            throws CoverageProcessingException, OperationNotFoundException
    {
        Coverage source = getPrimarySource(parameters);
        final String operationName = getOperationName(parameters);
        final Operation  operation = getOperation(operationName);
        /*
         * Detects the interpolation type for the source grid coverage.
         * The same interpolation will be applied on the result.
         */
        Interpolation[] interpolations = null;
        if (!operationName.equalsIgnoreCase("Interpolate")) {
            for (final GeneralParameterValue param : parameters.values()) {
                if (param instanceof ParameterValue<?>) {
                    final Object value = ((ParameterValue<?>) param).getValue();
                    if (value instanceof Interpolator2D) {
                        // If all sources use the same interpolation, preserves the
                        // interpolation for the resulting coverage. Otherwise, uses
                        // the default interpolation (nearest neighbor).
                        final Interpolation[] interp = ((Interpolator2D) value).getInterpolations();
                        if (interpolations == null) {
                            interpolations = interp;
                        } else if (!Arrays.equals(interpolations, interp)) {
                            // Set to no interpolation.
                            interpolations = null;
                            break;
                        }
                    }
                }
            }
        }
        /*
         * Gets the hints to be given to the operation. Note that the DefaultCoverageProcessor
         * constructor has explicitly set to null a few hints considered relevant, while not
         * necessarily specified by the user. If those hints were not overridden by the user,
         * then the corresponding null value will not be put in the hints map below.
         */
        final Hints hints = EMPTY_HINTS.clone();
        FactoryUtilities.addValidEntries(this.hints, hints, true);
        hints.put(Hints.GRID_COVERAGE_PROCESSOR, declaredProcessor);
        /*
         * Applies the operation, applies the same interpolation and log a message.
         * Note: we don't use "if (operation instanceof AbstractOperation)" below
         * because if it is not, we want the ClassCastException as the cause for the failure.
         */
        final AbstractOperation op;
        try {
            op = (AbstractOperation) operation;
        } catch (ClassCastException cause) {
            throw new OperationNotFoundException(Errors.getResources(getLocale()).getString(
                        Errors.Keys.NO_SUCH_OPERATION_1, operationName), cause);
        }
        Coverage cv = op.doOperation(parameters, hints);
        if (interpolations != null && (cv instanceof GridCoverage2D) && !(cv instanceof Interpolator2D)) {
            cv = Interpolator2D.create((GridCoverage2D) cv, interpolations);
        }
        log(source, cv, operationName, false);
        return cv;
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is needed because the
     * application class path can theoretically change, or additional plug-ins may become available.
     * Rather than re-scanning the classpath on every invocation of the API, the class path is
     * scanned automatically only on the first invocation. Clients can call this method to prompt
     * a re-scan. Thus this method need only be invoked by sophisticated applications which
     * dynamically make new plug-ins available at runtime.
     */
    public synchronized void scanForPlugins() {
        final Iterator<Operation> it = registry.getServiceProviders(Operation.class, null, null, null);
        while (it.hasNext()) {
            final Operation operation = it.next();
            final String name = operation.getName().trim();
            if (!operations.containsKey(name)) {
                addOperationImpl(operation);
            }
        }
    }
}
