/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
import org.opengis.metadata.citation.OnLineResource;
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
    public SLDLibrary createSLDLibrary(OnLineResource online) {
        return new DefaultSLDLibrary(online);
    }

    @Override
    public RemoteOWS createRemoteOWS(String service, OnLineResource online) {
        return new DefaultRemoteOWS(service, online);
    }

    @Override
    public InlineFeature createInLineFeature(Collection<Collection<Feature>> features) {
        return new DefaultInlineFeature(features);
    }

    @Override
    public CoverageConstraint createCoverageConstraint(String name, CoverageExtent extent) {
        return new DefaultCoverageConstraint(name, extent);
    }

    @Override
    public FeatureTypeConstraint createFeatureTypeConstraint(Name name, Filter filter, List<Extent> extents) {
        return new DefaultFeatureTypeConstraint(name, filter, extents);
    }

    @Override
    public CoverageExtent createCoverageExtent(String timeperiod) {
        return new DefaultCoverageExtent(timeperiod, null);
    }

    @Override
    public CoverageExtent createCoverageExtent(List<RangeAxis> ranges) {
        return new DefaultCoverageExtent(null, ranges);
    }

    @Override
    public Extent createExtent(String name, String value) {
        return new DefaultExtent(name, value);
    }

    @Override
    public RangeAxis createRangeAxis(String name, String value) {
        return new DefaultRangeAxis(name, value);
    }
}
