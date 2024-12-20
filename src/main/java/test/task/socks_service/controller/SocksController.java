package test.task.socks_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import test.task.socks_service.entity.Sock;
import test.task.socks_service.entity.dto.SocksPostRequest;
import test.task.socks_service.entity.dto.SocksUpdateRequest;
import test.task.socks_service.exception.NoSocksFoundException;
import test.task.socks_service.service.SocksService;

import java.io.IOException;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.PositiveOrZero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/socks")
@Tag(name = "Носки")
public class SocksController {

    private static final Logger logger = LoggerFactory.getLogger(SocksController.class);
    private final SocksService socksService;
    
    @Operation(description = "Регистрация прихода носков")
    @PostMapping("/income")
    public ResponseEntity<Sock> incomeSocks(@RequestBody @Valid SocksPostRequest request) {
        logger.info("Запрос на приход носков: {}", request);
        Sock sock = Sock.builder()
        .amount(request.getAmount())
        .color(request.getColor())
        .cottonPercentage(request.getCottonPercentage())
        .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(this.socksService.incomeSock(sock));
    }

    @Operation(description = "Регистрация отпуска носков")
    @PostMapping("/outcome")
    public ResponseEntity<Sock> outcomeSocks(@RequestBody @Valid SocksPostRequest request) throws Exception {
        logger.info("Запрос на уход носков: {}", request);
        Sock sock = Sock.builder()
        .amount(request.getAmount())
        .color(request.getColor())
        .cottonPercentage(request.getCottonPercentage())
        .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(this.socksService.outcomeSock(sock));
    }

    @Operation(description = "Обновление данных носков")
    @PutMapping("/{id}")
    public ResponseEntity<Sock> updateSocks(@PathVariable Long id,
     @RequestBody @Valid SocksUpdateRequest request) throws NoSocksFoundException{
        logger.info("Запрос на обновление носков с ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(this.socksService.updateSock(
            request.getColor(), request.getCottonPercentage(), id));
    }

    @Operation(description = "Получение общего количества носков с фильтрацией")
    @GetMapping
    public ResponseEntity<Integer> getAmountOfSocks(
            @RequestParam String color,
            @RequestParam @PositiveOrZero(message = "Содержание хлопка не может быть отрицательным")
            @Max(value = 100, message = "Содержание хлопка не может быть больше 100%") Double cottonPercentage,
            @RequestParam @Nullable String operator) throws NoSocksFoundException{
        Integer amount = this.socksService.getAmountOfSocks(color, cottonPercentage, operator);
        logger.info("Запрос на получение количества носков с фильтрацией: {}", amount);
        return ResponseEntity.ok(amount);
    }

    @Operation(description = "Загрузка партий носков из Excel или CSV файла")
    @PostMapping("/batch")
    public ResponseEntity<String> batchSocks(@RequestParam("file") MultipartFile file) {
        logger.info("Запрос на загрузку партии носков из файла: {}", file.getOriginalFilename());
        try {
            this.socksService.processSocksFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("Партия носков успешно загружена.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    @Operation(description = "Запрос на получение количества носков с дополнительной возможностью фильтрации")
    @GetMapping("/filter")
    public Page<Sock> filterSocks(
            @RequestParam(required = false) Double minCotton,
            @RequestParam(required = false) Double maxCotton,
            @RequestParam(required = false) String color,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cottonPercentage") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        return socksService.getFilteredAndSortedSocks(minCotton, maxCotton, color, page, size, sortField, sortDirection);
    }
    
}
