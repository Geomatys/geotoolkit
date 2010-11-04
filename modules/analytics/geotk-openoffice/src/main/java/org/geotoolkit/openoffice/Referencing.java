/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.openoffice;

import java.text.ParseException;
import java.text.DecimalFormatSymbols;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

import com.sun.star.lang.Locale;
import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.comp.loader.FactoryHelper;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.AnyConverter;

import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.Extent;

import org.geotoolkit.measure.Angle;
import org.geotoolkit.measure.Latitude;
import org.geotoolkit.measure.Longitude;
import org.geotoolkit.measure.AngleFormat;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.io.wkt.FormattableObject;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.operation.AbstractCoordinateOperation;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;


/**
 * Exports methods from the {@link org.geotoolkit.referencing} package as
 * <A HREF="http://www.openoffice.org">OpenOffice</A> add-ins.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Richard Deplanque (IRD)
 * @version 3.09
 *
 * @since 3.09 (derived from 2.2)
 * @module
 */
public final class Referencing extends Formulas implements XReferencing {
    /**
     * The name for the registration of this component.<BR>
     * <strong>NOTE:</strong> OpenOffice expects a field with exactly that name; do not rename!
     */
    private static final String __serviceName = "org.geotoolkit.openoffice.Referencing";

    /**
     * The name of the provided service.
     */
    private static final String ADDIN_SERVICE = "com.sun.star.sheet.AddIn";

    /**
     * The decimal separator. Will be computed when a no locale is set.
     */
    private char decimalSeparator = '.';

    /**
     * The pattern for the {@link #angleFormat}. Used in order to avoid creating
     * new formats when the pattern didn't changed.
     */
    private transient String anglePattern;

    /**
     * The format to use for parsing and formatting angles.
     * Will be created only when first needed.
     */
    private transient AngleFormat angleFormat;

    /**
     * The CRS authority factory. Will be created only when first needed.
     */
    private transient CRSAuthorityFactory crsFactory;

    /**
     * The coordinate operation factory. Will be created only when first needed.
     */
    private transient CoordinateOperationFactory opFactory;

    /**
     * The last coordinate operation used. Cached for performance reasons.
     */
    private transient CoordinateOperation lastOperation;

    /**
     * The last source and target CRS used for fetching {@link #lastOperation}.
     */
    private transient String lastSourceCRS, lastTargetCRS;

    /**
     * The last geodetic calculator used, or {@code null} if none. Cached for better
     * performance when many orthodromic distances are computed on the same ellipsoid.
     */
    private transient GeodeticCalculator calculator;

    /**
     * The CRS authority code used for {@link #calculator} setup,
     * or {@code null} if not yet defined.
     */
    private transient String calculatorCRS;

