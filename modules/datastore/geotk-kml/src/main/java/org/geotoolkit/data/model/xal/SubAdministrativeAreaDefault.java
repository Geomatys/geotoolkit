package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class SubAdministrativeAreaDefault implements SubAdministrativeArea {
private List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> subAdministrativeAreaNames;
    private Locality locality;
    private PostOffice postOffice;
    private PostalCode postalCode;
    private String type;
    private String usageType;
    private String indicator;

    /**
     * 
     * @param addressLines
     * @param subAdministrativeAreaNames
     * @param localisation
     * @param type
     * @param usageType
     * @param indicator
     * @throws XalException
     */
    public SubAdministrativeAreaDefault(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> subAdministrativeAreaNames,
            Object localisation, String type, String usageType, String indicator) throws XalException{
        this.addressLines = addressLines;
        this.subAdministrativeAreaNames = subAdministrativeAreaNames;
        if (localisation instanceof Locality){
            this.locality = (Locality) localisation;
        } else if (localisation instanceof PostOffice){
            this.postOffice = (PostOffice) localisation;
        } else if (localisation instanceof PostalCode){
            this.postalCode = (PostalCode) localisation;
        } else if (localisation != null){
            throw new XalException("This kind of type is not allowed here.");
        }
        this.type = type;
        this.usageType = usageType;
        this.indicator = indicator;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getSubAdministrativeAreaNames() {return this.subAdministrativeAreaNames;}

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
    public PostOffice getPostOffice() {return this.postOffice;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode getPostalCode() {return this.postalCode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getUsageType() {return this.usageType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getIndicator() {return this.indicator;}

}
