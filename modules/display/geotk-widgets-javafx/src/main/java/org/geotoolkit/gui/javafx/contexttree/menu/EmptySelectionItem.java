package org.geotoolkit.gui.javafx.contexttree.menu;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.CollectionMapLayer;

/**
 * A context action for {@link CollectionMapLayer}, whose job is to clean selection
 * filter on selected layer(s).
 * 
 * Note : Tree items are browsed recursively, so if a parent node is selected, all
 * its children will be impacted.
 * 
 * @author Alexis Manin (Geomatys)
 */
public class EmptySelectionItem  extends TreeMenuItem {
    
    @Override
    public MenuItem init(List<? extends TreeItem> selectedItems) {
        final List<CollectionMapLayer> layers = new ArrayList<>();
        findFeatureLayers(selectedItems, layers);
        
        if (layers.isEmpty()) return null;
        
        final MenuItem item = new MenuItem(GeotkFX.getString(this,"title"), new ImageView(GeotkFX.ICON_UNLINK));
        item.setOnAction((ActionEvent e)-> {
            for (final CollectionMapLayer layer : layers) {
                layer.setSelectionFilter(null);
            }
        });
        return item;
    }
    
    /**
     * Scan all tree items in the input list to find ones whose value is a {@link CollectionMapLayer}
     * @param selected
     * @param toKeep 
     */
    private static void findFeatureLayers(final List<? extends TreeItem> selected, final List<CollectionMapLayer> toKeep) {
        for (final TreeItem item : selected) {
            if (item.isLeaf()) {
                if (item.getValue() instanceof CollectionMapLayer) {
                    toKeep.add((CollectionMapLayer) item.getValue());
                }
            } else {
                findFeatureLayers(item.getChildren(), toKeep);
            }
        }
    }

}
