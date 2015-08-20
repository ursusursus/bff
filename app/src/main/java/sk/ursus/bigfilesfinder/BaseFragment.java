package sk.ursus.bigfilesfinder;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by vbrecka on 20.8.2015.
 */
public class BaseFragment extends Fragment implements MainActivity.BackListener {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).registerBackListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).unregisterBackListener(this);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
