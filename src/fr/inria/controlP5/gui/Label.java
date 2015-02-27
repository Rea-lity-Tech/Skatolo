/* 
 *  controlP5 is a processing gui library.
 * 
 * Copyright (C)  2006-2012 by Andreas Schlegel
 * Copyright (C)  2015 by Jeremy Laviole
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 * 
 * 
 */
package fr.inria.controlP5.gui;

import fr.inria.controlP5.ControlFont;
import fr.inria.controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

/**
 * A custom label using controlP5's BitFonts or PFont based ControlFonts.
 *
 *
 * @see controlP5.ControlFont
 * @example controllers/ControlP5Textlabel
 *
 */
public class Label implements CDrawable {

    private ControllerStyle style = new ControllerStyle();
    private ControlFont font;

    private boolean isMultiline;
    private boolean isFixedSize;
    private boolean isVisible = true;
    private boolean isColorBackground;
    private boolean isToUpperCase = true;
    private boolean changed;

    private int mainColor = 0xffffffff;
    private int colorBackground = 0xffffffff;

    private String text = "";

    // TODO: not used ? 
//    private int textHeight = 1;
    private int lineHeight = 0;
    private float offsetYratio = 0;
    private int letterSpacing = 0;

    private int height = -1;
    private int width = -1;

    private int alignX = ControlP5.LEFT;
    private int alignY = ControlP5.LEFT;
    public int textAlign = ControlP5.LEFT;

    public static int defaultPaddingX = 4;
    public static int defaultPaddingY = 4;

    public int paddingX = defaultPaddingX;
    public int paddingY = defaultPaddingY;

    protected Labeltype _myLabeltype;

    private ControlP5 cp5;

    private Label(Label theLabel) {
        text = theLabel.getText();
        isToUpperCase = theLabel.isToUpperCase();
        letterSpacing = theLabel.getLetterSpacing();
        lineHeight = theLabel.getLineHeight();
        font = theLabel.getFont();
        _myLabeltype = theLabel.getLabeltype();
    }

    public Label(ControlP5 theControlP5, String theValue) {
        init(theControlP5, theValue, 0, 0, mainColor);
    }

    public Label(ControlP5 theControlP5, String theValue, int theWidth, int theHeight, int theColor) {
        init(theControlP5, theValue, theWidth, theHeight, theColor);
    }

    private void init(ControlP5 theControlP5, String theValue, int theWidth, int theHeight, int theColor) {
        cp5 = theControlP5;
        width = theWidth;
        height = theHeight;
        text = theValue;
        mainColor = theColor;
        setLabeltype(new SinglelineLabel());
        setFont(cp5.controlFont);
        setLabeltype(new SinglelineLabel());
        set(text);
        style = new ControllerStyle();
    }

    public Label setLabeltype(Labeltype theType) {
        _myLabeltype = theType;
        return this;
    }

    public Labeltype getLabeltype() {
        return _myLabeltype;
    }

    public Label align(int[] a) {
        alignX = a[0];
        alignY = a[1];
        return this;
    }

    public Label align(int theX, int theY) {
        alignX = theX;
        alignY = theY;
        return this;
    }

    public Label alignX(int theX) {
        alignX = theX;
        return this;
    }

    public Label alignY(int theY) {
        alignY = theY;
        return this;
    }

    public int[] getAlign() {
        return new int[]{alignX, alignY};
    }

    public Label setPadding(int thePaddingX, int thePaddingY) {
        paddingX = thePaddingX;
        paddingY = thePaddingY;
        return this;
    }

    public Label setPaddingX(int thePaddingX) {
        paddingX = thePaddingX;
        return this;
    }

    public Label setPaddingY(int thePaddingY) {
        paddingY = thePaddingY;
        return this;
    }

    public void draw(PGraphics graphics, int theX, int theY, ControllerInterface<?> theController) {
        if (isVisible) {
            getLabeltype().draw(this, graphics, theX, theY, theController);
        }
    }

    @Override
    public void draw(PGraphics graphics) {
        if (isVisible) {
            font.adjust(graphics, this);
            draw(graphics, 0, 0);
        }
    }

