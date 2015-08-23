package sk.ursus.bigfilesfinder.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import sk.ursus.bigfilesfinder.FinderService;
import sk.ursus.bigfilesfinder.R;
import sk.ursus.bigfilesfinder.adapter.ResultsAdapter;
import sk.ursus.bigfilesfinder.model.FooBarFile;
import sk.ursus.bigfilesfinder.util.AnimUtils;
import sk.ursus.bigfilesfinder.util.BroadcastUtils;

/**
 * Created by ursusursus on 22.8.2015.
 */
public class ResultsFragment extends BaseFragment {

    public static final String TAG = "results_fragment";
    private static final String EXTRA_RESULTS = "results";
    private static final String EXTRA_WAS_LOADING = "was_loading";
    private static final String EXTRA_WAS_ERROR = "was_error";
    private static final String EXTRA_ERROR_CODE = "error_code";

    private ResultsAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private FloatingActionButton mFab;
    private int mErrorCode;

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.ACTION_SEARCH_STARTED);
        intentFilter.addAction(BroadcastUtils.ACTION_SEARCH_FINISHED);
        intentFilter.addAction(BroadcastUtils.ACTION_SEARCH_ERROR);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_results);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onResultsFragmentFinished();
            }
        });

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mErrorTextView = (TextView) view.findViewById(R.id.errorTextView);
        mListView = (ListView) view.findViewById(R.id.listView);
        mAdapter = new ResultsAdapter(getActivity());

        if (savedInstanceState != null) {
            // Progress bar
            final boolean wasLoading = savedInstanceState.getBoolean(EXTRA_WAS_LOADING);
            if (wasLoading) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            // Error
            final boolean wasError = savedInstanceState.getBoolean(EXTRA_WAS_ERROR);
            if (wasError) {
                mErrorTextView.setVisibility(View.VISIBLE);
                displayError(savedInstanceState.getInt(EXTRA_ERROR_CODE));
            }

            // List
            final ArrayList<FooBarFile> results = savedInstanceState.getParcelableArrayList(EXTRA_RESULTS);
            if (results != null) {
                mAdapter.setFooBars(savedInstanceState.<FooBarFile>getParcelableArrayList(EXTRA_RESULTS));
                mFab.setVisibility(View.VISIBLE);
            }
        }

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_RESULTS, mAdapter.getFooBars());
        outState.putBoolean(EXTRA_WAS_LOADING, mProgressBar.getVisibility() == View.VISIBLE);
        outState.putBoolean(EXTRA_WAS_ERROR, mErrorTextView.getVisibility() == View.VISIBLE);
        outState.putInt(EXTRA_ERROR_CODE, mErrorCode);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isAdded()) {
                return;
            }

            switch (intent.getAction()) {
                case BroadcastUtils.ACTION_SEARCH_STARTED:
                    handleLargestFilesStarted();
                    break;

                case BroadcastUtils.ACTION_SEARCH_FINISHED:
                    handleLargestFilesFound(intent);
                    break;

                case BroadcastUtils.ACTION_SEARCH_ERROR:
                    handleLargestFilesError(intent);
                    break;
            }
        }

        private void handleLargestFilesStarted() {
            mProgressBar.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.GONE);
        }

        private void handleLargestFilesError(Intent intent) {
            mProgressBar.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);
            displayError(intent.getIntExtra(BroadcastUtils.EXTRA_ERROR, 0));
        }

        private void handleLargestFilesFound(Intent intent) {
            final ArrayList<FooBarFile> filePaths = intent.getParcelableArrayListExtra(BroadcastUtils.EXTRA_FILES);
            mAdapter.setFooBars(filePaths);
            mAdapter.notifyDataSetChanged();

            AnimUtils.crossfade(mListView, mProgressBar);
            AnimUtils.bounceIn(mFab);
        }

    };

    private void displayError(int errorCode) {
        switch (errorCode) {
            case FinderService.ERROR_INVALID_INPUT:
                mErrorTextView.setText(R.string.error_invalid_input);
                break;
            case FinderService.ERROR_TASKS_RUNNING:
                mErrorTextView.setText(R.string.error_tasks_still_running);
                break;
            case FinderService.ERROR_NO_VALID_FOLDERS:
                mErrorTextView.setText(R.string.error_no_valid_folders);
                break;
            default:
                mErrorTextView.setText(R.string.error_default);
        }
        mErrorCode = errorCode;
    }
}
