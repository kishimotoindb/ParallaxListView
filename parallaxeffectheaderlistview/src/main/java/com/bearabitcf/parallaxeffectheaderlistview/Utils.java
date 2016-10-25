package com.bearabitcf.parallaxeffectheaderlistview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by bearabit on 2016/9/11 16:46.
 */
public class Utils {
    private static final boolean IS_LOG = true;
    private static final boolean IS_LOG_Event_Dispatch = false;

    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dp * density);
        return px;
    }

    public static String convertAction2String(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            default:
                return "ACTION_OTHER";
        }
    }

    public static void logEventDispatch(String tag, String content, MotionEvent ev) {
        String action = Utils.convertAction2String(ev);
        localLog(tag, content + action, IS_LOG_Event_Dispatch);
    }

    public static void log(String tag, String content) {
        localLog(tag, content, IS_LOG);
    }

    private static void localLog(String tag, String content, boolean isLog) {
        if (isLog) {
            if (TextUtils.isEmpty(tag)) {
                tag = "Default";
            }
            Log.i(tag, content);
        }
    }


}
