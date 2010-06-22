package org.geotoolkit.data.xal.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSubPremiseLocation implements SubPremiseLocation{

    private final GrPostal grPostal;
    private final String content;

    /**
     *
     * @param grPostal
     * @param content
     */
    public DefaultSubPremiseLocation(GrPostal grPostal, String content){
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
    public GrPostal getGrPostal() {return this.grPostal;}

}
