/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.process;

import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.opengis.metadata.identification.Identification;

/**
 * A Default process registry.
 * 
 * @author Cédric Briançon (Geomatys)
 */
public class DefaultProcessingRegistry extends AbstractProcessingRegistry {
    /**
     * Identification meta-data.
     */
    private final DefaultServiceIdentification identification;

    public DefaultProcessingRegistry(final DefaultServiceIdentification identification,
            final ProcessDescriptor ... descs){
        super(descs);
        this.identification = identification;
    }

    @Override
    public Identification getIdentification() {
        return identification;
    }

}
