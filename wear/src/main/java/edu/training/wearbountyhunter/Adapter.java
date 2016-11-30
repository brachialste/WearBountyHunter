package edu.training.wearbountyhunter;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by brachialste on 30/11/16.
 */

public class Adapter extends WearableListView.Adapter {

    private String[][] mDataset;
    private final LayoutInflater mInflater;
    private int iModo;

    public Adapter(Context context, String[][] mDataset, int iModo) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = mDataset;
        this.iModo = iModo;
    }

    // clase interna que define el ViewHolder
    public static class ItemViewHolder extends WearableListView.ViewHolder{
        private TextView textView;
        private ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            // fin the text view within the custom item's layout
            textView = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.imagen);
        }
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ItemViewHolder((mInflater.inflate(R.layout.list_item, null)));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        // retrieve the text view
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        // reemplaza los elementos con lo que viene en el array
        TextView view = itemViewHolder.textView;
        ImageView image = itemViewHolder.imageView;
        view.setText(mDataset[position][1]);
        view.setTag(mDataset[position][0]);
        if(iModo == 0){
            image.setImageResource(R.drawable.fug);
        }else{
            image.setImageResource(R.drawable.capt);
        }

        // coloca el dataa en el tag del itemView
        ((ItemViewHolder) holder).itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
