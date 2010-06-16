package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAddressDetails implements AddressDetails {

    private final PostalServiceElements postalServiceElements;
    private GenericTypedGrPostal address;
    private AddressLines addressLines;
    private Country country;
    private AdministrativeArea administrativeArea;
    private Locality locality;
    private Thoroughfare thoroughfare;
    private final String addressType;
    private final String currentStatus;
    private final String validFromDate;
    private final String validToDate;
    private final String usage;
    private final GrPostal grPostal;
    private final String addressDetailsKey;

    /**
     * 
     * @param postalServiceElements
     * @param localisation
     * @param addressType
     * @param currentStatus
     * @param validFromDate
     * @param validToDate
     * @param usage
     * @param grPostal
     * @param addressDetailsKey
     * @throws XalException
     */
    public DefaultAddressDetails(PostalServiceElements postalServiceElements, Object localisation,
            String addressType, String currentStatus, String validFromDate, String validToDate,
            String usage, GrPostal grPostal, String addressDetailsKey) throws XalException{
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
        this.usage = usage;
        this.grPostal = grPostal;
        this.addressDetailsKey = addressDetailsKey;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalServiceElements getPostalServiceElements() {return this.postalServiceElements;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getAddress() {return this.address;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AddressLines getAddressLines() {return this.addressLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Country getCountry() {return this.country;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AdministrativeArea getAdministrativeArea() {return this.administrativeArea;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Locality getLocality() {return this.locality;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Thoroughfare getThoroughfare() {return this.thoroughfare;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getAddressType() {return this.addressType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCurrentStatus() {return this.currentStatus;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getValidFromDate() {return this.validFromDate;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getValidToDate() {return this.validToDate;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getUsage() {return this.usage;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getAddressDetailsKey() {return this.addressDetailsKey;}

}
