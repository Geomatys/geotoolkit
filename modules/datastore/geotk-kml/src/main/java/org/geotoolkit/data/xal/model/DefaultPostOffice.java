package org.geotoolkit.data.xal.model;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPostOffice implements PostOffice {

    private final List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> postOfficeNames;
    private PostOfficeNumber postOfficeNumber;
    private final PostalRoute postalRoute;
    private final PostBox postBox;
    private final PostalCode postalCode;
    private final String type;
    private final String indicator;

    /**
     *
     * @param addressLines
     * @param localisation
     * @param postalRoute
     * @param postBox
     * @param postalCode
     * @param type
     * @param indicator
     * @throws XalException
     */
    public DefaultPostOffice(List<GenericTypedGrPostal> addressLines, Object localisation,
            PostalRoute postalRoute, PostBox postBox, PostalCode postalCode, String type, String indicator) throws XalException{

        this.addressLines = addressLines;
        if (localisation instanceof List){
            try{
                this.postOfficeNames = (List<GenericTypedGrPostal>) localisation;
            } catch (ClassCastException e){
                throw new XalException("Cast error. List<GenericTypedGrPostal> requiered.");
            }
        } else if (localisation instanceof PostOfficeNumber){
            this.postOfficeNumber = (PostOfficeNumber) localisation;
        } else if (localisation != null) {
            throw new XalException("This kind of localisation is not allowed here."+this.getClass()+localisation);
        }
        this.postalRoute = postalRoute;
        this.postBox = postBox;
        this.postalCode = postalCode;
        this.type = type;
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
    public List<GenericTypedGrPostal> getPostOfficeNames() {return this.postOfficeNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostOfficeNumber getPostOfficeNumber() {return this.postOfficeNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalRoute getPostalRoute() {return this.postalRoute;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBox getPostBox() {return this.postBox;}

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
    public String getIndicator() {return this.indicator;}

}
