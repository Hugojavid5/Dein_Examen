module org.hugo.dein_examen.dein_examen {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.hugo.dein_examen.dein_examen to javafx.fxml;
    exports org.hugo.dein_examen.dein_examen;
}