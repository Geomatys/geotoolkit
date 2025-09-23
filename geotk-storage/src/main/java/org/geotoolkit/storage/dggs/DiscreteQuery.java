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

import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.storage.Query;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.Envelope;

/**
 * A specialized query for DGGS.
 *
 * @todo not used yet
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteQuery extends Query {

    private Filter<? super Feature> selection;
    private String zoneId;
    private int zoneDepth;
    private String[] properties;

    /**
     * Sets searched zone identifier.
     *
     * @param zoneId zone identifier, or {@code null} if none.
     */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Returns the searched zone identifier.
     *
     * @return the zone identifier or {@code null} if none.
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * Sets the children zone depths to be returned.
     *
     * @param zoneDepth zone depths or {@code null} if none.
     */
    public void setZoneDepth(Integer zoneDepth) {
        this.zoneDepth = zoneDepth;
    }

    /**
     * Returns the children zone depths to be returned.
     *
     * @return children zone depth or {@code null} if none.
     */
    public Integer getZoneDepth() {
        return zoneDepth;
    }

    @Override
    public void setSelection(Envelope domain) {
        Filter<Feature> filter = null;
        if (domain != null) {
            final FilterFactory<Feature,Object,?> ff = DefaultFilterFactory.forFeatures();
            filter = ff.bbox(ff.property(AttributeConvention.GEOMETRY), domain);
        }
        setSelection(filter);
    }

    /**
     * Sets a filter for trimming zone instances.
     * Zones that do not pass the filter are discarded.
     *
     * @param  selection  the filter, or {@code null} if none.
     */
    public void setSelection(Filter<? super Feature> selection) {
        this.selection = selection;
    }

    /**
     * Returns the filter for trimming feature instances.
     * This is the value specified in the last call to {@link #setSelection(Filter)}.
     * The default value is {@code null}, which means that no filtering is applied.
     *
     * @return the filter, or {@code null} if none.
     */
    public Filter<? super Feature> getSelection() {
        return selection;
    }

    @Override
    public void setProjection(String... properties) {
        this.properties = properties;
    }

    public String[] getProjection() {
        return properties;
    }
}
