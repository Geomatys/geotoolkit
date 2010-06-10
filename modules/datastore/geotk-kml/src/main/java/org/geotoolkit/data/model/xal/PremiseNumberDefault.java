package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class PremiseNumberDefault implements PremiseNumber {

    private final SingleRangeEnum numberType;
    private final String type;
    private final String indicator;
    private final AfterBeforeEnum indicatorOccurrence;
    private final AfterBeforeEnum numberTypeOccurrence;
    private final GrPostal grPostal;
    private final String content;

    /**
     *
     * @param numberType
     * @param type
     * @param indicator
     * @param indicatorOccurrence
     * @param numberTypeOccurrence
     * @param grPostal
     * @param content
     */
    public PremiseNumberDefault(SingleRangeEnum numberType, String type, String indicator,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeEnum numberTypeOccurrence,
            GrPostal grPostal, String content){
        this.numberType = numberType;
        this.type = type;
        this.indicator = indicator;
        this.indicatorOccurrence = indicatorOccurrence;
        this.numberTypeOccurrence = numberTypeOccurrence;
        this.grPostal = grPostal;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SingleRangeEnum getNumberType() {return this.numberType;}

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
