package org.geotoolkit.benchmarks;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.kml.xml.KMLMarshallerPoolV230;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.w3c.dom.Document;

/**
 * Benchmarks the performance of decoding KML data using Jaxb using two different approaches:
 * <ul>
 *     <li>Direct stream-based decoding</li>
 *     <li>DOM-based document decoding</li>
 * </ul>
 *
 * The aim is to assess if it is worth it refining the current KML implementation that forces DOM based decoding.
 */
@Warmup(iterations = 4, time = 4)
@Measurement(iterations = 4, time = 4)
@Fork(value = 2, jvmArgs = {"-server", "-Xms64m", "-Xmx64m"})
@Threads(4)
public class KMLDecoderBenchmark {

    private static final String IN_MEMORY_DOC =
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <kml xmlns="http://www.opengis.net/kml/2.2">
        <Placemark>
          <name>Google Earth - New Placemark</name>
          <description>Some Descriptive text.</description>
          <LookAt>
            <longitude>-90.86879847669974</longitude>
            <latitude>48.25330383601299</latitude>
            <heading>2.7</heading>
            <tilt>8.3</tilt>
            <range>440.8</range>
          </LookAt>
          <Point>
            <coordinates>-90.86948943473118,48.25450093195546,0.0</coordinates>
          </Point>
        </Placemark>
        </kml>
        """;


    @State(Scope.Thread)
    public static class KMLInput {

        private MarshallerPool pool;
        private byte[] kmlBytes;
        private DocumentBuilderFactory factory;

        @Setup
        public void setup() {
            kmlBytes = IN_MEMORY_DOC.getBytes(StandardCharsets.UTF_8);
            pool = KMLMarshallerPoolV230.getINSTANCE();
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
        }
    }

    @Benchmark
    public Object decodeInputStream(KMLInput input) throws Exception {
        try (var stream = new ByteArrayInputStream(input.kmlBytes)) {
            var um = input.pool.acquireUnmarshaller();
            var decoded = um.unmarshal(stream);
            input.pool.recycle(um);
            return decoded;
        }
    }

    @Benchmark
    public Object decodeDocument(KMLInput input) throws Exception {
        final DocumentBuilder builder = input.factory.newDocumentBuilder();
        final Document doc;
        try (var stream = new ByteArrayInputStream(input.kmlBytes)) {
            doc = builder.parse(stream);
        }
        var um = input.pool.acquireUnmarshaller();
        var decoded = um.unmarshal(doc);
        input.pool.recycle(um);
        return decoded;
    }
}
