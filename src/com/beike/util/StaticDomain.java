package com.beike.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StaticDomain {

	private final static List<String> domainList = new ArrayList<String>();
	private final static String domain;
	private final static String fileName = "project";

	static {
		domain = PropertiesReader.getValue(fileName, "static_domain");
		String static_domain_range = PropertiesReader.getValue(fileName,
				"static_domain_range");
		if (static_domain_range != null) {
			String[] domains = static_domain_range.split(";");
			for (String temp : domains) {
				domainList.add(temp);
			}
		}
	}

	public static String getDomain() {
		return getDomain(null);
	}

	public static String getDomain(String srcName) {
		if (domain != null && !domain.trim().equals("")) {
			return "http://" + domain;
		} else {
			if (domainList.size() > 0) {
				if (srcName == null || srcName.trim().equals("")) {
					Random random = new Random();
					return "http://"
							+ domainList.get(random.nextInt(domainList.size()));
				} else {
					return "http://"
							+ domainList
									.get(str2Num(srcName, domainList.size()));
				}
			} else {
				return "http://www.qianpin.com";
			}
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++)
			System.out.println(getDomain(i+""));
	}

	public static int str2Num(String src, int maxValue) {
		if (src == null || src.trim().equals("")) {
			return 0;
		} else {
			src = src.trim();
			int sum = Math.abs(src.hashCode());
			return sum % maxValue;
		}
	}
}
