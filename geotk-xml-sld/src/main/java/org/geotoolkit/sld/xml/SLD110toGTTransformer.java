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
import java.util.Map;

import org.geotoolkit.sld.xml.v110.InlineFeature;
import org.geotoolkit.sld.xml.v110.LayerCoverageConstraints;
import org.geotoolkit.sld.xml.v110.LayerFeatureConstraints;
import org.geotoolkit.sld.xml.v110.NamedLayer;
import org.geotoolkit.sld.xml.v110.StyledLayerDescriptor;
import org.geotoolkit.sld.xml.v110.UseSLDLibrary;
import org.geotoolkit.sld.xml.v110.UserLayer;
import org.geotoolkit.sld.xml.v110.UserStyle;
import org.geotoolkit.sld.MutableLayer;
import org.geotoolkit.sld.MutableLayerCoverageConstraints;
import org.geotoolkit.sld.MutableLayerFeatureConstraints;
import org.geotoolkit.sld.MutableLayerStyle;
import org.geotoolkit.sld.MutableNamedLayer;
import org.geotoolkit.sld.MutableNamedStyle;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableUserLayer;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.geotoolkit.filter.FilterFactory2;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.util.FactoryException;
import org.opengis.sld.CoverageConstraint;
import org.opengis.sld.CoverageExtent;
import org.opengis.sld.Extent;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.RangeAxis;
import org.opengis.sld.RemoteOWS;
import org.opengis.sld.SLDLibrary;
import org.opengis.sld.Source;

