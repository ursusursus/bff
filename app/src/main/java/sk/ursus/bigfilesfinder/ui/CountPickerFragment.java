package sk.ursus.bigfilesfinder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import sk.ursus.bigfilesfinder.util.AnimUtils;
import sk.ursus.bigfilesfinder.R;
import sk.ursus.bigfilesfinder.util.Utils;

/**
 * Created by ursusursus on 22.8.2015.
 */
public class CountPickerFragment extends BaseFragment {

    public static final String TAG = "count_picker_fragment";

    public interface OnCountPickerFragmentListener {
        void onCountPickerFragmentFinished(int count);
    }
    private OnCountPickerFragmentListener mListener;

    public static CountPickerFragment newInstance() {
        return new CountPickerFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnCountPickerFragmentListener) {
            mListener = (OnCountPickerFragmentListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countpicker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText countEditText = (EditText) view.findViewById(R.id.countEditText);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        countEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (fab.getVisibility() != View.VISIBLE) {
                        AnimUtils.bounceIn(fab);
                    }
                } else {
                    if (fab.getVisibility() != View.INVISIBLE) {
                        AnimUtils.bounceOut(fab);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Integer count = tryParse(countEditText.getText().toString());
                if (count != null && count > 0) {
                    Utils.hideKeyboard(getActivity());
                    if(mListener != null) {
                        mListener.onCountPickerFragmentFinished(count);
                    }
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
