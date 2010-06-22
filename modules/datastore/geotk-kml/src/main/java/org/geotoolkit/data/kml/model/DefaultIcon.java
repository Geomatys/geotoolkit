package org.geotoolkit.data.kml.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultIcon extends DefaultLink implements Icon {

    public DefaultIcon(Link link){
        super(link.getObjectSimpleExtensions(), link.getIdAttributes(),
                link.getHref(), link.getBasicLinkSimpleExtensions(), link.getBasicLinkObjectExtensions(),
                link.getRefreshMode(), link.getRefreshInterval(), link.getViewRefreshMode(), link.getViewRefreshTime(),
                link.getViewBoundScale(), link.getViewFormat(), link.getHttpQuery(),
                link.getLinkSimpleExtensions(), link.getLinkObjectExtensions());
    }
}
