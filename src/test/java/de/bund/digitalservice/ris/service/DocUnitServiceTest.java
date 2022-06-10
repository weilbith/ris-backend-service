package de.bund.digitalservice.ris.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import de.bund.digitalservice.ris.datamodel.DocUnit;
import de.bund.digitalservice.ris.repository.DocUnitRepository;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

@ExtendWith(SpringExtension.class)
@Import(DocUnitService.class)
@TestPropertySource(properties = "otc.obs.bucket-name:testBucket")
@Tag("test")
class DocUnitServiceTest {
  @Autowired private DocUnitService service;

  @MockBean private DocUnitRepository repository;

  @MockBean private S3AsyncClient s3AsyncClient;

  @Test
  void testGenerateNewDocUnit() {
    var docUnit = new DocUnit();
    docUnit.setFiletype("docx");
    ArgumentCaptor<DocUnit> docUnitCaptor = ArgumentCaptor.forClass(DocUnit.class);
    when(repository.save(any(DocUnit.class))).thenReturn(Mono.just(DocUnit.EMPTY));

    StepVerifier.create(service.generateNewDocUnit())
        .consumeNextWith(
            docUnitResponseEntity -> {
              assertNotNull(docUnitResponseEntity);
              assertEquals(HttpStatus.CREATED, docUnitResponseEntity.getStatusCode());
            })
        .verifyComplete();

    verify(repository).save(docUnitCaptor.capture());
    assertEquals(docUnitCaptor.getValue(), docUnit);
  }

  // @Test public void testGenerateNewDocUnit_withException() {} TODO

