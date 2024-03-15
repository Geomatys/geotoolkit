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

import org.geotoolkit.sld.CoverageConstraint;
import org.geotoolkit.sld.CoverageExtent;
import org.geotoolkit.sld.Extent;
import org.geotoolkit.sld.FeatureTypeConstraint;
import org.geotoolkit.sld.InlineFeature;
import org.geotoolkit.sld.Layer;
import org.geotoolkit.sld.LayerCoverageConstraints;
import org.geotoolkit.sld.LayerFeatureConstraints;
import org.geotoolkit.sld.LayerStyle;
import org.geotoolkit.sld.NamedLayer;
import org.geotoolkit.sld.NamedStyle;
import org.geotoolkit.sld.RangeAxis;
import org.geotoolkit.sld.RemoteOWS;
import org.geotoolkit.sld.SLDLibrary;
import org.geotoolkit.sld.SLDVisitor;
import org.geotoolkit.sld.StyledLayerDescriptor;
import org.geotoolkit.sld.UserLayer;
import org.opengis.style.Style;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GTtoSLD100Transformer extends GTtoSE100Transformer implements SLDVisitor {

    private final org.geotoolkit.sld.xml.v100.ObjectFactory sld_factory;

    public GTtoSLD100Transformer() {
        this.sld_factory = new org.geotoolkit.sld.xml.v100.ObjectFactory();
    }

    @Override
    public org.geotoolkit.sld.xml.v100.StyledLayerDescriptor visit(final StyledLayerDescriptor sld, final Object data) {
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
    public Object visit(final SLDLibrary library, final Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store SLD libraries.");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.NamedLayer visit(final NamedLayer layer, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.UserLayer visit(final UserLayer layer, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.NamedStyle visit(final NamedStyle style, final Object data) {
        final org.geotoolkit.sld.xml.v100.NamedStyle named = sld_factory.createNamedStyle();
        named.setName(style.getName());
        return named;
    }

    @Override
    public Object visit(final LayerCoverageConstraints constraints, final Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store coverage constraints.");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.LayerFeatureConstraints visit(final LayerFeatureConstraints constraints, final Object data) {
        final org.geotoolkit.sld.xml.v100.LayerFeatureConstraints cons = sld_factory.createLayerFeatureConstraints();

        for(final FeatureTypeConstraint fc : constraints.constraints() ){
            cons.getFeatureTypeConstraint().add( visit(fc,null) );
        }

        return cons;
    }

    @Override
    public org.geotoolkit.sld.xml.v110.CoverageConstraint visit(final CoverageConstraint constraint, final Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store coverage constraints.");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.FeatureTypeConstraint visit(final FeatureTypeConstraint constraint, final Object data) {
        final org.geotoolkit.sld.xml.v100.FeatureTypeConstraint ftc = sld_factory.createFeatureTypeConstraint();

        ftc.setFeatureTypeName( visitName((org.opengis.util.GenericName) constraint.getFeatureTypeName()).toString() );
        ftc.setFilter(apply(constraint.getFilter()));

        for(final Extent ext : constraint.getExtent()){
            ftc.getExtent().add( visit(ext,null) );
        }

        return ftc;
    }

    @Override
    public Object visit(final CoverageExtent extent, final Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store coverage extent.");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.Extent visit(final Extent extent, final Object data) {
        final org.geotoolkit.sld.xml.v100.Extent ext = sld_factory.createExtent();
        ext.setName(extent.getName());
        ext.setValue(extent.getValue());
        return ext;
    }

    @Override
    public Object visit(final RangeAxis axi, final Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store range axis");
    }

    @Override
    public org.geotoolkit.sld.xml.v100.RemoteOWS visit(final RemoteOWS ows, final Object data) {
        final org.geotoolkit.sld.xml.v100.RemoteOWS remote = sld_factory.createRemoteOWS();
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



