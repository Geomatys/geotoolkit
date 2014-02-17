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
package org.geotoolkit.display3d.scene.light;

import com.jogamp.common.nio.Buffers;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.awt.*;
import java.nio.FloatBuffer;

/**
 * @author Thomas Rouby (Geomatys))
 */
public class Material {
    private final int face;
    private final int pname;
    private final FloatBuffer colorb;

    /**
     *
     * @param color
     * @link http://www.opengl.org/sdk/docs/man2/xhtml/glMaterial.xml
     *
     * @param face  Specifies which face or faces are being updated.
     *              Must be one of GL.GL_FRONT, GL.GL_BACK, or GL.GL_FRONT_AND_BACK.
     *
     * @param pname Specifies the material parameter of the face or faces that is being updated.
     *              Must be one GL2.GL_AMBIENT, GL2.GL_DIFFUSE, GL2.GL_SPECULAR, GL2.GL_EMISSION, GL2.GL_SHININESS, GL2.GL_AMBIENT_AND_DIFFUSE, or GL2.GL_COLOR_INDEXES
     */
    public Material(int face, int pname, Color color) {
        this(face, pname,  new float[]{
                (float) color.getRed() / 255.0f,
                (float) color.getGreen() / 255.0f,
                (float) color.getBlue() / 255.0f,
                (float) color.getAlpha() / 255.0f});
    }

    public Material(int face, int pname, float[] color) {
        this.face = face;
        this.pname = pname;

        this.colorb = Buffers.newDirectFloatBuffer(color);
    }

    public void update(GLAutoDrawable glDrawable) {
        final GL gl = glDrawable.getGL();
        if (gl instanceof GL2) {
            final GL2 gl2 = gl.getGL2();

            gl2.glMaterialfv(this.face, this.pname, this.colorb);
        }
    }

}
