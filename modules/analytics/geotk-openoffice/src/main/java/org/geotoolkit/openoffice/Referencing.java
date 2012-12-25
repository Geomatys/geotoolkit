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
package org.geotoolkit.openoffice;

import java.text.ParseException;
import java.text.DecimalFormatSymbols;

import com.sun.star.lang.Locale;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.AnyConverter;

import org.opengis.util.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;

import org.apache.sis.measure.Angle;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.measure.AngleFormat;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.geometry.GeneralDirectPosition;
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
     * The name for the registration of this component.
     * <strong>NOTE:</strong> OpenOffice expects a field with exactly that name; do not rename!
     */
    static final String __serviceName = "org.geotoolkit.openoffice.Referencing";

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
     * The service name that can be used to create such an object by a factory.
     */
    @Override
    public String getServiceName() {
        return __serviceName;
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
                exception.addSuppressed(ignore);
            }
            reportException("getValueAngle", exception, THROW_EXCEPTION);
            return Double.NaN;
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
    public double[][] getOrthodromicDistance(final XPropertySet xOptions,
                                             final double[][]   source,
                                             final double[][]   target,
                                             final Object       CRS)
    {
        final GeodeticCalculator calculator;
        try {
            calculator = getGeodeticCalculator(CRS);
        } catch (Throwable exception) {
            reportException("getOrthodromicDistance", exception, THROW_EXCEPTION);
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
                    reportException("getOrthodromicDistance", exception, false);
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
        } catch (Throwable exception) {
            reportException("getOrthodromicForward", exception, THROW_EXCEPTION);
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
                    reportException("getOrthodromicForward", exception, false);
                    failureReported = true;
                }
                continue;
            }
            result[j] = targetPt.getCoordinate();
        }
        return result;
    }
}
