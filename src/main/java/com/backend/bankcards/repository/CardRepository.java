package com.backend.bankcards.repository;

import com.backend.bankcards.entity.Card;
import com.backend.bankcards.enums.CardCategory;
import com.backend.bankcards.enums.CardStatus;
import com.backend.bankcards.enums.CardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card,Long> {
    @Query(value = "SELECT * FROM cards c WHERE " +
            "(:cardHolderName IS NULL OR c.owner_name ILIKE CONCAT('%', CAST(:cardHolderName AS TEXT), '%')) AND " +
            "(:bankName IS NULL OR c.bank_name ILIKE CONCAT('%', CAST(:bankName AS TEXT), '%')) AND " +
            "(:status IS NULL OR c.status = CAST(:status AS TEXT)) AND " +
            "(:cardType IS NULL OR c.card_type = CAST(:cardType AS TEXT)) AND " +
            "(:cardCategory IS NULL OR c.card_category = CAST(:cardCategory AS TEXT)) AND " +
            "(c.is_deleted = false)",
            countQuery = "SELECT count(*) FROM cards c WHERE " +
                    "(:cardHolderName IS NULL OR c.owner_name ILIKE CONCAT('%', CAST(:cardHolderName AS TEXT), '%')) AND " +
                    "(:bankName IS NULL OR c.bank_name ILIKE CONCAT('%', CAST(:bankName AS TEXT), '%')) AND " +
                    "(:status IS NULL OR c.status = CAST(:status AS TEXT)) AND " +
                    "(:cardType IS NULL OR c.card_type = CAST(:cardType AS TEXT)) AND " +
                    "(:cardCategory IS NULL OR c.card_category = CAST(:cardCategory AS TEXT)) AND " +
                    "(c.is_deleted = false)",
            nativeQuery = true)
    Page<Card> searchCards(
            @Param("cardHolderName") String cardHolderName,
            @Param("bankName") String bankName,
            @Param("status") String status,
            @Param("cardType") String cardType,
            @Param("cardCategory") String cardCategory,
            Pageable pageable);


    @Query("SELECT c FROM Card c WHERE c.isDeleted = false ORDER BY c.balance DESC")
    List<Card> findTopBalanceCards(Pageable pageable);

    List<Card> findAllByUserUsername(String username);

    Card findByStatus(CardStatus cardStatus);
}
