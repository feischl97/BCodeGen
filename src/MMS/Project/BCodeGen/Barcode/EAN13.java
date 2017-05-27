package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;

import MMS.Project.BCodeGen.Utils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.util.logging.Level;

public class EAN13 implements IBarcode{
	
	
	@Override
	public Image runGenerator() throws Exception {
		
		Utils.log("Starting rendering", Level.INFO, this);
		return new Image("https://www.minecraftskinstealer.com/achievement/a.php?i=19&h=Achievement+Get%21&t=You+found+a+barcode");
	}
	
	@Override
	public Node mandatoryProperties() {
		
		return new Label(toString());
	}
	
	@Override
	public String toString(){
		
		return "EAN 13";
	}
}