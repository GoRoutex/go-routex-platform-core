package platform.merchant.service.infrastructure.persistence.adapter.merchant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.model.MerchantApplicationForm;
import platform.merchant.service.domain.merchant.port.MerchantApplicationFormRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.repository.MerchantApplicationFormEntityRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MerchantApplicationFormRepositoryRepositoryAdapter implements MerchantApplicationFormRepositoryPort {

    private final MerchantApplicationFormEntityRepository merchantApplicationFormEntityRepository;
    private final MerchantApplicationFormPersistenceMapper merchantApplicationFormPersistenceMapper;

    @Override
    public MerchantApplicationForm save(MerchantApplicationForm merchantApplicationForm) {
        return merchantApplicationFormPersistenceMapper.toDomain(
                merchantApplicationFormEntityRepository.save(
                        merchantApplicationFormPersistenceMapper.toEntity(merchantApplicationForm)
                )
        );
    }

    @Override
    public boolean existsByFormCode(String formCode) {
        return merchantApplicationFormEntityRepository.existsByFormCode(formCode);
    }

    @Override
    public String generateFormCode() {
        return merchantApplicationFormEntityRepository.generateFormCode();
    }

    @Override
    public Optional<MerchantApplicationForm> findById(String id) {
        return merchantApplicationFormEntityRepository.findById(id)
                .map(merchantApplicationFormPersistenceMapper::toDomain);
    }

    @Override
    public PagedResult<MerchantApplicationForm> fetch(int pageNumber, int pageSize) {
        Page<platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantApplicationFormEntity> page =
                merchantApplicationFormEntityRepository.findAll(
                        PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "submittedAt"))
                );

        return PagedResult.<MerchantApplicationForm>builder()
                .items(page.getContent().stream().map(merchantApplicationFormPersistenceMapper::toDomain).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public PagedResult<MerchantApplicationForm> fetchByStatus(ApplicationFormStatus status, int pageNumber, int pageSize) {
        Page<platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantApplicationFormEntity> page =
                merchantApplicationFormEntityRepository.findByStatus(
                        status,
                        PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "submittedAt"))
                );

        return PagedResult.<MerchantApplicationForm>builder()
                .items(page.getContent().stream().map(merchantApplicationFormPersistenceMapper::toDomain).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
