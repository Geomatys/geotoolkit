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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.geotoolkit.util.SimpleInternationalString;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.sld.xml.v100.LayerFeatureConstraints;
import org.geotoolkit.sld.xml.v100.NamedLayer;
import org.geotoolkit.sld.xml.v100.StyledLayerDescriptor;
import org.geotoolkit.sld.xml.v100.UserLayer;
import org.geotoolkit.sld.xml.v100.UserStyle;
import org.geotoolkit.sld.MutableLayer;
import org.geotoolkit.sld.MutableLayerFeatureConstraints;
import org.geotoolkit.sld.MutableLayerStyle;
import org.geotoolkit.sld.MutableNamedLayer;
import org.geotoolkit.sld.MutableNamedStyle;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableUserLayer;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.metadata.citation.OnLineResource;
import org.opengis.sld.Extent;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.RemoteOWS;
import org.opengis.util.InternationalString;

/**
 * Transform a SLD v1.0.0 in GT classes.
 *
 * @author Johann Sorel (Geomatys)
 */
public class SLD100toGTTransformer extends SE100toGTTransformer{

    protected final MutableSLDFactory sldFactory;

    public SLD100toGTTransformer(FilterFactory2 filterFactory, MutableStyleFactory styleFactory, 
            MutableSLDFactory sldFactory) {
        super(filterFactory,styleFactory);
        this.sldFactory = sldFactory;
    }

    /**
     * Transform a jaxb v1.0.0 SLD in a GT SLD object.
     */
    public MutableStyledLayerDescriptor visit(StyledLayerDescriptor sld){
        final MutableStyledLayerDescriptor geoSLD = sldFactory.createSLD();
        geoSLD.setName(sld.getName());
        geoSLD.setVersion(sld.getVersion());
        final InternationalString title = (sld.getTitle() == null) ? null : new SimpleInternationalString(sld.getTitle());
        final InternationalString abs = (sld.getAbstract() == null) ? null : new SimpleInternationalString(sld.getAbstract());
        geoSLD.setDescription(styleFactory.description(title, abs));
        geoSLD.layers().addAll( visitLayers(sld.getNamedLayerOrUserLayer())); 
        return geoSLD;
    }
    
    /**
     * Transform a jaxb v1.0.0 layers in a GT Layer objects.
     */
    public Collection<? extends MutableLayer> visitLayers(List<Object> layers){
        if(layers == null || layers.isEmpty()){
            return Collections.emptyList();
        } else {
            final Collection<MutableLayer> sldLayers = new ArrayList<MutableLayer>();
            
            for(final Object obj : layers){
                if(obj instanceof NamedLayer){
                    final NamedLayer nl = (NamedLayer) obj;
                    final MutableNamedLayer mnl = sldFactory.createNamedLayer();
                    mnl.setName(nl.getName());
                    mnl.getConstraints().constraints().addAll(visitFeatureConstraints(nl.getLayerFeatureConstraints()));
                    mnl.styles().addAll( visitStyles(nl.getNamedStyleOrUserStyle()) );
                    sldLayers.add(mnl);
                }else if( obj instanceof UserLayer){
                    final UserLayer ul = (UserLayer) obj;
                    final MutableUserLayer mul = sldFactory.createUserLayer();
                    mul.setName(ul.getName());
                    mul.styles().addAll( visitUserStyles(ul.getUserStyle()) );
                    
                    if(ul.getLayerFeatureConstraints() != null){
                        final MutableLayerFeatureConstraints consts = sldFactory.createLayerFeatureConstraints();
                        consts.constraints().addAll(visitFeatureConstraints(ul.getLayerFeatureConstraints()));
                        mul.setConstraints(consts);
                    }
                    
                    if(ul.getRemoteOWS() != null){
                        mul.setSource(visiteRemoteOWS(ul.getRemoteOWS()));
                    }
                    
                    sldLayers.add(mul);
                }
            }
            
            return sldLayers;
        }
        
        
    }
       
