package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;
import MMS.Project.BCodeGen.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
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
		private boolean supHRP;
		private Color foreground = Color.BLACK;
		private Color background = Color.WHITE;
		private Color debugMarker = Color.PURPLE;
		
		// region debug Options
	
		private static final Boolean FORCE_DEBUG = false;
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
	   private CheckBox cbSupHRP;
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
		private static final float SPACER = 0.33f;
		private static final float TEXTHEIGTH = 2.75f;
		private static final float EXTENSION = 1.95f;
		private static final float DPMM = 33.3f;
	
		private static final String START = "101";
		private static final String END = "101";
		private static final String SEPARATOR = "01010";
	
		private Font defaultFont;
		
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
		
		System.out.println("Creating: " + this.getClass().getName());
		properties = generateMandatoryProperties(new Insets(5, 5, 5, 5));
		
		try {
			
			defaultFont = Font.createFont(Font.TRUETYPE_FONT, new File("OcrB Regular.ttf"));
			Utils.log("Loaded font: " + defaultFont.getFontName(), Level.INFO, this);
		}
		catch(Exception e){
			
			Utils.logErr("Loading backup font", Level.SEVERE, this);
			Utils.logEx(e, Level.SEVERE, this);
			
			defaultFont = new Font("Monospaced", Font.PLAIN, 12);
			Utils.logErr("Loaded backup font: " + defaultFont.getFontName(), Level.INFO, this);
		}
	}
	
	@Override
	public Image runGenerator() throws Exception {
		
		Utils.log("Running generator", Level.INFO, this);
		
		try {
			
			isValid(data);
		}
		catch(Exception e){
			
			throw new RuntimeException(e.getMessage());
		}
		
		String cleanData = data.substring(0, digits);
		Utils.log("Data         : " + cleanData, Level.INFO, this);
		
		if(digits != 8){
			
			cleanData += getChecksum(cleanData);
			Utils.log("Data /w check: " + cleanData, Level.INFO, this);
		}
		
		String rawData = getRaw(cleanData);
		Utils.log("Raw data     : " + rawData, Level.INFO, this);
		
		return render(rawData, cleanData);
	}
	
	// region Private methods
	
	/**
	 * Checks if data is valid, if not thwows exception
	 * @param data unchecked data
	 * @return valid/invalid
	 */
	protected boolean isValid(String data) throws Exception{
		
		if(strict && data.length() != digits){
			
			throw new IllegalArgumentException("Length needs to match digits");
		}
		
		if(!strict && data.length()<digits){
			
			throw new IllegalArgumentException("Data is to short to be processed further");
		}
		
		for(char c: data.toCharArray()){
			
			if(!Character.isDigit(c)){
				
				throw new IllegalArgumentException("Data contains non numerical character");
			}
		}
		
		return true;
	}
	
	/**
	 * Calculated the checksum for the EAN 8 barcode
	 * @param data checked, valid data
	 * @return checksum
	 */
	protected int getChecksum(String data){
		
		int mul = 3;
		int sum = 0;
		
		for(int i = 0; i < data.length(); i++){
			
			sum += Integer.parseInt("" + data.charAt(i) * mul);
			mul = (mul == 3) ? 1 : 3;
		}
		
		int nextMulOfTen = (sum + 9) - ((sum + 9) % 10);
		return nextMulOfTen - sum;
	}
	
	/**
	 * generates raw data from checked data with checksum
	 * @param data data with checksum
	 * @return raw binary string
	 */
	protected String getRaw(String data){
		
		StringBuilder raw = new StringBuilder();
		
		raw.append(START);
		
		for(int i = 0; i < data.length()/2; i++){
			
			raw.append(L_CODE.get(Integer.parseInt("" + data.charAt(i))));
		}
		
		raw.append(SEPARATOR);
		
		for(int i = data.length()/2; i < data.length(); i++){
			
			raw.append(R_CODE.get(Integer.parseInt("" + data.charAt(i))));
		}
		
		raw.append(END);
		
		return raw.toString();
	}
	
	/**
	 * Renders barcode from raw data
	 * @param raw data from raw generator
	 * @return
	 */
	protected Image render(String raw, String text){
		
		Utils.log("Starting rendering", Level.INFO, this);
		
		Stack<Stroke> strokes = new Stack<>();
		
		float[] dash = {10.0f};
		BasicStroke debugStroke = new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		
		
		// region Convert colors
		java.awt.Color debugPen = new java.awt.Color((float)debugMarker.getRed(),
		                                             (float)debugMarker.getGreen(),
		                                             (float)debugMarker.getBlue(),
		                                             (float)debugMarker.getOpacity());
		
		java.awt.Color forePen = new java.awt.Color( (float)foreground.getRed(),
		                                             (float)foreground.getGreen(),
		                                             (float)foreground.getBlue(),
		                                             (float)foreground.getOpacity());
		
		java.awt.Color backPen = new java.awt.Color( (float)background.getRed(),
		                                             (float)background.getGreen(),
		                                             (float)background.getBlue(),
		                                             (float)background.getOpacity());
		
		// endregion
		
		// region Calculate actual sizes in px
		
		int width = Math.round(WIDTH * SCALE * DPMM);
		int heigth = Math.round(HEIGTH * SCALE * DPMM);
		int margin = Math.round(MARGIN * SCALE * DPMM);
		int extension = Math.round(EXTENSION * SCALE * DPMM);
		
		// endregion
		
		// Calculate width of an individial bit
		float bit = Math.round((width/(float)raw.length()) * 100.0f) / 100.0f;
		
		if(debug){
			
			Utils.log("Pre-rendering report START", Level.INFO, this);
			Utils.log("Width : " + width, Level.INFO, this);
			Utils.log("Heigth: " + heigth, Level.INFO, this);
			Utils.log("Margin: " + margin, Level.INFO, this);
			Utils.log("Bit   : " + bit, Level.INFO, this);
			Utils.log("Raw   : " + raw.length(), Level.INFO, this);
			Utils.log("Bit's : " + (bit * raw.length()), Level.INFO, this);
			Utils.log("Delta : " + Math.round((width - (bit * raw.length())) * 100d)/100d, Level.INFO, this);
			Utils.log("Pre-rendering report END", Level.INFO, this);
		}
		
		// region Create array of special indizes for heigth variation later
		int l = raw.length() - 1;
		int[] specIDX = new int[]{0, 2, l/2 - 1, l/2 +1, l - 2, l};
		
		// Create image and grafics obj, set background to backPan
		BufferedImage image = new BufferedImage(width + 2 * margin,
		                                        heigth + 2* margin,
		                                        BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		
		// endregion
		
		g2d.setColor(backPen);
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		if(debug){
			
			g2d.setColor(debugPen);
			strokes.push(g2d.getStroke());
			g2d.setStroke(debugStroke);
			
			g2d.drawRect(0,0,2 * margin + width - 1, 2 * margin + heigth - 1);
			g2d.drawLine(margin, 0, margin, heigth + 2 * margin);
			g2d.drawLine(width + margin, 0, width + margin, heigth + 2 * margin);
			g2d.drawLine(0, margin, width + 2 * margin, margin);
			g2d.drawLine(0, heigth + margin, width + 2 * margin, heigth + margin);
			g2d.setStroke(strokes.pop());
		}
		
		float endY;
		
		for(int i = 0; i < raw.length(); i++){
		
			char c = raw.charAt(i);
			
			if(c == '1'){
				
				final int cmp = i;
				
				endY = (IntStream.of(specIDX).anyMatch(j -> j == cmp)) ? (heigth + extension) : heigth;
				
				for(int x = 0; x < bit; x++){
					
					g2d.setColor(forePen);
					g2d.drawLine((int)(margin + i * bit + x),
					             margin,
					             (int)(margin + i * bit + x),
					             (int)(endY + margin));
				}
				
				if(debug){
					
					g2d.setColor(debugPen);
					strokes.push(g2d.getStroke());
					g2d.setStroke(debugStroke);
					
					g2d.drawRect((int)(margin + i * bit),
					             margin,
					             (int)bit,
					             (int)endY);

					g2d.setStroke(strokes.pop());
				}
			}
		}
		
		if(!supHRP){
			
			int spacer = Math.round(SPACER * SCALE * DPMM);
			Font f = defaultFont.deriveFont(TEXTHEIGTH * SCALE * DPMM);
			
			g2d.setFont(f);
			FontMetrics fm = g2d.getFontMetrics();
			
			if(debug){
				
				Utils.log("Printing human readable representation", Level.INFO, this);
				g2d.setColor(debugPen);
				strokes.push(g2d.getStroke());
				g2d.setStroke(debugStroke);
				
				g2d.drawLine(0,
				             margin + heigth,
				             image.getWidth(),
				             margin + heigth);
				
				g2d.drawLine(0,
				             margin + heigth + fm.getHeight(),
				             image.getWidth(),
				             margin + heigth + fm.getHeight());
				
				g2d.drawLine((int)(margin + START.length() * bit + spacer),
				             heigth + margin,
				             (int)(margin + START.length() * bit + spacer),
				             heigth + margin + fm.getHeight());
				
				g2d.drawLine((int)(margin + width - START.length() * bit - spacer),
				             heigth + margin,
				             (int)(margin + width - START.length() * bit - spacer),
				             heigth + margin + fm.getHeight());
				
				g2d.drawString(text, 0, image.getHeight());
				
				g2d.setStroke(strokes.pop());
			}
			
			char[] txt = text.toCharArray();
			
			for(int i = 0; i < txt.length; i++){
				
				g2d.setColor(forePen);
				
				int addon = i >= 4 ? (int)(SEPARATOR.length() * bit) : 0;
				g2d.drawString(txt[i] + "",
				               (int)(margin + START.length() * bit + spacer + (i * 7 * bit) + addon),
				               heigth + margin + fm.getHeight());
			}
			
		}
		
		if(retard){
			
			g2d.setColor(forePen);
			g2d.fillRect((int)(0.75 * margin),
			           (int)(1.75 * margin),
			           (int)(width + 0.50 * margin),
			           (int)(0.10 * margin));
		}
		
		// Dispose graphics obj. and convert buffered image to javafx image
		g2d.dispose();
		WritableImage barcode = new WritableImage(image.getWidth(), image.getHeight());
		SwingFXUtils.toFXImage(image, barcode);
		return barcode;
	}
	
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
	protected Node generateMandatoryProperties(Insets insets){
		
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
				
				if(newValue.length() >= 7 && newValue.length() <= 8) {
					data = newValue;
					Utils.log("Data: " + newValue, Level.INFO, this);
				}
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
	protected Node generateDigitSettings(Insets insets){
		
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
	protected Node generateModeSettings(Insets insets){
		
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
	protected Node generateSizeSettings(Insets insets){
	
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
	protected Node generateColorSettings(Insets insets){
		
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
	protected Node generateOptionalSettings(Insets insets){
		
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
		
		cbSupHRP = new CheckBox("Suppress HRR");
		cbSupHRP.setTooltip(new Tooltip("Suppress human readable representation"));
		ChangeListener<Boolean> hrpChanged = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean
					oldValue, Boolean newValue) {
				
				supHRP = newValue;
				Utils.log("Suppres hrp-mode: " + newValue, Level.CONFIG, this);
			}
		};
		cbSupHRP.selectedProperty().addListener(hrpChanged);
		cbSupHRP.setSelected(false);
		gpOptionals.add(cbSupHRP, 0, 1);
		
		cbDebug = new CheckBox(FORCE_DEBUG ? "DEBUG FORCED": "Debug");
		cbDebug.setTooltip(new Tooltip("Enables debug mode (NOT RECOMMENDED)"));
		ChangeListener<Boolean> cbDebugChanged = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean
					oldValue, Boolean newValue) {
				
				if(FORCE_DEBUG){
					
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
		gpOptionals.add(cbDebug, 0, 2);
		
		tpOptionals.setContent(gpOptionals);
		return tpOptionals;
	}
	
	// endregion
	
	@Override
	public String toString(){
		
		return "EAN 8";
	}
}
