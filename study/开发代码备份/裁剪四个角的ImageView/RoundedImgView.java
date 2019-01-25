package com.ipanel.join.cq.vod.searchpage;

import ipanel.join.configuration.Bind;
import ipanel.join.configuration.View;
import ipanel.join.widget.ImgView;
import ipanel.join.widget.PropertyUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class RoundedImgView extends ImgView {

	/*左上角xy半径，右上角，右下角，左下角*/
	private float[] rids = { 8.0f, 8.0f, 8.0f, 8.0f, 8.0f, 8.0f, 8.0f, 8.0f };

	public RoundedImgView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RoundedImgView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImgView(Context ctx, View data) {
		super(ctx, data);

	}

	public RoundedImgView(Context context) {
		super(context);
	}

	Path path = new Path();
	RectF rectF;
	PorterDuffXfermode prDuffXfermode = new PorterDuffXfermode(
			PorterDuff.Mode.SRC_IN);

	@Override
	protected void onDraw(Canvas canvas) {
		if (rectF == null) {
			int w = this.getWidth();
			int h = this.getHeight();
			rectF = new RectF(0, 0, w, h);
		}
		/*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
		path.addRoundRect(rectF, rids, Path.Direction.CW);
		canvas.clipPath(path);
		super.onDraw(canvas);
	}
	/**
	 * 设置四个角圆角
	 * @param px
	 */
	public void setRadius(float px) {
		if (px < 0) {
			px = 0.0f;
		}
		for (int i = 0; i < rids.length; i++) {
			rids[i] = px;
		}
	}
	/**
	 * 设置顶部两个角圆角
	 * @param left
	 * @param right
	 */
	public void setTopRadius(float left, float right) {
		if (left < 0) {
			left = 0.0f;
		}
		if (right < 0) {
			right = 0.0f;
		}
		rids[0] = left;
		rids[1] = left;
		rids[2] = right;
		rids[3] = right;
	}
	/**
	 * 设置底部两个角圆角
	 * @param left
	 * @param right
	 */
	public void setBottomRadius(float left, float right) {
		if (left < 0) {
			left = 0.0f;
		}
		if (right < 0) {
			right = 0.0f;
		}
		rids[4] = left;
		rids[5] = left;
		rids[6] = right;
		rids[7] = right;
	}

	private float selectedRadius;
	private float unSelectedRadius;
	private boolean allowSelectedRadius;

	/**
	 * 允不允许设置选中状态和非选中状态的圆角形式
	 * 
	 * @param allowSelectedRadius
	 */
	public void setAllowSelectedRadius(boolean allowSelectedRadius) {
		this.allowSelectedRadius = allowSelectedRadius;
	}

	/**
	 * 设置选中状态和非选中状态的圆角形式
	 * 
	 * @param selectedRadius
	 * @param unSelectedRadius
	 */
	public void setSelectedRadius(float selectedRadius, float unSelectedRadius) {
		this.selectedRadius = selectedRadius;
		this.unSelectedRadius = unSelectedRadius;
		allowSelectedRadius = true;
		setSelected(isSelected());// 更新状态
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		super.setSelected(selected);
		if (allowSelectedRadius) {
			if (selected) {
				setRadius(selectedRadius);
			} else {
				setRadius(unSelectedRadius);
			}
		}
	}

	@Override
	protected void dispatchSetSelected(boolean selected) {
		// TODO Auto-generated method stub
		super.dispatchSetSelected(selected);
	}

}
