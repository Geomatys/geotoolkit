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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation;

import java.util.Map;
import java.util.List;
import javax.measure.unit.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.vecmath.SingularMatrixException;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Classes;
import org.apache.sis.metadata.iso.extent.Extents;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.Matrix4;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.apache.sis.internal.referencing.AxisDirections;
import org.geotoolkit.internal.referencing.OperationContext;
import org.geotoolkit.internal.referencing.VerticalDatumTypes;

import static java.util.Collections.singletonList;
import static org.apache.sis.measure.Units.MILLISECOND;
import static org.geotoolkit.referencing.CRS.equalsIgnoreMetadata;
import static org.geotoolkit.referencing.CRS.equalsApproximatively;
import static org.geotoolkit.referencing.IdentifiedObjects.nameMatches;
import static org.geotoolkit.internal.referencing.CRSUtilities.getGreenwichLongitude;


/**
 * Creates {@linkplain CoordinateOperation coordinate operations}. This factory is capable to find
 * coordinate {@linkplain Transformation transformations} or {@linkplain Conversion conversions}
 * between two {@linkplain CoordinateReferenceSystem coordinate reference systems}. It delegates
 * most of its work to one or many of {@code createOperationStep} methods. Subclasses can
 * override those methods in order to extend the factory capability to some more CRS.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 1.2
 * @module
 */
@ThreadSafe
public class DefaultCoordinateOperationFactory extends AbstractCoordinateOperationFactory {
    /**
     * The operation to use by {@link #createTransformationStep(GeographicCRS,GeographicCRS)} for
     * datum shift. This string can have one of the following values:
     * <p>
     * <ul>
     *   <li>{@code "Abridged Molodensky"} for the abridged Molodensky transformation.</li>
     *   <li>{@code "Molodensky"} for the Molodensky transformation.</li>
     *   <li>{@code null} for performing datum shifts is geocentric coordinates.</li>
     * </ul>
     */
    private final String molodenskyMethod;

    /**
     * {@code true} if datum shift are allowed even if no Bursa Wolf parameters is available.
     */
    private final boolean lenientDatumShift;

    /**
     * Constructs a coordinate operation factory using the default factories.
     */
    public DefaultCoordinateOperationFactory() {
        this(EMPTY_HINTS);
    }

    /**
     * Constructs a coordinate operation factory using the specified hints.
     * This constructor recognizes the {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS},
     * {@link Hints#DATUM_FACTORY DATUM} and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM}
     * {@code FACTORY} hints.
     *
     * @param userHints The hints, or {@code null} if none.
     */
    public DefaultCoordinateOperationFactory(final Hints userHints) {
        super(userHints);
        //
        // Default hints values
        //
        String  molodenskyMethod  = "Molodensky"; // Alternative: "Abridged Molodensky"
        boolean lenientDatumShift = false;
        //
        // Fetches the user-supplied hints
        //
        if (userHints != null) {
            Object candidate = userHints.get(Hints.DATUM_SHIFT_METHOD);
            if (candidate instanceof String) {
                molodenskyMethod = (String) candidate;
                if (molodenskyMethod.trim().equalsIgnoreCase("Geocentric")) {
                    molodenskyMethod = null;
                }
            }
            candidate = userHints.get(Hints.LENIENT_DATUM_SHIFT);
            if (candidate instanceof Boolean) {
                lenientDatumShift = ((Boolean) candidate).booleanValue();
            }
        }
        //
        // Stores the retained hints
        //
        this.molodenskyMethod  = molodenskyMethod;
        this.lenientDatumShift = lenientDatumShift;
        this.hints.put(Hints.DATUM_SHIFT_METHOD,  (molodenskyMethod != null) ? molodenskyMethod : "Geocentric");
        this.hints.put(Hints.LENIENT_DATUM_SHIFT, Boolean.valueOf(lenientDatumShift));
    }

    /**
     * Invoked by {@link org.geotoolkit.factory.FactoryRegistry} in order to set the ordering relative
     * to other factories. The current implementation specifies that this factory should defer to
     * {@link CachingCoordinateOperationFactory}.
     *
     * @since 3.00
     */
    @Override
    protected void setOrdering(final Organizer organizer) {
        super.setOrdering(organizer);
        organizer.after(CachingCoordinateOperationFactory.class, false);
    }

    /**
     * Returns an operation for conversion or transformation between two coordinate reference
     * systems. If an operation exists, it is returned. If more than one operation exists, the
     * default is returned. If no operation exists, then the exception is thrown.
     * <p>
     * The default implementation inspects the CRS and delegates the work to one or
     * many {@code createOperationStep(...)} methods. This method fails if no path
     * between the CRS is found.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws OperationNotFoundException if no operation path was found from {@code sourceCRS}
     *         to {@code targetCRS}.
     * @throws FactoryException if the operation creation failed for some other reason.
     */
    @Override
    public CoordinateOperation createOperation(final CoordinateReferenceSystem sourceCRS,
                                               final CoordinateReferenceSystem targetCRS)
            throws OperationNotFoundException, FactoryException
    {
        ensureNonNull("sourceCRS", sourceCRS);
        ensureNonNull("targetCRS", targetCRS);
        GeographicBoundingBox areaOfInterest = OperationContext.getAreaOfInterest();
        if (areaOfInterest != null) {
            return createOperation(sourceCRS, targetCRS, areaOfInterest);
        }
        areaOfInterest = Extents.intersection(
                CRS.getGeographicBoundingBox(sourceCRS),
                CRS.getGeographicBoundingBox(targetCRS));
        OperationContext.setAreaOfInterest(areaOfInterest);
        try {
            return createOperation(sourceCRS, targetCRS, areaOfInterest);
        } finally {
            OperationContext.clear();
        }
    }

