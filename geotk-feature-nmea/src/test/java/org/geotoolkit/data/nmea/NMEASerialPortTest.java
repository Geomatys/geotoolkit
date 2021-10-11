package org.geotoolkit.data.nmea;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.geotoolkit.data.nmea.FluxTest.check;
import static org.geotoolkit.data.nmea.SerialPortConfig.NMEA_0183;
import static org.junit.Assert.assertEquals;

public class NMEASerialPortTest extends AbstractSerialPortTest {

    @Test
    public void verifyStandardConfig() {
        verifyConfig(NMEA_0183);
    }

    @Test
    public void verifyCustomConfig() {
        verifyConfig(new SerialPortConfig(9600, 16, 2, SerialPort.EVEN_PARITY));
    }

    private void verifyConfig(final SerialPortConfig conf) {
        conf.accept(env.readable);
        assertEquals("Baud rate", conf.getBaudRate(), env.readable.getBaudRate());
        assertEquals("Number of data bits", conf.getNumDataBits(), env.readable.getNumDataBits());
        assertEquals("Parity", conf.getParity(), env.readable.getParity());
        assertEquals("Number of stop bits", conf.getNumStopBits(), env.readable.getNumStopBits());
    }

    @Test
    public void retryConnection() throws Exception {
        final Mono<FlowableFeatureSet> loopUntilFirstSentence = Discovery.serial(NMEA_0183)
                .connect(env.readable)
                .timeout(Duration.ofMillis(50))
                .retry(10);

        final Flux<Feature> dataStream = loopUntilFirstSentence
                .flatMapMany(it -> Flux.using(() -> it, FlowableFeatureSet::flow, FlowableFeatureSet::close));

        final StepVerifier verifier = prepareVerification(dataStream);

        Thread.sleep(100);
        NMEA_0183.accept(env.writeable);
        pushDataStream();
        verifier.verify(Duration.ofSeconds(2));
    }

    @Test
    public void readWithStandardConfig() throws Exception {
        read(NMEA_0183);
    }

    @Test
    public void readWithCustomConfig() throws Exception {
        read(new SerialPortConfig(9600, 8, 1, SerialPort.NO_PARITY));
    }

    private void read(final SerialPortConfig conf) throws IOException {
        final Flux<Feature> dataStream = Discovery.serial(conf)
                .connect(env.readable)
                .timeout(Duration.ofMillis(1000))
                .flatMapMany(dataset -> Flux.using(() -> dataset, FlowableFeatureSet::flow, FlowableFeatureSet::close));

        final StepVerifier verifier = prepareVerification(dataStream);

        conf.accept(env.writeable);
        pushDataStream();

        verifier.verify(Duration.ofSeconds(2));
    }

    private StepVerifier prepareVerification(final Flux<Feature> dataStream) {
        return StepVerifier.create(dataStream)
                .assertNext(next -> check(next, -6.5056, 53.3613, 61.7, "2011-05-28", "09:27:51Z"))
                .assertNext(next -> check(next, -6.5056, 53.3613, 61.9, "2011-05-28", "09:27:52Z"))
                .expectNoEvent(Duration.ofMillis(100))
                .thenCancel()
                .verifyLater();
    }

    private void pushDataStream() throws IOException {
        Assert.assertTrue("Cannot open port for writing datastream", env.writeable.openPort());
        try (final OutputStream stream = env.writeable.getOutputStream();
             final OutputStreamWriter writer = new OutputStreamWriter(stream, US_ASCII)) {
            writer.write("$GPGGA,092751.000,5321.6802,N,00630.3371,W,1,8,1.03,61.7,M,55.3,M,,*75\r\n");
            writer.write("$GPRMC,092751.000,A,5321.6802,N,00630.3371,W,0.06,31.66,280511,,,A*45\r\n");

            // Push a copy of the first sentence with updated time and altitude
            writer.write("$GPGGA,092752.000,5321.6802,N,00630.3371,W,1,8,1.03,61.9,M,55.3,M,,*78\r\n");
        } finally {
            env.writeable.closePort();
        }
    }
}
