package se.kmdev.tvepg.epg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.google.common.collect.Maps;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;
import java.util.Map;

import se.kmdev.tvepg.R;
import se.kmdev.tvepg.epg.domain.EPGEvent;
import se.kmdev.tvepg.epg.misc.EPGUtil;

/**
 * Classic EPG, electronic program guide, that scrolls both horizontal, vertical and diagonal.
 * It utilize onDraw() to draw the graphic on screen. So there are some private helper methods calculating positions etc.
 * Listed on Y-axis are channels and X-axis are programs/events. Data is added to EPG by using setEPGData()
 * and pass in an EPGData implementation. A click listener can be added using setEPGClickListener().
 * Created by Kristoffer, http://kmdev.se
 */
public class EPGoneDay extends ViewGroup {

    public static final int END_TIME = 24 * 60 * 60 * 1000;     // 24H
    public static final int HOURS_IN_VIEWPORT_MILLIS = 70 * 60 * 1000;     // 1 hour 10 minutes
    public static final int TIME_LABEL_SPACING_MILLIS = 60 * 60 * 1000;        // 1 hour
    public static final int TIME_SPACING_MILLIS = 15 * 60 * 1000; // 15 MINUTES

    private final Rect mClipRect;
    private final Rect mDrawingRect;
    private final Rect mMeasuringRect;
    private final Paint mPaint;
    private final Scroller mScroller;
    private final GestureDetector mGestureDetector;

    private final int mEventLayoutPadding;
    private final int mChannelLayoutMargin;
    private final int mChannelLayoutPadding;
    private final int mChannelLayoutHeight;
    private final int mChannelLayoutWidth;
    private final int mChannelLayoutBackground;
    private final int mEventLayoutBackground;
    private final int mEventLayoutTextColor;
    private final int mEventLayoutTextSize;
    private final int mEventLayoutBackgroundCurrent;
    private final int mEventLayoutTextColorCurrent;
    private final int mEventLayoutTextTimeColorCurrent;
    private final int mTimeBarLineWidth;
    private final int mTimeBarLineColor;
    private final int mTimeBarHeight;
    private final int mTimeBarTextSize;
    private final int mEventLayoutTextTimeColor;
    private final int mEventLayoutTimeTextSize;
    private final int mTimeLayoutColor;


    private final int mEPGBackground;
    private final Map<String, Bitmap> mChannelImageCache;
    private final Map<String, Target> mChannelImageTargetCache;

    private EPGClickListener mClickListener;
    private int mMaxHorizontalScroll;
    private int mMaxVerticalScroll;
    private long mMillisPerPixel;
    private long mTimeOffset;
    private long mTimeLowerBoundary;
    private long mTimeUpperBoundary;

    private EPGData epgData = null;

    public EPGoneDay(Context context) {
        this(context, null);
    }

