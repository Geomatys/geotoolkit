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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

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
import org.geotoolkit.xml.StaxStreamReader;

import org.opengis.geometry.Envelope;

import static javax.xml.stream.XMLStreamReader.*;
import static org.geotoolkit.data.osm.xml.OSMXMLConstants.*;

/**
 * Stax reader class for OSM XML planet files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMXMLReader extends StaxStreamReader{

    private final FastDateParser dateParser = new FastDateParser();
    private Envelope envelope;

    /**
     * Caches.
     */
    private final Map<String,String> tags = new LinkedHashMap<String, String>();
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

    @Override
    public void setInput(Object input) throws IOException, XMLStreamException {
        super.setInput(input);

        //search for the bound tag to generate the envelope
        searchLoop :
        while(reader.hasNext()){
            final int type = reader.next();

            switch (type) {
                // Si c'est un début d'elément, on garde son type
                case START_ELEMENT:
                    final String typeName = reader.getLocalName();
                    if(TAG_BOUNDS.equalsIgnoreCase(typeName)){
                        envelope = parseBound();
                        break searchLoop;
                    }else if(  TAG_NODE.equalsIgnoreCase(typeName)
                            || TAG_WAY.equalsIgnoreCase(typeName)
                            || TAG_REL.equalsIgnoreCase(typeName)
                            || TAG_MODIFY.equalsIgnoreCase(typeName)
                            || TAG_CREATE.equalsIgnoreCase(typeName)
                            || TAG_DELETE.equalsIgnoreCase(typeName)
                            || TAG_CHANGESET.equalsIgnoreCase(typeName)
                            || TAG_API.equalsIgnoreCase(typeName)){
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
            
            if(type == START_ELEMENT) {
                final String localName = reader.getLocalName();
                
                if(localName.equalsIgnoreCase(TAG_NODE)){
                    current = parseNode();
                }else if(localName.equalsIgnoreCase(TAG_WAY)){
                    current = parseWay();
                }else if(localName.equalsIgnoreCase(TAG_REL)){
                    current = parseRelation();
                }else if(localName.equalsIgnoreCase(TAG_CREATE)){
                    current = parseTransaction(TransactionType.CREATE);
                }else if(localName.equalsIgnoreCase(TAG_MODIFY)){
                    current = parseTransaction(TransactionType.MODIFY);
                }else if(localName.equalsIgnoreCase(TAG_DELETE)){
                    current = parseTransaction(TransactionType.DELETE);
                }else if(localName.equalsIgnoreCase(TAG_CHANGESET)){
                    current = parseChangeSet();
                }else if(localName.equalsIgnoreCase(TAG_API)){
                    current = parseAPI();
                }else{
                    System.out.println("Unexpected tag : " + localName);
                }
            }
        }

    }

    private Long toDateLong(String str){
        return dateParser.parseToMillis(str);
    }

    private Envelope parseBound() throws XMLStreamException {
        final String xmin = reader.getAttributeValue(null, ATT_BOUNDS_MINLON);
        final String xmax = reader.getAttributeValue(null, ATT_BOUNDS_MAXLON);
        final String ymin = reader.getAttributeValue(null, ATT_BOUNDS_MINLAT);
        final String ymax = reader.getAttributeValue(null, ATT_BOUNDS_MAXLAT);

        if(xmin == null || xmax == null || ymin == null || ymax == null){
            throw new XMLStreamException("Error in xml file, osm bounds not defined correctly");
        }

        toTagEnd(TAG_BOUNDS);

        return Bound.create(
                Double.parseDouble(xmin),
                Double.parseDouble(xmax),
                Double.parseDouble(ymin),
                Double.parseDouble(ymax));
    }

    /**
     * Parse attributs
     * @param reader
     * @return true if this entity Id matches the one searched (if there is one).
     * false otherwise.
     * 
     * @throws XMLStreamException
     */
    private boolean parseIdentifiedAttributs() throws XMLStreamException{

        final String strChangeset = reader.getAttributeValue(null, ATT_CHANGESET);
        final String strID = reader.getAttributeValue(null, ATT_ID);
        final String strTimestamp = reader.getAttributeValue(null, ATT_TIMESTAMP);
        final String strUID = reader.getAttributeValue(null, ATT_UID);
        user = reader.getAttributeValue(null, ATT_USER);
        final String strVersion = reader.getAttributeValue(null, ATT_VERSION);

        id = Long.parseLong(strID);
        //check if we are in search mode
        if(moveToId > 0 && id != moveToId) return false;
        if(id < 0) throw new XMLStreamException("Error in xml file, entity with no id");

        changeset = Integer.parseInt(strChangeset);
        if(changeset < 0)   throw new XMLStreamException("Error in xml file, change set is null");

        timestamp = toDateLong(strTimestamp);
        if(timestamp < 0)   throw new XMLStreamException("Error in xml file, timestamp is null");

        if(strUID != null){
            uid = Integer.parseInt(strUID);
        }

        version = Integer.parseInt(strVersion);
        if(version < 0)     throw new XMLStreamException("Error in xml file, version is null");

        return true;
    }

    private Node parseNode() throws XMLStreamException {
        resetCache();

        if(!parseIdentifiedAttributs()){
            //we dont want this entity
            toTagEnd(TAG_NODE);
            return null;
        }

        final String lat = reader.getAttributeValue(null, ATT_NODE_LAT);
        final String lon = reader.getAttributeValue(null, ATT_NODE_LON);

        if(lat == null || lon == null){
            throw new XMLStreamException("Error in xml file, osm node lat/lon not defined correctly");
        }

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    if(TAG_TAG.equalsIgnoreCase(reader.getLocalName())){
                        parseTag(tags);
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_NODE.equalsIgnoreCase(reader.getLocalName())){
                        //end of the node element
                        return new Node(
                                Double.parseDouble(lat),
                                Double.parseDouble(lon),
                                id, version, changeset, User.create(uid, user), timestamp, tags);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, node tag without end.");
    }

    private Way parseWay() throws XMLStreamException {
        resetCache();

        if(!parseIdentifiedAttributs()){
            //we dont want this entity
            toTagEnd(TAG_WAY);
            return null;
        }

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_TAG.equalsIgnoreCase(localName)){
                        parseTag(tags);
                    }else if(TAG_WAYND.equalsIgnoreCase(localName)){
                        nodes.add(parseWayNode());
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_WAY.equalsIgnoreCase(reader.getLocalName())){
                        //end of the node element
                        return new Way(nodes, id, version, changeset, User.create(uid, user), timestamp, tags);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, way tag without end.");
    }

    private Relation parseRelation() throws XMLStreamException {
        resetCache();

        if(!parseIdentifiedAttributs()){
            //we dont want this entity
            toTagEnd(TAG_REL);
            return null;
        }

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_TAG.equalsIgnoreCase(localName)){
                        parseTag(tags);
                    }else if(TAG_RELMB.equalsIgnoreCase(localName)){
                        members.add(parseRelationMember());
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_REL.equalsIgnoreCase(reader.getLocalName())){
                        //end of the relation element
                        return new Relation(members, id, version, changeset, User.create(uid, user), timestamp, tags);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, relation tag without end.");
    }

    private ChangeSet parseChangeSet() throws XMLStreamException {
        resetCache();

        final String strID = reader.getAttributeValue(null, ATT_ID);
        final String strUser = reader.getAttributeValue(null, ATT_USER);
        final String strUID = reader.getAttributeValue(null, ATT_UID);
        final String strTime = reader.getAttributeValue(null, ATT_CHANGESET_CREATEDAT);
        final String strOpen = reader.getAttributeValue(null, ATT_CHANGESET_OPEN);
        final String strMinLon = reader.getAttributeValue(null, ATT_CHANGESET_MINLON);
        final String strMinLat = reader.getAttributeValue(null, ATT_CHANGESET_MINLAT);
        final String strMaxLon = reader.getAttributeValue(null, ATT_CHANGESET_MAXLON);
        final String strMaxLat = reader.getAttributeValue(null, ATT_CHANGESET_MAXLAT);

        final Envelope env;
        if(strMinLon != null && strMinLat != null && strMaxLat != null && strMaxLon != null){
            env = Bound.create(
                    Double.parseDouble(strMinLon),
                    Double.parseDouble(strMaxLon),
                    Double.parseDouble(strMinLat),
                    Double.parseDouble(strMaxLat));
        }else{
            env = null;
        }


        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_TAG.equalsIgnoreCase(localName)){
                        parseTag(tags);
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_CHANGESET.equalsIgnoreCase(reader.getLocalName())){
                        //end of the changeset element
                        return new ChangeSet(
                                (strID!=null) ? Integer.parseInt(strID) : null,
                                (strUID!=null) ? User.create(Integer.parseInt(strUID),strUser) : User.NONE,
                                (strTime!=null) ? toDateLong(strTime) : null,
                                (strOpen!=null) ? Boolean.valueOf(strOpen) : null,
                                env,
                                tags);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, chageset tag without end.");
    }

    private Transaction parseTransaction(TransactionType tt) throws XMLStreamException {
        transaction.clear();

        final String version = reader.getAttributeValue(null, ATT_VERSION);
        final String generator = reader.getAttributeValue(null, ATT_GENERATOR);

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_NODE.equalsIgnoreCase(localName)){
                        transaction.add(parseNode());
                    }else if(TAG_WAY.equalsIgnoreCase(localName)){
                        transaction.add(parseWay());
                    }else if(TAG_REL.equalsIgnoreCase(localName)){
                        transaction.add(parseRelation());
                    }
                    break;
                case END_ELEMENT:
                    if(reader.getLocalName().equalsIgnoreCase(tt.getTagName())){
                        //end of the transaction element
                        return new Transaction(tt,transaction,version,generator);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, modify tag without end.");
    }

    private Api parseAPI() throws XMLStreamException {

        String versionMinimum       = null;
        String versionMaximum       = null;
        String areaMaximum          = null;
        String tracePointsPerPage   = null;
        String wayNodeMaximum       = null;
        String changesetMaximum     = null;
        String timeout              = null;

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(localName.equalsIgnoreCase(TAG_API_AREA)){
                        areaMaximum = reader.getAttributeValue(null, ATT_API_MAXIMUM);
                    }else if(localName.equalsIgnoreCase(TAG_API_CHANGESETS)){
                        changesetMaximum = reader.getAttributeValue(null, ATT_API_MAXIMUM_ELEMENTS);
                    }else if(localName.equalsIgnoreCase(TAG_API_TIMEOUT)){
                        timeout = reader.getAttributeValue(null, ATT_API_SECONDS);
                    }else if(localName.equalsIgnoreCase(TAG_API_TRACEPOINTS)){
                        tracePointsPerPage = reader.getAttributeValue(null, ATT_API_PER_PAGE);
                    }else if(localName.equalsIgnoreCase(TAG_API_VERSION)){
                        versionMinimum = reader.getAttributeValue(null, ATT_API_MINIMUM);
                        versionMaximum = reader.getAttributeValue(null, ATT_API_MAXIMUM);
                    }else if(localName.equalsIgnoreCase(TAG_API_WAYNODES)){
                        wayNodeMaximum = reader.getAttributeValue(null, ATT_API_MAXIMUM);
                    }
                    break;
                case END_ELEMENT:
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
                        return new Api(
                                versionMinimum,
                                versionMaximum,
                                Double.parseDouble(areaMaximum),
                                Integer.parseInt(tracePointsPerPage),
                                Integer.parseInt(wayNodeMaximum),
                                Integer.parseInt(changesetMaximum),
                                Integer.parseInt(timeout));
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, modify tag without end.");
    }


    private void parseTag(Map<String,String> tags) throws XMLStreamException{
        final String key = reader.getAttributeValue(null, ATT_TAG_KEY);
        final String value = reader.getAttributeValue(null, ATT_TAG_VALUE);

        if(key == null || value == null){
            throw new XMLStreamException("Error in xml file, tag has no proper key value pair.");
        }

        toTagEnd(TAG_TAG);

        tags.put(key, value);
        return;
    }

    private Long parseWayNode() throws XMLStreamException{
        final String ref = reader.getAttributeValue(null, ATT_WAYND_REF);

        if(ref == null){
            throw new XMLStreamException("Error in xml file, way node has no reference attribut.");
        }

        toTagEnd(TAG_WAYND);
        return Long.parseLong(ref);
    }

    private Member parseRelationMember() throws XMLStreamException{
        final String ref = reader.getAttributeValue(null, ATT_RELMB_REF);
        final String role = reader.getAttributeValue(null, ATT_RELMB_ROLE);
        final String type = reader.getAttributeValue(null, ATT_RELMB_TYPE);

        if(ref == null){
            throw new XMLStreamException("Error in xml file, relation member node has no reference attribut.");
        }

        toTagEnd(TAG_RELMB);

        return new Member(
                Long.parseLong(ref),
                MemberType.valueOfIgnoreCase(type), role);
    }

}