    /**
     * Constructs a default implementation of {@code XReferencing} interface.
     */
    public Referencing() {
        methods.put("getValueAngle", new MethodInfo("Text", "VALUE.ANGLE",
            "Converts text in degrees-minutes-seconds to an angle in decimal degrees.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "text",       "The text to be converted to an angle.",
                "pattern",    "The text that describes the format (example: \"D°MM.m'\")."
        }));
        methods.put("getTextAngle", new MethodInfo("Text", "TEXT.ANGLE",
            "Converts an angle to text according to a given format.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "value",      "The angle value (in decimal degrees) to be converted.",
                "pattern",    "The text that describes the format (example: \"D°MM.m'\")."
        }));
        methods.put("getTextLongitude", new MethodInfo("Text", "TEXT.LONGITUDE",
            "Converts a longitude to text according to a given format.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "value",      "The longitude value (in decimal degrees) to be converted.",
                "pattern",    "The text that describes the format (example: \"D°MM.m'\")."
        }));
        methods.put("getTextLatitude", new MethodInfo("Text", "TEXT.LATITUDE",
            "Converts a latitude to text according to a given format.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "value",      "The latitude value (in decimal degrees) to be converted.",
                "pattern",    "The text that describes the format (example: \"D°MM.m'\")."
        }));
        methods.put("getDescription", new MethodInfo("Referencing", "CRS.DESCRIPTION",
            "Returns a description for an object identified by the given authority code.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority."
        }));
        methods.put("getScope", new MethodInfo("Referencing", "CRS.SCOPE",
            "Returns the scope for an identified object.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority."
        }));
        methods.put("getValidArea", new MethodInfo("Referencing", "CRS.VALID.AREA",
            "Returns the valid area as a textual description for an identified object.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority."
        }));
        methods.put("getBoundingBox", new MethodInfo("Referencing", "CRS.BOUNDING.BOX",
            "Returns the valid area as a geographic bounding box for an identified object.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority."
        }));
        methods.put("getRemarks", new MethodInfo("Referencing", "CRS.REMARKS",
            "Returns the remarks for an identified object.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority."
        }));
        methods.put("getAxis", new MethodInfo("Referencing", "CRS.AXIS",
            "Returns the axis name for the specified dimension in an identified object.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority.",
                "dimension",  "The dimension (1, 2, ...)."
        }));
        methods.put("getParameter", new MethodInfo("Referencing", "CRS.PARAMETER",
            "Returns the value for a coordinate reference system parameter.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority.",
                "parameter",  "The parameter name (e.g. \"False easting\")."
        }));
        methods.put("getWKT", new MethodInfo("Referencing", "CRS.WKT",
            "Returns the Well Know Text (WKT) for an identified object.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority.",
                "authority",  "The authority name for choice of parameter names."
        }));
        methods.put("getTransformWKT", new MethodInfo("Referencing", "TRANSFORM.WKT",
            "Returns the Well Know Text (WKT) of a transformation between two coordinate reference systems.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "code",       "The code allocated by authority.",
                "authority",  "The authority name for choice of parameter names."
        }));
        methods.put("getAccuracy", new MethodInfo("Referencing", "TRANSFORM.ACCURACY",
            "Returns the accuracy of a transformation between two coordinate reference systems.",
            new String[] {
                "xOptions",    "Provided by OpenOffice.",
                "source CRS",  "The source coordinate reference system.",
                "target CRS",  "The target coordinate reference system."
        }));
        methods.put("getTransformedCoordinates", new MethodInfo("Referencing", "TRANSFORM.COORD",
            "Transform coordinates from the given source CRS to the given target CRS.",
            new String[] {
                "xOptions",    "Provided by OpenOffice.",
                "coordinates", "The coordinate values to transform.",
                "source CRS",  "The source coordinate reference system.",
                "target CRS",  "The target coordinate reference system."
        }));
        methods.put("getOrthodromicDistance", new MethodInfo("Referencing", "ORTHODROMIC.DISTANCE",
            "Computes the orthodromic distance and azimuth between two coordinates.",
            new String[] {
                "xOptions",    "Provided by OpenOffice.",
                "source",      "The source positions.",
                "target",      "The target positions.",
                "CRS",         "Authority code of the coordinate reference system."
        }));
        methods.put("getOrthodromicForward", new MethodInfo("Referencing", "ORTHODROMIC.FORWARD",
            "Computes the coordinates after a displacement of the specified distance.",
            new String[] {
                "xOptions",    "Provided by OpenOffice.",
                "source",      "The source positions.",
                "displacement","The distance and azimuth.",
                "CRS",         "Authority code of the coordinate reference system."
        }));
    }

    /**
     * Returns a factory for creating the service.
     * This method is called by the {@code com.sun.star.comp.loader.JavaLoader}; do not rename!
     *
     * @param   implementation The name of the implementation for which a service is desired.
     * @param   factories      The service manager to be used if needed.
     * @param   registry       The registry key
     * @return  A factory for creating the component.
     */
    public static XSingleServiceFactory __getServiceFactory(
                                        final String               implementation,
                                        final XMultiServiceFactory factories,
                                        final XRegistryKey         registry)
    {
        if (implementation.equals(Referencing.class.getName())) {
            return FactoryHelper.getServiceFactory(Referencing.class, __serviceName, factories, registry);
        }
        return null;
    }

    /**
     * Writes the service information into the given registry key.
     * This method is called by the {@code com.sun.star.comp.loader.JavaLoader}; do not rename!
     *
     * @param  registry     The registry key.
     * @return {@code true} if the operation succeeded.
     */
    public static boolean __writeRegistryServiceInfo(final XRegistryKey registry) {
        final String classname = Referencing.class.getName();
        return FactoryHelper.writeRegistryServiceInfo(classname, __serviceName, registry)
            && FactoryHelper.writeRegistryServiceInfo(classname, ADDIN_SERVICE, registry);
    }

    /**
     * The service name that can be used to create such an object by a factory.
     */
    @Override
    public String getServiceName() {
        return __serviceName;
    }

    /**
     * Provides the supported service names of the implementation, including also
     * indirect service names.
     *
     * @return Sequence of service names that are supported.
     */
    @Override
    public String[] getSupportedServiceNames() {
        return new String[] {ADDIN_SERVICE, __serviceName};
    }

    /**
     * Tests whether the specified service is supported, i.e. implemented by the implementation.
     *
     * @param  name Name of service to be tested.
     * @return {@code true} if the service is supported, {@code false} otherwise.
     */
    @Override
    public boolean supportsService(final String name) {
        return name.equals(ADDIN_SERVICE) || name.equals(__serviceName);
    }

    /**
     * Sets the locale to be used by this object.
     */
    @Override
    public void setLocale(final Locale locale) {
        anglePattern = null;
        angleFormat  = null;
        super.setLocale(locale);
    }




    // --------------------------------------------------------------------------------
    //     H E L P E R   M E T H O D S
    // --------------------------------------------------------------------------------

    /**
     * Returns the CRS authority factory.
     */
    private CRSAuthorityFactory crsFactory() {
        if (crsFactory == null) {
            crsFactory = CRS.getAuthorityFactory(null);
        }
        return crsFactory;
    }

    /**
     * Returns the coordinate operation for the two specified CRS.
     *
     * @param  method The method invoking this {@code #getCoordinateOperation} method.
     *                For logging purpose only.
     * @param  source The source CRS authority code.
     * @param  target The target CRS authority code.
     * @return The coordinate operation.
     * @throws FactoryException if the coordinate operation can't be created.
     */
    private CoordinateOperation getCoordinateOperation(final String method,
                                                       final String source,
                                                       final String target)
            throws FactoryException
    {
        if (lastOperation!=null && lastSourceCRS.equals(source) && lastTargetCRS.equals(target)) {
            return lastOperation;
        }
        final CoordinateReferenceSystem sourceCRS;
        final CoordinateReferenceSystem targetCRS;
        final CoordinateOperation       operation;
        final CRSAuthorityFactory       crsFactory = crsFactory();
        sourceCRS = crsFactory.createCoordinateReferenceSystem(source);
        targetCRS = crsFactory.createCoordinateReferenceSystem(target);
        if (opFactory == null) {
            opFactory = CRS.getCoordinateOperationFactory(Boolean.TRUE);
        }
        operation = opFactory.createOperation(sourceCRS, targetCRS);
        final Logger logger = getLogger();
        if (logger.isLoggable(Level.FINER)) {
            final LogRecord record = Loggings.format(Level.FINER,
                    Loggings.Keys.CREATED_COORDINATE_OPERATION_$3,
                    CRS.getDeclaredIdentifier(operation),
                    CRS.getDeclaredIdentifier(sourceCRS),
                    CRS.getDeclaredIdentifier(targetCRS));
            record.setSourceClassName(Referencing.class.getName());
            record.setSourceMethodName(method);
            logger.log(record);
        }
        lastSourceCRS = source;
        lastTargetCRS = target;
        lastOperation = operation;
        return operation;
    }

    /**
     * Returns the Well Know Text (WKT) for the specified object using the parameter names
     * from the specified authority.
     *
     * @param  object The object to format.
     * @param  authority The authority name for choice of parameter names. Usually "OGC".
     * @return The Well Know Text (WKT) for the specified object.
     * @throws IllegalArgumentException if {@code authority} is not a string value or void.
     * @throws UnsupportedOperationException if the object can't be formatted.
     */
    private static String toWKT(final Object object, final Object authority)
            throws IllegalArgumentException, UnsupportedOperationException
    {
        final String authorityString;
        if (AnyConverter.isVoid(authority)) {
            authorityString = "OGC";
        } else {
            authorityString = AnyConverter.toString(authority);
        }
        if (object instanceof FormattableObject) {
            return ((FormattableObject) object).toWKT(Citations.fromName(authorityString), 2);
        }
        if (object instanceof MathTransform) {
            return ((MathTransform) object).toWKT();
        }
        return ((IdentifiedObject) object).toWKT();
    }

    /**
     * Returns the geodetic calculator for the specified CRS, datum or ellipsoid.
     * This method caches the last calculator used for better performance when many
     * orthodromic distances are computed on the same ellipsoid.
     *
     * @throws IllegalArgumentException if {@code authorityCode} is not a string value or void.
     * @throws FactoryException if the geodetic calculator can't be created.
     */
    private GeodeticCalculator getGeodeticCalculator(final Object authorityCode)
            throws IllegalArgumentException, FactoryException
    {
        final String authorityString;
        if (AnyConverter.isVoid(authorityCode)) {
            authorityString = "EPSG:4326";
        } else {
            authorityString = AnyConverter.toString(authorityCode);
        }
        if (calculatorCRS==null || !calculatorCRS.equals(authorityString)) {
            final IdentifiedObject object = crsFactory().createObject(authorityString);
            if (object instanceof Ellipsoid) {
                calculator = new GeodeticCalculator((Ellipsoid) object);
            } else if (object instanceof GeodeticDatum) {
                calculator = new GeodeticCalculator(((GeodeticDatum) object).getEllipsoid());
            } else if (object instanceof CoordinateReferenceSystem) {
                calculator = new GeodeticCalculator((CoordinateReferenceSystem) object);
            } else {
                throw new FactoryException(Errors.format(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM));
            }
            calculatorCRS = authorityString;
        }
        return calculator;
    }

    /**
     * Returns the angle format to use for the specified pattern.
     *
     * @param  pattern he text that describes the format (example: "D°MM.m'").
     * @return The angle format.
     * @throws IllegalArgumentException if {@code pattern} is not a string value or void.
     */
    private AngleFormat getAngleFormat(final Object pattern) throws IllegalArgumentException {
        final String patternString;
        if (AnyConverter.isVoid(pattern)) {
            patternString = "D°MM'SS.s\"";
        } else {
            patternString = AnyConverter.toString(pattern);
        }
        if (angleFormat == null) {
            final java.util.Locale locale = getJavaLocale();
            angleFormat      = new AngleFormat(patternString, locale);
            anglePattern     = patternString;
            decimalSeparator = new DecimalFormatSymbols(locale).getDecimalSeparator();
        } else if (!patternString.equals(anglePattern)) {
            angleFormat.applyPattern(patternString);
            anglePattern = patternString;
        }
        return angleFormat;
    }




    // --------------------------------------------------------------------------------
    //     F O R M U L A   I M P L E M E N T A T I O N S
    // --------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValueAngle(final XPropertySet xOptions,
                                final String       text,
                                final Object       pattern)
            throws IllegalArgumentException
    {
        final AngleFormat angleFormat = getAngleFormat(pattern);
        try {
            return angleFormat.parse(text).degrees();
        } catch (ParseException exception) {
            /*
             * Parse failed. Tries to replace the dot by the decimal separator in current locale.
             */
            final String localized = text.replace('.', decimalSeparator);
            if (!text.equals(localized)) try {
                return angleFormat.parse(localized).degrees();
            } catch (ParseException ignore) {
                // Ignore; will throw the first exception.
            }
            throw new IllegalArgumentException(getLocalizedMessage(exception));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextAngle(final XPropertySet xOptions,
                               final double       value,
                               final Object       pattern)
            throws IllegalArgumentException
    {
        return getAngleFormat(pattern).format(new Angle(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextLongitude(final XPropertySet xOptions,
                                   final double       value,
                                   final Object       pattern)
            throws IllegalArgumentException
    {
        return getAngleFormat(pattern).format(new Longitude(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextLatitude(final XPropertySet xOptions,
                                  final double       value,
                                  final Object       pattern)
            throws IllegalArgumentException
    {
        return getAngleFormat(pattern).format(new Latitude(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(final XPropertySet xOptions,
                                 final String authorityCode)
    {
        final InternationalString description;
        try {
            description = crsFactory().getDescriptionText(authorityCode);
        } catch (Exception exception) {
            return getLocalizedMessage(exception);
        }
        return (description != null) ? description.toString(getJavaLocale()) : emptyString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScope(final XPropertySet xOptions,
                           final String authorityCode)
    {
        final IdentifiedObject object;
        try {
            object = crsFactory().createObject(authorityCode);
        } catch (Exception exception) {
            return getLocalizedMessage(exception);
        }
        final InternationalString description;
        if (object instanceof Datum) {
            description = ((Datum) object).getScope();
        } else if (object instanceof ReferenceSystem) {
            description = ((ReferenceSystem) object).getScope();
        } else if (object instanceof CoordinateOperation) {
            description = ((CoordinateOperation) object).getScope();
        } else {
            description = null;
        }
        return (description != null) ? description.toString(getJavaLocale()) : emptyString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValidArea(final XPropertySet xOptions,
                               final String authorityCode)
    {
        Extent validArea;
        try {
            validArea = crsFactory().createCoordinateReferenceSystem(authorityCode).getDomainOfValidity();
        } catch (Exception exception) {
            return getLocalizedMessage(exception);
        }
        if (validArea != null) {
            final InternationalString description = validArea.getDescription();
            if (description != null) {
                return description.toString(getJavaLocale());
            }
            final GeographicBoundingBox box = DefaultExtent.getGeographicBoundingBox(validArea);
            if (box != null) {
                return box.toString();
            }
        }
        return emptyString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] getBoundingBox(final XPropertySet xOptions,
                                     final String authorityCode)
    {
        Extent validArea;
        try {
            validArea = crsFactory().createCoordinateReferenceSystem(authorityCode).getDomainOfValidity();
        } catch (Exception exception) {
            reportException("getBoundingBox", exception);
            return getFailure(4,4);
        }
        final GeographicBoundingBox box = DefaultExtent.getGeographicBoundingBox(validArea);
        if (box == null) {
            return getFailure(4,4);
        }
        return new double[][] {
            new double[] {box.getNorthBoundLatitude(),
                          box.getWestBoundLongitude()},
            new double[] {box.getSouthBoundLatitude(),
                          box.getEastBoundLongitude()}};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemarks(final XPropertySet xOptions,
                             final String authorityCode)
    {
        final IdentifiedObject object;
        try {
            object = crsFactory().createObject(authorityCode);
        } catch (Exception exception) {
            return getLocalizedMessage(exception);
        }
        final InternationalString remarks = object.getRemarks();
        return (remarks!=null) ? remarks.toString(getJavaLocale()) : emptyString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAxis(final XPropertySet xOptions,
                          final String  authorityCode,
                          final int         dimension)
    {
        CoordinateSystem cs;
        try {
            cs = crsFactory().createCoordinateReferenceSystem(authorityCode).getCoordinateSystem();
        } catch (Exception exception) {
            return getLocalizedMessage(exception);
        }
        if (dimension >= 1 && dimension <= cs.getDimension()) {
            return cs.getAxis(dimension - 1).getName().getCode();
        } else {
            return Errors.format(Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, dimension);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParameter(final XPropertySet xOptions,
                               final String  authorityCode,
                               final String      parameter)
    {
        final IdentifiedObject object;
        try {
            object = crsFactory().createObject(authorityCode);
        } catch (FactoryException exception) {
            return getLocalizedMessage(exception);
        }
        final ParameterValueGroup parameters;
        if (object instanceof SingleOperation) {
            parameters = ((SingleOperation) object).getParameterValues();
        } else if (object instanceof GeneralDerivedCRS) {
            parameters = ((GeneralDerivedCRS) object).getConversionFromBase().getParameterValues();
        } else {
            parameters = ParameterGroup.EMPTY;
        }
        try {
            return parameters.parameter(parameter).getValue();
        } catch (ParameterNotFoundException exception) {
            return Errors.format(Errors.Keys.UNKNOWN_PARAMETER_$1, parameter);
        } catch (RuntimeException exception) {
            return getLocalizedMessage(exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWKT(final XPropertySet xOptions,
                         final String  authorityCode,
                         final Object  authority)
    {
        try {
            return toWKT(crsFactory().createObject(authorityCode), authority);
        } catch (Exception exception) {
            return getLocalizedMessage(exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransformWKT(final XPropertySet xOptions,
                                  final String       sourceCRS,
                                  final String       targetCRS,
                                  final Object       authority)
    {
        try {
            return toWKT(getCoordinateOperation("getTransformWKT", sourceCRS, targetCRS).getMathTransform(), authority);
        } catch (Exception exception) {
            return getLocalizedMessage(exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAccuracy(final XPropertySet xOptions,
                              final String       sourceCRS,
                              final String       targetCRS)
    {
        final CoordinateOperation operation;
        try {
             operation = getCoordinateOperation("getAccuracy", sourceCRS, targetCRS);
        } catch (FactoryException exception) {
            reportException("getAccuracy", exception);
            return Double.NaN;
        }
        return AbstractCoordinateOperation.getAccuracy(operation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] getTransformedCoordinates(final XPropertySet xOptions,
                                                final double[][]   coordinates,
                                                final String       sourceCRS,
                                                final String       targetCRS)
    {
        final CoordinateOperation operation;
        try {
             operation = getCoordinateOperation("getTransformedCoordinates", sourceCRS, targetCRS);
        } catch (FactoryException exception) {
            reportException("getTransformedCoordinates", exception);
            return getFailure(coordinates.length, 2);
        }
        /*
         * We now have every information needed for applying the coordinate operations.
         * Creates a result array and transform every point.
         */
        boolean failureReported = false;
        final MathTransform         mt       = operation.getMathTransform();
        final GeneralDirectPosition sourcePt = new GeneralDirectPosition(mt.getSourceDimensions());
        final GeneralDirectPosition targetPt = new GeneralDirectPosition(mt.getTargetDimensions());
        final double[][] result = new double[coordinates.length][];
        for (int j=0; j<coordinates.length; j++) {
            double[] coords = coordinates[j];
            if (coords == null) {
                continue;
            }
            for (int i=sourcePt.ordinates.length; --i>=0;) {
                sourcePt.ordinates[i] = (i < coords.length) ? coords[i] : 0;
            }
            final DirectPosition pt;
            try {
                pt = mt.transform(sourcePt, targetPt);
            } catch (TransformException exception) {
                /*
                 * The coordinate operation failed for this particular point. But maybe it will
                 * succeed for an other point. Set the values to NaN and continue the loop. Note:
                 * we will report the failure for logging purpose, but only the first one since
                 * all subsequent failures are likely to be the same one.
                 */
                if (!failureReported) {
                    reportException("getTransformedCoordinates", exception);
                    failureReported = true;
                }
                continue;
            }
            coords = new double[pt.getDimension()];
            for (int i=coords.length; --i>=0;) {
                coords[i] = pt.getOrdinate(i);
            }
            result[j] = coords;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] getOrthodromicDistance(final XPropertySet xOptions,
                                             final double[][]   source,
                                             final double[][]   target,
                                             final Object       CRS)
    {
        final GeodeticCalculator calculator;
        try {
            calculator = getGeodeticCalculator(CRS);
        } catch (Exception exception) {
            reportException("getOrthodromicDistance", exception);
            return getFailure(source.length, 2);
        }
        boolean failureReported = false;
        final int dim = calculator.getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
        final GeneralDirectPosition sourcePt = new GeneralDirectPosition(dim);
        final GeneralDirectPosition targetPt = new GeneralDirectPosition(dim);
        final double[][] result = new double[getLength(source, target)][];
        for (int j=0; j<result.length; j++) {
            final double[] src = source[j % source.length];
            final double[] dst = target[j % target.length];
            if (src == null || dst == null) {
                continue;
            }
            for (int i=dim; --i>=0;) {
                sourcePt.ordinates[i] = (i<src.length) ? src[i] : 0;
                targetPt.ordinates[i] = (i<dst.length) ? dst[i] : 0;
            }
            try {
                calculator.setStartingPosition   (sourcePt);
                calculator.setDestinationPosition(targetPt);
            } catch (TransformException exception) {
                if (!failureReported) {
                    reportException("getOrthodromicDistance", exception);
                    failureReported = true;
                }
                continue;
            }
            result[j] = new double[] {
                calculator.getOrthodromicDistance(),
                calculator.getAzimuth()
            };
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("fallthrough")
    public double[][] getOrthodromicForward(final XPropertySet xOptions,
                                            final double[][]   source,
                                            final double[][]   displacement,
                                            final Object       CRS)
    {
        final GeodeticCalculator calculator;
        try {
            calculator = getGeodeticCalculator(CRS);
        } catch (Exception exception) {
            reportException("getOrthodromicForward", exception);
            return getFailure(source.length, 2);
        }
        boolean failureReported = false;
        final int dim = calculator.getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
        final GeneralDirectPosition sourcePt = new GeneralDirectPosition(dim);
        final double[][] result = new double[getLength(source, displacement)][];
        for (int j=0; j<result.length; j++) {
            final double[] src = source      [j % source.length];
            final double[] mov = displacement[j % displacement.length];
            if (src == null || mov == null) {
                continue;
            }
            for (int i=dim; --i>=0;) {
                sourcePt.ordinates[i] = (i < src.length) ? src[i] : 0;
            }
            double distance=0, azimuth=0;
            switch (mov.length) {
                default:                    // Fall through
                case 2:  azimuth  = mov[1]; // Fall through
                case 1:  distance = mov[0]; // Fall through
                case 0:  break;
            }
            final DirectPosition targetPt;
            try {
                calculator.setStartingPosition(sourcePt);
                calculator.setDirection(azimuth, distance);
                targetPt = calculator.getDestinationPosition();
            } catch (TransformException exception) {
                if (!failureReported) {
                    reportException("getOrthodromicForward", exception);
                    failureReported = true;
                }
                continue;
            }
            result[j] = targetPt.getCoordinate();
        }
        return result;
    }
}