    /**
     * Transform a jaxb v1.0.0 constraints in a GT constraints class.
     */
    public Collection<? extends FeatureTypeConstraint> visitFeatureConstraints(LayerFeatureConstraints ftc){
        if(ftc == null || ftc.getFeatureTypeConstraint() == null || ftc.getFeatureTypeConstraint().isEmpty()){
            return Collections.emptyList();
        }else{
            final Collection<FeatureTypeConstraint> constraints = new ArrayList<FeatureTypeConstraint>();
            
            for(final org.geotoolkit.sld.xml.v100.FeatureTypeConstraint aftc : ftc.getFeatureTypeConstraint()){
                final Name name = new DefaultName(aftc.getFeatureTypeName());
                final Filter filter = visitFilter(aftc.getFilter());
                final List<Extent> extents = visitExtents(aftc.getExtent());
                final FeatureTypeConstraint cons = sldFactory.createFeatureTypeConstraint(name, filter, extents);
                constraints.add(cons);
            }
            
            return constraints;
        }
    }
    
    /**
     * Transform a jaxb v1.0.0 extents in a GT extent class.
     */
    public List<Extent> visitExtents(List<org.geotoolkit.sld.xml.v100.Extent> exts){
        if(exts == null || exts.isEmpty()){
            return Collections.emptyList();
        }else{
            final List<Extent> extents = new ArrayList<Extent>();
            
            for(final org.geotoolkit.sld.xml.v100.Extent ex : exts){
                extents.add(sldFactory.createExtent(ex.getName(), ex.getValue()));
            }
            
            return extents;
        }
    }
       
    /**
     * Transform a jaxb v1.0.0 remote ows in a GT remote ows class.
     */
    public RemoteOWS visiteRemoteOWS(org.geotoolkit.sld.xml.v100.RemoteOWS ows){
        if(ows == null){
            return null;
        }else{
            final OnLineResource online = visitOnlineResource(ows.getOnlineResource());
            if( online != null){
                return sldFactory.createRemoteOWS(ows.getService(), online);
            }else{
                return null;
            }
        }
    }
       
    /**
     * Transform a jaxb v1.0.0 layer style in a GT layer style class.
     */
    public Collection<? extends MutableLayerStyle> visitStyles(List<Object> styles){
        if(styles == null || styles.isEmpty()){
            return Collections.emptyList();
        }else{
            final Collection<MutableLayerStyle> mStyles = new ArrayList<MutableLayerStyle>();
            
            for(final Object obj : styles){
                
                if(obj instanceof org.geotoolkit.sld.xml.v100.NamedStyle){
                    final org.geotoolkit.sld.xml.v100.NamedStyle ns = (org.geotoolkit.sld.xml.v100.NamedStyle) obj;
                    final MutableNamedStyle mns = sldFactory.createNamedStyle();
                    mns.setName(ns.getName());
                    mStyles.add(mns);
                }else if(obj instanceof org.geotoolkit.sld.xml.v100.UserStyle){
                    final org.geotoolkit.sld.xml.v100.UserStyle us = (org.geotoolkit.sld.xml.v100.UserStyle) obj;
                    //we call SE transformer for this part
                    mStyles.add(visitUserStyle(us));
                }
            }
            
            return mStyles;
        }
    }
    
    /**
     * Transform a jaxb v1.0.0 layer style in a GT layer style class.
     */
    public Collection<? extends MutableStyle> visitUserStyles(List<UserStyle> styles){
        if(styles == null || styles.isEmpty()){
            return Collections.emptyList();
        }else{
            final Collection<MutableStyle> mStyles = new ArrayList<MutableStyle>();
            
            for(final org.geotoolkit.sld.xml.v100.UserStyle us : styles){
                //we call SE transformer for this part
                mStyles.add(visitUserStyle(us));
            }
            
            return mStyles;
        }
    }
    
    
}
