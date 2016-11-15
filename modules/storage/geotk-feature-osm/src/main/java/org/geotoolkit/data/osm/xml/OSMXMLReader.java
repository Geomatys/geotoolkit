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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.osm.model.Api;
import org.geotoolkit.data.osm.model.Bound;
import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.GPXFileMetadata;
import org.geotoolkit.data.osm.model.MemberType;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.data.osm.model.TransactionType;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.xml.StaxStreamReader;

import org.opengis.geometry.Envelope;

import static javax.xml.stream.XMLStreamReader.*;
import org.geotoolkit.data.osm.model.OSMModelConstants;
import static org.geotoolkit.data.osm.xml.OSMXMLConstants.*;
import org.opengis.feature.Feature;

/**
 * Stax reader class for OSM XML planet files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMXMLReader extends StaxStreamReader{

    private final GeometryFactory GF = new GeometryFactory();
    private final ISODateParser dateParser = new ISODateParser();
    private Envelope envelope;

    /**
     * Caches.
     */
    private final List<Feature> tags = new ArrayList<>();
    private final List<Feature> members = new ArrayList<>();
    private final List<Long> nodes = new ArrayList<>();
    private final List<Feature> transaction = new ArrayList<>();
    private long id = Long.MIN_VALUE;
    private int version = Integer.MIN_VALUE;
    private int changeset = Integer.MIN_VALUE;
    private String user = null;
    private int uid = OSMModelConstants.USER_ID_NONE;
    private long timestamp = Long.MIN_VALUE;

    private Object current;

    private long moveToId = -1;

    @Override
    public void setInput(final Object input) throws IOException, XMLStreamException {
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
                            || TAG_GPX.equalsIgnoreCase(typeName)
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
    public void moveTo(final Long id) throws XMLStreamException{
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
     * @return IdentifiedElement (Way,node,Relation), Transaction (in case of daily update files)
     * or GPXFileMetadata
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
        id = Long.MIN_VALUE;
        version = Integer.MIN_VALUE;
        changeset = Integer.MIN_VALUE;
        user = null;
        uid = OSMModelConstants.USER_ID_NONE;
        timestamp = Long.MIN_VALUE;
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
                }else if(localName.equalsIgnoreCase(TAG_GPX)){
                    current = parseGPX();
                }else{
                    System.out.println("Unexpected tag : " + localName);
                }
            }
        }

    }

    private Long toDateLong(final String str){
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
        if(id == Long.MIN_VALUE) throw new XMLStreamException("Error in xml file, entity with no id");

        changeset = Integer.parseInt(strChangeset);
        if(changeset == Integer.MIN_VALUE)   throw new XMLStreamException("Error in xml file, change set is null");

        timestamp = toDateLong(strTimestamp);
        if(timestamp == Long.MIN_VALUE)   throw new XMLStreamException("Error in xml file, timestamp is null");

        if(strUID != null){
            uid = Integer.parseInt(strUID);
        }

        version = Integer.parseInt(strVersion);
        if(version == Integer.MIN_VALUE)     throw new XMLStreamException("Error in xml file, version is null");

        return true;
    }

    private Feature parseNode() throws XMLStreamException {
        resetCache();

        if(!parseIdentifiedAttributs()){
            //we dont want this entity
            toTagEnd(TAG_NODE);
            return null;
        }

        String lat = reader.getAttributeValue(null, ATT_NODE_LAT);
        String lon = reader.getAttributeValue(null, ATT_NODE_LON);

        if(lat == null || lon == null){
            //throw new XMLStreamException("Error in xml file, osm node lat/lon not defined correctly");
            //TODO : recheck the evolution of the specification
            //TODO : new attribute visible
            //TODO : http://wiki.openstreetmap.org/wiki/API_v0.6/XSD
            lat = "NaN";
            lon = "NaN";
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
                        final Feature node = OSMModelConstants.TYPE_NODE.newInstance();
                        final Point pt = GF.createPoint(new Coordinate(Double.parseDouble(lon), Double.parseDouble(lat)));
                        node.setPropertyValue("point", pt);
                        if (user!=null || uid != OSMModelConstants.USER_ID_NONE) {
                            final Feature u = OSMModelConstants.TYPE_USER.newInstance();
                            u.setPropertyValue(ATT_UID, uid);
                            u.setPropertyValue(ATT_USER, user);
                            node.setPropertyValue("user", u);
                        }
                        node.setPropertyValue(ATT_ID, id);
                        node.setPropertyValue(ATT_VERSION, version);
                        node.setPropertyValue(ATT_CHANGESET, changeset);
                        node.setPropertyValue(ATT_TIMESTAMP, timestamp);
                        node.setPropertyValue("tags", tags);
                        return node;
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, node tag without end.");
    }

    private Feature parseWay() throws XMLStreamException {
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
                        //end of the way element
                        final Feature way = OSMModelConstants.TYPE_WAY.newInstance();
                        if (user!=null || uid != OSMModelConstants.USER_ID_NONE) {
                            final Feature u = OSMModelConstants.TYPE_USER.newInstance();
                            u.setPropertyValue(ATT_UID, uid);
                            u.setPropertyValue(ATT_USER, user);
                            way.setPropertyValue("user", u);
                        }
                        way.setPropertyValue(ATT_ID, id);
                        way.setPropertyValue(ATT_VERSION, version);
                        way.setPropertyValue(ATT_CHANGESET, changeset);
                        way.setPropertyValue(ATT_TIMESTAMP, timestamp);
                        way.setPropertyValue("tags", tags);
                        way.setPropertyValue(TAG_WAYND, nodes);
                        return way;
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, way tag without end.");
    }

    private Feature parseRelation() throws XMLStreamException {
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
                        final Feature relation = OSMModelConstants.TYPE_RELATION.newInstance();
                        if (user!=null || uid != OSMModelConstants.USER_ID_NONE) {
                            final Feature u = OSMModelConstants.TYPE_USER.newInstance();
                            u.setPropertyValue(ATT_UID, uid);
                            u.setPropertyValue(ATT_USER, user);
                            relation.setPropertyValue("user", u);
                        }
                        relation.setPropertyValue(ATT_ID, id);
                        relation.setPropertyValue(ATT_VERSION, version);
                        relation.setPropertyValue(ATT_CHANGESET, changeset);
                        relation.setPropertyValue(ATT_TIMESTAMP, timestamp);
                        relation.setPropertyValue("tags", tags);
                        relation.setPropertyValue("members", members);
                        return relation;
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
                        Feature user = null;
                        if(strUID!=null){
                            user = OSMModelConstants.TYPE_USER.newInstance();
                            user.setPropertyValue(ATT_UID, Integer.parseInt(strUID));
                            user.setPropertyValue(ATT_USER, strUser);
                        }
                        return new ChangeSet(
                                (strID!=null) ? Integer.parseInt(strID) : null,
                                user,
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

    private Transaction parseTransaction(final TransactionType tt) throws XMLStreamException {
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

    private GPXFileMetadata parseGPX() throws XMLStreamException{
        final String id = reader.getAttributeValue(null, ATT_ID);
        final String name = reader.getAttributeValue(null, ATT_GPX_NAME);
        final String lat = reader.getAttributeValue(null, ATT_GPX_LAT);
        final String lon = reader.getAttributeValue(null, ATT_GPX_LON);
        final String user = reader.getAttributeValue(null, ATT_USER);
        final String time = reader.getAttributeValue(null, ATT_TIMESTAMP);
        final String publik = reader.getAttributeValue(null, ATT_GPX_PUBLIC);
        final String pending = reader.getAttributeValue(null, ATT_GPX_PENDING);
        toTagEnd(TAG_GPX);

        return new GPXFileMetadata(Long.parseLong(id),name, user, 
                Boolean.parseBoolean(publik), Boolean.parseBoolean(pending),
                TemporalUtilities.parseDateSafe(time,false),
                Double.parseDouble(lat),
                Double.parseDouble(lon));

    }


    private void parseTag(final List<Feature> tags) throws XMLStreamException{
        final String key = reader.getAttributeValue(null, ATT_TAG_KEY);
        final String value = reader.getAttributeValue(null, ATT_TAG_VALUE);

        if(key == null || value == null){
            throw new XMLStreamException("Error in xml file, tag has no proper key value pair.");
        }

        toTagEnd(TAG_TAG);

        final Feature tag = OSMModelConstants.TYPE_TAG.newInstance();
        tag.setPropertyValue("k", key);
        tag.setPropertyValue("v", value);
        tags.add(tag);
    }

    private Long parseWayNode() throws XMLStreamException{
        final String ref = reader.getAttributeValue(null, ATT_WAYND_REF);

        if(ref == null){
            throw new XMLStreamException("Error in xml file, way node has no reference attribut.");
        }

        toTagEnd(TAG_WAYND);
        return Long.parseLong(ref);
    }

    private Feature parseRelationMember() throws XMLStreamException{
        final String ref = reader.getAttributeValue(null, ATT_RELMB_REF);
        final String role = reader.getAttributeValue(null, ATT_RELMB_ROLE);
        final String type = reader.getAttributeValue(null, ATT_RELMB_TYPE);

        if(ref == null){
            throw new XMLStreamException("Error in xml file, relation member node has no reference attribut.");
        }

        toTagEnd(TAG_RELMB);

        final Feature member = OSMModelConstants.TYPE_RELATION_MEMBER.newInstance();
        member.setPropertyValue("ref", Long.parseLong(ref));
        member.setPropertyValue("role", role);
        member.setPropertyValue("type", MemberType.valueOfIgnoreCase(type));
        return member;
    }

}
