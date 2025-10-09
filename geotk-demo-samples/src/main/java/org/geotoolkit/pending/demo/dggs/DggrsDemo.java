
package org.geotoolkit.pending.demo.dggs;

import java.util.Collection;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.dggs.a5.A5Dggrs;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem.Coder;
import org.geotoolkit.referencing.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicExtent;

public class DggrsDemo {

    public static void main(String[] args) throws Exception {

        //pick a DGGS implementation : A5Dggrs, H3Dggrs, NHealpixDggrs, ...
        final DiscreteGlobalGridReferenceSystem dggrs = new A5Dggrs();

        //create a coder instance to perform queries
        final Coder coder                           = dggrs.createCoder();

        //get a zone for a location
        final String hash                           = coder.encode(new DirectPosition2D(12.345, 67.89));

        //get a zone for a known identifier
        final Zone zone                             = coder.decode("2161727821137838080");

        //extract various informations from the zone
        final DirectPosition position               = zone.getPosition(); //centroid
        final Collection<? extends Zone> children   = zone.getChildren();
        final Collection<? extends Zone> neighbors  = zone.getNeighbors();
        final Collection<? extends Zone> parents    = zone.getParents();
        final Envelope envelope                     = zone.getEnvelope();
        final GeographicExtent geometry             = zone.getGeographicExtent();
        final Double areaMetersSquare               = zone.getAreaMetersSquare();
    }

}
