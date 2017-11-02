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
import org.geotoolkit.wfs.xml.v110.ValueType;
import org.geotoolkit.wfs.xml.v200.ValueReference;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.sort.SortOrder;

import static org.geotoolkit.wfs.xml.WFSVersion.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WFSXmlFactory {

    public static FeatureTypeList buildFeatureTypeList(final String version) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.FeatureTypeListType();
            case"1.1.0":
                return new org.geotoolkit.wfs.xml.v110.FeatureTypeListType();
            case"1.0.0":
                return new org.geotoolkit.wfs.xml.v100.FeatureTypeListType();
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static WFSCapabilities buildWFSCapabilities(final String version, final String updateSequence) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.WFSCapabilitiesType(version, updateSequence);
            case"1.1.0":
                return new org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType(version, updateSequence);
            case"1.0.0":
                return new org.geotoolkit.wfs.xml.v100.WFSCapabilitiesType(version, updateSequence);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static FeatureType buildFeatureType(final String version, final QName name, final String title, final String defaultCRS, final List<String> otherCRS, final Object bbox) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
            {
                final List<org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType> bboxes = new ArrayList<>();
                if (bbox != null && !(bbox instanceof org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType)) {
                    throw new IllegalArgumentException("unexpected object version for bbox");
                } else if (bbox != null) {
                    bboxes.add((org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType)bbox);
                }
                return new org.geotoolkit.wfs.xml.v200.FeatureTypeType(name, title, defaultCRS, otherCRS, bboxes);
            }
            case"1.1.0":
            {
                final List<org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType> bboxes = new ArrayList<>();
                if (bbox != null && !(bbox instanceof org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType)) {
                    throw new IllegalArgumentException("unexpected object version for bbox");
                } else if (bbox != null) {
                    bboxes.add((org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType)bbox);
                }
                return new org.geotoolkit.wfs.xml.v110.FeatureTypeType(name, title, defaultCRS, otherCRS, bboxes);
            }
            case"1.0.0":
            {
                final List<org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType> bboxes = new ArrayList<>();
                if (bbox != null && !(bbox instanceof org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType)) {
                    throw new IllegalArgumentException("unexpected object version for bbox");
                } else if (bbox != null) {
                    bboxes.add((org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType)bbox);
                }
                return new org.geotoolkit.wfs.xml.v100.FeatureTypeType(name, title, defaultCRS, bboxes);
            }
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Object buildBBOX(final String version, final String crsName, final double minx, final double miny, final double maxx, final double maxy) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType(
                        crsName,
                        minx,
                        miny,
                        maxx,
                        maxy);
            case"1.1.0":
                return new org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType(
                        crsName,
                        minx,
                        miny,
                        maxx,
                        maxy);
            case"1.0.0":
                return new org.geotoolkit.wfs.xml.v100.LatLongBoundingBoxType(minx, miny, maxx, maxy);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static WFSCapabilities buildWFSCapabilities(final String version,  final AbstractServiceIdentification si, final AbstractServiceProvider sp,
            final AbstractOperationsMetadata om, FeatureTypeList ftl, final FilterCapabilities fc) {

        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if ((si  != null && !(si instanceof org.geotoolkit.ows.xml.v110.ServiceIdentification)) ||
                        (sp  != null &&!(sp instanceof org.geotoolkit.ows.xml.v110.ServiceProvider))        ||
                        (om  != null &&!(om instanceof org.geotoolkit.ows.xml.v110.OperationsMetadata))     ||
                        (ftl != null &&!(ftl instanceof org.geotoolkit.wfs.xml.v200.FeatureTypeListType))   ||
                        (fc  != null &&!(fc instanceof org.geotoolkit.ogc.xml.v200.FilterCapabilities))) {
                    throw new IllegalArgumentException("Bad version of object");
                }
                return new  org.geotoolkit.wfs.xml.v200.WFSCapabilitiesType(version,
                        (org.geotoolkit.ows.xml.v110.ServiceIdentification)si,
                        (org.geotoolkit.ows.xml.v110.ServiceProvider)      sp,
                        (org.geotoolkit.ows.xml.v110.OperationsMetadata)   om,
                        (org.geotoolkit.wfs.xml.v200.FeatureTypeListType)  ftl,
                        (org.geotoolkit.ogc.xml.v200.FilterCapabilities)   fc);
        /*  TODO not supported yet
        else if (v100.equals(version)) {
        return new  org.geotoolkit.wfs.xml.v100.WFSCapabilitiesType(v100,
        (org.geotoolkit.ows.xml.v100.ServiceIdentification)si,
        (org.geotoolkit.ows.xml.v100.ServiceProvider)      sp,
        (org.geotoolkit.ows.xml.v100.OperationsMetadata)   om,
        (org.geotoolkit.wfs.xml.v110.FeatureTypeListType)  ftl,
        (org.geotoolkit.ogc.xml.v110.FilterCapabilities)   fc);
        } */
            case"1.1.0":
                if ((si  != null && !(si instanceof org.geotoolkit.ows.xml.v100.ServiceIdentification)) ||
                        (sp  != null && !(sp instanceof org.geotoolkit.ows.xml.v100.ServiceProvider))       ||
                        (om  != null && !(om instanceof org.geotoolkit.ows.xml.v100.OperationsMetadata))    ||
                        (ftl != null && !(ftl instanceof org.geotoolkit.wfs.xml.v110.FeatureTypeListType))  ||
                        (fc  != null && !(fc instanceof org.geotoolkit.ogc.xml.v110.FilterCapabilities))) {
                    throw new IllegalArgumentException("Bad version of object");
                }
                return new  org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType(version,
                        (org.geotoolkit.ows.xml.v100.ServiceIdentification)si,
                        (org.geotoolkit.ows.xml.v100.ServiceProvider)      sp,
                        (org.geotoolkit.ows.xml.v100.OperationsMetadata)   om,
                        (org.geotoolkit.wfs.xml.v110.FeatureTypeListType)  ftl,
                        (org.geotoolkit.ogc.xml.v110.FilterCapabilities)   fc);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static WFSFeatureCollection buildFeatureCollection(final String version, final String id, final Integer numberOfFeatures, final XMLGregorianCalendar timeStamp) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.FeatureCollectionType(numberOfFeatures, timeStamp);
            case"1.1.0":
                final org.geotoolkit.wfs.xml.v110.FeatureCollectionType fc = new org.geotoolkit.wfs.xml.v110.FeatureCollectionType(numberOfFeatures, timeStamp);
                fc.setId(id);
                return fc;
            case"1.0.0":
                return new org.geotoolkit.wfs.xml.v100.FeatureCollectionType(id);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ValueCollection buildValueCollection(final String version, final Integer numberOfFeatures, final XMLGregorianCalendar timeStamp) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.ValueCollectionType(numberOfFeatures, timeStamp);
            case"1.1.0":
                throw new IllegalArgumentException("The operation GetPropertyValue is not available in version:" + version);
            case"1.0.0":
                throw new IllegalArgumentException("The operation GetPropertyValue is not available in version:" + version);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static TransactionResponse buildTransactionResponse(final String version, final Integer totalInserted, final Integer totalUpdated, final Integer totalDeleted, final Integer totalReplaced,
            final Map<String, String> inserted, final Map<String, String> replaced) {
        if (v200.equals(version)) {
            org.geotoolkit.wfs.xml.v200.ActionResultsType insertResults = null;
            if (inserted.size() > 0) {
                final List<org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType> ift = new ArrayList<>();
                for (Entry<String, String> id : inserted.entrySet()) {
                    ift.add(new org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType(new org.geotoolkit.ogc.xml.v200.ResourceIdType(id.getKey()), id.getValue()));
                }
                insertResults = new org.geotoolkit.wfs.xml.v200.ActionResultsType(ift);
            }
            org.geotoolkit.wfs.xml.v200.ActionResultsType replaceResults = null;
            if (replaced.size() > 0) {
                final List<org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType> ift = new ArrayList<>();
                for (Entry<String, String> id : replaced.entrySet()) {
                    ift.add(new org.geotoolkit.wfs.xml.v200.CreatedOrModifiedFeatureType(new org.geotoolkit.ogc.xml.v200.ResourceIdType(id.getKey()), id.getValue()));
                }
                replaceResults = new org.geotoolkit.wfs.xml.v200.ActionResultsType(ift);
            }

            final org.geotoolkit.wfs.xml.v200.TransactionSummaryType ts = new org.geotoolkit.wfs.xml.v200.TransactionSummaryType(
                    totalInserted, totalUpdated, totalDeleted, totalReplaced);
            return new org.geotoolkit.wfs.xml.v200.TransactionResponseType(ts, null, insertResults, replaceResults, version);
        } else if (v110.equals(version)) {
            org.geotoolkit.wfs.xml.v110.InsertResultsType insertResults = null;
            if (inserted.size() > 0) {
                final List<org.geotoolkit.wfs.xml.v110.InsertedFeatureType> ift = new ArrayList<>();
                for (Entry<String, String> id : inserted.entrySet()) {
                    ift.add(new org.geotoolkit.wfs.xml.v110.InsertedFeatureType(new org.geotoolkit.ogc.xml.v110.FeatureIdType(id.getKey()), id.getValue()));
                }
                insertResults = new org.geotoolkit.wfs.xml.v110.InsertResultsType(ift);
            }
            final org.geotoolkit.wfs.xml.v110.TransactionSummaryType ts = new org.geotoolkit.wfs.xml.v110.TransactionSummaryType(totalInserted, totalUpdated, totalDeleted);
            return new org.geotoolkit.wfs.xml.v110.TransactionResponseType(ts, null, insertResults, version);
        } else if (v100.equals(version)) {
            final List<org.geotoolkit.wfs.xml.v100.InsertResultType> ift = new ArrayList<>();
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
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                throw new IllegalArgumentException("The operation GetGmlObject is not available in version:" + version);
            case"1.1.0":
                final org.geotoolkit.ogc.xml.v110.GmlObjectIdType gmlObjectId = new org.geotoolkit.ogc.xml.v110.GmlObjectIdType(id);
                return new org.geotoolkit.wfs.xml.v110.GetGmlObjectType(service, version, handle, gmlObjectId, outputFormat);
            case"1.0.0":
                throw new IllegalArgumentException("The operation GetGmlObject is not available in version:" + version);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DescribeFeatureType buildDecribeFeatureType(final String version, final String service, final String handle, final List<QName> typeNames, String outputFormat) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (outputFormat == null) {
                    outputFormat = "application/gml+xml; version=3.2";
                }
                return new org.geotoolkit.wfs.xml.v200.DescribeFeatureTypeType(service, version, handle, typeNames, outputFormat);
            case"1.1.0":
                if (outputFormat == null) {
                    outputFormat = "text/xml; subtype=gml/3.1.1";
                }
                return new org.geotoolkit.wfs.xml.v110.DescribeFeatureTypeType(service, version, handle, typeNames, outputFormat);
            case"1.0.0":
                if (outputFormat == null) {
                    outputFormat = "XMLSCHEMA";
                }
                return new org.geotoolkit.wfs.xml.v100.DescribeFeatureTypeType(service, version, typeNames, outputFormat);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DeleteElement buildDeleteElement(final String version, final XMLFilter filter, final String handle, final QName typeName) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v200.FilterType)) {
                    throw new IllegalArgumentException("unexpected object version for filter element");
                }
                return new org.geotoolkit.wfs.xml.v200.DeleteType((org.geotoolkit.ogc.xml.v200.FilterType)filter, handle, typeName);
            case"1.1.0":
                if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v110.FilterType)) {
                    throw new IllegalArgumentException("unexpected object version for filter element");
                }
                return new org.geotoolkit.wfs.xml.v110.DeleteElementType((org.geotoolkit.ogc.xml.v110.FilterType)filter, handle, typeName);
            case"1.0.0":
                if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v100.FilterType)) {
                    throw new IllegalArgumentException("unexpected object version for filter element");
                }
                return new org.geotoolkit.wfs.xml.v100.DeleteElementType((org.geotoolkit.ogc.xml.v100.FilterType)filter, handle, typeName);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ReplaceElement buildReplaceElement(final String version, final String inputFormat, final String srsName, final XMLFilter filter, final Object any) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v200.FilterType)) {
                    throw new IllegalArgumentException("unexpected object version for filter element");
                }
                return new org.geotoolkit.wfs.xml.v200.ReplaceType(inputFormat, (org.geotoolkit.ogc.xml.v200.FilterType)filter, any, inputFormat, srsName);
            case"1.1.0":
                throw new UnsupportedOperationException("not supported for version 1.1.0");
            case"1.0.0":
                throw new UnsupportedOperationException("not supported for version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Property buildProperty(final String version, final String propName, final Object value) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.PropertyType(new ValueReference(propName, null), value);
            case"1.1.0":
                return new org.geotoolkit.wfs.xml.v110.PropertyType(new QName(propName), new ValueType(value));
            case"1.0.0":
                return new org.geotoolkit.wfs.xml.v100.PropertyType(propName, value);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static UpdateElement buildUpdateElement(final String version, final String inputFormat, final String srsName, final XMLFilter filter, final QName typeName, final List<Property> properties) {
        if (v200.equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v200.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            final List<org.geotoolkit.wfs.xml.v200.PropertyType> ift = new ArrayList<>();
            if (properties != null) {
                for (Property p : properties) {
                    ift.add((org.geotoolkit.wfs.xml.v200.PropertyType)p);
                }
            }
            return new org.geotoolkit.wfs.xml.v200.UpdateType(inputFormat, ift,
                                                              (org.geotoolkit.ogc.xml.v200.FilterType)filter, typeName, srsName);
        } else if (v110.equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v110.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            final List<org.geotoolkit.wfs.xml.v110.PropertyType> ift = new ArrayList<>();
            if (properties != null) {
                for (Property p : properties) {
                    ift.add((org.geotoolkit.wfs.xml.v110.PropertyType)p);
                }
            }
            return new org.geotoolkit.wfs.xml.v110.UpdateElementType(inputFormat, ift,
                                                                    (org.geotoolkit.ogc.xml.v110.FilterType)filter, typeName, srsName);
        } else if (v100.equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v100.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            }
            final List<org.geotoolkit.wfs.xml.v100.PropertyType> ift = new ArrayList<>();
            if (properties != null) {
                for (Property p : properties) {
                    ift.add((org.geotoolkit.wfs.xml.v100.PropertyType)p);
                }
            }
            return new org.geotoolkit.wfs.xml.v100.UpdateElementType(ift, (org.geotoolkit.ogc.xml.v100.FilterType)filter, typeName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static InsertElement buildInsertElement(final String version, final String inputFormat, final String srsName, final Object any) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.InsertType(inputFormat, srsName, any);
            case"1.1.0":
                return new org.geotoolkit.wfs.xml.v110.InsertElementType(inputFormat, srsName, any);
            case"1.0.0":
                // TODO
                return new org.geotoolkit.wfs.xml.v100.InsertElementType();
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Transaction buildTransaction(final String version,  final String service, final String handle, final AllSomeType releaseAction, final DeleteElement deleteElement) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (deleteElement != null && !(deleteElement instanceof org.geotoolkit.wfs.xml.v200.DeleteType)) {
                    throw new IllegalArgumentException("unexpected object version for delete element");
                }
                return new org.geotoolkit.wfs.xml.v200.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v200.DeleteType)deleteElement);
            case"1.1.0":
                if (deleteElement != null && !(deleteElement instanceof org.geotoolkit.wfs.xml.v110.DeleteElementType)) {
                    throw new IllegalArgumentException("unexpected object version for delete element");
                }
                return new org.geotoolkit.wfs.xml.v110.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v110.DeleteElementType)deleteElement);
            case"1.0.0":
                if (deleteElement != null && !(deleteElement instanceof org.geotoolkit.wfs.xml.v100.DeleteElementType)) {
                    throw new IllegalArgumentException("unexpected object version for delete element");
                }
                return new org.geotoolkit.wfs.xml.v100.TransactionType(service, version, handle, releaseAction,  (org.geotoolkit.wfs.xml.v100.DeleteElementType)deleteElement);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Transaction buildTransaction(final String version,  final String service, final String handle, final AllSomeType releaseAction, final UpdateElement updateElement) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (updateElement != null && !(updateElement instanceof org.geotoolkit.wfs.xml.v200.UpdateType)) {
                    throw new IllegalArgumentException("unexpected object version for update element");
                }
                return new org.geotoolkit.wfs.xml.v200.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v200.UpdateType)updateElement);
            case"1.1.0":
                if (updateElement != null && !(updateElement instanceof org.geotoolkit.wfs.xml.v110.UpdateElementType)) {
                    throw new IllegalArgumentException("unexpected object version for update element");
                }
                return new org.geotoolkit.wfs.xml.v110.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v110.UpdateElementType)updateElement);
            case"1.0.0":
                if (updateElement != null && !(updateElement instanceof org.geotoolkit.wfs.xml.v100.UpdateElementType)) {
                    throw new IllegalArgumentException("unexpected object version for update element");
                }
                return new org.geotoolkit.wfs.xml.v100.TransactionType(service, version, handle, releaseAction,  (org.geotoolkit.wfs.xml.v100.UpdateElementType)updateElement);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Transaction buildTransaction(final String version,  final String service, final String handle, final AllSomeType releaseAction, final InsertElement insertElement) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (insertElement != null && !(insertElement instanceof org.geotoolkit.wfs.xml.v200.InsertType)) {
                    throw new IllegalArgumentException("unexpected object version for delete element");
                }
                return new org.geotoolkit.wfs.xml.v200.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v200.InsertType)insertElement);
            case"1.1.0":
                if (insertElement != null && !(insertElement instanceof org.geotoolkit.wfs.xml.v110.InsertElementType)) {
                    throw new IllegalArgumentException("unexpected object version for delete element");
                }
                return new org.geotoolkit.wfs.xml.v110.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v110.InsertElementType)insertElement);
            case"1.0.0":
                if (insertElement != null && !(insertElement instanceof org.geotoolkit.wfs.xml.v100.InsertElementType)) {
                    throw new IllegalArgumentException("unexpected object version for delete element");
                }
                return new org.geotoolkit.wfs.xml.v100.TransactionType(service, version, handle, releaseAction,  (org.geotoolkit.wfs.xml.v100.InsertElementType)insertElement);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

     public static Transaction buildTransaction(final String version,  final String service, final String handle, final AllSomeType releaseAction, final ReplaceElement replaceElement) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (replaceElement != null && !(replaceElement instanceof org.geotoolkit.wfs.xml.v200.ReplaceType)) {
                    throw new IllegalArgumentException("unexpected object version for replace element");
                }
                return new org.geotoolkit.wfs.xml.v200.TransactionType(service, version, handle, releaseAction, (org.geotoolkit.wfs.xml.v200.ReplaceType)replaceElement);
            case"1.1.0":
                throw new IllegalArgumentException("ReplaceElement is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("ReplaceElement is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static LockFeature buildLockFeature(final String version,  final String service, final String handle, final AllSomeType releaseAction, final XMLFilter filter,
            final QName typeName, final int expiry) {
        if (v200.equals(version)) {
            final List<org.geotoolkit.wfs.xml.v200.QueryType> queries = new ArrayList<>();
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v200.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            } else if (filter != null){
                final org.geotoolkit.wfs.xml.v200.QueryType query = new org.geotoolkit.wfs.xml.v200.QueryType((org.geotoolkit.ogc.xml.v200.FilterType)filter, Arrays.asList(typeName), handle);
                queries.add(query);
            }
            return new org.geotoolkit.wfs.xml.v200.LockFeatureType(service, version, handle, queries, expiry, releaseAction);
        } else if (v110.equals(version)) {
            final List<org.geotoolkit.wfs.xml.v110.LockType> queries = new ArrayList<>();
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v110.FilterType)) {
                throw new IllegalArgumentException("unexpected object version for filter element");
            } else if (filter != null){
                final org.geotoolkit.wfs.xml.v110.LockType query = new org.geotoolkit.wfs.xml.v110.LockType((org.geotoolkit.ogc.xml.v110.FilterType)filter, handle, typeName);
                queries.add(query);
            }
            return new org.geotoolkit.wfs.xml.v110.LockFeatureType(service, version, handle, queries, expiry, releaseAction);
        } else if (v100.equals(version)) {
            final List<org.geotoolkit.wfs.xml.v100.LockType> queries = new ArrayList<>();
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
        if (v200.equals(version)) {
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
        } else if (v110.equals(version)) {
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
        } else if (v100.equals(version)) {
            return new org.geotoolkit.wfs.xml.v100.GetCapabilitiesType(service, version);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AcceptVersions buildAcceptVersion(final String version, final List<String> acceptVersion) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildAcceptVersion("1.1.0", acceptVersion);
            case"1.1.0":
                return OWSXmlFactory.buildAcceptVersion("1.0.0", acceptVersion);
            case"1.0.0":
                return OWSXmlFactory.buildAcceptVersion("1.0.0", acceptVersion);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AcceptFormats buildAcceptFormat(final String version, final List<String> acceptFormats) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildAcceptFormat("1.1.0", acceptFormats);
            case"1.1.0":
                return OWSXmlFactory.buildAcceptFormat("1.0.0", acceptFormats);
            case"1.0.0":
                return OWSXmlFactory.buildAcceptFormat("1.0.0", acceptFormats);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Sections buildSections(final String version, final List<String> sections) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildSections("1.1.0", sections);
            case"1.1.0":
                return OWSXmlFactory.buildSections("1.0.0", sections);
            case"1.0.0":
                return OWSXmlFactory.buildSections("1.0.0", sections);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static StoredQuery buildStoredQuery(final String version, final String id, final String handle, final List<Parameter> parameters) {
        if (v200.equals(version)) {
            final List<org.geotoolkit.wfs.xml.v200.ParameterType> params = new ArrayList<>();
            if (parameters != null) {
                for (Parameter p : parameters) {
                    if (!(p instanceof org.geotoolkit.wfs.xml.v200.ParameterType)) {
                        throw new IllegalArgumentException("bad object version for parameter");
                    }
                    params.add((org.geotoolkit.wfs.xml.v200.ParameterType)p);
                }
            }
            return new org.geotoolkit.wfs.xml.v200.StoredQueryType(id, handle, params);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Query buildQuery(final String version, final XMLFilter filter, final List<QName> typeNames, final String featureVersion,
            final String srsName, final SortBy sortBy, final List<String> propertyNames) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
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
            case"1.1.0":
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
            case"1.0.0":
                if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v100.FilterType)) {
                    throw new IllegalArgumentException("unexpected object version for filter element");
                }
                QName typeName = null;
                if (typeNames != null && !typeNames.isEmpty()) {
                    typeName = typeNames.get(0);
                }
                return new org.geotoolkit.wfs.xml.v100.QueryType((org.geotoolkit.ogc.xml.v100.FilterType) filter,
                        typeName, featureVersion, propertyNames);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static SortBy buildSortBy(final String version, final String sortByParam, final SortOrder order) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
            {
                final List<org.geotoolkit.ogc.xml.v200.SortPropertyType> sortProperties = new ArrayList<>();
                sortProperties.add(new org.geotoolkit.ogc.xml.v200.SortPropertyType(sortByParam, order));
                return new org.geotoolkit.ogc.xml.v200.SortByType(sortProperties);
            }
            case"1.1.0":
            {
                final List<org.geotoolkit.ogc.xml.v110.SortPropertyType> sortProperties = new ArrayList<>();
                sortProperties.add(new org.geotoolkit.ogc.xml.v110.SortPropertyType(sortByParam, order));
                return new org.geotoolkit.ogc.xml.v110.SortByType(sortProperties);
            }
            case"1.0.0":
                return null;
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static XMLFilter buildBBOXFilter(final String version, String propertyName, double minx, double miny, double maxx, double maxy, String srs) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
            {
                final  org.geotoolkit.ogc.xml.v200.BBOXType bbox = new org.geotoolkit.ogc.xml.v200.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
                return new org.geotoolkit.ogc.xml.v200.FilterType(bbox);
            }
            case"1.1.0":
            {
                final  org.geotoolkit.ogc.xml.v110.BBOXType bbox = new org.geotoolkit.ogc.xml.v110.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
                return new org.geotoolkit.ogc.xml.v110.FilterType(bbox);
            }
            case"1.0.0":
            {
                final  org.geotoolkit.ogc.xml.v100.BBOXType bbox = new org.geotoolkit.ogc.xml.v100.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
                return new org.geotoolkit.ogc.xml.v100.FilterType(bbox);
            }
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Parameter buildParameter(final String version, final String name, final Object value) {
        if (v200.equals(version)) {
            return new org.geotoolkit.wfs.xml.v200.ParameterType(name, value);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ParameterExpression buildParameterDescription(final String version, final String name, final QName type) {
        if (v200.equals(version)) {
            return new org.geotoolkit.wfs.xml.v200.ParameterExpressionType(name, name, null, type);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetFeature buildGetFeature(final String version, final String service, final String handle, final Integer startIndex, final Integer maxFeature,
        final Query query, final ResultTypeType resultType, String outputFormat) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v200.QueryType)) {
                    throw new IllegalArgumentException("unexpected object version for query element");
                }
                return new org.geotoolkit.wfs.xml.v200.GetFeatureType(service, version, handle, startIndex, maxFeature,
                        Arrays.asList((org.geotoolkit.wfs.xml.v200.QueryType)query),
                        resultType, outputFormat);
            case"1.1.0":
                if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v110.QueryType)) {
                    throw new IllegalArgumentException("unexpected object version for query element");
                }
                return new org.geotoolkit.wfs.xml.v110.GetFeatureType(service, version, handle, maxFeature,
                        Arrays.asList((org.geotoolkit.wfs.xml.v110.QueryType)query),
                        resultType, outputFormat);
            case"1.0.0":
                if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v100.QueryType)) {
                    throw new IllegalArgumentException("unexpected object version for query element");
                }
                return new org.geotoolkit.wfs.xml.v100.GetFeatureType(service, version, handle, maxFeature,
                        Arrays.asList((org.geotoolkit.wfs.xml.v100.QueryType)query),
                        outputFormat);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetFeature buildGetFeature(final String version, final String service, final String handle, final Integer startIndex, final Integer maxFeature,
        final StoredQuery query, final ResultTypeType resultType, String outputFormat) {
        if (v200.equals(version)) {
            if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v200.StoredQueryType)) {
                throw new IllegalArgumentException("unexpected object version for query element");
            }
            return new org.geotoolkit.wfs.xml.v200.GetFeatureType(service, version, handle,
                                                                  Arrays.asList((org.geotoolkit.wfs.xml.v200.StoredQueryType)query),
                                                                  startIndex,
                                                                  maxFeature,
                                                                  resultType, outputFormat);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ListStoredQueries buildListStoredQueries(final String version, final String service, final String handle) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.ListStoredQueriesType(service, version, handle);
            case"1.1.0":
                throw new IllegalArgumentException("ListStoredQueries is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("ListStoredQueries is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static StoredQueryDescription buildStoredQueryDescription(final String version, final String id, final Query query, final List<ParameterExpression> parameters) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                org.geotoolkit.wfs.xml.v200.QueryExpressionTextType queryEx = null;
                if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v200.QueryType)) {
                    throw new IllegalArgumentException("unexpected object version for query element");
                } else if (query != null) {
                    final org.geotoolkit.wfs.xml.v200.QueryType query200 = (org.geotoolkit.wfs.xml.v200.QueryType) query;
                    queryEx = new org.geotoolkit.wfs.xml.v200.QueryExpressionTextType("urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression", query200, query200.getTypeNames());
                }
                final List<org.geotoolkit.wfs.xml.v200.ParameterExpressionType> parameters200 = new ArrayList<>();
                for (ParameterExpression param : parameters) {
                    if (!(param instanceof org.geotoolkit.wfs.xml.v200.ParameterExpressionType)) {
                        throw new IllegalArgumentException("unexpected object version for parameter element");
                    }
                    parameters200.add((org.geotoolkit.wfs.xml.v200.ParameterExpressionType)param);
                }
                return new org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType(id, null, null, parameters200, queryEx);
            case"1.1.0":
                throw new IllegalArgumentException("ListStoredQueries is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("ListStoredQueries is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
    public static CreateStoredQuery buildCreateStoredQuery(final String version, final String service, final String handle, final List<StoredQueryDescription> queryDescriptions) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                final List<org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType> storedQuery = new ArrayList<>();
                for (StoredQueryDescription description : queryDescriptions) {
                    if (!(description instanceof org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType)) {
                        throw new IllegalArgumentException("unexpected object version for queryDescription element");
                    }
                    storedQuery.add((org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType)description);
                }
                return new org.geotoolkit.wfs.xml.v200.CreateStoredQueryType(service, version, handle, storedQuery);
            case"1.1.0":
                throw new IllegalArgumentException("ListStoredQueries is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("ListStoredQueries is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DescribeStoredQueries buildDescribeStoredQueries(final String version, final String service, final String handle, final List<String> storedQueryId) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.DescribeStoredQueriesType(service, version, handle, storedQueryId);
            case"1.1.0":
                throw new IllegalArgumentException("DescribeStoredQueries is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("DescribeStoredQueries is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetPropertyValue buildGetPropertyValue(final String version, final String service, final String handle, final Integer startIndex, final Integer maxFeature,
        final Query query, final ResultTypeType resultType, final String outputFormat, final String valueReference) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                if (query != null && !(query instanceof org.geotoolkit.wfs.xml.v200.QueryType)) {
                    throw new IllegalArgumentException("unexpected object version for query element");
                }
                return new org.geotoolkit.wfs.xml.v200.GetPropertyValueType(service, version, handle, startIndex, maxFeature,
                        (org.geotoolkit.wfs.xml.v200.QueryType)query, resultType, outputFormat, valueReference);
            case"1.1.0":
                throw new IllegalArgumentException("GetPropertyValue is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("GetPropertyValue is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DropStoredQuery buildDropStoredQuery(final String version, final String service, final String handle, final String id) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.DropStoredQueryType(service, version, handle, id);
            case"1.1.0":
                throw new IllegalArgumentException("DropStoredQuery is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("DropStoredQuery is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ListStoredQueriesResponse buildListStoredQueriesResponse(final String version, final List<StoredQueryDescription> descriptions) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                final List<org.geotoolkit.wfs.xml.v200.StoredQueryListItemType> storedQuery = new ArrayList<>();
                for (StoredQueryDescription description : descriptions) {
                    final List<QName> returnTypes = new ArrayList<>();
                    for (QueryExpressionText queryEx : description.getQueryExpressionText()) {
                        returnTypes.addAll(queryEx.getReturnFeatureTypes());
                    }
                    if (returnTypes.isEmpty()) {
                        returnTypes.add(new QName(""));
                    }
                    storedQuery.add(new org.geotoolkit.wfs.xml.v200.StoredQueryListItemType(description.getId(),
                            (List<org.geotoolkit.wfs.xml.v200.Title>)description.getTitle(),
                            returnTypes));
                }
                return new org.geotoolkit.wfs.xml.v200.ListStoredQueriesResponseType(storedQuery);
            case"1.1.0":
                throw new IllegalArgumentException("StoredQueryListItem is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("StoredQueryListItem is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DescribeStoredQueriesResponse buildDescribeStoredQueriesResponse(final String version, final List<StoredQueryDescription> descriptions) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                final List<org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType> storedQuery = new ArrayList<>();
                for (StoredQueryDescription description : descriptions) {
                    if (description instanceof org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType) {
                        storedQuery.add((org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType) description);
                    } else {
                        throw new IllegalArgumentException("unexpected object version for StoredQueryDescription element");
                    }
                }
                return new org.geotoolkit.wfs.xml.v200.DescribeStoredQueriesResponseType(storedQuery);
            case"1.1.0":
                throw new IllegalArgumentException("DescribeStoredQueriesResponse is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("DescribeStoredQueriesResponse is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static CreateStoredQueryResponse buildCreateStoredQueryResponse(final String version, final String status) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.CreateStoredQueryResponseType(status);
            case"1.1.0":
                throw new IllegalArgumentException("CreateStoredQueryResponse is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("CreateStoredQueryResponse is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DropStoredQueryResponse buildDropStoredQueryResponse(final String version, final String status) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return new org.geotoolkit.wfs.xml.v200.DropStoredQueryResponseType(status);
            case"1.1.0":
                throw new IllegalArgumentException("CreateStoredQueryResponse is not available in version 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("CreateStoredQueryResponse is not available in version 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
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

    public static AbstractServiceIdentification buildServiceIdentification(final String version, final String title, final String _abstract,
            final List<String> keywords, final String serviceType, final List<String> serviceVersion, final String fees, final List<String> accessConstraint) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildServiceIdentification("1.1.0", title, _abstract, keywords, serviceType, serviceVersion, fees, accessConstraint);
            case"1.1.0":
                return OWSXmlFactory.buildServiceIdentification("1.0.0", title, _abstract, keywords, serviceType, serviceVersion, fees, accessConstraint);
            case"1.0.0":
                return OWSXmlFactory.buildServiceIdentification("1.0.0", title, _abstract, keywords, serviceType, serviceVersion, fees, accessConstraint);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AbstractContact buildContact(final String version, final String phone, final String fax, final String email,
            final String address, final String city, final String state,
            final String zipCode, final String country, final String hoursOfService, final String contactInstructions) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildContact("1.1.0", phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
            case"1.1.0":
                return OWSXmlFactory.buildContact("1.0.0", phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
            case"1.0.0":
                return OWSXmlFactory.buildContact("1.0.0", phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AbstractOnlineResourceType buildOnlineResource(final String version, final String url) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildOnlineResource("1.1.0", url);
            case"1.1.0":
                return OWSXmlFactory.buildOnlineResource("1.0.0", url);
            case"1.0.0":
                return OWSXmlFactory.buildOnlineResource("1.0.0", url);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AbstractResponsiblePartySubset buildResponsiblePartySubset(final String version, final String individualName, final String positionName,
            final AbstractContact contact, final String role) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildResponsiblePartySubset("1.1.0", individualName, positionName, contact, role);
            case"1.1.0":
                return OWSXmlFactory.buildResponsiblePartySubset("1.0.0", individualName, positionName, contact, role);
            case"1.0.0":
                return OWSXmlFactory.buildResponsiblePartySubset("1.0.0", individualName, positionName, contact, role);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AbstractServiceProvider buildServiceProvider(final String version, final String providerName,
            final AbstractOnlineResourceType providerSite, final AbstractResponsiblePartySubset serviceContact) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildServiceProvider("1.1.0", providerName, providerSite, serviceContact);
            case"1.1.0":
                return OWSXmlFactory.buildServiceProvider("1.0.0", providerName, providerSite, serviceContact);
            case"1.0.0":
                return OWSXmlFactory.buildServiceProvider("1.0.0", providerName, providerSite, serviceContact);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AbstractOperation buildOperation(final String version, final List<AbstractDCP> dcps,
            final List<AbstractDomain> parameters, final List<AbstractDomain> constraints, final String name) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildOperation("1.1.0", dcps, parameters, constraints, name);
            case"1.1.0":
                return OWSXmlFactory.buildOperation("1.0.0", dcps, parameters, constraints, name);
            case"1.0.0":
                return OWSXmlFactory.buildOperation("1.0.0", dcps, parameters, constraints, name);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AbstractDomain buildDomain(final String version, final String name, final List<String> allowedValues) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildDomain("1.1.0", name, allowedValues);
            case"1.1.0":
                return OWSXmlFactory.buildDomain("1.0.0", name, allowedValues);
            case"1.0.0":
                return OWSXmlFactory.buildDomain("1.0.0", name, allowedValues);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AbstractDomain buildDomainNoValues(final String version, final String name, final String values) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildDomainNoValues("1.1.0", name, values);
            case"1.1.0":
                throw new IllegalArgumentException("Novalues not supported in 1.1.0");
            case"1.0.0":
                throw new IllegalArgumentException("Novalues not supported in 1.0.0");
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static AbstractDCP buildDCP(final String version, final String getURL, final String postURL) {
        if (null == version) {
            throw new IllegalArgumentException("unexpected version number:" + version);
        } else switch (version) {
            case"2.0.0":
                return OWSXmlFactory.buildDCP("1.1.0", getURL, postURL);
            case"1.1.0":
                return OWSXmlFactory.buildDCP("1.0.0", getURL, postURL);
            case"1.0.0":
                return OWSXmlFactory.buildDCP("1.0.0", getURL, postURL);
            default:
                throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
}
