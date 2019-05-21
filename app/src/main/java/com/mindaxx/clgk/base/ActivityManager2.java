package com.mindaxx.clgk.base;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2018/5/16.
 */

public class ActivityManager2 {

    private static Stack<WeakReference<Activity>> activityStack = new Stack<>();
    private static String TAG = "ActivityManager2 xxx";

    /**
     * 添加Activity到堆栈
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(new WeakReference<Activity>(activity));
    }


    /*
    * 从栈中移除
    * */
    public static void removeActivity(Activity activity) {
        try {
            if (activityStack == null) return;
            activityStack.remove(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        try {
            if (activityStack == null) return;
            List<Activity> activities = new ArrayList<Activity>();
            for (WeakReference<Activity> activity : activityStack) {
                if (activity.get() == null) continue;
                if (activity.get().getClass().equals(cls)) {
                    activities.add(activity.get());
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Iterator<Activity> iterator = activities.iterator();
                    if (iterator.hasNext()) {
                        iterator.next().finish();
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            activityStack.removeAll(activities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        try {
            if (activityStack == null) return;
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    WeakReference<Activity> activity = activityStack.get(i);
                    if (activity.get() == null) continue;
                    if (!activity.get().isFinishing()) {
                        try {
                            activity.get().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            activityStack.clear();
            activityStack = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
