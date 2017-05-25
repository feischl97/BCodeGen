package MMS.Projekt.BCodeGen;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class Controller implements Initializable{

	// region Private fields
	
	private ObservableList<IBarcode> barcodes;
	private IBarcode iBarcode;
	private Image currentBarcode = null;
	
	// endregion
	
	// region GUI objects
	
	// TODO: Evaluate which menu functions ar needed and implement them
	
	@FXML
	ComboBox<IBarcode> cbo_IBarcode;
	@FXML
	private Button btn_Generate;
	@FXML
	private AnchorPane ap_Configuration;
	@FXML
	private ImageView iv_BarcodePreview;
	@FXML
	private Button btn_Export;
	@FXML
	private Label lbl_LeftStatus;
	@FXML
	private Label lbl_RightStatus;
	// endregion
	
	/**
	 * Constructor
	 */
	public Controller(){
	
	}
	
	/**
	 * this method sets the GUI up
	 */
	public void setup(){
	
		// Create Reflections object
		Reflections reflections = new Reflections();
	
		// Create Set of each class in Project that implements the IBarcode interface
		Set<Class<? extends IBarcode>> barcodeClass = reflections
				.getSubTypesOf(MMS.Projekt.BCodeGen.IBarcode.class);
		
		// Create foreach class implementing the IBarcode interface an instance and add it to the combobox
		barcodes = FXCollections.<IBarcode>observableArrayList();
		
		System.out.println("== Found Barcodes: ");
		
		// TODO: write more descriptive comment
		barcodeClass.forEach((e)->{
			try {
				
				IBarcode tmpBarcode = e.getConstructor().newInstance();
				barcodes.add(tmpBarcode);
				
				System.out.println("> " +
						                   tmpBarcode.getClass() +
						                   ": " +
						                   tmpBarcode.toString());
			}
			catch(Exception e){
				
				e.printStackTrace();
			}
		});
		
		if(barcodes.size() != 0) {
			
			cbo_IBarcode.setItems(barcodes);
			cbo_IBarcode.getSelectionModel().select(0);
		}
		else {
			
			Alert eAlert = new Alert(Alert.AlertType.ERROR);
			eAlert.setTitle("No barcode generation rules found!");
			eAlert.setHeaderText(eAlert.getTitle());
			eAlert.setContentText("The program could not find any barcode classes " +
			                     "please try to restart the application or install the newest version");
			eAlert.showAndWait();
			System.exit(2);
		}
		
		iBarcode = cbo_IBarcode.getValue();
		ap_Configuration.getChildren().clear();
		ap_Configuration.getChildren().add(iBarcode.mandatoryProperties());
		// btn_Generate.setDisable(true);
		
		cbo_IBarcode.valueProperty()
	               .addListener(
			               new ChangeListener<IBarcode>() {
				               @Override
				               public void changed(ObservableValue<? extends IBarcode>
						                                   observable, IBarcode oldValue, IBarcode newValue) {
										
				               	btn_Generate.setDisable(newValue
							                                       .toString()
							                                       .contains("Select"));
				               	
				               	ap_Configuration.getChildren().clear();
				               	
				               	Node node = newValue.mandatoryProperties();
				               	AnchorPane.setTopAnchor(node, 0.0);
					               AnchorPane.setLeftAnchor(node, 0.0);
					               AnchorPane.setRightAnchor(node, 0.0);
					               AnchorPane.setBottomAnchor(node, 0.0);
					               
				               	ap_Configuration.getChildren()
					                               .add(node);
				               	
				               	System.out.printf("Changed barcode type from %s to %s",
					                                 oldValue.getClass().toString(),
					                                 newValue.getClass().toString());
				               }
			               });
		
		btn_Generate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				try {
					
					Image barcode = iBarcode.runGenerator();
					currentBarcode = barcode;
					iv_BarcodePreview.setImage(barcode);
				}
				catch(Exception e){
					
					currentBarcode = null;
					Alert eAlert = new Alert(Alert.AlertType.ERROR);
					eAlert.setTitle("An exception while generating a barcode has occurred");
					eAlert.setHeaderText("An exception has occurred when attempted to generate" +
							                    " a barcode of type: " + iBarcode);
					eAlert.setContentText(e.toString());
					eAlert.show();
				}
			}
		});
		
		btn_Export.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				if(currentBarcode == null){
					
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Attention");
					alert.setHeaderText("You must generate a barcode before exporting it!");
					alert.show();
				}
				else {
					
					DirectoryChooser dirChooser = new DirectoryChooser();
					dirChooser.setTitle("Select a directoy");
					dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
					
					
					
				}
				
				
			}
		});
		
	}
	
	
	/**
	 * Called to initialize a controller after its root element has been
	 * completely processed.
	 *
	 * @param location  The location used to resolve relative paths for the root
	 *                    object, or
	 *                  <tt>null</tt> if the location is not known.
	 * @param resources The resources used to localize the root object, or
	 *                    <tt>null</tt> if
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
	}
}
