package com.backend.bankcards.repository;

import com.backend.bankcards.entity.Card;
import com.backend.bankcards.entity.TransactionEntity;
import jakarta.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
    List<TransactionEntity> findAllByFromCardInOrToCardInOrderByCreatedAtDesc(List<Card> myCards, List<Card> myCards1);
}
