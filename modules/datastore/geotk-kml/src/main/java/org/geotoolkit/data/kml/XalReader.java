package org.geotoolkit.data.kml;

import com.ctc.wstx.stax.WstxInputFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.geotoolkit.data.model.XalFactory;
import org.geotoolkit.data.model.XalFactoryDefault;
import org.geotoolkit.data.model.kml.KmlException;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.AddressIdentifier;
import org.geotoolkit.data.model.xal.AddressLines;
import org.geotoolkit.data.model.xal.AdministrativeArea;
import org.geotoolkit.data.model.xal.Country;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.Locality;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.SortingCode;
import org.geotoolkit.data.model.xal.Thoroughfare;
import org.geotoolkit.data.model.xal.Xal;
import org.geotoolkit.data.model.xal.XalException;
import org.geotoolkit.xml.StaxStreamReader;
import static org.geotoolkit.data.model.XalModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class XalReader extends StaxStreamReader{

    private XMLInputFactory inputFactory;//A SUPPRIMER
    private Xal root;
    private XalFactory xalFactory;

    public XalReader(File file) {
        super();
        this.initSource(file);
    }
    
    public XalReader() {
        super();
        this.xalFactory = new XalFactoryDefault();
    }

    public void setReader(XMLStreamReader reader){this.reader = reader;}

    public XMLStreamReader getReader(){return this.reader;}

    private void initSource(Object o) {
        // Choice of the StAX implementation of Java 6 interface
        System.setProperty("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");
        System.setProperty("javax.xml.stream.XMLEventFactory", "com.ctc.wstx.stax.WstxEventFactory");

        // Factories
        //XMLInputFactory factory = new WstxInputFactory();// Implementation explicitly named
        this.inputFactory = XMLInputFactory.newInstance();// Transparent implementation based on the previous choice.
        //this.inputFactory = new WstxInputFactory();
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        ((WstxInputFactory) inputFactory).configureForSpeed();

        if (this.inputFactory.isPropertySupported("javax.xml.stream.isValidating")) {
            this.inputFactory.setProperty("javax.xml.stream.isValidating", Boolean.TRUE);
//            System.out.println("Validation active : " + this.inputFactory.getProperty("javax.xml.stream.isValidating"));
        }

        // Errors displaying
        this.inputFactory.setXMLReporter(new XMLReporter() {

            @Override
            public void report(String message, String typeErreur, Object source, javax.xml.stream.Location location) throws XMLStreamException {
//                System.out.println("Erreur de type : " + typeErreur + ", message : " + message);
            }
        });

        try {
            this.setInput(o);
        } catch (IOException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.xalFactory = new XalFactoryDefault();
    }

    /**
     * <p>This method reads the Kml document assigned to the KmlReader.</p>
     *
     * @return The Kml object mapping the document.
     */
    public Xal read() {

        try {

            while (reader.hasNext()) {

                switch (reader.next()) {

                    case XMLStreamConstants.START_ELEMENT:
                        final String eName = reader.getLocalName();
                        final String eUri = reader.getNamespaceURI();

                        if (URI_XAL.equals(eUri)) {
                            if (TAG_XAL.equals(eName)) {
                                this.root = this.readXal();
                            }
                        }
                        break;
                }
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(KmlReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XalException ex) {
            System.out.println("XAL EXCEPTION : " + ex.getMessage());
        }
        return this.root;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws KmlException
     */
    private Xal readXal() throws XMLStreamException, XalException {
        String version = reader.getAttributeValue(null, ATT_VERSION);
        List<AddressDetails> addressDetails = new ArrayList<AddressDetails>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XAL.equals(eUri)) {
                        if (TAG_ADDRESS_DETAILS.equals(eName)) {
                            addressDetails.add(this.readAddressDetails());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_XAL.equals(reader.getLocalName()) && URI_XAL.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return this.xalFactory.createXal(addressDetails, version);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws XalException
     */
    public AddressDetails readAddressDetails() throws XMLStreamException, XalException{
        PostalServiceElements postalServiceElements = null;
        Object localisation = null;
        String addressType = reader.getAttributeValue(null, ATT_ADDRESS_TYPE);
        String currentStatus = reader.getAttributeValue(null, ATT_CURRENT_STATUS);
        String validFromDate = reader.getAttributeValue(null, ATT_VALID_FROM_DATE);
        String validToDate = reader.getAttributeValue(null, ATT_VALID_TO_DATE);
        String usage = reader.getAttributeValue(null, ATT_USAGE);
        GrPostal grPostal = this.readGrPostal();
        String addressDetailsKey = reader.getAttributeValue(null, ATT_ADDRESS_DETAILS_KEY);

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XAL.equals(eUri)) {
                        if (TAG_POSTAL_SERVICE_ELEMENTS.equals(eName)) {
                            postalServiceElements = this.readPostalServiceElements();
                        } else if (TAG_ADDRESS.equals(eName)){
                            localisation = this.readGenericTypedGrPostal();
                        } else if (TAG_ADDRESS_LINES.equals(eName)){
                            localisation = this.readAddressLines();
                        } else if (TAG_COUNTRY.equals(eName)){
                            localisation = this.readCountry();
                        } else if (TAG_ADMINISTRATIVE_AREA.equals(eName)){
                            localisation = this.readAdministrativeArea();
                        } else if (TAG_LOCALITY.equals(eName)){
                            localisation = this.readLocality();
                        } else if (TAG_THOROUGHFARE.equals(eName)){
                            localisation = this.readThoroughfare();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ADDRESS_DETAILS.equals(reader.getLocalName()) && URI_XAL.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return this.xalFactory.createAddressDetails(postalServiceElements, localisation,
                addressType, currentStatus, validFromDate, validToDate, usage, grPostal, addressDetailsKey);
    }

    private PostalServiceElements readPostalServiceElements() throws XMLStreamException{
        List<AddressIdentifier> addressIdentifiers = new ArrayList<AddressIdentifier>();
        GenericTypedGrPostal endorsementLineCode = null;
        GenericTypedGrPostal keyLineCode = null;
        GenericTypedGrPostal barCode = null;
        SortingCode sortingCode = null;
        GenericTypedGrPostal addressLatitude = null;
        GenericTypedGrPostal addressLatitudeDirection = null;
        GenericTypedGrPostal addressLongitude = null;
        GenericTypedGrPostal addressLongitudeDirection = null;
        List<GenericTypedGrPostal> supplementaryPostalServiceData = new ArrayList<GenericTypedGrPostal>();
        String type = reader.getAttributeValue(null, ATT_TYPE);

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XAL.equals(eUri)) {
                        if (TAG_ADDRESS_IDENTIFIER.equals(eName)) {
                            addressIdentifiers.add(this.readAddressIdentifier());
                        } else if (TAG_ENDORSEMENT_LINE_CODE.equals(eName)){
                            endorsementLineCode = this.readGenericTypedGrPostal();
                        } else if (TAG_KEY_LINE_CODE.equals(eName)){
                            keyLineCode = this.readGenericTypedGrPostal();
                        } else if (TAG_BARCODE.equals(eName)){
                            barCode = this.readGenericTypedGrPostal();
                        } else if (TAG_SORTING_CODE.equals(eName)){
                            sortingCode = this.readSortingCode();
                        } else if (TAG_ADDRESS_LATITUDE.equals(eName)){
                            addressLatitude = this.readGenericTypedGrPostal();
                        } else if (TAG_ADDRESS_LATITUDE_DIRECTION.equals(eName)){
                            addressLatitudeDirection = this.readGenericTypedGrPostal();
                        } else if (TAG_ADDRESS_LONGITUDE.equals(eName)){
                            addressLongitude = this.readGenericTypedGrPostal();
                        } else if (TAG_ADDRESS_LONGITUDE_DIRECTION.equals(eName)){
                            addressLongitudeDirection = this.readGenericTypedGrPostal();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_POSTAL_SERVICE_ELEMENTS.equals(reader.getLocalName()) && URI_XAL.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return this.xalFactory.createPostalServiceElements(addressIdentifiers, endorsementLineCode,
                keyLineCode, barCode, sortingCode, addressLatitude, addressLatitudeDirection,
                addressLongitude, addressLongitudeDirection, supplementaryPostalServiceData, type);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private GenericTypedGrPostal readGenericTypedGrPostal() throws XMLStreamException{
        String type = reader.getAttributeValue(null, ATT_TYPE);
        GrPostal grPostal = this.readGrPostal();
        String content = reader.getElementText();
        return this.xalFactory.createGenericTypedGrPostal(type, grPostal, content);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private SortingCode readSortingCode() throws XMLStreamException{
        String type = reader.getAttributeValue(null, ATT_TYPE);
        GrPostal grPostal = this.readGrPostal();
        return this.xalFactory.createSortingCode(type, grPostal);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private AddressIdentifier readAddressIdentifier() throws XMLStreamException{
        String content = this.reader.getElementText();
        String identifierType = reader.getAttributeValue(null, ATT_IDENTIFIER_TYPE);
        String type = reader.getAttributeValue(null, ATT_TYPE);
        GrPostal grPostal = this.readGrPostal();
        return this.xalFactory.createAddressIdentifier(content, identifierType, type, grPostal);
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    private AddressLines readAddressLines() throws XMLStreamException{
        List<GenericTypedGrPostal> addressLines = new ArrayList<GenericTypedGrPostal>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XAL.equals(eUri)) {
                        if (TAG_ADDRESS_LINE.equals(eName)) {
                            addressLines.add(this.readGenericTypedGrPostal());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ADDRESS_LINES.equals(reader.getLocalName()) && URI_XAL.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return this.xalFactory.createAddressLines(addressLines);
    }

    /**
     *
     * @return
     */
    public GrPostal readGrPostal(){
        return this.xalFactory.createGrPostal(reader.getAttributeValue(null, ATT_CODE));
    }

    private Country readCountry(){return null;}

    private AdministrativeArea readAdministrativeArea(){return null;}

    private Locality readLocality(){return null;}

    private Thoroughfare readThoroughfare(){return null;}

}
