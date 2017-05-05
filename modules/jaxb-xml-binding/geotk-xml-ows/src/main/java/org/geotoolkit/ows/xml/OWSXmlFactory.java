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
import org.opengis.metadata.extent.GeographicBoundingBox;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OWSXmlFactory {

    public static AbstractOnlineResourceType buildOnlineResource(final String currentVersion, final String url) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.OnlineResourceType(url);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.OnlineResourceType(url);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.OnlineResourceType(url);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

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

    public static AbstractDomain buildDomain(final String currentVersion, final String name, final List<String> value) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.DomainType(name, value);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.DomainType(name, value);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.DomainType(name, value);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractDomain buildDomainAnyValue(final String currentVersion, final String name) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.DomainType(name, new org.geotoolkit.ows.xml.v110.AnyValue());
        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("AnyValue is not available for v100 version.");
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.DomainType(name, new org.geotoolkit.ows.xml.v200.AnyValue());
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractDomain buildDomainRange(final String currentVersion, final String name, final String lower, final String upper) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.DomainType(name, new org.geotoolkit.ows.xml.v110.AllowedValues(new org.geotoolkit.ows.xml.v110.RangeType(lower, upper)));
        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("AnyValue is not available for v100 version.");
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.DomainType(name, new org.geotoolkit.ows.xml.v200.AllowedValues(new org.geotoolkit.ows.xml.v200.RangeType(lower, upper)));
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractDomain buildDomainNoValues(final String currentVersion, final String name, final String value) {
        if ("1.1.0".equals(currentVersion)) {
           return  new org.geotoolkit.ows.xml.v110.DomainType(name,
                                                              new org.geotoolkit.ows.xml.v110.NoValues(),
                                                              new org.geotoolkit.ows.xml.v110.ValueType(value));
        } else if ("1.0.0".equals(currentVersion)) {
           throw new IllegalArgumentException("No values not available for 1.0.0 version");
        } else if ("2.0.0".equals(currentVersion)) {
           return  new org.geotoolkit.ows.xml.v200.DomainType(name,
                                                              new org.geotoolkit.ows.xml.v200.NoValues(),
                                                              new org.geotoolkit.ows.xml.v200.ValueType(value));
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractRequestMethod buildRrequestMethod(final String currentVersion, final String url, final List<AbstractDomain> constraints) {
        if ("1.1.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v110.DomainType> constraintsV110 = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain constraint : constraints) {
                    if (constraint != null && !(constraint instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for constraint");
                    } else if (constraint != null) {
                        constraintsV110.add((org.geotoolkit.ows.xml.v110.DomainType)constraint);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v110.RequestMethodType(url, constraintsV110);
        } else if ("1.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v100.DomainType> constraintsV100 = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain constraint : constraints) {
                    if (constraint != null && !(constraint instanceof org.geotoolkit.ows.xml.v100.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for constraint");
                    } else if (constraint != null) {
                        constraintsV100.add((org.geotoolkit.ows.xml.v100.DomainType)constraint);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v100.RequestMethodType(url, constraintsV100);
        } else if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v200.DomainType> constraintsV200 = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain constraint : constraints) {
                    if (constraint != null && !(constraint instanceof org.geotoolkit.ows.xml.v200.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for constraint");
                    } else if (constraint != null) {
                        constraintsV200.add((org.geotoolkit.ows.xml.v200.DomainType)constraint);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v200.RequestMethodType(url, constraintsV200);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractDCP buildDCP(final String currentVersion, final String getURL, final String postURL) {
        if ("1.1.0".equals(currentVersion)) {
            org.geotoolkit.ows.xml.v110.RequestMethodType getReq = null;
            if (getURL != null) {
                getReq = new org.geotoolkit.ows.xml.v110.RequestMethodType(getURL);
            }
            org.geotoolkit.ows.xml.v110.RequestMethodType postReq = null;
            if (postURL != null) {
                postReq = new org.geotoolkit.ows.xml.v110.RequestMethodType(postURL);
            }
            final org.geotoolkit.ows.xml.v110.HTTP http = new org.geotoolkit.ows.xml.v110.HTTP(getReq, postReq);
            return new org.geotoolkit.ows.xml.v110.DCP(http);
        } else if ("1.0.0".equals(currentVersion)) {
            org.geotoolkit.ows.xml.v100.RequestMethodType getReq = null;
            if (getURL != null) {
                getReq = new org.geotoolkit.ows.xml.v100.RequestMethodType(getURL);
            }
            org.geotoolkit.ows.xml.v100.RequestMethodType postReq = null;
            if (postURL != null) {
                postReq = new org.geotoolkit.ows.xml.v100.RequestMethodType(postURL);
            }
            final org.geotoolkit.ows.xml.v100.HTTP http = new org.geotoolkit.ows.xml.v100.HTTP(getReq, postReq);
            return new org.geotoolkit.ows.xml.v100.DCP(http);
        } else if ("2.0.0".equals(currentVersion)) {
            org.geotoolkit.ows.xml.v200.RequestMethodType getReq = null;
            if (getURL != null) {
                getReq = new org.geotoolkit.ows.xml.v200.RequestMethodType(getURL);
            }
            org.geotoolkit.ows.xml.v200.RequestMethodType postReq = null;
            if (postURL != null) {
                postReq = new org.geotoolkit.ows.xml.v200.RequestMethodType(postURL);
            }
            final org.geotoolkit.ows.xml.v200.HTTP http = new org.geotoolkit.ows.xml.v200.HTTP(getReq, postReq);
            return new org.geotoolkit.ows.xml.v200.DCP(http);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractHTTP buildHttp(final String currentVersion, final List<AbstractRequestMethod> get, final List<AbstractRequestMethod> post) {
        if ("1.1.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v110.RequestMethodType> getV110 = new ArrayList<>();
            if (get != null) {
                for (AbstractRequestMethod g : get) {
                    if (g != null && !(g instanceof org.geotoolkit.ows.xml.v110.RequestMethodType)) {
                        throw new IllegalArgumentException("unexpected object version for get requestMethod");
                    } else if (g != null) {
                        getV110.add((org.geotoolkit.ows.xml.v110.RequestMethodType)g);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v110.RequestMethodType> postV110 = new ArrayList<>();
            if (post != null) {
                for (AbstractRequestMethod p : post) {
                    if (p != null && !(p instanceof org.geotoolkit.ows.xml.v110.RequestMethodType)) {
                        throw new IllegalArgumentException("unexpected object version for post requestMethod");
                    } else if (p != null) {
                        postV110.add((org.geotoolkit.ows.xml.v110.RequestMethodType)p);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v110.HTTP(getV110, postV110);
        } else if ("1.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v100.RequestMethodType> getV100 = new ArrayList<>();
            if (get != null) {
                for (AbstractRequestMethod g : get) {
                    if (g != null && !(g instanceof org.geotoolkit.ows.xml.v100.RequestMethodType)) {
                        throw new IllegalArgumentException("unexpected object version for get requestMethod");
                    } else if (g != null) {
                        getV100.add((org.geotoolkit.ows.xml.v100.RequestMethodType)g);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v100.RequestMethodType> postV100 = new ArrayList<>();
            if (post != null) {
                for (AbstractRequestMethod p : post) {
                    if (p != null && !(p instanceof org.geotoolkit.ows.xml.v100.RequestMethodType)) {
                        throw new IllegalArgumentException("unexpected object version for post requestMethod");
                    } else if (p != null) {
                        postV100.add((org.geotoolkit.ows.xml.v100.RequestMethodType)p);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v100.HTTP(getV100, postV100);
        } else if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v200.RequestMethodType> getV200 = new ArrayList<>();
            if (get != null) {
                for (AbstractRequestMethod g : get) {
                    if (g != null && !(g instanceof org.geotoolkit.ows.xml.v200.RequestMethodType)) {
                        throw new IllegalArgumentException("unexpected object version for get requestMethod");
                    } else if (g != null) {
                        getV200.add((org.geotoolkit.ows.xml.v200.RequestMethodType)g);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v200.RequestMethodType> postV200 = new ArrayList<>();
            if (post != null) {
                for (AbstractRequestMethod p : post) {
                    if (p != null && !(p instanceof org.geotoolkit.ows.xml.v200.RequestMethodType)) {
                        throw new IllegalArgumentException("unexpected object version for post requestMethod");
                    } else if (p != null) {
                        postV200.add((org.geotoolkit.ows.xml.v200.RequestMethodType)p);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v200.HTTP(getV200, postV200);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AbstractDCP buildDCP(final String currentVersion, final AbstractHTTP http) {
        if ("1.1.0".equals(currentVersion)) {
            if (http != null && !(http instanceof org.geotoolkit.ows.xml.v110.HTTP)) {
                throw new IllegalArgumentException("unexpected object version for http element");
            }
            return new org.geotoolkit.ows.xml.v110.DCP((org.geotoolkit.ows.xml.v110.HTTP)http);
        } else if ("1.0.0".equals(currentVersion)) {
            if (http != null && !(http instanceof org.geotoolkit.ows.xml.v100.HTTP)) {
                throw new IllegalArgumentException("unexpected object version for http element");
            }
            return new org.geotoolkit.ows.xml.v100.DCP((org.geotoolkit.ows.xml.v100.HTTP)http);
        } else if ("2.0.0".equals(currentVersion)) {
            if (http != null && !(http instanceof org.geotoolkit.ows.xml.v200.HTTP)) {
                throw new IllegalArgumentException("unexpected object version for http element");
            }
            return new org.geotoolkit.ows.xml.v200.DCP((org.geotoolkit.ows.xml.v200.HTTP)http);
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
                    if (dom != null && !(dom instanceof org.geotoolkit.ows.xml.v200.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for domains");
                    } else if (dom != null) {
                        param200.add((org.geotoolkit.ows.xml.v200.DomainType)dom);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v200.DomainType> const200 = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain dom : constraints) {
                    if (dom != null && !(dom instanceof org.geotoolkit.ows.xml.v200.DomainType)) {
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
                kw.add(new org.geotoolkit.ows.xml.v100.KeywordsType(keywords, null));
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
            final String zipCode, final String country, final String hoursOfService, final String contactInstructions) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.ContactType(phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.ContactType(phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.ContactType(phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
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
            final org.geotoolkit.ows.xml.v110.CodeType r;
            if (role != null) {
                r = new org.geotoolkit.ows.xml.v110.CodeType(role);
            } else {
                r = null;
            }
            return new org.geotoolkit.ows.xml.v110.ResponsiblePartySubsetType(individualName, positionName,
                                                                              (org.geotoolkit.ows.xml.v110.ContactType)contact,
                                                                              r);
        } else if ("1.0.0".equals(currentVersion)) {
            if (contact != null && !(contact instanceof org.geotoolkit.ows.xml.v100.ContactType)) {
                throw new IllegalArgumentException("unexpected object version for contact element");
            }
            final org.geotoolkit.ows.xml.v100.CodeType r;
            if (role != null) {
                r = new org.geotoolkit.ows.xml.v100.CodeType(role);
            } else {
                r = null;
            }
            return new org.geotoolkit.ows.xml.v100.ResponsiblePartySubsetType(individualName, positionName,
                                                                              (org.geotoolkit.ows.xml.v100.ContactType)contact,
                                                                              r);
        } else if ("2.0.0".equals(currentVersion)) {
            if (contact != null && !(contact instanceof org.geotoolkit.ows.xml.v200.ContactType)) {
                throw new IllegalArgumentException("unexpected object version for contact element");
            }
            final org.geotoolkit.ows.xml.v200.CodeType r;
            if (role != null) {
                r = new org.geotoolkit.ows.xml.v200.CodeType(role);
            } else {
                r = null;
            }
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
        } else if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v200.Operation> ops = new ArrayList<>();
            if (operations != null) {
                for (AbstractOperation op : operations) {
                    if (op != null && !(op instanceof org.geotoolkit.ows.xml.v200.Operation)) {
                        throw new IllegalArgumentException("unexpected object version for operation");
                    } else if (op != null) {
                        ops.add((org.geotoolkit.ows.xml.v200.Operation)op);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v200.DomainType> params = new ArrayList<>();
            if (parameters != null) {
                for (AbstractDomain param : parameters) {
                    if (param != null && !(param instanceof org.geotoolkit.ows.xml.v200.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for parameter");
                    } else if (param != null) {
                        params.add((org.geotoolkit.ows.xml.v200.DomainType)param);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v200.DomainType> consts = new ArrayList<>();
            if (constraints != null) {
                for (AbstractDomain constr : constraints) {
                    if (constr != null && !(constr instanceof org.geotoolkit.ows.xml.v200.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for constraint");
                    } else if (constr != null) {
                        consts.add((org.geotoolkit.ows.xml.v200.DomainType)constr);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v200.OperationsMetadata(ops, params, consts, extendedCapabilities);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static BoundingBox buildWGS84BoundingBox(String currentVersion, GeographicBoundingBox inputGeoBox) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType(inputGeoBox);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType(inputGeoBox);
        } else if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v200.WGS84BoundingBoxType(inputGeoBox);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
