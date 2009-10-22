/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

// J2SE dependencies
import java.util.List;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// JUnit dependencies
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// OpenGIS dependencies
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

// Geotools dependencies
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;


/**
 * Tests {@link ReferencedCanvas} and {@link ReferencedGraphic}.
 *
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @module pending
 */
public class ReferencedObjectTest {
    
    
//    extends TestCase implements PropertyChangeListener {
//}
//    /**
//     * Run the test case from the command line.
//     */
//    public static void main(final String[] args) throws Exception {
//        junit.textui.TestRunner.run(suite());
//    }
//
//    /**
//     * Returns the suite of tests.
//     */
//    public static Test suite() {
//        TestSuite suite = new TestSuite(ReferencedObjectTest.class);
//        return suite;
//    }
//
//    /**
//     * The last property change event fired by the canvas.
//     */
//    private PropertyChangeEvent event;
//
//    /**
//     * Constructs the test case.
//     */
//    public ReferencedObjectTest(final String name) {
//        super(name);
//    }
//
//    /**
//     * Invoked when a canvas property changed.
//     */
//    public void propertyChange(final PropertyChangeEvent event) {
//        this.event = event;
//    }
//
//    /**
//     * Returns the specified graphics as a list.
//     */
//    private static List asList(DummyGraphic g1, DummyGraphic g2, DummyGraphic g3) {
//        return Arrays.asList(new DummyGraphic[] {g1, g2, g3});
//    }
//
//    /**
//     * Tests if the specified envelope is equals to the specified rectangle.
//     */
//    private static void assertEnvelopeEquals(final double x, final double y,
//                                final double width, final double height, final Envelope envelope)
//    {
//        assertEquals("x",      x,      envelope.getMinimum(0), 1E-6);
//        assertEquals("y",      y,      envelope.getMinimum(1), 1E-6);
//        assertEquals("width",  width,  envelope.getLength (0), 1E-6);
//        assertEquals("height", height, envelope.getLength (1), 1E-6);
//    }
//
//    /**
//     * Tests basic graphic operations.
//     */
//    public void testGraphic() {
//        final DummyGraphic graphic = new DummyGraphic();
//        assertFalse(graphic.hasScaleListeners);
//        graphic.addPropertyChangeListener(this);
//        assertTrue(graphic.hasScaleListeners);
//        /*
//         * Tests the Z order hint.
//         */
//        assertTrue  ("Z order hint should be initially set to positive infinite.",
//                     Double.POSITIVE_INFINITY == graphic.getZOrderHint());
//        graphic.setZOrderHint(12);
//        assertEquals("Z order hint should be set.",
//                     12, graphic.getZOrderHint(), 0);
//        assertEquals("Z order event should be fired.",
//                     DisplayObject.Z_ORDER_HINT_PROPERTY, event.getPropertyName());
//        assertEquals("New Z order should be given.",
//                     new Double(12), event.getNewValue());
//        /*
//         * Tests the name property.
//         */
//        graphic.setName("Dummy");
//        assertEquals("Graphic name should be set. ",
//                     "Dummy", graphic.getName());
//        assertEquals("Name change should fires a property change event.",
//                     DisplayObject.NAME_PROPERTY, event.getPropertyName());
//        assertNull  ("The old title should not have been set.",
//                     event.getOldValue());
//        assertEquals("The new title should be given.",
//                     "Dummy", event.getNewValue());
//        /*
//         * Tests the visible property.
//         */
//        assertTrue("Graphics should be initially visible.",
//                   graphic.getVisible());
//        graphic.setVisible(false);
//        assertEquals("Visibility changes should fires an event.", 
//                     DisplayObject.VISIBLE_PROPERTY, event.getPropertyName());
//        assertEquals("Old visibility should be true.",
//                     Boolean.TRUE, event.getOldValue());
//        assertEquals("New visibility should be false.",
//                     Boolean.FALSE, event.getNewValue());
//        /*
//         * Tests disposal.
//         */
//        assertTrue  (graphic.hasScaleListeners);
//        assertEquals("Our test listener should be the only one registered.",
//                     1, graphic.listeners.getPropertyChangeListeners().length);
//        graphic.dispose();
//        assertFalse (graphic.hasScaleListeners);
//        assertEquals("All listeners should have been removed after graphic disposal.",
//                     0, graphic.listeners.getPropertyChangeListeners().length);
//    }
//
//    /**
//     * Tests basic canvas operations.
//     */
//    public void testCanvas() throws TransformException {
//        final DummyCanvas canvas = new DummyCanvas();
//        canvas.addPropertyChangeListener(this);
//        /*
//         * Tests rendering hints.
//         */
//        assertNull(canvas.getImplHint("KEY_RENDERING"));
//        canvas.setImplHint("KEY_RENDERING", RenderingHints.VALUE_RENDER_QUALITY);
//        assertSame(RenderingHints.VALUE_RENDER_QUALITY, canvas.getImplHint("KEY_RENDERING"));
//        assertSame(RenderingHints.VALUE_RENDER_QUALITY, canvas.getImplHint("key_rendering"));
//        assertSame(RenderingHints.VALUE_RENDER_QUALITY, canvas.getImplHint("keyRendering" ));
//        assertSame(RenderingHints.VALUE_RENDER_QUALITY, canvas.getImplHint("KeyRendering" ));
//        /*
//         * Tests the title property.
//         */
//        assertNull("Canvas title should be initially null.", 
//                   canvas.getTitle());
//        canvas.setTitle("Dummy");
//        assertEquals("Canvas title should bet set.",
//                     "Dummy", canvas.getTitle());
//        assertEquals("Title change should fires a property change event.",
//                     DisplayObject.TITLE_PROPERTY, event.getPropertyName());
//        assertNull  ("The old title should be null.",
//                     event.getOldValue());
//        assertEquals("The new title should be given.",
//                     "Dummy", event.getNewValue());
//        /*
//         * Tests the addition of a first graphic.
//         */
//        assertEquals("The default canvas CRS should be a generic (kind of wildcard) one.",
//                     DefaultEngineeringCRS.GENERIC_2D, canvas.getObjectiveCRS());
//        assertEquals("The canvas envelope should be two-dimensional.",
//                     2, canvas.getEnvelope().getDimension());
//        assertTrue  ("The canvas envelope should be initially null (NaN)",
//                     ((GeneralEnvelope) canvas.getEnvelope()).isNull());
//        assertTrue  ("The canvas should initially contains no graphic.",
//                     canvas.getGraphics().isEmpty());
//
//        final DummyGraphic graphic1 = new DummyGraphic();
//        assertEquals("The default graphic CRS should be a cartesian one.",
//                     DefaultEngineeringCRS.CARTESIAN_2D, graphic1.getObjectiveCRS());
//        assertTrue  ("The graphic envelope should be initially null (i.e. undefined).",
//                     ((GeneralEnvelope) graphic1.getEnvelope()).isNull());
//        assertNull  ("The graphic should not be owned by any canvas yet.",
//                     graphic1.getCanvas());
//        assertSame  ("Adding the new graphic to the canvas should not clone it.",
//                     graphic1, canvas.add(graphic1));
//        assertSame  ("The graphic should not be owned by our canvas.",
//                     canvas, graphic1.getCanvas());
//        assertEquals("The canvas CRS should have been automatically set to the graphic CRS.",
//                     DefaultEngineeringCRS.CARTESIAN_2D, canvas.getObjectiveCRS());
//        assertTrue  ("The canvas envelope should still null, since the graphic envelope was null.",
//                     ((GeneralEnvelope) canvas.getEnvelope()).isNull());
//        /*
//         * Tests the addition of a second graphic.
//         */
//        final DummyGraphic graphic2 = new DummyGraphic();
//        assertSame  ("Adding the new graphic to the canvas should not clone it.",
//                     graphic2, canvas.add(graphic2));
//        assertEquals("Adding a graphic should fires a property change event.",
//                     DisplayObject.GRAPHICS_PROPERTY, event.getPropertyName());
//        assertEquals("We should have only 1 graphic prior ther addition.",
//                     1, ((List) event.getOldValue()).size());
//        assertEquals("We should have 2 graphics after the addition.",
//                     2, ((List) event.getNewValue()).size());
//        assertSame  ("The same graphics list instance should be returned.",
//                     canvas.getGraphics(), event.getNewValue());
//        try {
//            canvas.getGraphics().remove(0);
//            fail("The graphic list should be immutable.");
//        } catch (UnsupportedOperationException e) {
//            // This is the expected exception.
//        }
//        /*
//         * Tests the addition of a third graphic.
//         */
//        final DummyGraphic graphic3 = new DummyGraphic();
//        assertSame  ("Adding the new graphic to the canvas should not clone it.",
//                     graphic3, canvas.add(graphic3));
//        assertEquals("Graphics should be listed in their insertion order.",
//                     asList(graphic1, graphic2, graphic3), canvas.getGraphics());
//        graphic3.setZOrderHint(1);
//        assertEquals("The graphic with z=1 should be first.",
//                     asList(graphic3, graphic1, graphic2), canvas.getGraphics());
//        graphic2.setZOrderHint(2);
//        assertEquals("The graphic with z=2 should be after the graphic with z=1",
//                     asList(graphic3, graphic2, graphic1), canvas.getGraphics());
//        graphic2.setZOrderHint(1);
//        assertEquals("Graphics with the same z value should be listed in their insertion order.",
//                     asList(graphic2, graphic3, graphic1), canvas.getGraphics());
//        graphic1.setZOrderHint(1);
//        assertEquals("Graphics with the same z value should be listed in their insertion order.",
//                     asList(graphic1, graphic2, graphic3), canvas.getGraphics());
//        assertSame  ("Adding a graphic already presents should not clone it.",
//                     graphic2, canvas.add(graphic2));
//        assertEquals("Adding a graphic already presents should have no effect on order.",
//                     asList(graphic1, graphic2, graphic3), canvas.getGraphics());
//        /*
//         * Tests envelope changes.
//         */
//        assertTrue  ("The envelope should still null.",
//                     ((GeneralEnvelope) canvas.getEnvelope()).isNull());
//        graphic2.setEnvelope(new Envelope2D(null, /*x*/5, /*y*/-5, /*width*/10, /*height*/20));
//        assertEquals("Envelope change should fires a property change event.",
//                     DisplayObject.ENVELOPE_PROPERTY, event.getPropertyName());
//        assertEquals("The new envelope should be the canvas envelope.",
//                     canvas.getEnvelope(), event.getNewValue());
//        assertEnvelopeEquals(5, -5, 10, 20, canvas.getEnvelope());
//        event = null;
//        graphic3.setEnvelope(new Envelope2D(null, 10, -5, 5, 15));
//        assertNull("Adding an envelope contained into the previous one should not fire any event.",
//                   event);
//        graphic1.setEnvelope(new Envelope2D(null, 15, -10, 5, 10));
//        assertEquals("Envelope change should fires a property change event.",
//                     DisplayObject.ENVELOPE_PROPERTY, event.getPropertyName());
//        assertEnvelopeEquals(5, -10, 15, 25, canvas.getEnvelope());
//        graphic1.setEnvelope(new Envelope2D(null, 5, -10, 5, 10));
//        assertEnvelopeEquals(5, -10, 10, 25, canvas.getEnvelope());
//        /*
//         * Tests cell dimensions.
//         */
//        assertNull("No cell dimension should be defined yet.",
//                   canvas.getTypicalCellDimension(null));
//        graphic1.setTypicalCellDimension(new double[] {1,3});
//        graphic3.setTypicalCellDimension(new double[] {3,2});
//        assertTrue("Typical cell dimensions should be the smallest one.",
//                   Arrays.equals(new double[]{1,2}, canvas.getTypicalCellDimension(null)));
//        /*
//         * Tests graphic addition in an other canvas.
//         */
//        if (true) {
//            final DummyCanvas canvas2 = new DummyCanvas();
//            assertNotSame(graphic3, canvas2.add(graphic3));
//            assertEquals(graphic3.getEnvelope(), canvas2.getEnvelope());
//            canvas2.dispose();
//        }
//        /*
//         * Tests CRS changes. Note: we disable the WARNING level in order to avoid polluting
//         * the standard output with warnings that are know to be normal for this test suite.
//         */
//        final Logger logger = canvas.getLogger();
//        final Level oldLevel = logger.getLevel();
//        logger.setLevel(Level.SEVERE);
//        canvas.objectiveToDisplay.setToScale(10, 10);
//        assertTrue("The objective to display transform should be the identity transform.",
//                   canvas.getObjectiveToDisplayTransform().isIdentity());
//        canvas.setObjectiveToDisplayTransform(canvas.objectiveToDisplay);
//        assertFalse("The objective to display transform should not be the identity anymore.",
//                   canvas.getObjectiveToDisplayTransform().isIdentity());
//        // The following is not a usual thing to do, just a trick for trying a different CRS.
//        canvas.setObjectiveCRS(canvas.getDisplayCRS());
//        assertTrue("Typical cell dimensions should have been updated.",
//                   Arrays.equals(new double[]{10,20}, canvas.getTypicalCellDimension(null)));
//        assertEnvelopeEquals(50, -100, 100, 250, canvas.getEnvelope());
//        /*
//         * Tests disposal.
//         */
//        assertEquals("Our test listener should be the only one registered.",
//                     1, canvas.listeners.getPropertyChangeListeners().length);
//        canvas.dispose();
//        assertEquals("All listeners should have been removed after canvas disposal.",
//                     0, canvas.listeners.getPropertyChangeListeners().length);
//        logger.setLevel(oldLevel);
//    }
}
