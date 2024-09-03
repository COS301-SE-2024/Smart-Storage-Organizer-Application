package com.example.smartstorageorganizer.model;

public class ItemAttributeModel {
    private boolean flammable;
    private boolean breakable;
    private boolean temperatureSensitive;
    private boolean perishable;
    private boolean hazardous;
    private boolean corrosive;
    private boolean explosive;
    private boolean humiditySensitive;
    private boolean radiationSensitive;
    private boolean lightSensitive;
    private boolean pressureSensitive;
    private boolean toxic;
    private boolean reactive;
    private boolean odorSensitive;
    private boolean magnetic;


    public ItemAttributeModel(boolean flammable, boolean breakable, boolean temperatureSensitive, boolean perishable, boolean hazardous, boolean corrosive, boolean explosive, boolean humiditySensitive, boolean radiationSensitive, boolean lightSensitive, boolean pressureSensitive, boolean toxic, boolean reactive, boolean odorSensitive, boolean magnetic) {
        this.flammable = flammable;
        this.breakable = breakable;
        this.temperatureSensitive = temperatureSensitive;
        this.perishable = perishable;
        this.hazardous = hazardous;
        this.corrosive = corrosive;
        this.explosive = explosive;
        this.humiditySensitive = humiditySensitive;
        this.radiationSensitive = radiationSensitive;
        this.lightSensitive = lightSensitive;
        this.pressureSensitive = pressureSensitive;
        this.toxic = toxic;
        this.reactive = reactive;
        this.odorSensitive = odorSensitive;
        this.magnetic = magnetic;
    }

    // Getters and Setters
    public boolean isFlammable() { return flammable; }
    public void setFlammable(boolean flammable) { this.flammable = flammable; }

    public boolean isBreakable() { return breakable; }
    public void setBreakable(boolean breakable) { this.breakable = breakable; }

    public boolean isTemperatureSensitive() { return temperatureSensitive; }
    public void setTemperatureSensitive(boolean temperatureSensitive) { this.temperatureSensitive = temperatureSensitive; }


}
