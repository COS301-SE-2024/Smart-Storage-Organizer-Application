package com.example.smartstorageorganizer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ItemAttributeModel;

public class ItemAttributeAdapter extends RecyclerView.Adapter<ItemAttributeAdapter.ViewHolder> {

    private ItemAttributeModel itemAttributes;

    public ItemAttributeAdapter(ItemAttributeModel itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attributes_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.flammableCheckbox.setChecked(itemAttributes.isFlammable());
        holder.breakableCheckbox.setChecked(itemAttributes.isBreakable());
        holder.temperatureCheckbox.setChecked(itemAttributes.isTemperatureSensitive());
        holder.perishableCheckbox.setChecked(itemAttributes.isPerishable());
        holder.hazardousCheckbox.setChecked(itemAttributes.isHazardous());
        holder.corrosiveCheckbox.setChecked(itemAttributes.isCorrosive());
        holder.explosiveCheckbox.setChecked(itemAttributes.isExplosive());
        holder.humidityCheckbox.setChecked(itemAttributes.isHumiditySensitive());
        holder.radiationCheckbox.setChecked(itemAttributes.isRadiationSensitive());
        holder.lightSensetiveCheckbox.setChecked(itemAttributes.isLightSensitive());
        holder.pressureCheckbox.setChecked(itemAttributes.isPressureSensitive());
        holder.toxicCheckbox.setChecked(itemAttributes.isToxic());
        holder.reactiveCheckbox.setChecked(itemAttributes.isReactive());
        holder.odorCheckbox.setChecked(itemAttributes.isOdorSensitive());
        holder.magneticbox.setChecked(itemAttributes.isMagnetic());
    }

    @Override
    public int getItemCount() {
        return 1; // Since we only have one ItemAttributesModel object.
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox flammableCheckbox;
        CheckBox breakableCheckbox;
        CheckBox temperatureCheckbox;
        CheckBox perishableCheckbox;
        CheckBox hazardousCheckbox;
        CheckBox corrosiveCheckbox;
        CheckBox explosiveCheckbox;
        CheckBox humidityCheckbox;
        CheckBox radiationCheckbox;
        CheckBox lightSensetiveCheckbox;
        CheckBox pressureCheckbox;
        CheckBox toxicCheckbox;
        CheckBox reactiveCheckbox;
        CheckBox odorCheckbox;
        CheckBox magneticbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flammableCheckbox = itemView.findViewById(R.id.flammable_checkbox);
            breakableCheckbox = itemView.findViewById(R.id.breakable_checkbox);
            temperatureCheckbox = itemView.findViewById(R.id.temperature_checkbox);
            perishableCheckbox = itemView.findViewById(R.id.perishable_checkbox);
            hazardousCheckbox = itemView.findViewById(R.id.hazadous_checkbox);
        }
    }
}
