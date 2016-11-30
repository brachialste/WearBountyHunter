package edu.training.wearbountyhunter;

import android.support.annotation.Nullable;
import android.support.wearable.view.WearableListView;
import android.widget.LinearLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by brachialste on 30/11/16.
 */

public class WearableListitemLayout extends LinearLayout
        implements WearableListView.OnCenterProximityListener{

    // propiedades de la clase
    private ImageView mImagen;
    private TextView mName;
    private float mFadedTextAlpha;

    public WearableListitemLayout(Context context) {
        super(context);
    }

    public WearableListitemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WearableListitemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mFadedTextAlpha = 40f / 100f;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // se obtiene la referencia a los elementos contenidos en el layout
        mImagen = (ImageView) findViewById(R.id.imagen);
        mName = (TextView) findViewById(R.id.name);
    }

    @Override
    public void onCenterPosition(boolean b) {
        mImagen.setAlpha(1f);
        mImagen.setImageAlpha(255);
        mImagen.setScaleX(1.4f);
        mImagen.setScaleY(1.4f);
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        mName.setAlpha(mFadedTextAlpha);
        mImagen.setImageAlpha(60);
        mImagen.setScaleX(.7f);
        mImagen.setScaleY(.7f);
    }
}
