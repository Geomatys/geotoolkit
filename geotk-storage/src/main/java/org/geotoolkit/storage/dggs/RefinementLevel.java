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
package org.geotoolkit.storage.dggs;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.sis.metadata.iso.citation.DefaultOrganisation;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.util.SimpleInternationalString;
import org.opengis.metadata.citation.Party;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.gazetteer.LocationType;
import org.opengis.util.InternationalString;

/**
 * A subtype of LocationType dedicated to DGGRS refinement levels.
 * It only stores a depth level.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class RefinementLevel implements LocationType {

    private final DiscreteGlobalGridReferenceSystem dggrs;
    private final int level;

    public RefinementLevel(DiscreteGlobalGridReferenceSystem dggrs, int level) {
        this.dggrs = dggrs;
        this.level = level;
    }

    public int getRefinementLevel() {
        return level;
    }

    @Override
    public InternationalString getName() {
        return new SimpleInternationalString("" + level);
    }

    @Override
    public InternationalString getTheme() {
        return new SimpleInternationalString("DGGRS");
    }

    @Override
    public Collection<? extends InternationalString> getIdentifications() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public InternationalString getDefinition() {
        return getName();
    }

    @Override
    public GeographicExtent getTerritoryOfUse() {
        return new DefaultGeographicBoundingBox(-180, 180, -90, 90);
    }

    @Override
    public DiscreteGlobalGridReferenceSystem getReferenceSystem() {
        return dggrs;
    }

    @Override
    public Party getOwner() {
        return new DefaultOrganisation();
    }

    @Override
    public Collection<? extends LocationType> getParents() {
        if (level == 0) return Collections.EMPTY_LIST;
        return List.of(new RefinementLevel(dggrs, level-1));
    }

    @Override
    public Collection<? extends LocationType> getChildren() {
        final int maxLevel = dggrs.getGridSystem().getHierarchy().getGrids().size();
        if (level >= maxLevel) return Collections.EMPTY_LIST;
        return List.of(new RefinementLevel(dggrs, level+1));
    }

}
