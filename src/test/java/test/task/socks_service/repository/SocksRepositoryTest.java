package test.task.socks_service.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import test.task.socks_service.entity.Sock;

@DataJpaTest
class SocksRepositoryTest {

    @Autowired
    private SocksRepository socksRepository;

    private Sock testSock;

    @BeforeEach
    public void setup() {
        testSock = new Sock();
        testSock.setAmount(10);
        testSock.setColor("Red");
        testSock.setCottonPercentage(30.0);
        socksRepository.save(testSock);
    }

    @AfterEach
    public void tearDown() {
        socksRepository.delete(testSock);
    }

    @Test
    void testFindByColorAndCottonPercentage() {
        Optional<Sock> foundSock = socksRepository.findByColorAndCottonPercentage(testSock.getColor(), testSock.getCottonPercentage());
        assertThat(foundSock).isPresent();
        assertThat(foundSock.get().getAmount()).isEqualTo(10);
    }

    @Test
    void testCountSocksByColorAndCottonPercentageGreaterThan() {
        Integer testCountNull = socksRepository.countSocksByColorAndCottonPercentageGreaterThan(testSock.getColor(), testSock.getCottonPercentage());
        assertThat(testCountNull).isNull();
        Integer testCountPositive = socksRepository.countSocksByColorAndCottonPercentageGreaterThan(testSock.getColor(), testSock.getCottonPercentage() - 2);
        assertThat(testCountPositive).isEqualTo(10);
    }

    @Test
    void testCountSocksByColorAndCottonPercentageLessThan() {
        Integer testCountNull = socksRepository.countSocksByColorAndCottonPercentageLessThan(testSock.getColor(), testSock.getCottonPercentage());
        assertThat(testCountNull).isNull();
        Integer testCountPositive = socksRepository.countSocksByColorAndCottonPercentageLessThan(testSock.getColor(), testSock.getCottonPercentage() + 2);
        assertThat(testCountPositive).isEqualTo(10);
    }

    @Test
    void testCountSocksByColorAndCottonPercentageEqual() {
        Integer testCountNull = socksRepository.countSocksByColorAndCottonPercentageEqual(testSock.getColor(), testSock.getCottonPercentage() + 2);
        assertThat(testCountNull).isNull();
        Integer testCountPositive = socksRepository.countSocksByColorAndCottonPercentageEqual(testSock.getColor(), testSock.getCottonPercentage());
        assertThat(testCountPositive).isEqualTo(10);
    }

}