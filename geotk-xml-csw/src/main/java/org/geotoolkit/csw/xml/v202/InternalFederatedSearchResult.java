/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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
package org.geotoolkit.csw.xml.v202;

import org.geotoolkit.csw.xml.FederatedSearchResult;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class InternalFederatedSearchResult implements FederatedSearchResult {

    protected SearchResultsType searchResult;


    public InternalFederatedSearchResult() {

    }

    public InternalFederatedSearchResult(SearchResultsType searchResult) {
        this.searchResult = searchResult;
    }

    @Override
    public SearchResultsType getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(SearchResultsType value) {
        this.searchResult = value;
    }

    @Override
    public int getMatched() {
        if (searchResult != null) {
            return searchResult.getNumberOfRecordsMatched();
        }
        return 0;
    }

    @Override
    public int getReturned() {
        if (searchResult != null) {
            return searchResult.getNumberOfRecordsReturned();
        }
        return 0;
    }
}
