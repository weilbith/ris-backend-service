import { expect } from "@playwright/test"
import jsdom from "jsdom"
import { openNorm } from "./e2e-utils"
import { testWithImportedNorm } from "./fixtures"
import norm from "./testdata/norm_basic.json"

testWithImportedNorm(
  "Check if XML can be retrieved by ELI and content is correct",
  async ({ page, normData, guid, request }) => {
    // Open frame data
    await openNorm(page, normData["officialLongTitle"], guid)
    await page.locator("a:has-text('Rahmen')").click()

    const eliInputValue = await page.inputValue("input#eli")

    await page.goto(`/api/v1/norms/xml/${eliInputValue}`)

    const response = await request.get(`/api/v1/norms/xml/${eliInputValue}`)
    expect(response.ok()).toBeTruthy()
    expect(response.headers()["content-type"]).toBe("application/xml")

    const xmlAsString = await response.text()
    const xmlDOM = new jsdom.JSDOM(xmlAsString, { contentType: "text/html" })

    xmlDOM.window.document
      .querySelectorAll("akn\\:FRBRthis")
      .forEach((frbrThis) => {
        // eslint-disable-next-line jest-dom/prefer-to-have-attribute
        expect(frbrThis.getAttribute("value")).toMatch(
          new RegExp(`^${eliInputValue}`)
        )
      })

    xmlDOM.window.document
      .querySelectorAll("akn\\:FRBRdate")
      .forEach((frbrDate) => {
        // eslint-disable-next-line jest-dom/prefer-to-have-attribute
        expect(frbrDate.getAttribute("date")).toBe(norm.announcementDate)
      })

    // eslint-disable-next-line jest-dom/prefer-to-have-attribute
    expect(
      xmlDOM.window.document
        .querySelector("akn\\:FRBRnumber")
        .getAttribute("value")
    ).toBe(`s${norm.printAnnouncementPage}`)

    // eslint-disable-next-line jest-dom/prefer-to-have-attribute
    expect(
      xmlDOM.window.document
        .querySelector("akn\\:FRBRname")
        .getAttribute("value")
    ).toBe("bgbl-1")

    const proprietary =
      xmlDOM.window.document.querySelector("akn\\:proprietary")
    expect(proprietary.querySelector("meta\\:typ").textContent.trim()).toBe(
      "gesetz"
    )
    expect(proprietary.querySelector("meta\\:form").textContent.trim()).toBe(
      "stammform"
    )
    expect(proprietary.querySelector("meta\\:fassung").textContent.trim()).toBe(
      "verkuendungsfassung"
    )
    expect(proprietary.querySelector("meta\\:art").textContent.trim()).toBe(
      "regelungstext"
    )
    expect(
      proprietary.querySelector("meta\\:initiant").textContent.trim()
    ).toBe("bundestag")
    expect(
      proprietary
        .querySelector("meta\\:bearbeitendeInstitution")
        .textContent.trim()
    ).toBe("nicht-vorhanden")

    expect(
      xmlDOM.window.document.querySelector("akn\\:docTitle").textContent.trim()
    ).toBe(norm.officialLongTitle)
    expect(
      xmlDOM.window.document
        .querySelector("akn\\:shortTitle")
        .textContent.trim()
    ).toBe(norm.officialShortTitle)

    xmlDOM.window.document
      .querySelectorAll("akn\\:article")
      .forEach((article, articleIndex) => {
        expect(article.querySelector("akn\\:marker").textContent.trim()).toBe(
          norm.articles[articleIndex].marker
        )
        expect(article.querySelector("akn\\:heading").textContent.trim()).toBe(
          norm.articles[articleIndex].title
        )

        article
          .querySelectorAll("akn\\:paragraph")
          .forEach((paragraph, paragraphIndex) => {
            if (
              norm.articles[articleIndex].paragraphs[paragraphIndex].marker !==
              undefined
            ) {
              expect(
                paragraph.querySelector("akn\\:marker").textContent.trim()
              ).toBe(
                norm.articles[articleIndex].paragraphs[paragraphIndex].marker
              )
            }
            expect(
              paragraph
                .querySelector("akn\\:p")
                .textContent.trim()
                .replace(/\n/, "")
            ).toBe(norm.articles[articleIndex].paragraphs[paragraphIndex].text)
          })
      })
  }
)