    public void draw(PGraphics graphics, int theX, int theY) {
        if (isVisible) {
            graphics.pushMatrix();
            graphics.translate(style.marginLeft, style.marginTop);
            graphics.translate(theX, theY);

            if (isColorBackground) {

                float ww = getStyle().paddingRight + getStyle().paddingLeft;
                if (getStyle().backgroundWidth > -1) {
                    ww += style.backgroundWidth;
                } else {
                    ww += font.getWidth();
                }
                float hh = getStyle().paddingBottom + getStyle().paddingTop;
                if (getStyle().backgroundHeight > -1) {
                    hh += getStyle().backgroundHeight;
                } else {
                    hh += font.getHeight();
                }
                graphics.fill(colorBackground);
                graphics.rect(0, 1, ww, hh);
            }
            graphics.translate(style.paddingLeft, style.paddingTop);
            font.draw(graphics, this);
            graphics.popMatrix();
        }
    }

    public Label hide() {
        return setVisible(false);
    }

    public Label show() {
        return setVisible(true);
    }

    public Label setVisible(boolean theValue) {
        isVisible = theValue;
        return this;
    }

    public Label updateFont(ControlFont theFont) {
        return setFont(theFont);
    }

    public Label set(String theValue) {
        return setText(theValue);
    }

    public Label setText(String theValue) {
        text = theValue;
        setChanged(true);
        return this;
    }

    public Label setFixedSize(boolean theValue) {
        isFixedSize = theValue;
        return this;
    }

    public boolean isFixedSize() {
        return isMultiline ? false : isFixedSize;
    }

    public String getText() {
        return text;
    }

    public String getTextFormatted() {
        return getLabeltype().getTextFormatted();
    }

    public ControllerStyle getStyle() {
        return style;
    }

    public Label setWidth(int theWidth) {
        width = theWidth;
        setChanged(true);
        return this;
    }

    public Label setHeight(int theHeight) {
        height = theHeight;
        setChanged(true);
        return this;
    }

    public int getWidth() {
        return _myLabeltype.getWidth();
    }

    public int getHeight() {
        return _myLabeltype.getHeight();
    }

    public int getOverflow() {
        return getLabeltype().getOverflow();
    }

    public Label setMultiline(boolean theValue) {
        isMultiline = theValue;
        _myLabeltype = (isMultiline) ? new MultilineLabel() : new SinglelineLabel();
        return this;
    }

    public Label toUpperCase(boolean theValue) {
        isToUpperCase = theValue;
        setChanged(true);
        return this;
    }

    public ControlFont getFont() {
        return font;
    }

    public Label setFont(int theBitFontIndex) {
        ControlP5.logger.warning("BitFont is now of type PFont, use setFont(PFont) instead.");
        return this;
    }

    public Label setFont(PFont thePFont) {
        return setFont(new ControlFont(thePFont));
    }

    public Label setFont(ControlFont theFont) {
        setLineHeight(theFont.getSize());
        font = new ControlFont(theFont.getFont(), theFont.getSize());
        font.init(this);
        setChanged(true);
        return this;
    }

    public Label setSize(int theSize) {
        font.setSize(theSize);
        return this;
    }

    public boolean isChanged() {
        return changed;
    }

    public Label setChanged(boolean theValue) {
        changed = theValue;
        return this;
    }

