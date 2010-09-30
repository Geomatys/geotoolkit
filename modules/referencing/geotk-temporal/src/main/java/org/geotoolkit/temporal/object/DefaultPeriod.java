/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.temporal.object;

import java.util.Date;
import org.geotoolkit.util.Utilities;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.Position;
import org.opengis.temporal.RelativePosition;

/**
 * A one-dimensional geometric primitive that represent extent in time.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultPeriod extends DefaultTemporalGeometricPrimitive implements Period {

    /**
     * This is the TM_Instant at which this Period starts.
     */
    private Instant begining;
    /**
     * This is the TM_Instant at which this Period ends.
     */
    private Instant ending;

    public DefaultPeriod() {

    }
    
    public DefaultPeriod(Instant begining, Instant ending) {
	// begining must be before or equals ending
        if (begining != null && 
                (RelativePosition.BEFORE.equals(begining.relativePosition(ending)) ||
                RelativePosition.EQUALS.equals(begining.relativePosition(ending)))) {
             this.begining = begining;
             this.ending = ending;
         }
     }

    /**
     * Links this period to the instant at which it starts.
     */
    public Instant getBeginning() {
        return begining;
    }

    public void setBegining(Instant begining) {
        this.begining = begining;
    }

    public void setBegining(Date date) {
        this.begining = new DefaultInstant(new DefaultPosition(date));
    }

    public void setBegining(Position pos) {
        this.begining = new DefaultInstant(pos);
    }

    /**
     * Links this period to the instant at which it ends.
     */
    public Instant getEnding() {
        return ending;
    }

    public void setEnding(Instant ending) {
        this.ending = ending;
    }

    public void setEnding(Position pos) {
        this.ending = new DefaultInstant(pos);
    }

    public void setEnding(Date date) {
        this.ending = new DefaultInstant(new DefaultPosition(date));
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultPeriod) {
            final DefaultPeriod that = (DefaultPeriod) object;

            return Utilities.equals(this.begining, that.begining) &&
                    Utilities.equals(this.ending, that.ending);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.begining != null ? this.begining.hashCode() : 0);
        hash = 37 * hash + (this.ending != null ? this.ending.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Period:").append('\n');
        if (begining != null) {
            s.append("begin:").append(begining).append('\n');
        }
        if (ending != null) {
            s.append("end:").append(ending).append('\n');
        }

        return s.toString();
    }
}
