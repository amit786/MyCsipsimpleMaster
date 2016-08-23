package com.csipsimple.f5chat.group;//package com.csipsimple.f5chat.group;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.database.DataSetObserver;
//import android.graphics.Canvas;
//import android.graphics.Rect;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.support.v4.view.ViewCompat;
//import android.support.v4.widget.EdgeEffectCompat;
//import android.util.AttributeSet;
//import android.view.GestureDetector;
//import android.view.HapticFeedbackConstants;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.ScrollView;
//import android.widget.Scroller;
//
//import com.csipsimple.R;
//
//
//// @formatter:off
///**
// * A view that shows items in a horizontally scrolling list. The items
// * come from the {@link ListAdapter} associated with this view. <br>
// * <br>
// * <b>Limitations:</b>
// * <ul>
// * <li>Does not support keyboard navigation</li>
// * <li>Does not support scroll bars<li>
// * <li>Does not support header or footer views<li>
// * <li>Does not support disabled items<li>
// * </ul>
// * <br>
// * <b>Custom XML Parameters Supported:</b><br>
// * <br>
// * <ul>
// * <li><b>divider</b> - The divider to use between items. This can be a color or a drawable. If a drawable is used
// * dividerWidth will automatically be set to the intrinsic width of the provided drawable, this can be overriden by providing a dividerWidth.</li>
// * <li><b>dividerWidth</b> - The width of the divider to be drawn.</li>
// * <li><b>android:requiresFadingEdge</b> - If horizontal fading edges are enabled this view will render them</li>
// * <li><b>android:fadingEdgeLength</b> - The length of the horizontal fading edges</li>
// * </ul>
// */
//// @formatter:on
//public class HorizontalListView extends AdapterView<ListAdapter> {
//    /**
//     * Defines where to insert items into the ViewGroup, as defined in {@code ViewGroup #addViewInLayout(View, int, LayoutParams, boolean)}
//     */
//    private static final int INSERT_AT_END_OF_LIST = -1;
//    private static final int INSERT_AT_START_OF_LIST = 0;
//
//    /**
//     * The velocity to use for overscroll absorption
//     */
//    private static final float FLING_DEFAULT_ABSORB_VELOCITY = 30f;
//
//    /**
//     * The friction amount to use for the fling tracker
//     */
//    private static final float FLING_FRICTION = 0.009f;
//
//    /**
//     * Used for tracking the state data necessary to restore the HorizontalListView to its previous state after a rotation occurs
//     */
//    private static final String BUNDLE_ID_CURRENT_X = "BUNDLE_ID_CURRENT_X";
//
//    /**
//     * The bundle id of the parents state. Used to restore the parent's state after a rotation occurs
//     */
//    private static final String BUNDLE_ID_PARENT_STATE = "BUNDLE_ID_PARENT_STATE";
//
//    /**
//     * Tracks ongoing flings
//     */
//    protected Scroller mFlingTracker = new Scroller(getContext());
//
//    /**
//     * Gesture listener to receive callbacks when gestures are detected
//     */
//    private final GestureListener mGestureListener = new GestureListener();
//
//    /**
//     * Used for detecting gestures within this view so they can be handled
//     */
//    private GestureDetector mGestureDetector;
//
//    /**
//     * This tracks the starting layout position of the leftmost view
//     */
//    private int mDisplayOffset;
//
//    /**
//     * Holds a reference to the adapter bound to this view
//     */
//    protected ListAdapter mAdapter;
//
//    /**
//     * Holds a cache of recycled views to be reused as needed
//     */
//    private List<Queue<View>> mRemovedViewsCache = new ArrayList<Queue<View>>();
//
//    /**
//     * Flag used to mark when the adapters data has changed, so the view can be relaid out
//     */
//    private boolean mDataChanged = false;
//
//    /**
//     * Temporary rectangle to be used for measurements
//     */
//    private Rect mRect = new Rect();
//
//    /**
//     * Tracks the currently touched view, used to delegate touches to the view being touched
//     */
//    private View mViewBeingTouched = null;
//
//    /**
//     * The width of the divider that will be used between list items
//     */
//    private int mDividerWidth = 0;
//
//    /**
//     * The drawable that will be used as the list divider
//     */
//    private Drawable mDivider = null;
//
//    /**
//     * The x position of the currently rendered view
//     */
//    protected int mCurrentX;
//
//    /**
//     * The x position of the next to be rendered view
//     */
//    protected int mNextX;
//
//    /**
//     * Used to hold the scroll position to restore to post rotate
//     */
//    private Integer mRestoreX = null;
//
//    /**
//     * Tracks the maximum possible X position, stays at max value until last item is laid out and it can be determined
//     */
//    private int mMaxX = Integer.MAX_VALUE;
//
//    /**
//     * The adapter index of the leftmost view currently visible
//     */
//    private int mLeftViewAdapterIndex;
//
//    /**
//     * The adapter index of the rightmost view currently visible
//     */
//    private int mRightViewAdapterIndex;
//
//    /**
//     * This tracks the currently selected accessibility item
//     */
//    private int mCurrentlySelectedAdapterIndex;
//
//    /**
//     * Callback interface to notify listener that the user has scrolled this view to the point that it is low on data.
//     */
//    private RunningOutOfDataListener mRunningOutOfDataListener = null;
//
//    /**
//     * This tracks the user value set of how many items from the end will be considered running out of data.
//     */
//    private int mRunningOutOfDataThreshold = 0;
//
//    /**
//     * Tracks if we have told the listener that we are running low on data. We only want to tell them once.
//     */
//    private boolean mHasNotifiedRunningLowOnData = false;
//
//    /**
//     * Callback interface to be invoked when the scroll state has changed.
//     */
//    private OnScrollStateChangedListener mOnScrollStateChangedListener = null;
//
//    /**
//     * Represents the current scroll state of this view. Needed so we can detect when the state changes so scroll listener can be notified.
//     */
//    private OnScrollStateChangedListener.ScrollState mCurrentScrollState = OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE;
//
//    /**
//     * Tracks the state of the left edge glow.
//     */
//    private EdgeEffectCompat mEdgeGlowLeft;
//
//    /**
//     * Tracks the state of the right edge glow.
//     */
//    private EdgeEffectCompat mEdgeGlowRight;
//
//    /**
//     * The height measure spec for this view, used to help size children views
//     */
//    private int mHeightMeasureSpec;
//
//    /**
//     * Used to track if a view touch should be blocked because it stopped a fling
//     */
//    private boolean mBlockTouchAction = false;
//
//    /**
//     * Used to track if the parent vertically scrollable view has been told to DisallowInterceptTouchEvent
//     */
//    private boolean mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = false;
//
//    /**
//     * The listener that receives notifications when this view is clicked.
//     */
//    private OnClickListener mOnClickListener;
//
//    public HorizontalListView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mEdgeGlowLeft = new EdgeEffectCompat(context);
//        mEdgeGlowRight = new EdgeEffectCompat(context);
//        mGestureDetector = new GestureDetector(context, mGestureListener);
//        bindGestureDetector();
//        initView();
//        retrieveXmlConfiguration(context, attrs);
//        setWillNotDraw(false);
//
//// If the OS version is high enough then set the friction on the fling tracker */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            HoneycombPlus.setFriction(mFlingTracker, FLING_FRICTION);
//        }
//    }
//
//    /**
//     * Registers the gesture detector to receive gesture notifications for this view
//     */
//    private void bindGestureDetector() {
//// Generic touch listener that can be applied to any view that needs to process gestures
//        final View.OnTouchListener gestureListenerHandler = new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(final View v, final MotionEvent event) {
//// Delegate the touch event to our gesture detector
//                return mGestureDetector.onTouchEvent(event);
//            }
//        };
//
//        setOnTouchListener(gestureListenerHandler);
//    }
//
//    /**
//     * When this HorizontalListView is embedded within a vertical scrolling view it is important to disable the parent view from interacting with
//     * any touch events while the user is scrolling within this HorizontalListView. This will start at this view and go up the view tree looking
//     * for a vertical scrolling view. If one is found it will enable or disable parent touch interception.
//     *
//     * @param disallowIntercept If true the parent will be prevented from intercepting child touch events
//     */
//    private void requestParentListViewToNotInterceptTouchEvents(Boolean disallowIntercept) {
//// Prevent calling this more than once needlessly
//        if (mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent != disallowIntercept) {
//            View view = this;
//
//            while (view.getParent() instanceof View) {
//// If the parent is a ListView or ScrollView then disallow intercepting of touch events
//                if (view.getParent() instanceof ListView || view.getParent() instanceof ScrollView) {
//                    view.getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
//                    mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = disallowIntercept;
//                    return;
//                }
//
//                view = (View) view.getParent();
//            }
//        }
//    }
//
//    /**
//     * Parse the XML configuration for this widget
//     *
//     * @param context Context used for extracting attributes
//     * @param attrs   The Attribute Set containing the ColumnView attributes
//     */
//    private void retrieveXmlConfiguration(Context context, AttributeSet attrs) {
//        if (attrs != null) {
//            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalListView);
//
//// Get the provided drawable from the XML
//            final Drawable d = a.getDrawable(R.styleable.HorizontalListView_android_divider);
//            if (d != null) {
//// If a drawable is provided to use as the divider then use its intrinsic width for the divider width
//                setDivider(d);
//            }
//
//// If a width is explicitly specified then use that width
//            final int dividerWidth = a.getDimensionPixelSize(R.styleable.HorizontalListView_dividerWidth, 0);
//            if (dividerWidth != 0) {
//                setDividerWidth(dividerWidth);
//            }
//
//            a.recycle();
//        }
//    }
//
//    @Override
//    public Parcelable onSaveInstanceState() {
//        Bundle bundle = new Bundle();
//
//// Add the parent state to the bundle
//        bundle.putParcelable(BUNDLE_ID_PARENT_STATE, super.onSaveInstanceState());
//
//// Add our state to the bundle
//        bundle.putInt(BUNDLE_ID_CURRENT_X, mCurrentX);
//
//        return bundle;
//    }
//
//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        if (state instanceof Bundle) {
//            Bundle bundle = (Bundle) state;
//
//// Restore our state from the bundle
//            mRestoreX = Integer.valueOf((bundle.getInt(BUNDLE_ID_CURRENT_X)));
//
//// Restore out parent's state from the bundle
//            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_ID_PARENT_STATE));
//        }
//    }
//
//    /**
//     * Sets the drawable that will be drawn between each item in the list. If the drawable does
//     * not have an intrinsic width, you should also call {@link #setDividerWidth(int)}
//     *
//     * @param divider The drawable to use.
//     */
//    public void setDivider(Drawable divider) {
//        mDivider = divider;
//
//        if (divider != null) {
//            setDividerWidth(divider.getIntrinsicWidth());
//        } else {
//            setDividerWidth(0);
//        }
//    }
//
//    /**
//     * Sets the width of the divider that will be drawn between each item in the list. Calling
//     * this will override the intrinsic width as set by {@link #setDivider(Drawable)}
//     *
//     * @param width The width of the divider in pixels.
//     */
//    public void setDividerWidth(int width) {
//        mDividerWidth = width;
//
//// Force the view to rerender itself
//        requestLayout();
//        invalidate();
//    }
//
//    private void initView() {
//        mLeftViewAdapterIndex = -1;
//        mRightViewAdapterIndex = -1;
//        mDisplayOffset = 0;
//        mCurrentX = 0;
//        mNextX = 0;
//        mMaxX = Integer.MAX_VALUE;
//        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
//    }
//
//    /**
//     * Will re-initialize the HorizontalListView to remove all child views rendered and reset to initial configuration.
//     */
//    private void reset() {
//        initView();
//        removeAllViewsInLayout();
//        requestLayout();
//    }
//
//    /**
//     * DataSetObserver used to capture adapter data change events
//     */
//    private DataSetObserver mAdapterDataObserver = new DataSetObserver() {
//        @Override
//        public void onChanged() {
//            mDataChanged = true;
//
//// Clear so we can notify again as we run out of data
//            mHasNotifiedRunningLowOnData = false;
//
//            unpressTouchedChild();
//
//// Invalidate and request layout to force this view to completely redraw itself
//            invalidate();
//            requestLayout();
//        }
//
//        @Override
//        public void onInvalidated() {
//// Clear so we can notify again as we run out of data
//            mHasNotifiedRunningLowOnData = false;
//
//            unpressTouchedChild();
//            reset();
//
//// Invalidate and request layout to force this view to completely redraw itself
//            invalidate();
//            requestLayout();
//        }
//    };
//
//    @Override
//    public void setSelection(int position) {
//        mCurrentlySelectedAdapterIndex = position;
//    }
//
//    @Override
//    public View getSelectedView() {
//        return getChild(mCurrentlySelectedAdapterIndex);
//    }
//
//    @Override
//    public void setAdapter(ListAdapter adapter) {
//        if (mAdapter != null) {
//            mAdapter.unregisterDataSetObserver(mAdapterDataObserver);
//        }
//
//        if (adapter != null) {
//// Clear so we can notify again as we run out of data
//            mHasNotifiedRunningLowOnData = false;
//
//            mAdapter = adapter;
//            mAdapter.registerDataSetObserver(mAdapterDataObserver);
//        }
//
//        initializeRecycledViewCache(mAdapter.getViewTypeCount());
//        reset();
//    }
//
//    @Override
//    public ListAdapter getAdapter() {
//        return mAdapter;
//    }
//
//    /**
//     * Will create and initialize a cache for the given number of different types of views.
//     *
//     * @param viewTypeCount - The total number of different views supported
//     */
//    private void initializeRecycledViewCache(int viewTypeCount) {
//// The cache is created such that the response from mAdapter.getItemViewType is the array index to the correct cache for that item.
//        mRemovedViewsCache.clear();
//        for (int i = 0; i < viewTypeCount; i++) {
//            mRemovedViewsCache.add(new LinkedList<View>());
//        }
//    }
//
//    /**
//     * Returns a recycled view from the cache that can be reused, or null if one is not available.
//     *
//     * @param adapterIndex
//     * @return
//     */
//    private View getRecycledView(int adapterIndex) {
//        int itemViewType = mAdapter.getItemViewType(adapterIndex);
//
//        if (isItemViewTypeValid(itemViewType)) {
//            return mRemovedViewsCache.get(itemViewType).poll();
//        }
//
//        return null;
//    }
//
//    /**
//     * Adds the provided view to a recycled views cache.
//     *
//     * @param adapterIndex
//     * @param view
//     */
//    private void recycleView(int adapterIndex, View view) {
//// There is one Queue of views for each different type of view.
//// Just add the view to the pile of other views of the same type.
//// The order they are added and removed does not matter.
//        int itemViewType = mAdapter.getItemViewType(adapterIndex);
//        if (isItemViewTypeValid(itemViewType)) {
//            mRemovedViewsCache.get(itemViewType).offer(view);
//        }
//    }
//
//    private boolean isItemViewTypeValid(int itemViewType) {
//        return itemViewType < mRemovedViewsCache.size();
//    }
//
//    /**
//     * Adds a child to this viewgroup and measures it so it renders the correct size
//     */
//    private void addAndMeasureChild(final View child, int viewPos) {
//        LayoutParams params = getLayoutParams(child);
//        addViewInLayout(child, viewPos, params, true);
//        measureChild(child);
//    }
//
//    /**
//     * Measure the provided child.
//     *
//     * @param child The child.
//     */
//    private void measureChild(View child) {
//        ViewGroup.LayoutParams childLayoutParams = getLayoutParams(child);
//        int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, getPaddingTop() + getPaddingBottom(), childLayoutParams.height);
//
//        int childWidthSpec;
//        if (childLayoutParams.width > 0) {
//            childWidthSpec = MeasureSpec.makeMeasureSpec(childLayoutParams.width, MeasureSpec.EXACTLY);
//        } else {
//            childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//        }
//
//        child.measure(childWidthSpec, childHeightSpec);
//    }
//
//    /**
//     * Gets a child's layout parameters, defaults if not available.
//     */
//    private ViewGroup.LayoutParams getLayoutParams(View child) {
//        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
//        if (layoutParams == null) {
//// Since this is a horizontal list view default to matching the parents height, and wrapping the width
//            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        }
//
//        return layoutParams;
//    }
//
//    @SuppressLint("WrongCall")
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//
//        if (mAdapter == null) {
//            return;
//        }
//
//// Force the OS to redraw this view
//        invalidate();
//
//// If the data changed then reset everything and render from scratch at the same offset as last time
//        if (mDataChanged) {
//            int oldCurrentX = mCurrentX;
//            initView();
//            removeAllViewsInLayout();
//            mNextX = oldCurrentX;
//            mDataChanged = false;
//        }
//
//// If restoring from a rotation
//        if (mRestoreX != null) {
//            mNextX = mRestoreX;
//            mRestoreX = null;
//        }
//
//// If in a fling
//        if (mFlingTracker.computeScrollOffset()) {
//// Compute the next position
//            mNextX = mFlingTracker.getCurrX();
//        }
//
//// Prevent scrolling past 0 so you can't scroll past the end of the list to the left
//        if (mNextX < 0) {
//            mNextX = 0;
//
//// Show an edge effect absorbing the current velocity
//            if (mEdgeGlowLeft.isFinished()) {
//                mEdgeGlowLeft.onAbsorb((int) determineFlingAbsorbVelocity());
//            }
//
//            mFlingTracker.forceFinished(true);
//            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
//        } else if (mNextX > mMaxX) {
//// Clip the maximum scroll position at mMaxX so you can't scroll past the end of the list to the right
//            mNextX = mMaxX;
//
//// Show an edge effect absorbing the current velocity
//            if (mEdgeGlowRight.isFinished()) {
//                mEdgeGlowRight.onAbsorb((int) determineFlingAbsorbVelocity());
//            }
//
//            mFlingTracker.forceFinished(true);
//            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
//        }
//
//// Calculate our delta from the last time the view was drawn
//        int dx = mCurrentX - mNextX;
//        removeNonVisibleChildren(dx);
//        fillList(dx);
//        positionChildren(dx);
//
//// Since the view has now been drawn, update our current position
//        mCurrentX = mNextX;
//
//// If we have scrolled enough to lay out all views, then determine the maximum scroll position now
//        if (determineMaxX()) {
//// Redo the layout pass since we now know the maximum scroll position
//            onLayout(changed, left, top, right, bottom);
//            return;
//        }
//
//// If the fling has finished
//        if (mFlingTracker.isFinished()) {
//// If the fling just ended
//            if (mCurrentScrollState == OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING) {
//                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
//            }
//        } else {
//// Still in a fling so schedule the next frame
//            ViewCompat.postOnAnimation(this, mDelayedLayout);
//        }
//    }
//
//    @Override
//    protected float getLeftFadingEdgeStrength() {
//        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
//
//// If completely at the edge then disable the fading edge
//        if (mCurrentX == 0) {
//            return 0;
//        } else if (mCurrentX < horizontalFadingEdgeLength) {
//// We are very close to the edge, so enable the fading edge proportional to the distance from the edge, and the width of the edge effect
//            return (float) mCurrentX / horizontalFadingEdgeLength;
//        } else {
//// The current x position is more then the width of the fading edge so enable it fully.
//            return 1;
//        }
//    }
//}
