package org.geotoolkit.geometry.math;


/**
 * Origin : Adapted from Unlicense-Lib
 *
 * @author Johann Sorel
 * @author Bertrand COTE
 */
public final class Vectors {

    private Vectors(){}

    public static Tuple createInt(int dimension) {
        switch (dimension) {
            case 1 : return new Tuple1i();
            case 2 : return new Tuple2i();
            case 3 : return new Tuple3i();
            default: throw new IllegalArgumentException("Unsupported vector size "+dimension);
        }
    }

    public static Vector createFloat(int dimension) {
        switch (dimension) {
            case 1 : return new Vector1f();
            case 2 : return new Vector2f();
            case 3 : return new Vector3f();
            default: throw new IllegalArgumentException("Unsupported vector size "+dimension);
        }
    }

    public static Vector createDouble(int dimension) {
        switch (dimension) {
            case 1 : return new Vector1d();
            case 2 : return new Vector2d();
            case 3 : return new Vector3d();
            default: throw new IllegalArgumentException("Unsupported vector size "+dimension);
        }
    }


    /**
     * Computes the vector's length.
     *
     * @param vector input vector
     * @return length
     */
    public static double length(final double[] vector){
        return Math.sqrt(lengthSquare(vector));
    }

    /**
     * Computes the vector's length.
     *
     * @param vector vector to process
     * @return vector length
     */
    public static float length(final float[] vector){
        return (float)Math.sqrt(lengthSquare(vector));
    }

    /**
     * Computes the vector's square length.
     *
     * @param vector input vector
     * @return vector square length
     */
    public static double lengthSquare(final double[] vector){
        double length = 0;
        for(int i=0;i<vector.length;i++){
            length += vector[i]*vector[i];
        }
        return length;
    }

    /**
     * Computes the vector's square length.
     *
     * @param vector input vector
     * @return vector square length
     */
    public static float lengthSquare(final float[] vector){
        float length = 0;
        for(int i=0;i<vector.length;i++){
            length += vector[i]*vector[i];
        }
        return length;
    }

    /**
     * Computes the shortest angle between given vectors.
     * formula : acos(dot(vector,other)/(length(vector)*length(other)))
     *
     * @param vector input vector
     * @param other second vector
     * @return shortest angle in radian
     */
    public static double shortestAngle(final double[] vector, final double[] other){
        return Math.acos(cos( vector, other ));
    }

    /**
     * Computes the shortest angle between given vectors.
     * formula : acos(dot(vector,other)/(length(vector)*length(other)))
     *
     * @param vector input vector
     * @param other second vector
     * @return shortest angle in radian
     */
    public static float shortestAngle(final float[] vector, final float[] other){
        return (float)Math.acos(cos( vector, other ));
    }


    /**
     * Cosine of the angle between the two vectors.
     * formula : dot(vector,other)
     *
     * @param vector input vector
     * @param other second vector
     * @return angle cosinus value
     */
    public static double cos( double[] vector, double[] other ) {
        return dot(vector, other)/(length(vector)*length(other));
    }

    /**
     * Cosine of the angle between the two vectors.
     * formula : dot(vector,other)
     *
     * @param vector input vector
     * @param other second vector
     * @return angle cosinus value
     */
    public static float cos( float[] vector, float[] other ) {
        return dot(vector, other)/(length(vector)*length(other));
    }

    /**
     * Sinus of the angle between the two vectors.
     * The returned value is signed for 2D vectors, and unsigned for 3D vectors.
     *
     * @param vector input vector
     * @param other second vector
     * @return angle sinus value
     */
    public static double sin( double[] vector, double[] other ) {
        if( vector.length == 2 ) {
            return (vector[0]*other[1]-vector[1]*other[0])/(length(vector)*length(other));
        } else if ( vector.length == 3 ) {
            return length(cross(vector, other))/(length(vector)*length(other));
        }
        throw new IllegalArgumentException(" Vector size must be 2 or 3");
    }

