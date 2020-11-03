package com.sunzk.colortest.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.sunzk.colortest.entity.HSB;
import com.sunzk.colortest.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class FindDiffView extends FrameLayout {
	
	private static final String TAG = "FindDiffView";
	
	private static final int CORNER_RADIUS = 10;
	
	private final int spacing;
	
	private CustomCardView[][] colorViewMap;
	private ArrayList<CustomCardView> viewArrayList = new ArrayList<>();
	private int countPerSide = -1;
	private int diffIndex;
	private OnDiffColorViewClickListener onDiffColorViewClickListener;
	private boolean waitForLayout = false;
	private HSB tempBaseColor;
	private HSB tempDiffColor;

	public FindDiffView(@NonNull Context context) {
		super(context);
		spacing = calSpacing(context);
	}

	public FindDiffView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		spacing = calSpacing(context);
	}

	public FindDiffView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		spacing = calSpacing(context);
	}

	public FindDiffView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		spacing = calSpacing(context);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed && waitForLayout) {
			post(() -> {
				resetCount(countPerSide, true);
				if (tempBaseColor != null && tempDiffColor != null) {
					resetColor(tempBaseColor, tempDiffColor);
				}
			});
		}
	}

	private int calSpacing(Context context) {
		return DisplayUtil.dip2px(context, 5);
	}

	/**
	 * 修改正方形的数量
	 * @param countPerSide 每边的正方形的数量
	 */
	public void resetCount(int countPerSide) {
		resetCount(countPerSide, false);
	}
	public void resetCount(int countPerSide, boolean force) {
		Log.d(TAG, "resetCount: " + this.countPerSide + " , " + countPerSide + " , " + force);
		if (this.countPerSide == countPerSide && !force) {
			return;
		}
		this.countPerSide = countPerSide;
		
		int viewSideLength = calViewSideLength(countPerSide);
		
		if (viewSideLength <= 0) {
			waitForLayout = true;
			Log.d(TAG, "resetCount: cal side length fail, wait for layout");
			return;
		} else {
			waitForLayout = false;
			Log.d(TAG, "resetCount: cal side length success:" + viewSideLength);
		}
		
		colorViewMap = new CustomCardView[countPerSide][countPerSide];
		this.removeAllViews();
		ArrayList<CustomCardView> tempList = new ArrayList<>(viewArrayList);
		viewArrayList.clear();
		int index;
		CustomCardView tempView;
		for (int row = 0; row < countPerSide; row++) {
			for (int column = 0; column < countPerSide; column++) {
				index = row * countPerSide + column;
				tempView = index >= tempList.size() ? null : tempList.get(index);
				tempView = createOrResetColorView(tempView, row, column, viewSideLength);
				colorViewMap[row][column] = tempView;
				viewArrayList.add(tempView);
			}
		}
	}

	public void resetColor(HSB baseColor, HSB diffColor) {
		this.tempBaseColor = baseColor;
		this.tempDiffColor = diffColor;
		if (waitForLayout || viewArrayList.isEmpty()) {
			Log.d(TAG, "resetColor: wait for layout:" + waitForLayout + " - " + viewArrayList.size());
			return;
		}
		Log.d(TAG, "resetColor: " + baseColor + " - " + diffColor);
		this.diffIndex = new Random().nextInt(viewArrayList.size());
		for (int i = 0; i < viewArrayList.size(); i++) {
			CustomCardView customCardView = viewArrayList.get(i);
			customCardView.setBackground(null);
			customCardView.getCardView().setVisibility(VISIBLE);
			if (i == diffIndex) {
				customCardView.getCardView().setCardBackgroundColor(diffColor.getRGBColor());
			} else {
				customCardView.getCardView().setCardBackgroundColor(baseColor.getRGBColor());
			}
		}
	}

	public void setOnDiffColorViewClickListener(OnDiffColorViewClickListener onDiffColorViewClickListener) {
		this.onDiffColorViewClickListener = onDiffColorViewClickListener;
	}
	
	public void showResult() {
		CustomCardView cardView = viewArrayList.get(diffIndex);
		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setCornerRadius(CORNER_RADIUS);
		gradientDrawable.setStroke(DisplayUtil.dip2px(getContext(), 2), 0xFFFFFFFF);
		gradientDrawable.setColor(cardView.getCardView().getCardBackgroundColor());
		cardView.getCardView().setVisibility(INVISIBLE);
		cardView.setBackground(gradientDrawable);
	}

	private int calViewSideLength(int countPerSide) {
		int viewWidth = getWidth();
		return ((viewWidth + spacing) / countPerSide) - spacing;
	}
	
	private CustomCardView createOrResetColorView(CustomCardView cardView, int row, int column, int viewSideLength) {
		LayoutParams layoutParams = createLayoutParams(row, column, viewSideLength);
		Log.d(TAG, "createOrResetColorView: " + row + "-" + column + " : " + layoutParams.leftMargin + "-" + layoutParams.topMargin);
		if (cardView == null) {
			cardView = new CustomCardView(getContext());
			cardView.getCardView().setRadius(CORNER_RADIUS);
			cardView.setClickable(true);
			cardView.setFocusable(true);
		}
		this.addView(cardView, layoutParams);
		CustomCardView finalCardView = cardView;
		cardView.setOnClickListener(v -> checkColorClick(finalCardView, row, column));
		return cardView;
	}

	private void checkColorClick(CustomCardView view, int row, int column) {
		if (onDiffColorViewClickListener != null) {
			boolean result = row * countPerSide + column == diffIndex;
			onDiffColorViewClickListener.onClick(view, result);
		}
	}

	private LayoutParams createLayoutParams(int row, int column, int viewSideLength) {
		Log.d(TAG, "createLayoutParams: " + row + "," + column + "," + viewSideLength + "," + spacing);
		LayoutParams layoutParams = new LayoutParams(viewSideLength, viewSideLength);
		layoutParams.topMargin = row * (viewSideLength + spacing);
		layoutParams.leftMargin = column * (viewSideLength + spacing);
		return layoutParams;
	}
	
	private static class CustomCardView extends FrameLayout {
		
		private static final String TAG = "CustomCardView";
		
		private CardView cardView;

		public CustomCardView(@NonNull Context context) {
			super(context);
			init();
		}

		public CustomCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
			super(context, attrs);
			init();
		}

		public CustomCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
			init();
		}

		public CustomCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
			super(context, attrs, defStyleAttr, defStyleRes);
			init();
		}
		
		private void init() {
			cardView = new CardView(getContext());
			addView(cardView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		public CardView getCardView() {
			return cardView;
		}
	}
	
}
