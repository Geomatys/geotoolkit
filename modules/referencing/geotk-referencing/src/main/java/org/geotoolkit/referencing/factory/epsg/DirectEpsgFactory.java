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
package org.geotoolkit.referencing.factory.epsg;

import java.util.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.awt.RenderingHints;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.measure.converter.ConversionException;
import javax.measure.quantity.Length;
import javax.measure.quantity.Angle;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.measure.unit.SI;
import net.jcip.annotations.ThreadSafe;

import org.opengis.parameter.*;
import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.util.NameSpace;
import org.opengis.util.GenericName;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.EvaluationMethodType;
import org.opengis.metadata.quality.PositionalAccuracy;

import org.geotoolkit.factory.Hints;
import org.apache.sis.measure.Units;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.metadata.iso.quality.DefaultQuantitativeResult;
import org.apache.sis.metadata.iso.quality.DefaultAbsoluteExternalPositionalAccuracy;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.metadata.iso.ImmutableIdentifier;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.referencing.factory.DirectAuthorityFactory;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.DefaultSingleOperation;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.operation.DefaultOperationMethod;
import org.geotoolkit.referencing.operation.DefaultConcatenatedOperation;
import org.geotoolkit.internal.referencing.factory.ImplementationHints;
import org.geotoolkit.internal.referencing.DeprecatedCode;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.io.TableWriter;
import org.apache.sis.referencing.cs.CoordinateSystems;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.Version;

import static org.geotoolkit.internal.InternalUtilities.COMPARISON_THRESHOLD;
import static org.geotoolkit.internal.referencing.CRSUtilities.PARAMETERS_KEY;


/**
 * A CRS authority factory backed by the EPSG database tables.
 * The EPSG database is freely available at <A HREF="http://www.epsg.org">http://www.epsg.org</a>.
 * Current version of this class requires EPSG database version 6.6 or above.
 * <p>
 * This factory accepts names as well as numerical identifiers. For example
 * "<cite>NTF (Paris) / France I</cite>" and {@code "27581"} both fetch the same object.
 * However, names may be ambiguous since the same name may be used for more than one object.
 * This is the case of "WGS 84" for example. If such an ambiguity is found, an exception
 * will be thrown. If names are not wanted as a legal EPSG code, subclasses can override the
 * {@link #isPrimaryKey(String)} method.
 * <p>
 * This factory doesn't cache any result. Any call to a {@code createFoo} method will send a new
 * query to the EPSG database. For caching, this factory should be wrapped in some buffered factory
 * like {@link ThreadedEpsgFactory}.
 * <p>
 * Because the primary distribution format for the EPSG database is MS-Access, this class uses
 * SQL statements formatted for the MS-Access syntax. For usage with an other database software,
 * a dialect-specific subclass must be used.
 *
 * @author Yann Cézard (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @author Matthias Basler
 * @author Andrea Aime (TOPP)
 * @author Johann Sorel (Geomatys)
 * @version 3.21
 *
 * @see ThreadedEpsgFactory
 * @see <a href="http://www.geotoolkit.org/modules/referencing/supported-codes.html">List of authority codes</a>
 *
 * @since 1.2
 * @module
 */
