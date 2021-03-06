package com.polluxframework.swagger.util;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhumin0508
 * created in  2018/5/10 15:59
 * modified By:
 */
public class StringUtils {
	private static final String BLANK_REG = "\\s*|\t|\r|\n";
	private static final String SLANT_STR = "/";

	private StringUtils() {
	}

	/**
	 * 判断container中是否包含指定的字段
	 *
	 * @param container 待验证的字段
	 * @param regulars  要匹配的字段
	 * @return 是否包含
	 */
	public static boolean isContains(String container, String[] regulars) {
		if (container == null) {
			return false;
		}
		for (String bean : regulars) {
			if (bean != null && emptyStrContains(container, bean)) {
				return true;
			}
		}
		return false;
	}

	private static boolean emptyStrContains(String container, String str) {
		if (str.length() == 0) {
			return container.length() == 0;
		}
		return container.contains(str);
	}

	/**
	 * 判断container中是否包含指定的字段
	 *
	 * @param container 待验证的字段
	 * @param sets      要匹配的字段集合
	 * @return 是否包含
	 */
	public static boolean isContains(String container, Set<String> sets) {
		if (container == null) {
			return false;
		}
		for (String bean : sets) {
			if (bean != null && emptyStrContains(container, bean)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param str 需要去除空格 换行 回车 制表符的字符串
	 * @return 去除了空格 换行 回车 制表符的字符串
	 */
	public static String replaceBlank(String str) {
		String result = null;
		if (str != null) {
			Pattern p = Pattern.compile(BLANK_REG);
			Matcher m = p.matcher(str);
			result = m.replaceAll(org.apache.commons.lang3.StringUtils.EMPTY);
		}
		return result;
	}

	/**
	 * 大写第一个字母
	 *
	 * @param str 需要大写的字母
	 * @return 返回大写第一个字母的字符串
	 */
	public static String toUpperCaseFirstLetter(String str) {
		String result = str;
		if (!isEmpty(str)) {
			result = str.substring(0, 1).toUpperCase();
			if (str.length() > 1) {
				result = result + str.substring(1);
			}
		}
		return result;
	}

	/**
	 * 小写第一个字母
	 *
	 * @param str 需要小写的字母
	 * @return 返回小写第一个字母的字符串
	 */
	public static String toLowerCaseFirstLetter(String str) {
		String result = str;
		if (!isEmpty(str)) {
			result = str.substring(0, 1).toLowerCase();
			if (str.length() > 1) {
				result = result + str.substring(1);
			}
		}
		return result;
	}

	public static String addFirstSlant(String str) {
		if ((!isEmpty(str)) && (!str.startsWith(SLANT_STR))) {
			return SLANT_STR + str;
		}
		return str;
	}

	public static String removerLastSlant(String str) {
		if ((!isEmpty(str)) && str.endsWith(SLANT_STR)) {
			return str.substring(0, str.length() - 1);
		}
		return str;
	}


	public static String formatMapping(String mapping) {
		String result = mapping;
		result = addFirstSlant(result);
		return removerLastSlant(result);
	}

	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}
}
