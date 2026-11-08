package com.winlator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/** Windows 7-inspired Aero desktop for the Winlator compatibility engine. */
public class DesktopActivity extends Activity {
    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
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
            Intent intent = new Intent(DesktopActivity.this, MainActivity.class);
            intent.putExtra("selected_menu_item_id", menuItem);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open Winlator: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(8, 48, 87));
        getWindow().setNavigationBarColor(Color.rgb(8, 35, 62));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable wallpaper = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{0xff39a9df, 0xff0879bd, 0xff064b91, 0xff031d4b});
        root.setBackground(wallpaper);

        // Desktop area with the clean, left-aligned Windows desktop icon column.
        LinearLayout desktop = new LinearLayout(this);
        desktop.setOrientation(LinearLayout.VERTICAL);
        desktop.setGravity(Gravity.TOP | Gravity.START);
        desktop.setPadding(dp(12), dp(12), dp(12), dp(8));
        root.addView(desktop, new LinearLayout.LayoutParams(-1, 0, 1));

        addDesktopIcon(desktop, "🖥️", "Computer", () -> openWinlator(R.id.menu_item_containers));
        addDesktopIcon(desktop, "🌐", "Network", () -> openWinlator(R.id.menu_item_settings));
        addDesktopIcon(desktop, "📁", "Programs", () -> openWinlator(R.id.menu_item_shortcuts));
        addDesktopIcon(desktop, "🗑️", "Recycle Bin", () ->
            Toast.makeText(this, "Recycle Bin is not connected yet", Toast.LENGTH_SHORT).show());

        // Aero-glass taskbar.
        LinearLayout taskbar = new LinearLayout(this);
        taskbar.setGravity(Gravity.CENTER_VERTICAL);
        taskbar.setPadding(dp(7), dp(5), dp(8), dp(5));
        taskbar.setBackground(shape(0xdd163d61, 0x887fc9f4, 0));
        root.addView(taskbar, new LinearLayout.LayoutParams(-1, dp(54)));

        TextView start = text("⊞", 31, Color.WHITE, true);
        start.setShadowLayer(3, 0, 1, 0xff17466a);
        start.setBackground(shape(0xff1688c8, 0xffb9eaff, 28));
        taskbar.addView(start, new LinearLayout.LayoutParams(dp(48), dp(44)));
        start.setOnClickListener(v -> showStartMenu(v));

        TextView appButton = text("Windows 7 Mobile", 13, Color.WHITE, true);
        appButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        appButton.setPadding(dp(12), 0, 0, 0);
        appButton.setBackground(shape(0x553e86b7, 0x557fc9f4, 5));
        LinearLayout.LayoutParams appParams = new LinearLayout.LayoutParams(0, dp(40), 1);
        appParams.setMargins(dp(8), 0, dp(8), 0);
        taskbar.addView(appButton, appParams);
        appButton.setOnClickListener(v -> showStartMenu(start));

        TextView clock = text("Winlator", 11, Color.WHITE, false);
        taskbar.addView(clock, new LinearLayout.LayoutParams(dp(68), -1));

        setContentView(root);
    }

    private void addDesktopIcon(LinearLayout parent, String glyph, String caption, Runnable action) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(3), dp(3), dp(3), dp(3));
        TextView icon = text(glyph, 31, Color.WHITE, false);
        item.addView(icon, new LinearLayout.LayoutParams(dp(68), dp(48)));
        TextView name = text(caption, 12, Color.WHITE, false);
        name.setShadowLayer(3, 1, 1, Color.BLACK);
        item.addView(name, new LinearLayout.LayoutParams(dp(94), dp(25)));
        parent.addView(item, new LinearLayout.LayoutParams(dp(100), dp(83)));
        item.setClickable(true);
        item.setFocusable(true);
        item.setOnClickListener(v -> action.run());
    }

    private void showStartMenu(View anchor) {
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setPadding(dp(9), dp(9), dp(9), dp(9));
        menu.setBackground(shape(0xf2e8f2fa, 0xff75a9ce, 9));

        TextView heading = text("Windows 7", 18, 0xff173d5b, true);
        heading.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        heading.setPadding(dp(12), 0, 0, 0);
        menu.addView(heading, new LinearLayout.LayoutParams(-1, dp(44)));
        addMenuItem(menu, "Computer", () -> openWinlator(R.id.menu_item_containers));
        addMenuItem(menu, "Programs", () -> openWinlator(R.id.menu_item_shortcuts));
        addMenuItem(menu, "Control Panel", () -> openWinlator(R.id.menu_item_settings));
        addMenuItem(menu, "Run Windows Apps", () -> openWinlator(R.id.menu_item_containers));
        addMenuItem(menu, "Exit Desktop", this::finish);

        PopupWindow popup = new PopupWindow(menu, dp(260), -2, true);
        popup.setBackgroundDrawable(shape(0xf2e8f2fa, 0xff75a9ce, 9));
        popup.setElevation(dp(16));
        popup.setOutsideTouchable(true);
        popup.showAtLocation(anchor, Gravity.BOTTOM | Gravity.LEFT, dp(7), dp(61));
    }

    private void addMenuItem(LinearLayout menu, String title, Runnable action) {
        TextView item = text(title, 14, 0xff193c59, false);
        item.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        item.setPadding(dp(14), 0, dp(8), 0);
        menu.addView(item, new LinearLayout.LayoutParams(-1, dp(46)));
        item.setClickable(true);
        item.setOnClickListener(v -> action.run());
    }
}
