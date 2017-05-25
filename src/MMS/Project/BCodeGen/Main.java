package MMS.Project.BCodeGen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {

    private static Controller guiCtrl;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        
        try {
    
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainWindow" +
                                                                                ".fxml"));
            
            Parent root = fxmlLoader.load();
            
            guiCtrl = fxmlLoader.getController();
            
            primaryStage.setTitle("Barcode Generator");
            primaryStage.setScene(new Scene(root, 1080, 720));
            primaryStage.show();
            
            guiCtrl.setup();
        }
        catch(Exception e){
    
            Alert eAlert = new Alert(Alert.AlertType.ERROR);
            eAlert.setTitle("Not able to load FXML");
            eAlert.setHeaderText("An exception occured when attempting to load \"MainWindow.fxml\" resource");
            eAlert.setContentText(e.toString());
            eAlert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
