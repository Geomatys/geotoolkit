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
package org.geotoolkit.dggs.mgrs;

import com.google.common.geometry.S2Polygon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.gazetteer.MilitaryGridReferenceSystem;
import static org.geotoolkit.dggs.mgrs.MgrsDggrs.MGRS;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.internal.shared.AbstractDiscreteGlobalGrid;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class MgrsDgg extends AbstractDiscreteGlobalGrid<MgrsDggh> {

    private final List<Zone> roots;
    private final MilitaryGridReferenceSystem.Coder coder;

    public MgrsDgg(MgrsDggh dggh, int level) {
        super(dggh, level);

        if (level == 0) {
            Envelope env = CRS.getDomainOfValidity(CommonCRS.WGS84.normalizedGeographic());
            try {
                final MilitaryGridReferenceSystem.Coder coder = MGRS.createCoder();
                coder.setPrecision(100000);
                Iterator<String> codes = coder.encode(env);
                roots = new ArrayList<>();
                while (codes.hasNext()) {
                    roots.add(new MgrsZone(dggh.dggrs, codes.next()));
                }
            } catch (TransformException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            roots = null;
        }

        this.coder = MgrsDggrs.MGRS.createCoder();
        switch (level) {
            //case 0 : coder.setPrecision(1000000);  //todo wainting for SIS fix
            case 0 : coder.setPrecision(100000);
            case 1 : coder.setPrecision(10000);
            case 2 : coder.setPrecision(1000);
            case 3 : coder.setPrecision(100);
            case 4 : coder.setPrecision(10);
            case 5 : coder.setPrecision(1);
            default: throw new UnsupportedOperationException("Requested level if greater then maximum level");
        }
    }

    @Override
    public Zone getZone(DirectPosition dp) throws TransformException {
        final String zid = coder.encode(dp);
        return new MgrsZone(hierarchy.dggrs, zid);
    }

    @Override
    public Stream<Zone> getZones(GeographicExtent extent) throws TransformException {
        if (extent == null && level == 0) {
            return roots.stream();
        }

        //search from root
        final S2Polygon geometry = DiscreteGlobalGridSystems.toS2Polygon(extent);
        try (Stream<Zone> zones = hierarchy.getGrids().get(0).getZones()) {
            return DiscreteGlobalGridSystems.spatialSearch(zones.toList(), level, geometry);
        }
    }

}
