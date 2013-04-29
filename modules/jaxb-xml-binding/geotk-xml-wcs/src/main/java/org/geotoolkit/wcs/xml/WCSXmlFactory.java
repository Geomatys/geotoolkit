/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.wcs.xml;

import java.util.List;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.Sections;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WCSXmlFactory {

    public static DescribeCoverage createDescribeCoverage(final String version, final List<String> coverage) {
        if ("1.1.1".equals(version)) {
            return new org.geotoolkit.wcs.xml.v111.DescribeCoverageType(coverage);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wcs.xml.v100.DescribeCoverageType(coverage);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wcs.xml.v200.DescribeCoverageType(coverage);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static GetCoverage createGetCoverage(final String version, final String coverage, final DomainSubset ds, final RangeSubset rs,
            final String interpolationMethod, final Output out) {
        if ("1.1.1".equals(version)) {
            if (ds != null && !(ds instanceof org.geotoolkit.wcs.xml.v111.DomainSubsetType)) {
                throw new IllegalArgumentException("unexpected object version for DomainSubset element");
            }
            if (rs != null && !(rs instanceof org.geotoolkit.wcs.xml.v111.RangeSubsetType)) {
                throw new IllegalArgumentException("unexpected object version for RangeSubset element");
            }
            if (out != null && !(out instanceof org.geotoolkit.wcs.xml.v111.OutputType)) {
                throw new IllegalArgumentException("unexpected object version for Output element");
            }
            org.geotoolkit.ows.xml.v110.CodeType c = null;
            if (coverage != null) {
                c = new org.geotoolkit.ows.xml.v110.CodeType(coverage);
            }
            return new org.geotoolkit.wcs.xml.v111.GetCoverageType(c, 
                                                                   (org.geotoolkit.wcs.xml.v111.DomainSubsetType)ds,
                                                                   (org.geotoolkit.wcs.xml.v111.RangeSubsetType)rs,
                                                                   (org.geotoolkit.wcs.xml.v111.OutputType)out);
        } else if ("1.0.0".equals(version)) {
            if (ds != null && !(ds instanceof org.geotoolkit.wcs.xml.v100.DomainSubsetType)) {
                throw new IllegalArgumentException("unexpected object version for DomainSubset element");
            }
            if (rs != null && !(rs instanceof org.geotoolkit.wcs.xml.v111.RangeSubsetType)) {
                throw new IllegalArgumentException("unexpected object version for RangeSubset element");
            }
            if (out != null && !(out instanceof org.geotoolkit.wcs.xml.v100.OutputType)) {
                throw new IllegalArgumentException("unexpected object version for Output element");
            }
            return new org.geotoolkit.wcs.xml.v100.GetCoverageType(coverage,
                                                                   (org.geotoolkit.wcs.xml.v100.DomainSubsetType)ds,
                                                                   (org.geotoolkit.wcs.xml.v100.RangeSubsetType)rs,
                                                                   interpolationMethod,
                                                                   (org.geotoolkit.wcs.xml.v100.OutputType)out);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static DomainSubset createDomainSubset(final String version, final TimeSequence time, final Object spatial) {
        if ("1.1.1".equals(version)) {
            if (time != null && !(time instanceof org.geotoolkit.wcs.xml.v111.TimeSequenceType)) {
                throw new IllegalArgumentException("unexpected object version for time element");
            }
            if (spatial != null && !(spatial instanceof org.geotoolkit.ows.xml.v110.BoundingBoxType)) {
                throw new IllegalArgumentException("unexpected object version for ServiceIdentification element");
            }
            return new org.geotoolkit.wcs.xml.v111.DomainSubsetType((org.geotoolkit.wcs.xml.v111.TimeSequenceType)time, 
                                                                    (org.geotoolkit.ows.xml.v110.BoundingBoxType)spatial);
        } else if ("1.0.0".equals(version)) {
            if (time != null && !(time instanceof org.geotoolkit.wcs.xml.v100.TimeSequenceType)) {
                throw new IllegalArgumentException("unexpected object version for time element");
            }
            if (spatial != null && !(spatial instanceof org.geotoolkit.wcs.xml.v100.SpatialSubsetType)) {
                throw new IllegalArgumentException("unexpected object version for ServiceIdentification element");
            }
            return new org.geotoolkit.wcs.xml.v100.DomainSubsetType((org.geotoolkit.wcs.xml.v100.TimeSequenceType)time, 
                                                                    (org.geotoolkit.wcs.xml.v100.SpatialSubsetType)spatial);
        } else if ("2.0.0".equals(version)) {
            throw new IllegalArgumentException("There is no timeSequence in v200");
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static TimeSequence createTimeSequence(final String version, final String timePosition) {
        if ("1.1.1".equals(version)) {
            return new org.geotoolkit.wcs.xml.v111.TimeSequenceType(timePosition);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wcs.xml.v100.TimeSequenceType(timePosition);
        } else if ("2.0.0".equals(version)) {
            throw new IllegalArgumentException("There is no timeSequence in v200");
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static GetCapabilitiesResponse createCapabilitiesResponse(final String version, final String updateSequence) {
        if ("1.1.1".equals(version)) {
            return new org.geotoolkit.wcs.xml.v111.Capabilities(version, updateSequence);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wcs.xml.v100.WCSCapabilitiesType(updateSequence);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wcs.xml.v200.CapabilitiesType(version, updateSequence);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static GetCapabilitiesResponse createCapabilitiesResponse(final String version, final AbstractServiceIdentification si,
            final AbstractServiceProvider sp, final AbstractOperationsMetadata om, final Content cont, final String updateSequence) {
        if ("1.1.1".equals(version) || "1.0.0".equals(version)) {
            if (si != null && !(si instanceof org.geotoolkit.ows.xml.v110.ServiceIdentification)) {
                throw new IllegalArgumentException("unexpected object version for ServiceIdentification element");
            }
            if (sp != null && !(sp instanceof org.geotoolkit.ows.xml.v110.ServiceProvider)) {
                throw new IllegalArgumentException("unexpected object version for ServiceProvider element");
            }
            if (om != null && !(om instanceof org.geotoolkit.ows.xml.v110.OperationsMetadata)) {
                throw new IllegalArgumentException("unexpected object version for OperationsMetadata element");
            }
        } else if ("2.0.0".equals(version)) {
            if (si != null && !(si instanceof org.geotoolkit.ows.xml.v200.ServiceIdentification)) {
            throw new IllegalArgumentException("unexpected object version for ServiceIdentification element");
            }
            if (sp != null && !(sp instanceof org.geotoolkit.ows.xml.v200.ServiceProvider)) {
                throw new IllegalArgumentException("unexpected object version for ServiceProvider element");
            }
            if (om != null && !(om instanceof org.geotoolkit.ows.xml.v200.OperationsMetadata)) {
                throw new IllegalArgumentException("unexpected object version for OperationsMetadata element");
            }
        }
        
        if ("1.1.1".equals(version)) {
            if (cont != null && !(cont instanceof org.geotoolkit.wcs.xml.v111.Contents)) {
                throw new IllegalArgumentException("unexpected object version for Contents element");
            }
            return new org.geotoolkit.wcs.xml.v111.Capabilities((org.geotoolkit.ows.xml.v110.ServiceIdentification)si,
                                                                (org.geotoolkit.ows.xml.v110.ServiceProvider)sp,
                                                                (org.geotoolkit.ows.xml.v110.OperationsMetadata)om,
                                                                version, updateSequence,
                                                                (org.geotoolkit.wcs.xml.v111.Contents)cont);
        } else if ("1.0.0".equals(version)) {
            if (cont != null && !(cont instanceof org.geotoolkit.wcs.xml.v100.ContentMetadata)) {
                throw new IllegalArgumentException("unexpected object version for Contents element");
            }
            return new org.geotoolkit.wcs.xml.v100.WCSCapabilitiesType(null,
                                                                       null,
                                                                       (org.geotoolkit.wcs.xml.v100.ContentMetadata)cont,
                                                                       updateSequence);
        } else if ("2.0.0".equals(version)) {
            if (cont != null && !(cont instanceof org.geotoolkit.wcs.xml.v200.ContentsType)) {
                throw new IllegalArgumentException("unexpected object version for Contents element");
            }
            return new org.geotoolkit.wcs.xml.v200.CapabilitiesType((org.geotoolkit.ows.xml.v200.ServiceIdentification)si,
                                                                    (org.geotoolkit.ows.xml.v200.ServiceProvider)sp,
                                                                    (org.geotoolkit.ows.xml.v200.OperationsMetadata)om,
                                                                    version, updateSequence,
                                                                    (org.geotoolkit.wcs.xml.v200.ContentsType)cont);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static GetCapabilities createGetCapabilities(final String version, final AcceptVersions versions, final Sections sections,
            final AcceptFormats formats, final String updateSequence, final String service) {
        if ("1.1.1".equals(version)) {
            if (versions != null && !(versions instanceof org.geotoolkit.ows.xml.v110.AcceptVersionsType)) {
                throw new IllegalArgumentException("unexpected object version for AcceptVersion element");
            }
            if (sections != null && !(sections instanceof org.geotoolkit.ows.xml.v110.SectionsType)) {
                throw new IllegalArgumentException("unexpected object version for Sections element");
            }
            if (formats != null && !(formats instanceof org.geotoolkit.ows.xml.v110.AcceptFormatsType)) {
                throw new IllegalArgumentException("unexpected object version for AcceptFormat element");
            }
            return new org.geotoolkit.wcs.xml.v111.GetCapabilitiesType((org.geotoolkit.ows.xml.v110.AcceptVersionsType)versions,
                                                                       (org.geotoolkit.ows.xml.v110.SectionsType)sections,
                                                                       (org.geotoolkit.ows.xml.v110.AcceptFormatsType)formats,
                                                                       updateSequence,
                                                                       service);
        } else if ("1.0.0".equals(version)) {
            String section = "/";
            if (sections != null && !sections.getSection().isEmpty()) {
                section = sections.getSection().get(0);
            }
            return new org.geotoolkit.wcs.xml.v100.GetCapabilitiesType(version, service, section, updateSequence);
        } else if ("2.0.0".equals(version)) {
            if (versions != null && !(versions instanceof org.geotoolkit.ows.xml.v200.AcceptVersionsType)) {
                throw new IllegalArgumentException("unexpected object version for AcceptVersion element");
            }
            if (sections != null && !(sections instanceof org.geotoolkit.ows.xml.v200.SectionsType)) {
                throw new IllegalArgumentException("unexpected object version for Sections element");
            }
            if (formats != null && !(formats instanceof org.geotoolkit.ows.xml.v200.AcceptFormatsType)) {
                throw new IllegalArgumentException("unexpected object version for AcceptFormat element");
            }
            return new org.geotoolkit.wcs.xml.v200.GetCapabilitiesType((org.geotoolkit.ows.xml.v200.AcceptVersionsType)versions,
                                                                       (org.geotoolkit.ows.xml.v200.SectionsType)sections,
                                                                       (org.geotoolkit.ows.xml.v200.AcceptFormatsType)formats,
                                                                       updateSequence,
                                                                       service);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
}
