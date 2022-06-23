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
 */
package org.geotoolkit.image.jai;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RIFRegistry;

import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.internal.image.jai.*;

import static org.geotoolkit.image.internal.Setup.PRODUCT_NAME;


/**
 * A set of static methods for managing JAI's {@linkplain OperationRegistry operation registry}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
public final class Registry extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    private Registry() {
    }

    /**
     * Unconditionally registers all JAI operations provided in the {@link org.geotoolkit.image.jai}
     * package. This method usually don't need to be invoked, since JAI should parse automatically
     * the {@code META-INF/registryFile.jai} file at startup time. However, this default mechanism
     * may fail when the Geotk JAR file is unreachable from the JAI class loader, in which case
     * the {@link org.geotoolkit.coverage.processing} package will invoke this method as a fallback.
     * <p>
     * Note to module maintainer: if this method is updated, remember to update the
     * {@code META-INF/registryFile.jai} file accordingly.
     *
     * @param  registry The operation registry to register with.
     * @return {@code true} if all registrations have been successful.
     */
    @Configuration
    public static boolean registerGeotoolkitServices(final OperationRegistry registry) {
        LogRecord record;
        String op = PRODUCT_NAME;
        try {
            op = Combine.OPERATION_NAME;
            registry.registerDescriptor(new CombineDescriptor());
            RIFRegistry.register(registry, op, PRODUCT_NAME, new CombineCRIF());

            op = Hysteresis.OPERATION_NAME;
            registry.registerDescriptor(new HysteresisDescriptor());
            RIFRegistry.register(registry, op, PRODUCT_NAME, new HysteresisCRIF());

            op = NodataFilter.OPERATION_NAME;
            registry.registerDescriptor(new NodataFilterDescriptor());
            RIFRegistry.register(registry, op, PRODUCT_NAME, new NodataFilterCRIF());

            op = SilhouetteMask.OPERATION_NAME;
            registry.registerDescriptor(new SilhouetteMaskDescriptor());
            RIFRegistry.register(registry, op, PRODUCT_NAME, new SilhouetteMaskCRIF());

            record  = Loggings.format(Level.CONFIG, Loggings.Keys.RegisteredJaiOperations);
            op = null;
        } catch (IllegalArgumentException exception) {
            /*
             * Logs a message with the WARNING level, because DefaultProcessing class initialization
             * is likely to fails (since it tries to load operations declared in META-INF/services,
             * and some of them depend on JAI operations).
             */
            record = Loggings.getResources(null).getLogRecord(Level.WARNING,
                    Loggings.Keys.CantRegisterJaiOperation_1, op);
            record.setThrown(exception);
        }
        log("registerGeotoolkitServices", record);
        return op == null;
    }

    /**
     * Logs the specified record.
     */
    private static void log(final String method, final LogRecord record) {
        record.setSourceClassName(Registry.class.getName());
        record.setSourceMethodName(method);
        final Logger logger = Logger.getLogger("org.geotoolkit.image.jai");
        record.setLoggerName(logger.getName());
        logger.log(record);
    }
}
