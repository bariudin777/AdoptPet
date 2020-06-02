package com.example.getpet.ViewHolder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getpet.R;
import com.example.getpet.myInterface.AnimalClickListner;


public class AnimalListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_animal_name;
    private AnimalClickListner listener;

    public AnimalListHolder(@NonNull View itemView) {
        super(itemView);

        txt_animal_name = itemView.findViewById(R.id.al_animal_l_name);
    }


    @Override
    public void onClick(View v) {

        listener.onClick(v, getAdapterPosition(), false);

    }

    public void setListener(AnimalClickListner listener) {
        this.listener = listener;
    }
}
