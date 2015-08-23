package sk.ursus.bigfilesfinder.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.HashSet;

import sk.ursus.bigfilesfinder.R;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FilesAdapter extends BaseAdapter {

    public interface OnCheckedListener {
        void onChecked(int position, boolean checked);
    }

    private final LayoutInflater mInflater;
    private final OnCheckedListener mListener;
    private HashSet<String> mSelectedPaths;
    private Drawable mSwooshDrawable;
    private Drawable mPlusDrawable;
    private File[] mFiles;

    public FilesAdapter(Context context, OnCheckedListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;

        final Resources res = context.getResources();
        mPlusDrawable = DrawableCompat.wrap(res.getDrawable(R.drawable.ic_action_add));
        DrawableCompat.setTint(mPlusDrawable, res.getColor(R.color.gray_foo));

        mSwooshDrawable = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.ic_action_check));
        DrawableCompat.setTint(mSwooshDrawable, res.getColor(R.color.orange));
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
        if(mSelectedPaths.contains(file.getAbsolutePath())) {
            holder.mAddImageView.setImageDrawable(mSwooshDrawable);
        } else {
            holder.mAddImageView.setImageDrawable(mPlusDrawable);
        }
        holder.mAddContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChecked(position, !mSelectedPaths.contains(file.getAbsolutePath()));
                }
            }
        });

        return convertView;
    }

    public void setSelected(HashSet<String> selectedPaths) {
        mSelectedPaths = selectedPaths;
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
        private final ImageView mAddImageView;

        public ViewHolder(View view) {
            mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
            mAddContainer = (ViewGroup) view.findViewById(R.id.addContainer);
            mAddImageView = (ImageView) view.findViewById(R.id.addImageView);
        }

    }

}
