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

import java.awt.geom.AffineTransform;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class StatelessContextParams<T extends MapLayer> {

    public RenderingContext2D context;
    public final AffineTransform objectiveToDisplay = new AffineTransform(2,0,0,2,0,0);
    public final GeometryCSTransformer objToDisplayTransformer =
            new GeometryCSTransformer(new CoordinateSequenceMathTransformer(null));

    /**
     * This envelope should be the painted are in ojective CRS,
     * but symbolizer may need to enlarge it because of symbols size.
     */
    public org.locationtech.jts.geom.Envelope objectiveJTSEnvelope = null;

    public StatelessContextParams(){
    }

    public void update(final RenderingContext2D context){
        this.context = context;
        if (context.wraps != null) {
            this.objectiveJTSEnvelope = context.wraps.objectiveJTSEnvelope;
        }

        final AffineTransform2D objtoDisp = context.getObjectiveToDisplay();
        if (!objtoDisp.equals(objectiveToDisplay)) {
            objectiveToDisplay.setTransform(objtoDisp);
            ((CoordinateSequenceMathTransformer)objToDisplayTransformer.getCSTransformer())
                    .setTransform(objtoDisp);
        }

    }

}
