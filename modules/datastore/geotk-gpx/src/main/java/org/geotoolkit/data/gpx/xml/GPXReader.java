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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.gpx.model.Bound;
import org.geotoolkit.data.gpx.model.CopyRight;

import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.model.Person;
import org.geotoolkit.xml.StaxStreamReader;
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

    private MetaData metadata;
    
    private Object current;

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

    public boolean hasNext() throws XMLStreamException{
        read();
        return current != null;
    }

    /**
     *
     * @return GPX WayPoint, Route or track
     * @throws XMLStreamException
     */
    public Object next() throws XMLStreamException{
        read();
        final Object ele = current;
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
                        name = reader.getText();
                    }else if(TAG_DESC.equalsIgnoreCase(localName)){
                        desc = reader.getText();
                    }else if(TAG_PERSON.equalsIgnoreCase(localName)){
                        person = parsePerson();
                    }else if(TAG_COPYRIGHT.equalsIgnoreCase(localName)){
                        copyright = parseCopyRight();
                    }else if(TAG_LINK.equalsIgnoreCase(localName)){
                        links.add(parseLink());
                    }else if(TAG_METADATA_TIME.equalsIgnoreCase(localName)){
                        time = parseTime();
                    }else if(TAG_METADATA_KEYWORDS.equalsIgnoreCase(localName)){
                        keywords = reader.getText();
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
        //todo
        toTagEnd(TAG_COPYRIGHT);
        return null;
    }

    private Date parseTime() throws XMLStreamException {
        //todo
        return null;
    }

    private URI parseLink() throws XMLStreamException {
        //todo
        toTagEnd(TAG_PERSON);
        return null;
    }

    private Person parsePerson() throws XMLStreamException {
        String name = null;
        String email = null;
        URI uri = null;

        for(int i=0; i<reader.getAttributeCount();i++){
            final String attName = reader.getAttributeLocalName(i);
            if(TAG_NAME.equalsIgnoreCase(attName)){
                name = reader.getText();
            }else if(TAG_PERSON_EMAIL.equalsIgnoreCase(attName)){
                email = reader.getText();
            }else if(TAG_LINK.equalsIgnoreCase(attName)){
                uri = parseLink();
            }
        }

        toTagEnd(TAG_PERSON);

        return new Person(name, email, uri);
    }

    private Envelope parseBound() throws XMLStreamException {
        String xmin = null;
        String xmax = null;
        String ymin = null;
        String ymax = null;

        for(int i=0; i<reader.getAttributeCount();i++){
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

        return Bound.create(
                Double.parseDouble(xmin),
                Double.parseDouble(xmax),
                Double.parseDouble(ymin),
                Double.parseDouble(ymax));
    }

}
