package com.weikan.app.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;

/**
 * 多渠道打包工具；<br/>
 * 利用的是Zip文件“可以添加comment（注释）”的数据结构特点，在文件的末尾写入任意数据，而不用重新解压zip文件（apk文件就是zip文件格式）；<br/>
 * 创建时间： 2014-12-16 18:56:29
 * @author zhangguojun
 * @version 1.1
 * @since JDK1.6 Android2.2
 */
public class MCPTool {

	public static String extractZipComment (String filename) {
		String retStr = null;
		try {
			File file = new File(filename);
			int fileLen = (int)file.length();
			FileInputStream in = new FileInputStream(file);
			byte[] buffer = new byte[Math.min(fileLen, 8192)];
			int len;
			in.skip(fileLen - buffer.length);
			if ((len = in.read(buffer)) > 0) {
				retStr = getZipCommentFromBuffer (buffer, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retStr;
	}

	private static String getZipCommentFromBuffer (byte[] buffer, int len) {
		byte[] magicDirEnd = {0x50, 0x4b, 0x05, 0x06};
		int buffLen = Math.min(buffer.length, len);
		for (int i = buffLen-magicDirEnd.length-22; i >= 0; i--) {
			boolean isMagicStart = true;
			for (int k=0; k < magicDirEnd.length; k++) {
				if (buffer[i+k] != magicDirEnd[k]) {
					isMagicStart = false;
					break;
				}
			}
			if (isMagicStart) {
				int commentLen = buffer[i+20] + buffer[i+22]*256;
				int realLen = buffLen - i - 22;
				System.out.println ("ZIP comment found at buffer position " + (i+22) + " with len="+commentLen+", good!");
				if (commentLen != realLen) {
					System.out.println ("WARNING! ZIP comment size mismatch: directory says len is "+
							commentLen+", but file ends after " + realLen + " bytes!");
				}
				String comment = new String (buffer, i+22, Math.min(commentLen, realLen));
				return comment;
			}
		}
		System.out.println("ERROR! ZIP comment NOT found!");
		return null;
	}

	/**
	 * Android平台读取渠道号
	 * @param context Android中的android.content.Context对象
	 * @param defValue 读取不到时用该值作为默认值
	 * @return
	 */
	public static String getChannelId(Context context, String defValue) {
		String content = extractZipComment(context.getPackageCodePath());
		return content == null || content.length() == 0 ? defValue : content;
	}

//	/**
//	 * 获取已安装apk文件的存储路径（这里使用反射，因为MCPTool项目本身不需要导入Android的运行库）
//	 * @param context Android中的Context对象
//	 * @return
//	 */
//	private static String getPackageCodePath(Object context) {
//		try {
//			return (String) context.getClass().getMethod("getPackageCodePath").invoke(context);
//		} catch (Exception ignore) {
//		}
//		return null;
//	}
}