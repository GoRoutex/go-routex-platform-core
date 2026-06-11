package platform.merchant.service.infrastructure.persistence.adapter.merchant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.port.MerchantRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantEntity;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.repository.MerchantEntityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MerchantRepositoryRepositoryAdapter implements MerchantRepositoryPort {

    private final MerchantEntityRepository merchantEntityRepository;
    private final MerchantPersistenceMapper merchantPersistenceMapper;

    @Override
    public Merchant save(Merchant merchant) {
        MerchantEntity savedEntity = merchantEntityRepository.save(merchantPersistenceMapper.toEntity(merchant));
        return merchantPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Merchant> findById(String merchantId) {
        return merchantEntityRepository.findById(merchantId)
                .map(merchantPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return merchantEntityRepository.existsByCode(code);
    }

    @Override
    public String generateMerchantCode() {
        return merchantEntityRepository.generateMerchantcode();
    }

    @Override
    public PagedResult<Merchant> fetch(int pageNumber, int pageSize) {
        Page<MerchantEntity> page = merchantEntityRepository.findAll(
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        return toPagedResult(page);
    }

    @Override
    public PagedResult<Merchant> fetch(String merchantName, int pageNumber, int pageSize) {
        if (merchantName == null || merchantName.isBlank()) {
            return fetch(pageNumber, pageSize);
        }

        Page<MerchantEntity> page = merchantEntityRepository
                .findByDisplayNameContainingIgnoreCaseOrLegalNameContainingIgnoreCase(
                        merchantName.trim(),
                        merchantName.trim(),
                        PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
                );

        return toPagedResult(page);
    }

    @Override
    public List<Merchant> findByIds(List<String> merchantIds) {
        if (merchantIds == null || merchantIds.isEmpty()) {
            return List.of();
        }

        Map<String, Merchant> merchantsById = merchantEntityRepository.findAllById(merchantIds).stream()
                .map(merchantPersistenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toMap(Merchant::getId, Function.identity()));

        return merchantIds.stream()
                .map(merchantsById::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    public List<String> findIdsByMerchantName(String merchantName) {
        if (merchantName == null || merchantName.isBlank()) {
            return List.of();
        }

        return merchantEntityRepository
                .findByDisplayNameContainingIgnoreCaseOrLegalNameContainingIgnoreCase(merchantName.trim(), merchantName.trim())
                .stream()
                .map(MerchantEntity::getId)
                .toList();
    }

    @Override
    public Map<String, Merchant> findNamesByIds(List<String> merchantIds) {
        List<MerchantEntity> merchants = merchantEntityRepository.findAllByIdIn(merchantIds);
        return merchants.stream()
                .map(merchantPersistenceMapper::toDomain)
                .collect(Collectors.toMap(
                        Merchant::getId,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(Merchant::getCreatedAt))
                ));
    }

    private PagedResult<Merchant> toPagedResult(Page<MerchantEntity> page) {
        List<Merchant> items = page.getContent().stream()
                .map(merchantPersistenceMapper::toDomain)
                .toList();

        return PagedResult.<Merchant>builder()
                .items(items)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

}
