package com.windchat.im.boot.test;

import java.util.Base64;

import com.windchat.im.business.utils.FileServerUtils;

public class TestDefaultIcon {
	public static void main(String args[]) {
		// String fileUrl = "site-admin-icon.jpg";
		String fileUrl = "管理.png";
		byte[] fileBytes = FileServerUtils.fileToBinary(fileUrl);

		String str = Base64.getEncoder().encodeToString(fileBytes);
		System.out.println(str);

	}
}
