/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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
package org.geotoolkit.display2d.container.stateless;

import com.vividsolutions.jts.geom.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.util.Classes;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class StatelessContextParams<T extends MapLayer> {

    /**
     * 50pixels ensure large strokes of graphics won't show on the map.
     * TODO : need to find a better way to reduce the geometry preserving length
     */
    public static final int CLIP_PIXEL_MARGIN = 50;
    
    public RenderingContext2D context;
    public final AbstractCanvas2D canvas;
    public final T layer;
    public final AffineTransform objectiveToDisplay = new AffineTransform(2,0,0,2,0,0);
    public final GeometryCSTransformer objToDisplayTransformer =
            new GeometryCSTransformer(new CoordinateSequenceMathTransformer(null));
    public CoordinateReferenceSystem objectiveCRS;
    public CoordinateReferenceSystem displayCRS;
    
    /**
     * This envelope should be the painted are in ojective CRS,
     * but symbolizer may need to enlarge it because of symbols size.
     */
    public com.vividsolutions.jts.geom.Envelope objectiveJTSEnvelope = null;

    //clipping geometries
    public Rectangle2D displayClipRect;
    public Polygon displayClip;
    
    public StatelessContextParams(final AbstractCanvas2D canvas, final T layer){
        this.canvas = canvas;
        this.layer = layer;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Classes.getShortName(StatelessContextParams.class));
        sb.append("  ");
        sb.append(objectiveToDisplay);
        return sb.toString();
    }

    public void update(final RenderingContext2D context){
        this.context = context;
        this.objectiveCRS = context.getObjectiveCRS2D();
        this.displayCRS = context.getDisplayCRS();
        if(context.wraps!=null){
            this.objectiveJTSEnvelope = context.wraps.objectiveJTSEnvelope;
        }

        final AffineTransform2D objtoDisp = context.getObjectiveToDisplay();
        if(!objtoDisp.equals(objectiveToDisplay)){
            objectiveToDisplay.setTransform(objtoDisp);
            ((CoordinateSequenceMathTransformer)objToDisplayTransformer.getCSTransformer())
                    .setTransform(objtoDisp);
        }
        
        displayClipRect = (Rectangle2D) context.getCanvasDisplayBounds().clone();
        displayClipRect.setRect(
                displayClipRect.getX()-CLIP_PIXEL_MARGIN, 
                displayClipRect.getY()-CLIP_PIXEL_MARGIN, 
                displayClipRect.getWidth()+2*CLIP_PIXEL_MARGIN, 
                displayClipRect.getHeight()+2*CLIP_PIXEL_MARGIN);
        displayClip = JTS.toGeometry(context.getCanvasDisplayBounds());
    }

}
