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
package org.geotoolkit.referencing.rs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.gazetteer.ReferencingByIdentifiers;
import org.apache.sis.util.Utilities;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReferenceSystems {

    private ReferenceSystems(){}

    public static ReferenceSystem createCompound(ReferenceSystem ... rss) {
        if (rss.length == 0) return null;
        if (rss.length == 1) return rss[0];

        final List<ReferenceSystem> lst = new ArrayList<>();
        for (ReferenceSystem rs : rss) {
            if (rs instanceof CompoundRS crs) {
                lst.addAll(crs.getComponents());
            } else {
                lst.add(rs);
            }
        }
        return new DefaultCompoundRS(null, lst);
    }

    public static int getDimension(ReferenceSystem rs) {
        int dim = 0;
        for (ReferenceSystem single : getSingleComponents(rs, false)) {
            if (single instanceof ReferencingByIdentifiers) {
                dim += 1;
            } else if (single instanceof CoordinateReferenceSystem crs) {
                dim += crs.getCoordinateSystem().getDimension();
            } else {
                throw new UnsupportedOperationException("Unexpected reference system " + rs.getClass().getName());
            }
        }
        return dim;
    }

    public static List<ReferenceSystem> getSingleComponents(ReferenceSystem rs, boolean splitVertical) {
        List<ReferenceSystem> rss;
        if (rs instanceof CompoundRS crs) {
            rss = crs.getSingleComponents();
        } else if (rs instanceof CoordinateReferenceSystem crs) {
            rss = (List) CRS.getSingleComponents(crs);
        } else {
            rss = List.of(rs);
        }

        if (splitVertical) {
            rss = new ArrayList<>(rss);
            for (int i = 0; i < rss.size(); i++) {
                ReferenceSystem cdt = rss.get(i);
                if (cdt instanceof CoordinateReferenceSystem crs) {
                    VerticalCRS vertical = CRS.getVerticalComponent(crs, true);
                    if (vertical != null && vertical != cdt) {
                        SingleCRS horizontal = CRS.getHorizontalComponent(crs);
                        if ((horizontal.getCoordinateSystem().getDimension() + vertical.getCoordinateSystem().getDimension()) != crs.getCoordinateSystem().getDimension()) {
                            throw new UnsupportedOperationException("Splited vertical crs + horizontal does not match original crs size");
                        }
                        if (Utilities.equalsIgnoreMetadata(horizontal, CommonCRS.WGS84.normalizedGeographic())) {
                            horizontal = CommonCRS.WGS84.normalizedGeographic();
                        } else if (Utilities.equalsIgnoreMetadata(horizontal, CommonCRS.WGS84.geographic())) {
                            horizontal = CommonCRS.WGS84.geographic();
                        }
                        if (Utilities.equalsIgnoreMetadata(vertical, CommonCRS.Vertical.ELLIPSOIDAL.crs())) {
                            vertical = CommonCRS.Vertical.ELLIPSOIDAL.crs();
                        }
                        rss.set(i, horizontal);
                        rss.add(i+1, vertical);
                        i++;
                    }
                }
            }
        }

        return rss;
    }

    public static Optional<ReferenceSystem> getHorizontalComponent(ReferenceSystem rs) {
        for (ReferenceSystem single : getSingleComponents(rs, true)) {
            if (single instanceof ReferencingByIdentifiers
              ||((single instanceof CoordinateReferenceSystem c) && CRS.isHorizontalCRS(c))) {
                return Optional.of(single);
            }
        }
        return Optional.empty();
    }

    public static Optional<VerticalCRS> getVerticalComponent(ReferenceSystem rs) {
        for (ReferenceSystem single : getSingleComponents(rs, true)) {
            if (single instanceof VerticalCRS vcrs) {
                return Optional.of(vcrs);
            }
        }
        return Optional.empty();
    }

    public static Optional<TemporalCRS> getTemporalComponent(ReferenceSystem rs) {
        for (ReferenceSystem single : getSingleComponents(rs, true)) {
            if (single instanceof TemporalCRS tcrs) {
                return Optional.of(tcrs);
            }
        }
        return Optional.empty();
    }

    /**
     * Any reference system have a leaning real world CRS somehow.
     * In the worst case scenario this crs will be based on CRS:84.
     */
    public static CoordinateReferenceSystem getLeaningCRS(ReferenceSystem rs) throws FactoryException {
        if (rs instanceof CoordinateReferenceSystem crs) return crs;
        if (rs instanceof DefaultCompoundRS drs) return drs.getLeaningCrs();
        if (rs instanceof DiscreteGlobalGridReferenceSystem dggrs) return dggrs.getGridSystem().getCrs();

        final List<ReferenceSystem> singleComponents = ReferenceSystems.getSingleComponents(rs, true);
        final List<CoordinateReferenceSystem> crss = new ArrayList<>(singleComponents.size()+1);
        for (int i = 0; i < singleComponents.size(); i++) {
            final ReferenceSystem srs = singleComponents.get(i);
            if (srs instanceof CoordinateReferenceSystem crs) {
                crss.add(crs);
            } else if (srs instanceof DiscreteGlobalGridReferenceSystem dggrs) {
                crss.add(dggrs.getGridSystem().getCrs());
            } else {
                throw new UnsupportedOperationException("todo");
            }
        }
        return CRS.compound(crss.toArray(CoordinateReferenceSystem[]::new));
    }
}
