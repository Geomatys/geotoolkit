/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggs.healpix;

import java.util.List;
import org.geotoolkit.dggs.AbstractDggrsTest;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.ZonalIdentifier;
import org.geotoolkit.storage.dggs.Zone;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class HealPixTest extends AbstractDggrsTest {

    public HealPixTest() {
        super(new NHealpixDggrs());
    }

    public static void main(String[] args) throws Exception {

        final NHealpixDggrs dggrs = new NHealpixDggrs();
        final List<ZonalIdentifier> rootZoneIds = dggrs.getRootZoneIds();
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();

        for (ZonalIdentifier zoneId : rootZoneIds) {
            final Zone zone = coder.decode(zoneId);
            final List<Zone> candidates = zone.getChildrenAtRelativeDepth(0).toList();

            //check the coder can find the zone by location
            for (Zone z : candidates) {
                coder.setPrecisionLevel(z.getLocationType().getRefinementLevel());
                String candidate = coder.encode(z.getPosition());
                System.out.println(((NHealpixZone)z).getNpixel() +"  " + z.getGeographicIdentifier() +" " + z.getPosition());
                Zone cdt = coder.decode(candidate);
                System.out.println(((NHealpixZone)cdt).getNpixel() +"  " + cdt.getGeographicIdentifier());

                System.out.println(" ");

            }

        }

    }


}
