/**
 * Some modifications will have to be made to this file now-and-then,
 * see the following link for module-info documentation: https://blog.codefx.org/java/java-module-system-tutorial/
 *
 * Add to this however you'd like.
 */
module nerdygadgets {

    // We need this.
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    // Allow javafx.<package> to access nl.nerdygadgets.<...> packages.
    // See 'Reflection' for more information on why they need to be opened like this (access related)
    opens nl.nerdygadgets.pages.controllers to javafx.fxml;
    opens nl.nerdygadgets.main to javafx.graphics;
    opens nl.nerdygadgets.infrastructure.design to java.xml;

    // finally, export.
    exports nl.nerdygadgets.pages.controllers;
}