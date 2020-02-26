/*
 *  Copyright 2012, Tera-soft Co., Ltd.  All right reserved.
 *
 *  THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TERA-SOFT CO.,
 *  LTD.  THE CONTENTS OF THIS FILE MAY NOT BE DISCLOSED TO THIRD
 *  PARTIES, COPIED OR DUPLICATED IN ANY FORM, IN WHOLE OR IN PART,
 *  WITHOUT THE PRIOR WRITTEN PERMISSION OF TERA-SOFT CO., LTD
 *
 */
package com.es.learn.core;


import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Administrator
 *
 */
public class StringUtils {

	 /**
     * 获得指定数目的UUID 
     * @param number int 需要获得的UUID数量 
     * @return String[] UUID数组 
     */
    public static String[] getUUIDNew(int number){
        if(number < 1){
            return null;
        }
        String[] retArray = new String[number];
        for(int i=0;i<number;i++){
            retArray[i] = getUUID();
        }
        return retArray;
    }

    /**
     * 获得一个UUID 
     * @return String UUID 
     */
    public static String getUUIDNew(){
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号 
        return uuid.replaceAll("-", "");
    }
    
	/**
	 * LENGTH
	 */
	public static final int LENGTH = 20;

	/**
	 * digest
	 */
	private static MessageDigest digest = null;


	/**
	 * 判断一个字符串是否为null或空字符串“”
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str){
		if(str==null || str=="" || str.equals("")|| str.equals("null") ||str.trim().length()==0){
			return true;
		}
		return false;
	}

	/**
	 * 判断一个字符串是不是 null 且不是空字符串“”
	 * @param str
	 * @return
	 */
	public static boolean isNotNullAndEmpty(String str){
		if(str==null || str=="" || str.trim().length()==0){
			return false;
		}
		return true;
	}

	/**
	 * 对字符串处理:将指定位置到指定位置的字符以星号代替
	 *
	 * @param content
	 *            传入的字符串
	 * @param begin
	 *            开始位置
	 * @param end
	 *            结束位置
	 * @return
	 */
	public static String getStarString(String content, int begin, int end) {

		if (begin >= content.length() || begin < 0) {
			return content;
		}
		if (end > content.length() || end < 0) {
			return content;
		}
		if (begin >= end) {
			return content;
		}
		String starStr = "";
		for (int i = begin; i < end; i++) {
			starStr = starStr + "*";
		}
		return content.substring(0, begin) + starStr + content.substring(end, content.length());

	}
	/**
	 * getNullStringAsBlankStr
	 * @param str get from xml,html ...
	 * @return double
	 */
	public static String getNullStringAsBlankStr(String str) {
		if ((str == null) || str == "" || str.trim().length() == 0) {
			return "";
		} else {
			return str;
		}
	}

	/**
	 * getNullStringAsDoubleZero
	 * @param str get from xml,html ...
	 * @return double
	 */
	public static double getNullStringAsDoubleZero(String str) {
		if ((str == null) || str == "" || str.trim().length() == 0) {
			return 0.0;
		} else {
			return Double.parseDouble(str.trim());
		}
	}

	/**
	 * getNullStringAsDoubleNegativeOne
	 * @param str get from xml,html ...
	 * @return double
	 */
	public static double getNullStringAsDoubleNegativeOne(String str) {
		if ((str == null) || str == "" || str.trim().length() == 0) {
			return -1.0;
		} else {
			return Double.parseDouble(str.trim());
		}
	}

	/**
	 * getNullStringAsIntZero
	 * @param str get from xml,html ...
	 * @return int
	 */
	public static int getNullStringAsIntZero(String str) {
		if ((str == null) || str == "" || str.trim().length() == 0) {
			return 0;
		} else {
			return Integer.parseInt(str.trim());
		}
	}

	/**
	 * getNullStringAsIntNegativeOne
	 * @param str get from xml,html ...
	 * @return int
	 */
	public static int getNullStringAsIntNegativeOne(String str) {
		if ((str == null) || str == "" || str.trim().length() == 0) {
			return -1;
		} else {
			return Integer.parseInt(str.trim());
		}
	}

