package com.android.mms.presenters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.mms.ui.Presenter;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public abstract class RecyclePresenter<V extends View, M> extends Presenter<M> {

    public RecyclePresenter(Context context, M modelInterface) {
        super(context, modelInterface);
    }

    @Override
    public final View present(ViewGroup parent, PresenterOptions presenterOptions) {
        V view = null;
        Class<V> recycleClass = getViewClass();
        if (presenterOptions.shouldRecycle()) {
            // Only try to recycle if parent has one child
            if (parent.getChildCount() == 1) {
                Class viewClass = parent.getChildAt(0).getClass();
                if (viewClass.equals(recycleClass)) {
                    // View was recycled, lets re-use it
                    view = (V) parent.getChildAt(0);
                }
            }
        } else {
            view = presenterOptions.getCachedView(recycleClass);
            if (view != null) {
                parent.addView(view);
            }
        }

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            //noinspection unchecked
            view = inflateView(getPreviewAttachmentLayoutId(), parent);
            parent.addView(view);
        }

        bindMessageAttachmentView(view, presenterOptions);
        return view;
    }

    private V inflateView(int resId, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        //noinspection unchecked
        return (V) layoutInflater.inflate(getPreviewAttachmentLayoutId(), parent, false);
    }

    @Override
    public final void presentThumbnail(ViewGroup parent, AttachmentPresenterOptions presenterOptions) {
        super.presentThumbnail(parent, presenterOptions);
        V view = inflateView(getPreviewAttachmentLayoutId(), parent);
        parent.addView(view);
        bindPreviewAttachmentView(view, presenterOptions);
    }

    protected abstract Class<V> getViewClass();
    public abstract int getMessageAttachmentLayoutId();
    protected abstract void bindMessageAttachmentView(V view, PresenterOptions presenterOptions);
    public abstract int getPreviewAttachmentLayoutId();
    protected abstract void bindPreviewAttachmentView(V view, AttachmentPresenterOptions presenterOptions);
}