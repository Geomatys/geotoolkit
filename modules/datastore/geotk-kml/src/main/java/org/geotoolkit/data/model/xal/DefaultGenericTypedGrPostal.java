package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultGenericTypedGrPostal implements GenericTypedGrPostal {

    public final String type;
    public final GrPostal grPostal;
    public final String content;
    
    /**
     * 
     * @param type
     * @param grPostal
     * @param content
     */
    public DefaultGenericTypedGrPostal(String type, GrPostal grPostal, String content){
        this.type = type;
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
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}


}