    /**
     * Sinus of the angle between the two vectors.
     * The returned value is signed for 2D vectors, and unsigned for 3D vectors.
     *
     * @param vector input vector
     * @param other second vector
     * @return angle sinus value
     */
    public static float sin( float[] vector, float[] other ) {
        if ( vector.length == 2 ) {
            return (vector[0]*other[1]-vector[1]*other[0])/(length(vector)*length(other));
        } else if ( vector.length == 3 ) {
            return length(cross(vector, other))/(length(vector)*length(other));
        }
        throw new IllegalArgumentException(" Vector size must be 2 or 3");
    }

    ////////////////////////////////////////////////////////////////////////////
    // OPERATIONS WITHOuT BUFFER ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Copy vector into another one.
     * @param target
     * @param src
     */
    public static void copy(double[] target, double[] src) {
        if (src.length<target.length) {
            throw new IllegalArgumentException(" Source vector size must be equal or greater than target vector");
        }
        System.arraycopy(src, 0, target, 0, target.length);
    }

    /**
     * Add vector and other.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return addition of both vectors
     */
    public static double[] add(final double[] vector, final double[] other){
        return add(vector,other,null);
    }

    /**
     * Add vector and other.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return addition of both vectors
     */
    public static float[] add(final float[] vector, final float[] other){
        return add(vector,other,null);
    }

    /**
     * Subtract vector and other.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return subtraction of both vectors
     */
    public static double[] subtract(final double[] vector, final double[] other){
        return subtract(vector,other,null);
    }

    /**
     * Subtract vector and other.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return subtraction of both vectors
     */
    public static float[] subtract(final float[] vector, final float[] other){
        return subtract(vector,other,null);
    }

    /**
     * Multiply vector and other.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return multiplication of both vectors
     */
    public static double[] multiply(final double[] vector, final double[] other){
        return multiply(vector,other,null);
    }

    /**
     * Multiply vector and other.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return multiplication of both vectors
     */
    public static float[] multiply(final float[] vector, final float[] other){
        return multiply(vector,other,null);
    }

    /**
     * Divide vector and other.
     * Vectors must have the same size.
     *
     * @param vector numerators vector.
     * @param other denominators vector.
     * @return division of both vectors
     */
    public static double[] divide(final double[] vector, final double[] other){
        return divide(vector,other,null);
    }

    /**
     * Divide vector and other.
     * Vectors must have the same size.
     *
     * @param vector numerators vector.
     * @param other denominators vector.
     * @return division of both vectors
     */
    public static float[] divide(final float[] vector, final float[] other){
        return divide(vector,other,null);
    }

    /**
     * Scale vector by the given scale.
     *
     * @param vector the vector to scale.
     * @param scale the scale coefficient.
     * @return the scaled vector.
     */
    public static double[] scale(final double[] vector, final double scale){
        return scale(vector,scale,null);
    }

    /**
     * Scale vector by the given scale.
     *
     * @param vector the vector to scale.
     * @param scale the scale coefficient.
     * @return the scaled vector.
     */
    public static float[] scale(final float[] vector, final float scale){
        return scale(vector,scale,null);
    }

    /**
     * Cross product of v1 and v2.
     * Vectors must have size 3.
     *
     * @param vector first vector
     * @param other second vector
     * @return the cross product of vector by other.
     */
    public static double[] cross(final double[] vector, final double[] other){
        return cross(vector,other,null);
    }

    /**
     * Cross product of v1 and v2.
     * Vectors must have size 3.
     *
     * @param vector first vector
     * @param other second vector
     * @return the cross product of vector by other.
     */
    public static float[] cross(final float[] vector, final float[] other){
        return cross(vector,other,null);
    }

    /**
     * Dot product of vector and other.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return dot product
     */
    public static double dot(final double[] vector, final double[] other){
        double dot = 0;
        for(int i=0;i<vector.length;i++){
            dot += vector[i]*other[i];
        }
        return dot;
    }

    /**
     * Dot product of vector and other.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return dot product
     */
    public static float dot(final float[] vector, final float[] other){
        float dot = 0;
        for(int i=0;i<vector.length;i++){
            dot += vector[i]*other[i];
        }
        return dot;
    }

    /**
     * Normalizes vector.
     * Vectors must have the same size.
     *
     * @param vector the vector to normalize.
     * @return the normalized vector.
     */
    public static double[] normalize(final double[] vector){
        return normalize(vector, null);
    }

