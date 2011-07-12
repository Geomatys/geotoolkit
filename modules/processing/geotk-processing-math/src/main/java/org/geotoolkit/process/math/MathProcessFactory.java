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
package org.geotoolkit.process.math;

import org.geotoolkit.process.math.add.AddDescriptor;
import java.util.Collections;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.process.AbstractProcessFactory;
import org.geotoolkit.process.math.absolute.AbsoluteDescriptor;
import org.geotoolkit.process.math.acos.AcosDescriptor;
import org.geotoolkit.process.math.asin.AsinDescriptor;
import org.geotoolkit.process.math.atan.AtanDescriptor;
import org.geotoolkit.process.math.atan2.Atan2Descriptor;
import org.geotoolkit.process.math.avg.AvgDescriptor;
import org.geotoolkit.process.math.ceil.CeilDescriptor;
import org.geotoolkit.process.math.cos.CosDescriptor;
import org.geotoolkit.process.math.divide.DivideDescriptor;
import org.geotoolkit.process.math.floor.FloorDescriptor;
import org.geotoolkit.process.math.log.LogDescriptor;
import org.geotoolkit.process.math.max.MaxDescriptor;
import org.geotoolkit.process.math.min.MinDescriptor;
import org.geotoolkit.process.math.multiply.MultiplyDescriptor;
import org.geotoolkit.process.math.power.PowerDescriptor;
import org.geotoolkit.process.math.round.RoundDescriptor;
import org.geotoolkit.process.math.sin.SinDescriptor;
import org.geotoolkit.process.math.substract.SubstractDescriptor;
import org.geotoolkit.process.math.sum.SumDescriptor;
import org.geotoolkit.process.math.tan.TanDescriptor;
import org.geotoolkit.process.math.todegree.ToDegreeDescriptor;
import org.geotoolkit.process.math.toradian.ToRadianDescriptor;

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
public class MathProcessFactory extends AbstractProcessFactory{
    
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

    public MathProcessFactory() {
        super(AddDescriptor.INSTANCE, SubstractDescriptor.INSTANCE, DivideDescriptor.INSTANCE, MultiplyDescriptor.INSTANCE,
              PowerDescriptor.INSTANCE, AbsoluteDescriptor.INSTANCE, AcosDescriptor.INSTANCE, AsinDescriptor.INSTANCE,
              AtanDescriptor.INSTANCE, Atan2Descriptor.INSTANCE, CosDescriptor.INSTANCE, SinDescriptor.INSTANCE,
              TanDescriptor.INSTANCE, RoundDescriptor.INSTANCE, CeilDescriptor.INSTANCE, FloorDescriptor.INSTANCE,
              LogDescriptor.INSTANCE, ToDegreeDescriptor.INSTANCE, ToRadianDescriptor.INSTANCE, MinDescriptor.INSTANCE,
              MaxDescriptor.INSTANCE, SumDescriptor.INSTANCE, AvgDescriptor.INSTANCE);
    }
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
}
