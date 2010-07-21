/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.data.gx.model;

import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import static org.geotoolkit.data.kml.model.KmlModelConstants.TYPE_KML_ENTITY;

/**
 *
 * @author Samuel Andrés
 */
public class GxModelConstants {

    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public static final String GX_NAMESPACE = "http://www.google.com/kml/ext/2.2";

    public static final FeatureType TYPE_TOUR;
    public static final AttributeDescriptor ATT_PLAY_LIST;

    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final FeatureTypeFactory ftf = ftb.getFeatureTypeFactory();

        //-------------------- TOUR ------------------------------
        ATT_PLAY_LIST = adb.create(
                new DefaultName(GX_NAMESPACE, "playList"), PlayList.class,0,1,false,null);

        ftb.reset();
        ftb.setName(GX_NAMESPACE, GxConstants.TAG_TOUR);
        ftb.setSuperType(TYPE_KML_ENTITY);
        TYPE_TOUR = ftb.buildFeatureType();

    }

    private GxModelConstants(){}

}
