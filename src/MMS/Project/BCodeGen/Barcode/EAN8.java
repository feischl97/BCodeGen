package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;
import MMS.Project.BCodeGen.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.logging.Level;
import java.util.stream.IntStream;

/**
 * This class is used to generate a EAN 8 barcode
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
		
		// region debug Options
	
		private static final Boolean FORCE_DEBUG = true;
		// endregion
		
	// endregion
	
	// region GUI elements
	
	   private Node properties;
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
	
	public EAN8(){
		
		System.out.println("Successfully created " + this.getClass().getName());
		properties = generateMandatoryProperties(new Insets(5, 5, 5, 5));
	}
	
	@Override
	public Image runGenerator() throws Exception {
		
		Utils.log("Running generator", Level.INFO, this);
		
		return null;
	}
	
	
	// region Private methods
	
	
	
	// endregion
	
	@Override
	public Node mandatoryProperties() {
		
		Utils.log("Calling properties", Level.SEVERE, this);
		return properties;
	}
	
	// region GUI-Creator helper functions
	
	/**
	 * This mehtod generated all properties for this barcode type
	 * @param insets
	 * @return
	 */
	private Node generateMandatoryProperties(Insets insets){
		
		Utils.log("First time generating mandatory properties",
		          Level.SEVERE,
		          this);
		
		VBox container = new VBox();
		container.setPadding(insets);
		container.getChildren().add(new Label("Data: "));
		
		tfdata = new TextField();
		tfdata.setText("12345678");
		
		tfdata.setTooltip(new Tooltip("Data for barcode\n" +
				                              "Only numbers are allowed " +
				                              "for digitcount check mode and digit settings"));
		
		ChangeListener<String> tfDataChanged = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String
					oldValue, String newValue) {
				
				data = newValue;
				Utils.log("Data: " + newValue, Level.INFO, this);
			}
		};
		tfdata.textProperty().addListener(tfDataChanged);
		tfDataChanged.changed(tfdata.textProperty(),
		                      null,
		                      tfdata.getText());
		
		container.getChildren().add(tfdata);
		
		GridPane gpSettings = new GridPane();
		gpSettings.setHgap(insets.getTop());
		gpSettings.setVgap(insets.getLeft());
		
		gpSettings.add(generateDigitSettings(insets), 0, 0);
		gpSettings.add(generateModeSettings(insets), 1, 0);
		gpSettings.add(generateSizeSettings(insets), 0, 1);
		gpSettings.add(generateColorSettings(insets), 1, 1);
		gpSettings.add(generateOptionalSettings(insets), 0, 2);
		
		container.getChildren().add(gpSettings);
		return container;
	}
	
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
		rbDigits7.setTooltip(new Tooltip("Sets neccessary digits to 7\n" +
				                                 "Check digit will be calculated\n" +
															"(RECOMMENDED)"));
		gpDigits.add(rbDigits7, 0, 0);
		
		rbDigits8 = new RadioButton("8-Digits");
		rbDigits8.setTooltip(new Tooltip("Sets necessary digits to 8\n" +
				                                 "Check digit won't be calculated nor checked"));
		gpDigits.add(rbDigits8, 0, 1);
		
		tgDigits = new ToggleGroup();
		tgDigits.getToggles().addAll(rbDigits7, rbDigits8);
		
		ChangeListener<Toggle> tgDigitsChanged = new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle
					oldValue, Toggle newValue) {
				
				if(tgDigits.getSelectedToggle() == rbDigits7){
					
					digits = 7;
				}
				else {
					
					digits = 8;
				}
				
				Utils.log("Digits: " + digits, Level.INFO, this);
			}
		};
		
		tgDigits.selectedToggleProperty().addListener(tgDigitsChanged);
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
		rbIgnore.setTooltip(new Tooltip("Ignores every digit after the 7th or 8th Digit (RECOMMENDED)"));
		gpMode.add(rbIgnore, 0, 0);
		
		rbStrict = new RadioButton("Strict");
		rbStrict.setTooltip(new Tooltip("Will throw an error if input is longer than 7 or 8 digits"));
		gpMode.add(rbStrict, 0, 1);
		
		tgMode = new ToggleGroup();
		tgMode.getToggles().addAll(rbIgnore, rbStrict);
		
		ChangeListener<Toggle> tgModeChanged = new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle
					oldValue, Toggle newValue) {
				
				if(tgMode.getSelectedToggle() == rbIgnore){
					
					strict = false;
				}
				else{
					strict = true;
					
				}
				
				Utils.log("Mode: " + (strict ? "Strinct" : "Ignore"), Level.INFO, this);
			}
		};
		tgMode.selectedToggleProperty().addListener(tgModeChanged);
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
		cboScale.setTooltip(new Tooltip("Scales barcode up or down, (RECOMMENDEND: 1.00)"));
		ChangeListener<Double> cboScaleChanged = new ChangeListener<Double>() {
			@Override
			public void changed(ObservableValue<? extends Double> observable, Double
					oldValue, Double newValue) {
				
				SCALE = (float)newValue.doubleValue();
				Utils.log("Scale: " + SCALE, Level.INFO, this);
			}
		};
		cboScale.valueProperty().addListener(cboScaleChanged);
		cboScale.setValue(1.00d);
		
		gpSize.add(cboScale, 1, 0);
		gpSize.add(new Label("Dots per mm: " + DPMM), 0, 1);
		tpSize.setContent(gpSize);
		return tpSize;
	}
	
	/**
	 * Generates the container and color picker for the generator color settings
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
		cpForeground = new ColorPicker();
		cpForeground.setTooltip(new Tooltip("Changes foreground color (RECOMMENDEND: Black)"));
		ChangeListener<Color> cpForegroundChanged = new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color
					oldValue, Color newValue) {
				foreground = newValue;
				Utils.log("Foregound color: " + newValue.toString(), Level.CONFIG, this);
			}
		};
		cpForeground.valueProperty().addListener(cpForegroundChanged);
		cpForeground.setValue(Color.BLACK);
		gpColor.add(cpForeground, 0, 1);
		
		gpColor.add(new Label("Background: "), 0, 2);
		cpBackground = new ColorPicker();
		cpBackground.setTooltip(new Tooltip("Changes background color (RECOMMENDEND: White)"));
		ChangeListener<Color> cpBackgroundChanged = new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color
					oldValue, Color newValue) {
				
				background = newValue;
				Utils.log("Background color: " + newValue.toString(), Level.CONFIG, this);
			}
		};
		cpBackground.valueProperty().addListener(cpBackgroundChanged);
		cpBackground.setValue(Color.WHITE);
		gpColor.add(cpBackground, 0, 3);
		
		tpColor.setContent(gpColor);
		return tpColor;
	}
	
	/**
	 * Generates the container and radio buttons for optional settings
	 * @param insets padding between the elementes
	 * @return container with gui elements
	 */
	private Node generateOptionalSettings(Insets insets){
		
		TitledPane tpOptionals = new TitledPane();
		tpOptionals.setText("Optionals");
		tpOptionals.setCollapsible(true);
		
		GridPane gpOptionals = new GridPane();
		gpOptionals.setVgap(insets.getTop());
		gpOptionals.setHgap(insets.getLeft());
		
		cbRetard = new CheckBox("Strike trough");
		cbRetard.setTooltip(new Tooltip("Adds grounding to barcode"));
		ChangeListener<Boolean> retardChanged = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean
					oldValue, Boolean newValue) {
				
				retard = newValue;
				Utils.log("Retard-mode: " + newValue, Level.CONFIG, this);
			}
		};
		cbRetard.selectedProperty().addListener(retardChanged);
		cbRetard.setSelected(false);
		gpOptionals.add(cbRetard, 0, 0);
		
		cbDebug = new CheckBox(FORCE_DEBUG ? "DEBUG FORCED": "Debug");
		cbDebug.setTooltip(new Tooltip("Enables debug mode (NOT RECOMMENDED)"));
		ChangeListener<Boolean> cbDebugChanged = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean
					oldValue, Boolean newValue) {
				
				if(!FORCE_DEBUG){
					
					debug = true;
				}
				else {
					
					debug = newValue;
				}
				
				Utils.log("DEBGU-MODE: " + debug + " FORCED: " + FORCE_DEBUG,
				          Level.SEVERE,
				          this);
			}
		};
		cbDebug.selectedProperty().addListener(cbDebugChanged);
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
