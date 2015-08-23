package sk.ursus.bigfilesfinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sk.ursus.bigfilesfinder.R;
import sk.ursus.bigfilesfinder.model.FooBarFile;
import sk.ursus.bigfilesfinder.util.Utils;

/**
 * Created by ursusursus on 22.8.2015.
 */
public class ResultsAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private ArrayList<FooBarFile> mFooBars;

    public ResultsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public ArrayList<FooBarFile> getFooBars() {
        return mFooBars;
    }

    public void setFooBars(ArrayList<FooBarFile> fooBars) {
        mFooBars = fooBars;
    }

    @Override
    public int getCount() {
        return mFooBars != null ? mFooBars.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mFooBars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_result, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FooBarFile filePath = (FooBarFile) getItem(position);
        holder.mTitleTextView.setText(filePath.getName());
        holder.mSubtitleTextView.setText(filePath.getPath());
        holder.mFileSizeTextView.setText(Utils.bytesToMegabytes(filePath.getSize()) + " MB");
        return convertView;
    }

    private static class ViewHolder {

        private final TextView mTitleTextView;
        private final TextView mSubtitleTextView;
        private final TextView mFileSizeTextView;

        public ViewHolder(View view) {
            mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
            mSubtitleTextView = (TextView) view.findViewById(R.id.subtitleTextView);
            mFileSizeTextView = (TextView) view.findViewById(R.id.filesizeTextView);
        }

    }
}
