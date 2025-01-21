package edu.ssu.gonzalez.michael.gonzalez_michael_recipebook;

public class Ingredient {

    private String iname;
    private String qty;
    private String unit;

    public Ingredient() {
        this.iname = "";
        this.qty = "";
        this.unit = "";
    }
    public Ingredient(String iname, String qty, String unit) {
        this.iname = iname;
        this.qty = qty;
        this.unit = unit;
    }

    public String getIname() {
        return iname;
    }

    public void setIname(String iname) {
        this.iname = iname;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "iname='" + iname + '\'' +
                ", qty='" + qty + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass() ) {
            return false;
        }

        Ingredient right = (Ingredient) obj;

        return (this.iname.equals(right.iname)) && (this.qty.equals(right.qty)) && (this.unit.equals(right.unit));
    }
}
