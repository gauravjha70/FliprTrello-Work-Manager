package com.example.flipr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CardsAdapter extends ArrayAdapter<CardsClass> {

    Context context;
    ArrayList<CardsClass> items;

    TextView name;
    LinearLayout dateLayout;
    TextView date;

    public CardsAdapter(@NonNull Context context, int resource, @NonNull List<CardsClass> items) {
        super(context, resource, items);
        this.context = context;
        this.items = (ArrayList<CardsClass>) items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cards_item_adapter, null);
        }

        name = convertView.findViewById(R.id.name);
        dateLayout = convertView.findViewById(R.id.date_layout);
        date = convertView.findViewById(R.id.date);

        CardsClass item = getItem(position);

        if(item!=null)
        {
            name.setText(item.getCardName());

            if(item.getDueDateSet())
            {
                dateLayout.setVisibility(View.VISIBLE);
                Date d = item.getDueDate();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                date.setText(simpleDateFormat.format(d));
            }

        }



        return convertView;
    }
}
