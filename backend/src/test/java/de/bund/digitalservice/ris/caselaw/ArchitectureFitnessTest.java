package de.bund.digitalservice.ris.caselaw;

import com.tngtech.archunit.core.domain.JavaClass.Predicates;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.Predefined;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SliceRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

class ArchitectureFitnessTest {

  static final String ADAPTER_LAYER_PACKAGES = "de.bund.digitalservice.ris.caselaw.adapter..";
  static final String R2DBC_LAYER_PACKAGES =
      "de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc..";
  static final String JPA_LAYER_PACKAGES =
      "de.bund.digitalservice.ris.caselaw.adapter.database.jpa..";
  static final String DOMAIN_LAYER_PACKAGES = "de.bund.digitalservice.ris.caselaw.domain..";
  static final String RIS_PACKAGES = "de.bund.digitalservice.ris..";

  static JavaClasses classes;

  @BeforeAll
  static void setUp() {
    classes =
        new ClassFileImporter()
            .withImportOption(Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("de.bund.digitalservice.ris.caselaw");
  }

  @Test
  void packagesShouldBeFreeOfCycles() {
    SliceRule rule =
        SlicesRuleDefinition.slices()
            .matching("de.bund.digitalservice.ris.caselaw.(*)")
            .should()
            .beFreeOfCycles();
    rule.check(classes);
  }

  @Test
  void domainClassesShouldOnlyDependOnDomainOrExternalLibClasses() {
    ArchRule rule =
        ArchRuleDefinition.classes()
            .that()
            .resideInAPackage(DOMAIN_LAYER_PACKAGES)
            .should()
            .onlyDependOnClassesThat(
                Predicates.resideInAPackage(DOMAIN_LAYER_PACKAGES)
                    .or(Predicates.resideOutsideOfPackage(RIS_PACKAGES)));
    rule.check(classes);
  }

  @Test
  @Disabled("Domain classes should only have private fields")
  void domainClassesShouldOnlyHavePrivateFields() {
    ArchRule rule =
        ArchRuleDefinition.fields()
            .that()
            .areDeclaredInClassesThat()
            .areNotEnums()
            .and()
            .areDeclaredInClassesThat()
            .resideInAPackage(DOMAIN_LAYER_PACKAGES)
            .should()
            .bePrivate();
    rule.check(classes);
  }

  @Test
  void controllerClassesShouldNotResideOutsideOfAdapterPackage() {
    ArchRule rule =
        ArchRuleDefinition.noClasses()
            .that()
            .areAnnotatedWith(Controller.class)
            .or()
            .areAnnotatedWith(RestController.class)
            .should()
            .resideOutsideOfPackage(ADAPTER_LAYER_PACKAGES);
    rule.check(classes);
  }

  @Test
  void controllerClassesShouldNotDependOnEachOther() {
    ArchRule rule =
        ArchRuleDefinition.noClasses()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .dependOnClassesThat()
            .resideInAPackage(ADAPTER_LAYER_PACKAGES)
            .andShould()
            .dependOnClassesThat()
            .areAnnotatedWith(Controller.class)
            .andShould()
            .dependOnClassesThat()
            .areAnnotatedWith(RestController.class);
    rule.check(classes);
  }

  @Test
  void onlyRepositoryInterfacesWithoutExtensionAllowedInDomain() {
    ArchRule rule =
        ArchRuleDefinition.classes()
            .that()
            .resideInAPackage(DOMAIN_LAYER_PACKAGES)
            .and()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .beInterfaces()
            .andShould()
            .notBeAssignableTo(Object.class);
    rule.check(classes);
  }

  @Test
  void repositoryInR2DBCAdapterPackageAreInterfacesWhichExtendReactiveCrudRepository() {
    ArchRule rule =
        ArchRuleDefinition.classes()
            .that()
            .resideInAPackage(R2DBC_LAYER_PACKAGES)
            .and()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .beInterfaces()
            .andShould()
            .beAssignableTo(ReactiveCrudRepository.class);
    rule.check(classes);
  }

  @Test
  void repositoryInJPAAdapterPackageAreInterfacesWhichExtendJpaCrudRepository() {
    ArchRule rule =
        ArchRuleDefinition.classes()
            .that()
            .resideInAPackage(JPA_LAYER_PACKAGES)
            .and()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .beInterfaces()
            .andShould()
            .beAssignableTo(JpaRepository.class);
    rule.check(classes);
  }
}