    /**
     * Implementation of {@link #createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.
     * This is a private API for now, but may become public later.
     *
     * @todo HACK: For now, the area of interest is fetched from the OperationContext ThreadLocal, and
     *             the given {@code areaOfInterest} argument is ignored. However in a future version,
     *             we will use the argument and ignore the ThreadLocal. The ThreadLocal hack is used
     *             for avoiding to break the API (i.e. all protected methods in this class). We will
     *             try to fix this issue in a cleaner way in Apache SIS.
     *
     * @param areaOfInterest Ignored for now (see "hack" in above javadoc).
     */
    private CoordinateOperation createOperation(final CoordinateReferenceSystem sourceCRS,
                                                final CoordinateReferenceSystem targetCRS,
                                                final GeographicBoundingBox areaOfInterest)
            throws OperationNotFoundException, FactoryException
    {
        if (equalsIgnoreMetadata(sourceCRS, targetCRS)) {
            final int dim  = getDimension(sourceCRS);
            assert    dim == getDimension(targetCRS) : dim;
            return createFromAffineTransform(IDENTITY, sourceCRS, targetCRS, Matrices.create(dim+1));
        } else {
            // Query the database (if any) before to try to find the operation by ourself.
            final CoordinateOperation candidate = createFromDatabase(sourceCRS, targetCRS);
            if (candidate != null) {
                return candidate;
            }
        }
        /*
         * Now perform "instanceof" checks for all supported types. We check CompoundCRS first
         * because experience shows that it produces simpler transformation chains than if we
         * check them last.
         */
        ////////////////////////////////////////////
        ////                                    ////
        ////     Compound  -->  various CRS     ////
        ////                                    ////
        ////////////////////////////////////////////
        if (sourceCRS instanceof CompoundCRS) {
            final CompoundCRS source = (CompoundCRS) sourceCRS;
            if (targetCRS instanceof CompoundCRS) {
                return createOperationStep(source, (CompoundCRS) targetCRS);
            }
            if (targetCRS instanceof SingleCRS) {
                return createOperationStep(source, (SingleCRS) targetCRS);
            }
        }
        if (targetCRS instanceof CompoundCRS) {
            final CompoundCRS target = (CompoundCRS) targetCRS;
            if (sourceCRS instanceof SingleCRS) {
                return createOperationStep((SingleCRS) sourceCRS, target);
            }
        }
        /////////////////////////////////////////////////////////////////////
        ////                                                             ////
        ////     Geographic  -->  Geographic, Projected or Geocentric    ////
        ////                                                             ////
        /////////////////////////////////////////////////////////////////////
        if (sourceCRS instanceof GeographicCRS) {
            final GeographicCRS source = (GeographicCRS) sourceCRS;
            if (targetCRS instanceof GeographicCRS) {
                return createOperationStep(source, (GeographicCRS) targetCRS);
            }
            if (targetCRS instanceof ProjectedCRS) {
                return createOperationStep(source, (ProjectedCRS) targetCRS);
            }
            if (targetCRS instanceof GeocentricCRS) {
                return createOperationStep(source, (GeocentricCRS) targetCRS);
            }
            if (targetCRS instanceof VerticalCRS) {
                return createOperationStep(source, (VerticalCRS) targetCRS);
            }
        }
        /////////////////////////////////////////////////////////
        ////                                                 ////
        ////     Projected  -->  Projected or Geographic     ////
        ////                                                 ////
        /////////////////////////////////////////////////////////
        if (sourceCRS instanceof ProjectedCRS) {
            final ProjectedCRS source = (ProjectedCRS) sourceCRS;
            if (targetCRS instanceof ProjectedCRS) {
                return createOperationStep(source, (ProjectedCRS) targetCRS);
            }
            if (targetCRS instanceof GeographicCRS) {
                return createOperationStep(source, (GeographicCRS) targetCRS);
            }
        }
        //////////////////////////////////////////////////////////
        ////                                                  ////
        ////     Geocentric  -->  Geocentric or Geographic    ////
        ////                                                  ////
        //////////////////////////////////////////////////////////
        if (sourceCRS instanceof GeocentricCRS) {
            final GeocentricCRS source = (GeocentricCRS) sourceCRS;
            if (targetCRS instanceof GeocentricCRS) {
                return createOperationStep(source, (GeocentricCRS) targetCRS);
            }
            if (targetCRS instanceof GeographicCRS) {
                return createOperationStep(source, (GeographicCRS) targetCRS);
            }
        }
        /////////////////////////////////////////
        ////                                 ////
        ////     Vertical  -->  Vertical     ////
        ////                                 ////
        /////////////////////////////////////////
        if (sourceCRS instanceof VerticalCRS) {
            final VerticalCRS source = (VerticalCRS) sourceCRS;
            if (targetCRS instanceof VerticalCRS) {
                return createOperationStep(source, (VerticalCRS) targetCRS);
            }
        }
        /////////////////////////////////////////
        ////                                 ////
        ////     Temporal  -->  Temporal     ////
        ////                                 ////
        /////////////////////////////////////////
        if (sourceCRS instanceof TemporalCRS) {
            final TemporalCRS source = (TemporalCRS) sourceCRS;
            if (targetCRS instanceof TemporalCRS) {
                return createOperationStep(source, (TemporalCRS) targetCRS);
            }
        }
        //////////////////////////////////////////////////////////////////
        ////                                                          ////
        ////     Any coordinate reference system -->  Derived CRS     ////
        ////                                                          ////
        //////////////////////////////////////////////////////////////////
        if (targetCRS instanceof GeneralDerivedCRS) {
            // Note: this code is identical to 'createOperationStep(GeographicCRS, ProjectedCRS)'
            //       except that the later invokes directly the right method for 'step1' instead
            //       of invoking 'createOperation' recursively.
            final GeneralDerivedCRS target = (GeneralDerivedCRS) targetCRS;
            return concatenate(createOperation(sourceCRS, target.getBaseCRS()),
                               target.getConversionFromBase());
        }
        //////////////////////////////////////////////////////////////////
        ////                                                          ////
        ////     Derived CRS -->  Any coordinate reference system     ////
        ////                                                          ////
        //////////////////////////////////////////////////////////////////
        if (sourceCRS instanceof GeneralDerivedCRS) {
            // Note: this code is identical to 'createOperationStep(ProjectedCRS, GeographicCRS)'
            //       except that the later invokes directly the right method for 'step2' instead
            //       of invoking 'createOperation' recursively.
            final GeneralDerivedCRS       source = (GeneralDerivedCRS) sourceCRS;
            final CoordinateReferenceSystem base = source.getBaseCRS();
            CoordinateOperation            step1 = source.getConversionFromBase();
            MathTransform              transform = step1.getMathTransform();
            try {
                transform = transform.inverse();
            } catch (NoninvertibleTransformException exception) {
                throw new OperationNotFoundException(getErrorMessage(sourceCRS, base), exception);
            }
            step1 = createFromMathTransform(INVERSE_OPERATION, sourceCRS, base, transform);
            return concatenate(step1, createOperation(base, targetCRS));
        }
        /////////////////////////////////////////
        ////                                 ////
        ////     Generic  -->  various CS    ////
        ////     Various CS --> Generic      ////
        ////                                 ////
        /////////////////////////////////////////
        if (sourceCRS == DefaultEngineeringCRS.GENERIC_2D ||
            targetCRS == DefaultEngineeringCRS.GENERIC_2D ||
            sourceCRS == DefaultEngineeringCRS.GENERIC_3D ||
            targetCRS == DefaultEngineeringCRS.GENERIC_3D)
        {
            final int dimSource = getDimension(sourceCRS);
            final int dimTarget = getDimension(targetCRS);
            if (dimTarget == dimSource) {
                final Matrix matrix = Matrices.create(dimTarget+1, dimSource+1);
                return createFromAffineTransform(IDENTITY, sourceCRS, targetCRS, matrix);
            }
        }
        throw new OperationNotFoundException(getErrorMessage(sourceCRS, targetCRS));
    }

    /**
     * Returns an operation using a particular method for conversion or transformation between
     * two coordinate reference systems. If the operation exists on the implementation, then it
     * is returned. If the operation does not exist on the implementation, then the implementation
     * has the option of inferring the operation from the argument objects. If for whatever reason
     * the specified operation will not be returned, then the exception is thrown.
     * <p>
     * Current implementation ignores the {@code method} argument.
     * This behavior may change in a future Geotk version.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @param  method the algorithmic method for conversion or transformation.
     * @throws OperationNotFoundException if no operation path was found from {@code sourceCRS}
     *         to {@code targetCRS}.
     * @throws FactoryException if the operation creation failed for some other reason.
     */
    @Override
    public CoordinateOperation createOperation(final CoordinateReferenceSystem sourceCRS,
                                               final CoordinateReferenceSystem targetCRS,
                                               final OperationMethod           method)
            throws OperationNotFoundException, FactoryException
    {
        ensureNonNull("method", method); // As a matter of principle.
        return createOperation(sourceCRS, targetCRS);
    }




    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ////////////                                                         ////////////
    ////////////               N O R M A L I Z A T I O N S               ////////////
    ////////////                                                         ////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Makes sure that the specified geocentric CRS uses standard axis, prime meridian and
     * the specified datum. If {@code crs} already meets all those conditions, then it is
     * returned unchanged. Otherwise, a new normalized geocentric CRS is created and returned.
     *
     * @param  crs The geocentric coordinate reference system to normalize.
     * @param  datum The expected datum.
     * @return The normalized coordinate reference system.
     * @throws FactoryException if the construction of a new CRS was needed but failed.
     */
    private GeocentricCRS normalize(final GeocentricCRS crs, final GeodeticDatum datum)
            throws FactoryException
    {
        final CartesianCS   STANDARD  = DefaultCartesianCS.GEOCENTRIC;
        final GeodeticDatum candidate = crs.getDatum();
        if (equalsIgnorePrimeMeridian(candidate, datum)) {
            if (getGreenwichLongitude(candidate.getPrimeMeridian()) ==
                getGreenwichLongitude(datum    .getPrimeMeridian()))
            {
                if (hasStandardAxis(crs.getCoordinateSystem(), STANDARD)) {
                    return crs;
                }
            }
        }
        final CRSFactory crsFactory = factories.getCRSFactory();
        return crsFactory.createGeocentricCRS(getTemporaryName(crs), datum, STANDARD);
    }

    /**
     * Makes sure that the specified geographic CRS uses standard axis (longitude and latitude in
     * decimal degrees). Optionally, this method can also make sure that the CRS use the Greenwich
     * prime meridian. Other datum properties are left unchanged. If {@code crs} already meets all
     * those conditions, then it is returned unchanged. Otherwise, a new normalized geographic CRS
     * is created and returned.
     *
     * @param  crs The geographic coordinate reference system to normalize.
     * @param  forceGreenwich {@code true} for forcing the Greenwich prime meridian.
     * @return The normalized coordinate reference system.
     * @throws FactoryException if the construction of a new CRS was needed but failed.
     */
    private GeographicCRS normalize(final GeographicCRS crs, final boolean forceGreenwich)
            throws FactoryException
    {
        GeodeticDatum datum = crs.getDatum();
        final EllipsoidalCS cs = crs.getCoordinateSystem();
        final EllipsoidalCS STANDARD = (cs.getDimension() <= 2) ?
                DefaultEllipsoidalCS.GEODETIC_2D :
                DefaultEllipsoidalCS.GEODETIC_3D;
        if (forceGreenwich && getGreenwichLongitude(datum.getPrimeMeridian()) != 0) {
            datum = new TemporaryDatum(datum);
        } else if (hasStandardAxis(cs, STANDARD)) {
            return crs;
        }
        /*
         * The specified geographic coordinate system doesn't use standard axis
         * (EAST, NORTH) or the greenwich meridian. Create a new one meeting those criterions.
         */
        final CRSFactory crsFactory = factories.getCRSFactory();
        return crsFactory.createGeographicCRS(getTemporaryName(crs), datum, STANDARD);
    }

