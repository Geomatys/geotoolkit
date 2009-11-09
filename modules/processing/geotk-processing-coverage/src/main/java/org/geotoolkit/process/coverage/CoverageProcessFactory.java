/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.process.coverage;

import java.util.Collections;

import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.process.AbstractProcessFactory;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 *
 * @author Johann sorel (Geomatys)
 */
public class CoverageProcessFactory extends AbstractProcessFactory{

    public static String NAME = "coverage";

    private static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        Identifier id = new DefaultIdentifier(NAME);
        DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);

    }


    public CoverageProcessFactory(){
        addDescriptor(new CoverageToVectorDescriptor(IDENTIFICATION));
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

}
