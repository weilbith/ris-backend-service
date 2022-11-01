package de.bund.digitalservice.ris.caselaw.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import de.bund.digitalservice.ris.caselaw.domain.LookupTableImporterService;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = LookupTableImporterController.class)
@WithMockUser
class LookupTableImporterControllerTest {
  @Autowired private WebTestClient webClient;

  @MockBean private LookupTableImporterService service;

  @Test
  void testImportDocumentTypeLookupTable() {
    when(service.importDocumentTypeLookupTable(any(ByteBuffer.class))).thenReturn(Mono.empty());

    webClient
        .mutateWith(csrf())
        .put()
        .uri("/api/v1/lookuptableimporter/doktyp")
        .bodyValue(BodyInserters.fromValue(new byte[] {}))
        .exchange()
        .expectStatus()
        .isOk();

    verify(service, times(1)).importDocumentTypeLookupTable(any(ByteBuffer.class));
  }
}
