/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.openoffice.geoapi;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.AnyConverter;

import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.Datum;
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
import org.opengis.openoffice.XReferencing;

import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.io.wkt.FormattableObject;
import org.geotoolkit.io.wkt.Convention;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.operation.AbstractCoordinateOperation;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.openoffice.MethodInfo;
import org.geotoolkit.openoffice.Formulas;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;
import org.apache.sis.metadata.iso.extent.Extents;


/**
 * Exports methods from the {@link org.geotoolkit.referencing} package as
 * <A HREF="http://www.openoffice.org">OpenOffice</A> add-ins.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Richard Deplanque (IRD)
 * @version 3.20
 *
 * @since 3.20 (derived from 2.2)
 * @module
 */
public final class Referencing extends Formulas implements XReferencing {
    /**
     * The name for the registration of this component.
     * <strong>NOTE:</strong> OpenOffice expects a field with exactly that name; do not rename!
     */
    public static final String __serviceName = "org.opengis.openoffice.Referencing";

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
     * Constructs a default implementation of {@code XReferencing} interface.
     */
    public Referencing() {
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
    }

    /**
     * The service name that can be used to create such an object by a factory.
     */
    @Override
    public String getServiceName() {
        return __serviceName;
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
                    Loggings.Keys.CREATED_COORDINATE_OPERATION_3,
                    IdentifiedObjects.getIdentifier(operation),
                    IdentifiedObjects.getIdentifier(sourceCRS),
                    IdentifiedObjects.getIdentifier(targetCRS));
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
            return ((FormattableObject) object).toWKT(
                    Convention.forIdentifier(authorityString, Convention.OGC), 2);
        }
        if (object instanceof MathTransform) {
            return ((MathTransform) object).toWKT();
        }
        return ((IdentifiedObject) object).toWKT();
    }




    // --------------------------------------------------------------------------------
    //     F O R M U L A   I M P L E M E N T A T I O N S
    // --------------------------------------------------------------------------------

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
        } catch (Throwable exception) {
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
        } catch (Throwable exception) {
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
        } catch (Throwable exception) {
            return getLocalizedMessage(exception);
        }
        if (validArea != null) {
            final InternationalString description = validArea.getDescription();
            if (description != null) {
                return description.toString(getJavaLocale());
            }
            final GeographicBoundingBox box = Extents.getGeographicBoundingBox(validArea);
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
        } catch (Throwable exception) {
            reportException("getBoundingBox", exception, THROW_EXCEPTION);
            return getFailure(4,4);
        }
        final GeographicBoundingBox box = Extents.getGeographicBoundingBox(validArea);
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
        } catch (Throwable exception) {
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
        } catch (Throwable exception) {
            return getLocalizedMessage(exception);
        }
        if (dimension >= 1 && dimension <= cs.getDimension()) {
            return cs.getAxis(dimension - 1).getName().getCode();
        } else {
            return Errors.format(Errors.Keys.INDEX_OUT_OF_BOUNDS_1, dimension);
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
        } catch (Throwable exception) {
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
            return Errors.format(Errors.Keys.UNKNOWN_PARAMETER_1, parameter);
        } catch (Throwable exception) {
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
        } catch (Throwable exception) {
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
        } catch (Throwable exception) {
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
        } catch (Throwable exception) {
            reportException("getAccuracy", exception, THROW_EXCEPTION);
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
        } catch (Throwable exception) {
            reportException("getTransformedCoordinates", exception, THROW_EXCEPTION);
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
                    reportException("getTransformedCoordinates", exception, false);
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
}
