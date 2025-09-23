package org.geotoolkit.data.nmea;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;

import org.opengis.feature.Feature;

import org.apache.sis.feature.internal.shared.AttributeConvention;

import net.sf.marineapi.nmea.sentence.Sentence;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Point;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxTest {

    public static final double EPSI = 1e-4;

    @Test
    public void line2Sentences() throws Exception {
        StepVerifier.create(readSample())
                .expectSubscription()
                // Test sample contains 101 lines, but all lines starting with $PG are ignored (unsupported sentences)
                .expectNextCount(77)
                .verifyComplete();
    }

    @Test
    public void read() throws Exception {
        final Flux<Feature> dataFlux = new FeatureProcessor().emit(readSample());

        StepVerifier.create(dataFlux)
                .expectSubscription()
                .assertNext(f -> check(f, 19.671422, 60.065708, null, "2010-05-28", "13:15:50+00:00"))
                .assertNext(f -> check(f, 19.671422, 60.065708, -1.6, "2010-05-28", "13:15:50+00:00"))
                .assertNext(f -> check(f, 19.671460, 60.065713, -1.6, "2010-05-28", "13:15:52+00:00"))
                .assertNext(f -> check(f, 19.671460, 60.065713, -1.4, "2010-05-28", "13:15:52+00:00"))
                .assertNext(f -> check(f, 19.671502, 60.065718, -1.4, "2010-05-28", "13:15:54+00:00"))
                .assertNext(f -> check(f, 19.671502, 60.065718, -1.2, "2010-05-28", "13:15:54+00:00"))
                .assertNext(f -> check(f, 19.671543, 60.065722, -1.2, "2010-05-28", "13:15:56+00:00"))
                .assertNext(f -> check(f, 19.671543, 60.065722, -1.3, "2010-05-28", "13:15:56+00:00"))
                .assertNext(f -> check(f, 19.671582, 60.065727, -1.3, "2010-05-28", "13:15:58+00:00"))
                .assertNext(f -> check(f, 19.671582, 60.065727, -1.5, "2010-05-28", "13:15:58+00:00"))
                .assertNext(f -> check(f, 19.671622, 60.065730, -1.5, "2010-05-28", "13:16:00+00:00"))
                .assertNext(f -> check(f, 19.671622, 60.065730, -1.3, "2010-05-28", "13:16:00+00:00"))
                .assertNext(f -> check(f, 19.671660, 60.065732, -1.3, "2010-05-28", "13:16:02+00:00"))
                .assertNext(f -> check(f, 19.671660, 60.065732, -1.4, "2010-05-28", "13:16:02+00:00"))
                .verifyComplete();
    }

    private Flux<Sentence> readSample() throws URISyntaxException, IOException {
        final URL sampleFile = FluxTest.class.getResource("Garmin-GPS76.txt");
        final List<String> allRecords = Files.readAllLines(Paths.get(sampleFile.toURI()));
        return Flux.fromIterable(allRecords)
                .delayElements(Duration.ofMillis(2))
                .map(FeatureProcessor::read)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    static void check(final Feature candidate, final double lon, final double lat, final Double altitude, final String isoLocalDate, final String isoOffsetTime) {
        final Object geomVal = candidate.getPropertyValue(AttributeConvention.GEOMETRY);
        Assert.assertTrue("Expected a point object", geomVal instanceof Point);
        Assert.assertEquals("Longitude", lon, ((Point) geomVal).getX(), EPSI);
        Assert.assertEquals("Latitude", lat, ((Point) geomVal).getY(), EPSI);

        final Object altiValue = candidate.getPropertyValue(NMEAStore.ALT_NAME.toString());
        if (altitude == null) {
            Assert.assertNull("Altitude", altiValue);
        } else {
            Assert.assertTrue("Altitude should be a decimal value", altitude instanceof Number);
            Assert.assertEquals("Altitude", altitude, ((Number)altiValue).doubleValue(), EPSI);
        }

        if (isoLocalDate!= null) {
            final Object dateVal = candidate.getPropertyValue(NMEAStore.DATE_NAME.toString());
            Assert.assertEquals("Date", LocalDate.parse(isoLocalDate), dateVal);
        }

        if (isoOffsetTime != null) {
            final Object timeVal = candidate.getPropertyValue(NMEAStore.TIME_NAME.toString());
            Assert.assertEquals("Time with offset", OffsetTime.parse(isoOffsetTime), timeVal);
        }
    }
}
