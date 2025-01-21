package edu.ssu.gonzalez.michael.gonzalez_michael_recipebook;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private org.bson.types.ObjectId oid;
    private String name;
    private String author;
    private String hyperlink;
    private List<Ingredient> ingredients;
    private List<String> instructions;
    private List<String> tags;

    public Recipe() {
        this.oid = null;
        this.name = "";
        this.author = "";
        this.hyperlink = "";
        this.ingredients = new ArrayList<Ingredient>();
        this.instructions = new ArrayList<String>();
        this.tags = new ArrayList<String>();
    }

    public Recipe(org.bson.types.ObjectId oid, String name, String author, String hyperlink, List<Ingredient> ingredients, List<String> instructions, List<String> tags) {
        this.oid = oid;
        this.name = name;
        this.author = author;
        this.hyperlink = hyperlink;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.tags = tags;
    }

    public org.bson.types.ObjectId getOid() {
        return oid;
    }

    public void setOid(org.bson.types.ObjectId oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", hyperlink='" + hyperlink + '\'' +
                ", ingredients=" + ingredients +
                ", instructions=" + instructions +
                ", tags=" + tags +
                '}';
    }

    //find updates helper function
    //Recipe Recipe -> ListOfBool?
}
