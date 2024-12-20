package test.task.socks_service.specification;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import test.task.socks_service.entity.Sock;

class SockSpecificationTest {

    @Test
    void testFilterByCottonPercentageRange_BothValuesProvided() {
        Double minCotton = 50.0;
        Double maxCotton = 80.0;
        Specification<Sock> specification = SockSpecification.filterByCottonPercentageRange(minCotton, maxCotton);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Sock> criteriaQuery = mock(CriteriaQuery.class);
        Root<Sock> root = mock(Root.class);
        
        when(criteriaBuilder.between(root.get("cottonPercentage"), minCotton, maxCotton)).thenReturn(null);
        specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, times(1)).between(root.get("cottonPercentage"), minCotton, maxCotton);
    }

    @Test
    void testFilterByCottonPercentageRange_MinValueProvided() {

        Double minCotton = 50.0;
        Double maxCotton = null;
        Specification<Sock> specification = SockSpecification.filterByCottonPercentageRange(minCotton, maxCotton);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Sock> criteriaQuery = mock(CriteriaQuery.class);
        Root<Sock> root = mock(Root.class);
        
        when(criteriaBuilder.greaterThanOrEqualTo(root.get("cottonPercentage"), minCotton)).thenReturn(null);

        specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, times(1)).greaterThanOrEqualTo(root.get("cottonPercentage"), minCotton);
    }

    @Test
    void testFilterByCottonPercentageRange_MaxValueProvided() {
        Double minCotton = null;
        Double maxCotton = 80.0;
        Specification<Sock> specification = SockSpecification.filterByCottonPercentageRange(minCotton, maxCotton);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Sock> criteriaQuery = mock(CriteriaQuery.class);
        Root<Sock> root = mock(Root.class);

        when(criteriaBuilder.lessThanOrEqualTo(root.get("cottonPercentage"), maxCotton)).thenReturn(null);

        specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, times(1)).lessThanOrEqualTo(root.get("cottonPercentage"), maxCotton);
    }

    @Test
    void testFilterByCottonPercentageRange_NeitherValueProvided() {

        Double minCotton = null;
        Double maxCotton = null;
        Specification<Sock> specification = SockSpecification.filterByCottonPercentageRange(minCotton, maxCotton);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Sock> criteriaQuery = mock(CriteriaQuery.class);
        Root<Sock> root = mock(Root.class);

        when(criteriaBuilder.conjunction()).thenReturn(null);

        specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, times(1)).conjunction();
    }

    @Test
    void testFilterByColor_ValidColor() {
        
        String color = "Red";
        Specification<Sock> specification = SockSpecification.filterByColor(color);
    
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Sock> criteriaQuery = mock(CriteriaQuery.class);
        Root<Sock> root = mock(Root.class);

        when(criteriaBuilder.equal(root.get("color"), color)).thenReturn(null);
        specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, times(1)).equal(root.get("color"), color);
    }

    @Test
    void testFilterByColor_NullColor() {
        String color = null;
        Specification<Sock> specification = SockSpecification.filterByColor(color);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Sock> criteriaQuery = mock(CriteriaQuery.class);
        Root<Sock> root = mock(Root.class);

        when(criteriaBuilder.conjunction()).thenReturn(null);

        specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, times(1)).conjunction();
    }

    @Test
    void testFilterByColor_EmptyColor() {

        String color = "";
        Specification<Sock> specification = SockSpecification.filterByColor(color);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Sock> criteriaQuery = mock(CriteriaQuery.class);
        Root<Sock> root = mock(Root.class);

        when(criteriaBuilder.conjunction()).thenReturn(null);

        specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, times(1)).conjunction();
    }
}