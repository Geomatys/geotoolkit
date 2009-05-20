/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.isowrapper.geometries;

import java.util.Collections;
import java.util.Set;
import org.opengis.geometry.Boundary;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.Precision;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author sorel
 */
public class AbstractISOJTSGeometry<T extends com.vividsolutions.jts.geom.Geometry> implements Geometry{

    protected final Set<Primitive> EMPTY_PRIMITIVE_SET = Collections.emptySet();
    protected final Set<Complex> EMPTY_COMPLEXE_SET = Collections.emptySet();

    protected final T jtsGeometry;

    public AbstractISOJTSGeometry(T jtsGeometry) {
        this.jtsGeometry = jtsGeometry;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Precision getPrecision() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getMbRegion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getRepresentativePoint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boundary getBoundary() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Complex getClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSimple() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCycle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double distance(Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getDimension(DirectPosition point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCoordinateDimension() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<? extends Complex> getMaximalComplex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry transform(CoordinateReferenceSystem newCRS) throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry transform(CoordinateReferenceSystem newCRS, MathTransform transform) throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Envelope getEnvelope() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getCentroid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getConvexHull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getBuffer(double distance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMutable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry toImmutable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(DirectPosition point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean intersects(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet union(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet intersection(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet difference(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet symmetricDifference(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
