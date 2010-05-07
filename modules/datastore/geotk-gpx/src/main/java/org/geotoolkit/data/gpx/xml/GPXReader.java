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

package org.geotoolkit.data.gpx.xml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gpx.model.CopyRight;
import org.geotoolkit.data.gpx.model.GPXModelConstants;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.model.Person;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.xml.StaxStreamReader;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.geometry.Envelope;

import static javax.xml.stream.XMLStreamReader.*;
import static org.geotoolkit.data.gpx.xml.GPXConstants.*;

/**
 * Stax reader class for GPX files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GPXReader extends StaxStreamReader{

    private final GeometryFactory gf = new GeometryFactory();
    private MetaData metadata;    
    private Feature current;
    private int wayPointInc = 0;
    private int routeInc = 0;
    private int trackInc = 0;

    @Override
    public void setInput(Object input) throws IOException, XMLStreamException {
        super.setInput(input);

        //search for the bound tag to generate the envelope
        searchLoop :
        while(reader.hasNext()){
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String typeName = reader.getLocalName();
                    if(TAG_METADATA.equalsIgnoreCase(typeName)){
                        metadata = parseMetaData();
                        break searchLoop;
                    }else if(  TAG_WPT.equalsIgnoreCase(typeName)
                            || TAG_TRK.equalsIgnoreCase(typeName)
                            || TAG_RTE.equalsIgnoreCase(typeName)){
                        //there is no metadata tag
                        break searchLoop;
                    }
            }
        }

    }

    public MetaData getMetadata() {
        return metadata;
    }

    @Override
    public void reset() throws IOException, XMLStreamException {
        super.reset();
        metadata = null;
        current = null;
        wayPointInc = 0;
        routeInc = 0;
        trackInc = 0;
    }

    public boolean hasNext() throws XMLStreamException{
        read();
        return current != null;
    }

    /**
     *
     * @return GPX WayPoint, Route or track
     * @throws XMLStreamException
     */
    public Feature next() throws XMLStreamException{
        read();
        final Feature ele = current;
        current = null;
        return ele;
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
                if(TAG_WPT.equalsIgnoreCase(localName)){
                    current = parseWayPoint(wayPointInc++);
                    break;
                }else if(TAG_RTE.equalsIgnoreCase(localName)){
                    current = parseRoute(routeInc++);
                    break;
                }else if(TAG_TRK.equalsIgnoreCase(localName)){
                    current = parseTrack(trackInc++);
                    break;
                }
            }
        }

    }

    private MetaData parseMetaData() throws XMLStreamException{

        String name = null;
        String desc = null;
        Person person = null;
        CopyRight copyright = null;
        List<URI> links = new ArrayList<URI>();
        Date time = null;
        String keywords = null;
        Envelope env = null;

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_NAME.equalsIgnoreCase(localName)){
                        name = reader.getElementText();
                    }else if(TAG_DESC.equalsIgnoreCase(localName)){
                        desc = reader.getElementText();
                    }else if(TAG_AUTHOR.equalsIgnoreCase(localName)){
                        person = parsePerson();
                    }else if(TAG_COPYRIGHT.equalsIgnoreCase(localName)){
                        copyright = parseCopyRight();
                    }else if(TAG_LINK.equalsIgnoreCase(localName)){
                        links.add(parseLink());
                    }else if(TAG_METADATA_TIME.equalsIgnoreCase(localName)){
                        time = parseTime();
                    }else if(TAG_METADATA_KEYWORDS.equalsIgnoreCase(localName)){
                        keywords = reader.getElementText();
                    }else if(TAG_BOUNDS.equalsIgnoreCase(localName)){
                        env = parseBound();
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_METADATA.equalsIgnoreCase(reader.getLocalName())){
                        //end of the metadata element
                        return new MetaData(
                                name,
                                desc,
                                person,
                                copyright,
                                links,
                                time,
                                keywords,
                                env);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, relation tag without end.");
    }

    private CopyRight parseCopyRight() throws XMLStreamException {
        String author = null;
        String year = null;
        String uri = null;

        for(int i=0,n=reader.getAttributeCount(); i<n;i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_COPYRIGHT_AUTHOR.equalsIgnoreCase(attName)){
                author = reader.getAttributeValue(i);
            }
        }

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_COPYRIGHT_YEAR.equalsIgnoreCase(localName)){
                        year = reader.getElementText();
                    }else if(TAG_COPYRIGHT_LICENSE.equalsIgnoreCase(localName)){
                        uri = reader.getElementText();
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_COPYRIGHT.equalsIgnoreCase(reader.getLocalName())){
                        try {
                            //end of the copyright element
                            return new CopyRight(author,
                                    (year != null) ? Integer.valueOf(year) : null,
                                    (uri != null) ? new URI(uri) : null);
                        } catch (URISyntaxException ex) {
                            throw new XMLStreamException(ex);
                        }
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, copyright tag without end.");
    }

    private Date parseTime() throws XMLStreamException {
        final String str = reader.getElementText();
        
        try {
            return TemporalUtilities.parseDate(str);
        } catch (ParseException ex) {
            Logger.getLogger(GPXReader.class.getName()).log(Level.WARNING, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(GPXReader.class.getName()).log(Level.WARNING, null, ex);
        }
        return null;
    }

    private URI parseLink() throws XMLStreamException {

        String text = null;
        String mime = null;

        for(int i=0,n=reader.getAttributeCount(); i<n;i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_LINK_HREF.equalsIgnoreCase(attName)){
                text = reader.getAttributeValue(i);
            }
        }

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_LINK_TEXT.equalsIgnoreCase(localName)){
                        text = reader.getElementText();
                    }else if(TAG_LINK_TYPE.equalsIgnoreCase(localName)){
                        mime = reader.getElementText();
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_LINK.equalsIgnoreCase(reader.getLocalName())){
                        try {
                            //end of the link element
                            return new URI(text);
                        } catch (URISyntaxException ex) {
                            throw new XMLStreamException(ex);
                        }
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, link tag without end.");
    }

    private Person parsePerson() throws XMLStreamException {
        String name = null;
        String email = null;
        URI uri = null;

        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_NAME.equalsIgnoreCase(localName)){
                        name = reader.getElementText();
                    }else if(TAG_AUTHOR_EMAIL.equalsIgnoreCase(localName)){
                        email = reader.getElementText();
                    }else if(TAG_LINK.equalsIgnoreCase(localName)){
                        uri = parseLink();
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_AUTHOR.equalsIgnoreCase(reader.getLocalName())){
                        //end of the author element
                        return new Person(name,email,uri);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, person tag without end.");
    }

    private Envelope parseBound() throws XMLStreamException {
        String xmin = null;
        String xmax = null;
        String ymin = null;
        String ymax = null;

        for(int i=0,n=reader.getAttributeCount(); i<n;i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_BOUNDS_MINLON.equalsIgnoreCase(attName)){
                xmin = reader.getAttributeValue(i);
            }else if(ATT_BOUNDS_MAXLON.equalsIgnoreCase(attName)){
                xmax = reader.getAttributeValue(i);
            }else if(ATT_BOUNDS_MINLAT.equalsIgnoreCase(attName)){
                ymin = reader.getAttributeValue(i);
            }else if(ATT_BOUNDS_MAXLAT.equalsIgnoreCase(attName)){
                ymax = reader.getAttributeValue(i);
            }
        }

        if(xmin == null || xmax == null || ymin == null || ymax == null){
            throw new XMLStreamException("Error in xml file, metadata bounds not defined correctly");
        }

        toTagEnd(TAG_BOUNDS);

        return GPXModelConstants.createEnvelope(
                Double.parseDouble(xmin),
                Double.parseDouble(xmax),
                Double.parseDouble(ymin),
                Double.parseDouble(ymax));
    }

    private Feature parseWayPoint(int index) throws XMLStreamException{
        //way points might be located in different tag names : wpt, rtept and trkpt
        //we kind the current tag name to know when we reach the end.
        final String tagName = reader.getLocalName();

        Point geometry = null;
        Double ele = null;
        Date time = null;
        Double magvar = null;
        Double geoidheight = null;
        String name = null;
        String cmt = null;
        String desc = null;
        String src = null;
        List<URI> links = null;
        String sym = null;
        String type = null;
        String fix = null;
        Integer sat = null;
        Double hdop = null;
        Double vdop = null;
        Double pdop = null;
        Double ageofdgpsdata = null;
        Integer dgpsid = null;

        String lat = null;
        String lon = null;

        for(int i=0,n=reader.getAttributeCount(); i<n;i++){
            final String attName = reader.getAttributeLocalName(i);
            if(ATT_WPT_LAT.equalsIgnoreCase(attName)){
                lat = reader.getAttributeValue(i);
            }else if(ATT_WPT_LON.equalsIgnoreCase(attName)){
                lon = reader.getAttributeValue(i);
            }
        }

        if(lat == null || lon == null){
            throw new XMLStreamException("Error in xml file, way point lat/lon not defined correctly");
        }else{
            geometry = gf.createPoint(new Coordinate(Double.parseDouble(lon), Double.parseDouble(lat)));
        }

        while (reader.hasNext()) {
            final int eventType = reader.next();

            switch (eventType) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_WPT_ELE.equalsIgnoreCase(localName)){
                        ele = Double.valueOf(reader.getElementText());
                    }else if(TAG_WPT_TIME.equalsIgnoreCase(localName)){
                        time = TemporalUtilities.parseDateSafe(reader.getElementText(),false);
                    }else if(TAG_WPT_MAGVAR.equalsIgnoreCase(localName)){
                        magvar = Double.valueOf(reader.getElementText());
                    }else if(TAG_WPT_GEOIHEIGHT.equalsIgnoreCase(localName)){
                        geoidheight = Double.valueOf(reader.getElementText());
                    }else if(TAG_NAME.equalsIgnoreCase(localName)){
                        name = reader.getElementText();
                    }else if(TAG_CMT.equalsIgnoreCase(localName)){
                        cmt = reader.getElementText();
                    }else if(TAG_DESC.equalsIgnoreCase(localName)){
                        desc = reader.getElementText();
                    }else if(TAG_SRC.equalsIgnoreCase(localName)){
                        src = reader.getElementText();
                    }else if(TAG_LINK.equalsIgnoreCase(localName)){
                        if(links == null) links = new ArrayList<URI>();
                        links.add(parseLink());
                    }else if(TAG_WPT_SYM.equalsIgnoreCase(localName)){
                        sym = reader.getElementText();
                    }else if(TAG_TYPE.equalsIgnoreCase(localName)){
                        type = reader.getElementText();
                    }else if(TAG_WPT_FIX.equalsIgnoreCase(localName)){
                        fix = reader.getElementText();
                    }else if(TAG_WPT_SAT.equalsIgnoreCase(localName)){
                        sat = Integer.valueOf(reader.getElementText());
                    }else if(TAG_WPT_HDOP.equalsIgnoreCase(localName)){
                        hdop = Double.valueOf(reader.getElementText());
                    }else if(TAG_WPT_PDOP.equalsIgnoreCase(localName)){
                        pdop = Double.valueOf(reader.getElementText());
                    }else if(TAG_WPT_VDOP.equalsIgnoreCase(localName)){
                        vdop = Double.valueOf(reader.getElementText());
                    }else if(TAG_WPT_AGEOFGPSDATA.equalsIgnoreCase(localName)){
                        ageofdgpsdata = Double.valueOf(reader.getElementText());
                    }else if(TAG_WPT_DGPSID.equalsIgnoreCase(localName)){
                        dgpsid = Integer.valueOf(reader.getElementText());
                    }
                    break;
                case END_ELEMENT:
                    if(tagName.equalsIgnoreCase(reader.getLocalName())){
                        //end of the way point element
                        return GPXModelConstants.createWayPoint(index, geometry, ele,
                                time, magvar, geoidheight, name, cmt, desc, src, links,
                                sym, type, fix, sat, hdop, vdop, pdop, ageofdgpsdata, dgpsid);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, "+tagName+" tag without end.");
    }

    private Feature parseRoute(int index) throws XMLStreamException{

        int ptInc = 0;
        String name = null;
        String cmt = null;
        String desc = null;
        String src = null;
        List<URI> links = null;
        String type = null;
        Integer number = null;
        List<Feature> wayPoints = null;

        while (reader.hasNext()) {
            final int eventType = reader.next();

            switch (eventType) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_RTE_RTEPT.equalsIgnoreCase(localName)){
                        if(wayPoints == null) wayPoints = new ArrayList<Feature>();
                        wayPoints.add(parseWayPoint(ptInc++));
                    }else if(TAG_NAME.equalsIgnoreCase(localName)){
                        name = reader.getElementText();
                    }else if(TAG_CMT.equalsIgnoreCase(localName)){
                        cmt = reader.getElementText();
                    }else if(TAG_DESC.equalsIgnoreCase(localName)){
                        desc = reader.getElementText();
                    }else if(TAG_SRC.equalsIgnoreCase(localName)){
                        src = reader.getElementText();
                    }else if(TAG_LINK.equalsIgnoreCase(localName)){
                        if(links == null) links = new ArrayList<URI>();
                        links.add(parseLink());
                    }else if(TAG_NUMBER.equalsIgnoreCase(localName)){
                        number = Integer.valueOf(reader.getElementText());
                    }else if(TAG_TYPE.equalsIgnoreCase(localName)){
                        type = reader.getElementText();
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_RTE.equalsIgnoreCase(reader.getLocalName())){
                        //end of the way point element
                        return GPXModelConstants.createRoute(index, name, cmt,
                                desc, src, links, number, type, wayPoints);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, "+TAG_RTE+" tag without end.");
    }

    private ComplexAttribute parseTrackSegment(int index) throws XMLStreamException{

        int ptInc = 0;
        List<Feature> wayPoints = null;

        while (reader.hasNext()) {
            final int eventType = reader.next();

            switch (eventType) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_TRK_SEG_PT.equalsIgnoreCase(localName)){
                        if(wayPoints == null) wayPoints = new ArrayList<Feature>();
                        wayPoints.add(parseWayPoint(ptInc++));
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_TRK_SEG.equalsIgnoreCase(reader.getLocalName())){
                        //end of the track segment element
                        return GPXModelConstants.createTrackSegment(index, wayPoints);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, "+TAG_TRK_SEG+" tag without end.");
    }

    private Feature parseTrack(int index) throws XMLStreamException{

        int segInc = 0;
        String name = null;
        String cmt = null;
        String desc = null;
        String src = null;
        List<URI> links = null;
        String type = null;
        Integer number = null;
        List<ComplexAttribute> segments = null;

        while (reader.hasNext()) {
            final int eventType = reader.next();

            switch (eventType) {
                case START_ELEMENT:
                    final String localName = reader.getLocalName();
                    if(TAG_TRK_SEG.equalsIgnoreCase(localName)){
                        if(segments == null) segments = new ArrayList<ComplexAttribute>();
                        segments.add(parseTrackSegment(segInc++));
                    }else if(TAG_NAME.equalsIgnoreCase(localName)){
                        name = reader.getElementText();
                    }else if(TAG_CMT.equalsIgnoreCase(localName)){
                        cmt = reader.getElementText();
                    }else if(TAG_DESC.equalsIgnoreCase(localName)){
                        desc = reader.getElementText();
                    }else if(TAG_SRC.equalsIgnoreCase(localName)){
                        src = reader.getElementText();
                    }else if(TAG_LINK.equalsIgnoreCase(localName)){
                        if(links == null) links = new ArrayList<URI>();
                        links.add(parseLink());
                    }else if(TAG_NUMBER.equalsIgnoreCase(localName)){
                        number = Integer.valueOf(reader.getElementText());
                    }else if(TAG_TYPE.equalsIgnoreCase(localName)){
                        type = reader.getElementText();
                    }
                    break;
                case END_ELEMENT:
                    if(TAG_TRK.equalsIgnoreCase(reader.getLocalName())){
                        //end of the track element
                        return GPXModelConstants.createTrack(index, name, cmt,
                                desc, src, links, number, type, segments);
                    }
                    break;
            }
        }

        throw new XMLStreamException("Error in xml file, "+TAG_TRK+" tag without end.");
    }

}
