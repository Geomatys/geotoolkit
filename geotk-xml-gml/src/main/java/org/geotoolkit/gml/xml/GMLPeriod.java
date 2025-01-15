/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.gml.xml;

import java.time.temporal.TemporalAmount;
import java.util.Optional;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface GMLPeriod extends Period {

    public String getId();

    AbstractTimePosition getBeginPosition();

    AbstractTimePosition getEndPosition();

    public static Optional<GMLPeriod> castOrWrap(Object period) {
        if (period instanceof GMLPeriod p) {
            return Optional.of(p);
        }
        if (period instanceof Period p) {
            return Optional.of(new GMLPeriod() {
                @Override public String               getId()            {return null;}
                @Override public TemporalAmount       length()           {return p.length();}
                @Override public Instant              getBeginning()     {return p.getBeginning();}
                @Override public Instant              getEnding()        {return p.getEnding();}
                @Override public AbstractTimePosition getBeginPosition() {return AbstractTimePosition.of(getBeginning());}
                @Override public AbstractTimePosition getEndPosition()   {return AbstractTimePosition.of(getEnding());}
                @Override public TemporalOperatorName findRelativePosition(TemporalPrimitive other) {
                    return p.findRelativePosition(other);
                }
            });
        }
        return Optional.empty();
    }
}
