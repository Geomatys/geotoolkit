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
package org.geotoolkit.data.kml.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.opengis.util.CodeList;

/**
 *
 * @author Samuel Andrés
 */
public final class Extensions {

    private final Map<Names, Entry<List<SimpleType>, List<Object>>> map =
            new HashMap<Names, Entry<List<SimpleType>, List<Object>>>() {

                @Override
                public Entry<List<SimpleType>, List<Object>> get(Object key) {
                    Entry<List<SimpleType>, List<Object>> entry = super.get(key);

                    if (entry == null) {
                        entry = new SimpleImmutableEntry<List<SimpleType>, List<Object>>(
                                new ArrayList<SimpleType>(),
                                new ArrayList<Object>());
                        map.put((Names) key, entry);
                    }
                    return entry;
                }
            };

    public Extensions() {
    }

    public List<SimpleType> simples(Names name) {
        return map.get(name).getKey();
    }

    public List<Object> complexes(Names name) {
        return map.get(name).getValue();
    }

    public static class Names extends CodeList<Names> {

        private static final List<Names> VALUES = new ArrayList<Names>();

        public static final Names OBJECT = new Names("OBJECT");
            public static final Names GEOMETRY = new Names("GEOMETRY");
                public static final Names LINE_STRING = new Names("LINE_STRING");
                public static final Names LINAR_RING = new Names("LINEAR_RING");
                public static final Names POINT = new Names("POINT");
                public static final Names MODEL = new Names("MODEL");
                public static final Names POLYGON = new Names("POLYGON");
                public static final Names MULTI_GEOMETRY = new Names("MULTI_GEOMETRY");
            public static final Names FEATURE = new Names("FEATURE");
                public static final Names PLACEMARK = new Names("PLACEMARK");
                public static final Names NETWORK_LINK = new Names("NETWORK_LINK");
                public static final Names CONTAINER = new Names("CONTAINER");
                    public static final Names FOLDER = new Names("FOLDER");
                    public static final Names DOCUMENT = new Names("DOCUMENT");
                public static final Names OVERLAY = new Names("OVERLAY");
                    public static final Names GROUND_OVERLAY = new Names("GROUND_OVERLAY");
                    public static final Names SCREEN_OVERLAY = new Names("SCREEN_OVERLAY");
                    public static final Names PHOTO_OVERLAY = new Names("PHOTO_OVERLAY");
            public static final Names REGION = new Names("REGION");
            public static final Names LOD = new Names("LOD");
            public static final Names ORIENTATION = new Names("ORIENTATION");
            public static final Names SCHEMA_DATA = new Names("SCHEMA_DATA");
            public static final Names LINK = new Names("LINK");
            public static final Names VIEW_VOLUME = new Names("VIEW_VOLUME");
            public static final Names PAIR = new Names("PAIR");
            public static final Names STYLE_SELECTOR = new Names("STYLE_SELECTOR");
                public static final Names STYLE = new Names("STYLE");
                public static final Names STYLE_MAP = new Names("STYLE_MAP");
            public static final Names SUB_STYLE = new Names("SUB_STYLE");
                public static final Names BALLOON_STYLE = new Names("BALLOON_STYLE");
                public static final Names LIST_STYLE = new Names("LIST_STYLE");
                public static final Names COLOR_STYLE = new Names("COLOR_STYLE");
                    public static final Names ICON_STYLE = new Names("ICON_STYLE");
                    public static final Names LABEL_STYLE = new Names("LABEL_STYLE");
                    public static final Names POLY_STYLE = new Names("POLY_STYLE");
                    public static final Names LINE_STYLE = new Names("LINE_STYLE");
            public static final Names ABSTRACT_LAT_LON_BOX = new Names("ABSTRACT_LAT_LON_BOX");
                public static final Names LAT_LON_BOX = new Names("LAT_LON_BOX");
                public static final Names LAT_LON_ALT_BOX = new Names("LAT_LON_ALT_BOX");
            public static final Names VIEW = new Names("VIEW");
                public static final Names CAMERA = new Names("CAMERA");
                public static final Names LOOK_AT = new Names("LOOK_AT");
            public static final Names TIME_PRIMITIVE = new Names("TIME_PRIMITIVE");
                public static final Names TIME_STAMP = new Names("TIME_STAMP");
                public static final Names TIME_SPAN = new Names("TIME_SPAN");
        public static final Names BOUNDARY = new Names("BOUNDARY");
        public static final Names ALIAS = new Names("ALIAS");
        public static final Names IMAGE_PYRAMID = new Names("IMAGE_PYRAMID");
        public static final Names ITEM_ICON = new Names("ITEM_ICON");
        public static final Names LOCATION = new Names("LOCATION");
        public static final Names RESOURCE_MAP = new Names("RESOURCE_MAP");
        public static final Names SCALE = new Names("SCALE");
        public static final Names KML = new Names("KML");
        public static final Names BASIC_LINK = new Names("BASIC_LINK");
        public static final Names NETWORK_LINK_CONTROL = new Names("NETWORK_LINK_CONTROL");
        public static final Names LINEAR_RING = new Names("LINEAR_RING");

        private Names(final String name) {
            super(name, VALUES);
        }

        public static Names[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new Names[VALUES.size()]);
            }
        }

        public Names[] family() {
            return values();
        }
    }
}
