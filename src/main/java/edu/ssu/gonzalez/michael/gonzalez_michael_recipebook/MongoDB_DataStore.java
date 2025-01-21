package edu.ssu.gonzalez.michael.gonzalez_michael_recipebook;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

public class MongoDB_DataStore {
    //store mongoclient as private? for access by CRUD operations
    //or even store collection as private variable?
    private MongoClient client = null;
    private static final String connectionString = "mongodb+srv://michaeltgonza:lemon69@michaeldb0.4zwajpp.mongodb.net/?retryWrites=true&w=majority";


    private void openConnection() {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try {
            client = MongoClients.create(settings);
        } catch (MongoException e) {
            printMongoDBException(e);
        }
    }

    //close connection
    private void closeConnection() {
        client.close();
    }

    public void printMongoDBException(MongoException ex) {
        ex.printStackTrace(System.err);
        System.err.println("Error Code: " + ex.getCode());
        System.err.println("Message: " + ex.getMessage());
        Throwable t = ex.getCause();

        while (t != null) {
            System.out.println("Cause: " + t);
            t = t.getCause();
        }
    }

    //insert recipe
    public void insertRecipe(Recipe newRecipe) {
        openConnection();

        MongoDatabase db = client.getDatabase("RecipeBook");
        MongoCollection<Document> coll = db.getCollection("Recipes");

        //to append ingredients, I need a list of Documents for each ingredient
        List<Document> ingredientDocs = new ArrayList<>();

        //to generate the list of documents, I need to take each ingredient and create its document
        for (Ingredient i : newRecipe.getIngredients()) {
            ingredientDocs.add(new Document("iname", i.getIname())
                    .append("qty", i.getQty()).
                    append("unit", i.getUnit()));
        }

        Document insertRecipe = new Document("_id", new ObjectId());
        insertRecipe.append("name", newRecipe.getName())
                .append("author", newRecipe.getAuthor())
                .append("hyperlink", newRecipe.getHyperlink())
                .append("ingredients", ingredientDocs)
                .append("instructions", newRecipe.getInstructions())
                .append("tags", newRecipe.getTags());

        coll.insertOne(insertRecipe);

        closeConnection();
    }

    public void updateRecipe(ObjectId toUpdate, Recipe newRecipe) {
        openConnection();

        MongoDatabase db = client.getDatabase("RecipeBook");
        MongoCollection<Document> coll = db.getCollection("Recipes");

        //extend later to find difference between objects and only update relevant fields

        //to append ingredients, I need a list of Documents for each ingredient
        List<Document> ingredientDocs = new ArrayList<>();

        //to generate the list of documents, I need to take each ingredient and create its document
        for (Ingredient i : newRecipe.getIngredients()) {
            ingredientDocs.add(new Document("iname", i.getIname())
                    .append("qty", i.getQty()).
                    append("unit", i.getUnit()));
        }

        Document updateRecipe = new Document();
        updateRecipe.append("name", newRecipe.getName())
                .append("author", newRecipe.getAuthor())
                .append("hyperlink", newRecipe.getHyperlink())
                .append("ingredients", ingredientDocs)
                .append("instructions", newRecipe.getInstructions())
                .append("tags", newRecipe.getTags());

        coll.replaceOne(eq("_id", toUpdate), updateRecipe);
        closeConnection();
    }

    public void deleteRecipe(ObjectId toDelete) {
        openConnection();

        MongoDatabase db = client.getDatabase("RecipeBook");
        MongoCollection<Document> coll = db.getCollection("Recipes");

        coll.deleteOne(eq("_id", toDelete));

        closeConnection();
    }

    //get all ids and name of all recipes in database
    public HashMap<ObjectId, String> getAllRecipes() {
        openConnection();

        HashMap<ObjectId, String> recipeListView = new HashMap<>();

        MongoDatabase db = client.getDatabase("RecipeBook");
        MongoCollection<Document> coll = db.getCollection("Recipes");

        MongoCursor<Document> cursor = coll.find(new Document()).iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                recipeListView.put(doc.getObjectId("_id"), doc.getString("name"));
            }
        } finally {
            cursor.close();
        }

        closeConnection();

        return recipeListView;
    }

    //search for recipes by name, tag, or ingredient
    public HashMap<ObjectId, String> searchRecipes(String searchTerm, String searchField) {
        openConnection();

        HashMap<ObjectId, String> recipeListView = new HashMap<>();
        Bson filter;

        MongoDatabase db = client.getDatabase("RecipeBook");
        MongoCollection<Document> coll = db.getCollection("Recipes");

        //for name I want to see if the name string contains the search term
        //requires regex?
        //could be text search instead
        if (searchField.equals("name")) {
            String regexSearch = "." + searchTerm + ".";
            filter = regex(searchField, regexSearch);
        } else if (searchField.equals("tags")) {
            filter = eq(searchField, searchTerm);
        } else if (searchField.equals("ingredients")) {
            String newField = searchField + ".iname";
            filter = eq(newField, searchTerm);
        } else {
            filter = new Document();
        }

        MongoCursor<Document> cursor = coll.find(filter).iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                recipeListView.put(doc.getObjectId("_id"), doc.getString("name"));
            }
        } finally {
            cursor.close();
        }

        closeConnection();

        return recipeListView;
    }


    //load single selected recipe
    public Recipe loadRecipe(ObjectId oid) {
        openConnection();

        Recipe newLoadedRecipe = new Recipe();

        MongoDatabase db = client.getDatabase("RecipeBook");
        MongoCollection<Document> coll = db.getCollection("Recipes");

        coll.find(eq("_id", oid));

        MongoCursor<Document> cursor = coll.find(eq("_id", oid)).iterator();

        try {
            Document loadedRecipe = cursor.next();
            newLoadedRecipe = documentToRecipe(loadedRecipe);
        } finally {
            cursor.close();
        }

        closeConnection();

        return newLoadedRecipe;
    }

    private Recipe documentToRecipe(Document doc) {
        Recipe rec = new Recipe();
        rec.setOid(doc.getObjectId("_id"));
        rec.setName(doc.getString("name"));
        rec.setAuthor(doc.getString("author"));
        rec.setHyperlink(doc.getString("hyperlink"));

        List<Ingredient> ingredientList = new ArrayList<>();
        List<Document> ingredientDocs = doc.getList("ingredients", Document.class);
        for (Document d : ingredientDocs) {
            ingredientList.add(new Ingredient(d.getString("iname"), d.getString("qty"), d.getString("unit")));
        }
        rec.setIngredients(ingredientList);
        rec.setInstructions(doc.getList("instructions", String.class));
        rec.setTags(doc.getList("tags", String.class));

        return rec;
    }

    public int countRecipes() {
        openConnection();

        int count;

        MongoDatabase db = client.getDatabase("RecipeBook");
        MongoCollection<Document> coll = db.getCollection("Recipes");

        count = (int) coll.countDocuments();

        closeConnection();

        return count;
    }


}