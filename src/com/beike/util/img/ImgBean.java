package com.beike.util.img;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int index = 0;
	// 文件存放路径
	private String filePath;
	// 文件切分后的大小,格式为width_length
	private final List<String> sizeList = new ArrayList<String>();
	// 文件
	private File file;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void addSize(String size) {
		Pattern pattern = Pattern.compile("^[0-9]+_[0-9]+$");
		Matcher matcher = pattern.matcher(size);
		if (!matcher.find()) {
			throw new RuntimeException("img size error,format ****_****");
		}
		sizeList.add(size);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSize() {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < sizeList.size(); i++) {
			if (sb.length() == 0) {
				sb.append(sizeList.get(i));
			} else {
				sb.append(",").append(sizeList.get(i));
			}
		}
		return sb.toString();
	}

}
