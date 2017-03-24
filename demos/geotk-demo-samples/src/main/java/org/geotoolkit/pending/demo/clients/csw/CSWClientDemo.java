
package org.geotoolkit.pending.demo.clients.csw;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.internal.storage.io.IOUtilities;
import org.apache.sis.xml.MarshalContext;
import org.geotoolkit.csw.CatalogServicesClient;
import org.geotoolkit.csw.GetCapabilitiesRequest;
import org.geotoolkit.csw.GetRecordByIdRequest;
import org.geotoolkit.csw.GetRecordsRequest;
import org.geotoolkit.csw.xml.AbstractRecord;
import org.geotoolkit.csw.xml.CSWMarshallerPool;
import org.geotoolkit.csw.xml.GetRecordByIdResponse;
import org.geotoolkit.csw.xml.ResultType;
import org.geotoolkit.csw.xml.v202.Capabilities;
import org.geotoolkit.csw.xml.v202.GetRecordsResponseType;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.ValueConverter;
import org.apache.sis.xml.XML;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.ows.xml.v100.ExceptionReport;
import org.opengis.metadata.Metadata;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CSWClientDemo {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException, IOException, JAXBException {
        Demos.init();

        final MarshallerPool pool = CSWMarshallerPool.getInstance();
        final Unmarshaller um = pool.acquireUnmarshaller();
        final MarshallWarnings warnings = new MarshallWarnings();
        um.setProperty(XML.CONVERTER, warnings);
        
        // build a new CSW client
        final CatalogServicesClient cswServer = new CatalogServicesClient(new URL("http://catalog.data.gov/csw?"), "2.0.2");


        /**
         * make a getCapabilities request
         */
        final GetCapabilitiesRequest getCapa  = cswServer.createGetCapabilities();

        InputStream is = getCapa.getResponseStream();

        // unmarshall the response
        Capabilities capabilities = (Capabilities) um.unmarshal(is);

        // print the title of the server
        System.out.println(capabilities.getServiceIdentification().getTitle());


        /**
         * make a getRecords request
         */
        final GetRecordsRequest getRecords = cswServer.createGetRecords();
        getRecords.setTypeNames("gmd:MD_Metadata");
        getRecords.setConstraintLanguage("CQL");
        getRecords.setConstraintLanguageVersion("1.1.0");
        getRecords.setConstraint("apiso:Title like '%'");
        getRecords.setElementSetName(ElementSetType.FULL);
        is = getRecords.getResponseStream();

        // unmarshall the response
        Object obj = um.unmarshal(is);
        GetRecordsResponseType response;
        
        if (obj instanceof ExceptionReport) {
            System.out.println("Error received:" + obj);
            return;
        } else {
            response = ((JAXBElement<GetRecordsResponseType>) obj).getValue();
        }

        
        // print the number of result matching the request
        System.out.println(response.getSearchResults().getNumberOfRecordsMatched());


        /**
         * retrieve results in dublin core
         */
        getRecords.setResultType(ResultType.RESULTS);

        is = getRecords.getResponseStream();

        obj = um.unmarshal(is);
        
        // unmarshall the response
        if (obj instanceof ExceptionReport) {
            System.out.println("Error received:" + obj);
            return;
        } else {
            response = ((JAXBElement<GetRecordsResponseType>) obj).getValue();
        }

        // print the first result (Dublin core)
        AbstractRecord record = (AbstractRecord) response.getSearchResults().getAny().get(0);
        System.out.println(record);


        /**
         * retrieve results in ISO 19139
         */
        getRecords.setOutputSchema("http://www.isotc211.org/2005/gmd");

        is = getRecords.getResponseStream();

        // unmarshall the response
        obj = um.unmarshal(is);
        
        // unmarshall the response
        if (obj instanceof ExceptionReport) {
            System.out.println("Error received:" + obj);
            return;
        } else {
            response = ((JAXBElement<GetRecordsResponseType>) obj).getValue();
        }

        // print the first result (ISO 19139)
        Metadata meta = (Metadata) response.getSearchResults().getAny().get(0);
        System.out.println(meta);

        final String identifier = meta.getFileIdentifier();


         /**
         * make a getRecordById request
         */
        final GetRecordByIdRequest getRecordById = cswServer.createGetRecordById();
        getRecordById.setOutputSchema("http://www.isotc211.org/2005/gmd");
        getRecordById.setIds(identifier);

        is = getRecordById.getResponseStream();

        // unmarshall the response
        obj = um.unmarshal(is);
        
        // unmarshall the response
        GetRecordByIdResponse responseBi;
        if (obj instanceof ExceptionReport) {
            System.out.println("Error received:" + obj);
            return;
        } else {
            responseBi = ((JAXBElement<GetRecordByIdResponse>) obj).getValue();
        }

        // print the result (same as getRecords first result)
        meta = (Metadata) responseBi.getAny().get(0);
        System.out.println(meta);

        pool.recycle(um);

    }
    
    private static class MarshallWarnings extends ValueConverter {

        // The warnings collected during (un)marshalling.
        private final List<String> messages = new ArrayList<>();

        // Collects the warnings and allows the process to continue.
        @Override
        protected <T> boolean exceptionOccured(final MarshalContext context, final T value, final Class<T> sourceType, final Class<?> targetType, final Exception exception) {
            messages.add(exception.getLocalizedMessage() + " value=[" + value + "] sourceType:" + sourceType + " targetType:" + targetType);
            return true;
        }

        /**
         * @return the messages
         */
        public List<String> getMessages() {
            return messages;
        }

        public boolean isEmpty() {
            return messages.isEmpty();
        }

        @Override
        public URI toURI(final MarshalContext context, String value) throws URISyntaxException {
            if (value != null && !(value = value.trim()).isEmpty()) {
                try {
                    value = IOUtilities.encodeURI(value);
                    if (value.contains("\\")) {
                        value = value.replace("\\", "%5C");
                    }
                    return new URI(value);
                } catch (URISyntaxException e) {
                    if (!exceptionOccured(context, value, String.class, URI.class, e)) {
                        throw e;
                    }
                }
            }
            return null;
        }
    }
}
