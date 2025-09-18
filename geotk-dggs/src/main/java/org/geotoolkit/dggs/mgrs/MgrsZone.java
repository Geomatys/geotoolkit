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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.sis.referencing.gazetteer.MilitaryGridReferenceSystem;
import org.geotoolkit.referencing.dggs.RefinementLevel;
import org.geotoolkit.referencing.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.citation.Party;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.referencing.gazetteer.Location;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class MgrsZone implements Zone {

    private final MgrsDggrs dggrs;
    private final Location location;

    public MgrsZone(MgrsDggrs dggrs, String hash) throws TransformException {
        this.dggrs = dggrs;
        final MilitaryGridReferenceSystem.Coder coder = MgrsDggrs.MGRS.createCoder();
        location = coder.decode(hash);
    }

    public MgrsZone(MgrsDggrs dggrs, Location location) {
        this.dggrs = dggrs;
        this.location = location;
    }

    @Override
    public Object getIdentifier() {
        return location.getGeographicIdentifier().toString();
    }

    @Override
    public CharSequence getTextIdentifier() {
        return location.getGeographicIdentifier().toString();
    }

    @Override
    public InternationalString getGeographicIdentifier() {
        return location.getGeographicIdentifier();
    }

    @Override
    public long getLongIdentifier() {
        throw new UnsupportedOperationException("MGRS do not have a numeric zone notation");
    }

    @Override
    public Collection<? extends InternationalString> getAlternativeGeographicIdentifiers() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getShapeType() {
        return "rectangle";
    }

    @Override
    public Double getAreaMetersSquare() {
        return 0.0;
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
    public RefinementLevel getLocationType() {
        final String hash = location.getGeographicIdentifier().toString();
        final int level;
        switch (hash.length()) {
            case 2 : level = 0; break;
            case 4 : level = 1; break;
            case 6 : level = 2; break;
            case 8 : level = 3; break;
            case 10 : level = 4; break;
            case 12 : level = 5; break;
            case 14 : level = 6; break;
            default : throw new IllegalArgumentException("Incorrect MGRS code : " + hash);
        }
        return new RefinementLevel(dggrs, level);
    }

    @Override
    public Collection<? extends Zone> getParents() {
        final Collection<? extends Location> candidates = location.getParents();
        final List<Zone> zones = new ArrayList<>(candidates.size());
        for(Location l : candidates) {
            zones.add(new MgrsZone(dggrs, l));
        }
        return zones;
    }

    @Override
    public Collection<? extends Zone> getChildren() {
        final Collection<? extends Location> candidates = location.getParents();
        final List<Zone> zones = new ArrayList<>(candidates.size());
        for(Location l : candidates) {
            zones.add(new MgrsZone(dggrs, l));
        }
        return zones;
    }

    @Override
    public Collection<? extends Zone> getNeighbors() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public TemporalExtent getTemporalExtent() {
        return null;
    }

    @Override
    public BoundingPolygon getGeographicExtent() {
        return (BoundingPolygon) location.getGeographicExtent();
    }

    @Override
    public DirectPosition getPosition() {
        return location.getPosition();
    }

    @Override
    public Party getAdministrator() {
        return dggrs.getOverallOwner();
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
        final MgrsZone other = (MgrsZone) obj;
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        return Objects.equals(this.dggrs, other.dggrs);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.dggrs);
        hash = 53 * hash + Objects.hashCode(this.location);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getGeographicIdentifier();
    }
}
