/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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

import java.util.List;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface AbstractOperationsMetadata {

    /**
     * Update all the url in a OWS capabilities document.
     *
     * @param url The url of the web application.
     */
    void updateURL(String url);

    void addConstraint(final AbstractDomain domain);

    List<? extends AbstractOperation> getOperation();

    AbstractOperation getOperation(final String operationName);

    void removeOperation(final String operationName);

    AbstractDomain getConstraint(final String name);

    AbstractDomain getParameter(final String name);

    void removeConstraint(final String name);

    Object getExtendedCapabilities();

    void setExtendedCapabilities(final Object extendedCapabilities);

    AbstractOperationsMetadata clone();
}
