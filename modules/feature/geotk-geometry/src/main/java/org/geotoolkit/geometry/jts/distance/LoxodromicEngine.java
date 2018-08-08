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
import org.opengis.referencing.operation.TransformException;

import static org.geotoolkit.geometry.jts.distance.Utilities.PI_4;
import static org.geotoolkit.geometry.jts.distance.Utilities.DEGREE_TO_RADIAN;
import static org.geotoolkit.geometry.jts.distance.Utilities.isLatLon;

/**
 * Loxodromic distance calculus based on <a href="http://serge.mehl.free.fr/anx/loxodromie.html">{@literal M.} Serge Mehl paper</a>.
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
         * WARNING: Given coordinates must be expressed in the same CRS as the
         * one given at build time.
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

            double Δλ = DEGREE_TO_RADIAN.convert(end.x -start.x);
            final double Δφ = DEGREE_TO_RADIAN.convert(end.y - start.y);

            /* special case : if the segment is aligned on a parallal or a
             * meridian, we don't need to use complex loxodromic equation. We
             * can fallback on a simple arc perimeter.
             */
            if (-1e-7 < Δλ && Δλ < 1e-7) {
                return meridianDistance(Δφ);
            } else if (-1e-7 < Δφ && Δφ < 1e-7) {
                return parallalDistance(Δλ, start.y);
            }

            // First, we compute the constant heading
            double tanα = Δλ
                    /
                    (Math.log(Math.tan(end.y / 2 + PI_4))
                    -Math.log(Math.tan(start.y / 2 + PI_4))
                    );

            double α = Math.atan(tanα);

            // Then, we can compute loxodromic distance.
            return authalicRadius * (Δφ / Math.cos(α));
        }

        /**
         * Prepare the given coordinate in a geographic, longitude first CRS.
         * @param base The coordinate to reproject.
         * @param tr The transform to apply (should be projected -> geographic).
         * @param flipAxes A flag indicating if the given transform produces
         * latitude first coordinates. If true, we'll inverse ordinates obtained
         * from the input transform.
         *
         * @return A longitude/latitude coordinate, never null.
         */
        private static Coordinate transform(final Coordinate base, final MathTransform tr, final boolean flipAxes) {
            final double[] coords = {base.x, base.y};
            try {
                tr.transform(coords, 0, coords, 0, 1);
            } catch (TransformException ex) {
                throw new RuntimeException(ex);
            }

            return flipAxes?
                    new Coordinate(coords[1], coords[0]) :
                    new Coordinate(coords[0], coords[1]);
        }

        /**
         * Compute perimeter of a part of a meridian.
         *
         * @implNote : the calculus is approximated with the ellipsoid authalic
         * radius (see {@link DefaultEllipsoid#getAuthalicRadius() } for more
         * details). It allow to distribute the flattening error on the entire
         * ellipsoid surface.
         *
         * @param Δφ Delta latitude : the angle (in degree) covered by the arc
         * to compute distance for.
         *
         * @return The perimeter of the great arc in meter.
         */
        private double meridianDistance(double Δφ ) {
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
         * @param Δλ Delta longitude : the angle (in radian) representing the
         * angle of the arc to measure.
         * @param φ The latitude of the parallel to measure. We need it, because
         * the radius of the parallal is dependant of it.
         *
         * @return The perimeter of the specified parallal arc, in meter.
         */
        private double parallalDistance(double Δλ, double φ) {
            final double ringRadius = Math.cos(φ) * authalicRadius;
            return ringRadius * DEGREE_TO_RADIAN.convert(Δλ);
        }
}
