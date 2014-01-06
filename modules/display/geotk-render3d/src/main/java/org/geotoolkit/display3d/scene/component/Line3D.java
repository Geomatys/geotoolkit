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
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;
import java.util.List;
import org.geotoolkit.display3d.Map3D;

/**
 * @author Thomas Rouby (Geomatys)
 */
public class Line3D extends ColoredObject3D {
    private boolean closed  = false;

    public Line3D(Map3D map, List<? extends Object> points, float[] color){
        this(map,points);
        this.setColor(color);
    }

    public Line3D(Map3D map, List<? extends Object> points){
        super(map);
        this.setVertices(points);
    }

    public Line3D(Line3D orig){
        super(orig);
        this.closed = orig.closed;
    }

    private void createIndices(){
        final int indicesLength = this.getNumVertex();
        if (this.getNumIndices() != indicesLength){
            final int[] indices = new int[indicesLength];
            for (int i=0; i<indicesLength; i++){
                indices[i] = i;
            }
            this.indicesb = Buffers.newDirectIntBuffer(indices);
        }
    }

    @Override
    public void setVertices(float[] vertices) {
        super.setVertices(vertices);
        createIndices();
    }

    @Override
    public void setVertices(List<? extends Object> points) {
        super.setVertices(points);
        createIndices();
    }

    @Override
    public void setVertex(Object _point) {
        super.setVertex(_point);
        createIndices();
    }

    @Override
    protected void drawInternal(GLAutoDrawable glDrawable) throws GLException {

        final GL gl = glDrawable.getGL();
        if (gl.isGL2()) {

            final GL2 gl2 = gl.getGL2();

            if (this.getNumIndices() == this.getNumVertex() && this.getNumVertex() > 0) {
                gl2.glDisable(GL.GL_TEXTURE_2D);
                gl2.glEnable(GL.GL_BLEND);
                gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
                gl2.glColor4f(this.getColor()[0], this.getColor()[1], this.getColor()[2], this.getColor()[3]);

                gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
                gl2.glVertexPointer(3, GL.GL_FLOAT, 0, this.verticesb.rewind());
                if (closed) {
                    gl2.glDrawElements(GL2.GL_LINE_LOOP, this.getNumIndices(), GL2.GL_UNSIGNED_INT, this.indicesb.rewind());
                } else {
                    gl2.glDrawElements(GL2.GL_LINE_STRIP, this.getNumIndices(), GL2.GL_UNSIGNED_INT, this.indicesb.rewind());
                }

                gl2.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                gl2.glDisable(GL.GL_BLEND);
                gl2.glEnable(GL.GL_TEXTURE_2D);
            }

        }
    }
}
