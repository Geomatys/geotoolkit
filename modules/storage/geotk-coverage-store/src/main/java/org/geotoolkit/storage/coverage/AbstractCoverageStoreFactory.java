/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.storage.coverage;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.AbstractDataStoreFactory;
import org.opengis.parameter.ParameterDescriptor;

/**
 * General implementation of methods for CoverageStoreFactory implementations.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCoverageStoreFactory extends AbstractDataStoreFactory implements CoverageStoreFactory{

    /**
     * Identifier, Mandatory.
     * Subclasses should redeclared this parameter with a different default value.
     */
    public static final ParameterDescriptor<String> IDENTIFIER = new ParameterBuilder()
            .addName("identifier")
            .addName(Bundle.formatInternational(Bundle.Keys.paramIdentifierAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramIdentifierRemarks))
            .setRequired(true)
            .create(String.class, null);

    /** parameter for namespace of the coveragestore */
    public static final ParameterDescriptor<String> NAMESPACE = new ParameterBuilder()
            .addName("namespace")
            .addName(Bundle.formatInternational(Bundle.Keys.paramNamespaceAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramNamespaceRemarks))
            .setRequired(false)
            .create(String.class, null);

}
