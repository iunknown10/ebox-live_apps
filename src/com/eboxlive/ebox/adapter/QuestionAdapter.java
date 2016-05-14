package com.eboxlive.ebox.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.entity.ChatMessage;
import com.eboxlive.ebox.fragment.DetailQuestionFragment;
import com.eboxlive.ebox.util.ChatUtil;

public class QuestionAdapter extends BaseAdapter {
	
	private static final String tag="QuestionAdapter";
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
    private List<ChatMessage> list;
    private LayoutInflater mInflater;
    private Drawable rightDrawable;
   
    @SuppressWarnings("deprecation")
	public QuestionAdapter(Context context, List<ChatMessage> coll) 
    {
        this.list = coll;
        mInflater = LayoutInflater.from(context);
        rightDrawable=context.getResources().getDrawable(R.drawable.head_photo);
    }
    
    public void setList(List<ChatMessage> coll)
    {
    	this.list=coll;
    	notifyDataSetChanged();
    }
    
    public void add(ChatMessage msg)
    {
    	list.add(msg);
    	notifyDataSetChanged();
    }

    public void clickAdd(int questionID,int clicks)
    {
    	if(questionID == 0)
    	{
    		Log.e(tag,"clickAdd questionid is 0");
    		return;
    	}
    	if(clicks == 0)
    	{
    		Log.e(tag,"clickAdd clicks is 0");
    		return;
    	}
    	for (ChatMessage chatMessage : list)
    	{
			if(chatMessage.question_id == questionID)
			{
				chatMessage.clicks=clicks;
				notifyDataSetChanged();
				break;
			}
		}
    }
    
    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

	public int getItemViewType(int position) 
	{
		ChatMessage entity = list.get(position);
	 	
	 	if (entity.isMy)
	 	{
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}else{
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}
	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
    @SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
    	
    	final ChatMessage entity = list.get(position);
    	boolean isComMsg = entity.isMy;
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	  if (isComMsg == false)
			  {
				  convertView = mInflater.inflate(R.layout.question_item_msg_text_left, null);
			  }else{
				  convertView = mInflater.inflate(R.layout.question_item_msg_text_right, null);
			  }

	    	  viewHolder = new ViewHolder();
			  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			  viewHolder.headImage = (ImageView) convertView.findViewById(R.id.iv_userhead);
			  viewHolder.askTimesText = (TextView) convertView.findViewById(R.id.askTimesText);
			  viewHolder.clickAddBtn = (Button) convertView.findViewById(R.id.addClickBtn);
			  viewHolder.isComMsg = isComMsg;
			  
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	
	    viewHolder.tvSendTime.setText(entity.time_str);
	    viewHolder.tvUserName.setText(entity.user_name);
	    viewHolder.tvContent.setText(entity.message);
	    viewHolder.askTimesText.setText((entity.clicks+1)+"人问过");
	    if((TextUtils.isEmpty(entity.head_image) == false) && entity.head_image.length()>10)
	    {
	    	DetailQuestionFragment.imageLoader.loadImage(entity.head_image, viewHolder.headImage);
	    }
	    else
	    {
	    	viewHolder.headImage.setImageDrawable(rightDrawable);
	    }
	    viewHolder.clickAddBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ChatUtil.instance().clickAddQuestion(entity.live_id, entity.clicks+1, entity.question_id);
			}
		});
	    
	    return convertView;
    }
    
    static class ViewHolder { 
    	public ImageView headImage;
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public TextView askTimesText;
        public Button clickAddBtn;
        public boolean isComMsg = true;
    }
}
