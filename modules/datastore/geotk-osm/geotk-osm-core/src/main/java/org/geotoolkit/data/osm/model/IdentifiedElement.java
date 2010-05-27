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

package org.geotoolkit.data.osm.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;

import org.opengis.feature.type.AttributeDescriptor;

/**
 * Commun abstract class for OSM main model objects : Node, Way and Relation.
 * Thoses share several properties like identifiers, versions, timestamp, user or tags.
 * This classe extends Feature, which makes it usable anywhere is geotoolkit.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class IdentifiedElement extends AbstractFeature<Collection<Property>> implements Serializable {

    static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    protected final long id;
    protected final int version;
    protected final int changeset;
    protected final User user;
    protected final long timestamp;
    protected final List<Tag> tags;

    public IdentifiedElement(AttributeDescriptor desc, long id, int version, int changeset, User user,
            long timestamp, Map<String,String> tags) {
        super(desc,new DefaultFeatureId(String.valueOf(id)));
        this.id = id;
        this.version = version;
        this.changeset = changeset;
        this.user = user;
        this.timestamp = timestamp;

        if(tags == null || tags.isEmpty()){
            this.tags = Collections.EMPTY_LIST;
        }else{
            final Tag[] array = new Tag[tags.size()];
            int i=0;
            for(Map.Entry<String,String> entry : tags.entrySet()){
                array[i] = new Tag(entry.getKey(), entry.getValue());
                i++;
            }
            this.tags = UnmodifiableArrayList.wrap(array);
        }
    }

    /**
     * Identifier of the current osm element.
     * @return element id
     */
    public long getId() {
        return id;
    }

    /**
     * Current version of the osm element.
     * @return version
     */
    public int getVersion() {
        return version;
    }

    /**
     * The changeset identifier that contain the version of the element.
     * @return changeset id
     */
    public int getChangeset() {
        return changeset;
    }

    /**
     * Most
     * @return User
     */
    public User getUser() {
        return user;
    }

    /**
     * Time of the element creation or update if version is superior to 1.
     * @return long in millisecond since 1 january 1970
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Get all tags related to this osm element.
     * @return List of tags, never null but can be empty.
     */
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IdentifiedElement other = (IdentifiedElement) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        if (this.changeset != other.changeset) {
            return false;
        }
        if (this.user != other.user && (this.user == null || !this.user.equals(other.user))) {
            return false;
        }
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if (this.tags != other.tags && (this.tags == null || !this.tags.equals(other.tags))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 19 * hash + this.version;
        hash = 19 * hash + this.changeset;
        hash = 19 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 19 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
        hash = 19 * hash + (this.tags != null ? this.tags.hashCode() : 0);
        return hash;
    }

}
