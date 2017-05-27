package MMS.Project.BCodeGen;

import MMS.Project.BCodeGen.Barcode.Dummy;
import MMS.Project.BCodeGen.Barcode.EAN13;
import MMS.Project.BCodeGen.Barcode.EAN8;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.reflections.Reflections;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

public class Controller implements Initializable{

	// region Private fields
	
	private ObservableList<IBarcode> barcodes;
	private IBarcode iBarcode;
	private Image currentBarcode = null;
	
	private static int barcodeNo = 0;
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
		// Reflections reflections = new Reflections();
	
		// Create Set of each class in Project that implements the IBarcode interface
		// Set<Class<? extends IBarcode>> barcodeClass = reflections
		// 	.getSubTypesOf(IBarcode.class);
		
		// Create foreach class implementing the IBarcode interface an instance and
		// add it to the combobox
		
		barcodes = FXCollections.observableArrayList();
		barcodes.addAll(
				new Dummy(),
				new EAN8(),
		      new EAN13()
		      // TODO: Add barcodes
		               );
		
		// TODO: write more descriptive comment
//		barcodeClass.forEach((e)->{
//			try {
//
//				IBarcode tmpBarcode = e.getConstructor().newInstance();
//				barcodes.add(tmpBarcode);
//
//				System.out.println("> " +
//						                   tmpBarcode.getClass() +
//						                   ": " +
//						                   tmpBarcode.toString());
//			}
//			catch(Exception ex){
//
//				ex.printStackTrace();
//			}
//		});
		
//		if(barcodes.size() != 0) {
//
//			cbo_IBarcode.setItems(barcodes);
//			cbo_IBarcode.getSelectionModel().select(0);
//		}
//		else {
//
//			Alert eAlert = new Alert(Alert.AlertType.ERROR);
//			eAlert.setTitle("No barcode generation rules found!");
//			eAlert.setHeaderText(eAlert.getTitle());
//			eAlert.setContentText("The program could not find any barcode classes " +
//			                     "please try to restart the application or install the newest version");
//			eAlert.showAndWait();
//			System.exit(2);
//		}
		
		cbo_IBarcode.setItems(barcodes);
		ChangeListener<IBarcode> cboIBarcodeChanged = new ChangeListener<IBarcode>() {
			@Override
			public void changed(ObservableValue<? extends IBarcode>
					                    observable, IBarcode oldValue, IBarcode newValue) {
				
				btn_Generate.setDisable(newValue
						                        .toString()
						                        .contains("Select barcode type"));
				
				ap_Configuration.getChildren().clear();
				
				Node node = newValue.mandatoryProperties();
				AnchorPane.setTopAnchor(node, 0.0);
				AnchorPane.setLeftAnchor(node, 0.0);
				AnchorPane.setRightAnchor(node, 0.0);
				AnchorPane.setBottomAnchor(node, 0.0);
				
				ap_Configuration.getChildren()
				                .add(node);
				
				iBarcode = newValue;
				
				Utils.log("Changed barcode type: " + newValue.toString(), Level.SEVERE, this);
			}
		};
		cbo_IBarcode.valueProperty().addListener(cboIBarcodeChanged);
		cbo_IBarcode.getSelectionModel().select(0);
		cbo_IBarcode.setTooltip(new Tooltip("Select the barcode type you want to generate"));
		
		btn_Generate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				try {
					
					Image barcode = iBarcode.runGenerator();
					currentBarcode = barcode;
					iv_BarcodePreview.setImage(barcode);
				}
				catch(NullPointerException e){
					
					iv_BarcodePreview.setImage(new Image("https://www.minecraftskinstealer.com/achievement/a.php?i=20&h=Achievement+Get%21&t=You+broke+the+program%21"));
					Utils.log("Not able to generate barcode: " + e.toString(), Level.SEVERE, this);
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
		btn_Generate.setTooltip(new Tooltip("Generates a barcode"));
		
		btn_Export.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				if(currentBarcode == null){
					
					// Show alert if no barcode was generated
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Attention");
					alert.setHeaderText("You must generate a barcode before exporting it!");
					alert.show();
				}
				else {
					
					// Save barcode to file system
					// Step 1: choose a location
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Select location");
					fileChooser.setInitialDirectory(
							new File(
									System.getProperty("user.home") +
											"/Desktop"));
					
					fileChooser.setInitialFileName("file");
					
					fileChooser.getExtensionFilters().addAll(
							new ExtensionFilter("png", "*.png"),
					      new ExtensionFilter("bmp", "*.bmp")
					                                        );
					File file = fileChooser.showSaveDialog(null);
					
					if(file != null){
						
						System.out.println("File location: " + file);
						
						BufferedImage img = SwingFXUtils.fromFXImage(currentBarcode, null);
						
						
						try {
							
							ImageIO.write(img,
							              getFileExtension(file.getName()),
							              file);
						}catch(Exception e){
							
							e.printStackTrace();
						}
						
					}
					
				}
				
				
			}
		});
		btn_Export.setTooltip(new Tooltip("Exports barcode to a image file"));
	}
	
	private String getFileExtension(String filename){
	
		String extension = "";
		
		int i = filename.lastIndexOf('.');
		if(i > 0){
			
			extension = filename.substring(i + 1);
		}
		
		return extension;
	}
	
	public void updateStatus(String msg, StatusKind kind){
		
		if(kind == StatusKind.LEFT){
			
			lbl_LeftStatus.setText(msg);
		}
		else {
			
			lbl_RightStatus.setText(msg);
		}
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
