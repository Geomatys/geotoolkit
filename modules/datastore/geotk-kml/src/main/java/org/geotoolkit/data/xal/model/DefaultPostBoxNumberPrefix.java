package org.geotoolkit.data.xal.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPostBoxNumberPrefix implements PostBoxNumberPrefix {

    private final String content;
    private final String numberPrefixSeparator;
    private final GrPostal grPostal;

    /**
     * 
     * @param numberPrefixSeparator
     * @param grPostal
     * @param content
     */
    public DefaultPostBoxNumberPrefix(String numberPrefixSeparator, GrPostal grPostal, String content){
        this.content = content;
        this.numberPrefixSeparator = numberPrefixSeparator;
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
    public String getNumberPrefixSeparator() {return this.numberPrefixSeparator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
