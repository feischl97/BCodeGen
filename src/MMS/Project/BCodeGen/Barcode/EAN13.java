package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

public class EAN13 implements IBarcode{
	
	
	@Override
	public Image runGenerator() throws Exception {
		
		return null;
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