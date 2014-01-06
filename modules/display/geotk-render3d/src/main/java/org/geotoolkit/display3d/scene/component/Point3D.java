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

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;
import javax.media.opengl.GL;
import org.geotoolkit.display3d.Map3D;

/**
 * @author Thomas Rouby (Geomatys)
 */
public class Point3D extends ColoredObject3D {

    public Point3D(Map3D map, Object point){
        super(map);
        setVertex(point);
    }

    public Point3D(Map3D map, Object point, float[] color){
        this(map,point);
        setColor(color);
    }

    @Override
    protected void drawInternal(GLAutoDrawable glDrawable) throws GLException {
        final GL gl = glDrawable.getGL();
        if (gl.isGL2()) {

            final GL2 gl2 = gl.getGL2();

            gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

            gl2.glEnable(GL2.GL_POINT_SMOOTH);
            gl2.glEnable(GL2.GL_BLEND);

            gl2.glColor4f(this.getColor()[0], this.getColor()[1], this.getColor()[2], this.getColor()[3]);

            gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
            gl2.glVertexPointer(3, GL.GL_FLOAT, 0, this.verticesb.rewind());
            gl2.glDrawElements(GL2.GL_POINTS, this.getNumIndices(), GL2.GL_UNSIGNED_INT, this.indicesb.rewind());

            gl2.glColor4f(1.0f,1.0f,1.0f,1.0f);
        }
    }
}
