package de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.proceedingdecision;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DatabaseProceedingDecisionRepository
    extends R2dbcRepository<ProceedingDecisionDTO, Long> {

  Flux<ProceedingDecisionDTO> findAllById(Long documentUnitId);

  Mono<ProceedingDecisionDTO> findByUuid(UUID documentUnitUuid);

  @Query(
      "SELECT * FROM doc_unit WHERE "
          + "(:courtType IS NULL OR gerichtstyp = :courtType) AND "
          + "(:courtLocation IS NULL OR gerichtssitz = :courtLocation) AND"
          + "(:decisionDate IS NULL OR decision_date = :decisionDate) AND"
          + "(:docUnitIds IS NULL OR id IN (SELECT * FROM UNNEST(:docUnitIds))) AND "
          + "(:docTypeId IS NULL OR document_type_id = :docTypeId)")
  Flux<ProceedingDecisionDTO> findByCourtDateFileNumberAndDocumentType(
      String courtType,
      String courtLocation,
      Instant decisionDate,
      Long[] docUnitIds,
      Long docTypeId);
}
