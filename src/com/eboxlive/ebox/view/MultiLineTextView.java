package com.eboxlive.ebox.view;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.util.ScreenUtil;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

@SuppressWarnings("unused")
public class MultiLineTextView extends TextView {

	private static final String tag="MultiLineTextView";
	private final String namespace = "http://schemas.android.com/apk/res/android";
	private Context ctx;
	
	private static final int TYPE_DIMEN=0;
	private static final int TYPE_DIMEN_PIXEL=1;
	private static final int TYPE_STRING=2;
	private static final int TYPE_COLOR=3;
	
	private int textSize; 
	private int textColor;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;
    
    private int marginLeft;  
    private int marginRight;  
    private int marginTop;  
    private int marginBottom;
    private Paint paint = new Paint();
    
	public MultiLineTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		ctx=context;
		
		marginLeft=getInt(attrs, "layout_marginLeft", TYPE_DIMEN_PIXEL);
		marginRight=getInt(attrs, "layout_marginRight", TYPE_DIMEN_PIXEL);
		marginTop=getInt(attrs, "layout_marginTop", TYPE_DIMEN_PIXEL);
		marginBottom=getInt(attrs, "layout_marginBottom", TYPE_DIMEN_PIXEL);
		
		paddingLeft=getInt(attrs, "paddingLeft", TYPE_DIMEN_PIXEL);
		paddingRight=getInt(attrs, "paddingRight", TYPE_DIMEN_PIXEL);
		paddingTop=getInt(attrs, "paddingTop", TYPE_DIMEN_PIXEL);
		paddingBottom=getInt(attrs, "paddingBottom", TYPE_DIMEN_PIXEL);
		
		Log.d(tag,"marginLeft:"+ marginLeft);
	
		textSize=getInt(attrs, "textSize", TYPE_DIMEN_PIXEL);
		Log.d(tag,"textSize:"+ textSize);
		
		textColor=getInt(attrs, "textColor", TYPE_COLOR);
		Log.d(tag,"textColor:"+ attrs.getAttributeValue(namespace, "textColor"));
		
		paint.setTextSize(textSize);
		paint.setColor(textColor);  
		paint.setAntiAlias(true);		
		paint.setTextAlign(Align.CENTER);
	}
	
	private int getInt(AttributeSet attrs, String key, int type)
	{
		String temp = attrs.getAttributeValue(namespace, key);
		Log.i(tag,key+":"+temp);
		if(TextUtils.isEmpty(temp))
		{
			switch (type) 
			{
				case TYPE_COLOR:{return getResources().getColor(R.color.black);}
				case TYPE_DIMEN_PIXEL: {return 0;}
				case TYPE_DIMEN: {return 0;}
				default: { return 0; }
			}
		}
		if(temp.charAt(0) == '@')
		{
			switch (type) 
			{
				case TYPE_COLOR:{return getResources().getColor(Integer.parseInt(temp.substring(1)));}
				case TYPE_DIMEN_PIXEL: {return getResources().getDimensionPixelSize(Integer.parseInt(temp.substring(1)));}
				case TYPE_DIMEN: {return (int) getResources().getDimension(Integer.parseInt(temp.substring(1)));}
				default: { return (int) getResources().getDimension(Integer.parseInt(temp.substring(1))); }
			}
		}
		
		int index=temp.indexOf("dip");
		if(index>0)
		{
			return ScreenUtil.dipTopx(ctx, Float.parseFloat(temp.substring(0,index)));
		}
		return Integer.parseInt(temp);
	}
	
	private String getString(AttributeSet attrs, String key)
	{
		String temp = attrs.getAttributeValue(namespace, key);
		if(temp.charAt(0) == '@')
		{
			return getResources().getString(Integer.parseInt(temp.substring(1)));
		}
		return temp;
	}

    @Override  
    protected void onDraw(Canvas canvas) 
    {  
        //super.onDraw(canvas);
        
    	int drawWidth=0;
        String text=getText().toString();
        if(TextUtils.isEmpty(text)) return;
        
		int index=text.indexOf('\n');
        String line1=text.substring(0,index);
		String line2=text.substring(index);

		if(!TextUtils.isEmpty(line1))
		{
			canvas.drawText(line1,paddingLeft,0, paint);  
		}
		if(!TextUtils.isEmpty(line2))
		{
			//canvas.drawText(line2, paddingLeft, paddingTop + 3*textSize, paint);  
		}
    }
}
