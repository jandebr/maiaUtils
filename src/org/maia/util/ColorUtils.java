package org.maia.util;

import java.awt.Color;
import java.util.List;

public class ColorUtils {

	private static float[] rgbaComps = new float[4];

	private static float[] hsbComps = new float[3];

	private ColorUtils() {
	}

	public static double getHue(Color color) {
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComps);
		return hsbComps[0];
	}

	public static double getSaturation(Color color) {
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComps);
		return hsbComps[1];
	}

	public static double getBrightness(Color color) {
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComps);
		return hsbComps[2];
	}

	public static Color adjustBrightness(Color color, double factor) {
		if (factor == 0)
			return color;
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComps);
		double brightness = hsbComps[2];
		double darkness = 1.0 - brightness;
		if (factor >= 0) {
			// increase brightness
			brightness = 1.0 - darkness * (1.0 - factor);
		} else {
			// increase darkness
			darkness = 1.0 - brightness * (1.0 + factor);
			brightness = 1.0 - darkness;
		}
		int rgba = (color.getAlpha() << 24)
				| (Color.HSBtoRGB(hsbComps[0], hsbComps[1] * (float) (Math.min(1.0, 1.0 - factor)), (float) brightness)
						& 0x00ffffff);
		return new Color(rgba, true);
	}

	public static Color adjustSaturation(Color color, double factor) {
		if (factor == 0)
			return color;
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComps);
		double saturation = hsbComps[1];
		double grayness = 1.0 - saturation;
		if (factor >= 0) {
			// increase saturation
			saturation = 1.0 - grayness * (1.0 - factor);
		} else {
			// increase grayness
			grayness = 1.0 - saturation * (1.0 + factor);
			saturation = 1.0 - grayness;
		}
		int rgba = (color.getAlpha() << 24)
				| (Color.HSBtoRGB(hsbComps[0], (float) saturation, hsbComps[2]) & 0x00ffffff);
		return new Color(rgba, true);
	}

	public static boolean isFullyTransparent(Color color) {
		return color.getAlpha() == 0;
	}

	public static boolean isFullyOpaque(Color color) {
		return color.getAlpha() == 255;
	}

	public static double getTransparency(Color color) {
		return 1.0 - color.getAlpha() / 255.0;
	}

	public static Color setTransparency(Color color, double transparency) {
		if (transparency == 0) {
			if (color.getAlpha() == 255) {
				return color;
			} else {
				return new Color(color.getRGB() | 0xff000000, true);
			}
		} else if (transparency == 1.0) {
			if (color.getAlpha() == 0) {
				return color;
			} else {
				return new Color(color.getRGB() & 0x00ffffff, true);
			}
		} else {
			color.getRGBColorComponents(rgbaComps);
			rgbaComps[3] = (float) (1.0 - transparency);
			return new Color(rgbaComps[0], rgbaComps[1], rgbaComps[2], rgbaComps[3]);
		}
	}

	public static Color combineByTransparency(List<Color> colors) {
		Color color = null;
		if (colors.size() == 1) {
			color = colors.get(0);
		} else if (colors.size() > 1) {
			Color c0 = colors.get(0);
			Color c1 = combineByTransparency(colors.subList(1, colors.size()));
			color = combineByTransparency(c0, c1);
		}
		return color;
	}

	public static Color combineByTransparency(Color frontColor, Color backColor) {
		if (isFullyOpaque(frontColor))
			return frontColor;
		double alpha = frontColor.getAlpha() / 255.0;
		double beta = 1.0 - alpha;
		double gamma = 1.0 - backColor.getAlpha() / 255.0;
		int red = (int) Math.round(alpha * frontColor.getRed() + beta * backColor.getRed());
		int green = (int) Math.round(alpha * frontColor.getGreen() + beta * backColor.getGreen());
		int blue = (int) Math.round(alpha * frontColor.getBlue() + beta * backColor.getBlue());
		int al = (int) Math.round(255.0 * (1.0 - beta * gamma));
		return new Color(red, green, blue, al);
	}

	public static Color interpolate(Color from, Color to, double ratio) {
		double rev = 1.0 - ratio;
		int red = (int) Math.round(rev * from.getRed() + ratio * to.getRed());
		int green = (int) Math.round(rev * from.getGreen() + ratio * to.getGreen());
		int blue = (int) Math.round(rev * from.getBlue() + ratio * to.getBlue());
		int alpha = (int) Math.round(rev * from.getAlpha() + ratio * to.getAlpha());
		return new Color(red, green, blue, alpha);
	}

}