package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.AddressLines;
import org.geotoolkit.data.model.xal.AdministrativeArea;
import org.geotoolkit.data.model.xal.Country;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.Locality;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.Thoroughfare;
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

    /**
     *
     * @param addressDetails
     * @throws XMLStreamException
     */
    private void writeAddressDetails(AddressDetails addressDetails) throws XMLStreamException{
        writer.writeStartElement(TAG_ADDRESS_DETAILS);
        if (addressDetails.getPostalServiceElements() != null){
            this.writePostalServiceElements(addressDetails.getPostalServiceElements());
        }
        if (addressDetails.getAddress() != null){
            this.writeAddress(addressDetails.getAddress());
        }
         if (addressDetails.getAddressLines() != null){
            this.writeAddressLines(addressDetails.getAddressLines());
        }
        if (addressDetails.getCountry() != null){
            this.writeCountry(addressDetails.getCountry());
        }
        if (addressDetails.getAdministrativeArea() != null){
            this.writeAdministrativeArea(addressDetails.getAdministrativeArea());
        }
        if (addressDetails.getLocality() != null){
            this.writeLocality(addressDetails.getLocality());
        }
        if (addressDetails.getThoroughfare() != null){
            this.writeThoroughfare(addressDetails.getThoroughfare());
        }
        if (addressDetails.getAddressType() != null){
            writer.writeAttribute(ATT_ADDRESS_TYPE, addressDetails.getAddressType());
        }
        if (addressDetails.getCurrentStatus() != null){
            writer.writeAttribute(ATT_CURRENT_STATUS, addressDetails.getCurrentStatus());
        }
        if (addressDetails.getValidFromDate() != null){
            writer.writeAttribute(ATT_VALID_FROM_DATE, addressDetails.getValidFromDate());
        }
        if (addressDetails.getValidToDate() != null){
            writer.writeAttribute(ATT_VALID_TO_DATE, addressDetails.getValidToDate());
        }
        if (addressDetails.getUsage() != null){
            writer.writeAttribute(ATT_USAGE, addressDetails.getUsage());
        }
        if (addressDetails.getGrPostal() != null){
            this.writeGrPostal(addressDetails.getGrPostal());
        }
        if (addressDetails.getAddressDetailsKey() != null){
            writer.writeAttribute(ATT_ADDRESS_DETAILS_KEY, addressDetails.getAddressDetailsKey());
        }
        writer.writeEndElement();
    }

    private void writePostalServiceElements(PostalServiceElements postalServiceElements) throws XMLStreamException{
        writer.writeStartElement(TAG_POSTAL_SERVICE_ELEMENTS);

        writer.writeEndElement();
    }

    private void writeAddress(GenericTypedGrPostal address) throws XMLStreamException{
        writer.writeStartElement(TAG_ADDRESS);
        this.writeGenericTypedGrPostal(address);
        writer.writeEndElement();
    }

    private void writeAddressLines(AddressLines addressLines) throws XMLStreamException{
        writer.writeStartElement(TAG_ADDRESS_LINES);
        for (GenericTypedGrPostal addressLine : addressLines.getAddressLines()){
            this.writeAddressLine(addressLine);
        }
        writer.writeEndElement();
    }

    private void writeAddressLine(GenericTypedGrPostal addressline) throws XMLStreamException{
        writer.writeStartElement(TAG_ADDRESS_LINE);
        this.writeGenericTypedGrPostal(addressline);
        writer.writeEndElement();
    }

    private void writeCountry(Country country) throws XMLStreamException{
        writer.writeStartElement(TAG_COUNTRY);
        writer.writeEndElement();
    }

    private void writeAdministrativeArea(AdministrativeArea administrativeArea) throws XMLStreamException{
        writer.writeStartElement(TAG_ADMINISTRATIVE_AREA);
        writer.writeEndElement();
    }

    private void writeLocality(Locality locality) throws XMLStreamException{
        writer.writeStartElement(TAG_LOCALITY);
        writer.writeEndElement();
    }

    private void writeThoroughfare(Thoroughfare thoroughfare) throws XMLStreamException{
        writer.writeStartElement(TAG_THOROUGHFARE);
        writer.writeEndElement();
    }

    private void writeGenericTypedGrPostal(GenericTypedGrPostal generic) throws XMLStreamException{
        if (generic.getType() != null){
            writer.writeAttribute(ATT_TYPE, generic.getType());
        }
        if (generic.getGrPostal() != null){
            this.writeGrPostal(generic.getGrPostal());
        }
        if (generic.getContent() != null){
            writer.writeCharacters(generic.getContent());
        }
    }

    private void writeGrPostal(GrPostal grPostal) throws XMLStreamException{
        if (grPostal.getCode() != null){
            writer.writeAttribute(ATT_CODE, grPostal.getCode());
        }
    }


}