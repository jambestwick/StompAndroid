package com.yxytech.parkingcloud.baselibrary.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * 基础的Fragment 实现则Retrifit生命周期的绑定
 * Created by Water on 2018/3/26.
 */

public abstract class BaseFragment extends Fragment implements LifecycleProvider<FragmentEvent> {

    protected String TAG;
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();
    protected ProgressDialog proDia; // 加载进度条
    protected Context mContext;
    public View view;
    protected int pageSize = 20;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
        mContext = context;
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        proDia = new ProgressDialog(getContext());
        if (view != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null)
                viewGroup.removeView(view);
        } else {
            view = inflater.inflate(getLayoutId(), container, false);

            onBindViewBefore(view);
            if (savedInstanceState != null) {
                onRestartInstance(savedInstanceState);
            }
            initView(view);
            initData();
        }
        return view;
    }

    /**
     * 获取ViewDataBing
     *
     * @param <T>
     * @return
     */
    public <T extends ViewDataBinding> T getBind() {
        return DataBindingUtil.bind(view);
    }


    protected void initData() {
    }


    protected void initView(View view) {
    }


    /**
     * 恢复数据的方法
     *
     * @param savedInstanceState
     */
    protected void onRestartInstance(Bundle savedInstanceState) {

    }

    ;

    /**
     * 绑定数据之前要做的是激情
     *
     * @param view
     */
    protected void onBindViewBefore(View view) {
        //do something...
    }

    ;

    /**
     * 添加布局
     *
     * @return
     */
    protected abstract int getLayoutId();


    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

      public  void onFragmentShow(){}


}
