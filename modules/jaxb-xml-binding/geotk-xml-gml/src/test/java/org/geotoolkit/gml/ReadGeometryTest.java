package org.geotoolkit.gml;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import java.net.URL;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.junit.Assert;

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

        Assert.assertEquals("Bad boundaries", expectedEnvelope, col.getEnvelopeInternal());
    }

    final <T> T read(final URL source) throws Exception {
        final MarshallerPool pool = GMLMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();

        Object unmarshalled = unmarshaller.unmarshal(source);

        pool.recycle(unmarshaller);

        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }
        return (T) unmarshalled;
    }
}
