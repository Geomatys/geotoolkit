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
package org.geotoolkit.referencing.factory.epsg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import javax.naming.NoInitialContextException;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.referencing.factory.ThreadedAuthorityFactory;
import org.geotoolkit.referencing.factory.NoSuchFactoryException;
import org.geotoolkit.internal.referencing.factory.ImplementationHints;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.JNDI;
import org.geotoolkit.internal.sql.Dialect;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.internal.io.Installation;

import static org.geotoolkit.internal.referencing.CRSUtilities.EPSG_VERSION;


/**
 * The EPSG factory registered in {@link AuthorityFactoryFinder}.
 * This class has the following responsibilities:
 * <p>
 * <ul>
 *   <li>Aquire a {@linkplain DataSource data source} using a
 *       {@linkplain #CONFIGURATION_FILE configuration file}, JNDI or otherwise.</li>
 *   <li>Specify a {@linkplain DirectEpsgFactory worker class} that will talk to the database
 *       in the event of a cache miss. The class will be specific to the dialect of SQL used
 *       by the database hosting the EPSG tables.</li>
 * </ul>
 * <p>
 * Note that we are working with <strong>the same</strong> tables as defined by EPSG. The only
 * thing that changes is the database used to host these tables, and optionally the schema and
 * table names. The EPSG database version can be determined by the {@linkplain Citation#getEdition
 * edition attribute} of the {@linkplain AuthorityFactory#getAuthority authority}.
 * <p>
 * Users should not creates instance of this class directly. They should invoke one of
 * <code>{@linkplain AuthorityFactoryFinder}.getFooAuthorityFactory("EPSG")</code> methods
 * instead, unless they want to derive their own subclass. In the later case, the following
 * methods are good candidate for overriding:
 * <p>
 * <ul>
 *   <li>{@link #createDataSource(Properties)} used to create a default {@code DataSource}
 *       when none was explicitly specified by the user (typically as a hint).</li>
 *   <li>{@link #createBackingStore(Hints)} used to create a worker instance capable
 *       to speak the SQL dialect of that database.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.18
 *
 * @see DirectEpsgFactory
 * @see <a href="http://www.geotoolkit.org/modules/referencing/supported-codes.html">List of authority codes</a>
 *
 * @since 2.1
 * @module
 */
