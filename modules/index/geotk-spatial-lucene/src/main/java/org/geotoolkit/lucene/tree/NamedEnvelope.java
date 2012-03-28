/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.lucene.tree;

import java.io.*;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A envelope used in R-Tree to record the identifier with the associated envelope.
 * 
 * The serialization mecanism has been overriden because we had huge file caused by a duplication of the CRS 
 * attribute in the file.
 * 
 * @author Guilhem Legal (Geomatys)
 */
public class NamedEnvelope extends GeneralEnvelope implements Externalizable {
    
    /**
     * The identifier of the envelope.
     */
    private String name;
    
    /**
     * Empty constructor required by the externalizable pattern.
     */
    public NamedEnvelope() {
        super(2);
    }
    
    /**
     * Build a new envelope with the specified CRS and name.
     */
    public NamedEnvelope(final CoordinateReferenceSystem crs, final String name) {
        super(crs);
        this.name = name;
    }
    
    /**
     * Build a new envelope from the specified one and affect a name to it.
     */
    public NamedEnvelope(final Envelope env, final String name ) {
        super(env);
        this.name = name;
    }

    /**
     * @return the name / identifier bounded to the envelope
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void writeExternal(final ObjectOutput stream) throws IOException {
        stream.writeDouble(super.getLower(0));
        stream.writeDouble(super.getLower(1));
        stream.writeDouble(super.getUpper(0));
        stream.writeDouble(super.getUpper(1));
        final byte[] bytes = name.getBytes("UTF-8");
        stream.writeInt(bytes.length);
        stream.write(bytes);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void readExternal(final ObjectInput stream) throws IOException, ClassNotFoundException {
        final double minx = stream.readDouble();
        final double miny = stream.readDouble();
        final double maxx = stream.readDouble();
        final double maxy = stream.readDouble();
        final int length = stream.readInt();
        final byte[] bytes = new byte[length];
        stream.read(bytes);
        final String envName = new String(bytes, "UTF-8");
        this.name = envName;
        setRange(0, minx, maxx);
        setRange(1, miny, maxy);
    }
}
