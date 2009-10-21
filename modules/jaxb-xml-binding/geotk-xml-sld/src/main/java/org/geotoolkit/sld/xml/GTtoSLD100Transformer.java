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
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GTtoSLD100Transformer extends GTtoSE100Transformer implements SLDVisitor {

    private final org.geotoolkit.sld.xml.v100.ObjectFactory sld_factory;

    public GTtoSLD100Transformer() {
        this.sld_factory = new org.geotoolkit.sld.xml.v100.ObjectFactory();
    }

    @Override
    public org.geotoolkit.sld.xml.v100.StyledLayerDescriptor visit(StyledLayerDescriptor sld, Object data) {
        final org.geotoolkit.sld.xml.v100.StyledLayerDescriptor versionned = sld_factory.createStyledLayerDescriptor();
        versionned.setName(sld.getName());
//        versionned.setVersion(sld.getVersion());
        if (sld.getDescription() != null) {
            if(sld.getDescription().getAbstract() != null)
                versionned.setAbstract(sld.getDescription().getAbstract().toString());
            if(sld.getDescription().getAbstract() != null)
                versionned.setTitle(sld.getDescription().getTitle().toString());
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
    public Object visit(SLDLibrary library, Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store SLD libraries.");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.NamedLayer visit(NamedLayer layer, Object data) {
        final org.geotoolkit.sld.xml.v100.NamedLayer named = sld_factory.createNamedLayer();
        named.setName(layer.getName());
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
    public org.geotoolkit.sld.xml.v100.UserLayer visit(UserLayer layer, Object data) {
        final org.geotoolkit.sld.xml.v100.UserLayer user = sld_factory.createUserLayer();
        user.setName(layer.getName());
        
        if(layer.getConstraints() instanceof LayerFeatureConstraints){
            final LayerFeatureConstraints cons = (LayerFeatureConstraints) layer.getConstraints();
            user.setLayerFeatureConstraints( visit(cons,null) );
        }else if(layer.getConstraints() instanceof LayerCoverageConstraints){
           //SLD v1.0.0 doesnt handle coverage constraints
        }
        
        
        if(layer.getSource() instanceof RemoteOWS){
            final RemoteOWS remote = (RemoteOWS) layer.getSource();
            user.setRemoteOWS( visit(remote,null) );
        }else if(layer.getSource() instanceof InlineFeature){
            //SLD v1.0.0 doesnt handle inline feature
        }
        
        for(final Style style : layer.styles()){
            user.getUserStyle().add( visit(style,null) );
        }
        
        return user;
    }

    @Override
    public org.geotoolkit.sld.xml.v100.NamedStyle visit(NamedStyle style, Object data) {
        final org.geotoolkit.sld.xml.v100.NamedStyle named = sld_factory.createNamedStyle();
        named.setName(style.getName());
        return named;
    }

    @Override
    public Object visit(LayerCoverageConstraints constraints, Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store coverage constraints.");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.LayerFeatureConstraints visit(LayerFeatureConstraints constraints, Object data) {
        final org.geotoolkit.sld.xml.v100.LayerFeatureConstraints cons = sld_factory.createLayerFeatureConstraints();
        
        for(final FeatureTypeConstraint fc : constraints.constraints() ){
            cons.getFeatureTypeConstraint().add( visit(fc,null) );
        }
        
        return cons;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.CoverageConstraint visit(CoverageConstraint constraint, Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store coverage constraints.");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.FeatureTypeConstraint visit(FeatureTypeConstraint constraint, Object data) {
        final org.geotoolkit.sld.xml.v100.FeatureTypeConstraint ftc = sld_factory.createFeatureTypeConstraint();
        
        ftc.setFeatureTypeName( visitName(constraint.getFeatureTypeName()).getLocalPart() );
        ftc.setFilter( visit(constraint.getFilter()));
        
        for(final Extent ext : constraint.getExtent()){
            ftc.getExtent().add( visit(ext,null) );
        }
        
        return ftc;
    }

    @Override
    public Object visit(CoverageExtent extent, Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store coverage extent.");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.Extent visit(Extent extent, Object data) {
        final org.geotoolkit.sld.xml.v100.Extent ext = sld_factory.createExtent();
        ext.setName(extent.getName());
        ext.setValue(extent.getValue());
        return ext;
    }

    @Override
    public Object visit(RangeAxis axi, Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store range axis");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.RemoteOWS visit(RemoteOWS ows, Object data) {
        final org.geotoolkit.sld.xml.v100.RemoteOWS remote = sld_factory.createRemoteOWS();
        remote.setService(ows.getService());
        remote.setOnlineResource( visit(ows.getOnlineResource(), null) );
        return remote;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.InlineFeature visit(InlineFeature inline, Object data) {
        //TODO handle this when GML will be ready
        return null;
    }
}
        


