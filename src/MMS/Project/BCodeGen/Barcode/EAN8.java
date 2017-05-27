package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * Created by x19de on 25.05.2017.
 */
public class EAN8 implements IBarcode{
	
	// region configuration fields
	
		private String data;
		private int digits;
		private boolean strict;
		private boolean retard;
		private boolean debug;
		private Color foreground = Color.BLACK;
		private Color background = Color.WHITE;
		private Color debugmarker = Color.PURPLE;
	
		private static final Boolean FORCE_DEBUG = true;
		
	// endregion
	
	// region GUI elements
	
		private TextField tfdata;
		private ToggleGroup tgDigits;
		private RadioButton rbDigits7;
		private RadioButton rbDigits8;
		private ToggleGroup tgMode;
		private RadioButton rbStrict;
		private RadioButton rbIgnore;
		private ColorPicker cpForeground;
	   private ColorPicker cpBackground;
	   private CheckBox cbRetard;
	   private CheckBox cbDebug;
	   private ComboBox<Double> cboScale;
	
	// endregion
	
	// region Constants and measurements
	
		private float SCALE = 1.0f;
		private static final float MINSCALE = 0.80f;
		private static final float MAXSCALE = 2.00f;
		private static final float SCALESTEP = 0.05f;
		private static final float WIDTH = 22.11f;
		private static final float MARGIN = 4.62f;
		private static final float HEIGTH = 21.31f;
		private static final float DPMM = 25;
	
		private static final String START = "101";
		private static final String END = "101";
		private static final String SEPARATOR = "01010";
	
	private static final Map<Integer, String> L_CODE = new TreeMap<Integer, String>(){{
		
		put(0, "0001101");
		put(1, "0011001");
		put(2, "0010011");
		put(3, "0111101");
		put(4, "0100011");
		put(5, "0110001");
		put(6, "0101111");
		put(7, "0111011");
		put(8, "0110111");
		put(9, "0001011");
	}};
	
	private static final Map<Integer, String> R_CODE = new TreeMap<Integer, String>(){{
		
		put(0, "1110010");
		put(1, "1100110");
		put(2, "1101100");
		put(3, "1000010");
		put(4, "1011100");
		put(5, "1001110");
		put(6, "1010000");
		put(7, "1000100");
		put(8, "1001000");
		put(9, "1110100");
	}};
	
	// endregion
	
	@Override
	public Image runGenerator() throws Exception {
		
		return null;
	}
	
	
	// region Private methods
	
	
	
	// endregion
	
	@Override
	public Node mandatoryProperties() {
		
		Insets small = new Insets(3, 3, 3, 3);
		
		VBox container = new VBox();
		container.setPadding(small);
		container.getChildren().add(new Label("Data: "));
		
		tfdata = new TextField();
		container.getChildren().add(tfdata);
		
		
		GridPane gpSettings = new GridPane();
		gpSettings.setHgap(small.getTop());
		gpSettings.setVgap(small.getLeft());
		
		gpSettings.add(generateDigitSettings(small), 0, 0);
		gpSettings.add(generateModeSettings(small), 1, 0);
		gpSettings.add(generateSizeSettings(small), 0, 1);
		gpSettings.add(generateColorSettings(small), 1, 1);
		gpSettings.add(generateOptionalSettings(small), 0, 2);
		
		container.getChildren().add(gpSettings);
		return container;
	}
	
	// region GUI-Creator helper fkt's
	
	/**
	 * Generates the container and radio buttons for the digit amout settings
	 * @param insets padding between the elements
	 * @return container with gui elements
	 */
	private Node generateDigitSettings(Insets insets){
		
		TitledPane tpDigits = new TitledPane();
		tpDigits.setCollapsible(true);
		tpDigits.setText("Digits");
		tpDigits.setPadding(insets);
		
		GridPane gpDigits = new GridPane();
		gpDigits.setHgap(insets.getTop());
		gpDigits.setVgap(insets.getLeft());
		
		rbDigits7 = new RadioButton("7-Digits");
		gpDigits.add(rbDigits7, 0, 0);
		
		rbDigits8 = new RadioButton("8-Digits");
		gpDigits.add(rbDigits8, 0, 1);
		
		tgDigits = new ToggleGroup();
		tgDigits.getToggles().addAll(rbDigits7, rbDigits8);
		rbDigits7.setSelected(true);
		
		tpDigits.setContent(gpDigits);
		
		return tpDigits;
	}
	
