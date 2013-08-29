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
package org.geotoolkit.ows.xml;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.inspire.xml.MultiLingualCapabilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OWSXmlFactory {
    
    
    public static AcceptVersions buildAcceptVersion(final String currentVersion, final List<String> acceptVersion) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.AcceptVersionsType(acceptVersion);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.AcceptVersionsType(acceptVersion);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.AcceptVersionsType(acceptVersion);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AcceptFormats buildAcceptFormat(final String currentVersion, final List<String> acceptformats) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.AcceptFormatsType(acceptformats);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.AcceptFormatsType(acceptformats);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.AcceptFormatsType(acceptformats);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static Sections buildSections(final String currentVersion, final List<String> sections) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.SectionsType(sections);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.SectionsType(sections);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.SectionsType(sections);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractOperation buildOperation(final String currentVersion, final List<AbstractDCP> dcps,
            final List<AbstractDomain> parameters, final List<AbstractDomain> constraints, final String name) {
        if ("1.1.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v110.DCP> dcp110 = new ArrayList<>();
            if (dcps != null) {
                for (AbstractDCP dcp : dcps) {
                    if (dcp != null && !(dcp instanceof org.geotoolkit.ows.xml.v110.DCP)) {
                        throw new IllegalArgumentException("unexpected object version for dcps");
                    } else if (dcp != null) {
                        dcp110.add((org.geotoolkit.ows.xml.v110.DCP)dcp);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v110.DomainType> param110 = new ArrayList<>();
            if (parameters != null) {
                for (AbstractDomain dom : parameters) {
                    if (dom != null && !(dom instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for domains");
                    } else if (dom != null) {
                        param110.add((org.geotoolkit.ows.xml.v110.DomainType)dom);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v110.DomainType> const110 = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain dom : constraints) {
                    if (dom != null && !(dom instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for domains");
                    } else if (dom != null) {
                        const110.add((org.geotoolkit.ows.xml.v110.DomainType)dom);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v110.Operation(dcp110, param110, const110, null, name);
        } else if ("1.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v100.DCP> dcp100 = new ArrayList<>();
            if (dcps != null) {
                for (AbstractDCP dcp : dcps) {
                    if (dcp != null && !(dcp instanceof org.geotoolkit.ows.xml.v100.DCP)) {
                        throw new IllegalArgumentException("unexpected object version for dcps");
                    } else if (dcp != null) {
                        dcp100.add((org.geotoolkit.ows.xml.v100.DCP)dcp);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v100.DomainType> param100 = new ArrayList<>();
            if (parameters != null) {
                for (AbstractDomain dom : parameters) {
                    if (dom != null && !(dom instanceof org.geotoolkit.ows.xml.v100.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for domains");
                    } else if (dom != null) {
                        param100.add((org.geotoolkit.ows.xml.v100.DomainType)dom);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v100.DomainType> const100 = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain dom : constraints) {
                    if (dom != null && !(dom instanceof org.geotoolkit.ows.xml.v100.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for domains");
                    } else if (dom != null) {
                        const100.add((org.geotoolkit.ows.xml.v100.DomainType)dom);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v100.Operation(dcp100, param100, const100, null, name);
        } else if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v200.DCP> dcp200 = new ArrayList<>();
            if (dcps != null) {
                for (AbstractDCP dcp : dcps) {
                    if (dcp != null && !(dcp instanceof org.geotoolkit.ows.xml.v200.DCP)) {
                        throw new IllegalArgumentException("unexpected object version for dcps");
                    } else if (dcp != null) {
                        dcp200.add((org.geotoolkit.ows.xml.v200.DCP)dcp);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v200.DomainType> param200 = new ArrayList<>();
            if (parameters != null) {
                for (AbstractDomain dom : parameters) {
                    if (dom != null && !(dom instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for domains");
                    } else if (dom != null) {
                        param200.add((org.geotoolkit.ows.xml.v200.DomainType)dom);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v200.DomainType> const200 = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain dom : constraints) {
                    if (dom != null && !(dom instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for domains");
                    } else if (dom != null) {
                        const200.add((org.geotoolkit.ows.xml.v200.DomainType)dom);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v200.Operation(dcp200, param200, const200, null, name);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractServiceIdentification buildServiceIdentification(final String currentVersion, final String title, final String _abstract,
            final List<String> keywords, final String serviceType, final List<String> serviceVersion, final String fees, final List<String> accessConstraint) {
        if ("1.1.0".equals(currentVersion)) {
            final org.geotoolkit.ows.xml.v110.LanguageStringType titlee = new org.geotoolkit.ows.xml.v110.LanguageStringType(title);
            final org.geotoolkit.ows.xml.v110.LanguageStringType abs = new org.geotoolkit.ows.xml.v110.LanguageStringType(_abstract);
            final org.geotoolkit.ows.xml.v110.KeywordsType kw = new org.geotoolkit.ows.xml.v110.KeywordsType(keywords);
            final org.geotoolkit.ows.xml.v110.CodeType st = new org.geotoolkit.ows.xml.v110.CodeType(serviceType);
            return new org.geotoolkit.ows.xml.v110.ServiceIdentification(titlee, abs, kw, st, serviceVersion, fees, accessConstraint);
        } else if ("1.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v100.KeywordsType> kw = new ArrayList<>();
            if (keywords != null) {
                for (String k : keywords) {
                    kw.add(new org.geotoolkit.ows.xml.v100.KeywordsType(k));
                }
            }
            final org.geotoolkit.ows.xml.v100.CodeType st = new org.geotoolkit.ows.xml.v100.CodeType(serviceType);
            return new org.geotoolkit.ows.xml.v100.ServiceIdentification(title, _abstract, kw, st, serviceVersion, fees, accessConstraint);
        } else if ("2.0.0".equals(currentVersion)) {
            final org.geotoolkit.ows.xml.v200.LanguageStringType titlee = new org.geotoolkit.ows.xml.v200.LanguageStringType(title);
            final org.geotoolkit.ows.xml.v200.LanguageStringType abs = new org.geotoolkit.ows.xml.v200.LanguageStringType(_abstract);
            final org.geotoolkit.ows.xml.v200.KeywordsType kw = new org.geotoolkit.ows.xml.v200.KeywordsType(keywords);
            final org.geotoolkit.ows.xml.v200.CodeType st = new org.geotoolkit.ows.xml.v200.CodeType(serviceType);
            return new org.geotoolkit.ows.xml.v200.ServiceIdentification(titlee, abs, kw, st, serviceVersion, fees, accessConstraint);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractContact buildContact(final String currentVersion, final String phone, final String fax, final String email,
            final String address, final String city, final String state,
            final String zipCode, final String country) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.ContactType(phone, fax, email, address, city, state, zipCode, country);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.ContactType(phone, fax, email, address, city, state, zipCode, country);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.ContactType(phone, fax, email, address, city, state, zipCode, country);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractResponsiblePartySubset buildResponsiblePartySubset(final String currentVersion, final String individualName, final String positionName,
            final AbstractContact contact, final String role) {
        if ("1.1.0".equals(currentVersion)) {
            if (contact != null && !(contact instanceof org.geotoolkit.ows.xml.v110.ContactType)) {
                throw new IllegalArgumentException("unexpected object version for contact element");
            }
            final org.geotoolkit.ows.xml.v110.CodeType r = new org.geotoolkit.ows.xml.v110.CodeType(role);
            return new org.geotoolkit.ows.xml.v110.ResponsiblePartySubsetType(individualName, positionName, 
                                                                              (org.geotoolkit.ows.xml.v110.ContactType)contact,
                                                                              r);
        } else if ("1.0.0".equals(currentVersion)) {
            if (contact != null && !(contact instanceof org.geotoolkit.ows.xml.v100.ContactType)) {
                throw new IllegalArgumentException("unexpected object version for contact element");
            }
            final org.geotoolkit.ows.xml.v100.CodeType r = new org.geotoolkit.ows.xml.v100.CodeType(role);
            return new org.geotoolkit.ows.xml.v100.ResponsiblePartySubsetType(individualName, positionName,
                                                                              (org.geotoolkit.ows.xml.v100.ContactType)contact,
                                                                              r);
        } else if ("2.0.0".equals(currentVersion)) {
            if (contact != null && !(contact instanceof org.geotoolkit.ows.xml.v200.ContactType)) {
                throw new IllegalArgumentException("unexpected object version for contact element");
            }
            final org.geotoolkit.ows.xml.v200.CodeType r = new org.geotoolkit.ows.xml.v200.CodeType(role);
            return new org.geotoolkit.ows.xml.v200.ResponsiblePartySubsetType(individualName, positionName,
                                                                              (org.geotoolkit.ows.xml.v200.ContactType)contact,
                                                                              r);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractServiceProvider buildServiceProvider(final String currentVersion, final String providerName,
            final AbstractOnlineResourceType providerSite, final AbstractResponsiblePartySubset serviceContact) {
        if ("1.1.0".equals(currentVersion)) {
            if (providerSite != null && !(providerSite instanceof org.geotoolkit.ows.xml.v110.OnlineResourceType)) {
                throw new IllegalArgumentException("unexpected object version for providerSite element");
            }
            if (serviceContact != null && !(serviceContact instanceof org.geotoolkit.ows.xml.v110.ResponsiblePartySubsetType)) {
                throw new IllegalArgumentException("unexpected object version for serviceContact element");
            }
            return new org.geotoolkit.ows.xml.v110.ServiceProvider(providerName,
                                                                   (org.geotoolkit.ows.xml.v110.OnlineResourceType)providerSite,
                                                                   (org.geotoolkit.ows.xml.v110.ResponsiblePartySubsetType)serviceContact);
        } else if ("1.0.0".equals(currentVersion)) {
            if (providerSite != null && !(providerSite instanceof org.geotoolkit.ows.xml.v100.OnlineResourceType)) {
                throw new IllegalArgumentException("unexpected object version for providerSite element");
            }
            if (serviceContact != null && !(serviceContact instanceof org.geotoolkit.ows.xml.v100.ResponsiblePartySubsetType)) {
                throw new IllegalArgumentException("unexpected object version for serviceContact element");
            }
            return new org.geotoolkit.ows.xml.v100.ServiceProvider(providerName,
                                                                   (org.geotoolkit.ows.xml.v100.OnlineResourceType)providerSite,
                                                                   (org.geotoolkit.ows.xml.v100.ResponsiblePartySubsetType)serviceContact);
        } else if ("2.0.0".equals(currentVersion)) {
            if (providerSite != null && !(providerSite instanceof org.geotoolkit.ows.xml.v200.OnlineResourceType)) {
                throw new IllegalArgumentException("unexpected object version for providerSite element");
            }
            if (serviceContact != null && !(serviceContact instanceof org.geotoolkit.ows.xml.v200.ResponsiblePartySubsetType)) {
                throw new IllegalArgumentException("unexpected object version for serviceContact element");
            }
            return new org.geotoolkit.ows.xml.v200.ServiceProvider(providerName,
                                                                   (org.geotoolkit.ows.xml.v200.OnlineResourceType)providerSite,
                                                                   (org.geotoolkit.ows.xml.v200.ResponsiblePartySubsetType)serviceContact);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractOperationsMetadata buildOperationsMetadata(final String currentVersion, final List<AbstractOperation> operations, final List<AbstractDomain> parameters,
             final List<AbstractDomain> constraints, final Object extendedCapabilities) {
        if ("1.1.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v110.Operation> ops = new ArrayList<>();
            if (operations != null) {
                for (AbstractOperation op : operations) {
                    if (op != null && !(op instanceof org.geotoolkit.ows.xml.v110.Operation)) {
                        throw new IllegalArgumentException("unexpected object version for operation");
                    } else if (op != null) {
                        ops.add((org.geotoolkit.ows.xml.v110.Operation)op);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v110.DomainType> params = new ArrayList<>();
            if (parameters != null) {
                for (AbstractDomain param : parameters) {
                    if (param != null && !(param instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for parameter");
                    } else if (param != null) {
                        params.add((org.geotoolkit.ows.xml.v110.DomainType)param);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v110.DomainType> consts = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain constr : constraints) {
                    if (constr != null && !(constr instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for constraint");
                    } else if (constr != null) {
                        consts.add((org.geotoolkit.ows.xml.v110.DomainType)constr);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v110.OperationsMetadata(ops, params, consts, extendedCapabilities);
        } else if ("1.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v100.Operation> ops = new ArrayList<>();
            if (operations != null) {
                for (AbstractOperation op : operations) {
                    if (op != null && !(op instanceof org.geotoolkit.ows.xml.v100.Operation)) {
                        throw new IllegalArgumentException("unexpected object version for operation");
                    } else if (op != null) {
                        ops.add((org.geotoolkit.ows.xml.v100.Operation)op);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v100.DomainType> params = new ArrayList<>();
            if (parameters != null) {
                for (AbstractDomain param : parameters) {
                    if (param != null && !(param instanceof org.geotoolkit.ows.xml.v100.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for parameter");
                    } else if (param != null) {
                        params.add((org.geotoolkit.ows.xml.v100.DomainType)param);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v100.DomainType> consts = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain constr : constraints) {
                    if (constr != null && !(constr instanceof org.geotoolkit.ows.xml.v100.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for constraint");
                    } else if (constr != null) {
                        consts.add((org.geotoolkit.ows.xml.v100.DomainType)constr);
                    }
                }
            }
            if (extendedCapabilities != null && !(extendedCapabilities instanceof MultiLingualCapabilities)) {
                throw new IllegalArgumentException("unexpected object type for extendedCapabilities");
            }
            return new org.geotoolkit.ows.xml.v100.OperationsMetadata(ops, params, consts, (MultiLingualCapabilities) extendedCapabilities);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
