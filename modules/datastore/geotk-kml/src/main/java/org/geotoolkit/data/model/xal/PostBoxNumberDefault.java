package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class PostBoxNumberDefault implements PostBoxNumber {

    private final String content;
    private final GrPostal grPostal;

    /**
     *
     * @param grPostal
     * @param content
     */
    public PostBoxNumberDefault(GrPostal grPostal, String content){
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
