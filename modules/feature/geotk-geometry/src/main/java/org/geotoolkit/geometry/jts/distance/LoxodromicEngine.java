package org.geotoolkit.geometry.jts.distance;

import java.util.function.ToDoubleBiFunction;
import java.util.function.UnaryOperator;
import org.apache.sis.internal.referencing.ReferencingUtilities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.datum.DefaultEllipsoid;
import org.apache.sis.util.ArgumentChecks;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import static org.geotoolkit.geometry.jts.distance.Utilities.*;

/**
 * Loxodromic distance calculus based on <a href="http://serge.mehl.free.fr/anx/loxodromie.html">{@literal M.} Serge Mehl paper</a>.
 *
 * Points given to this computer must all be on the coordinate system given at
 * build time. For now, only geographic andd projected systems are managed. An
 * evolution would be to extend support for geocentric systems.
 *
 * All results are in meter.
 *
 * @implNote NOT thread-safe.
 *
 * @author Alexis Manin (Geomatys)
 */
public class LoxodromicEngine implements ToDoubleBiFunction<Coordinate, Coordinate> {

    private final double authalicRadius;

    private final UnaryOperator<Coordinate> positionConversion;

    /**
     * Prepare the engine to work with points in the specified coordinate
     * reference system.
     *
     * @param crs The coordinate reference system of future given points.
     */
    LoxodromicEngine(final CoordinateReferenceSystem crs) {
        ArgumentChecks.ensureDimensionMatches("Geometry CRS", 2, crs);
        final GeographicCRS geoCrs;
        if (crs instanceof GeographicCRS) {
            geoCrs = (GeographicCRS) crs;
            if (isLatLon(geoCrs)) {
                positionConversion = in -> new Coordinate(in.y, in.x); // transform lat,lon to lon,lat
            } else {
                positionConversion = UnaryOperator.identity();
            }
        } else if (crs instanceof ProjectedCRS) {
            final ProjectedCRS projCrs = (ProjectedCRS) crs;
            geoCrs = projCrs.getBaseCRS();

            final MathTransform proj2Geo;
            try {
                proj2Geo = projCrs.getConversionFromBase().getMathTransform().inverse();
            } catch (NoninvertibleTransformException ex) {
                throw new RuntimeException(ex);
            }
            final boolean isLatLon = isLatLon(geoCrs);
            positionConversion = base -> transform(base, proj2Geo, isLatLon);
        } else {
            // TODO : for geodetic systems, we should try to unravel ellipsoid from datum.
            throw new IllegalArgumentException("Loxodromic distance computing can only be applied on a geodesic system.");
        }

        final Ellipsoid el = ReferencingUtilities.getEllipsoidOfGeographicCRS(geoCrs);
        if (el == null) {
            throw new IllegalStateException("Cannot obtain an ellipsoid from a geographic CRS (and it is worrying...)");
        }

        authalicRadius = el.getAxisUnit()
                .getConverterTo(Units.METRE)
                .convert(DefaultEllipsoid.castOrCopy(el).getAuthalicRadius());
    }

    /**
     * Compute loxodromic distance between the given points.
     *
     * WARNING: Given coordinates must be expressed in the same CRS as the one
     * given at build time.
     *
     * @implNote : the calculus is approximated with the ellipsoid authalic
     * radius (see {@link DefaultEllipsoid#getAuthalicRadius() } for more
     * details). It allow to distribute the flattening error on the entire
     * ellipsoid surface.
     *
     * @param start First point of the segment
     * @param end Last point of the segment.
     * @return The distance of the given segment, in meters.
     */
    @Override
    public double applyAsDouble(Coordinate start, Coordinate end) {
        start = positionConversion.apply(start);
        end = positionConversion.apply(end);

        final double λA = DEGREE_TO_RADIAN.convert(start.x);
        final double φA = DEGREE_TO_RADIAN.convert(start.y);

        final double λB = DEGREE_TO_RADIAN.convert(end.x);
        final double φB = DEGREE_TO_RADIAN.convert(end.y);

        final double Δλ = λB - λA;
        final double Δφ = φB - φA;

        /* special case : if the segment is aligned on a parallal or a
         * meridian, we don't need to use complex loxodromic equation. We
         * can fallback on a simple arc perimeter.
         */
        if (-1e-7 < Δλ && Δλ < 1e-7) {
            return meridianDistance(Δφ);
        } else if (-1e-7 < Δφ && Δφ < 1e-7) {
            return parallalDistance(Δλ, φA);
        }

        // First, we compute the constant heading
        // gdi = Gudermann inverse. For more information, see https://www.mathcurve.com/courbes3d/loxodromie/sphereloxodromie.shtml
        final double gdiφA = Math.log(Math.tan(φA / 2 + PI_4));
        final double gdiφB = Math.log(Math.tan(φB / 2 + PI_4));

        final double tanα = Δλ / (gdiφB - gdiφA);
        final double α = Math.atan(tanα);

        // Then, we can compute loxodromic distance.
        return authalicRadius * Math.abs(Δφ) / Math.cos(α);
    }

    /**
     * Compute perimeter of a part of a meridian.
     *
     * @implNote : the calculus is approximated with the ellipsoid authalic
     * radius (see {@link DefaultEllipsoid#getAuthalicRadius() } for more
     * details). It allow to distribute the flattening error on the entire
     * ellipsoid surface.
     *
     * @param Δφ Delta latitude : the angle (in degree) covered by the arc to
     * compute distance for.
     *
     * @return The perimeter of the great arc in meter.
     */
    private double meridianDistance(double Δφ) {
        return authalicRadius * DEGREE_TO_RADIAN.convert(Δφ);
    }

    /**
     * Compute the perimeter of a piece of parallal.
     *
     * @implNote : the calculus is approximated with the ellipsoid authalic
     * radius (see {@link DefaultEllipsoid#getAuthalicRadius() } for more
     * details). It allow to distribute the flattening error on the entire
     * ellipsoid surface.
     *
     * @param Δλ Delta longitude : the angle (in radian) representing the angle
     * of the arc to measure.
     * @param φ The latitude (in radian) of the parallel to measure. We need it,
     * because the radius of the parallal is dependant of it.
     *
     * @return The perimeter of the specified parallal arc, in meter.
     */
    private double parallalDistance(double Δλ, double φ) {
        final double ringRadius = Math.cos(φ) * authalicRadius;
        return ringRadius * Δλ;
    }
}