    /**
     * A datum identical to the specified datum except for the prime meridian, which is replaced
     * by Greenwich. This datum is processed in a special way by {@link #equalsIgnorePrimeMeridian}.
     */
    private static final class TemporaryDatum extends DefaultGeodeticDatum {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = -8964199103509187219L;

        /** The wrapped datum. */
        private final GeodeticDatum datum;

        /** Wrap the specified datum. */
        public TemporaryDatum(final GeodeticDatum datum) {
            super(getTemporaryName(datum), datum.getEllipsoid());
            this.datum = datum;
        }

        /** Unwrap the datum. */
        public static GeodeticDatum unwrap(GeodeticDatum datum) {
            while (datum instanceof TemporaryDatum) {
                datum = ((TemporaryDatum) datum).datum;
            }
            return datum;
        }

        /** Compares this datum with the specified object for equality. */
        @Override
        public boolean equals(final Object object, final ComparisonMode mode) {
            if (object instanceof TemporaryDatum && super.equals(object, mode)) {
                final GeodeticDatum other = ((TemporaryDatum) object).datum;
                switch (mode) {
                    case STRICT: return datum.equals(other);
                    default:     return equalsIgnoreMetadata(datum, other);
                }
            }
            return false;
        }
    }

    /**
     * Returns {@code true} if the specified coordinate system uses standard axis and units.
     *
     * @param crs The coordinate system to test.
     * @param standard The coordinate system that defines the standard. Usually
     *        {@link DefaultEllipsoidalCS#GEODETIC_2D} or {@link DefaultCartesianCS#PROJECTED}.
     */
    private static boolean hasStandardAxis(final CoordinateSystem cs, final CoordinateSystem standard) {
        final int dimension = standard.getDimension();
        if (cs.getDimension() != dimension) {
            return false;
        }
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis a1 =       cs.getAxis(i);
            final CoordinateSystemAxis a2 = standard.getAxis(i);
            if (!a1.getDirection().equals(a2.getDirection()) ||
                !a1.getUnit()     .equals(a2.getUnit()))
            {
                return false;
            }
        }
        return true;
    }




    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ////////////                                                         ////////////
    ////////////            A X I S   O R I E N T A T I O N S            ////////////
    ////////////                                                         ////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns an affine transform between two ellipsoidal coordinate systems. Only
     * units, axis order (e.g. transforming from (NORTH,WEST) to (EAST,NORTH)) and
     * prime meridian are taken in account. Other attributes (especially the datum)
     * must be checked before invoking this method.
     * <p>
     * If a conversion from 2D to 3D is requested, this method will set the elevation
     * ordinate to 0 in the target 3D coordinates. In other words, the coordinates are
     * assumed on the ellipsoid surface. This is done that way for consistency with
     * {@link org.geotoolkit.referencing.operation.transform.MolodenskyTransform}, which
     * also assumes that the points are on the ellipsoid when the <var>z</var> values are
     * missing (while in the Molodenski case, the result is more complicated than just 0).
     *
     * @param  sourceCS The source coordinate system.
     * @param  targetCS The target coordinate system.
     * @param  sourcePM The source prime meridian.
     * @param  targetPM The target prime meridian.
     * @return The transformation from {@code sourceCS} to {@code targetCS} as
     *         an affine transform. Only axis orientation, units and prime meridian are
     *         taken in account.
     * @throws OperationNotFoundException If the affine transform can't be constructed.
     */
    private Matrix swapAndScaleAxis(final EllipsoidalCS sourceCS,
                                    final EllipsoidalCS targetCS,
                                    final PrimeMeridian sourcePM,
                                    final PrimeMeridian targetPM)
            throws OperationNotFoundException
    {
        /*
         * Compute swapAndScaleAxis(sourceCS, targetCS) with a special case for the conversion
         * from 2D to 3D Geographic CRS: we expect that the new dimension is the ellipsoidal
         * height, so we create a Matrix which will "generate" the new ordinates with NaN value.
         */
        Matrix matrix = null;
        if (sourceCS.getDimension() == 2 && targetCS.getDimension() == 3) {
            for (int k=3; --k>=0;) {
                if (AxisDirection.UP.equals(AxisDirections.absolute(targetCS.getAxis(k).getDirection()))) {
                    final CoordinateSystemAxis axis0 = targetCS.getAxis(k != 0 ? 0 : 1);
                    final CoordinateSystemAxis axis1 = targetCS.getAxis(k != 2 ? 2 : 1);
                    final EllipsoidalCS step = new DefaultEllipsoidalCS("Step", axis0, axis1);
                    final Matrix reduced = swapAndScaleAxis(sourceCS, step);
                    assert reduced.getNumRow() == 3 && reduced.getNumCol() == 3 : reduced;
                    matrix = Matrices.create(4, 3);
                    matrix.setElement(3, 2, 1);
                    for (int jm=0,j=0; j<3; j++) {
                        if (j == k) {
                            matrix.setElement(j, j, 0);
                            // Translation term intentionally left to 0 - see method javadoc.
                        } else {
                            for (int i=3; --i>=0;) {
                                matrix.setElement(jm, i, reduced.getElement(j, i));
                            }
                            jm++;
                        }
                    }
                    break;
                }
            }
        }
        if (matrix == null) {
            matrix = swapAndScaleAxis(sourceCS, targetCS);
        }
        for (int i=targetCS.getDimension(); --i>=0;) {
            final CoordinateSystemAxis axis = targetCS.getAxis(i);
            final AxisDirection direction = axis.getDirection();
            if (AxisDirection.EAST.equals(AxisDirections.absolute(direction))) {
                /*
                 * A longitude ordinate has been found (i.e. the axis is oriented toward EAST or
                 * WEST). Compute the amount of angle to add to the source longitude in order to
                 * get the destination longitude. This amount is measured in units of the target
                 * axis.  The affine transform is then updated in order to take this rotation in
                 * account. Note that the resulting longitude may be outside the usual [-180..180°]
                 * range.
                 */
                final Unit<Angle>       unit = axis.getUnit().asType(Angle.class);
                final double sourceLongitude = getGreenwichLongitude(sourcePM, unit);
                final double targetLongitude = getGreenwichLongitude(targetPM, unit);
                final int   lastMatrixColumn = matrix.getNumCol()-1;
                double rotate = sourceLongitude - targetLongitude;
                if (AxisDirection.WEST.equals(direction)) {
                    rotate = -rotate;
                }
                rotate += matrix.getElement(i, lastMatrixColumn);
                matrix.setElement(i, lastMatrixColumn, rotate);
            }
        }
        return matrix;
    }




    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ////////////                                                         ////////////
    ////////////        T R A N S F O R M A T I O N S   S T E P S        ////////////
    ////////////                                                         ////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates an operation between two temporal coordinate reference systems.
     * The default implementation checks if both CRS use the same datum, and
     * then adjusts for axis direction, units and epoch.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    protected CoordinateOperation createOperationStep(final TemporalCRS sourceCRS,
                                                      final TemporalCRS targetCRS)
            throws FactoryException
    {
        final TemporalDatum sourceDatum = sourceCRS.getDatum();
        final TemporalDatum targetDatum = targetCRS.getDatum();
        /*
         * Compute the epoch shift.  The epoch is the time "0" in a particular coordinate
         * reference system. For example, the epoch for java.util.Date object is january 1,
         * 1970 at 00:00 UTC.  We compute how much to add to a time in 'sourceCRS' in order
         * to get a time in 'targetCRS'. This "epoch shift" is in units of 'targetCRS'.
         */
        final TimeCS sourceCS = sourceCRS.getCoordinateSystem();
        final TimeCS targetCS = targetCRS.getCoordinateSystem();
        final Unit<Duration> targetUnit = targetCS.getAxis(0).getUnit().asType(Duration.class);
        double epochShift = sourceDatum.getOrigin().getTime() -
                            targetDatum.getOrigin().getTime();
        epochShift = MILLISECOND.getConverterTo(targetUnit).convert(epochShift);
        /*
         * Check axis orientation.  The method 'swapAndScaleAxis' should returns a matrix
         * of size 2x2. The element at index (0,0) may be 1 if sourceCRS and targetCRS axis
         * are in the same direction, or -1 if there are in opposite direction (e.g.
         * "PAST" vs "FUTURE"). This number may be something else than -1 or +1 if a unit
         * conversion was applied too,  for example 60 if time in 'sourceCRS' was in hours
         * while time in 'targetCRS' was in minutes.
         *
         * The "epoch shift" previously computed is a translation.
         * Consequently, it is added to element (0,1).
         */
        final Matrix matrix = swapAndScaleAxis(sourceCS, targetCS);
        final int translationColumn = matrix.getNumCol() - 1;
        if (translationColumn >= 0) { // Paranoiac check: should always be 1.
            final double translation = matrix.getElement(0, translationColumn);
            matrix.setElement(0, translationColumn, translation + epochShift);
        }
        return createFromAffineTransform(AXIS_CHANGES, sourceCRS, targetCRS, matrix);
    }

    /**
     * Creates an operation between two vertical coordinate reference systems.
     * The default implementation checks if both CRS use the same datum, and
     * then adjusts for axis direction and units.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    protected CoordinateOperation createOperationStep(final VerticalCRS sourceCRS,
                                                      final VerticalCRS targetCRS)
            throws FactoryException
    {
        final VerticalDatum sourceDatum = sourceCRS.getDatum();
        final VerticalDatum targetDatum = targetCRS.getDatum();
        if (!equalsIgnoreMetadata(sourceDatum, targetDatum)) {
            throw new OperationNotFoundException(getErrorMessage(sourceDatum, targetDatum));
        }
        final VerticalCS sourceCS = sourceCRS.getCoordinateSystem();
        final VerticalCS targetCS = targetCRS.getCoordinateSystem();
        final Matrix     matrix   = swapAndScaleAxis(sourceCS, targetCS);
        return createFromAffineTransform(AXIS_CHANGES, sourceCRS, targetCRS, matrix);
    }

    /**
     * Creates an operation between a geographic and a vertical coordinate reference systems.
     * The default implementation accepts the conversion only if the geographic CRS is a
     * tridimensional one and the vertical CRS is for <cite>height above the ellipsoid</cite>.
     * More elaborated operation, like transformation from ellipsoidal to geoidal height,
     * should be implemented here.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     *
     * @todo Implement GEOT-352 here.
     */
    protected CoordinateOperation createOperationStep(final GeographicCRS sourceCRS,
                                                      final VerticalCRS   targetCRS)
            throws FactoryException
    {
        if (VerticalDatumTypes.ELLIPSOIDAL.equals(targetCRS.getDatum().getVerticalDatumType())) {
            final Matrix matrix = swapAndScaleAxis(sourceCRS.getCoordinateSystem(),
                                                   targetCRS.getCoordinateSystem());
            return createFromAffineTransform(AXIS_CHANGES, sourceCRS, targetCRS, matrix);
        }
        throw new OperationNotFoundException(getErrorMessage(sourceCRS, targetCRS));
    }

    /**
     * Creates an operation between two geographic coordinate reference systems. The default
     * implementation can adjust axis order and orientation (e.g. transforming from
     * {@code (NORTH,WEST)} to {@code (EAST,NORTH)}), performs units conversion
     * and apply datum shifts if needed.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     *
     * @todo When rotating the prime meridian, we should ensure that
     *       transformed longitudes stay in the range [-180..+180°].
     */
    protected CoordinateOperation createOperationStep(final GeographicCRS sourceCRS,
                                                      final GeographicCRS targetCRS)
            throws FactoryException
    {
        final EllipsoidalCS sourceCS    = sourceCRS.getCoordinateSystem();
        final EllipsoidalCS targetCS    = targetCRS.getCoordinateSystem();
        final GeodeticDatum sourceDatum = sourceCRS.getDatum();
        final GeodeticDatum targetDatum = targetCRS.getDatum();
        final PrimeMeridian sourcePM    = sourceDatum.getPrimeMeridian();
        final PrimeMeridian targetPM    = targetDatum.getPrimeMeridian();
        if (equalsIgnorePrimeMeridian(sourceDatum, targetDatum)) {
            /*
             * If both geographic CRS use the same datum, then there is no need for a datum shift.
             * Just swap axis order, and rotate the longitude coordinate if prime meridians are
             * different. Note: this special block is mandatory for avoiding never-ending loop,
             * since it is invoked by 'createOperationStep(GeocentricCRS...)'.
             *
             * TODO: We should ensure that longitude is in range [-180..+180°].
             */
            final Matrix matrix = swapAndScaleAxis(sourceCS, targetCS, sourcePM, targetPM);
            return createFromAffineTransform(AXIS_CHANGES, sourceCRS, targetCRS, matrix);
        }
        /*
         * The two geographic CRS use different datum. If Molodensky transformations
         * are allowed, try them first. Note that in some cases if the datum shift can't
         * be performed in a single Molodensky transformation step (i.e. if we need to
         * go through at least one intermediate datum), then we will use the geocentric
         * transform below instead: it allows to concatenate many Bursa Wolf parameters
         * in a single affine transform.
         */
        if (molodenskyMethod != null) {
            ReferenceIdentifier identifier = DATUM_SHIFT;
            BursaWolfParameters bursaWolf  = null;
            if (sourceDatum instanceof DefaultGeodeticDatum) {
                bursaWolf = ((DefaultGeodeticDatum) sourceDatum).getBursaWolfParameters(targetDatum);
            }
            if (bursaWolf == null) {
                /*
                 * No direct path found. Try the more expensive matrix calculation, and
                 * see if we can retrofit the result in a BursaWolfParameters object.
                 */
                final Matrix shift = DefaultGeodeticDatum.getAffineTransform(sourceDatum, targetDatum);
                if (shift != null) try {
                    bursaWolf = new BursaWolfParameters(targetDatum, null);
                    bursaWolf.setPositionVectorTransformation(shift, 1E-4);
                } catch (IllegalArgumentException ignore) {
                    /*
                     * A matrix exists, but we are unable to retrofit it as a set of Bursa-Wolf
                     * parameters. Do NOT set the 'bursaWolf' variable: it must stay null, which
                     * means to perform the datum shift using geocentric coordinates.
                     */
                } else if (lenientDatumShift) {
                    /*
                     * No BursaWolf parameters available. No affine transform to be applied in
                     * geocentric coordinates are available neither (the "shift" matrix above),
                     * so performing a geocentric transformation will not help. But the user wants
                     * us to perform the datum shift anyway. We will notify the user through
                     * positional accuracy, which is set indirectly through ELLIPSOID_SHIFT.
                     */
                    bursaWolf  = new BursaWolfParameters(targetDatum, null);
                    identifier = ELLIPSOID_SHIFT;
                }
            }
            /*
             * Applies the Molodensky transformation now. Note: in current parameters, we can't
             * specify a different input and output dimension. However, our Molodensky transform
             * allows that. We should expand the parameters block for this case (TODO).
             */
            if (bursaWolf != null && bursaWolf.isTranslation()) {
                final Ellipsoid sourceEllipsoid = sourceDatum.getEllipsoid();
                final Ellipsoid targetEllipsoid = targetDatum.getEllipsoid();
                if (bursaWolf.isIdentity() && equalsIgnoreMetadata(sourceEllipsoid, targetEllipsoid)) {
                    final Matrix matrix = swapAndScaleAxis(sourceCS, targetCS, sourcePM, targetPM);
                    return createFromAffineTransform(identifier, sourceCRS, targetCRS, matrix);
                }
                final int sourceDim = getDimension(sourceCRS);
                final int targetDim = getDimension(targetCRS);
                final ParameterValueGroup parameters;
                parameters = getMathTransformFactory().getDefaultParameters(molodenskyMethod);
                parameters.parameter("src_semi_major").setValue(sourceEllipsoid.getSemiMajorAxis());
                parameters.parameter("src_semi_minor").setValue(sourceEllipsoid.getSemiMinorAxis());
                parameters.parameter("tgt_semi_major").setValue(targetEllipsoid.getSemiMajorAxis());
                parameters.parameter("tgt_semi_minor").setValue(targetEllipsoid.getSemiMinorAxis());
                parameters.parameter("dx").setValue(bursaWolf.tX);
                parameters.parameter("dy").setValue(bursaWolf.tY);
                parameters.parameter("dz").setValue(bursaWolf.tZ);
                parameters.parameter("dim").setValue(sourceDim);
                boolean ready = true;
                if (sourceDim != targetDim) try {
                    // Following is Geotk-specific, so it may not be supported
                    // if the math transform provider come from an other library.
                    parameters.parameter("src_dim").setValue(sourceDim);
                    parameters.parameter("tgt_dim").setValue(targetDim);
                } catch (ParameterNotFoundException e) {
                    ready = false; // Will fallback on geocentric transformation.
                    Logging.recoverableException(LOGGER,
                            DefaultCoordinateOperationFactory.class, "createOperationStep", e);
                }
                if (ready) {
                    final GeographicCRS normSourceCRS = normalize(sourceCRS, true);
                    final GeographicCRS normTargetCRS = normalize(targetCRS, true);
                    return concatenate(
                            createOperationStep(sourceCRS, normSourceCRS),
                            createFromParameters(identifier, normSourceCRS, normTargetCRS, parameters),
                            createOperationStep(normTargetCRS, targetCRS));
                }
            }
        }
        /*
         * If the two geographic CRS use different datum, transform from the
         * source to target datum through the geocentric coordinate system.
         * The transformation chain is:
         *
         *     source geographic CRS                                               -->
         *     geocentric CRS with a preference for datum using Greenwich meridian -->
         *     target geographic CRS
         */
        final CartesianCS STANDARD = DefaultCartesianCS.GEOCENTRIC;
        final GeocentricCRS stepCRS;
        final CRSFactory crsFactory = factories.getCRSFactory();
        if (getGreenwichLongitude(targetPM) == 0) {
            stepCRS = crsFactory.createGeocentricCRS(
                      getTemporaryName(targetCRS), targetDatum, STANDARD);
        } else {
            stepCRS = crsFactory.createGeocentricCRS(
                      getTemporaryName(sourceCRS), sourceDatum, STANDARD);
        }
        return concatenate(createOperationStep(sourceCRS, stepCRS),
                           createOperationStep(stepCRS, targetCRS));
    }

    /**
     * Creates an operation between two projected coordinate reference systems.
     * The default implementation can adjust axis order and orientation. It also
     * performs units conversion if it is the only extra change needed. Otherwise,
     * it performs three steps:
     *
     * <ul>
     *   <li>Unproject from {@code sourceCRS} to its base
     *       {@linkplain GeographicCRS geographic CRS}.</li>
     *   <li>Convert the source to target base geographic CRS.</li>
     *   <li>Project from the base {@linkplain GeographicCRS geographic CRS}
     *       to the {@code targetCRS}.</li>
     * </ul>
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    protected CoordinateOperation createOperationStep(final ProjectedCRS sourceCRS,
                                                      final ProjectedCRS targetCRS)
            throws FactoryException
    {
        /*
         * Apply the transformation in 3 steps (the 3 arrows below):
         *
         *     source projected CRS   --(unproject)-->
         *     source geographic CRS  --------------->
         *     target geographic CRS  ---(project)--->
         *     target projected CRS
         */
        final GeographicCRS sourceGeo = sourceCRS.getBaseCRS();
        final GeographicCRS targetGeo = targetCRS.getBaseCRS();
        return concatenate(tryDB(sourceCRS, sourceGeo),
                           tryDB(sourceGeo, targetGeo),
                           tryDB(targetGeo, targetCRS));
    }

    /**
     * Creates an operation from a geographic to a projected coordinate reference system.
     * The default implementation constructs the following operation chain:
     *
     * <blockquote><code>
     * sourceCRS  &rarr;  {@linkplain ProjectedCRS#getBaseCRS baseCRS}  &rarr;  targetCRS
     * </code></blockquote>
     *
     * where the conversion from {@code baseCRS} to {@code targetCRS} is obtained from
     * <code>targetCRS.{@linkplain ProjectedCRS#getConversionFromBase getConversionFromBase()}</code>.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    protected CoordinateOperation createOperationStep(final GeographicCRS sourceCRS,
                                                      final ProjectedCRS  targetCRS)
            throws FactoryException
    {
        return concatenate(tryDB(sourceCRS, targetCRS.getBaseCRS()),
                           targetCRS.getConversionFromBase());
    }

    /**
     * Creates an operation from a projected to a geographic coordinate reference system.
     * The default implementation constructs the following operation chain:
     *
     * <blockquote><code>
     * sourceCRS  &rarr;  {@linkplain ProjectedCRS#getBaseCRS baseCRS}  &rarr;  targetCRS
     * </code></blockquote>
     *
     * where the conversion from {@code sourceCRS} to {@code baseCRS} is obtained from the inverse of
     * <code>sourceCRS.{@linkplain ProjectedCRS#getConversionFromBase getConversionFromBase()}</code>.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     *
     * @todo Provides a non-null method.
     */
    protected CoordinateOperation createOperationStep(final ProjectedCRS  sourceCRS,
                                                      final GeographicCRS targetCRS)
            throws FactoryException
    {
        final GeographicCRS base  = sourceCRS.getBaseCRS();
        CoordinateOperation step1 = sourceCRS.getConversionFromBase();
        MathTransform transform = step1.getMathTransform();
        try {
            transform = transform.inverse();
        } catch (NoninvertibleTransformException exception) {
            throw new OperationNotFoundException(getErrorMessage(sourceCRS, base), exception);
        }
        step1 = createFromMathTransform(INVERSE_OPERATION, sourceCRS, base, transform);
        return concatenate(step1, tryDB(base, targetCRS));
    }

    /**
     * Creates an operation between two geocentric coordinate reference systems.
     * The default implementation can adjust for axis order and orientation,
     * performs units conversion and apply Bursa Wolf transformation if needed.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     *
     * @todo Rotation of prime meridian not yet implemented.
     * @todo Transformation version set to "(unknown)". We should search this information somewhere.
     */
    protected CoordinateOperation createOperationStep(final GeocentricCRS sourceCRS,
                                                      final GeocentricCRS targetCRS)
            throws FactoryException
    {
        final GeodeticDatum sourceDatum = sourceCRS.getDatum();
        final GeodeticDatum targetDatum = targetCRS.getDatum();
        final CoordinateSystem sourceCS = sourceCRS.getCoordinateSystem();
        final CoordinateSystem targetCS = targetCRS.getCoordinateSystem();
        final double sourcePM, targetPM;
        sourcePM = getGreenwichLongitude(sourceDatum.getPrimeMeridian());
        targetPM = getGreenwichLongitude(targetDatum.getPrimeMeridian());
        if (equalsIgnorePrimeMeridian(sourceDatum, targetDatum)) {
            if (sourcePM == targetPM) {
                /*
                 * If both CRS use the same datum and the same prime meridian,
                 * then the transformation is probably just axis swap or unit
                 * conversions.
                 */
                final Matrix matrix = swapAndScaleAxis(sourceCS, targetCS);
                return createFromAffineTransform(AXIS_CHANGES, sourceCRS, targetCRS, matrix);
            }
            // Prime meridians are differents. Performs the full transformation.
        }
        if (sourcePM != targetPM) {
            throw new OperationNotFoundException("Rotation of prime meridian not yet implemented");
        }
        /*
         * Transform between differents ellipsoids using Bursa Wolf parameters.
         * The Bursa Wolf parameters are used with "standard" geocentric CS, i.e.
         * with x axis towards the prime meridian, y axis towards East and z axis
         * toward North. The following steps are applied:
         *
         *     source CRS                      -->
         *     standard CRS with source datum  -->
         *     standard CRS with target datum  -->
         *     target CRS
         */
        final CartesianCS STANDARD = DefaultCartesianCS.GEOCENTRIC;
        final XMatrix matrix;
        ReferenceIdentifier identifier = DATUM_SHIFT;
        try {
            Matrix datumShift = DefaultGeodeticDatum.getAffineTransform(
                                    TemporaryDatum.unwrap(sourceDatum),
                                    TemporaryDatum.unwrap(targetDatum));
            if (datumShift == null) {
                if (lenientDatumShift) {
                    datumShift = new Matrix4(); // Identity transform.
                    identifier = ELLIPSOID_SHIFT;
                } else {
                    throw new OperationNotFoundException(Errors.format(
                                Errors.Keys.BURSA_WOLF_PARAMETERS_REQUIRED));
                }
            }
            final Matrix normalizeSource = swapAndScaleAxis(sourceCS, STANDARD);
            final Matrix normalizeTarget = swapAndScaleAxis(STANDARD, targetCS);
            /*
             * Since all steps are matrix, we can multiply them into a single matrix operation.
             * Note: XMatrix.multiply(XMatrix) is equivalents to AffineTransform.concatenate(...):
             *       First transform by the supplied transform and then transform the result
             *       by the original transform.
             *
             * We compute: matrix = normalizeTarget * datumShift * normalizeSource
             */
            matrix = new Matrix4(normalizeTarget);
            matrix.multiply(datumShift);
            matrix.multiply(normalizeSource);
        } catch (SingularMatrixException cause) {
            throw new OperationNotFoundException(getErrorMessage(sourceDatum, targetDatum), cause);
        }
        return createFromAffineTransform(identifier, sourceCRS, targetCRS, matrix);
    }

    /**
     * Creates an operation from a geographic to a geocentric coordinate reference systems.
     * If the source CRS doesn't have a vertical axis, height above the ellipsoid will be
     * assumed equal to zero everywhere. The default implementation uses the
     * {@code "Ellipsoid_To_Geocentric"} math transform.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    protected CoordinateOperation createOperationStep(final GeographicCRS sourceCRS,
                                                      final GeocentricCRS targetCRS)
            throws FactoryException
    {
        /*
         * This transformation is a 3 steps process:
         *
         *    source     geographic CRS  -->
         *    normalized geographic CRS  -->
         *    normalized geocentric CRS  -->
         *    target     geocentric CRS
         *
         * "Normalized" means that axis point toward standards direction (East, North, etc.),
         * units are metres or decimal degrees, prime meridian is Greenwich and height is measured
         * above the ellipsoid. However, the horizontal datum is preserved.
         */
        final GeographicCRS normSourceCRS = normalize(sourceCRS, true);
        final GeodeticDatum datum         = normSourceCRS.getDatum();
        final GeocentricCRS normTargetCRS = normalize(targetCRS, datum);
        final Ellipsoid         ellipsoid = datum.getEllipsoid();
        final Unit<Length>           unit = ellipsoid.getAxisUnit();
        final ParameterValueGroup   param;
        param = getMathTransformFactory().getDefaultParameters("Ellipsoid_To_Geocentric");
        param.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis(), unit);
        param.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis(), unit);
        param.parameter("dim")       .setValue(getDimension(normSourceCRS));
        return concatenate(
                createOperationStep (sourceCRS, normSourceCRS),
                createFromParameters(GEOCENTRIC_CONVERSION, normSourceCRS, normTargetCRS, param),
                createOperationStep (normTargetCRS, targetCRS));
    }

    /**
     * Creates an operation from a geocentric to a geographic coordinate reference systems.
     * The default implementation use the <code>"Geocentric_To_Ellipsoid"</code> math transform.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    protected CoordinateOperation createOperationStep(final GeocentricCRS sourceCRS,
                                                      final GeographicCRS targetCRS)
            throws FactoryException
    {
        final GeographicCRS normTargetCRS = normalize(targetCRS, true);
        final GeodeticDatum datum         = normTargetCRS.getDatum();
        final GeocentricCRS normSourceCRS = normalize(sourceCRS, datum);
        final Ellipsoid         ellipsoid = datum.getEllipsoid();
        final Unit<Length>           unit = ellipsoid.getAxisUnit();
        final ParameterValueGroup   param;
        param = getMathTransformFactory().getDefaultParameters("Geocentric_To_Ellipsoid");
        param.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis(), unit);
        param.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis(), unit);
        param.parameter("dim")       .setValue(getDimension(normTargetCRS));

        final CoordinateOperation step1, step2, step3;
        step1 = createOperationStep (sourceCRS, normSourceCRS);
        step2 = createFromParameters(GEOCENTRIC_CONVERSION, normSourceCRS, normTargetCRS, param);
        step3 = createOperationStep (normTargetCRS, targetCRS);
        return concatenate(step1, step2, step3);
    }

    /**
     * Creates an operation from a compound to a single coordinate reference systems.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     *
     * @todo (<a href="http://jira.geotoolkit.org/browse/GEOTK-83">GEOTK-83</a>)
     *       This method works for some simple cases (e.g. no datum change), and give up
     *       otherwise. Before to give up at the end of this method, we should try the following:
     *       <ul>
     *         <li>Maybe {@code sourceCRS} uses a non-ellipsoidal height. We should replace
     *             the non-ellipsoidal height by an ellipsoidal one, create a transformation step
     *             for that (to be concatenated), and then try again this operation step.</li>
     *
     *         <li>Maybe {@code sourceCRS} contains some extra axis, like a temporal CRS.
     *             We should revisit this code in other to lets supplemental ordinates to be
     *             pass through or removed.</li>
     *       </ul>
     */
    protected CoordinateOperation createOperationStep(final CompoundCRS sourceCRS,
                                                      final SingleCRS   targetCRS)
            throws FactoryException
    {
        final List<SingleCRS> sources = DefaultCompoundCRS.getSingleCRS(sourceCRS);
        if (sources.size() == 1) {
            return createOperation(sources.get(0), targetCRS);
        }
        if (needsGeodetic3D(sources, targetCRS, true)) {
            /*
             * There is a change of datum.  It may be a vertical datum change (for example from
             * ellipsoidal to geoidal height), in which case geographic coordinates are usually
             * needed. It may also be a geodetic datum change, in which case the height is part
             * of computation. Try to convert the source CRS into a 3D-geodetic CRS.
             */
            final CoordinateReferenceSystem source3D = factories.toGeodetic3D(sourceCRS);
            if (source3D != sourceCRS) {
                return createOperation(source3D, targetCRS);
            }
            /*
             * TODO: Search for non-ellipsoidal height, and lets supplemental axis (e.g. time)
             *       pass through. See javadoc comments above.
             */
            if (!lenientDatumShift && needsGeodetic3D(sources, targetCRS, false)) {
                throw new OperationNotFoundException(getErrorMessage(sourceCRS, targetCRS));
            }
        }
        // No need for a datum change (see 'needGeodetic3D' javadoc).
        final List<SingleCRS> targets = singletonList(targetCRS);
        return createOperationStep(sourceCRS, sources, targetCRS, targets);
    }

    /**
     * Creates an operation from a single to a compound coordinate reference system.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    protected CoordinateOperation createOperationStep(final SingleCRS   sourceCRS,
                                                      final CompoundCRS targetCRS)
            throws FactoryException
    {
        final List<SingleCRS> targets = DefaultCompoundCRS.getSingleCRS(targetCRS);
        if (targets.size() == 1) {
            return createOperation(sourceCRS, targets.get(0));
        }
        /*
         * This method has almost no chance to succeed (we can't invent ordinate values!) unless
         * 'sourceCRS' is a 3D-geodetic CRS and 'targetCRS' is a 2D + 1D one. Test for this case.
         * Otherwise, the 'createOperationStep' invocation will throws the appropriate exception.
         */
        final CoordinateReferenceSystem target3D = factories.toGeodetic3D(targetCRS);
        if (target3D != targetCRS) {
            return createOperation(sourceCRS, target3D);
        }
        final List<SingleCRS> sources = singletonList(sourceCRS);
        return createOperationStep(sourceCRS, sources, targetCRS, targets);
    }

    /**
     * Creates an operation between two compound coordinate reference systems.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    protected CoordinateOperation createOperationStep(final CompoundCRS sourceCRS,
                                                      final CompoundCRS targetCRS)
            throws FactoryException
    {
        final List<SingleCRS> sources = DefaultCompoundCRS.getSingleCRS(sourceCRS);
        final List<SingleCRS> targets = DefaultCompoundCRS.getSingleCRS(targetCRS);
        if (targets.size() == 1) {
            return createOperation(sourceCRS, targets.get(0));
        }
        if (sources.size() == 1) { // After 'targets' because more likely to fails to transform.
            return createOperation(sources.get(0), targetCRS);
        }
        /*
         * If the source CRS contains both a geodetic and a vertical CRS, then we can process
         * only if there is no datum change. If at least one of those CRS appears in the target
         * CRS with a different datum, then the datum shift must be applied on the horizontal and
         * vertical components together.
         */
        for (final SingleCRS target : targets) {
            if (needsGeodetic3D(sources, target, true)) {
                final CoordinateReferenceSystem source3D = factories.toGeodetic3D(sourceCRS);
                final CoordinateReferenceSystem target3D = factories.toGeodetic3D(targetCRS);
                if (source3D != sourceCRS || target3D != targetCRS) {
                    return createOperation(source3D, target3D);
                }
                /*
                 * TODO: Search for non-ellipsoidal height, and lets supplemental axis pass through.
                 *       See javadoc comments for createOperation(CompoundCRS, SingleCRS).
                 */
                if (!lenientDatumShift && needsGeodetic3D(sources, target, false)) {
                    throw new OperationNotFoundException(getErrorMessage(sourceCRS, targetCRS));
                }
            }
        }
        // No need for a datum change (see 'needGeodetic3D' javadoc).
        return createOperationStep(sourceCRS, sources, targetCRS, targets);
    }

    /**
     * Implementation of transformation step on compound CRS.
     *
     * {@note
     * If there is a horizontal (geographic or projected) CRS together with a vertical CRS,
     * then we can't performs the transformation since the vertical value has an impact on
     * the horizontal value, and this impact is not taken in account if the horizontal and
     * vertical components are not together in a 3D geographic CRS.  This case occurs when
     * the vertical CRS is not a height above the ellipsoid. It must be checked by the
     * caller before this method is invoked.}
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  sources   The source CRS components.
     * @param  targetCRS Output coordinate reference system.
     * @param  targets   The target CRS components.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException If the operation can't be constructed.
     */
    private CoordinateOperation createOperationStep(
            final CoordinateReferenceSystem sourceCRS, final List<SingleCRS> sources,
            final CoordinateReferenceSystem targetCRS, final List<SingleCRS> targets)
            throws FactoryException
    {
        /*
         * Try to find operations from source CRSs to target CRSs. All pairwise combinations are
         * tried, but the preference is given to CRS in the same order (source[0] with target[0],
         * source[1] with target[1], etc.). Operations found are stored in 'steps', but are not
         * yet given to pass through transforms. We need to know first if some ordinate values
         * need reordering (for matching the order of target CRS) if any ordinates reordering and
         * source ordinates drops are required.
         */
        final int sourceDim = getDimension(sourceCRS);
        final CoordinateOperation[] subOps = new CoordinateOperation[targets.size()];
        final boolean[]   sourceIsUsed     = new boolean            [sources.size()];
        final SingleCRS[] orderedSources   = new SingleCRS          [subOps.length];
        final int[]       srcToOrderedSrc  = new int                [sourceDim];
        int subOpCount=0, orderedSourceDim = 0;
search: for (int j=0; j<targets.size(); j++) {
            final SingleCRS target = targets.get(j);
            OperationNotFoundException cause = null;
            int lower, upper=0; // Range of dimension indices where 'source' is found in 'sourceCRS'.
            for (int i=0; i<sources.size(); i++) {
                final SingleCRS source = sources.get(i);
                lower  = upper;
                upper += getDimension(source);
                if (!sourceIsUsed[i]) { // Each 'source' can be used only once.
                    try {
                        subOps[subOpCount] = createOperation(source, target);
                    } catch (OperationNotFoundException exception) {
                        // No operation path for this pair. Remember the exception and search
                        // for an other pair.  We give precedence to exceptions occuring with
                        // pairs for which 'source' and 'target' are at the same index since
                        // this is often the pairs were a conversion was expected to be applied.
                        if (cause == null || i == j) {
                            cause = exception;
                        }
                        continue;
                    }
                    orderedSources[subOpCount++] = source;
                    while (lower < upper) {
                        srcToOrderedSrc[orderedSourceDim++] = lower++;
                    }
                    sourceIsUsed[i] = true;
                    continue search;
                }
            }
            /*
             * No source CRS was found for current target CRS.
             * Consequently, we can't get a transformation path.
             */
            throw new OperationNotFoundException(getErrorMessage(sourceCRS, targetCRS), cause);
        }
        assert subOpCount == subOps.length : subOpCount;
        /*
         * A transformation has been found for every source and target CRS pairs.
         * Some reordering of ordinate values may be needed. Prepare it now as an
         * affine transform. This transform also drop source dimensions not used
         * for any target coordinates.
         */
        final XMatrix select = Matrices.create(orderedSourceDim + 1, sourceDim + 1);
        for (int j=0; j<orderedSourceDim; j++) {
            select.setElement(j, j, 0); // Safe since orderedSourceDim <= sourceDim.
            select.setElement(j, srcToOrderedSrc[j], 1);
        }
        select.setElement(orderedSourceDim, orderedSourceDim, 0);
        select.setElement(orderedSourceDim, sourceDim, 1);
        CoordinateOperation operation = null;
        /*
         * Get a CRS which is equivalent to 'sourceCRS', but with axis order rearranged by
         * the above matrix. This 'stepSourceCRS' will be modified progressively, with the
         * parts (originally source CRS) progressively replaced by target CRS.
         */
        CoordinateReferenceSystem stepSourceCRS = sourceCRS;
        if (!select.isIdentity()) {
            if (orderedSources.length == 1) {
                // Slight optimization of the next block (in the 'else' case).
                stepSourceCRS = orderedSources[0];
            } else {
                stepSourceCRS = factories.getCRSFactory().createCompoundCRS(
                        getTemporaryName(sourceCRS), orderedSources);
            }
            operation = createFromAffineTransform(AXIS_CHANGES, sourceCRS, stepSourceCRS, select);
        }
        /*
         * Move 'subOpCount' to the index of the last non-identity operation.
         */
        while (subOpCount != 0 && subOps[--subOpCount].getMathTransform().isIdentity());
        /*
         * Now create a PassThroughTransform for each sub-transform found above. We get a source
         * and target CRS for each step - called stepSourceCRS and stepTargetCRS. Those CRS are
         * required by PassThroughOperation.
         */
        int upper = 0; // Range of dimension indices where 'source' is found in 'orderedSources'.
        for (int i=0; i<orderedSources.length; i++) {
            final SingleCRS source = orderedSources[i];
            final SingleCRS target = targets.get(i);
            CoordinateOperation subOperation = subOps[i];
            orderedSources[i] = target; // Used for stepTargetCRS construction.
            /*
             * The above line modified in-place a single element in orderedSources because
             * we need to create a new CRS - which is a mix of target and source CRS - for
             * each step. Only when the loop has been completed, "orderedSources" would be
             * actually the complete targetCRS definition.
             */
            final MathTransform subTransform = subOperation.getMathTransform();
            final CoordinateReferenceSystem stepTargetCRS;
            if (i >= subOpCount) {
                // If all remaining parts are identity transforms,
                // then we have reached the final target CRS.
                stepTargetCRS = targetCRS;
            } else if (subTransform.isIdentity()) {
                // In any identity transform, the source and target CRS are
                // equal. So we don't need to create a new stepTargetCRS.
                stepTargetCRS = stepSourceCRS;
            } else if (orderedSources.length == 1) {
                // Slight optimization of the next block, keeping in mind
                // that orderedSources[0] has been set to 'target' above.
                stepTargetCRS = target;
            } else {
                stepTargetCRS = factories.getCRSFactory().createCompoundCRS(
                        getTemporaryName(target), orderedSources);
            }
            int delta = getDimension(source);
            final int lower = upper;
            upper += delta;
            /*
             * Constructs the pass through transform only if there is at least one ordinate to
             * pass. Actually the code below would work inconditionally, but we perform this
             * check anyway for avoiding the creation of intermediate objects.
             */
            if (!(lower == 0 && upper == orderedSourceDim)) {
                final MathTransform step = getMathTransformFactory()
                        .createPassThroughTransform(lower, subTransform, orderedSourceDim - upper);
                final Map<String,?> properties = IdentifiedObjects.getProperties(subOperation);
                /*
                 * The DefaultPassThroughOperation constuctor expect a SingleOperation.
                 * In most case, the 'subOperation' is already of this kind. However if
                 * it is not, try to copy it in such object.
                 */
                final SingleOperation op;
                if (subOperation instanceof SingleOperation) {
                    op = (SingleOperation) subOperation;
                } else {
                    op = (SingleOperation) DefaultSingleOperation.create(properties,
                            subOperation.getSourceCRS(), subOperation.getTargetCRS(), subTransform,
                            new DefaultOperationMethod(subTransform), subOperation.getClass());
                }
                subOperation = new DefaultPassThroughOperation(properties, stepSourceCRS, stepTargetCRS, op, step);
            }
            /*
             * Concatenate the operation with the ones we have found so far, and use the
             * current 'stepTargetCRS' as the source CRS for the next operation step. We
             * also need to adjust the dimension indices, since the preivous operations
             * may have removed some dimensions. Note that the delta may also be negative
             * in a few occasions.
             */
            operation = (operation == null) ? subOperation : concatenate(operation, subOperation);
            stepSourceCRS = stepTargetCRS;
            delta -= getDimension(target);
            upper -= delta;
            orderedSourceDim -= delta;
        }
        assert upper == orderedSourceDim : upper;
        return operation;
    }

    /**
     * Returns {@code true} if a transformation path from {@code sourceCRS} to {@code targetCRS}
     * is likely to require a tri-dimensional geodetic CRS as an intermediate step. This method
     * is used for enforcing the following rule: <i>ellipsoidal height shall never be separated
     * from the geographic coordinates</i>, because some transformation steps (the datum shift)
     * require the full 3D coordinates <strong>even if the result is two-dimensional</strong>.
     * <p>
     * We relax the above rule by returning {@code false} if using the (2D + 1D) components
     * rather than a single 3D CRS is not expected to change the numerical result. We do that
     * because replacing a (2D + 1D) pair by a 3D singleton cause a lost of information (like
     * the authority codes). More specifically, this method returns {@code false} if at least
     * one of the following conditions is meet:
     *
     * <ul>
     *   <li><p>The target datum is neither a vertical or geodetic datum. Consequently,
     *       eventual datum change (typically a temporal datum change) is not the caller
     *       business. It will be handled by the generic method above.</p></li>
     *
     *   <li><p>There is geodetic datum change. While a 3D SingleCRS is conceptually required,
     *       it is not numerically required if the the target CRS is not geodetic 3D.</p></li>
     *
     *   <li><p>A datum change is required, but source CRS doesn't have both a geodetic
     *       and a vertical CRS, so we can't apply a 3D datum shift anyway.</p></li>
     * </ul>
     *
     * @param strict {@code false} if we tolerate some error (typically up to 3 metres).
     *        To be set to {@code false} only in last resort before throwing an exception.
     *        Must be {@code true} in all other cases.
     */
    private static boolean needsGeodetic3D(final List<SingleCRS> sourceCRS,
            final SingleCRS targetCRS, final boolean strict)
    {
        final Datum targetDatum = targetCRS.getDatum();
        final boolean targetIsGeodetic = (targetDatum instanceof GeodeticDatum);
        if (!targetIsGeodetic && !(targetDatum instanceof VerticalDatum)) {
            return false;
        }
        boolean hasHorizontal  = false; // Whatever at least one source component is horizontal.
        boolean hasVertical    = false; // Whatever at least one source component is vertical.
        boolean needDatumShift = false;
        for (final SingleCRS crs : sourceCRS) {
            if (crs.getCoordinateSystem().getDimension() >= 3) {
                /*
                 * If the CRS is already three-dimensional, we don't have to rebuild it.
                 * This method shall return 'true' only if there is (2D + 1D) to combine.
                 */
                continue;
            }
            final Datum sourceDatum = crs.getDatum();
            final boolean sourceIsGeodetic = (sourceDatum instanceof GeodeticDatum);
            if (sourceIsGeodetic) {
                hasHorizontal = true;
            } else if (sourceDatum instanceof VerticalDatum) {
                hasVertical = true;
            } else {
                continue;
            }
            /*
             * If we have found the source component of the same kind than the target element
             * (either a GeodeticDatum or a VerticalDatum), check if there is a need for a datum
             * shift. The other source components are ignored, which is okay if the target CRS is
             * only 1D or 2D since this means that the extra source components will be discarded
             * anyway. The case where the target CRS is 3D is handled at the end of this method.
             */
            if (!needDatumShift && sourceIsGeodetic == targetIsGeodetic) {
                assert Classes.implementSameInterfaces(sourceDatum.getClass(),
                        targetDatum.getClass(), Datum.class) : targetDatum;

                if (sourceIsGeodetic && targetIsGeodetic) {
                    final GeodeticDatum sd = (GeodeticDatum) sourceDatum;
                    final GeodeticDatum td = (GeodeticDatum) targetDatum;
                    if (strict) {
                        needDatumShift = !equalsIgnorePrimeMeridian(sd, td);
                    } else {
                        needDatumShift = isDatumShiftRequired(sd, td);
                    }
                } else {
                    needDatumShift = !equalsIgnoreMetadata(sourceDatum, targetDatum);
                }
            }
        }
        return hasHorizontal && hasVertical &&
               (needDatumShift || targetCRS.getCoordinateSystem().getDimension() >= 3);
    }





    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ////////////                                                         ////////////
    ////////////                M I S C E L L A N E O U S                ////////////
    ////////////                                                         ////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns {@code true} if conversions between CRS using the given datum are likely to require
     * a datum shift. This method is not provided in the public API because it is not correct. For
     * example NAD83 and WGS84 are close enough for having Bursa-Wolf parameters set to 0, but are
     * nevertheless not identical. They don't have the same ellipsoid for instance. Considering
     * them as equal is okay only if we don't need an accuracy better than 3 metres.
     * <p>
     * Use this method only in last resort, before throwing an exception if we can't consider
     * that there is no datum shift. In current implementation, this method is used only in
     * the context of {@link CompoundCRS}.
     *
     * @param  source The datum of the source CRS.
     * @param  target The datum of the target CRS
     * @return {@code true} If conversions between CRS using the given datum are likely to require
     *         a datum shift. Note that a return value of {@code false} does not mean that there is
     *         no rotation of prime meridian.
     *
     * @since 3.07
     */
    private static boolean isDatumShiftRequired(final GeodeticDatum source, final GeodeticDatum target) {
        if (equalsApproximatively(source, target)) {
            return false;
        }
        if (source instanceof DefaultGeodeticDatum) {
            final BursaWolfParameters param = ((DefaultGeodeticDatum) source).getBursaWolfParameters(target);
            if (param != null && param.isIdentity()) {
                return false;
            }
        }
        if (target instanceof DefaultGeodeticDatum) {
            final BursaWolfParameters param = ((DefaultGeodeticDatum) target).getBursaWolfParameters(source);
            if (param != null && param.isIdentity()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares the specified datum for equality, except the prime meridian. In principle,
     * comparing the datum names should be sufficient. However we also compare ellipsoids
     * for safety, with tolerance for rounding errors since the datum names were supposed
     * to be the main criterion.
     *
     * @param  object1 The first object to compare (may be null).
     * @param  object2 The second object to compare (may be null).
     * @return {@code true} if both objects are equals.
     */
    private static boolean equalsIgnorePrimeMeridian(GeodeticDatum object1,
                                                     GeodeticDatum object2)
    {
        object1 = TemporaryDatum.unwrap(object1);
        object2 = TemporaryDatum.unwrap(object2);
        if (equalsApproximatively(object1.getEllipsoid(), object2.getEllipsoid())) {
            return nameMatches(object1, object2.getName().getCode()) ||
                   nameMatches(object2, object1.getName().getCode());
        }
        return false;
    }

    /**
     * Tries to get a coordinate operation from a database (typically EPSG). The exact behavior
     * depends on the {@link AuthorityBackedFactory} implementation (the most typical subclass),
     * but usually the database query is delegated to some instance of
     * {@link org.opengis.referencing.operation.CoordinateOperationAuthorityFactory}.
     * <p>
     * If no coordinate operation was found in the database, then this method delegates
     * to {@link #createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}.
     * <p>
     * If {@code sourceCRS} and {@code targetCRS} are the same, then this method returns
     * {@code null} as a shortcut for identity transform.
     *
     * @throws FactoryException If an exception occurred while invoking {@code createOperation}.
     */
    private CoordinateOperation tryDB(final SingleCRS sourceCRS, final SingleCRS targetCRS) throws FactoryException {
        if (sourceCRS == targetCRS) {
            return null;
        }
        final CoordinateOperation operation = createFromDatabase(sourceCRS, targetCRS);
        if (operation != null) {
            return operation;
        }
        return createOperation(sourceCRS, targetCRS);
    }

    /**
     * If the coordinate operation is explicitly defined in some database (typically EPSG),
     * returns it. Otherwise (if there is no database, or if the database doesn't contains
     * an explicit operation from {@code sourceCRS} to {@code targetCRS}, or if this method
     * failed to create an operation from the database), returns {@code null}.
     * <p>
     * The default implementation always returns {@code null}, since there is no database
     * connected to a {@code DefaultCoordinateOperationFactory} instance. In other words,
     * the default implementation is "standalone": it tries to figure out transformation
     * paths by itself. Subclasses should override this method if they can fetch a more
     * accurate operation from some database. The mean subclass doing so is
     * {@link AuthorityBackedFactory}.
     * <p>
     * This method is invoked by <code>{@linkplain #createOperation createOperation}(sourceCRS,
     * targetCRS)</code> before to try to figure out a transformation path by itself. It is also
     * invoked by various {@code createOperationStep(...)} methods when an intermediate CRS was
     * obtained by {@link GeneralDerivedCRS#getBaseCRS()} (this case occurs especially during
     * {@linkplain GeographicCRS geographic} from/to {@linkplain ProjectedCRS projected} CRS
     * operations). This method is <strong>not</strong> invoked for synthetic CRS generated by
     * {@code createOperationStep(...)}, since those temporary CRS are not expected to exist
     * in a database.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS} if and only if
     *         one is explicitly defined in some underlying database, or {@code null} otherwise.
     *
     * @since 2.3
     */
    protected CoordinateOperation createFromDatabase(final CoordinateReferenceSystem sourceCRS,
                                                     final CoordinateReferenceSystem targetCRS)
    {
        return null;
    }
}
