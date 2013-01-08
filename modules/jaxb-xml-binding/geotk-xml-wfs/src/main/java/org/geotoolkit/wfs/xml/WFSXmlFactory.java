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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.SortBy;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.geotoolkit.ows.xml.*;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.sort.SortOrder;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WFSXmlFactory {

    public static FeatureTypeList buildFeatureTypeList(final String version) {
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

    public static WFSCapabilities buildWFSCapabilities(final String version, final String updateSequence) {
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

    public static FeatureType buildFeatureType(final String version, final QName name, final String title, final String defaultCRS, final List<String> otherCRS, final Object bbox) {
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

    public static Object buildBBOX(final String version, final String crsName, final double minx, final double miny, final double maxx, final double maxy) {
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

    public static WFSCapabilities buildWFSCapabilities(final String version,  final AbstractServiceIdentification si, final AbstractServiceProvider sp,
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

    public static WFSFeatureCollection buildFeatureCollection(final String version, final String id, final Integer numberOfFeatures, final XMLGregorianCalendar timeStamp) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v200.FeatureCollectionType(numberOfFeatures, timeStamp);
        } else if ("1.1.0".equals(version)) {
            final org.geotoolkit.wfs.xml.v110.FeatureCollectionType fc = new org.geotoolkit.wfs.xml.v110.FeatureCollectionType(numberOfFeatures, timeStamp);
            fc.setId(id);
            return fc;
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v100.FeatureCollectionType(id);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ValueCollection buildValueCollection(final String version, final Integer numberOfFeatures, final XMLGregorianCalendar timeStamp) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v200.ValueCollectionType(numberOfFeatures, timeStamp);
        } else if ("1.1.0".equals(version)) {
            throw new IllegalArgumentException("The operation GetPropertyValue is not available in version:" + version);
        } else if ("1.0.0".equals(version)) {
             throw new IllegalArgumentException("The operation GetPropertyValue is not available in version:" + version);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static TransactionResponse buildTransactionResponse(final String version, final Integer totalInserted, final Integer totalUpdated, final Integer totalDeleted, final Integer totalReplaced,
            final Map<String, String> inserted, final Map<String, String> replaced) {
        if ("2.0.0".equals(version)) {
            org.geotoolkit.wfs.xml.v200.ActionResultsType insertResults = null;
            if (inserted.size() > 0) {
                final List<org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType> ift = new ArrayList<org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType>();
                for (Entry<String, String> id : inserted.entrySet()) {
                    ift.add(new org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType(new org.geotoolkit.ogc.xml.v200.ResourceIdType(id.getKey()), id.getValue()));
                }
                insertResults = new org.geotoolkit.wfs.xml.v200.ActionResultsType(ift);
            }
            org.geotoolkit.wfs.xml.v200.ActionResultsType replaceResults = null;
            if (replaced.size() > 0) {
                final List<org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType> ift = new ArrayList<org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType>();
                for (Entry<String, String> id : replaced.entrySet()) {
                    ift.add(new org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType(new org.geotoolkit.ogc.xml.v200.ResourceIdType(id.getKey()), id.getValue()));
                }
                replaceResults = new org.geotoolkit.wfs.xml.v200.ActionResultsType(ift);
            }

            final org.geotoolkit.wfs.xml.v200.TransactionSummaryType ts = new org.geotoolkit.wfs.xml.v200.TransactionSummaryType(totalInserted, totalUpdated, totalDeleted, totalReplaced);
            return new org.geotoolkit.wfs.xml.v200.TransactionResponseType(ts, null, insertResults, replaceResults, version);
        } else if ("1.1.0".equals(version)) {
            org.geotoolkit.wfs.xml.v110.InsertResultsType insertResults = null;
            if (inserted.size() > 0) {
                final List<org.geotoolkit.wfs.xml.v110.InsertedFeatureType> ift = new ArrayList<org.geotoolkit.wfs.xml.v110.InsertedFeatureType>();
                for (Entry<String, String> id : inserted.entrySet()) {
                    ift.add(new org.geotoolkit.wfs.xml.v110.InsertedFeatureType(new org.geotoolkit.ogc.xml.v110.FeatureIdType(id.getKey()), id.getValue()));
                }
                insertResults = new org.geotoolkit.wfs.xml.v110.InsertResultsType(ift);
            }
            final org.geotoolkit.wfs.xml.v110.TransactionSummaryType ts = new org.geotoolkit.wfs.xml.v110.TransactionSummaryType(totalInserted, totalUpdated, totalDeleted);
            return new org.geotoolkit.wfs.xml.v110.TransactionResponseType(ts, null, insertResults, version);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.wfs.xml.v100.InsertResultType> ift = new ArrayList<org.geotoolkit.wfs.xml.v100.InsertResultType>();
            if (inserted.size() > 0) {
                for (Entry<String, String> id : inserted.entrySet()) {
                    ift.add(new org.geotoolkit.wfs.xml.v100.InsertResultType(new org.geotoolkit.ogc.xml.v100.FeatureIdType(id.getKey()), id.getValue()));
                }
            }
            return new org.geotoolkit.wfs.xml.v100.WFSTransactionResponseType(null, ift, version);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetGmlObject buildGetGmlObject(final String version, final String id, final String service, final String handle, final String outputFormat) {
        if ("2.0.0".equals(version)) {
            throw new IllegalArgumentException("The operation GetGmlObject is not available in version:" + version);
        } else if ("1.1.0".equals(version)) {
            final org.geotoolkit.ogc.xml.v110.GmlObjectIdType gmlObjectId = new org.geotoolkit.ogc.xml.v110.GmlObjectIdType(id);
            return new org.geotoolkit.wfs.xml.v110.GetGmlObjectType(service, version, handle, gmlObjectId, outputFormat);
        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("The operation GetGmlObject is not available in version:" + version);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DescribeFeatureType buildDecribeFeatureType(final String version, final String service, final String handle, final List<QName> typeNames, String outputFormat) {
        if ("2.0.0".equals(version)) {
            if (outputFormat == null) {
               outputFormat = "application/gml+xml; version=3.2";
            }
            return new org.geotoolkit.wfs.xml.v200.DescribeFeatureTypeType(service, version, handle, typeNames, outputFormat);
        } else if ("1.1.0".equals(version)) {
            if (outputFormat == null) {
               outputFormat = "text/xml; subtype=gml/3.1.1";
            }
            return new org.geotoolkit.wfs.xml.v110.DescribeFeatureTypeType(service, version, handle, typeNames, outputFormat);
        } else if ("1.0.0".equals(version)) {
            if (outputFormat == null) {
               outputFormat = "XMLSCHEMA";
            }
            return new org.geotoolkit.wfs.xml.v100.DescribeFeatureTypeType(service, version, typeNames, outputFormat);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DeleteElement buildDeleteElement(final String version, final XMLFilter filter, final String handle, final QName typeName) {
        if ("2.0.0".equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v200.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            return new org.geotoolkit.wfs.xml.v200.DeleteType((org.geotoolkit.ogc.xml.v200.FilterType)filter, handle, typeName);
        } else if ("1.1.0".equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v110.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            return new org.geotoolkit.wfs.xml.v110.DeleteElementType((org.geotoolkit.ogc.xml.v110.FilterType)filter, handle, typeName);
        } else if ("1.0.0".equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v100.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            return new org.geotoolkit.wfs.xml.v100.DeleteElementType((org.geotoolkit.ogc.xml.v100.FilterType)filter, handle, typeName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Transaction buildTransaction(final String version,  final String service, final String handle, final AllSomeType releaseAction, final DeleteElement deleteElement) {
        if ("2.0.0".equals(version)) {
            if (deleteElement != null && !(deleteElement instanceof org.geotoolkit.wfs.xml.v200.DeleteType)) {
                throw new IllegalArgumentException("unexpected object version for delete element");
            }
            return new org.geotoolkit.wfs.xml.v200.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v200.DeleteType)deleteElement);
        } else if ("1.1.0".equals(version)) {
            if (deleteElement != null && !(deleteElement instanceof org.geotoolkit.wfs.xml.v110.DeleteElementType)) {
                throw new IllegalArgumentException("unexpected object version for delete element");
            }
            return new org.geotoolkit.wfs.xml.v110.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v110.DeleteElementType)deleteElement);
        } else if ("1.0.0".equals(version)) {
            if (deleteElement != null && !(deleteElement instanceof org.geotoolkit.wfs.xml.v100.DeleteElementType)) {
                throw new IllegalArgumentException("unexpected object version for delete element");
            }
            return new org.geotoolkit.wfs.xml.v100.TransactionType(service, version, handle, releaseAction,  (org.geotoolkit.wfs.xml.v100.DeleteElementType)deleteElement);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }


    public static LockFeature buildLockFeature(final String version,  final String service, final String handle, final AllSomeType releaseAction, final XMLFilter filter,
            final QName typeName, final int expiry) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.wfs.xml.v200.QueryType> queries = new ArrayList<org.geotoolkit.wfs.xml.v200.QueryType>();
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v200.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            } else if (filter != null){
                final org.geotoolkit.wfs.xml.v200.QueryType query = new org.geotoolkit.wfs.xml.v200.QueryType((org.geotoolkit.ogc.xml.v200.FilterType)filter, Arrays.asList(typeName), handle);
                queries.add(query);
            }
            return new org.geotoolkit.wfs.xml.v200.LockFeatureType(service, version, handle, queries, expiry, releaseAction);
        } else if ("1.1.0".equals(version)) {
            final List<org.geotoolkit.wfs.xml.v110.LockType> queries = new ArrayList<org.geotoolkit.wfs.xml.v110.LockType>();
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v110.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            } else if (filter != null){
                final org.geotoolkit.wfs.xml.v110.LockType query = new org.geotoolkit.wfs.xml.v110.LockType((org.geotoolkit.ogc.xml.v110.FilterType)filter, handle, typeName);
                queries.add(query);
            }
            return new org.geotoolkit.wfs.xml.v110.LockFeatureType(service, version, handle, queries, expiry, releaseAction);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.wfs.xml.v100.LockType> queries = new ArrayList<org.geotoolkit.wfs.xml.v100.LockType>();
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v100.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            } else if (filter != null){
                final org.geotoolkit.wfs.xml.v100.LockType query = new org.geotoolkit.wfs.xml.v100.LockType((org.geotoolkit.ogc.xml.v100.FilterType)filter, handle, typeName);
                queries.add(query);
            }
            return new org.geotoolkit.wfs.xml.v100.LockFeatureType(service, version, queries, expiry, releaseAction);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetCapabilities buildGetCapabilities(final String version, final String service) {
        return buildGetCapabilities(version, null, null, null, null, service);
    }
    
    public static GetCapabilities buildGetCapabilities(final String version, final AcceptVersions versions, final Sections sections, final AcceptFormats formats, final String updateSequence, final String service) {
        if ("2.0.0".equals(version)) {
            if (versions != null && !(versions instanceof org.geotoolkit.ows.xml.v110.AcceptVersionsType)) {
                throw new IllegalArgumentException("unexpected object version for AcceptVersion element");
            }
            if (sections != null && !(sections instanceof org.geotoolkit.ows.xml.v110.SectionsType)) {
                throw new IllegalArgumentException("unexpected object version for Sections element");
            }
            if (formats != null && !(formats instanceof org.geotoolkit.ows.xml.v110.AcceptFormatsType)) {
                throw new IllegalArgumentException("unexpected object version for AcceptFormat element");
            }
            return new org.geotoolkit.wfs.xml.v200.GetCapabilitiesType((org.geotoolkit.ows.xml.v110.AcceptVersionsType)versions,
                                                                       (org.geotoolkit.ows.xml.v110.SectionsType)sections,
                                                                       (org.geotoolkit.ows.xml.v110.AcceptFormatsType)formats,
                                                                       updateSequence,
                                                                       service);
        } else if ("1.1.0".equals(version)) {
            if (versions != null && !(versions instanceof org.geotoolkit.ows.xml.v100.AcceptVersionsType)) {
                throw new IllegalArgumentException("unexpected object version for AcceptVersion element");
            }
            if (sections != null && !(sections instanceof org.geotoolkit.ows.xml.v100.SectionsType)) {
                throw new IllegalArgumentException("unexpected object version for Sections element");
            }
            if (formats != null && !(formats instanceof org.geotoolkit.ows.xml.v100.AcceptFormatsType)) {
                throw new IllegalArgumentException("unexpected object version for AcceptFormat element");
            }
            return new org.geotoolkit.wfs.xml.v110.GetCapabilitiesType((org.geotoolkit.ows.xml.v100.AcceptVersionsType)versions,
                                                                       (org.geotoolkit.ows.xml.v100.SectionsType)sections,
                                                                       (org.geotoolkit.ows.xml.v100.AcceptFormatsType)formats,
                                                                       updateSequence,
                                                                       service);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v100.GetCapabilitiesType(service, version);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AcceptVersions buildAcceptVersion(final String currentVersion, final List<String> acceptVersion) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptVersion("1.1.0", acceptVersion);
        } else if ("1.1.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptVersion("1.0.0", acceptVersion);
        } else if ("1.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptVersion("1.0.0", acceptVersion);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AcceptFormats buildAcceptFormat(final String currentVersion, final List<String> acceptFormats) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptFormat("1.1.0", acceptFormats);
        } else if ("1.1.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptFormat("1.0.0", acceptFormats);
        } else if ("1.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptFormat("1.0.0", acceptFormats);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Sections buildSections(final String currentVersion, final List<String> sections) {
        if ("2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildSections("1.1.0", sections);
        } else if ("1.1.0".equals(currentVersion)) {
            return OWSXmlFactory.buildSections("1.0.0", sections);
        } else if ("1.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildSections("1.0.0", sections);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static StoredQuery buildStoredQuery(final String currentVersion, final String id, final String handle, final List<Parameter> parameters) {
        if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.wfs.xml.v200.ParameterType> params = new ArrayList<org.geotoolkit.wfs.xml.v200.ParameterType>();
            for (Parameter p : parameters) {
                if (!(p instanceof org.geotoolkit.wfs.xml.v200.ParameterType)) {
                    throw new IllegalArgumentException("bad object version for parameter");
                }
                params.add((org.geotoolkit.wfs.xml.v200.ParameterType)p);
            }
            return new org.geotoolkit.wfs.xml.v200.StoredQueryType(id, handle, params);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Query buildQuery(final String currentVersion, final XMLFilter filter, final List<QName> typeNames, final String featureVersion,
            final String srsName, final SortBy sortBy, final List<String> propertyNames) {
        if ("2.0.0".equals(currentVersion)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v200.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            if (sortBy != null && !(sortBy instanceof org.geotoolkit.ogc.xml.v200.SortByType)) {
                throw new IllegalArgumentException("unexpected object version for sortBy element");
            }
            return new org.geotoolkit.wfs.xml.v200.QueryType((org.geotoolkit.ogc.xml.v200.FilterType) filter,
                                                              typeNames, featureVersion, srsName,
                                                             (org.geotoolkit.ogc.xml.v200.SortByType)sortBy,
                                                              propertyNames);
        } else if ("1.1.0".equals(currentVersion)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v110.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            if (sortBy != null && !(sortBy instanceof org.geotoolkit.ogc.xml.v110.SortByType)) {
                throw new IllegalArgumentException("unexpected object version for sortBy element");
            }
            return new org.geotoolkit.wfs.xml.v110.QueryType((org.geotoolkit.ogc.xml.v110.FilterType) filter,
                                                              typeNames, featureVersion, srsName,
                                                             (org.geotoolkit.ogc.xml.v110.SortByType)sortBy,
                                                              propertyNames);
        } else if ("1.0.0".equals(currentVersion)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v100.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            QName typeName = null;
            if (typeNames != null && !typeNames.isEmpty()) {
                typeName = typeNames.get(0);
            }
            return new org.geotoolkit.wfs.xml.v100.QueryType((org.geotoolkit.ogc.xml.v100.FilterType) filter,
                                                              typeName, featureVersion, propertyNames);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static SortBy buildSortBy(final String currentVersion, final String sortByParam, final SortOrder order) {
        if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ogc.xml.v200.SortPropertyType> sortProperties = new ArrayList<org.geotoolkit.ogc.xml.v200.SortPropertyType>();
            sortProperties.add(new org.geotoolkit.ogc.xml.v200.SortPropertyType(sortByParam, order));
            return new org.geotoolkit.ogc.xml.v200.SortByType(sortProperties);
        } else if ("1.1.0".equals(currentVersion)) {
            final List<org.geotoolkit.ogc.xml.v110.SortPropertyType> sortProperties = new ArrayList<org.geotoolkit.ogc.xml.v110.SortPropertyType>();
            sortProperties.add(new org.geotoolkit.ogc.xml.v110.SortPropertyType(sortByParam, order));
            return new org.geotoolkit.ogc.xml.v110.SortByType(sortProperties);
        } else if ("1.0.0".equals(currentVersion)) {
            return null;
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static XMLFilter buildBBOXFilter(final String currentVersion, String propertyName, double minx, double miny, double maxx, double maxy, String srs) {
        if ("2.0.0".equals(currentVersion)) {
            final  org.geotoolkit.ogc.xml.v200.BBOXType bbox = new org.geotoolkit.ogc.xml.v200.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
            return new org.geotoolkit.ogc.xml.v200.FilterType(bbox);
        } else if ("1.1.0".equals(currentVersion)) {
            final  org.geotoolkit.ogc.xml.v110.BBOXType bbox = new org.geotoolkit.ogc.xml.v110.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
            return new org.geotoolkit.ogc.xml.v110.FilterType(bbox);
        } else if ("1.0.0".equals(currentVersion)) {
            final  org.geotoolkit.ogc.xml.v100.BBOXType bbox = new org.geotoolkit.ogc.xml.v100.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
            return new org.geotoolkit.ogc.xml.v100.FilterType(bbox);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Parameter buildParameter(final String currentVersion, final String name, final Object value) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.wfs.xml.v200.ParameterType(name, value);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static GetFeature buildGetFeature(final String currentVersion, final String service, final String handle, final Integer maxFeature,
        final Query query, final ResultTypeType resultType, String outputFormat) {
        if ("2.0.0".equals(currentVersion)) {
            if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v200.QueryType)) {
                throw new IllegalArgumentException("unexpected object version for query element");
            }
            return new org.geotoolkit.wfs.xml.v200.GetFeatureType(service, currentVersion, handle, maxFeature,
                                                                  Arrays.asList((org.geotoolkit.wfs.xml.v200.QueryType)query),
                                                                  resultType, outputFormat);
        } else if ("1.1.0".equals(currentVersion)) {
            if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v110.QueryType)) {
                throw new IllegalArgumentException("unexpected object version for query element");
            }
            return new org.geotoolkit.wfs.xml.v110.GetFeatureType(service, currentVersion, handle, maxFeature,
                                                                  Arrays.asList((org.geotoolkit.wfs.xml.v110.QueryType)query),
                                                                  resultType, outputFormat);
        } else if ("1.0.0".equals(currentVersion)) {
            if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v100.QueryType)) {
                throw new IllegalArgumentException("unexpected object version for query element");
            }
            return new org.geotoolkit.wfs.xml.v100.GetFeatureType(service, currentVersion, handle, maxFeature,
                                                                  Arrays.asList((org.geotoolkit.wfs.xml.v100.QueryType)query),
                                                                  outputFormat);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static GetFeature buildGetFeature(final String currentVersion, final String service, final String handle, final Integer maxFeature,
        final StoredQuery query, final ResultTypeType resultType, String outputFormat) {
        if ("2.0.0".equals(currentVersion)) {
            if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v200.StoredQueryType)) {
                throw new IllegalArgumentException("unexpected object version for query element");
            }
            return new org.geotoolkit.wfs.xml.v200.GetFeatureType(service, currentVersion, handle, 
                                                                  Arrays.asList((org.geotoolkit.wfs.xml.v200.StoredQueryType)query),
                                                                  maxFeature,
                                                                  resultType, outputFormat);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static ListStoredQueries buildListStoredQueries(final String version, final String service, final String handle) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v200.ListStoredQueriesType(service, version, handle);

        } else if ("1.1.0".equals(version)) {
            throw new IllegalArgumentException("ListStoredQueries is not available in version 1.1.0");

        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("ListStoredQueries is not available in version 1.0.0");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DescribeStoredQueries buildDescribeStoredQueries(final String version, final String service, final String handle, final List<String> storedQueryId) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wfs.xml.v200.DescribeStoredQueriesType(service, version, handle, storedQueryId);

        } else if ("1.1.0".equals(version)) {
            throw new IllegalArgumentException("DescribeStoredQueries is not available in version 1.1.0");

        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("DescribeStoredQueries is not available in version 1.0.0");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetPropertyValue buildGetPropertyValue(final String currentVersion, final String service, final String handle, final Integer maxFeature, final String featureId,
        final Query query, final ResultTypeType resultType, String outputFormat) {
        if ("2.0.0".equals(currentVersion)) {
            if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v200.QueryType)) {
                throw new IllegalArgumentException("unexpected object version for query element");
            }
            return new org.geotoolkit.wfs.xml.v200.GetPropertyValueType(service, currentVersion, handle, maxFeature, featureId,
                    (org.geotoolkit.wfs.xml.v200.QueryType)query, resultType, outputFormat);

        } else if ("1.1.0".equals(currentVersion)) {
            throw new IllegalArgumentException("GetPropertyValue is not available in version 1.1.0");

        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("GetPropertyValue is not available in version 1.0.0");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static DropStoredQuery buildDropStoredQuery(final String currentVersion, final String service, final String handle, final String id) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.wfs.xml.v200.DropStoredQueryType(service, currentVersion, handle, id);

        } else if ("1.1.0".equals(currentVersion)) {
            throw new IllegalArgumentException("DropStoredQuery is not available in version 1.1.0");

        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("DropStoredQuery is not available in version 1.0.0");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static ListStoredQueriesResponse buildListStoredQueriesResponse(final String currentVersion, final List<StoredQueryDescription> descriptions) {
        if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.wfs.xml.v200.StoredQueryListItemType> storedQuery = new ArrayList<org.geotoolkit.wfs.xml.v200.StoredQueryListItemType>();
            for (StoredQueryDescription description : descriptions) {
                final List<QName> returnTypes = new ArrayList<QName>();
                for (QueryExpressionText queryEx : description.getQueryExpressionText()) {
                    returnTypes.addAll(queryEx.getReturnFeatureTypes());
                }
                storedQuery.add(new org.geotoolkit.wfs.xml.v200.StoredQueryListItemType(description.getId(),
                                                            (List<org.geotoolkit.wfs.xml.v200.Title>)description.getTitle(),
                                                            returnTypes));
            }
            return new org.geotoolkit.wfs.xml.v200.ListStoredQueriesResponseType(storedQuery);

        } else if ("1.1.0".equals(currentVersion)) {
            throw new IllegalArgumentException("StoredQueryListItem is not available in version 1.1.0");

        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("StoredQueryListItem is not available in version 1.0.0");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static DescribeStoredQueriesResponse buildDescribeStoredQueriesResponse(final String currentVersion, final List<StoredQueryDescription> descriptions) {
        if ("2.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType> storedQuery = new ArrayList<org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType>();
            for (StoredQueryDescription description : descriptions) {
                if (description instanceof org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType) {
                    storedQuery.add((org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType) description);
                } else {
                    throw new IllegalArgumentException("unexpected object version for StoredQueryDescription element");
                }
            }
            return new org.geotoolkit.wfs.xml.v200.DescribeStoredQueriesResponseType(storedQuery);

        } else if ("1.1.0".equals(currentVersion)) {
            throw new IllegalArgumentException("DescribeStoredQueriesResponse is not available in version 1.1.0");

        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("DescribeStoredQueriesResponse is not available in version 1.0.0");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static CreateStoredQueryResponse buildCreateStoredQueryResponse(final String currentVersion, final String status) {
        if ("2.0.0".equals(currentVersion)) {

            return new org.geotoolkit.wfs.xml.v200.CreateStoredQueryResponseType(status);

        } else if ("1.1.0".equals(currentVersion)) {
            throw new IllegalArgumentException("CreateStoredQueryResponse is not available in version 1.1.0");

        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("CreateStoredQueryResponse is not available in version 1.0.0");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static DropStoredQueryResponse buildDropStoredQueryResponse(final String currentVersion, final String status) {
        if ("2.0.0".equals(currentVersion)) {

            return new org.geotoolkit.wfs.xml.v200.DropStoredQueryResponseType(status);

        } else if ("1.1.0".equals(currentVersion)) {
            throw new IllegalArgumentException("CreateStoredQueryResponse is not available in version 1.1.0");

        } else if ("1.0.0".equals(currentVersion)) {
            throw new IllegalArgumentException("CreateStoredQueryResponse is not available in version 1.0.0");

        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static Query cloneQuery(final Query query) {
        if (query instanceof org.geotoolkit.wfs.xml.v200.QueryType) {
            return new org.geotoolkit.wfs.xml.v200.QueryType((org.geotoolkit.wfs.xml.v200.QueryType)query);
        } else if (query instanceof org.geotoolkit.wfs.xml.v110.QueryType) {
            return new org.geotoolkit.wfs.xml.v110.QueryType((org.geotoolkit.wfs.xml.v110.QueryType)query);
        } else if (query instanceof org.geotoolkit.wfs.xml.v100.QueryType) {
            return new org.geotoolkit.wfs.xml.v100.QueryType((org.geotoolkit.wfs.xml.v100.QueryType)query);
        } else if (query == null) {
            return null;
        } else {
            throw new IllegalArgumentException("unexpected query implementation:" + query.getClass().getName());
        }
    } 
}
