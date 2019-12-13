package org.geotoolkit.gml;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.junit.Assert;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public abstract class ReadGeometryTest {

    protected void testCollection(
            final URL geometryFile,
            final Class<? extends GeometryCollection> expectedType,
            final int expectedInnerGeometries,
            final Envelope expectedEnvelope) throws Exception {
        final AbstractGeometry geom = read(geometryFile);
        final GeometryTransformer tr = new GeometryTransformer(geom);
        final Geometry result = tr.get();
        Assert.assertNotNull("Read geometry is null", result);
        Assert.assertTrue(
                String.format(
                        "Bad geometry type.%nExpected: %s%nBut was: %s",
                        expectedType, result.getClass()
                ),
                expectedType.isAssignableFrom(result.getClass())
        );

        final GeometryCollection col = (GeometryCollection) result;
        Assert.assertEquals("Bad number of polygon members", expectedInnerGeometries, col.getNumGeometries());

        final Envelope actual = col.getEnvelopeInternal();
        Assert.assertEquals(expectedEnvelope.getMinX(), actual.getMinX(), 0.01);
        Assert.assertEquals(expectedEnvelope.getMinY(), actual.getMinY(), 0.01);
        Assert.assertEquals(expectedEnvelope.getMaxX(), actual.getMaxX(), 0.01);
        Assert.assertEquals(expectedEnvelope.getMaxY(), actual.getMaxY(), 0.01);
    }

    protected final <T> T read(final URL source) throws Exception {
        final MarshallerPool pool = GMLMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        unmarshaller.setEventHandler((ValidationEvent event) -> {
            final boolean shouldContinue = event.getSeverity() < ValidationEvent.ERROR;

            final LogRecord record = new LogRecord(shouldContinue ? Level.WARNING : Level.SEVERE, event.getMessage());
            record.setThrown(event.getLinkedException());
            Logging.getLogger("org.geotoolkit.gml").log(record);

            return shouldContinue;
        });

        Object unmarshalled = unmarshaller.unmarshal(source);

        pool.recycle(unmarshaller);

        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }
        return (T) unmarshalled;
    }
}
