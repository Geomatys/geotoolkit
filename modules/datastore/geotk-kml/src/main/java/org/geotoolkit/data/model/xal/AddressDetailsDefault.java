package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class AddressDetailsDefault implements AddressDetails {

    private PostalServiceElements postalServiceElements;
    private GenericTypedGrPostal address;
    private AddressLines addressLines;
    private Country country;
    private AdministrativeArea administrativeArea;
    private Locality locality;
    private Thoroughfare thoroughfare;
    private String addressType;
    private String currentStatus;
    private String validFromDate;
    private String validToDate;
    private String usage;
    private GrPostal grPostal;
    private String addressDetailsKey;

    public AddressDetailsDefault(PostalServiceElements postalServiceElements, Object localisation,
            String addressType, String currentStatus, String validFromDate, String validToDate,
            String usage, GrPostal grPostal, String AddressDetailsKey) throws XalException{
        this.postalServiceElements = postalServiceElements;
        if (localisation instanceof GenericTypedGrPostal){
            this.address = (GenericTypedGrPostal) localisation;
        } else if (localisation instanceof AddressLines){
            this.addressLines = (AddressLines) localisation;
        } else if (localisation instanceof Country){
            this.country = (Country) localisation;
        } else if (localisation instanceof AdministrativeArea){
            this.administrativeArea = (AdministrativeArea) localisation;
        } else if (localisation instanceof Locality){
            this.locality = (Locality) localisation;
        } else if (localisation instanceof Thoroughfare){
            this.thoroughfare = ((Thoroughfare) localisation);
        } else if (localisation !=  null) {
            throw new XalException("This kind of localisation is not allowed.");
        }

        this.addressType = addressType;
        this.currentStatus = currentStatus;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
        this.usage = this.usage;
        this.grPostal = grPostal;
        this.addressDetailsKey = addressDetailsKey;
    }

    @Override
    public PostalServiceElements getPostalServiceElements() {return this.postalServiceElements;}

    @Override
    public GenericTypedGrPostal getAddress() {return this.address;}

    @Override
    public AddressLines getAddressLines() {return this.addressLines;}

    @Override
    public Country getCountry() {return this.country;}

    @Override
    public AdministrativeArea getAdministrativeArea() {return this.administrativeArea;}

    @Override
    public Locality getLocality() {return this.locality;}

    @Override
    public Thoroughfare getThoroughfare() {return this.thoroughfare;}

    @Override
    public String getAddressType() {return this.addressType;}

    @Override
    public String getCurrentStatus() {return this.currentStatus;}

    @Override
    public String getValidFromDate() {return this.validFromDate;}

    @Override
    public String getValidToDate() {return this.validToDate;}

    @Override
    public String getUsage() {return this.usage;}

    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

    @Override
    public String getAddressDetailsKey() {return this.addressDetailsKey;}

}
