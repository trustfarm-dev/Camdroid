package org.hschott.camdroid.processor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import org.hschott.camdroid.R;

public abstract class ConfigurationFragment extends Fragment {

	private Toast toast;

	public ConfigurationFragment() {
		super();
	}

	public abstract int getLayoutId();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        this.toast = Toast.makeText(this.getActivity(), null,
                Toast.LENGTH_SHORT);

		View c = inflater.inflate(R.layout.configcontainer_ui, null);
		View close = (View) c.findViewById(R.id.container_close);
		if (close != null) {
			close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ConfigurationFragment.this.remove();
				}
			});
		}

		View v = inflater.inflate(this.getLayoutId(), null);
		((ViewGroup) c.findViewById(R.id.container)).addView(v);

		return c;
	}

	@Override
	public void onPause() {
		super.onPause();
		this.remove();
	}

	public void remove() {
		FragmentActivity activity = this.getActivity();
		if (activity != null) {
			activity.getSupportFragmentManager().beginTransaction()
					.remove(this).commit();
		}
	}

	public void showValue(Object text) {
		if (this.toast != null) {
			this.toast.setText(text.toString());
			this.toast.show();
		}
	}
}