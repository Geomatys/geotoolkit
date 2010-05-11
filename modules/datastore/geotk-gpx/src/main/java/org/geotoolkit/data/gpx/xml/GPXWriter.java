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

import com.vividsolutions.jts.geom.Point;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.gpx.model.CopyRight;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.model.Person;
import org.geotoolkit.xml.StaxStreamWriter;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.Envelope;

import static org.geotoolkit.data.gpx.xml.GPXConstants.*;
import static org.geotoolkit.data.gpx.model.GPXModelConstants.*;


/**
 * Stax writer class for GPX files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GPXWriter extends StaxStreamWriter{

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public void writeStartDocument() throws XMLStreamException{
        writer.writeStartDocument("UTF-8", "1.0");
    }

    public void writeEndDocument() throws XMLStreamException{
        writer.writeEndDocument();
    }

    public void writeGPXTag() throws XMLStreamException{
        writer.setDefaultNamespace(GPX_NAMESPACE);
        writer.writeStartElement(GPX_NAMESPACE, TAG_GPX);
        writer.writeDefaultNamespace(GPX_NAMESPACE);
    }

    public void write(MetaData metadata, Collection<? extends Feature> wayPoints,
            Collection<? extends Feature> routes, Collection<? extends Feature> tracks) throws XMLStreamException{

        writeGPXTag();

        if(metadata != null){
            write(metadata);
        }

        if(wayPoints != null){
            final Iterator<? extends Feature> ite = wayPoints.iterator();
            try{
                while(ite.hasNext()){
                    writeWayPoint(ite.next(), TAG_WPT);
                }
            }catch(DataStoreRuntimeException ex){
                throw new XMLStreamException(ex);
            }finally{
                if(ite instanceof Closeable){
                    try { ((Closeable) ite).close();
                    } catch (IOException ex) {
                        throw new XMLStreamException(ex);
                    }
                }
            }
        }

        if(routes != null){
            final Iterator<? extends Feature> ite = routes.iterator();
            try{
                while(ite.hasNext()){
                    writeRoute(ite.next());
                }
            }catch(DataStoreRuntimeException ex){
                throw new XMLStreamException(ex);
            }finally{
                if(ite instanceof Closeable){
                    try { ((Closeable) ite).close();
                    } catch (IOException ex) {
                        throw new XMLStreamException(ex);
                    }
                }
            }
        }

        if(tracks != null){
            final Iterator<? extends Feature> ite = tracks.iterator();
            try{
                while(ite.hasNext()){
                    writeTrack(ite.next());
                }
            }catch(DataStoreRuntimeException ex){
                throw new XMLStreamException(ex);
            }finally{
                if(ite instanceof Closeable){
                    try { ((Closeable) ite).close();
                    } catch (IOException ex) {
                        throw new XMLStreamException(ex);
                    }
                }
            }
        }

        writer.writeEndElement();
    }

    public void write(MetaData metadata) throws XMLStreamException{
        writer.writeStartElement(GPX_NAMESPACE, TAG_METADATA);
        writeSimpleTag(GPX_NAMESPACE, TAG_NAME, metadata.getName());
        writeSimpleTag(GPX_NAMESPACE, TAG_DESC, metadata.getDescription());
        writePerson(metadata.getPerson());
        writeCopyRight(metadata.getCopyRight());
        for(URI uri : metadata.getLinks()){
            writeLink(uri);
        }

        final Date d = metadata.getTime();
        if(d != null){
            writeSimpleTag(GPX_NAMESPACE, TAG_METADATA_TIME, sdf.format(d));
        }

        writeSimpleTag(GPX_NAMESPACE, TAG_METADATA_KEYWORDS, metadata.getKeywords());
        writeBounds(metadata.getBounds());

        writer.writeEndElement();
    }

    public void writeWayPoint(Feature feature, final String tagName) throws XMLStreamException{
        if(feature == null) return;

        writer.writeStartElement(GPX_NAMESPACE, tagName);

        final Point pt = (Point) feature.getProperty(GPX_GEOMETRY).getValue();
        writer.writeAttribute(GPX_NAMESPACE, ATT_WPT_LAT, Double.toString(pt.getY()));
        writer.writeAttribute(GPX_NAMESPACE, ATT_WPT_LON, Double.toString(pt.getX()));

        writeProperty(TAG_WPT_ELE,          feature.getProperty(TAG_WPT_ELE));
        writeProperty(TAG_WPT_TIME,         feature.getProperty(TAG_WPT_TIME));
        writeProperty(TAG_WPT_MAGVAR,      feature.getProperty(TAG_WPT_MAGVAR));
        writeProperty(TAG_WPT_GEOIHEIGHT,   feature.getProperty(TAG_WPT_GEOIHEIGHT));
        writeProperty(TAG_NAME,             feature.getProperty(TAG_NAME));
        writeProperty(TAG_CMT,              feature.getProperty(TAG_CMT));
        writeProperty(TAG_DESC,             feature.getProperty(TAG_DESC));
        writeProperty(TAG_SRC,              feature.getProperty(TAG_SRC));
        writeLinks(feature.getProperties(TAG_LINK));
        writeProperty(TAG_WPT_SYM,          feature.getProperty(TAG_WPT_SYM));
        writeProperty(TAG_TYPE,             feature.getProperty(TAG_TYPE));
        writeProperty(TAG_WPT_FIX,          feature.getProperty(TAG_WPT_FIX));
        writeProperty(TAG_WPT_SAT,          feature.getProperty(TAG_WPT_SAT));
        writeProperty(TAG_WPT_HDOP,         feature.getProperty(TAG_WPT_HDOP));
        writeProperty(TAG_WPT_VDOP,         feature.getProperty(TAG_WPT_VDOP));
        writeProperty(TAG_WPT_PDOP,         feature.getProperty(TAG_WPT_PDOP));
        writeProperty(TAG_WPT_AGEOFGPSDATA, feature.getProperty(TAG_WPT_AGEOFGPSDATA));
        writeProperty(TAG_WPT_DGPSID,       feature.getProperty(TAG_WPT_DGPSID));

        writer.writeEndElement();
    }

    public void writeRoute(Feature feature) throws XMLStreamException{
        if(feature == null) return;

        writer.writeStartElement(GPX_NAMESPACE, TAG_RTE);

        writeProperty(TAG_NAME,             feature.getProperty(TAG_NAME));
        writeProperty(TAG_CMT,              feature.getProperty(TAG_CMT));
        writeProperty(TAG_DESC,             feature.getProperty(TAG_DESC));
        writeProperty(TAG_SRC,              feature.getProperty(TAG_SRC));
        writeLinks(feature.getProperties(TAG_LINK));
        writeProperty(TAG_NUMBER,           feature.getProperty(TAG_NUMBER));
        writeProperty(TAG_TYPE,             feature.getProperty(TAG_TYPE));

        for(Property prop : feature.getProperties(TAG_RTE_RTEPT)){
            writeWayPoint((Feature) prop.getValue(),TAG_RTE_RTEPT);
        }

        writer.writeEndElement();
    }

    public void writeTrack(Feature feature) throws XMLStreamException{
        if(feature == null) return;

        writer.writeStartElement(GPX_NAMESPACE, TAG_TRK);

        writeProperty(TAG_NAME,             feature.getProperty(TAG_NAME));
        writeProperty(TAG_CMT,              feature.getProperty(TAG_CMT));
        writeProperty(TAG_DESC,             feature.getProperty(TAG_DESC));
        writeProperty(TAG_SRC,              feature.getProperty(TAG_SRC));
        writeLinks(feature.getProperties(TAG_LINK));
        writeProperty(TAG_NUMBER,           feature.getProperty(TAG_NUMBER));
        writeProperty(TAG_TYPE,             feature.getProperty(TAG_TYPE));

        for(Property prop : feature.getProperties(TAG_TRK_SEG)){
            writeTrackSegment((ComplexAttribute) prop.getValue());
        }

        writer.writeEndElement();
    }

    public void writeTrackSegment(ComplexAttribute feature) throws XMLStreamException{
        if(feature == null) return;
        writer.writeStartElement(GPX_NAMESPACE, TAG_TRK_SEG);

        for(final Property prop : feature.getProperties(TAG_TRK_SEG_PT)){
            writeWayPoint((Feature) prop.getValue(),TAG_TRK_SEG_PT);
        }

        writer.writeEndElement();
    }

    public void writePerson(Person person) throws XMLStreamException{
        if(person == null) return;
        
        writer.writeStartElement(GPX_NAMESPACE, TAG_AUTHOR);
        writeSimpleTag(GPX_NAMESPACE, TAG_NAME, person.getName());
        writeSimpleTag(GPX_NAMESPACE, TAG_AUTHOR_EMAIL, person.getEmail());
        writeLink(person.getLink());
        writer.writeEndElement();
    }

    public void writeLinks(Collection<Property> props) throws XMLStreamException{
        if(props == null || props.isEmpty()) return;
        for(final Property prop : props){
            if(prop != null){
                final URI uri = (URI) prop.getValue();
                writeLink(uri);
            }
        }
    }

    public void writeLink(URI uri) throws XMLStreamException{
        if(uri == null) return;

        writer.writeStartElement(GPX_NAMESPACE, TAG_LINK);
        writer.writeAttribute(GPX_NAMESPACE, ATT_LINK_HREF, uri.toASCIIString());
        writer.writeEndElement();
    }

    public void writeCopyRight(CopyRight copyRight) throws XMLStreamException{
        if(copyRight == null) return;

        writer.writeStartElement(GPX_NAMESPACE, TAG_COPYRIGHT);
        final String author = copyRight.getAuthor();
        if(author != null){
            writer.writeAttribute(GPX_NAMESPACE, ATT_COPYRIGHT_AUTHOR, author);
        }
        writeSimpleTag(GPX_NAMESPACE, TAG_COPYRIGHT_YEAR, copyRight.getYear());
        writeSimpleTag(GPX_NAMESPACE, TAG_COPYRIGHT_LICENSE, copyRight.getLicense());
        writer.writeEndElement();
    }

    public void writeBounds(Envelope env) throws XMLStreamException{
        if(env == null) return;

        writer.writeStartElement(GPX_NAMESPACE, TAG_BOUNDS);

        writer.writeAttribute(GPX_NAMESPACE, ATT_BOUNDS_MINLAT, Double.toString(env.getMinimum(1)));
        writer.writeAttribute(GPX_NAMESPACE, ATT_BOUNDS_MINLON, Double.toString(env.getMinimum(0)));
        writer.writeAttribute(GPX_NAMESPACE, ATT_BOUNDS_MAXLAT, Double.toString(env.getMaximum(1)));
        writer.writeAttribute(GPX_NAMESPACE, ATT_BOUNDS_MAXLON, Double.toString(env.getMaximum(0)));

        writer.writeEndElement();
    }

    public void writeProperty(String tagName,Property prop) throws XMLStreamException{
        if(prop == null) return;

        Object val = prop.getValue();
        if(val instanceof Date){
            val = sdf.format(val);
        }

        writeSimpleTag(GPX_NAMESPACE, tagName, val);
    }

}
