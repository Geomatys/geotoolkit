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
package org.geotoolkit.referencing.dggs.internal.shared;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.opengis.metadata.citation.Party;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractZone<T extends DiscreteGlobalGridReferenceSystem> implements Zone {

    protected T dggrs;

    public AbstractZone(T dggrs) {
        this.dggrs = dggrs;
    }

    @Override
    public Double volumeMetersCube() {
        return null;
    }

    @Override
    public Double temporalDurationSeconds() {
        return null;
    }

    @Override
    public Collection<? extends InternationalString> getAlternativeGeographicIdentifiers() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public TemporalExtent getTemporalExtent() {
        return null;
    }

    @Override
    public Party getAdministrator() {
        return dggrs.getOverallOwner();
    }

    @Override
    public boolean isNeighbor(Object zone) {
        final Zone cdt = dggrs.getGridSystem().getHierarchy().getZone(zone);
        return getNeighbors().contains(cdt);
    }

    @Override
    public boolean isSibling(Object zone) {
        final Zone cdt = dggrs.getGridSystem().getHierarchy().getZone(zone);
        if (cdt.getLocationType().getRefinementLevel() != getLocationType().getRefinementLevel()) return false;
        final Collection<? extends Zone> parents = getParents();
        for (Zone z : cdt.getParents()) {
            if (parents.contains(z)) return true;
        }
        return false;
    }

    @Override
    public boolean isAncestorOf(Object zone, int maxRelativeDepth) {
        final Zone cdt = dggrs.getGridSystem().getHierarchy().getZone(zone);
        final int cdtz = cdt.getLocationType().getRefinementLevel();
        final int relativeDepth = cdtz - getLocationType().getRefinementLevel();
        if (relativeDepth <= 0 || relativeDepth > maxRelativeDepth) return false;
        return searchInParents(cdt, this, maxRelativeDepth);
    }

    private boolean searchInParents(Zone base, Zone searched, int toDepth) {
        if (toDepth == 0) {
            return searched.equals(base);
        } else {
            for (Zone p : base.getParents()) {
                if (searchInParents(p, searched, toDepth-1)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDescendantOf(Object zone, int maxRelativeDepth) {
        final Zone cdt = dggrs.getGridSystem().getHierarchy().getZone(zone);
        return cdt.isAncestorOf(this, maxRelativeDepth);
    }

    @Override
    public boolean overlaps(Object zone) {
        final Zone cdt = dggrs.getGridSystem().getHierarchy().getZone(zone);
        if (cdt.getLocationType().getRefinementLevel() == getLocationType().getRefinementLevel()) return false;
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractZone other = (AbstractZone) obj;
        if (!Objects.equals(this.getTextIdentifier(), other.getTextIdentifier())) {
            return false;
        }
        return Objects.equals(this.dggrs, other.dggrs);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.dggrs);
        hash = 53 * hash + Objects.hashCode(this.getTextIdentifier());
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getGeographicIdentifier();
    }
}
