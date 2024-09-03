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

    public boolean isPerishable() { return perishable; }
    public void setPerishable(boolean perishable) { this.perishable = perishable; }

    public boolean isHazardous() { return hazardous; }
    public void setHazardous(boolean hazardous) { this.flammable = hazardous; }

    public boolean isCorrosive() { return corrosive; }
    public void setCorrosive(boolean corrosive) { this.corrosive = corrosive; }

    public boolean isExplosive() { return explosive; }
    public void setExplosive(boolean explosive) { this.explosive = explosive; }

    public boolean isHumiditySensitive() { return humiditySensitive; }
    public void setHumiditySensitive(boolean humiditySensitive) { this.humiditySensitive = humiditySensitive; }

    public boolean isRadiationSensitive() { return radiationSensitive; }
    public void setRadiationSensitive(boolean radiationSensitive) { this.flammable = radiationSensitive; }

    public boolean isLightSensitive() { return lightSensitive; }
    public void setLightSensitive(boolean lightSensitive) { this.lightSensitive = lightSensitive; }

    public boolean isPressureSensitive() { return pressureSensitive; }
    public void setPressureSensitive(boolean pressureSensitive) { this.pressureSensitive = pressureSensitive; }

    public boolean isToxic() { return toxic; }
    public void setToxic(boolean toxic) { this.toxic = toxic; }

    public boolean isReactive() { return reactive; }
    public void setReactive(boolean reactive) { this.reactive = reactive; }

    public boolean isOdorSensitive() { return odorSensitive; }
    public void setOdorSensitive(boolean odorSensitive) { this.odorSensitive = odorSensitive; }

    public boolean isMagnetic() { return magnetic; }
    public void setMagnetic(boolean magnetic) { this.magnetic = magnetic; }

}
