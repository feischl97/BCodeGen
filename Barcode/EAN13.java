package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;

import MMS.Project.BCodeGen.Utils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.util.logging.Level;

public class EAN13 extends EAN8{
	
	
	@Override
	public Image runGenerator() throws Exception {
		
		Utils.log("Starting rendering", Level.INFO, this);
		return null;
	}
	
	@Override
	public String toString(){
		
		return "EAN 13";
	}
}