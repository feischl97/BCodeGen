package MMS.Project.BCodeGen.Barcode;

import MMS.Project.BCodeGen.IBarcode;
import MMS.Project.BCodeGen.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.awt.*;

import java.awt.image.BufferedImage;
import java.util.logging.Level;

import javafx.scene.control.TextField;


/**
 * Created by Paul on 02.06.2017.
 */
public class DataMatrix implements IBarcode {

    private final BarcodeFormat BARCODE_TYPE = BarcodeFormat.DATA_MATRIX;

    private String data;

    private Node properties;
    private TextField tfData;
    private TextField tfMargin;
    private TextField tfBitSize;

    private final Color BLACK = Color.BLACK;
    private final Color WHITE = Color.WHITE;
    private int bitSize = 20;//size of a black/white square
    private int multiplier = 2;

    public DataMatrix() {
        System.out.println("Creating: " + this.getClass().getName());
        properties = generateMandatoryProperties(new Insets(5, 5, 5, 5));
    }


    @Override
    public Image runGenerator() throws Exception {

        DataMatrixWriter dataMatrixWriter = new DataMatrixWriter();
        BitMatrix encoded = dataMatrixWriter.encode(data, BARCODE_TYPE, 1, 1);
        int margin = multiplier*bitSize;

        //transfer BitMatrix to bigger image
        BufferedImage image = new BufferedImage(encoded.getWidth() * bitSize + margin * 2, encoded.getHeight() * bitSize + margin * 2, BufferedImage.TYPE_INT_ARGB);

        int black = BLACK.getRGB();
        int white = WHITE.getRGB();

        Graphics2D g2d = image.createGraphics();
        g2d.setPaint(new Color(white));
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
        try {
            for (int i = 0; i < encoded.getWidth(); i++) {
                for (int j = 0; j < encoded.getHeight(); j++) {
                    Boolean paintBlack = encoded.get(i, j);
                    for (int x = 0; x < bitSize && i * bitSize + x < image.getWidth(); x++) {
                        for (int y = 0; y < bitSize && j * bitSize + y < image.getHeight(); y++) {

                            if (paintBlack) {
                                image.setRGB(i * bitSize + x + margin, j * bitSize + y + margin, black);
                            } else {
                                image.setRGB(i * bitSize + x + margin, j * bitSize + y + margin, white);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        WritableImage barcode = new WritableImage(image.getWidth(), image.getHeight());
        SwingFXUtils.toFXImage(image, barcode);
        return barcode;


    }

    @Override
    public Node mandatoryProperties() {
        return properties;
    }

    protected Node generateMandatoryProperties(Insets insets) {

        Utils.log("First time generating mandatory properties",
                Level.SEVERE,
                this);

        VBox container = new VBox();
        container.setPadding(insets);
        container.getChildren().add(new Label("Data: "));

        tfData = new TextField("MMS 2017");
        tfData.setTooltip(new Tooltip("Data for DataMatrix"));

        ChangeListener<String> tfDataChanged = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                data = newValue;
            }
        };
        tfData.textProperty().addListener(tfDataChanged);
        tfDataChanged.changed(tfData.textProperty(),
                null,
                tfData.getText());

        container.getChildren().add(tfData);

        GridPane gpSettings = new GridPane();
        gpSettings.setHgap(insets.getTop());
        gpSettings.setVgap(insets.getLeft());

        gpSettings.add(generateMarginSettings(insets), 0, 0);
        gpSettings.add(generateBitSizeSettings(insets), 1, 0);

//        gpSettings.add(generateColorSettings(insets), 1, 1);
//        gpSettings.add(generateOptionalSettings(insets), 0, 2);

        container.getChildren().add(gpSettings);
        return container;

    }

    private Node generateMarginSettings(Insets insets) {

        VBox container = new VBox();
        container.getChildren().add(new Label("Margin:"));
        tfMargin = new TextField("2");
        tfMargin.setTooltip(new Tooltip("Quiet zone around the code in bit. Usually 1-4."));

        ChangeListener<String> tfMarginChanged = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    multiplier = Integer.parseInt(newValue);
                } catch (Exception e) {//if empty string or !int
                    if(!newValue.equals("")) {
                        tfMargin.setText(oldValue);
                    }
                }
            }
        };

        tfMargin.textProperty().addListener(tfMarginChanged);
        container.getChildren().add(tfMargin);
        return container;

    }

    private Node generateBitSizeSettings(Insets insets) {

        VBox container = new VBox();
        container.getChildren().add(new Label("Bitsize:"));
        tfBitSize = new TextField("20");
        tfBitSize.setTooltip(new Tooltip("How much pixel should a bit be wide? Higher = better resolution, but also requires more storage. Default 20."));

        ChangeListener<String> tfMarginChanged = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    bitSize = Integer.parseInt(newValue);
                } catch (Exception e) {//if empty string or !int
                    if(!newValue.equals("")) {
                        tfBitSize.setText(oldValue);
                    }
                }
            }
        };

        tfBitSize.textProperty().addListener(tfMarginChanged);
        container.getChildren().add(tfBitSize);
        return container;

    }

    public String toString() {
        return "DataMatrix";
    }

}