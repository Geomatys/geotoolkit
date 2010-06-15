package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class LocalityDefault implements Locality {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<GenericTypedGrPostal> localityNames;
    private PostBox postBox;
    private LargeMailUser largeMailUser;
    private PostOffice postOffice;
    private PostalRoute postalRoute;
    private final Thoroughfare thoroughfare;
    private final Premise premise;
    private final DependentLocality dependentLocality;
    private final PostalCode postalCode;
    private final String type;
    private final String usageType;
    private final String indicator;

    /**
     * 
     * @param addressLines
     * @param localityNames
     * @param postal
     * @param thoroughfare
     * @param premise
     * @param dependentLocality
     * @param postalCode
     * @param type
     * @param usageType
     * @param indicator
     * @throws XalException
     */
    public LocalityDefault(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> localityNames,
            Object postal,
            Thoroughfare thoroughfare, Premise premise, DependentLocality dependentLocality, PostalCode postalCode,
            String type, String usageType, String indicator) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.localityNames = (localityNames == null) ? EMPTY_LIST : localityNames;
        if (postal instanceof PostBox){
            this.postBox = (PostBox) postal;
        } else if (postal instanceof LargeMailUser){
            this.largeMailUser = (LargeMailUser) postal;
        } else if (postal instanceof PostOffice){
            this.postOffice = (PostOffice) postal;
        } else if (postal instanceof PostalRoute){
            this.postalRoute = (PostalRoute) postal;
        } else if (postal != null){
            throw new XalException("This kind of type is not allowed here.");
        }
        this.thoroughfare = thoroughfare;
        this.premise = premise;
        this.dependentLocality = dependentLocality;
        this.postalCode = postalCode;
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
    public List<GenericTypedGrPostal> getLocalityNames() {return this.localityNames;}

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
    public LargeMailUser getLargeMailUser() {return this.largeMailUser;}

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
    public PostalRoute getPostalRoute() {return this.postalRoute;}

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
    public Premise getPremise() {return this.premise;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentLocality getDependentLocality() {return this.dependentLocality;}

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
