package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Created by x19de on 25.05.2017.
 */
public class EAN8 implements IBarcode{
	
	@Override
	public Image runGenerator() throws Exception {
		return null;
	}
	
	@Override
	public Node mandatoryProperties() {
		return null;
	}
	
	@Override
	public String toString(){
		
		return "EAN 8";
	}
}
