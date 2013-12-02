/*
 * Copyright 2009 Andrey Khalzov, and individual contributors as indicated by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * Created by Andrey Khalzov
 * 27.12.2008 23:21:31
 */
package org.wannatrak;

import javax.imageio.ImageIO;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class CaptchaGenerator {

    private static final String FONT = "Monospaced";
    private static final int STYLE = Font.BOLD;
    
    private String value;
    private static final int FONT_SIZE = 36;

    public CaptchaGenerator(int numOfSymbols) {
        final Random rand = new Random(System.currentTimeMillis());

        value = "";
        for (int i = 0; i < numOfSymbols; i++) {
            value += (char) ('a' + rand.nextInt(25));
        }
    }

    public void writeImageToOutputStream(
            int bgRed,
            int bgGreen,
            int bgBlue,
            int height,
            int width,
            OutputStream outputStream
    ) throws IOException {
        final BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Random rand = new Random(System.currentTimeMillis());
        final Graphics2D g = bimage.createGraphics();

        // create a random color
        final Color color = new Color(bgRed, bgGreen, bgBlue);

        // the the background to the random color to fill the
        // background and make it darker
        g.setColor(color);
        g.fillRect(0, 0, width, height);

        g.setFont(new Font(FONT, STYLE, FONT_SIZE));

        final int w = (g.getFontMetrics()).stringWidth(value);
        final int d = (g.getFontMetrics()).getDescent();
        final int a = (g.getFontMetrics()).getMaxAscent();

        int x = 0;
        int y = 0;

        // randomly set the color of the lines and just draw think at an angle
        for (int i = 0; i < 15; i++) {
           g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
           g.drawLine(x + rand.nextInt(w), y + rand.nextInt(d), width - rand.nextInt(w), height - rand.nextInt(d));
        }

        // randomly set the color and make it really bright for more readability
        g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)).darker());

        // we need to position the text in the center of the box
        x = width / 2 - w / 2;
        y = height / 2 + a / 2 - 12;

        // affine transform is used to rock the text a bit
        final AffineTransform fontAT = new AffineTransform();
        int xp = x - 2;
        // walk through each character and rotate it randomly
        for (int c = 0; c < value.length(); c++) {
            // apply a random radian either left or right (left is half since it's too far back)
            final int rotate = rand.nextInt(20);
            fontAT.rotate(rand.nextBoolean() ? Math.toRadians(rotate) : -Math.toRadians(rotate / 2));
            final Font fx = new Font(FONT, STYLE, FONT_SIZE).deriveFont(fontAT);
            g.setFont(fx);
            final String ch = String.valueOf(value.charAt(c));
            final int ht = rand.nextInt(3);
            // draw the string and move the y either up or down slightly
            g.drawString(ch, xp, y + (rand.nextBoolean() ? -ht : ht));
            // move our pointer
            xp += g.getFontMetrics().stringWidth(ch) - 4;
        }

        ImageIO.write(bimage, "png", outputStream);

        g.dispose();
    }

    /**
     * return the value to check for when the user enters it in. Make sure you
     * store this off in the session or something like a database and NOT in the
     * form of the webpage since the whole point of this exercise is to ensure that
     * only humans and not machines are entering the data.
     *
     * @return
     */
    public String getVerificationValue() {
        return this.value;
    }
}
