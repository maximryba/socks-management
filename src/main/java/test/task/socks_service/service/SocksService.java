package test.task.socks_service.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import test.task.socks_service.entity.Sock;
import test.task.socks_service.exception.NoSocksFoundException;

public interface SocksService {

    Sock incomeSock(Sock sock);

    Sock outcomeSock(Sock sock) throws Exception;

    Integer getAmountOfSocks(String color, Double cottonPercentage, String operator) throws NoSocksFoundException;

    Sock updateSock(String color, Double cottonPercentage, Long id) throws NoSocksFoundException;

    void processSocksFile(MultipartFile file) throws IOException;

    Page<Sock> getFilteredAndSortedSocks(Double minCotton, Double maxCotton, String color, int page, int size, String sortField, String sortDirection);

}
