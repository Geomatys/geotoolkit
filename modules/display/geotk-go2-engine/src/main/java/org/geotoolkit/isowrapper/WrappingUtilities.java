

package org.geotoolkit.isowrapper;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.geotoolkit.isowrapper.geometries.AbstractISOJTSGeometry;
import org.geotoolkit.isowrapper.geometries.ISOJTSMultiPoint;
import org.geotoolkit.isowrapper.geometries.ISOJTSPoint;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WrappingUtilities {

    private WrappingUtilities(){}

    public AbstractISOJTSGeometry wrap(Geometry geom){

        if(geom == null) return null;


        if(geom instanceof Point){
            return new ISOJTSPoint((Point)geom);
        }else if(geom instanceof MultiPoint){
            new ISOJTSMultiPoint((MultiPoint)geom);
        }else if(geom instanceof LineString){
            //ISO Curve
        }else if(geom instanceof MultiLineString){
            //ISO MultiCurve
        }else if(geom instanceof Polygon){
            //ISO surface
        }else if(geom instanceof MultiPolygon){
            //ISO MultiSurface
        }

        return null;
    }


}
