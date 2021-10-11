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
package org.geotoolkit.ows.xml;

import java.util.Collection;
import java.util.List;

/**
 *  Super abstract type for all the different versions of Operation.
 *
 * @author Guilhem Legal
 * @module
 */
public interface AbstractOperation {

    String getName();

    List<? extends AbstractDCP> getDCP();

    List<? extends AbstractDomain> getParameter();

    AbstractDomain getParameter(String name);

    AbstractDomain getParameterIgnoreCase(String name);

    List<? extends AbstractDomain> getConstraint();

    AbstractDomain getConstraint(String name);

    AbstractDomain getConstraintIgnoreCase(String name);

    void updateParameter(final String parameterName, final Collection<String> values);

    void updateParameter(final String parameterName, final Range range);
}
