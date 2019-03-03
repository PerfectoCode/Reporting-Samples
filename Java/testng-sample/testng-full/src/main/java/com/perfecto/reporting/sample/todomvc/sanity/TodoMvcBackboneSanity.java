package com.perfecto.reporting.sample.todomvc.sanity;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;



public class TodoMvcBackboneSanity extends AbstractTodoMvcSanityTest {

    @Override
    protected By getNewTodoBy() {
        return By.className("new-todo");
    }

    @Override
    protected String getUrl() {
        return "http://todomvc.com/examples/backbone/";
    }

    @Test
    public void todoFiltering() {
        String todoName = createUniqueTodo("backbone");
        filterActive();

        reportiumClient.stepStart("Verify todo " + todoName + " appears in the active todos list");
        verifyAddedTodo(todoName);
        reportiumClient.stepEnd();

        reportiumClient.stepStart("Mark todo " + todoName + " as completed - it should be removed from the list");
        completeTodo(todoName);
        reportiumClient.stepEnd();

        verifyRemovedTodo(todoName);

        verifyTitle();
    }

    /**
     * Click on the "Completed" button to show only completed todos
     */

    protected void filterActive() {
        WebElement active = findElement(By.xpath("//a[@href='#/active']"));
        active.click();
    }


    protected void verifyTitle() {
        Assert.assertEquals(driver.getTitle(), "Backbone.js • TodoMVC");
    }
}
