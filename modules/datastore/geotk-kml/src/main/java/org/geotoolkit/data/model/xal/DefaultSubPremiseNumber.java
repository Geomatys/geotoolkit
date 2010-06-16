package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSubPremiseNumber implements SubPremiseNumber {

    private final String indicator;
    private final AfterBeforeEnum indicatorOccurrence;
    private final AfterBeforeEnum numberTypeOccurrence;
    private final String premiseNumberSeparator;
    private final String type;
    private final GrPostal grPostal;
    private final String content;

    /**
     *
     * @param indicator
     * @param indicatorOccurrence
     * @param numberTypeOccurrence
     * @param premiseNumberSeparator
     * @param type
     * @param grPostal
     * @param content
     */
    public DefaultSubPremiseNumber(String indicator, AfterBeforeEnum indicatorOccurrence,
            AfterBeforeEnum numberTypeOccurrence, String premiseNumberSeparator,
            String type, GrPostal grPostal, String content){
        this.indicator = indicator;
        this.indicatorOccurrence = indicatorOccurrence;
        this.numberTypeOccurrence = numberTypeOccurrence;
        this.premiseNumberSeparator = premiseNumberSeparator;
        this.type = type;
        this.grPostal = grPostal;
        this.content = content;
    }

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public String getIndicator() {return this.indicator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeEnum getIndicatorOccurrence() {return this.indicatorOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeEnum getNumberTypeOccurrence() {return this.numberTypeOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getPremiseNumberSeparator() {return this.premiseNumberSeparator;}

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