@ThreadSafe
@ImplementationHints(forceLongitudeFirst=false)
public class DirectEpsgFactory extends DirectAuthorityFactory implements CRSAuthorityFactory,
        CSAuthorityFactory, DatumAuthorityFactory, CoordinateOperationAuthorityFactory
{
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////                                                                                 ///////
    //////   HARD CODED VALUES (other than SQL statements) RELATIVE TO THE EPSG DATABASE   ///////
    //////                                                                                 ///////
    //////////////////////////////////////////////////////////////////////////////////////////////

    // See org.geotoolkit.measure.Units.valueOfEPSG(int) for hard-code units from EPSG codes.

    /**
     * Sets a Bursa-Wolf parameter from an EPSG parameter.
     *
     * @param  parameters The Bursa-Wolf parameters to modify.
     * @param  code       The EPSG code for a parameter from the [PARAMETER_CODE] column.
     * @param  value      The value of the parameter from the [PARAMETER_VALUE] column.
     * @param  unit       The unit of the parameter value from the [UOM_CODE] column.
     * @throws FactoryException if the code is unrecognized.
     */
    private static void setBursaWolfParameter(final BursaWolfParameters parameters,
            final int code, double value, final Unit<?> unit) throws FactoryException
    {
        Unit<?> target = unit;
        if (code >= 8605) {
            if      (code <= 8607) target = SI   .METRE;
            else if (code <= 8710) target = NonSI.SECOND_ANGLE;
            else if (code == 8611) target = Units.PPM;
        }
        if (target != unit) try {
            value = unit.getConverterToAny(target).convert(value);
        } catch (ConversionException e) {
            throw new FactoryException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
        }
        switch (code) {
            case 8605: parameters.tX = value; break;
            case 8606: parameters.tY = value; break;
            case 8607: parameters.tZ = value; break;
            case 8608: parameters.rX = value; break;
            case 8609: parameters.rY = value; break;
            case 8610: parameters.rZ = value; break;
            case 8611: parameters.dS = value; break;
            default: throw new FactoryException(Errors.format(
                    Errors.Keys.UNEXPECTED_PARAMETER_1, code));
        }
    }
    // Datum shift operation methods
    /** First Bursa-Wolf method.   */ private static final int BURSA_WOLF_MIN_CODE = 9603;
    /**  Last Bursa-Wolf method.   */ private static final int BURSA_WOLF_MAX_CODE = 9607;
    /**   Rotation frame method.   */ private static final int ROTATION_FRAME_CODE = 9607;

    /**
     * List of tables and columns to test for codes values. This table is used by the
     * {@link #createObject} method in order to detect which of the following methods
     * should be invoked for a given code:
     *
     * {@link #createCoordinateReferenceSystem}
     * {@link #createCoordinateSystem}
     * {@link #createDatum}
     * {@link #createEllipsoid}
     * {@link #createUnit}
     *
     * The order is significant: it is the key for a {@code switch} statement.
     *
     * @see #createObject
     * @see #lastObjectType
     */
    private static final TableInfo[] TABLES_INFO = {
        new TableInfo(CoordinateReferenceSystem.class,
                "[Coordinate Reference System]",
                "COORD_REF_SYS_CODE",
                "COORD_REF_SYS_NAME",
                "COORD_REF_SYS_KIND",
                new Class<?>[] { ProjectedCRS.class, GeographicCRS.class, GeocentricCRS.class,
                                 VerticalCRS.class,  CompoundCRS.class,   EngineeringCRS.class},
                new String[]   {"projected",        "geographic",        "geocentric",
                                "vertical",         "compound",          "engineering"}),

        new TableInfo(CoordinateSystem.class,
                "[Coordinate System]",
                "COORD_SYS_CODE",
                "COORD_SYS_NAME",
                "COORD_SYS_TYPE",
                new Class<?>[] { CartesianCS.class, EllipsoidalCS.class, SphericalCS.class, VerticalCS.class},
                new String[]   {"Cartesian",       "ellipsoidal",       "spherical",       "vertical"}),
                               //Really upper-case C.
        new TableInfo(CoordinateSystemAxis.class,
                "[Coordinate Axis] AS CA INNER JOIN [Coordinate Axis Name] AS CAN" +
                                 " ON CA.COORD_AXIS_NAME_CODE=CAN.COORD_AXIS_NAME_CODE",
                "COORD_AXIS_CODE",
                "COORD_AXIS_NAME"),

        new TableInfo(Datum.class,
                "[Datum]",
                "DATUM_CODE",
                "DATUM_NAME",
                "DATUM_TYPE",
                new Class<?>[] { GeodeticDatum.class, VerticalDatum.class, EngineeringDatum.class},
                new String[]   {"geodetic",          "vertical",          "engineering"}),

        new TableInfo(Ellipsoid.class,
                "[Ellipsoid]",
                "ELLIPSOID_CODE",
                "ELLIPSOID_NAME"),

        new TableInfo(PrimeMeridian.class,
                "[Prime Meridian]",
                "PRIME_MERIDIAN_CODE",
                "PRIME_MERIDIAN_NAME"),

        new TableInfo(CoordinateOperation.class,
                "[Coordinate_Operation]",
                "COORD_OP_CODE",
                "COORD_OP_NAME",
                "COORD_OP_TYPE",
                new Class<?>[] { Projection.class, Conversion.class, Transformation.class},
                new String[]   {"conversion",     "conversion",     "transformation"}),
                // Note: Projection is handle in a special way.

        new TableInfo(OperationMethod.class,
                "[Coordinate_Operation Method]",
                "COORD_OP_METHOD_CODE",
                "COORD_OP_METHOD_NAME"),

        new TableInfo(ParameterDescriptor.class,
                "[Coordinate_Operation Parameter]",
                "PARAMETER_CODE",
                "PARAMETER_NAME"),

        new TableInfo(Unit.class,
                "[Unit of Measure]",
                "UOM_CODE",
                "UNIT_OF_MEAS_NAME")
    };

    ///////////////////////////////////////////////////////////////////////////////
    ////////                                                               ////////
    ////////        E N D   O F   H A R D   C O D E D   V A L U E S        ////////
    ////////                                                               ////////
    ////////    NOTE: 'createFoo(...)' methods may still have hard-coded   ////////
    ////////    values (others than SQL statements) in 'equalsIgnoreCase'  ////////
    ////////    expressions.                                               ////////
    ///////////////////////////////////////////////////////////////////////////////




    /**
     * The name for the transformation accuracy metadata.
     */
    private static final InternationalString TRANSFORMATION_ACCURACY =
            Vocabulary.formatInternational(Vocabulary.Keys.TRANSFORMATION_ACCURACY);

    /**
     * The authority for this database. Will be created only when first needed. This authority
     * will contains the database version in the {@linkplain Citation#getEdition edition}
     * attribute, together with the {@linkplain Citation#getEditionDate edition date}.
     */
    private Citation authority;

    /**
     * Last object type returned by {@link #createObject}, or -1 if none.
     * This type is an index in the {@link #TABLES_INFO} array and is
     * strictly for {@link #createObject} internal use.
     */
    private int lastObjectType = -1;

    /**
     * The last table in which object name were looked for.
     * This is for internal use by {@link #toPrimaryKey} only.
     */
    private String lastTableForName;

    /**
     * The calendar instance for creating {@link java.util.Date} objects from a year
     * (the "epoch" in datum definition). We use the local timezone, which may not be
     * quite accurate. But there is no obvious timezone for "epoch", and the "epoch"
     * is approximative anyway.
     */
    private Calendar calendar;

    /**
     * The object to use for parsing dates, created when first needed. This is used for
     * parsing the origin of temporal datum. This is Geotk-specific extension.
     */
    private DateFormat dateFormat;

    /**
     * A pool of prepared statements. Key are {@link String} objects related to their
     * originating method name (for example "Ellipsoid" for {@link #createEllipsoid},
     * while values are {@link PreparedStatement} objects.
     * <p>
     * <strong>Note:</strong> It is okay to use {@link IdentityHashMap} instead of {@link HashMap}
     * because the keys will always be the exact same object, namely the hard-coded argument given
     * to calls to {@link #prepareStatement} in this class.
     */
    private final Map<String,PreparedStatement> statements = new IdentityHashMap<>();

    /**
     * The set of authority codes for different types. This map is used by the
     * {@link #getAuthorityCodes} method as a cache for returning the set created
     * in a previous call.
     * <p>
     * Note that this {@code DirectEpsgFactory} can not be disposed as long as this map is not
     * empty, since {@link AuthorityCodes} caches some SQL statements and consequently require
     * the {@linkplain #connection} to be open. This is why we use soft references rather than
     * hard ones, in order to know when no {@link AuthorityCodes} are still in use.
     * <p>
     * The {@link AuthorityCodes#finalize} methods takes care of closing the statements used by
     * the sets. The {@link AuthorityCodes} reference in this map is then cleared by the garbage
     * collector. The {@link #canDispose} method checks if there is any remaining live reference
     * in this map, and returns {@code false} if some are found (thus blocking the call to
     * {@link #dispose} by the {@link ThreadedEpsgFactory} timer).
     */
    private Map<Class<?>, Reference<AuthorityCodes>> authorityCodes;

    /**
     * Cache for axis names. This service is not provided by {@link CachingAuthorityFactory}
     * since {@link AxisName} objects are particular to the EPSG database.
     *
     * @see #getAxisName(String)
     */
    private Map<String,AxisName> axisNames;

    /**
     * Cache for axis numbers. This service is not provided by {@link CachingAuthorityFactory}
     * since the number of axis is used internally in this class.
     *
     * @see #getDimensionForCS(String)
     */
    private Map<String,Integer> axisCounts;

    /**
     * Cache for projection checks. This service is not provided by {@link CachingAuthorityFactory}
     * since the check that a transformation is a projection is used internally in this class.
     *
     * @see #isProjection(String)
     */
    private Map<String,Boolean> codeProjection;

    /**
     * Cache the positional accuracies. Most coordinate operation use a small
     * set of accuracy values.
     *
     * @see #getAccuracy(double)
     */
    private Map<Double,PositionalAccuracy> accuracies;

    /**
     * Pool of naming systems, used for caching.
     * There is usually few of them (about 15).
     *
     * @see #createProperties
     */
    private final Map<String,NameSpace> scopes = new HashMap<>();

    /**
     * The properties to be given the objects to construct.
     * Reused every time {@link #createProperties} is invoked.
     */
    private final Map<String,Object> properties = new HashMap<>();

    /**
     * A safety guard for preventing never-ending loops in recursive calls to
     * {@link #createDatum}. This is used by {@link #createBursaWolfParameters},
     * which need to create a target datum. The target datum could have its own
     * Bursa-Wolf parameters, with one of them pointing again to the source datum.
     */
    private final Set<String> safetyGuard = new HashSet<>();

    /**
     * The buffered authority factory, or {@code this} if none. This field is set
     * to a different value by {@link ThreadedEpsgFactory} only, which will point toward a
     * buffered factory wrapping this {@code DirectEpsgFactory} for efficiency.
     */
    AbstractAuthorityFactory buffered = this;

    /**
     * The connection to the EPSG database. This connection is specified at
     * {@linkplain #DirectEpsgFactory(Hints, Connection) construction time}
     * and closed by the {@link #dispose(boolean)} method, or when this
     * {@code DirectEpsgFactory} instance if garbage collected.
     */
    protected final Connection connection;

    /**
     * Creates a factory using the given connection. The connection is
     * {@linkplain Connection#close() closed} when this factory is
     * {@linkplain #dispose(boolean) disposed}.
     * <p>
     * <b>Note:</b> we recommend to avoid keeping the connection open for a long time. An easy
     * way to get the connection created only when first needed and closed automatically after
     * a short timeout is to instantiate this {@code DirectEpsgFactory} class only in a
     * {@link org.geotoolkit.referencing.factory.ThreadedAuthorityFactory}. This approach also
     * gives concurrency and caching services in bonus.
     *
     * @param userHints The underlying factories used for objects creation,
     *        or {@code null} for the default ones.
     * @param connection The connection to the underlying EPSG database.
     */
    public DirectEpsgFactory(final Hints userHints, final Connection connection) {
        super(userHints);
        // The following hints have no effect on this class behaviour,
        // but tell to the user what this factory do about axis order.
        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE);
        hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS,   Boolean.FALSE);
        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS,        Boolean.FALSE);
        this.connection = connection;
        ensureNonNull("connection", connection);
    }

    /**
     * Returns the authority for this EPSG database. This authority will contains the database
     * version in the {@linkplain Citation#getEdition edition} attribute, together with the
     * {@linkplain Citation#getEditionDate edition date}.
     */
    @Override
    public synchronized Citation getAuthority() {
        if (authority == null) {
            createAuthority();
        }
        return authority;
    }

    /**
     * Creates the authority for this EPSG database. The result is stored in the
     * {@link #authority} field.
     */
    private void createAuthority() {
        assert Thread.holdsLock(this);
        try {
            final String query = adaptSQL(
                    "SELECT VERSION_NUMBER, VERSION_DATE FROM [Version History]" +
                    " ORDER BY VERSION_DATE DESC, VERSION_HISTORY_CODE DESC");
            final DatabaseMetaData metadata  = connection.getMetaData();
            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(query))
            {
                if (result.next()) {
                    final String version = result.getString(1);
                    final Date   date    = result.getDate  (2);
                    final String engine  = metadata.getDatabaseProductName();
                    final DefaultCitation c = new DefaultCitation(Citations.EPSG);
                    c.getAlternateTitles().add(Vocabulary.formatInternational(
                            Vocabulary.Keys.DATA_BASE_3, "EPSG", version, engine));
                    c.setEdition(new SimpleInternationalString(version));
                    c.setEditionDate(date);
                    c.freeze();
                    authority = c;
                    hints.put(Hints.VERSION, new Version(version));  // For getImplementationHints()
                } else {
                    authority = Citations.EPSG;
                }
            }
        } catch (SQLException exception) {
            Logging.unexpectedException(LOGGER, DirectEpsgFactory.class, "getAuthority", exception);
            authority = Citations.EPSG;
        }
    }

    /**
     * Returns a description of the database engine.
     *
     * @throws FactoryException if the database's metadata can't be fetched.
     */
    @Override
    public synchronized String getBackingStoreDescription() throws FactoryException {
        final Citation    authority = getAuthority();
        final TableWriter table     = new TableWriter(null, " ");
        final Vocabulary  resources = Vocabulary.getResources(null);
        CharSequence cs;
        if ((cs = authority.getEdition()) != null) {
            table.write(resources.getString(Vocabulary.Keys.VERSION_OF_1, "EPSG"));
            table.write(':');
            table.nextColumn();
            table.write(cs.toString());
            table.nextLine();
        }
        try {
            String s;
            final DatabaseMetaData metadata = connection.getMetaData();
            if ((s = metadata.getDatabaseProductName()) != null) {
                table.write(resources.getLabel(Vocabulary.Keys.DATABASE_ENGINE));
                table.nextColumn();
                table.write(s);
                if ((s = metadata.getDatabaseProductVersion()) != null) {
                    table.write(' ');
                    table.write(resources.getString(Vocabulary.Keys.VERSION_1, s));
                }
                table.nextLine();
            }
            if ((s = metadata.getURL()) != null) {
                table.write(resources.getLabel(Vocabulary.Keys.DATABASE_URL));
                table.nextColumn();
                table.write(s);
                table.nextLine();
            }
        } catch (SQLException exception) {
            throw new FactoryException(exception);
        }
        return table.toString();
    }

    /**
     * Returns the implementation hints for this factory. The returned map contains all the
     * values specified in {@linkplain DirectAuthorityFactory#getImplementationHints subclass},
     * with the addition of {@link Hints#VERSION VERSION}.
     */
    @Override
    public synchronized Map<RenderingHints.Key,?> getImplementationHints() {
        if (authority == null) {
            createAuthority(); // For the computation of Hints.VERSION.
        }
        return super.getImplementationHints();
    }

    /**
     * Returns the set of authority codes of the given type.
     * <p>
     * <strong>NOTE:</strong> This method returns a living connection to the underlying database.
     * This means that the returned set can executes efficiently idioms like the following one:
     *
     * {@preformat java
     *     getAuthorityCodes(type).containsAll(others)
     * }
     *
     * But do not keep the returned reference for a long time. The returned set should stay valid
     * even if retained for a long time (as long as this factory has not been {@linkplain #dispose
     * disposed}), but the existence of those long-living connections may prevent this factory to
     * release some resources. If the set of codes is needed for a long time, copy their values in
     * an other collection object.
     *
     * @param  type The spatial reference objects type (may be {@code Object.class}).
     * @return The set of authority codes for spatial reference objects of the given type.
     *         If this factory doesn't contains any object of the given type, then this method
     *         returns an {@linkplain java.util.Collections#EMPTY_SET empty set}.
     * @throws FactoryException if access to the underlying database failed.
     */
    @Override
    public Set<String> getAuthorityCodes(final Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        return getAuthorityCodes0(type);
    }

    /**
     * Implementation of {@link #getAuthorityCodes} as a private method, for protecting
     * {@link #getDescriptionText} from user overriding of {@link #getAuthorityCodes}.
     */
    private synchronized Set<String> getAuthorityCodes0(final Class<?> type) throws FactoryException {
        if (authorityCodes == null) {
            authorityCodes = new HashMap<>();
        }
        /*
         * If the set were already requested previously for the given type, returns it.
         * Otherwise, a new one will be created (but will not use the database connection yet).
         */
        Reference<AuthorityCodes> reference = authorityCodes.get(type);
        AuthorityCodes candidate = (reference != null) ? reference.get() : null;
        if (candidate != null) {
            return candidate;
        }
        Set<String> result = Collections.emptySet();
        for (final TableInfo table : TABLES_INFO) {
            /*
             * We test 'isAssignableFrom' in the two ways, which may seems strange but
             * intends to catch the following use cases:
             *
             *  - table.type.isAssignableFrom(type)
             *    is for the case where a table is for CoordinateReferenceSystem while the user
             *    type is some subtype like GeographicCRS. The GeographicCRS need to be queried
             *    into the CoordinateReferenceSystem table. An additional filter will be applied
             *    inside the AuthorityCodes class implementation.
             *
             *  - type.isAssignableFrom(table.type)
             *    is for the case where the user type is IdentifiedObject or Object, in which
             *    case we basically want to iterate through every tables.
             */
            if (table.type.isAssignableFrom(type) || type.isAssignableFrom(table.type)) {
                /*
                 * Maybe an instance already existed but was not found above because the user
                 * specified some implementation class instead of an interface class. Before
                 * to return the newly created set, check again in the cached sets using the
                 * type computed by AuthorityCodes itself.
                 */
                final AuthorityCodes codes;
                codes = new AuthorityCodes(connection, table, type, this);
                reference = authorityCodes.get(codes.type);
                candidate = (reference != null) ? reference.get() : null;
                if (candidate == null) {
                    candidate = codes;
                    reference = new WeakReference<>(candidate);
                    authorityCodes.put(codes.type, reference);
                } else {
                    // We will reuse the existing 'candidate' instead of the newly created 'codes'.
                    assert candidate.sqlAll.equals(codes.sqlAll) : codes.type;
                }
                /*
                 * We now have the codes for a single type.  Append with the codes of previous
                 * types, if any. This usually happen only if the user asked for the Object or
                 * IdentifiedObject type.
                 */
                if (result.isEmpty()) {
                    result = candidate;
                } else {
                    if (result instanceof AuthorityCodes) {
                        result = new LinkedHashSet<>(result);
                    }
                    result.addAll(candidate);
                }
            }
        }
        return result;
    }

    /**
     * Gets a description of the object corresponding to a code.
     *
     * @param  code Value allocated by authority.
     * @return A description of the object, or {@code null} if the object
     *         corresponding to the specified {@code code} has no description.
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     * @throws FactoryException if the query failed for some other reason.
     */
    @Override
    public InternationalString getDescriptionText(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        final String primaryKey = trimAuthority(code);
        for (int i=0; i<TABLES_INFO.length; i++) {
            final Set<String> codes = getAuthorityCodes0(TABLES_INFO[i].type);
            if (codes instanceof AuthorityCodes) {
                final String text = ((AuthorityCodes) codes).asMap().get(primaryKey);
                if (text != null) {
                    return new SimpleInternationalString(text);
                }
            }
        }
        /*
         * Maybe the user overridden some object creation
         * methods with a value for the supplied code.
         */
        final Identifier identifier = createObject(code).getName();
        if (identifier instanceof GenericName) {
            return ((GenericName) identifier).toInternationalString();
        }
        return new SimpleInternationalString(identifier.getCode());
    }

    /**
     * Returns a prepared statement for the specified name. Most {@link PreparedStatement}
     * creations are performed through this method, except {@link #getNumericalIdentifier}
     * and {@link #createObject}.
     *
     * @param  key A key uniquely identifying the caller
     *         (e.g. {@code "Ellipsoid"} for {@link #createEllipsoid}).
     * @param  sql The SQL statement to use if for creating the {@link PreparedStatement}
     *         object. Will be used only if no prepared statement was already created for
     *         the specified key.
     * @return The prepared statement.
     * @throws SQLException if the prepared statement can't be created.
     */
    private PreparedStatement prepareStatement(final String key, final String sql)
            throws SQLException
    {
        assert Thread.holdsLock(this);
        PreparedStatement stmt = statements.get(key);
        if (stmt == null) {
            stmt = connection.prepareStatement(adaptSQL(sql));
            statements.put(key, stmt);
        }
        // Partial check that the statement is for the right SQL query.
        assert stmt.getParameterMetaData().getParameterCount() == CharSequences.count(sql, '?');
        return stmt;
    }

    /**
     * Gets the string from the specified {@link ResultSet}. The string is required
     * to be non-null. A null string will throw an exception.
     *
     * @param  result The result set to fetch value from.
     * @param  columnIndex The column index (1-based).
     * @param  code The identifier of the record where the string was found.
     * @return The string at the specified column.
     * @throws SQLException if a SQL error occurred.
     * @throws FactoryException If a null value was found.
     */
    private static String getString(final ResultSet result, final int columnIndex, final Object code)
            throws SQLException, FactoryException
    {
        final String value = result.getString(columnIndex);
        ensureNonNull(result, columnIndex, code);
        return value.trim();
    }

    /**
     * Same as {@link #getString(ResultSet,int,String)}, but report the fault on an alternative
     * column if the value is null.
     */
    private static String getString(final ResultSet result, final int columnIndex,
                                    final String    code,   final int columnFault)
            throws SQLException, FactoryException
    {
        final String str = result.getString(columnIndex);
        if (result.wasNull()) {
            final ResultSetMetaData metadata = result.getMetaData();
            final String column = metadata.getColumnName(columnFault);
            final String table  = metadata.getTableName (columnFault);
            result.close();
            throw new FactoryException(Errors.format(
                    Errors.Keys.NULL_VALUE_IN_TABLE_3, code, column, table));
        }
        return str.trim();
    }

    /**
     * Gets the value from the specified {@link ResultSet}. The value is required
     * to be non-null. A null value (i.e. blank) will throw an exception.
     *
     * @param  result The result set to fetch value from.
     * @param  columnIndex The column index (1-based).
     * @param  code The identifier of the record where the string was found.
     * @return The double at the specified column.
     * @throws SQLException if a SQL error occurred.
     * @throws FactoryException If a null value was found.
     */
    private static double getDouble(final ResultSet result, final int columnIndex, final Object code)
            throws SQLException, FactoryException
    {
        final double value = result.getDouble(columnIndex);
        ensureNonNull(result, columnIndex, code);
        return value;
    }

    /**
     * Gets the value from the specified {@link ResultSet}. The value is required
     * to be non-null. A null value (i.e. blank) will throw an exception.
     *
     * @param  result The result set to fetch value from.
     * @param  columnIndex The column index (1-based).
     * @param  code The identifier of the record where the string was found.
     * @return The integer at the specified column.
     * @throws SQLException if a SQL error occurred.
     * @throws FactoryException If a null value was found.
     */
    private static int getInt(final ResultSet result, final int columnIndex, final Object code)
            throws SQLException, FactoryException
    {
        final int value = result.getInt(columnIndex);
        ensureNonNull(result, columnIndex, code);
        return value;
    }

    /**
     * Makes sure that the last result was non-null. Used for {@code getString}, {@code getDouble}
     * and {@code getInt} methods only.
     */
    private static void ensureNonNull(final ResultSet result, final int columnIndex, final Object code)
            throws SQLException, FactoryException
    {
        if (result.wasNull()) {
            final ResultSetMetaData metadata = result.getMetaData();
            final String column = metadata.getColumnName(columnIndex);
            final String table  = metadata.getTableName (columnIndex);
            result.close();
            throw new FactoryException(Errors.format(
                    Errors.Keys.NULL_VALUE_IN_TABLE_3, code, column, table));
        }
    }

    /**
     * Sets the value of the primary key to search for, and executes the given prepared statement.
     * The primary key should be the value returned by {@link #toPrimaryKey}. Its values is assigned
     * to the parameter #1.
     *
     * @param  stmt The prepared statement in which to set the primary key.
     * @param  primaryKey The primary key.
     * @throws NoSuchIdentifierException If the primary key has not been found.
     * @throws SQLException If an error occurred while querying the database.
     */
    static ResultSet executeQuery(final PreparedStatement stmt, final String primaryKey)
            throws NoSuchIdentifierException, SQLException
    {
        final int n;
        try {
            n = Integer.parseInt(primaryKey);
        } catch (NumberFormatException e) {
            final NoSuchIdentifierException ne = new NoSuchIdentifierException(
                    Errors.format(Errors.Keys.ILLEGAL_IDENTIFIER_1, primaryKey), primaryKey);
            ne.initCause(e);
            throw ne;
        }
        stmt.setInt(1, n);
        return stmt.executeQuery();
    }

    /**
     * Sets the value of the primary keys to search for, and executes the given prepared statement.
     * The primary keys should be the values returned by {@link #toPrimaryKey}. Their values are
     * assigned to parameters #1 and 2.
     *
     * @param  stmt The prepared statement in which to set the primary key.
     * @param  primaryKey The primary key.
     * @throws SQLException If an error occurred.
     */
    private static ResultSet executeQuery(final PreparedStatement stmt, final String key1, final String key2)
            throws NoSuchIdentifierException, SQLException
    {
        final int n;
        try {
            n = Integer.parseInt(key2);
        } catch (NumberFormatException e) {
            final NoSuchIdentifierException ne = new NoSuchIdentifierException(
                    Errors.format(Errors.Keys.ILLEGAL_IDENTIFIER_1, key2), key2);
            ne.initCause(e);
            throw ne;
        }
        stmt.setInt(2, n);
        return executeQuery(stmt, key1);
    }

    /**
     * Converts a code from an arbitrary name to the numerical identifier (the primary key).
     * If the supplied code is already a numerical value, then it is returned unchanged.
     * If the code is not found in the name column, it is returned unchanged as well so that
     * the caller will produces an appropriate "Code not found" error message. If the code
     * is found more than once, then an exception is thrown.
     * <p>
     * Note that this method includes a call to {@link #trimAuthority}, so there is no need to
     * call it before or after this method.
     *
     * @param  type       The type of object to create.
     * @param  code       The code to check.
     * @param  table      The table where the code should appears.
     * @param  codeColumn The column name for the code.
     * @param  nameColumn The column name for the name.
     * @return The numerical identifier (i.e. the table primary key value).
     * @throws SQLException if an error occurred while reading the database.
     */
    private String toPrimaryKey(final Class<?> type, final String code, final String table,
            final String codeColumn, final String nameColumn) throws SQLException, FactoryException
    {
        assert Thread.holdsLock(this);
        String identifier = trimAuthority(code);
        if (!isPrimaryKey(identifier)) {
            /*
             * The character is not the numerical code. Search the value in the database.
             * If a prepared statement is already available, reuse it providing that it was
             * created for the current table. Otherwise, we will create a new statement.
             */
            final String KEY = "NumericalIdentifier";
            PreparedStatement statement = statements.get(KEY);
            if (statement != null) {
                if (!table.equals(lastTableForName)) {
                    statements.remove(KEY);
                    statement.close();
                    statement        = null;
                    lastTableForName = null;
                }
            }
            if (statement == null) {
                final String query = "SELECT " + codeColumn + " FROM " + table +
                                     " WHERE " + nameColumn + " = ?";
                statement = connection.prepareStatement(adaptSQL(query));
                statements.put(KEY, statement);
            }
            // Don't use executeQuery(statement, primaryKey) because "identifier" is a name here.
            statement.setString(1, identifier);
            identifier = null;
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    identifier = ensureSingleton(result.getString(1), identifier, code);
                }
            }
            if (identifier == null) {
                throw noSuchAuthorityCode(type, code);
            }
        }
        return identifier;
    }

    /**
     * Makes sure that an object constructed from the database is not incoherent.
     * If the code supplied to a {@code createFoo} method exists in the database,
     * then we should find only one record. However, we will do a paranoiac check and
     * verify if there is more records, using a {@code while (results.next())}
     * loop instead of {@code if (results.next())}. This method is invoked in
     * the loop for making sure that, if there is more than one record (which should
     * never happen), at least they have identical contents.
     *
     * @param  newValue The newly constructed object.
     * @param  oldValue The object previously constructed, or {@code null} if none.
     * @param  code The EPSG code (for formatting error message).
     * @throws FactoryException if a duplication has been detected.
     */
    private static <T> T ensureSingleton(final T newValue, final T oldValue, final String code)
            throws FactoryException
    {
        if (oldValue == null) {
            return newValue;
        }
        if (oldValue.equals(newValue)) {
            return oldValue;
        }
        throw new FactoryException(Errors.format(Errors.Keys.DUPLICATED_VALUES_FOR_KEY_1, code));
    }

    /**
     * Returns the name for the {@link IdentifiedObject} to construct.
     * This method also search for alias.
     *
     * @param  table      The table on which a query has been executed.
     * @param  name       The name for the {@link IndentifiedObject} to construct.
     * @param  code       The EPSG code of the object to construct.
     * @param  remarks    Remarks, or {@code null} if none.
     * @param  deprecated {@code true} if the object to create is deprecated.
     * @return The name together with a set of properties.
     */
    private Map<String,Object> createProperties(final String table, final String name, String code,
            String remarks, final boolean deprecated) throws SQLException, FactoryException
    {
        properties.clear();
        final Citation authority = getAuthority();
        if (name != null) {
            properties.put(IdentifiedObject.NAME_KEY,
                    new NamedIdentifier(authority, name.trim()));
        }
        if (code != null) {
            code = code.trim();
            final InternationalString edition = authority.getEdition();
            final String version = (edition!=null) ? edition.toString() : null;
            final ImmutableIdentifier identifier;
            if (deprecated) {
                identifier = new DeprecatedCode(authority, "EPSG", code, version, null);
            } else {
                identifier = new ImmutableIdentifier(authority, "EPSG", code, version, null);
            }
            properties.put(IdentifiedObject.IDENTIFIERS_KEY, identifier);
        }
        if (remarks != null && !(remarks = remarks.trim()).isEmpty()) {
            properties.put(IdentifiedObject.REMARKS_KEY, remarks);
        }
        /*
         * Search for alias.
         */
        List<GenericName> alias = null;
        final PreparedStatement stmt;
        stmt = prepareStatement(
                "[Alias]", "SELECT NAMING_SYSTEM_NAME, ALIAS, OBJECT_TABLE_NAME" +
                " FROM [Alias] INNER JOIN [Naming System]" +
                  " ON [Alias].NAMING_SYSTEM_CODE =" +
                " [Naming System].NAMING_SYSTEM_CODE" +
                " WHERE OBJECT_CODE = ?");
        try (ResultSet result = executeQuery(stmt, code)) {
            while (result.next()) {
                String owner = result.getString(3);
                if (owner != null) {
                    /*
                     * We have found an alias for a object having the ID we are looking for,
                     * but we need to check if it is really from the same table since a few
                     * different tables have objects with the same ID.
                     */
                    if (owner.startsWith(AnsiDialectEpsgFactory.TABLE_PREFIX)) {
                        owner = owner.substring(AnsiDialectEpsgFactory.TABLE_PREFIX.length());
                    }
                    if (!CharSequences.isAcronymForWords(owner, table)) {
                        continue;
                    }
                }
                final String scope = result.getString(1);
                final String local = getString(result, 2, code);
                final GenericName generic;
                if (scope == null) {
                    generic = nameFactory.createLocalName(null, local);
                } else {
                    NameSpace cached = scopes.get(scope);
                    if (cached == null) {
                        cached = nameFactory.createNameSpace(
                                 nameFactory.createLocalName(null, scope),
                                 Collections.singletonMap("separator", ":"));
                        scopes.put(scope, cached);
                    }
                    generic = nameFactory.createLocalName(cached, local);
                }
                if (alias == null) {
                    alias = new ArrayList<>();
                }
                alias.add(generic);
            }
        }
        if (alias != null) {
            properties.put(IdentifiedObject.ALIAS_KEY,
                    alias.toArray(new GenericName[alias.size()]));
        }
        return properties;
    }

    /**
     * Returns the name for the {@link IdentifiedObject} to construct.
     * This method also search for alias.
     *
     * @param  table      The table on which a query has been executed.
     * @param  name       The name for the {@link IndentifiedObject} to construct.
     * @param  code       The EPSG code of the object to construct.
     * @param  area       The area of use, or {@code null} if none.
     * @param  scope      The scope, or {@code null} if none.
     * @param  remarks    Remarks, or {@code null} if none.
     * @param  deprecated {@code true} if the object to create is deprecated.
     * @return The name together with a set of properties.
     */
    private Map<String,Object> createProperties(final String table, final String name, final String code,
            String area, String scope, String remarks, final boolean deprecated) throws SQLException, FactoryException
    {
        final Map<String,Object> properties = createProperties(table, name, code, remarks, deprecated);
        if (area != null  &&  !(area = area.trim()).isEmpty()) {
            final Extent extent = buffered.createExtent(area);
            properties.put(Datum.DOMAIN_OF_VALIDITY_KEY, extent);
        }
        if (scope != null &&  !(scope = scope.trim()).isEmpty()) {
            properties.put(Datum.SCOPE_KEY, scope);
        }
        return properties;
    }

    /**
     * Returns an arbitrary object from a code. The default implementation invokes one of
     * {@link #createCoordinateReferenceSystem createCoordinateReferenceSystem},
     * {@link #createCoordinateSystem createCoordinateSystem}, {@link #createDatum createDatum},
     * {@link #createEllipsoid createEllipsoid}, or {@link #createUnit createUnit} methods
     * according the object type.
     *
     * @param  code The EPSG value.
     * @return The object.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized IdentifiedObject createObject(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        final String      KEY   = "IdentifiedObject";
        PreparedStatement stmt  = statements.get(KEY); // Null allowed.
        StringBuilder     query = null; // Will be created only if the last statement doesn't suit.
        /*
         * Iterates through all tables listed in TABLES_INFO, starting with the table used during
         * the last call to 'createObject(code)'.  This approach assumes that two consecutive calls
         * will often return the same type of object.  If the object type changed, then this method
         * will have to discard the old prepared statement and prepare a new one, which may be a
         * costly operation. Only the last successful prepared statement is cached, in order to keep
         * the amount of statements low. Unsuccessful statements are immediately disposed.
         */
        final String  epsg         = trimAuthority(code);
        final boolean isPrimaryKey = isPrimaryKey(epsg);
        final int     tupleToSkip  = isPrimaryKey ? lastObjectType : -1;
        int index = -1;
        for (int i=-1; i<TABLES_INFO.length; i++) {
            if (i == tupleToSkip) {
                // Avoid to test the same table twice.  Note that this test also avoid a
                // NullPointerException if 'stmt' is null, since 'lastObjectType' should
                // be -1 in this case.
                continue;
            }
            try {
                if (i >= 0) {
                    final TableInfo table = TABLES_INFO[i];
                    final String column = isPrimaryKey ? table.codeColumn : table.nameColumn;
                    if (column == null) {
                        continue;
                    }
                    if (query == null) {
                        query = new StringBuilder("SELECT ");
                    }
                    query.setLength(7); // 7 is the length of "SELECT " in the line above.
                    query.append(table.codeColumn).append(" FROM ").append(table.table)
                         .append(" WHERE ").append(column).append(" = ?");
                    if (isPrimaryKey) {
                        assert !statements.containsKey(KEY) : table;
                        stmt = prepareStatement(KEY, query.toString());
                    } else {
                        // Do not cache the statement for names.
                        stmt = connection.prepareStatement(adaptSQL(query.toString()));
                    }
                }
                /*
                 * Checks if at least one record is found for the code. If the code is the primary
                 * key, then we will stop at the first table found since the EPSG database contains
                 * few duplicate identifiers (actually it still have some, but we assume that the
                 * risk of collision is low). If the code is a name, then we need to search in all
                 * tables since duplicate names exist.
                 */
                final ResultSet result;
                if (isPrimaryKey) {
                    result = executeQuery(stmt, epsg);
                } else {
                    stmt.setString(1, epsg);
                    result = stmt.executeQuery();
                }
                final boolean present;
                try {
                    present = result.next();
                } finally {
                    result.close();
                }
                if (present) {
                    if (index >= 0) {
                        throw new FactoryException(Errors.format(Errors.Keys.DUPLICATED_VALUES_FOR_KEY_1, code));
                    }
                    index = (i < 0) ? lastObjectType : i;
                    if (isPrimaryKey) {
                        // Don't scan other tables, since primary keys should be unique.
                        // Note that names may be duplicated, so we don't stop for names.
                        break;
                    }
                }
                if (isPrimaryKey) {
                    if (statements.remove(KEY) == null) {
                        throw new AssertionError(code); // Should never happen.
                    }
                }
                stmt.close();
            } catch (SQLException exception) {
                throw databaseFailure(IdentifiedObject.class, code, exception);
            }
        }
        /*
         * If a record has been found in one table, then delegates to the appropriate method.
         */
        if (isPrimaryKey) {
            lastObjectType = index;
        }
        if (index >= 0) {
            switch (index) {
                case 0:  return createCoordinateReferenceSystem(code);
                case 1:  return createCoordinateSystem         (code);
                case 2:  return createCoordinateSystemAxis     (code);
                case 3:  return createDatum                    (code);
                case 4:  return createEllipsoid                (code);
                case 5:  return createPrimeMeridian            (code);
                case 6:  return createCoordinateOperation      (code);
                case 7:  return createOperationMethod          (code);
                case 8:  return createParameterDescriptor      (code);
                case 9:  break; // Can't cast Unit to IdentifiedObject
                default: throw new AssertionError(index); // Should not happen
            }
        }
        return super.createObject(code);
    }

    /**
     * Returns an unit from a code.
     *
     * @param  code Value allocated by authority.
     * @return The unit object.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized Unit<?> createUnit(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        Unit<?> returnValue = null;
        try {
            final String primaryKey = toPrimaryKey(Unit.class, code,
                    "[Unit of Measure]", "UOM_CODE", "UNIT_OF_MEAS_NAME");
            final PreparedStatement stmt;
            stmt = prepareStatement("[Unit of Measure]",
                    "SELECT UOM_CODE," +
                          " FACTOR_B," +
                          " FACTOR_C," +
                          " TARGET_UOM_CODE," +
                          " UNIT_OF_MEAS_NAME" +
                    " FROM [Unit of Measure]" +
                    " WHERE UOM_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final int source = getInt(result,   1, code);
                    final double   b = result.getDouble(2);
                    final double   c = result.getDouble(3);
                    final int target = getInt(result,   4, code);
                    final Unit<?> base = Units.valueOfEPSG(target);
                    /*
                     * If the unit is a base unit, then b==1, c==1 and source == target. Most
                     * standard base units are hard-coded in the Units.valueOfEPSG(int) method.
                     * However we allow the user to define his own base units.
                     */
                    if (source == target) {
                        if (b != 1 || c != 1) {
                            throw new FactoryException(Errors.format(Errors.Keys.INCONSISTENT_VALUE));
                        }
                    } else if (base == null) {
                        throw noSuchAuthorityCode(Unit.class, String.valueOf(target));
                    }
                    Unit<?> unit = Units.valueOfEPSG(source);
                    if (unit != null) {
                        // TODO: check unit consistency here.
                    } else if (base == null) {
                        // Unit symbol is self describing, rely on JSR-275 to parse unit.
                        final String name = result.getString(5);
                        try {
                            unit = Units.valueOf(name);
                        } catch (IllegalArgumentException e) {
                            // TODO: this error message is not quite accurate...
                            throw new FactoryException(Errors.format(Errors.Keys.UNKNOWN_UNIT_1, code), e);
                        }
                    } else if (b != 0 && c != 0) {
                        unit = Units.multiply(base, b/c);
                    } else {
                        throw new FactoryException(Errors.format(Errors.Keys.UNKNOWN_UNIT_1, code));
                    }
                    returnValue = ensureSingleton(unit, returnValue, code);
                }
            }
        }
        catch (SQLException exception) {
            throw databaseFailure(Unit.class, code, exception);
        }
        if (returnValue == null) {
            throw noSuchAuthorityCode(Unit.class, code);
        }
        return returnValue;
    }

    /**
     * Returns an ellipsoid from a code.
     *
     * @param  code The EPSG value.
     * @return The ellipsoid object.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized Ellipsoid createEllipsoid(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        Ellipsoid returnValue = null;
        try {
            final String primaryKey = toPrimaryKey(Ellipsoid.class, code,
                    "[Ellipsoid]", "ELLIPSOID_CODE", "ELLIPSOID_NAME");
            final PreparedStatement stmt;
            stmt = prepareStatement("[Ellipsoid]",
                    "SELECT ELLIPSOID_CODE," +
                          " ELLIPSOID_NAME," +
                          " SEMI_MAJOR_AXIS," +
                          " INV_FLATTENING," +
                          " SEMI_MINOR_AXIS," +
                          " UOM_CODE," +
                          " REMARKS," +
                          " DEPRECATED" +
                    " FROM [Ellipsoid]" +
                    " WHERE ELLIPSOID_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    /*
                     * One of 'semiMinorAxis' and 'inverseFlattening' values can be NULL in
                     * the database. Consequently, we don't use 'getString(ResultSet, int)'
                     * because we don't want to thrown an exception if a NULL value is found.
                     */
                    final String  epsg              = getString(result, 1, code);
                    final String  name              = getString(result, 2, code);
                    final double  semiMajorAxis     = getDouble(result, 3, code);
                    final double  inverseFlattening = result.getDouble( 4);
                    final double  semiMinorAxis     = result.getDouble( 5);
                    final String  unitCode          = getString(result, 6, code);
                    final String  remarks           = result.getString( 7);
                    final boolean deprecated        = result.getInt   ( 8) != 0;
                    final Unit<Length> unit         = buffered.createUnit(unitCode).asType(Length.class);
                    final Map<String,Object> properties = createProperties("[Ellipsoid]",
                            name, epsg, remarks, deprecated);
                    final Ellipsoid ellipsoid;
                    if (inverseFlattening == 0) {
                        if (semiMinorAxis == 0) {
                            // Both are null, which is not allowed.
                            final String column = result.getMetaData().getColumnName(3);
                            throw new FactoryException(Errors.format(Errors.Keys.NULL_VALUE_IN_TABLE_3, code, column));
                        } else {
                            // We only have semiMinorAxis defined -> it's OK
                            ellipsoid = factories.getDatumFactory().createEllipsoid(
                                    properties, semiMajorAxis, semiMinorAxis, unit);
                        }
                    } else {
                        if (semiMinorAxis != 0) {
                            // Both 'inverseFlattening' and 'semiMinorAxis' are defined.
                            // Log a warning and create the ellipsoid using the inverse flattening.
                            final LogRecord record = Loggings.format(Level.WARNING, Loggings.Keys.AMBIGUOUS_ELLIPSOID, code);
                            record.setLoggerName(LOGGER.getName());
                            LOGGER.log(record);
                        }
                        ellipsoid = factories.getDatumFactory().createFlattenedSphere(
                                properties, semiMajorAxis, inverseFlattening, unit);
                    }
                    /*
                     * Now that we have built an ellipsoid, compare
                     * it with the previous one (if any).
                     */
                    returnValue = ensureSingleton(ellipsoid, returnValue, code);
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(Ellipsoid.class, code, exception);
        }
        if (returnValue == null) {
             throw noSuchAuthorityCode(Ellipsoid.class, code);
        }
        return returnValue;
    }

    /**
     * Returns a prime meridian, relative to Greenwich.
     *
     * @param  code Value allocated by authority.
     * @return The prime meridian object.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized PrimeMeridian createPrimeMeridian(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        PrimeMeridian returnValue = null;
        try {
            final String primaryKey = toPrimaryKey(PrimeMeridian.class, code,
                    "[Prime Meridian]", "PRIME_MERIDIAN_CODE", "PRIME_MERIDIAN_NAME");
            final PreparedStatement stmt;
            stmt = prepareStatement("[Prime Meridian]",
                    "SELECT PRIME_MERIDIAN_CODE," +
                          " PRIME_MERIDIAN_NAME," +
                          " GREENWICH_LONGITUDE," +
                          " UOM_CODE," +
                          " REMARKS," +
                          " DEPRECATED" +
                    " FROM [Prime Meridian]" +
                    " WHERE PRIME_MERIDIAN_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final String  epsg       = getString(result, 1, code);
                    final String  name       = getString(result, 2, code);
                    final double  longitude  = getDouble(result, 3, code);
                    final String  unitCode   = getString(result, 4, code);
                    final String  remarks    = result.getString( 5);
                    final boolean deprecated = result.getInt   ( 6) != 0;
                    final Unit<Angle> unit = buffered.createUnit(unitCode).asType(Angle.class);
                    final Map<String,Object> properties = createProperties("[Prime Meridian]",
                            name, epsg, remarks, deprecated);
                    PrimeMeridian primeMeridian = factories.getDatumFactory().createPrimeMeridian(
                            properties, longitude, unit);
                    returnValue = ensureSingleton(primeMeridian, returnValue, code);
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(PrimeMeridian.class, code, exception);
        }
        if (returnValue == null) {
            throw noSuchAuthorityCode(PrimeMeridian.class, code);
        }
        return returnValue;
    }

    /**
     * Returns an area of use.
     *
     * @param  code Value allocated by authority.
     * @return The area of use.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized Extent createExtent(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        Extent returnValue = null;
        try {
            final String primaryKey = toPrimaryKey(Extent.class, code,
                    "[Area]", "AREA_CODE", "AREA_NAME");
            final PreparedStatement stmt;
            stmt = prepareStatement("[Area]",
                    "SELECT AREA_OF_USE," +
                          " AREA_SOUTH_BOUND_LAT," +
                          " AREA_NORTH_BOUND_LAT," +
                          " AREA_WEST_BOUND_LON," +
                          " AREA_EAST_BOUND_LON" +
                    " FROM [Area]" +
                    " WHERE AREA_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    DefaultExtent extent = null;
                    final String description = result.getString(1);
                    if (description != null) {
                        extent = new DefaultExtent();
                        extent.setDescription(new SimpleInternationalString(description));
                    }
                    double ymin = result.getDouble(2); if (result.wasNull()) ymin = Double.NaN;
                    double ymax = result.getDouble(3); if (result.wasNull()) ymax = Double.NaN;
                    double xmin = result.getDouble(4); if (result.wasNull()) xmin = Double.NaN;
                    double xmax = result.getDouble(5); if (result.wasNull()) xmax = Double.NaN;
                    if (!Double.isNaN(ymin) || !Double.isNaN(ymax) || !Double.isNaN(xmin) || !Double.isNaN(xmax)) {
                        /*
                         * Fix an error found in EPSG:3790 New Zealand - South Island - Mount Pleasant mc
                         * for older database (this error is fixed in EPSG database 8.2).
                         */
                        if (ymin > ymax) {
                            final double t = ymin;
                            ymin = ymax;
                            ymax = t;
                        }
                        if (extent == null) {
                            extent = new DefaultExtent();
                        }
                        extent.setGeographicElements(Collections.singleton(
                                new DefaultGeographicBoundingBox(xmin, xmax, ymin, ymax)));
                    }
                    if (extent != null) {
                        extent.freeze();
                        returnValue = ensureSingleton(extent, returnValue, code);
                    }
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(Extent.class, code, exception);
        }
        if (returnValue == null) {
            throw noSuchAuthorityCode(Extent.class, code);
        }
        return returnValue;
    }

    /**
     * Returns Bursa-Wolf parameters for a geodetic datum. If the specified datum has
     * no conversion informations, then this method will returns {@code null}.
     *
     * @param  code The EPSG code of the {@link GeodeticDatum}.
     * @param  toClose The result set to close if this method is going to invokes
     *         {@link #createDatum} recursively. This hack is necessary because many
     *         JDBC drivers do not support multiple result sets for the same statement.
     *         The result set is closed if an only if this method returns a non-null value.
     * @return an array of Bursa-Wolf parameters (in which case {@code toClose} has
     *         been closed), or {@code null} (in which case {@code toClose} has
     *         <strong>not</strong> been closed).
     */
    private BursaWolfParameters[] createBursaWolfParameters(final String code, final ResultSet toClose)
            throws SQLException, FactoryException
    {
        if (safetyGuard.contains(code)) {
            /*
             * Do not try to create Bursa-Wolf parameters if the datum is already
             * in process of being created. This check avoid never-ending loops in
             * recursive call to 'createDatum'.
             */
            return null;
        }
        PreparedStatement stmt;
        stmt = prepareStatement("BursaWolfParametersSet",
                "SELECT CO.COORD_OP_CODE," +
                      " CO.COORD_OP_METHOD_CODE," +
                      " CRS2.DATUM_CODE" +
                " FROM [Coordinate_Operation] AS CO" +
          " INNER JOIN [Coordinate Reference System] AS CRS2" +
                  " ON CO.TARGET_CRS_CODE = CRS2.COORD_REF_SYS_CODE" +
               " WHERE CO.COORD_OP_METHOD_CODE >= " + BURSA_WOLF_MIN_CODE +
                 " AND CO.COORD_OP_METHOD_CODE <= " + BURSA_WOLF_MAX_CODE +
                 " AND CO.SOURCE_CRS_CODE IN (" +
              " SELECT CRS1.COORD_REF_SYS_CODE " + // GEOT-1129
                " FROM [Coordinate Reference System] AS CRS1 " +
               " WHERE CRS1.DATUM_CODE = ?)" +
            " ORDER BY CRS2.DATUM_CODE," +
                     " ABS(CO.DEPRECATED), CO.COORD_OP_ACCURACY," +
                     " CO.COORD_OP_CODE DESC"); // GEOT-846 fix
        List<Object> bwInfos = null;
        try (ResultSet result = executeQuery(stmt, code)) {
            while (result.next()) {
                final int    operation = getInt   (result, 1, code);
                final int    method    = getInt   (result, 2, code);
                final String datum     = getString(result, 3, code);
                if (bwInfos == null) {
                    bwInfos = new ArrayList<>();
                }
                bwInfos.add(new BursaWolfInfo(operation, method, datum));
            }
        }
        if (bwInfos == null) {
            // Don't close the ResultSet here.
            return null;
        }
        toClose.close();
        /*
         * Sorts the infos in preference order. The "ORDER BY" clause above was not enough;
         * we also need to take the "supersession" table in account. Once the sorting is done,
         * keep only one Bursa-Wolf parameters for each datum.
         */
        int size = bwInfos.size();
        if (size > 1) {
            final BursaWolfInfo[] codes = bwInfos.toArray(new BursaWolfInfo[size]);
            sort(codes);
            bwInfos.clear();
            final Set<String> added = new HashSet<>();
            for (int i=0; i<codes.length; i++) {
                final BursaWolfInfo candidate = codes[i];
                if (added.add(candidate.target)) {
                    bwInfos.add(candidate);
                }
            }
            size = bwInfos.size();
        }
        /*
         * We got all the needed informations before to built Bursa-Wolf parameters because the
         * 'createDatum(...)' call below may invokes 'createBursaWolfParameters(...)' recursively,
         * and not all JDBC drivers supported multi-result set for the same statement. Now, iterate
         * throw the results and fetch the parameter values for each BursaWolfParameters object.
         */
        stmt = prepareStatement("BursaWolfParameters",
                "SELECT PARAMETER_CODE," +
                      " PARAMETER_VALUE," +
                      " UOM_CODE" +
                " FROM [Coordinate_Operation Parameter Value]" +
                " WHERE COORD_OP_CODE = ?" +
                  " AND COORD_OP_METHOD_CODE = ?");
        for (int i=0; i<size; i++) {
            final BursaWolfInfo info = (BursaWolfInfo) bwInfos.get(i);
            final GeodeticDatum datum;
            try {
                safetyGuard.add(code);
                datum = buffered.createGeodeticDatum(info.target);
            } finally {
                safetyGuard.remove(code);
            }
            final BursaWolfParameters parameters = new BursaWolfParameters(datum, null);
            stmt.setInt(1, info.operation);
            stmt.setInt(2, info.method);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    setBursaWolfParameter(parameters,
                                        getInt   (result, 1, info.operation),
                                        getDouble(result, 2, info.operation),
                    buffered.createUnit(getString(result, 3, info.operation)));
                }
            }
            if (info.method == ROTATION_FRAME_CODE) {
                // Coordinate frame rotation (9607): same as 9606,
                // except for the sign of rotation parameters.
                parameters.reverseRotation();
            }
            bwInfos.set(i, parameters);
        }
        return bwInfos.toArray(new BursaWolfParameters[size]);
    }

    /**
     * Returns a datum from a code.
     *
     * @param  code Value allocated by authority.
     * @return The datum object.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     *
     * @todo Current implementation maps all "vertical" datum to
     *       {@link VerticalDatumType#GEOIDAL}. We don't know yet how
     *       to maps the exact vertical datum type from the EPSG database.
     */
    @Override
    public synchronized Datum createDatum(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        Datum returnValue = null;
        try {
            final String primaryKey = toPrimaryKey(Datum.class, code,
                    "[Datum]", "DATUM_CODE", "DATUM_NAME");
            final PreparedStatement stmt;
            stmt = prepareStatement("[Datum]",
                    "SELECT DATUM_CODE," +
                          " DATUM_NAME," +
                          " DATUM_TYPE," +
                          " ORIGIN_DESCRIPTION," +
                          " REALIZATION_EPOCH," +
                          " AREA_OF_USE_CODE," +
                          " DATUM_SCOPE," +
                          " REMARKS," +
                          " DEPRECATED," +
                          " ELLIPSOID_CODE," +     // Only for geodetic type
                          " PRIME_MERIDIAN_CODE" + // Only for geodetic type
                    " FROM [Datum]" +
                    " WHERE DATUM_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final String  epsg       = getString(result, 1, code);
                    final String  name       = getString(result, 2, code);
                    final String  type       = getString(result, 3, code).trim();
                    final String  anchor     = result.getString( 4);
                    final String  epoch      = result.getString( 5);
                    final String  area       = result.getString( 6);
                    final String  scope      = result.getString( 7);
                    final String  remarks    = result.getString( 8);
                    final boolean deprecated = result.getInt   ( 9) != 0;
                    Map<String,Object> properties = createProperties("[Datum]",
                            name, epsg, area, scope, remarks, deprecated);
                    if (anchor != null) {
                        properties.put(Datum.ANCHOR_POINT_KEY, anchor);
                    }
                    if (epoch != null && !epoch.isEmpty()) try {
                        final int year = Integer.parseInt(epoch);
                        if (calendar == null) {
                            calendar = Calendar.getInstance();
                        }
                        calendar.clear();
                        calendar.set(year, 0, 1);
                        properties.put(Datum.REALIZATION_EPOCH_KEY, calendar.getTime());
                    } catch (NumberFormatException exception) {
                        // Not a fatal error...
                        Logging.unexpectedException(LOGGER, DirectEpsgFactory.class, "createDatum", exception);
                    }
                    final DatumFactory factory = factories.getDatumFactory();
                    final Datum datum;
                    /*
                    * Now build datum according their datum type. Constructions are straightforward,
                    * except for the "geodetic" datum type which need some special processing:
                    *
                    *   - Because it invokes again 'createProperties' indirectly (through calls to
                    *     'createEllipsoid' and 'createPrimeMeridian'), it must protect 'properties'
                    *     from changes.
                    *
                    *   - Because 'createBursaWolfParameters' may invokes 'createDatum' recursively,
                    *     we must close the result set if Bursa-Wolf parameters are found. In this
                    *     case, we lost our paranoiac check for duplication.
                    */
                    switch (type.toLowerCase(Locale.US)) {
                        case "geodetic": {
                            properties = new HashMap<>(properties); // Protect from changes
                            final Ellipsoid         ellipsoid = buffered.createEllipsoid    (getString(result, 10, code));
                            final PrimeMeridian      meridian = buffered.createPrimeMeridian(getString(result, 11, code));
                            final BursaWolfParameters[] param = createBursaWolfParameters(primaryKey, result);
                            if (param != null) {
                                properties.put(DefaultGeodeticDatum.BURSA_WOLF_KEY, param);
                            }
                            datum = factory.createGeodeticDatum(properties, ellipsoid, meridian);
                            break;
                        }
                        case "vertical": {
                            // TODO: Find the right datum type.
                            datum = factory.createVerticalDatum(properties, VerticalDatumType.GEOIDAL);
                            break;
                        }
                        case "temporal": {
                            // Origin date is stored in ORIGIN_DESCRIPTION field. A column of SQL type
                            // "date" type would have been better, but we can not modify the EPSG model.
                            final java.util.Date originDate;
                            if (anchor == null || anchor.isEmpty()) {
                                throw new FactoryException("A temporal datum origin is required.");
                            }
                            if (dateFormat == null) {
                                dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                // TODO: share the this.calendar value?
                            }
                            try {
                                originDate = dateFormat.parse(anchor);
                            } catch (ParseException e) {
                                throw new FactoryException("Failed to parse temporal datum origin date: " + e.getMessage(), e);
                            }
                            datum = factory.createTemporalDatum(properties, originDate);
                            break;
                        }
                        case "engineering": {
                            datum = factory.createEngineeringDatum(properties);
                            break;
                        }
                        default: {
                            throw new FactoryException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, type));
                        }
                    }
                    returnValue = ensureSingleton(datum, returnValue, code);
                    if (result.isClosed()) {
                        return returnValue;
                    }
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(Datum.class, code, exception);
        }
        if (returnValue == null) {
            throw noSuchAuthorityCode(Datum.class, code);
        }
        return returnValue;
    }

    /**
     * Returns the name and description for the specified {@linkplain CoordinateSystemAxis
     * coordinate system axis} code. Many axis share the same name and description, so it
     * is worth to cache them.
     */
    private AxisName getAxisName(final String code) throws FactoryException {
        assert Thread.holdsLock(this);
        if (axisNames == null) {
            axisNames = new HashMap<>();
        }
        AxisName returnValue = axisNames.get(code);
        if (returnValue == null) try {
            final PreparedStatement stmt;
            stmt = prepareStatement("[Coordinate Axis Name]",
                    "SELECT COORD_AXIS_NAME, DESCRIPTION, REMARKS" +
                    " FROM [Coordinate Axis Name]" +
                    " WHERE COORD_AXIS_NAME_CODE = ?");
            try (ResultSet result = executeQuery(stmt, code)) {
                while (result.next()) {
                    final String name  = getString(result, 1, code);
                    String description = result.getString (2);
                    String remarks     = result.getString (3);
                    if (description == null) {
                        description = remarks;
                    } else if (remarks != null) {
                        description += System.lineSeparator() + remarks;
                    }
                    final AxisName axis = new AxisName(name, description);
                    returnValue = ensureSingleton(axis, returnValue, code);
                }
            }
            if (returnValue == null) {
                throw noSuchAuthorityCode(AxisName.class, code);
            }
            axisNames.put(code, returnValue);
        } catch (SQLException exception) {
            throw databaseFailure(AxisName.class, code, exception);
        }
        return returnValue;
    }

    /**
     * Returns a {@linkplain CoordinateSystemAxis coordinate system axis} from a code.
     *
     * @param  code Value allocated by authority.
     * @throws NoSuchAuthorityCodeException if the specified {@code code} was not found.
     * @throws FactoryException if the object creation failed for some other reason.
     */
    @Override
    public synchronized CoordinateSystemAxis createCoordinateSystemAxis(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        CoordinateSystemAxis returnValue = null;
        try {
            final String primaryKey = trimAuthority(code);
            final PreparedStatement stmt;
            stmt = prepareStatement("[Coordinate Axis]",
                    "SELECT COORD_AXIS_CODE," +
                          " COORD_AXIS_NAME_CODE," +
                          " COORD_AXIS_ORIENTATION," +
                          " COORD_AXIS_ABBREVIATION," +
                          " UOM_CODE" +
                    " FROM [Coordinate Axis]" +
                   " WHERE COORD_AXIS_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final String epsg         = getString(result, 1, code);
                    final String nameCode     = getString(result, 2, code);
                    final String orientation  = getString(result, 3, code);
                    final String abbreviation = getString(result, 4, code);
                    final String unit         = getString(result, 5, code);
                    AxisDirection direction;
                    try {
                        direction = CoordinateSystems.parseAxisDirection(orientation);
                    } catch (IllegalArgumentException exception) {
                        throw new FactoryException(exception.getLocalizedMessage(), exception);
                    }
                    final AxisName an = getAxisName(nameCode);
                    final Map<String,Object> properties = createProperties("[Coordinate Axis]", an.name, epsg, an.description, false);
                    final CSFactory factory = factories.getCSFactory();
                    final CoordinateSystemAxis axis = factory.createCoordinateSystemAxis(
                            properties, abbreviation, direction, buffered.createUnit(unit));
                    returnValue = ensureSingleton(axis, returnValue, code);
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(CoordinateSystemAxis.class, code, exception);
        }
        if (returnValue == null) {
            throw noSuchAuthorityCode(CoordinateSystemAxis.class, code);
        }
        return returnValue;
    }

    /**
     * Returns the coordinate system axis from an EPSG code for a {@link CoordinateSystem}.
     * <p>
     * <strong>WARNING:</strong> The EPSG database uses "{@code ORDER}" as a column name.
     * This is tolerated by Access, but MySQL doesn't accept this name.
     *
     * @param  code the EPSG code for coordinate system owner.
     * @param  dimension of the coordinate system, which is also the size of the returned array.
     * @return An array of coordinate system axis.
     * @throws SQLException if an error occurred during database access.
     * @throws FactoryException if the code has not been found.
     */
    private CoordinateSystemAxis[] createAxesForCoordinateSystem(final String code, final int dimension)
            throws SQLException, FactoryException
    {
        assert Thread.holdsLock(this);
        final CoordinateSystemAxis[] axis = new CoordinateSystemAxis[dimension];
        final PreparedStatement stmt;
        stmt = prepareStatement("AxisOrder",
                "SELECT COORD_AXIS_CODE" +
                " FROM [Coordinate Axis]" +
                " WHERE COORD_SYS_CODE = ?" +
                " ORDER BY [ORDER]");
                // WARNING: Be careful about the column name : MySQL rejects ORDER as a column name.
        int i = 0;
        try (ResultSet result = executeQuery(stmt, code)) {
            while (result.next()) {
                final String axisCode = getString(result, 1, code);
                if (i < axis.length) {
                    // If 'i' is out of bounds, an exception will be thrown after the loop.
                    // We don't want to thrown an ArrayIndexOutOfBoundsException here.
                    axis[i] = buffered.createCoordinateSystemAxis(axisCode);
                }
                ++i;
            }
        }
        if (i != axis.length) {
            throw new FactoryException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_2, axis.length, i));
        }
        return axis;
    }

    /**
     * Returns a coordinate system from a code.
     *
     * @param  code Value allocated by authority.
     * @return The coordinate system object.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized CoordinateSystem createCoordinateSystem(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        CoordinateSystem returnValue = null;
        final PreparedStatement stmt;
        try {
            final String primaryKey = toPrimaryKey(CoordinateSystem.class, code,
                    "[Coordinate System]", "COORD_SYS_CODE", "COORD_SYS_NAME");
            stmt = prepareStatement("[Coordinate System]",
                    "SELECT COORD_SYS_CODE," +
                          " COORD_SYS_NAME," +
                          " COORD_SYS_TYPE," +
                          " DIMENSION," +
                          " REMARKS," +
                          " DEPRECATED" +
                    " FROM [Coordinate System]" +
                    " WHERE COORD_SYS_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final String  epsg       = getString(result, 1, code);
                    final String  name       = getString(result, 2, code);
                    final String  type       = getString(result, 3, code).trim();
                    final int     dimension  = getInt   (result, 4, code);
                    final String  remarks    = result.getString( 5);
                    final boolean deprecated = result.getInt   ( 6) != 0;
                    final CoordinateSystemAxis[] axis = createAxesForCoordinateSystem(primaryKey, dimension);
                    final Map<String,Object> properties = createProperties("[Coordinate System]",
                            name, epsg, remarks, deprecated); // Must be after axis
                    final CSFactory factory = factories.getCSFactory();
                    CoordinateSystem cs = null;
                    switch (type.toLowerCase(Locale.US)) {
                        case "ellipsoidal": {
                            switch (dimension) {
                                case 2: cs=factory.createEllipsoidalCS(properties, axis[0], axis[1]); break;
                                case 3: cs=factory.createEllipsoidalCS(properties, axis[0], axis[1], axis[2]); break;
                            }
                            break;
                        }
                        case "cartesian": {
                            switch (dimension) {
                                case 2: cs=factory.createCartesianCS(properties, axis[0], axis[1]); break;
                                case 3: cs=factory.createCartesianCS(properties, axis[0], axis[1], axis[2]); break;
                            }
                            break;
                        }
                        case "spherical": {
                            switch (dimension) {
                                case 3: cs=factory.createSphericalCS(properties, axis[0], axis[1], axis[2]); break;
                            }
                            break;
                        }
                        case "vertical":
                        case "gravity-related": {
                            switch (dimension) {
                                case 1: cs=factory.createVerticalCS(properties, axis[0]); break;
                            }
                            break;
                        }
                        case "time": // Was used in older ISO-19111 versions.
                        case "temporal": {
                            switch (dimension) {
                                case 1: cs=factory.createTimeCS(properties, axis[0]); break;
                            }
                            break;
                        }
                        case "linear": {
                            switch (dimension) {
                                case 1: cs=factory.createLinearCS(properties, axis[0]); break;
                            }
                            break;
                        }
                        case "polar": {
                            switch (dimension) {
                                case 2: cs=factory.createPolarCS(properties, axis[0], axis[1]); break;
                            }
                            break;
                        }
                        case "cylindrical": {
                            switch (dimension) {
                                case 3: cs=factory.createCylindricalCS(properties, axis[0], axis[1], axis[2]); break;
                            }
                            break;
                        }
                        case "affine": {
                            switch (dimension) {
                                case 2: cs=factory.createAffineCS(properties, axis[0], axis[1]); break;
                                case 3: cs=factory.createAffineCS(properties, axis[0], axis[1], axis[2]); break;
                            }
                            break;
                        }
                        default: {
                            throw new FactoryException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, type));
                        }
                    }
                    if (cs == null) {
                        throw new FactoryException(Errors.format(Errors.Keys.UNEXPECTED_DIMENSION_FOR_CS_1, type));
                    }
                    returnValue = ensureSingleton(cs, returnValue, code);
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(CoordinateSystem.class, code, exception);
        }
        if (returnValue == null) {
            throw noSuchAuthorityCode(CoordinateSystem.class, code);
        }
        return returnValue;

    }

    /**
     * Returns the primary key for a coordinate reference system name.
     * This method is used both by {@link #createCoordinateReferenceSystem}
     * and {@link #createFromCoordinateReferenceSystemCodes}
     */
    private String toPrimaryKeyCRS(final String code) throws SQLException, FactoryException {
        return toPrimaryKey(CoordinateReferenceSystem.class, code,
                "[Coordinate Reference System]", "COORD_REF_SYS_CODE", "COORD_REF_SYS_NAME");
    }

    /**
     * Returns a coordinate reference system from a code.
     *
     * @param  code Value allocated by authority.
     * @return The coordinate reference system object.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized CoordinateReferenceSystem createCoordinateReferenceSystem(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        CoordinateReferenceSystem returnValue = null;
        try {
            final String primaryKey = toPrimaryKeyCRS(code);
            final PreparedStatement stmt;
            stmt = prepareStatement("[Coordinate Reference System]",
                    "SELECT COORD_REF_SYS_CODE," +
                          " COORD_REF_SYS_NAME," +
                          " AREA_OF_USE_CODE," +
                          " CRS_SCOPE," +
                          " REMARKS," +
                          " DEPRECATED," +
                          " COORD_REF_SYS_KIND," +
                          " COORD_SYS_CODE," +       // Null for CompoundCRS
                          " DATUM_CODE," +           // Null for ProjectedCRS
                          " SOURCE_GEOGCRS_CODE," +  // For ProjectedCRS
                          " PROJECTION_CONV_CODE," + // For ProjectedCRS
                          " CMPD_HORIZCRS_CODE," +   // For CompoundCRS only
                          " CMPD_VERTCRS_CODE" +     // For CompoundCRS only
                    " FROM [Coordinate Reference System]" +
                    " WHERE COORD_REF_SYS_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final String  epsg       = getString(result, 1, code);
                    final String  name       = getString(result, 2, code);
                    final String  area       = result.getString( 3);
                    final String  scope      = result.getString( 4);
                    final String  remarks    = result.getString( 5);
                    final boolean deprecated = result.getInt   ( 6) != 0;
                    final String  type       = getString(result, 7, code);
                    // Note: Do not invoke 'createProperties' now, even if we have all required
                    //       informations, because the 'properties' map is going to overwritten
                    //       by calls to 'createDatum', 'createCoordinateSystem', etc.
                    final CRSFactory factory = factories.getCRSFactory();
                    final CoordinateReferenceSystem crs;
                    switch (type.toLowerCase(Locale.US)) {
                        /* ----------------------------------------------------------------------
                         *   GEOGRAPHIC CRS
                         *
                         *   NOTE: 'createProperties' MUST be invoked after any call to an other
                         *         'createFoo' method. Consequently, do not factor out.
                         * ---------------------------------------------------------------------- */
                        case "geographic 2d":
                        case "geographic 3d": {
                            final String csCode    = getString(result, 8, code);
                            final String dmCode    = result.getString( 9);
                            final EllipsoidalCS cs = buffered.createEllipsoidalCS(csCode);
                            final GeodeticDatum datum;
                            if (dmCode != null) {
                                datum = buffered.createGeodeticDatum(dmCode);
                            } else {
                                final String geoCode = getString(result, 10, code, 9);
                                result.close(); // Must be close before createGeographicCRS
                                final GeographicCRS baseCRS = buffered.createGeographicCRS(geoCode);
                                datum = baseCRS.getDatum();
                            }
                            final Map<String,Object> properties = createProperties(
                                    "[Coordinate Reference System]", name, epsg, area, scope, remarks, deprecated);
                            crs = factory.createGeographicCRS(properties, datum, cs);
                            break;
                        }
                        /* ----------------------------------------------------------------------
                         *   PROJECTED CRS
                         *
                         *   NOTE: This method invokes itself indirectly, through createGeographicCRS.
                         *         Consequently, we can't use 'result' anymore. We must close it here.
                         * ---------------------------------------------------------------------- */
                        case "projected": {
                            final String csCode  = getString(result,  8, code);
                            final String geoCode = getString(result, 10, code);
                            final String opCode  = getString(result, 11, code);
                            result.close(); // Must be close before createGeographicCRS
                            final CartesianCS         cs = buffered.createCartesianCS(csCode);
                            final GeographicCRS  baseCRS = buffered.createGeographicCRS(geoCode);
                            final CoordinateOperation op = buffered.createCoordinateOperation(opCode);
                            if (op instanceof Conversion) {
                                final Map<String,Object> properties = createProperties(
                                        "[Coordinate Reference System]", name, epsg, area, scope, remarks, deprecated);
                                crs = factory.createProjectedCRS(properties, baseCRS, (Conversion)op, cs);
                            } else {
                                throw noSuchAuthorityCode(Projection.class, opCode);
                            }
                            break;
                        }
                        /* ----------------------------------------------------------------------
                         *   VERTICAL CRS
                         * ---------------------------------------------------------------------- */
                        case "vertical": {
                            final String        csCode = getString(result, 8, code);
                            final String        dmCode = getString(result, 9, code);
                            final VerticalCS    cs     = buffered.createVerticalCS   (csCode);
                            final VerticalDatum datum  = buffered.createVerticalDatum(dmCode);
                            final Map<String,Object> properties = createProperties(
                                    "[Coordinate Reference System]", name, epsg, area, scope, remarks, deprecated);
                            crs = factory.createVerticalCRS(properties, datum, cs);
                            break;
                        }
                        /* ----------------------------------------------------------------------
                         *   TEMPORAL CRS
                         *
                         *   NOTE : The original EPSG database does not define any temporal CRS.
                         *          This block is a Geotk-specific extension.
                         * ---------------------------------------------------------------------- */
                        case "temporal": {
                            final String        csCode = getString(result, 8, code);
                            final String        dmCode = getString(result, 9, code);
                            final TimeCS        cs     = buffered.createTimeCS(csCode);
                            final TemporalDatum datum  = buffered.createTemporalDatum(dmCode);
                            final Map<String,Object> properties = createProperties(
                                    "[Coordinate Reference System]", name, epsg, area, scope, remarks, deprecated);
                            crs = factory.createTemporalCRS(properties, datum, cs);
                            break;
                        }
                        /* ----------------------------------------------------------------------
                         *   COMPOUND CRS
                         *
                         *   NOTE: This method invokes itself recursively.
                         *         Consequently, we can't use 'result' anymore.
                         * ---------------------------------------------------------------------- */
                        case "compound": {
                            final String code1 = getString(result, 12, code);
                            final String code2 = getString(result, 13, code);
                            result.close();
                            final CoordinateReferenceSystem crs1, crs2;
                            if (!safetyGuard.add(epsg)) {
                                throw recursiveCall(CompoundCRS.class, epsg);
                            } try {
                                crs1 = buffered.createCoordinateReferenceSystem(code1);
                                crs2 = buffered.createCoordinateReferenceSystem(code2);
                            } finally {
                                safetyGuard.remove(epsg);
                            }
                            // Note: Don't invoke 'createProperties' sooner.
                            final Map<String,Object> properties = createProperties(
                                    "[Coordinate Reference System]", name, epsg, area, scope, remarks, deprecated);
                            crs  = factory.createCompoundCRS(properties,
                                    new CoordinateReferenceSystem[] {crs1, crs2});
                            break;
                        }
                        /* ----------------------------------------------------------------------
                         *   GEOCENTRIC CRS
                         * ---------------------------------------------------------------------- */
                        case "geocentric": {
                            final String           csCode = getString(result, 8, code);
                            final String           dmCode = getString(result, 9, code);
                            final CoordinateSystem cs     = buffered.createCoordinateSystem(csCode);
                            final GeodeticDatum    datum  = buffered.createGeodeticDatum   (dmCode);
                            final Map<String,Object> properties = createProperties(
                                    "[Coordinate Reference System]", name, epsg, area, scope, remarks, deprecated);
                            if (cs instanceof CartesianCS) {
                                crs = factory.createGeocentricCRS(properties, datum, (CartesianCS) cs);
                            } else if (cs instanceof SphericalCS) {
                                crs = factory.createGeocentricCRS(properties, datum, (SphericalCS) cs);
                            } else {
                                throw new FactoryException(Errors.format(
                                        Errors.Keys.ILLEGAL_COORDINATE_SYSTEM_FOR_CRS_2,
                                        cs.getClass(), GeocentricCRS.class));
                            }
                            break;
                        }
                        /* ----------------------------------------------------------------------
                         *   ENGINEERING CRS
                         * ---------------------------------------------------------------------- */
                        case "engineering": {
                            final String           csCode = getString(result, 8, code);
                            final String           dmCode = getString(result, 9, code);
                            final CoordinateSystem cs     = buffered.createCoordinateSystem(csCode);
                            final EngineeringDatum datum  = buffered.createEngineeringDatum(dmCode);
                            final Map<String,Object> properties = createProperties(
                                    "[Coordinate Reference System]", name, epsg, area, scope, remarks, deprecated);
                            crs = factory.createEngineeringCRS(properties, datum, cs);
                            break;
                        }
                        /* ----------------------------------------------------------------------
                         *   UNKNOWN CRS
                         * ---------------------------------------------------------------------- */
                        default: {
                            throw new FactoryException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, type));
                        }
                    }
                    returnValue = ensureSingleton(crs, returnValue, code);
                    if (result.isClosed()) {
                        return returnValue;
                    }
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(CoordinateReferenceSystem.class, code, exception);
        }
        if (returnValue == null) {
             throw noSuchAuthorityCode(CoordinateReferenceSystem.class, code);
        }
        return returnValue;
    }

    /**
     * Returns a parameter descriptor from a code.
     *
     * @param  code The parameter descriptor code allocated by EPSG authority.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized ParameterDescriptor<?> createParameterDescriptor(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        ParameterDescriptor<?> returnValue = null;
        final PreparedStatement stmt;
        try {
            final String primaryKey = toPrimaryKey(ParameterDescriptor.class, code,
                    "[Coordinate_Operation Parameter]", "PARAMETER_CODE", "PARAMETER_NAME");
            stmt = prepareStatement("[Coordinate_Operation Parameter]",
                    "SELECT PARAMETER_CODE," +
                          " PARAMETER_NAME," +
                          " DESCRIPTION," +
                          " DEPRECATED" +
                    " FROM [Coordinate_Operation Parameter]" +
                    " WHERE PARAMETER_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final String  epsg       = getString(result, 1, code);
                    final String  name       = getString(result, 2, code);
                    final String  remarks    = result.getString( 3);
                    final boolean deprecated = result.getInt   ( 4) != 0;
                    final Unit<?>  unit;
                    final Class<?> type;
                    /*
                     * Search for units. We will choose the most commonly used one in parameter values.
                     * If the parameter appears to have at least one non-null value in the "Parameter
                     * File Name" column, then the type is assumed to be URI as a string. Otherwise,
                     * the type is a floating point number.
                     */
                    final PreparedStatement units = prepareStatement("ParameterUnit",
                            "SELECT MIN(UOM_CODE) AS UOM," +
                                " MIN(PARAM_VALUE_FILE_REF) AS FILEREF" +
                                " FROM [Coordinate_Operation Parameter Value]" +
                            " WHERE (PARAMETER_CODE = ?)" +
                            " GROUP BY UOM_CODE" +
                            " ORDER BY COUNT(UOM_CODE) DESC");
                    try (ResultSet resultUnits = executeQuery(units, epsg)) {
                        if (resultUnits.next()) {
                            String element = resultUnits.getString(1);
                            unit = (element!=null) ? buffered.createUnit(element) : null;
                            element = resultUnits.getString(2);
                            type = (element != null && !element.trim().isEmpty()) ? String.class : Double.class;
                        } else {
                            unit = null;
                            type = double.class;
                        }
                    }
                    /*
                     * Now creates the parameter descriptor.
                     */
                    final ParameterDescriptor<?> descriptor;
                    final Map<String,Object> properties = createProperties(
                            "[Coordinate_Operation Parameter]", name, epsg, remarks, deprecated);
                    @SuppressWarnings({"unchecked","rawtypes"})
                    final ParameterDescriptor<?> tmp = new DefaultParameterDescriptor(
                            properties, type, null, null, null, null, unit, true);
                    descriptor = tmp;
                    returnValue = ensureSingleton(descriptor, returnValue, code);
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(OperationMethod.class, code, exception);
        }
        if (returnValue == null) {
             throw noSuchAuthorityCode(OperationMethod.class, code);
        }
        return returnValue;
    }

    /**
     * Returns all parameter descriptors for the specified method.
     *
     * @param  method The operation method code.
     * @return The parameter descriptors.
     * @throws SQLException if a SQL statement failed.
     */
    private ParameterDescriptor<?>[] createParameterDescriptors(final String method)
            throws FactoryException, SQLException
    {
        final PreparedStatement stmt;
        stmt = prepareStatement("[Coordinate_Operation Parameter Usage]",
                "SELECT PARAMETER_CODE" +
                " FROM [Coordinate_Operation Parameter Usage]" +
                " WHERE COORD_OP_METHOD_CODE = ?" +
             " ORDER BY SORT_ORDER");
        final List<ParameterDescriptor<?>> descriptors;
        try (ResultSet result = executeQuery(stmt, method)) {
            descriptors = new ArrayList<>();
            while (result.next()) {
                final String param = getString(result, 1, method);
                descriptors.add(buffered.createParameterDescriptor(param));
            }
        }
        return descriptors.toArray(new ParameterDescriptor<?>[descriptors.size()]);
    }

    /**
     * Fill parameter values in the specified group.
     *
     * @param  method    The EPSG code for the operation method.
     * @param  operation The EPSG code for the operation (conversion or transformation).
     * @param  value     The parameter values to fill.
     * @throws SQLException if a SQL statement failed.
     */
    private void fillParameterValues(final String method, final String operation,
            final ParameterValueGroup parameters) throws FactoryException, SQLException
    {
        final PreparedStatement stmt;
        stmt = prepareStatement("[Coordinate_Operation Parameter Value]",
                "SELECT CP.PARAMETER_NAME," +
                      " CV.PARAMETER_VALUE," +
                      " CV.PARAM_VALUE_FILE_REF," +
                      " CV.UOM_CODE" +
               " FROM ([Coordinate_Operation Parameter Value] AS CV" +
          " INNER JOIN [Coordinate_Operation Parameter] AS CP" +
                   " ON CV.PARAMETER_CODE = CP.PARAMETER_CODE)" +
          " INNER JOIN [Coordinate_Operation Parameter Usage] AS CU" +
                  " ON (CP.PARAMETER_CODE = CU.PARAMETER_CODE)" +
                 " AND (CV.COORD_OP_METHOD_CODE = CU.COORD_OP_METHOD_CODE)" +
                " WHERE CV.COORD_OP_METHOD_CODE = ?" +
                  " AND CV.COORD_OP_CODE = ?" +
             " ORDER BY CU.SORT_ORDER");
        try (ResultSet result = executeQuery(stmt, method, operation)) {
            while (result.next()) {
                final String name  = getString(result, 1, operation);
                final double value = result.getDouble( 2);
                final Unit<?> unit;
                Object reference;
                if (result.wasNull()) {
                    /*
                     * If no numeric values were provided in the database, then the values must
                     * appears in some external file. It may be a file to download from FTP.
                     */
                    reference = getString(result, 3, operation);
                    unit = null;
                } else {
                    reference = null;
                    final String unitCode = result.getString(4);
                    unit = (unitCode != null) ? buffered.createUnit(unitCode) : null;
                }
                final ParameterValue<?> param;
                try {
                    param = parameters.parameter(name);
                } catch (ParameterNotFoundException exception) {
                    /*
                     * Wraps the unchecked ParameterNotFoundException into the checked
                     * NoSuchIdentifierException, which is a FactoryException subclass.
                     * Note that in theory, NoSuchIdentifierException is for MathTransforms rather
                     * than parameters.  However, we are close in spirit here since we are setting
                     * up MathTransform's parameters. Using NoSuchIdentifierException allows users
                     * (including CoordinateOperationSet) to know that the failure is probably
                     * caused by a MathTransform not yet supported in Geotk (or only partially
                     * supported) rather than some more serious failure in the database side.
                     * CoordinateOperationSet uses this information in order to determine if it
                     * should try the next coordinate operation or propagate the exception.
                     */
                    final NoSuchIdentifierException e = new NoSuchIdentifierException(
                            Errors.format(Errors.Keys.CANT_SET_PARAMETER_VALUE_1, name), name);
                    e.initCause(exception);
                    throw e;
                }
                try {
                    if (reference != null) {
                        param.setValue(reference);
                    } else if (unit != null) {
                        param.setValue(value, unit);
                    } else {
                        param.setValue(value);
                    }
                } catch (RuntimeException exception) { // Catch InvalidParameterValueException, ArithmeticException
                    throw new FactoryException(Errors.format(Errors.Keys.CANT_SET_PARAMETER_VALUE_1, name), exception);
                }
            }
        }
    }

    /**
     * Returns an operation method from a code.
     *
     * @param  code The operation method code allocated by EPSG authority.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized OperationMethod createOperationMethod(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        OperationMethod returnValue = null;
        final PreparedStatement stmt;
        try {
            final String primaryKey = toPrimaryKey(OperationMethod.class, code,
                    "[Coordinate_Operation Method]", "COORD_OP_METHOD_CODE", "COORD_OP_METHOD_NAME");
            stmt = prepareStatement("[Coordinate_Operation Method]",
                    "SELECT COORD_OP_METHOD_CODE," +
                          " COORD_OP_METHOD_NAME," +
                          " FORMULA," +
                          " REMARKS," +
                          " DEPRECATED" +
                     " FROM [Coordinate_Operation Method]" +
                    " WHERE COORD_OP_METHOD_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final String  epsg       = getString(result, 1, code);
                    final String  name       = getString(result, 2, code);
                    final String  formula    = result.getString( 3);
                    final String  remarks    = result.getString( 4);
                    final boolean deprecated = result.getInt   ( 5) != 0;
                    final Integer[] dim = getDimensionsForMethod(epsg);
                    final ParameterDescriptor<?>[] descriptors = createParameterDescriptors(epsg);
                    final Map<String,Object> properties = createProperties(
                            "[Coordinate_Operation Method]", name, epsg, remarks, deprecated);
                    if (formula != null) {
                        properties.put(OperationMethod.FORMULA_KEY, formula);
                    }
                    final OperationMethod method;
                    method = new DefaultOperationMethod(properties, dim[0], dim[1],
                            new DefaultParameterDescriptorGroup(properties, descriptors));
                    returnValue = ensureSingleton(method, returnValue, code);
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(OperationMethod.class, code, exception);
        }
        if (returnValue == null) {
             throw noSuchAuthorityCode(OperationMethod.class, code);
        }
        return returnValue;
    }

    /**
     * Returns the source and target dimensions for the specified method, provided that
     * they are the same for all operations using that method. The returned array has
     * a length of 2 and is never null, but some elements in that array may be null.
     */
    private Integer[] getDimensionsForMethod(final String code) throws NoSuchIdentifierException, SQLException {
        final Integer[] dimensions = new Integer[2];
        final boolean[] differents = new boolean[2];
        int numDifferences = 0;
        boolean projections = false;
        do {
            /*
             * This loop is executed twice. On the first execution, we look for the source and
             * target CRS declared directly in the "Coordinate Operations" table. This applies
             * mostly to coordinate transformations, since those fields are typically empty in
             * the case of projected CRS.
             *
             * In the second execution, we will look for the base geographic CRS and
             * the resulting projected CRS that use the given operation method. This
             * allows us to handle the case of projected CRS (typically 2 dimensional).
             */
            final String key, sql;
            if (!projections) {
                key = "MethodDimensions";
                sql = "SELECT DISTINCT SRC.COORD_SYS_CODE," +
                                     " TGT.COORD_SYS_CODE" +
                      " FROM [Coordinate_Operation] AS CO" +
                " INNER JOIN [Coordinate Reference System] AS SRC ON SRC.COORD_REF_SYS_CODE = CO.SOURCE_CRS_CODE" +
                " INNER JOIN [Coordinate Reference System] AS TGT ON TGT.COORD_REF_SYS_CODE = CO.TARGET_CRS_CODE" +
                      " WHERE CO.DEPRECATED = 0 AND COORD_OP_METHOD_CODE = ?";
            } else {
                key = "DerivedDimensions";
                sql = "SELECT DISTINCT SRC.COORD_SYS_CODE," +
                                     " TGT.COORD_SYS_CODE" +
                      " FROM [Coordinate Reference System] AS TGT" +
                " INNER JOIN [Coordinate Reference System] AS SRC ON TGT.SOURCE_GEOGCRS_CODE = SRC.COORD_REF_SYS_CODE" +
                " INNER JOIN [Coordinate_Operation] AS CO ON TGT.PROJECTION_CONV_CODE = CO.COORD_OP_CODE" +
                      " WHERE CO.DEPRECATED = 0 AND COORD_OP_METHOD_CODE = ?";
            }
            final PreparedStatement stmt = prepareStatement(key, sql);
            try (ResultSet result = executeQuery(stmt, code)) {
                while (result.next()) {
                    for (int i=0; i<dimensions.length; i++) {
                        if (!differents[i]) { // Note worth to test heterogenous dimensions.
                            final Integer dim = getDimensionForCS(result.getString(i + 1));
                            if (dim != null) {
                                if (dimensions[i] == null) {
                                    dimensions[i] = dim;
                                } else if (!dim.equals(dimensions[i])) {
                                    dimensions[i] = null;
                                    differents[i] = true;
                                    if (++numDifferences == differents.length) {
                                        // All dimensions has been set to null.
                                        return dimensions;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while ((projections = !projections) == true);
        return dimensions;
    }

    /**
     * Returns the dimension of the specified Coordinate System, or {@code null} if not found.
     */
    private Integer getDimensionForCS(final String code) throws NoSuchIdentifierException, SQLException {
        if (axisCounts == null) {
            axisCounts = new HashMap<>();
        }
        Integer dimension = axisCounts.get(code);
        if (dimension == null) {
            final PreparedStatement stmt;
            stmt = prepareStatement("Dimension",
                    " SELECT COUNT(COORD_AXIS_CODE)" +
                     " FROM [Coordinate Axis]" +
                     " WHERE COORD_SYS_CODE = ?");
            try (ResultSet result = executeQuery(stmt, code)) {
                dimension = Integer.valueOf(result.next() ? result.getInt(1) : 0);
                axisCounts.put(code, dimension);
            }
        }
        return (dimension.intValue() != 0) ? dimension : null;
    }

    /**
     * Returns {@code true} if the {@linkplain CoordinateOperation coordinate operation} for the
     * specified code is a {@linkplain Projection projection}. The caller must have ensured that
     * the designed operation is a {@linkplain Conversion conversion} before to invoke this method.
     *
     * @throws NoSuchIdentifierException If the code has not been found.
     * @throws SQLException If an error occurred while querying the database.
     */
    final boolean isProjection(final String code) throws NoSuchIdentifierException, SQLException {
        if (codeProjection == null) {
            codeProjection = new HashMap<>();
        }
        final PreparedStatement stmt;
        Boolean projection = codeProjection.get(code);
        if (projection == null) {
            stmt = prepareStatement("isProjection",
                    "SELECT COORD_REF_SYS_CODE" +
                    " FROM [Coordinate Reference System]" +
                    " WHERE PROJECTION_CONV_CODE = ?" +
                      " AND COORD_REF_SYS_KIND LIKE 'projected%'");
            final boolean found;
            try (ResultSet result = executeQuery(stmt, code)) {
                found = result.next();
            }
            projection = Boolean.valueOf(found);
            codeProjection.put(code, projection);
        }
        return projection.booleanValue();
    }

    /**
     * Wraps the given accuracy value in a {@link PositionalAccuracy} metadata.
     *
     * @since 3.18
     */
    private PositionalAccuracy getAccuracy(final double accuracy) {
        if (accuracies == null) {
            accuracies = new HashMap<>();
        }
        final Double key = accuracy;
        PositionalAccuracy element = accuracies.get(key);
        if (element == null) {
            final DefaultQuantitativeResult accuracyResult;
            final DefaultAbsoluteExternalPositionalAccuracy accuracyElement;
            accuracyResult = new DefaultQuantitativeResult(new double[] {accuracy});
            accuracyResult.setValueUnit(SI.METRE); // In metres by definition in the EPSG database.
            accuracyElement = new DefaultAbsoluteExternalPositionalAccuracy(accuracyResult);
            accuracyElement.setMeasureDescription(TRANSFORMATION_ACCURACY);
            accuracyElement.setEvaluationMethodType(EvaluationMethodType.DIRECT_EXTERNAL);
            accuracyElement.freeze();
            element = accuracyElement;
            accuracies.put(key, element);
        }
        return element;
    }

    /**
     * Returns a coordinate operation from a code. The returned object will either be a
     * {@linkplain Conversion conversion} or a {@linkplain Transformation transformation},
     * depending on the code.
     *
     * @param  code Value allocated by authority.
     * @return The coordinate operation object.
     * @throws NoSuchAuthorityCodeException if this method can't find the requested code.
     * @throws FactoryException if some other kind of failure occurred in the backing
     *         store. This exception usually have {@link SQLException} as its cause.
     */
    @Override
    public synchronized CoordinateOperation createCoordinateOperation(final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        ensureNonNull("code", code);
        CoordinateOperation returnValue = null;
        try {
            final String primaryKey = toPrimaryKey(CoordinateOperation.class, code,
                    "[Coordinate_Operation]", "COORD_OP_CODE", "COORD_OP_NAME");
            final PreparedStatement stmt;
            stmt = prepareStatement("[Coordinate_Operation]",
                    "SELECT COORD_OP_CODE," +
                          " COORD_OP_NAME," +
                          " COORD_OP_TYPE," +
                          " SOURCE_CRS_CODE," +
                          " TARGET_CRS_CODE," +
                          " COORD_OP_METHOD_CODE," +
                          " COORD_TFM_VERSION," +
                          " COORD_OP_ACCURACY," +
                          " AREA_OF_USE_CODE," +
                          " COORD_OP_SCOPE," +
                          " REMARKS," +
                          " DEPRECATED" +
                    " FROM [Coordinate_Operation]" +
                    " WHERE COORD_OP_CODE = ?");
            try (ResultSet result = executeQuery(stmt, primaryKey)) {
                while (result.next()) {
                    final String epsg = getString(result, 1, code);
                    final String name = getString(result, 2, code);
                    final String type = getString(result, 3, code).trim().toLowerCase(Locale.US);
                    final boolean isTransformation = type.equals("transformation");
                    final boolean isConversion     = type.equals("conversion");
                    final boolean isConcatenated   = type.equals("concatenated operation");
                    final String sourceCode, targetCode, methodCode;
                    if (isConversion) {
                        // Optional for conversions, mandatory for all others.
                        sourceCode = result.getString(4);
                        targetCode = result.getString(5);
                    } else {
                        sourceCode = getString(result, 4, code);
                        targetCode = getString(result, 5, code);
                    }
                    if (isConcatenated) {
                        // Not applicable to concatenated operation, mandatory for all others.
                        methodCode = result.getString(6);
                    } else {
                        methodCode = getString(result, 6, code);
                    }
                          String  version    = result.getString( 7);
                          double  accuracy   = result.getDouble( 8); if (result.wasNull()) accuracy=Double.NaN;
                    final String  area       = result.getString( 9);
                    final String  scope      = result.getString(10);
                    final String  remarks    = result.getString(11);
                    final boolean deprecated = result.getInt   (12) != 0;
                    /*
                     * Gets the source and target CRS. They are mandatory for transformations (it
                     * was checked above in this method) and optional for conversions. Conversions
                     * are usually "defining conversions" and don't define source and target CRS.
                     * In EPSG database 6.7, all defining conversions are projections and their
                     * dimensions are always 2. However, this is not generalizable to other kind
                     * of operation methods. For example the "Geocentric translation" operation
                     * method has 3-dimensional source and target.
                     */
                    final Integer sourceDimensions, targetDimensions;
                    final CoordinateReferenceSystem sourceCRS, targetCRS;
                    if (sourceCode != null) {
                        sourceCRS = buffered.createCoordinateReferenceSystem(sourceCode);
                        sourceDimensions = sourceCRS.getCoordinateSystem().getDimension();
                    } else {
                        sourceCRS = null;
                        sourceDimensions = 2; // Acceptable default for projections only.
                    }
                    if (targetCode != null) {
                        targetCRS = buffered.createCoordinateReferenceSystem(targetCode);
                        targetDimensions = targetCRS.getCoordinateSystem().getDimension();
                    } else {
                        targetCRS = null;
                        targetDimensions = 2; // Acceptable default for projections only.
                    }
                    /*
                     * Gets the operation method. This is mandatory for conversions and transformations
                     * (it was checked above in this method) but optional for concatenated operations.
                     * Fetching parameter values is part of this block.
                     */
                    final boolean isBursaWolf;
                    OperationMethod method;
                    ParameterValueGroup parameters;
                    if (methodCode == null) {
                        isBursaWolf = false;
                        method      = null;
                        parameters  = null;
                    } else {
                        final int num;
                        try {
                            num = Integer.parseInt(methodCode);
                        } catch (NumberFormatException exception) {
                            throw new FactoryException(exception);
                        }
                        isBursaWolf = (num>=BURSA_WOLF_MIN_CODE && num<=BURSA_WOLF_MAX_CODE);
                        // Reminder: The source and target dimensions MUST be computed when
                        //           the information is available. Dimension is not always 2!!
                        method = buffered.createOperationMethod(methodCode);
                        if (!Objects.equals(method.getSourceDimensions(), sourceDimensions) ||
                            !Objects.equals(method.getTargetDimensions(), targetDimensions))
                        {
                            method = new DefaultOperationMethod(method, sourceDimensions, targetDimensions);
                        }
                        /*
                         * Note that some parameters required for MathTransform creation are implicit in
                         * the EPSG database (e.g. semi-major and semi-minor axis length in the case of
                         * map projections). We ask the parameter value group straight from the math
                         * transform factory instead of from the operation method in order to get all
                         * required parameter descriptors, including implicit ones.
                         */
                        final String methodName = method.getName().getCode();
                        String methodIdentifier = methodName;
                        final String mid = IdentifiedObjects.toString(IdentifiedObjects.getIdentifier(method, Citations.EPSG));
                        if (mid != null) {
                            // Use the EPSG code if possible, because operation method names are
                            // sometime ambiguous (e.g. "Lambert Azimuthal Equal Area (Spherical)")
                            // See http://jira.geotoolkit.org/browse/GEOTK-128
                            methodIdentifier = mid;
                        }
                        final MathTransformFactory mtFactory = factories.getMathTransformFactory();
                        try {
                            parameters = mtFactory.getDefaultParameters(methodIdentifier);
                        } catch (NoSuchIdentifierException exception) {
                            /*
                             * If we can not found the operation method by EPSG code, try searching
                             * by operation name. As a side effect, this second attemt will produce
                             * a better error message if the operation is really not found.
                             */
                            if (methodIdentifier.equals(methodName)) throw exception;
                            parameters = mtFactory.getDefaultParameters(methodName);
                            Logging.recoverableException(LOGGER, DirectEpsgFactory.class,
                                    "createCoordinateOperation", exception);
                        }
                        fillParameterValues(methodCode, epsg, parameters);
                    }
                    /*
                     * Creates common properties. The 'version' and 'accuracy' are usually defined
                     * for transformations only. However, we check them for all kind of operations
                     * (including conversions) and copy the information unconditionally if present.
                     *
                     * NOTE: This block must be executed last before object creations below, because
                     *       methods like createCoordinateReferenceSystem and createOperationMethod
                     *       overwrite the properties map.
                     */
                    Map<String,Object> properties = createProperties("[Coordinate_Operation]",
                            name, epsg, area, scope, remarks, deprecated);
                    if (version!=null && !(version = version.trim()).isEmpty()) {
                        properties.put(CoordinateOperation.OPERATION_VERSION_KEY, version);
                    }
                    if (!Double.isNaN(accuracy)) {
                        properties.put(CoordinateOperation.COORDINATE_OPERATION_ACCURACY_KEY, getAccuracy(accuracy));
                    }
                    /*
                     * Creates the operation. Conversions should be the only operations allowed to
                     * have null source and target CRS. In such case, the operation is a defining
                     * conversion (usually to be used later as part of a ProjectedCRS creation),
                     * and always a projection in the specific case of the EPSG database (which
                     * allowed us to assume 2-dimensional operation method in the code above for
                     * this specific case - not to be generalized to the whole EPSG database).
                     */
                    final CoordinateOperation operation;
                    if (isConversion && (sourceCRS==null || targetCRS==null)) {
                        // Note: we usually can't resolve sourceCRS and targetCRS because there
                        // is many of them for the same coordinate operation (projection) code.
                        operation = new DefiningConversion(properties, method, parameters);
                    } else if (isConcatenated) {
                        /*
                         * Concatenated operation: we need to close the current result set, because
                         * we are going to invoke this method recursively in the following lines.
                         *
                         * Note: we instantiate directly the Geotk's implementation of
                         * ConcatenatedOperation instead of using CoordinateOperationFactory in order
                         * to avoid loading the quite large Geotk's implementation of that factory,
                         * and also because it is not part of FactoryContainer anyway.
                         */
                        result.close();
                        properties = new HashMap<>(properties); // Because this class uses a shared map.
                        final PreparedStatement cstmt = prepareStatement("[Coordinate_Operation Path]",
                                "SELECT SINGLE_OPERATION_CODE" +
                                 " FROM [Coordinate_Operation Path]" +
                                " WHERE (CONCAT_OPERATION_CODE = ?)" +
                              " ORDER BY OP_PATH_STEP");
                        final List<String> codes;
                        try (ResultSet cr = executeQuery(cstmt, epsg)) {
                            codes = new ArrayList<>();
                            while (cr.next()) {
                                codes.add(cr.getString(1));
                            }
                        }
                        final CoordinateOperation[] operations = new CoordinateOperation[codes.size()];
                        if (!safetyGuard.add(epsg)) {
                            throw recursiveCall(ConcatenatedOperation.class, epsg);
                        } try {
                            for (int i=0; i<operations.length; i++) {
                                operations[i] = buffered.createCoordinateOperation(codes.get(i));
                            }
                        } finally {
                            safetyGuard.remove(epsg);
                        }
                        try {
                            return new DefaultConcatenatedOperation(properties, operations);
                        } catch (IllegalArgumentException exception) {
                            // May happen if there is less than 2 operations to concatenate.
                            // It happen for some deprecated CRS like 8658 for example.
                            throw new FactoryException(exception);
                        }
                    } else {
                        /*
                         * Needs to create a math transform. A special processing is performed for
                         * datum shift methods, since the conversion from ellipsoid to geocentric
                         * for "geocentric translations" is implicit in the EPSG database. Even in
                         * the case of Molodenski transforms, the axis length to set are the same.
                         */
                        if (isBursaWolf) try {
                            Ellipsoid ellipsoid = CRSUtilities.getHeadGeoEllipsoid(sourceCRS);
                            if (ellipsoid != null) {
                                final Unit<Length> axisUnit = ellipsoid.getAxisUnit();
                                parameters.parameter("src_semi_major").setValue(ellipsoid.getSemiMajorAxis(), axisUnit);
                                parameters.parameter("src_semi_minor").setValue(ellipsoid.getSemiMinorAxis(), axisUnit);
                                parameters.parameter("src_dim").setValue(sourceCRS.getCoordinateSystem().getDimension());
                            }
                            ellipsoid = CRSUtilities.getHeadGeoEllipsoid(targetCRS);
                            if (ellipsoid != null) {
                                final Unit<Length> axisUnit = ellipsoid.getAxisUnit();
                                parameters.parameter("tgt_semi_major").setValue(ellipsoid.getSemiMajorAxis(), axisUnit);
                                parameters.parameter("tgt_semi_minor").setValue(ellipsoid.getSemiMinorAxis(), axisUnit);
                                parameters.parameter("tgt_dim").setValue(targetCRS.getCoordinateSystem().getDimension());
                            }
                            // Since Geotk will implement the transformation as a concatenation of
                            // MathTransforms, it will not be able to find those parameters alone.
                            properties.put(PARAMETERS_KEY, parameters);
                        } catch (ParameterNotFoundException exception) {
                            throw new FactoryException(Errors.format(
                                    Errors.Keys.GEOTOOLKIT_EXTENSION_REQUIRED_1,
                                    method.getName().getCode(), exception));
                        }
                        /*
                         * At this stage, the parameters are ready for use. Creates the math transform
                         * and wraps it in the final operation (a Conversion or a Transformation).
                         */
                        final Class<? extends SingleOperation> expected;
                        if (isTransformation) {
                            expected = Transformation.class;
                        } else if (isConversion) {
                            expected = Conversion.class;
                        } else {
                            throw new FactoryException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, type));
                        }
                        final MathTransform mt = factories.getMathTransformFactory().createBaseToDerived(
                                sourceCRS, parameters, targetCRS.getCoordinateSystem());
                        // TODO: use GeoAPI factory method once available (http://jira.codehaus.org/browse/GEO-216).
                        operation = DefaultSingleOperation.create(properties, sourceCRS, targetCRS, mt, method, expected);
                    }
                    returnValue = ensureSingleton(operation, returnValue, code);
                    if (result.isClosed()) {
                        return returnValue;
                    }
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure(CoordinateOperation.class, code, exception);
        }
        if (returnValue == null) {
             throw noSuchAuthorityCode(CoordinateOperation.class, code);
        }
        return returnValue;
    }

    /**
     * Creates operations from coordinate reference system codes.
     * The returned set is ordered with the most accurate operations first.
     *
     * @param  sourceCode Coded value of source coordinate reference system.
     * @param  targetCode Coded value of target coordinate reference system.
     * @throws FactoryException if the object creation failed.
     *
     * @todo The ordering is not consistent among all database software, because the "accuracy"
     *       column may contains null values. When used in an "ORDER BY" clause, PostgreSQL put
     *       null values last, while Access and HSQL put them first. The PostgreSQL's behavior is
     *       better for what we want (put operations with unknown accuracy last). Unfortunately,
     *       I don't know yet how to instruct Access to put null values last using standard SQL
     *       ("IIF" is not standard, and Access doesn't seem to understand "CASE ... THEN" clauses).
     */
    @Override
    public synchronized Set<CoordinateOperation> createFromCoordinateReferenceSystemCodes(
            final String sourceCode, final String targetCode) throws FactoryException
    {
        ensureNonNull("sourceCode", sourceCode);
        ensureNonNull("targetCode", targetCode);
        final String pair = sourceCode + " \u21E8 " + targetCode;
        final CoordinateOperationSet set = new CoordinateOperationSet(buffered);
        try {
            final String sourceKey = toPrimaryKeyCRS(sourceCode);
            final String targetKey = toPrimaryKeyCRS(targetCode);
            boolean searchTransformations = false;
            do {
                /*
                 * This 'do' loop is executed twice: the first time for searching defining
                 * conversions, and the second time for searching all other kind of operations.
                 * Defining conversions are searched first because they are, by definition, the
                 * most accurate operations.
                 *
                 * TODO: Remove the "area" and "accuracy" ordering, since they are now replaced
                 *       by Java code (because we need to compute intersections while supporting
                 *       anti-meridian spanning).
                 */
                final String key, sql;
                if (searchTransformations) {
                    key = "TransformationFromCRS";
                    sql = "SELECT COORD_OP_CODE" +
                          " FROM [Coordinate_Operation] AS CO" +
                          " JOIN [Area] ON AREA_OF_USE_CODE = AREA_CODE" +
                          " WHERE SOURCE_CRS_CODE = ?" +
                            " AND TARGET_CRS_CODE = ?" +
                       " ORDER BY ABS(CO.DEPRECATED), COORD_OP_ACCURACY, " +
                       " ABS((AREA_EAST_BOUND_LON - AREA_WEST_BOUND_LON) *" +
                          " (AREA_NORTH_BOUND_LAT - AREA_SOUTH_BOUND_LAT) * COS(0.5*RADIANS" +
                           "(AREA_NORTH_BOUND_LAT + AREA_SOUTH_BOUND_LAT))) DESC";
                } else {
                    key = "ConversionFromCRS";
                    sql = "SELECT PROJECTION_CONV_CODE" +
                          " FROM [Coordinate Reference System]" +
                          " WHERE SOURCE_GEOGCRS_CODE = ?" +
                            " AND COORD_REF_SYS_CODE = ?";
                }
                final PreparedStatement stmt = prepareStatement(key, sql);
                try (ResultSet result = executeQuery(stmt, sourceKey, targetKey)) {
                    while (result.next()) {
                        final String code = getString(result, 1, pair);
                        set.addAuthorityCode(code, searchTransformations ? null : targetKey);
                    }
                }
            } while ((searchTransformations = !searchTransformations) == true);
            /*
             * Search finished. We may have a lot of coordinate operations
             * (e.g. about 40 for "ED50" (EPSG:4230) to "WGS 84" (EPSG:4326)).
             * Alter the ordering using the information supplied in the supersession table.
             */
            final String[] codes = set.getAuthorityCodes();
            sort(codes);
            set.setAuthorityCodes(codes);
        } catch (SQLException exception) {
            throw databaseFailure(CoordinateOperation.class, pair, exception);
        }
        /*
         * Before to return the set, tests the creation of 1 object in order to report early
         * (i.e. now) any problems with SQL statements. Remaining operations will be created
         * only when first needed.
         */
        set.resolve(1);
        return set;
    }

    /**
     * Sorts an array of codes in preference order. This method orders pairwise the codes according
     * the information provided in the supersession table. If the same object is superseded by more
     * than one object, then the most recent one is inserted first. Except for the codes moved as a
     * result of pairwise ordering, this method try to preserve the old ordering of the supplied
     * codes (since deprecated operations should already be last). The ordering is performed in
     * place.
     *
     * @param codes The codes, usually as an array of {@link String}. If the array do not contains
     *              string objects, then the {@link Object#toString} method must returns the code
     *              for each element.
     */
    private void sort(final Object[] codes) throws SQLException, FactoryException {
        if (codes.length <= 1) {
            return; // Nothing to sort.
        }
        final PreparedStatement stmt;
        stmt = prepareStatement("[Supersession]",
                "SELECT SUPERSEDED_BY" +
                " FROM [Supersession]" +
                " WHERE OBJECT_CODE = ?" +
                " ORDER BY SUPERSESSION_YEAR DESC");
        int maxIterations = 15; // For avoiding never-ending loop.
        do {
            boolean changed = false;
            for (int i=0; i<codes.length; i++) {
                final String code = codes[i].toString();
                try (ResultSet result = executeQuery(stmt, code)) {
                    while (result.next()) {
                        final String replacement = getString(result, 1, code);
                        for (int j=i+1; j<codes.length; j++) {
                            final Object candidate = codes[j];
                            if (replacement.equals(candidate.toString())) {
                                /*
                                 * Found a code to move in front of the superceded one.
                                 */
                                System.arraycopy(codes, i, codes, i+1, j-i);
                                codes[i++] = candidate;
                                changed = true;
                            }
                        }
                    }
                }
            }
            if (!changed) {
                return;
            }
        }
        while (--maxIterations != 0);
        LOGGER.finer("Possible recursivity in supersessions.");
    }

    /**
     * Returns a finder which can be used for looking up unidentified objects.
     *
     * @param  type The type of objects to look for.
     * @return A finder to use for looking up unidentified objects.
     * @throws FactoryException if the finder can not be created.
     */
    @Override
    public IdentifiedObjectFinder getIdentifiedObjectFinder(
            final Class<? extends IdentifiedObject> type) throws FactoryException
    {
        return new Finder(buffered, type);
    }

    /**
     * An implementation of {@link IdentifiedObjectFinder} which scans over a smaller set
     * of authority codes.
     * <p>
     * <b>Implementation note:</b> Since this method may be invoked indirectly by
     * {@link LongitudeFirstEpsgFactory}, it must be insensitive to axis order.
     */
    private final class Finder extends IdentifiedObjectFinder {
        /**
         * Creates a new finder backed by the specified <em>buffered</em> authority factory.
         */
        Finder(final AbstractAuthorityFactory buffered, final Class<? extends IdentifiedObject> type) {
            super(buffered, type);
        }

        /**
         * Returns a set of authority codes that <strong>may</strong> identify the same object
         * than the specified one. This implementation tries to get a smaller set than what
         * {@link DirectEpsgFactory#getAuthorityCodes} would produce. Deprecated objects must
         * be last in iteration order.
         */
        @Override
        protected Set<String> getCodeCandidates(final IdentifiedObject object) throws FactoryException {
            String select = "COORD_REF_SYS_CODE";
            String from   = "[Coordinate Reference System]";
            final String where;
            final Comparable<?> code;
            if (object instanceof Ellipsoid) {
                select = "ELLIPSOID_CODE";
                from   = "[Ellipsoid]";
                where  = "SEMI_MAJOR_AXIS";
                code   = ((Ellipsoid) object).getSemiMajorAxis();
            } else {
                IdentifiedObject dependency;
                if (object instanceof GeneralDerivedCRS) {
                    dependency = ((GeneralDerivedCRS) object).getBaseCRS();
                    where      = "SOURCE_GEOGCRS_CODE";
                } else if (object instanceof SingleCRS) {
                    dependency = ((SingleCRS) object).getDatum();
                    where      = "DATUM_CODE";
                } else if (object instanceof GeodeticDatum) {
                    dependency = ((GeodeticDatum) object).getEllipsoid();
                    select     = "DATUM_CODE";
                    from       = "[Datum]";
                    where      = "ELLIPSOID_CODE";
                } else {
                    // Not a supported type. Returns all codes.
                    return super.getCodeCandidates(object);
                }
                /*
                 * Get the dependency from the parent finder, typically a CachingAuthorityFactory.
                 * We do that in order to get the dependency from the cache if possible. Note that
                 * the dependency, if found, will also be stored in the cache. This is desirable
                 * since this method may be invoked (indirectly) in a loop for many CRS objects
                 * sharing the same CS or Datum dependencies for instance.
                 *
                 * Note: an older Geotk version created a new finder from the 'buffered' factory.
                 * However this had the side effect of creating new JDBC connections.
                 */
                dependency = findFromParent(dependency, dependency.getClass());
                if (dependency == null) {
                    // Dependency not found.
                    return Collections.emptySet();
                }
                Identifier id = IdentifiedObjects.getIdentifier(dependency, getAuthority());
                if (id == null || (code = id.getCode()) == null) {
                    // Identifier not found (malformed CRS object?).
                    // Conservatively scans all objects.
                    return super.getCodeCandidates(object);
                }
            }
            /*
             * Build the SQL statement. The code can be any of the following type:
             *
             * - A String, which represent a foreigner key as an integer value.
             *   The search will require an exact match.
             *
             * - A floating point number, in which case the search will be performed
             *   with some tolerance threshold.
             */
            final StringBuilder buffer = new StringBuilder(60);
            buffer.append("SELECT ").append(select).append(" FROM ").append(from).append(" WHERE ").append(where);
            if (code instanceof Number) {
                final double value = ((Number) code).doubleValue();
                final double tolerance = Math.abs(value * COMPARISON_THRESHOLD);
                buffer.append(">=").append(value - tolerance).append(" AND ").append(where)
                      .append("<=").append(value + tolerance);
            } else {
                buffer.append('=').append(code);
            }
            buffer.append(" ORDER BY ABS(DEPRECATED), ");
            if (code instanceof Number) {
                buffer.append("ABS(").append(select).append('-').append(code).append(')');
            } else {
                buffer.append(select); // Only for making order determinist.
            }
            final String sql = adaptSQL(buffer.toString());
            final Set<String> result = new LinkedHashSet<>();
            try (Statement s = connection.createStatement();
                 ResultSet r = s.executeQuery(sql))
            {
                while (r.next()) {
                    result.add(r.getString(1));
                }
            } catch (SQLException exception) {
                throw databaseFailure(Identifier.class, String.valueOf(code), exception);
            }
            return result;
        }
    }

    /**
     * Constructs an exception for recursive calls.
     */
    private static FactoryException recursiveCall(final Class<?> type, final String code) {
        return new FactoryException(Errors.format(Errors.Keys.RECURSIVE_CALL_2, type, code));
    }

    /**
     * Constructs an exception for a database failure.
     */
    private static FactoryException databaseFailure(Class<?> type, String code, SQLException cause) {
        return new FactoryException(Errors.format(Errors.Keys.DATABASE_FAILURE_2, type, code), cause);
    }

    /**
     * Invoked when a new {@link PreparedStatement} is about to be created from a SQL string.
     * Since the <A HREF="http://www.epsg.org">EPSG database</A> is available mainly in MS-Access
     * format, SQL statements are formatted using a syntax specific to this particular database
     * software (for example "{@code SELECT * FROM [Coordinate Reference System]}"). When a
     * subclass targets another database vendor, it must overrides this method in order to adapt
     * the local SQL syntax. For example a subclass connecting to a <cite>PostgreSQL</cite>
     * database could replace the watching braces ({@code '['} and {@code ']'}) by the quote
     * character ({@code '"'}).
     * <p>
     * The default implementation returns the given statement unchanged.
     *
     * @param  statement The statement in MS-Access syntax.
     * @return The SQL statement adapted to the syntax of the target database.
     */
    protected String adaptSQL(final String statement) {
        return statement;
    }

    /**
     * Returns {@code true} if the specified code may be a primary key in some table. This method
     * does not need to check any entry in the database. It should just checks from the syntax if
     * the code looks like a valid EPSG identifier. The default implementation returns {@code true}
     * if all non-space characters are {@linkplain Character#isDigit(char) digits}.
     * <p>
     * When this method returns {@code false}, some {@code createFoo(...)} methods look for the
     * code in the name column instead of the primary key column. This allows to accept the
     * "<cite>NTF (Paris) / France I</cite>" string (for example) in addition to the {@code "27581"}
     * primary key. Both should fetch the same object.
     * <p>
     * If this method returns {@code true} in all cases, then this factory never search for matching
     * names. In such case, an appropriate exception will be thrown in {@code createFoo(...)}
     * methods if the code is not found in the primary key column. Subclasses can overrides this
     * method that way if this is the intended behavior.
     *
     * @param  code The code the inspect.
     * @return {@code true} if the code is probably a primary key.
     * @throws FactoryException if an unexpected error occurred while inspecting the code.
     */
    protected boolean isPrimaryKey(final String code) throws FactoryException {
        final int length = code.length();
        for (int i=0; i<length; i++) {
            final char c = code.charAt(i);
            if (!Character.isDigit(c) && !Character.isSpaceChar(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if it is safe to dispose this factory. This method is invoked indirectly
     * by {@link ThreadedEpsgFactory} after some timeout in order to release resources. This method
     * will block the disposal if some {@linkplain #getAuthorityCodes set of authority codes} are
     * still in use.
     */
    final synchronized boolean canDispose() {
        boolean can = true;
        if (authorityCodes != null) {
            System.gc(); // For cleaning as much weak references as we can before we check them.
            for (final Iterator<Reference<AuthorityCodes>> it=authorityCodes.values().iterator(); it.hasNext();) {
                final AuthorityCodes codes = it.next().get();
                if (codes == null) {
                    it.remove();
                    continue;
                }
                /*
                 * A set of authority codes is still in use. We can't dispose this factory.
                 * But we continue the iteration anyway to cleanup the weak references.
                 */
                can = false;
            }
        }
        return can;
    }

    /**
     * Closes the JDBC connection used by this factory.
     *
     * @param shutdown {@code false} for normal disposal, or {@code true} if
     *        this method is invoked during the process of a JVM shutdown.
     */
    @Override
    protected synchronized void dispose(final boolean shutdown) {
        final boolean isClosed;
        try {
            isClosed = connection.isClosed();
            if (authorityCodes != null) {
                for (final Iterator<Reference<AuthorityCodes>> it=authorityCodes.values().iterator(); it.hasNext();) {
                    final AuthorityCodes set = it.next().get();
                    if (set != null) {
                        set.finalize();
                    }
                    it.remove();
                }
            }
            for (final Iterator<PreparedStatement> it=statements.values().iterator(); it.hasNext();) {
                it.next().close();
                it.remove();
            }
            connection.close();
            super.dispose(shutdown);
        } catch (SQLException exception) {
            /*
             * Do not log if we are in process of JVM shutdown,
             * because the loggers are not available anymore.
             */
            if (!shutdown) {
                Logging.unexpectedException(LOGGER, DirectEpsgFactory.class, "dispose", exception);
            }
            return;
        }
        if (!isClosed && !shutdown) {
            /*
             * The above code was run unconditionally as a safety, even if the connection
             * was already closed. However we will log a message only if we actually closed
             * the connection, otherwise the log records are a little bit misleading.
             */
            final LogRecord record = Loggings.format(Level.FINE, Loggings.Keys.CLOSED_EPSG_DATABASE);
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
        }
    }

    /**
     * Invokes {@link #dispose dispose(false)} when this factory is garbage collected.
     */
    @Override
    protected final void finalize() {
        dispose(false);
    }
}
