package com.example.bankcards.service;


import com.example.bankcards.dto.auth.UserDetailsImpl;
import com.example.bankcards.entity.CardJpa;
import com.example.bankcards.repository.CardJpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DataSecurityService {
    private final CardJpaRepository cardRepository;

    public DataSecurityService(CardJpaRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public boolean isOwner(Long userId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getUserId().equals(userId);
        }
        return false;
    }

    public boolean isOwnerOrAdmin(Long cardId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return true;
            }
            CardJpa card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Карта не найдена"));
            return card.getUser().getUserId().equals(userDetails.getUserId());
        }
        return false;
    }
}