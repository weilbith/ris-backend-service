package de.bund.digitalservice.ris.norms.conventions

import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage
import com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.Predefined
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.conditions.ArchPredicates.are
import de.bund.digitalservice.ris.norms.conventions.predicate.Predicates.aKotlinStaticClass

const val BASE_PACKAGE_PATH = "de.bund.digitalservice.ris.norms"

val allClasses =
    ClassFileImporter()
        .withImportOption(Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("$BASE_PACKAGE_PATH..")
        .that(are(not(aKotlinStaticClass())))

fun areFromTheDomain() = resideInAPackage("$BASE_PACKAGE_PATH.domain..")

fun areFromTheApplication() = resideInAPackage("$BASE_PACKAGE_PATH.application..")

private fun inputPortPackage() = "$BASE_PACKAGE_PATH.application.port.input.."

fun areFromTheInputPortPackage() = resideInAPackage(inputPortPackage())

fun beInsideTheInputPortPackage() = ArchCondition.from<JavaClass>(areFromTheInputPortPackage())

fun areAnInputPort() = areFromTheInputPortPackage().and(simpleNameEndingWith("UseCase"))

fun anInputPort() = areAnInputPort()

private val outputPortPackage = "$BASE_PACKAGE_PATH.application.port.output.."

fun areFromTheOutputPortPackage() = resideInAPackage(outputPortPackage)

fun beInsideTheOutputPortPackage() = ArchCondition.from<JavaClass>(areFromTheOutputPortPackage())

fun areAnOutputPort() = areFromTheOutputPortPackage().and(simpleNameEndingWith("OutputPort"))

fun anyOutputPort() = areAnOutputPort()

fun areAnyPort() = areAnInputPort().or(areAnOutputPort())

fun areFromTheServicePackage() = resideInAPackage("$BASE_PACKAGE_PATH.application.service..")

fun areAService() = areFromTheServicePackage().and(simpleNameEndingWith("Service"))

fun areFromAnyStandardLibrary() =
    resideInAnyPackage("kotlin..", "java..", "org.jetbrains.annotations..")
