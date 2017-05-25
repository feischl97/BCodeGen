package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

/**
 * Created by x19de on 24.05.2017.
 */
public class Dummy implements IBarcode {
	
	public Dummy(){
	
	}
	
	/**
	 * runs the current barcode generator
	 * @return generated barcode as Image
	 */
	@Override
	public Image runGenerator() {
		
		return new Image("http://docs.oracle.com/javafx/" +
				                 "javafx/images/javafx-documentation.png");
	}
	
	/**
	 * gets the properties mandatory to be implemented
	 * @return A anchorPane that contains controls which set the properties
	 * (examples in EAN13 & EAN8 class)
	 */
	@Override
	public AnchorPane mandatoryProperties() {
		
		return new AnchorPane(new Label("Select a barcode type"));
	}
	
	@Override
	public String toString(){
		
		return "<Select barcode type>";
	}
}
