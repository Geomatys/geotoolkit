/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.xml;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.Tag;
import org.geotoolkit.data.osm.model.User;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.geometry.Envelope;

import static org.geotoolkit.data.osm.xml.OSMXMLConstants.*;


/**
 * Stax writer class for OSM files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMXMLWriter extends StaxStreamWriter{

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public void writeStartDocument() throws XMLStreamException{
        writer.writeStartDocument("UTF-8", "1.0");
    }

    public void writeEndDocument() throws XMLStreamException{
        writer.writeEndDocument();
    }

    public void writeOSMTag() throws XMLStreamException{
        writer.writeStartElement(TAG_OSM);
    }

    public void writeChangeSet(ChangeSet cs) throws XMLStreamException{
        writer.writeStartElement(TAG_CHANGESET);

        final Integer id = cs.getId();
        if(id != null){
            writer.writeAttribute(ATT_ID, id.toString());
        }

        final User user = cs.getUser();
        if(user != User.NONE){
            writer.writeAttribute(ATT_UID, Integer.toString(user.getId()));
            writer.writeAttribute(ATT_USER, user.getUserName());
        }

        final Long createdAt = cs.getTimestamp();
        if(createdAt != null){
            final Date d = new Date(createdAt);
            writer.writeAttribute(ATT_CHANGESET_CREATEDAT, sdf.format(d));
        }

        final Boolean open = cs.getOpen();
        if(open != null){
            writer.writeAttribute(ATT_CHANGESET_OPEN, open.toString());
        }

        final Envelope env = cs.getEnv();
        if(env != null){
            writer.writeAttribute(ATT_CHANGESET_MINLON, Double.toString(env.getMinimum(0)));
            writer.writeAttribute(ATT_CHANGESET_MINLAT, Double.toString(env.getMinimum(1)));
            writer.writeAttribute(ATT_CHANGESET_MAXLON, Double.toString(env.getMaximum(0)));
            writer.writeAttribute(ATT_CHANGESET_MAXLAT, Double.toString(env.getMaximum(1)));
        }

        writeTags(cs.getTags());
        writer.writeEndElement();
    }

    public void writeTags(List<Tag> tags) throws XMLStreamException{
        if(tags == null || tags.isEmpty()) return;

        for(final Tag tag : tags){
            writer.writeStartElement(TAG_TAG);
            writer.writeAttribute(ATT_TAG_KEY, tag.getK());
            writer.writeAttribute(ATT_TAG_VALUE, tag.getV());
            writer.writeEndElement();
        }
    }

}
