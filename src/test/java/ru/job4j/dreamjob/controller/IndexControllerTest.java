package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IndexControllerTest {

    @Test
    public void whenGetIndexThenGetPage() {
        var indexController = new IndexController();

        var view = indexController.getIndex();

        assertThat(view).isEqualTo("index");
    }
}