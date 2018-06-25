package com.example.arch1.testapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {


    private Context ctx;
    private ArrayList<Calculations> list;
    private HistoryAdapter.OnHistoryClickListener listener;
    private OnHistoryLongPressListener longPressListener;

    public interface OnHistoryClickListener {
        void onHistoryClick(Calculations data, int position);
    }

    public interface OnHistoryLongPressListener {
        void onHistoryLongPressed(Calculations data, int position);
    }

    public HistoryAdapter(Context context, ArrayList<Calculations> list, OnHistoryClickListener listener,
                          OnHistoryLongPressListener longPressListener) {
        this.ctx = context;
        this.list = list;
        this.listener = listener;
        this.longPressListener = longPressListener;
    }

    public void setList(ArrayList<Calculations> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.history_item_layout, parent, false);
        HistoryViewHolder holder = new HistoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Calculations calculations = list.get(position);

        holder.title.setText(calculations.equation);
        holder.body.setText(calculations.answer);
        holder.date.setText(calculations.date);
        holder.bind(calculations, listener, longPressListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView title, body, date;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_equation);
            body = itemView.findViewById(R.id.tv_answer);
            date = itemView.findViewById(R.id.tv_date);
        }

        public void bind(final Calculations data, final OnHistoryClickListener listener,
                         final OnHistoryLongPressListener longPressListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onHistoryClick(data, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longPressListener.onHistoryLongPressed(data, getAdapterPosition());
                    return false;
                }
            });
        }
    }
}
