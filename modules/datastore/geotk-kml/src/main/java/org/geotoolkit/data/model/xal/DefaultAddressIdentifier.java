package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAddressIdentifier implements AddressIdentifier {

    private final String content;
    private final String identifierType;
    private final String type;
    private final GrPostal grPostal;

    /**
     *
     * @param content
     * @param identifierType
     * @param type
     * @param grPostal
     */
    public DefaultAddressIdentifier(String content, String identifierType, String type, GrPostal grPostal){
        this.content = content;
        this.identifierType = identifierType;
        this.type = type;
        this.grPostal = grPostal;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getContent() {return this.content;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getIdentifierType() {return this.identifierType;}

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
    public GrPostal getGrPostal() {return this.grPostal;}

}
