package cc.aoeiuv020.hookmcmods;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressWarnings("RedundantThrows")
public class MainHook implements IXposedHookLoadPackage {
    @SuppressWarnings("All")
    private static final boolean DEBUG = BuildConfig.DEBUG && false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage: " + lpparam.processName + ", " + lpparam.processName);
        XposedHelpers.findAndHookMethod(Instrumentation.class, "callApplicationOnCreate", Application.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!(param.args[0] instanceof Application)) return;
                hookDebug(lpparam);
/*
                hookAdMobManager(lpparam);
                hookYandexAdsManager(lpparam);
                hookLoading(lpparam);
                hookEarn(lpparam);
                hookShowAd(lpparam);
                hookBack(lpparam);
*/
                hookAccess(lpparam);
                hookRate(lpparam);
            }
        });

    }

    private void hookRate(XC_LoadPackage.LoadPackageParam lpparam) {
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        };

        XposedHelpers.findAndHookMethod(
                "zp0",
                lpparam.classLoader, "z",
                XposedHelpers.findClass("androidx.fragment.app.FragmentManager", lpparam.classLoader),
                XposedHelpers.findClass("java.lang.String", lpparam.classLoader),
                r
        );
    }

    private void hookAccess(XC_LoadPackage.LoadPackageParam lpparam) {
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        };

        XposedHelpers.findAndHookMethod(
                "o01",
                lpparam.classLoader, "f", r
        );
    }

    private void hookBack(XC_LoadPackage.LoadPackageParam lpparam) {
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(param.thisObject, "finish");
                param.setResult(null);
            }
        };

        XposedHelpers.findAndHookMethod(
                "com.appscreat.project.activity.ActivityItem",
                lpparam.classLoader, "onBackPressed", r
        );
    }

    private void hookEarn(XC_LoadPackage.LoadPackageParam lpparam) {
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(99999999);
            }
        };

        XposedHelpers.findAndHookMethod(
                "n21",
                lpparam.classLoader, "b", r
        );
    }

    private void hookShowAd(XC_LoadPackage.LoadPackageParam lpparam) {
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log(param);
                param.setResult(null);
            }
        };
        XposedHelpers.findAndHookMethod(
                "com.appscreat.project.ads.yandex.YandexAdsInterstitial",
                lpparam.classLoader, "onShowAd", Activity.class, r
        );
        XposedHelpers.findAndHookMethod(
                "com.appscreat.project.ads.admob.AdMobInterstitial",
                lpparam.classLoader, "onShowAd", Activity.class, r
        );
    }

    private void hookLoading(XC_LoadPackage.LoadPackageParam lpparam) {
        var r = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(param.thisObject, "m0");
            }
        };

        XposedHelpers.findAndHookMethod(
                "com.appscreat.project.activity.ActivityLoading",
                lpparam.classLoader, "onResume", r
        );
    }

    private void hookYandexAdsManager(XC_LoadPackage.LoadPackageParam lpparam) {
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        };

        XposedHelpers.findAndHookMethod(
                "com.appscreat.project.ads.yandex.YandexAdsManager",
                lpparam.classLoader, "isYandexAdsEnabled", r
        );
    }

    private void hookAdMobManager(XC_LoadPackage.LoadPackageParam lpparam) {
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        };

        XposedHelpers.findAndHookMethod(
                "com.appscreat.project.ads.admob.AdMobManager",
                lpparam.classLoader, "isAdMobEnabled", r
        );
    }

    private void log(XC_MethodHook.MethodHookParam param) {
        XposedBridge.log("hook: " + param.thisObject.getClass().getName() + "." + param.method.getName());
        if (DEBUG) {
            XposedBridge.log(new Throwable());
        }
    }

    private void hookDebug(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!DEBUG) {
            return;
        }
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // 无功能，必要时断点使用的，
                log(param);
            }
        };

        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader, "onResume", r
        );
    }
}
