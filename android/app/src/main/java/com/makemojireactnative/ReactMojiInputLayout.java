package com.makemojireactnative;

import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by s_baa on 8/6/2016.
 */
public class ReactMojiInputLayout extends SimpleViewManager<ResizeableLL> {
    @Override
    public String getName() {
        return "RCTMojiInputLayout";
    }

    @Override
    protected ResizeableLL createViewInstance(final ThemedReactContext reactContext) {
        final ResizeableLL mojiInputLayout = new ResizeableLL(reactContext);
        mojiInputLayout.setFocusable(true);
        mojiInputLayout.setFocusableInTouchMode(true);
        Log.d(getName(),"createviewinstance");
        return mojiInputLayout;
    }


}
