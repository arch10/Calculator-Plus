package com.example.arch1.testapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    public ListAdapter(Context context, ArrayList<ListData> list, ListAdapter.OnSettingClickListener listener) {
        ctx = context;
        this.list = list;
        this.listener = listener;
    }

    public void setList(ArrayList<ListData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.setting_list_layout, parent, false);
        SettingViewHolder holder = new SettingViewHolder(view);
        return holder;
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

        public SettingViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.desc);
            icon = itemView.findViewById(R.id.iv_icon);
        }

        public void bind(final ListData data, final ListAdapter.OnSettingClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnSettingClick(data, getAdapterPosition());
                }
            });
        }
    }

    private int getColor() {
        AppPreferences preferences = AppPreferences.getInstance(ctx);
        String theme = preferences.getStringPreference(AppPreferences.APP_THEME);

        switch (theme) {
            case "green":
                return R.color.colorMaterialGreen;
            case "orange":
                return R.color.colorMaterialOrange;
            case "blue":
                return R.color.colorMaterialBlue;
            case "lgreen":
                return R.color.colorMaterialLGreen;
            case "pink":
                return R.color.colorMaterialPink;
            default:
                return R.color.colorMaterialSteelGrey;
        }
    }
}