  @Test
  void testAttachFileToDocUnit() {
    // given
    var byteBufferFlux = Flux.just(ByteBuffer.wrap(new byte[] {}));
    var headerMap = new LinkedMultiValueMap<String, String>();
    headerMap.put("Content-Type", List.of("content/type"));
    headerMap.put("X-Filename", List.of("testfile.docx"));
    var httpHeaders = HttpHeaders.readOnlyHttpHeaders(headerMap);

    var toSave = new DocUnit();
    toSave.setId(1);
    toSave.setS3path("88888888-4444-4444-4444-121212121212");
    toSave.setFiletype("docx");
    toSave.setFilename("testfile.docx");

    var savedDocUnit = new DocUnit();
    savedDocUnit.setId(1);
    savedDocUnit.setS3path("88888888-4444-4444-4444-121212121212");
    savedDocUnit.setFiletype("docx");
    when(repository.save(any(DocUnit.class))).thenReturn(Mono.just(savedDocUnit));
    when(repository.findById(1)).thenReturn(Mono.just(savedDocUnit));

    when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
        .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));

    var putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
    var asyncRequestBodyCaptor = ArgumentCaptor.forClass(AsyncRequestBody.class);

    var testUuid = UUID.fromString("88888888-4444-4444-4444-121212121212");
    try (MockedStatic<UUID> mockedUUIDStatic = mockStatic(UUID.class)) {
      mockedUUIDStatic.when(UUID::randomUUID).thenReturn(testUuid);

      // when and then
      StepVerifier.create(service.attachFileToDocUnit("1", byteBufferFlux, httpHeaders))
          .consumeNextWith(
              docUnit -> {
                assertNotNull(docUnit);
                assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(savedDocUnit), docUnit);
              })
          .verifyComplete();

      verify(s3AsyncClient)
          .putObject(putObjectRequestCaptor.capture(), asyncRequestBodyCaptor.capture());
      assertEquals("testBucket", putObjectRequestCaptor.getValue().bucket());
      assertEquals("88888888-4444-4444-4444-121212121212", putObjectRequestCaptor.getValue().key());
      assertEquals("content/type", putObjectRequestCaptor.getValue().contentType());
      StepVerifier.create(asyncRequestBodyCaptor.getValue())
          .expectNext(ByteBuffer.wrap(new byte[] {}))
          .verifyComplete();
      verify(repository).save(toSave);
    }
  }

  @Test
  void testRemoveFileFromDocUnit() {
    // TODO implement and test removal from bucket

    var docUnitBefore = new DocUnit();
    docUnitBefore.setId(1);
    docUnitBefore.setS3path("88888888-4444-4444-4444-121212121212");
    docUnitBefore.setFilename("testfile.docx");

    var docUnitAfter = new DocUnit();
    docUnitAfter.setId(1);

    when(repository.findById(1)).thenReturn(Mono.just(docUnitBefore));
    // is the thenReturn ok? Or am I bypassing the actual functionality-test? TODO
    when(repository.save(any(DocUnit.class))).thenReturn(Mono.just(docUnitAfter));
    CompletableFuture<DeleteObjectResponse> completableFuture =
        CompletableFuture.completedFuture(DeleteObjectResponse.builder().build());
    when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class))).thenReturn(completableFuture);

    StepVerifier.create(service.removeFileFromDocUnit("1"))
        .consumeNextWith(
            docUnitResponseEntity -> {
              assertNotNull(docUnitResponseEntity);
              assertEquals(HttpStatus.OK, docUnitResponseEntity.getStatusCode());
            })
        .verifyComplete();

    ArgumentCaptor<DocUnit> docUnitCaptor = ArgumentCaptor.forClass(DocUnit.class);
    verify(repository).save(docUnitCaptor.capture());
    assertEquals(docUnitCaptor.getValue(), docUnitAfter);
  }

  @Test
  void testGenerateNewDocUnitAndAttachFile_withExceptionFromBucket() throws S3Exception {
    // given
    var byteBufferFlux = Flux.just(ByteBuffer.wrap(new byte[] {}));

    when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
        .thenThrow(SdkException.create("exception", null));

    // when and then
    StepVerifier.create(service.attachFileToDocUnit("1", byteBufferFlux, HttpHeaders.EMPTY))
        .consumeNextWith(
            responseEntity -> {
              assertNotNull(responseEntity);
              assertNotNull(responseEntity.getBody());
              assertEquals(DocUnit.EMPTY, responseEntity.getBody());
              assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            })
        .verifyComplete();

    verify(s3AsyncClient).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
    verify(repository, times(0)).save(any(DocUnit.class));
  }

  @Test
  void testGenerateNewDocUnitAndAttachFile_withExceptionFromRepository() {
    // given
    var byteBufferFlux = Flux.just(ByteBuffer.wrap(new byte[] {}));

    when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
        .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));
    doThrow(new IllegalArgumentException()).when(repository).save(any(DocUnit.class));
    when(repository.findById(1)).thenReturn(Mono.just(DocUnit.EMPTY));

    // when and then
    StepVerifier.create(service.attachFileToDocUnit("1", byteBufferFlux, HttpHeaders.EMPTY))
        .consumeNextWith(
            responseEntity -> {
              assertNotNull(responseEntity);
              assertNotNull(responseEntity.getBody());
              assertEquals(DocUnit.EMPTY, responseEntity.getBody());
              assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            })
        .verifyComplete();

    verify(s3AsyncClient).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
    verify(repository).save(any(DocUnit.class));
  }

  @Test
  void testGetAll() {
    StepVerifier.create(service.getAll())
        .consumeNextWith(Assertions::assertNotNull)
        .verifyComplete();

    verify(repository).findAll();
  }

  @Test
  void testGetById() {
    when(repository.findById(1)).thenReturn(Mono.just(DocUnit.EMPTY));
    StepVerifier.create(service.getById("1"))
        .consumeNextWith(
            monoResponse -> assertEquals(monoResponse.getBody().getClass(), DocUnit.class))
        .verifyComplete();
    verify(repository).findById(1);
  }

  @Test
  void testUpdateDocUnit() {
    var docUnit = DocUnit.EMPTY;
    when(repository.save(docUnit)).thenReturn(Mono.just(docUnit));
    StepVerifier.create(service.updateDocUnit(docUnit))
        .consumeNextWith(monoResponse -> assertEquals(monoResponse.getBody(), docUnit))
        .verifyComplete();
    verify(repository).save(docUnit);
  }
}
