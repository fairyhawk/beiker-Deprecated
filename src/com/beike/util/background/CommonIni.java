package com.beike.util.background;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

public class CommonIni {

	private static String[] emailParameter = null;
	protected static Properties commonIni = new Properties();

	private static BigDecimal i99Price = new BigDecimal("99.00");
	static {
		FileInputStream fileinput = null;
		try {
			fileinput = new FileInputStream(getRootPath()
					+ "/CommonIni.properties");
			commonIni.load(fileinput);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileinput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 采用相对路径法读取配置文件，方便移植到不同的机器上
	 * 首先用java.lang.Class装载一个LogVo类然后实例化，之后用这个实例化取得装载类的根路径
	 * ，就是web-inf下面的classes下的路径
	 * 
	 * @return String
	 */
	private static String getRootPath() {
		String path = null;
		Class cls = null;

		try {
			cls = Class.forName(LogVo.class.getName());
			LogVo gl = (LogVo) cls.newInstance();
			java.net.URL abspath = gl.getClass().getClassLoader()
					.getResource("com/beiker/");
			path = (abspath.getPath()).toString();
			if (path != null)
				path = path.substring(0, path.indexOf("WEB-INF") + 8);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}

		if (path != null && path.startsWith("file:")) {
			path = path.substring(5, path.length());
		}

		return path == null ? "./" : path;
	}

	public static String getProperty(String key) {
		if (key == null) {
			return null;
		} else {
			return commonIni.getProperty(key);
		}
	}

	static {
		emailParameter = new String[7];
		emailParameter[0] = "stmp";
		emailParameter[1] = commonIni.getProperty("emailserver");
		emailParameter[2] = commonIni.getProperty("emailport");
		emailParameter[3] = commonIni.getProperty("emailauth");
		emailParameter[4] = commonIni.getProperty("emailuser");
		emailParameter[5] = commonIni.getProperty("emailpassword");
		emailParameter[6] = commonIni.getProperty("emailuserNickname");
		try {
			i99Price = new BigDecimal(commonIni.getProperty("i99"));
		} catch (Exception e) {

		}

	}

	public static String[] getEmailParameter() {
		return emailParameter;
	}

	public static BigDecimal getI99Price() {
		return i99Price;
	}

}