	/**
	 * getNullStringAsBooleanFalse
	 * @param str get from xml,html ...
	 * @return boolean
	 */
	public static boolean getNullStringAsBooleanFalse(String str) {
		if ((str == null) || str == "" || str.trim().length() == 0) {
			return false;
		} else {
			if (str.trim().equalsIgnoreCase("true")) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * getNullStringAsBooleanTrue
	 * @param str get from xml,html ...
	 * @return boolean
	 */
	public static boolean getNullStringAsBooleanTrue(String str) {
		if ((str == null) || str == "" || str.trim().length() == 0) {
			return true;
		} else {
			if (str.trim().equalsIgnoreCase("true")) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * isNumber
	 * @param c char
	 * @return boolean
	 */
	public static boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}

	/**
	 * MD5加密
	 * @param data data
	 * @return String
	 */
	public static final synchronized String hash(String data) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest.");
				nsae.printStackTrace();
			}
		}
		digest.update(data.getBytes());
		return encodeHex(digest.digest());
	}

	/**
	 * @param bytes bytes
	 * @return String
	 */
	public static final String encodeHex(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		int i;
		for (i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 * Count the occurrences of the substring in string s
	 * @param s string to search in. Returns 0 if this is null
	 * @param sub string to search for. Return 0 if this is null.
	 * @return int
	 */
	public static int countOccurrencesOf(String s, String sub) {
		if (s == null || sub == null || "".equals(sub)) {
			return 0;
		}
		int count = 0, pos = 0, idx = 0;
		while ((idx = s.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * Replaces all occurences of a substring within a string with another string.
	 * @param inString
	 *            String to examine
	 * @param oldPattern
	 *            String to replace
	 * @param newPattern
	 *            String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern,
                                 String newPattern) {
		// Pick up error conditions
		if (inString == null) {
			return null;
		}
		if (oldPattern == null || newPattern == null) {
			return inString;
		}

		StringBuffer sbuf = new StringBuffer();
		// Output StringBuffer we'll build up
		int pos = 0; // Our position in the old string
		int index = inString.indexOf(oldPattern);
		// The index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sbuf.append(inString.substring(pos, index));
			sbuf.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sbuf.append(inString.substring(pos));
		// Remember to append any characters to the right of a match
		return sbuf.toString();
	} // replace

	/**
	 * @param inString
	 *            String to examine
	 * @param pattern
	 *            pattern to delete all occurrences of
	 * @return a String
	 */
	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, "");
	}

	/**
	 * @param inString inString
	 * @param chars characters to delete e.g. az\n will delete as, zs and new lines
	 * @return String
	 */
	public static String deleteAny(String inString, String chars) {
		if (inString == null || chars == null) {
			return inString;
		}
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (chars.indexOf(c) == -1) {
				out.append(c);
			}
		}
		return out.toString();
	}

	/**
	 * Take a String which is a delimited list and convert it to a String array
	 * @param s
	 *            String
	 * @param delimiter
	 *            delimiter. This will not be returned
	 * @return an array of the tokens in the list
	 */
	@SuppressWarnings("unchecked")
	public static String[] delimitedListToStringArray(String s, String delimiter) {
		if (s == null || s.equals("")) {
			return new String[0];
		}
		if (delimiter == null || delimiter.equals("")) {
			return new String[] {s};
		}

		/*
		 * StringTokenizer st = new StringTokenizer(s, delimiter); String[]
		 * tokens = new String[st.countTokens()]; for (int i = 0; i <
		 * tokens.length; i++) { tokens[i] = st.nextToken(); } return tokens;
		 */

		List l = new LinkedList();
		@SuppressWarnings("unused")
		int delimCount = 0;
		int pos = 0;
		int delpos = 0;
		while ((delpos = s.indexOf(delimiter, pos)) != -1) {
			l.add(s.substring(pos, delpos));
			pos = delpos + delimiter.length();
		}
		if (pos <= s.length()) {
			// Add rest of String
			l.add(s.substring(pos));
		}

		return (String[]) l.toArray(new String[l.size()]);

	} // delimitedListToStringArray

	/**
	 * Convert a CSV list into an array of Strings
	 * @param s
	 *            CSV list
	 * @return an array of Strings. Returns the empty array if s is null.
	 */
	public static String[] commaDelimitedListToStringArray(String s) {
		return delimitedListToStringArray(s, ",");
	}

	/**
	 * Convenience method to convert a CSV string list to a set. Note that this
	 * will suppress duplicates.
	 * @param s
	 *            CSV String
	 * @return a Set of String entries in the list
	 */
	@SuppressWarnings("unchecked")
	public static Set commaDelimitedListToSet(String s) {
		Set set = new TreeSet();
		String[] tokens = commaDelimitedListToStringArray(s);
		for (int i = 0; i < tokens.length; i++) {
			set.add(tokens[i]);
		}
		return set;
	}

	/*
	 * public int countSubstrings(String s, String sub) { if (s == null || sub ==
	 * null) return 0; int pos = 0; for (int count = 0; pos < sub.length; i++ }
	 */

	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. Useful for toString() implementations
	 * @param arr
	 *            array to display. Elements may be of any type (toString() will
	 *            be called on each element).
	 * @param delim
	 *            delimiter to use (probably a ,)
	 * @return String
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (arr == null) {
			return "null";
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < arr.length; i++) {
				if (i > 0) {
					sb.append(delim);
				}
				sb.append(arr[i]);
			}
			return sb.toString();
		}
	} // arrayToDelimitedString

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. Useful for toString() implementations
	 * @param c
	 *            Collection to display
	 * @param delim
	 *            delimiter to use (probably a ,)
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static String collectionToDelimitedString(Collection c, String delim) {
		if (c == null) {
			return "null";
		}
		return iteratorToDelimitedString(c.iterator(), delim);
	} // collectionToDelimitedString

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. Useful for toString() implementations
	 * @param itr
	 *            Collection to display
	 * @param delim
	 *            delimiter to use (probably a ,)
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static String iteratorToDelimitedString(Iterator itr, String delim) {
		if (itr == null) {
			return "null";
		} else {
			StringBuffer sb = new StringBuffer();
			int i = 0;
			while (itr.hasNext()) {
				if (i++ > 0) {
					sb.append(delim);
				}
				sb.append(itr.next());
			}
			return sb.toString();
		}
	}

	/**
	 * @param o Object
	 * @param len len
	 * @return String
	 */
	public static String format(Object o, int len) {
		String str;
		if (o == null || o.toString() == null) {
			str = "";
		} else {
			str = o.toString();
		}
		int strLength = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < 256) {
				strLength++;
			} else {
				strLength += 2;
			}
		}
		if (strLength >= len) {
			return str;
		}
		StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < len - strLength; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * String转ascii数组
	 * @param s String
	 * @return ascii数组
	 */
	public static int[] getASCII(String s) {
		if (s == null) {
			return null;
		}
		int[] ascii = new int[s.length()];
		for (int i = 0; i < ascii.length; i++) {
			ascii[i] = s.charAt(i);
		}
		return ascii;
	}

	/**
	 * ascii数组转String
	 * @param ascii ascii
	 * @return String
	 */
	public static String getString(int[] ascii) {
		if (ascii == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ascii.length; i++) {
			sb.append((char) ascii[i]);
		}
		return sb.toString();
	}

	/**
	 * getStringLength
	 * @param str str
	 * @return length
	 */
	public static int getStringLength(String str) {
		return str.length();
	}

	/**
	 * @return UUID
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 将字符串Y、N转换成true、false，默认及空值返回false
	 * @param ynStr 字符串Y、N
	 * @return true、false，默认及空值返回false
	 */
	public static boolean getStringYN2Boolean(String ynStr) {
		if ("Y".equalsIgnoreCase(ynStr)) {
			//如果是字符串Y，那么返回true
			return true;
		} else if ("N".equalsIgnoreCase(ynStr)) {
			//如果是字符串N，那么返回false
			return false;
		}
		//其他情况，默认返回false
		return false;
	}
	/**
	 * 通过数组下标获取指定的位置的元素，特殊处理下标越界
	 * @param strArray String数组
	 * @param index 下标
	 * @return 指定位置的元素，如果下标越界，返回""
	 */
	public static String getValueInArray(String[] strArray, int index) {
		String value = "";
		if (null != strArray) {
			//如果数组不为空才进行指定位置元素的获取
			if (index >= strArray.length) {
				//如果下标越界,那么不进行获取，返回""
				value = "";
			} else {
				value = strArray[index];
			}
		}
		return value;
	}

	/**
	 * toUtf8String
	 * @param s String
	 * @return String
	 */
	public static String toUtf8String(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
					b = new byte[0];
					throw new StringUtilsException("Cannot get 'utf-8' bytes!", ex);
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) {
						k += 256;
					}
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 字符串解析成int类型的数组
	 * @param string 待解析字符串
	 * @param regex 分隔符
	 * @return int[]
	 */
	public static int[] tranStrToIntArray(String string, String regex) {
		if (null == string || "".equals(string)) {
			return null;
		}

		if (regex.equals(string.substring(0, 1))) {
			string = string.substring(1, string.length());
		}
		if (regex.equals(string.substring(string.length() - 1, string.length()))) {
			string = string.substring(0, string.length() - 1);
		}
		String[] strs = string.trim().split(regex);
		int[] result = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			result[i] = Integer.parseInt(strs[i]);
		}
		return result;

	}

	/**
	 * 创建重复的字符串
	 * @param number int
	 * @param regex string
	 * @return string
	 */
	public static String generateRepeatString(int number, String regex) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < number; i++) {
			sb.append(regex);
		}
		return sb.toString();
	}

	/**
	 * 数组字符串解析成int字符串
	 * @param stringArray array
	 * @return int
	 */
	public static int[] tranStringArrayToInt(String[] stringArray) {
		if (null == stringArray || stringArray.length == 0) {
			return null;
		}
		int[] result = new int[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			result[i] = Integer.parseInt(stringArray[i]);
		}
		return result;
	}

	/**
	 * 创建Token
	 * @return Token
	 */
	public static String createToken() {
		Random random = new Random();
		Date date = new Date();
		String token = date.getTime() + "" + random.nextInt(999999999);
		return token;
	}

	/**
	 * 格式化数字
	 * @param value 值
	 * @param scale 精度
	 * @return 格式化后的值
	 */
	public static String formatNumber(double value, int scale) {
		String scaleStr = "";
		if (scale > 0) {
			scaleStr = ".";
		}
		for (int i = 0; i < scale; i++) {
			scaleStr += "0";
		}
		BigDecimal b = new BigDecimal(value);
		double v = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		DecimalFormat df = new DecimalFormat("#,###" + scaleStr);
		return df.format(v);
	}

	/**
	 * MD5加密
	 * @param str str
	 * @return String
	 */
	public static String md5(String str) {
		if (str == null) {
			return null;
		}
		String rtn = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] md5Bytes = md.digest();
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			rtn = hexValue.toString();
			// 32位的加密
			// System.out.println("result: " + hexValue.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}


	public static String hideCharacter(String obj, int index, int lastIndex){
		int len=obj.length()-index-lastIndex;
		String center="";
		for(int i=0;i<len;i++){
			center+="*";
		}
		obj=obj.substring(0, index)+center+obj.substring(obj.length()-lastIndex, obj.length());
		return obj;
	}

	public   static String StringFilter(String str)   throws PatternSyntaxException {
		// 只允许字母和数字
		// String   regEx  =  "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`?ࣁ½ࣁ½ࠓ¾~!@#$%^&*+\\[\\].<>/~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？👍]";
		Pattern p= Pattern.compile(regEx);
		Matcher m=p.matcher(str);
		return m.replaceAll("").trim();
	}

	public   static String StringFilter1(String str)   throws PatternSyntaxException {
		str = str.replaceAll("[^,|:;(a-zA-Z0-9.*\\u4e00-\\u9fa5())]+", "");
		str=str.replace("null", "");
		//str=StringFilter(str);
		//str=str.replace("\\u003F", "");
		return str;
	}
	public static String StringFilter2(String str)   throws PatternSyntaxException {
		str = str.replaceAll("[^-_|:(a-zA-Z0-9.*\\u4e00-\\u9fa5())]+", "");
		str=str.replace("null", "");
		return str;
	}

	/**
	 * 校验是否只含有汉字、数字和英文字母
	 * @param str
	 * @return true ：是，false:否
	 */
	public static boolean isLetterDigitOrChinese(String str) {
		String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";//其他需要，直接修改正则表达式就好
		return str.matches(regex);
	}
}

/**
 * @author Wallace chu
 *
 */
@SuppressWarnings("serial")
class StringUtilsException extends RuntimeException {

	/**
	 * @param message message
	 * @param e Exception
	 */
	public StringUtilsException(String message, Exception e) {
		super(message, e);
	}




}

