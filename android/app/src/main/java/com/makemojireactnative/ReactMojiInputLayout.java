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
import com.makemoji.mojilib.HyperMojiListener;
import com.makemoji.mojilib.Moji;
import com.makemoji.mojilib.MojiInputLayout;

/**
 * Created by s_baa on 8/6/2016.
 */
public class ReactMojiInputLayout extends SimpleViewManager<MojiInputLayout> {
    @Override
    public String getName() {
        return "RCTMojiInputLayout";
    }

    @Override
    protected MojiInputLayout createViewInstance(final ThemedReactContext reactContext) {
        final MojiInputLayout mojiInputLayout = new MojiInputLayout(reactContext);
        Log.d(getName(),"createviewinstance");
        mojiInputLayout.setSendLayoutClickListener(new MojiInputLayout.SendClickListener() {
            @Override
            public boolean onClick(String html, Spanned spanned) {
                WritableMap event = Arguments.createMap();
                event.putString("html", html);
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        mojiInputLayout.getId(),
                        "onSendPressed",
                        event);
                return true;
            }
        });
        mojiInputLayout.setHyperMojiClickListener(new HyperMojiListener() {
            @Override
            public void onClick(String url) {
                WritableMap event = Arguments.createMap();
                event.putString("url", url);
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        mojiInputLayout.getId(),
                        "onHyperMojiClick",
                        event);
            }
            });
        mojiInputLayout.setCameraButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WritableMap event = Arguments.createMap();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        mojiInputLayout.getId(),
                        "onCameraPressed",
                        event);
                mojiInputLayout.invalidate();
                mojiInputLayout.requestLayout();
                mojiInputLayout.layout(mojiInputLayout.getLeft(),mojiInputLayout.getTop(),mojiInputLayout.getRight(),mojiInputLayout.getBottom());

                Toast.makeText(mojiInputLayout.getContext(),"camera click",Toast.LENGTH_SHORT).show();
            }
            });
        return mojiInputLayout;
    }


}
