import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class AddTextToImage {

    public static void main(String[] args) {

        String configFilePath = args.length > 0 ? args[0] : "config.properties";
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found: " + configFilePath);
            return;
        } catch (IOException e) {
            System.err.println("Error reading config file: " + e.getMessage());
            return;
        }

        // Get the input directory path or use the default value
        String inputDirPath = properties.getProperty("textToImage.directoryToTxtFiles", "src/main/resources/textFiles");
        File inputDir = new File(inputDirPath);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.err.println("Input directory not found: " + inputDirPath);
            return;
        }

        // Get the image file path or use the default value
        String imageFilePath = properties.getProperty("textToImage.backgroundImagePath",
                "src/main/resources/backgroundImage/backgroundImage.JPG");
        File imageFile = new File(imageFilePath);
        if (!imageFile.exists() || imageFile.isDirectory()) {
            System.err.println("Image file not found: " + imageFilePath);
            return;
        }
        // Save the image
        String outputFilePath = properties.getProperty("textToImage.outputFilePath",
                "output/");

        File[] inputFiles = inputDir.listFiles();
        if (inputFiles == null) {
            System.err.println("No files found in input directory: " + inputDirPath);
            return;
        }

        // Process each input file
        for (File inputFile : inputFiles) {
            try {
                // Load the image
                BufferedImage image = ImageIO.read(imageFile);

                // Create a graphics context
                Graphics2D g = image.createGraphics();

                // Choose a font and size
                Font font = new Font("Arial", Font.PLAIN, 84);
                g.setFont(font);

                Scanner scanner = new Scanner(inputFile);
                // Read the text from the file
                String text = "";
                while (scanner.hasNextLine()) {
                    text += scanner.nextLine() + "\n";
                }
                scanner.close();
                String[] lines = text.split("\\n");
                // Get the size of the text
                int textHeight = g.getFontMetrics().getHeight() + 40;

                // Calculate the position of the text
                int x = (image.getWidth()) / 2;

                // Set the color of the text
                g.setColor(Color.WHITE);

                // Add the text to the image
                int y = (image.getHeight() - textHeight * lines.length) / 2 + g.getFontMetrics().getAscent();
                for (String line : lines) {
                    int textWidth = g.getFontMetrics().stringWidth(line);
                    int lineX = x - textWidth / 2;
                    g.drawString(line, lineX, y);
                    y += textHeight;
                }

                File outputDir = new File(outputFilePath);
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                File outputFile = new File(outputFilePath + "/" + inputFile.getName() + ".jpg");
                ImageIO.write(image, "jpg", outputFile);
            } catch (IOException e) {
                System.err.println("Error processing file: " + inputFile.getName() + ", " + e.getMessage());
            }
        }
    }
}
