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
package org.geotoolkit.process.jts;

import java.util.Collections;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.process.AbstractProcessFactory;
import org.geotoolkit.process.jts.buffer.BufferDescriptor;


import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Provide a set of JTS operations as processes.
 * those are not very useful on their own, but are necessary when chaining
 * processes.
 * 
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class JTSProcessFactory extends AbstractProcessFactory{
    
    /** factory name **/
    public static final String NAME = "jts";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public JTSProcessFactory() {
        super(BufferDescriptor.INSTANCE);
    }
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
}
