/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display3d.scene.component;

import com.jogamp.common.nio.Buffers;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.scene.SceneNode3D;

/**
 * An abstract object to 3D view
 * All object to draw in a scene is an extension of Object3D
 *
 * @author Thomas Rouby (Geomatys)
 */
public abstract class Object3D extends SceneNode3D {

    /**
     * The vertices array store, for n point p0,p1, ..., pn
     * {p0x, p0y, p0z, p1x, p1y, p1z, ..., pnx, pny, pnz}
     */
//    protected float[] vertices;
    protected FloatBuffer verticesb;

    /**
     * All vertex indices to draw triangles
     * This is call with the rule GL.GL_TRIANGLES
     */
//    protected int[] indices;
    protected IntBuffer indicesb;

//    protected final UpdateManager updateManager = new UpdateManager();

    /**
     * Default constructor
     */
    protected Object3D(Map3D map){
        super(map);
    }

    /**
     * Copy constructor
     * @param orig
     */
    public Object3D(final Object3D orig){
        super(orig.getCanvas());
        this.verticesb = orig.verticesb.duplicate();
        this.indicesb = orig.indicesb.duplicate();
    }

    /**
     * Get the vertices buffer no modified
     *
     * @return the vertices buffer
     */
    public FloatBuffer getVertices() {
        return this.verticesb;
    }

    public float[] getVerticesAsArray(){
        if (this.verticesb.hasArray()){
            return this.verticesb.array();
        } else {
            final float[] vertices = new float[this.verticesb.capacity()];
            this.verticesb.position(0);
            this.verticesb.get(vertices);
            return vertices;
        }
    }

    public void setVertices(float[] vertices) {
        this.verticesb = Buffers.newDirectFloatBuffer(vertices);
    }

    public void setVertices(List<? extends Object> points){
        final int size = points.size();
        final float[] vertices = new float[size*3];

        for (int i=0; i<size; i++){
            final Object candidate = points.get(i);

            if (candidate instanceof Tuple3d){
                final Tuple3d point = (Tuple3d)candidate;
                vertices[i*3    ] = (float)point.x;
                vertices[i*3 + 1] = (float)point.y;
                vertices[i*3 + 2] = (float)point.z;
            } else if (candidate instanceof Tuple3f){
                final Tuple3f point = (Tuple3f)candidate;
                vertices[i*3    ] = point.x;
                vertices[i*3 + 1] = point.y;
                vertices[i*3 + 2] = point.z;
            } else if (candidate instanceof float[]){
                final float[] point = (float[])candidate;
                System.arraycopy(point, 0, vertices, i*3, Math.min(point.length, 3));
            } else if (candidate instanceof double[]){
                final double[] point = (double[])candidate;
                switch(point.length){
                    case 3 : vertices[i*3 + 2] = (float)point[2];
                    case 2 : vertices[i*3 + 1] = (float)point[1];
                    case 1 : vertices[i*3    ] = (float)point[0];
                }
            }
        }
        this.verticesb = Buffers.newDirectFloatBuffer(vertices);
    }

    public void setVertex(Object _point){
        final float[] vertices = new float[3];

        if (_point instanceof Tuple3d){
            final Tuple3d point = (Tuple3d) _point;
            vertices[0] = (float)point.x;
            vertices[1] = (float)point.y;
            vertices[2] = (float)point.z;
        } else if (_point instanceof Tuple3f){
            final Tuple3f point = (Tuple3f) _point;
            vertices[0] = point.x;
            vertices[1] = point.y;
            vertices[2] = point.z;
        } else if (_point instanceof float[]){
            final float[] point = (float[]) _point;
            System.arraycopy(point, 0, vertices, 0, Math.min(point.length, 3));
        } else if (_point instanceof double[]){
            final double[] point = (double[]) _point;
            switch(point.length){
                case 3 : vertices[2] = (float)point[2];
                case 2 : vertices[1] = (float)point[1];
                case 1 : vertices[0] = (float)point[0];
            }
        }

        this.verticesb = Buffers.newDirectFloatBuffer(vertices);
    }

    public int getNumVertex(){
        if (this.verticesb != null) {
            return verticesb.capacity()/3;
        }
        return 0;
    }

    public IntBuffer getIndices() {
        return this.indicesb;
    }

    public int[] getIndicesAsArray(){
        if (this.indicesb.hasArray()){
            return this.indicesb.array();
        } else {
            final int[] indices = new int[this.indicesb.capacity()];
            this.indicesb.position(0);
            this.indicesb.get(indices);
            return indices;
        }
    }

    public int getNumIndices(){
        if (this.indicesb != null) {
            return this.indicesb.capacity();
        }
        return 0;
    }

}
