import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Code provided for PS-1
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Dartmouth CS 10, Summer 2025
 *
 * @author Tim Pierson, Dartmouth CS10, Winter 2025, based on prior terms RegionFinder

 * @author Alex Tang, Dartmouth CS10, Fall 2025 (building upon the scaffold)
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
        // TODO: YOUR CODE HERE
        //Good to check if there is actual image
        if (image == null) return;
        int w = image.getWidth();
        int h = image.getHeight();
        regions = new ArrayList<>();//Using an arraylist to keep track of the regions

        boolean[][] visited = new boolean[w][h]; //2d array for
        //Looping through all the pixels, skip that pixel if it's been visited
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (visited[x][y]) continue;

                Color c = new Color(image.getRGB(x, y));//stores the color rgb values
                if (!colorMatch(c, targetColor)) continue; // skip if color isn't close enough

                ArrayList<Point> region = new ArrayList<>(); //empty list to hold this region's pixels
                checkPoints(x,y,targetColor, visited, region); //every neighbor of the pixel gets added to region
                if (region.size()>=minRegion){ //if region is big enough, add region to the overall regions arraylist
                    regions.add(region);
                }
            }
        }
    }
    public void checkPoints(int startX, int startY, Color target, boolean[][] visited, ArrayList<Point> region) {
        int w = image.getWidth(), h = image.getHeight();

        if (startX < 0 || startY < 0 || startX >= w || startY >= h) return; //checks if parameters are in bounds
        if (visited[startX][startY]) return; //if already visited no need to check

        Color startC = new Color(image.getRGB(startX, startY)); //gets the rgb
        if (!colorMatch(startC, target)) return;  //must match the target color

        ArrayList<Point> toVisit = new ArrayList<>();
        toVisit.add(new Point(startX, startY)); //que of pixels to process
        visited[startX][startY] = true; //immediately sets it to true

        int head = 0; //uses a while loop that continues until the region is completed
        while (head < toVisit.size()) {
            Point p = toVisit.get(head++);
            region.add(p);

            int x = p.x, y = p.y;

            // loop over ALL neighbors including diagonals
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dy == 0) continue; // skip self

                    int nx = x + dx, ny = y + dy;
                    if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                    if (visited[nx][ny]) continue;

                    Color c = new Color(image.getRGB(nx, ny));
                    if (colorMatch(c, target)) {
                        visited[nx][ny] = true;
                        toVisit.add(new Point(nx, ny));//adds the points to toVisit so it can continue running (goes back to the while loop) until all points are done
                    }
                }
            }
        }
    }


    /**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	protected static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
        int colordistance;
        int difred =  c1.getRed() - c2.getRed();
        int difgreen =  c1.getGreen() - c2.getGreen();
        int difblue =  c1.getBlue() - c2.getBlue();
        colordistance = difred*difred + difgreen *difgreen + difblue * difblue;
        int totaldiff =maxColorDiff * maxColorDiff;
        return colordistance <= totaldiff;
	}  //uses actual mathematical distance formula (the one based on pythagorean)

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
        // TODO: YOUR CODE HERE
        if (regions == null || regions.isEmpty()) return null; //check if there is a region
        ArrayList<Point> largest = regions.get(0);
        for (int i = 1; i < regions.size(); i++) {
            ArrayList<Point> current = regions.get(i);
            if (current.size() > largest.size()) {
                largest = current; //goes through all the regions and finds the largest one
            }
        }
        return largest;
    }

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE
        if (regions == null || regions.isEmpty()) return;
        Random rng = new Random();
        for (ArrayList<Point> region : regions) {
            int r = rng.nextInt(256);
            int g = rng.nextInt(256);
            int b = rng.nextInt(256);
            int rgb = new Color(r, g, b).getRGB();

            for (int i = 0; i < region.size(); i++) { //could probably do a for Point P : region) but i like c style
                Point p = region.get(i);
                recoloredImage.setRGB(p.x, p.y, rgb);
            }
        }
    }
}
