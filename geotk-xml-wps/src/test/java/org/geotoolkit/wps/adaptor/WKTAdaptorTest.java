package org.geotoolkit.wps.adaptor;

import java.util.List;

import org.apache.sis.referencing.CommonCRS;

import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.DataInput;

import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WKTAdaptorTest {

    private static final GeometryFactory GF = JTS.getFactory();
    public static final WKTAdaptor ADAPTOR = new WKTAdaptor(WKTAdaptor.MIME_TYPE, WKTAdaptor.ENC_UTF8);

    private static String adapt(final Geometry geom) {
        final DataInput value = ADAPTOR.toWPS2Input(geom);
        assertNotNull(value);
        final List<Object> content = value.getData().getContent();
        assertNotNull(content);
        assertEquals("Number of values", 1, content.size());
        final Object innerContent = content.get(0);
        assertTrue(innerContent instanceof ComplexData);
        final ComplexData cData = (ComplexData) innerContent;
        return (String) cData.getContent().stream()
                .filter(v -> v instanceof String)
                .findAny()
                .orElseThrow(() -> new AssertionError("No String content found"));
    }

    @Test
    public void test2D() {
        Point point = GF.createPoint(new Coordinate(2.2, 2.4, 3));
        JTS.setCRS(point, CommonCRS.defaultGeographic());
        Assert.assertEquals("SRID=4326;POINT (2.2 2.4)", adapt(point));

        point = GF.createPoint(new Coordinate(2.2, 2.4));
        JTS.setCRS(point, CommonCRS.defaultGeographic());
        Assert.assertEquals("SRID=4326;POINT (2.2 2.4)", adapt(point));
    }

    /**
     * TODO: once longitude first forcing is remove from the adaptor, fix that test by inverting point X and Y coordinates.
     */
    @Test
    public void test3D() {
        Point point = GF.createPoint(new Coordinate(2.4, 2.2, 3));
        JTS.setCRS(point, CommonCRS.WGS84.geographic3D());
        Assert.assertEquals("SRID=4979;POINT Z(2.2 2.4 3)", adapt(point));
    }

    /**
     * When no CRS is available, always fallback on 2D.
     */
    @Test
    public void testNoCrs() {
        Point point = GF.createPoint(new Coordinate(2.2, 2.4));
        Assert.assertEquals("POINT (2.2 2.4)", adapt(point));

        point = GF.createPoint(new Coordinate(2.2, 2.4, 4.4));
        Assert.assertEquals("POINT (2.2 2.4)", adapt(point));
    }
}
