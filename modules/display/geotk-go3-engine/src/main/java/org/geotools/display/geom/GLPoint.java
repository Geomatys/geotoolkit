/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.display.geom;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import org.geotoolkit.math.Statistics;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author axel
 */
public class GLPoint {


    /**
     *  coordinated Points
     */
    private double[] ordinatesPt;
    /**
     *  Dimension GL point
     */
    private int dimension;
    /**
     * Define the GLPoint size
     */
    private float size;

  
    /**
     *  PointGL constructor
     *
     *  Stock coords of the geometry in a table
     *
     *  @param geometry
     *	Geometry of object and size for this GLPoint.
     *
     *
     */
    public GLPoint(double[] ordinate, float size) {
            this.dimension = ordinate.length;

        //dimension can be equal 2 || 3
        assert (3 == dimension || 2 == dimension);
       
        this.size = size;
        this.ordinatesPt = new double[ordinate.length];
        this.ordinatesPt = ordinate;
    
    }

    /**
     *
     * @param Use array ordinate for creat a GLPoint with a default size.
     */
    public GLPoint(double[] ordinate) {

        this.dimension = ordinate.length;
        //dimension can be equal 2 || 3
        assert (3 == dimension || 2 == dimension);
        
        this.ordinatesPt = new double[ordinate.length];
        this.ordinatesPt = ordinate;

    }
    /**
     * Creat a GLPoint default with a default size
     */
     public GLPoint() {

        //dimension can be equal 2 || 3
        this.dimension = 3;
        this.ordinatesPt = new double[this.dimension];
        for (int i=0;i<this.dimension;i++)
            this.ordinatesPt[i] = 0.0;

    }





    public float getSizePoint() {
        return this.size;
    }

    public void setSizePoint(float size) {
        this.size = size;
    }

    /**
     *  display the GL_Point
     *
     * @param gl t
     *
     */
    public void display(GL gl) {
        if (this.size > 1) {
            gl.glPointSize(this.size);
        }
        System.out.println("Valeur du GLPoint dans GLPoint Dans GL est "+this.ordinatesPt[0]);
        gl.glBegin(GL.GL_POINTS);
        gl.glVertex3d(this.ordinatesPt[0], this.ordinatesPt[1], this.ordinatesPt[2]);
        gl.glEnd();
    }


   public void display(GL gl,Color tabColor) {
     gl.glColor3f((float)((float)tabColor.getRed()/255.0f),(float)((float)tabColor.getGreen()/255.0f),(float)((float)tabColor.getBlue()/255.0f));
       if (this.size > 1) {
            gl.glPointSize(this.size);
        }
        System.out.println("Valeur du GLPoint dans GLPoint dans GL Dispaly "+this.ordinatesPt[0]);
        gl.glBegin(GL.GL_POINTS);
        gl.glVertex3d(this.ordinatesPt[0], this.ordinatesPt[1], this.ordinatesPt[2]);
        gl.glEnd();
    }


    public double getX() {

        return this.ordinatesPt[0];
    }

    public double getY() {
        return this.ordinatesPt[1];
    }

    public double getZ() {
        return this.ordinatesPt[2];
    }

    public void setX(double a) {
        this.ordinatesPt[0] = a;
    }

    public void setY(double b) {
        this.ordinatesPt[1] = b;
    }

    public void setZ(double c) {
        this.ordinatesPt[2] = c;
    }

}
