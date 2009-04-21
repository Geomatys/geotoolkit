/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
import javax.naming.NameNotFoundException;
import javax.naming.NoInitialContextException;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.referencing.factory.ThreadedAuthorityFactory;
import org.geotoolkit.referencing.factory.NoSuchFactoryException;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.JNDI;
import org.geotoolkit.internal.jdbc.Dialect;
import org.geotoolkit.internal.jdbc.DefaultDataSource;
import org.geotoolkit.internal.io.Installation;


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
 * @version 3.0
 *
 * @since 2.4
 * @module
 */
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
     *   <li>{@code "EPSG/DataSource.properties"} in the Geotoolkit application data directory</li>
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
     * @since 3.0
     */
    public static final String CONFIGURATION_FILE = "EPSG-DataSource.properties";

    /**
     * The factories to be given to the backing store.
     */
    private final ReferencingFactoryContainer factories;

    /**
     * The data source, or {@code null} if the connection has not yet been etablished.
     */
    private DataSource datasource;

    /**
     * Property read from the {@value #CONFIGURATION_FILE} file, or {@code null} if none.
     * {@code schema} is the name of the schema in the database where to look for the tables.
     * {@code user} and {@code password} are the values to be given to
     * {@link DataSource#getConnection(String,String)}, while
     * <p>
     * This property is defined automatically before {@link #createDataSource(Properties)}
     * is invoked, and is used by {@link #createBackingStore(Hints)}. Subclasses can change
     * this value in their {@code createDataSource} implementation.
     *
     * @since 3.0
     */
    protected String schema, user, password;

    /**
     * The map used for adapting the SQL statements to a particular dialect. This map is
     * initialized by {@link AnsiDialectEpsgFactory} and saved here only in order to avoid
     * querying the database metadata everytime a new backing store factory is created.
     */
    private transient Map<String,String> dialect;

    /**
     * Constructs an authority factory using the default set of factories. The instance
     * created by this method will use the connection parameters specified in the
     * {@value #CONFIGURATION_FILE} if such file is found.
     */
    public ThreadedEpsgFactory() {
        this(EMPTY_HINTS);
        hints.put(Hints.EPSG_DATA_SOURCE, null);
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
     * @since 3.0
     */
    public ThreadedEpsgFactory(final DataSource source) {
        this(EMPTY_HINTS);
        ensureNonNull("source", source);
        hints.put(Hints.EPSG_DATA_SOURCE, source);
    }

    /**
     * Constructs an authority factory using a set of factories created from the specified hints.
     * Hints of special interrest are:
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
        setKeyCollisionAllowed(true);
        setTimeout(15000L); // Close the connection after 15 seconds of inactivity.
    }

    /**
     * Returns the authority for this EPSG database.
     * This authority will contains the database version in the {@linkplain Citation#getEdition
     * edition} attribute, together with the {@linkplain Citation#getEditionDate edition date}.
     */
    @Override
    public Citation getAuthority() {
        final Citation authority = super.getAuthority();
        return (authority != null) ? authority : Citations.EPSG;
    }

    /**
     * Returns the the default JDBC URL to use for the connection. This method returns
     * a URL using the JavaDB driver, connecting to the database in the installation
     * directory specified by the setup program in the
     * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module.
     * If this setup program has not been run, then a platform-dependant location relative
     * to the user home directory is returned.
     *
     * @return The default JDBC URL to use for the connection to the EPSG database.
     */
    static String getDefaultURL() {
        final File directory = Installation.EPSG.directory(true);
        return "jdbc:derby:" + directory.getPath().replace(File.pathSeparatorChar, '/');
    }

    /**
     * Loads the {@linkplain #CONFIGURATION_FILE configuration file}, or returns {@code null} if
     * no configuration file can be found. The search path is documented in {@link #getDataSource}.
     *
     * @return The properties, or {@code null} if none wer found.
     * @throws FactoryException if an error occured while reading the properties.
     */
    private static Properties properties() throws FactoryException {
        File file = new File(CONFIGURATION_FILE);
        if (!file.isFile()) {
            file = new File(System.getProperty("user.home", "."), CONFIGURATION_FILE);
            if (!file.isFile()) {
                file = new File(Installation.EPSG.directory(true), "DataSource.properties");
                if (!file.isFile()) {
                    return null;
                }
            }
        }
        final Properties p = new Properties();
        try {
            final InputStream in = new FileInputStream(file);
            p.load(in);
            in.close();
        } catch (IOException exception) {
            throw new FactoryException(Errors.format(Errors.Keys.CANT_READ_$1, file), exception);
        }
        return p;
    }

    /**
     * Returns the data source for the EPSG database. The default implementation performs
     * the following steps:
     * <p>
     * <ul>
     *   <li>If a {@link DataSource} object was given explicitly to the constructor, it is returned.</li>
     *   <li>Otherwise if hint value is associated to the {@link Hints#EPSG_DATA_SOURCE} key,
     *       then there is a choice:
     *   <ul>
     *     <li>If that value is an instance of {@link DataSource}, it is returned.</li>
     *     <li>If that value is an instance of {@link Name}, then a
     *         {@linkplain InitialContext#lookup(Name) JNDI lookup} is performed for that name.</li>
     *     <li>If that value is an instance of {@link String}, then a
     *         {@linkplain InitialContext#lookup(String) JNDI lookup} is performed for that name.</li>
     *   </ul>
     *   <li>Otherwise if at least one of the following files exist, then the first one is used for
     *       etablishing a connection. See {@link #CONFIGURATION_FILE} for more informations.
     *       <ul>
     *         <li>{@code "EPSG-DataSource.properties"} in the current directory</li>
     *         <li>{@code "EPSG-DataSource.properties"} in the user's home directory</li>
     *         <li>{@code "EPSG/DataSource.properties"} in the Geotoolkit application data directory</li>
     *       </ul>
     *   </li>
     *   <li>Otherwise the factory is assumed not available.</li>
     * </ul>
     *
     * @return The data source. Should never be {@code null}.
     * @throws SQLException if the connection to the database failed.
     * @throws FactoryException if the operation failed for an other reason (for example an I/O
     *         error while reading the configuration file, or a failure to lookup the JNDI name).
     */
    protected synchronized DataSource getDataSource() throws FactoryException, SQLException {
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
            } catch (NoInitialContextException exception) { // TODO: Multi-catch with Java 7.
                throw new NoSuchFactoryException(Errors.format(Errors.Keys.NO_DATA_SOURCE), exception);
            } catch (NameNotFoundException exception) {
                throw new NoSuchFactoryException(Errors.format(Errors.Keys.NO_DATA_SOURCE), exception);
            } catch (Exception exception) { // Multi-catch: NamingException, ClassCastException
                throw new FactoryException(Errors.format(
                        Errors.Keys.CANT_GET_DATASOURCE_$1, hint), exception);
            }
            if (datasource == null) {
                throw new NoSuchFactoryException(Errors.format(Errors.Keys.NO_DATA_SOURCE));
            }
            this.datasource = datasource;
        }
        return datasource;
    }

    /**
     * Creates a default data source, optionnaly using the given configuration. This method is
     * invoked by {@link #getDataSource()} when no explicit value was provided for {@link
     * Hints#EPSG_DATA_SOURCE}. If a {@linkplain #CONFIGURATION_FILE configuration file} file
     * has been found, its content is given as the sole argument to this method. Otherwise the
     * {@code properties} argument is null.
     * <p>
     * The default implementation returns a data source backed by the {@code "URL"} property
     * (to be given to {@link java.sql.DriverManager#getConnection(String)}) if this property
     * exists, or return {@code null} otherwise. Subclasses should override this method if
     * they can create a data source from the other properties ({@code "serverName"},
     * {@code "databaseName"}, <cite>etc.</cite>), or if they can provide a default data source.
     *
     * @param  properties The properties loaded from the configuration file if it was found,
     *         or {@code null} otherwise.
     * @return A data source created from the properties, or {@code null} if this method
     *         can not create a data source.
     * @throws SQLException if the connection to the database failed.
     * @throws FactoryException if the operation failed for an other reason.
     *
     * @since 3.0
     */
    protected DataSource createDataSource(final Properties properties)
            throws FactoryException, SQLException
    {
        if (properties != null) {
            final String url = properties.getProperty("URL");
            if (url != null) {
                return new DefaultDataSource(url);
            }
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
     *         return new OracleDialectEpsgFactory(hints, getDataSource().getConnection());
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
     */
    protected AbstractAuthorityFactory createBackingStore(final Hints hints)
            throws FactoryException, SQLException
    {
        final DataSource source = getDataSource();
        final Connection connection;
        if (user != null && password != null) {
            connection = source.getConnection(user, password);
        } else {
            connection = source.getConnection();
        }
        connection.setReadOnly(true);
        final DatabaseMetaData metadata = connection.getMetaData();
        final AnsiDialectEpsgFactory factory;
        switch (Dialect.guess(metadata)) {
            case ACCESS: {
                return new DirectEpsgFactory(hints, connection);
            }
            default: // Fallback on ANSI syntax by default.
            case ANSI: {
                final Map<String,String> dialect = this.dialect;
                if (dialect != null) {
                    return new AnsiDialectEpsgFactory(hints, connection, dialect);
                }
                factory = new AnsiDialectEpsgFactory(hints, connection);
                break;
            }
        }
        if (schema != null) {
            factory.setSchema(schema, true);
        }
        factory.autoconfig(metadata);
        dialect = factory.toANSI;
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
    protected synchronized AbstractAuthorityFactory createBackingStore() throws FactoryException {
        String product = null, url = null;
        final AbstractAuthorityFactory factory;
        final Hints sourceHints = EMPTY_HINTS.clone();
        sourceHints.putAll(hints);
        sourceHints.putAll(factories.getImplementationHints());
        try {
            factory = createBackingStore(sourceHints);
            if (factory instanceof DirectEpsgFactory) {
                DatabaseMetaData metadata = ((DirectEpsgFactory) factory).connection.getMetaData();
                product = metadata.getDatabaseProductName();
                url     = metadata.getURL();
            }
        } catch (SQLException exception) {
            final String message = Errors.format(Errors.Keys.CANT_CONNECT_DATABASE_$1, "EPSG");
            if ("08001".equals(exception.getSQLState())) {
                /*
                 * No suitable driver. Throwing a NoSuchFactoryException is significant since
                 * ThreadedAuthorityFactory will use a finer logging level in this case.
                 */
                throw new NoSuchFactoryException(message, exception);
            }
            /*
             * Other kind of error, presumed more serious.
             */
            throw new FactoryException(message, exception);
        }
        if (product == null) {
            product = '<' + Vocabulary.format(Vocabulary.Keys.UNKNOW) + '>';
        }
        if (url == null) {
            url = product;
        }
        log(Loggings.format(Level.CONFIG, Loggings.Keys.CONNECTED_EPSG_DATABASE_$2, url, product));
        if (factory instanceof DirectEpsgFactory) {
            ((DirectEpsgFactory) factory).buffered = this;
        }
        return factory;
    }

    /**
     * For internal use by {@link #createFactory()} and {@link #createBackingStore()} only.
     */
    private static void log(final LogRecord record) {
        record.setSourceClassName(ThreadedEpsgFactory.class.getName());
        record.setSourceMethodName("createBackingStore"); // The public caller.
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
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
     */
    @Override
    protected synchronized void dispose(final boolean shutdown) {
        schema = user = password = null;
        datasource = null;
        super.dispose(shutdown);
    }
}
