

package org.geotoolkit.pending.demo.symbology;

import javax.swing.JComponent;
import org.geotoolkit.map.MapContext;

public class JSEStylePane extends JAbstractMapPane{

    public JSEStylePane(MapContext context){
        super(context);
    }

    @Override
    protected JComponent createConfigPane() {
        return null;
    }

}
