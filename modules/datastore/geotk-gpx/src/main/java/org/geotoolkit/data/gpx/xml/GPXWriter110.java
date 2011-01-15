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

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gpx.model.CopyRight;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.model.Person;

import static org.geotoolkit.data.gpx.xml.GPXConstants.*;
import static org.geotoolkit.data.gpx.model.GPXModelConstants.*;


/**
 * Stax writer class for GPX 1.1 files.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GPXWriter110 extends GPXWriter100{

    public GPXWriter110(final String creator){
        super(GPX_NAMESPACE_V11,creator);
    }

    @Override
    protected String getVersion() {
        return "1.1";
    }

    @Override
    public void write(final MetaData metadata) throws XMLStreamException{
        writer.writeStartElement(namespace, TAG_METADATA);
        writeSimpleTag(namespace, TAG_NAME, metadata.getName());
        writeSimpleTag(namespace, TAG_DESC, metadata.getDescription());
        writePerson(metadata.getPerson());
        writeCopyRight(metadata.getCopyRight());
        for(URI uri : metadata.getLinks()){
            writeLink(uri);
        }

        final Date d = metadata.getTime();
        if(d != null){
            writeSimpleTag(namespace, TAG_METADATA_TIME, sdf.format(d));
        }

        writeSimpleTag(namespace, TAG_METADATA_KEYWORDS, metadata.getKeywords());
        writeBounds(metadata.getBounds());

        writer.writeEndElement();
        writer.flush();
    }

    public void writePerson(final Person person) throws XMLStreamException{
        if(person == null) return;
        
        writer.writeStartElement(namespace, TAG_AUTHOR);
        writeSimpleTag(namespace, TAG_NAME, person.getName());
        writeSimpleTag(namespace, TAG_AUTHOR_EMAIL, person.getEmail());
        writeLink(person.getLink());
        writer.writeEndElement();
    }

    @Override
    public void writeLinkURIs(final Collection<URI> links) throws XMLStreamException{
        if(links != null && !links.isEmpty()){
            for(URI uri : links){
                writeLink(uri);
            }
        }
    }

    @Override
    public void writeLink(final URI uri) throws XMLStreamException{
        if(uri == null) return;

        writer.writeStartElement(namespace, TAG_LINK);
        writer.writeAttribute(ATT_LINK_HREF, uri.toASCIIString());
        writer.writeEndElement();
    }

    public void writeCopyRight(final CopyRight copyRight) throws XMLStreamException{
        if(copyRight == null) return;

        writer.writeStartElement(namespace, TAG_COPYRIGHT);
        final String author = copyRight.getAuthor();
        if(author != null){
            writer.writeAttribute(ATT_COPYRIGHT_AUTHOR, author);
        }
        writeSimpleTag(namespace, TAG_COPYRIGHT_YEAR, copyRight.getYear());
        writeSimpleTag(namespace, TAG_COPYRIGHT_LICENSE, copyRight.getLicense());
        writer.writeEndElement();
    }

}
