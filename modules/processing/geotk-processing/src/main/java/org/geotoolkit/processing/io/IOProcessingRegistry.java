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
package org.geotoolkit.processing.io;

import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.processing.AbstractProcessingRegistry;
import org.geotoolkit.processing.io.createtempfile.CreateTempFileDescriptor;
import org.geotoolkit.processing.io.createtempfolder.CreateTempFolderDescriptor;
import org.geotoolkit.processing.io.delete.DeleteDescriptor;
import org.geotoolkit.processing.io.packfile.PackFileDescriptor;
import org.geotoolkit.processing.io.unpackfile.UnpackFileDescriptor;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Factory for io process
 * Factory name : "io"
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class IOProcessingRegistry extends AbstractProcessingRegistry {

    /**Factory name*/
    public static final String NAME = "io";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    /**
     * Default constructor 
     */
    public IOProcessingRegistry() {
        super(CreateTempFileDescriptor.INSTANCE,
              CreateTempFolderDescriptor.INSTANCE,
              DeleteDescriptor.INSTANCE,
              UnpackFileDescriptor.INSTANCE,
              PackFileDescriptor.INSTANCE);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
}
