/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.display.geom;

import javax.media.opengl.GL;

/**
 *
 * @author axel
 */
public class GLTriangle {

    /**
     *  coordinated Points
     */
    private double[] ordinatesPt;
    /**
     * Define the dimension ordinates
     */
    private int dimension;

    public GLTriangle(double[] ordinate) {

        this.dimension = ordinate.length / 3;
        //dimension can be equal 2 || 3
        assert (3 == dimension || 2 == dimension);
        this.ordinatesPt = new double[ordinate.length];
        this.ordinatesPt = ordinate;
    }

    public void display(GL gl) {
         gl.glColor3f(1.0f,0.0f,0.0f);
    

        // Drawing Using Triangles
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex3d(this.ordinatesPt[0], this.ordinatesPt[1], this.ordinatesPt[2]);   // Top
        gl.glVertex3d(this.ordinatesPt[3], this.ordinatesPt[4], this.ordinatesPt[5]); // Bottom Left
        gl.glVertex3d(this.ordinatesPt[6], this.ordinatesPt[7], this.ordinatesPt[8]);  // Bottom Right
        gl.glEnd();
    }
}
