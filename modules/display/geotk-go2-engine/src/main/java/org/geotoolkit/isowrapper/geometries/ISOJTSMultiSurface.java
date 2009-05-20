/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.isowrapper.geometries;

import java.util.HashSet;
import java.util.Set;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.primitive.OrientableSurface;

/**
 *
 * @author sorel
 */
public class ISOJTSMultiSurface extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.MultiPolygon> implements MultiSurface{

    public ISOJTSMultiSurface(com.vividsolutions.jts.geom.MultiPolygon pl) {
        super(pl);
    }

    @Override
    public Set<OrientableSurface> getElements() {
        Set<OrientableSurface> curves = new HashSet<OrientableSurface>();
        int num = jtsGeometry.getNumGeometries();

        for(int i=0; i<num;i++){
            com.vividsolutions.jts.geom.Polygon poly = (com.vividsolutions.jts.geom.Polygon) jtsGeometry.getGeometryN(i);
            curves.add(new ISOJTSSurface(poly));
        }

        return curves;
    }

    @Override
    public double getArea() {
        return jtsGeometry.getArea();
    }

}
