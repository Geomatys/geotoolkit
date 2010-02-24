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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.geotoolkit.data.osm.model.Bound;
import org.geotoolkit.data.osm.model.IdentifiedElement;
import org.geotoolkit.data.osm.model.Member;
import org.geotoolkit.data.osm.model.MemberType;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.User;
import org.geotoolkit.data.osm.model.Way;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.geometry.Envelope;

import static org.geotoolkit.data.osm.xml.OSMXMLConstants.*;

/**
 * Stax reader class for OSM XML planet files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMXMLReader{

    private static final ObjectConverter<String,Date> STRING_TO_DATE;

    static{
        //todo, must update filter factory, use meta-inf object factory registry
        //to replace static initialization
        FactoryFinder.getFilterFactory(null);


        ObjectConverter<String,Date> oc = null;
        try {
            oc = ConverterRegistry.system().converter(String.class, Date.class);
        } catch (NonconvertibleObjectException ex) {
            throw new IllegalStateException("String to date converter is needed to read osm files.");
        }
        STRING_TO_DATE = oc;
    }

    private final XMLStreamReader reader;
    private Envelope envelope;

    /**
     * Caches.
     */
    private final Map<String,String> tags = new HashMap<String, String>();
    private final List<Member> members = new ArrayList<Member>();
    private final List<Long> nodes = new ArrayList<Long>();
    private long id = -1;
    private int version = -1;
    private int changeset = -1;
    private String user = null;
    private int uid = User.USER_ID_NONE;
    private long timestamp = -1;

    private IdentifiedElement current;

    private long moveToId = -1;

    public OSMXMLReader(File file) throws FileNotFoundException, XMLStreamException{
        final XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
        XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.FALSE);
        reader = XMLfactory.createXMLStreamReader(new FileInputStream(file));

        //search for the bound tag to generate the envelope
        searchLoop :
        while(reader.hasNext()){
            final int type = reader.next();

            switch (type) {
                // Si c'est un début d'elément, on garde son type
                case XMLStreamReader.START_ELEMENT:
                    final String typeName = reader.getLocalName();
                    if(typeName.equalsIgnoreCase(TAG_BOUNDS)){
                        envelope = parseBound(reader);
                        break searchLoop;
                    }else if(typeName.equalsIgnoreCase(TAG_NODE)
                            || typeName.equalsIgnoreCase(TAG_WAY)
                            || typeName.equalsIgnoreCase(TAG_REL)){
                        //there is no bounds tag
                        break searchLoop;
                    }
            }
        }

    }

    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * Iterate in the file until we reach a entity with an id
     * that match the given one.
     * 
     * @param id : identifier to move to
     */
    public void moveTo(Long id) throws XMLStreamException{
        moveToId = id;
        read();
        //we have reached the wanted item, reset the search id
        moveToId = -1;
    }

    public boolean hasNext() throws XMLStreamException{
        read();
        return current != null;
    }
    
    public IdentifiedElement next() throws XMLStreamException{
        read();
        final IdentifiedElement ele = current;
        current = null;
        return ele;
    }

    public void close() throws IOException, XMLStreamException{
        reader.close();
    }

    private void resetCache(){
        members.clear();
        tags.clear();
        nodes.clear();
        id = -1;
        version = -1;
        changeset = -1;
        user = null;
        uid = User.USER_ID_NONE;
        timestamp = -1;
    }

    private void read() throws XMLStreamException{
        if(current != null) return;

        boolean first = true;
        while ( (reader.hasNext() && current == null) || first) {
            final int type;
            if(first){
                type = reader.getEventType();
                first = false;
            }else{
                type = reader.next();
            }
            
            switch (type) {
                // Si c'est un début d'elément, on garde son type
                case XMLStreamReader.START_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(TAG_NODE)){
                        resetCache();
                        current = parseNode(reader);
                    }else if(reader.getLocalName().equalsIgnoreCase(TAG_WAY)){
                        resetCache();
                        current = parseWay(reader);
                    }else if(reader.getLocalName().equalsIgnoreCase(TAG_REL)){
                        resetCache();
                        current = parseRelation(reader);
                    }
            }
        }

        if(reader.hasNext()){
            //nothing left to read
            reader.close();
        }
    }

    private static Long toDateLong(String str){
        try {
            Date d = STRING_TO_DATE.convert(str);
            return d.getTime();
        } catch (NonconvertibleObjectException ex) {
            Logger.getLogger(OSMXMLReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private Envelope parseBound(XMLStreamReader reader) throws XMLStreamException {
        Double xmin = null;
        Double xmax = null;
        Double ymin = null;
        Double ymax = null;

        for(int i=0; i<reader.getAttributeCount();i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_BOUNDS_MINLON.equalsIgnoreCase(attName)){
                xmin = Double.valueOf(reader.getAttributeValue(i));
            }else if(ATT_BOUNDS_MAXLON.equalsIgnoreCase(attName)){
                xmax = Double.valueOf(reader.getAttributeValue(i));
            }else if(ATT_BOUNDS_MINLAT.equalsIgnoreCase(attName)){
                ymin = Double.valueOf(reader.getAttributeValue(i));
            }else if(ATT_BOUNDS_MAXLAT.equalsIgnoreCase(attName)){
                ymax = Double.valueOf(reader.getAttributeValue(i));
            }
        }

        if(xmin == null || xmax == null || ymin == null || ymax == null){
            throw new XMLStreamException("Error in xml file, osm bounds not defined correctly");
        }

        toTagEnd(reader, TAG_BOUNDS);

        return Bound.create(xmin, xmax, ymin, ymax);

    }

    /**
     * Parse attributs
     * @param reader
     * @return true if this entity Id matches the one searched (if there is one).
     * false otherwise.
     * 
     * @throws XMLStreamException
     */
    private boolean parseIdentifiedAttributs(XMLStreamReader reader) throws XMLStreamException{
        for(int i=0; i<reader.getAttributeCount();i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_CHANGESET.equalsIgnoreCase(attName)){
                changeset = Integer.parseInt(reader.getAttributeValue(i));
            }else if(ATT_ID.equalsIgnoreCase(attName)){
                id = Long.parseLong(reader.getAttributeValue(i));

                //check if we are in search mode
                if(moveToId > 0 && id != moveToId) return false;

            }else if(ATT_TIMESTAMP.equalsIgnoreCase(attName)){
                timestamp = toDateLong(reader.getAttributeValue(i));
            }else if(ATT_UID.equalsIgnoreCase(attName)){
                uid = Integer.parseInt(reader.getAttributeValue(i));
            }else if(ATT_USER.equalsIgnoreCase(attName)){
                user = reader.getAttributeValue(i);
            }else if(ATT_VERSION.equalsIgnoreCase(attName)){
                version = Integer.parseInt(reader.getAttributeValue(i));
            }
        }

        if(changeset < 0)   throw new XMLStreamException("Error in xml file, change set is null");
        if(timestamp < 0)   throw new XMLStreamException("Error in xml file, timestamp is null");
        if(version < 0)     throw new XMLStreamException("Error in xml file, version is null");
        if(id < 0)          throw new XMLStreamException("Error in xml file, entity with no id");

        return true;
    }


    private Node parseNode(XMLStreamReader reader) throws XMLStreamException {
        Double lat = null;
        Double lon = null;

        if(!parseIdentifiedAttributs(reader)){
            //we dont want this entity
            toTagEnd(reader, TAG_NODE);
            return null;
        }

        for(int i=0; i<reader.getAttributeCount();i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_NODE_LAT.equalsIgnoreCase(attName)){
                lat = Double.valueOf(reader.getAttributeValue(i));
            }else if(ATT_NODE_LON.equalsIgnoreCase(attName)){
                lon = Double.valueOf(reader.getAttributeValue(i));
            }
        }

        if(lat == null || lon == null){
            throw new XMLStreamException("Error in xml file, osm node lat/lon not defined correctly");
        }

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(TAG_TAG)){
                        parseTag(reader, tags);
                    }
                    break;
                case XMLStreamReader.CDATA:
                case XMLStreamReader.CHARACTERS:
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(TAG_NODE)){
                        //end of the node element
                        return new Node(lat, lon, id, version, changeset, User.create(uid, user), timestamp, tags);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, node tag without end.");
    }

    private Way parseWay(XMLStreamReader reader) throws XMLStreamException {

        if(!parseIdentifiedAttributs(reader)){
            //we dont want this entity
            toTagEnd(reader, TAG_WAY);
            return null;
        }

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(TAG_TAG)){
                        parseTag(reader, tags);
                    }else if(reader.getLocalName().equalsIgnoreCase(TAG_WAYND)){
                        nodes.add(parseWayNode(reader));
                    }
                    break;
                case XMLStreamReader.CDATA:
                case XMLStreamReader.CHARACTERS:
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(TAG_WAY)){
                        //end of the node element
                        return new Way(nodes, id, version, changeset, User.create(uid, user), timestamp, tags);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, way tag without end.");
    }

    private Relation parseRelation(XMLStreamReader reader) throws XMLStreamException {

        if(!parseIdentifiedAttributs(reader)){
            //we dont want this entity
            toTagEnd(reader, TAG_REL);
            return null;
        }

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(TAG_TAG)){
                        parseTag(reader, tags);
                    }else if(reader.getLocalName().equalsIgnoreCase(TAG_RELMB)){
                        members.add(parseRelationMember(reader));
                    }
                    break;
                case XMLStreamReader.CDATA:
                case XMLStreamReader.CHARACTERS:
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(TAG_REL)){
                        //end of the relation element
                        return new Relation(members, id, version, changeset, User.create(uid, user), timestamp, tags);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, relation tag without end.");
    }


    private static void parseTag(XMLStreamReader reader, Map<String,String> tags) throws XMLStreamException{
        String key = null;
        String value = null;

        for(int i=0; i<reader.getAttributeCount();i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_TAG_KEY.equalsIgnoreCase(attName)){
                key = reader.getAttributeValue(i);
            }else if(ATT_TAG_VALUE.equalsIgnoreCase(attName)){
                value = reader.getAttributeValue(i);
            }
        }

        if(key == null || value == null){
            throw new XMLStreamException("Error in xml file, tag has no proper key value pair.");
        }

        toTagEnd(reader, TAG_TAG);

        tags.put(key, value);
        return;
    }

    private static Long parseWayNode(XMLStreamReader reader) throws XMLStreamException{
        Long ref = null;

        for(int i=0; i<reader.getAttributeCount();i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_WAYND_REF.equalsIgnoreCase(attName)){
                ref = Long.valueOf(reader.getAttributeValue(i));
            }
        }

        if(ref == null){
            throw new XMLStreamException("Error in xml file, way node has no reference attribut.");
        }

        toTagEnd(reader, TAG_WAYND);
        return ref;
    }

    private static Member parseRelationMember(XMLStreamReader reader) throws XMLStreamException{
        Long ref = null;
        String role = null;
        String type = null;

        for(int i=0; i<reader.getAttributeCount();i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_RELMB_REF.equalsIgnoreCase(attName)){
                ref = Long.valueOf(reader.getAttributeValue(i));
            }else if(ATT_RELMB_ROLE.equalsIgnoreCase(attName)){
                role = reader.getAttributeValue(i);
            }else if(ATT_RELMB_TYPE.equalsIgnoreCase(attName)){
                type = reader.getAttributeValue(i);
            }
        }

        if(ref == null){
            throw new XMLStreamException("Error in xml file, relation member node has no reference attribut.");
        }

        toTagEnd(reader, TAG_RELMB);

        return new Member(ref, MemberType.valueOfIgnoreCase(type), role);
    }


    private static void toTagEnd(final XMLStreamReader reader, final String tagName) throws XMLStreamException{
        while (reader.hasNext()) {
            final int type = reader.next();
            switch (type) {
                case XMLStreamReader.END_ELEMENT:
                    if(tagName.equalsIgnoreCase(reader.getLocalName())){
                        //end of the tag.
                        return;
                    }
            }
        }

        throw new XMLStreamException("Error in xml file, Tag "+tagName+" without end.");
    }

}