	/**
	 * Generates the container and radio buttons for the generator mode settings
	 * @param insets padding between the elements
	 * @return container with gui elements
	 */
	private Node generateModeSettings(Insets insets){
		
		TitledPane tpMode = new TitledPane();
		tpMode.setText("Mode");
		tpMode.setCollapsible(true);
		tpMode.setPadding(insets);
		
		GridPane gpMode = new GridPane();
		gpMode.setVgap(insets.getTop());
		gpMode.setHgap(insets.getLeft());
		
		rbIgnore = new RadioButton("Ignore");
		gpMode.add(rbIgnore, 0, 0);
		
		rbStrict = new RadioButton("Strict");
		gpMode.add(rbStrict, 0, 1);
		
		tgMode = new ToggleGroup();
		tgMode.getToggles().addAll(rbIgnore, rbStrict);
		rbIgnore.setSelected(true);
		
		tpMode.setContent(gpMode);
		return tpMode;
	}
	
	/**
	 * Generates the container, combo box  for the generator size settings
	 * Also generated the different size options based on min and max
	 * @param insets padding between the elements
	 * @return container with gui elements
	 */
	private Node generateSizeSettings(Insets insets){
	
		TitledPane tpSize = new TitledPane();
		tpSize.setText("Size");
		tpSize.setCollapsible(true);
		tpSize.setPadding(insets);
		
		GridPane gpSize = new GridPane();
		gpSize.setHgap(insets.getLeft());
		gpSize.setVgap(insets.getTop());
		
		List<Double> scales = new ArrayList<>();
		IntStream
				.rangeClosed(0, (int)((MAXSCALE - MINSCALE)/SCALESTEP))
				.forEach((i)->{scales.add(Math.round((MINSCALE + SCALESTEP * i) * 100d)/100d);});
		
		gpSize.add(new Label("Scale: "), 0, 0);
		
		cboScale = new ComboBox<>(FXCollections.observableList(scales));
		cboScale.setValue(1.00d);
		gpSize.add(cboScale, 1, 0);
		
		gpSize.add(new Label("Dots per mm: " + DPMM), 0, 1);
		
		tpSize.setContent(gpSize);
		return tpSize;
	}
	
	/**
	 * Generates the container and colorboxes for the generator color settings
	 * @param insets padding between the elementes
	 * @return container with gui elements
	 */
	private Node generateColorSettings(Insets insets){
		
		TitledPane tpColor = new TitledPane();
		tpColor.setText("Color");
		tpColor.setCollapsible(true);
		tpColor.setPadding(insets);
		
		GridPane gpColor = new GridPane();
		gpColor.setHgap(insets.getLeft());
		gpColor.setVgap(insets.getTop());
		
		gpColor.add(new Label("Foreground: "), 0, 0);
		cpForeground = new ColorPicker(Color.BLACK);
		gpColor.add(cpForeground, 0, 1);
		
		gpColor.add(new Label("Background: "), 0, 2);
		cpBackground = new ColorPicker(Color.WHITE);
		gpColor.add(cpBackground, 0, 3);
		
		tpColor.setContent(gpColor);
		return tpColor;
	}
	
	private Node generateOptionalSettings(Insets insets){
		
		TitledPane tpOptionals = new TitledPane();
		tpOptionals.setText("Optionals");
		tpOptionals.setCollapsible(true);
		
		GridPane gpOptionals = new GridPane();
		gpOptionals.setVgap(insets.getTop());
		gpOptionals.setHgap(insets.getLeft());
		
		cbRetard = new CheckBox("Strik trough");
		gpOptionals.add(cbRetard, 0, 0);
		
		cbDebug = new CheckBox(FORCE_DEBUG ? "DEBUG FORCED": "Debug");
		cbDebug.setSelected(FORCE_DEBUG);
		cbDebug.setDisable(FORCE_DEBUG);
		gpOptionals.add(cbDebug, 0, 1);
		
		
		tpOptionals.setContent(gpOptionals);
		return tpOptionals;
	}
	
	// endregion
	
	@Override
	public String toString(){
		
		return "EAN 8";
	}
}
