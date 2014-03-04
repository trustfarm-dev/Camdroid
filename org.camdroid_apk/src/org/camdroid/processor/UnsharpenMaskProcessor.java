package org.camdroid.processor;

import org.camdroid.OnCameraPreviewListener.FrameDrawer;
import org.camdroid.R;
import org.camdroid.UIFragment;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class UnsharpenMaskProcessor extends AbstractOpenCVFrameProcessor {

	public static class UnsharpenMaskUIFragment extends ConfigurationFragment
			implements UIFragment {
		public static UnsharpenMaskUIFragment newInstance() {
			UnsharpenMaskUIFragment f = new UnsharpenMaskUIFragment();
			return f;
		}

		@Override
		public int getLayoutId() {
			return R.layout.unsharpenmask_ui;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = super
					.onCreateView(inflater, container, savedInstanceState);

			SeekBar sigmaXSeekBar = (SeekBar) v.findViewById(R.id.sigma_x);
			sigmaXSeekBar.setMax(35);
			sigmaXSeekBar.setProgress(sigma_x);

			sigmaXSeekBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							if (progress % 2 == 0) {
								sigma_x = progress == 0 ? 1 : progress;
							} else {
								sigma_x = progress;
							}
							if (fromUser) {
								UnsharpenMaskUIFragment.this.showValue(sigma_x
										+ "px");
							}
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
						}
					});

			SeekBar alphaSeekBar = (SeekBar) v.findViewById(R.id.alpha);
			alphaSeekBar.setMax(50);
			alphaSeekBar.setProgress(alpha);

			alphaSeekBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							alpha = progress;
							if (fromUser) {
								UnsharpenMaskUIFragment.this
										.showValue((double) alpha / 10);
							}
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
						}
					});

			SeekBar betaSeekBar = (SeekBar) v.findViewById(R.id.beta);
			betaSeekBar.setMax(20);
			betaSeekBar.setProgress(beta);

			betaSeekBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							beta = progress;
							if (fromUser) {
								UnsharpenMaskUIFragment.this
										.showValue(((double) beta - 10) / 10);
							}
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
						}
					});

			return v;
		}
	}

	private static int sigma_x = 3;
	private static int alpha = 18;
	private static int beta = 2;

	private Mat mask;

	public UnsharpenMaskProcessor(FrameDrawer drawer) {
		super(drawer);
	}

	@Override
	public void allocate(int width, int height) {
		super.allocate(width, height);
		this.mask = new Mat();
	}

	@Override
	public Fragment getConfigUiFragment() {
		return UnsharpenMaskUIFragment.newInstance();
	}

	@Override
	public void release() {
		super.release();
		this.aquireLock();
		this.mask.release();
		this.locked = false;
	}

	@Override
	public void run() {
		if (this.locked)
			return;

		if (this.in == null || this.out == null)
			return;

		this.locked = true;

		try {
			Mat rgb = this.rgb();

			Imgproc.blur(rgb, this.mask, new Size(sigma_x, sigma_x));
			Core.addWeighted(rgb, (double) alpha / 10, this.mask,
					((double) beta - 10) / 10, 0, rgb);

			Utils.matToBitmap(rgb, this.out);

			this.drawer.drawBitmap(this.out);
		} catch (Exception e) {
		}

		this.locked = false;
	}
}