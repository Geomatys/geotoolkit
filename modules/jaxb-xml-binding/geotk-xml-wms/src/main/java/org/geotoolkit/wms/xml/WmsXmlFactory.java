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

    public static AbstractKeywordList createKeyword(final String currentVersion, final List<String> keywords) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.KeywordList(keywords.toArray(new String[keywords.size()]));
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.KeywordList(keywords.toArray(new String[keywords.size()]));
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractContactAddress createContactAddress(final String currentVersion, final String addressType,
            final String address, final String city, final String stateOrProvince, final String postCode, final String country) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.ContactAddress(addressType, address, city, stateOrProvince, postCode, country);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.ContactAddress(addressType, address, city, stateOrProvince, postCode, country);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractContactPersonPrimary createContactPersonPrimary(final String currentVersion, final String contactPerson, final String contactOrganization) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.ContactPersonPrimary(contactPerson, contactOrganization);
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.ContactPersonPrimary(contactPerson, contactOrganization);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractContactInformation createContactInformation(final String currentVersion, final AbstractContactPersonPrimary contactPersonPrimary,
            final String contactPosition, final AbstractContactAddress contactAddress, final String contactVoiceTelephone, final String contactFacsimileTelephone,
            final String contactElectronicMailAddress) {
        if ("1.1.1".equals(currentVersion)) {
            if (contactAddress != null && !(contactAddress instanceof org.geotoolkit.wms.xml.v111.ContactAddress)) {
                throw new IllegalArgumentException("unexpected object version for contactAddress");
            }
            if (contactPersonPrimary != null && !(contactAddress instanceof org.geotoolkit.wms.xml.v111.ContactPersonPrimary)) {
                throw new IllegalArgumentException("unexpected object version for contactPersonPrimary");
            }
            return new org.geotoolkit.wms.xml.v111.ContactInformation((org.geotoolkit.wms.xml.v111.ContactPersonPrimary)contactPersonPrimary,
                                                                      contactPosition,
                                                                      (org.geotoolkit.wms.xml.v111.ContactAddress)contactAddress,
                                                                      contactVoiceTelephone,
                                                                      contactFacsimileTelephone,
                                                                      contactElectronicMailAddress);
        } else if ("1.3.0".equals(currentVersion)) {
            if (contactAddress != null && !(contactAddress instanceof org.geotoolkit.wms.xml.v130.ContactAddress)) {
                throw new IllegalArgumentException("unexpected object version for contactAddress");
            }
            if (contactPersonPrimary != null && !(contactAddress instanceof org.geotoolkit.wms.xml.v130.ContactPersonPrimary)) {
                throw new IllegalArgumentException("unexpected object version for contactPersonPrimary");
            }
            return new org.geotoolkit.wms.xml.v130.ContactInformation((org.geotoolkit.wms.xml.v130.ContactPersonPrimary)contactPersonPrimary,
                                                                      contactPosition,
                                                                      (org.geotoolkit.wms.xml.v130.ContactAddress)contactAddress,
                                                                      contactVoiceTelephone,
                                                                      contactFacsimileTelephone,
                                                                      contactElectronicMailAddress);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }


    public static AbstractService createService(final String currentVersion, final String name, final String title, final String _abstract,
            final AbstractKeywordList keywordList, final AbstractOnlineResource onlineResource,
            final AbstractContactInformation contactInformation, final String fees, final String accessConstraint,
            final int layerLimit, final int maxWidth, final int maxHeight) {
        if ("1.1.1".equals(currentVersion)) {
            if (keywordList != null && !(keywordList instanceof org.geotoolkit.wms.xml.v111.KeywordList)) {
                throw new IllegalArgumentException("unexpected object version for keywordList");
            }
            if (onlineResource != null && !(onlineResource instanceof org.geotoolkit.wms.xml.v111.OnlineResource)) {
                throw new IllegalArgumentException("unexpected object version for onlineResource");
            }
            if (contactInformation != null && !(contactInformation instanceof org.geotoolkit.wms.xml.v111.ContactInformation)) {
                throw new IllegalArgumentException("unexpected object version for contactInformation");
            }
            return new org.geotoolkit.wms.xml.v111.Service(name,
                                                           title,
                                                           _abstract,
                                                           (org.geotoolkit.wms.xml.v111.KeywordList)keywordList,
                                                           (org.geotoolkit.wms.xml.v111.OnlineResource)onlineResource,
                                                           (org.geotoolkit.wms.xml.v111.ContactInformation)contactInformation,
                                                           fees,
                                                           accessConstraint);
        } else if ("1.3.0".equals(currentVersion)) {
            if (keywordList != null && !(keywordList instanceof org.geotoolkit.wms.xml.v130.KeywordList)) {
                throw new IllegalArgumentException("unexpected object version for keywordList");
            }
            if (onlineResource != null && !(onlineResource instanceof org.geotoolkit.wms.xml.v130.OnlineResource)) {
                throw new IllegalArgumentException("unexpected object version for onlineResource");
            }
            if (contactInformation != null && !(contactInformation instanceof org.geotoolkit.wms.xml.v130.ContactInformation)) {
                throw new IllegalArgumentException("unexpected object version for contactInformation");
            }
            return new org.geotoolkit.wms.xml.v130.Service(name,
                                                           title,
                                                           _abstract,
                                                           (org.geotoolkit.wms.xml.v130.KeywordList)keywordList,
                                                           (org.geotoolkit.wms.xml.v130.OnlineResource)onlineResource,
                                                           (org.geotoolkit.wms.xml.v130.ContactInformation)contactInformation,
                                                           fees,
                                                           accessConstraint,
                                                           layerLimit,
                                                           maxWidth,
                                                           maxHeight);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractCapability createCapability(final String currentVersion) {
        if ("1.1.1".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v111.Capability();
        } else if ("1.3.0".equals(currentVersion)) {
            return new org.geotoolkit.wms.xml.v130.Capability();
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractWMSCapabilities createCapabilities(final String currentVersion, final AbstractService service,
            final AbstractCapability capability, final String updateSequence) {
        
        if ("1.1.1".equals(currentVersion)) {
            if (service != null && !(service instanceof org.geotoolkit.wms.xml.v111.Service)) {
                throw new IllegalArgumentException("unexpected object version for service");
            }
            if (capability != null && !(capability instanceof org.geotoolkit.wms.xml.v111.Capability)) {
                throw new IllegalArgumentException("unexpected object version for capability");
            }
            return new org.geotoolkit.wms.xml.v111.WMT_MS_Capabilities((org.geotoolkit.wms.xml.v111.Service)service,
                                                                       (org.geotoolkit.wms.xml.v111.Capability)capability,
                                                                       currentVersion,
                                                                       updateSequence);
        } else if ("1.3.0".equals(currentVersion)) {
            if (service != null && !(service instanceof org.geotoolkit.wms.xml.v130.Service)) {
                throw new IllegalArgumentException("unexpected object version for service");
            }
            if (capability != null && !(capability instanceof org.geotoolkit.wms.xml.v130.Capability)) {
                throw new IllegalArgumentException("unexpected object version for capability");
            }
            return new org.geotoolkit.wms.xml.v130.WMSCapabilities((org.geotoolkit.wms.xml.v130.Service)service,
                                                                   (org.geotoolkit.wms.xml.v130.Capability)capability,
                                                                   currentVersion,
                                                                   updateSequence);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
