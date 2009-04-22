/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.style.xml;

import org.geotoolkit.internal.jaxb.v110.se.OnlineResourceType;
import org.geotoolkit.internal.jaxb.v110.sld.UseSLDLibrary;
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
 */
public class GTtoSLD110Transformer extends GTtoSE110Transformer implements SLDVisitor {

    private final org.geotoolkit.internal.jaxb.v110.sld.ObjectFactory sld_factory;
    private final org.geotoolkit.internal.jaxb.v110.se.ObjectFactory se_factory;

    public GTtoSLD110Transformer() {
        this.sld_factory = new org.geotoolkit.internal.jaxb.v110.sld.ObjectFactory();
        this.se_factory = new org.geotoolkit.internal.jaxb.v110.se.ObjectFactory();
    }
  
    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor visit(StyledLayerDescriptor sld, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.StyledLayerDescriptor versionned = sld_factory.createStyledLayerDescriptor();
        versionned.setName(sld.getName());
//        versionned.setVersion(sld.getVersion());
        versionned.setDescription( visit(sld.getDescription(), null));
        
        for(SLDLibrary lib : sld.libraries()){
            versionned.getUseSLDLibrary().add( visit(lib,null) );
        }
        
        for(Layer layer : sld.layers()){
            if(layer instanceof NamedLayer){
                NamedLayer named = (NamedLayer) layer;
                versionned.getNamedLayerOrUserLayer().add( visit(named,null) );
            }else if(layer instanceof UserLayer){
                UserLayer user = (UserLayer) layer;
                versionned.getNamedLayerOrUserLayer().add( visit(user,null) );
            }
        }
        
        return versionned;
    }

    @Override
    public UseSLDLibrary visit(SLDLibrary library, Object data) {
        UseSLDLibrary lib = sld_factory.createUseSLDLibrary();
        OnlineResourceType online = visit(library.getOnlineResource(), null);
        lib.setOnlineResource(online);        
        return lib;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.NamedLayer visit(NamedLayer layer, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.NamedLayer named = sld_factory.createNamedLayer();
        named.setName(layer.getName());
        named.setDescription( visit(layer.getDescription(), null));
        named.setLayerFeatureConstraints( visit(layer.getConstraints(),null) );
        
        for(LayerStyle style : layer.styles()){
            if(style instanceof NamedStyle){
                NamedStyle ns = (NamedStyle) style;
                named.getNamedStyleOrUserStyle().add( visit(ns,null) );
            }else if(style instanceof Style){
                Style us = (Style) style;
                named.getNamedStyleOrUserStyle().add( visit(us,null) );
            }
        }
        
        return named;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.UserLayer visit(UserLayer layer, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.UserLayer user = sld_factory.createUserLayer();
        user.setName(layer.getName());
        user.setDescription( visit(layer.getDescription(),null) );
        
        if(layer.getConstraints() instanceof LayerFeatureConstraints){
            LayerFeatureConstraints cons = (LayerFeatureConstraints) layer.getConstraints();
            user.setLayerFeatureConstraints( visit(cons,null) );
        }else if(layer.getConstraints() instanceof LayerCoverageConstraints){
            LayerCoverageConstraints cons = (LayerCoverageConstraints) layer.getConstraints();
            user.setLayerCoverageConstraints( visit(cons,null) );
        }
        
        
        if(layer.getSource() instanceof RemoteOWS){
            RemoteOWS remote = (RemoteOWS) layer.getSource();
            user.setRemoteOWS( visit(remote,null) );
        }else if(layer.getSource() instanceof InlineFeature){
            InlineFeature feature = (InlineFeature) layer.getSource();
            user.setInlineFeature( visit(feature,null) );
        }
        
        for(Style style : layer.styles()){
            user.getUserStyle().add( visit(style,null) );
        }
        
        return user;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.NamedStyle visit(NamedStyle style, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.NamedStyle named = sld_factory.createNamedStyle();
        named.setName(style.getName());
        named.setDescription( visit(style.getDescription(), null));
        return named;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.LayerCoverageConstraints visit(LayerCoverageConstraints constraints, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.LayerCoverageConstraints cons = sld_factory.createLayerCoverageConstraints();
        
        for( CoverageConstraint fc : constraints.constraints() ){
            cons.getCoverageConstraint().add( visit(fc,null) );
        }
        
        return cons;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.LayerFeatureConstraints visit(LayerFeatureConstraints constraints, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.LayerFeatureConstraints cons = sld_factory.createLayerFeatureConstraints();
        
        for( FeatureTypeConstraint fc : constraints.constraints() ){
            cons.getFeatureTypeConstraint().add( visit(fc,null) );
        }
        
        return cons;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.CoverageConstraint visit(CoverageConstraint constraint, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.CoverageConstraint ftc = sld_factory.createCoverageConstraint();
        
        ftc.setCoverageName(constraint.getCoverageName());
        ftc.setCoverageExtent( visit(constraint.getCoverageExtent(),null) );
        
        return ftc;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.FeatureTypeConstraint visit(FeatureTypeConstraint constraint, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.FeatureTypeConstraint ftc = sld_factory.createFeatureTypeConstraint();
        
        ftc.setFeatureTypeName( visitName(constraint.getFeatureTypeName()));
        ftc.setFilter( visit(constraint.getFilter()));
        
        for(Extent ext : constraint.getExtent()){
            ftc.getExtent().add( visit(ext,null) );
        }
        
        return ftc;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.CoverageExtent visit(CoverageExtent extent, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.CoverageExtent ce = sld_factory.createCoverageExtent();
        
        if(extent.getTimePeriod() != null){
            ce.setTimePeriod(extent.getTimePeriod());
        }else if( extent.rangeAxis() != null ){
            for(RangeAxis axe : extent.rangeAxis()){
                ce.getRangeAxis().add( visit(axe,null) );
            }
        }
        
        return ce;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.Extent visit(Extent extent, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.Extent ext = sld_factory.createExtent();
        ext.setName(extent.getName());
        ext.setValue(extent.getValue());
        return ext;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.RangeAxis visit(RangeAxis axi, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.RangeAxis axe = sld_factory.createRangeAxis();
        axe.setName(axi.getName());
        axe.setValue(axi.getValue());
        return axe;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.RemoteOWS visit(RemoteOWS ows, Object data) {
        org.geotoolkit.internal.jaxb.v110.sld.RemoteOWS remote = sld_factory.createRemoteOWS();
        remote.setService(ows.getService());
        remote.setOnlineResource( visit(ows.getOnlineResource(), null) );
        return remote;
    }

    @Override
    public org.geotoolkit.internal.jaxb.v110.sld.InlineFeature visit(InlineFeature inline, Object data) {
        //TODO handle this when GML will be ready
        return null;
    }
    
}
        


