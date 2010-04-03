/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.lang;

import java.util.Locale;
import java.util.ServiceLoader;
import javax.imageio.spi.IIORegistry;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.internal.SetupService;
import org.geotoolkit.resources.Errors;


/**
 * A central place where to perform initialisation and shutdown of all Geotk services.
 * Users are not required to perform explicit initialization and shutdown for most services
 * (except a few cases like <cite>World File readers</cite>), since services are automatically
 * discovered when needed (using {@link ServiceLoader}) and disposed on JVM termination (using
 * {@linkplain Runtime#addShutdownHook shutdown hook}. However the above-cited mechanism is not
 * suffisient when an application is undeployed and re-deployed in a JEE environment without
 * restarting the JVM. For example the Geotk Image I/O plugins needs to be
 * {@linkplain IIORegistry#deregisterServiceProvider(Object) deregistered} during the
 * <cite>undeploy</cite> phase before they get 
 * {@linkplain IIORegistry#registerServiceProvider(Object) registered} again during the
 * <cite>re-deploy</cite> phase. Explicit invocations of {@link #initialize(boolean)} and
 * {@link #shutdown()} methods can improve the system stability in such cases.
 * <p>
 * The amont of works performed by this class depends on the modules available in the classpath.
 * The table below lists the work performed by current implementation. Users wanting more control
 * can perform those tasks themself instead than relying on the methods defined in this
 * {@code Setup} class.
 * <p>
 * <table border="3" cellpadding="6">
 *   <tr bgcolor="lightblue">
 *     <th nowrap>Module</th>
 *     <th nowrap>Work done by {@link #initialize(boolean)}</th>
 *     <th nowrap>Work done by {@link #shutdown()}</th>
 *   </tr><tr>
 *     <td>{@code geotk-utility}</td>
 *     <td>
 *       Invoke {@link Logging#forceMonolineConsoleOutput Logging.forceMonolineConsoleOutput(null)}
 *       (only if {@code server} = {@code false}).
 *     </td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>{@code geotk-coverage}</td>
 *     <td>
 *       Invoke {@link org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()}.
 *     </td>
 *     <td>
 *       Remove from the JAI {@link javax.media.jai.OperationRegistry} every
 *       plugins defined in any {@code org.geotoolkit} package.
 *     </td>
 *   </tr><tr>
 *     <td>{@code geotk-coverageio}</td>
 *     <td>
 *       Invoke the {@code Spi.registerDefaults(null)} method for
 *       {@link org.geotoolkit.image.io.plugin.WorldFileImageReader.Spi#registerDefaults WorldFileImageReader} and
 *       {@link org.geotoolkit.image.io.plugin.WorldFileImageWriter.Spi#registerDefaults WorldFileImageWriter}.
 *     </td>
 *     <td>
 *       Remove from {@link IIORegistry} every plugins defined in any {@code org.geotoolkit} package.</li>
 *     </td>
 *   </tr><tr>
 *     <td>{@code geotk-coverageio-netcdf}</td>
 *     <td>
 *       Invoke {@link ucar.nc2.dataset.NetcdfDataset#initNetcdfFileCache(int,int,int)}
 *       if no cache is already defined.
 *     </td>
 *     <td>
 *       Invoke {@link ucar.nc2.dataset.NetcdfDataset#shutdown()}.
 *     </td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
public final class Setup {
    /**
     * The setup state.
     * <ul>
     *   <li>0: initial state.</li>
     *   <li>1: {@link #initialize()} has been invoked.</li>
     *   <li>2: {@link #shutdown()} has been invoked.</li>
     * </ul>
     */
    private static int state;

    /**
     * Do not allow instantiation of this class.
     */
    private Setup() {
    }

    /**
     * Performs the initialisation of all Geotk services. This method is typically invoked only
     * once. If it is invoked more than once and {@code #shutdown()} has not been invoked, then
     * every calls after the first method call are ignored.
     *
     * @param server {@code true} if the initialisation is performed for execution on a server
     *        rather than on a desktop.
     */
    @Configuration
    public static synchronized void initialize(final boolean server) {
        if (state == 1) {
            return;
        }
        final boolean reinit = (state == 2);
        state = 1;
        if (!server) {
            Logging.ALL.forceMonolineConsoleOutput(null);
        }
        /*
         * Following are normally not needed, since the factory registry scans automatically
         * the classpath when first needed. However some factories may have been unregistered
         * during the shutdown process, so we need to scan the classpath to re-register them.
         */
        if (reinit) {
            FactoryFinder.scanForPlugins();
            IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
        }
        /*
         * Now performs every module-specific initialization.
         */
        for (final SetupService service : ServiceLoader.load(SetupService.class)) {
            service.initialize(reinit);
        }
    }

    /**
     * Shutdowns all Geotk services. This method can be safely invoked even if the
     * {@link #initialize(boolean)} method has never been invoked.
     * <p>
     * The Geotk library should not be used anymore after this method call. If nevertheless the
     * {@link #initialize(boolean)} method is invoked again, then this {@code Setup} class will
     * try to restart Geotk services on a <cite>best effort</cite> basis but without garantees.
     */
    @Configuration
    public static synchronized void shutdown() {
        state = 2;
        for (final SetupService service : ServiceLoader.load(SetupService.class)) {
            service.shutdown();
        }
    }

    /**
     * Shows the <cite>Swing</cite> Graphical User Interface for configuring the Geotk library.
     * This method requires the {@code geotk-setup} module to be present on the classpath.
     * <p>
     * Users can also display the same GUI from the command line by running the following
     * command in the shell (replace {@code SNAPSHOT} by the actual Geotk version number):
     *
     * {@preformat shell
     *     java -jar geotk-setup-SNAPSHOT.jar
     * }
     *
     * @throws UnsupportedOperationException if the {@code geotk-setup} module is not on the classpath.
     */
    public static void showControlPanel() throws UnsupportedOperationException {
        try {
            Class.forName("org.geotoolkit.internal.setup.ControlPanel")
                 .getMethod("show", Locale.class).invoke(null, new Object[] {null});
        } catch (ClassNotFoundException exception) {
            throw new UnsupportedOperationException(Errors.format(
                    Errors.Keys.MISSING_MODULE_$1, "geotk-setup"), exception);
        } catch (InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new UndeclaredThrowableException(cause);
        } catch (Exception exception) {
            // Should never happen if we didn't broke our ControlPanel class.
            throw new AssertionError(exception);
        }
    }
}
