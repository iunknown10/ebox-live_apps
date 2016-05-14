package com.eboxlive.ebox.layoutloader;

import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @Description 通过资源名获取布局
 */
public interface OcILayoutLoader
{
	public int getLayoutID(String resIDName) throws ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException,
			NameNotFoundException;

}
