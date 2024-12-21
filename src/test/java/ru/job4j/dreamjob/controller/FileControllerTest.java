package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    public void whenGetByIdThenGetSameDataAndResponseOk() {
        var id = 1;
        var content = new byte[] {1, 2, 3};
        var fileDto = new FileDto("Test1", content);
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.getFileById(integerArgumentCaptor.capture())).thenReturn(Optional.of(fileDto));
        var expectedResponse = ResponseEntity.ok(content);

        var response = fileController.getById(id);
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void whenGetByIdUnsuccessfulThenGetSameDataAndResponseNotFound() {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.getFileById(integerArgumentCaptor.capture())).thenReturn(Optional.empty());
        var expectedResponse = ResponseEntity.notFound().build();

        var response = fileController.getById(id);
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(response).isEqualTo(expectedResponse);
    }
}