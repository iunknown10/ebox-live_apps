package com.eboxlive.ebox.fragment;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AlertFragment extends DialogFragment implements OnClickListener
{
	private Button okBtn;
	private Button cancelBtn;
	private TextView summaryText;
	
	private static final String tag="AlertFragment";
	public static enum AlertResult
	{
		AR_OK, AR_CANCEL
	}
	
	private int mNum;
	private boolean showButton=false;
	private String title="";
	private String summary="";
	private int alertType=-1;
	
	public static final int ALERT_RESULT_OK=1;
	public static final int ALERT_RESULT_CANCEL=2;
	private MyHandler handler;
	
	static AlertFragment newInstance(String titleStr,String summaryStr,boolean showBtn,int type) 
	{
		AlertFragment f = new AlertFragment();
		Bundle b = new Bundle();
		b.putString("title", titleStr);
		b.putString("summary", summaryStr);
		b.putBoolean("showBtn", showBtn);
		b.putInt("type", type);
		f.setArguments(b);

		return f;
    }
	
	public interface OnAlertCompleted{
		void onCompleted(AlertResult result,int type);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        Log.d(tag, "onCreate()");
        mNum=5;
        showButton = getArguments().getBoolean("showBtn");
        title = getArguments().getString("title");
        summary = getArguments().getString("summary");
        alertType = getArguments().getInt("type");
        
        handler=new MyHandler(new HandleCallback() {
			@Override
			public void handleMessage(Message msg) 
			{
				if( (!AlertFragment.this.isDetached()) && (!AlertFragment.this.isRemoving()) )
				{
					dismiss();
				}
			}
		});
        
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch (mNum) 
        {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch (mNum) 
        {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        setStyle(style, theme);
    }

    @SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
        View v = inflater.inflate(R.layout.fragment_alert, container, false);
        okBtn=(Button)v.findViewById(R.id.alertOkBtn);
        cancelBtn=(Button)v.findViewById(R.id.alertCancelBtn);
        summaryText=(TextView)v.findViewById(R.id.alertSummary);
        summaryText.setText("");
        
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        summaryText.setText(summary);
        
        if(!showButton)
        {
        	summaryText.setVisibility(View.GONE);
        	okBtn.setVisibility(View.GONE);
        	cancelBtn.setVisibility(View.GONE);
        }
        
        //标题字体
        TextView t = (TextView)getDialog().findViewById( android.R.id.title );
        t.setText((CharSequence) title);
        t.setTextColor(getResources().getColor(R.color.black));
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tab_font_size));
        
        //标题下的分隔线
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = getDialog().findViewById(titleDividerId);
        if (titleDivider != null)
        {
            titleDivider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
        
        //自定义标题view
        //getDialog().requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //getDialog().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.fragment_alert);
        if(showButton == false)
        {
        	handler.sendEmptyMessageDelayed(0, 2000);
        }
        return v;
    }
	
	/*
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog dialog=new AlertDialog.Builder(getActivity())
        .setIcon(R.drawable.ic_launcher)
        .setTitle("title")
        .setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Log.d(tag, "OK");
                }
            }
        )
        .setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	Log.d(tag, "Cancel");
                }
            }
        ).create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
	}
	*/

    public static void showDialog(OcActivity ctx,String titleStr,String summaryStr,boolean showBtn,int type) 
    {
        FragmentTransaction ft = ctx.getSupportFragmentManager().beginTransaction();
        AlertFragment prev = (AlertFragment) ctx.getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_ALERT);
        if (prev != null) 
        {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);

        AlertFragment newFragment = AlertFragment.newInstance(titleStr,summaryStr,showBtn,type);
        newFragment.show(ft, Constants.FRAGMENT_ALERT);
    }
    
    public static void hideDialog(OcActivity ctx)
    {
    	AlertFragment prev = (AlertFragment) ctx.getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_ALERT);
        if (prev != null) 
        {
            prev.dismiss();
        }
    }

	@Override
	public void onClick(View v) 
	{
		Log.e(tag,"T:"+alertType);
		if(v.getId() == R.id.alertOkBtn)
		{
			((OcActivity)getActivity()).onCompleted(AlertResult.AR_OK,alertType);
		}
		else if(v.getId() == R.id.alertCancelBtn)
		{
			((OcActivity)getActivity()).onCompleted(AlertResult.AR_CANCEL,alertType);
		}
		
		if( (!AlertFragment.this.isDetached()) && (!AlertFragment.this.isRemoving()) )
		{
			dismiss();
		}
	}
}
