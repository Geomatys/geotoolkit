package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class PostalRouteDefault implements PostalRoute {

    private final List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> postalRouteNames;
    private PostalRouteNumber postalRouteNumber;
    private final PostBox postBox;
    private final String type;

    /**
     * 
     * @param addressLines
     * @param localisation
     * @param postBox
     * @param type
     * @throws XalException
     */
    public PostalRouteDefault(List<GenericTypedGrPostal> addressLines,
            Object localisation, PostBox postBox, String type) throws XalException{
        this.addressLines = addressLines;
        if (localisation instanceof List){
            try{
                this.postalRouteNames = (List<GenericTypedGrPostal>) localisation;
            } catch (ClassCastException e){
                throw new XalException("Cast error. List<GenericTypedGrPostal> requiered.");
            }
        } else if (localisation instanceof PostalRouteNumber){
            this.postalRouteNumber = (PostalRouteNumber) localisation;
        } else if (localisation != null){
            throw new XalException("This kind of localisation is not allowed here.");
        }
        this.postBox = postBox;
        this.type = type;
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
    public List<GenericTypedGrPostal> getPostalRouteNames() {return this.postalRouteNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalRouteNumber getPostalRouteNumber() {return this.postalRouteNumber;}

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
    public String getType() {return this.type;}

}
