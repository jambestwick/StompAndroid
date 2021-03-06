package com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by Water on 2017/4/20.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private Object[] mIdsAndViews = new Object[0];

    private OnItemClickListener mOnItemClickListener;

    private boolean mInitClickListener;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    };

    private final View.OnClickListener mOnItemCheckedChangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemCheckedChangeListener != null && v instanceof Checkable){
                mOnItemCheckedChangeListener.onItemCheck(v,((Checkable) v).isChecked(),getAdapterPosition());
            }
        }
    };

    private OnItemLongClickListener mOnItemLongClickListener;

    private boolean mInitLongClickListener;

    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null){
                return mOnItemLongClickListener.onItemLongClick(v,getAdapterPosition());
            }
            return false;
        }
    };

    private OnItemCheckedChangeListener mOnItemCheckedChangeListener;

    public BaseViewHolder(View itemView) {
        super(itemView);
//        AutoUtils.autoSize(itemView);
        mInitClickListener = false;
        mInitLongClickListener = false;
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

        if (onItemClickListener != null && !mInitClickListener){
            mInitClickListener = true;
            this.itemView.setOnClickListener(mOnClickListener);
        }
    }

    void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.mOnItemLongClickListener = onItemLongClickListener;

        if (onItemLongClickListener != null && !mInitLongClickListener){
            mInitLongClickListener = true;
            this.itemView.setOnLongClickListener(mOnLongClickListener);
        }
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onItemCheckedChangeListener) {
        this.mOnItemCheckedChangeListener = onItemCheckedChangeListener;
    }

    /**
     * ??????????????????????????????????????????clickable???true????????????{@link BaseAdapter#setOnItemClickListener(OnItemClickListener)}
     * ?????????????????????????????????,????????????{@link BaseAdapter#setOnItemClickListener(OnItemClickListener)}
     * @param id ?????????????????????View Id
     * @param clickable true?????????????????????false?????????????????????
     * @return ??????this
     */
    public BaseViewHolder setClickable(@IdRes int id, boolean clickable){
        View view = find(id);
        if (view != null){
            if (clickable){
                view.setOnClickListener(mOnClickListener);
            }else{
                view.setOnClickListener(null);
            }
        }
        return this;
    }

    /**
     * ??????????????????????????????????????????clickable???true????????????{@link BaseAdapter#setOnItemClickListener(OnItemClickListener)}
     * ?????????????????????????????????,????????????{@link BaseAdapter#setOnItemClickListener(OnItemClickListener)}
     * @param id ?????????????????????View Id
     * @param longClickable true?????????????????????false?????????????????????
     * @return ??????this
     */
    public BaseViewHolder setLongClickable(@IdRes int id, boolean longClickable){
        View view = find(id);
        if (view != null){
            if (longClickable){
                view.setOnLongClickListener(mOnLongClickListener);
            }else{
                view.setOnLongClickListener(null);
            }
        }
        return this;
    }

    /**
     *
     * @param id ????????? {@link Checkable} ?????????View ID
     * @param checkable ??????????????????????????????
     * @return ??????this
     */
    public BaseViewHolder setCheckable(@IdRes int id, boolean checkable){
        final View view = find(id);
        if (view == null){
            return this;
        }
        if (!checkable){
            if (view instanceof Checkable){
                view.setOnClickListener(null);
            }
            return this;
        }
        if (view instanceof Checkable){
            view.setOnClickListener(mOnItemCheckedChangeClickListener);
        }
        return this;
    }

    public BaseViewHolder setText(@IdRes int textId, String text){
        TextView textView = find(textId);
        if (textView != null){
            textView.setText(text);
        }
        return this;
    }

    public BaseViewHolder setText(@IdRes int textId, @StringRes int stringId){
        String text = itemView.getResources().getString(stringId);
        return setText(textId,text);
    }

    public BaseViewHolder setText(@IdRes int textId, TextCallback callback){
        TextView textView = find(textId);
        if (textView != null && callback != null){
            callback.callback(textView);
        }
        return this;
    }

    public BaseViewHolder setImage(@IdRes int imageId, @DrawableRes int resId){
        ImageView imageView = find(imageId);
        if (imageView != null){
            imageView.setImageResource(resId);
        }
        return this;
    }

    public BaseViewHolder setImage(@IdRes int imageId, @Nullable Drawable drawable){
        ImageView imageView = find(imageId);
        if (imageView != null){
            imageView.setImageDrawable(drawable);
        }
        return this;
    }

    public BaseViewHolder setImage(@IdRes int imageId, Bitmap bm){
        ImageView imageView = find(imageId);
        if (imageView != null){
            imageView.setImageBitmap(bm);
        }
        return this;
    }

    public BaseViewHolder setImage(@IdRes int imageId, ImageCallback callback){
        ImageView imageView = find(imageId);
        if (imageView != null && callback != null){
            callback.callback(imageView);
        }
        return this;
    }

    /**
     * ??????View???????????????
     * @param viewId ???????????????{@link Checkable}?????????View Id
     * @param checked ??????????????????
     * @return ??????this
     */
    public BaseViewHolder setChecked(@IdRes int viewId, boolean checked){
        View view = find(viewId);
        if (view == null){
            return this;
        }
        if (view instanceof Checkable){
            if (((Checkable) view).isChecked() != checked){
                ((Checkable) view).setChecked(checked);
            }
        }
        return this;
    }

    public BaseViewHolder setView(@IdRes int viewId, ViewCallback callback){
        if (find(viewId) != null && callback != null){
            callback.callback(find(viewId));
        }
        return this;
    }

    public BaseViewHolder setTypeface(@IdRes int textId, Typeface typeface){
        TextView textView = find(textId);
        if (textView != null){
            textView.setTypeface(typeface);
        }
        return this;
    }

    /**
     * ??????View???visibility??????
     * @param id View id
     * @param visibility ???????????????{@link View#GONE},{@link View#VISIBLE}??????{@link View#INVISIBLE}
     * @return ??????this
     */
    public BaseViewHolder setVisibility(@IdRes int id, int visibility){
        View view = find(id);
        if (view != null){
            view.setVisibility(visibility);
        }
        return this;
    }

    /**
     * ????????????id???????????????View??????
     * @param viewId View id
     * @param <T> ???View???????????????
     * @return ????????????id????????????View?????????????????????????????????null
     */
    @CheckResult
    public <T extends View> T find(@IdRes int viewId){
        int indexToAdd = -1;
        for (int i = 0; i < mIdsAndViews.length; i+=2) {
            Integer id = (Integer) mIdsAndViews[i];
            if (id != null && id == viewId){
                return (T) mIdsAndViews[i+1];
            }

            if (id == null){
                indexToAdd = i;
            }
        }

        if (indexToAdd == -1){
            indexToAdd = mIdsAndViews.length;
            mIdsAndViews = Arrays.copyOf(mIdsAndViews,
                    indexToAdd < 2 ? 2 : indexToAdd * 2);
        }

        mIdsAndViews[indexToAdd] = viewId;
        mIdsAndViews[indexToAdd+1] = itemView.findViewById(viewId);
        return (T) mIdsAndViews[indexToAdd+1];
    }

    /**
     * ????????????id???????????????ImageView??????
     * @param imageId ImageView id
     * @return ????????????id?????????ImageView?????????????????????????????????null
     */
    @CheckResult
    public ImageView findImage(@IdRes int imageId){
        return find(imageId);
    }

    /**
     * ????????????id???????????????TextView??????
     * @param textId TextView id
     * @return ????????????id?????????TextView?????????????????????????????????null
     */
    @CheckResult
    public TextView findText(@IdRes int textId){
        return find(textId);
    }
}
