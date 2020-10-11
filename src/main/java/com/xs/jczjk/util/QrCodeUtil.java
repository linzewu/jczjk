package com.xs.jczjk.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class QrCodeUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeUtil.class);

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	public static File createQrCode(String url, String path) {
		try {
			Map<EncodeHintType, String> hints = Maps.newHashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 400, 400, hints);
			File file = new File(path);
			writeToFile(bitMatrix, "jpg", file);
			return file;
		} catch (Exception e) {
			LOGGER.error("二维码图片生成失败", e);
			return null;
		}
	}

	private static String generateImgName() {
		Date now = new Date();
		String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now);
		String rand = getRandom(3);
		StringBuilder sb = new StringBuilder();
		sb.append("IMG").append(time).append(rand).append(".jpg");
		return sb.toString();
	}

	/**
	 * 
	 * 生成固定位数的随机数
	 * 
	 * @param length 随机数长度
	 * @return string
	 */
	private static String getRandom(int length) {
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = '0';
		}
		String patten = new String(chars);
		int randomBase = Integer.valueOf("1" + patten);
		int rand = (int) (Math.random() * randomBase);
		DecimalFormat decimalFormat = new DecimalFormat(patten);
		return decimalFormat.format(rand);
	}

	private static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format " + format + " to " + file);
		}
	}

	private static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}
}
