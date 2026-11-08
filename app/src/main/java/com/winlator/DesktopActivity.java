package com.winlator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextClock;
import com.winlator.container.Container;
import com.winlator.container.ContainerManager;

/** Windows 7-inspired touch desktop. Windows applications run through Winlator/Wine. */
public class DesktopActivity extends Activity {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    private GradientDrawable shape(int color, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);
        v.setGravity(Gravity.CENTER);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setShadowLayer(2, 1, 1, 0x99000000);
        return v;
    }

    private void openWinlator(int menuItem) {
        try {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("selected_menu_item_id", menuItem);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Winlator could not open: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(0xff174e78);
        getWindow().setNavigationBarColor(0xff123c5c);

        FrameLayout root = new FrameLayout(this);
        root.addView(new HillsWallpaper(this), new FrameLayout.LayoutParams(-1, -1));

        LinearLayout layer = new LinearLayout(this);
        layer.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams layerParams = new FrameLayout.LayoutParams(-1, -1);
        root.addView(layer, layerParams);

        LinearLayout desktop = new LinearLayout(this);
        desktop.setOrientation(LinearLayout.VERTICAL);
        desktop.setGravity(Gravity.TOP | Gravity.START);
        desktop.setPadding(dp(10), dp(10), dp(8), dp(8));
        layer.addView(desktop, new LinearLayout.LayoutParams(-1, 0, 1));

        addDesktopIcon(desktop, "▣", "Computer", 0xffd9f1ff, () -> openWinlator(R.id.menu_item_containers));
        addDesktopIcon(desktop, "▤", "Network", 0xffd9f1ff, () -> openWinlator(R.id.menu_item_settings));
        addDesktopIcon(desktop, "▧", "Programs", 0xffffe8a6, () -> openWinlator(R.id.menu_item_shortcuts));
        addDesktopIcon(desktop, "♲", "Recycle Bin", 0xffe8f5ff, () ->
            Toast.makeText(this, "Recycle Bin", Toast.LENGTH_SHORT).show());

        LinearLayout taskbar = new LinearLayout(this);
        taskbar.setGravity(Gravity.CENTER_VERTICAL);
        taskbar.setPadding(dp(7), dp(4), dp(9), dp(4));
        GradientDrawable glass = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xddb8e8ff, 0xee286a9b, 0xf51b4e7a});
        glass.setStroke(dp(1), 0xffa9dfff);
        taskbar.setBackground(glass);
        layer.addView(taskbar, new LinearLayout.LayoutParams(-1, dp(54)));

        TextView start = label("⊞", 30, Color.WHITE, true);
        GradientDrawable orb = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xffa8eaff, 0xff168bd2, 0xff07508e});
        orb.setShape(GradientDrawable.OVAL);
        orb.setStroke(dp(2), 0xffd5f4ff);
        start.setBackground(orb);
        start.setElevation(dp(5));
        taskbar.addView(start, new LinearLayout.LayoutParams(dp(46), dp(46)));
        start.setOnClickListener(v -> showStartMenu(v));

        TextView active = label("Windows 7 Mobile", 13, Color.WHITE, true);
        active.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        active.setPadding(dp(14), 0, 0, 0);
        active.setBackground(shape(0x554e9bd0, 0x668dd4ff, 6));
        LinearLayout.LayoutParams activeParams = new LinearLayout.LayoutParams(0, dp(40), 1);
        activeParams.setMargins(dp(9), 0, dp(8), 0);
        taskbar.addView(active, activeParams);
        active.setOnClickListener(v -> showStartMenu(start));

        TextClock clock = new TextClock(this);
        clock.setFormat12Hour("h:mm a");
        clock.setFormat24Hour("HH:mm");
        clock.setTextColor(Color.WHITE);
        clock.setTextSize(12);
        clock.setGravity(Gravity.CENTER);
        taskbar.addView(clock, new LinearLayout.LayoutParams(dp(62), -1));

        setContentView(root);
    }

    private void addDesktopIcon(LinearLayout parent, String glyph, String caption, int tint, Runnable action) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(2), dp(2), dp(2), dp(2));
        TextView icon = label(glyph, 34, tint, true);
        icon.setBackground(shape(0x332b8dc5, 0x557bd5ff, 9));
        item.addView(icon, new LinearLayout.LayoutParams(dp(58), dp(52)));
        TextView name = label(caption, 12, Color.WHITE, false);
        name.setShadowLayer(3, 1, 1, Color.BLACK);
        item.addView(name, new LinearLayout.LayoutParams(dp(94), dp(24)));
        parent.addView(item, new LinearLayout.LayoutParams(dp(100), dp(82)));
        item.setClickable(true);
        item.setFocusable(true);
        item.setOnClickListener(v -> action.run());
    }

    private void openFileManager() {
        ContainerManager manager = new ContainerManager(this);
        java.util.ArrayList<Container> containers = manager.getContainers();
        if (containers.isEmpty()) {
            Toast.makeText(this, "Create a Windows container first", Toast.LENGTH_LONG).show();
            openWinlator(R.id.menu_item_containers);
            return;
        }
        Container selected = containers.get(0);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("container_id", selected.id);
        intent.putExtra("start_path", "D:\\\\");
        startActivity(intent);
    }

    private void openInstallerFolder() {
        ContainerManager manager = new ContainerManager(this);
        if (manager.getContainers().isEmpty()) {
            Toast.makeText(this, "Create a Windows container first", Toast.LENGTH_LONG).show();
            openWinlator(R.id.menu_item_containers);
            return;
        }
        Toast.makeText(this, "Place your EXE or MSI file in Android Downloads. It is available as drive D: in the Windows File Manager.", Toast.LENGTH_LONG).show();
        openFileManager();
    }

    private void showStartMenu(View anchor) {
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setPadding(dp(10), dp(10), dp(10), dp(10));
        menu.setBackground(shape(0xf3eaf4fb, 0xff75b7e1, 10));

        TextView heading = label("Windows 7", 19, 0xff17466a, true);
        heading.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        heading.setPadding(dp(12), 0, 0, 0);
        menu.addView(heading, new LinearLayout.LayoutParams(-1, dp(46)));
        addMenuItem(menu, "Computer", "Manage Windows containers", () -> openWinlator(R.id.menu_item_containers));
        addMenuItem(menu, "Programs", "Installed Windows shortcuts", () -> openWinlator(R.id.menu_item_shortcuts));
        addMenuItem(menu, "File Manager", "Browse Windows drives and Downloads", this::openFileManager);
        addMenuItem(menu, "Install / Run EXE or MSI", "Open the Downloads folder", this::openInstallerFolder);
        addMenuItem(menu, "Control Panel", "Winlator settings", () -> openWinlator(R.id.menu_item_settings));
        addMenuItem(menu, "Windows Apps", "Open your containers", () -> openWinlator(R.id.menu_item_containers));
        View line = new View(this);
        line.setBackgroundColor(0xffb4cddd);
        menu.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
        addMenuItem(menu, "Exit Desktop", "Close this launcher", this::finish);

        PopupWindow popup = new PopupWindow(menu, dp(285), -2, true);
        popup.setBackgroundDrawable(shape(0xf3eaf4fb, 0xff75b7e1, 10));
        popup.setElevation(dp(18));
        popup.setOutsideTouchable(true);
        popup.showAtLocation(anchor, Gravity.BOTTOM | Gravity.LEFT, dp(7), dp(62));
    }

    private void addMenuItem(LinearLayout menu, String title, String subtitle, Runnable action) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(dp(13), dp(4), dp(8), dp(4));
        TextView t = label(title, 14, 0xff173d5a, true);
        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        TextView s = label(subtitle, 10, 0xff52758d, false);
        s.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        item.addView(t, new LinearLayout.LayoutParams(-1, dp(22)));
        item.addView(s, new LinearLayout.LayoutParams(-1, dp(18)));
        menu.addView(item, new LinearLayout.LayoutParams(-1, dp(48)));
        item.setClickable(true);
        item.setOnClickListener(v -> action.run());
    }

    /** Original rolling-hills wallpaper, painted locally so no network asset is needed. */
    private static class HillsWallpaper extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        HillsWallpaper(Activity activity) { super(activity); }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w = getWidth(), h = getHeight();
            p.setShader(new LinearGradient(0, 0, 0, h * .72f,
                new int[]{0xff39a9e6, 0xff8ed8f1, 0xffd6eff4}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p);
            p.setShader(null);
            p.setColor(0xfffff3bd); c.drawCircle(w * .78f, h * .23f, Math.min(w,h) * .085f, p);
            Path back = new Path();
            back.moveTo(0, h*.67f); back.cubicTo(w*.25f,h*.43f,w*.42f,h*.75f,w*.66f,h*.57f);
            back.cubicTo(w*.82f,h*.45f,w*.9f,h*.56f,w,h*.48f); back.lineTo(w,h); back.lineTo(0,h); back.close();
            p.setShader(new LinearGradient(0,h*.48f,0,h,new int[]{0xff71b95c,0xff287a35},null,Shader.TileMode.CLAMP));
            c.drawPath(back,p); p.setShader(null);
            Path front = new Path();
            front.moveTo(0,h*.78f); front.cubicTo(w*.22f,h*.63f,w*.38f,h*.78f,w*.58f,h*.72f);
            front.cubicTo(w*.76f,h*.62f,w*.88f,h*.74f,w,h*.66f); front.lineTo(w,h); front.lineTo(0,h); front.close();
            p.setShader(new LinearGradient(0,h*.65f,0,h,new int[]{0xff5eaa39,0xff155d29},null,Shader.TileMode.CLAMP));
            c.drawPath(front,p); p.setShader(null);
        }
    }
}
