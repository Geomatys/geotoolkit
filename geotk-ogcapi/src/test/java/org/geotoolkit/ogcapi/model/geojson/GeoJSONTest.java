/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.geojson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.List;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.base.MemoryFeatureSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.GeographicCRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeoJSONTest {

    private static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void testWriteFeature() throws JsonProcessingException, DataStoreException {
        final GeographicCRS crs = CommonCRS.WGS84.normalizedGeographic();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Car");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(Point.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Double.class).setName("att1");
        final FeatureType type = ftb.build();
        final Point geom = GF.createPoint(new CoordinateXY(10, 20));
        geom.setUserData(crs);
        final Feature feature = type.newInstance();
        feature.setPropertyValue(AttributeConvention.IDENTIFIER, "uid");
        feature.setPropertyValue("att1", 123.456);
        feature.setPropertyValue(AttributeConvention.GEOMETRY, geom);

        final FeatureSet fs = new MemoryFeatureSet(null, type, List.of(feature));


        final GeoJSONMapper mapper = new GeoJSONMapper();

        {//test default geojson
            final GeoJSONFeature json = mapper.transform(feature);
            Assertions.assertEquals(
                """
                {
                  "type" : "Feature",
                  "id" : "uid",
                  "geometry" : {
                    "type" : "Point",
                    "coordinates" : [ 10.0, 20.0 ]
                  },
                  "properties" : {
                    "att1" : 123.456
                  }
                }""", toJsonString(json));
        }

        {// disable id writing
            mapper.setIncludeFeatureId(false);
            final GeoJSONFeature json = mapper.transform(feature);
            Assertions.assertEquals(
                """
                {
                  "type" : "Feature",
                  "geometry" : {
                    "type" : "Point",
                    "coordinates" : [ 10.0, 20.0 ]
                  },
                  "properties" : {
                    "att1" : 123.456
                  }
                }""", toJsonString(json));
        }

        {//test geometry bbox
            mapper.setBboxOnGeometry(true);
            final GeoJSONFeature json = mapper.transform(feature);
            Assertions.assertEquals(
                """
                {
                  "type" : "Feature",
                  "geometry" : {
                    "type" : "Point",
                    "bbox" : [ 10.0, 20.0, 10.0, 20.0 ],
                    "coordinates" : [ 10.0, 20.0 ]
                  },
                  "properties" : {
                    "att1" : 123.456
                  }
                }""", toJsonString(json));
            mapper.setBboxOnGeometry(false);
        }

        {//test geometry crs
            mapper.setIncludeCoordRefSysOnGeometry(true);
            final GeoJSONFeature json = mapper.transform(feature);
            Assertions.assertEquals(
                """
                {
                  "type" : "Feature",
                  "geometry" : {
                    "type" : "Point",
                    "coordinates" : [ 10.0, 20.0 ],
                    "coordRefSys" : {
                      "href" : "CRS:84"
                    }
                  },
                  "properties" : {
                    "att1" : 123.456
                  }
                }""", toJsonString(json));
            mapper.setIncludeCoordRefSysOnGeometry(false);
        }

        {//test feature bbox
            mapper.setBboxOnFeature(true);
            final GeoJSONFeature json = mapper.transform(feature);
            Assertions.assertEquals(
                """
                {
                  "type" : "Feature",
                  "bbox" : [ 10.0, 20.0, 10.0, 20.0 ],
                  "geometry" : {
                    "type" : "Point",
                    "coordinates" : [ 10.0, 20.0 ]
                  },
                  "properties" : {
                    "att1" : 123.456
                  }
                }""", toJsonString(json));
            mapper.setBboxOnFeature(false);
        }

        {//test feature crs
            mapper.setIncludeCoordRefSysOnFeature(true);
            final GeoJSONFeature json = mapper.transform(feature);
            Assertions.assertEquals(
                """
                {
                  "type" : "Feature",
                  "coordRefSys" : {
                    "href" : "CRS:84"
                  },
                  "geometry" : {
                    "type" : "Point",
                    "coordinates" : [ 10.0, 20.0 ]
                  },
                  "properties" : {
                    "att1" : 123.456
                  }
                }""", toJsonString(json));
            mapper.setIncludeCoordRefSysOnFeature(false);
        }

        {//test feature type on feature
            mapper.setIncludeTypeOnFeature(true);
            final GeoJSONFeature json = mapper.transform(feature);
            Assertions.assertEquals(
                """
                {
                  "type" : "Feature",
                  "featureType" : "Car",
                  "geometry" : {
                    "type" : "Point",
                    "coordinates" : [ 10.0, 20.0 ]
                  },
                  "properties" : {
                    "att1" : 123.456
                  }
                }""", toJsonString(json));
            mapper.setIncludeTypeOnFeature(false);
        }

        {//test collection bbox
            mapper.setBboxOnCollection(true);
            final GeoJSONFeatureCollection json = mapper.transform(fs);
            Assertions.assertEquals(
                """
                {
                  "type" : "FeatureCollection",
                  "bbox" : [ 10.0, 20.0, 10.0, 20.0 ],
                  "features" : [ {
                    "type" : "Feature",
                    "geometry" : {
                      "type" : "Point",
                      "coordinates" : [ 10.0, 20.0 ]
                    },
                    "properties" : {
                      "att1" : 123.456
                    }
                  } ]
                }""", toJsonString(json));
            mapper.setBboxOnCollection(false);
        }

        {//test collection crs
            mapper.setIncludeCoordRefSysOnCollection(true);
            final GeoJSONFeatureCollection json = mapper.transform(fs);
            Assertions.assertEquals(
                """
                {
                  "type" : "FeatureCollection",
                  "coordRefSys" : {
                    "href" : "CRS:84"
                  },
                  "features" : [ {
                    "type" : "Feature",
                    "geometry" : {
                      "type" : "Point",
                      "coordinates" : [ 10.0, 20.0 ]
                    },
                    "properties" : {
                      "att1" : 123.456
                    }
                  } ]
                }""", toJsonString(json));
            mapper.setIncludeCoordRefSysOnCollection(false);
        }

        {//test featureType on collection
            mapper.setIncludeTypeOnCollection(true);
            final GeoJSONFeatureCollection json = mapper.transform(fs);
            Assertions.assertEquals(
                """
                {
                  "type" : "FeatureCollection",
                  "featureType" : "Car",
                  "features" : [ {
                    "type" : "Feature",
                    "geometry" : {
                      "type" : "Point",
                      "coordinates" : [ 10.0, 20.0 ]
                    },
                    "properties" : {
                      "att1" : 123.456
                    }
                  } ]
                }""", toJsonString(json));
        }
    }

    @Test
    public void testReadFeature() throws JsonProcessingException, DataStoreException {

        final String json = """
                                {
                                  "type" : "Feature",
                                  "id" : "uid",
                                  "geometry" : {
                                    "type" : "Point",
                                    "coordinates" : [ 10.0, 20.0 ]
                                  },
                                  "properties" : {
                                    "att1" : 123.456
                                  }
                                }""";

        final GeoJSONFeature geojson = fromJsonString(json, GeoJSONFeature.class);
        final Feature feature = GeoJSON.fromGeoJSON(geojson, null);


        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("geojson");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY).addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(Point.class).setName(AttributeConvention.GEOMETRY_PROPERTY).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Double.class).setName("att1");
        final FeatureType type = ftb.build();
        final Point geom = GF.createPoint(new CoordinateXY(10, 20));
        final Feature expected = type.newInstance();
        expected.setPropertyValue(AttributeConvention.IDENTIFIER, "uid");
        expected.setPropertyValue("att1", 123.456);
        expected.setPropertyValue(AttributeConvention.GEOMETRY, geom);

        Assertions.assertEquals(expected, feature);
    }

    private static String toJsonString(Object obj) throws JsonProcessingException {
        JsonMapper mapper = new JsonMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(obj);
    }

    private static <T> T fromJsonString(String json, Class<T> clazz) throws JsonProcessingException {
        JsonMapper mapper = new JsonMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.readValue(json, clazz);
    }

}
