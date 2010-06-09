package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class PostOfficeNumberDefault implements PostOfficeNumber {

    private final String indicator;
    private final AfterBeforeEnum indicatorOccurence;
    private final GrPostal grPostal;
    private final String content;

    /**
     * 
     * @param indicator
     * @param indicatorOccurence
     * @param grPostal
     * @param content
     */
    public PostOfficeNumberDefault(String indicator,
            AfterBeforeEnum indicatorOccurence, GrPostal grPostal, String content){
        this.indicator = indicator;
        this.indicatorOccurence = indicatorOccurence;
        this.grPostal = grPostal;
        this.content = content;
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
    public String getIndicator() {return this.indicator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeEnum getIndicatorOccurrence() {return this.indicatorOccurence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
