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

import com.vividsolutions.jts.geom.Point;
import java.util.Collections;
import org.apache.sis.feature.SingleAttributeTypeBuilder;
import org.apache.sis.feature.DefaultAssociationRole;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;

import org.geotoolkit.util.NamesExt;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.data.osm.xml.OSMXMLConstants;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Global OSM constants, defines the namespace and Feature types.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class OSMModelConstants {

    public static final CoordinateReferenceSystem OSM_CRS = CommonCRS.WGS84.normalizedGeographic();


    public static final String OSM_NAMESPACE = "http://openstreetmap.org";

    public static final AttributeType ATT_ID;
    public static final AttributeType ATT_VERSION;
    public static final AttributeType ATT_CHANGESET;
    public static final FeatureAssociationRole ATT_USER;
    public static final AttributeType ATT_TIMESTAMP;
    public static final FeatureAssociationRole ATT_TAG;
    public static final FeatureAssociationRole ATT_RELATION_MEMBER;
    public static final AttributeType ATT_NODE_POINT;
    public static final AttributeType ATT_WAY_NODES;
    public static final AttributeType ATT_K;
    public static final AttributeType ATT_V;
    public static final AttributeType ATT_USER_ID;
    public static final AttributeType ATT_USER_NAME;
    public static final AttributeType ATT_MEMBER_ROLE;
    public static final AttributeType ATT_MEMBER_TYPE;
    public static final AttributeType ATT_MEMBER_REF;

    public static final FeatureType TYPE_USER;
    public static final FeatureType TYPE_TAG;
    public static final FeatureType TYPE_RELATION_MEMBER;
    public static final FeatureType TYPE_IDENTIFIED;
    public static final FeatureType TYPE_NODE;
    public static final FeatureType TYPE_WAY;
    public static final FeatureType TYPE_RELATION;

    /**
     * No user id.
     */
    public static final int USER_ID_NONE = -1;
    public static final Feature USER_NONE;

    static {
        FeatureTypeBuilder ftb;
        ATT_ID = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, "id"), Long.class);
        ATT_VERSION = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, OSMXMLConstants.ATT_VERSION), Integer.class);
        ATT_CHANGESET = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, OSMXMLConstants.ATT_CHANGESET), Integer.class);
        ATT_TIMESTAMP = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, OSMXMLConstants.ATT_TIMESTAMP), Long.class);

        //------------------- USER TYPE ----------------------------------------
        ATT_USER_ID = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, OSMXMLConstants.ATT_UID), Integer.class);
        ATT_USER_NAME = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, OSMXMLConstants.ATT_USER), String.class);

        ftb = new FeatureTypeBuilder();
        ftb.setName(OSM_NAMESPACE, "User");
        ftb.addAttribute(ATT_USER_ID);
        ftb.addAttribute(ATT_USER_NAME);
        TYPE_USER = ftb.build();

        //------------------- TAG TYPE -----------------------------------------
        ATT_K = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, "k"), String.class);
        ATT_V = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, "v"), String.class);

        ftb = new FeatureTypeBuilder();
        ftb.setName(OSM_NAMESPACE, OSMXMLConstants.TAG_TAG);
        ftb.addAttribute(ATT_K);
        ftb.addAttribute(ATT_V);
        TYPE_TAG = ftb.build();
        ATT_TAG = new DefaultAssociationRole(Collections.singletonMap("name", NamesExt.create(OSM_NAMESPACE, "tags")), TYPE_TAG, 0, Integer.MAX_VALUE);

        //------------------- IDENTIFIED TYPE ----------------------------------
        ATT_USER = new DefaultAssociationRole(Collections.singletonMap("name", NamesExt.create(OSM_NAMESPACE, "user")), TYPE_USER, 1, 1);
        ftb = new FeatureTypeBuilder();
        ftb.setName(OSM_NAMESPACE, "Identified");
        ftb.setAbstract(true);
        ftb.addAttribute(ATT_ID).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(ATT_VERSION);
        ftb.addAttribute(ATT_CHANGESET);
        ftb.addAssociation(ATT_USER);
        ftb.addAttribute(ATT_TIMESTAMP);
        ftb.addAssociation(ATT_TAG);
        TYPE_IDENTIFIED = ftb.build();

        //------------------- NODE TYPE ----------------------------------------
        ATT_NODE_POINT = new SingleAttributeTypeBuilder().setName(OSM_NAMESPACE,"point").setValueClass(Point.class).setCRS(OSM_CRS).build();
        ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TYPE_IDENTIFIED);
        ftb.setName(OSM_NAMESPACE, "Node");
        ftb.addAttribute(ATT_NODE_POINT).addRole(AttributeRole.DEFAULT_GEOMETRY);
        TYPE_NODE = ftb.build();


        //------------------- WAY TYPE -----------------------------------------
        ATT_WAY_NODES = new SingleAttributeTypeBuilder().setName(OSM_NAMESPACE, OSMXMLConstants.TAG_WAYND)
                .setValueClass(Long.class).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE).build();

        ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TYPE_IDENTIFIED);
        ftb.setName(OSM_NAMESPACE, OSMXMLConstants.TAG_WAY);
        ftb.addAttribute(ATT_WAY_NODES);
        TYPE_WAY = ftb.build();

        //------------------- RELATION MEMBER TYPE -----------------------------
        ATT_MEMBER_ROLE = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, OSMXMLConstants.ATT_RELMB_ROLE),String.class);
        ATT_MEMBER_TYPE = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, OSMXMLConstants.ATT_RELMB_TYPE),MemberType.class);
        ATT_MEMBER_REF = SingleAttributeTypeBuilder.create(NamesExt.create(OSM_NAMESPACE, OSMXMLConstants.ATT_RELMB_REF),Long.class);

        ftb = new FeatureTypeBuilder();
        ftb.setName(OSM_NAMESPACE, "Member");
        ftb.addAttribute(ATT_MEMBER_ROLE);
        ftb.addAttribute(ATT_MEMBER_TYPE);
        ftb.addAttribute(ATT_MEMBER_REF);
        TYPE_RELATION_MEMBER = ftb.build();

        //------------------- RELATION TYPE ------------------------------------
        ATT_RELATION_MEMBER = new DefaultAssociationRole(
                Collections.singletonMap("name", NamesExt.create(OSM_NAMESPACE, "members")), TYPE_RELATION_MEMBER, 0, Integer.MAX_VALUE);

        ftb = new FeatureTypeBuilder();
        ftb.setSuperTypes(TYPE_IDENTIFIED);
        ftb.setName(OSM_NAMESPACE, "Relation");
        ftb.addAssociation(ATT_RELATION_MEMBER);
        TYPE_RELATION = ftb.build();

        USER_NONE = TYPE_USER.newInstance();
    }

    private OSMModelConstants(){}

}
