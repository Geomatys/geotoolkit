
package org.geotoolkit.pending.demo.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CRS;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

public class SRIDDemo {

    public static void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException {
        Demos.init();

        CoordinateReferenceSystem crs = CRS.forCode("EPSG:3395");

        //converting the CRS to an integer
        int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);

        //srid back to CRS
        CoordinateReferenceSystem backcrs = CRS.forCode(SRIDGenerator.toSRS(srid, SRIDGenerator.Version.V1));


        Point pt = new GeometryFactory().createPoint(new Coordinate(10, 50));
        //set crs on a geometry
        JTS.setCRS(pt, crs);
        //extract crs from geometry srid or user map
        backcrs = JTS.findCoordinateReferenceSystem(pt);

    }

}
