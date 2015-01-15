/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.process.image;

import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.process.AbstractProcessingRegistry;
import org.geotoolkit.process.image.bandcombine.BandCombineDescriptor;
import org.geotoolkit.process.image.bandselect.BandSelectDescriptor;
import org.geotoolkit.process.image.dynamicrange.DynamicRangeStretchDescriptor;
import org.geotoolkit.process.image.reformat.ReformatDescriptor;
import org.geotoolkit.process.image.replace.ReplaceDescriptor;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Image processing registry.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ImageProcessingRegistry extends AbstractProcessingRegistry{

    public static final String NAME = "image";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        Identifier id = new DefaultIdentifier(NAME);
        DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public ImageProcessingRegistry(){
        super(BandSelectDescriptor.INSTANCE,
              BandCombineDescriptor.INSTANCE,
              ReformatDescriptor.INSTANCE,
              ReplaceDescriptor.INSTANCE,
              DynamicRangeStretchDescriptor.INSTANCE);
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
}
