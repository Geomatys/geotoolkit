/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.container.statefull;

import java.awt.geom.AffineTransform;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class StatefullContextParams {

    public final ReferencedCanvas2D canvas;
    public final FeatureMapLayer layer;
    public final AffineTransform objectiveToDisplay = new AffineTransform();
    public final GeometryCoordinateSequenceTransformer dataToObjectiveTransformer = new GeometryCoordinateSequenceTransformer();
    public final GeometryCoordinateSequenceTransformer dataToDisplayTransformer = new GeometryCoordinateSequenceTransformer();
    public CoordinateReferenceSystem objectiveCRS;
    public CoordinateReferenceSystem displayCRS;
    public boolean decimate = false;
    public double decimation = 0;

    public MathTransform dataToObjective = null;

    public StatefullContextParams(ReferencedCanvas2D canvas, FeatureMapLayer layer){
        this.canvas = canvas;
        this.layer = layer;
    }

    public void updateGeneralizationFactor(RenderingContext2D renderingContext, CoordinateReferenceSystem dataCRS){
        //check if needed generalization
        final Boolean generalize = (Boolean) renderingContext.getCanvas().getRenderingHint(GO2Hints.KEY_GENERALIZE);
        if(generalize != null && generalize.booleanValue() == true){
            decimate = true;
            try {
                final MathTransform trs = renderingContext.getMathTransform(renderingContext.getObjectiveCRS(), dataCRS);
                DirectPosition vect = new DirectPosition2D(renderingContext.getResolution()[0], renderingContext.getResolution()[1]);
                vect = trs.transform(vect, vect);
                double[] decim = vect.getCoordinate();
                decimation = (decim[0]<decim[1]) ? decim[0] : decim[1] ;
            } catch (Exception ex) {
                ex.printStackTrace();
                decimation = 0;
            }
        }else{
            decimate = false;
        }
    }

}
