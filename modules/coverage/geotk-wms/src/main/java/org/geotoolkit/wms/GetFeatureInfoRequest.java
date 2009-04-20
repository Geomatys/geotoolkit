

package org.geotoolkit.wms;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface GetFeatureInfoRequest extends GetMapRequest{

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    String getSelectedLayer();

    void setSelectedLayer(String layer);

}
