package ru.vvdev.yamap;

import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.vvdev.yamap.view.YamapView;

public class YamapViewManager extends ViewGroupManager<YamapView> {
    public static final String REACT_CLASS = "YamapView";

    private static final int SET_CENTER = 1;
    private static final int FIT_ALL_MARKERS = 2;
    private static final int SET_ZOOM = 3;
    private static final int GET_CAMERA_POSITION = 4;
    private static final int GET_VISIBLE_REGION = 5;
    private static final int SET_TRAFFIC_VISIBLE = 6;

    YamapViewManager() {
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .build();
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put("cameraPosition", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onCameraPositionReceived")))
                .put("cameraPositionChanged", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onCameraPositionChange")))
                .put("visibleRegion", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onVisibleRegionReceived")))
                .put("onMapPress", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onMapPress")))
                .put("onMapLongPress", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onMapLongPress")))
                .build();
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "setCenter",
                SET_CENTER,
                "fitAllMarkers",
                FIT_ALL_MARKERS,
                "setZoom",
                SET_ZOOM,
                "getCameraPosition",
                GET_CAMERA_POSITION,
                "getVisibleRegion",
                GET_VISIBLE_REGION,
                "setTrafficVisible",
                SET_TRAFFIC_VISIBLE);
    }

    @Override
    public void receiveCommand(
            @NonNull YamapView view,
            String commandType,
            @Nullable ReadableArray args) {
        Assertions.assertNotNull(view);
        Assertions.assertNotNull(args);
        switch (commandType) {
            case "setCenter":
                setCenter(castToYaMapView(view), args.getMap(0), (float) args.getDouble(1), (float) args.getDouble(2), (float) args.getDouble(3), (float) args.getDouble(4), args.getInt(5));
                return;
            case "fitAllMarkers":
                fitAllMarkers(view);
                return;
            case "setZoom":
                if (args != null) {
                    view.setZoom((float)args.getDouble(0), (float)args.getDouble(1), args.getInt(2));
                }
                return;
            case "getCameraPosition":
                if (args != null) {
                    view.emitCameraPositionToJS(args.getString(0));
                }
                return;
            case "getVisibleRegion":
                if (args != null) {
                    view.emitVisibleRegionToJS(args.getString(0));
                }
                return;
            case "setTrafficVisible":
                if (args != null) {
                   view.setTrafficVisible(args.getBoolean(0));
                }
                return;
            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported command %d received by %s.",
                        commandType,
                        getClass().getSimpleName()));
        }
    }

    private YamapView castToYaMapView(View view) {
        return (YamapView) view;
    }

    @Nonnull
    @Override
    public YamapView createViewInstance(@Nonnull ThemedReactContext context) {
        YamapView view = new YamapView(context);
        MapKitFactory.getInstance().onStart();
        view.onStart();
        return view;
    }

    private void setCenter(YamapView view, ReadableMap center, float zoom, float azimuth, float tilt, float duration, int animation) {
        if (center != null) {
            Point centerPosition = new Point(center.getDouble("lat"), center.getDouble("lon"));
            CameraPosition pos = new CameraPosition(centerPosition, zoom, azimuth, tilt);
            view.setCenter(pos, duration, animation);
        }
    }

    private void fitAllMarkers(View view) {
        castToYaMapView(view).fitAllMarkers();
    }

    // props
    @ReactProp(name = "userLocationIcon")
    public void setUserLocationIcon(View view, String icon) {
        if (icon != null) {
            castToYaMapView(view).setUserLocationIcon(icon);
        }
    }

    @ReactProp(name = "userLocationAccuracyFillColor")
    public void setUserLocationAccuracyFillColor(View view, int color) {
        castToYaMapView(view).setUserLocationAccuracyFillColor(color);
    }

    @ReactProp(name = "userLocationAccuracyStrokeColor")
    public void setUserLocationAccuracyStrokeColor(View view, int color) {
        castToYaMapView(view).setUserLocationAccuracyStrokeColor(color);
    }

    @ReactProp(name = "userLocationAccuracyStrokeWidth")
    public void setUserLocationAccuracyStrokeWidth(View view, float width) {
        castToYaMapView(view).setUserLocationAccuracyStrokeWidth(width);
    }

    @ReactProp(name = "showUserPosition")
    public void setShowUserPosition(View view, Boolean show) {
        castToYaMapView(view).setShowUserPosition(show);
    }

    @ReactProp(name = "nightMode")
    public void setNightMode(View view, Boolean nightMode) {
        castToYaMapView(view).setNightMode(nightMode != null ? nightMode : false);
    }

    @ReactProp(name = "scrollGesturesEnabled")
    public void setScrollGesturesEnabled(View view, Boolean scrollGesturesEnabled) {
        castToYaMapView(view).setScrollGesturesEnabled(scrollGesturesEnabled == true);
    }

    @ReactProp(name = "rotateGesturesEnabled")
    public void setRotateGesturesEnabled(View view, Boolean rotateGesturesEnabled) {
        castToYaMapView(view).setRotateGesturesEnabled(rotateGesturesEnabled == true);
    }

    @ReactProp(name = "zoomGesturesEnabled")
    public void setZoomGesturesEnabled(View view, Boolean zoomGesturesEnabled) {
        castToYaMapView(view).setZoomGesturesEnabled(zoomGesturesEnabled == true);
    }

    @ReactProp(name = "tiltGesturesEnabled")
    public void setTiltGesturesEnabled(View view, Boolean tiltGesturesEnabled) {
        castToYaMapView(view).setTiltGesturesEnabled(tiltGesturesEnabled == true);
    }

    @ReactProp(name = "mapStyle")
    public void setMapStyle(View view, String style) {
        if (style != null) {
            castToYaMapView(view).setMapStyle(style);
        }
    }

    @Override
    public void addView(YamapView parent, View child, int index) {
        parent.addFeature(child, index);
        super.addView(parent, child, index);
    }

    @Override
    public void removeViewAt(YamapView parent, int index) {
        parent.removeChild(index);
        super.removeViewAt(parent, index);
    }
}
