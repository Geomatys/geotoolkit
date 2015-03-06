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

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.AbstractContact;
import org.geotoolkit.ows.xml.AbstractDCP;
import org.geotoolkit.ows.xml.AbstractDomain;
import org.geotoolkit.ows.xml.AbstractOnlineResourceType;
import org.geotoolkit.ows.xml.AbstractOperation;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractResponsiblePartySubset;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.OWSXmlFactory;
import org.geotoolkit.ows.xml.Sections;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WCSXmlFactory {

    public static CoverageInfo createCoverageInfo(final String version, final String identifier, final String title, 
            final String _abstract, final Object bbox, final String coverageSubType) {
        if ("1.1.1".equals(version)) {
            if (bbox != null && !(bbox instanceof org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType)) {
                throw new IllegalArgumentException("unexpected object version for bbox element");
            }
            return new org.geotoolkit.wcs.xml.v111.CoverageSummaryType(identifier, title, _abstract, 
                                                                      (org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType)bbox);
        } else if ("1.0.0".equals(version)) {
            if (bbox != null && !(bbox instanceof org.geotoolkit.wcs.xml.v100.LonLatEnvelopeType)) {
                throw new IllegalArgumentException("unexpected object version for bbox element");
            }
            return new org.geotoolkit.wcs.xml.v100.CoverageOfferingBriefType(identifier, title, _abstract, 
                                                                      (org.geotoolkit.wcs.xml.v100.LonLatEnvelopeType)bbox);
        } else if ("2.0.0".equals(version)) {
            if (bbox != null && !(bbox instanceof org.geotoolkit.ows.xml.v200.WGS84BoundingBoxType)) {
                throw new IllegalArgumentException("unexpected object version for bbox element");
            }
            QName coverageSB = null;
            if (coverageSubType != null) {
                coverageSB = new QName(coverageSubType);
            }
            
            return new org.geotoolkit.wcs.xml.v200.CoverageSummaryType(identifier, title, _abstract, 
                                                                      (org.geotoolkit.ows.xml.v200.WGS84BoundingBoxType)bbox,
                                                                       coverageSB);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static Content createContent(final String version, final List<CoverageInfo> coverageinfos) {
        if ("1.1.1".equals(version)) {
            final List<org.geotoolkit.wcs.xml.v111.CoverageSummaryType> cov111 = new ArrayList<>();
            for (CoverageInfo c : coverageinfos) {
                if (!(c instanceof org.geotoolkit.wcs.xml.v111.CoverageSummaryType)) {
                    throw new IllegalArgumentException("unexpected object version for coverageInfo element");
                }
                cov111.add((org.geotoolkit.wcs.xml.v111.CoverageSummaryType)c);
            }
            return new org.geotoolkit.wcs.xml.v111.Contents(cov111, null, null, null);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.wcs.xml.v100.CoverageOfferingBriefType> cov100 = new ArrayList<>();
            for (CoverageInfo c : coverageinfos) {
                if (!(c instanceof org.geotoolkit.wcs.xml.v100.CoverageOfferingBriefType)) {
                    throw new IllegalArgumentException("unexpected object version for coverageInfo element");
                }
                cov100.add((org.geotoolkit.wcs.xml.v100.CoverageOfferingBriefType)c);
            }
            return new org.geotoolkit.wcs.xml.v100.ContentMetadata(cov100);
        } else if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.wcs.xml.v200.CoverageSummaryType> cov200 = new ArrayList<>();
            for (CoverageInfo c : coverageinfos) {
                if (!(c instanceof org.geotoolkit.wcs.xml.v200.CoverageSummaryType)) {
                    throw new IllegalArgumentException("unexpected object version for coverageInfo element");
                }
                cov200.add((org.geotoolkit.wcs.xml.v200.CoverageSummaryType)c);
            }
            return new org.geotoolkit.wcs.xml.v200.ContentsType(cov200);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
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
    
    public static DescribeCoverageResponse createDescribeCoverageResponse(final String version, final List<CoverageInfo> coverageinfos) {
        if ("1.1.1".equals(version)) {
            final List<org.geotoolkit.wcs.xml.v111.CoverageDescriptionType> cov111 = new ArrayList<>();
            for (CoverageInfo c : coverageinfos) {
                if (!(c instanceof org.geotoolkit.wcs.xml.v111.CoverageDescriptionType)) {
                    throw new IllegalArgumentException("unexpected object version for coverageInfo element");
                }
                cov111.add((org.geotoolkit.wcs.xml.v111.CoverageDescriptionType)c);
            }
            return new org.geotoolkit.wcs.xml.v111.CoverageDescriptions(cov111);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.wcs.xml.v100.CoverageOfferingType> cov100 = new ArrayList<>();
            for (CoverageInfo c : coverageinfos) {
                if (!(c instanceof org.geotoolkit.wcs.xml.v100.CoverageOfferingType)) {
                    throw new IllegalArgumentException("unexpected object version for coverageInfo element");
                }
                cov100.add((org.geotoolkit.wcs.xml.v100.CoverageOfferingType)c);
            }
            return new org.geotoolkit.wcs.xml.v100.CoverageDescription(cov100, "1.0.0");
        } else if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.wcs.xml.v200.CoverageDescriptionType> cov200 = new ArrayList<>();
            for (CoverageInfo c : coverageinfos) {
                if (!(c instanceof org.geotoolkit.wcs.xml.v200.CoverageDescriptionType)) {
                    throw new IllegalArgumentException("unexpected object version for coverageInfo element");
                }
                cov200.add((org.geotoolkit.wcs.xml.v200.CoverageDescriptionType)c);
            }
            return new org.geotoolkit.wcs.xml.v200.CoverageDescriptionsType(cov200);
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
        } else if ("2.0.0".equals(version)) {
            
            
            return new org.geotoolkit.wcs.xml.v200.GetCoverageType(coverage, null, null);
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

    public static AbstractServiceIdentification createServiceIdentification(final String version, final String title, final String _abstract,
            final List<String> keywords, final String serviceType, final List<String> serviceVersion, final String fees, final List<String> accessConstraint) {
        if ("1.1.1".equals(version)) {
            return OWSXmlFactory.buildServiceIdentification("1.1.0", title, _abstract, keywords, serviceType, serviceVersion, fees, accessConstraint);
        } else if ("1.0.0".equals(version)) {
            final org.geotoolkit.wcs.xml.v100.Keywords kw = new org.geotoolkit.wcs.xml.v100.Keywords(keywords);
            final org.geotoolkit.gml.xml.v311.CodeListType f = new org.geotoolkit.gml.xml.v311.CodeListType(fees);
            final org.geotoolkit.gml.xml.v311.CodeListType ac = new org.geotoolkit.gml.xml.v311.CodeListType(accessConstraint);
            return new org.geotoolkit.wcs.xml.v100.ServiceType(null, title, title, _abstract, kw, null, f, ac, fees);
        } else if ("2.0.0".equals(version)) {
            return OWSXmlFactory.buildServiceIdentification("2.0.0", title, _abstract, keywords, serviceType, serviceVersion, fees, accessConstraint);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static AbstractOnlineResourceType buildOnlineResource(final String currentVersion, final String url) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildOnlineResource("2.0.0", url);
        } else if ("1.0.0".equals(currentVersion)) {
            return null;
        } else if ("1.1.1".equals(currentVersion)) {
            return OWSXmlFactory.buildOnlineResource("1.1.0", url);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractServiceProvider buildServiceProvider(final String currentVersion, final String providerName,
            final AbstractOnlineResourceType providerSite, final AbstractResponsiblePartySubset serviceContact) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildServiceProvider("2.0.0", providerName, providerSite, serviceContact);
        } else if ("1.0.0".equals(currentVersion)) {
            return null;
        } else if ("1.1.1".equals(currentVersion)) {
            return OWSXmlFactory.buildServiceProvider("1.1.0", providerName, providerSite, serviceContact);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractContact buildContact(final String currentVersion, final String phone, final String fax, final String email,
            final String address, final String city, final String state, final String zipCode, final String country,
            final String hoursOfService, final String contactInstructions) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildContact("2.0.0", phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
        } else if ("1.1.1".equals(currentVersion)) {
            return OWSXmlFactory.buildContact("1.1.0", phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.wcs.xml.v100.ContactType(phone, fax, email, address, city, state, zipCode, country);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractResponsiblePartySubset buildResponsiblePartySubset(final String currentVersion, final String individualName, final String positionName,
            final AbstractContact contact, final String role) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildResponsiblePartySubset("2.0.0", individualName, positionName, contact, role);
        } else if ("1.1.1".equals(currentVersion)) {
            return OWSXmlFactory.buildResponsiblePartySubset("1.1.0", individualName, positionName, contact, role);
        } else if ("1.0.0".equals(currentVersion)) {
            if (contact != null && !(contact instanceof org.geotoolkit.wcs.xml.v100.ContactType)) {
                throw new IllegalArgumentException("unexpected object version for contact element");
            }
            return new org.geotoolkit.wcs.xml.v100.ResponsiblePartyType(individualName, positionName, null, (org.geotoolkit.wcs.xml.v100.ContactType)contact);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractDomain buildDomain(final String currentVersion, final String name, final List<String> allowedValues) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildDomain("2.0.0", name, allowedValues);
        } else if ("1.1.1".equals(currentVersion)) {
            return OWSXmlFactory.buildDomain("1.1.0", name, allowedValues);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractOperation buildOperation(final String currentVersion, final List<AbstractDCP> dcps,
            final List<AbstractDomain> parameters, final List<AbstractDomain> constraints, final String name) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildOperation("2.0.0", dcps, parameters, constraints, name);
        } else if ("1.1.1".equals(currentVersion)) {
            return OWSXmlFactory.buildOperation("1.1.0", dcps, parameters, constraints, name);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractDCP buildDCP(final String currentVersion, final String getURL, final String postURL) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildDCP("2.0.0", getURL, postURL);
        } else if ("1.1.1".equals(currentVersion)) {
            return OWSXmlFactory.buildDCP("1.1.0", getURL, postURL);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static GetCapabilitiesResponse createCapabilitiesResponse(final String version, final AbstractServiceIdentification si,
            final AbstractServiceProvider sp, final AbstractOperationsMetadata om, final Content cont, final String updateSequence) {
        
        if ("1.1.1".equals(version)) {
            if (si != null && !(si instanceof org.geotoolkit.ows.xml.v110.ServiceIdentification)) {
                throw new IllegalArgumentException("unexpected object version for ServiceIdentification element");
            }
            if (sp != null && !(sp instanceof org.geotoolkit.ows.xml.v110.ServiceProvider)) {
                throw new IllegalArgumentException("unexpected object version for ServiceProvider element");
            }
            if (om != null && !(om instanceof org.geotoolkit.ows.xml.v110.OperationsMetadata)) {
                throw new IllegalArgumentException("unexpected object version for OperationsMetadata element");
            }
            if (cont != null && !(cont instanceof org.geotoolkit.wcs.xml.v111.Contents)) {
                throw new IllegalArgumentException("unexpected object version for Contents element");
            }
            return new org.geotoolkit.wcs.xml.v111.Capabilities((org.geotoolkit.ows.xml.v110.ServiceIdentification)si,
                                                                (org.geotoolkit.ows.xml.v110.ServiceProvider)sp,
                                                                (org.geotoolkit.ows.xml.v110.OperationsMetadata)om,
                                                                version, updateSequence,
                                                                (org.geotoolkit.wcs.xml.v111.Contents)cont);
        } else if ("1.0.0".equals(version)) {
            if (si != null && !(si instanceof org.geotoolkit.wcs.xml.v100.ServiceType)) {
                throw new IllegalArgumentException("unexpected object version for ServiceIdentification element");
            }
            if (om != null && !(om instanceof org.geotoolkit.wcs.xml.v100.WCSCapabilityType)) {
                throw new IllegalArgumentException("unexpected object version for ServiceIdentification element");
            }
            if (cont != null && !(cont instanceof org.geotoolkit.wcs.xml.v100.ContentMetadata)) {
                throw new IllegalArgumentException("unexpected object version for Contents element");
            }
            return new org.geotoolkit.wcs.xml.v100.WCSCapabilitiesType((org.geotoolkit.wcs.xml.v100.ServiceType)si,
                                                                       (org.geotoolkit.wcs.xml.v100.WCSCapabilityType)om,
                                                                       (org.geotoolkit.wcs.xml.v100.ContentMetadata)cont,
                                                                       updateSequence);
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
