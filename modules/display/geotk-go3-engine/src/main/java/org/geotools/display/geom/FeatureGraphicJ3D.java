/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.display.geom;

import java.awt.Color;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotools.display.primitive.Graphic3D;
import org.geotoolkit.display.primitive.ReferencedGraphic.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.opengis.display.primitive.Graphic;

/**
 *
 * @author axel
 */
public class FeatureGraphicJ3D extends Graphic3D{

    public FeatureGraphicJ3D(ReferencedCanvas canvas ) {
        super(canvas);
    }



    @Override
    public void paint(GLAutoDrawable drawable) {

        GL gl = drawable.getGL();

//        // Clear the drawing area
//        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
//        // Reset the current matrix to the "identity"
//        gl.glLoadIdentity();


          /**
         * GLPoint
         */

        Color tabColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        float sizePoint = (float) Math.random() * 10;
            tabColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
            GLPoint pointtest = new GLPoint(new double[]{0, 0, 0.0}, sizePoint);//spooler CRS
            pointtest.display(gl, tabColor);
            System.out.println("Point GL value => " + pointtest.getX() + pointtest.getY() + pointtest.getZ());
        
     
          // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
