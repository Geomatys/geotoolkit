package org.geotools.display.canvas;

import java.awt.Image;
import java.awt.Shape;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.factory.Hints;
import org.opengis.display.canvas.CanvasController;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class DefaultJ3DCanvas extends J3DCanvas implements GLEventListener {

private final RenderingContext3D context = new DefaultRenderingContext3D();

    public DefaultJ3DCanvas (CoordinateReferenceSystem crs,Hints hints) {
     super(crs,hints);
    }
    

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();
//        System.err.println("INIT GL IS: " + gl.getClass().getName());
//
        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(1.0f, 0.5f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.


//    gl.glClearColor(0, 0, 0, 1);							// Couleur servant à effacer la fenêtre (noir)
//    gl.glShadeModel(gl.GL_SMOOTH);							// Modèle d'ombrage : lissage de Gouraud
//	gl.glEnable(gl.GL_CULL_FACE);								// Ne traite pas les faces cachées
//	gl.glEnable(gl.GL_DEPTH_TEST);							// Active le Z-Buffer
//	gl.glDepthFunc(gl.GL_LEQUAL);								// Mode de fonctionnement du Z-Buffer
//	gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_NICEST);	// Active la correction de perspective (pour ombrage, texture, ...)

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
//
//        if (height <= 0) { // avoid a divide by zero error!
//
//            height = 1;
//        }
//        final float h = (float) width / (float) height;
//
//        System.out.println("Valeurs du Canvas => "+ width +" "+ height);
//        gl.glViewport(0, 0, width, height);
//        gl.glMatrixMode(GL.GL_PROJECTION);
//        gl.glLoadIdentity();
//        glu.gluPerspective(45.0f, h, 1.0, 20.0);
//        gl.glMatrixMode(GL.GL_MODELVIEW);
//        gl.glLoadIdentity();
//
////    gl.glMatrixMode(GL.GL_PROJECTION);
////    gl.glLoadIdentity();
////    gl.glOrtho(0, width, 0, height, -1.0, 1.0);
//

        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glOrtho(0.0,1.0,0.0,1.0,0.0,0.0);

    }

    public void display(GLAutoDrawable drawable) {
        render(drawable);
  
    }




    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public CanvasController getController() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    protected RenderingContext getRenderingContext() {
        return context;
    }
}

