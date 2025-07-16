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
package org.geotoolkit.hips;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.rs.CodeIterator;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class HIPSTest {

    public static void main(String[] args) throws Exception {

        try (HIPSStore store = new HIPSStore(HIPSProvider.provider(), new StorageConnector(new URI("https://data.camras.nl/hips/hipslist")))) {


            for (Resource r : store.components()) {
                System.out.println(r.getIdentifier().get());

                if (r instanceof DiscreteGlobalGridResource dgr) {
                    System.out.println(dgr.getAvailableDepths());
                    System.out.println(dgr.getMaxRelativeDepth());

                    final DiscreteGlobalGridReferenceSystem dggrs = dgr.getGridGeometry().getReferenceSystem();
                    final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
                    final List<Object> roots = dggrs.getGridSystem().getHierarchy().getGrids().get(0).getZones().map(Zone::getIdentifier).toList();
                    final Zone baseZone = coder.decode(roots.get(0));
                    Stream<Zone> children = baseZone.getChildrenAtRelativeDepth(3);
                    List<Object> zones = children.map(Zone::getIdentifier).toList();
                    DiscreteGlobalGridCoverage coverage = dgr.read(new DiscreteGlobalGridGeometry(dggrs, zones, null));

                    CodeIterator ite = coverage.createIterator();
                    while (ite.next()) {
//                        System.out.println(ite.getZone().getGeographicIdentifier() + "   " + Arrays.toString(ite.getCell((double[])null)));
                    }

                    break;
                }

            }

        }

    }

}
