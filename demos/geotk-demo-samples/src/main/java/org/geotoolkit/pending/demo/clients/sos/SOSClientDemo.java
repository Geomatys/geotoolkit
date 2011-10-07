
package org.geotoolkit.pending.demo.clients.sos;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.observation.xml.v100.ObservationCollectionType;
import org.geotoolkit.ows.xml.v110.Operation;
import org.geotoolkit.ows.xml.v110.ValueType;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.sos.DescribeSensorRequest;
import org.geotoolkit.sos.GetCapabilitiesRequest;
import org.geotoolkit.sos.GetObservationRequest;
import org.geotoolkit.sos.SensorObservationServiceServer;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.geotoolkit.sos.xml.v100.Capabilities;
import org.geotoolkit.sos.xml.v100.GetObservation.FeatureOfInterest;
import org.geotoolkit.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SOSClientDemo {
    
    public static void main(String[] args) throws MalformedURLException, URISyntaxException, IOException, JAXBException {
        final MarshallerPool pool = SOSMarshallerPool.getInstance();
        Unmarshaller um = null;

        try {
            um = pool.acquireUnmarshaller();

            // build a new SOS client
            final SensorObservationServiceServer sosServer = new SensorObservationServiceServer(new URL("http://test.geomatys.com/swe_TS/WS/sos?"), "1.0.0");
            
            /**
             * make a getCapabilities request
             */
            final GetCapabilitiesRequest getCapa  = sosServer.createGetCapabilities();

            InputStream is = getCapa.getResponseStream();

            // unmarshall the response
            Capabilities capabilities = (Capabilities) um.unmarshal(is);
            
            // print the title of the server
            System.out.println(capabilities.getServiceIdentification().getTitle());
            
            // extract a sensorML identifier and outputFormat to make a describeSensor request
            Operation describeSensorOperation = capabilities.getOperationsMetadata().getOperation("DescribeSensor");
            
            String sensorID     = ((ValueType) describeSensorOperation.getParameter("procedure").getAllowedValues().getValueOrRange().get(0)).getValue();
            String outputFormat = ((ValueType) describeSensorOperation.getParameter("outputFormat").getAllowedValues().getValueOrRange().get(0)).getValue();
            
            // extract a all the parameters necessary to make a getObservation request
            Operation getObservationOperation = capabilities.getOperationsMetadata().getOperation("GetObservation");
            
            String offering          = ((ValueType) getObservationOperation.getParameter("offering").getAllowedValues().getValueOrRange().get(0)).getValue();
            String responseFormat    = ((ValueType) getObservationOperation.getParameter("responseFormat").getAllowedValues().getValueOrRange().get(0)).getValue();
            String phenomenon        = ((ValueType) getObservationOperation.getParameter("observedProperty").getAllowedValues().getValueOrRange().get(0)).getValue();
            String procedure         = ((ValueType) getObservationOperation.getParameter("procedure").getAllowedValues().getValueOrRange().get(0)).getValue();
            String featureOfInterest = ((ValueType) getObservationOperation.getParameter("featureOfInterest").getAllowedValues().getValueOrRange().get(0)).getValue();
            
            /**
             * make a DescribeSensor request
             */
            final DescribeSensorRequest descSensor = sosServer.createDescribeSensor();
            descSensor.setSensorId(sensorID);
            descSensor.setOutputFormat(outputFormat);
            
            is = descSensor.getResponseStream();
            
            // unmarshall the response
            AbstractSensorML sensorMLResponse = (AbstractSensorML) um.unmarshal(is);
            
            System.out.println(sensorMLResponse);
            
            /**
             * make a GetObservation request
             */
            final GetObservationRequest getObs = sosServer.createGetObservation();
            getObs.setOffering(offering);
            getObs.setObservedProperties(phenomenon);
            getObs.setProcedures(procedure);
            getObs.setResponseFormat(responseFormat);
            getObs.setFeatureOfInterest(new FeatureOfInterest(Arrays.asList(featureOfInterest)));
            
            is = getObs.getResponseStream();
            
            // unmarshall the response
            ObservationCollectionType getObsResponse = (ObservationCollectionType) um.unmarshal(is);
            
            System.out.println(getObsResponse);
            
            
            
        } finally {
            if (um != null) {
                pool.release(um);
            }
        }
    }
}
