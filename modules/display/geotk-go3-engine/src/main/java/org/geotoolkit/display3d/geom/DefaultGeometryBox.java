

package org.geotoolkit.display3d.geom;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.shape.Box;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DefaultGeometryBox extends Box{

    public DefaultGeometryBox(Geometry geom, double miny, double maxy) {
        super();
        Envelope env = geom.getEnvelopeInternal();
        this.setData(   new Vector3(env.getMinX(), miny, env.getMinY()),
                        new Vector3(env.getMaxX(), maxy, env.getMaxY()));
    }

}
