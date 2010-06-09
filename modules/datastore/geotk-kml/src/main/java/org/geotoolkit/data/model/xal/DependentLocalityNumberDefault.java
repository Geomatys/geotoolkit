package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class DependentLocalityNumberDefault implements DependentLocalityNumber{

    private final AfterBeforeEnum nameNumberOccurence;
    private final GrPostal grPostal;
    private final String content;

    /**
     *
     * @param nameNumberOccurence
     * @param grPostal
     * @param content
     */
    public DependentLocalityNumberDefault(AfterBeforeEnum nameNumberOccurence,
            GrPostal grPostal, String content){
        this.nameNumberOccurence = nameNumberOccurence;
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
    public AfterBeforeEnum getNameNumberOccurrence() {return this.nameNumberOccurence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}
}
