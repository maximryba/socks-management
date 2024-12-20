package test.task.socks_service.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import test.task.socks_service.entity.Sock;
import test.task.socks_service.exception.NoEnoughSocksException;
import test.task.socks_service.exception.NoSocksFoundException;
import test.task.socks_service.repository.SocksRepository;
import test.task.socks_service.specification.SockSpecification;

@Service
@RequiredArgsConstructor
public class CustomSockService implements SocksService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomSockService.class);
    private final SocksRepository socksRepository;

    @Override
    @Transactional
    public Sock incomeSock(Sock sock) {
        logger.info("Приход носков: {}", sock);
        Optional<Sock> sockOptional = this.socksRepository.findByColorAndCottonPercentage(sock.getColor(), sock.getCottonPercentage());
        if (sockOptional.isPresent()) {
            sockOptional.get().setAmount(sockOptional.get().getAmount() + sock.getAmount());
            return sockOptional.get();
        } else {
            return this.socksRepository.save(sock);
        }
        
    }

    @Override
    @Transactional
    public Sock outcomeSock(Sock sock) throws Exception {
        logger.info("Отпуск носков: {}", sock);
        Optional<Sock> sockOptional = this.socksRepository.findByColorAndCottonPercentage(sock.getColor(), sock.getCottonPercentage());
        if (!sockOptional.isPresent()) {
            throw new NoSocksFoundException("На складе не найдено носков данного типа");
        } else if (sockOptional.get().getAmount() < sock.getAmount()) {
            throw new NoEnoughSocksException("На складе недостаточно носков данного типа");
        } else {
            sockOptional.get().setAmount(sockOptional.get().getAmount() - sock.getAmount());
            return sockOptional.get();
        }
    }

    @Override
    public Integer getAmountOfSocks(String color, Double cottonPercentage, String operator) throws NoSocksFoundException {
    Integer amount;
    switch (operator) {
        case "moreThan":
            amount = this.socksRepository.countSocksByColorAndCottonPercentageGreaterThan(color, cottonPercentage);
            break;
        case "lessThan":
            amount = this.socksRepository.countSocksByColorAndCottonPercentageLessThan(color, cottonPercentage);
            break;
        case "equal":
            amount = this.socksRepository.countSocksByColorAndCottonPercentageEqual(color, cottonPercentage);
            break;
        default:
            throw new IllegalArgumentException("Неверный оператор сравнения: " + operator);
    }
    if (amount == null) {
        amount = 0;
    }

    if (amount == 0) {
        throw new NoSocksFoundException("На складе не найдено носков по вашему фильтру");
    }

    return amount;
}

    @Override
    @Transactional
    public Sock updateSock(String color, Double cottonPercentage, Long id) throws NoSocksFoundException {
        logger.info("Обновление носков с ID: {}", id);
        Optional<Sock> sockOptional = this.socksRepository.findById(id);

        if (sockOptional.isPresent()) {
            sockOptional.get().setColor(color);
            sockOptional.get().setCottonPercentage(cottonPercentage);
            return sockOptional.get();
        } else {
            throw new NoSocksFoundException("На складе не найдено носков с ID: " + id);
        }
    }

    @Override
    public void processSocksFile(MultipartFile file) throws IOException {
        logger.info("Начата обработка файла: {}", file.getOriginalFilename());
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.endsWith(".xlsx")) {
            processExcelFile(file);
        } else if (fileName != null && fileName.endsWith(".csv")) {
            processCsvFile(file);
        } else {
            logger.error("Неподдерживаемый формат файла: {}", fileName);
            throw new IOException("Неподдерживаемый формат файла. Поддерживаются только .xlsx и .csv");
        }
        logger.info("Обработка файла завершена: {}", file.getOriginalFilename());
    }

    @Transactional
    private void processExcelFile(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Sock> socks = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Sock sock = new Sock();
                sock.setColor(row.getCell(0).getStringCellValue());
                sock.setCottonPercentage(row.getCell(1).getNumericCellValue());
                sock.setAmount((int) row.getCell(2).getNumericCellValue());
                socks.add(sock);
            }

            socksRepository.saveAll(socks);
        }
    }

    @Transactional
    private void processCsvFile(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<Sock> socks = new ArrayList<>();
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                Sock sock = new Sock();
                sock.setColor(values[0]);
                sock.setCottonPercentage(Double.parseDouble(values[1]));
                sock.setAmount(Integer.parseInt(values[2]));
                socks.add(sock);
            }

            socksRepository.saveAll(socks);
        }
    }

    @Override
    public Page<Sock> getFilteredAndSortedSocks(Double minCotton, Double maxCotton, String color, int page, int size, String sortField, String sortDirection) {
        Specification<Sock> spec = Specification.where(SockSpecification.filterByCottonPercentageRange(minCotton, maxCotton))
                                               .and(SockSpecification.filterByColor(color));

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return this.socksRepository.findAll(spec, pageRequest);
    }

}
