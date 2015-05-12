
package org.geotoolkit.gui.javafx.parameter;

import java.nio.file.Path;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.measure.unit.SI;
import org.apache.sis.parameter.ParameterBuilder;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class ParameterTestApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        final Stage stage = new Stage();
        stage.setScene(new Scene(testParameterGroupPane()));
        stage.setWidth(600);
        stage.setHeight(600);
        stage.setTitle("test stage");
        stage.setOnCloseRequest((WindowEvent event) -> {
            System.exit(0);
        });
        stage.show();

    }

    public static void main(final String[] args) throws Exception {
        launch(args);
    }

    public static Parent testParameterGroupPane() {
        ParameterBuilder groupBuilder = new ParameterBuilder();
        ParameterBuilder builder = new ParameterBuilder();

        ParameterDescriptorGroup innerGroup = groupBuilder.addName("inner group").setRemarks("I'm a magnificient group.").createGroup(0, 2,
                builder.addName("first").setRemarks("Here are my remarks.").setRequired(true).create(String.class, null),
                builder.addName("second").setRemarks("Here are my remarks.").setRequired(true).create(String.class, null),
                builder.addName("third").setRemarks("Here are my remarks.").setRequired(true).create(String.class, null)
        );

        ParameterDescriptorGroup anotherGroup = groupBuilder.addName("another group").setRemarks("I'm a magnificient group.").createGroup(1, 10,
                builder.addName("first").setRemarks("Here are my remarks.").setRequired(true).create(String.class, null),
                builder.addName("third").setRemarks("A path to be set").setRequired(false).create(Path.class, null)
        );

        ParameterValueGroup editGroup = groupBuilder.addName("My parameter group").createGroup(
                builder.addName("first").setRemarks("Here are my remarks.").setRequired(true).create(String.class, null),
                builder.addName("second").setRequired(true).create(0.0, SI.METRE),
                builder.addName("third").setRemarks("A path to be set").setRequired(false).create(Path.class, null),
                innerGroup,
                anotherGroup,
                builder.addName("last").setRemarks("The last parameter.").setRequired(false).create(String.class, null)
        ).createValue();
        
        final ScrollPane scrollPane = new ScrollPane(new FXParameterGroupPane(editGroup));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }    
}