/**
 * Transform a SLD v1.1.0 in GT classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class SLD110toGTTransformer extends SE110toGTTransformer{

    protected final MutableSLDFactory sldFactory;

    public SLD110toGTTransformer(final FilterFactory2 filterFactory, final MutableStyleFactory styleFactory,
            final MutableSLDFactory sldFactory) {
        super(filterFactory,styleFactory);
        this.sldFactory = sldFactory;
    }

    public SLD110toGTTransformer(final FilterFactory2 filterFactory, final MutableStyleFactory styleFactory,
            final MutableSLDFactory sldFactory, final Map<String, String> namespaceMapping) {
        super(filterFactory,styleFactory, namespaceMapping);
        this.sldFactory = sldFactory;
    }

    /**
     * Transform a jaxb v1.1.0 SLD in a GT class.
     */
    public MutableStyledLayerDescriptor visit(final StyledLayerDescriptor sld) throws FactoryException{
        final MutableStyledLayerDescriptor geoSLD = sldFactory.createSLD();
        geoSLD.setName(sld.getName());
        geoSLD.setVersion(sld.getVersion());
        geoSLD.setDescription(visitDescription(sld.getDescription()));
        geoSLD.libraries().addAll( visitLibraries(sld.getUseSLDLibrary()) );
        geoSLD.layers().addAll( visitLayers(sld.getNamedLayerOrUserLayer()));
        return geoSLD;
    }

    /**
     * Transform a jaxb v1.1.0 SLD UseSLDLibrary in a GT SLD library objects.
     */
    public Collection<? extends SLDLibrary> visitLibraries( final List<UseSLDLibrary> libs){
        if(libs == null || libs.isEmpty()){
            return Collections.emptyList();
        }else{
            final Collection<SLDLibrary> sldLibs = new ArrayList<SLDLibrary>();
            for(final UseSLDLibrary use : libs){
                final OnlineResource online = visitOnlineResource(use.getOnlineResource());
                if(online != null) sldLibs.add(sldFactory.createSLDLibrary(online));

            }
            return sldLibs;
        }
    }

    /**
     * Transform a jaxb v1.1.0 SLD layers in a GT layers Objects
     */
    public Collection<? extends MutableLayer> visitLayers(final List<Object> layers) throws FactoryException{
        if(layers == null || layers.isEmpty()){
            return Collections.emptyList();
        } else {
            final Collection<MutableLayer> sldLayers = new ArrayList<MutableLayer>();

            for(final Object obj : layers){
                if(obj instanceof NamedLayer){
                    final NamedLayer nl = (NamedLayer) obj;
                    final MutableNamedLayer mnl = sldFactory.createNamedLayer();
                    mnl.setName(nl.getName());
                    mnl.setDescription(visitDescription(nl.getDescription()));
                    mnl.getConstraints().constraints().addAll(visitFeatureConstraints(nl.getLayerFeatureConstraints()));
                    mnl.styles().addAll( visitStyles(nl.getNamedStyleOrUserStyle()) );
                    sldLayers.add(mnl);
                }else if( obj instanceof UserLayer){
                    final UserLayer ul = (UserLayer) obj;
                    final MutableUserLayer mul = sldFactory.createUserLayer();
                    mul.setName(ul.getName());
                    mul.setDescription(visitDescription(ul.getDescription()));
                    mul.styles().addAll( visitUserStyles(ul.getUserStyle()) );

                    if(ul.getLayerCoverageConstraints() != null){
                        final MutableLayerCoverageConstraints consts = sldFactory.createLayerCoverageConstraints();
                        consts.constraints().addAll(visitCoverageConstraints(ul.getLayerCoverageConstraints()));
                        mul.setConstraints(consts);
                    }else if(ul.getLayerFeatureConstraints() != null){
                        final MutableLayerFeatureConstraints consts = sldFactory.createLayerFeatureConstraints();
                        consts.constraints().addAll(visitFeatureConstraints(ul.getLayerFeatureConstraints()));
                        mul.setConstraints(consts);
                    }

                    if(ul.getInlineFeature() != null){
                        mul.setSource(visitInlineFeature(ul.getInlineFeature()));
                    }else if(ul.getRemoteOWS() != null){
                        mul.setSource(visiteRemoteOWS(ul.getRemoteOWS()));
                    }

                    sldLayers.add(mul);
                }
            }

            return sldLayers;
        }


    }

    /**
     * Transform a jaxb v1.1.0 SLD constraints in a GT constraints class.
     */
    public Collection<? extends FeatureTypeConstraint> visitFeatureConstraints(final LayerFeatureConstraints ftc) throws FactoryException{
        if(ftc == null || ftc.getFeatureTypeConstraint() == null || ftc.getFeatureTypeConstraint().isEmpty()){
            return Collections.emptyList();
        }else{
            final Collection<FeatureTypeConstraint> constraints = new ArrayList<FeatureTypeConstraint>();

            for(final org.geotoolkit.sld.xml.v110.FeatureTypeConstraint aftc : ftc.getFeatureTypeConstraint()){
                final GenericName name = visitQName(aftc.getFeatureTypeName());
                final Filter filter = visitFilter(aftc.getFilter());
                final List<Extent> extents = visitExtents(aftc.getExtent());
                final FeatureTypeConstraint cons = sldFactory.createFeatureTypeConstraint(name, filter, extents);
                constraints.add(cons);
            }

            return constraints;
        }
    }

    /**
     * Transform a jaxb v1.1.0 SLD constraints in a GT constraints class.
     */
    public Collection<? extends CoverageConstraint> visitCoverageConstraints(final LayerCoverageConstraints ftc){
        if(ftc == null || ftc.getCoverageConstraint() == null || ftc.getCoverageConstraint().isEmpty()){
            return Collections.emptyList();
        }else{
            final Collection<CoverageConstraint> constraints = new ArrayList<CoverageConstraint>();

            for(final org.geotoolkit.sld.xml.v110.CoverageConstraint aftc : ftc.getCoverageConstraint()){
                final String name = aftc.getCoverageName();
                final CoverageExtent extent = visitCoverageExtent(aftc.getCoverageExtent());
                final CoverageConstraint cons = sldFactory.createCoverageConstraint(name, extent);
                constraints.add(cons);
            }

            return constraints;
        }
    }

    /**
     * Transform a jaxb v1.1.0 SLD extents in a GT extents class.
     */
    public List<Extent> visitExtents(final List<org.geotoolkit.sld.xml.v110.Extent> exts){
        if(exts == null || exts.isEmpty()){
            return Collections.emptyList();
        }else{
            final List<Extent> extents = new ArrayList<Extent>();

            for(final org.geotoolkit.sld.xml.v110.Extent ex : exts){
                extents.add(sldFactory.createExtent(ex.getName(), ex.getValue()));
            }

            return extents;
        }
    }

    /**
     * Transform a jaxb v1.1.0 SLD extent in a GT extent class.
     */
    public CoverageExtent visitCoverageExtent(final org.geotoolkit.sld.xml.v110.CoverageExtent coverageExtent) {
        if(coverageExtent == null){
            return null;
        }else{

            if(coverageExtent.getTimePeriod() != null){
                return sldFactory.createCoverageExtent(coverageExtent.getTimePeriod());
            }else if(coverageExtent.getRangeAxis() != null){
                return sldFactory.createCoverageExtent(visitRangeAxis(coverageExtent.getRangeAxis()));
            }else{
                return null;
            }


        }
    }

    /**
     * Transform a jaxb v1.1.0 SLD RangeAxis in a GT RangeAxis class.
     */
    public List<RangeAxis> visitRangeAxis(final List<org.geotoolkit.sld.xml.v110.RangeAxis> ranges){
        if(ranges == null || ranges.isEmpty()){
            return Collections.emptyList();
        }else{
            final List<RangeAxis> axis = new ArrayList<RangeAxis>();

            for(final org.geotoolkit.sld.xml.v110.RangeAxis axe : ranges){
                axis.add( sldFactory.createRangeAxis(axe.getName(), axe.getValue()) );
            }

            return axis;
        }
    }

    /**
     * Transform a jaxb v1.1.0 SLD remoteOWS in a GT remoteOWS class.
     */
    public RemoteOWS visiteRemoteOWS(final org.geotoolkit.sld.xml.v110.RemoteOWS ows){
        if(ows == null){
            return null;
        }else{
            final OnlineResource online = visitOnlineResource(ows.getOnlineResource());
            if( online != null){
                return sldFactory.createRemoteOWS(ows.getService(), online);
            }else{
                return null;
            }
        }
    }

    /**
     * Transform a jaxb v1.1.0 SLD inlineFeature in a GT inlineFeature class.
     */
    public Source visitInlineFeature(final InlineFeature inlineFeature) {
        //TODO : fix this when GML works.
        return null;
    }

    /**
     * Transform a jaxb v1.1.0 SLD layer style in a GT style class.
     */
    public Collection<? extends MutableLayerStyle> visitStyles(final List<Object> styles) throws FactoryException{
        if(styles == null || styles.isEmpty()){
            return Collections.emptyList();
        }else{
            final Collection<MutableLayerStyle> mStyles = new ArrayList<MutableLayerStyle>();

            for(final Object obj : styles){
                if(obj instanceof org.geotoolkit.sld.xml.v110.NamedStyle){
                    final org.geotoolkit.sld.xml.v110.NamedStyle ns = (org.geotoolkit.sld.xml.v110.NamedStyle) obj;
                    final MutableNamedStyle mns = sldFactory.createNamedStyle();
                    mns.setName(ns.getName());
                    mns.setDescription(visitDescription(ns.getDescription()));
                    mStyles.add(mns);
                }else if(obj instanceof org.geotoolkit.sld.xml.v110.UserStyle){
                    final org.geotoolkit.sld.xml.v110.UserStyle us = (org.geotoolkit.sld.xml.v110.UserStyle) obj;
                    //we call SE transformer for this part
                    mStyles.add(visitUserStyle(us));
                }
            }

            return mStyles;
        }
    }


    /**
     * Transform a jaxb v1.1.0 layer style in a GT layer style class.
     */
    public Collection<? extends MutableStyle> visitUserStyles(final List<UserStyle> styles) throws FactoryException{
        if(styles == null || styles.isEmpty()){
            return Collections.emptyList();
        }else{
            final Collection<MutableStyle> mStyles = new ArrayList<MutableStyle>();

            for(final org.geotoolkit.sld.xml.v110.UserStyle us : styles){
                //we call SE transformer for this part
                mStyles.add(visitUserStyle(us));
            }

            return mStyles;
        }
    }

}