    public EPGoneDay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EPGoneDay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);

        resetBoundaries();

        mDrawingRect = new Rect();
        mClipRect = new Rect();
        mMeasuringRect = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGestureDetector = new GestureDetector(context, new OnGestureListener());
        mChannelImageCache = Maps.newHashMap();
        mChannelImageTargetCache = Maps.newHashMap();

        // Adding some friction that makes the epg less flappy.
        mScroller = new Scroller(context);
        mScroller.setFriction(0.1f);

        mEPGBackground = getResources().getColor(R.color.epg_background);
        mTimeLayoutColor = getResources().getColor(R.color.epg_time_line_color);

        mEventLayoutPadding = getResources().getDimensionPixelSize(R.dimen.epg_event_layout_padding);
        mEventLayoutTextTimeColor = getResources().getColor(R.color.epg_event_layout_time_text);
        mEventLayoutTimeTextSize = getResources().getDimensionPixelSize(R.dimen.epg_event_layout_time_text);

        mChannelLayoutMargin = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_margin);
        mChannelLayoutPadding = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_padding);
        mChannelLayoutHeight = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_height);
        mChannelLayoutWidth = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_width);
        mChannelLayoutBackground = getResources().getColor(R.color.epg_channel_layout_background);

        mEventLayoutBackground = getResources().getColor(R.color.epg_event_layout_background);
        mEventLayoutTextColor = getResources().getColor(R.color.epg_event_layout_text);
        mEventLayoutTextSize = getResources().getDimensionPixelSize(R.dimen.epg_event_layout_text);

        mEventLayoutBackgroundCurrent = getResources().getColor(R.color.epg_event_layout_background_current);
        mEventLayoutTextTimeColorCurrent = getResources().getColor(R.color.epg_event_layout_time_text_current);
        mEventLayoutTextColorCurrent = getResources().getColor(R.color.epg_event_layout_text_current);


        mTimeBarHeight = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_height);
        mTimeBarTextSize = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_text);
        mTimeBarLineWidth = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_line_width);
        mTimeBarLineColor = getResources().getColor(R.color.epg_time_bar);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (epgData != null && epgData.hasData()) {
            mTimeLowerBoundary = getTimeFrom(getScrollX());
            mTimeUpperBoundary = getTimeFrom(getScrollX() + getWidth());

            Rect drawingRect = mDrawingRect;
            drawingRect.left = getScrollX();
            drawingRect.top = getScrollY();
            drawingRect.right = drawingRect.left + getWidth();
            drawingRect.bottom = drawingRect.top + getHeight();

            drawChannelListItems(canvas, drawingRect);
            drawEvents(canvas, drawingRect);
            drawTimebar(canvas, drawingRect);
            drawTimeLine(canvas, drawingRect);

            // If scroller is scrolling/animating do scroll. This applies when doing a fling.
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        recalculateAndRedraw();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    private void drawTimebarBottomStroke(Canvas canvas, Rect drawingRect) {
        drawingRect.left = getScrollX();
        drawingRect.top = getScrollY() + mTimeBarHeight;
        drawingRect.right = drawingRect.left + getWidth();
        drawingRect.bottom = drawingRect.top + mChannelLayoutMargin;

        // Bottom stroke
        mPaint.setColor(mEPGBackground);
        canvas.drawRect(drawingRect, mPaint);
    }

    private void drawTimebar(Canvas canvas, Rect drawingRect) {
        drawingRect.left = getScrollX() + mChannelLayoutWidth + mChannelLayoutMargin;
        drawingRect.top = getScrollY();
        drawingRect.right = drawingRect.left + getWidth();
        drawingRect.bottom = drawingRect.top + mTimeBarHeight;

        mClipRect.left = getScrollX() + mChannelLayoutWidth + mChannelLayoutMargin;
        mClipRect.top = getScrollY();
        mClipRect.right = getScrollX() + getWidth();
        mClipRect.bottom = mClipRect.top + mTimeBarHeight;

        canvas.save();
        canvas.clipRect(mClipRect);

        // Background
        mPaint.setColor(mTimeLayoutColor);
        canvas.drawRect(drawingRect, mPaint);

        // Time stamps
        mPaint.setColor(mEventLayoutTextColor);

        for (int i = 0; i < (HOURS_IN_VIEWPORT_MILLIS / TIME_SPACING_MILLIS) + 1; i++) {
            // Get time and round to nearest half hour
            final long time = TIME_SPACING_MILLIS *
                    (((mTimeLowerBoundary + (TIME_SPACING_MILLIS * i)) +
                            (TIME_SPACING_MILLIS / 2)) / TIME_SPACING_MILLIS);

            if (time % TIME_LABEL_SPACING_MILLIS == 0) {
                mPaint.setTextSize(mTimeBarTextSize);
                canvas.drawText("|",
                        getXFrom(time) - dptopx(2),
                        drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mTimeBarTextSize / 2)) - dptopx(12), mPaint);

                canvas.drawText(EPGUtil.getShortHour(time),
                        getXFrom(time) - dptopx(6),
                        drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mTimeBarTextSize / 2)) + dptopx(4), mPaint);
            } else {
                mPaint.setTextSize(0.8f * mTimeBarTextSize);
                canvas.drawText("|",
                        getXFrom(time) - dptopx(2),
                        drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mTimeBarTextSize / 2)) - dptopx(18), mPaint);

                canvas.drawText(EPGUtil.getShortMin(time),
                        getXFrom(time) - dptopx(5),
                        drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mTimeBarTextSize / 2)) - dptopx(4), mPaint);
            }
        }

        canvas.restore();

        drawTimebarDayIndicator(canvas, drawingRect);
        drawTimebarBottomStroke(canvas, drawingRect);
    }

    private void drawTimebarDayIndicator(Canvas canvas, Rect drawingRect) {
        drawingRect.left = getScrollX();
        drawingRect.top = getScrollY();
        drawingRect.right = drawingRect.left + mChannelLayoutWidth;
        drawingRect.bottom = drawingRect.top + mTimeBarHeight;

        // Background
        mPaint.setColor(mTimeLayoutColor);
        canvas.drawRect(drawingRect, mPaint);

        // Text
        mPaint.setColor(mEventLayoutTextColor);
        mPaint.setTextSize(mTimeBarTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Time",
                drawingRect.left + ((drawingRect.right - drawingRect.left) / 2),
                drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mTimeBarTextSize / 2)), mPaint);

        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawTimeLine(Canvas canvas, Rect drawingRect) {
        long now = EPGUtil.getCurrentTime();

        if (shouldDrawTimeLine(now)) {
            drawingRect.left = getXFrom(now);
            drawingRect.top = getScrollY();
            drawingRect.right = drawingRect.left + mTimeBarLineWidth;
            drawingRect.bottom = drawingRect.top + getHeight();

            mPaint.setColor(mTimeBarLineColor);
            canvas.drawRect(drawingRect, mPaint);
        }

    }

    private void drawEvents(Canvas canvas, Rect drawingRect) {
        final int firstPos = getFirstVisibleChannelPosition();
        final int lastPos = getLastVisibleChannelPosition() + 2;

        for (int pos = firstPos; pos < lastPos; pos++)
            if (pos < epgData.getChannelCount()) {

                // Set clip rectangle
                mClipRect.left = getScrollX() + mChannelLayoutWidth + mChannelLayoutMargin;
                mClipRect.top = getTopFrom(pos);
                mClipRect.right = getScrollX() + getWidth();
                mClipRect.bottom = mClipRect.top + mChannelLayoutHeight;

                canvas.save();
                canvas.clipRect(mClipRect);

                // Draw each event
                boolean foundFirst = false;

                List<EPGEvent> epgEvents = epgData.getEvents(pos);

                for (EPGEvent event : epgEvents) {
                    if (isEventVisible(event.getStart(), event.getEnd())) {
                        drawEvent(canvas, pos, event, drawingRect);
                        foundFirst = true;
                    } else if (foundFirst) {
                        break;
                    }
                }

                canvas.restore();
            }

    }

    private void drawEvent(final Canvas canvas, final int channelPosition, final EPGEvent event, final Rect drawingRect) {

        setEventDrawingRectangle(channelPosition, event.getStart(), event.getEnd(), drawingRect);

        // Background
        mPaint.setColor(event.isCurrent() ? mEventLayoutBackgroundCurrent : mEventLayoutBackground);
        canvas.drawRect(drawingRect, mPaint);

        // Add left and right inner padding
        drawingRect.left += mEventLayoutPadding;
        drawingRect.right -= mEventLayoutPadding;

        // Text title
        mPaint.setColor(event.isCurrent() ? mEventLayoutTextColorCurrent : mEventLayoutTextColor);
        mPaint.setTextSize(mEventLayoutTextSize);

        // Move drawing.top so text will be centered (text is drawn bottom>up)
        mPaint.getTextBounds(event.getTitle(), 0, event.getTitle().length(), mMeasuringRect);
        drawingRect.top += dptopx(22);

        String title = event.getTitle();
        title = title.substring(0,
                mPaint.breakText(title, true, drawingRect.right - drawingRect.left, null));
        canvas.drawText(title, drawingRect.left, drawingRect.top, mPaint);

        // Text time
        mPaint.setColor(event.isCurrent() ? mEventLayoutTextTimeColorCurrent : mEventLayoutTextTimeColor);
        mPaint.setTextSize(mEventLayoutTimeTextSize);

        String time = EPGUtil.getShortTime(event.getStart()) + "-" + EPGUtil.getShortTime(event.getEnd());
        time = time.substring(0,
                mPaint.breakText(time, true, drawingRect.right - drawingRect.left, null));
        canvas.drawText(time, drawingRect.left, drawingRect.top + dptopx(22), mPaint);

    }

    private void setEventDrawingRectangle(final int channelPosition, final long start, final long end, final Rect drawingRect) {
        drawingRect.left = getXFrom(start);
        drawingRect.top = getTopFrom(channelPosition);
        drawingRect.right = getXFrom(end) - mChannelLayoutMargin;
        drawingRect.bottom = drawingRect.top + mChannelLayoutHeight;
    }

    private void drawChannelListItems(Canvas canvas, Rect drawingRect) {
        // Background
        mMeasuringRect.left = getScrollX();
        mMeasuringRect.top = getScrollY();
        mMeasuringRect.right = drawingRect.left + mChannelLayoutWidth;
        mMeasuringRect.bottom = mMeasuringRect.top + getHeight();

        mPaint.setColor(mChannelLayoutBackground);
        canvas.drawRect(mMeasuringRect, mPaint);

        for (int pos = getFirstVisibleChannelPosition(); pos < getLastVisibleChannelPosition() + 2; pos++)
            if (pos < epgData.getChannelCount()) {
                drawChannelItem(canvas, pos, drawingRect);
            }
    }

    private void drawChannelItem(final Canvas canvas, int position, Rect drawingRect) {
        drawingRect.left = getScrollX();
        drawingRect.top = getTopFrom(position);
        drawingRect.right = drawingRect.left + mChannelLayoutWidth;
        drawingRect.bottom = drawingRect.top + mChannelLayoutHeight;

        mPaint.setColor(getResources().getColor(R.color.white));
        canvas.drawRect(drawingRect, mPaint);

        // Loading channel image into target for
        final String imageURL = epgData.getChannel(position).getImageURL();

        if (mChannelImageCache.containsKey(imageURL)) {
            Bitmap image = mChannelImageCache.get(imageURL);
            drawingRect = getDrawingRectForChannelImage(drawingRect, image);
            canvas.drawBitmap(image, null, drawingRect, null);
        } else {
            final int smallestSide = Math.min(mChannelLayoutHeight, mChannelLayoutWidth);

            if (!mChannelImageTargetCache.containsKey(imageURL)) {
                mChannelImageTargetCache.put(imageURL, new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mChannelImageCache.put(imageURL, bitmap);
                        redraw();
                        mChannelImageTargetCache.remove(imageURL);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                EPGUtil.loadImageInto(getContext(), imageURL, smallestSide, smallestSide, mChannelImageTargetCache.get(imageURL));
            }

        }
    }

    private Rect getDrawingRectForChannelImage(Rect drawingRect, Bitmap image) {
        drawingRect.left += mChannelLayoutPadding;
        drawingRect.top += mChannelLayoutPadding;
        drawingRect.right -= mChannelLayoutPadding;
        drawingRect.bottom -= mChannelLayoutPadding;

        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        final float imageRatio = imageHeight / (float) imageWidth;

        final int rectWidth = drawingRect.right - drawingRect.left;
        final int rectHeight = drawingRect.bottom - drawingRect.top;

        // Keep aspect ratio.
        if (imageWidth > imageHeight) {
            final int padding = (int) (rectHeight - (rectWidth * imageRatio)) / 2;
            drawingRect.top += padding;
            drawingRect.bottom -= padding;
        } else if (imageWidth <= imageHeight) {
            final int padding = (int) (rectWidth - (rectHeight / imageRatio)) / 2;
            drawingRect.left += padding;
            drawingRect.right -= padding;
        }

        return drawingRect;
    }

    private boolean shouldDrawTimeLine(long now) {
        return now >= mTimeLowerBoundary && now < mTimeUpperBoundary;
    }

    private boolean isEventVisible(final long start, final long end) {
        return (start >= mTimeLowerBoundary && start <= mTimeUpperBoundary)
                || (end >= mTimeLowerBoundary && end <= mTimeUpperBoundary)
                || (start <= mTimeLowerBoundary && end >= mTimeUpperBoundary);
    }

    private int getFirstVisibleChannelPosition() {
        final int y = getScrollY();

        int position = (y - mChannelLayoutMargin - mTimeBarHeight)
                / (mChannelLayoutHeight + mChannelLayoutMargin);

        if (position < 0) {
            position = 0;
        }
        return position;
    }

    private int getLastVisibleChannelPosition() {
        final int y = getScrollY();
        final int totalChannelCount = epgData.getChannelCount();
        final int screenHeight = getHeight();
        int position = (y + screenHeight - mTimeBarHeight - mChannelLayoutMargin)
                / (mChannelLayoutHeight + mChannelLayoutMargin);

        if (position > totalChannelCount - 1) {
            position = totalChannelCount - 1;
        }

        // Add one extra row if we don't fill screen with current..
        return (y + screenHeight) > (position * (mChannelLayoutHeight + mChannelLayoutMargin)) && position < totalChannelCount - 1 ? position + 1 : position;
    }

    private void calculateMaxHorizontalScroll() {
        mMaxHorizontalScroll = (int) ((END_TIME / mMillisPerPixel) - getWidth() + mChannelLayoutWidth + mChannelLayoutMargin);
    }

    private void calculateMaxVerticalScroll() {
        final int maxVerticalScroll = getTopFrom(epgData.getChannelCount() - 1) + mChannelLayoutHeight;
        mMaxVerticalScroll = maxVerticalScroll < getHeight() ? 0 : maxVerticalScroll - getHeight();
    }

    private int getXFrom(long time) {
        return (int) ((time - mTimeOffset) / mMillisPerPixel) + mChannelLayoutMargin
                + mChannelLayoutWidth + mChannelLayoutMargin;
    }

    private int getTopFrom(int position) {
        int y = position * (mChannelLayoutHeight + mChannelLayoutMargin)
                + mChannelLayoutMargin + mTimeBarHeight;
        return y;
    }

    private long getTimeFrom(int x) {
        return (x * mMillisPerPixel) + mTimeOffset;
    }

    private long calculateMillisPerPixel() {
        return HOURS_IN_VIEWPORT_MILLIS / getResources().getDisplayMetrics().widthPixels;
    }

    private void resetBoundaries() {
        mMillisPerPixel = calculateMillisPerPixel();
        mTimeOffset = 0;
        mTimeLowerBoundary = getTimeFrom(0);
        mTimeUpperBoundary = getTimeFrom(getWidth());
    }

    private Rect calculateChannelsHitArea() {
        mMeasuringRect.top = mTimeBarHeight;
        mMeasuringRect.bottom = getHeight();
        mMeasuringRect.left = 0;
        mMeasuringRect.right = mChannelLayoutWidth;
        return mMeasuringRect;
    }

    private Rect calculateProgramsHitArea() {
        mMeasuringRect.top = mTimeBarHeight;
        mMeasuringRect.bottom = getHeight();
        mMeasuringRect.left = mChannelLayoutWidth;
        mMeasuringRect.right = getWidth();
        return mMeasuringRect;
    }

    private int getChannelPosition(int y) {
        y -= mTimeBarHeight;
        int channelPosition = (y + mChannelLayoutMargin)
                / (mChannelLayoutHeight + mChannelLayoutMargin);

        if (epgData.getChannelCount() > channelPosition) {
            return epgData.getChannelCount() == 0 ? -1 : channelPosition;
        } else return -1;
    }

    private int getProgramPosition(int channelPosition, long time) {
        if (channelPosition < epgData.getChannelCount()) {
            List<EPGEvent> events = epgData.getEvents(channelPosition);

            if (events != null) {

                for (int eventPos = 0; eventPos < events.size(); eventPos++) {
                    EPGEvent event = events.get(eventPos);

                    if (event.getStart() <= time && event.getEnd() >= time) {
                        return eventPos;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Add click listener to the EPG.
     *
     * @param epgClickListener to add.
     */
    public void setEPGClickListener(EPGClickListener epgClickListener) {
        mClickListener = epgClickListener;
    }

    /**
     * Add data to EPG. This must be set for EPG to able to draw something.
     *
     * @param epgData pass in any implementation of EPGData.
     */
    public void setEPGData(EPGData epgData) {
        this.epgData = epgData;
    }

    public void recalculateAndRedraw() {
        if (epgData != null && epgData.hasData()) {
            resetBoundaries();

            calculateMaxVerticalScroll();
            calculateMaxHorizontalScroll();

            scrollTo(0, 0);

            redraw();
        }
    }

    /**
     * Does a invalidate() and requestLayout() which causes a redraw of screen.
     */
    public void redraw() {
        invalidate();
        requestLayout();
    }

    /**
     * Clears the local image cache for channel images. Can be used when leaving epg and you want to
     * free some memory. Images will be fetched again when loading EPG next time.
     */
    public void clearEPGImageCache() {
        mChannelImageCache.clear();
    }

    private float dptopx(int dp) {
        Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private class OnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            // This is absolute coordinate on screen not taking scroll into account.
            int x = (int) e.getX();
            int y = (int) e.getY();

            // Adding scroll to clicked coordinate
            int scrollX = getScrollX() + x;
            int scrollY = getScrollY() + y;

            int channelPosition = getChannelPosition(scrollY);
            if (channelPosition != -1 && mClickListener != null) {
                if (calculateChannelsHitArea().contains(x, y)) {
                    // Channel area is clicked
                    mClickListener.onChannelClicked(channelPosition, epgData.getChannel(channelPosition));
                } else if (calculateProgramsHitArea().contains(x, y)) {
                    // Event area is clicked
                    int programPosition = getProgramPosition(channelPosition, getTimeFrom(getScrollX() + x - calculateProgramsHitArea().left));
                    if (programPosition != -1) {
                        mClickListener.onEventClicked(channelPosition, programPosition, epgData.getEvent(channelPosition, programPosition));
                    }
                }
            }

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            int dx = (int) distanceX;
            int dy = (int) distanceY;
            int x = getScrollX();
            int y = getScrollY();


            // Avoid over scrolling
            if (x + dx < 0) {
                dx = 0 - x;
            }
            if (y + dy < 0) {
                dy = 0 - y;
            }
            if (x + dx > mMaxHorizontalScroll) {
                dx = mMaxHorizontalScroll - x;
            }
            if (y + dy > mMaxVerticalScroll) {
                dy = mMaxVerticalScroll - y;
            }

            scrollBy(dx, dy);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float vX, float vY) {

            mScroller.fling(getScrollX(), getScrollY(), -(int) vX,
                    -(int) vY, 0, mMaxHorizontalScroll, 0, mMaxVerticalScroll);

            redraw();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
                return true;
            }
            return true;
        }
    }

}
