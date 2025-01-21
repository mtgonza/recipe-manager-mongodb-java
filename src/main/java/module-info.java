module edu.ssu.gonzalez.michael.gonzalez_michael_recipebook {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;


    opens edu.ssu.gonzalez.michael.gonzalez_michael_recipebook to javafx.fxml;
    exports edu.ssu.gonzalez.michael.gonzalez_michael_recipebook;
}