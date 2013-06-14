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

package org.geotoolkit.data.gpx.model;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.apache.sis.io.TableAppender;

import org.opengis.geometry.Envelope;

/**
 * Metadatas of GPX files.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MetaData {

    private final String name;
    private final String description;
    private final Person person;
    private final CopyRight copyRight;
    private final List<URI> links;
    private final Date time;
    private final String keywords;
    private final Envelope bounds;

    public MetaData(final String name, final String description, final Person person, final CopyRight copyRight,
            final List<URI> links, final Date time, final String keywords, final Envelope bounds) {
        this.name = name;
        this.description = description;
        this.person = person;
        this.copyRight = copyRight;
        this.time = time;
        this.keywords = keywords;
        this.bounds = bounds;

        if(links != null && !links.isEmpty()){
            this.links = links;
        }else{
            this.links = Collections.emptyList();
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Person getPerson() {
        return person;
    }

    public CopyRight getCopyRight() {
        return copyRight;
    }

    public List<URI> getLinks() {
        return links;
    }

    public Date getTime() {
        return time;
    }

    public String getKeywords() {
        return keywords;
    }

    public Envelope getBounds() {
        return bounds;
    }

    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        final TableAppender tablewriter = new TableAppender(writer);

        tablewriter.writeHorizontalSeparator();
        tablewriter.append("GPX-Metadata\t \n");
        tablewriter.writeHorizontalSeparator();

        tablewriter.append("Name\t"+getName()+"\n");
        tablewriter.append("Desc\t"+getDescription()+"\n");
        tablewriter.append("Time\t"+getTime()+"\n");
        tablewriter.append("Keywords\t"+getKeywords()+"\n");
        tablewriter.append("Bounds\t"+getBounds()+"\n");

        final Person person = getPerson();
        if(person != null){
            tablewriter.append("Person - Name\t"+person.getName()+"\n");
            tablewriter.append("Person - EMail\t"+person.getEmail()+"\n");
            tablewriter.append("Person - Link\t"+person.getLink()+"\n");
        }else{
            tablewriter.append("Person\t"+person+"\n");
        }

        final CopyRight copyright = getCopyRight();
        if(copyright != null){
            tablewriter.append("CopyRight - Author\t"+copyright.getAuthor()+"\n");
            tablewriter.append("CopyRight - Year\t"+copyright.getYear()+"\n");
            tablewriter.append("CopyRight - License\t"+copyright.getLicense()+"\n");
        }else{
            tablewriter.append("CopyRight\t"+copyright+"\n");
        }

        tablewriter.append("Links\t");
        final List<URI> links = getLinks();
        if(links.isEmpty()){
            tablewriter.append("None\n");
        }else{
            tablewriter.append("\n");
            for(final URI uri : getLinks()){
                tablewriter.append("\t"+uri+"\n");
            }
        }
        
        tablewriter.writeHorizontalSeparator();

        try {
            tablewriter.flush();
            writer.flush();
        } catch (IOException ex) {
            //will never happen is this case
            ex.printStackTrace();
        }

        return writer.getBuffer().toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 59 * hash + (this.person != null ? this.person.hashCode() : 0);
        hash = 59 * hash + (this.copyRight != null ? this.copyRight.hashCode() : 0);
        hash = 59 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 59 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        hash = 59 * hash + (this.bounds != null ? this.bounds.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetaData other = (MetaData) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.person != other.person && (this.person == null || !this.person.equals(other.person))) {
            return false;
        }
        if (this.copyRight != other.copyRight && (this.copyRight == null || !this.copyRight.equals(other.copyRight))) {
            return false;
        }

        if(!Objects.equals(this.links, other.links)){
            return false;
        }
        
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        if ((this.keywords == null) ? (other.keywords != null) : !this.keywords.equals(other.keywords)) {
            return false;
        }
        if (this.bounds != other.bounds && (this.bounds == null || !this.bounds.equals(other.bounds))) {
            return false;
        }
        return true;
    }

}
