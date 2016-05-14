package com.eboxlive.ebox.annotation;

import java.lang.reflect.Field;

import com.eboxlive.ebox.annotation.OcInject;
import com.eboxlive.ebox.annotation.OcInjectResource;
import com.eboxlive.ebox.annotation.OcInjectView;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

public class OcInjector
{
	private static OcInjector instance;

	private OcInjector()
	{

	}

	public static OcInjector getInstance()
	{
		if (instance == null)
		{
			instance = new OcInjector();
		}
		return instance;
	}

	public void inJectAll(Activity activity)
	{
		// TODO Auto-generated method stub
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0)
		{
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(OcInjectView.class))
				{
					injectView(activity, field);
				} else if (field.isAnnotationPresent(OcInjectResource.class))
				{
					injectResource(activity, field);
				} else if (field.isAnnotationPresent(OcInject.class))
				{
					inject(activity, field);
				}
			}
		}
	}

	private void inject(Activity activity, Field field)
	{
		// TODO Auto-generated method stub
		try
		{
			field.setAccessible(true);
			field.set(activity, field.getType().newInstance());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void injectView(Activity activity, Field field)
	{
		// TODO Auto-generated method stub
		if (field.isAnnotationPresent(OcInjectView.class))
		{
			OcInjectView viewInject = field.getAnnotation(OcInjectView.class);
			int viewId = viewInject.id();
			try
			{
				Log.d("injectView", field.getName());
				field.setAccessible(true);
				field.set(activity, activity.findViewById(viewId));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void injectResource(Activity activity, Field field)
	{
		// TODO Auto-generated method stub
		if (field.isAnnotationPresent(OcInjectResource.class))
		{
			OcInjectResource resourceJect = field
					.getAnnotation(OcInjectResource.class);
			int resourceID = resourceJect.id();
			try
			{
				field.setAccessible(true);
				Resources resources = activity.getResources();
				String type = resources.getResourceTypeName(resourceID);
				if (type.equalsIgnoreCase("string"))
				{
					field.set(activity,
							activity.getResources().getString(resourceID));
				} else if (type.equalsIgnoreCase("drawable"))
				{
					field.set(activity,
							activity.getResources().getDrawable(resourceID));
				} else if (type.equalsIgnoreCase("layout"))
				{
					field.set(activity,
							activity.getResources().getLayout(resourceID));
				} else if (type.equalsIgnoreCase("array"))
				{
					if (field.getType().equals(int[].class))
					{
						field.set(activity, activity.getResources()
								.getIntArray(resourceID));
					} else if (field.getType().equals(String[].class))
					{
						field.set(activity, activity.getResources()
								.getStringArray(resourceID));
					} else
					{
						field.set(activity, activity.getResources()
								.getStringArray(resourceID));
					}

				} else if (type.equalsIgnoreCase("color"))
				{
					if (field.getType().equals(Integer.TYPE))
					{
						field.set(activity,
								activity.getResources().getColor(resourceID));
					} else
					{
						field.set(activity, activity.getResources()
								.getColorStateList(resourceID));
					}

				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void inject(Activity activity)
	{
		// TODO Auto-generated method stub
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0)
		{
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(OcInject.class))
				{
					inject(activity, field);
				}
			}
		}
	}

	public void injectView(Activity activity)
	{
		// TODO Auto-generated method stub
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0)
		{
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(OcInjectView.class))
				{
					injectView(activity, field);
				}
			}
		}
	}
	
	private void injectFragment(Fragment fragment, View view, Field field)
	{
		if (field.isAnnotationPresent(OcInjectView.class))
		{
			OcInjectView viewInject = field.getAnnotation(OcInjectView.class);
			int viewId = viewInject.id();
			try
			{
				Log.d("OcInjector", "injectFragment "+field);
				field.setAccessible(true);
				field.set(fragment, view.findViewById(viewId));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void injectFragment(Fragment fragment,View view)
	{
		Field[] fields = fragment.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0)
		{
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(OcInjectView.class))
				{
					injectFragment(fragment, view, field);
				}
			}
		}
	}

	public void injectResource(Activity activity)
	{
		// TODO Auto-generated method stub
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0)
		{
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(OcInjectResource.class))
				{
					injectResource(activity, field);
				}
			}
		}
	}

}
