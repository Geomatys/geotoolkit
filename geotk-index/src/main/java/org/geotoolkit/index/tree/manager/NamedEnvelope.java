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

package org.geotoolkit.index.tree.manager;

import java.io.*;
import java.util.Objects;
import org.apache.sis.geometry.GeneralEnvelope;
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
    private String id;

    /**
     * Used for multiple envelope correspunding to the same metadata.
     */
    private int nbEnv = 0;

    /**
     * Empty constructor required by the externalizable pattern.
     */
    public NamedEnvelope() {
        super(2);
    }

    /**
     * Build a new envelope with the specified CRS and name.
     */
    public NamedEnvelope(final CoordinateReferenceSystem crs, final String id) {
        super(crs);
        this.id = id;
    }

    /**
     * Build a new envelope with the specified CRS and name.
     */
    public NamedEnvelope(final CoordinateReferenceSystem crs, final String id, final int nbEnv) {
        super(crs);
        this.id = id;
        this.nbEnv = nbEnv;
    }

    /**
     * Build a new envelope from the specified one and affect a name to it.
     */
    public NamedEnvelope(final Envelope env, final String id) {
        super(env);
        this.id = id;
    }

    /**
     * @return the name / identifier bounded to the envelope
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return the number of envelope in the index for the same metadata.
     */
    public int getNbEnv() {
        return nbEnv;
    }

    public void setNbEnv(int nbEnv) {
        this.nbEnv = nbEnv;
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
        stream.writeInt(nbEnv);
        stream.writeUTF(id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void readExternal(final ObjectInput stream) throws IOException {
        final double minx = stream.readDouble();
        final double miny = stream.readDouble();
        final double maxx = stream.readDouble();
        final double maxy = stream.readDouble();
        this.nbEnv        = stream.readInt();
        this.id           = stream.readUTF();
        setRange(0, minx, maxx);
        setRange(1, miny, maxy);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NamedEnvelope) {
            final NamedEnvelope that = (NamedEnvelope) obj;
            return Objects.equals(this.id, that.id);
        }
        return false;
    }

    public boolean fullEquals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NamedEnvelope && super.equals(obj)) {
            final NamedEnvelope that = (NamedEnvelope) obj;
            return Objects.equals(this.id, that.id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ID: " + id + " nb envelope: " + nbEnv + ". " + super.toString();
    }
}
