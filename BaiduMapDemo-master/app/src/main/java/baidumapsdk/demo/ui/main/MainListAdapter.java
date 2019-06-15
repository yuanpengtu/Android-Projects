package baidumapsdk.demo.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hxw on 2017/4/22.
 */

public class MainListAdapter extends BaseAdapter {
    private List<DemoInfo> mList;
    LayoutInflater mInflater;

    public MainListAdapter(Context context, List<DemoInfo> data) {
        mInflater = LayoutInflater.from(context);
        this.mList = data;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        //观察convertView随ListView滚动情况
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_demo_info, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }

        holder.title.setText(mList.get(i).getTitle());
        holder.desc.setText(mList.get(i).getDesc());
        if (i >= 25) {
            holder.title.setTextColor(Color.YELLOW);
        }
        return convertView;
    }

    public void setmList(List<DemoInfo> mList) {
        this.mList = mList;
    }

    class ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.desc)
        TextView desc;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
