import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Code provided for PS-1
 * Webcam-based drawing
 * Dartmouth CS 10, Summer 2025
 *
 * @author Tim Pierson, Dartmouth CS10, Winter 2025 (based on CamPaint from previous terms)

 * @author Alex Tang, Dartmouth CS10, Fall 2025 (building upon the scaffold)
 */

public class CamPaint extends VideoGUI {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Set painting to white to start
		Color white = new Color(255, 255, 255);

		for (int x = 0; x < painting.getWidth(); x++) {
			for (int y = 0; y < painting.getHeight(); y++) {
				painting.setRGB(x, y, white.getRGB());
			}
		}
	}

	/**
	 * VideoGUI method, here drawing one of live webcam, recolored image, or painting,
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void handleImage() {
        // TODO: YOUR CODE HERE
        if (targetColor != null) {
            // Run region finder on current webcam frame
            finder.setImage(image);
            finder.findRegions(targetColor);
            finder.recolorImage();

            // Use largest region as the brush
            ArrayList<Point> region = finder.largestRegion();
            if (region != null) {
                for (Point p : region) {
                    if (p.x >= 0 && p.x < painting.getWidth() && p.y >= 0 && p.y < painting.getHeight()) {
                        painting.setRGB(p.x, p.y, paintColor.getRGB());
                    }
                }
            }
        }
        if (displayMode == 'w') {
            setImage1(image);
        } else if (displayMode == 'r') {
            setImage1(finder.getRecoloredImage());
        } else if (displayMode == 'p') {
            setImage1(painting);
        }
    }


	/**
	 * Overrides the Webcam method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
        if (image == null) return;   // don’t crash if no frame yet
        targetColor = new Color(image.getRGB(x, y));
	}

	/**
	 * Webcam method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			ImageIOLibrary.saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			ImageIOLibrary.saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		new CamPaint();
	}
}
