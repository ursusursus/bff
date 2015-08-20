package sk.ursus.bigfilesfinder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        holder.mAddContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChecked(position, true);
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
        private final ViewGroup mAddContainer;

        public ViewHolder(View view) {
            mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
            mAddContainer = (ViewGroup) view.findViewById(R.id.addContainer);

            final ImageView mAddImageView = (ImageView) view.findViewById(R.id.addImageView);
            Drawable d = mAddImageView.getDrawable();
            d = DrawableCompat.wrap(d);
            DrawableCompat.setTint(d, view.getResources().getColor(R.color.gray_foo));
        }

    }

}
