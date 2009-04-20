/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.display.geom;

import java.awt.Color;
import javax.media.opengl.GL;

/**
 *
 * @author axel
 */
public class GLSegment {

    /**
     *  coordinated Points
     */
    private double[] ordinatesPt;
    /**
     * Define the dimension ordinates
     */
    private int dimension;
    /**
     * Define the GLSegment size
     */
    private float size;

    public GLSegment(double[] ordinate, float size) {

        this.dimension = ordinate.length / 2;
        //dimension can be equal 2 || 3
        assert (3 == dimension || 2 == dimension);
        this.size = size;
        this.ordinatesPt = new double[ordinate.length];
        this.ordinatesPt = ordinate;
    }

    public float getSizeLine() {
        return this.size;
    }

    public void setSizeLine(float size) {
        this.size = size;
    }

    public void display(GL gl) {
        if (this.size > 1) {
        gl.glLineWidth(this.size);
        }
        gl.glBegin(GL.GL_LINES);
            gl.glVertex3d(this.ordinatesPt[0],this.ordinatesPt[1],this.ordinatesPt[2]);
            gl.glVertex3d(this.ordinatesPt[3],this.ordinatesPt[4],this.ordinatesPt[5]);
        gl.glEnd();
    }




    public void display(GL gl,Color tabColor) {
     gl.glColor3f((float)((float)tabColor.getRed()/255.0f),(float)((float)tabColor.getGreen()/255.0f),(float)((float)tabColor.getBlue()/255.0f));
        gl.glBegin(GL.GL_LINES);
            gl.glVertex3d(this.ordinatesPt[0],this.ordinatesPt[1],this.ordinatesPt[2]);
            gl.glVertex3d(this.ordinatesPt[3],this.ordinatesPt[4],this.ordinatesPt[5]);
        gl.glEnd();
    }



    public double getPointStartX() {

        return this.ordinatesPt[0];
    }

    public double getPointStartY() {
        return this.ordinatesPt[1];
    }

    public double getPointStartZ() {
        return this.ordinatesPt[2];
    }

    public double getPointEndX() {

        return this.ordinatesPt[3];
    }

    public double getPointEndY() {
        return this.ordinatesPt[4];
    }

    public double getPointEndZ() {
        return this.ordinatesPt[5];
    }

    public double[] getordinatesPt() {
        return this.ordinatesPt;
    }
}
