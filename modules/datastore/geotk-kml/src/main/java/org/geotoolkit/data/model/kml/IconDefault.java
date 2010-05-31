package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class IconDefault extends LinkDefault implements Icon {

    public IconDefault(Link link){
        super(link.getObjectSimpleExtensions(), link.getIdAttributes(),
                link.getHref(), link.getBasicLinkSimpleExtensions(), link.getBasicLinkObjectExtensions(),
                link.getRefreshMode(), link.getRefreshInterval(), link.getViewRefreshMode(), link.getViewRefreshTime(),
                link.getViewBoundScale(), link.getViewFormat(), link.getHttpQuery(),
                link.getLinkSimpleExtensions(), link.getLinkObjectExtensions());
    }
}
