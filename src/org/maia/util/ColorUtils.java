package org.maia.util;

import java.awt.Color;
import java.util.List;

public class ColorUtils {

	private static float[] rgbaComps = new float[4];

	private static float[] hsbComps = new float[3];

	private ColorUtils() {
	}

	public static float getHue(Color color) {
		return getHue(color.getRGB());
	}

	public static float getHue(int rgb) {
		Color.RGBtoHSB((rgb >>> 16) & 0xff, (rgb >>> 8) & 0xff, rgb & 0xff, hsbComps);
		return hsbComps[0];
	}

	public static float getSaturation(Color color) {
		return getSaturation(color.getRGB());
	}

	public static float getSaturation(int rgb) {
		Color.RGBtoHSB((rgb >>> 16) & 0xff, (rgb >>> 8) & 0xff, rgb & 0xff, hsbComps);
		return hsbComps[1];
	}

	public static float getBrightness(Color color) {
		return getBrightness(color.getRGB());
	}

	public static float getBrightness(int rgb) {
		Color.RGBtoHSB((rgb >>> 16) & 0xff, (rgb >>> 8) & 0xff, rgb & 0xff, hsbComps);
		return hsbComps[2];
	}

	public static Color adjustBrightness(Color color, float factor) {
		if (factor == 0)
			return color;
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComps);
		float brightness = hsbComps[2];
		float darkness = 1f - brightness;
		if (factor >= 0) {
			// increase brightness
			brightness = 1f - darkness * (1f - factor);
		} else {
			// increase darkness
			darkness = 1f - brightness * (1f + factor);
			brightness = 1f - darkness;
		}
		int rgba = (color.getAlpha() << 24)
				| (Color.HSBtoRGB(hsbComps[0], hsbComps[1] * Math.min(1f, 1f - factor), brightness) & 0x00ffffff);
		return new Color(rgba, true);
	}

	public static Color adjustSaturation(Color color, float factor) {
		if (factor == 0)
			return color;
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComps);
		float saturation = hsbComps[1];
		float grayness = 1f - saturation;
		if (factor >= 0) {
			// increase saturation
			saturation = 1f - grayness * (1f - factor);
		} else {
			// increase grayness
			grayness = 1f - saturation * (1f + factor);
			saturation = 1f - grayness;
		}
		int rgba = (color.getAlpha() << 24) | (Color.HSBtoRGB(hsbComps[0], saturation, hsbComps[2]) & 0x00ffffff);
		return new Color(rgba, true);
	}

	public static boolean isFullyTransparent(Color color) {
		return color.getAlpha() == 0;
	}

	public static boolean isFullyOpaque(Color color) {
		return color.getAlpha() == 255;
	}

	public static float getTransparency(Color color) {
		return 1f - color.getAlpha() / 255f;
	}

	public static Color setTransparency(Color color, float transparency) {
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
			rgbaComps[3] = 1f - transparency;
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
		return new Color(combineByTransparency(frontColor.getRGB(), backColor.getRGB()), true);
	}

	public static int combineByTransparency(int frontColorRgb, int backColorRgb) {
		int frontAlpha = frontColorRgb >>> 24;
		if (frontAlpha == 255) {
			return frontColorRgb;
		} else {
			float alpha = frontAlpha / 255f;
			float beta = 1f - alpha;
			float gamma = 1f - (backColorRgb >>> 24) / 255f;
			int rgb = interpolate(frontColorRgb, backColorRgb, beta) & 0x00ffffff;
			int al = Math.round(255f * (1f - beta * gamma));
			return al << 24 | rgb;
		}
	}

	public static Color interpolate(Color from, Color to, float ratio) {
		return new Color(interpolate(from.getRGB(), to.getRGB(), ratio), true);
	}

	public static int interpolate(int fromRgb, int toRgb, float ratio) {
		float rev = 1f - ratio;
		int alpha = Math.round(rev * ((fromRgb & 0xff000000) >>> 24) + ratio * ((toRgb & 0xff000000) >>> 24));
		int red = Math.round(rev * ((fromRgb & 0xff0000) >>> 16) + ratio * ((toRgb & 0xff0000) >>> 16));
		int green = Math.round(rev * ((fromRgb & 0xff00) >>> 8) + ratio * ((toRgb & 0xff00) >>> 8));
		int blue = Math.round(rev * (fromRgb & 0xff) + ratio * (toRgb & 0xff));
		return alpha << 24 | red << 16 | green << 8 | blue;
	}

}