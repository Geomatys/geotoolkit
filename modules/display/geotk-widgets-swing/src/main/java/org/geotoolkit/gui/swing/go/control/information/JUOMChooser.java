
package org.geotoolkit.gui.swing.go.control.information;

import java.util.List;
import javax.measure.unit.Unit;
import javax.swing.JComboBox;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JUOMChooser extends JComboBox{

    public JUOMChooser(List<Unit> units) {
        super(new ListComboBoxModel(units));
        setSelectedIndex(0);
    }

}
