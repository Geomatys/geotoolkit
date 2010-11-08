
package org.geotoolkit.pending.demo.geometry;

import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

public class SRIDDemo {

    public static void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException {

        final CoordinateReferenceSystem crs = CRS.decode("EPSG:3395");

        //converting the CRS to an integer
        final int srid = SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1);
        
        //srid back to CRS
        final CoordinateReferenceSystem backcrs = CRS.decode(SRIDGenerator.toSRS(srid, SRIDGenerator.Version.V1));

    }

}
