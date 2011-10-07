
package org.geotoolkit.pending.demo.clients.csw;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.csw.CatalogServicesServer;
import org.geotoolkit.csw.GetCapabilitiesRequest;
import org.geotoolkit.csw.GetRecordByIdRequest;
import org.geotoolkit.csw.GetRecordsRequest;
import org.geotoolkit.csw.xml.AbstractRecord;
import org.geotoolkit.csw.xml.CSWMarshallerPool;
import org.geotoolkit.csw.xml.GetRecordByIdResponse;
import org.geotoolkit.csw.xml.ResultType;
import org.geotoolkit.csw.xml.v202.Capabilities;
import org.geotoolkit.csw.xml.v202.GetRecordsResponseType;
import org.geotoolkit.xml.MarshallerPool;
import org.opengis.metadata.Metadata;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CSWClientDemo {
    
    public static void main(String[] args) throws MalformedURLException, URISyntaxException, IOException, JAXBException {
        final MarshallerPool pool = CSWMarshallerPool.getInstance();
        Unmarshaller um = null;
        
        try {
            um = pool.acquireUnmarshaller();
            
            // build a new CSW client
            final CatalogServicesServer cswServer = new CatalogServicesServer(new URL("http://demo.geomatys.com/mdweb-demo/WS/csw/default?"), "2.0.2");

            
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
            getRecords.setConstraint("Title like '%'");
            is = getRecords.getResponseStream();
            
            // unmarshall the response
            GetRecordsResponseType response = ((JAXBElement<GetRecordsResponseType>) um.unmarshal(is)).getValue();
            
            // print the number of result matching the request
            System.out.println(response.getSearchResults().getNumberOfRecordsMatched());
            
            
            /**
             * retrieve results in dublin core
             */
            getRecords.setResultType(ResultType.RESULTS);
            
            is = getRecords.getResponseStream();
            
            // unmarshall the response
            response = ((JAXBElement<GetRecordsResponseType>) um.unmarshal(is)).getValue();
            
            // print the first result (Dublin core)
            AbstractRecord record = response.getSearchResults().getAbstractRecord().get(0);
            System.out.println(record);
            
            
            /**
             * retrieve results in ISO 19139
             */
            getRecords.setOutputSchema("http://www.isotc211.org/2005/gmd");
            
            is = getRecords.getResponseStream();
            
            // unmarshall the response
            response = ((JAXBElement<GetRecordsResponseType>) um.unmarshal(is)).getValue();
            
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
            GetRecordByIdResponse responseBi = ((JAXBElement<GetRecordByIdResponse>) um.unmarshal(is)).getValue();
            
            // print the result (same as getRecords first result)
            meta = (Metadata) responseBi.getAny().get(0);
            System.out.println(meta);
            
        } finally {
            if (um != null) {
                pool.release(um);
            }
        }
        

    }
}
