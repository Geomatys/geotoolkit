/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.display.canvas;

import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.internal.shared.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AbstractCanvas2DTest {

    /**
     * Test configuring a canvas with a size and envelope generates a
     * grid geometry with the right envelope.
     */
    @Test
    public void testEnvelopeAndSizeToGrid() throws TransformException, NoninvertibleTransformException, FactoryException {

        final CoordinateReferenceSystem crs84 = CommonCRS.WGS84.normalizedGeographic();
        final GeneralEnvelope env = new GeneralEnvelope(crs84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);

        final AbstractCanvas2D canvas = new MockCanvas();
        canvas.setDisplayBounds(new Rectangle2D.Double(0, 0, 360, 180));
        canvas.setObjectiveCRS(crs84);
        canvas.setVisibleArea(env);

        Assert.assertEquals(crs84, canvas.getObjectiveCRS());
        Assert.assertEquals(env, new GeneralEnvelope(canvas.getVisibleEnvelope()));

        final AffineTransform2D objtoDisp = canvas.getObjectiveToDisplay();
        Assert.assertEquals(new AffineTransform2D(1, 0, 0, -1, 179.5, 89.5), objtoDisp);

        final MathTransform cornerTrs = canvas.getGridGeometry2D().getGridToCRS(PixelInCell.CELL_CORNER);
        Assert.assertEquals(new AffineTransform2D(1, 0, 0, -1, -180, 90), cornerTrs);
    }

    @Test
    public void testCenterTransform() throws NoninvertibleTransformException, TransformException, FactoryException {
        final CoordinateReferenceSystem crs84 = CommonCRS.WGS84.normalizedGeographic();
        final AbstractCanvas2D canvas = new MockCanvas();
        canvas.setObjectiveCRS(crs84);
        canvas.setDisplayBounds(new Rectangle2D.Double(0, 0, 360, 180));

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        canvas.setVisibleArea(env);

        final AffineTransform2D objtoDisp = canvas.getObjectiveToDisplay();
        final AffineTransform centerTrs = canvas.getCenterTransform();
        assertEquals(new AffineTransform2D(1, 0, 0, -1, 0, 0), centerTrs);

        //reset it and check
        canvas.setCenterTransform(centerTrs);
        final AffineTransform objToDisp2 = canvas.getObjectiveToDisplay();
        assertEquals(objtoDisp, objToDisp2);
    }

    private static class MockCanvas extends AbstractCanvas2D {

        @Override
        public boolean repaint(Shape area) {
            return true;
        }

        @Override
        public Image getSnapShot() {
            return null;
        }

    }
}
