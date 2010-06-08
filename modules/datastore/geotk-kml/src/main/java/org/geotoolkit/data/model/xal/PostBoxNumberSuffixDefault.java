package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class PostBoxNumberSuffixDefault implements PostBoxNumberSuffix {

    private final String content;
    private final String numberSuffixSeparator;
    private final GrPostal grPostal;

    /**
     *
     * @param numberPrefixSeparator
     * @param grPostal
     * @param content
     */
    public PostBoxNumberSuffixDefault(String numberSuffixSeparator, GrPostal grPostal, String content){
        this.content = content;
        this.numberSuffixSeparator = numberSuffixSeparator;
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
    public String getNumberSuffixSeparator() {return this.numberSuffixSeparator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
