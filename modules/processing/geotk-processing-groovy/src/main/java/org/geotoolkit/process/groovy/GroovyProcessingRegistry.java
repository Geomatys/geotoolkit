/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.groovy;

import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.process.AbstractProcessingRegistry;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

import java.util.Collections;

/**
 * Provide a set of commun dynamic operations as processes defined by groovy language.
 * 
 * @authorChristophe Mourette (Geomatys)
 * @module pending
 */
public class GroovyProcessingRegistry extends AbstractProcessingRegistry{

    /** factory name **/
    public static final String NAME = "dynamic";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public GroovyProcessingRegistry() {
        super( GroovyDescriptor.INSTANCE);
    }
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
}
