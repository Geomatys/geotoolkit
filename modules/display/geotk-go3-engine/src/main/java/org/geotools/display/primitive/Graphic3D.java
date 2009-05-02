/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.display.primitive;

import java.awt.Shape;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.ReferencedGraphic;
import org.opengis.display.primitive.Graphic;

/**
 *
 * @author axel
 */
public abstract class Graphic3D extends ReferencedGraphic{

    public Graphic3D(ReferencedCanvas canvas ) {
        super(canvas,canvas.getObjectiveCRS());
    }

    public abstract void paint(GLAutoDrawable drawable);


}
