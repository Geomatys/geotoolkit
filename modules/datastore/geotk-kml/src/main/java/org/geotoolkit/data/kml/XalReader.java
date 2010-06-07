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
import org.geotoolkit.data.model.xal.CountryNameCode;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.Locality;
import org.geotoolkit.data.model.xal.PostOffice;
import org.geotoolkit.data.model.xal.PostalCode;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.SortingCode;
import org.geotoolkit.data.model.xal.SubAdministrativeArea;
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

    private Xal root;
    private static final XalFactory xalFactory = new XalFactoryDefault();
    
    public XalReader() {
        super();
    }

    public void setReader(XMLStreamReader reader){this.reader = reader;}

    public XMLStreamReader getReader(){return this.reader;}

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

        return XalReader.xalFactory.createXal(addressDetails, version);
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

        return XalReader.xalFactory.createAddressDetails(postalServiceElements, localisation,
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

        return XalReader.xalFactory.createPostalServiceElements(addressIdentifiers, endorsementLineCode,
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
        return XalReader.xalFactory.createGenericTypedGrPostal(type, grPostal, content);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    private SortingCode readSortingCode() throws XMLStreamException{
        String type = reader.getAttributeValue(null, ATT_TYPE);
        GrPostal grPostal = this.readGrPostal();
        return XalReader.xalFactory.createSortingCode(type, grPostal);
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
        return XalReader.xalFactory.createAddressIdentifier(content, identifierType, type, grPostal);
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

        return XalReader.xalFactory.createAddressLines(addressLines);
    }

    /**
     *
     * @return
     */
    public GrPostal readGrPostal(){
        return XalReader.xalFactory.createGrPostal(reader.getAttributeValue(null, ATT_CODE));
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     * @throws XalException
     */
    private Country readCountry() throws XMLStreamException, XalException{
        List<GenericTypedGrPostal> addressLines = new ArrayList<GenericTypedGrPostal>();
        List<CountryNameCode> countryNameCodes = new ArrayList<CountryNameCode>();
        List<GenericTypedGrPostal> countryNames = new ArrayList<GenericTypedGrPostal>();
        Object localisation = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XAL.equals(eUri)) {
                        if (TAG_ADDRESS_LINE.equals(eName)) {
                            addressLines.add(this.readGenericTypedGrPostal());
                        } else if (TAG_COUNTRY_NAME_CODE.equals(eName)){
                            countryNameCodes.add(this.readCountryNameCode());
                        } else if (TAG_COUNTRY_NAME.equals(eName)){
                            countryNames.add(this.readGenericTypedGrPostal());
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
                    if (TAG_COUNTRY.equals(reader.getLocalName()) && URI_XAL.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return XalReader.xalFactory.createCountry(addressLines, countryNameCodes, countryNames, localisation);
    }

    private CountryNameCode readCountryNameCode() throws XMLStreamException{
        String scheme = reader.getAttributeValue(null, ATT_SCHEME);
        GrPostal grPostal = this.readGrPostal();
        String content = reader.getElementText();
        return XalReader.xalFactory.createCountryNameCode(scheme, grPostal, content);
    }

    private AdministrativeArea readAdministrativeArea() throws XMLStreamException, XalException{
        List<GenericTypedGrPostal> addressLines = new ArrayList<GenericTypedGrPostal>();
        List<GenericTypedGrPostal> administrativeAreaNames = new ArrayList<GenericTypedGrPostal>();
        SubAdministrativeArea subAdministrativeArea = null;
        Object localisation = null;
        String type = reader.getAttributeValue(null, ATT_TYPE);
        String usageType = reader.getAttributeValue(null, ATT_USAGE_TYPE);
        String indicator = reader.getAttributeValue(null, ATT_INDICATOR);

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XAL.equals(eUri)) {
                        if (TAG_ADDRESS_LINE.equals(eName)) {
                            addressLines.add(this.readGenericTypedGrPostal());
                        } else if (TAG_ADMINISTRATIVE_AREA_NAME.equals(eName)){
                            administrativeAreaNames.add(this.readGenericTypedGrPostal());
                        } else if (TAG_SUB_ADMINISTRATIVE_AREA.equals(eName)){
                            subAdministrativeArea = this.readSubAdministrativeArea();
                        } else if (TAG_LOCALITY.equals(eName)){
                            localisation = this.readLocality();
                        } else if (TAG_POST_OFFICE.equals(eName)){
                            localisation = this.readPostOffice();
                        } else if (TAG_POSTAL_CODE.equals(eName)){
                            localisation = this.readPostalCode();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_ADMINISTRATIVE_AREA.equals(reader.getLocalName()) && URI_XAL.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return XalReader.xalFactory.createAdministrativeArea(addressLines, administrativeAreaNames,
                subAdministrativeArea, localisation, type, usageType, indicator);
    }

    private SubAdministrativeArea readSubAdministrativeArea() throws XMLStreamException, XalException{
        List<GenericTypedGrPostal> addressLines = new ArrayList<GenericTypedGrPostal>();
        List<GenericTypedGrPostal> subAdministrativeAreaNames = new ArrayList<GenericTypedGrPostal>();
        Object localisation = null;
        String type = reader.getAttributeValue(null, ATT_TYPE);
        String usageType = reader.getAttributeValue(null, ATT_USAGE_TYPE);
        String indicator = reader.getAttributeValue(null, ATT_INDICATOR);

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XAL.equals(eUri)) {
                        if (TAG_ADDRESS_LINE.equals(eName)) {
                            addressLines.add(this.readGenericTypedGrPostal());
                        } else if (TAG_SUB_ADMINISTRATIVE_AREA_NAME.equals(eName)){
                            subAdministrativeAreaNames.add(this.readGenericTypedGrPostal());
                        } else if (TAG_LOCALITY.equals(eName)){
                            localisation = this.readLocality();
                        } else if (TAG_POST_OFFICE.equals(eName)){
                            localisation = this.readPostOffice();
                        } else if (TAG_POSTAL_CODE.equals(eName)){
                            localisation = this.readPostalCode();
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_SUB_ADMINISTRATIVE_AREA.equals(reader.getLocalName()) && URI_XAL.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }
        return XalReader.xalFactory.createSubAdministrativeArea(addressLines, subAdministrativeAreaNames,
                localisation, type, usageType, indicator);
    }

    private PostOffice readPostOffice(){
        return null;
    }

    private PostalCode readPostalCode(){
        return null;
    }

    private Locality readLocality(){return null;}

    private Thoroughfare readThoroughfare(){return null;}

}
