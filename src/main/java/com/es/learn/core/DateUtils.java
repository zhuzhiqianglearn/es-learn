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


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


/**
 * @author Administrator
 *
 */
public class DateUtils {

	/**
	 * DEFAULT_TIME_PATTERN
	 */
	public static final String DEFAULT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * DEFAULT_DATE_PATTERN
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	/**
	 * DEFAULT_DATE_PATTERN
	 */
	public static final String DEFAULT_yyyy_MM_PATTERN = "yyyy-MM";
	/**
	 * 8位的日期格式
	 */
	public static final String EIGHT_BIT_DATE_PATTERN = "yyyyMMdd";
	/**
	 * 8位的日期格式
	 */
	public static final String EIGHT_DURATION_DATE_PATTERN = "yyyy/MM/dd";
	public static Date getDate(String dateStr) throws ParseException {
		DateFormat format = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
		return format.parse(dateStr);
	}
}
