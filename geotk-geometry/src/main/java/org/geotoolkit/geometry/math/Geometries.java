
package org.geotoolkit.geometry.math;

import java.util.Arrays;
import java.util.List;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.operation.matrix.Matrix4;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import static org.geotoolkit.geometry.math.Vectors.*;
import org.locationtech.jts.geom.Coordinate;

/**
 * Origin : Adapted from Unlicense-Lib
 *
 * Math utilities for TIN creation.
 *
 */
public class Geometries {

    private static final double TOLERANCE = 0;//Math.ulp(2048.0f)*5 ;

    public static double[] calculateNormal(Coordinate a, Coordinate b, Coordinate c){
        final double[] ab = {b.x-a.x,b.y-a.y,b.z-a.z};
        final double[] ac = {c.x-a.x,c.y-a.y,c.z-a.z};
        final double[] res = cross(ab, ac);
        Vectors.normalize(res, res);
        return res;
    }

    /**
     * Calculate normal of triangle made of given 3 points.
     *
     * @param a first triangle point
     * @param b second triangle point
     * @param c third triangle point
     * @return triangle normal
     */
    public static float[] calculateNormal(float[] a, float[] b, float[] c){
        final float[] ab = Vectors.subtract(b,a);
        final float[] ac = Vectors.subtract(c,a);
        final float[] res = cross(ab, ac);
        Vectors.normalize(res, res);
        return res;
    }

    /**
     * Calculate normal of triangle made of given 3 points.
     *
     * @param a first triangle point
     * @param b second triangle point
     * @param c third triangle point
     * @return triangle normal
     */
    public static double[] calculateNormal(double[] a, double[] b, double[] c){
        final double[] ab = Vectors.subtract(b,a);
        final double[] ac = Vectors.subtract(c,a);
        final double[] res = Vectors.cross(ab, ac);
        Vectors.normalize(res, res);
        return res;
    }

    public static double[] calculateNormalD(float[] a, float[] b, float[] c){
        final double[] ab = subtract(b, a);
        final double[] ac = subtract(c, a);
        final double[] res = cross(ab, ac);
        normalize(res);
        return res;
    }

    public static Vector calculateNormal(Tuple a, Tuple b, Tuple c){
        Vector ab = Vectors.createDouble(a.getDimension());
        ab.add(b);
        ab.subtract(a);
        Vector ac = Vectors.createDouble(a.getDimension());
        ac.add(c);
        ac.subtract(a);
        Vector res = ab.cross(ac);
        res.normalize();
        return res;
    }

    public static double[] subtract(float[] A, float[] B){
        return new double[]{A[0]-B[0], A[1]-B[1], A[2]-B[2]};
    }

    public static double[] cross(double[] vector, double[] other){
        final double newX = (vector[1] * other[2]) - (vector[2] * other[1]);
        final double newY = (vector[2] * other[0]) - (vector[0] * other[2]);
        final double newZ = (vector[0] * other[1]) - (vector[1] * other[0]);
        return new double[]{newX,newY,newZ};
    }

    public static float[] cross(float[] vector, float[] other){
        final float newX = (vector[1] * other[2]) - (vector[2] * other[1]);
        final float newY = (vector[2] * other[0]) - (vector[0] * other[2]);
        final float newZ = (vector[0] * other[1]) - (vector[1] * other[0]);
        return new float[]{newX,newY,newZ};
    }

    public static void normalize(float[] vector){
        final float nlength = 1f/(float)length(vector);
        vector[0] *= nlength;
        vector[1] *= nlength;
        vector[2] *= nlength;
    }

    public static void normalize(double[] vector){
        final double nlength = 1d/length(vector);
        vector[0] *= nlength;
        vector[1] *= nlength;
        vector[2] *= nlength;
    }

