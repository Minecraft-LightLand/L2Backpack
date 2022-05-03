package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScalePic {

	public static void main(String[] args) throws IOException {
		File in = new File("./in.png");
		BufferedImage img = ImageIO.read(in);
		BufferedImage ans = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < 16; y++) {
				int col = img.getRGB(x, y);
				if (col >> 24 == 0){
					col = 0xFFFFFFFF;
				}
				for (int i = 0; i < 25; i++)
					for (int j = 0; j < 25; j++) {
						ans.setRGB(x * 25 + i, y * 25 + j, col);
					}
			}
		File out = new File("./out.png");
		if (!out.exists()) {
			out.createNewFile();
		}
		ImageIO.write(ans, "PNG", out);
	}

}
