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
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.SortBy;
import org.geotoolkit.ows.xml.AbstractDomain;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.sort.SortOrder;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class CswXmlFactory {

    public static GetDomainResponse getDomainResponse(final String version, final List<DomainValues> domainValues) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.GetDomainResponseType(domainValues);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.GetDomainResponseType(domainValues);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static DomainValues getDomainValues(final String version, final String parameterName, final String propertyName, final List<String> listOfValues, final QName type) {
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.DomainValuesType(parameterName, null, listOfValues, type);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.DomainValuesType(parameterName, null, listOfValues, type);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static AbstractDomain createDomain(final String version, final String name, final List<String> allowedValues) {
        if ("2.0.2".equals(version) || "2.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v100.DomainType(name, allowedValues);
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
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static SortBy createSortBy(final String version, final Map<String, SortOrder> sortRules) {
        if ("2.0.2".equals(version)) {
            if (sortRules != null && !sortRules.isEmpty()) {
                final List<org.geotoolkit.ogc.xml.v110.SortPropertyType> rules = new ArrayList<org.geotoolkit.ogc.xml.v110.SortPropertyType>();
                final Set<Entry<String, SortOrder>> entries = sortRules.entrySet();
                for (final Entry<String, SortOrder> entry : entries) {
                    rules.add(new org.geotoolkit.ogc.xml.v110.SortPropertyType(entry.getKey(), entry.getValue()));
                }
                return new org.geotoolkit.ogc.xml.v110.SortByType(rules);
            } 
            return null;
        } else if ("2.0.0".equals(version)) {
            return null;
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
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static SearchResults createSearchResults(final String version, final String resultSetId, final ElementSetType elementSet, final int numberOfResultMatched,
            final List<? extends AbstractRecord> dcrecords, final List<Object> records, final Integer numberOfRecordsReturned, final int nextRecord) {
        
        if ("2.0.2".equals(version)) {
            final List<org.geotoolkit.csw.xml.v202.AbstractRecordType> dcRecords200 = new ArrayList<org.geotoolkit.csw.xml.v202.AbstractRecordType>();
            if (dcrecords != null) {
                for (AbstractRecord dcRecord : dcrecords) {
                    if (dcRecord instanceof org.geotoolkit.csw.xml.v202.AbstractRecordType) {
                        dcRecords200.add((org.geotoolkit.csw.xml.v202.AbstractRecordType)dcRecord);
                    } else {
                        throw new IllegalArgumentException("bad version of dublin core record.");
                    }
                }
            }
            return new org.geotoolkit.csw.xml.v202.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, dcRecords200, records, numberOfRecordsReturned, nextRecord);
        } else if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.csw.xml.v200.AbstractRecordType> dcRecords200 = new ArrayList<org.geotoolkit.csw.xml.v200.AbstractRecordType>();
            if (dcrecords != null) {
                for (AbstractRecord dcRecord : dcrecords) {
                    if (dcRecord instanceof org.geotoolkit.csw.xml.v200.AbstractRecordType) {
                        dcRecords200.add((org.geotoolkit.csw.xml.v200.AbstractRecordType)dcRecord);
                    } else {
                        throw new IllegalArgumentException("bad version of dublin core record.");
                    }
                }
            }
            return new org.geotoolkit.csw.xml.v200.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, dcRecords200, records, numberOfRecordsReturned, nextRecord);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
    
    public static SearchResults createSearchResults(final String version, final String resultSetId, final ElementSetType elementSet, final int numberOfResultMatched, final int nextRecord) {
        
        if ("2.0.2".equals(version)) {
            return new org.geotoolkit.csw.xml.v202.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, nextRecord);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.SearchResultsType(resultSetId, elementSet, numberOfResultMatched, nextRecord);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
}