@ImplementationHints(forceLongitudeFirst=false)
public class ThreadedEpsgFactory extends ThreadedAuthorityFactory implements CRSAuthorityFactory,
        CSAuthorityFactory, DatumAuthorityFactory, CoordinateOperationAuthorityFactory
{
    /**
     * The user configuration file, which is {@value}. This file is used if no {@link DataSource}
     * object were specified explicitly to the constructor, either directly or as a hint. In such
     * case, {@code ThreadedEpsgFactory} will look for the first of the following files:
     * <p>
     * <ul>
     *   <li><code>{@value}</code> in the current directory</li>
     *   <li><code>{@value}</code> in the user's home directory</li>
     *   <li>{@code "EPSG/DataSource.properties"} in the Geotk application data directory</li>
     * </ul>
     * <p>
     * This file should contain the properties listed below.
     * <P>
     * <TABLE BORDER="1">
     * <TR BGCOLOR="#EEEEFF">
     *   <TH>Property</TH>
     *   <TH>Type</TH>
     *   <TH>Description</TH>
     * </TR>
     * <TR>
     *   <TD>&nbsp;{@code URL}&nbsp;</TD>
     *   <TD>&nbsp;{@code String}&nbsp;</TD>
     *   <TD>&nbsp;URL to the database.&nbsp;</TD>
     * </TR>
     * <TR>
     *   <TD>&nbsp;{@code schema}&nbsp;</TD>
     *   <TD>&nbsp;{@code String}&nbsp;</TD>
     *   <TD>&nbsp;The schema for the EPSG tables.&nbsp;</TD>
     * </TR>
     * <TR>
     *   <TD>&nbsp;{@code user}&nbsp;</TD>
     *   <TD>&nbsp;{@code String}&nbsp;</TD>
     *   <TD>&nbsp;User used to make database connections.&nbsp;</TD>
     * </TR>
     * <TR>
     *   <TD>&nbsp;{@code password}&nbsp;</TD>
     *   <TD>&nbsp;{@code String}&nbsp;</TD>
     *   <TD>&nbsp;Password used to make database connections.&nbsp;</TD>
     * </TR>
     * </TABLE>
     *
     * @since 3.00
     */
    public static final String CONFIGURATION_FILE = "EPSG-DataSource.properties";

    /**
     * The factories to be given to the backing store.
     */
    private final ReferencingFactoryContainer factories;

    /**
     * The data source, or {@code null} if the connection has not yet been established.
     */
    private DataSource datasource;

    /**
     * The name of the schema in the database where to look for the tables, or {@code null} if none.
     * <p>
     * This property is read from the {@value #CONFIGURATION_FILE} file by {@link #getDataSource()},
     * and is used by {@link #createBackingStore(Hints)}. If a subclass wants to change this value,
     * then overriding the {@link #createDataSource(Properties)} method is a convenient way to do so.
     *
     * @since 3.00
     */
    protected String schema;

    /**
     * The user name to be given to {@link DataSource#getConnection(String,String)}, or {@code null}
     * if none. In the later case, {@link DataSource#getConnection()} will be used instead.
     * <p>
     * This property is read from the {@value #CONFIGURATION_FILE} file by {@link #getDataSource()},
     * and is used by {@link #createBackingStore(Hints)}. If a subclass wants to change this value,
     * then overriding the {@link #createDataSource(Properties)} method is a convenient way to do so.
     *
     * @since 3.00
     */
    protected String user;

    /**
     * The password to be given to {@link DataSource#getConnection(String,String)}, or {@code null}
     * if none.
     * <p>
     * This property is read from the {@value #CONFIGURATION_FILE} file by {@link #getDataSource()},
     * and is used by {@link #createBackingStore(Hints)}. If a subclass wants to change this value,
     * then overriding the {@link #createDataSource(Properties)} method is a convenient way to do so.
     *
     * @since 3.00
     */
    protected String password;

    /**
     * The map used for adapting the SQL statements to a particular dialect. This map is
     * initialized by {@link AnsiDialectEpsgFactory} and saved here only in order to avoid
     * querying the database metadata every time a new backing store factory is created.
     */
    private transient Map<String,String> toANSI;

    /**
     * Constructs an authority factory using the default set of factories. The instance
     * created by this method will use the first of the following possibilities:
     * <p>
     * <ul>
     *   <li>The {@linkplain DataSource data source} specified by {@link Hints#EPSG_DATA_SOURCE}
     *       in the {@linkplain Hints#getSystemDefault system default hints}, if any.</li>
     *   <li>The connection parameters specified in the {@value #CONFIGURATION_FILE} if such file
     *       is found.</li>
     *   <li>The JavaDB (a.k.a. Derby) embedded database if the Derby JDBC driver is found on
     *       the classpath.</li>
     *   <li>The HSQL embedded database if the HSQL JDBC driver is found on the classpath.</li>
     * </ul>
     */
    public ThreadedEpsgFactory() {
        this(EMPTY_HINTS);
        // See http://jira.geotoolkit.org/browse/GEOTK-159
        hints.put(Hints.EPSG_DATA_SOURCE, Hints.getSystemDefault(Hints.EPSG_DATA_SOURCE));
    }

    /**
     * Constructs an authority factory which will connect to an EPSG database using the
     * given data source. The example below creates a data source for a connection to a
     * PostgreSQL database on the local machine.
     *
     * {@preformat java
     *     PGSimpleDataSource source = new PGSimpleDataSource();
     *     ds.setServerName("localhost");
     *     ds.setDatabaseName("EPSG");
     *     ds.setUser("postgre");
     * }
     *
     * @param source The data source for the EPSG database, or {@code null}.
     *
     * @since 3.00
     */
    public ThreadedEpsgFactory(final DataSource source) {
        this(EMPTY_HINTS);
        ensureNonNull("source", source);
        hints.put(Hints.EPSG_DATA_SOURCE, source);
    }

    /**
     * Constructs an authority factory using a set of factories created from the specified hints.
     * Hints of special interest are:
     * <p>
     * <ul>
     *   <li>{@link Hints#EPSG_DATA_SOURCE}</li>
     * </ul>
     * <p>
     * This constructor recognizes also the {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS},
     * {@link Hints#DATUM_FACTORY DATUM} and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM}
     * {@code FACTORY} hints.
     *
     * @param userHints An optional set of hints, or {@code null} for the default ones.
     */
    public ThreadedEpsgFactory(final Hints userHints) {
        super(userHints);
        factories = ReferencingFactoryContainer.instance(userHints);
        hints.put(Hints.EPSG_DATA_SOURCE, (userHints != null) ? userHints.get(Hints.EPSG_DATA_SOURCE) : null);
        /*
         * Allow key collision, actually not because we have different object for the same code
         * (Geotk use also the object type in the key, so there is no more collission that way),
         * but rather to allow some recursivity. For example a call to getDatum(...) may invoke
         * createBursaWolfParameters(...) which invoke getDatum(...) again for the same code
         * while the datum was still under construction. The current Cache implementation signals
         * that as a key collision, even if the object to be constructed is actually the same.
         */
        setKeyCollisionAllowed(true);
        setTimeout(15000L); // Close the connection after 15 seconds of inactivity.
    }

    /**
     * Returns the default JDBC URL to use for connection to the EPSG embedded database.
     * This method returns a URL using the JavaDB driver, connecting to the database in the
     * installation directory specified by the setup program in the
     * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module.
     * If this setup program has not been run, then a platform-dependent location relative
     * to the user home directory is returned.
     * <p>
     * If no database exists in the above-cited directory, then a new EPSG database will be
     * created by {@code ThreadedEpsgFactory} when first needed provided that the
     * <a href="http://www.geotoolkit.org/modules/referencing/geotk-epsg">geotk-epsg</a>
     * module is reachable on the classpath.
     * <p>
     * Note that the directory may change in any Geotk version. More specifically, every
     * upgrade of the embedded EPSG database may cause a change of the default directory.
     *
     * @return The default JDBC URL to use for the connection to the EPSG database.
     *
     * @since 3.00
     */
    public static String getDefaultURL() {
        try {
            return getDefaultURL(false);
        } catch (IOException e) {
            // Should never happen when the 'create' argument is 'false'.
            throw new AssertionError(e);
        }
    }

    /**
     * Returns the default JDBC URL to use for connection to the EPSG embedded database.
     * The returned URL expects an existing database, unless the {@code create} parameter
     * is {@code true} in which case the URL allows database creation.
     *
     * @param  create {@code true} if this method should create the database directory if
     *         it does not already exist, or {@code false} otherwise.
     * @return The default JDBC URL to use for the connection to the EPSG database.
     * @throws IOException If the database directory can not be created.
     */
    static String getDefaultURL(boolean create) throws IOException {
        File directory;
        if (create) {
            directory = Installation.EPSG.validDirectory(true);
        } else {
            directory = Installation.EPSG.directory(true);
        }
        String driver  = "derby";
        if (!Dialect.DERBY.isDriverRegistered()) {
            /*
             * If the Dervy driver is not found, looks for the HSQL driver.
             * If it is not found neither, we will keep the Derby driver as
             * the default one.
             */
            try {
                Class.forName(Dialect.HSQL.driverClass);
                directory = new File(directory, "HSQL");
                driver = "hsqldb";
                create = false;
            } catch (ClassNotFoundException e) {
                // Ignore - we will stay with the Derby driver.
            }
        }
        final StringBuilder buffer = new StringBuilder("jdbc:").append(driver).append(':')
                .append(directory.getPath().replace(File.separatorChar, '/'))
                .append('/').append(EPSG_VERSION);
        if (create) {
            // Allow the creation of the database only if the needed scripts are available.
            buffer.append(";create=true");
        }
        return buffer.toString();
    }

    /**
     * Loads the {@linkplain #CONFIGURATION_FILE configuration file}, or returns {@code null} if
     * no configuration file can be found. The search path is documented in {@link #getDataSource}.
     *
     * @return The properties, or {@code null} if none wer found.
     * @throws FactoryException if an error occurred while reading the properties.
     */
    private static Properties properties() throws FactoryException {
        File file = new File(CONFIGURATION_FILE);
        if (!file.isFile()) {
            file = new File(System.getProperty("user.home", "."), CONFIGURATION_FILE);
            if (!file.isFile()) {
                file = new File(Installation.EPSG.directory(true), Installation.DATASOURCE_FILE);
                if (!file.isFile()) {
                    return null;
                }
            }
        }
        final Properties p = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            p.load(in);
        } catch (IOException exception) {
            throw new FactoryException(Errors.format(Errors.Keys.CANT_READ_FILE_1, file), exception);
        }
        return p;
    }

    /**
     * Returns the data source for the EPSG database. The default implementation performs
     * the following steps:
     *
     * <ol>
     *   <li><p>If a {@link DataSource} object was given explicitly to the constructor, it is
     *       returned.</p></li>
     *
     *   <li><p>Otherwise if hint value is associated to the {@link Hints#EPSG_DATA_SOURCE} key,
     *       then there is a choice:
     *       <ul>
     *         <li>If that value is an instance of {@link DataSource}, it is returned.</li>
     *         <li>If that value is an instance of {@link Name}, then a
     *             {@linkplain InitialContext#lookup(Name) JNDI lookup} is performed for that name.</li>
     *         <li>If that value is an instance of {@link String}, then a
     *             {@linkplain InitialContext#lookup(String) JNDI lookup} is performed for that name.</li>
     *       </ul></p>
     *   </li>
     *
     *   <li><p>Otherwise if at least one of the following files exist, then the first one is used
     *       for etablishing a connection (See {@link #CONFIGURATION_FILE} for more informations).
     *       <ul>
     *         <li>{@code "EPSG-DataSource.properties"} in the current directory</li>
     *         <li>{@code "EPSG-DataSource.properties"} in the user's home directory</li>
     *         <li>{@code "EPSG/DataSource.properties"} in the Geotk application data directory</li>
     *       </ul></p>
     *   </li>
     *
     *   <li><p>Otherwise the {@linkplain #getDefaultURL() default URL} to the embedded database
     *       is used. If the database does not exist and the {@code geotk-epsg.jar} file is
     *       reachable on the classpath, then the EPSG database will be created when first
     *       needed.</p></li>
     * </ol>
     *
     * The two last steps are actually encapsulated in a call to {@link #createDataSource(Properties)}.
     * Subclasses can override that method if they want more control on that part (for example in
     * order to perform different tasks depending the content of the properties file).
     *
     * @return The data source. Should never be {@code null}.
     * @throws FactoryException if the operation failed (for example an I/O error while
     *         reading the configuration file, or a failure to lookup the JNDI name).
     */
    protected synchronized DataSource getDataSource() throws FactoryException {
        DataSource datasource = this.datasource;
        if (datasource == null) {
            schema = user = password = null;
            final Object hint = hints.get(Hints.EPSG_DATA_SOURCE);
            if (hint == null) {
                /*
                 * No EPSG_DATA_SOURCE hint. Search for CONFIGURATION_FILE.
                 * If none are found, ask for a default data source.
                 */
                final Properties p = properties();
                if (p != null) {
                    schema   = p.getProperty("schema");
                    user     = p.getProperty("user");
                    password = p.getProperty("password");
                }
                datasource = createDataSource(p);
            } else if (hint instanceof DataSource) {
                datasource = (DataSource) hint;
            } else try {
                final Object source;
                final Hints hints = EMPTY_HINTS.clone();
                hints.putAll(this.hints);
                final InitialContext context = JNDI.getInitialContext(hints);
                if (hint instanceof Name) {
                    source = context.lookup((Name) hint);
                } else {
                    String name = hint.toString();
                    name = JNDI.fixName(context, name);
                    source = context.lookup(name);
                }
                datasource = (DataSource) source;
            } catch (NoInitialContextException | NameNotFoundException exception) {
                throw new NoSuchFactoryException(Errors.format(Errors.Keys.NO_DATA_SOURCE), exception);
            } catch (NamingException | ClassCastException exception) {
                throw new FactoryException(Errors.format(
                        Errors.Keys.CANT_GET_DATASOURCE_1, hint), exception);
            }
            if (datasource == null) {
                throw new NoSuchFactoryException(Errors.format(Errors.Keys.NO_DATA_SOURCE));
            }
            this.datasource = datasource;
        }
        return datasource;
    }

    /**
     * Creates a default data source, optionally using the given configuration. This method is
     * invoked by {@link #getDataSource()} when no explicit value was provided for
     * {@link Hints#EPSG_DATA_SOURCE}. If a {@linkplain #CONFIGURATION_FILE configuration file}
     * has been found, its content is given as the sole argument to this method. Otherwise the
     * {@code properties} argument is null.
     * <p>
     * The default implementation performs the following steps:
     * <ul>
     *   <li>If the {@code properties} is non-null, then the value associated to the {@code "URL"}
     *       key is taken. Otherwise the {@linkplain #getDefaultURL() default URL} to the embedded
     *       database is used.</li>
     *   <li>A new {@code DataSource} is created, which will use the above URL for fetching a
     *       connection through {@link java.sql.DriverManager#getConnection(String)}.</li>
     * </ul>
     * <p>
     * If the default URL was used and no database exists at that URL, then a new database will
     * be created using the {@link EpsgInstaller} when first needed. This operation is possible
     * only if the {@code geotk-epsg.jar} file is reachable on the classpath, otherwise an
     * exception will be thrown the first time the factory will be used.</p>
     * <p>
     * Subclasses should override this method if they can create a data source from other
     * properties ({@code "serverName"}, {@code "databaseName"}, <i>etc.</i>), or if
     * they can provide a default data source.
     *
     * @param  properties The properties loaded from the configuration file if it was found,
     *         or {@code null} otherwise.
     * @return A data source created from the properties, or {@code null} if this method
     *         can not create a data source.
     * @throws FactoryException if the operation failed for an other reason.
     *
     * @since 3.00
     */
    protected DataSource createDataSource(final Properties properties) throws FactoryException {
        if (properties != null) {
            final String url = properties.getProperty("URL");
            if (url != null) {
                return new DefaultDataSource(url);
            }
        } else {
            // Allow the creation of the database only if the needed scripts are available.
            final boolean create = (ThreadedEpsgFactory.class.getResource("Data.sql") != null);
            final String url;
            try {
                url = getDefaultURL(create);
            } catch (IOException e) {
                throw new FactoryException(e);
            }
            if (create) {
                // Create a data source capable to create the database when first needed.
                return EmbeddedDataSource.instance(url);
            }
            return new DefaultDataSource(url);
        }
        return null;
    }

    /**
     * Creates the backing store for the {@linkplain #getDataSource() current data source}.
     * The default implementation tries to guess the most appropriate subclass of
     * {@link DirectEpsgFactory} from the {@linkplain DatabaseMetaData database metadata}.
     * Subclasses should override this method if they can return an instance tuned for the
     * SQL dialect of the underlying database. Example for a Oracle data source:
     *
     * {@preformat java
     *     protected AbstractAuthorityFactory createBackingStore(Hints hints) throws SQLException {
     *         return new OracleDialectEpsgFactory(hints, getDataSource().getConnection(user, password));
     *     }
     * }
     *
     * @param  hints A map of hints, including the low-level factories to use for CRS creation.
     *         This argument should be given unchanged to {@code DirectEpsgFactory} constructor.
     * @return The {@linkplain DirectEpsgFactory EPSG factory} using SQL dialect appropriate
     *         for this data source.
     * @throws SQLException if the connection to the database failed.
     * @throws FactoryException if the operation failed for an other reason (for example a
     *         failure to {@linkplain #getDataSource() get the data source}).
     *
     * @see #user
     * @see #password
     */
    protected AbstractAuthorityFactory createBackingStore(final Hints hints)
            throws FactoryException, SQLException
    {
        final DataSource source = getDataSource();
        final Connection connection;
        final String user, password, schema;
        final Map<String,String> toANSI;
        synchronized (this) {
            user     = this.user;
            password = this.password;
            schema   = this.schema;
            toANSI   = this.toANSI;
        }
        if (user != null && password != null) {
            connection = source.getConnection(user, password);
        } else {
            connection = source.getConnection();
        }
        connection.setReadOnly(true);
        final DatabaseMetaData metadata = connection.getMetaData();
        final AnsiDialectEpsgFactory factory;
        switch (Dialect.guess(metadata)) {
            /*
             * NOTE: It is better to keep the code below outside synchronized block, if
             * possible, because the creation of those EPSG factories implies fetching
             * a lot of dependencies, which may lead to tricky dead-lock in the factory
             * system if there is too many locks hold.
             */
            case ACCESS: {
                return new DirectEpsgFactory(hints, connection);
            }
            default: // Fallback on ANSI syntax by default.
            case ANSI: {
                if (toANSI != null) {
                    return new AnsiDialectEpsgFactory(hints, connection, toANSI);
                    // The toANSI map already contains the schema, if any.
                }
                factory = new AnsiDialectEpsgFactory(hints, connection);
                break;
            }
            case HSQL: {
                if (toANSI != null) {
                    return new HsqlDialectEpsgFactory(hints, connection, toANSI);
                }
                factory = new HsqlDialectEpsgFactory(hints, connection);
                break;
            }
            case ORACLE: {
                if (toANSI != null) {
                    return new OracleDialectEpsgFactory(hints, connection, toANSI);
                }
                factory = new OracleDialectEpsgFactory(hints, connection);
                break;
            }
        }
        if (schema != null) {
            factory.setSchema(schema, metadata.getIdentifierQuoteString(), true);
        }
        factory.autoconfig(metadata);
        synchronized (this) {
            /*
             * It could happen that the map has been assigned in an other thread. In such case,
             * the content should be identical unless the database metadata changed (which should
             * not occur - this class assumes that the database is stable). If such change happen
             * anyway, keep the most recent map.
             */
            this.toANSI = factory.toANSI;
        }
        return factory;
    }

    /**
     * Creates the backing store authority factory. This method is invoked automatically
     * by {@link ThreadedAuthorityFactory} when a new backing store is required, either
     * because the previous one has been disposed after its timeout or because a new one
     * is required for concurrency.
     * <p>
     * The default implementation invokes {@link #createBackingStore(Hints)} with a map
     * of hints derived from this factory {@linkplain #hints hints}, then logs a message
     * at the {@link Level#CONFIG CONFIG} level. The log message contains the URL to the
     * database.
     *
     * @return The backing store to uses in {@code createXXX(...)} methods.
     * @throws FactoryException if the constructor failed to connect to the EPSG database.
     *         This exception usually has a {@link SQLException} as its cause.
     */
    @Override
    protected AbstractAuthorityFactory createBackingStore() throws FactoryException {
        /*
         * NOTE: This method should not be synchronized, in order to a void dead-lock.
         * Every methods invoked below should be thread-safe. The only things requirying
         * special attention are access to hints and the createBackingStore(Hints) method.
         */
        final boolean isLoggable = LOGGER.isLoggable(Level.INFO);
        String product = null, url = null;
        final AbstractAuthorityFactory factory;
        final Hints sourceHints = EMPTY_HINTS.clone();
        synchronized (this) {
            // See javadoc in super-class method.
            sourceHints.putAll(hints);
        }
        sourceHints.putAll(factories.getImplementationHints());
        try {
            factory = createBackingStore(sourceHints);
            if (isLoggable && factory instanceof DirectEpsgFactory) {
                DatabaseMetaData metadata = ((DirectEpsgFactory) factory).connection.getMetaData();
                product = metadata.getDatabaseProductName();
                url     = metadata.getURL();
            }
        } catch (SQLException exception) {
            final String message = Errors.format(Errors.Keys.CANT_CONNECT_DATABASE_1, "EPSG");
            final String state = exception.getSQLState();
            if ("08001".equals(state) || "XJ004".equals(state)) {
                /*
                 * No suitable driver (08001) or database not found (XJ004).
                 * Throwing a NoSuchFactoryException is significant since
                 * ThreadedAuthorityFactory will use a finer logging level in this case.
                 */
                throw new NoSuchFactoryException(message, exception);
            }
            /*
             * Other kind of error, presumed more serious.  If the SQLException has an other
             * SQLException has its cause, keep only the root cause (it has more informative
             * error message on Derby).
             */
            Throwable cause;
            while ((cause = exception.getCause()) instanceof SQLException) {
                exception = (SQLException) cause;
            }
            throw new FactoryException(message, exception);
        }
        if (isLoggable) {
            if (product == null) {
                product = '<' + Vocabulary.format(Vocabulary.Keys.UNKNOWN) + '>';
            }
            if (url == null) {
                url = product;
            }
            /*
             * Log to the INFO level rather than CONFIG, because experience suggests that this
             * information is really worth to be known to users. Many problems reported on the
             * mailing list are related to whatever the referencing module get a connection to
             * an EPSG database, and which one. It should not pollute the console because this
             * connection is typically fetched only once. Even if the connection is closed and
             * the user continue to request CRS, the cached values will typically be returned.
             */
            final LogRecord record = Loggings.format(Level.INFO,
                    Loggings.Keys.CONNECTED_EPSG_DATABASE_2, url, product);
            record.setSourceClassName(ThreadedEpsgFactory.class.getName());
            record.setSourceMethodName("createBackingStore");
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
        }
        if (factory instanceof DirectEpsgFactory) {
            ((DirectEpsgFactory) factory).buffered = this;
        }
        return factory;
    }

    /**
     * Returns {@code true} if the backing store can be disposed now. This method is invoked
     * automatically after the amount of time specified by {@link #setTimeout} if the factory
     * were not used during that time.
     *
     * @param backingStore The backing store in process of being disposed.
     */
    @Override
    protected boolean canDisposeBackingStore(final AbstractAuthorityFactory backingStore) {
        if (backingStore instanceof DirectEpsgFactory) {
            return ((DirectEpsgFactory) backingStore).canDispose();
        }
        return super.canDisposeBackingStore(backingStore);
    }

    /**
     * Releases resources immediately instead of waiting for the garbage collector.
     * This method disposes all backing stores, which imply closing their connections.
     *
     * @param shutdown {@code false} for normal disposal, or {@code true} if this method is invoked
     *        during the process of a JVM shutdown. In the later case this method may shutdown the
     *        embedded database, if there is one (for example JavaDB).
     */
    @Override
    protected void dispose(final boolean shutdown) {
        super.dispose(shutdown); // Close the connections first.
        synchronized (this) {
            if (shutdown && (datasource instanceof DefaultDataSource)) {
                ((DefaultDataSource) datasource).shutdown();
            }
            schema = user = password = null;
            datasource = null;
        }
    }
}
