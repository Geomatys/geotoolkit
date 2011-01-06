/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sld;

import java.util.Collection;
import java.util.List;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.style.DefaultMutableStyle;
import org.geotoolkit.style.MutableStyle;

import org.opengis.feature.Feature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.sld.CoverageConstraint;
import org.opengis.sld.CoverageExtent;
import org.opengis.sld.Extent;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.InlineFeature;
import org.opengis.sld.RangeAxis;
import org.opengis.sld.RemoteOWS;
import org.opengis.sld.SLDLibrary;

/**
 * Default immplementation of SLD Factory.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSLDFactory extends Factory implements MutableSLDFactory{
    
    @Override
    public MutableStyledLayerDescriptor createSLD() {
        return new DefaultMutableSLD();
    }

    @Override
    public MutableNamedLayer createNamedLayer() {
        return new DefaultMutableNamedLayer();
    }

    @Override
    public MutableUserLayer createUserLayer() {
        return new DefaultMutableUserLayer();
    }

    @Override
    public MutableNamedStyle createNamedStyle() {
        return new DefaultMutableNamedStyle();
    }

    @Override
    public MutableStyle createUserStyle() {
        return new DefaultMutableStyle();
    }

    @Override
    public MutableLayerCoverageConstraints createLayerCoverageConstraints() {
        return new DefaultMutableLayerCoverageConstraints();
    }

    @Override
    public MutableLayerFeatureConstraints createLayerFeatureConstraints() {
        return new DefaultMutableLayerFeatureConstraints();
    }

    @Override
    public SLDLibrary createSLDLibrary(final OnlineResource online) {
        return new DefaultSLDLibrary(online);
    }

    @Override
    public RemoteOWS createRemoteOWS(final String service, final OnlineResource online) {
        return new DefaultRemoteOWS(service, online);
    }

    @Override
    public InlineFeature createInLineFeature(final Collection<Collection<Feature>> features) {
        return new DefaultInlineFeature(features);
    }

    @Override
    public CoverageConstraint createCoverageConstraint(
            final String name, final CoverageExtent extent) {
        return new DefaultCoverageConstraint(name, extent);
    }

    @Override
    public FeatureTypeConstraint createFeatureTypeConstraint(final Name name,
            final Filter filter, final List<Extent> extents) {
        return new DefaultFeatureTypeConstraint(name, filter, extents);
    }

    @Override
    public CoverageExtent createCoverageExtent(final String timeperiod) {
        return new DefaultCoverageExtent(timeperiod, null);
    }

    @Override
    public CoverageExtent createCoverageExtent(final List<RangeAxis> ranges) {
        return new DefaultCoverageExtent(null, ranges);
    }

    @Override
    public Extent createExtent(final String name, final String value) {
        return new DefaultExtent(name, value);
    }

    @Override
    public RangeAxis createRangeAxis(final String name, final String value) {
        return new DefaultRangeAxis(name, value);
    }
}
