package co.com.pragma.sqs.listener.mapper;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.sqs.listener.dto.LoanApplicationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationSQSMapperTest {

    private final LoanApplicationSQSMapper mapper = Mappers.getMapper(LoanApplicationSQSMapper.class);

    @Test
    void shouldMapLoanApplicationDTOToLoanApplicationSuccessfully() {
        // Given
        LoanApplicationDTO dto = createTestLoanApplicationDTO();

        // When
        LoanApplication result = mapper.loanApplicationDTOToLoanApplication(dto);

        // Then
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getAmount(), result.getAmount());
        assertEquals(dto.getTerm(), result.getTerm());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getLoanTypeId(), result.getLoanTypeId());
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(1L);
        dto.setAmount(null);
        dto.setTerm(null);
        dto.setEmail(null);
        dto.setLoanTypeId(null);

        // When
        LoanApplication result = mapper.loanApplicationDTOToLoanApplication(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getAmount());
        assertNull(result.getTerm());
        assertNull(result.getEmail());
        assertNull(result.getLoanTypeId());
    }

    @Test
    void shouldHandleNullDTO() {
        // When
        LoanApplication result = mapper.loanApplicationDTOToLoanApplication(null);

        // Then
        assertNull(result);
    }

    @Test
    void shouldMapWithLargeValues() {
        // Given
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(Long.MAX_VALUE);
        dto.setAmount(Double.MAX_VALUE);
        dto.setTerm(Integer.MAX_VALUE);
        dto.setEmail("test@example.com");
        dto.setLoanTypeId(Long.MAX_VALUE);

        // When
        LoanApplication result = mapper.loanApplicationDTOToLoanApplication(dto);

        // Then
        assertNotNull(result);
        assertEquals(Long.MAX_VALUE, result.getId());
        assertEquals(Double.MAX_VALUE, result.getAmount());
        assertEquals(Integer.MAX_VALUE, result.getTerm());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Long.MAX_VALUE, result.getLoanTypeId());
    }

    @Test
    void shouldMapWithMinValues() {
        // Given
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(1L);
        dto.setAmount(0.01);
        dto.setTerm(1);
        dto.setEmail("a@b.c");
        dto.setLoanTypeId(1L);

        // When
        LoanApplication result = mapper.loanApplicationDTOToLoanApplication(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(0.01, result.getAmount());
        assertEquals(1, result.getTerm());
        assertEquals("a@b.c", result.getEmail());
        assertEquals(1L, result.getLoanTypeId());
    }

    private LoanApplicationDTO createTestLoanApplicationDTO() {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(1L);
        dto.setAmount(50000.0);
        dto.setTerm(12);
        dto.setEmail("test@example.com");
        dto.setLoanTypeId(1L);
        return dto;
    }
}