    public int getTextHeight() {
        return font.getTextHeight();
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public Label setOffsetY(int theValue) {
        return this;
    }

    public Label setOffsetYratio(float theValue) {
        offsetYratio = theValue;
        setChanged(true);
        return this;
    }

    public float getOffsetYratio() {
        return offsetYratio;
    }

    public Label setLineHeight(int theValue) {
        lineHeight = theValue;
        setChanged(true);
        return this;
    }

    public Label setColor(int theValue, boolean theFlag) {
        setColor(theValue);
        setFixedSize(theFlag);
        return this;
    }

    public Label setColor(int theColor) {
        mainColor = theColor;
        setChanged(true);
        return this;
    }

    public int getColor() {
        return mainColor;
    }

    public Label setColorBackground(int theColor) {
        enableColorBackground();
        colorBackground = theColor;
        return this;
    }

    public Label disableColorBackground() {
        isColorBackground = false;
        return this;
    }

    public Label enableColorBackground() {
        isColorBackground = true;
        return this;
    }

    public int getLetterSpacing() {
        return letterSpacing;
    }

    public Label setLetterSpacing(int theValue) {
        letterSpacing = theValue;
        setChanged(true);
        return this;
    }

    public boolean isMultiline() {
        return isMultiline;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isToUpperCase() {
        return isToUpperCase;
    }

    protected Label copy() {
        return new Label(this);
    }

    public interface Labeltype {

        public void draw(Label theLabel, PGraphics graphics, int theX, int theY, ControllerInterface<?> theController);

        public int getWidth();

        public int getHeight();

        public int getOverflow();

        public String getTextFormatted();
    }

    public class SinglelineTextfield extends SinglelineLabel {

        public String getTextFormatted() {
            return text;
        }
    }

    public class SinglelineLabel implements Labeltype {

        private void align(PApplet theApplet, ControllerInterface<?> theController, int theAlignX, int theAlignY) {
            int x = 0;
            int y = 0;
            switch (theAlignX) {
                case (ControlP5.CENTER):
                    x = (theController.getWidth() - font.getWidth()) / 2;
                    break;
                case (ControlP5.LEFT):
                    x = paddingX;
                    break;
                case (ControlP5.RIGHT):
                    x = theController.getWidth() - font.getWidth() - paddingX;
                    break;
                case (ControlP5.LEFT_OUTSIDE):
                    x = -font.getWidth() - paddingX;
                    break;
                case (ControlP5.RIGHT_OUTSIDE):
                    x = theController.getWidth() + paddingX;
                    break;
            }
            switch (theAlignY) {
                case (ControlP5.CENTER):
                    y = theController.getHeight() / 2 + font.getTop() - font.getCenter();
                    break;
                case (ControlP5.TOP):
                    y = 0;
                    break;
                case (ControlP5.BOTTOM):
                    y = theController.getHeight() - font.getHeight() - 1;
                    break;
                case (ControlP5.BASELINE):
                    y = theController.getHeight() + font.getTop() - 1;
                    break;
                case (ControlP5.BOTTOM_OUTSIDE):
                    y = theController.getHeight() + paddingY;
                    break;
                case (ControlP5.TOP_OUTSIDE):
                    y = -font.getHeight() - paddingY;
                    break;
            }
            theApplet.translate(x, y);
        }

        private void align(PGraphics graphics, ControllerInterface<?> theController, int theAlignX, int theAlignY) {
            int x = 0;
            int y = 0;
            switch (theAlignX) {
                case (ControlP5.CENTER):
                    x = (theController.getWidth() - font.getWidth()) / 2;
                    break;
                case (ControlP5.LEFT):
                    x = paddingX;
                    break;
                case (ControlP5.RIGHT):
                    x = theController.getWidth() - font.getWidth() - paddingX;
                    break;
                case (ControlP5.LEFT_OUTSIDE):
                    x = -font.getWidth() - paddingX;
                    break;
                case (ControlP5.RIGHT_OUTSIDE):
                    x = theController.getWidth() + paddingX;
                    break;
            }
            switch (theAlignY) {
                case (ControlP5.CENTER):
                    y = theController.getHeight() / 2 + font.getTop() - font.getCenter();
                    break;
                case (ControlP5.TOP):
                    y = 0;
                    break;
                case (ControlP5.BOTTOM):
                    y = theController.getHeight() - font.getHeight() - 1;
                    break;
                case (ControlP5.BASELINE):
                    y = theController.getHeight() + font.getTop() - 1;
                    break;
                case (ControlP5.BOTTOM_OUTSIDE):
                    y = theController.getHeight() + paddingY;
                    break;
                case (ControlP5.TOP_OUTSIDE):
                    y = -font.getHeight() - paddingY;
                    break;
            }
            graphics.translate(x, y);
        }

        public void draw(Label theLabel, PGraphics graphics, int theX, int theY, ControllerInterface<?> theController) {
            font.adjust(graphics, theLabel);
            graphics.pushMatrix();
            align(graphics, theController, alignX, alignY);
            theLabel.draw(graphics, theX, theY);
            graphics.popMatrix();
        }

        @Override
        public int getWidth() {
            return isFixedSize ? width : font.getWidth();
        }

        @Override
        public int getHeight() {
            return font.getHeight();
        }

        @Override
        public int getOverflow() {
            return -1;
        }

        @Override
        public String getTextFormatted() {
            return (isToUpperCase ? text.toUpperCase() : text);
        }
    }

    class MultilineLabel implements Labeltype {

        @Override
        public void draw(Label theLabel, PGraphics graphics, int theX, int theY, ControllerInterface<?> theController) {
            font.adjust(graphics, theLabel);
            theLabel.draw(graphics, theX, theY);
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public int getOverflow() {
            return font.getOverflow();
        }

        @Override
        public String getTextFormatted() {
            return (isToUpperCase ? text.toUpperCase() : text);
        }

    }

}
