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

import java.util.Date;
import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.activity.DetailActivity;
import com.eboxlive.ebox.activity.LiveActivity;
import com.eboxlive.ebox.adapter.CoursesListAdapter.ViewHolder;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.entity.VodEntity;
import com.eboxlive.ebox.fragment.CoursesPageFragment;
import com.eboxlive.ebox.fragment.DetailCatalogFragment;
import com.eboxlive.ebox.image.RecyclingImageView;

@SuppressWarnings("unused")
public class CatalogAdapter extends BaseAdapter {
	
	private static final String tag = CatalogAdapter.class.getSimpleName();
    private Context ctx;
    private LayoutInflater mInflater;
    private List<VodEntity> list;
    private String kaishi;
    private String jieshu;
    private String checkPos="";
    private static int lightgray=0;
    private static int black=0;
    private static int lightgreen=0;
    
    @SuppressWarnings("deprecation")
	public CatalogAdapter(Context context,List<VodEntity> list) 
    {
        ctx = context;
        mInflater = LayoutInflater.from(context);
        this.list=list;
        kaishi=context.getResources().getString(R.string.kaishi);
        jieshu=context.getResources().getString(R.string.jieshu);
        lightgray=context.getResources().getColor(R.color.lightgray);
        black=context.getResources().getColor(R.color.black);
        lightgreen=context.getResources().getColor(R.color.green1);
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
    
    public void setCheckItem(int position)
    {
    	checkPos=String.valueOf(position);
    	((DetailActivity)ctx).updatePlayer(list.get(position));
    	notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) 
    {
    	ViewHolder holder=new ViewHolder();
		if(convertView == null)
		{
			convertView=LayoutInflater.from(ctx).inflate(R.layout.directory_list_item, null);
			holder.biaotiText=(TextView)convertView.findViewById(R.id.dirBiaoTi);
			holder.timeText=(TextView)convertView.findViewById(R.id.dirTimeText);
			holder.imageView=(RecyclingImageView)convertView.findViewById(R.id.dirImage);
			convertView.setTag(holder);
		}
		else 
		{
			holder=(ViewHolder) convertView.getTag();
		}
		CourseEntity entity=DetailActivity.courseEntity;
		holder.imageView.setTag(list.get(position).vod_snapshot);
		holder.biaotiText.setText(entity.post_title);
		holder.timeText.setText(ctx.getResources().getText(R.string.riqi)+"："+entity.vod_add_time);
		Log.e(tag,"Postion:"+position);
		
		String g=String.valueOf(position);
		holder.imageView.setTag(g);
		if(checkPos.equalsIgnoreCase(g))
		{
			holder.biaotiText.setTextColor(lightgreen);
			holder.timeText.setTextColor(lightgreen);
		}
		else {
			holder.biaotiText.setTextColor(black);
			holder.timeText.setTextColor(lightgray);
		}
		
		DetailCatalogFragment.imageLoader.loadImage(list.get(position).vod_snapshot,holder.imageView);
		
		return convertView;
    }
    
    static class ViewHolder{
		TextView biaotiText;
		TextView timeText;
		RecyclingImageView imageView;
	}
}
