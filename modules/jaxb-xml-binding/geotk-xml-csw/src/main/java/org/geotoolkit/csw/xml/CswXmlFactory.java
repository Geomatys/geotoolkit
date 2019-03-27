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
package org.geotoolkit.csw.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.SortBy;
import org.geotoolkit.ows.xml.AbstractDomain;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.ExceptionResponse;
import org.geotoolkit.ows.xml.OWSXmlFactory;
import org.geotoolkit.ows.xml.Sections;
import org.opengis.filter.Filter;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.sort.SortOrder;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class CswXmlFactory {

    public static GetDomainResponse getDomainResponse(final String version, final List<DomainValues> domainValues) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.GetDomainResponseType(domainValues);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.GetDomainResponseType(domainValues);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.GetDomainResponseType(domainValues);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static DomainValues getDomainValues(final String version, final String parameterName, final String propertyName, final List<? extends Object> listOfValues, final QName type) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.DomainValuesType(parameterName, null, (List<String>) listOfValues, type);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.DomainValuesType(parameterName, null, (List<String>) listOfValues, type);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.DomainValuesType(parameterName, null, (List<Object>) listOfValues, type);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static AbstractDomain createDomain(final String version, final String name, final List<String> allowedValues) {
        if ("2.0.2".equals(version) || "2.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v100.DomainType(name, allowedValues);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v200.DomainType(name, allowedValues);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static ElementSetName createElementSetName(final String version, final ElementSetType elementSet) {
        if (elementSet == null) {return null;}
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.ElementSetNameType(elementSet);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.ElementSetNameType(elementSet);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.ElementSetNameType(elementSet);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static DistributedSearch createDistributedSearch(final String version, final Integer stepBeyond) {
        if (stepBeyond == null) {return null;}
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.DistributedSearchType(stepBeyond);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.DistributedSearchType(stepBeyond);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.DistributedSearchType(stepBeyond);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static SortBy createSortBy(final String version, final Map<String, SortOrder> sortRules) {
        if ("2.0.2".equals(version)) {
            if (sortRules != null && !sortRules.isEmpty()) {
                final List<org.geotoolkit.ogc.xml.v110.SortPropertyType> rules = new ArrayList<>();
                final Set<Entry<String, SortOrder>> entries = sortRules.entrySet();
                for (final Entry<String, SortOrder> entry : entries) {
                    rules.add(new org.geotoolkit.ogc.xml.v110.SortPropertyType(entry.getKey(), entry.getValue()));
                }
                return new org.geotoolkit.ogc.xml.v110.SortByType(rules);
            }
            return null;
        } else if ("3.0.0".equals(version)) {
            if (sortRules != null && !sortRules.isEmpty()) {
                final List<org.geotoolkit.ogc.xml.v200.SortPropertyType> rules = new ArrayList<>();
                final Set<Entry<String, SortOrder>> entries = sortRules.entrySet();
                for (final Entry<String, SortOrder> entry : entries) {
                    rules.add(new org.geotoolkit.ogc.xml.v200.SortPropertyType(entry.getKey(), entry.getValue()));
                }
                return new org.geotoolkit.ogc.xml.v200.SortByType(rules);
            }
            return null;
        } else if ("2.0.0".equals(version)) {
            return null;
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static QueryConstraint createQueryConstraint(final String version, final Filter filter, final String filterVersion) {
        if ("2.0.2".equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v110.FilterType)) {
                 throw new IllegalArgumentException("bad version of filter.");
            }
            return new org.geotoolkit.csw.xml.v202.QueryConstraintType((org.geotoolkit.ogc.xml.v110.FilterType) filter, filterVersion);
        } else if ("2.0.0".equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v110.FilterType)) {
                 throw new IllegalArgumentException("bad version of filter.");
            }
            return new org.geotoolkit.csw.xml.v200.QueryConstraintType((org.geotoolkit.ogc.xml.v110.FilterType) filter, filterVersion);
        } else if ("3.0.0".equals(version)) {
            if (filter != null && !(filter instanceof org.geotoolkit.ogc.xml.v200.FilterType)) {
                 throw new IllegalArgumentException("bad version of filter.");
            }
            return new org.geotoolkit.csw.xml.v300.QueryConstraintType((org.geotoolkit.ogc.xml.v200.FilterType) filter, filterVersion);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static QueryConstraint createQueryConstraint(final String version, final String cqlText, final String filterVersion) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.QueryConstraintType(cqlText, filterVersion);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.QueryConstraintType(cqlText, filterVersion);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.QueryConstraintType(cqlText, filterVersion);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static Query createQuery(final String version, final List<QName> typeNames, final ElementSetName elementSet, final SortBy sortBy,
            final QueryConstraint constraint) {

        if ("2.0.2".equals(version)) {
            if (elementSet != null && !(elementSet instanceof org.geotoolkit.csw.xml.v202.ElementSetNameType)) {
                 throw new IllegalArgumentException("bad version of elementset.");
            }
            if (sortBy != null && !(sortBy instanceof org.geotoolkit.ogc.xml.v110.SortByType)) {
                 throw new IllegalArgumentException("bad version of sortBy.");
            }
            if (constraint != null && !(constraint instanceof org.geotoolkit.csw.xml.v202.QueryConstraintType)) {
                 throw new IllegalArgumentException("bad version of constraint.");
            }
            return new org.geotoolkit.csw.xml.v202.QueryType(typeNames,
                                                             (org.geotoolkit.csw.xml.v202.ElementSetNameType)elementSet,
                                                             (org.geotoolkit.ogc.xml.v110.SortByType)sortBy,
                                                             (org.geotoolkit.csw.xml.v202.QueryConstraintType)constraint);
        } else if ("2.0.0".equals(version)) {
            if (elementSet != null && !(elementSet instanceof org.geotoolkit.csw.xml.v200.ElementSetNameType)) {
                 throw new IllegalArgumentException("bad version of elementset.");
            }
            if (constraint != null && !(constraint instanceof org.geotoolkit.csw.xml.v200.QueryConstraintType)) {
                 throw new IllegalArgumentException("bad version of constraint.");
            }
            return new org.geotoolkit.csw.xml.v200.QueryType(typeNames,
                                                             (org.geotoolkit.csw.xml.v200.ElementSetNameType)elementSet,
                                                             (org.geotoolkit.csw.xml.v200.QueryConstraintType)constraint);
        } else if ("3.0.0".equals(version)) {
            if (elementSet != null && !(elementSet instanceof org.geotoolkit.csw.xml.v300.ElementSetNameType)) {
                 throw new IllegalArgumentException("bad version of elementset.");
            }
            if (sortBy != null && !(sortBy instanceof org.geotoolkit.ogc.xml.v200.SortByType)) {
                 throw new IllegalArgumentException("bad version of sortBy.");
            }
            if (constraint != null && !(constraint instanceof org.geotoolkit.csw.xml.v300.QueryConstraintType)) {
                 throw new IllegalArgumentException("bad version of constraint.");
            }
            return new org.geotoolkit.csw.xml.v300.QueryType(typeNames,
                                                             (org.geotoolkit.csw.xml.v300.ElementSetNameType)elementSet,
                                                             (org.geotoolkit.ogc.xml.v200.SortByType)sortBy,
                                                             (org.geotoolkit.csw.xml.v300.QueryConstraintType)constraint);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static GetRecordsRequest createGetRecord(final String version, final String service, final ResultType resultType, final String requestId,
            final String outputFormat, final String outputSchema, final Integer startPosition, final Integer maxRecords, final AbstractQuery abstractQuery,
            final DistributedSearch distributedSearch) {

        if ("2.0.2".equals(version)) {
            if (abstractQuery != null && !(abstractQuery instanceof org.geotoolkit.csw.xml.v202.AbstractQueryType)) {
                 throw new IllegalArgumentException("bad version of abstractQuery.");
            }
            if (distributedSearch != null && !(distributedSearch instanceof org.geotoolkit.csw.xml.v202.DistributedSearchType)) {
                 throw new IllegalArgumentException("bad version of distributedSearch.");
            }
            return new org.geotoolkit.csw.xml.v202.GetRecordsType(service, version, resultType, requestId, outputFormat, outputSchema, startPosition, maxRecords,
                                                                  (org.geotoolkit.csw.xml.v202.AbstractQueryType)abstractQuery,
                                                                  (org.geotoolkit.csw.xml.v202.DistributedSearchType)distributedSearch);
        } else if ("2.0.0".equals(version)) {
            if (abstractQuery != null && !(abstractQuery instanceof org.geotoolkit.csw.xml.v200.AbstractQueryType)) {
                 throw new IllegalArgumentException("bad version of abstractQuery.");
            }
            if (distributedSearch != null && !(distributedSearch instanceof org.geotoolkit.csw.xml.v200.DistributedSearchType)) {
                 throw new IllegalArgumentException("bad version of distributedSearch.");
            }
            return new org.geotoolkit.csw.xml.v200.GetRecordsType(service, version, resultType, requestId, outputFormat, outputSchema, startPosition, maxRecords,
                                                                  (org.geotoolkit.csw.xml.v200.AbstractQueryType)abstractQuery,
                                                                  (org.geotoolkit.csw.xml.v200.DistributedSearchType)distributedSearch);
        } else if ("3.0.0".equals(version)) {
            if (abstractQuery != null && !(abstractQuery instanceof org.geotoolkit.csw.xml.v300.AbstractQueryType)) {
                 throw new IllegalArgumentException("bad version of abstractQuery.");
            }
            if (distributedSearch != null && !(distributedSearch instanceof org.geotoolkit.csw.xml.v300.DistributedSearchType)) {
                 throw new IllegalArgumentException("bad version of distributedSearch.");
            }
            return new org.geotoolkit.csw.xml.v300.GetRecordsType(service, version, requestId, outputFormat, outputSchema, startPosition, maxRecords,
                                                                  (org.geotoolkit.csw.xml.v300.AbstractQueryType)abstractQuery,
                                                                  (org.geotoolkit.csw.xml.v300.DistributedSearchType)distributedSearch);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static AbstractCapabilities createCapabilities(final String version,
                                                          final AbstractServiceIdentification serviceIdentification,
                                                          final AbstractServiceProvider serviceProvider,
                                                          final AbstractOperationsMetadata operationsMetadata,
                                                          final String updateSequence,
                                                          final FilterCapabilities filterCapabilities) {
        if ("2.0.2".equals(version) || "2.0.0".equals(version)) {
            if (serviceIdentification != null && !(serviceIdentification instanceof org.geotoolkit.ows.xml.v100.ServiceIdentification)) {
                 throw new IllegalArgumentException("bad version of serviceIdentification.");
            }
            if (serviceProvider != null && !(serviceProvider instanceof org.geotoolkit.ows.xml.v100.ServiceProvider)) {
                 throw new IllegalArgumentException("bad version of serviceProvider.");
            }
            if (operationsMetadata != null && !(operationsMetadata instanceof org.geotoolkit.ows.xml.v100.OperationsMetadata)) {
                 throw new IllegalArgumentException("bad version of operationsMetadata.");
            }
            if (filterCapabilities != null && !(filterCapabilities instanceof org.geotoolkit.ogc.xml.v110.FilterCapabilities)) {
                 throw new IllegalArgumentException("bad version of filterCapabilities.");
            }

            if ("2.0.2".equals(version)) {
                return new org.geotoolkit.csw.xml.v202.Capabilities((org.geotoolkit.ows.xml.v100.ServiceIdentification)serviceIdentification,
                                                                    (org.geotoolkit.ows.xml.v100.ServiceProvider)serviceProvider,
                                                                    (org.geotoolkit.ows.xml.v100.OperationsMetadata)operationsMetadata,
                                                                    version, updateSequence,
                                                                    (org.geotoolkit.ogc.xml.v110.FilterCapabilities)filterCapabilities);
            } else {
                return new org.geotoolkit.csw.xml.v200.CapabilitiesType((org.geotoolkit.ows.xml.v100.ServiceIdentification)serviceIdentification,
                                                                        (org.geotoolkit.ows.xml.v100.ServiceProvider)serviceProvider,
                                                                        (org.geotoolkit.ows.xml.v100.OperationsMetadata)operationsMetadata,
                                                                        version, updateSequence);
            }

        } else  if ("3.0.0".equals(version)) {
            if (serviceIdentification != null && !(serviceIdentification instanceof org.geotoolkit.ows.xml.v200.ServiceIdentification)) {
                 throw new IllegalArgumentException("bad version of serviceIdentification.");
            }
            if (serviceProvider != null && !(serviceProvider instanceof org.geotoolkit.ows.xml.v200.ServiceProvider)) {
                 throw new IllegalArgumentException("bad version of serviceProvider.");
            }
            if (operationsMetadata != null && !(operationsMetadata instanceof org.geotoolkit.ows.xml.v200.OperationsMetadata)) {
                 throw new IllegalArgumentException("bad version of operationsMetadata.");
            }
            if (filterCapabilities != null && !(filterCapabilities instanceof org.geotoolkit.ogc.xml.v200.FilterCapabilities)) {
                 throw new IllegalArgumentException("bad version of filterCapabilities.");
            }


            return new org.geotoolkit.csw.xml.v300.CapabilitiesType((org.geotoolkit.ows.xml.v200.ServiceIdentification)serviceIdentification,
                                                                (org.geotoolkit.ows.xml.v200.ServiceProvider)serviceProvider,
                                                                (org.geotoolkit.ows.xml.v200.OperationsMetadata)operationsMetadata,
                                                                version, updateSequence,
                                                                (org.geotoolkit.ogc.xml.v200.FilterCapabilities)filterCapabilities);

        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static AbstractCapabilities createCapabilities(final String version, final String updateSequence) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.Capabilities(version, updateSequence);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.CapabilitiesType(version, updateSequence);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.CapabilitiesType(version, updateSequence);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static GetRecordsResponse createGetRecordsResponse(final String version, final String requestId, final long time,
                                                       final SearchResults searchResults) {
        if ("2.0.2".equals(version)) {
            if (searchResults != null && !(searchResults instanceof org.geotoolkit.csw.xml.v202.SearchResultsType)) {
                 throw new IllegalArgumentException("bad version of searchResults.");
            }

            return new org.geotoolkit.csw.xml.v202.GetRecordsResponseType(requestId, time, version,
                                                                          (org.geotoolkit.csw.xml.v202.SearchResultsType)searchResults);
        } else if ("2.0.0".equals(version)) {
            if (searchResults != null && !(searchResults instanceof org.geotoolkit.csw.xml.v200.SearchResultsType)) {
                 throw new IllegalArgumentException("bad version of abstractQuery.");
            }
            return new org.geotoolkit.csw.xml.v200.GetRecordsResponseType(requestId, time, version,
                                                                          (org.geotoolkit.csw.xml.v200.SearchResultsType)searchResults);
        } else if ("3.0.0".equals(version)) {
            if (searchResults != null && !(searchResults instanceof org.geotoolkit.csw.xml.v300.SearchResultsType)) {
                 throw new IllegalArgumentException("bad version of searchResults.");
            }

            return new org.geotoolkit.csw.xml.v300.GetRecordsResponseType(requestId, time, version,
                                                                          (org.geotoolkit.csw.xml.v300.SearchResultsType)searchResults);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static GetRecordByIdResponse createGetRecordByIdResponse(final String version, final List<Object> any) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.GetRecordByIdResponseType(any);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.GetRecordByIdResponseType(any);
        } else if ("3.0.0".equals(version)) {
            Object record = null;
            if (!any.isEmpty()) {
                record = any.get(0);
            }
            return new org.geotoolkit.csw.xml.v300.InternalGetRecordByIdResponse(record);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static SearchResults createSearchResults(final String version, final String resultSetId, final ElementSetType elementSet, final int numberOfResultMatched,
            final List<Object> records, final Integer numberOfRecordsReturned, final int nextRecord, final List<FederatedSearchResultBase> federatedResults) {

        if ("2.0.2".equals(version) || "2.0.0".equals(version)) {
            // before 3.0 version, federated results are aggregated with normal results
            for (FederatedSearchResultBase federated : federatedResults) {
                if (federated instanceof FederatedSearchResult) {
                    FederatedSearchResult federatedResult = (FederatedSearchResult) federated;
                    if (federatedResult.getSearchResult() != null) {
                        records.addAll(federatedResult.getSearchResult().getAny());
                    }
                }
            }

            if ("2.0.2".equals(version)) {
                return new org.geotoolkit.csw.xml.v202.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, records, numberOfRecordsReturned, nextRecord);
            } else if ("2.0.0".equals(version)) {
                return new org.geotoolkit.csw.xml.v200.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, records, numberOfRecordsReturned, nextRecord);
            }
            // can not happen
            return null;
        } else if ("3.0.0".equals(version)) {
            final List<org.geotoolkit.csw.xml.v300.FederatedSearchResultBaseType> federatedResults300 = new ArrayList<>();
            if (federatedResults != null) {
                for (FederatedSearchResultBase sc : federatedResults) {
                    if (sc instanceof org.geotoolkit.csw.xml.v300.FederatedSearchResultBaseType) {
                        federatedResults300.add((org.geotoolkit.csw.xml.v300.FederatedSearchResultBaseType)sc);
                    } else {
                        throw new IllegalArgumentException("bad version of federated results.");
                    }
                }
            }
            return new org.geotoolkit.csw.xml.v300.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, records, numberOfRecordsReturned, nextRecord, federatedResults300);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static SearchResults createSearchResults(final String version, final String resultSetId, final ElementSetType elementSet, final int numberOfResultMatched, final int nextRecord) {

        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, nextRecord);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, nextRecord);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, nextRecord);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static DescribeRecord createDescribeRecord(final String version, final String service, final List<QName> typeName, final String outputFormat, final String schemaLanguage) {

        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.DescribeRecordType(service, version, typeName, outputFormat, schemaLanguage);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.DescribeRecordType(service, version, typeName, outputFormat, schemaLanguage);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static GetRecordById createGetRecordById(final String version, final String service, final ElementSetName elementSet, final String outputFormat, final String outputSchema, final List<String> id) {

        if ("2.0.2".equals(version)) {
            if (elementSet != null && !(elementSet instanceof org.geotoolkit.csw.xml.v202.ElementSetNameType)) {
                 throw new IllegalArgumentException("bad version of elementset.");
            }
            return new org.geotoolkit.csw.xml.v202.GetRecordByIdType(service, version, (org.geotoolkit.csw.xml.v202.ElementSetNameType)elementSet, outputFormat, outputSchema, id);
        } else if ("2.0.0".equals(version)) {
            if (elementSet != null && !(elementSet instanceof org.geotoolkit.csw.xml.v200.ElementSetNameType)) {
                 throw new IllegalArgumentException("bad version of elementset.");
            }
            String singleID = null;
            if (id != null && !id.isEmpty()) {
                singleID = id.get(0);
            }
            return new org.geotoolkit.csw.xml.v200.GetRecordByIdType(service, version, (org.geotoolkit.csw.xml.v200.ElementSetNameType)elementSet, singleID);
        } else if ("3.0.0".equals(version)) {
            if (elementSet != null && !(elementSet instanceof org.geotoolkit.csw.xml.v300.ElementSetNameType)) {
                 throw new IllegalArgumentException("bad version of elementset.");
            }
            String uniqueId = null;
            if (!id.isEmpty()) {
                uniqueId = id.get(0);
            }
            return new org.geotoolkit.csw.xml.v300.GetRecordByIdType(service, version, (org.geotoolkit.csw.xml.v300.ElementSetNameType)elementSet, outputFormat, outputSchema, uniqueId);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static GetCapabilities createGetCapabilities(final String version, final AcceptVersions acceptVersions, final Sections sections,
            final AcceptFormats acceptFormats, final String updateSequence, final String service) {

        if ("2.0.2".equals(version) || "2.0.0".equals(version)) {
            if (acceptVersions != null && !(acceptVersions instanceof org.geotoolkit.ows.xml.v100.AcceptVersionsType)) {
                 throw new IllegalArgumentException("bad version of acceptVersions.");
            }
            if (sections != null && !(sections instanceof org.geotoolkit.ows.xml.v100.SectionsType)) {
                 throw new IllegalArgumentException("bad version of sections.");
            }
            if (acceptFormats != null && !(acceptFormats instanceof org.geotoolkit.ows.xml.v100.AcceptFormatsType)) {
                 throw new IllegalArgumentException("bad version of acceptFormats.");
            }

            if ("2.0.2".equals(version)) {
                return new org.geotoolkit.csw.xml.v202.GetCapabilitiesType((org.geotoolkit.ows.xml.v100.AcceptVersionsType)acceptVersions,
                                                                           (org.geotoolkit.ows.xml.v100.SectionsType)sections,
                                                                           (org.geotoolkit.ows.xml.v100.AcceptFormatsType)acceptFormats,
                                                                           updateSequence,
                                                                           service);

            } else {
                return new org.geotoolkit.csw.xml.v200.GetCapabilitiesType((org.geotoolkit.ows.xml.v100.AcceptVersionsType)acceptVersions,
                                                                           (org.geotoolkit.ows.xml.v100.SectionsType)sections,
                                                                           (org.geotoolkit.ows.xml.v100.AcceptFormatsType)acceptFormats,
                                                                           updateSequence,
                                                                           service);
            }
        } else if ("3.0.0".equals(version)) {
            if (acceptVersions != null && !(acceptVersions instanceof org.geotoolkit.ows.xml.v200.AcceptVersionsType)) {
                 throw new IllegalArgumentException("bad version of acceptVersions.");
            }
            if (sections != null && !(sections instanceof org.geotoolkit.ows.xml.v200.SectionsType)) {
                 throw new IllegalArgumentException("bad version of sections.");
            }
            if (acceptFormats != null && !(acceptFormats instanceof org.geotoolkit.ows.xml.v200.AcceptFormatsType)) {
                 throw new IllegalArgumentException("bad version of acceptFormats.");
            }

            return new org.geotoolkit.csw.xml.v300.GetCapabilitiesType((org.geotoolkit.ows.xml.v200.AcceptVersionsType)acceptVersions,
                                                                       (org.geotoolkit.ows.xml.v200.SectionsType)sections,
                                                                       (org.geotoolkit.ows.xml.v200.AcceptFormatsType)acceptFormats,
                                                                       updateSequence,
                                                                       service);


        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static AcceptVersions buildAcceptVersion(final String currentVersion, final List<String> acceptVersion) {
        if ("2.0.2".equals(currentVersion) || "2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptVersion("1.0.0", acceptVersion);
        } else if ("3.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptVersion("2.0.0", acceptVersion);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static AcceptFormats buildAcceptFormat(final String currentVersion, final List<String> acceptFormats) {
        if ("2.0.2".equals(currentVersion) || "2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptFormat("1.0.0", acceptFormats);
        } else if ("3.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildAcceptFormat("2.0.0", acceptFormats);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Sections buildSections(final String currentVersion, final List<String> sections) {
        if ("2.0.2".equals(currentVersion) || "2.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildSections("1.0.0", sections);
        } else if ("3.0.0".equals(currentVersion)) {
            return OWSXmlFactory.buildSections("2.0.0", sections);
        }  else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static GetDomain createGetDomain(final String version, final String service, final String propertyName, final String parameterName) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.GetDomainType(service, version, propertyName, parameterName);
        } else if ("2.0.0".equals(version)){
            final QName propQname = new QName(propertyName);
            return new org.geotoolkit.csw.xml.v200.GetDomainType(service, version, propQname, parameterName);
        } else if ("3.0.0".equals(version)){
            return new org.geotoolkit.csw.xml.v300.GetDomainType(service, version, propertyName, parameterName);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static Harvest createHarvest(final String version, final String service, final String source, final String resourceType,
            final String resourceFormat, final String handler, final Duration harvestInterval) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.HarvestType(service, version, source, resourceType, resourceFormat, handler, harvestInterval);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.HarvestType(service, version, source, resourceType, resourceFormat, handler, harvestInterval);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static DescribeRecordResponse createDescribeRecordResponse(final String version, final List<SchemaComponent> components) {
        if ("2.0.2".equals(version)) {
            final List<org.geotoolkit.csw.xml.v202.SchemaComponentType> components202 = new ArrayList<>();
            if (components != null) {
                for (SchemaComponent sc : components) {
                    if (sc instanceof org.geotoolkit.csw.xml.v202.SchemaComponentType) {
                        components202.add((org.geotoolkit.csw.xml.v202.SchemaComponentType)sc);
                    } else {
                        throw new IllegalArgumentException("bad version of schemaComponent.");
                    }
                }
            }
            return new org.geotoolkit.csw.xml.v202.DescribeRecordResponseType(components202);
        } else if ("2.0.0".equals(version)){
            final List<org.geotoolkit.csw.xml.v200.SchemaComponentType> components200 = new ArrayList<>();
            if (components != null) {
                for (SchemaComponent sc : components) {
                    if (sc instanceof org.geotoolkit.csw.xml.v200.SchemaComponentType) {
                        components200.add((org.geotoolkit.csw.xml.v200.SchemaComponentType)sc);
                    } else {
                        throw new IllegalArgumentException("bad version of schemaComponent.");
                    }
                }
            }
            return new org.geotoolkit.csw.xml.v200.DescribeRecordResponseType(components200);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static SchemaComponent createSchemaComponent(final String version, final String targetNamespace, final String schemaLanguage, final Object xsd) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.SchemaComponentType(targetNamespace, schemaLanguage, xsd);
        } else if ("2.0.0".equals(version)){
            return new org.geotoolkit.csw.xml.v200.SchemaComponentType(targetNamespace, schemaLanguage, xsd);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static Transaction createTransaction(final String version, final String service, final Delete delete) {
        if ("2.0.2".equals(version)) {
            if (delete != null && !(delete instanceof org.geotoolkit.csw.xml.v202.DeleteType)) {
                throw new IllegalArgumentException("bad version of delete.");
            }
            return new org.geotoolkit.csw.xml.v202.TransactionType(service, version, (org.geotoolkit.csw.xml.v202.DeleteType)delete);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            if (delete != null && !(delete instanceof org.geotoolkit.csw.xml.v300.DeleteType)) {
                throw new IllegalArgumentException("bad version of delete.");
            }
            return new org.geotoolkit.csw.xml.v300.TransactionType(service, version, (org.geotoolkit.csw.xml.v300.DeleteType)delete);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static Transaction createTransaction(final String version, final String service, final Insert... inserts) {
        if ("2.0.2".equals(version)) {
            final org.geotoolkit.csw.xml.v202.InsertType[] insert202 = new org.geotoolkit.csw.xml.v202.InsertType[inserts.length];
            int i = 0;
            for (Insert sc : inserts) {
                if (sc instanceof org.geotoolkit.csw.xml.v202.InsertType) {
                    insert202[i] = (org.geotoolkit.csw.xml.v202.InsertType)sc;
                } else {
                    throw new IllegalArgumentException("bad version of insertResult.");
                }
                i++;
            }

            return new org.geotoolkit.csw.xml.v202.TransactionType(service, version, insert202);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            final org.geotoolkit.csw.xml.v300.InsertType[] insert300 = new org.geotoolkit.csw.xml.v300.InsertType[inserts.length];
            int i = 0;
            for (Insert sc : inserts) {
                if (sc instanceof org.geotoolkit.csw.xml.v300.InsertType) {
                    insert300[i] = (org.geotoolkit.csw.xml.v300.InsertType)sc;
                } else {
                    throw new IllegalArgumentException("bad version of insertResult.");
                }
                i++;
            }

            return new org.geotoolkit.csw.xml.v300.TransactionType(service, version, insert300);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static Transaction createTransaction(final String version, final String service, final Update... updates) {
        if ("2.0.2".equals(version)) {
            org.geotoolkit.csw.xml.v202.UpdateType[] update202 = null;
            if (updates != null) {
                update202 = new org.geotoolkit.csw.xml.v202.UpdateType[updates.length];
                int i = 0;
                for (Update sc : updates) {
                    if (sc instanceof org.geotoolkit.csw.xml.v202.UpdateType) {
                        update202[i] = (org.geotoolkit.csw.xml.v202.UpdateType)sc;
                    } else {
                        throw new IllegalArgumentException("bad version of insertResult.");
                    }
                    i++;
                }
            }
            return new org.geotoolkit.csw.xml.v202.TransactionType(service, version, update202);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            org.geotoolkit.csw.xml.v300.UpdateType[] update300 = null;
            if (updates != null) {
                update300 = new org.geotoolkit.csw.xml.v300.UpdateType[updates.length];
                int i = 0;
                for (Update sc : updates) {
                    if (sc instanceof org.geotoolkit.csw.xml.v300.UpdateType) {
                        update300[i] = (org.geotoolkit.csw.xml.v300.UpdateType)sc;
                    } else {
                        throw new IllegalArgumentException("bad version of insertResult.");
                    }
                    i++;
                }
            }
            return new org.geotoolkit.csw.xml.v300.TransactionType(service, version, update300);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static TransactionSummary createTransactionSummary(final String version, final int totalInserted, final int totalUpdated, final int totalDeleted, final String requestId) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.TransactionSummaryType(totalInserted, totalUpdated, totalDeleted, requestId);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.TransactionSummaryType(totalInserted, totalUpdated, totalDeleted, requestId);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static TransactionResponse createTransactionResponse(final String version, final TransactionSummary summary, final List<InsertResult> inserts) {
        if ("2.0.2".equals(version)) {
            if (summary != null && !(summary instanceof org.geotoolkit.csw.xml.v202.TransactionSummaryType)) {
                throw new IllegalArgumentException("bad version of transactionSummary.");
            }
            final List<org.geotoolkit.csw.xml.v202.InsertResultType> insert202 = new ArrayList<>();
            if (inserts != null) {
                for (InsertResult sc : inserts) {
                    if (sc instanceof org.geotoolkit.csw.xml.v202.InsertResultType) {
                        insert202.add((org.geotoolkit.csw.xml.v202.InsertResultType)sc);
                    } else {
                        throw new IllegalArgumentException("bad version of insertResult.");
                    }
                }
            }
            return new org.geotoolkit.csw.xml.v202.TransactionResponseType((org.geotoolkit.csw.xml.v202.TransactionSummaryType)summary,  insert202, version);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            if (summary != null && !(summary instanceof org.geotoolkit.csw.xml.v300.TransactionSummaryType)) {
                throw new IllegalArgumentException("bad version of transactionSummary.");
            }
            final List<org.geotoolkit.csw.xml.v300.InsertResultType> insert300 = new ArrayList<>();
            if (inserts != null) {
                for (InsertResult sc : inserts) {
                    if (sc instanceof org.geotoolkit.csw.xml.v300.InsertResultType) {
                        insert300.add((org.geotoolkit.csw.xml.v300.InsertResultType)sc);
                    } else {
                        throw new IllegalArgumentException("bad version of insertResult.");
                    }
                }
            }
            return new org.geotoolkit.csw.xml.v300.TransactionResponseType((org.geotoolkit.csw.xml.v300.TransactionSummaryType)summary,  insert300, version);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }


    public static HarvestResponse createHarvestResponse(final String version, final TransactionResponse response) {
        if ("2.0.2".equals(version)) {
            if (response != null && !(response instanceof org.geotoolkit.csw.xml.v202.TransactionResponseType)) {
                throw new IllegalArgumentException("bad version of transactionResponse.");
            }
            return new org.geotoolkit.csw.xml.v202.HarvestResponseType((org.geotoolkit.csw.xml.v202.TransactionResponseType)response);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            if (response != null && !(response instanceof org.geotoolkit.csw.xml.v300.TransactionResponseType)) {
                throw new IllegalArgumentException("bad version of transactionResponse.");
            }
            return new org.geotoolkit.csw.xml.v300.HarvestResponseType((org.geotoolkit.csw.xml.v300.TransactionResponseType)response);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static HarvestResponse createHarvestResponse(final String version, final Acknowledgement ack) {
        if ("2.0.2".equals(version)) {
            if (ack != null && !(ack instanceof org.geotoolkit.csw.xml.v202.AcknowledgementType)) {
                throw new IllegalArgumentException("bad version of acknowledgement.");
            }
            return new org.geotoolkit.csw.xml.v202.HarvestResponseType((org.geotoolkit.csw.xml.v202.AcknowledgementType)ack);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            if (ack != null && !(ack instanceof org.geotoolkit.csw.xml.v300.AcknowledgementType)) {
                throw new IllegalArgumentException("bad version of acknowledgement.");
            }
            return new org.geotoolkit.csw.xml.v300.HarvestResponseType((org.geotoolkit.csw.xml.v300.AcknowledgementType)ack);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static Acknowledgement createAcknowledgement(final String version, final String requestID, final Object request, final long timestamp) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.AcknowledgementType(requestID, new org.geotoolkit.csw.xml.v202.EchoedRequestType(request), timestamp);
        } else if ("2.0.0".equals(version)){
            return new org.geotoolkit.csw.xml.v200.AcknowledgementType(requestID, new org.geotoolkit.csw.xml.v200.EchoedRequestType(request), timestamp);
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.AcknowledgementType(requestID, new org.geotoolkit.csw.xml.v300.EchoedRequestType(request), timestamp);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static InsertResult createInsertResult(final String version, final List<Object> briefRecord, final String handleRef) {
        if ("2.0.2".equals(version)) {

            return new org.geotoolkit.csw.xml.v202.InsertResultType(briefRecord, handleRef);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v300.InsertResultType(briefRecord, handleRef);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static FederatedSearchException buildFederatedSearchException(String version, String catalogueURL, ExceptionResponse ex) {
        if ("2.0.2".equals(version)) {
            if (ex != null && !(ex instanceof org.geotoolkit.ows.xml.v100.ExceptionReport)) {
                throw new IllegalArgumentException("bad version of exception.");
            }
            return new org.geotoolkit.csw.xml.v202.InternalFederatedSearchException((org.geotoolkit.ows.xml.v100.ExceptionReport)ex);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            if (ex != null && !(ex instanceof org.geotoolkit.ows.xml.v200.ExceptionReport)) {
                throw new IllegalArgumentException("bad version of exception.");
            }
            return new org.geotoolkit.csw.xml.v300.FederatedExceptionType(catalogueURL, (org.geotoolkit.ows.xml.v200.ExceptionReport)ex);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static FederatedSearchResult buildFederatedSearchResult(String version, String catalogueURL, SearchResults sr) {
        if ("2.0.2".equals(version)) {
            if (sr != null && !(sr instanceof org.geotoolkit.csw.xml.v202.SearchResultsType)) {
                throw new IllegalArgumentException("bad version of exception.");
            }
            return new org.geotoolkit.csw.xml.v202.InternalFederatedSearchResult((org.geotoolkit.csw.xml.v202.SearchResultsType)sr);
        } else if ("2.0.0".equals(version)){
            // dont exist ???????
            return null;
        } else if ("3.0.0".equals(version)) {
            if (sr != null && !(sr instanceof org.geotoolkit.csw.xml.v300.SearchResultsType)) {
                throw new IllegalArgumentException("bad version of exception.");
            }
            return new org.geotoolkit.csw.xml.v300.FederatedSearchResultType(catalogueURL, (org.geotoolkit.csw.xml.v300.SearchResultsType)sr);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static String getOwsVersion(final String version) {
        if ("2.0.2".equals(version) | "2.0.0".equals(version)){
            return "1.0.0";
        } else if ("3.0.0".equals(version)) {
            return "2.0.0";
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static ExceptionResponse buildExceptionReport(String version, final String exceptionText, final String exceptionCode, final String locator, final String exVersion) {
        return OWSXmlFactory.buildExceptionReport(getOwsVersion(version), exceptionText, exceptionCode, locator, exVersion);
    }
}
