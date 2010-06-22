package org.geotoolkit.data.xal.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultThoroughfareNumberPrefix implements ThoroughfareNumberPrefix {

    private final String numberPrefixSeparator;
    private final String type;
    private final GrPostal grPostal;
    private final String content;

    /**
     *
     * @param numberPrefixSeparator
     * @param type
     * @param grPostal
     * @param content
     */
    public DefaultThoroughfareNumberPrefix(String numberPrefixSeparator,
        String type, GrPostal grPostal, String content){
        this.numberPrefixSeparator = numberPrefixSeparator;
        this.type = type;
        this.grPostal = grPostal;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getNumberPrefixSeparator() {return this.numberPrefixSeparator;}

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
