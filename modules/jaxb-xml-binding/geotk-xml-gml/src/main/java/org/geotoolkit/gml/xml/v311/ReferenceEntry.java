/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml.v311;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;

/**
 * Une reference decrivant un resultat pour une ressource MIME externe.
 *
 * @version $Id:
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Reference")
public class ReferenceEntry implements Reference{
    
    /**
     * L'identifiant de la reference.
     */
    @XmlTransient
    private String id;
    
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute
    private java.lang.Boolean owns;
    
    
    /**
     * COnstructeur utilisé par jaxB
     */
    public ReferenceEntry(){}
    
    /**
     * Créé une nouvelle reference. reduit pour l'instant a voir en fontion des besoins.
     */
    public ReferenceEntry(String id, String href) {
        this.id   = id;
        this.href = href;
    }

    /**
     * retourne l'identifiant de la reference.
     */
    public String getId() {
        return id;
    }

    /**
     * retourne l'identifiant de la reference.
     */
    public String getName() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    
    public List<String> getNilReason() {
        return nilReason;
    }

    /**
     * {@inheritDoc}
     */
    
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * {@inheritDoc}
     */
    
    public String getActuate() {
        return actuate;
    }

    /**
     * {@inheritDoc}
     */
    
    public String getArcrole() {
        return arcrole;
    }

    /**
     * {@inheritDoc}
     */
    
    public String getHref() {
        return href;
    }

    /**
     * {@inheritDoc}
     */
    
    public String getRole() {
        return role;
    }

    /**
     * {@inheritDoc}
     */
    
    public String getShow() {
        return show;
    }

    /**
     * {@inheritDoc}
     */
    
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    
    public java.lang.Boolean getOwns() {
        return owns;
    }
    
     /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ReferenceEntry) {
            final ReferenceEntry that = (ReferenceEntry) object;

            return Utilities.equals(this.actuate,            that.actuate)          &&
                   Utilities.equals(this.arcrole,            that.arcrole)          &&
                   Utilities.equals(this.type,               that.type)             &&
                   Utilities.equals(this.href,               that.href)             &&
                   Utilities.equals(this.nilReason,          that.nilReason)        &&
                   Utilities.equals(this.remoteSchema,       that.remoteSchema)     &&
                   Utilities.equals(this.show,               that.show)             &&
                   Utilities.equals(this.role,               that.role)             &&
                   Utilities.equals(this.title,              that.title)            &&
                 //  Utilities.equals(this.id,                 that.id)               && because its transient
                   Utilities.equals(this.owns,               that.owns);
        }
        return false;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 47 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 47 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 47 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 47 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 47 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 47 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 47 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 47 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 47 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 47 * hash + (this.owns != null ? this.owns.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("id=");
        s.append(id).append('\n');
        
        if(actuate != null) {
            s.append("actuate=").append(actuate).append('\n');
        }
        if(arcrole != null) {
            s.append("arcrole=").append(arcrole).append('\n');
        }
        if(href != null) {
            s.append("href=").append(href).append('\n');
        }
        if(role != null) {
            s.append("role=").append(role).append('\n');
        }
        if(show != null) {
            s.append("show=").append(show).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        if(owns != null) {
            s.append("owns=").append(owns).append('\n');
        }
        if(type != null) {
            s.append("type=").append(type).append('\n');
        }
        return s.toString();
    }
    
}
