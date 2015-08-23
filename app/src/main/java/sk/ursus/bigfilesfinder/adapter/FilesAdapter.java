package sk.ursus.bigfilesfinder.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.HashSet;

import sk.ursus.bigfilesfinder.R;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    public interface FiledAdapterListener {
        void onItemClick(int file);
        void onItemChecked(int position, boolean checked);
    }

    private final LayoutInflater mInflater;
    private final FiledAdapterListener mListener;

    private HashSet<String> mSelectedPaths;
    private File[] mFiles;

    private Drawable mSwooshDrawable;
    private Drawable mPlusDrawable;

    public FilesAdapter(Context context, FiledAdapterListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;

        final Resources res = context.getResources();
        mPlusDrawable = DrawableCompat.wrap(res.getDrawable(R.drawable.ic_action_add));
        DrawableCompat.setTint(mPlusDrawable, res.getColor(R.color.gray_foo));

        mSwooshDrawable = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.ic_action_check));
        DrawableCompat.setTint(mSwooshDrawable, res.getColor(R.color.orange));
    }

    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        final View view = mInflater.inflate(R.layout.item_file, viewGroup, false);
        return new FilesViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(FilesViewHolder holder, final int position) {
        final File file = mFiles[position];

        // Title
        holder.mTitleTextView.setText(file.getName());

        // Icon
        if (mSelectedPaths.contains(file.getAbsolutePath())) {
            holder.mAddImageView.setImageDrawable(mSwooshDrawable);
        } else {
            holder.mAddImageView.setImageDrawable(mPlusDrawable);
        }
        holder.mAddContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemChecked(position, !mSelectedPaths.contains(file.getAbsolutePath()));
                }
            }
        });
    }

    public File getItem(int position) {
        return mFiles != null ? mFiles[position] : null;
    }

    @Override
    public int getItemCount() {
        return mFiles != null ? mFiles.length : 0;
    }

    public void setSelected(HashSet<String> selectedPaths) {
        mSelectedPaths = selectedPaths;
    }

    public void setFiles(File[] files) {
        mFiles = files;
    }

    public static class FilesViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTitleTextView;
        private final ViewGroup mAddContainer;
        private final ImageView mAddImageView;

        public FilesViewHolder(View view, final FiledAdapterListener listener) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
            mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
            mAddContainer = (ViewGroup) view.findViewById(R.id.addContainer);
            mAddImageView = (ImageView) view.findViewById(R.id.addImageView);
        }

    }

}
