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
import org.opengis.geometry.coordinate.Geodesic;
import org.opengis.geometry.coordinate.GeodesicString;
import org.opengis.geometry.coordinate.GeometryFactory;
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
    public Arc createArc( final Position startPoint, final Position midPoint, final Position endPoint )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    public Arc createArc( final Position startPoint, final Position endPoint, final double bulge, final double[] normal )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    public ArcByBulge createArcByBulge( final Position startPoint, final Position endPoint, final double bulge,
            final double[] normal ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return null;
    }

    public ArcString createArcString( final List points ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return null;
    }

    public ArcStringByBulge createArcStringByBulge( final List points, final double[] bulges, final List normals )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    public BSplineCurve createBSplineCurve( final int degree, final PointArray points, final List knots,
            final KnotType knotSpec ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return null;
    }

    public BSplineSurface createBSplineSurface( final List points, final int[] degree, final List[] knots,
            final KnotType knotSpec ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return null;
    }

    public DirectPosition createDirectPosition() {
        return new MockDirectPosition();
    }

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

        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return crs;
        }
        public double[] getCoordinate() {
            double copy[] = new double[crs.getCoordinateSystem().getDimension()];
            System.arraycopy(coordinates, 0, copy, 0, getDimension());
            return copy;
        }
        @Deprecated
        public double[] getCoordinates() {
            return getCoordinate();
        }
        public int getDimension() {
            return crs.getCoordinateSystem().getDimension();
        }

        public double getOrdinate( final int dimension ) throws IndexOutOfBoundsException {
            return coordinates[dimension];
        }

        public void setOrdinate( final int dimension, final double value ) throws IndexOutOfBoundsException {
            coordinates[dimension] = value;

        }
        public DirectPosition getDirectPosition() {
            return this;
        }
        @Deprecated
        public DirectPosition getPosition() {
            return this;
        }
        public MockDirectPosition clone() {
            return new MockDirectPosition(this);
        }
    }

    public Envelope createEnvelope( final DirectPosition lowerCorner,
            final DirectPosition upperCorner ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return new Envelope(){
            public double getCenter( int dimension ) {
                return getMedian( dimension );
            }
            public double getMedian( int dimension ) {
                double lower = lowerCorner.getOrdinate(dimension);
                double upper = upperCorner.getOrdinate(dimension);
                return (upper + lower) / 2.0;
            }
            public CoordinateReferenceSystem getCoordinateReferenceSystem() {
                return crs;
            }
            public int getDimension() {
                return crs.getCoordinateSystem().getDimension();
            }
            public double getLength( int dimension ) {
                return getSpan( dimension );
            }
            public double getSpan( int dimension ) {
                double lower = lowerCorner.getOrdinate(dimension);
                double upper = upperCorner.getOrdinate(dimension);
                return Math.abs(upper - lower);
            }
            public DirectPosition getLowerCorner() {
                return lowerCorner;
            }

            public double getMaximum( int dimension ) {
                return upperCorner.getOrdinate(dimension);
            }

            public double getMinimum( int dimension ) {
                return lowerCorner.getOrdinate(dimension);
            }

            public DirectPosition getUpperCorner() {
                return upperCorner;
            }
        };
    }

    public Geodesic createGeodesic( final Position startPoint, final Position endPoint )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }

    public GeodesicString createGeodesicString( final List points )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    public LineSegment createLineSegment( final Position startPoint, final Position endPoint )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    /** Takes a List<Position> ... */
    public LineString createLineString( final List points ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return new LineString(){
            PointArray points;
            public List asLineSegments() {
                return null;
            }
            public PointArray getControlPoints() {
                return null;
            }
            public CurveBoundary getBoundary() {
                return null;
            }

            public Curve getCurve() {
                return null;
            }

            public CurveInterpolation getInterpolation() {
                return null;
            }

            public int getNumDerivativesAtEnd() {
                return 0;
            }

            public int getNumDerivativesAtStart() {
                return 0;
            }

            public int getNumDerivativesInterior() {
                return 0;
            }

            public PointArray getSamplePoints() {
                return null;
            }

            public CurveSegment reverse() {
                return null;
            }

            public LineString asLineString( double maxSpacing, double maxOffset ) {
                return this;
            }

            public DirectPosition forConstructiveParam( double cp ) {
                return null;
            }

            public DirectPosition forParam( double s ) {
                return null;
            }

            public double getEndConstructiveParam() {
                return 0;
            }

            public double getEndParam() {
                return 0;
            }

            public DirectPosition getEndPoint() {
                return points.getDirectPosition(points.size() - 1, null);
            }

            public ParamForPoint getParamForPoint( DirectPosition p ) {
                return null;
            }

            public double getStartConstructiveParam() {
                return 0;
            }

            public double getStartParam() {
                return 0;
            }

            public DirectPosition getStartPoint() {
                return points.getDirectPosition(0, null);
            }

            public double[] getTangent( double s ) {
                return null;
            }

            public double length( Position point1, Position point2 ) {
                return 0;
            }

            public double length( double cparam1, double cparam2 ) {
                return 0;
            }
        };
    }

    public MultiPrimitive createMultiPrimitive() {
        return null;
    }

    public Polygon createPolygon( final SurfaceBoundary boundary )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    public Polygon createPolygon( final SurfaceBoundary boundary, final Surface spanSurface )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    public PolyhedralSurface createPolyhedralSurface( final List tiles )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    public Tin createTin( final Set post, final Set stopLines, final Set breakLines, final double maxLength )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        return null;
    }

    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return null;
    }
    public Curve createCurve( final List segments ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return new MockCurve(segments);
    }
    class MockCurve implements Curve {
        List segments;
        MockCurve( final List segments ) {
            this.segments = segments;
        }
        public List getSegments() {
            return segments;
        }
        public MockCurve clone() {
            return new MockCurve(getSegments());
        }
        public CompositeCurve getComposite() {
            return null;
        }

        public int getOrientation() {
            return 0;
        }
        public MockCurve getPrimitive() {
            return this;
        }

        public Set getComplexes() {
            return null;
        }

        public Set getContainedPrimitives() {
            // TODO Auto-generated method stub
            return null;
        }

        public Set getContainingPrimitives() {
            return null;
        }

        public OrientableCurve[] getProxy() {
            return null;
        }

        public CurveBoundary getBoundary() {
            return null;
        }

        public Geometry getBuffer( final double distance ) {
            return null;
        }

        public DirectPosition getCentroid() {
            return null;
        }

        public Complex getClosure() {
            return null;
        }

        public Geometry getConvexHull() {
            return null;
        }

        public int getCoordinateDimension() {
            return crs.getCoordinateSystem().getDimension();
        }

        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return crs;
        }

        public int getDimension( final DirectPosition point ) {
            return 0;
        }

        public double distance(final Geometry geometry) {
            return 0;
        }

        public double getDistance(final Geometry geometry) {
            return distance(geometry);
        }

        public Envelope getEnvelope() {
            return null;
        }

        public Set getMaximalComplex() {
            return null;
        }

        public Geometry getMbRegion() {
            return null;
        }

        public Precision getPrecision() {
            return precision;
        }

        public DirectPosition getRepresentativePoint() {
            return null;
        }

        public boolean isCycle() {
            return false;
        }

        public boolean isMutable() {
            return false;
        }

        public boolean isSimple() {
            return false;
        }

        public Geometry toImmutable() {
            return this;
        }

        public Geometry transform( final CoordinateReferenceSystem newCRS ) throws TransformException {
            return null;
        }

        public Geometry transform( final CoordinateReferenceSystem newCRS, final MathTransform transform )
                throws TransformException {
            return null;
        }

        public boolean contains( final TransfiniteSet pointSet ) {
            return false;
        }

        public boolean contains( final DirectPosition point ) {
            return false;
        }

        public TransfiniteSet difference( final TransfiniteSet pointSet ) {
            return null;
        }

        public boolean equals( final TransfiniteSet pointSet ) {
            return false;
        }

        public TransfiniteSet intersection( final TransfiniteSet pointSet ) {
            return null;
        }

        public boolean intersects( final TransfiniteSet pointSet ) {
            return false;
        }

        public TransfiniteSet symmetricDifference( final TransfiniteSet pointSet ) {
            return null;
        }

        public TransfiniteSet union( final TransfiniteSet pointSet ) {
            return null;
        }

        public LineString asLineString( final double maxSpacing, final double maxOffset ) {
            return null;
        }

        public DirectPosition forConstructiveParam( final double cp ) {
            return null;
        }

        public DirectPosition forParam( final double s ) {
            return null;
        }

        public double getEndConstructiveParam() {
            return 0;
        }

        public double getEndParam() {
            return 0;
        }

        public DirectPosition getEndPoint() {
            return null;
        }

        public ParamForPoint getParamForPoint( final DirectPosition p ) {
            return null;
        }

        public double getStartConstructiveParam() {
            return 0;
        }

        public double getStartParam() {
            return 0;
        }

        public DirectPosition getStartPoint() {
            return null;
        }

        public double[] getTangent( final double s ) {
            return null;
        }

        public double length( final Position point1, final Position point2 ) {
            return 0;
        }

        public double length( final double cparam1, final double cparam2 ) {
            return 0;
        }
    }

    public Point createPoint( final double[] coordinates ) throws MismatchedDimensionException {
        return createPoint(createPoint(coordinates));
    }
    public Point createPoint( final Position position ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        return new MockPoint(position.getDirectPosition());
    }
    class MockPoint implements Point {
        private DirectPosition position;
        MockPoint( final DirectPosition position ) {
            this.position = position;
        }
        public MockPoint clone() {
            return new MockPoint(new MockDirectPosition(position));
        }
        public Bearing getBearing( final Position toPoint ) {
            return null;
        }
        public DirectPosition getDirectPosition() {
            return position;
        }
        @Deprecated
        public DirectPosition getPosition() {
            return position;
        }
        public void setDirectPosition( final DirectPosition position ) throws UnmodifiableGeometryException {
            this.position = position;
        }
        @Deprecated
        public void setPosition( final DirectPosition position ) throws UnmodifiableGeometryException {
            this.position = position;
        }
        public Set getComplexes() {
            return null;
        }

        public Composite getComposite() {
            return null;
        }

        public Set getContainedPrimitives() {
            return null;
        }

        public Set getContainingPrimitives() {
            return null;
        }

        public OrientablePrimitive[] getProxy() {
            return null;
        }

        public PrimitiveBoundary getBoundary() {
            return null;
        }

        public Geometry getBuffer( final double distance ) {
            return null;
        }

        public DirectPosition getCentroid() {
            return position;
        }

        public Complex getClosure() {
            return null;
        }

        public Geometry getConvexHull() {
            return null;
        }

        public int getCoordinateDimension() {
            return getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
        }

        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return crs;
        }

        public int getDimension( final DirectPosition point ) {
            return 0;
        }

        public double distance(final Geometry geometry) {
            return 0;
        }

        public double getDistance(final Geometry geometry) {
            return distance(geometry);
        }

        public Envelope getEnvelope() {
            return null;
        }

        public Set getMaximalComplex() {
            return null;
        }

        public Geometry getMbRegion() {
            return null;
        }

        public Precision getPrecision() {
            return precision;
        }
        public DirectPosition getRepresentativePoint() {
            return position;
        }
        public boolean isCycle() {
            return false;
        }

        public boolean isMutable() {
            return true;
        }
        public boolean isSimple() {
            return true;
        }
        public Geometry toImmutable() {
            return null;
        }

        public Geometry transform( final CoordinateReferenceSystem newCRS ) throws TransformException {
            return null;
        }

        public Geometry transform( final CoordinateReferenceSystem newCRS, final MathTransform transform )
                throws TransformException {
            return null;
        }

        public boolean contains( final TransfiniteSet pointSet ) {
            return pointSet.contains(position);
        }

        public boolean contains( final DirectPosition point ) {
            return point.equals(position);
        }

        public TransfiniteSet difference( final TransfiniteSet pointSet ) {
            return null;
        }

        public boolean equals( final TransfiniteSet pointSet ) {
            return false;
        }

        public TransfiniteSet intersection( final TransfiniteSet pointSet ) {
            return null;
        }

        public boolean intersects( final TransfiniteSet pointSet ) {
            return false;
        }

        public TransfiniteSet symmetricDifference( final TransfiniteSet pointSet ) {
            return null;
        }

        public TransfiniteSet union( final TransfiniteSet pointSet ) {
            return null;
        }
    }

    public Primitive createPrimitive( final Envelope envelope )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }
    public Ring createRing( final List curves ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }
    public Solid createSolid( final SolidBoundary boundary ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }
    public Surface createSurface( final List surfaces ) throws MismatchedReferenceSystemException,
            MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }
    public Surface createSurface( final SurfaceBoundary boundary )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }
    public SurfaceBoundary createSurfaceBoundary( final Ring exterior, final List interiors )
            throws MismatchedReferenceSystemException, MismatchedDimensionException {
        // TODO Auto-generated method stub
        return null;
    }
}
