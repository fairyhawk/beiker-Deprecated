package com.beike.util.img;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.log4j.Logger;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ImgUtil {

	private static final Logger logger = Logger.getLogger(ImgUtil.class);
	private static final String CONTENT_CHARSET = "UTF-8";
	public static HttpClient client = null;
	public static final String PASSWD = "121SS#@!1SDSX$##!!@@#$DSS";
	private static int HTTP_APP_MAX_CONNECTION = 26;
	private static int HTTP_TOTAL_MAX_CONNECTION = 30;
	private static int CONNECTION_ESTABLISH_TIMEOUT = 10000;
	// 标识
	public static final String Flag = "flag";
	// 成功
	public static final String Flag_YES = "1";
	// 失败
	public static final String Flag_NO = "0";
	// 失败原因
	public static final String Flag_NO_MSG = "error_msg";
	// 文件存储的相对路径
	public static final String FILE_PATH = "filePath";
	// 文件名
	public static final String FILE_NAME = "fileName";
	// 文件的HTTP访问路径
	public static final String FILE_URL = "fileUrl";

	private static String img_addr = "http://172.16.21.1:8080/img/";

	/**
	 * 获取httpClient instance
	 * 
	 * @return
	 */
	static {

		if (client == null) {
			client = new HttpClient();
			HttpConnectionManager hcm = new MultiThreadedHttpConnectionManager();
			hcm.getParams().setConnectionTimeout(CONNECTION_ESTABLISH_TIMEOUT);
			hcm.getParams().setMaxConnectionsPerHost(
					client.getHostConfiguration(), HTTP_APP_MAX_CONNECTION);
			hcm.getParams().setMaxTotalConnections(HTTP_TOTAL_MAX_CONNECTION);
			client.setHttpConnectionManager(hcm);
			client.getParams().setContentCharset(CONTENT_CHARSET);
		}

		try {
			Properties properties = PropertiesLoaderUtils
					.loadAllProperties("project.properties");
			img_addr = properties.getProperty("img_addr");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws IOException {
		File file = new File("E:\\20120330165405993_185.jpg");
		// List<ImgBean> imgList = new ArrayList<ImgBean>();
		// for (int i = 0; i < 10; i++) {
		// ImgBean imgBean = new ImgBean();
		// imgBean.setIndex(i);
		// imgBean.setFile(file);
		// imgBean.setFilePath("sfasfd");
		// imgBean.addSize("220_132");
		// imgBean.addSize("300_180");
		// imgList.add(imgBean);
		// }
		// System.out.println(sendImg(imgList));
		try {
			Map<String, Object> map = sendImg("test", file, "220_132", "30_31",
					"30_32");
			map = new TreeMap<String, Object>(map);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// File file = new File(
		// "/Users/luqingrun/Downloads/20120301150032631_849.jpg");
		// // List<ImgBean> imgList = new ArrayList<ImgBean>();
		// // for (int i = 0; i < 10; i++) {
		// // ImgBean imgBean = new ImgBean();
		// // imgBean.setIndex(i);
		// // imgBean.setFile(file);
		// // imgBean.setFilePath("sfasfd");
		// // imgBean.addSize("220_132");
		// // imgBean.addSize("300_180");
		// // imgList.add(imgBean);
		// // }
		// // System.out.println(sendImg(imgList));
		// sendImg("test", file, "220_132", "30_31", "30_32");
		//
		// //
		getImg("http://c1.qianpincdn.com/test/20120406/e1da4ff24ff0fb82960cdfb538b791ee.jpg",
				"E:\\file\\不.jpg");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> sendImg(String filePath, File file,
			String... size) throws Exception {
		List<ImgBean> imgList = new ArrayList<ImgBean>();
		ImgBean imgBean = new ImgBean();
		imgBean.setFile(file);
		imgBean.setIndex(0);
		imgBean.setFilePath(filePath);
		if (size != null) {
			for (int i = 0; i < size.length; i++) {
				imgBean.addSize(size[i]);
			}
		}
		imgList.add(imgBean);
		Map<String, Object> map = sendImg(imgList);
		if (!Flag_NO.equals(String.valueOf(map.get(Flag)))) {
			return (Map<String, Object>) map.get("0");
		} else {
			throw new RuntimeException(String.valueOf(map.get(Flag_NO_MSG)));
		}
	}

	public static Map<String, Object> sendImg(List<ImgBean> imgList)
			throws IOException, Exception {
		String targetURL = img_addr + "upLoad.do";
		PostMethod filePost = new PostMethod(targetURL);
		Part[] parts = new Part[imgList.size() * 4 + 1];
		for (int i = 0; i < imgList.size(); i++) {
			File file = imgList.get(i).getFile();
			FilePart filePart = new FilePart("files", file);
			parts[i * 4] = filePart;
			Part pathPart = new StringPart("filePaths", imgList.get(i)
					.getFilePath());
			parts[i * 4 + 1] = pathPart;
			Part sizePart = new StringPart("sizes", imgList.get(i).getSize());
			parts[i * 4 + 2] = sizePart;
			Part indexPart = new StringPart("indexs", ""
					+ imgList.get(i).getIndex());
			parts[i * 4 + 3] = indexPart;
		}
		Part passwdPart = new StringPart("passwd", PASSWD);
		parts[parts.length - 1] = passwdPart;
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost
				.getParams()));
		int status = client.executeMethod(filePost);
		if (status == HttpStatus.SC_OK) {
			String str = filePost.getResponseBodyAsString();
			Map<String, Object> map = JsonUtil.getMapFromJsonString(str);
			if (Flag_YES.equals(map.get(Flag))) {
				map.remove(Flag);
				map.remove(Flag_NO_MSG);
				return map;
			} else {
				throw new RuntimeException(String.valueOf(map.get(Flag_NO_MSG)));
			}
		} else {
			throw new RuntimeException("状态码不正确:" + status);
		}
	}

	private static InputStream getImgStream(String filePath)
			throws HttpException, IOException {
		String targetURL = img_addr + "downLoad.do";
		PostMethod filePost = new PostMethod(targetURL);
		filePost.addParameter("filePath", filePath);
		filePost.addParameter("passwd", PASSWD);
		int status = client.executeMethod(filePost);
		if (status == HttpStatus.SC_OK) {
			return filePost.getResponseBodyAsStream();
		} else {
			throw new RuntimeException("状态码不正确:" + status);
		}
	}

	public static File getImg(String filePath, String absPath) {
		File file = new File(absPath);
		InputStream input = null;
		OutputStream output = null;
		try {
			input = getImgStream(filePath);
			output = new FileOutputStream(file);
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = input.read(b)) > 0) {
				output.write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("文件获取失败");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
}
