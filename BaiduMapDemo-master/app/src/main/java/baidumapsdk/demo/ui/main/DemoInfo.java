package baidumapsdk.demo.ui.main;

import android.app.Activity;

/**
 * Created by hxw on 2017/4/22.
 */

public class DemoInfo {
    private final int title;
    private final int desc;
    private final Class<? extends Activity> demoClass;

    public DemoInfo(int title, int desc,
                    Class<? extends Activity> demoClass) {
        this.title = title;
        this.desc = desc;
        this.demoClass = demoClass;
    }

    public int getTitle() {
        return title;
    }

    public int getDesc() {
        return desc;
    }

    public Class<? extends Activity> getDemoClass() {
        return demoClass;
    }
}
