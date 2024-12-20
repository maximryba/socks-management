package test.task.socks_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import test.task.socks_service.entity.Sock;
import test.task.socks_service.exception.NoEnoughSocksException;
import test.task.socks_service.exception.NoSocksFoundException;
import test.task.socks_service.repository.SocksRepository;

import java.util.Arrays;

class CustomSockServiceTest {

    @Mock
    private SocksRepository socksRepository;

    @InjectMocks
    private CustomSockService customSockService;

    private Sock testSock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testSock = new Sock();
        testSock.setColor("Red");
        testSock.setCottonPercentage(30.0);
        testSock.setAmount(10);
    }

    @Test
    void testIncomeSock_NewSock() {
        when(socksRepository.findByColorAndCottonPercentage(anyString(), any(Double.class))).thenReturn(Optional.empty());
        when(socksRepository.save(any(Sock.class))).thenReturn(testSock);

        Sock result = customSockService.incomeSock(testSock);

        assertThat(result.getAmount()).isEqualTo(10);
        verify(socksRepository).save(testSock);
    }

    @Test
    void testOutcomeSock_Success() throws Exception {
        when(socksRepository.findByColorAndCottonPercentage(anyString(), any(Double.class))).thenReturn(Optional.of(testSock));

        Sock result = customSockService.outcomeSock(testSock);

        assertThat(result.getAmount()).isZero();
        verify(socksRepository).findByColorAndCottonPercentage(testSock.getColor(), testSock.getCottonPercentage());
    }

    @Test
    void testOutcomeSock_NoSocksFound() {
        when(socksRepository.findByColorAndCottonPercentage(anyString(), any(Double.class))).thenReturn(Optional.empty());

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(NoSocksFoundException.class, () -> {
            customSockService.outcomeSock(testSock);
        });

        assertThat(exception.getMessage()).isEqualTo("На складе не найдено носков данного типа");
    }

    @Test
    void testOutcomeSock_NotEnoughSocks() {
        testSock.setAmount(1);
        when(socksRepository.findByColorAndCottonPercentage(anyString(), any(Double.class))).thenReturn(Optional.of(testSock));
        
        Sock requestedSock = new Sock();
        requestedSock.setColor("Red");
        requestedSock.setCottonPercentage(30.0);
        requestedSock.setAmount(2);
    
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(NoEnoughSocksException.class, () -> {
            customSockService.outcomeSock(requestedSock);
        });
    
        assertThat(exception.getMessage()).isEqualTo("На складе недостаточно носков данного типа");
    }

    @Test
    void testGetAmountOfSocks_MoreThan() throws NoSocksFoundException {
        when(socksRepository.countSocksByColorAndCottonPercentageGreaterThan(anyString(), any(Double.class))).thenReturn(5);

        Integer amount = customSockService.getAmountOfSocks("Red", 20.0, "moreThan");

        assertThat(amount).isEqualTo(5);
    }

    @Test
    void testGetAmountOfSocks_LessThan() throws NoSocksFoundException {
        when(socksRepository.countSocksByColorAndCottonPercentageLessThan(anyString(), any(Double.class))).thenReturn(3);

        Integer amount = customSockService.getAmountOfSocks("Red", 40.0, "lessThan");

        assertThat(amount).isEqualTo(3);
    }

    @Test
    void testGetAmountOfSocks_Equal() throws NoSocksFoundException {
        when(socksRepository.countSocksByColorAndCottonPercentageEqual(anyString(), any(Double.class))).thenReturn(2);

        Integer amount = customSockService.getAmountOfSocks("Red", 30.0, "equal");

        assertThat(amount).isEqualTo(2);
    }

    @Test
    void testGetAmountOfSocks_NoSocksFound() {
        when(socksRepository.countSocksByColorAndCottonPercentageGreaterThan(anyString(), any(Double.class))).thenReturn(0);

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(NoSocksFoundException.class, () -> {
            customSockService.getAmountOfSocks("Red", 20.0, "moreThan");
        });

        assertThat(exception.getMessage()).isEqualTo("На складе не найдено носков по вашему фильтру");
    }

    @Test
    void testUpdateSock_Success() throws NoSocksFoundException {
        when(socksRepository.findById(any(Long.class))).thenReturn(Optional.of(testSock));

        Sock updatedSock = customSockService.updateSock("Blue", 50.0, 1L);

        assertThat(updatedSock.getColor()).isEqualTo("Blue");
        assertThat(updatedSock.getCottonPercentage()).isEqualTo(50.0);
    }

    @Test
    void testUpdateSock_NotFound() {
        when(socksRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(NoSocksFoundException.class, () -> {
            customSockService.updateSock("Blue", 50.0, 1L);
        });

        assertThat(exception.getMessage()).isEqualTo("На складе не найдено носков с ID: 1");
    }

    @Test
    void testProcessSocksFile_Csv() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("socks.csv");
        when(file.getInputStream()).thenReturn(getClass().getResourceAsStream("../resources/test_socks.csv"));

        customSockService.processSocksFile(file);

        verify(socksRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void testProcessSocksFile_UnsupportedFormat() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("socks.txt");

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> {
            customSockService.processSocksFile(file);
        });

        assertThat(exception.getMessage()).isEqualTo("Неподдерживаемый формат файла. Поддерживаются только .xlsx и .csv");
    }

    @Test
    void testGetFilteredAndSortedSocks() {
    Page<Sock> page = new PageImpl<>(Arrays.asList(testSock));
    when(socksRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

    Page<Sock> result = customSockService.getFilteredAndSortedSocks(20.0, 40.0, "Red", 0, 10, "color", "ASC");

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getColor()).isEqualTo("Red");
}
}