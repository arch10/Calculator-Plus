package com.example.arch1.testapplication;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.SettingViewHolder> {

    private Context ctx;
    private ArrayList<ListData> list;
    private ListAdapter.OnSettingClickListener listener;

    public interface OnSettingClickListener {
        void OnSettingClick(ListData data, int position);
    }

    ListAdapter(Context context, ArrayList<ListData> list, ListAdapter.OnSettingClickListener listener) {
        ctx = context;
        this.list = list;
        this.listener = listener;
    }

    void setList(ArrayList<ListData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.setting_list_layout, parent, false);
        return new SettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        ListData data = list.get(position);

        holder.title.setText(data.getTitle());
        holder.body.setText(data.getBody());
        holder.icon.setImageResource(data.getImg());
        holder.icon.setColorFilter(ContextCompat.getColor(ctx, getColor()));
        holder.bind(data, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class SettingViewHolder extends RecyclerView.ViewHolder {

        TextView title, body;
        ImageView icon;

        SettingViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.desc);
            icon = itemView.findViewById(R.id.iv_icon);
        }

        void bind(final ListData data, final ListAdapter.OnSettingClickListener listener) {
            itemView.setOnClickListener(v -> listener.OnSettingClick(data, getAdapterPosition()));
        }
    }

    private int getColor() {
        AppPreferences preferences = AppPreferences.getInstance(ctx);
        String theme = preferences.getStringPreference(AppPreferences.APP_THEME);

        switch (theme) {
            case Theme.GREEN:
                return R.color.colorMaterialGreen;
            case Theme.ORANGE:
                return R.color.colorMaterialOrange;
            case Theme.BLUE:
                return R.color.colorMaterialBlue;
            case Theme.RED:
                return R.color.colorMaterialRed;
            case Theme.LIGHT_GREEN:
                return R.color.colorMaterialLGreen;
            case Theme.PINK:
                return R.color.colorMaterialPink;
            case Theme.PURPLE:
                return R.color.colorMaterialPurple;
            case Theme.MATERIAL_LIGHT:
                return R.color.colorMaterialBlue;
            case Theme.MATERIAL_DARK:
                return R.color.colorMaterialBlue;
            case Theme.DEFAULT:
                return R.color.colorMaterialSteelGrey;
            default:
                return R.color.colorMaterialBlue;
        }
    }
}
