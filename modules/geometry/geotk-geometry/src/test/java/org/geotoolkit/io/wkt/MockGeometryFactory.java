/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io.wkt;

import java.util.List;
import java.util.Set;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.Precision;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.UnmodifiableGeometryException;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.Composite;
import org.opengis.geometry.complex.CompositeCurve;
import org.opengis.geometry.coordinate.Arc;
import org.opengis.geometry.coordinate.ArcByBulge;
import org.opengis.geometry.coordinate.ArcString;
import org.opengis.geometry.coordinate.ArcStringByBulge;
import org.opengis.geometry.coordinate.BSplineCurve;
import org.opengis.geometry.coordinate.BSplineSurface;
import org.opengis.geometry.coordinate.GenericCurve;
import org.opengis.geometry.coordinate.Geodesic;
import org.opengis.geometry.coordinate.GeodesicString;
import org.opengis.geometry.coordinate.GeometryFactory;
import org.opengis.geometry.coordinate.Knot;
import org.opengis.geometry.coordinate.KnotType;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.ParamForPoint;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.coordinate.Tin;
import org.opengis.geometry.primitive.Bearing;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveBoundary;
import org.opengis.geometry.primitive.CurveInterpolation;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.PrimitiveBoundary;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.SurfacePatch;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Quick implementation for testing purposes.
 *
 * @author Jody
 * @module pending
 */
public class MockGeometryFactory implements GeometryFactory, PrimitiveFactory, PositionFactory {
    CoordinateReferenceSystem crs;

    public Precision precision;

    public MockGeometryFactory() {
        this(DefaultGeographicCRS.WGS84);
    }

    public MockGeometryFactory( final CoordinateReferenceSystem crs ) {
        this.crs = crs;
    }

    @Override
    public Arc createArc( final Position startPoint, final Position midPoint, final Position endPoint )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    @Override
    public Arc createArc( final Position startPoint, final Position endPoint, final double bulge, final double[] normal )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    @Override
    public ArcByBulge createArcByBulge( final Position startPoint, final Position endPoint, final double bulge,
            final double[] normal ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return null;
    }

    @Override
    public ArcString createArcString( final List<Position> points ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return null;
    }

    @Override
    public ArcStringByBulge createArcStringByBulge( final List<Position> points, final double[] bulges, final List<double[]> normals )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    @Override
    public BSplineCurve createBSplineCurve( final int degree, final PointArray points, final List<Knot> knots,
            final KnotType knotSpec ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return null;
    }

    @Override
    public BSplineSurface createBSplineSurface( final List<PointArray> points, final int[] degree, final List<Knot>[] knots,
            final KnotType knotSpec ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return null;
    }

    @Override
    public DirectPosition createDirectPosition() {
        return new MockDirectPosition();
    }

    @Override
    public DirectPosition createDirectPosition( final double[] coordinates ) {
        return new MockDirectPosition(coordinates);
    }

    @Override
    public Precision getPrecision() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Position createPosition(final Position position) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PointArray createPointArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PointArray createPointArray(final double[] coordinates, final int start, final int length) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PointArray createPointArray(final float[] coordinates, final int start, final int length) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    class MockDirectPosition implements DirectPosition {
        double[] coordinates;

        MockDirectPosition() {
            this(new double[crs.getCoordinateSystem().getDimension()]);
        }

        public MockDirectPosition( final double[] coordinates ) {
            this.coordinates = coordinates;
        }

        public MockDirectPosition( final DirectPosition position ) {
            assert position.getCoordinateReferenceSystem() == crs;
            coordinates = position.getCoordinate();
        }

        @Override
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return crs;
        }

        @Override
        public double[] getCoordinate() {
            double copy[] = new double[crs.getCoordinateSystem().getDimension()];
            System.arraycopy(coordinates, 0, copy, 0, getDimension());
            return copy;
        }

        @Deprecated
        public double[] getCoordinates() {
            return getCoordinate();
        }

        @Override
        public int getDimension() {
            return crs.getCoordinateSystem().getDimension();
        }

        @Override
        public double getOrdinate( final int dimension ) throws IndexOutOfBoundsException {
            return coordinates[dimension];
        }

        @Override
        public void setOrdinate( final int dimension, final double value ) throws IndexOutOfBoundsException {
            coordinates[dimension] = value;

        }

        @Override
        public DirectPosition getDirectPosition() {
            return this;
        }

