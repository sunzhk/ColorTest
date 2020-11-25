package com.sunzk.colortest.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by chances on 16/6/23.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
    private int leftSpace;
    private int topSpace;
    private int rightSpace;
    private int bottomSpace;
    private boolean isShowInterValInEdge = true;

    public SpaceItemDecoration(int leftSpace, int topSpace, int rightSpace, int bottomSpace) {
        this.leftSpace = leftSpace;
        this.topSpace = topSpace;
        this.rightSpace = rightSpace;
        this.bottomSpace = bottomSpace;
    }

    public SpaceItemDecoration(int leftSpace, int topSpace, int rightSpace, int bottomSpace, boolean isShowInterValInEdge) {
        this.leftSpace = leftSpace;
        this.topSpace = topSpace;
        this.rightSpace = rightSpace;
        this.bottomSpace = bottomSpace;
        this.isShowInterValInEdge = isShowInterValInEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = bottomSpace;
        outRect.left = leftSpace;
        outRect.right = rightSpace;
        outRect.top = topSpace;
        if (!isShowInterValInEdge) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {

            } else if (layoutManager instanceof LinearLayoutManager) {
                int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
                if (orientation == LinearLayoutManager.HORIZONTAL) {
                    int childAdapterPosition = parent.getChildAdapterPosition(view);
                    if (childAdapterPosition == 0) {
                        outRect.left = 0;
                    } else if (childAdapterPosition == layoutManager.getItemCount() - 1) {
                        outRect.right = 0;
                    }
                } else if (orientation == LinearLayoutManager.VERTICAL) {
                    int childAdapterPosition = parent.getChildAdapterPosition(view);
                    if (childAdapterPosition == 0) {
                        outRect.top = 0;
                    } else if (childAdapterPosition == layoutManager.getItemCount() - 1) {
                        outRect.bottom = 0;
                    }
                }
            }
        }
    }
}