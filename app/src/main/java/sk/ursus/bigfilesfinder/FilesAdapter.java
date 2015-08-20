package sk.ursus.bigfilesfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.File;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FilesAdapter extends BaseAdapter {

    private final OnCheckedListener mListener;

    public interface OnCheckedListener {
        void onChecked(int position, boolean checked);
    }

    private final LayoutInflater mInflater;
    private File[] mFiles;

    public FilesAdapter(Context context, OnCheckedListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_file, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final File file = (File) getItem(position);
        holder.mTitleTextView.setText(file.getName());
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mListener != null) {
                    mListener.onChecked(position, isChecked);
                }
            }
        });

        return convertView;
    }

    public void setFiles(File[] files) {
        mFiles = files;
    }

    @Override
    public int getCount() {
        return mFiles != null ? mFiles.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return mFiles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {

        public final TextView mTitleTextView;
        private final CheckBox mCheckBox;

        public ViewHolder(View view) {
            mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
            mCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        }

    }

}