    /**
     * Normalizes vector.
     * Vectors must have the same size.
     *
     * @param vector the vector to normalize.
     * @return the normalized vector.
     */
    public static float[] normalize(final float[] vector){
        return normalize(vector, null);
    }

    /**
     * Negates the vector, equivalent to multiply all values by -1.
     *
     * @param vector the vector to negate.
     * @return the negated vector.
     */
    public static double[] negate(final double[] vector){
        return negate(vector, null);
    }

    /**
     * Negates the vector, equivalent to multiply all values by -1.
     *
     * @param vector the vector to negate.
     * @return the negated vector.
     */
    public static float[] negate(final float[] vector){
        return negate(vector, null);
    }

    /**
     * Interpolates between given vectors.
     *
     * @param start start vector (return value for ratio == 0.)
     * @param end end vector (return value for ratio == 1.)
     * @param ratio : 0 is close to start vector, 1 is on end vector
     * @return the interpolated vector.
     */
    public static float[] lerp(final float[] start, final float[] end, final float ratio) {
        return lerp(start, end, ratio, null);
    }

    /**
     * Interpolates between given vectors.
     *
     * @param start start vector (return value for ratio == 0.)
     * @param end end vector (return value for ratio == 1.)
     * @param ratio : 0 is close to start vector, 1 is on end vector
     * @return the interpolated vector.
     */
    public static double[] lerp(final double[] start, final double[] end, final double ratio) {
        return lerp(start, end, ratio, null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // OPERATIONS WITH BUFFER //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Add vector and other, result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @param buffer must have same size as vector or be null.
     * @return addition of both vectors, buffer if not null
     */
    public static double[] add(final double[] vector, final double[] other, double[] buffer) {
        if( vector.length != other.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new double[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]+other[i];
        }
        return buffer;
    }

    /**
     * Add vector and other, result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @param buffer must have same size as vector or be null.
     * @return addition of both vectors, buffer if not null
     */
    public static float[] add(final float[] vector, final float[] other, float[] buffer) {
        if( vector.length != other.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new float[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]+other[i];
        }
        return buffer;
    }

    /**
     * Subtract vector and other, result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @return substraction of both vectors, buffer if not null
     */
    public static double[] subtract(final double[] vector, final double[] other, double[] buffer){
        if( vector.length != other.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new double[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]-other[i];
        }
        return buffer;
    }

    /**
     * Subtract vector and other, result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @param buffer must have same size as vector or be null.
     * @return substraction of both vectors, buffer if not null
     */
    public static float[] subtract(final float[] vector, final float[] other, float[] buffer){
        if( vector.length != other.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new float[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]-other[i];
        }
        return buffer;
    }

    /**
     * Multiply vector and other, result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @param buffer must have same size as vector or be null.
     * @return multiplication of both vectors, buffer if not null
     */
    public static double[] multiply(final double[] vector, final double[] other, double[] buffer){
        if( vector.length != other.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new double[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]*other[i];
        }
        return buffer;
    }

    /**
     * Multiply vector and other, result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have the same size.
     *
     * @param vector first vector
     * @param other second vector
     * @param buffer must have same size as vector or be null.
     * @return multiplication of both vectors, buffer if not null
     */
    public static float[] multiply(final float[] vector, final float[] other, float[] buffer){
        if( vector.length != other.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new float[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]*other[i];
        }
        return buffer;
    }

    /**
     * Divide vector and other, result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have the same size.
     *
     * @param vector numerators vector.
     * @param other denominators vector.
     * @param buffer must have same size as vector or be null.
     * @return division of both vectors, buffer if not null
     */
    public static double[] divide(final double[] vector, final double[] other, double[] buffer){
        if( vector.length != other.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new double[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]/other[i];
        }
        return buffer;
    }

    /**
     * Divide vector and other, result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have the same size.
     *
     * @param vector numerators vector.
     * @param other denominators vector.
     * @param buffer must have same size as vector or be null.
     * @return division of both vectors, buffer if not null
     */
    public static float[] divide(final float[] vector, final float[] other, float[] buffer){
        if( vector.length != other.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new float[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]/other[i];
        }
        return buffer;
    }

    /**
     * Scale vector by the given scale, result is stored in buffer.
     * If buffer is null, a new vector is created.
     *
     * @param vector the vector to scale.
     * @param scale the scale coefficient.
     * @param buffer must have same size as vector or be null.
     * @return the scaled vector.
     */
    public static double[] scale(final double[] vector, final double scale, double[] buffer){
        if( buffer == null ){
            buffer = new double[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]*scale;
        }
        return buffer;
    }

    /**
     * Scale vector by the given scale, result is stored in buffer.
     * If buffer is null, a new vector is created.
     *
     * @param vector the vector to scale.
     * @param scale the scale coefficient.
     * @param buffer must have same size as vector or be null.
     * @return the scaled vector.
     */
    public static float[] scale(final float[] vector, final float scale, float[] buffer){
        if( buffer == null ){
            buffer = new float[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for(int i=0;i<vector.length;i++){
            buffer[i] = vector[i]*scale;
        }
        return buffer;
    }

    /**
     * Cross product of v1 and v2. result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have size 3.
     * @param vector first vector
     * @param other second vector
     * @param buffer must have same size as vector and other or be null.
     * @return the cross product of vector by other.
     */
    public static double[] cross(final double[] vector, final double[] other, double[] buffer){
        if( vector.length!=3 || other.length!=3 ) {
            throw new IllegalArgumentException("vector and v2 size must be 3.");
        }
        if( buffer == null ){
            buffer = new double[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector and other.");
        }

        final double newX = (vector[1] * other[2]) - (vector[2] * other[1]);
        final double newY = (vector[2] * other[0]) - (vector[0] * other[2]);
        final double newZ = (vector[0] * other[1]) - (vector[1] * other[0]);
        buffer[0] = newX;
        buffer[1] = newY;
        buffer[2] = newZ;
        return buffer;
    }

    /**
     * Cross product of v1 and v2. result is stored in buffer.
     * If buffer is null, a new vector is created.
     * Vectors must have size 3.
     * @param vector first vector
     * @param other second vector
     * @param buffer must have same size as vector and other or be null.
     * @return the cross product of vector by other.
     */
    public static float[] cross(final float[] vector, final float[] other, float[] buffer){
        if( vector.length!=3 || other.length!=3 ) {
            throw new IllegalArgumentException("vector and other size must be 3.");
        }
        if( buffer == null ){
            buffer = new float[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector and other.");
        }

        buffer[0] = (vector[1] * other[2]) - (vector[2] * other[1]); // new X
        buffer[1] = (vector[2] * other[0]) - (vector[0] * other[2]); // new Y
        buffer[2] = (vector[0] * other[1]) - (vector[1] * other[0]); // new Z
        return buffer;
    }

    /**
     * Normalizes vector, result is stored in buffer.
     *
     * If buffer is null, a new vector is created.
     * Vectors must have the same size
     * @param vector the vector to normalize.
     * @param buffer must have same size as vector or be null.
     * @return the normalized vector.
     */
    public static double[] normalize(final double[] vector, double[] buffer){
        if( buffer == null ){
            buffer = new double[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        scale(vector, 1d/length(vector), buffer);
        return buffer;
    }

    /**
     * Normalizes vector, result is stored in buffer.
     *
     * If buffer is null, a new vector is created.
     * Vectors must have the same size
     * @param vector the vector to normalize.
     * @param buffer must have same size as vector or be null.
     * @return the normalized vector.
     */
    public static float[] normalize(final float[] vector, float[] buffer){
        if( buffer == null ){
            buffer = new float[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        scale(vector, 1f/length(vector), buffer);
        return buffer;
    }

    /**
     * Negates the vector, equivalent to multiply all values by -1.
     *
     * @param vector the vector to negate.
     * @param buffer must have same size as vector or be null.
     * @return the negated vector.
     */
    public static double[] negate(final double[] vector, double[] buffer){
        if( buffer == null ){
            buffer = new double[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for( int i=0; i<vector.length; i++ ) {
            buffer[i] = - vector[i];
        }
        return buffer;
    }

    /**
     * Negates the vector, equivalent to multiply all values by -1.
     *
     * @param vector the vector to negate.
     * @param buffer must have same size as vector or be null.
     * @return the negated vector.
     */
    public static float[] negate(final float[] vector, float[] buffer){
        if( buffer == null ){
            buffer = new float[vector.length];
        } else if( vector.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as vector.");
        }

        for( int i=0; i<vector.length; i++ ) {
            buffer[i] = - vector[i];
        }
        return buffer;
    }


    /**
     * Interpolates between given vectors.
     *
     * @param start start vector (return value for ratio == 0.)
     * @param end end vector (return value for ratio == 1.)
     * @param ratio : 0 is close to start vector, 1 is on end vector
     * @param buffer must have same size as start and end or be null.
     * @return the interpolated vector.
     */
    public static float[] lerp(final float[] start, final float[] end, final float ratio, float[] buffer) {
        if( start.length != end.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new float[start.length];
        } else if( start.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as start and end vectors.");
        }

        for(int i=0;i<start.length;i++){
            buffer[i] = (1-ratio)*start[i] + ratio * end[i];
        }
        return buffer;
    }

    public static double[] lerp(final double[] start, final double[] end, final double ratio, double[] buffer) {
        if( start.length != end.length ) {
            throw new IllegalArgumentException("Both vectors must have same length.");
        }
        if( buffer == null ){
            buffer = new double[start.length];
        } else if( start.length != buffer.length ) {
                throw new IllegalArgumentException("Buffer must have same length as start and end vectors.");
        }

        for(int i=0;i<start.length;i++){
            buffer[i] = (1-ratio)*start[i] + ratio * end[i];
        }
        return buffer;
    }

    ////////////////////////////////////////////////////////////////////////////
    // OPERATIONS WITH MULTIPLE ELEMENTS AT THE SAME TIME //////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Add source1 and source2, result is stored in buffer.
     * @param source1 first vectors array
     * @param source2 second vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param source2Offset second array offset
     * @param bufferOffset output buffer offset
     * @param tupleSize tuples size
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static double[] add(double[] source1, double[] source2, double[] buffer,
            int source1Offset, int source2Offset, int bufferOffset, int tupleSize, int nbTuple){
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] + source2[source2Offset+i];
        }
        return buffer;
    }

    /**
     * Add source1 and source2, result is stored in buffer.
     * @param source1 first vectors array
     * @param source2 second vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param source2Offset second array offset
     * @param bufferOffset output buffer offset
     * @param tupleSize tuples size
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static float[] add(float[] source1, float[] source2, float[] buffer,
            int source1Offset, int source2Offset, int bufferOffset, int tupleSize, int nbTuple){
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] + source2[source2Offset+i];
        }
        return buffer;
    }

    /**
     * Add 'addition' to all source1 elements, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param addition vector to add
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static double[] addRegular(double[] source1, double[] buffer,
            int source1Offset, int bufferOffset, double[] addition, int nbTuple){
        final int tupleSize = addition.length;
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] + addition[i%tupleSize];
        }
        return buffer;
    }

    /**
     * Add 'addition' to all source1 elements, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param addition vector to add
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static float[] addRegular(float[] source1, float[] buffer,
            int source1Offset, int bufferOffset, float[] addition, int nbTuple){
        final int tupleSize = addition.length;
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] + addition[i%tupleSize];
        }
        return buffer;
    }

    /**
     * Substract source1 and source2, result is stored in buffer.
     * @param source1 first vectors array
     * @param source2 second vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param source2Offset second array offset
     * @param bufferOffset output buffer offset
     * @param tupleSize tuples size
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static double[] subtract(double[] source1, double[] source2, double[] buffer,
            int source1Offset, int source2Offset, int bufferOffset, int tupleSize, int nbTuple){
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] - source2[source2Offset+i];
        }
        return buffer;
    }

    /**
     * Substract source1 and source2, result is stored in buffer.
     * @param source1 first vectors array
     * @param source2 second vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param source2Offset second array offset
     * @param bufferOffset output buffer offset
     * @param tupleSize tuples size
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static float[] subtract(float[] source1, float[] source2, float[] buffer,
            int source1Offset, int source2Offset, int bufferOffset, int tupleSize, int nbTuple){
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] - source2[source2Offset+i];
        }
        return buffer;
    }

    /**
     * Substract 'substraction' to all source1 elements, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param substraction vector to subtract
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static double[] subtractRegular(double[] source1, double[] buffer,
            int source1Offset, int bufferOffset, double[] substraction, int nbTuple){
        final int tupleSize = substraction.length;
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] - substraction[i%tupleSize];
        }
        return buffer;
    }

    /**
     * Substract 'substraction' to all source1 elements, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param substraction vector to subtract
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static float[] subtractRegular(float[] source1, float[] buffer,
            int source1Offset, int bufferOffset, float[] substraction, int nbTuple){
        final int tupleSize = substraction.length;
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] - substraction[i%tupleSize];
        }
        return buffer;
    }

    /**
     * Scale source1, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param scale sclaing factor
     * @param tupleSize tuples size
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static double[] scaleRegular(double[] source1, double[] buffer, int source1Offset,
            int bufferOffset, double scale, int tupleSize, int nbTuple){
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] * scale;
        }
        return buffer;
    }

    /**
     * Scale source1, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param scale scaling factor
     * @param tupleSize tuples size
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static float[] scaleRegular(float[] source1, float[] buffer, int source1Offset,
            int bufferOffset, float scale, int tupleSize, int nbTuple){
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] * scale;
        }
        return buffer;
    }

    /**
     * Scale source1, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param scale scaling factor
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static double[] multiplyRegular(double[] source1, double[] buffer, int source1Offset,
            int bufferOffset, double[] scale, int nbTuple){
        final int tupleSize = scale.length;
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] * scale[i%tupleSize];
        }
        return buffer;
    }

    /**
     * Scale source1, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param scale scaling factor
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static float[] multiplyRegular(float[] source1, float[] buffer, int source1Offset,
            int bufferOffset, float[] scale, int nbTuple){
        final int tupleSize = scale.length;
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] * scale[i%tupleSize];
        }
        return buffer;
    }


    /**
     * Divide source1, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param scale scaling factor
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static double[] divideRegular(double[] source1, double[] buffer, int source1Offset,
            int bufferOffset, double[] scale, int nbTuple){
        final int tupleSize = scale.length;
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] / scale[i%tupleSize];
        }
        return buffer;
    }

    /**
     * Divide source1, result is stored in buffer.
     * @param source1 first vectors array
     * @param buffer result buffer, not null
     * @param source1Offset first array offset
     * @param bufferOffset output buffer offset
     * @param scale scaling factor
     * @param nbTuple number of tuple to process
     * @return buffer result array, same as buffer parameter
     */
    public static float[] divideRegular(float[] source1, float[] buffer, int source1Offset,
            int bufferOffset, float[] scale, int nbTuple){
        final int tupleSize = scale.length;
        for(int i=0,n=nbTuple*tupleSize;i<n;i++){
            buffer[bufferOffset+i] = source1[source1Offset+i] / scale[i%tupleSize];
        }
        return buffer;
    }


    // =========================================================================

    /**
     * From cartesian to polar coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#To_polar_coordinates_from_Cartesian_coordinates
     * <ul>
     * <li>r: cartesian's norm.</li>
     * <li>theta: angle between x and cartesian = atan2(y , x)</li>
     * </ul>
     *
     * @param cartesian { x, y }
     * @return { r, theta }
     */
    public static double[] cartesianToPolar( double[] cartesian ) {
        if( cartesian.length != 2 ) throw new IllegalArgumentException("cartesian.length must be 2({ x, y }).");
        final double r = Vectors.length( cartesian );
        final double theta = Math.atan2(cartesian[1] , cartesian[0]);
        return new double[]{ r, theta };
    }

    /**
     * From cartesian to polar coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#To_polar_coordinates_from_Cartesian_coordinates
     * <ul>
     * <li>r: cartesian's norm.</li>
     * <li>theta: angle between x and cartesian = atan2(y , x)</li>
     * </ul>
     *
     * @param cartesian { x, y }
     * @return { r, theta }
     */
    public static float[] cartesianToPolar( float[] cartesian ) {
        if( cartesian.length != 2 ) throw new IllegalArgumentException("cartesian.length must be 2({ x, y }).");
        final float r = Vectors.length( cartesian );
        final float theta = (float)Math.atan2(cartesian[1] , cartesian[0]);
        return new float[]{ r, theta };
    }

    /**
     * From polar to cartesian coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#To_Cartesian_coordinates_from_polar_coordinates
     * <ul>
     * <li>x: r*cos(theta).</li>
     * <li>y: r*sin(theta).</li>
     * </ul>
     *
     * @param polar { r, theta }
     * @return { x, y }
     */
    public static double[] polarToCartesian( double[] polar ) {
        if( polar.length != 2 ) throw new IllegalArgumentException("polar.length must be 2({ r, theta }).");
        final double x = polar[0]*Math.cos(polar[1]);
        final double y = polar[0]*Math.sin(polar[1]);
        return new double[]{ x, y };
    }

    /**
     * From polar to cartesian coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#To_Cartesian_coordinates_from_polar_coordinates
     * <ul>
     * <li>x: r*cos(theta).</li>
     * <li>y: r*sin(theta).</li>
     * </ul>
     *
     * @param polar { r, theta }
     * @return { x, y }
     */
    public static float[] polarToCartesian( float[] polar ) {
        if( polar.length != 2 ) throw new IllegalArgumentException("polar.length must be 2({ r, theta }).");
        final float x = polar[0]*(float)Math.cos(polar[1]);
        final float y = polar[0]*(float)Math.sin(polar[1]);
        return new float[]{ x, y };
    }

    /**
     * From cartesian to cylindrical coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#From_Cartesian_Coordinates_2
     * <ul>
     * <li>r: { x, y }'s norm.</li>
     * <li>theta: angle between x and { x, y, 0 }.</li>
     * <li>z: z (same as in cartesian).</li>
     * </ul>
     *
     * @param cartesian { x, y, z }
     * @return { r, theta, z }
     */
    public static double[] cartesianToCylindrical( double[] cartesian ) {
        if ( cartesian.length != 3 ) throw new IllegalArgumentException("cartesian.length must be 3({ x, y, z }).");
        final double[] polar = cartesianToPolar( new double[] {cartesian[0], cartesian[1]} );
        final double r = polar[0];
        final double theta = polar[1];
        final double h = cartesian[2];
        return new double[]{ r, theta, h };
    }

    /**
     * From cartesian to cylindrical coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#From_Cartesian_Coordinates_2
     * <ul>
     * <li>r: { x, y }'s norm.</li>
     * <li>theta: angle between x and { x, y, 0 }.</li>
     * <li>z: z (same as in cartesian).</li>
     * </ul>
     *
     * @param cartesian { x, y, z }
     * @return { r, theta, z }
     */
    public static float[] cartesianToCylindrical( float[] cartesian ) {
        if ( cartesian.length != 3 ) throw new IllegalArgumentException("cartesian.length must be 3({ x, y, z }).");
        final float[] polar = cartesianToPolar( new float[] {cartesian[0], cartesian[1]} );
        final float r = polar[0];
        final float theta = polar[1];
        final float h = cartesian[2];
        return new float[]{ r, theta, h };
    }

    /**
     * From cartesian to cylindrical coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#From_Cartesian_Coordinates_2
     * <ul>
     * <li>x: r*cos(theta).</li>
     * <li>y: r*sin(theta).</li>
     * <li>z: z (same as in cylindrical).</li>
     * </ul>
     *
     * @param cylindrical { r, theta, z }
     * @return { x, y, z }
     */
    public static double[] cylindricalToCartesian( double[] cylindrical ) {
        if ( cylindrical.length != 3 ) throw new IllegalArgumentException("cartesian.length must be 3({ r, theta, z }).");
        final double x = cylindrical[0]*Math.cos(cylindrical[1]);
        final double y = cylindrical[0]*Math.sin(cylindrical[1]);
        final double z = cylindrical[2];
        return new double[]{ x, y, z };
    }

     /**
     * From cartesian to cylindrical coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#From_Cartesian_Coordinates_2
     * <ul>
     * <li>x: r*cos(theta).</li>
     * <li>y: r*sin(theta).</li>
     * <li>z: z (same as in cylindrical).</li>
     * </ul>
     *
     * @param cylindrical { r, theta, z }
     * @return { x, y, z }
     */
    public static float[] cylindricalToCartesian( float[] cylindrical ) {
        if ( cylindrical.length != 3 ) throw new IllegalArgumentException("cartesian.length must be 3({ r, theta, z }).");
        final float x = cylindrical[0]*(float)Math.cos(cylindrical[1]);
        final float y = cylindrical[0]*(float)Math.sin(cylindrical[1]);
        final float z = cylindrical[2];
        return new float[]{ x, y, z };
    }

    // =========================================================================

    /**
     * From cartesian to spherical coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#From_Cartesian_coordinates
     * <ul>
     * <li>rho: cartesian's norm.</li>
     * <li>theta: angle between z and cartesian.</li>
     * <li>phi: angle between plan x.z and plan cartesian.z</li>
     * </ul>
     *
     * @param cartesian { x, y, z }
     * @return { rho, theta, phi }
     */
    public static double[] cartesianToSpherical( double[] cartesian ) {
        if( cartesian.length != 3 ) throw new IllegalArgumentException("cartesian.length must 3({ x, y, z }).");
        final double rho = Vectors.length(cartesian);
        final double theta = Math.atan2( cartesian[1], cartesian[0] );
        final double phi = Math.atan2( cartesian[2], Math.sqrt(cartesian[0]*cartesian[0] + cartesian[1]*cartesian[1]) );
        return new double[]{ rho, theta, phi };
    }

    /**
     * From cartesian to spherical coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#From_Cartesian_coordinates
     * <ul>
     * <li>rho: cartesian's norm.</li>
     * <li>theta: angle between z and cartesian.</li>
     * <li>phi: angle between plan x.z and plan cartesian.z</li>
     * </ul>
     *
     * @param cartesian { x, y, z }
     * @return { rho, theta, phi }
     */
    public static float[] cartesianToSpherical( float[] cartesian ) {
        if( cartesian.length != 3 ) throw new IllegalArgumentException("cartesian.length must 3({ x, y, z }).");
        final float rho = Vectors.length(cartesian);
        final float theta = (float)Math.atan2( cartesian[1], cartesian[0] );
        final float phi = (float)Math.atan2( cartesian[2], Math.sqrt(cartesian[0]*cartesian[0] + cartesian[1]*cartesian[1]) );
        return new float[]{ rho, theta, phi };
    }

    /**
     * From spherical to cartesian coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#From_spherical_coordinates
     * <ul>
     * <li>x: rho*cos(phi)*cos(theta).</li>
     * <li>y: rho*cos(phi)*sin(theta).</li>
     * <li>z: rho*sin(phi).</li>
     * </ul>
     *
     * @param spherical { rho, theta, phi }
     * @return { x, y, z }
     */
    public static double[] sphericalToCartesian( double[] spherical ) {
        if( spherical.length != 3 ) throw new IllegalArgumentException("cartesian.length must 3({ rho, theta, phi }).");
        final double rhoCosPhi = spherical[0]*Math.cos(spherical[2]);
        final double x = rhoCosPhi*Math.cos(spherical[1]);
        final double y = rhoCosPhi*Math.sin(spherical[1]);
        final double z = spherical[0]*Math.sin(spherical[2]);
        return new double[]{ x, y, z };
    }

    /**
     * From spherical to cartesian coordinate system transformation.
     * http://en.wikipedia.org/wiki/List_of_common_coordinate_transformations#From_spherical_coordinates
     * <ul>
     * <li>x: rho*cos(phi)*cos(theta).</li>
     * <li>y: rho*cos(phi)*sin(theta).</li>
     * <li>z: rho*sin(phi).</li>
     * </ul>
     *
     * @param spherical { rho, theta, phi }
     * @return { x, y, z }
     */
    public static float[] sphericalToCartesian( float[] spherical ) {
        if( spherical.length != 3 ) throw new IllegalArgumentException("cartesian.length must 3({ rho, theta, phi }).");
        final float rhoCosPhi = spherical[0]*(float)Math.cos(spherical[2]);
        final float x = rhoCosPhi*(float)Math.cos(spherical[1]);
        final float y = rhoCosPhi*(float)Math.sin(spherical[1]);
        final float z = spherical[0]*(float)Math.sin(spherical[2]);
        return new float[]{ x, y, z };
    }
}
