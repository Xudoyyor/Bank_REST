package com.backend.bankcards.service.userService;

import com.backend.bankcards.dto.transactionDTO.TransferRequest;
import com.backend.bankcards.entity.Card;
import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.enums.CardStatus;
import com.backend.bankcards.exception.InsufficientFundsException;
import com.backend.bankcards.repository.CardRepository;
import com.backend.bankcards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private UserCardServiceImpl userCardService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // 1. SecurityContext-ni soxtalashtirish (Mocking SecurityContext)
        // Bu getName() chaqirilganda "hudoyor" qaytarishini ta'minlaydi
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("hudoyor");
        SecurityContextHolder.setContext(securityContext);

        // 2. Test foydalanuvchisini yaratish
        testUser = new UserEntity();
        testUser.setUsername("hudoyor");
    }

    @Test
    @DisplayName("Pul o'tkazmasi muvaffaqiyatli bo'lishi kerak")
    void transfer_ShouldSucceed_WhenBalancesAreEnough() {
        // Setup
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(new BigDecimal("1000.00"));
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setUser(testUser); // User-ni albatta qo'shish kerak!

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(new BigDecimal("500.00"));
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setUser(testUser); // User-ni albatta qo'shish kerak!

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("300.00"));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        // Execute
        userCardService.transfer(request);

        // Assert
        assertEquals(new BigDecimal("700.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("800.00"), toCard.getBalance());

        verify(cardRepository, times(1)).save(fromCard);
        verify(cardRepository, times(1)).save(toCard);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Balans yetarli bo'lmaganda xato berishi kerak")
    void transfer_ShouldThrowException_WhenInsufficientFunds() {
        // Setup
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(new BigDecimal("100.00"));
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setUser(testUser);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setUser(testUser);

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("500.00"));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        // Execute & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            userCardService.transfer(request);
        });
    }
}