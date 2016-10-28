package com.hackgsu.fall2016.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.hackgsu.fall2016.android.R;

/**
 * Created by Joshua King on 10/15/16.
 */
public class ThemedEmptyStateRecyclerView extends RecyclerView {
	private
	@ColorInt int       colorTheme;
	private   Drawable  emptyStateDrawable;
	private   ImageView emptyStateImageView;
	private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
		@Override
		public void onChanged () {
			Adapter<?> adapter = getAdapter();
			if (adapter != null && emptyStateImageView != null) {
				if (adapter.getItemCount() == 0) {
					if (emptyStateImageView != null) {
						emptyStateImageView.setVisibility(View.VISIBLE);
						emptyStateImageView.animate().alpha(1).setDuration(500).start();
						ThemedEmptyStateRecyclerView.this.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
							@Override
							public void run () {
								ThemedEmptyStateRecyclerView.this.setVisibility(View.GONE);
							}
						}).start();
					}
				}
				else if (emptyStateImageView != null) {
					ThemedEmptyStateRecyclerView.this.setVisibility(View.VISIBLE);
					ThemedEmptyStateRecyclerView.this.animate().alpha(1).setDuration(500).start();
					emptyStateImageView.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
						@Override
						public void run () {
							emptyStateImageView.setVisibility(View.GONE);
						}
					}).start();
				}
			}
		}
	};

	public ThemedEmptyStateRecyclerView (Context context) {
		super(context);
		init(null, 0);
	}

	public ThemedEmptyStateRecyclerView (Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public ThemedEmptyStateRecyclerView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	@Override
	public void setAdapter (Adapter adapter) {
		super.setAdapter(adapter);

		if (adapter != null) {
			adapter.registerAdapterDataObserver(emptyObserver);
		}

		emptyObserver.onChanged();
	}

	@Override
	protected void onAttachedToWindow () {
		super.onAttachedToWindow();

		if (emptyStateDrawable != null && getParent() instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) getParent();
			emptyStateImageView = new ImageView(getContext());
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;

			Drawable drawable = DrawableCompat.wrap(emptyStateDrawable);
			DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.transparent_white));

			emptyStateImageView.setLayoutParams(params);
			emptyStateImageView.setImageDrawable(drawable);

			parent.addView(emptyStateImageView);
		}

		emptyObserver.onChanged();
	}

	protected int getColorTheme () {
		return colorTheme;
	}

	@CallSuper
	protected void init (AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ThemedEmptyStateRecyclerView, defStyle, 0);
		colorTheme = a.getColor(R.styleable.ThemedEmptyStateRecyclerView_colorTheme, ContextCompat.getColor(getContext(), android.R.color.black));
		emptyStateDrawable = a.getDrawable(R.styleable.ThemedEmptyStateRecyclerView_emptyStateDrawable);
		a.recycle();

		emptyObserver.onChanged();
	}
}