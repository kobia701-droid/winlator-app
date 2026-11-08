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

/**
 * Lightweight Windows 7-inspired launcher shell for Winlator.
 * The Windows compatibility layer remains provided by Winlator/Wine/Box64.
 */
public class DesktopActivity extends Activity {
    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int strokeColor, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        if (strokeColor != 0) drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private TextView label(String text, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private void openWinlator(int menuItem) {
        Intent intent = new Intent(this, MainActivity.class);
        if (menuItem != 0) intent.putExtra("selected_menu_item_id", menuItem);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(17, 65, 105));
        getWindow().setNavigationBarColor(Color.rgb(12, 31, 51));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{Color.rgb(20, 100, 160), Color.rgb(10, 48, 91), Color.rgb(4, 25, 53)}));

        LinearLayout desktop = new LinearLayout(this);
        desktop.setOrientation(LinearLayout.VERTICAL);
        desktop.setPadding(dp(14), dp(16), dp(14), dp(8));
        desktop.setGravity(Gravity.TOP | Gravity.START);
        root.addView(desktop, new LinearLayout.LayoutParams(-1, 0, 1));

        addDesktopIcon(desktop, "🖥", "Computer", () -> openWinlator(R.id.menu_item_containers));
        addDesktopIcon(desktop, "📁", "Programs", () -> openWinlator(R.id.menu_item_shortcuts));
        addDesktopIcon(desktop, "🗑", "Recycle Bin", () ->
            Toast.makeText(this, "Recycle Bin is not connected yet", Toast.LENGTH_SHORT).show());
        addDesktopIcon(desktop, "⚙", "Control Panel", () -> openWinlator(R.id.menu_item_settings));

        LinearLayout taskbar = new LinearLayout(this);
        taskbar.setGravity(Gravity.CENTER_VERTICAL);
        taskbar.setPadding(dp(8), dp(5), dp(10), dp(5));
        taskbar.setBackground(shape(Color.argb(235, 16, 39, 62), Color.argb(180, 150, 205, 235), 0));
        root.addView(taskbar, new LinearLayout.LayoutParams(-1, dp(58)));

        TextView start = label("⊞", 29, Color.WHITE, true);
        start.setBackground(shape(Color.rgb(36, 130, 190), Color.rgb(170, 220, 245), 24));
        taskbar.addView(start, new LinearLayout.LayoutParams(dp(54), dp(46)));
        start.setOnClickListener(v -> showStartMenu(v));

        TextView taskTitle = label("Windows 7 Mobile", 14, Color.WHITE, true);
        taskTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        taskTitle.setPadding(dp(12), 0, 0, 0);
        taskbar.addView(taskTitle, new LinearLayout.LayoutParams(0, -1, 1));

        TextView clock = label("Winlator", 12, Color.WHITE, false);
        taskbar.addView(clock, new LinearLayout.LayoutParams(dp(76), -1));

        setContentView(root);
    }

    private void addDesktopIcon(LinearLayout parent, String icon, String name, Runnable action) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(4), dp(5), dp(4), dp(5));
        TextView glyph = label(icon, 30, Color.WHITE, false);
        item.addView(glyph, new LinearLayout.LayoutParams(dp(64), dp(48)));
        TextView caption = label(name, 12, Color.WHITE, false);
        caption.setShadowLayer(3, 1, 1, Color.BLACK);
        item.addView(caption, new LinearLayout.LayoutParams(dp(86), dp(26)));
        parent.addView(item, new LinearLayout.LayoutParams(dp(94), dp(88)));
        item.setOnClickListener(v -> action.run());
    }

    private void showStartMenu(View anchor) {
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setPadding(dp(10), dp(10), dp(10), dp(10));
        menu.setBackground(shape(Color.rgb(225, 237, 247), Color.rgb(80, 125, 160), 12));
        addMenuItem(menu, "🖥  Computer", () -> openWinlator(R.id.menu_item_containers));
        addMenuItem(menu, "📁  Programs", () -> openWinlator(R.id.menu_item_shortcuts));
        addMenuItem(menu, "⚙  Control Panel", () -> openWinlator(R.id.menu_item_settings));
        addMenuItem(menu, "▶  Run Windows Apps", () -> openWinlator(R.id.menu_item_containers));
        addMenuItem(menu, "⏻  Exit Desktop", this::finish);
        PopupWindow popup = new PopupWindow(menu, dp(245), -2, true);
        popup.setBackgroundDrawable(shape(Color.rgb(225, 237, 247), Color.rgb(80, 125, 160), 12));
        popup.setElevation(dp(12));
        popup.setOutsideTouchable(true);
        popup.showAtLocation(anchor, Gravity.BOTTOM | Gravity.LEFT, dp(8), dp(64));
    }

    private void addMenuItem(LinearLayout menu, String text, Runnable action) {
        TextView item = label(text, 15, Color.rgb(25, 45, 65), false);
        item.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        item.setPadding(dp(12), 0, dp(8), 0);
        menu.addView(item, new LinearLayout.LayoutParams(-1, dp(48)));
        item.setOnClickListener(v -> action.run());
    }
}
