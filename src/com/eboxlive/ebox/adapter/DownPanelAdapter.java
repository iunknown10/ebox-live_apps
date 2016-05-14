package com.eboxlive.ebox.adapter;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.activity.DownloadPanelActivity;
import com.eboxlive.ebox.activity.LiveActivity;
import com.eboxlive.ebox.adapter.CoursesListAdapter.ViewHolder;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.entity.VodEntity;
import com.eboxlive.ebox.fragment.CoursesPageFragment;
import com.eboxlive.ebox.fragment.DetailCatalogFragment;
import com.eboxlive.ebox.image.RecyclingImageView;

@SuppressWarnings("unused")
public class DownPanelAdapter extends BaseAdapter {
	
	private static final String tag = DownPanelAdapter.class.getSimpleName();
    private Context ctx;
    private LayoutInflater mInflater;
    private List<VodEntity> list;
    private String kaishi;
    private String jieshu;
    private List<Integer> selectList;
    
    private static int lightgray=0;
    private static int black=0;
    private static int lightgreen=0;
    
    @SuppressWarnings("deprecation")
	public DownPanelAdapter(Context context,List<VodEntity> list) 
    {
        ctx = context;
        mInflater = LayoutInflater.from(context);
        this.list=list;
        kaishi=context.getResources().getString(R.string.kaishi);
        jieshu=context.getResources().getString(R.string.jieshu);
        selectList=new ArrayList<Integer>();
        
        lightgray=context.getResources().getColor(R.color.lightgray);
        black=context.getResources().getColor(R.color.black);
        lightgreen=context.getResources().getColor(R.color.lightgreen);
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

    @SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) 
    {
    	ViewHolder holder=new ViewHolder();
		if(convertView == null)
		{
			convertView=LayoutInflater.from(ctx).inflate(R.layout.downpanel_list_item, null);
			holder.biaotiText=(TextView)convertView.findViewById(R.id.dpBiaoTi);
			holder.startText=(TextView)convertView.findViewById(R.id.dpStartText);
			holder.endText=(TextView)convertView.findViewById(R.id.dpEndText);
			holder.imageView=(RecyclingImageView)convertView.findViewById(R.id.dpImage);
			holder.checkBox=(CheckBox)convertView.findViewById(R.id.dpCheckBox);
			convertView.setTag(holder);
		}
		else 
		{
			holder=(ViewHolder) convertView.getTag();
		}
		CourseEntity entity=DownloadPanelActivity.courseEntity;
		holder.imageView.setTag(list.get(position).vod_snapshot);
		holder.biaotiText.setText(entity.post_title);
		if(!TextUtils.isEmpty(list.get(position).vod_start))
		{
			holder.startText.setText(kaishi+":"+(String)DateFormat.format("yyyy-MM-dd HH:mm:ss", 
					Long.parseLong(list.get(position).vod_start)*1000));
		}
		if(!TextUtils.isEmpty(list.get(position).vod_end))
		{
			holder.endText.setText(jieshu+":"+(String)DateFormat.format("yyyy-MM-dd HH:mm:ss", 
					Long.parseLong(list.get(position).vod_end)*1000));
		}
		Log.e(tag,"Postion:"+position);
		
		String g=String.valueOf(position);
		holder.checkBox.setTag(g);
		if(list.get(position).selected)
		{
			holder.checkBox.setChecked(true);
			holder.biaotiText.setTextColor(lightgreen);
			holder.startText.setTextColor(lightgreen);
			holder.endText.setTextColor(lightgreen);
		}
		else {
			holder.checkBox.setChecked(false);
			holder.biaotiText.setTextColor(black);
			holder.startText.setTextColor(lightgray);
			holder.endText.setTextColor(lightgray);
		}
		holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				String t=buttonView.getTag().toString();
				String tg=((OcActivity)ctx).tag;
				int pos=Integer.parseInt(t);
				
				if(isChecked && (!TextUtils.isEmpty(t)))
				{
					list.get(pos).selected=true;
					if(!selectList.contains(pos))
					{
						selectList.add(pos);
					}
				}
				else 
				{
					list.get(pos).selected=false;
					if(selectList.contains(pos))
					{
						selectList.remove(selectList.indexOf(pos));
					}
				}
				Log.d(tag,"SIZE:"+selectList.size());
				((DownloadPanelActivity)ctx).dpTextView.setText(
					   ctx.getResources().getString(R.string.yixuanze)+":"+
					   selectList.size()+
					   ctx.getResources().getString(R.string.wenjian));
				notifyDataSetChanged();
			}
		});
		
		DownloadPanelActivity.imageLoader.loadImage(list.get(position).vod_snapshot,holder.imageView);
		
		return convertView;
    }
    
    static class ViewHolder{
		TextView biaotiText;
		TextView startText;
		TextView endText;
		CheckBox checkBox;
		RecyclingImageView imageView;
	}
}
