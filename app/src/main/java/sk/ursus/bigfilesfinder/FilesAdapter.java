package sk.ursus.bigfilesfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FilesAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private File[] mFiles;

    public FilesAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

        return convertView;
    }

    private static class ViewHolder {

        public final TextView mTitleTextView;

        public ViewHolder(View view) {
            mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
        }

    }

}
