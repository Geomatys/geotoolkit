package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class ThoroughfareNumberFromDefault implements ThoroughfareNumberFrom {

    private final List<Object> content;
    private final GrPostal grPostal;

    /**
     *
     * @param content
     * @param grPostal
     * @throws XalException
     */
    public ThoroughfareNumberFromDefault(List<Object> content, GrPostal grPostal) throws XalException{
        this.content = (content == null) ? EMPTY_LIST : this.verifContent(content);
        this.grPostal = grPostal;
    }

    /**
     * 
     * @param content
     * @return
     * @throws XalException
     */
    private List<Object> verifContent(List<Object> content) throws XalException{
        for (Object object : content){
            if(!(object instanceof String)
                    && !(object instanceof GenericTypedGrPostal)
                    && !(object instanceof ThoroughfareNumberPrefix)
                    && !(object instanceof ThoroughfareNumber)
                    && !(object instanceof ThoroughfareNumberSuffix))
                throw new XalException("This kind of content ("+object.getClass()+") is not allowed here : "+this.getClass());
        }
        return content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getContent() {return this.content;}


    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}
}
