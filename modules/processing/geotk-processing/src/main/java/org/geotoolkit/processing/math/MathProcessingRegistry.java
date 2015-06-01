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
package org.geotoolkit.processing.math;

import org.geotoolkit.processing.math.add.AddDescriptor;
import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.processing.AbstractProcessingRegistry;
import org.geotoolkit.processing.math.absolute.AbsoluteDescriptor;
import org.geotoolkit.processing.math.acos.AcosDescriptor;
import org.geotoolkit.processing.math.asin.AsinDescriptor;
import org.geotoolkit.processing.math.atan.AtanDescriptor;
import org.geotoolkit.processing.math.atan2.Atan2Descriptor;
import org.geotoolkit.processing.math.avg.AvgDescriptor;
import org.geotoolkit.processing.math.ceil.CeilDescriptor;
import org.geotoolkit.processing.math.cos.CosDescriptor;
import org.geotoolkit.processing.math.divide.DivideDescriptor;
import org.geotoolkit.processing.math.floor.FloorDescriptor;
import org.geotoolkit.processing.math.log.LogDescriptor;
import org.geotoolkit.processing.math.max.MaxDescriptor;
import org.geotoolkit.processing.math.median.MedianDescriptor;
import org.geotoolkit.processing.math.min.MinDescriptor;
import org.geotoolkit.processing.math.multiply.MultiplyDescriptor;
import org.geotoolkit.processing.math.power.PowerDescriptor;
import org.geotoolkit.processing.math.round.RoundDescriptor;
import org.geotoolkit.processing.math.sin.SinDescriptor;
import org.geotoolkit.processing.math.substract.SubstractDescriptor;
import org.geotoolkit.processing.math.sum.SumDescriptor;
import org.geotoolkit.processing.math.tan.TanDescriptor;
import org.geotoolkit.processing.math.todegree.ToDegreeDescriptor;
import org.geotoolkit.processing.math.toradian.ToRadianDescriptor;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Provide a set of commun math operations as processes.
 * those are not very useful on their own, but are necessary when chaining
 * processes.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MathProcessingRegistry extends AbstractProcessingRegistry{
    
    /** factory name **/
    public static final String NAME = "math";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public MathProcessingRegistry() {
        super(AddDescriptor.INSTANCE, SubstractDescriptor.INSTANCE, DivideDescriptor.INSTANCE, MultiplyDescriptor.INSTANCE,
              PowerDescriptor.INSTANCE, AbsoluteDescriptor.INSTANCE, AcosDescriptor.INSTANCE, AsinDescriptor.INSTANCE,
              AtanDescriptor.INSTANCE, Atan2Descriptor.INSTANCE, CosDescriptor.INSTANCE, SinDescriptor.INSTANCE,
              TanDescriptor.INSTANCE, RoundDescriptor.INSTANCE, CeilDescriptor.INSTANCE, FloorDescriptor.INSTANCE,
              LogDescriptor.INSTANCE, ToDegreeDescriptor.INSTANCE, ToRadianDescriptor.INSTANCE, MinDescriptor.INSTANCE,
              MaxDescriptor.INSTANCE, SumDescriptor.INSTANCE, AvgDescriptor.INSTANCE, MedianDescriptor.INSTANCE);
    }
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
}
