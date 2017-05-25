package MMS.Project.BCodeGen;

import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Interface to be implemented by all barcodes.
 */
public interface IBarcode {
	
	/**
	 * runs the current barcode generator
	 * @return generated barcode as Image
	 */
	public Image runGenerator() throws Exception;
	
	/**
	 * gets the properties mandatory to be implemented
	 * @return A anchorPane that contains controls which set the properties
	 * (examples in EAN13 & EAN8 class)
	 */
	public Node mandatoryProperties();
}
