package MMS.Projekt.BCodeGen;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable{

	// region Private fields
	
	private ObservableList<IBarcode> barcodes;
	private IBarcode iBarcode;
	
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
		Set<Class<? extends IBarcode>> ibarcodes = reflections.getSubTypesOf(MMS.Projekt.BCodeGen.IBarcode.class);
		
		// Create for each class implementing the IBarcode interface an instance and add it to the combobox
		List<IBarcode> tmp = new ArrayList<IBarcode>();
		for(Class<? extends IBarcode> aClass: ibarcodes) {
			
			try {
				
				tmp.add(aClass.getConstructor().newInstance());
			}
			catch(Exception e){
				
				e.printStackTrace();
			}
		}
		
		System.out.println("== Found Barcodes: ");
		for(IBarcode bcd: tmp){
			
			try {
				System.out.println("> " + bcd.getClass().toString() + ": " + bcd.toString());
				
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
		
		barcodes = FXCollections.observableList(tmp);
		
		cbo_IBarcode.setItems(barcodes);
		cbo_IBarcode.getSelectionModel().select(0);
		
		
		
		
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
