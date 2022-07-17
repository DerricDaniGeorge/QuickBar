package com.derric.quickbar.uicomponents;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.derric.quickbar.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPalettePreference extends Preference {

    public ColorPalettePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ColorPalettePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ColorPalettePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPalettePreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        RelativeLayout colorPaletteLayout = holder.itemView.findViewById(R.id.color_palette_layout);
        int colorId = preferences.getInt("quickBarColor", R.color.google_sheet_green);
        switch (colorId) {
            case R.color.blue:
                colorPaletteLayout.findViewById(R.id.blue).setSelected(true);
                break;
            case R.color.pink:
                colorPaletteLayout.findViewById(R.id.pink).setSelected(true);
                break;
            case R.color.orange:
                colorPaletteLayout.findViewById(R.id.orange).setSelected(true);
                break;
            case R.color.light_red:
                colorPaletteLayout.findViewById(R.id.lightRed).setSelected(true);
                break;
            case R.color.grey_white:
                colorPaletteLayout.findViewById(R.id.grey_white).setSelected(true);
                break;
            case R.color.blueish_black:
                colorPaletteLayout.findViewById(R.id.blueish_black).setSelected(true);
                break;
            case R.color.google_sheet_green:
                colorPaletteLayout.findViewById(R.id.google_sheet_green).setSelected(true);
                break;
            case R.color.google_slides_yellow:
                colorPaletteLayout.findViewById(R.id.google_slides_yellow).setSelected(true);
                break;
        }
        SharedPreferences.Editor editor = preferences.edit();
        List<ImageButton> colors = new ArrayList<>();

        for (int i = 0; i < colorPaletteLayout.getChildCount(); i++) {
            View childView = colorPaletteLayout.getChildAt(i);
            if (childView instanceof ImageButton) {
                colors.add((ImageButton) childView);
            }
        }
        for (ImageButton colorButton : colors) {
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Highlight currently selected color button
                    v.setSelected(true);
                    //Unselect all other color buttons
                    for (ImageButton otherButton : colors) {
                        if (otherButton.getId() != v.getId()) {
                            otherButton.setSelected(false);
                        }
                    }
                    //Save the color to settings

                    switch (v.getId()) {
                        case R.id.pink:
                            editor.putInt("quickBarColor", R.color.pink);
                            break;
                        case R.id.orange:
                            editor.putInt("quickBarColor", R.color.orange);
                            break;
                        case R.id.lightRed:
                            editor.putInt("quickBarColor", R.color.light_red);
                            break;
                        case R.id.blue:
                            editor.putInt("quickBarColor", R.color.blue);
                            break;
                        case R.id.grey_white:
                            editor.putInt("quickBarColor", R.color.grey_white);
                            break;
                        case R.id.blueish_black:
                            editor.putInt("quickBarColor", R.color.blueish_black);
                            break;
                        case R.id.google_sheet_green:
                            editor.putInt("quickBarColor", R.color.google_sheet_green);
                            break;
                        case R.id.google_slides_yellow:
                            editor.putInt("quickBarColor", R.color.google_slides_yellow);
                            break;
                    }
                    editor.commit();
                }
            });
        }

    }
}
