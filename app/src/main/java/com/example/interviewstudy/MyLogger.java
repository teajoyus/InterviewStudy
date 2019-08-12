package com.example.interviewstudy;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.example.interviewstudy.trace.TraceMethedActivity;

/**
 * 
 * @author linmh
 * @time 2012-12-18
 * 封装的日志类
 */
public class MyLogger {
	private final static boolean logFlag = true;

	public final static String tag = "[linmh]";
	private String mClassName;
	public String tag2;
	private static MyLogger dlog;
	private static MyLogger slog;
	private static final String TAG = "@linmh@ ";
	public MyLogger(String name,String tag) {
		mClassName = name;
		this.tag2 = tag;
	}
	public MyLogger(String name) {
		mClassName = name;
	}
	/**
	/**
	 * Purpose:Mark user one
	 * 
	 * @return
	 */
	public static MyLogger dLog() {
		if (dlog == null) {
			dlog = new MyLogger(TAG);
		}
		return dlog;
	}

	/**
	 * Purpose:Mark user two
	 * 
	 * @return
	 */
	public static MyLogger sLog() {
		if (slog == null) {
			slog = new MyLogger(TAG);
		}
		return slog;
	}

	/**
	 * Get The Current Function Name
	 * 
	 * @return
	 */
	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
			return mClassName + "[ " + Thread.currentThread().getName() + ": "
					+ st.getFileName() + ":" + st.getLineNumber() + " "
					+ st.getMethodName() + " ]";
		}
		return null;
	}

	/**
	 * The Log Level:i
	 * 
	 * @param str
	 */
	public void i(Object str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				if(tag2!=null){
					Log.i(tag2, name + " - " + str);
				}else{
					Log.i(tag, name + " - " + str);
				}
			} else {
				if(tag2!=null){
					Log.i(tag2, str.toString());
				}else{
					Log.i(tag, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:d
	 * 
	 * @param str
	 */
	public void d(Object str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				Log.d(tag, name + " - " + str);
			} else {
				Log.d(tag, str.toString());
			}
		}
	}

	/**
	 * The Log Level:V
	 * 
	 * @param str
	 */
	public void v(Object str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				Log.v(tag, name + " - " + str);
			} else {
				Log.v(tag, str.toString());
			}
		}
	}

	/**
	 * The Log Level:w
	 * 
	 * @param str
	 */
	public void w(Object str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				Log.w(tag, name + " - " + str);
			} else {
				Log.w(tag, str.toString());
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param str
	 */
	public void e(Object str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				Log.e(tag, name + " - " + str);
			} else {
				Log.e(tag, str.toString());
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param ex
	 */
	public void e(Exception ex) {
		if (logFlag) {
			Log.e(tag, "error", ex);
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param log
	 * @param tr
	 */
	public void e(String log, Throwable tr) {
		if (logFlag) {
			String line = getFunctionName();
			Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}"
					+ "[" + mClassName + line + ":] " + log + "\n", tr);
		}
	}



	public static void main(String[] args){
		System.out.println("123");
		final MyLogger activity = new MyLogger("");
		new Thread(new Runnable() {
			@Override
			public void run() {
				activity.testANR();
			}
		}).start();

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		activity.initView();
	}

	private synchronized static  void testANR() {
		initView();
		System.out.println("testANR");
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("testANR2");

	}

	private static  synchronized void initView(){
		System.out.println("initView");
	}
}
