package org.geotoolkit.data.kml.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultIcon extends DefaultLink implements Icon {

    /**
     * 
     * @param link
     */
    public DefaultIcon(Link link){
        super(link.extensions().simples(Extensions.Names.OBJECT),
                link.getIdAttributes(), link.getHref(),
                link.extensions().simples(Extensions.Names.BASIC_LINK),
                link.extensions().complexes(Extensions.Names.BASIC_LINK),
                link.getRefreshMode(), link.getRefreshInterval(),
                link.getViewRefreshMode(), link.getViewRefreshTime(),
                link.getViewBoundScale(), link.getViewFormat(), link.getHttpQuery(),
                link.extensions().simples(Extensions.Names.LINK),
                link.extensions().complexes(Extensions.Names.LINK));
    }
}
