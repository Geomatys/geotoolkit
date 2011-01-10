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
package org.geotoolkit.sld.xml;

import org.geotoolkit.se.xml.v110.OnlineResourceType;
import org.geotoolkit.sld.xml.v110.UseSLDLibrary;
import org.opengis.sld.CoverageConstraint;
import org.opengis.sld.CoverageExtent;
import org.opengis.sld.Extent;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.InlineFeature;
import org.opengis.sld.Layer;
import org.opengis.sld.LayerCoverageConstraints;
import org.opengis.sld.LayerFeatureConstraints;
import org.opengis.sld.LayerStyle;
import org.opengis.sld.NamedLayer;
import org.opengis.sld.NamedStyle;
import org.opengis.sld.RangeAxis;
import org.opengis.sld.RemoteOWS;
import org.opengis.sld.SLDLibrary;
import org.opengis.sld.SLDVisitor;
import org.opengis.sld.StyledLayerDescriptor;
import org.opengis.sld.UserLayer;
import org.opengis.style.Style;

/**
 * Transform a GT SLD in a jaxb SLD v1.1.0 object
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GTtoSLD110Transformer extends GTtoSE110Transformer implements SLDVisitor {

    private final org.geotoolkit.sld.xml.v110.ObjectFactory sld_factory;
    private final org.geotoolkit.se.xml.v110.ObjectFactory se_factory;

    public GTtoSLD110Transformer() {
        this.sld_factory = new org.geotoolkit.sld.xml.v110.ObjectFactory();
        this.se_factory = new org.geotoolkit.se.xml.v110.ObjectFactory();
    }
  
    @Override
    public org.geotoolkit.sld.xml.v110.StyledLayerDescriptor visit(final StyledLayerDescriptor sld, final Object data) {
        final org.geotoolkit.sld.xml.v110.StyledLayerDescriptor versionned = sld_factory.createStyledLayerDescriptor();
        versionned.setName(sld.getName());
//        versionned.setVersion(sld.getVersion());
        versionned.setDescription( visit(sld.getDescription(), null));
        
        for(final SLDLibrary lib : sld.libraries()){
            versionned.getUseSLDLibrary().add( visit(lib,null) );
        }
        
        for(final Layer layer : sld.layers()){
            if(layer instanceof NamedLayer){
                final NamedLayer named = (NamedLayer) layer;
                versionned.getNamedLayerOrUserLayer().add( visit(named,null) );
            }else if(layer instanceof UserLayer){
                final UserLayer user = (UserLayer) layer;
                versionned.getNamedLayerOrUserLayer().add( visit(user,null) );
            }
        }
        
        return versionned;
    }

    @Override
    public UseSLDLibrary visit(final SLDLibrary library, final Object data) {
        final UseSLDLibrary lib = sld_factory.createUseSLDLibrary();
        final OnlineResourceType online = visit(library.getOnlineResource(), null);
        lib.setOnlineResource(online);        
        return lib;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.NamedLayer visit(final NamedLayer layer, final Object data) {
        final org.geotoolkit.sld.xml.v110.NamedLayer named = sld_factory.createNamedLayer();
        named.setName(layer.getName());
        named.setDescription( visit(layer.getDescription(), null));
        named.setLayerFeatureConstraints( visit(layer.getConstraints(),null) );
        
        for(final LayerStyle style : layer.styles()){
            if(style instanceof NamedStyle){
                final NamedStyle ns = (NamedStyle) style;
                named.getNamedStyleOrUserStyle().add( visit(ns,null) );
            }else if(style instanceof Style){
                final Style us = (Style) style;
                named.getNamedStyleOrUserStyle().add( visit(us,null) );
            }
        }
        
        return named;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.UserLayer visit(final UserLayer layer, final Object data) {
        final org.geotoolkit.sld.xml.v110.UserLayer user = sld_factory.createUserLayer();
        user.setName(layer.getName());
        user.setDescription( visit(layer.getDescription(),null) );
        
        if(layer.getConstraints() instanceof LayerFeatureConstraints){
            final LayerFeatureConstraints cons = (LayerFeatureConstraints) layer.getConstraints();
            user.setLayerFeatureConstraints( visit(cons,null) );
        }else if(layer.getConstraints() instanceof LayerCoverageConstraints){
            final LayerCoverageConstraints cons = (LayerCoverageConstraints) layer.getConstraints();
            user.setLayerCoverageConstraints( visit(cons,null) );
        }
        
        
        if(layer.getSource() instanceof RemoteOWS){
            final RemoteOWS remote = (RemoteOWS) layer.getSource();
            user.setRemoteOWS( visit(remote,null) );
        }else if(layer.getSource() instanceof InlineFeature){
            final InlineFeature feature = (InlineFeature) layer.getSource();
            user.setInlineFeature( visit(feature,null) );
        }
        
        for(final Style style : layer.styles()){
            user.getUserStyle().add( visit(style,null) );
        }
        
        return user;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.NamedStyle visit(final NamedStyle style, final Object data) {
        final org.geotoolkit.sld.xml.v110.NamedStyle named = sld_factory.createNamedStyle();
        named.setName(style.getName());
        named.setDescription( visit(style.getDescription(), null));
        return named;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.LayerCoverageConstraints visit(final LayerCoverageConstraints constraints, final Object data) {
        final org.geotoolkit.sld.xml.v110.LayerCoverageConstraints cons = sld_factory.createLayerCoverageConstraints();
        
        for(final CoverageConstraint fc : constraints.constraints() ){
            cons.getCoverageConstraint().add( visit(fc,null) );
        }
        
        return cons;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.LayerFeatureConstraints visit(final LayerFeatureConstraints constraints, final Object data) {
        final org.geotoolkit.sld.xml.v110.LayerFeatureConstraints cons = sld_factory.createLayerFeatureConstraints();
        
        for(final FeatureTypeConstraint fc : constraints.constraints() ){
            cons.getFeatureTypeConstraint().add( visit(fc,null) );
        }
        
        return cons;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.CoverageConstraint visit(final CoverageConstraint constraint, final Object data) {
        final org.geotoolkit.sld.xml.v110.CoverageConstraint ftc = sld_factory.createCoverageConstraint();
        
        ftc.setCoverageName(constraint.getCoverageName());
        ftc.setCoverageExtent( visit(constraint.getCoverageExtent(),null) );
        
        return ftc;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.FeatureTypeConstraint visit(final FeatureTypeConstraint constraint, final Object data) {
        final org.geotoolkit.sld.xml.v110.FeatureTypeConstraint ftc = sld_factory.createFeatureTypeConstraint();
        
        ftc.setFeatureTypeName( visitName(constraint.getFeatureTypeName()));
        ftc.setFilter( visit(constraint.getFilter()));
        
        for(final Extent ext : constraint.getExtent()){
            ftc.getExtent().add( visit(ext,null) );
        }
        
        return ftc;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.CoverageExtent visit(final CoverageExtent extent, final Object data) {
        final org.geotoolkit.sld.xml.v110.CoverageExtent ce = sld_factory.createCoverageExtent();
        
        if(extent.getTimePeriod() != null){
            ce.setTimePeriod(extent.getTimePeriod());
        }else if( extent.rangeAxis() != null ){
            for(final RangeAxis axe : extent.rangeAxis()){
                ce.getRangeAxis().add( visit(axe,null) );
            }
        }
        
        return ce;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.Extent visit(final Extent extent, final Object data) {
        final org.geotoolkit.sld.xml.v110.Extent ext = sld_factory.createExtent();
        ext.setName(extent.getName());
        ext.setValue(extent.getValue());
        return ext;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.RangeAxis visit(final RangeAxis axi, final Object data) {
        final org.geotoolkit.sld.xml.v110.RangeAxis axe = sld_factory.createRangeAxis();
        axe.setName(axi.getName());
        axe.setValue(axi.getValue());
        return axe;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.RemoteOWS visit(final RemoteOWS ows, final Object data) {
        final org.geotoolkit.sld.xml.v110.RemoteOWS remote = sld_factory.createRemoteOWS();
        remote.setService(ows.getService());
        remote.setOnlineResource( visit(ows.getOnlineResource(), null) );
        return remote;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.InlineFeature visit(final InlineFeature inline, final Object data) {
        //TODO handle this when GML will be ready
        return null;
    }
    
}
        