    public static double length(float[] vector){
        final double length = vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2];
        return Math.sqrt(length);
    }

    public static double length(double[] vector){
        final double length = vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2];
        return Math.sqrt(length);
    }

    public static boolean isOnLine(Tuple a, Tuple b, Tuple p) {
//        final double d = Math.abs(distanceSquare(a, b, p));
//        return d < TOLERANCE && d > -TOLERANCE;

        if(lineSide(a,b,p) != 0) return false;
        final double[] ab = {b.get(0)-a.get(0),b.get(1)-a.get(1)};
        final double[] ac = {p.get(0)-a.get(0),p.get(1)-a.get(1)};
        final double e = dot2D(ac, ab);
        // cases where point is outside segment
        if (e <= 0.0f) return false;
        final double f = dot2D(ab, ab);
        if (e >= f) return false;
        return true;
    }

    public static boolean isOnLine(float[] a, float[] b, float[] p) {
//        final double d = Math.abs(distanceSquare(a, b, p));
//        return d < TOLERANCE && d > -TOLERANCE;

        if(lineSide(a,b,p) != 0) return false;
        final double[] ab = subtract(b,a);
        final double[] ac = subtract(p,a);
        final double e = dot2D(ac, ab);
        // cases where point is outside segment
        if (e <= 0.0f) return false;
        final double f = dot2D(ab, ab);
        if (e >= f) return false;
        return true;
    }

    public static double lineSide(Tuple a, Tuple b, Tuple c) {
        return (b.get(0)-a.get(0)) * (c.get(1)-a.get(1)) - (c.get(0)-a.get(0)) * (b.get(1)-a.get(1));
    }

    public static double lineSide(float[] a, float[] b, float[] c) {
        return (b[0]-a[0]) * (c[1]-a[1]) - (c[0]-a[0]) * (b[1]-a[1]);
    }

    public static boolean inTriangle2D(Tuple a, Tuple b, Tuple c, Tuple p){
        final double[] bary = getBarycentricValue2D(a, b, c, p);
        return bary[1] >= 0.0 && bary[2] >= 0.0 && (bary[1] + bary[2]) <= (1.0+TOLERANCE);
    }

    public static boolean inTriangle2D(float[] a, float[] b, float[] c, float[] p){
        final double[] bary = getBarycentricValue(a, b, c, p);
        return bary[1] >= 0.0 && bary[2] >= 0.0 && (bary[1] + bary[2]) <= (1.0+TOLERANCE);
    }

    public static boolean isCounterClockwise(Tuple a, Tuple b, Tuple c) {
        return lineSide(a, b, c) > 0;
    }

    public static boolean isCounterClockwise(float[] a, float[] b, float[] c) {
        return lineSide(a, b, c) > 0;
    }

    public static double[] getBarycentricValue2D(Tuple a, Tuple b, Tuple c, Tuple p){
        final double[] v0 = {b.get(0)-a.get(0), b.get(1)-a.get(1)};
        final double[] v1 = {c.get(0)-a.get(0), c.get(1)-a.get(1)};
        final double[] v2 = {p.get(0)-a.get(0), p.get(1)-a.get(1)};
        final double d00 = dot2D(v0,v0);
        final double d01 = dot2D(v0,v1);
        final double d11 = dot2D(v1,v1);
        final double d20 = dot2D(v2,v0);
        final double d21 = dot2D(v2,v1);
        final double denom = d00 * d11 - d01 * d01;
        final double v = (d11 * d20 - d01 * d21) / denom;
        final double w = (d00 * d21 - d01 * d20) / denom;
        final double u = 1.0f - v - w;
        return new double[]{u, v, w};
    }

    public static double[] getBarycentricValue(final float[] a, final float[] b, final float[] c, final float[] p){
        final double[] v0 = subtract(b, a);
        final double[] v1 = subtract(c, a);
        final double[] v2 = subtract(p, a);
        final double d00 = dot2D(v0,v0);
        final double d01 = dot2D(v0,v1);
        final double d11 = dot2D(v1,v1);
        final double d20 = dot2D(v2,v0);
        final double d21 = dot2D(v2,v1);
        final double denom = d00 * d11 - d01 * d01;
        final double v = (d11 * d20 - d01 * d21) / denom;
        final double w = (d00 * d21 - d01 * d20) / denom;
        final double u = 1.0f - v - w;
        return new double[]{u, v, w};
    }

    public static boolean inCircle(Tuple a, Tuple b, Tuple c, Tuple d) {
        return inCircle(a.toArrayFloat(), b.toArrayFloat(), c.toArrayFloat(), d.toArrayFloat());
    }

    public static boolean inCircle(float[] a, float[] b, float[] c, float[] d) {
        double a2 = a[0] * a[0] + a[1] * a[1];
        double b2 = b[0] * b[0] + b[1] * b[1];
        double c2 = c[0] * c[0] + c[1] * c[1];
        double d2 = d[0] * d[0] + d[1] * d[1];

        double det44 = 0;
        det44 += d2 * det33(a[0], a[1], 1, b[0], b[1], 1, c[0], c[1], 1);
        det44 -= d[0] * det33(a2, a[1], 1, b2, b[1], 1, c2, c[1], 1);
        det44 += d[1] * det33(a2, a[0], 1, b2, b[0], 1, c2, c[0], 1);
        det44 -= 1 * det33(a2, a[0], a[1], b2, b[0], b[1], c2, c[0], c[1]);

        if (det44 < 0) {
            return true;
        }
        return false;
    }

    private static double det33(double... m) {
        double det33 = 0;
        det33 += m[0] * (m[4] * m[8] - m[5] * m[7]);
        det33 -= m[1] * (m[3] * m[8] - m[5] * m[6]);
        det33 += m[2] * (m[3] * m[7] - m[4] * m[6]);
        return det33;
    }

    public static double dot2D(final float[] vector, final float[] other){
        double dot = 0;
        for(int i=0;i<2;i++){
            dot += (double)vector[i]*(double)other[i];
        }
        return dot;
    }

    public static double dot2D(final double[] vector, final double[] other){
        double dot = 0;
        for(int i=0;i<2;i++){
            dot += vector[i]*other[i];
        }
        return dot;
    }

    public static double distance(Tuple a, Vector planNormal, double planD){
        return planNormal.dot(a) - planD;
    }

    public static double distance(Vector3d a, Vector3d planNormal, double planD){
        return planNormal.dot(a) - planD;
    }

    public static double distanceSquare(final float[] segmentStart, final float[] segmentEnd, final float[] point){
        final double[] ab = subtract(segmentEnd, segmentStart);
        final double[] ac = subtract(point,segmentStart);
        final double[] bc = subtract(point,segmentEnd);
        final double e = dot2D(ac, ab);
        // cases where point is outside segment
        if (e <= 0.0f) return dot2D(ac, ac);
        final double f = dot2D(ab, ab);
        if (e >= f) return dot2D(bc, bc);
        // cases where point projects onto segment
        return dot2D(ac, ac)- e*e /f;
    }

    /**
     * Calculate normal of triangle made of given 3 points.
     *
     * @param a first triangle point
     * @param b second triangle point
     * @param c third triangle point
     * @param buffer to store normal values
     * @return triangle normal
     */
    public static float[] calculateNormal(float[] a, float[] b, float[] c, float[] buffer){
        final float[] ab = Vectors.subtract(b,a);
        final float[] ac = Vectors.subtract(c,a);
        buffer = Vectors.cross(ab, ac, buffer);
        return Vectors.normalize(buffer, buffer);
    }

    public static double[] calculateCircleCenter(double[] a, double[] b, double[] c) {
        final double as = (b[1]-a[1]) / (b[0]-a[0]);
        final double bs = (c[1]-b[1]) / (c[0]-b[0]);
        final double[] center = new double[2];
        center[0] = (as * bs * (a[1]-c[1]) + bs * (a[0]+b[0]) - as * (b[0]+c[0])) / (2 * (bs-as));
        center[1] = -1.0 * (center[0] - (a[0]+b[0])/2.0) / as + (a[1]+b[1])/2.0;
        return center;
    }

    /**
     *
     * @param c symmetry center
     * @param p point to reflect
     * @return reflected point
     */
    public static double[] calculatePointSymmetry(double[] c, double[] p){
        final double[] r = new double[c.length];
        for(int i=0;i<r.length;i++) r[i] = (2*c[i])-p[i];
        return r;
    }

    /**
     * Test if the Point p is on the line porting the edge
     *
     * @param a line origine
     * @param b line end
     * @param p Point to test
     * @return true/false
     */
    public static boolean isOnLine(Coordinate a, Coordinate b, Coordinate p) {
        // test if the vector product is zero
        return lineSide(a,b,p) == 0;
    }


    /**
     * return true if a, b and c turn in Counter Clockwise direction
     *
     * @param a,the 3 points to test
     * @param b the 3 points to test
     * @param c the 3 points to test
     * @return true if a, b and c turn in Counter Clockwise direction
     */
    public static boolean isCounterClockwise(Coordinate a, Coordinate b, Coordinate c) {
        return lineSide(a, b, c) > 0;
    }

    /**
     * Test the side of a point compare to a line.
     *
     * @param a line start
     * @param b line end
     * @param c to test
     * @return greater than 0 if point is on the left side
     *          equal 0 if point is on the line
     *          inferior than 0 if point is on the right side
     */
    public static double lineSide(Coordinate a, Coordinate b, Coordinate c) {
        return (b.x-a.x) * (c.y-a.y) - (c.x-a.x) * (b.y-a.y);
    }

    /**
     * The Delaunay criteria:
     *
     * test if the point d is inside the circumscribed circle of triangle a,b,c
     *
     * @param a first triangle point
     * @param b second triangle point
     * @param c third triangle point
     * @param d point to test
     * @return true/false
     */
    public static boolean inCircle(Coordinate a, Coordinate b, Coordinate c, Coordinate d) {
        /*
         if "d" is strictly INSIDE the circle, then

         |d² dx dy 1|
         |a² ax ay 1|
         det |b² bx by 1| < 0
         |c² cx cy 1|

         */
        double a2 = a.x * a.x + a.y * a.y;
        double b2 = b.x * b.x + b.y * b.y;
        double c2 = c.x * c.x + c.y * c.y;
        double d2 = d.x * d.x + d.y * d.y;

        double det44 = 0;
        det44 += d2 * det33(a.x, a.y, 1, b.x, b.y, 1, c.x, c.y, 1);
        det44 -= d.x * det33(a2, a.y, 1, b2, b.y, 1, c2, c.y, 1);
        det44 += d.y * det33(a2, a.x, 1, b2, b.x, 1, c2, c.x, 1);
        det44 -= 1 * det33(a2, a.x, a.y, b2, b.x, b.y, c2, c.x, c.y);

        if (det44 < 0) {
            return true;
        }
        return false;
    }

    /**
     * Test if a point is inside given triangle.
     *
     * @param a first triangle point
     * @param b second triangle point
     * @param c third triangle point
     * @param p point to test
     * @return true if point is inside triangle
     */
    public static boolean inTriangle(double[] a, double[] b, double[] c, double[] p){
        final double[] bary = getBarycentricValue(a, b, c, p);
        return bary[1] >= 0.0 && bary[2] >= 0.0 && (bary[1] + bary[2]) <= 1.0;
    }

    /**
     * Calculate the barycentric value in triangle for given point.
     *
     * @param a first triangle point
     * @param b second triangle point
     * @param c third triangle point
     * @param p point to test
     * @return Vector barycentric values
     */
    public static double[] getBarycentricValue(final double[] a, final double[] b, final double[] c, final double[] p){
        final double[] v0 = Vectors.subtract(b, a);
        final double[] v1 = Vectors.subtract(c, a);
        final double[] v2 = Vectors.subtract(p, a);
        final double d00 = dot(v0, v0);
        final double d01 = dot(v0,v1);
        final double d11 = dot(v1,v1);
        final double d20 = dot(v2,v0);
        final double d21 = dot(v2,v1);
        final double denom = d00 * d11 - d01 * d01;
        final double v = (d11 * d20 - d01 * d21) / denom;
        final double w = (d00 * d21 - d01 * d20) / denom;
        final double u = 1.0f - v - w;
        return new double[]{u, v, w};
    }

    /**
     * Calculate constant D of a plan.
     * Same as normal.dot(point).
     *
     * @param normal plan normal
     * @param pointOnPlan a point in the plan
     * @return plan D constant value
     */
    public static double calculatePlanD(double[] normal, double[] pointOnPlan){
        return Vectors.dot(normal, pointOnPlan);
    }

    /**
     * Calculate projection of a point on a plan.
     *
     * @param point point to project
     * @param planNormal plan normal
     * @param planD plan D constant
     * @return projected point
     */
    public static double[] projectPointOnPlan(double[] point, double[] planNormal, double planD){
        double[] va = Vectors.subtract(point, Vectors.scale(planNormal,planD) );
        double d = Vectors.dot(planNormal, va);
        return Vectors.subtract(va, Vectors.scale(planNormal, d));
    }

    /**
     * Test if given sequence of tuple is in clockwise direction.
     * This method expect the coordinates to be a closed line.
     *
     * @param coordinates line coordinates
     * @return true if clockwise
     */
    public static boolean isClockWise(List<Coordinate> coordinates){
        final double area = calculateArea(coordinates);
        return area > 0;
    }

    /**
     * Test if given sequence of tuple is in clockwise direction.
     * This method expect the coordinates to be a closed line.
     *
     * @param coordinates polygon outer line
     * @return area
     */
    public static double calculateArea(List<Coordinate> coordinates){
        double area = 0;
        final int numPoints = coordinates.size();
        for(int i=0;i<numPoints-1;i++){
            final Coordinate start = coordinates.get(i);
            final Coordinate end = coordinates.get(i+1);
            area += (start.x+end.x) * (start.y-end.y);
        }
        return area/2.0;
    }

    /**
     * Create an Affine that transform the given bbox to be centerd into target bbox.
     * The source bbox is scaled with given scale.
     *
     * @param source
     * @param target
     * @param scale wanted scale.
     * @return
     */
    public static MatrixSIS centeredScaled(GeneralEnvelope source, GeneralEnvelope target, double scale){

        final double[] sourceCenter = source.getMedian().getCoordinates();
        final double[] targetCenter = target.getMedian().getCoordinates();
        Vectors.scale(sourceCenter, scale, sourceCenter);
        final double[] trs = Vectors.subtract(targetCenter, sourceCenter);

        final MatrixSIS mt = new Matrix4();
        for (int i=0;i<trs.length;i++){
            mt.setElement(i, i, scale);
            mt.setElement(i, trs.length, trs[i]);
        }

        return mt;
    }

    /**
     * Create an Affine that transform the given bbox to fit into target bbox.
     * Dimensions ratio are preserved and will be centered in target bbox.
     *
     * @param source
     * @param target
     * @return
     */
    public static MatrixSIS scaled(GeneralEnvelope source, GeneralEnvelope target){
        //find min scale
        final int dim = source.getDimension();
        double scale = target.getSpan(0) / source.getSpan(0);
        for (int i=1;i<dim;i++){
            scale = Math.min( target.getSpan(i) / source.getSpan(i), scale);
        }
        return centeredScaled(source, target, scale);
    }


    /**
     * JavaScript can not read binary data such as float if they are not byte aligned.
     * Float require 4 bytes alignment and Double 8.
     * In the B3DM specification the gltf must be 8 bytes aligned.
     *
     * @param data source array
     * @param isJson is true padding will be spaces
     * @param previousDataLength length of data before data array which must be included in padding
     * @param padding wanted padding
     *
     * @return padded byte array
     */
    public static byte[] pad(byte[] data, boolean isJson, int previousDataLength, int padding) {
        if (data==null) return null;

        final int remaining = (previousDataLength+data.length) % padding;
        if (remaining == 0) return data;

        final byte[] array = new byte[data.length + (padding-remaining)];
        Arrays.fill(array, isJson ? (byte)' ' : 0x00);
        System.arraycopy(data, 0, array, 0, data.length);
        return array;
    }

}
