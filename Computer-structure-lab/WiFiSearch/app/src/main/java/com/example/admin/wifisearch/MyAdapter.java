package com.example.admin.wifisearch;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.util.List;

/**
 * Created by Jesse Huang on 2017/5/10.
 */

public class MyAdapter extends BaseAdapter {

    private List<WifiBean> mList;
    private LayoutInflater mInflater;
    //通过构造方法将数据源和数据适配器关联起来
    public MyAdapter(Context context, List<WifiBean> list){
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){  //没有缓存的

            convertView = mInflater.inflate(R.layout.item_content,null);
        }
        ImageView imageView = (ImageView)convertView.findViewById(R.id.item_image);
        TextView topText = (TextView) convertView.findViewById(R.id.tv_one);
        TextView bottomText = (TextView) convertView.findViewById(R.id.tv_two);
        WifiBean wifiBean = mList.get(position);
        imageView.setImageResource(wifiBean.ImageId);
        topText.setText(wifiBean.Top);
        bottomText.setText(wifiBean.Bottom);
        return convertView;
    }
}

