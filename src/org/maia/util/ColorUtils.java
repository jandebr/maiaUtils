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
		return new Color(adjustBrightness(color.getRGB(), factor), true);
	}

	public static int adjustBrightness(int rgb, float factor) {
		if (factor == 0f)
			return rgb;
		Color.RGBtoHSB((rgb >>> 16) & 0xff, (rgb >>> 8) & 0xff, rgb & 0xff, hsbComps);
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
		return (rgb & 0xff000000)
				| (Color.HSBtoRGB(hsbComps[0], hsbComps[1] * Math.min(1f, 1f - factor), brightness) & 0x00ffffff);
	}

	public static Color adjustSaturation(Color color, float factor) {
		return new Color(adjustSaturation(color.getRGB(), factor), true);
	}

	public static int adjustSaturation(int rgb, float factor) {
		if (factor == 0f)
			return rgb;
		Color.RGBtoHSB((rgb >>> 16) & 0xff, (rgb >>> 8) & 0xff, rgb & 0xff, hsbComps);
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
		return (rgb & 0xff000000) | (Color.HSBtoRGB(hsbComps[0], saturation, hsbComps[2]) & 0x00ffffff);
	}

	public static Color adjustSaturationAndBrightness(Color color, float saturationFactor, float brightnessFactor) {
		return new Color(adjustSaturationAndBrightness(color.getRGB(), saturationFactor, brightnessFactor), true);
	}

	public static int adjustSaturationAndBrightness(int rgb, float saturationFactor, float brightnessFactor) {
		if (saturationFactor != 0f || brightnessFactor != 0f) {
			Color.RGBtoHSB((rgb >>> 16) & 0xff, (rgb >>> 8) & 0xff, rgb & 0xff, hsbComps);
			if (saturationFactor != 0f) {
				float saturation = hsbComps[1];
				float grayness = 1f - saturation;
				if (saturationFactor >= 0) {
					// increase saturation
					saturation = 1f - grayness * (1f - saturationFactor);
				} else {
					// increase grayness
					grayness = 1f - saturation * (1f + saturationFactor);
					saturation = 1f - grayness;
				}
				hsbComps[1] = saturation;
			}
			if (brightnessFactor != 0f) {
				float brightness = hsbComps[2];
				float darkness = 1f - brightness;
				if (brightnessFactor >= 0) {
					// increase brightness
					brightness = 1f - darkness * (1f - brightnessFactor);
				} else {
					// increase darkness
					darkness = 1f - brightness * (1f + brightnessFactor);
					brightness = 1f - darkness;
				}
				hsbComps[1] *= Math.min(1f, 1f - brightnessFactor);
				hsbComps[2] = brightness;
			}
			rgb = (rgb & 0xff000000) | (Color.HSBtoRGB(hsbComps[0], hsbComps[1], hsbComps[2]) & 0x00ffffff);
		}
		return rgb;
	}

	public static boolean isFullyTransparent(Color color) {
		return color.getAlpha() == 0;
	}

	public static boolean isFullyOpaque(Color color) {
		return color.getAlpha() == 255;
	}

	public static float getTransparency(Color color) {
		return getTransparency(color.getRGB());
	}

	public static float getTransparency(int rgb) {
		return 1f - ((rgb >>> 24) & 0xff) / 255f;
	}

	public static Color setTransparency(Color color, float transparency) {
		if (transparency == 0f && color.getAlpha() == 255) {
			return color;
		} else if (transparency == 1f && color.getAlpha() == 0) {
			return color;
		} else {
			return new Color(setTransparency(color.getRGB(), transparency), true);
		}
	}

	public static int setTransparency(int rgb, float transparency) {
		int alpha = Math.round((1f - transparency) * 255f);
		return alpha << 24 | (rgb & 0x00ffffff);
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