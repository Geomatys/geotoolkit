/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2018, Geomatys
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.ServiceLoader;
import javax.imageio.spi.IIORegistry;
import javax.sql.DataSource;
import org.apache.sis.internal.metadata.sql.Initializer;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.logging.MonolineFormatter;
import org.geotoolkit.internal.SetupService;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.internal.sql.AuthenticatedDataSource;
import org.geotoolkit.internal.sql.DefaultDataSource;


/**
 * A central place where to perform initialization and shutdown of all Geotk services.
 * Users are not required to perform explicit initialization and shutdown for most services
 * (except a few cases like <cite>World File Image Readers</cite>), since services are automatically
 * discovered when needed (using {@link ServiceLoader}) and disposed on JVM termination (using
 * {@linkplain Runtime#addShutdownHook shutdown hook}). However the above-cited mechanism is not
 * sufficient when an application is undeployed and re-deployed in a JEE environment without
 * restarting the JVM. For example the {@linkplain org.geotoolkit.image.io.plugin Geotk Image I/O
 * plugins} needs to be {@linkplain IIORegistry#deregisterServiceProvider(Object) deregistered}
 * during the <cite>undeploy</cite> phase before they get
 * {@linkplain IIORegistry#registerServiceProvider(Object) registered} again during the
 * <cite>re-deploy</cite> phase. Explicit invocations of {@link #initialize(Properties)} and
 * {@link #shutdown()} methods can improve the system stability in such cases.
 * <p>
 * The amount of works performed by this class depends on the modules available in the classpath.
 * The table below lists the work performed by current implementation. Users wanting more control
 * can perform those tasks themselves instead than relying on the methods defined in this
 * {@code Setup} class.
 * <p>
 * <table border="3" cellpadding="6">
 *   <tr bgcolor="lightblue">
 *     <th nowrap>Module</th>
 *     <th nowrap>Methods invoked by {@link #initialize(Properties)}</th>
 *     <th nowrap>Work done by {@link #shutdown()}</th>
 *   </tr><tr>
 *     <td nowrap>{@code geotk-utility}</td>
 *     <td><ul>
 *       <li>If {@code platform != "server"}:
 *         <ul><li><code>{@linkplain Logging#forceMonolineConsoleOutput Logging.forceMonolineConsoleOutput}(null)</code></li></ul>
 *       </li>
 *     </ul></td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td nowrap>{@code geotk-coverage}</td>
 *     <td><ul>
 *       <li>{@link org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()}</li>
 *     </ul></td>
 *     <td>
 *       Remove from the JAI {@link javax.media.jai.OperationRegistry} every
 *       plugins defined in any {@code org.geotoolkit} package.
 *     </td>
 *   </tr><tr>
 *     <td nowrap>{@code geotk-coverageio}</td>
 *     <td><ul>
 *       <li><code>{@linkplain org.geotoolkit.image.io.plugin.WorldFileImageReader.Spi#registerDefaults WorldFileImageReader.Spi.registerDefaults}(null)</code></li>
 *       <li><code>{@linkplain org.geotoolkit.image.io.plugin.WorldFileImageWriter.Spi#registerDefaults WorldFileImageWriter.Spi.registerDefaults}(null)</code></li>
 *     </ul></td>
 *     <td>
 *       Remove from {@link IIORegistry} every plugins defined in any {@code org.geotoolkit} package.
 *     </td>
 *   </tr><tr>
 *     <td nowrap>{@code geotk-coverageio-netcdf}</td>
 *     <td><ul>
 *       <li>If {@code netcdfCacheLimit != 0}:
 *         <ul><li>{@link ucar.nc2.dataset.NetcdfDataset#initNetcdfFileCache(int,int,int)}</li></ul>
 *       </li>
 *     </ul></td>
 *     <td>
 *       {@link ucar.nc2.dataset.NetcdfDataset#shutdown()}.
 *     </td>
 *   </tr>
 * </table>
 *
 * {@section Note on system preferences}
 * In current implementation, invoking {@link #initialize(Properties)}
 * with a property entry {@code platform=server} also disable the usage of
 * {@linkplain java.util.prefs.Preferences#systemRoot() system preferences}. This is a temporary
 * workaround for the JDK 6 behavior on Unix system, which display "<cite>WARNING: Couldn't flush
 * system prefs</cite>" if the {@code etc/.java} directory has not been created during the Java
 * installation process.
 * <p>
 * This workaround may be removed in a future version if JDK 7 uses its new {@code java.nio.file}
 * package for performing a better work with system preferences.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class Setup extends Static {
    /**
     * The setup state.
     * <ul>
     *   <li>0: initial state.</li>
     *   <li>1: {@link #initialize(Properties)} has been invoked.</li>
     *   <li>2: {@link #shutdown()} has been invoked.</li>
     * </ul>
     */
    private static int state;

    /**
     * Data source for the EPSG database, or {@code null} if none.
     */
    private static DataSource EPSG;

    /**
     * Do not allow instantiation of this class.
     */
    private Setup() {
    }

    /**
     * Sets the data source for the EPSG database.
     *
     * @param source data source for the EPSG database, or {@code null} if none.
     */
    public static synchronized void setEPSG(final DataSource source) {
        EPSG = source;
    }

    /**
     * Returns the EPSG data source.
     */
    public static synchronized DataSource getEPSG() {
        if (EPSG == null) try {
            final Properties properties = Installation.EPSG.getDataSource();
            if (properties != null) {
                final String url = properties.getProperty("URL");
                if (url != null) {
                    EPSG = new DefaultDataSource(url);
                    final String user = properties.getProperty("user");
                    if (user != null) {
                        EPSG = new AuthenticatedDataSource(EPSG, user, properties.getProperty("password"), false);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return EPSG;
    }

    /**
     * Returns the value for the given key in the given properties map, or the default value
     * if none. The given properties map is allowed to be {@code null}.
     */
    private static String get(final Properties properties, final String key, final String def) {
        return (properties != null) ? properties.getProperty(key, def) : def;
    }

    /**
     * Performs the initialization of all Geotk services. This method is typically invoked only
     * once. If it is invoked more than once and {@link #shutdown()} has not been invoked, then
     * every calls after the first method call are ignored.
     * <p>
     * The {@code properties} map allows some control on the initialization process. Current
     * implementation recognizes the entries listed in the table below. Any entry not listed
     * in this table are silently ignored.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue">
     *     <th nowrap>&nbsp;Key&nbsp;</th>
     *     <th nowrap>&nbsp;Valid values&nbsp;</th>
     *     <th nowrap>&nbsp;Default&nbsp;</th>
     *     <th nowrap>&nbsp;Meaning&nbsp;</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@code platform}&nbsp;</td>
     *     <td nowrap>&nbsp;{@code server}|{@code desktop}&nbsp;</td>
     *     <td nowrap>&nbsp;{@code desktop}&nbsp;</td>
     *     <td nowrap>&nbsp;Whatever the library is run for a desktop application or a server.&nbsp;</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@code netcdfCacheLimit}&nbsp;</td>
     *     <td nowrap>&nbsp;Positive integer&nbsp;</td>
     *     <td nowrap>&nbsp;0&nbsp;</td>
     *     <td nowrap>&nbsp;Maximum number of elements in the NetCDF cache (0 for no cache).&nbsp;</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@code force}&nbsp;</td>
     *     <td nowrap>&nbsp;{@code true}|{@code false}&nbsp;</td>
     *     <td nowrap>&nbsp;{@code false}&nbsp;</td>
     *     <td nowrap>&nbsp;If {@code true}, attempt a re-initialization after a shutdown.&nbsp;</td>
     *   </tr>
     * </table>
     *
     * @param  properties Optional set of properties controlling the initialization process,
     *         or {@code null}.
     * @throws IllegalStateException If the Geotk library has been {@linkplain #shutdown() shutdown}
     *         and the given properties map doesn't contain a {@code force=true} entry.
     */
    @Configuration
    public static synchronized void initialize(final Properties properties) throws IllegalStateException {
        if (state == 1) {
            return;
        }
        final boolean reinit = (state == 2);
        if (reinit && !Boolean.parseBoolean(get(properties, "force", "false"))) {
            throw new IllegalStateException();
        }
        state = 1;
        if ("server".equalsIgnoreCase(get(properties, "platform", "desktop"))) {
        } else {
            MonolineFormatter.install();
        }
        Initializer.setDefault(Setup::getEPSG);
        /*
         * Following are normally not needed, since the factory registry scans automatically
         * the classpath when first needed. However some factories may have been unregistered
         * during the shutdown process, so we need to scan the classpath to re-register them.
         */
        if (reinit) {
            DefaultFactories.fireClasspathChanged();
        }
        /*
         * Now performs every module-specific initialization.
         */
        for (final SetupService service : ServiceLoader.load(SetupService.class)) {
            service.initialize(properties, reinit);
        }
    }

    /**
     * Shutdowns all Geotk services. This method can be safely invoked even if the
     * {@link #initialize(Properties)} method has never been invoked.
     * <p>
     * The Geotk library should not be used anymore after this method call.
     */
    @Configuration
    public static synchronized void shutdown() {
        if (state != 2) {
            state = 2;
            Initializer.setDefault(null);
            for (final SetupService service : ServiceLoader.load(SetupService.class)) {
                service.shutdown();
            }
            /*
             * The following method should be invoked only when we think there is not any code still
             * runnning that may invoke Threads.executor(boolean). It is actually hard to ensure that,
             * but a search on Threads.SHUTDOWN_HOOKS and Threads.executeDisposal(Runnable) is helpful.
             */
            Threads.shutdown();
        }
    }
}
