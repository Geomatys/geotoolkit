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

package org.geotoolkit.wms.xml;

import java.util.ArrayList;
import java.util.List;
import org.opengis.metadata.extent.GeographicBoundingBox;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WmsXmlFactory {

    public static AbstractGeographicBoundingBox createGeographicBoundingBox(final String currentVersion, final GeographicBoundingBox bbox) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.LatLonBoundingBox(bbox);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.EXGeographicBoundingBox(bbox);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractGeographicBoundingBox createGeographicBoundingBox(final String currentVersion, final double minx, final double miny, 
            final double maxx, final double maxy) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.LatLonBoundingBox(minx, miny, maxx, maxy);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.EXGeographicBoundingBox(minx, miny, maxx, maxy);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractBoundingBox createBoundingBox(final String currentVersion, final String crs, final double minx, final double miny,
            final double maxx, final double maxy, final double resx, final double resy) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.BoundingBox(crs, minx, miny, maxx, maxy, resx, resy);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.BoundingBox(crs, minx, miny, maxx, maxy, resx, resy);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractDimension createDimension(final String currentVersion, final String name, final String units, final String _default, final String value) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.Dimension(name, units, _default, value);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.Dimension(name, units, _default, value);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractDimension createDimension(final String currentVersion, final String value, final String name, final String units, final String unitSymbol, 
            final String _default, final Boolean multipleValues, final Boolean nearestValue,
            final Boolean current) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.Dimension(value, name, units, unitSymbol, _default, multipleValues, nearestValue, current);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.Dimension(value, name, units, unitSymbol, _default, multipleValues, nearestValue, current);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractOnlineResource createOnlineResource(final String currentVersion, final String href) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.OnlineResource(href);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.OnlineResource(href);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractLegendURL createLegendURL(final String currentVersion, final String format, final AbstractOnlineResource res, final Integer width, final Integer height) {
        if ("1.1.1".equals(currentVersion)) {
            if (res != null && !(res instanceof org.geotoolkit.wms.xml.v111.OnlineResource)) {
                throw new IllegalArgumentException("unexpected object version for onlineResource");
            } 
            return new org.geotoolkit.wms.xml.v111.LegendURL(format, (org.geotoolkit.wms.xml.v111.OnlineResource)res, width, height);
        } else if ("1.3.0".equals(currentVersion)) {
            if (res != null && !(res instanceof org.geotoolkit.wms.xml.v130.OnlineResource)) {
                throw new IllegalArgumentException("unexpected object version for onlineResource");
            } 
            return new org.geotoolkit.wms.xml.v130.LegendURL(format, (org.geotoolkit.wms.xml.v130.OnlineResource)res, width, height);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractLogoURL createLogoURL(final String currentVersion, final String format, final String href, final Integer width, final Integer height) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.LogoURL(format,href, width, height);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.LogoURL(format, href, width, height);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static Style createStyle(final String currentVersion, final String name, final String title, final String _abstract, final AbstractLegendURL... legendURLs) {
        if ("1.1.1".equals(currentVersion)) {
            org.geotoolkit.wms.xml.v111.LegendURL[] lURLs = null;
            if (legendURLs != null) {
                lURLs = new org.geotoolkit.wms.xml.v111.LegendURL[legendURLs.length];
                int i = 0;
                for (AbstractLegendURL op : legendURLs) {
                    if (op != null && !(op instanceof org.geotoolkit.wms.xml.v111.LegendURL)) {
                        throw new IllegalArgumentException("unexpected object version for legendURL");
                    } else if (op != null) {
                        lURLs[i] = (org.geotoolkit.wms.xml.v111.LegendURL)op;
                    }
                    i++;
                }
            }
            return new org.geotoolkit.wms.xml.v111.Style(name, title, _abstract, null, null, lURLs);
        } else if ("1.3.0".equals(currentVersion)) {
            org.geotoolkit.wms.xml.v130.LegendURL[] lURLs = null;
            if (legendURLs != null) {
                lURLs = new org.geotoolkit.wms.xml.v130.LegendURL[legendURLs.length];
                int i = 0;
                for (AbstractLegendURL op : legendURLs) {
                    if (op != null && !(op instanceof org.geotoolkit.wms.xml.v130.LegendURL)) {
                        throw new IllegalArgumentException("unexpected object version for legendURL");
                    } else if (op != null) {
                        lURLs[i] = (org.geotoolkit.wms.xml.v130.LegendURL)op;
                    }
                    i++;
                }
            }
            return new org.geotoolkit.wms.xml.v130.Style(name, title, _abstract, null, null, lURLs);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractLayer createLayer(final String currentVersion, final String name, final String _abstract, final String keyword,
            final List<String> crs, final AbstractGeographicBoundingBox geobbox, final AbstractBoundingBox bbox, final String queryable, 
            final List<AbstractDimension> dimensions, final List<Style> styles) {
        
        if ("1.1.1".equals(currentVersion)) {
            if (geobbox != null && !(geobbox instanceof org.geotoolkit.wms.xml.v111.LatLonBoundingBox)) {
                throw new IllegalArgumentException("unexpected object version for geobbox");
            } 
            if (bbox != null && !(bbox instanceof org.geotoolkit.wms.xml.v111.BoundingBox)) {
                throw new IllegalArgumentException("unexpected object version for bbox");
            }
            final List<org.geotoolkit.wms.xml.v111.Style> ops = new ArrayList<org.geotoolkit.wms.xml.v111.Style>();
            if (styles != null) {
                for (Style op : styles) {
                    if (op != null && !(op instanceof org.geotoolkit.wms.xml.v111.Style)) {
                        throw new IllegalArgumentException("unexpected object version for style");
                    } else if (op != null) {
                        ops.add((org.geotoolkit.wms.xml.v111.Style)op);
                    }
                }
            }
            return new org.geotoolkit.wms.xml.v111.Layer(name, _abstract, keyword, crs, 
                                                         (org.geotoolkit.wms.xml.v111.LatLonBoundingBox)geobbox,
                                                         (org.geotoolkit.wms.xml.v111.BoundingBox) bbox,
                                                         queryable, dimensions, ops);
        } else if ("1.3.0".equals(currentVersion)) {
            if (geobbox != null && !(geobbox instanceof org.geotoolkit.wms.xml.v130.EXGeographicBoundingBox)) {
                throw new IllegalArgumentException("unexpected object version for geobbox");
            } 
            if (bbox != null && !(bbox instanceof org.geotoolkit.wms.xml.v130.BoundingBox)) {
                throw new IllegalArgumentException("unexpected object version for bbox");
            }
            final List<org.geotoolkit.wms.xml.v130.Style> ops = new ArrayList<org.geotoolkit.wms.xml.v130.Style>();
            if (styles != null) {
                for (Style op : styles) {
                    if (op != null && !(op instanceof org.geotoolkit.wms.xml.v130.Style)) {
                        throw new IllegalArgumentException("unexpected object version for style");
                    } else if (op != null) {
                        ops.add((org.geotoolkit.wms.xml.v130.Style)op);
                    }
                }
            }
            return new org.geotoolkit.wms.xml.v130.Layer(name, _abstract, keyword, crs, 
                                                         (org.geotoolkit.wms.xml.v130.EXGeographicBoundingBox)geobbox,
                                                         (org.geotoolkit.wms.xml.v130.BoundingBox) bbox,
                                                         queryable, dimensions, ops);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractLayer createLayer(final String currentVersion, final String title, final String _abstract, final List<String> crs, 
             final AbstractGeographicBoundingBox geobbox, final List<AbstractLayer> layers) {
        
        if ("1.1.1".equals(currentVersion)) {
            if (geobbox != null && !(geobbox instanceof org.geotoolkit.wms.xml.v111.LatLonBoundingBox)) {
                throw new IllegalArgumentException("unexpected object version for geobbox");
            } 
            
            return new org.geotoolkit.wms.xml.v111.Layer(title, _abstract, crs, 
                                                         (org.geotoolkit.wms.xml.v111.LatLonBoundingBox)geobbox,
                                                         layers);
        } else if ("1.3.0".equals(currentVersion)) {
            if (geobbox != null && !(geobbox instanceof org.geotoolkit.wms.xml.v130.EXGeographicBoundingBox)) {
                throw new IllegalArgumentException("unexpected object version for geobbox");
            } 
            return new org.geotoolkit.wms.xml.v130.Layer(title, _abstract, crs, 
                                                         (org.geotoolkit.wms.xml.v130.EXGeographicBoundingBox)geobbox,
                                                         layers);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
