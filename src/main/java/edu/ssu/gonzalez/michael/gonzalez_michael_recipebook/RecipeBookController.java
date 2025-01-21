package edu.ssu.gonzalez.michael.gonzalez_michael_recipebook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.bson.types.ObjectId;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class RecipeBookController implements Initializable {

    @FXML
    private Button btnAddRecipe;
    @FXML
    private Button btnDeleteRecipe;
    @FXML
    private Button btnLoadRecipe;
    @FXML
    private Button btnUpdateRecipe;

    @FXML
    private Button btnSearch;

    @FXML
    private TextField txtHyperlink;

    @FXML
    private TextArea txtIngredients;

    @FXML
    private TextArea txtInstructions;

    @FXML
    private TextField txtRecipeAuthor;

    @FXML
    private TextField txtRecipeName;

    @FXML
    private TextField txtTags;

    @FXML
    private CheckBox cbTag;
    @FXML

    private TextField txtSearch;

    @FXML
    private ListView<String> lvUpdateDeleteRecipe;

    @FXML
    private TextField txtUpdateAuthor;

    @FXML
    private TextArea txtUpdateIngredients;

    @FXML
    private TextArea txtUpdateInstructions;

    @FXML
    private TextField txtUpdateLink;

    @FXML
    private TextField txtUpdateName;

    @FXML
    private TextField txtUpdateTags;

    @FXML
    private Label lblRecipeCounter;


    private String recipeName, recipeAuthor, recipeHyperlink, recipeTags, recipeIngredients, recipeInstructions, ingredientName, ingredientQty, ingredientUnit;

    private String[] tagsFields, ingredientsFields, instructionsFields, curIngredientFields;

    private List<String> instructionsArray, tagsArray;
    private Recipe recipeObject;

    private MongoDB_DataStore dataStore;
    private Ingredient ingredientObject;

    private List<Ingredient> ingredientsArray;

    private List<ObjectId> objectIdList;
    private List<String> recipeNameList;
    private ObservableList<String> recipeNames;


    @FXML
    void btnAddRecipe_Click(MouseEvent event) {
        recipeName = txtRecipeName.getText();
        recipeAuthor = txtRecipeAuthor.getText();
        recipeHyperlink = txtHyperlink.getText();


        // Tags: Input is single line, separated by commas.
        tagsArray = new ArrayList<>();
        recipeTags = txtTags.getText();
        tagsFields = recipeTags.split(",");

        for (int i = 0; i < tagsFields.length; i++) {
            tagsArray.add(tagsFields[i].trim());
            //System.out.println(tagsArray.get(i));
        }


        // Ingredients: Input has the form "amount unit ingredient" with
        // each ingredient on a new line.
        ingredientsArray = new ArrayList<>();
        recipeIngredients = txtIngredients.getText();

        if(!recipeIngredients.isEmpty()) {
            ingredientsFields = recipeIngredients.split("\\R");

            for (int i = 0; i < ingredientsFields.length; i++) {
                curIngredientFields = ingredientsFields[i].split("\\s+", 3);
                ingredientQty = curIngredientFields[0];
                ingredientUnit = curIngredientFields[1];
                ingredientName = curIngredientFields[2];
                ingredientObject = new Ingredient(ingredientName, ingredientQty, ingredientUnit);
                ingredientsArray.add(ingredientObject);
            }
        }

        // Instructions: Input has a single step per line.
        instructionsArray = new ArrayList<>();
        recipeInstructions = txtInstructions.getText();
        instructionsFields = recipeInstructions.split("\\R");

        for (int i = 0; i < instructionsFields.length; i++) {
            instructionsArray.add(instructionsFields[i].trim());
        }


        // Recipe Object
        recipeObject = new Recipe(null, recipeName, recipeAuthor, recipeHyperlink, ingredientsArray, instructionsArray, tagsArray);
        dataStore = new MongoDB_DataStore();
        dataStore.insertRecipe(recipeObject);

        lblRecipeCounter.setText(Integer.toString(dataStore.countRecipes()));
    }


    @FXML
    void btnDeleteRecipe_Click(MouseEvent event) {
        //get selection index from listview
        int selectedIndex = lvUpdateDeleteRecipe.getSelectionModel().getSelectedIndex();

        //find objectid in observable list
        //pass object id to deleteRecipe
        dataStore.deleteRecipe(objectIdList.get(selectedIndex));

        // clear all text fields to blank
        txtUpdateName.setText("");
        txtUpdateAuthor.setText("");
        txtUpdateLink.setText("");
        txtUpdateTags.setText("");
        txtUpdateIngredients.setText("");
        txtUpdateInstructions.setText("");

        // remove from observable lists
        objectIdList.remove(selectedIndex);
        recipeNameList.remove(selectedIndex);

        recipeNames.remove(selectedIndex);

        lblRecipeCounter.setText(Integer.toString(dataStore.countRecipes()));
    }

    @FXML
    void btnLoadRecipe(MouseEvent event) {
        dataStore = new MongoDB_DataStore();

        //maybe shouldn't be data member here
        recipeObject = dataStore.loadRecipe(objectIdList.get(lvUpdateDeleteRecipe.getSelectionModel().getSelectedIndex()));

        txtUpdateName.setText(recipeObject.getName());
        txtUpdateAuthor.setText(recipeObject.getAuthor());
        txtUpdateLink.setText(recipeObject.getHyperlink());

        // tag
        tagsArray = recipeObject.getTags();

        recipeTags = "";
        for (int i = 0; i < tagsArray.size(); i++) {
            recipeTags += tagsArray.get(i);

            if (i != tagsArray.size() - 1) {
                recipeTags += " , ";
            }
        }

        txtUpdateTags.setText(recipeTags);

        // ingredients
        ingredientsArray = recipeObject.getIngredients();

        recipeIngredients = "";
        for (int i = 0; i < ingredientsArray.size(); i++) {
            recipeIngredients += ingredientsArray.get(i).getQty();
            recipeIngredients += " ";
            recipeIngredients += ingredientsArray.get(i).getUnit();
            recipeIngredients += " ";
            recipeIngredients += ingredientsArray.get(i).getIname();
            recipeIngredients += "\n";
        }

        txtUpdateIngredients.setText(recipeIngredients);

        // instructions.
        instructionsArray = recipeObject.getInstructions();

        recipeInstructions = "";
        for (int i = 0; i < instructionsArray.size(); i++) {
            recipeInstructions += instructionsArray.get(i);
            recipeInstructions += "\n\n";
        }

        txtUpdateInstructions.setText(recipeInstructions);


        //txtUpdateTags.setText(recipeObject.getTags());
    }

    @FXML
    void btnUpdateRecipe_Click(MouseEvent event) {
        //get selectionindex from listview
        int selectedIndex = lvUpdateDeleteRecipe.getSelectionModel().getSelectedIndex();

        // find objectid w/ list
        ObjectId oidToUpdate = objectIdList.get(selectedIndex);

        // create new recipe object from all text inputs
        recipeName = txtUpdateName.getText();
        recipeAuthor = txtUpdateAuthor.getText();
        recipeHyperlink = txtUpdateLink.getText();


        // Tags: Input is single line, separated by commas.
        tagsArray = new ArrayList<>();
        recipeTags = txtUpdateTags.getText();
        tagsFields = recipeTags.split(",");

        for (int i = 0; i < tagsFields.length; i++) {
            tagsArray.add(tagsFields[i].trim());
            //System.out.println(tagsArray.get(i));
        }


        // Ingredients: Input has the form "amount unit ingredient" with
        // each ingredient on a new line.
        ingredientsArray = new ArrayList<>();
        recipeIngredients = txtUpdateIngredients.getText();

        if(!recipeIngredients.isEmpty()) {
            ingredientsFields = recipeIngredients.split("\\R");

            for (int i = 0; i < ingredientsFields.length; i++) {
                curIngredientFields = ingredientsFields[i].split("\\s+", 3);
                ingredientQty = curIngredientFields[0];
                ingredientUnit = curIngredientFields[1];
                ingredientName = curIngredientFields[2];
                ingredientObject = new Ingredient(ingredientName, ingredientQty, ingredientUnit);
                ingredientsArray.add(ingredientObject);
            }
        }

        // Instructions: Input has a single step per line.
        instructionsArray = new ArrayList<>();
        recipeInstructions = txtUpdateInstructions.getText();
        instructionsFields = recipeInstructions.split("\\R");

        for (int i = 0; i < instructionsFields.length; i++) {
            instructionsArray.add(instructionsFields[i].trim());
        }

        // Recipe Object
        recipeObject = new Recipe(null, recipeName, recipeAuthor, recipeHyperlink, ingredientsArray, instructionsArray, tagsArray);

        dataStore = new MongoDB_DataStore();
        dataStore.updateRecipe(oidToUpdate, recipeObject);
    }


    @FXML
    void btnSearch_Click(MouseEvent event) {
        dataStore = new MongoDB_DataStore();
        HashMap<ObjectId, String> recipeListView = new HashMap<>();
        objectIdList = new ArrayList<>();
        recipeNameList = new ArrayList<>();

        String search = txtSearch.getText();

        if (search.isEmpty()) {
            recipeListView = dataStore.getAllRecipes();
        }

        if(cbTag.isSelected() && !search.isEmpty()) {
            recipeListView = dataStore.searchRecipes(search, "tags");
        }

        for(HashMap.Entry<ObjectId, String> entry : recipeListView.entrySet()) {
            objectIdList.add(entry.getKey());
            recipeNameList.add(entry.getValue());
        }

        recipeNames = FXCollections.observableArrayList(recipeNameList);

        lvUpdateDeleteRecipe.setItems(recipeNames);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataStore = new MongoDB_DataStore();

        lblRecipeCounter.setText(Integer.toString(dataStore.countRecipes()));
    }

}