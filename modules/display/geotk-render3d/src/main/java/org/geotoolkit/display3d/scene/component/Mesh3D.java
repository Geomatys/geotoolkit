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
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;
import javax.media.opengl.GL;
import org.geotoolkit.display3d.Map3D;

/**
 * @author Thomas Rouby (Geomatys)
 */
public class Mesh3D extends TexturedObject3D {

    protected boolean wireframe = false;

    protected Mesh3D(Map3D map){
        super(map);
    }

    public Mesh3D(Map3D map, float[] vertices, int[] indices, float[] uvs) {
        super(map);
        this.verticesb = Buffers.newDirectFloatBuffer(vertices);
        this.indicesb = Buffers.newDirectIntBuffer(indices);
        this.uvsb = Buffers.newDirectFloatBuffer(uvs);
    }

    @Override
    protected void drawInternal(GLAutoDrawable glDrawable) throws GLException {

        final GL gl = glDrawable.getGL();
        if (gl.isGL2()) {

            final GL2 gl2 = gl.getGL2();

            if (this.wireframe){
                gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
            } else {
                gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            }

            for (int i=0; i<MAX_TEXTURE; i++){
                this.checkTexture(glDrawable, i);
            }

            gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
            gl2.glVertexPointer(3, GL.GL_FLOAT, 0, this.verticesb.position(0));
            gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
            gl2.glTexCoordPointer(2, GL.GL_FLOAT, 0, this.uvsb.position(0));
            gl2.glDrawElements(GL2.GL_TRIANGLES, this.getNumIndices(), GL2.GL_UNSIGNED_INT, this.indicesb.position(0));
        }
    }
}
