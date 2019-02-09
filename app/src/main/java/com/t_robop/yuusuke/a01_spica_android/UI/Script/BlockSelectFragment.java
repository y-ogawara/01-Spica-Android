package com.t_robop.yuusuke.a01_spica_android.UI.Script;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.t_robop.yuusuke.a01_spica_android.Block;
import com.t_robop.yuusuke.a01_spica_android.MyApplication;
import com.t_robop.yuusuke.a01_spica_android.R;
import com.t_robop.yuusuke.a01_spica_android.databinding.FragmentBlockSelectBinding;
import com.t_robop.yuusuke.a01_spica_android.model.UIBlockModel;

public class BlockSelectFragment extends Fragment implements ScriptContract.SelectView {

    private ScriptContract.Presenter mScriptPresenter;

    private FragmentBlockSelectBinding mBinding;

    public BlockSelectFragment() {
    }

    private BlockClickListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_block_select, container, false);
        View root = mBinding.getRoot();

        mBinding.bgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(BlockSelectFragment.this).commit();
            }
        });

        UIBlockModel script = mScriptPresenter.getTargetScript();

        mBinding.selectDialogBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mBinding.susumu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButton(Block.FrontBlock.id);
            }
        });
        mBinding.magaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButton(Block.LeftBlock.id);
            }
        });
        mBinding.sagaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButton(Block.BackBlock.id);
            }
        });

        int colorFilterInt = MyApplication.getInstance().getResources().getColor(R.color.filter_color);
        if (script.getIfState() != 0) {
            mBinding.mosimoImageview.setColorFilter(colorFilterInt);
        } else {
            mBinding.mosimo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickButton(Block.IfStartBlock.id);
                }
            });
        }
        if (script.isInLoop()) {
            mBinding.kurikaesuImageview.setColorFilter(colorFilterInt);
        } else {
            mBinding.kurikaesu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickButton(Block.ForStartBlock.id);
                }
            });
        }
        if (!(script.getIfState() != 0 && script.isInLoop())) {
            mBinding.nukeruImageview.setColorFilter(colorFilterInt);
        } else {
            mBinding.nukeru.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickButton(Block.BreakBlock.id);
                }
            });
        }

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popupAnimation(mBinding.selectDialogBg);
        alphaAnimation(mBinding.bgSelect);
    }

    @Deprecated
    public void drawArrangeableBlocks() {
    }

    @Override
    public void setPresenter(ScriptContract.Presenter presenter) {
        this.mScriptPresenter = presenter;
    }


    public interface BlockClickListener {
        void onClickButton(String blockId);
    }

    // FragmentがActivityに追加されたら呼ばれるメソッド
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BlockClickListener) {
            mListener = (BlockClickListener) context;
        }
    }

    // FragmentがActivityから離れたら呼ばれるメソッド
    @Override
    public void onDetach() {
        super.onDetach();
        // 画面からFragmentが離れたあとに処理が呼ばれることを避けるためにNullで初期化しておく
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void popupAnimation(View view) {
        // ScaleAnimation(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue)
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.01f, 1.0f, 0.01f, 1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // animation時間 ms
        scaleAnimation.setDuration(200);
        // 繰り返し回数
        scaleAnimation.setRepeatCount(0);
        // animationが終わったそのまま表示にする
        scaleAnimation.setFillAfter(true);
        //アニメーションの開始
        view.startAnimation(scaleAnimation);
    }

    private void alphaAnimation(View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0.5f);
        // animation時間 ms
        alphaAnimation.setDuration(200);
        // 繰り返し回数
        alphaAnimation.setRepeatCount(0);
        // animationが終わったそのまま表示にする
        alphaAnimation.setFillAfter(true);
        //アニメーションの開始
        view.startAnimation(alphaAnimation);
    }
}
