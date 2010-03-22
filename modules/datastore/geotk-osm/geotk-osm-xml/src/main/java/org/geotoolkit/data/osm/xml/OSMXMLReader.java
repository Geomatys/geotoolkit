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

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.geotoolkit.data.osm.model.Api;

import org.geotoolkit.data.osm.model.Bound;
import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.IdentifiedElement;
import org.geotoolkit.data.osm.model.Member;
import org.geotoolkit.data.osm.model.MemberType;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.data.osm.model.TransactionType;
import org.geotoolkit.data.osm.model.User;
import org.geotoolkit.data.osm.model.Way;
import org.geotoolkit.temporal.object.FastDateParser;

import org.opengis.geometry.Envelope;

import static org.geotoolkit.data.osm.xml.OSMXMLConstants.*;

/**
 * Stax reader class for OSM XML planet files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMXMLReader{

    private final XMLStreamReader reader;
    private final FastDateParser dateParser = new FastDateParser();
    private Envelope envelope;

    /**
     * Caches.
     */
    private final Map<String,String> tags = new HashMap<String, String>();
    private final List<Member> members = new ArrayList<Member>();
    private final List<Long> nodes = new ArrayList<Long>();
    private final List<IdentifiedElement> transaction = new ArrayList<IdentifiedElement>();
    private long id = -1;
    private int version = -1;
    private int changeset = -1;
    private String user = null;
    private int uid = User.USER_ID_NONE;
    private long timestamp = -1;

    private Object current;

    private long moveToId = -1;


    private static final XMLStreamReader toReader(File file) throws XMLStreamException, FileNotFoundException{
        final XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
        XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.FALSE);
        return XMLfactory.createXMLStreamReader(new FileInputStream(file));
    }

    public OSMXMLReader(File file) throws FileNotFoundException, XMLStreamException{
        this(toReader(file));
    }

    public OSMXMLReader(XMLStreamReader reader) throws FileNotFoundException, XMLStreamException{
        this.reader = reader;

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
                            || typeName.equalsIgnoreCase(TAG_REL)
                            || typeName.equalsIgnoreCase(TAG_MODIFY)
                            || typeName.equalsIgnoreCase(TAG_CREATE)
                            || typeName.equalsIgnoreCase(TAG_DELETE)
                            || typeName.equalsIgnoreCase(TAG_CHANGESET)
                            || typeName.equalsIgnoreCase(TAG_API)){
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

    /**
     *
     * @return IdentifiedElement (Way,node,Relation) or Transaction (in case of daily update files)
     * @throws XMLStreamException
     */
    public Object next() throws XMLStreamException{
        read();
        final Object ele = current;
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
        while ( first || (current == null && reader.hasNext()) ) {
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
                    final String localName = reader.getLocalName();
                    if(localName.equalsIgnoreCase(TAG_NODE)){
                        current = parseNode(reader);
                    }else if(localName.equalsIgnoreCase(TAG_WAY)){
                        current = parseWay(reader);
                    }else if(localName.equalsIgnoreCase(TAG_REL)){
                        current = parseRelation(reader);
                    }else if(localName.equalsIgnoreCase(TAG_CREATE)){
                        current = parseCreate(reader);
                    }else if(localName.equalsIgnoreCase(TAG_MODIFY)){
                        current = parseModify(reader);
                    }else if(localName.equalsIgnoreCase(TAG_DELETE)){
                        current = parseDelete(reader);
                    }else if(localName.equalsIgnoreCase(TAG_CHANGESET)){
                        current = parseChangeSet(reader);
                    }else if(localName.equalsIgnoreCase(TAG_API)){
                        current = parseAPI(reader);
                    }else{
                        System.out.println("Unexpected tag : " + localName);
                    }
            }
        }

    }

    private Long toDateLong(String str){
        return dateParser.parseToMillis(str);
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
        resetCache();

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
        resetCache();

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
        resetCache();

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

    private ChangeSet parseChangeSet(XMLStreamReader reader) throws XMLStreamException {
        //todo, we need this ?
        toTagEnd(reader, TAG_CHANGESET);
        return new ChangeSet();
    }

    private Transaction parseCreate(XMLStreamReader reader) throws XMLStreamException {
        return parseTransaction(reader, TransactionType.CREATE);
    }

    private Transaction parseModify(XMLStreamReader reader) throws XMLStreamException {
        return parseTransaction(reader, TransactionType.MODIFY);
    }

    private Transaction parseDelete(XMLStreamReader reader) throws XMLStreamException {
        return parseTransaction(reader, TransactionType.DELETE);
    }

    private Transaction parseTransaction(XMLStreamReader reader, TransactionType tt) throws XMLStreamException {
        transaction.clear();

        final String version = reader.getAttributeValue(null, ATT_VERSION);
        final String generator = reader.getAttributeValue(null, ATT_GENERATOR);

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(localName.equalsIgnoreCase(TAG_NODE)){
                        transaction.add(parseNode(reader));
                    }else if(localName.equalsIgnoreCase(TAG_WAY)){
                        transaction.add(parseWay(reader));
                    }else if(localName.equalsIgnoreCase(TAG_REL)){
                        transaction.add(parseRelation(reader));
                    }
                    break;
                case XMLStreamReader.CDATA:
                case XMLStreamReader.CHARACTERS:
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(tt.getTagName())){
                        //end of the transaction element
                        return new Transaction(tt,transaction,version,generator);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, modify tag without end.");
    }

    private Api parseAPI(XMLStreamReader reader) throws XMLStreamException {

        String versionMinimum = null;
        String versionMaximum = null;
        Double areaMaximum = null;
        Integer tracePointsPerPage = null;
        Integer wayNodeMaximum = null;
        Integer changesetMaximum = null;
        Integer timeout = null;

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(localName.equalsIgnoreCase(TAG_API_AREA)){
                        areaMaximum = Double.valueOf(reader.getAttributeValue(null, ATT_API_MAXIMUM));
                    }else if(localName.equalsIgnoreCase(TAG_API_CHANGESETS)){
                        changesetMaximum = Integer.valueOf(reader.getAttributeValue(null, ATT_API_MAXIMUM_ELEMENTS));
                    }else if(localName.equalsIgnoreCase(TAG_API_TIMEOUT)){
                        timeout = Integer.valueOf(reader.getAttributeValue(null, ATT_API_SECONDS));
                    }else if(localName.equalsIgnoreCase(TAG_API_TRACEPOINTS)){
                        tracePointsPerPage = Integer.valueOf(reader.getAttributeValue(null, ATT_API_PER_PAGE));
                    }else if(localName.equalsIgnoreCase(TAG_API_VERSION)){
                        versionMinimum = reader.getAttributeValue(null, ATT_API_MINIMUM);
                        versionMaximum = reader.getAttributeValue(null, ATT_API_MAXIMUM);
                    }else if(localName.equalsIgnoreCase(TAG_API_WAYNODES)){
                        wayNodeMaximum = Integer.valueOf(reader.getAttributeValue(null, ATT_API_MAXIMUM));
                    }
                    break;
                case XMLStreamReader.CDATA:
                case XMLStreamReader.CHARACTERS:
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(TAG_API)){

                        if(versionMinimum == null){
                            throw new XMLStreamException("Invalid Api element, missing parameter : versionMinimum");
                        }
                        if(versionMaximum == null ){
                            throw new XMLStreamException("Invalid Api element, missing parameter : versionMaximum");
                        }
                        if(areaMaximum == null){
                            throw new XMLStreamException("Invalid Api element, missing parameter : areaMaximum");
                        }
                        if(tracePointsPerPage == null){
                            throw new XMLStreamException("Invalid Api element, missing parameter : tracePointsPerPage");
                        }
                        if(wayNodeMaximum == null){
                            throw new XMLStreamException("Invalid Api element, missing parameter : wayNodeMaximum");
                        }
                        if(changesetMaximum == null){
                            throw new XMLStreamException("Invalid Api element, missing parameter : changesetMaximum");
                        }
                        if(timeout == null){
                            throw new XMLStreamException("Invalid Api element, missing parameter : timeout");
                        }

                        //end of the api element
                        return new Api(versionMinimum, versionMaximum, areaMaximum,
                                tracePointsPerPage, wayNodeMaximum, changesetMaximum, timeout);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, modify tag without end.");
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
