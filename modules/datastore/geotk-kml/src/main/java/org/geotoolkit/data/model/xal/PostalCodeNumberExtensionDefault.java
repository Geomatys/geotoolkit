package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class PostalCodeNumberExtensionDefault implements PostalCodeNumberExtension {

    private final String type;
    private final String numberExtensionSeparator;
    private final GrPostal grPostal;
    private final String content;

    /**
     *
     * @param type
     * @param numberExtensionSeparator
     * @param grPostal
     * @param content
     */
    public PostalCodeNumberExtensionDefault(String type, String numberExtensionSeparator,
            GrPostal grPostal, String content){
        this.type = type;
        this.numberExtensionSeparator = numberExtensionSeparator;
        this.grPostal = grPostal;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getNumberExtensionSeparator() {return this.numberExtensionSeparator;}

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
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
