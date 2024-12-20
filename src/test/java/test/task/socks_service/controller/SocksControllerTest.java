package test.task.socks_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import test.task.socks_service.entity.Sock;
import test.task.socks_service.entity.dto.SocksPostRequest;
import test.task.socks_service.entity.dto.SocksUpdateRequest;
import test.task.socks_service.exception.NoSocksFoundException;
import test.task.socks_service.service.SocksService;

import java.io.IOException;

class SocksControllerTest {

    @InjectMocks
    private SocksController socksController;

    @Mock
    private SocksService socksService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIncomeSocks() {
        SocksPostRequest request = new SocksPostRequest("Red", 75.0, 100);
        Sock sock = new Sock(1L, "Red", 75.0, 100);

        when(socksService.incomeSock(any(Sock.class))).thenReturn(sock);

        ResponseEntity<Sock> response = socksController.incomeSocks(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sock, response.getBody());
        verify(socksService, times(1)).incomeSock(any(Sock.class));
    }

    @Test
    void testOutcomeSocks() throws Exception {
        SocksPostRequest request = new SocksPostRequest("Blue", 80.0, 150);
        Sock sock = new Sock(2L, "Blue", 80.0, 150);

        when(socksService.outcomeSock(any(Sock.class))).thenReturn(sock);

        ResponseEntity<Sock> response = socksController.outcomeSocks(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sock, response.getBody());
        verify(socksService, times(1)).outcomeSock(any(Sock.class));
    }

    @Test
    void testUpdateSocks() throws NoSocksFoundException {
        SocksUpdateRequest request = new SocksUpdateRequest("Green", 90.0);
        Sock sock = new Sock(3L, "Green", 90.0, 200);

        when(socksService.updateSock(anyString(), anyDouble(), anyLong())).thenReturn(sock);

        ResponseEntity<Sock> response = socksController.updateSocks(3L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sock, response.getBody());
        verify(socksService, times(1)).updateSock(anyString(), anyDouble(), anyLong());
    }

    @Test
    void testGetAmountOfSocks() throws NoSocksFoundException {
        String color = "Red";
        Double cottonPercentage = 75.0;
        String operator = "greater";
        Integer expectedAmount = 100;

        when(socksService.getAmountOfSocks(color, cottonPercentage, operator)).thenReturn(expectedAmount);

        ResponseEntity<Integer> response = socksController.getAmountOfSocks(color, cottonPercentage, operator);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAmount, response.getBody());
        verify(socksService, times(1)).getAmountOfSocks(color, cottonPercentage, operator);
    }

    @Test
    void testBatchSocks() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("socks.csv");

        doNothing().when(socksService).processSocksFile(file);

        ResponseEntity<String> response = socksController.batchSocks(file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Партия носков успешно загружена.", response.getBody());
        verify(socksService, times(1)).processSocksFile(file);
    }

    @Test
    void testBatchSocksIOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("socks.csv");
        doThrow(new IOException("File error")).when(socksService).processSocksFile(file);

        ResponseEntity<String> response = socksController.batchSocks (file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Ошибка при загрузке файла: File error", response.getBody());
        verify(socksService, times(1)).processSocksFile(file);
    }
}