package br.com.liveo.mvp.base;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.lang.reflect.Method;

import br.com.liveo.mvp.R;

/**
 * Created by rudsonlima on 8/29/17.
 */

public abstract class BaseFragmentActivity extends FragmentActivity {

    public enum ActivityAnimation {
        TRANSLATE_LEFT, TRANSLATE_RIGHT, TRANSLATE_UP, TRANSLATE_DOWN, TRANSLATE_FADE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected ViewDataBinding bindView(int layout) {
        return DataBindingUtil.setContentView(this, layout);
    }
    
    public void onEventKeyboard(final KeyboardVisibilityEventListener listener) {
        KeyboardVisibilityEvent.setEventListener(this,
                isOpen -> {
                    if (listener != null) {
                        listener.onVisibilityChanged(isOpen);
                    }
                });
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
        }
    }

    public void startActivity(Intent intent, final ActivityAnimation animation) {
        startActivity(intent);
        putAnimation(this, animation);
    }

    public void startActivityForResult(Intent intent, int requestCode, final ActivityAnimation animation) {
        startActivityForResult(intent, requestCode);
        putAnimation(this, animation);
    }

    public void finish(final ActivityAnimation animation) {
        finish();
        putAnimation(this, animation);
    }

    private static void putAnimation(final Activity source,
                                     final ActivityAnimation animation) {
        try {
            Method method = Activity.class.getMethod("overridePendingTransition", int.class, int.class);

            int[] animations = getAnimation(animation);
            method.invoke(source, animations[0], animations[1]);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private static int[] getAnimation(final ActivityAnimation animation) {
        int exitAnim;
        int enterAnim;

        switch (animation) {

            case TRANSLATE_UP:
                enterAnim = R.anim.translate_slide_up;
                exitAnim = R.anim.translate_no_change;
                break;

            case TRANSLATE_DOWN:
                enterAnim = R.anim.translate_no_change;
                exitAnim = R.anim.translate_slide_down;
                break;

            case TRANSLATE_RIGHT:
                enterAnim = R.anim.translate_right_enter;
                exitAnim = R.anim.translate_right_exit;
                break;

            case TRANSLATE_FADE:
                enterAnim = R.anim.translate_fade_in;
                exitAnim = R.anim.translate_fade_out;
                break;

            case TRANSLATE_LEFT:
            default:
                enterAnim = R.anim.translate_left_enter;
                exitAnim = R.anim.translate_left_exit;
                break;
        }

        return new int[]{enterAnim, exitAnim};
    }
}
