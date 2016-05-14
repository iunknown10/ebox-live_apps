package com.eboxlive.ebox.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.entity.ChatMessage;
import com.eboxlive.ebox.fragment.DetailDiscussFragment;

public class ChatAdapter extends BaseAdapter {
	
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
    private List<ChatMessage> list;
    private LayoutInflater mInflater;
    private Drawable rightDrawable;
    
    @SuppressWarnings("deprecation")
	public ChatAdapter(Context context, List<ChatMessage> coll) 
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
    
    public void insert(int pos,ChatMessage msg)
    {
    	list.add(pos, msg);
    	notifyDataSetChanged();
    }
    
    public int getLastChatID()
    {
    	if(list.size()>0)
    	{
    		return (int)list.get(0).chat_id-1;
    	}
    	return 0;
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
    	
    	ChatMessage entity = list.get(position);
    	boolean isComMsg = entity.isMy;
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	  if (isComMsg == false)
			  {
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
			  }else{
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
			  }

	    	  viewHolder = new ViewHolder();
			  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			  viewHolder.headImage = (ImageView) convertView.findViewById(R.id.iv_userhead);
			  viewHolder.isComMsg = isComMsg;
			  
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	
	    viewHolder.tvSendTime.setText(entity.time_str);
	    viewHolder.tvUserName.setText(entity.user_name);
	    viewHolder.tvContent.setText(entity.message);
	    if((TextUtils.isEmpty(entity.head_image) == false) && entity.head_image.length()>10)
	    {
	    	DetailDiscussFragment.imageLoader.loadImage(entity.head_image, viewHolder.headImage);
	    }
	    else
	    {
	    	viewHolder.headImage.setImageDrawable(rightDrawable);
	    }
	    
	    return convertView;
    }
    
    static class ViewHolder { 
    	public ImageView headImage;
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public boolean isComMsg = true;
    }
}
