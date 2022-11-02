package cn.entertech.uicomponentsdk.widget;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.utils.ScreenUtil;

public class ChartMoreListAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<MenuItem> mData;
    private int mTextColor = -1;

    public ChartMoreListAdapter(Context context, List<MenuItem> data){
        super();
        this.mContext = context;
        this.mData = data;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chart_more_list, null);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(MATCH_PARENT,  ScreenUtil.dip2px(mContext,44));
            convertView.setLayoutParams(layoutParams);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.text = (TextView) convertView.findViewById(R.id.tv_text);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.text.setText(mData.get(position).text);
        holder.icon.setImageResource(mData.get(position).iconRes);
        if (mTextColor != -1){
            holder.text.setTextColor(mTextColor);
            holder.icon.setImageTintList(ColorStateList.valueOf(mTextColor));
        }
        return convertView;
    }

    public void setTextColor(int color){
        this.mTextColor = color;
    }

    public static class ViewHolder{
        public ImageView icon;
        public TextView text;
    }

    public static class MenuItem{
        public String text;
        public int iconRes;
    }
}
