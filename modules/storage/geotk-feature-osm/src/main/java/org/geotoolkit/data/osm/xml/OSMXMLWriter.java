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

import com.vividsolutions.jts.geom.Point;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.MemberType;
import org.geotoolkit.data.osm.model.OSMModelConstants;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.geometry.Envelope;

import static org.geotoolkit.data.osm.xml.OSMXMLConstants.*;
import org.opengis.feature.Feature;


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

    public void writeOSMChangeTag(final String version, final String generator) throws XMLStreamException{
        writer.writeStartElement(TAG_OSM_CHANGE);
        if(version != null){
            writer.writeAttribute(ATT_VERSION, version);
        }

        if(generator != null){
            writer.writeAttribute(ATT_GENERATOR, generator);
        }
    }

    public void writeChangeSet(final ChangeSet cs) throws XMLStreamException{
        writer.writeStartElement(TAG_CHANGESET);

        final Integer id = cs.getId();
        if(id != null){
            writer.writeAttribute(ATT_ID, id.toString());
        }

        final Feature user = cs.getUser();
        if(user != null && user != OSMModelConstants.USER_NONE){
            writer.writeAttribute(ATT_UID, Integer.toString((int)user.getPropertyValue(ATT_UID)));
            writer.writeAttribute(ATT_USER, (String)user.getPropertyValue(ATT_USER));
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

    public void writeTransaction(final Transaction transaction) throws XMLStreamException{
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

        for(final Feature ele : transaction.getElements()){
            writeElement(ele);
        }

        writer.writeEndElement();
    }

    public void writeElement(final Feature element) throws XMLStreamException{
        if(element.getType() == OSMModelConstants.TYPE_NODE){
            writeNode(element);
        }else if(element.getType() == OSMModelConstants.TYPE_WAY){
            writeWay(element);
        }else if(element.getType() == OSMModelConstants.TYPE_RELATION){
            writeRelation(element);
        }
    }

    private void writeCommunAttributs(final Feature element) throws XMLStreamException{
        final int changeset = (Integer)element.getPropertyValue(ATT_CHANGESET);
        if(changeset > 0){
            writer.writeAttribute(ATT_CHANGESET, Integer.toString(changeset));
        }

        writer.writeAttribute(ATT_ID, Long.toString((Long)element.getPropertyValue(ATT_ID)));
        writer.writeAttribute(ATT_TIMESTAMP, sdf.format(new Date((Long)element.getPropertyValue(ATT_TIMESTAMP))));

        final Feature user = (Feature) element.getPropertyValue("user");
        if(user != null){
            writer.writeAttribute(ATT_UID, Integer.toString((Integer)user.getPropertyValue("id")));
            final String name = (String)user.getPropertyValue(ATT_USER);
            if(name != null){
                writer.writeAttribute(ATT_USER,name);
            }
        }

        final int version = (Integer)element.getPropertyValue(ATT_VERSION);
        if(version > 0){
            writer.writeAttribute(ATT_VERSION, Integer.toString(version));
        }
    }

    public void writeNode(final Feature element) throws XMLStreamException{
        writer.writeStartElement(TAG_NODE);
        final Point pt = (Point) element.getPropertyValue("point");
        writer.writeAttribute(ATT_NODE_LAT, Double.toString(pt.getY()));
        writer.writeAttribute(ATT_NODE_LON, Double.toString(pt.getX()));
        writeCommunAttributs(element);
        writeTags((Collection) element.getPropertyValue("tags"));
        writer.writeEndElement();
    }

    public void writeWay(final Feature element) throws XMLStreamException{
        writer.writeStartElement(TAG_WAY);
        writeCommunAttributs(element);
        writeWayNodes((Collection)element.getPropertyValue(TAG_WAYND));
        writeTags((Collection) element.getPropertyValue("tags"));
        writer.writeEndElement();
    }

    private void writeWayNodes(final Collection<Long> nodes) throws XMLStreamException{
        for(Long ref : nodes){
            writer.writeStartElement(TAG_WAYND);
            writer.writeAttribute(ATT_WAYND_REF, Long.toString(ref));
            writer.writeEndElement();
        }
    }

    public void writeRelation(final Feature element) throws XMLStreamException{
        writer.writeStartElement(TAG_REL);
        writeCommunAttributs(element);
        writeRelationMembers((Collection) element.getPropertyValue("members"));
        writeTags((Collection) element.getPropertyValue("tags"));
        writer.writeEndElement();
    }

    private void writeRelationMembers(final Collection<Feature> members) throws XMLStreamException{
        for(Feature m : members){
            writer.writeStartElement(TAG_RELMB);
            writer.writeAttribute(ATT_RELMB_REF, Long.toString((Long)m.getPropertyValue(ATT_RELMB_REF)));
            writer.writeAttribute(ATT_RELMB_ROLE, (String) m.getPropertyValue(ATT_RELMB_ROLE));
            writer.writeAttribute(ATT_RELMB_TYPE, ((MemberType)m.getPropertyValue(ATT_RELMB_TYPE)).getAttributValue());
            writer.writeEndElement();
        }
    }


    public void writeTags(final Collection<Feature> tags) throws XMLStreamException{
        if(tags == null || tags.isEmpty()) return;

        for(final Feature tag : tags){
            writer.writeStartElement(TAG_TAG);
            writer.writeAttribute(ATT_TAG_KEY, (String) tag.getPropertyValue(ATT_TAG_KEY));
            writer.writeAttribute(ATT_TAG_VALUE, (String) tag.getPropertyValue(ATT_TAG_VALUE));
            writer.writeEndElement();
        }
    }

}