        @Deprecated
        public DirectPosition getPosition() {
            return this;
        }

        @Override
        public MockDirectPosition clone() {
            return new MockDirectPosition(this);
        }
    }

    @Override
    public Envelope createEnvelope( final DirectPosition lowerCorner,
            final DirectPosition upperCorner ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return new Envelope(){
            public double getCenter( int dimension ) {
                return getMedian( dimension );
            }

            @Override
            public double getMedian( int dimension ) {
                double lower = lowerCorner.getOrdinate(dimension);
                double upper = upperCorner.getOrdinate(dimension);
                return (upper + lower) / 2.0;
            }

            @Override
            public CoordinateReferenceSystem getCoordinateReferenceSystem() {
                return crs;
            }

            @Override
            public int getDimension() {
                return crs.getCoordinateSystem().getDimension();
            }

            @Deprecated
            public double getLength( int dimension ) {
                return getSpan( dimension );
            }

            @Override
            public double getSpan( int dimension ) {
                double lower = lowerCorner.getOrdinate(dimension);
                double upper = upperCorner.getOrdinate(dimension);
                return Math.abs(upper - lower);
            }

            @Override
            public DirectPosition getLowerCorner() {
                return lowerCorner;
            }

            @Override
            public double getMaximum( int dimension ) {
                return upperCorner.getOrdinate(dimension);
            }

            @Override
            public double getMinimum( int dimension ) {
                return lowerCorner.getOrdinate(dimension);
            }

            @Override
            public DirectPosition getUpperCorner() {
                return upperCorner;
            }
        };
    }

    @Override
    public Geodesic createGeodesic( final Position startPoint, final Position endPoint )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GeodesicString createGeodesicString( final List points )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    @Override
    public LineSegment createLineSegment( final Position startPoint, final Position endPoint )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    /** Takes a List<Position> ... */
    @Override
    public LineString createLineString( final List<Position> points ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return new LineString() {
            PointArray points;

            @Override
            public List asLineSegments() {
                return null;
            }

            @Override
            public PointArray getControlPoints() {
                return null;
            }

            @Override
            public CurveBoundary getBoundary() {
                return null;
            }

            @Override
            public Curve getCurve() {
                return null;
            }

            @Override
            public CurveInterpolation getInterpolation() {
                return null;
            }

            @Override
            public int getNumDerivativesAtEnd() {
                return 0;
            }

            @Override
            public int getNumDerivativesAtStart() {
                return 0;
            }

            @Override
            public int getNumDerivativesInterior() {
                return 0;
            }

            @Override
            public PointArray getSamplePoints() {
                return null;
            }

            @Override
            public CurveSegment reverse() {
                return null;
            }

            @Override
            public LineString asLineString( double maxSpacing, double maxOffset ) {
                return this;
            }

            @Override
            public DirectPosition forConstructiveParam( double cp ) {
                return null;
            }

            @Override
            public DirectPosition forParam( double s ) {
                return null;
            }

            @Override
            public double getEndConstructiveParam() {
                return 0;
            }

            @Override
            public double getEndParam() {
                return 0;
            }

            @Override
            public DirectPosition getEndPoint() {
                return points.getDirectPosition(points.size() - 1, null);
            }

            @Override
            public ParamForPoint getParamForPoint( DirectPosition p ) {
                return null;
            }

            @Override
            public double getStartConstructiveParam() {
                return 0;
            }

            @Override
            public double getStartParam() {
                return 0;
            }

            @Override
            public DirectPosition getStartPoint() {
                return points.getDirectPosition(0, null);
            }

            @Override
            public double[] getTangent( double s ) {
                return null;
            }

            @Override
            public double length( DirectPosition point1, DirectPosition point2 ) {
                return 0;
            }

            @Override
            public double length( double cparam1, double cparam2 ) {
                return 0;
            }
        };
    }

    @Override
    public MultiPrimitive createMultiPrimitive() {
        return null;
    }

    @Override
    public Polygon createPolygon( final SurfaceBoundary boundary )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    @Override
    public Polygon createPolygon( final SurfaceBoundary boundary, final Surface spanSurface )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    @Override
    public PolyhedralSurface createPolyhedralSurface( final List<Polygon> tiles )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    @Override
    public Tin createTin( final Set<Position> post, final Set<LineString> stopLines, final Set<LineString> breakLines, final double maxLength )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return null;
    }

    @Override
    public Curve createCurve( final List<CurveSegment> segments ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return new MockCurve(segments);
    }

    class MockCurve implements Curve {
        List<CurveSegment> segments;

        MockCurve( final List<CurveSegment> segments ) {
            this.segments = segments;
        }

        @Override
        public List<CurveSegment> getSegments() {
            return segments;
        }

        @Override
        public MockCurve clone() {
            return new MockCurve(getSegments());
        }

        @Override
        public CompositeCurve getComposite() {
            return null;
        }

        @Override
        public int getOrientation() {
            return 0;
        }

        @Override
        public MockCurve getPrimitive() {
            return this;
        }

        @Override
        public Set<Complex> getComplexes() {
            return null;
        }

        @Override
        public Set<Primitive> getContainedPrimitives() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<Primitive> getContainingPrimitives() {
            return null;
        }

        @Override
        public OrientableCurve[] getProxy() {
            return null;
        }

        @Override
        public CurveBoundary getBoundary() {
            return null;
        }

        @Override
        public Geometry getBuffer( final double distance ) {
            return null;
        }

        @Override
        public DirectPosition getCentroid() {
            return null;
        }

        @Override
        public Complex getClosure() {
            return null;
        }

        @Override
        public Geometry getConvexHull() {
            return null;
        }

        @Override
        public int getCoordinateDimension() {
            return crs.getCoordinateSystem().getDimension();
        }

        @Override
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return crs;
        }

        @Override
        public int getDimension( final DirectPosition point ) {
            return 0;
        }

        @Override
        public double distance(final Geometry geometry) {
            return 0;
        }

        @Deprecated
        public double getDistance(final Geometry geometry) {
            return distance(geometry);
        }

        @Override
        public Envelope getEnvelope() {
            return null;
        }

        @Override
        public Set<Complex> getMaximalComplex() {
            return null;
        }

        @Override
        public Geometry getMbRegion() {
            return null;
        }

        @Override
        public Precision getPrecision() {
            return precision;
        }

        @Override
        public DirectPosition getRepresentativePoint() {
            return null;
        }

        @Override
        public boolean isCycle() {
            return false;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public boolean isSimple() {
            return false;
        }

        @Override
        public Geometry toImmutable() {
            return this;
        }

        @Override
        public Geometry transform( final CoordinateReferenceSystem newCRS ) throws TransformException {
            return null;
        }

        @Override
        public Geometry transform( final CoordinateReferenceSystem newCRS, final MathTransform transform )
                throws TransformException {
            return null;
        }

        @Override
        public boolean contains( final TransfiniteSet pointSet ) {
            return false;
        }

        @Override
        public boolean contains( final DirectPosition point ) {
            return false;
        }

        @Override
        public TransfiniteSet difference( final TransfiniteSet pointSet ) {
            return null;
        }

        @Override
        public boolean equals( final TransfiniteSet pointSet ) {
            return false;
        }

        @Override
        public TransfiniteSet intersection( final TransfiniteSet pointSet ) {
            return null;
        }

        @Override
        public boolean intersects( final TransfiniteSet pointSet ) {
            return false;
        }

        @Override
        public TransfiniteSet symmetricDifference( final TransfiniteSet pointSet ) {
            return null;
        }

        @Override
        public TransfiniteSet union( final TransfiniteSet pointSet ) {
            return null;
        }

        @Override
        public LineString asLineString( final double maxSpacing, final double maxOffset ) {
            return null;
        }

        @Override
        public DirectPosition forConstructiveParam( final double cp ) {
            return null;
        }

        @Override
        public DirectPosition forParam( final double s ) {
            return null;
        }

        @Override
        public double getEndConstructiveParam() {
            return 0;
        }

        @Override
        public double getEndParam() {
            return 0;
        }

        @Override
        public DirectPosition getEndPoint() {
            return null;
        }

        @Override
        public ParamForPoint getParamForPoint( final DirectPosition p ) {
            return null;
        }

        @Override
        public double getStartConstructiveParam() {
            return 0;
        }

        @Override
        public double getStartParam() {
            return 0;
        }

        @Override
        public DirectPosition getStartPoint() {
            return null;
        }

        @Override
        public double[] getTangent( final double s ) {
            return null;
        }

        @Override
        public double length( final DirectPosition point1, final DirectPosition point2 ) {
            return 0;
        }

        @Override
        public double length( final double cparam1, final double cparam2 ) {
            return 0;
        }

        @Override
        public PointArray getSamplePoints() {
            return null;
        }

        @Override
        public GenericCurve reverse() {
            return null;
        }
    }

    @Override
    public Point createPoint( final double[] coordinates ) throws MismatchedDimensionException {
        return createPoint(createPoint(coordinates));
    }

    @Override
    public Point createPoint( final Position position ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return new MockPoint(position.getDirectPosition());
    }

    class MockPoint implements Point {
        private DirectPosition position;

        MockPoint( final DirectPosition position ) {
            this.position = position;
        }

        @Override
        public MockPoint clone() {
            return new MockPoint(new MockDirectPosition(position));
        }

        @Override
        public Bearing getBearing( final Position toPoint ) {
            return null;
        }

        @Override
        public DirectPosition getDirectPosition() {
            return position;
        }

        @Deprecated
        public DirectPosition getPosition() {
            return position;
        }

        @Override
        public void setDirectPosition( final DirectPosition position ) throws UnmodifiableGeometryException {
            this.position = position;
        }

        @Deprecated
        public void setPosition( final DirectPosition position ) throws UnmodifiableGeometryException {
            this.position = position;
        }

        @Override
        public Set<Complex> getComplexes() {
            return null;
        }

        @Override
        public Composite getComposite() {
            return null;
        }

        @Override
        public Set<Primitive> getContainedPrimitives() {
            return null;
        }

        @Override
        public Set<Primitive> getContainingPrimitives() {
            return null;
        }

        @Override
        public OrientablePrimitive[] getProxy() {
            return null;
        }

        @Override
        public PrimitiveBoundary getBoundary() {
            return null;
        }

        @Override
        public Geometry getBuffer( final double distance ) {
            return null;
        }

        @Override
        public DirectPosition getCentroid() {
            return position;
        }

        @Override
        public Complex getClosure() {
            return null;
        }

        @Override
        public Geometry getConvexHull() {
            return null;
        }

        @Override
        public int getCoordinateDimension() {
            return getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
        }

        @Override
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return crs;
        }

        @Override
        public int getDimension( final DirectPosition point ) {
            return 0;
        }

        @Override
        public double distance(final Geometry geometry) {
            return 0;
        }

        @Deprecated
        public double getDistance(final Geometry geometry) {
            return distance(geometry);
        }

        @Override
        public Envelope getEnvelope() {
            return null;
        }

        @Override
        public Set<Complex> getMaximalComplex() {
            return null;
        }

        @Override
        public Geometry getMbRegion() {
            return null;
        }

        @Override
        public Precision getPrecision() {
            return precision;
        }

        @Override
        public DirectPosition getRepresentativePoint() {
            return position;
        }

        @Override
        public boolean isCycle() {
            return false;
        }

        @Override
        public boolean isMutable() {
            return true;
        }

        @Override
        public boolean isSimple() {
            return true;
        }

        @Override
        public Geometry toImmutable() {
            return null;
        }

        @Override
        public Geometry transform( final CoordinateReferenceSystem newCRS ) throws TransformException {
            return null;
        }

        @Override
        public Geometry transform( final CoordinateReferenceSystem newCRS, final MathTransform transform )
                throws TransformException {
            return null;
        }

        @Override
        public boolean contains( final TransfiniteSet pointSet ) {
            return pointSet.contains(position);
        }

        @Override
        public boolean contains( final DirectPosition point ) {
            return point.equals(position);
        }

        @Override
        public TransfiniteSet difference( final TransfiniteSet pointSet ) {
            return null;
        }

        @Override
        public boolean equals( final TransfiniteSet pointSet ) {
            return false;
        }

        @Override
        public TransfiniteSet intersection( final TransfiniteSet pointSet ) {
            return null;
        }

        @Override
        public boolean intersects( final TransfiniteSet pointSet ) {
            return false;
        }

        @Override
        public TransfiniteSet symmetricDifference( final TransfiniteSet pointSet ) {
            return null;
        }

        @Override
        public TransfiniteSet union( final TransfiniteSet pointSet ) {
            return null;
        }
    }

    @Override
    public Primitive createPrimitive( final Envelope envelope )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ring createRing( final List<OrientableCurve> curves ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Solid createSolid( final SolidBoundary boundary ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Surface createSurface( final List<SurfacePatch> surfaces ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Surface createSurface( final SurfaceBoundary boundary )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SurfaceBoundary createSurfaceBoundary( final Ring exterior, final List<Ring> interiors )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }
}
