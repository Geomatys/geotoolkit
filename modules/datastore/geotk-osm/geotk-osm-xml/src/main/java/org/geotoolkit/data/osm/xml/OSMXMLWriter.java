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
import org.geotoolkit.data.osm.model.IdentifiedElement;
import org.geotoolkit.data.osm.model.Member;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Tag;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.data.osm.model.User;
import org.geotoolkit.data.osm.model.Way;
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

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public void writeStartDocument() throws XMLStreamException{
        writer.writeStartDocument("UTF-8", "1.0");
    }

    public void writeEndDocument() throws XMLStreamException{
        writer.writeEndDocument();
    }

    public void writeOSMTag() throws XMLStreamException{
        writer.writeStartElement(TAG_OSM);
    }

    public void writeOSMChangeTag(String version, String generator) throws XMLStreamException{
        writer.writeStartElement(TAG_OSM_CHANGE);
        if(version != null){
            writer.writeAttribute(ATT_VERSION, version);
        }

        if(generator != null){
            writer.writeAttribute(ATT_GENERATOR, generator);
        }
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

    public void writeTransaction(Transaction transaction) throws XMLStreamException{
        if(transaction == null) return;

        writer.writeStartElement(transaction.getType().getTagName());

        final String version = transaction.getVersion();
        final String generator = transaction.getGenerator();
        if(version != null){
            writer.writeAttribute(ATT_VERSION, version);
        }
        if(generator != null){
            writer.writeAttribute(ATT_GENERATOR, generator);
        }

        for(final IdentifiedElement ele : transaction.getElements()){
            writeElement(ele);
        }

        writer.writeEndElement();
    }

    public void writeElement(IdentifiedElement element) throws XMLStreamException{
        if(element instanceof Node){
            writeNode((Node) element);
        }else if(element instanceof Way){
            writeWay((Way) element);
        }else if(element instanceof Relation){
            writeRelation((Relation) element);
        }
    }

    private void writeCommunAttributs(IdentifiedElement element) throws XMLStreamException{
        final int changeset = element.getChangeset();
        if(changeset > 0){
            writer.writeAttribute(ATT_CHANGESET, Integer.toString(changeset));
        }

        writer.writeAttribute(ATT_ID, Long.toString(element.getId()));
        writer.writeAttribute(ATT_TIMESTAMP, sdf.format(new Date(element.getTimestamp())));

        final User user = element.getUser();
        if(user != null && user != User.NONE){
            writer.writeAttribute(ATT_UID, Integer.toString(user.getId()));
            final String name = user.getUserName();
            if(name != null){
                writer.writeAttribute(ATT_USER,name);
            }
        }

        final int version = element.getVersion();
        if(version > 0){
            writer.writeAttribute(ATT_VERSION, Integer.toString(version));
        }
    }

    public void writeNode(Node element) throws XMLStreamException{
        writer.writeStartElement(TAG_NODE);
        writer.writeAttribute(ATT_NODE_LAT, Double.toString(element.getLatitude()));
        writer.writeAttribute(ATT_NODE_LON, Double.toString(element.getLongitude()));
        writeCommunAttributs(element);
        writeTags(element.getTags());
        writer.writeEndElement();
    }

    public void writeWay(Way element) throws XMLStreamException{
        writer.writeStartElement(TAG_WAY);
        writeCommunAttributs(element);
        writeWayNodes(element.getNodesIds());
        writeTags(element.getTags());
        writer.writeEndElement();
    }

    private void writeWayNodes(List<Long> nodes) throws XMLStreamException{
        for(Long ref : nodes){
            writer.writeStartElement(TAG_WAYND);
            writer.writeAttribute(ATT_WAYND_REF, Long.toString(ref));
            writer.writeEndElement();
        }
    }

    public void writeRelation(Relation element) throws XMLStreamException{
        writer.writeStartElement(TAG_REL);
        writeCommunAttributs(element);
        writeRelationMembers(element.getMembers());
        writeTags(element.getTags());
        writer.writeEndElement();
    }

    private void writeRelationMembers(List<Member> members) throws XMLStreamException{
        for(Member m : members){
            writer.writeStartElement(TAG_RELMB);
            writer.writeAttribute(ATT_RELMB_REF, Long.toString(m.getReference()));
            writer.writeAttribute(ATT_RELMB_ROLE, m.getRole());
            writer.writeAttribute(ATT_RELMB_TYPE, m.getType().getAttributValue());
            writer.writeEndElement();
        }
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
