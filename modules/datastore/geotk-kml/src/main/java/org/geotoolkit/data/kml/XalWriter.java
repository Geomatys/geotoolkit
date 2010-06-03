package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.Xal;
import org.geotoolkit.xml.StaxStreamWriter;
import static org.geotoolkit.data.model.XalModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class XalWriter extends StaxStreamWriter {

    public XalWriter(File file){
        this.initSource(file);
    }

    private void initSource(Object o) {
        System.setProperty("javax.xml.stream.XMLOutputFactory", "com.ctc.wstx.stax.WstxOutputFactory");
        try {
            //this.outputFactory = XMLOutputFactory.newInstance();
            this.setOutput(o);
        } catch (IOException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>This method writes a Kml 2.2 document into the file assigned to the KmlWriter.</p>
     *
     * @param xal The Kml object to write.
     */
    public void write(Xal xal) {
        try {

            // FACULTATIF : INDENTATION DE LA SORTIE
            //streamWriter = new IndentingXMLStreamWriter(streamWriter);

            writer.writeStartDocument("UTF-8", "1.0");

            writer.writeStartElement(TAG_XAL);
            writer.setDefaultNamespace(URI_XAL);
            writer.writeDefaultNamespace(URI_XAL);
            /*streamWriter.writeNamespace(PREFIX_ATOM, URI_ATOM);
            streamWriter.writeNamespace(PREFIX_XAL, URI_XAL);
            streamWriter.writeNamespace(PREFIX_XSI, URI_XSI);
            streamWriter.writeAttribute(URI_XSI,
                    "schemaLocation",
                    URI_KML+" C:/Users/w7mainuser/Documents/OGC_SCHEMAS/sld/1.1.0/StyledLayerDescriptor.xsd");
            streamWriter.writeAttribute("version", "0");*/
            this.writeXal(xal);
            writer.writeEndElement();
            writer.writeEndDocument();

            writer.flush();
            writer.close();

        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param xal The Kml object to write.
     * @throws XMLStreamException
     */
    private void writeXal(Xal xal) throws XMLStreamException{
        if (xal.getVersion() != null) writer.writeAttribute(ATT_VERSION, xal.getVersion());
        for(AddressDetails ad : xal.getAddressDetails()){
            this.writeAddressDetails(ad);
        }
    }

    private void writeAddressDetails(AddressDetails addressDetails) throws XMLStreamException{
        writer.writeStartElement(TAG_ADDRESS_DETAILS);
        writer.writeEndElement();
    }
}