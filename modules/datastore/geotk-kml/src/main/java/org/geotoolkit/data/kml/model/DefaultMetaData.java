package org.geotoolkit.data.kml.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
@Deprecated
public class DefaultMetaData implements Metadata {

    private List<Object> content;

    /**
     *
     * @deprecated
     */
    @Deprecated
    public DefaultMetaData(){
        this.content = EMPTY_LIST;
    }

    /**
     * 
     * @param content
     * @deprecated
     */
    @Deprecated
    public DefaultMetaData(List<Object> content){
        this.content = (content == null) ? EMPTY_LIST : content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getContent() {
        return this.content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setContent(List<Object> content) {
        this.content = (content == null) ? EMPTY_LIST : content;
    }

}
