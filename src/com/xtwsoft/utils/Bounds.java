/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-26 下午09:13:14
 */
package com.xtwsoft.utils;

public class Bounds {
	private int minx;
	private int maxx;
	private int miny;
	private int maxy;

	public Bounds() {
		init();
	}

	public Bounds(int x1, int y1, int x2, int y2) {
		init(x1, y1, x2, y2);
	}

	public Bounds(Bounds env) {
		init(env);
	}

	public void init() {
		setToNull();
	}

	private void init(int x1, int y1, int x2, int y2) {
		if (x1 < x2) {
			minx = x1;
			maxx = x2;
		} else {
			minx = x2;
			maxx = x1;
		}
		if (y1 < y2) {
			miny = y1;
			maxy = y2;
		} else {
			miny = y2;
			maxy = y1;
		}
	}

	private void init(Bounds env) {
		minx = env.minx;
		miny = env.miny;
		maxx = env.maxx;
		maxy = env.maxy;
	}

	public void setToNull() {
		minx = 0;
		miny = 0;
		maxx = -1;
		maxy = -1;
	}

	public boolean isNull() {
		return maxx < minx;
	}

	public int getWidth() {
		if (isNull())
			return 0;
		else
			return maxx - minx;
	}

	public int getHeight() {
		if (isNull())
			return 0;
		else
			return maxy - miny;
	}

	public int getMinX() {
		return minx;
	}

	public int getMaxX() {
		return maxx;
	}

	public int getMinY() {
		return miny;
	}

	public int getMaxY() {
		return maxy;
	}


	public void expandBy(double distance) {
		expandBy(distance, distance);
	}

	public void expandBy(double deltaX, double deltaY) {
		if (isNull())
			return;
		minx -= deltaX;
		miny -= deltaY;
		maxx += deltaX;
		maxy += deltaY;
		if (minx > maxx || miny > maxy)
			setToNull();
	}

	public void expandToInclude(int x, int y) {
		if (isNull()) {
			minx = x;
			miny = y;
			maxx = x;
			maxy = y;
		} else {
			if (x < minx)
				minx = x;
			if (y < miny)
				miny = y;
			if (x > maxx)
				maxx = x;
			if (y > maxy)
				maxy = y;
		}
	}

	public void expandToInclude(Bounds other) {
		if (other.isNull())
			return;
		if (isNull()) {
			minx = other.getMinX();
			miny = other.getMinY();
			maxx = other.getMaxX();
			maxy = other.getMaxY();
		} else {
			if (other.minx < minx)
				minx = other.minx;
			if (other.miny < miny)
				miny = other.miny;
			if (other.maxx > maxx)
				maxx = other.maxx;
			if (other.maxy > maxy)
				maxy = other.maxy;
		}
	}

	public void translate(int transX, int transY) {
		if (isNull()) {
			return;
		} else {
			init(getMinX() + transX, getMaxX() + transX, getMinY() + transY,
					getMaxY() + transY);
			return;
		}
	}

	public Bounds intersection(Bounds env) {
		if (isNull() || env.isNull() || !intersects(env)) {
			return new Bounds();
		} else {
			int intMinX = minx <= env.minx ? env.minx : minx;
			int intMinY = miny <= env.miny ? env.miny : miny;
			int intMaxX = maxx >= env.maxx ? env.maxx : maxx;
			int intMaxY = maxy >= env.maxy ? env.maxy : maxy;
			return new Bounds(intMinX, intMaxX, intMinY, intMaxY);
		}
	}

	public boolean contains(int x, int y) {
		return x >= minx && x <= maxx && y >= miny && y <= maxy;
	}
	
	public boolean intersects(Bounds other) {
		if (isNull() || other.isNull())
			return false;
		else
			return other.minx <= maxx && other.maxx >= minx
					&& other.miny <= maxy && other.maxy >= miny;
	}

	public boolean intersects(int minX,int minY,int maxX,int maxY) {
		return this.minx <= maxX && this.maxx >= minX
				&& this.miny <= maxY && this.maxy >= minY;
	}

	public boolean overlaps(Bounds other) {
		return intersects(other);
	}

	public boolean intersects(int x, int y) {
		return x <= maxx && x >= minx && y <= maxy && y >= miny;
	}

	public boolean overlaps(int x, int y) {
		return intersects(x, y);
	}

	public boolean contains(Bounds other) {
		if (isNull() || other.isNull())
			return false;
		else
			return other.minx >= minx && other.maxx <= maxx
					&& other.miny >= miny && other.maxy <= maxy;
	}

	public boolean equals(Object other) {
		if (!(other instanceof Bounds))
			return false;
		Bounds otherEnvelope = (Bounds) other;
		if (isNull())
			return otherEnvelope.isNull();
		else
			return maxx == otherEnvelope.getMaxX()
					&& maxy == otherEnvelope.getMaxY()
					&& minx == otherEnvelope.getMinX()
					&& miny == otherEnvelope.getMinY();
	}

	public String toString() {
		return "Bounds:(" + minx + "," + miny + " , " + maxx + "," + maxy + ")";
	}
}
