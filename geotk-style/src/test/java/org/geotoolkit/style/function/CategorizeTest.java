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

package org.geotoolkit.style.function;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import org.apache.sis.coverage.grid.GridExtent;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.geotoolkit.image.RecolorRenderedImage;
import org.geotoolkit.style.StyleConstants;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Expression;
import static org.geotoolkit.filter.FilterUtilities.FF;
import static org.junit.Assert.*;
import static java.awt.Color.*;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CategorizeTest {

    public CategorizeTest() {
    }

    @Test
    public void categorize(){
        final String attribut = "att_value";

        final FilterFactory ff = FF;

        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("test");
        sftb.addAttribute(Double.class).setName(attribut);
        final FeatureType sft = sftb.build();

        final Feature f1 = sft.newInstance();
        f1.setPropertyValue(attribut, -5d);
        final Feature f2 = sft.newInstance();
        f2.setPropertyValue(attribut, 8d);
        final Feature f3 = sft.newInstance();
        f3.setPropertyValue(attribut, 30d);

        final Expression Lookup = ff.property(attribut);
        final Map<Expression,Expression> values = new HashMap<Expression, Expression>();
        values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, ff.literal(GREEN));
        values.put(ff.literal(1d), ff.literal(RED));
        values.put(ff.literal(8d), ff.literal(YELLOW));
        values.put(ff.literal(15d), ff.literal(BLUE));

        Categorize categorize = new DefaultCategorize(Lookup, values, ThreshholdsBelongTo.PRECEDING, null);

        Color c = (Color) categorize.apply(f1);
        assertEquals(c, GREEN);
        c = (Color) categorize.apply(f2);
        assertEquals(c, YELLOW);
        c = (Color) categorize.apply(f3);
        assertEquals(c, BLUE);

        categorize = new DefaultCategorize(Lookup, values, ThreshholdsBelongTo.SUCCEEDING, null);
        c = (Color) categorize.apply(f1);
        assertEquals(c, GREEN);
        c = (Color) categorize.apply(f2);
        assertEquals(c, RED);
        c = (Color) categorize.apply(f3);
        assertEquals(c, BLUE);


        //test get lookup property
        Collection<String> requieredAttributs = new HashSet<String>();
        ListingPropertyVisitor.VISITOR.visit(categorize, requieredAttributs);

        assertEquals(requieredAttributs.size(), 1);
        assertEquals(requieredAttributs.iterator().next(), attribut);
    }

    /**
     * Ensure color map works on source images that do not define any color model.
     * Happens with some NetCDF datasets.
     */
    @Test
    public void testImageWithoutColorModel() {
        var bufImg = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);
        var sourceData = bufImg.getWritableTile(0, 0);
        sourceData.setSample(0, 0, 0, 0);
        sourceData.setSample(1, 0, 0, 1);
        sourceData.setSample(1, 1, 0, 2);
        sourceData.setSample(0, 1, 0, 3);
        var sourceWithoutColorModel = new RecolorRenderedImage(bufImg, null);

        var values = new HashMap<Expression, Expression>();
        values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, FF.literal(BLACK));
        values.put(FF.literal(0), FF.literal(WHITE));
        values.put(FF.literal(1), FF.literal(RED));
        values.put(FF.literal(2), FF.literal(GREEN));
        values.put(FF.literal(3), FF.literal(BLUE));
        var categorize = new DefaultCategorize(null, values, ThreshholdsBelongTo.SUCCEEDING, null);

        var result = categorize.apply(sourceWithoutColorModel);
        assertNotNull(result);
        assertTrue(result instanceof RenderedImage);
        // Acquire buffered image to be able to access color by pixel position
        var testImg = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);
        testImg.createGraphics()
               .drawRenderedImage((RenderedImage) result, new AffineTransform());
        assertEquals("Pixel at position (0, 0) should have been colored in white", 0xFFFFFFFF, testImg.getRGB(0, 0));
        assertEquals("Pixel at position (1, 0) should have been colored in red", 0xFFFF0000, testImg.getRGB(1, 0));
        assertEquals("Pixel at position (1, 1) should have been colored in green", 0xFF00FF00, testImg.getRGB(1, 1));
        assertEquals("Pixel at position (0, 1) should have been colored in blue", 0xFF0000FF, testImg.getRGB(0, 1));
    }
}
