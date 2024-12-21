package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    private CandidateService candidateService;

    private CityService cityService;

    private CandidateController candidateController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenGetAllThenGetSameDataAndPage() {
        var expectedCandidates = List.of(
                new Candidate(1, "test1", "desc1", now(), 1, 2),
                new Candidate(2, "test2", "desc2", now(), 3, 4));
        when(candidateService.findAll()).thenReturn(expectedCandidates);
        var model = new ConcurrentModel();

        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(actualCandidates).isEqualTo(expectedCandidates);
        assertThat(view).isEqualTo("candidates/list");
    }

    @Test
    public void whenGetCreationPageThenGetSameDataAndPage() {
        var expectedCities = List.of(
                new City(1, "test1"),
                new City(2, "test2"));
        when(cityService.findAll()).thenReturn(expectedCities);
        var model = new ConcurrentModel();

        var view = candidateController.getCreationPage(model);
        var actualCities = model.getAttribute("cities");

        assertThat(actualCities).isEqualTo(expectedCities);
        assertThat(view).isEqualTo("candidates/create");
    }

    @Test
    public void whenCreateThenGetSameDataAndPage() throws Exception {
        var candidate = new Candidate(1, "name1", "desc1", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate);
        var model = new ConcurrentModel();

        var view = candidateController.create(candidate, model, testFile);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenCreateThrowExceptionThenGetSameDataAndPage() {
        var expectedException = new RuntimeException("Exception1");
        when(candidateService.save(any(), any())).thenThrow(expectedException);
        var model = new ConcurrentModel();

        var view = candidateController.create(new Candidate(), model, testFile);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenGetByIdThenGetSameDataAndPage() {
        var candidate = new Candidate(1, "name1", "desc1", now(), 1, 1);
        var expectedCities = List.of(
                new City(1, "test1"),
                new City(2, "test2"));
        when(cityService.findAll()).thenReturn(expectedCities);
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(candidateService.findById(integerArgumentCaptor.capture())).thenReturn(Optional.of(candidate));
        var model = new ConcurrentModel();

        var view = candidateController.getById(model, candidate.getId());
        var actualId = integerArgumentCaptor.getValue();
        var actualCities = model.getAttribute("cities");
        var actualCandidate = model.getAttribute("candidate");

        assertThat(actualId).isEqualTo(candidate.getId());
        assertThat(actualCities).isEqualTo(expectedCities);
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(view).isEqualTo("candidates/one");
    }

    @Test
    public void whenGetByIdUnsuccessfulThenGetSameDataAndPage() {
        var candidate = new Candidate(1, "name1", "desc1", now(), 1, 1);
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(candidateService.findById(integerArgumentCaptor.capture())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var view = candidateController.getById(model, candidate.getId());
        var actualId = integerArgumentCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(actualId).isEqualTo(candidate.getId());
        assertThat(actualMessage).isEqualTo("Кандидат с указанным идентификатором не найден");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenUpdateThenGetSameDataAndPage() throws Exception {
        var candidate = new Candidate(1, "name1", "desc1", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();

        var view = candidateController.update(candidate, model, testFile);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenUpdateUnsuccessfulThenGetSameDataAndPage() throws Exception {
        var candidate = new Candidate(1, "name1", "desc1", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(false);
        var model = new ConcurrentModel();

        var view = candidateController.update(candidate, model, testFile);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
        assertThat(actualMessage).isEqualTo("Кандидат с указанным идентификатором не найден");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenUpdateThrowExceptionThenGetSameDataAndPage() {
        var expectedException = new RuntimeException("Exception");
        when(candidateService.update(any(), any())).thenThrow(expectedException);
        var model = new ConcurrentModel();

        var view = candidateController.update(new Candidate(), model, testFile);
        var actualMessage = model.getAttribute("message");

        assertThat(actualMessage).isEqualTo(expectedException.getMessage());
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenDeleteThenGetSameDataAndPage() {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(candidateService.deleteById(integerArgumentCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();

        var view = candidateController.delete(model, id);
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteUnsuccessfulThenGetSameDataAndPage() {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(candidateService.deleteById(integerArgumentCaptor.capture())).thenReturn(false);
        var model = new ConcurrentModel();

        var view = candidateController.delete(model, id);
        var actualId = integerArgumentCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(actualId).isEqualTo(id);
        assertThat(actualMessage).isEqualTo("Кандидат с указанным идентификатором не найден");
        assertThat(view).isEqualTo("errors/404");
    }
}