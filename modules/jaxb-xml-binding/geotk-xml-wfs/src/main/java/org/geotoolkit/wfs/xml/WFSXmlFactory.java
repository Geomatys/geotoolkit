/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wfs.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.opengis.filter.capability.FilterCapabilities;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WFSXmlFactory {
    
    public FeatureTypeList buildFeatureTypeList(final String version) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v200.FeatureTypeListType();
        } else if ("1.1.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v110.FeatureTypeListType();
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v100.FeatureTypeListType();
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
    
    public WFSCapabilities buildWFSCapabilities(final String version, final String updateSequence) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v200.WFSCapabilitiesType(version, updateSequence);
        } else if ("1.1.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType(version, updateSequence);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v100.WFSCapabilitiesType(version, updateSequence);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
    
    public FeatureType buildFeatureType(final String version, final QName name, final String title, final String defaultCRS, final List<String> otherCRS, final Object bbox) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType> bboxes = new ArrayList<org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType>();
            if (bbox != null && !(bbox instanceof org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType)) {
                throw new IllegalArgumentException("unexpected object version for bbox");
            } else if (bbox != null) {
                bboxes.add((org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType)bbox);
            }
            return new org.geotoolkit.wfs.xml.v200.FeatureTypeType(name, title, defaultCRS, otherCRS, bboxes);
        } else if ("1.1.0".equals(version)) {
            final List<org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType> bboxes = new ArrayList<org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType>();
            if (bbox != null && !(bbox instanceof org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType)) {
                throw new IllegalArgumentException("unexpected object version for bbox");
            } else if (bbox != null) {
                bboxes.add((org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType)bbox);
            }
            return new org.geotoolkit.wfs.xml.v110.FeatureTypeType(name, title, defaultCRS, otherCRS, bboxes);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType> bboxes = new ArrayList<org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType>();
            if (bbox != null && !(bbox instanceof org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType)) {
                throw new IllegalArgumentException("unexpected object version for bbox");
            } else if (bbox != null) {
                bboxes.add((org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType)bbox);
            }
            return new org.geotoolkit.wfs.xml.v100.FeatureTypeType(name, title, defaultCRS, bboxes);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
    
    public Object buildBBOX(final String version, final String crsName, final double minx, final double miny, final double maxx, final double maxy) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType(
                       crsName,
                       minx,
                       miny,
                       maxx,
                       maxy);
        } else if ("1.1.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType(
                       crsName,
                       minx,
                       miny,
                       maxx,
                       maxy);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType(minx, miny, maxx, maxy);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
    
    public WFSCapabilities buildWFSCapabilities(final String version,  final AbstractServiceIdentification si, final AbstractServiceProvider sp, 
            final AbstractOperationsMetadata om, FeatureTypeList ftl, final FilterCapabilities fc) {
        
        if ("2.0.0".equals(version)) {
            if ((si  != null && !(si instanceof org.geotoolkit.ows.xml.v110.ServiceIdentification)) ||
                (sp  != null &&!(sp instanceof org.geotoolkit.ows.xml.v110.ServiceProvider))        ||
                (om  != null &&!(om instanceof org.geotoolkit.ows.xml.v110.OperationsMetadata))     ||
                (ftl != null &&!(ftl instanceof org.geotoolkit.wfs.xml.v200.FeatureTypeListType))   ||
                (fc  != null &&!(fc instanceof org.geotoolkit.ogc.xml.v200.FilterCapabilities))) {
                throw new IllegalArgumentException("Bad version of object");
            }
            return new  org.geotoolkit.wfs.xml.v200.WFSCapabilitiesType("2.0.0", 
                       (org.geotoolkit.ows.xml.v110.ServiceIdentification)si, 
                       (org.geotoolkit.ows.xml.v110.ServiceProvider)      sp, 
                       (org.geotoolkit.ows.xml.v110.OperationsMetadata)   om, 
                       (org.geotoolkit.wfs.xml.v200.FeatureTypeListType)  ftl, 
                       (org.geotoolkit.ogc.xml.v200.FilterCapabilities)   fc);
        } else if ("1.1.0".equals(version)) {
            if ((si  != null && !(si instanceof org.geotoolkit.ows.xml.v100.ServiceIdentification)) ||
                (sp  != null && !(sp instanceof org.geotoolkit.ows.xml.v100.ServiceProvider))       ||
                (om  != null && !(om instanceof org.geotoolkit.ows.xml.v100.OperationsMetadata))    ||
                (ftl != null && !(ftl instanceof org.geotoolkit.wfs.xml.v110.FeatureTypeListType))  ||
                (fc  != null && !(fc instanceof org.geotoolkit.ogc.xml.v110.FilterCapabilities))) {
                throw new IllegalArgumentException("Bad version of object");
            }
            return new  org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType("1.1.0", 
                       (org.geotoolkit.ows.xml.v100.ServiceIdentification)si, 
                       (org.geotoolkit.ows.xml.v100.ServiceProvider)      sp, 
                       (org.geotoolkit.ows.xml.v100.OperationsMetadata)   om, 
                       (org.geotoolkit.wfs.xml.v110.FeatureTypeListType)  ftl, 
                       (org.geotoolkit.ogc.xml.v110.FilterCapabilities)   fc);
        }/*  TODO not supported yet
             else if ("1.0.0".equals(version)) {
            return new  org.geotoolkit.wfs.xml.v100.WFSCapabilitiesType("1.0.0", 
                       (org.geotoolkit.ows.xml.v100.ServiceIdentification)si, 
                       (org.geotoolkit.ows.xml.v100.ServiceProvider)      sp, 
                       (org.geotoolkit.ows.xml.v100.OperationsMetadata)   om, 
                       (org.geotoolkit.wfs.xml.v110.FeatureTypeListType)  ftl, 
                       (org.geotoolkit.ogc.xml.v110.FilterCapabilities)   fc);
        } */else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
}
